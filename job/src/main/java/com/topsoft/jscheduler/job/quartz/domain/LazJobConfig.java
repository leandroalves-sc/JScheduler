package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.domain.type.QuartzExecutionType;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzJobType;

public class LazJobConfig{
	
	private Integer jobSequence;
	private boolean holidayExecution, continueOnException;
	private QuartzJobType jobType;
	
	private QuartzExecutionType executionType;
	private LazJobExecutionConfig executionConfig;
	
	private List<LazJobMonitor> monitors;
	
	public LazJobConfig(){
		
		this.jobType = QuartzJobType.GROUP;
		this.monitors = new ArrayList<LazJobMonitor>();
	}

	public Integer getJobSequence(){
		return jobSequence;
	}
	
	public void setJobSequence( Integer jobSequence ){
		this.jobSequence = jobSequence;
	}
	
	public List<LazJobMonitor> getMonitors(){
		return monitors;
	}
	
	public void setMonitors( List<LazJobMonitor> monitors ){
		this.monitors = monitors;
	}
	
	public QuartzJobType getJobType(){
		return jobType;
	}
	
	public void setJobType( QuartzJobType jobType ){
		this.jobType = jobType;
	}
	
	public boolean isHolidayExecution(){
		return holidayExecution;
	}
	
	public void setHolidayExecution( boolean holidayExecution ){
		this.holidayExecution = holidayExecution;
	}
	
	public boolean isContinueOnException(){
		return continueOnException;
	}
	
	public void setContinueOnException( boolean continueOnException ){
		this.continueOnException = continueOnException;
	}

	public QuartzExecutionType getExecutionType(){
		return executionType;
	}
	
	public void setExecutionType( QuartzExecutionType executionType ){
		this.executionType = executionType;
	}
	
	public LazJobExecutionConfig getExecutionConfig(){
		return executionConfig;
	}
	
	public void setExecutionConfig( LazJobExecutionConfig executionConfig ){
		this.executionConfig = executionConfig;
	}
}