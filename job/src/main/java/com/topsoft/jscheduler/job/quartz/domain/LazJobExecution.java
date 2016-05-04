package com.topsoft.jscheduler.job.quartz.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.Icon;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.topsoft.topframework.base.domain.BaseEntity;
import com.topsoft.topframework.base.util.LazImage;

@javax.persistence.Entity
@Table(name = "QRTZ_JOB_HISTORY")
@AttributeOverride(name = "id", column = @Column(name = "JOB_HISTORY_ID") )
public class LazJobExecution extends BaseEntity<Integer> {

	@Column(name = "SCHED_NAME")
	private String schedulerName;
	
	@Column(name = "JOB_NAME")
	private String jobName;
	
	@Column(name = "JOB_GROUP")
	private String jobGroup;
	
	@Column(name = "FIRED_TIME")
	private Date firedTime;
	
	@Column(name = "END_TIME")
	private Date endTime;
	
	@Column(name = "STATE")
	private String state;

	@Column(name = "JOB_LOG")
	private String jobLog;

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public Date getFiredTime() {
		return firedTime;
	}

	@Transient
	public String getFormattedFiredTime() {
		return DateFormatUtils.format(firedTime, "EEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss");
	}

	public void setFiredTime(Date firedTime) {
		this.firedTime = firedTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Transient
	public String getFormattedEndTime() {
		return DateFormatUtils.format(endTime, "EEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss");
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getJobLog() {
		return jobLog;
	}

	public void setJobLog(String jobLog) {
		this.jobLog = jobLog;
	}

	@Transient
	public Icon getImgStatus() {

		String state = getState();

		if (state.equals("SUSPENDED"))
			return LazImage.YELLOW.getIcon();
		else if (state.equals("ERROR"))
			return LazImage.RED.getIcon();

		if (jobLog.indexOf("at chubb.lazuw") != -1)
			return LazImage.ORANGE.getIcon();

		return LazImage.GREEN.getIcon();
	}

	@Transient
	public String getToolTipStatus() {

		String state = getState();

		if (state.equals("SUSPENDED"))
			return "Suspened execution";
		else if (state.equals("ERROR"))
			return "Error";

		if (jobLog.indexOf("at chubb.lazuw") != -1)
			return "Successful with WARNING";

		return "Normal";
	}

	@Override
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
}