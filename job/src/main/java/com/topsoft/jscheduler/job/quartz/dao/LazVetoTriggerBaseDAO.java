package com.topsoft.jscheduler.job.quartz.dao;

import java.util.List;

import org.quartz.TriggerKey;

import com.topsoft.topframework.base.dao.BaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;

public interface LazVetoTriggerBaseDAO extends BaseDAO<LazVetoTrigger,Integer> {

	public List<LazVetoTrigger> findVetoExecutions( TriggerKey key );
	public LazVetoTrigger findVetoExecution( TriggerKey key, long fireTime );
	public void deleteAllVetos( TriggerKey key );
}
