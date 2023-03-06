package com.mindone.Boryeongapi.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
public class QuerydslConfig {

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Bean
//    public JPAQueryFactory jpaQueryFactory() {
//        return new JPAQueryFactory(entityManager);
//    }

    @PersistenceContext(unitName = "mainEntityManagerFactory")
    private EntityManager mainEntityManager;

    @PersistenceContext(unitName = "hmiEntityManagerFactory")
    private EntityManager hmiEntityManager;

    @Bean
    public JPAQueryFactory mianQueryFactory() {
        return new JPAQueryFactory(mainEntityManager);
    }

    @Bean
    public JPAQueryFactory secondJpaQueryFactory() {
        return new JPAQueryFactory(hmiEntityManager);
    }
}