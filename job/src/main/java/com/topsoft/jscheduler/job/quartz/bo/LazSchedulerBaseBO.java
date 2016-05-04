package com.topsoft.jscheduler.job.quartz.bo;

import java.util.List;

import com.topsoft.topframework.base.bo.BaseBO;
import com.topsoft.jscheduler.job.quartz.domain.LazScheduler;

public interface LazSchedulerBaseBO extends BaseBO<LazScheduler, String> {
	
	public List<String> findAllNames();
}