package main.java.org.baeldung.spring;

import java.util.HashMap;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.base.Preconditions;

@Configuration
@ComponentScan({ "main.java.org.baeldung.persistence" })
@PropertySource( "classpath:persistence.properties" )
@EnableJpaRepositories(basePackages = "main.java.org.baeldung.persistence.dao",
                       entityManagerFactoryRef = "userEntityManager", 
                       transactionManagerRef = "userTransactionManager")
public class UserConfig {
	
	// Constants ----------------------------------------------------------------------------------
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "jdbc.driverClassName";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "narf_users.jdbc.pass";
    private static final String PROPERTY_NAME_DATABASE_URL = "narf_users.jdbc.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "narf_users.jdbc.user";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";

    
    @Autowired
    private Environment environment;

    
    public UserConfig() {
        super();
    }

    @Bean(name = "userEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean userEntityManager() {
    	
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        
        em.setDataSource(userDataSource());
        
        em.setPackagesToScan(new String[] { "main.java.org.baeldung.persistence.model" });

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        
        em.setJpaVendorAdapter(vendorAdapter);
        
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        
        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        properties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));
        properties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        properties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
        
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "userDataSource")
    public DataSource userDataSource() {
    	
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_DRIVER)));
        dataSource.setUrl(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_URL)));
        dataSource.setUsername(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_USERNAME)));
        dataSource.setPassword(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_PASSWORD)));

        return dataSource;
    }

    @Primary
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager() {
    	
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        
        transactionManager.setEntityManagerFactory(userEntityManager().getObject());
        
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
