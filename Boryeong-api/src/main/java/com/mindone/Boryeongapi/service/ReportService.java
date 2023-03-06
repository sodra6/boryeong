package com.mindone.Boryeongapi.service;

import com.mindone.Boryeongapi.domain.entity.main.Tagmap;
import com.mindone.Boryeongapi.domain.entity.main.Tele;
import com.mindone.Boryeongapi.domain.entity.main.Valve;
import com.mindone.Boryeongapi.repository.main.TagmapRepositoy;
import com.mindone.Boryeongapi.repository.custom.ReportRepositoryCustom;
import com.mindone.Boryeongapi.repository.main.TeleRepository;
import com.querydsl.core.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    ReportRepositoryCustom reportRepositoryCustom;

    @Autowired
    TagmapRepositoy tagmapRepositoy;

    @Autowired
    TeleRepository teleRepository;

    public Map<String, Object> getReport(String srtDate) {
        Map<String, Object> result = new HashMap<>();

        String[] lineArr = {"A", "B"};

        //날짜 형식
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();

        try {
            date = dateFormat.parse(srtDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        //당일 23시
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.HOUR, -1);

        /* 공급량 현황 */
        //보령쪽(A) 유량계 기준...
        List<Tagmap> tagmapList = tagmapRepositoy.findByKindAndLine("FLOW", "A");

        Map<String, Double> supplyReport = new HashMap<>();
        if (tagmapList != null && tagmapList.size() > 0) {
            String id = tagmapList.get(0).getId();
            //일 공급량
            double todayFlow = reportRepositoryCustom.findDaySupply(id, date, cal.getTime());

            //전일 23시
            cal.add(Calendar.DATE, -1);
            Date toDate = cal.getTime();
            //전일 00시
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            //전일 공급량
            double beforeFlow = reportRepositoryCustom.findDaySupply(id, cal.getTime(), toDate);
            //일 평균 공급량
            double avgFlow = reportRepositoryCustom.findAvgSupply(id);

            supplyReport.put("todayFlow", todayFlow);   //일 공급량
            supplyReport.put("beforeFlow", beforeFlow); //전일 공급량
            supplyReport.put("beforeCompare", todayFlow - beforeFlow);  //전일 대비
            supplyReport.put("avgFlow", avgFlow);   //일 평균 공급량
            supplyReport.put("avgCompare", todayFlow - avgFlow);    //평균 대비
        }
        result.put("supplyReport", supplyReport);

        /* 밸브 현황 */
        //당일 23:59:59
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.SECOND, -1);
        Map<String, Object> valveReport = new HashMap<>();
        //A, B 순서대로 조회
        for (String line : lineArr) {
            List<Valve> valveCloseList = reportRepositoryCustom.findValveNonTimeList("OPEN", line, date, cal.getTime());
            Map<String, Object> valveTimeMap = new HashMap<>();
            for (Valve valveData : valveCloseList) {
                //수압계 ID
                String id = valveData.getId().replace("_" + line, "");
                //(시):(분)
                int hour = valveData.getColtDt().getHours();
                int minutes = valveData.getColtDt().getMinutes();
                String time = (hour < 10 ? "0" + hour : hour) + ":" + (minutes < 10 ? "0" + minutes : minutes);

                //시간 리스트 삽입
                if (valveTimeMap.containsKey(id)) {
                    List<String> timeList = (List<String>) valveTimeMap.get(id);
                    timeList.add(time);
                    valveTimeMap.put(id, timeList);
                } else {
                    List<String> timeList = new ArrayList<>();
                    timeList.add(time);
                    valveTimeMap.put(id, timeList);
                }
            }
            /*
            List<ReportDTO> valveCloseList = reportRepositoryCustom.findDataNonTimeList("VALVE", "OPEN", line, date, cal.getTime());
            Map<String, Object> valveTimeMap = new HashMap<>();
            for (ReportDTO reportData : valveCloseList) {
                //수압계 ID
                String id = reportData.getId().replace("_" + line, "");
                //(시):(분)
                int hour = reportData.getLogTime().getHours();
                int minutes = reportData.getLogTime().getMinutes();
                String time = (hour < 10 ? "0" + hour : hour) + ":" + (minutes < 10 ? "0" + minutes : minutes);

                //시간 리스트 삽입
                if (valveTimeMap.containsKey(id)) {
                    List<String> timeList = (List<String>) valveTimeMap.get(id);
                    timeList.add(time);
                    valveTimeMap.put(id, timeList);
                } else {
                    List<String> timeList = new ArrayList<>();
                    timeList.add(time);
                    valveTimeMap.put(id, timeList);
                }
            }
             */
            valveReport.put(line, valveTimeMap);
        }

        result.put("valveReport", valveReport);

        /* 집수조 수심 현황 */
        Map<String, Double> depthReport = new HashMap<>();
        Tuple todayLevel = reportRepositoryCustom.findDayLevel(date, cal.getTime());

        Double getVal = todayLevel.get(0, double.class);
        Double roundVal = getVal == null ? null : (double) Math.round(getVal);
        depthReport.put("todayAvg", todayLevel == null || todayLevel.size() <= 0 ? null : roundVal);//(double) Math.round(todayLevel.get(0, double.class)));  //일 평균 수심
        getVal = todayLevel.get(1, double.class);
        roundVal = getVal == null ? null : (double) Math.round(getVal);
        depthReport.put("todayMax", todayLevel == null || todayLevel.size() <= 0 ? null : roundVal);//(double) Math.round(todayLevel.get(1, double.class)));  //일 최대 수심
        getVal = todayLevel.get(2, double.class);
        roundVal = getVal == null ? null : (double) Math.round(getVal);
        depthReport.put("todayMin", todayLevel == null || todayLevel.size() <= 0 ? null : roundVal);//(double) Math.round(todayLevel.get(2, double.class)));  //일 최소 수심
        cal.add(Calendar.DATE, -1);
        Date levelToDt = cal.getTime();

        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Tuple beforeLevel = reportRepositoryCustom.findDayLevel(cal.getTime(), levelToDt);
        getVal = beforeLevel.get(0, double.class);
        roundVal = getVal == null ? null : (double) Math.round(getVal);
        depthReport.put("beforeAvg", beforeLevel == null || beforeLevel.size() <= 0 ? null : roundVal);//(double) Math.round(beforeLevel.get(0, double.class)));    //전일 평균 수심
        getVal = beforeLevel.get(1, double.class);
        roundVal = getVal == null ? null : (double) Math.round(getVal);
        depthReport.put("beforeMax", beforeLevel == null || beforeLevel.size() <= 0 ? null : roundVal);//(double) Math.round(beforeLevel.get(1, double.class)));    //전일 최대 수심
        getVal = beforeLevel.get(2, double.class);
        roundVal = getVal == null ? null : (double) Math.round(getVal);
        depthReport.put("beforeMin", beforeLevel == null || beforeLevel.size() <= 0 ? null : roundVal);//(double) Math.round(beforeLevel.get(2, double.class)));    //전일 최소 수심
        getVal = depthReport.get("todayMax") == null || depthReport.get("beforeMax") == null ? null : depthReport.get("todayMax") - depthReport.get("beforeMax");
        depthReport.put("compare", beforeLevel == null || beforeLevel.size() <= 0 ? null : getVal);//(double) Math.round(beforeLevel.get(1, double.class)) - (double) Math.round(todayLevel.get(1, double.class)));   //전일 최대 대비

        result.put("depthReport", depthReport);

        /* 수압 현황 */
        Map<String, Object> pressReport = new HashMap<>();
        //당일 23:59:59
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.SECOND, -1);

        //수압 보고서 통계 구분
        String[] statArr = {"avg", "max", "min"};
        //A, B 순서대로 조회
        for (String line : lineArr) {
            List<Tuple> pressList = reportRepositoryCustom.findDayPress(line, date, cal.getTime());
            Map<String, Object> statMap = new HashMap<>();

            for (int i = 0; i < statArr.length; i++) {
                Map<String, Double> pressDataMap = new HashMap<>();
                for (Tuple t : pressList) {
                    String pressName = t.get(0, String.class);
                    pressDataMap.put(pressName.replace("_" + line, ""), roundOff((double) Math.round(t.get(i + 1, double.class) * 100) * 0.01, 2));
                }
                statMap.put(statArr[i], pressDataMap);
            }
            pressReport.put(line, statMap);
        }

        result.put("pressReport", pressReport);

        /* 통신 현황 */
        Map<String, Object> teleReport = new HashMap<>();
        try {
            String[] teleArr = { "전용회선", "무선통신", "통신이상" };
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.SECOND, -1);
            for (String line : lineArr) {
                //통신 상태 리스트
                //List<Tele> teleNonList = reportRepositoryCustom.findDayTele("TELE", "전용회선", line, date, cal.getTime());
                //List<ReportDTO> teleNonList = reportRepositoryCustom.findDayTele("TELE", "전용회선", line, date, cal.getTime());
                List<Tagmap> tagmapTeleList = tagmapRepositoy.findByKindAndLine("TELE", line);
                List<String> tagIdList = new ArrayList<>();
                for (Tagmap tag : tagmapTeleList) {
                    tagIdList.add(tag.getId());
                }
                List<Tele> teleNonList = teleRepository.findByIdInAndColtDtBetween(tagIdList, date, cal.getTime());

                Map<String, Object> teleKindMap = new HashMap<>();

                Map<String, Object> timeRangeMap = new HashMap<>();
                //timeRangeMap.put("전용회선", new HashMap<String, Object>());
                for (String strTele : teleArr) {
                    Map<String, Object> teleTimeMap = new HashMap<>();

                    Map<String, Object> teleRangeMap = new HashMap<>();

                    Date compareDt = new Date();
                    Date beforeDt = new Date();
                    //List<String> strList = new ArrayList<>();
                    //String[] timeRangeArr = { "", "" };

                    for (Tele teleData : teleNonList) {
                        if (!strTele.equals(teleData.getVal())) continue;

                        String id = teleData.getId().replace("_" + line, "");
                        int hour = teleData.getColtDt().getHours();
                        int minutes = teleData.getColtDt().getMinutes();
                        String time = (hour < 10 ? "0" + hour : hour) + ":" + (minutes < 10 ? "0" + minutes : minutes);

//                        if (teleTimeMap.containsKey(id)) {
//                            List<String> timeList = (List<String>) teleTimeMap.get(id);
//                            timeList.add(time);
//                            teleTimeMap.put(id, timeList);
//                        } else {
//                            List<String> timeList = new ArrayList<>();
//                            timeList.add(time);
//                            teleTimeMap.put(id, timeList);
//                        }

                        /* 통신 현황 시간 리스트 */
                        List<String[]> timeArrList = (List<String[]>) teleRangeMap.get(id);
                        if(timeArrList != null) {
                            String[] timeRangeArr = timeArrList.get(timeArrList.size() - 1);
                            if (timeRangeArr[0].isEmpty()) {
                                timeRangeArr[0] = time;
                            } else {
                                compareDt = addMin(beforeDt, 1);
                                if (compareDt.getHours() == teleData.getColtDt().getHours()
                                        && compareDt.getMinutes() == teleData.getColtDt().getMinutes()) {
                                    timeRangeArr[1] = time;
                                } else {
                                    //시간 범위 저장
                                    if (timeRangeArr[1].isEmpty()) {
                                        timeArrList.add(new String[] { timeRangeArr[0] });
                                    } else {
                                        timeArrList.add(new String[] { timeRangeArr[0] + "~" + timeRangeArr[1] });
                                    }
                                    timeArrList.remove(timeRangeArr);
                                    timeArrList.add(new String[] { time, "" });
                                }
                            }
                            teleRangeMap.put(id, timeArrList);
                        } else {
                            List<String[]> tempList = new ArrayList<>();
                            tempList.add(new String[] { time, "" });
                            teleRangeMap.put(id, tempList);
                        }
                        beforeDt = teleData.getColtDt();
                    }

                    /* 통신 현황 시간 리스트 (마지막 정리) */
                    for (String timeId : teleRangeMap.keySet()) {
                        List<String[]> timeList = (List<String[]>) teleRangeMap.get(timeId);
                        String[] lastTimeArrList = timeList.get(timeList.size() - 1);
                        if (!lastTimeArrList[0].isEmpty()) {
                            if (lastTimeArrList[1].isEmpty()){
                                timeList.add(new String[] { lastTimeArrList[0] });
                            } else {
                                timeList.add(new String[] { lastTimeArrList[0] + "~" + lastTimeArrList[1] });
                            }
                        }
                        timeList.remove(lastTimeArrList);
                    }

                    //teleKindMap.put(strTele, teleTimeMap);
                    timeRangeMap.put(strTele, teleRangeMap);
                }

                /* 전용회선 */
//                Map<String, Object> leasedMap = new HashMap<>();
//
//                for (String teleValKind : timeRangeMap.keySet()) {
//                    Map<String, Object> thisMap = (Map<String, Object>) timeRangeMap.get(teleValKind);
//                    if (teleValKind.equals("전용회선")) {
//                        leasedMap = thisMap;
//                        continue;
//                    }
//
//                    for (String teleId : thisMap.keySet()){
//                        List<String[]> timeList = (List<String[]>) thisMap.get(teleId);
//                        for (String[] timeArr : timeList) {
//
//                        }
//                    }
//                }
//                timeRangeMap.put("전용회선", leasedMap);

                //teleReport.put(line, teleKindMap);
                teleReport.put(line, timeRangeMap);
            }

            /*
            Map<String, Object> teleKindMap = new HashMap<>();
            for (String strTele : teleArr) {
//                    String[] timeArr = { "", "" };
//                    int lasthour = 0;
//                    int lastMin = 0;
                Map<String, Object> teleTimeMap = new HashMap<>();
                for (ReportDTO reportData : teleNonList) {
                    if (!strTele.equals(reportData.status)) continue;

                    String id = reportData.getId().replace("_" + line, "");
                    int hour = reportData.getLogTime().getHours();
                    int minutes = reportData.getLogTime().getMinutes();
                    String time = (hour < 10 ? "0" + hour : hour) + ":" + (minutes < 10 ? "0" + minutes : minutes);

//                        if (timeArr[0].isEmpty()) {
//                            System.out.println("first");
//                            timeArr[0] = time;
//                            timeArr[1] = time;
//                            lasthour = hour;
//                            lastMin = minutes;
//                        } else if (!timeArr[1].isEmpty() && ((hour == lasthour && minutes == lastMin + 1) || (hour == lasthour + 1 && minutes == lastMin))) {
//                            System.out.println("second");
//                            timeArr[1] = time;
//                            lasthour = hour;
//                            lastMin = minutes;
//                        } else {
//                            if (!timeArr[0].isEmpty() && !timeArr[1].isEmpty()) {
//                                System.out.println("add");
//                                strList.add(timeArr);
//                                timeArr[0] = "";
//                                timeArr[1] = "";
//                                lasthour = 0;
//                                lastMin = 0;
//                            }
//                        }

                    if (teleTimeMap.containsKey(id)) {
                        List<String> timeList = (List<String>) teleTimeMap.get(id);
                        timeList.add(time);
                        teleTimeMap.put(id, timeList);
                    } else {
                        List<String> timeList = new ArrayList<>();
                        timeList.add(time);
                        teleTimeMap.put(id, timeList);
                    }
                }
//                    if (!timeArr[0].isEmpty() && !timeArr[1].isEmpty()) {
//                        System.out.println("fin add");
//                        strList.add(timeArr);
//                    }
                teleKindMap.put(strTele, teleTimeMap);
            }
            teleReport.put(line, teleKindMap);
        }
//            for (String[] strA : strList) {
//                System.out.println(strA[0] + "~" + strA[1]);
//            }
        */
        } catch (Exception e) {
            logger.warning(e.toString());
            e.printStackTrace();
        }
        result.put("teleReport", teleReport);

        return result;
    }

    private Date addMin(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    private double roundOff(double v, int until) {
        return Double.parseDouble(String.format("%." + until + "f", v));
    }
}
