package com.mindone.Boryeongapi.repository.custom;

import com.mindone.Boryeongapi.domain.dto.ReportDTO;
import com.mindone.Boryeongapi.domain.entity.hmi.QHmi;
import com.mindone.Boryeongapi.domain.entity.main.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom{

    @Autowired
    @PersistenceContext(unitName = "mainEntityManagerFactory")
    private EntityManager em;

    private final JPAQueryFactory mianQueryFactory;

    QHmi hmi = QHmi.hmi;
    QTagmap tagmap = QTagmap.tagmap;
    QFlow flow = QFlow.flow;
    QTele tele = QTele.tele;
    QValve valve = QValve.valve;
    QDepth depth = QDepth.depth;
    QPress press = QPress.press;

    /* 일 공급량 */
    public double findDaySupply(String id, Date fromDt, Date toDt){
        Double result = mianQueryFactory.query().select(flow.val.sum())
                .from(flow)
                .where(flow.id.eq(id)
                        .and(flow.coltDt.between(fromDt, toDt)))
                .fetchOne();

        if (result == null) result = 0.0;
        return (double)Math.round(result);
    }

    /* 일 평균 공급량 */
    public double findAvgSupply(String id){
        String jpql = "SELECT   AVG(VAL)" +
                      " FROM (" +
                      "       SELECT    ID, TO_CHAR(COLT_DT, 'YYYYMMDD'), SUM(VAL) VAL" +
                      "       FROM      FLOW" +
                      "       WHERE     ID = :id" +
                      "       GROUP BY ID, TO_CHAR(COLT_DT, 'YYYYMMDD')" +
                      "       ORDER BY ID, TO_CHAR(COLT_DT, 'YYYYMMDD') DESC" +
                      " )";

        Query query = em.createNativeQuery(jpql);
        query.setParameter("id", id);
        return (double)Math.round(Double.parseDouble(query.getSingleResult().toString()));
    }

    public List<Tele> findDayTele(String kind, String strDiv, String line, Date fromDt, Date toDt) {
        List<Tele> result = new ArrayList<>();
        result = mianQueryFactory.query()
                .select(tele)
                .from(tele, tagmap)
                .where(tagmap.kind.eq(kind)
                        .and(tagmap.line.eq(line))
                        .and(tele.id.eq(tagmap.id))
                        .and(tele.coltDt.between(fromDt, toDt))
                        .and(tele.val.ne(strDiv)))
                .orderBy(tele.id.asc(), tele.coltDt.asc())
                .fetch();
        return result;
    }

    /*
     * 해당 데이터가 아닌 데이터를 가져옴.(통신 통계에 사용됨.)
     * param : 종류, 해당 데이터, 방향, 시작일, 종료일
     * result : List<ReportDTO>
     * */
    public List<Tele> findTeleNonTimeList(String strDiv, String line, Date fromDt, Date toDt) {
        List<Tele> result = new ArrayList<>();
        result = mianQueryFactory.query()
                .select(tele)
                .from(tele, tagmap)
                .where(tagmap.line.eq(line)
                        .and(tele.id.eq(tagmap.id))
                        .and(tele.val.ne(strDiv)))
                .orderBy(tele.id.asc(), tele.coltDt.asc())
                .fetch();
        return result;
    }

    /*
     * 해당 데이터가 아닌 데이터를 가져옴.(밸브 통계에 사용됨.)
     * param : 종류, 해당 데이터, 방향, 시작일, 종료일
     * result : List<ReportDTO>
     * */
    public List<Valve> findValveNonTimeList(String strDiv, String line, Date fromDt, Date toDt) {
        List<Valve> result = new ArrayList<>();
        result = mianQueryFactory.query()
                .select(valve)
                .from(valve, tagmap)
                .where(tagmap.line.eq(line)
                        .and(valve.id.eq(tagmap.id))
                        .and(valve.val.ne(strDiv)))
                .orderBy(valve.id.asc(), valve.coltDt.asc())
                .fetch();
        return result;
    }

    /*** HMI에서 데이터를 가져옴. ***/

    /*
    * 해당 데이터가 아닌 데이터를 가져옴.(밸브, 통신 통계에 사용됨.)
    * param : 종류, 해당 데이터, 방향, 시작일, 종료일
    * result : List<ReportDTO>
    * */
    public List<ReportDTO> findDataNonTimeList(String kind, String strDiv, String line, Date fromDt, Date toDt) {
        List<ReportDTO> result = new ArrayList<>();
        result = mianQueryFactory.query()
                .select(Projections.constructor(ReportDTO.class, tagmap.id.as("id"), hmi.logTime.as("logTime"), hmi.val))
                .from(hmi, tagmap)
                .where(tagmap.kind.eq(kind)
                        .and(tagmap.line.eq(line))
                        .and(hmi.tagId.eq(tagmap.tagId))
                        .and(hmi.logTime.between(fromDt, toDt))
                        .and(hmi.val.ne(strDiv)))
                .orderBy(hmi.tagId.asc(), hmi.logTime.asc())
                .fetch();
        return result;
    }

    /*
    * 수심 통계
    * param : 시작일, 종료일
    * result : [평균, 최대, 최소]
    * */
    public Tuple findDayLevel(Date fromDt, Date toDt) {
        return mianQueryFactory.query()
                .select(depth.val.avg(), depth.val.max(), depth.val.min())
                .from(depth, tagmap)
                .where(depth.coltDt.between(fromDt, toDt)
                        .and(tagmap.kind.eq("DEPTH"))
                        .and(depth.id.eq(tagmap.id)))
                .fetchOne();
        /*
        return queryFactory.query()
                .select(hmi.val.castToNum(double.class).avg(), hmi.val.castToNum(double.class).max(), hmi.val.castToNum(double.class).min())
                .from(hmi, tagmap)
                .where(hmi.logTime.between(fromDt, toDt)
                        .and(tagmap.kind.eq("DEPTH"))
                        .and(hmi.tagId.eq(tagmap.tagId)))
                .fetchOne();
         */
    }

    /*
    * 수압 통계
    * param : 방향, 시작일, 종료일
    * result : List<[ID, 평균, 최대, 최소]>
    * */
    public List<Tuple> findDayPress(String line, Date fromDt, Date toDt) {
        return mianQueryFactory.query()
                .select(press.id, press.val.avg(), press.val.max(), press.val.min())
                .from(press, tagmap)
                .where(press.coltDt.between(fromDt, toDt)
                        .and(tagmap.kind.eq("PRESS"))
                        .and(tagmap.line.eq(line))
                        .and(press.id.eq(tagmap.id)))
                .groupBy(press.id)
                .orderBy(press.id.asc())
                .fetch();
        /*
        return queryFactory.query()
                .select(tagmap.id, hmi.val.castToNum(double.class).avg(), hmi.val.castToNum(double.class).max(), hmi.val.castToNum(double.class).min())
                .from(hmi, tagmap)
                .where(hmi.logTime.between(fromDt, toDt)
                        .and(tagmap.kind.eq("PRESS"))
                        .and(tagmap.line.eq(line))
                        .and(hmi.tagId.eq(tagmap.tagId)))
                .groupBy(tagmap.id)
                .orderBy(tagmap.id.asc())
                .fetch();
        */
    }

}
