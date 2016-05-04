package com.topsoft.jscheduler.job.quartz.listener;

import java.sql.Timestamp;
import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.plugins.history.LoggingTriggerHistoryPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.topsoft.jscheduler.job.quartz.bo.LazJobExecutionBO;
import com.topsoft.jscheduler.job.quartz.bo.LazVetoTriggerBaseBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;
import com.topsoft.jscheduler.job.quartz.job.impl.JobInterceptor;
import com.topsoft.jscheduler.job.quartz.log.LazLogAppender;

public class LazTriggerListener extends LoggingTriggerHistoryPlugin {

	private static Logger log = LoggerFactory.getLogger(LazTriggerListener.class);

	private LazLogAppender appender;

	public LazTriggerListener() {
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {

		if (appender == null)
			appender = LazLogAppender.getLazAppender();

		appender.initLog(Thread.currentThread().getId());

		log.info("Job " + context.getJobDetail().getKey().getName() + " starting process.");
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {

		try {

			ApplicationContext appContext = (ApplicationContext) context.getScheduler()
				.getContext()
				.get(JobInterceptor.getApplicationContextKey());

			long endTime = System.currentTimeMillis();

			JobDetail jobDetail = context.getJobDetail();
			jobDetail.getJobDataMap().put("lastFireTime", new Date(context.getFireTime().getTime()));

			log.info("Job " + jobDetail.getKey().getName() + " finished.");
			log.info("Total execution time: " + ((endTime - context.getFireTime().getTime()) / 1000.0) + " seconds.");

			StringBuilder logs = appender != null ? appender.finalizeLog(Thread.currentThread().getId()) : null;

			LazJobExecution execution = new LazJobExecution();
			execution.setSchedulerName(context.getScheduler().getSchedulerName());
			execution.setJobName(jobDetail.getKey().getName());
			execution.setJobGroup(jobDetail.getKey().getGroup());
			execution.setFiredTime(new Timestamp(context.getFireTime().getTime()));
			execution.setEndTime(new Timestamp(endTime));
			execution.setState("SUCCESS");
			execution.setJobLog(logs != null ? logs.toString() : "");

			LazJobExecutionBO executionBO = appContext.getBean(LazJobExecutionBO.class);

			if (executionBO != null)
				executionBO.insert(execution);
		}
		catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {

		try {

			ApplicationContext appContext = (ApplicationContext) context.getScheduler()
				.getContext()
				.get(JobInterceptor.getApplicationContextKey());

			LazVetoTriggerBaseBO vetoFacade = appContext.getBean(LazVetoTriggerBaseBO.class);

			if (vetoFacade != null) {

				LazVetoTrigger veto = vetoFacade
					.findVetoExecution(trigger.getKey(), context.getScheduledFireTime().getTime());

				if (veto != null) {

					log.info("Job " + context.getJobDetail().getKey().getName() + " with suspended execution");
					log.info("Reason: " + veto.getReason());

					StringBuilder logs = appender != null ? appender.finalizeLog(Thread.currentThread().getId()) : null;

					LazJobExecution execution = new LazJobExecution();
					execution.setSchedulerName(context.getScheduler().getSchedulerName());
					execution.setJobName(context.getJobDetail().getKey().getName());
					execution.setJobGroup(context.getJobDetail().getKey().getGroup());
					execution.setFiredTime(new Timestamp(context.getScheduledFireTime().getTime()));
					execution.setEndTime(new Timestamp(context.getScheduledFireTime().getTime()));
					execution.setState("SUSPENDED");
					execution.setJobLog(logs != null ? logs.toString() : "");

					LazJobExecutionBO executionBO = appContext.getBean(LazJobExecutionBO.class);

					if (executionBO != null)
						executionBO.insert(execution);

					return true;
				}
			}
		}
		catch (SchedulerException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public String getName() {
		return "lazQuartzTriggerListener";
	}
}