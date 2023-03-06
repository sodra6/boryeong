package com.mindone.Boryeongapi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindone.Boryeongapi.domain.dto.StatusDTO;
import com.mindone.Boryeongapi.domain.dto.TimeRangeDTO;
import com.mindone.Boryeongapi.domain.entity.main.Depth;
import com.mindone.Boryeongapi.domain.entity.main.Flow;
import com.mindone.Boryeongapi.domain.entity.main.Press;
import com.mindone.Boryeongapi.domain.entity.main.Setting;
import com.mindone.Boryeongapi.domain.entity.main.Tagmap;
import com.mindone.Boryeongapi.domain.entity.main.Tele;
import com.mindone.Boryeongapi.domain.entity.main.Valve;
import com.mindone.Boryeongapi.repository.custom.DashBoardRepositoryCustom;
import com.mindone.Boryeongapi.repository.main.DepthRepository;
import com.mindone.Boryeongapi.repository.main.FlowRepository;
import com.mindone.Boryeongapi.repository.main.PressRepository;
import com.mindone.Boryeongapi.repository.main.SettingRepository;
import com.mindone.Boryeongapi.repository.main.TagmapRepositoy;

@Service
//@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
public class DashBoardService{

    @Autowired
    TagmapRepositoy tagmapRepositoy;

    @Autowired
    SettingRepository settingsRepository;

    @Autowired
    DashBoardRepositoryCustom dashBoardRepositoryCustom;

    @Autowired
    FlowRepository flowRepository;

    @Autowired
    DepthRepository depthRepository;

    @Autowired
    PressRepository pressRepository;

