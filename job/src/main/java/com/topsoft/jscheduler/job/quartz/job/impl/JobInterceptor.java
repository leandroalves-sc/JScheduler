package com.topsoft.jscheduler.job.quartz.job.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alee.utils.ThreadUtils;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobMonitor;
import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzLevel;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzPeriod;
import com.topsoft.jscheduler.job.quartz.event.QuartzEvent;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.log.LazLogAppender;
import com.topsoft.topframework.base.mail.domain.Mail;
import com.topsoft.topframework.base.mail.service.MailService;
import com.topsoft.topframework.swing.event.LazEvent;

public class JobInterceptor extends QuartzJobBean {

	private static Logger log = LoggerFactory.getLogger(JobInterceptor.class);

	private JobExecutionContext jobContext;
	private ApplicationContext appContext;
	private QuartzBO quartzBO;

	public JobInterceptor() {
	}

	@Override
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		try {

			this.jobContext = jobContext;

			this.appContext = (ApplicationContext) jobContext.getScheduler()
				.getContext()
				.get(getApplicationContextKey());
			this.quartzBO = appContext.getBean(QuartzBO.class);

			if (jobContext.getScheduler().isInStandbyMode()) {

				log.info("*** Server in standby, service was not executed! ***");
				return;
			}

			if (!jobContext.getMergedJobDataMap().isEmpty()) {

				log.info("*** Received parameters ***");

				for (Entry<String, Object> entry : jobContext.getMergedJobDataMap().entrySet())
					if (!entry.getKey().equals("config"))
						log.info("** " + entry.getKey() + " = " + entry.getValue());
			}

			LazJobDetail runningJob = quartzBO.findJobByKey(jobContext.getJobDetail().getKey());

			if (runningJob.isGroup())
				jobEvent(runningJob, QuartzLevel.GROUP, QuartzPeriod.START);

			for (LazJobDetail job : runningJob.getJobs()) {

				if (job.isJobOfGroup() && runningJob.isGroup() && job.getState() == LazTriggerState.PAUSED)
					continue;

				try {

					LazJob lazJob = appContext.getBean(job.getJobConfig().getExecutionConfig().getJobClass());

					if (lazJob != null) {

						jobEvent(job, QuartzLevel.SERVICE, QuartzPeriod.START);

						log.info("*** Running job " + lazJob.getClass().getSimpleName() + " ***");
						lazJob.execute(job, jobContext);
						log.info("*** Job " + lazJob.getClass().getSimpleName() + " finished ***");

						jobEvent(job, QuartzLevel.SERVICE, QuartzPeriod.END);
					}
				}
				catch (Exception exception) {

					log.error("ERROR: ", exception);
					jobEvent(job, exception);

					if (!job.getJobConfig().isContinueOnException())
						return;
				}
			}

			if (runningJob.isGroup())
				jobEvent(runningJob, QuartzLevel.GROUP, QuartzPeriod.END);
		}
		catch (SchedulerException e) {
			throw new JobExecutionException(e);
		}
	}

	private void jobEvent(LazJobDetail job, Exception exception) {
		jobEvent(job, null, null, exception);
	}

	private void jobEvent(LazJobDetail job, QuartzLevel level, QuartzPeriod period) {
		jobEvent(job, level, period, null);
	}

	private void jobEvent(LazJobDetail job, QuartzLevel level, QuartzPeriod period, Exception exception) {

		if (exception != null) {

			LazLogAppender appender = LazLogAppender.getLazAppender();
			String log = appender == null ? "" : appender.getThreadLog(Thread.currentThread().getId()).toString();

			LazEvent.dispatchEvent(quartzBO, new QuartzEvent(jobContext, job, exception, log));
		}
		else if (period != null)
			LazEvent.dispatchEvent(quartzBO, new QuartzEvent(jobContext, job, period));

		ThreadUtils.sleepSafely(2500);

		try {

			MailService mailService = (MailService) appContext.getBean(MailService.class);
			Mail mail = new Mail();

			for (LazJobMonitor monitor : job.getMonitors()) {

				if ((monitor.isErrorMonitor() && exception != null) || (monitor
					.isSmsMonitor() && level == monitor.getSmsLevel() && (monitor
						.getSmsPeriod() == QuartzPeriod.START_END || monitor.getSmsPeriod() == period)))
					if (monitor.getUser().hasCellPhoneAdded())
						sendSMS(monitor, level, period, job, exception);

				if ((monitor.isErrorMonitor() && exception != null) || (monitor
					.isEmailMonitor() && level == monitor.getEmailLevel() && (monitor
						.getEmailPeriod() == QuartzPeriod.START_END || monitor.getEmailPeriod() == period)))
					if (monitor.getUser().hasEmailAdded())
						mail.addTo(monitor.getUser().getEmail());
			}

			if (mail.getTo().length != 0) {

				StringBuilder msg = new StringBuilder();

				if (exception != null) {

					mail.setSubject(getSchedulerName() + " - " + job
						.getNameCompleted() + ": Error while executing service");

					msg.append("<br><b>Error while executing service: </b>" + job.getNameCompleted());
					msg.append("<br><b>Date: </b>" + new SimpleDateFormat("HH:mm:ss")
						.format(Calendar.getInstance().getTime()));

					if (!job.getJobConfig().isContinueOnException())
						msg.append("<br><br><b><font color='red'/>Job ended!!!</font></b>");

					String errorMsg = ExceptionUtils.getStackTrace(exception);
					msg.append("<br><br><b>ERROR: </b><br><br>" + errorMsg);
				}
				else {

					mail.setSubject(getSchedulerName() + " - " + job
						.getNameCompleted() + ": Service " + (period == QuartzPeriod.START ? "started" : "finished"));

					msg.append("<br>Service " + job
						.getNameCompleted() + " " + (period == QuartzPeriod.START ? "started" : "finished"));
					msg.append(" at " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
					msg.append(" successfully.");
				}

				mail.setText(msg.toString());

				mailService.send(mail);
			}
		}
		catch (Exception e) {
			return;
		}
	}

	private void sendSMS(LazJobMonitor monitor, QuartzLevel level, QuartzPeriod period, LazJobDetail job, Exception exception) {

		try {

			StringBuilder msg = new StringBuilder();

			msg.append("[" + getSchedulerName() + "] " + job.getNameCompleted());

			if (exception != null) {

				msg.append("ERROR!!! Msg: " + exception.getMessage());
				msg.append(" at " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
			}
			else {

				msg.append(period == QuartzPeriod.START ? " started" : " finished");
				msg.append(" at " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
				msg.append(" successfully.");
			}

			// SM01Soap sm01 = new SM01Locator().getSM01Soap();
			// sm01.enviar( (short) 4,
			// monitor.getUser().getCellularNumberOnlyNumbers(), msg.toString(),
			// "LAZUWPS" );
		}
		catch (Exception e) {
			return;
		}
	}

	public String getSchedulerName() {

		try {
			return jobContext.getScheduler().getSchedulerName();
		}
		catch (SchedulerException e) {
			return null;
		}
	}

	/**
	 * Key no SchedulerContext que contem o ApplicationContext.<br>
	 * Este nome deve ser configurado na property
	 * applicationContextSchedulerContextKey no SchedulerFactoryBean
	 * 
	 * @return
	 */
	public static String getApplicationContextKey() {
		return "applicationContext";
	}
}