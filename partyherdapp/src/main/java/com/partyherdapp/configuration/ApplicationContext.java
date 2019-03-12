package com.partyherdapp.configuration;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan("com.partyherdapp")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement

public class ApplicationContext {

	
	@Resource
	private Environment environment;
	
	private static final String[] packagesToScan = { "com.partyherd.entities" };

	
	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setSuffix(".html");
		return viewResolver;
	}
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() throws IllegalStateException, PropertyVetoException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("db.driver"));
		dataSource.setUrl(environment.getRequiredProperty("db.url"));
		dataSource.setUsername(environment.getRequiredProperty("db.username"));
		dataSource.setPassword(environment.getRequiredProperty("db.password"));
//		dataSource.setTestOnBorrow(true);
//		dataSource.setTestWhileIdle(true);
//		dataSource.setMaxIdle(0);
		return dataSource;
	}
	
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws ClassNotFoundException, IllegalStateException, PropertyVetoException{
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();

		HibernateJpaVendorAdapter hpVA = new HibernateJpaVendorAdapter();
		hpVA.setDatabase(Database.MYSQL);
		entityManager.setJpaVendorAdapter(hpVA);

		entityManager.setDataSource(getDataSource());
		entityManager.setPackagesToScan(packagesToScan);
		entityManager.setPersistenceUnitName("orcl");
		entityManager.setJpaProperties(hibProperties());
		return entityManager;
	}
	
	@Bean
	public JpaTransactionManager transactionManager() throws ClassNotFoundException, IllegalStateException, PropertyVetoException {
		JpaTransactionManager transactionManager = new JpaTransactionManager();

		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

		return transactionManager;
	}
	
	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("show_sql", true);
		return properties;
	}
}
