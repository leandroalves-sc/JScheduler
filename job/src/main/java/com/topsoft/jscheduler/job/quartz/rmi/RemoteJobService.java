package com.topsoft.jscheduler.job.quartz.rmi;

import org.quartz.JobKey;

public interface RemoteJobService{

	public boolean isAlive();
	public String getSchedulerName();
	public void runJob( JobKey key );
}
