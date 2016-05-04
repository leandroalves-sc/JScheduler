package com.topsoft.jscheduler.job.quartz.domain;

import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo( use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type" )
@JsonSubTypes({ 
	@Type(value = LazJobExecutionJava.class, name = "java"),
	@Type(value = LazJobExecutionSleep.class, name = "sleep"),
	@Type(value = LazJobExecutionDelegate.class, name = "delegate"), 
	@Type(value = LazJobExecutionDB.class, name = "db"),
	@Type(value = LazJobExecutionFTP.class, name = "ftp"), 
	@Type(value = LazJobExecutionFile.class, name = "file"),
	@Type(value = LazJobExecutionFileRun.class, name = "fileCopy")
}) 
public interface LazJobExecutionConfig{

	public static final int LABEL_WIDTH = 125;
	
	@JsonIgnore
	public List<LazJobParam> getJobParams();
	
	@JsonIgnore
	public Class<? extends LazJob> getJobClass();
}