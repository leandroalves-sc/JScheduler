package com.topsoft.jscheduler.job.quartz.bo;

import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.topsoft.topframework.swing.event.LazActionDispatcher;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.event.QuartzListener;

public interface QuartzBO extends LazActionDispatcher<QuartzListener>{
	
	public boolean isInStandbyMode();
	public void setQuartzStandbyMode( boolean standby );
	public boolean isReadOnly();
	
	public List<LazJobDetail> findAllJobs();
	public List<LazJobDetail> findAllJobs( String schedulerName );
	
	public Scheduler getScheduler( String name );
	public String getSchedulerName();
	
	public void runJob( JobDetail job );
	public void runJob( JobDetail job, JobDataMap params );
	public void pauseJob( LazJobDetail job );
	public void startJob( LazJobDetail job );
	public LazJobDetail findJobByKey( JobKey key );
	public LazJobDetail findJobByKey( String scheduler, JobKey key );
	
	public boolean checkExists( TriggerKey key );
	public boolean deleteTrigger( TriggerKey key );
	public boolean deleteJob( LazJobDetail job );
	
	public void saveJob( LazJobDetail job );
	public void saveTrigger( JobDetail job, Trigger trigger );
	
	public void moveJob( LazJobDetail job, int direction );
	
	public void scheduleTrigger( JobDetail jobDetail, String reason, Date newDate );
	public void suspendTrigger( Trigger trigger, String reason );
	public void resumeTrigger( Trigger trigger );
}