package com.topsoft.jscheduler.job.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JSchedulerContext extends AnnotationConfigApplicationContext{

	public JSchedulerContext( String quartzScheduler, Class<?>... annotatedClasses ){
		
		super();

		BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(String.class)
			.addConstructorArgValue(quartzScheduler)
			.getBeanDefinition();
		registerBeanDefinition("schedulerName", beanDefinition);
		
		register(annotatedClasses);
		refresh();
	}
}
