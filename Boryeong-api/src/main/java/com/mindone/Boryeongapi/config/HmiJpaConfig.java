package com.mindone.Boryeongapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "hmiEntityManagerFactory"
        , transactionManagerRef = "hmiTransactionManager"
        , basePackages = "com.mindone.Boryeongapi.repository.hmi"
)
public class HmiJpaConfig {
    @Autowired
    private Environment env;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Bean
    public DataSource hmiDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.hmi.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.hmi.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.hmi.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.hmi.datasource.password"));
        return dataSource;
    }


    @Bean(name="hmiEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean hmiEntityManagerFactory (EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());

        return builder
                .dataSource(hmiDataSource())
                .packages("com.mindone.Boryeongapi.domain.entity.hmi")
                .persistenceUnit("hmiEntityManager")
                .properties(properties)
                .build();
    }

    @Bean(name="hmiTransactionManager")
    public PlatformTransactionManager hmiTransactionManager(@Qualifier(value = "hmiEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}