package com.topsoft.jscheduler.job.quartz.domain;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.topsoft.topframework.base.domain.BaseEntity;
import org.apache.commons.lang3.time.DateFormatUtils;

@javax.persistence.Entity
@Table(name = "QRTZ_HOLIDAY")
@AttributeOverride(name = "id", column = @Column(name = "HOLIDAY_ID") )
public class Holiday extends BaseEntity<Integer> implements Comparable<Holiday> {

	@Column(name = "HOLIDAY_DATE")
	private Date date;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "RECURRENT")
	private boolean recurrent;
	
	@Column(name = "USER_ID")
	private Integer userID;

	public Holiday() {
		this.creationDate = Calendar.getInstance().getTime();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date holidayDate) {
		this.date = holidayDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Transient
	public String getFormattedDate() {
		return DateFormatUtils.format(date, "dd/MM/yyyy");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
	}

	@Transient
	public String getRecurrentFlag() {
		return recurrent ? "Yes" : "No";
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	@Override
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	@Override
	public int compareTo(Holiday dto) {

		if (date != null && dto != null)
			return date.compareTo(dto.getDate());

		return 0;
	}
}