package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.impl.DelegateJob;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobExecutionDelegate implements LazJobExecutionConfig{

	private String schedulerName;
	private String groupName;
	private String jobName;
	
	public String getSchedulerName(){
		return schedulerName;
	}
	
	public void setSchedulerName( String schedulerName ){
		this.schedulerName = schedulerName;
	}
	
	public String getGroupName(){
		return groupName;
	}
	
	public void setGroupName( String groupName ){
		this.groupName = groupName;
	}
	
	public String getJobName(){
		return jobName;
	}
	
	public void setJobName( String jobName ){
		this.jobName = jobName;
	}

	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams(){
		return new ArrayList<LazJobParam>();
	}

	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass(){
		return DelegateJob.class;
	}
}