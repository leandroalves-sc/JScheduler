package com.topsoft.jscheduler.job.quartz.bo;

import java.util.List;

import org.quartz.TriggerKey;

import com.topsoft.topframework.base.bo.BaseBO;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;

public interface LazVetoTriggerBaseBO extends BaseBO<LazVetoTrigger, Integer> {
	
	public List<LazVetoTrigger> findVetoExecutions( TriggerKey key );
	public LazVetoTrigger findVetoExecution( TriggerKey key, long fireTime );
	public void deleteAllVetos( TriggerKey key );
}