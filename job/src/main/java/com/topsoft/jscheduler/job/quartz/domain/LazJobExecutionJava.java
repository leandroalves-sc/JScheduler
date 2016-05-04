package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.util.LazJobContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobExecutionJava implements LazJobExecutionConfig{

	private String javaClass;
	
	public LazJobExecutionJava(){}
	
	public LazJobExecutionJava( String javaClass ){
		this.javaClass = javaClass;
	}

	public String getJavaClass(){
		return javaClass;
	}
	
	public void setJavaClass( String javaClass ){
		this.javaClass = javaClass;
	}
	
	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams(){
		
		List<LazJobParam> params = new ArrayList<LazJobParam>();
		LazJob job = LazJobContext.getBean( getJavaClass(), LazJob.class );
		
		if( job != null ){
			
			List<LazJobParam> jobParams = job.getJobParams();
			
			if( jobParams != null && !jobParams.isEmpty() ) 
				params.addAll( jobParams );
		}
		
		return params;
	}

	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass(){
		return LazJobContext.getBean( getJavaClass(), LazJob.class ).getClass();
	}
}