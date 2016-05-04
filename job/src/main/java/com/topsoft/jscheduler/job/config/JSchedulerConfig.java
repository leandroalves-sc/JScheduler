package com.topsoft.jscheduler.job.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Import({ PropertiesConfig.class, PersistenceConfig.class })
@ComponentScan(basePackages = { "com.topsoft.jscheduler" })
public class JSchedulerConfig {

	@Autowired
	private PropertiesConfig props;

	@Bean
	public JavaMailSender mailSender() {

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(props.getMailServerHost());
		sender.setPort(Integer.valueOf(props.getMailServerPort()));
		sender.setUsername(props.getMailServerUsername());
		sender.setPassword(props.getMailServerPassword());

		Properties properties = new Properties();
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtps.auth", "true");

		sender.setJavaMailProperties(properties);

		return sender;
	}
}
