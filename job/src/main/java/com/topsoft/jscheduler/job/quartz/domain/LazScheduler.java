package com.topsoft.jscheduler.job.quartz.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Table;

import com.topsoft.topframework.base.domain.BaseEntity;

@javax.persistence.Entity
@Table(name = "QRTZ_SCHEDULER")
@AttributeOverride(name = "id", column = @Column(name = "SCHED_NAME") )
public class LazScheduler extends BaseEntity<String> {

	@Column(name = "HOSTNAME")
	private String hostname;

	public LazScheduler() {}

	public LazScheduler(String name, String hostname) {

		this.id = name;
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}