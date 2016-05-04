package com.topsoft.jscheduler.job.quartz.bo;

import java.util.List;

import org.quartz.JobKey;

import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;

public interface LazJobExecutionBO {
	
	public LazJobExecution insert( LazJobExecution dto );
	public void deleteAllHistory( JobKey key );
	
	public List<LazTrigger> findAllNextJobExecutions( LazJobDetail job, int qtde );	
	public List<LazJobExecution> findAllLastJobExecutions( LazJobDetail job, int qtde );	
	
	public DataPage<LazJobExecution> findPageLastJobExecutions( LazJobDetail job, Page page );
}