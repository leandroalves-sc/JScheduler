package com.topsoft.jscheduler.job.quartz.event;

import java.util.EventListener;

public interface QuartzListener extends EventListener{
	
	public void jobEvent( QuartzEvent event );
}
