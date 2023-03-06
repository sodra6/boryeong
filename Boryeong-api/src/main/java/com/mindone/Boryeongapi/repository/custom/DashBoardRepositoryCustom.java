package com.mindone.Boryeongapi.repository.custom;

import com.mindone.Boryeongapi.domain.entity.main.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface DashBoardRepositoryCustom {

    Double findSupply(Date fromDt, Date toDt);

    Double findAvgPress(Date fromDt, Date toDt);

    Double findDepth(int hour);

    //List<StatusTmpDTO> findStatus(Date fromDt, Date toDt);

    List<Object[]> findAllData(String table, String kind, String direction, Date fromDate, Date toDate, List<Tagmap> columns, List<Setting> settings) throws ParseException;

    List<Press> findRecentPress(String line);

    List<Valve> findRecentValve(String line);

    List<Tele> findRecentTele(String line);

    //Map<String, Object> findRecentData(Map<String, Object> param, List<Setting> settings);
}
