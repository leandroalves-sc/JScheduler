package com.topsoft.jscheduler.job.quartz.job;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobMonitor;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.job.impl.JobInterceptor;

public abstract class LazJob{

	public LazJob(){}
	
	protected void sendSMS( LazJobDetail job, String message ){

		for( LazJobMonitor monitor : job.getMonitors() ){
			
			if( monitor.isErrorMonitor() && monitor.getUser().hasCellPhoneAdded() ){
				
//				try{
//					
//					SM01Soap sm01 = new SM01Locator().getSM01Soap();
//					sm01.enviar( (short) 4, monitor.getUser().getCellularNumberOnlyNumbers(), message, "LAZUWPS" );
//				}
//				catch( Exception e ){}
			}
		}
	}
	
	public <A extends Annotation> A getAnnotation( Class<A> annotation ){
		
		if( getClass().isAnnotationPresent( annotation ) )
			return getClass().getAnnotation( annotation );
		
		if( getClass().getSuperclass() == null )
			return null;

		return getClass().getSuperclass().getAnnotation( annotation );
	}
	
	public void triggerJob( ApplicationContext applicationContext, JobKey jobKey ){
	
		triggerJob( applicationContext, jobKey, null );
	}
	
	public void triggerJob( ApplicationContext applicationContext, JobKey jobKey, Map<String,Object> params ){
		
		QuartzBO quartzBO = applicationContext.getBean( QuartzBO.class );
		
		SchedulerJob schedulerJob = AnnotationUtils.findAnnotation( this.getClass(), SchedulerJob.class );
		JobInterceptor interceptor = new JobInterceptor();
		
		LazJobDetail jobDetail = quartzBO.findJobByKey( schedulerJob.scheduler(), jobKey );		
		Scheduler scheduler = quartzBO.getScheduler( schedulerJob.scheduler() );
		
		if( params != null && !params.isEmpty() ){
			
			jobDetail.getJobDataMap().clear();
			
			for( String param : params.keySet() )
				jobDetail.getJobDataMap().put( param, params.get( param ) );
		}
		
		TriggerFiredBundle tfb = new TriggerFiredBundle( jobDetail, (OperableTrigger) TriggerBuilder.newTrigger().startNow().build(), null, false, null, null, null, null );
	
		execute( jobDetail, new JobExecutionContextImpl( scheduler, tfb, interceptor ) );
	}
	
	public abstract void execute( LazJobDetail job, JobExecutionContext context ) throws BusinessException;
	public abstract List<LazJobParam> getJobParams();
}