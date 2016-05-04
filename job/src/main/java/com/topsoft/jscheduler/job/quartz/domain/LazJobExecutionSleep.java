package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.impl.SleepJob;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobExecutionSleep implements LazJobExecutionConfig{

	private int minutos;
	
	public int getMinutos(){
		return minutos;
	}
	
	public void setMinutos( int minutos ){
		this.minutos = minutos;
	}

	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams(){
		return new ArrayList<LazJobParam>();
	}
	
	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass(){
		return SleepJob.class;
	}
}