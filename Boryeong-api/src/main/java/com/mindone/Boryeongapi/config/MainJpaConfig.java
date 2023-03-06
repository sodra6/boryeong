package com.mindone.Boryeongapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "mainEntityManagerFactory"
        , transactionManagerRef = "mainTransactionManager"
        , basePackages = "com.mindone.Boryeongapi.repository.main"
)
public class MainJpaConfig {
    @Autowired
    private Environment env;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Primary
    @Bean
    public DataSource mainDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.main.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.main.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.main.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.main.datasource.password"));
        return dataSource;
    }

    @Primary
    @Bean(name="mainEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory (EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());

        return builder
                .dataSource(mainDataSource())
                .packages("com.mindone.Boryeongapi.domain.entity.main")
                .persistenceUnit("mainEntityManager")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name="mainTransactionManager")
    public PlatformTransactionManager mainTransactionManager(@Qualifier(value = "mainEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}