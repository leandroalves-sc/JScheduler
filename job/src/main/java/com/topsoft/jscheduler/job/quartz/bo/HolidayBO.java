package com.topsoft.jscheduler.job.quartz.bo;

import java.util.Date;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.domain.Holiday;

public interface HolidayBO {

	public List<Holiday> findAll();
	public void remove( Holiday holiday );
	public Holiday insert( Holiday holiday );
	public boolean isHoliday( Date date );
}