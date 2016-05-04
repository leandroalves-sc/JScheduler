package com.topsoft.jscheduler.job.quartz.rmi.impl;

import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.rmi.RemoteJobService;

@Service(value = "remoteJobService")
public class RemoteJobServiceImpl implements RemoteJobService {

	private static Logger log = LoggerFactory.getLogger(RemoteJobServiceImpl.class);

	@Autowired
	private QuartzBO quartzBO;

	@Override
	public String getSchedulerName() {
		return quartzBO.getSchedulerName();
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public void runJob(JobKey key) {

		log.info("Receiving remote call for Job: " + key);

		LazJobDetail job = quartzBO.findJobByKey(key);

		if (job != null)
			quartzBO.runJob(job);
		else
			log.error("Job NULL");

		log.info("Job " + key + " executed");
	}
}