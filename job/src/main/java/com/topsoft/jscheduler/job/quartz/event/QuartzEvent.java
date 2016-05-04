package com.topsoft.jscheduler.job.quartz.event;

import org.quartz.JobExecutionContext;

import com.topsoft.topframework.swing.event.LazEvent;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzPeriod;

public class QuartzEvent extends LazEvent<QuartzListener>{

	private static final long serialVersionUID = -7544967680491366124L;
	
	public static final int JOB_STARTED  = 1;
	public static final int JOB_FINISHED = 2;
	public static final int JOB_ERROR    = 3;
	
	private LazJobDetail job;
	private QuartzPeriod period;
	private JobExecutionContext jobContext;
	private Exception exception;
	private String log;
	
	public QuartzEvent( JobExecutionContext jobContext, LazJobDetail job, QuartzPeriod period ){
		this( jobContext, job, period, null, null );
	}
	
	public QuartzEvent( JobExecutionContext jobContext, LazJobDetail job, Exception exception, String log ){
		this( jobContext, job, null, exception, log );
	}
	
	private QuartzEvent( JobExecutionContext jobContext, LazJobDetail job, QuartzPeriod period, Exception exception, String log ){
		
		super( job, exception != null ? JOB_ERROR : ( period == QuartzPeriod.START ? JOB_STARTED : JOB_FINISHED ) );
		
		this.job = job;
		this.period = period;
		this.jobContext = jobContext;
		this.exception = exception;
		this.log = log;
	}
	
	public LazJobDetail getJob(){
		return job;
	}
	
	public void setJob( LazJobDetail job ){
		this.job = job;
	}
	
	public QuartzPeriod getPeriod(){
		return period;
	}
	
	public void setPeriod( QuartzPeriod period ){
		this.period = period;
	}

	public JobExecutionContext getJobContext(){
		return jobContext;
	}
	
	public void setJobContext( JobExecutionContext jobContext ){
		this.jobContext = jobContext;
	}

	public Exception getException(){
		return exception;
	}
	
	public void setException( Exception exception ){
		this.exception = exception;
	}
	
	public String getLog(){
		return log;
	}

	public String paramString(){

		String typeStr;
		
		if( period != null )
			typeStr = period.toString();
		else
			typeStr = "unknown type";

		return typeStr;
	}
	
	@Override
	public void dispatchEvent( QuartzListener listener ){		
		listener.jobEvent( this );
	}
}