package com.topsoft.jscheduler.job.quartz.job.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Service;

import com.alee.utils.ThreadUtils;
import com.topsoft.jscheduler.job.quartz.bo.LazSchedulerBaseBO;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionDelegate;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.domain.LazScheduler;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.rmi.RemoteJobService;
import com.topsoft.topframework.base.exception.BusinessException;

@Service
public class DelegateJob extends LazJob {

	private static Logger log = LoggerFactory.getLogger(DelegateJob.class);
	private static final int WAIT_TIME = 1000 * 60 * 5; // 5 minutos

	@Autowired
	private QuartzBO quartzBO;

	@Autowired
	private LazSchedulerBaseBO schedulerBO;

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		if (LazJobExecutionDelegate.class.isAssignableFrom(job.getJobConfig().getExecutionConfig().getClass())) {

			LazJobExecutionDelegate config = (LazJobExecutionDelegate) job.getJobConfig().getExecutionConfig();

			log.info("Initiating Delegate Job");
			log.info("Scheduler: " + config.getSchedulerName());
			log.info("Group: " + config.getGroupName());
			log.info("Job: " + config.getJobName());

			if (quartzBO.getSchedulerName().equals(config.getSchedulerName())) {

				// Local execution
				LazJobDetail jobToRun = quartzBO
					.findJobByKey(new JobKey(StringUtils.isBlank(config.getJobName()) ? config.getGroupName() : config
						.getJobName(), config.getGroupName()));

				if (jobToRun != null)
					quartzBO.runJob(jobToRun);
			}
			else {

				remoteCall(job, config);
			}
		}
	}

	private void remoteCall(LazJobDetail job, LazJobExecutionDelegate config) throws BusinessException {

		// Remove execution
		LazScheduler scheduler = schedulerBO.findByID(config.getSchedulerName());

		if (scheduler == null)
			throw new BusinessException("Scheduler " + config.getSchedulerName() + " does not found.");

		if (StringUtils.isBlank(scheduler.getHostname()))
			throw new BusinessException("Hostname " + scheduler.getHostname() + "inv�lido");

		try {

			RmiProxyFactoryBean rmi = new RmiProxyFactoryBean();
			rmi.setServiceUrl("rmi://" + scheduler.getHostname() + ":1099/remoteJobService");
			rmi.setServiceInterface(RemoteJobService.class);
			rmi.afterPropertiesSet();

			RemoteJobService jobService = (RemoteJobService) rmi.getObject();

			if (jobService != null) {

				// Check if the answering scheduler is the needed one
				if (jobService.isAlive() && !config.getSchedulerName().equals(jobService.getSchedulerName()))
					throw new RemoteLookupFailureException(config.getSchedulerName() + " is not active");

				jobService.runJob(new JobKey(StringUtils.isBlank(config.getJobName()) ? config.getGroupName() : config
					.getJobName(), config.getGroupName()));
			}
		}
		catch (RemoteLookupFailureException e) {

			String message = "ERROR!!! Msg: " + config
				.getSchedulerName() + " is not active. Execution can not be delegated to the service. New try in " + TimeUnit.MILLISECONDS
					.toMinutes(WAIT_TIME) + " minutes(s). ";

			sendSMS(job, message);
			ThreadUtils.sleepSafely(WAIT_TIME);
			remoteCall(job, config);
		}
		catch (Exception e) {
			throw new BusinessException("Error while delegating Job execution", e);
		}
	}

	@Override
	public List<LazJobParam> getJobParams() {
		return null;
	}
}