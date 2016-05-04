package com.topsoft.jscheduler.job.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:persistence.properties" })
public class PersistenceConfig {

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(env.getProperty("jdbc.user"));
		dataSource.setPassword(env.getProperty("jdbc.pass"));
		
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("com.topsoft.jscheduler.**.*");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(additionalProperties());
		em.setSharedCacheMode(SharedCacheMode.ALL);
		
		return em;
	}

	@Bean
	public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
		
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		
		return transactionManager;
	}

	final Properties additionalProperties() {
		
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
//
//		hibernateProperties.setProperty("hibernate.connection.driver_class", env.getProperty("jdbc.driverClassName"));
//		hibernateProperties.setProperty("hibernate.connection.provider_class", env.getProperty("hibernate.connection.provider_class"));
//		hibernateProperties.setProperty("hibernate.c3p0.max_size", env.getProperty("hibernate.c3p0.max_size"));
//		hibernateProperties.setProperty("hibernate.c3p0.min_size", env.getProperty("hibernate.c3p0.min_size"));
//		hibernateProperties.setProperty("hibernate.c3p0.acquire_increment", env.getProperty("hibernate.c3p0.acquire_increment"));
//		hibernateProperties.setProperty("hibernate.c3p0.idle_test_period", env.getProperty("hibernate.c3p0.idle_test_period"));
//		hibernateProperties.setProperty("hibernate.c3p0.max_statements", env.getProperty("hibernate.c3p0.max_statements"));
//		hibernateProperties.setProperty("hibernate.c3p0.timeout", env.getProperty("hibernate.c3p0.timeout"));

		return hibernateProperties;
	}
}