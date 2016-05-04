package com.topsoft.jscheduler.job.quartz.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.topsoft.jscheduler.job.quartz.dao.LazSchedulerBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazScheduler;

@Repository
public class LazSchedulerBaseBaseDAOImpl extends QuartzBaseBaseDAOImpl<LazScheduler, String> implements LazSchedulerBaseDAO {

	public LazSchedulerBaseBaseDAOImpl() {
		super();
	}

	@Override
	public List<Order> findAllOrder(CriteriaBuilder builder, Root<LazScheduler> root) {
		return Arrays.asList(builder.asc(root.get("id")));
	}
}