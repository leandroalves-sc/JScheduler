package com.topsoft.jscheduler.job.quartz.bo.impl;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.jscheduler.job.quartz.bo.HolidayBO;
import com.topsoft.jscheduler.job.quartz.dao.HolidayBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.Holiday;
import com.topsoft.topframework.base.security.SecurityContext;
import com.topsoft.topframework.base.util.SystemUtil;

@Service
@Transactional(rollbackFor = Throwable.class)
public class HolidayBOImpl implements HolidayBO {

	@Autowired
	private HolidayBaseDAO dao;

	@Autowired
	private SecurityContext context;

	@Override
	public List<Holiday> findAll() {

		Calendar cal = Calendar.getInstance();
		List<Holiday> list = dao.findAll();

		for (Holiday holiday : list) {
			if (holiday.isRecurrent()) {

				Calendar dt = Calendar.getInstance();
				dt.setTime(holiday.getDate());
				dt.set(Calendar.YEAR, cal.get(Calendar.YEAR));

				holiday.setDate(dt.getTime());
			}
		}

		Collections.sort(list);

		return list;
	}

	@Override
	public boolean isHoliday(Date date) {

		List<Holiday> holidays = findAll();

		for (Holiday holiday : holidays)
			if (SystemUtil.truncDate(date).compareTo(holiday.getDate()) == 0)
				return true;

		return false;
	}

	@Override
	public void remove(Holiday holiday) {
		dao.remove(holiday);
	}

	@Override
	public Holiday insert(Holiday holiday) {

		holiday.setUserID(context.getLoggedUser().getId());

		return dao.insert(holiday);
	}
}