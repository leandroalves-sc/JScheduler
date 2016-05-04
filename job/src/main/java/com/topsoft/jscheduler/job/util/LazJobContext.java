package com.topsoft.jscheduler.job.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.topsoft.jscheduler.job.config.JSchedulerConfig;
import com.topsoft.jscheduler.job.config.JSchedulerContext;
import com.topsoft.topframework.base.security.SecurityContext;

public class LazJobContext {

	private static LazJobContext jobContext;
	private ConfigurableApplicationContext context;

	private LazJobContext(String quartzScheduler) {
		context = new JSchedulerContext(quartzScheduler, JSchedulerConfig.class);
		context.start();
	}

	public static void initialize(String quartzScheduler) {
		jobContext = new LazJobContext(quartzScheduler);
	}

	public static ApplicationContext getInstance() {

		if (jobContext == null || jobContext.context == null)
			throw new RuntimeException("Fail while loading context");

		return jobContext.context;
	}

	public static SecurityContext getSecurityContext() {
		return getInstance().getBean(SecurityContext.class);
	}

	public static Object getBean(String beanName) {
		return getInstance().getBean(beanName);
	}

	public static <T extends Object> T getBean(String beanName, Class<T> c) {
		return getInstance().getBean(beanName, c);
	}

	public static <T> T getBean(Class<T> classType) {
		return getInstance().getBean(classType);
	}
}