package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.impl.FileRunJob;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobExecutionFileRun implements LazJobExecutionConfig{

	private String filePath;
	private String args;
	
	public LazJobExecutionFileRun(){
		filePath = "C:\\";
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void setFilePath( String filePath ){
		this.filePath = filePath;
	}
	
	public String getArgs(){
		return args;
	}
	
	public void setArgs( String args ){
		this.args = args;
	}

	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams(){
		return new ArrayList<LazJobParam>();
	}
	
	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass(){
		return FileRunJob.class;
	}
}