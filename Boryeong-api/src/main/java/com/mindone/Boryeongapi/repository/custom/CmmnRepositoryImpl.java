package com.mindone.Boryeongapi.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.querydsl.jpa.JPAExpressions.select;

@Repository
@RequiredArgsConstructor
public class CmmnRepositoryImpl implements CmmnRepositoryCustom {

    //private final EntityManager em;

    //private final JPAQueryFactory queryFactory;

}
