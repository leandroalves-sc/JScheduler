package com.topsoft.jscheduler.job.quartz.bo;

import java.util.List;

import org.quartz.JobDataMap;

import com.topsoft.topframework.base.bo.BaseBO;
import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.jscheduler.job.quartz.domain.Database;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionDB;

public interface DatabaseBaseBO extends BaseBO<Database,Integer> {

	public List<String> findAllOwners( Database database );
	public List<String> findAllObjects( Database database, String schema );

	public String callObject( LazJobExecutionDB config, JobDataMap params ) throws BusinessException;
}