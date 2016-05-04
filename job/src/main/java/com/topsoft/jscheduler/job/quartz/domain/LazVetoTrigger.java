package com.topsoft.jscheduler.job.quartz.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.topsoft.topframework.base.domain.BaseEntity;
import org.quartz.TriggerKey;

@javax.persistence.Entity
@Table(name = "QRTZ_VETO_TRIGGERS")
@AttributeOverride(name = "id", column = @Column(name = "VETO_ID") )
public class LazVetoTrigger extends BaseEntity<Integer> {

	@Column(name = "SCHED_NAME")
	private String schedulerName;

	@Column(name = "TRIGGER_NAME")
	private String name;

	@Column(name = "TRIGGER_GROUP")
	private String group;

	@Column(name = "VETO_TIME")
	private Long vetoTime;

	@Column(name = "USERID")
	private String userId;

	@Column(name = "REASON")
	private String reason;

	public LazVetoTrigger() {}

	public LazVetoTrigger(String schedulerName, TriggerKey key) {

		this.schedulerName = schedulerName;
		this.name = key.getName();
		this.group = key.getGroup();
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getVetoTime() {
		return vetoTime;
	}

	public void setVetoTime(Long vetoTime) {
		this.vetoTime = vetoTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
}