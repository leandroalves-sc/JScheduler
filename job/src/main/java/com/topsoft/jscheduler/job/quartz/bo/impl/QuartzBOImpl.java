package com.topsoft.jscheduler.job.quartz.bo.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.utils.Key;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.jscheduler.job.quartz.bo.LazJobExecutionBO;
import com.topsoft.jscheduler.job.quartz.bo.LazSchedulerBaseBO;
import com.topsoft.jscheduler.job.quartz.bo.LazVetoTriggerBaseBO;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazCronExpression;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobGroup;
import com.topsoft.jscheduler.job.quartz.domain.LazScheduler;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;
import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;
import com.topsoft.jscheduler.job.quartz.event.QuartzListener;
import com.topsoft.jscheduler.job.quartz.rmi.RemoteJobService;
import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.base.util.SystemUtil;
import com.topsoft.topframework.swing.LazAlert;

@Service
@Transactional(rollbackFor = Throwable.class)
public class QuartzBOImpl implements QuartzBO, ApplicationContextAware {

	@Autowired
	private LazVetoTriggerBaseBO vetoBO;

	@Autowired
	private LazJobExecutionBO executionsBO;
	
	@Autowired
	private DataSource dataSource; 

	private Scheduler scheduler;
	private ApplicationContext context;
	private Map<String, Scheduler> schedulers;
	private EventListenerList listenerList;
	private boolean readOnly;

