package com.mindone.Boryeongapi.Scheduler;

import com.mindone.Boryeongapi.domain.entity.hmi.Hmi;
import com.mindone.Boryeongapi.domain.entity.main.*;
import com.mindone.Boryeongapi.repository.hmi.HmiRepository;
import com.mindone.Boryeongapi.repository.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Scheduler {

    @Autowired
    HmiRepository hmiRepository;

    @Autowired
    TagmapRepositoy tagmapRepositoy;

    @Autowired
    FlowRepository flowRepository;

    @Autowired
    TeleRepository teleRepository;

    @Autowired
    PressRepository pressRepository;

    @Autowired
    DepthRepository depthRepository;

    @Autowired
    ValveRepository valveRepository;

    private static boolean flowJobRunning = false;
    private static boolean teleJobRunning = false;
    private static boolean pressJobRunning = false;
    private static boolean depthJobRunning = false;
    private static boolean valveJobRunning = false;

    //유량 데이터
    @Scheduled(cron = "0 0/5 * * * ?")   //주기 설정(5분마다)
    public void flowJobSch() {
        if (flowJobRunning) return;
        flowJobRunning = true;
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2001, 0, 1);
//            String[] lineArr = { "A", "B" };    // 보령, 원산도

            List<Tagmap> tagFlowList = tagmapRepositoy.findByKindAndSort("FLOW", 0);

            for (Tagmap tagmap : tagFlowList){
                String tagId = tagmap.getTagId();

                //유량 데이터 마지막 시간 조회
                Flow lastFlowData = flowRepository.findFirstByIdOrderByColtDtDesc(tagmap.getId());
                Date startDt = lastFlowData == null ? cal.getTime() : lastFlowData.getColtDt();
                Date endDt = new Date();

                List<Tagmap> tagDataList = new ArrayList<Tagmap>();
                if (tagmap.getLine().equals("IN")) tagDataList = tagmapRepositoy.findByKindAndSort("FLOW", 1);
                else if (tagmap.getLine().equals("OUT")) tagDataList = tagmapRepositoy.findByKindAndSort("FLOW", 2);

                List<String> tagIdList = tagDataList.stream().map(a -> a.getTagId()).collect(Collectors.toList());

                //HMI 데이터
                List<Hmi> hmiList = hmiRepository.findByTagIdInAndLogTimeBetweenOrderByLogTime(tagIdList, startDt, endDt);

                if (hmiList != null && hmiList.size() > 0) {
                    Date hmiStartTime = hmiList.get(0).getLogTime();
                    hmiStartTime.setMinutes(0);
                    hmiStartTime.setSeconds(0);
                    Date hmiEndTime = hmiList.get(hmiList.size() - 1).getLogTime();
                    hmiEndTime.setMinutes(0);
                    hmiEndTime.setSeconds(0);

                    for (Date coltDate = hmiStartTime; coltDate.before(hmiEndTime); coltDate = addHour(coltDate, 1)) {
                        Date fromColtDate = coltDate;
                        Date toDate = addHour(coltDate, 1);
                        double insertValue = 0.0;
                        List<Hmi> list = hmiList.stream()
                                .filter(a -> a.getLogTime().compareTo(fromColtDate) >= 0 && a.getLogTime().compareTo(toDate) <= 0)
                                .collect(Collectors.toList());
                        if (list.size() > 0) {
                            double maxValue = 0.0;
                            double minValue = 0.0;
                            for (int i = 0; i < tagIdList.size(); i++) {
                                maxValue += Double.parseDouble(list.get(list.size() - 1 - i).getVal());
                                minValue += Double.parseDouble(list.get(i).getVal());
                            }
                            insertValue = maxValue - minValue;

                            Flow insertFlow = new Flow();
                            insertFlow.setId(tagmap.getId());
                            insertFlow.setColtDt(toDate);
                            insertFlow.setVal(insertValue);
                            insertFlow.setTotalVal(maxValue);
                            flowRepository.save(insertFlow);
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        flowJobRunning = false;
    }

    //통신
    @Scheduled(cron = "0 0/1 * * * ?")   //주기 설정(1분마다)
    public void teleJobSch() {
        if (teleJobRunning) return;
        teleJobRunning = true;
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2001, 0, 1);
            String[] lineArr = { "A", "B" };    // 보령, 원산도
            for (String s : lineArr) {
                //tag ID 조회
                List<Tagmap> tagList = tagmapRepositoy.findByKindAndLine("TELE", s);

                for (Tagmap tagData : tagList) {
                    List<Tele> insertList = new ArrayList<>();
                    String tagId = tagData.getTagId();

                    //통신 마지막 데이터 조회
                    List<String> notTelList = new ArrayList<>();
                    notTelList.add("통신안됨");
                    Tele lastTeleData = teleRepository.findFirstByIdAndValNotInOrderByColtDtDesc(tagData.getId(), notTelList);//findFirstByIdOrderByColtDtDesc
                    Date startDt = lastTeleData == null ? cal.getTime() : lastTeleData.getColtDt();
                    Date endDt = new Date();

                    //HMI 데이터
                    List<Hmi> hmiList = hmiRepository.findByTagIdAndLogTimeBetweenOrderByLogTime(tagId, startDt, endDt);

                    if (hmiList != null && hmiList.size() > 0) {
                        Date hmiStartTime = hmiList.get(0).getLogTime();
                        hmiStartTime.setSeconds(0);
                        Date hmiEndTime = hmiList.get(hmiList.size() - 1).getLogTime();
                        hmiEndTime.setSeconds(0);

                        for (Date coltDate = hmiStartTime; coltDate.before(hmiEndTime); coltDate = addMin(coltDate, 1)) {
                            Date fromColtDate = coltDate;
                            Date toDate = addMin(coltDate, 1);
                            List<Hmi> list = hmiList.stream()
                                    .filter(a -> (a.getLogTime().compareTo(fromColtDate) >= 0 && a.getLogTime().before(toDate)))
                                    .collect(Collectors.toList());
                            Tele insertTele = new Tele();
                            insertTele.setId(tagData.getId());
                            insertTele.setColtDt(toDate);
                            if (list.size() > 0) {
                                insertTele.setVal(list.get(0).getVal());
                            } else {
                                insertTele.setVal("통신안됨");
                            }
                            insertList.add(insertTele);
                        }
                        teleRepository.saveAll(insertList);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        teleJobRunning = false;
    }

    //수압
    @Scheduled(cron = "0 0/1 * * * ?")   //주기 설정(1분마다)
    public void pressJobSch() {
        if (pressJobRunning) return;
        pressJobRunning = true;
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2001, 0, 1);
            String[] lineArr = { "A", "B" };    // 보령, 원산도
            for (String s : lineArr) {
                //tag ID 조회
                List<Tagmap> tagList = tagmapRepositoy.findByKindAndLine("PRESS", s);

                for (Tagmap tagData : tagList) {
                    List<Press> insertList = new ArrayList<>();
                    String tagId = tagData.getTagId();

                    //수압 마지막 데이터 조회
                    Press lastTeleData = pressRepository.findFirstByIdOrderByColtDtDesc(tagData.getId());
                    Date startDt = lastTeleData == null ? cal.getTime() : lastTeleData.getColtDt();
                    Date endDt = new Date();

                    //HMI 데이터
                    List<Hmi> hmiList = hmiRepository.findByTagIdAndLogTimeBetweenOrderByLogTime(tagId, startDt, endDt);

                    if (hmiList != null && hmiList.size() > 0) {
                        Date hmiStartTime = hmiList.get(0).getLogTime();
                        hmiStartTime.setSeconds(0);
                        Date hmiEndTime = hmiList.get(hmiList.size() - 1).getLogTime();
                        hmiEndTime.setSeconds(0);

                        for (Date coltDate = hmiStartTime; coltDate.before(hmiEndTime); coltDate = addMin(coltDate, 1)) {
                            Date fromColtDate = coltDate;
                            Date toDate = addMin(coltDate, 1);
                            List<Hmi> list = hmiList.stream()
                                    .filter(a -> (a.getLogTime().compareTo(fromColtDate) >= 0 && a.getLogTime().before(toDate)))
                                    .collect(Collectors.toList());
                            Press insertPress = new Press();
                            insertPress.setId(tagData.getId());
                            insertPress.setColtDt(toDate);
                            if (list.size() > 0) {
                                insertPress.setVal(Double.parseDouble(list.get(0).getVal()));
                                insertList.add(insertPress);
                            }
                        }
                        pressRepository.saveAll(insertList);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        pressJobRunning = false;
    }

    //수심
    @Scheduled(cron = "0 0/1 * * * ?")   //주기 설정(1분마다)
    public void depthJobSch() {
        if (depthJobRunning) return;
        depthJobRunning = true;
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2001, 0, 1);
            //tag ID 조회
            List<Tagmap> tagList = tagmapRepositoy.findByKindOrderBySort("DEPTH");

            for (Tagmap tagData : tagList) {
                List<Depth> insertList = new ArrayList<>();
                String tagId = tagData.getTagId();

                //수압 마지막 데이터 조회
                Depth lastTeleData = depthRepository.findFirstByIdOrderByColtDtDesc(tagData.getId());
                Date startDt = lastTeleData == null ? cal.getTime() : lastTeleData.getColtDt();
                Date endDt = new Date();

                //HMI 데이터
                List<Hmi> hmiList = hmiRepository.findByTagIdAndLogTimeBetweenOrderByLogTime(tagId, startDt, endDt);

                if (hmiList != null && hmiList.size() > 0) {
                    Date hmiStartTime = hmiList.get(0).getLogTime();
                    hmiStartTime.setSeconds(0);
                    Date hmiEndTime = hmiList.get(hmiList.size() - 1).getLogTime();
                    hmiEndTime.setSeconds(0);

                    for (Date coltDate = hmiStartTime; coltDate.before(hmiEndTime); coltDate = addMin(coltDate, 1)) {
                        Date fromColtDate = coltDate;
                        Date toDate = addMin(coltDate, 1);
                        List<Hmi> list = hmiList.stream()
                                .filter(a -> (a.getLogTime().compareTo(fromColtDate) >= 0 && a.getLogTime().before(toDate)))
                                .collect(Collectors.toList());
                        Depth insertDepth = new Depth();
                        insertDepth.setId(tagData.getId());
                        insertDepth.setColtDt(toDate);
                        if (list.size() > 0) {
                            insertDepth.setVal(Double.parseDouble(list.get(0).getVal()));
                            insertList.add(insertDepth);
                        }
                    }
                    depthRepository.saveAll(insertList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        depthJobRunning = false;
    }

    //밸브
    @Scheduled(cron = "0 0/1 * * * ?")   //주기 설정(1분마다)
    public void valveJobSch() {
        if (valveJobRunning) return;
        valveJobRunning = true;
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2001, 0, 1);
            String[] lineArr = { "A", "B" };    // 보령, 원산도
            for (String s : lineArr) {
                //tag ID 조회
                List<Tagmap> tagList = tagmapRepositoy.findByKindAndLine("VALVE", s);

                for (Tagmap tagData : tagList) {
                    List<Valve> insertList = new ArrayList<>();
                    String tagId = tagData.getTagId();

                    //수압 마지막 데이터 조회
                    Valve lastTeleData = valveRepository.findFirstByIdOrderByColtDtDesc(tagData.getId());
                    Date startDt = lastTeleData == null ? cal.getTime() : lastTeleData.getColtDt();
                    Date endDt = new Date();

                    //HMI 데이터
                    List<Hmi> hmiList = hmiRepository.findByTagIdAndLogTimeBetweenOrderByLogTime(tagId, startDt, endDt);

                    if (hmiList != null && hmiList.size() > 0) {
                        Date hmiStartTime = hmiList.get(0).getLogTime();
                        hmiStartTime.setSeconds(0);
                        Date hmiEndTime = hmiList.get(hmiList.size() - 1).getLogTime();
                        hmiEndTime.setSeconds(0);

                        for (Date coltDate = hmiStartTime; coltDate.before(hmiEndTime); coltDate = addMin(coltDate, 1)) {
                            Date fromColtDate = coltDate;
                            Date toDate = addMin(coltDate, 1);
                            List<Hmi> list = hmiList.stream()
                                    .filter(a -> (a.getLogTime().compareTo(fromColtDate) >= 0 && a.getLogTime().before(toDate)))
                                    .collect(Collectors.toList());
                            Valve insertValve = new Valve();
                            insertValve.setId(tagData.getId());
                            insertValve.setColtDt(toDate);
                            if (list.size() > 0) {
                                insertValve.setVal(list.get(0).getVal());
                                insertList.add(insertValve);
                            }
                        }
                        valveRepository.saveAll(insertList);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        valveJobRunning = false;
    }

    private Date addSec(Date date, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, sec);
        return cal.getTime();
    }

    private Date addMin(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    private Date addHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }
}
