package com.topsoft.jscheduler.job.quartz.security.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;
import com.topsoft.topframework.base.security.Environment;
import com.topsoft.topframework.base.security.SecurityContext;

/**
 * @author Leandro Alves
 * 
 */
@Service
public class SecurityContextImpl extends PropertyPlaceholderConfigurer implements SecurityContext, ApplicationContextAware {

	private ApplicationContext context;
	private Environment environment;
	private User user;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	public ApplicationContext getContext() {
		return context;
	}

	@Override
	public User getLoggedUser() {

		if (user == null) {

			QuartzUser user = new QuartzUser();
			user.setUsername("leandroalves");
			user.setName("Leandro Alves");

			this.user = user;
		}

		return user;
	}

	@Override
	public Environment getEnvironment() {

		if (environment == null)
			environment = Environment.PROD;

		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public String getEnvProperty(String key) {
		return environment.getProperty(key);
	}
}