	public QuartzBOImpl() {

		this.schedulers = new HashMap<String, Scheduler>();
		this.listenerList = new EventListenerList();
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {

		this.context = context;

		String defaultScheduler = null;

		if (context.containsBean("schedulerName"))
			defaultScheduler = context.getBean("schedulerName", String.class);

		LazSchedulerBaseBO schedulerBO = context.getBean(LazSchedulerBaseBO.class);

		for (LazScheduler lazScheduler : schedulerBO.findAll())
			schedulers.put(lazScheduler.getId(), initializeScheduler(lazScheduler.getId()));

		if (defaultScheduler != null) {

			String hostname = SystemUtil.getHostName();

			scheduler = schedulers.get(defaultScheduler);

			if (scheduler == null) {

				schedulerBO.insert(new LazScheduler(defaultScheduler, hostname));
				scheduler = initializeScheduler(defaultScheduler);

				LazJobDetail job = new LazJobDetail();
				job.setKey(new JobKey(Key.DEFAULT_GROUP, Key.DEFAULT_GROUP));
				saveJob(job);
			}
			else {

				LazScheduler lazScheduler = schedulerBO.findByID(defaultScheduler);

				if (!lazScheduler.getHostname().equals(hostname)) {

					if (isSchedulerWorking(lazScheduler.getHostname())) {

						LazAlert.showError(defaultScheduler + " already started at HOST " + lazScheduler.getHostname() + ". Application initiated in ready-only mode.");

						readOnly = true;
						return;
					}
					else {

						lazScheduler.setHostname(hostname);
						schedulerBO.update(lazScheduler);
					}
				}
			}

			try {

				this.scheduler.start();
			}
			catch (Exception e) {
				LazAlert.showError("Error while initiating Scheduler " + defaultScheduler);
				System.exit(1);
			}
		}
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	private boolean isSchedulerWorking(String hostname) {

		try {

			RmiProxyFactoryBean rmi = new RmiProxyFactoryBean();
			rmi.setServiceUrl("rmi://" + hostname + ":1099/remoteJobService");
			rmi.setServiceInterface(RemoteJobService.class);
			rmi.afterPropertiesSet();

			RemoteJobService jobService = (RemoteJobService) rmi.getObject();

			return jobService != null && jobService.isAlive();
		}
		catch (Exception e) {
			return false;
		}
	}

	private Scheduler initializeScheduler(String schedulerName) {

		try {

			Properties quartzProperties = new Properties();
			quartzProperties.put("org.quartz.scheduler.instanceName", schedulerName);
			quartzProperties.put("org.quartz.scheduler.skipUpdateCheck", "true");
			quartzProperties.put("org.quartz.scheduler.instanceId", "AUTO");
			quartzProperties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			quartzProperties.put("org.quartz.threadPool.threadPriority", "5");
			quartzProperties.put("org.quartz.threadPool.threadCount", "50");
			quartzProperties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
			quartzProperties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
			quartzProperties.put("org.quartz.jobStore.misfireThreshold", "60000");
			quartzProperties.put("org.quartz.jobStore.useProperties", "false");
			quartzProperties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
			quartzProperties.put("org.quartz.jobStore.isClustered", "false");
			quartzProperties.put("org.quartz.plugin.triggHistory.class", "com.topsoft.jscheduler.job.quartz.listener.LazTriggerListener");

			SchedulerFactoryBean bean = new SchedulerFactoryBean();
			bean.setApplicationContextSchedulerContextKey("applicationContext");
			bean.setApplicationContext(context);
			bean.setSchedulerName(schedulerName);
			bean.setDataSource(dataSource);
			bean.setAutoStartup(false);
			bean.setQuartzProperties(quartzProperties);
			bean.afterPropertiesSet();

			return bean.getScheduler();
		}
		catch (Exception e) {
			throw new BusinessException("Error while initiating Scheduler " + schedulerName, e);
		}
	}

	@Override
	public String getSchedulerName() {

		try {

			return scheduler.getSchedulerName();
		}
		catch (SchedulerException e) {
			return null;
		}
	}

	@Override
	public List<LazJobDetail> findAllJobs() {
		return findAllJobs(scheduler);
	}

	@Override
	public List<LazJobDetail> findAllJobs(String schedulerName) {
		return findAllJobs(schedulers.get(schedulerName));
	}

	@Override
	public Scheduler getScheduler(String name) {
		return schedulers.get(name);
	}

	private List<LazJobDetail> findAllJobs(Scheduler scheduler) {

		List<LazJobDetail> groups = new ArrayList<LazJobDetail>();

		try {

			if (scheduler != null) {

				for (String groupName : scheduler.getJobGroupNames()) {

					LazJobDetail job = findJobByKey(scheduler, new JobKey(groupName, groupName));

					if (job != null)
						groups.add(job);
				}
			}

			if (!groups.isEmpty())
				Collections.sort(groups);

			return groups;
		}
		catch (SchedulerException e) {
			return groups;
		}
	}

	@Override
	public LazJobDetail findJobByKey(JobKey key) {
		return findJobByKey(scheduler, key);
	}

	@Override
	public LazJobDetail findJobByKey(String scheduler, JobKey key) {
		return findJobByKey(schedulers.get(scheduler), key);
	}

	private LazJobDetail findJobByKey(Scheduler scheduler, JobKey key) {

		try {

			JobDetail jobDetail = scheduler.getJobDetail(key);

			if (jobDetail != null) {

				LazJobDetail job = LazJobDetail.newInstance(jobDetail);

				if (job != null) {

					if (job.isGroup()) {

						LazJobGroup group = LazJobGroup.newInstance(jobDetail);

						for (JobKey jobKey : scheduler
							.getJobKeys(GroupMatcher.jobGroupEquals(group.getKey().getGroup()))) {

							if (group.getKey().compareTo(jobKey) != 0) {

								LazJobDetail jobGroup = findJobByKey(scheduler, jobKey);

								if (jobGroup != null && !jobGroup.isGroup())
									group.addJob(jobGroup);
							}
						}

						if (group.isAllJobsPaused())
							group.setState(LazTriggerState.PAUSED);

						job = group;
					}

					job.setTriggers(findAllTriggersByJob(scheduler, job));

					if (isJobRunning(scheduler, job))
						job.setState(LazTriggerState.RUNNING);
					else if (job.getState() != LazTriggerState.PAUSED)
						job.setState(null);
				}

				return job;
			}
		}
		catch (SchedulerException e) {
			return null;
		}

		return null;
	}

	private List<LazTrigger> findAllTriggersByJob(Scheduler scheduler, LazJobDetail job) {

		List<LazTrigger> triggers = new ArrayList<LazTrigger>();

		try {

			if (!job.isJobOfGroup()) {

				for (Trigger trigger : scheduler.getTriggersOfJob(job.getKey())) {

					if (CronTriggerImpl.class.isAssignableFrom(trigger.getClass())) {

						LazTrigger lazTrigger = LazTrigger.newInstance(trigger);

						LazTriggerState state = LazTriggerState
							.valueOf(scheduler.getTriggerState(lazTrigger.getKey()).toString());
						lazTrigger.getJobDataMap().put("state", state);

						triggers.add(lazTrigger);
					}
				}

				Collections.sort(triggers);
			}

			return triggers;
		}
		catch (SchedulerException e) {
			return triggers;
		}
	}

	@Override
	public void saveJob(LazJobDetail job) {

		try {

			if (job.isJobOfGroup() && job.getJobConfig().getJobSequence() == null)
				job.getJobConfig().setJobSequence(job.getJobGroup().getJobs().size() + 1);

			job.setJobConfig(job.getJobConfig());

			scheduler.addJob(job, true);
		}
		catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveTrigger(JobDetail job, Trigger trigger) {

		try {

			AbstractTrigger<?> newTrigger = (AbstractTrigger<?>) trigger;
			newTrigger.setKey(new TriggerKey(trigger.getKey().getName(), job.getKey().getGroup()));
			newTrigger.setJobKey(job.getKey());

			if (newTrigger != null) {

				if (scheduler.checkExists(newTrigger.getKey())) {

					LazTriggerState state = LazTriggerState
						.valueOf(scheduler.getTriggerState(newTrigger.getKey()).toString());
					scheduler.rescheduleJob(newTrigger.getKey(), newTrigger);

					if (state == LazTriggerState.PAUSED)
						scheduler.pauseTrigger(newTrigger.getKey());
				}
				else
					scheduler.scheduleJob(newTrigger);
			}
		}
		catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean deleteJob(LazJobDetail job) {

		try {

			for (LazTrigger trigger : job.getTriggers()) {

				vetoBO.deleteAllVetos(trigger.getKey());
				deleteTrigger(trigger.getKey());
			}

			executionsBO.deleteAllHistory(job.getKey());
			scheduler.deleteJob(job.getKey());

			if (job.isJobOfGroup()) {

				LazJobGroup group = job.getJobGroup();

				if (group != null)
					if (group.getJobs().remove(job))
						fixSequences(group.getJobs());
			}

			return true;
		}
		catch (SchedulerException e) {
			return false;
		}
	}

	@Override
	public boolean deleteTrigger(TriggerKey key) {

		try {

			return scheduler.unscheduleJob(key);
		}
		catch (SchedulerException e) {
			return false;
		}
	}

	@Override
	public boolean checkExists(TriggerKey key) {

		try {

			return scheduler.checkExists(key);
		}
		catch (SchedulerException e) {
			return false;
		}
	}

	public boolean isInStandbyMode() {

		try {

			return scheduler.isInStandbyMode();
		}
		catch (SchedulerException e) {
			throw new BusinessException("Error while initiating schedulers");
		}
	}

	@Override
	public void setQuartzStandbyMode(boolean standby) {

		try {

			if (standby)
				scheduler.standby();
			else
				scheduler.start();
		}
		catch (SchedulerException e) {
			throw new BusinessException("Error while pausing/starting schedules");
		}
	}

	private boolean isJobRunning(Scheduler scheduler, JobDetail job) throws SchedulerException {

		for (JobExecutionContext context : scheduler.getCurrentlyExecutingJobs())
			if (context.getJobDetail().getKey().compareTo(job.getKey()) == 0)
				return true;

		return false;
	}

	@Override
	public void runJob(JobDetail job) {
		runJob(job, new JobDataMap());
	}

	@Override
	public void runJob(JobDetail job, JobDataMap params) {

		try {

			if (isJobRunning(scheduler, job))
				throw new BusinessException("Service already in execution. Please wait until the end of execution");

			scheduler.triggerJob(job.getKey(), params);
		}
		catch (SchedulerException e) {
			throw new BusinessException("Error while executing job");
		}
	}

	@Override
	public void pauseJob(LazJobDetail job) {

		try {

			if (job.isJobOfGroup() || (job.isGroup() && (job.getTriggers() == null || job.getTriggers().isEmpty()))) {

				job.setState(LazTriggerState.PAUSED);
				saveJob(job);
			}
			else
				scheduler.pauseJob(job.getKey());
		}
		catch (SchedulerException e) {
			throw new BusinessException("Error while pausing job");
		}
	}

	@Override
	public void startJob(LazJobDetail job) {

		try {

			if (job.isJobOfGroup() || (job.isGroup() && (job.getTriggers() == null || job.getTriggers().isEmpty()))) {

				job.setState(null);
				saveJob(job);
			}
			else
				scheduler.resumeJob(job.getKey());
		}
		catch (SchedulerException e) {
			throw new BusinessException("Error while starting job");
		}
	}

	@Override
	public void moveJob(LazJobDetail job, int direction) {

		LazJobGroup group = job.getJobGroup();

		if (group != null) {

			List<LazJobDetail> jobs = group.getJobs();
			int index = jobs.indexOf(job);

			if (index != -1) {

				Collections.swap(jobs, index, index + (direction == SwingConstants.TOP ? -1 : 1));
				fixSequences(jobs);
			}
		}
	}

	private void fixSequences(List<LazJobDetail> jobs) {

		int index = 1;

		for (LazJobDetail jobDetail : jobs) {

			jobDetail.getJobConfig().setJobSequence(index++);
			saveJob(jobDetail);
		}
	}

	@Override
	public void suspendTrigger(Trigger trigger, String reason) {

		try {

			LazVetoTrigger veto = new LazVetoTrigger(scheduler.getSchedulerName(), trigger.getKey());
			veto.setVetoTime(trigger.getNextFireTime().getTime());
			veto.setReason(reason);

			vetoBO.insert(veto);
		}
		catch (SchedulerException e) {
			throw new BusinessException("Error while suspending service execution");
		}
	}

	@Override
	public void resumeTrigger(Trigger trigger) {

		LazVetoTrigger veto = vetoBO.findVetoExecution(trigger.getKey(), trigger.getNextFireTime().getTime());

		if (veto != null)
			vetoBO.remove(veto);
	}

	@Override
	public void scheduleTrigger(JobDetail job, String reason, Date newDate) {

		try {

			LazTrigger trigger = new LazTrigger();
			trigger.setKey(new TriggerKey(Key.createUniqueName(job.getKey().getGroup()), job.getKey().getGroup()));
			trigger.setCronExpression(new LazCronExpression(newDate).getExpression());
			trigger.setDescription(reason);

			saveTrigger(job, trigger);
		}
		catch (ParseException e) {
			throw new BusinessException("Error while scheduling new execution");
		}
	}

	@Override
	public void addActionListener(QuartzListener l) {
		listenerList.add(QuartzListener.class, l);
	}

	@Override
	public void removeActionListener(QuartzListener l) {
		listenerList.remove(QuartzListener.class, l);
	}

	@Override
	public QuartzListener[] getActionListeners() {
		return (QuartzListener[]) (listenerList.getListeners(QuartzListener.class));
	}
}