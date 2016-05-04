package com.topsoft.jscheduler.job.quartz.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.topsoft.jscheduler.job.quartz.dao.HolidayBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.Holiday;

@Repository
public class HolidayBaseBaseDAOImpl extends QuartzBaseBaseDAOImpl<Holiday, Integer> implements HolidayBaseDAO {

	public HolidayBaseBaseDAOImpl() {
		super();
	}

	@Override
	public List<Order> findAllOrder(CriteriaBuilder builder, Root<Holiday> root) {
		return Arrays.asList(builder.asc(root.get("date")));
	}
}