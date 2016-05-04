package com.topsoft.jscheduler.job.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:config.properties")
public class PropertiesConfig {

    @Autowired
    private Environment env;

    public String getMailServerHost() {
        return env.getProperty("mail.server.host");
    }

    public String getMailServerPort() {
        return env.getProperty("mail.server.port");
    }

    public String getMailServerUsername() {
        return env.getProperty("mail.server.username");
    }

    public String getMailServerPassword() {
        return env.getProperty("mail.server.password");
    }
}