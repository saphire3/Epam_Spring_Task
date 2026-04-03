package com.epam.training.config;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class HibernateConfigTest {

    @Test
    void sessionFactory_returnsBean() {
        HibernateConfig config = new HibernateConfig("create-drop", false);
        DataSource dataSource = mock(DataSource.class);

        LocalSessionFactoryBean bean = config.sessionFactory(dataSource);

        assertNotNull(bean);
    }

    @Test
    void sessionFactory_withShowSqlEnabled() {
        HibernateConfig config = new HibernateConfig("create-drop", true);
        DataSource dataSource = mock(DataSource.class);

        LocalSessionFactoryBean bean = config.sessionFactory(dataSource);

        assertNotNull(bean);
    }

    @Test
    void sessionFactory_withUpdateDdlAuto() {
        HibernateConfig config = new HibernateConfig("update", false);
        DataSource dataSource = mock(DataSource.class);

        LocalSessionFactoryBean bean = config.sessionFactory(dataSource);

        assertNotNull(bean);
    }

    @Test
    void transactionManager_returnsBean() {
        HibernateConfig config = new HibernateConfig("create-drop", false);
        SessionFactory sessionFactory = mock(SessionFactory.class);

        HibernateTransactionManager txManager = config.transactionManager(sessionFactory);

        assertNotNull(txManager);
    }
}
