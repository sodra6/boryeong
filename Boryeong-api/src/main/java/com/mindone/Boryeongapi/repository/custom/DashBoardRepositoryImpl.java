package com.mindone.Boryeongapi.repository.custom;

import com.mindone.Boryeongapi.domain.entity.main.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class DashBoardRepositoryImpl implements DashBoardRepositoryCustom {

    @Autowired
    @PersistenceContext(unitName = "mainEntityManagerFactory")
    private EntityManager em;

    private final JPAQueryFactory mianQueryFactory;

    QTagmap tagmap = QTagmap.tagmap;
    QFlow flow = QFlow.flow;
    QPress press = QPress.press;

    @Override
    public Double findSupply(Date fromDt, Date toDt) {
        Double result = 0.0;
        try{
            result = mianQueryFactory.query().select(flow.val.sum())
                    .from(flow, tagmap)
                    .where(tagmap.kind.eq("FLOW")
                            .and(tagmap.line.eq("A"))
                            .and(flow.id.eq(tagmap.id))
                            .and(flow.coltDt.between(fromDt, toDt)))
                    .fetchOne();
        } catch (Exception e){
            System.out.println(e);
        }
        if (result == null) result = 0.0;
        return (double)Math.round(result);
    }

    @Override
    public Double findAvgPress(Date fromDt, Date toDt) {
        Double result = 0.0;
        try{
            /* from ~ to 기간동안 수압평균 */
            result = mianQueryFactory.query()
                    .select(press.val.avg())
                    .from(press)
                    .where(press.coltDt.between(fromDt, toDt))
                    .fetchOne();
        } catch (Exception e){
            System.out.println(e.toString());
        }
        if (result == null) result = 0.0;
        return (double)Math.round(result*10)/10;
    }

    @Override
    public Double findDepth(int hour) {
        /* hour시간 전의 데이터 중 가장 최근 값 */
        String jpql = " SELECT  VAL" +
                      " FROM    DEPTH" +
                      " WHERE   COLT_DT BETWEEN SYSDATE - 1 AND SYSDATE - (:hour/24)" +
                      " AND     ROWNUM <= 1";

        Query query = em.createNativeQuery(jpql);
        query.setParameter("hour", hour);
        String result = "0.0";
        Object obj = query.getResultStream().findFirst().orElse(null);
        if (obj != null) result = obj.toString();
        return (double)Math.round(Double.parseDouble(result));
    }

    /* Hmi에서 직접... */

//    public Double findSupply(Date fromDt, Date toDt) {
//        Double result = 0.0;
//        try{
//            /* (from ~ to 기간동안 적산값의 [max - min]) */
//            result = queryFactory.query().select(hmi.val.castToNum(double.class).max().subtract(hmi.val.castToNum(double.class).min()))
//                    .from(hmi, tagmap)
//                    .where(tagmap.line.eq("A")
//                            .and(tagmap.kind.eq("FLOW"))
//                            .and(hmi.logTime.between(fromDt, toDt))
//                            .and(hmi.tagId.eq(tagmap.tagId)))
//                    .fetchOne();
//
//        } catch (Exception e){
//            System.out.println(e);
//        }
//        if (result == null) result = 0.0;
//        return (double)Math.round(result);
//    }
//
//    public Double findAvgPress(Date fromDt, Date toDt) {
//        Double result = 0.0;
//        try{
//            /* from ~ to 기간동안 수압평균 */
//            result = queryFactory.query()
//                    .select(hmi.val.castToNum(double.class).avg())
//                    .from(hmi, tagmap)
//                    .where(tagmap.kind.eq("PRESS")
//                            .and(hmi.logTime.between(fromDt, toDt))
//                            .and(hmi.tagId.eq(tagmap.tagId)))
//                    .fetchOne();
//        } catch (Exception e){
//            System.out.println(e.toString());
//        }
//        if (result == null) result = 0.0;
//        return (double)Math.round(result*10)/10;
//    }
//
//    public Double findDepth(int hour) {
//        /* hour시간 전의 데이터 중 가장 최근 값 */
//        String jpql = "WITH temp AS (" +
//                " SELECT    MAX(aa.LOG_TIME) MAX_TIME, aa.TAG_ID" +
//                " FROM      HMI aa, TAGMAP bb" +
//                " WHERE     1=1" +
//                " AND       bb.KIND = 'DEPTH'" +
//                " AND       aa.LOG_TIME BETWEEN SYSDATE - 1 AND SYSDATE - (:hour / 24)" +
//                " AND       aa.TAG_ID = bb.TAG_ID" +
//                " GROUP BY  aa.TAG_ID" +
//                ")" +
//                " SELECT a.VAL" +
//                " FROM HMI a, temp b" +
//                " WHERE a.TAG_ID = b.TAG_ID" +
//                " AND a.LOG_TIME = b.MAX_TIME";
//
//        Query query = em.createNativeQuery(jpql);
//        query.setParameter("hour", hour);
//        String result = "0.0";
//        Object obj = query.getResultStream().findFirst().orElse(null);
//        if (obj != null) result = obj.toString();
//        return (double)Math.round(Double.parseDouble(result));
//    }
//
    /*
    public List<StatusTmpDTO> findStatus(Date fromDt, Date toDt) {
        return queryFactory.query()
                .select(Projections.constructor(StatusTmpDTO.class, tagmap.id.as("id"), tagmap.line.as("line"), tagmap.kind.as("kind"), hmi.val.as("val")))
                .from(hmi, tagmap)
                .where(hmi.tagId.eq(tagmap.tagId)
                        .and(hmi.logTime.between(fromDt, toDt)))
                .fetch();
    }
    */

    @Override
    public List<Object[]> findAllData(String table, String kind, String direction, Date fromDate, Date toDate, List<Tagmap> columns, List<Setting> settings) throws ParseException {
        List<Object[]> result;

        String jpql = "SELECT logDate, logTime ";
        for (Tagmap taginfo : columns) {
            jpql += ", " + taginfo.getId();
        }
        jpql += " FROM (" +
                " SELECT TO_CHAR(a.COLT_DT, 'yyyy.mm.dd') logDate" +
                " , TO_CHAR(a.COLT_DT, 'hh24:mi') logTime";
        for (Tagmap taginfo : columns) {
            jpql += " , CASE WHEN EXISTS (SELECT VAL FROM " + table + " WHERE COLT_DT = a.COLT_DT AND ID = '" + taginfo.getId() + "')";
            jpql += " THEN (SELECT VAL FROM " + table + " WHERE COLT_DT = a.COLT_DT AND ID = '" + taginfo.getId() + "')";
            jpql += " ELSE " + (kind.equals("VALVE") || kind.equals("TELE") ? "'0'" : "0") +
                    " END AS " + taginfo.getId();
        }

        jpql += " FROM " + table + " a, TAGMAP b" +
                " WHERE 1=1" +
                " AND b.NAME IN (SELECT aa.NAME" +
                "               FROM TAGMAP aa" +
                "               WHERE 1=1" +
                "               AND aa.KIND = :kind";
        if (!kind.equals("DEPTH")) {
            jpql += "               AND aa.LINE = :direction";
        }
        jpql += " ) AND a.COLT_DT BETWEEN :fromDate AND :toDate" +
                " AND a.ID = b.ID" +
                " )" +
                " GROUP BY logDate, logTime";

        for (Tagmap taginfo : columns) {
            jpql += ", " + taginfo.getId();
        }
        jpql += " ORDER BY logDate, logTime DESC";
        Query query = em.createNativeQuery(jpql);
        query.setParameter("kind", kind);
        if (!kind.equals("DEPTH")) {
            query.setParameter("direction", direction);
        }
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        result = query.getResultList();

        return result;
    }
    /*
    public List<Object[]> findAllData(String kind, String direction, Date fromDate, Date toDate, List<Tagmap> columns, List<Setting> settings) throws ParseException {
        List<Object[]> result;

        String jpql = "SELECT logDate, logTime ";
        for (Tagmap taginfo : columns) {
            jpql += ", " + taginfo.getId();
        }
        jpql += " FROM (" +
                " SELECT TO_CHAR(a.LOG_TIME, 'yyyy.mm.dd') logDate" +
                " , TO_CHAR(a.LOG_TIME, 'hh24:mi') logTime";

        for (Tagmap taginfo : columns) {
            jpql += " , CASE WHEN EXISTS (SELECT VAL FROM HMI WHERE LOG_TIME = a.LOG_TIME AND TAG_ID = '" + taginfo.getTagId() + "')";
            jpql += " THEN (SELECT VAL FROM HMI WHERE LOG_TIME = a.LOG_TIME AND TAG_ID = '" + taginfo.getTagId() + "')";
            jpql += " ELSE '0' END AS " + taginfo.getId();
        }

        jpql += " FROM HMI a, TAGMAP b" +
                " WHERE 1=1" +
                " AND b.NAME IN (SELECT aa.NAME" +
                "               FROM TAGMAP aa" +
                "               WHERE 1=1" +
                "               AND aa.KIND = :kind";
        if (!kind.equals("DEPTH")) {
            jpql += "               AND aa.LINE = :direction";
        }
        jpql += " ) AND a.LOG_TIME BETWEEN :fromDate AND :toDate" +
                " AND a.TAG_ID = b.TAG_ID" +
                " )" +
                " GROUP BY logDate, logTime";

        for (Tagmap taginfo : columns) {
            jpql += ", " + taginfo.getId();
        }

        jpql += " ORDER BY logDate, logTime DESC";

        Query query = em.createNativeQuery(jpql);
        query.setParameter("kind", kind);
        if (!kind.equals("DEPTH")) {
            query.setParameter("direction", direction);
        }
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        result = query.getResultList();

        return result;
    }
     */

    @Override
    public List<Press> findRecentPress(String line){
        List<Press> result;
        String jpql = "SELECT *" +
                " FROM PRESS" +
                " WHERE (ID, COLT_DT) IN (" +
                "   SELECT  ID, MAX(COLT_DT)" +
                "   FROM    PRESS" +
                "   WHERE   ID IN (SELECT ID FROM TAGMAP WHERE LINE = :line AND KIND = 'PRESS')" +
                "   GROUP BY ID" +
                "   )";
        Query query = em.createNativeQuery(jpql, Press.class);
        query.setParameter("line", line);
        result = query.getResultList();

        return result;
    }

    @Override
    public List<Valve> findRecentValve(String line){
        List<Valve> result;
        String jpql = "SELECT *" +
                " FROM VALVE" +
                " WHERE (ID, COLT_DT) IN (" +
                "   SELECT  ID, MAX(COLT_DT)" +
                "   FROM    VALVE" +
                "   WHERE   ID IN (SELECT ID FROM TAGMAP WHERE LINE = :line AND KIND = 'VALVE')" +
                "   GROUP BY ID" +
                "   )";
        Query query = em.createNativeQuery(jpql, Valve.class);
        query.setParameter("line", line);
        result = query.getResultList();

        return result;
    }

    @Override
    public List<Tele> findRecentTele(String line) {
        List<Tele> result;
        String jpql = "SELECT *" +
                " FROM TELE" +
                " WHERE (ID, COLT_DT) IN (" +
                "   SELECT  ID, MAX(COLT_DT)" +
                "   FROM    TELE" +
                "   WHERE   ID IN (SELECT ID FROM TAGMAP WHERE LINE = :line AND KIND = 'TELE')" +
                "   GROUP BY ID" +
                "   )";
        Query query = em.createNativeQuery(jpql, Tele.class);
        query.setParameter("line", line);
        result = query.getResultList();

        return result;
    }
}
