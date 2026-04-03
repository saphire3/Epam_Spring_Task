package com.epam.training.config;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    private final String ddlAuto;
    private final boolean showSql;

    public HibernateConfig(
            @Value("${app.hibernate.ddl-auto:create-drop}") String ddlAuto,
            @Value("${app.hibernate.show-sql:false}") boolean showSql) {
        this.ddlAuto = ddlAuto;
        this.showSql = showSql;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setAnnotatedClasses(
                User.class,
                Trainee.class,
                Trainer.class,
                Training.class,
                TrainingType.class
        );

        Properties props = new Properties();
        props.put("hibernate.show_sql", String.valueOf(showSql));
        props.put("hibernate.format_sql", String.valueOf(showSql));
        props.put("hibernate.hbm2ddl.auto", ddlAuto);
        props.put("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");

        factoryBean.setHibernateProperties(props);
        return factoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}
