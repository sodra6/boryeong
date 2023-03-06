package com.mindone.Boryeongapi.repository.custom;

import com.mindone.Boryeongapi.domain.dto.ReportDTO;
import com.mindone.Boryeongapi.domain.entity.main.Tele;
import com.mindone.Boryeongapi.domain.entity.main.Valve;
import com.querydsl.core.Tuple;

import java.util.Date;
import java.util.List;

public interface ReportRepositoryCustom {

    double findDaySupply(String id, Date fromDt, Date toDt);

    double findAvgSupply(String id);

    List<ReportDTO> findDataNonTimeList(String kind, String strDiv, String line, Date fromDt, Date toDt);

    Tuple findDayLevel(Date fromDt, Date toDt);

    List<Tuple> findDayPress(String line, Date fromDt, Date toDt);

    List<Tele> findDayTele(String kind, String strDiv, String line, Date fromDt, Date toDt);

    List<Tele> findTeleNonTimeList(String strDiv, String line, Date fromDt, Date toDt);

    List<Valve> findValveNonTimeList(String strDiv, String line, Date fromDt, Date toDt);
}
