package com.topsoft.jscheduler.job.quartz.dao;

import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobKey;

import com.topsoft.topframework.base.dao.BaseDAO;
import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;

public interface LazJobExecutionBaseDAO extends BaseDAO<LazJobExecution,Integer> {

	public DataPage<LazJobExecution> findPageLastJobExecutions( JobDetail job, Page page );
	public List<LazJobExecution> findAllLastJobExecutions( JobDetail job, int qtde );
	public void deleteAllHistory( JobKey key );
}
