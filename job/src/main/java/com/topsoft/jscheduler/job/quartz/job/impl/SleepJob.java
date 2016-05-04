package com.topsoft.jscheduler.job.quartz.job.impl;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alee.utils.ThreadUtils;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionSleep;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.topframework.base.exception.BusinessException;

@Service
public class SleepJob extends LazJob {

	private static Logger log = LoggerFactory.getLogger(SleepJob.class);

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		if (LazJobExecutionSleep.class.isAssignableFrom(job.getJobConfig().getExecutionConfig().getClass())) {

			LazJobExecutionSleep config = (LazJobExecutionSleep) job.getJobConfig().getExecutionConfig();

			log.info("Waiting " + config.getMinutos() + " minutes(s)");
			ThreadUtils.sleepSafely(1000 * 60 * config.getMinutos());
			log.info("Done");
		}
	}

	@Override
	public List<LazJobParam> getJobParams() {
		return null;
	}
}