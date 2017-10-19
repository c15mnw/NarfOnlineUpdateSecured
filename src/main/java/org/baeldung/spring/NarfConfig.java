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
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.base.Preconditions;

@Configuration
@ComponentScan({ "main.java.org.baeldung.persistence" })
@PropertySource( "classpath:persistence.properties" )
@EnableJpaRepositories(basePackages = "com.roslin.mwicks.spring.narf.repository",
                       entityManagerFactoryRef = "narfEntityManager", 
                       transactionManagerRef = "narfTransactionManager")
public class NarfConfig {
	
	// Constants ----------------------------------------------------------------------------------
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "jdbc.driverClassName";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "narf_orders.jdbc.pass";
    private static final String PROPERTY_NAME_DATABASE_URL = "narf_orders.jdbc.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "narf_orders.jdbc.user";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_GLOBALLY_QUOTED_IDS = "hibernate.globally_quoted_identifiers";


    @Autowired
    private Environment environment;

    public NarfConfig() {
        super();
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
       
    	LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
       
    	sessionFactory.setDataSource(narfDataSource());
    	sessionFactory.setPackagesToScan(new String[] { "com.roslin.mwicks.spring.narf.model" });
    	sessionFactory.setHibernateProperties(hibernateProperties());
       
    	return sessionFactory;
    }
  
    
    private Properties hibernateProperties() {
    	
    	return new Properties() {
            {
            	setProperty(PROPERTY_NAME_HIBERNATE_DIALECT, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
            	setProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));
            	setProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
            	setProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
            	setProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
            	setProperty("hibernate.globally_quoted_identifiers","true");
            }
    	};
    }
    
    
    @Bean(name = "narfEntityManager")
    public LocalContainerEntityManagerFactoryBean narfEntityManager() {
    	
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        
        em.setDataSource(narfDataSource());
        
        em.setPackagesToScan(new String[] { "com.roslin.mwicks.spring.narf.model" });

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

    @Bean(name = "narfDataSource")
    public DataSource narfDataSource() {
    	
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_DRIVER)));
        dataSource.setUrl(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_URL)));
        dataSource.setUsername(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_USERNAME)));
        dataSource.setPassword(Preconditions.checkNotNull(environment.getProperty(PROPERTY_NAME_DATABASE_PASSWORD)));

        return dataSource;
    }

    @Bean(name = "narfTransactionManager")
    public PlatformTransactionManager narfTransactionManager() {
    	
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        
        transactionManager.setEntityManagerFactory(narfEntityManager().getObject());
        
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