    public Map<String, Object> getDashBoard() {
        //결과
        Map<String, Object> result = new HashMap<>();

        //대쉬보드(왼쪽)
        TimeRangeDTO timeRange = new TimeRangeDTO();
        //대쉬보드(오른쪽)
        StatusDTO status = new StatusDTO();
        try {
            //날짜 형식
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            //시간을 빼주기 위해 생성
            Calendar cal = Calendar.getInstance();
            //현재 시간
            Date now = new Date();
            //오늘 날짜
            Date today = new Date();
            //00시 00분으로 맞춰주기 위한 작업
            String nowTimeStr = dateFormat.format(now);
            try {
                today = dateFormat.parse(nowTimeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            /* 평균공급량 */
            timeRange.todaySupply = dashBoardRepositoryCustom.findSupply(today, now);
            cal.setTime(now);
            cal.add(Calendar.DATE, -7);
            timeRange.weekSupply = dashBoardRepositoryCustom.findSupply(cal.getTime(), now);
            cal.setTime(now);
            cal.add(Calendar.MONTH, -1);
            timeRange.monthSupply = dashBoardRepositoryCustom.findSupply(cal.getTime(), now);
            /* 평균수압 */
            timeRange.todayPress = dashBoardRepositoryCustom.findAvgPress(today, now);
            cal.setTime(now);
            cal.add(Calendar.DATE, -7);
            timeRange.weekPress = dashBoardRepositoryCustom.findAvgPress(cal.getTime(), now);
            cal.setTime(now);
            cal.add(Calendar.MONTH, -1);
            timeRange.monthPress = dashBoardRepositoryCustom.findAvgPress(cal.getTime(), now);
            /* 수심 */
            timeRange.depthNow = dashBoardRepositoryCustom.findDepth(0);
            timeRange.depth1h = dashBoardRepositoryCustom.findDepth(1);
            timeRange.depth2h = dashBoardRepositoryCustom.findDepth(2);


            //기준값 리스트
            List<Setting> settings = settingsRepository.findAll();

            cal.setTime(now);
            cal.add(Calendar.HOUR, -3);
            //유량 상태
            List<Tagmap> flowTagmapList = tagmapRepositoy.findByKindAndLine("FLOW","A");
            Flow flowData = flowRepository.findFirstByIdAndColtDtBetweenOrderByColtDtDesc(flowTagmapList.get(0).getId(), cal.getTime(), now);
            if (flowData != null) {
                Setting flowSetting = settings.stream().filter(a -> a.getId().equals(flowData.getId())).findAny().orElse(null);
                double flowVal = flowData.getVal();
                status.flow = flowSetting != null ? (flowVal >= flowSetting.getDangerVal() ? "이상" : (flowVal >= flowSetting.getWarningVal() ? "관심" : "정상")) : "이상";
            } else {
                status.flow = "이상";
            }

            cal.setTime(now);
            cal.add(Calendar.MINUTE, -1);
            //수심 상태
            Depth depthData = depthRepository.findFirstByColtDtBetweenOrderByColtDtDesc(cal.getTime(), now);
            if (depthData != null) {
                Setting depthSetting = settings.stream().filter(a -> a.getId().equals(flowData.getId())).findAny().orElse(null);
                double depthVal = depthData.getVal();
                status.depth = depthSetting != null ? (depthVal >= depthSetting.getDangerVal() ? "이상" : (depthVal >= depthSetting.getWarningVal() ? "관심" : "정상")) : "이상";
            } else {
                status.depth = "이상";
            }

            String[] lineArr = { "A", "B" };

            //수압 상태
            for (String line : lineArr) {
                List<Press> pressList = dashBoardRepositoryCustom.findRecentPress(line);
                String pressStatus = "정상";
                for (Press press : pressList) {
                    Setting pressSetting = settings.stream().filter(a -> a.getId().equals(press.getId())).findAny().orElse(null);
                    if (pressSetting != null) {
                        double pressVal = press.getVal();
                        if (pressVal >= pressSetting.getDangerVal()) {
                            pressStatus = "이상";
                        } else if (pressVal >= pressSetting.getWarningVal() && !pressStatus.equals("이상")) {
                            pressStatus = "관심";
                        }
                    }
                }
                if (line.equals("A")) {
                    status.brPress = pressStatus;
                } else if (line.equals("B")) {
                    status.wsPress = pressStatus;
                }
            }

            //밸브 상태
            for (String line : lineArr) {
                List<Valve> valveList = dashBoardRepositoryCustom.findRecentValve(line);
                String valveStatus = "Open";
                for (Valve valve : valveList) {
                    if (!valve.getVal().equalsIgnoreCase("OPEN")) {
                        valveStatus = "Close";
                        break;
                    }
                }
                if (line.equals("A")) {
                    status.brValve = valveStatus;
                } else if (line.equals("B")) {
                    status.wsValve = valveStatus;
                }
            }

            //통신 상태
            for (String line : lineArr) {
                List<Tele> teleList = dashBoardRepositoryCustom.findRecentTele(line);
                boolean isLeased = true;
                int teleCnt = 0;
                for (Tele tele : teleList) {
                    if (tele.getVal().equals("전용회선")) {
                        teleCnt++;
                        break;
                    } else if (tele.getVal().equals("무선통신")){
                        teleCnt++;
                        isLeased = false;
                    } else {
                        isLeased = false;
                    }
                }
                if (line.equals("A")) {
                    status.brTele = isLeased ? "전용회선" : "무선통신(" + teleCnt + ")";
                } else if (line.equals("B")) {
                    status.wsTele = isLeased ? "전용회선" : "무선통신(" + teleCnt + ")";
                }
            }

            /* HMI 직접 접근 방법 */
            /*
            //값
            List<StatusTmpDTO> values = dashBoardRepositoryCustom.findStatus(cal.getTime(), now);

            String brPressStatus = "정상";
            String wsPressStatus = "정상";

            int brOpenCnt = 0;
            int brTeleCnt = 0;
            int wsOpenCnt = 0;
            int wsTeleCnt = 0;
            boolean brIsLeased = false;
            boolean wsIsLeased = false;
            for (StatusTmpDTO data : values) {
                switch (data.kind){
                    case "VALVE":
                        if (data.val.equals("OPEN")){
                            if (data.line.equals("A")) brOpenCnt++;
                            else if(data.line.equals("B")) wsOpenCnt++;
                        }
                        break;
                    case "PRESS":
                        Setting setPress = settings.stream().parallel().filter(a -> a.getId().contains(data.id) && a.getKind().equals(data.kind) && a.getLine().equals(data.line)).findAny().orElse(null);
                        double warnVal = setPress.getWarningVal();
                        double dangVal = setPress.getDangerVal();
                        double dataVal = Double.parseDouble(data.val);

                        if (dataVal >= dangVal){
                            if (data.line.equals("A")) brPressStatus = "이상";
                            else if (data.line.equals("B")) wsPressStatus = "이상";
                        } else if (dataVal >= warnVal && !brPressStatus.equals("이상")){
                            if (data.line.equals("A")) brPressStatus = "관심";
                            else if (data.line.equals("B")) wsPressStatus = "관심";
                        }
                        break;
                    case "TELE":
                        if (data.val.equals("전용회선")){
                            if (data.line.equals("A")) brIsLeased = true;
                            else if (data.line.equals("B")) wsIsLeased = true;
                        }
                        else {
                            if (data.line.equals("A")) brTeleCnt++;
                            else if (data.line.equals("B")) wsTeleCnt++;
                        }
                        break;
                }
            }
            //보령방향 상태값
            status.brValve = brOpenCnt == 10 ? "Open" : "Close";
            status.brPress = brPressStatus;
            status.brTele = brIsLeased ? "전용회선" : "무선통신(" + brTeleCnt + ")";
            //원산도방향 상태값
            status.wsValve = wsOpenCnt == 10 ? "Open" : "Close";
            status.wsPress = wsPressStatus;
            status.wsTele = wsIsLeased ? "전용회선" : "무선통신(" + wsTeleCnt + ")";

            Setting setLev1 = settings.stream().parallel().filter(a -> a.getKind().equals("DEPTH") && a.getId().contains("1") ).findAny().orElse(null);
            Setting setLev2 = settings.stream().parallel().filter(a -> a.getKind().equals("DEPTH") && a.getId().contains("2") ).findAny().orElse(null);
            double level1h = timeRange.depth1h - timeRange.depthNow;
            double level2h = timeRange.depth2h - timeRange.depthNow;

            //수위 상태값
            status.depth = level1h >= setLev1.getDangerVal() ? "이상" : (level1h >= setLev1.getWarningVal() ? "관심" : "정상");
            status.depth = level2h >= setLev2.getDangerVal() ? "이상" : (level2h >= setLev2.getWarningVal() && !status.depth.equals("이상")
                    ? "관심" : (!status.depth.equals("이상") && !status.depth.equals("관심")
                    ? "정상" : status.depth));

            Setting setFlow = settings.stream().parallel().filter(a -> a.getKind().equals("FLOW")).findAny().orElse(null);
            StatusTmpDTO flowA = values.stream().parallel().filter(a -> a.kind.equals("FLOW") && a.line.equals("A")).findAny().orElse(null);
            StatusTmpDTO flowB = values.stream().parallel().filter(a -> a.kind.equals("FLOW") && a.line.equals("B")).findAny().orElse(null);
            double warnFlowVal = setFlow == null ? 0.0 : setFlow.getWarningVal();
            double dangFLowVal = setFlow == null ? 0.0 : setFlow.getDangerVal();
            double dataFlowVal = (flowA == null ? 0.0 : Double.parseDouble(flowA.val)) - (flowB == null ? 0.0 : Double.parseDouble(flowB.val));

            //유량 상태값
            status.flow = dataFlowVal >= dangFLowVal ? "이상" : (dataFlowVal >= warnFlowVal ? "관심" : "정상");
             */

            result.put("timeRange", timeRange);
            result.put("statusRange", status);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Object> getPopupData(String kind, String direction, String fromDate, String toDate) throws ParseException {
        //결과
        Map<String, Object> result = new HashMap<>();
        //시간별 데이터 리스트
        List<Map<String, Object>> listMap = new ArrayList<>();

        //날짜 포맷
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //시작 날짜
        Date fromDt = dateFormat.parse(fromDate + " 00:00");
        //종료 날짜
        Date toDt = dateFormat.parse(toDate + " 23:59");

        //Tag 조회
        List<Tagmap> columns;

        if (kind.equals("DEPTH")){
            columns = tagmapRepositoy.findByKindOrderBySort(kind);
        } else {
            columns = tagmapRepositoy.findByKindAndLineOrderBySort(kind, direction);
        }

        //기준값 조회
        List<Setting> settings = null;
        if (!kind.equals("VALVE") || !kind.equals("TELE")) {
            if (kind.equals("DEPTH")){
                settings = settingsRepository.findByKind(kind);
            } else {
                settings = settingsRepository.findByKindAndLine(kind, direction);
            }
        }

        //데이터 리스트
        List<Object[]> rows = dashBoardRepositoryCustom.findAllData(kind, kind, direction, fromDt, toDt, columns, settings);

        if (rows == null) return null;

        //데이터 개수
        int rowCnt = rows.size();

        //상태 현황
        Map<String, String> totalStatus = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            totalStatus.put(kind + (i + 1), "normal");
        }

        for (int i = 0; i < rowCnt; i++) {
            //로우 데이터
            Object[] datas = rows.get(i);
            //리스트에 저장하기 위한 map 객체
            Map<String, Object> map = new HashMap<>();

            for (int j = 0; j < datas.length; j++) {
                if (j == 0) map.put("date", datas[j]);        //날짜
                else if (j == 1) map.put("time", datas[j]);   //시간
                else {
                    //값(String)
                    String strValue = String.valueOf(datas[j]);
                    //데이터명
                    String colName = kind + (j - 1);
                    String colId = columns.get(j - 2).getId();
                    //map 객체(값, 상태)
                    Map<String, String> valStatus = new HashMap<>();

                    //밸브나 통신상태인 경우
                    if (kind.equals("VALVE") || kind.equals("TELE")) {
                        valStatus.put("val", strValue);
                        valStatus.put("status", strValue);

                        //상태 현황 저장
                        if (strValue.equals("CLOSE")){
                            totalStatus.put(colName, strValue);
                        }
                    } else {
                        //해당 기준값
                        Setting setting = settings.stream().filter(a -> a.getId().equals(colId)).findAny().orElse(null);
                        //관심 기준값
                        double warnVal = setting == null ? 0.0 : setting.getWarningVal();
                        //이상 기준값
                        double dangVal = setting == null ? 0.0 : setting.getDangerVal();
                        //해당 데이터 값
                        double val = strValue == null ? 0.0 :Double.parseDouble(strValue);
                        //기준값에 따른 상태 값
                        String strStatus = val >= dangVal ? "danger" : (val >= warnVal ? "warn" : "normal");

                        valStatus.put("val", String.valueOf(val));
                        valStatus.put("status", strStatus);

                        //상태 현황 저장
                        if (strStatus.equals("danger")) {
                            totalStatus.put(colName, "danger");
                        } else if (strStatus.equals("warn")) {
                            if (totalStatus.get(colName).equals("danger")) {
                                totalStatus.put(colName, "warn");
                            }
                        }
                    }
                    map.put(colName, valStatus);
                }
            }
            listMap.add(map);
        }

        result.put("totalStatus", totalStatus); //상태 현황
        result.put("statusList", listMap);      //데이터 리스트

        return result;
    }
}
