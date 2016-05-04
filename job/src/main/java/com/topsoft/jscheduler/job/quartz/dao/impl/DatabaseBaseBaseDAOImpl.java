package com.topsoft.jscheduler.job.quartz.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.topsoft.jscheduler.job.quartz.dao.DatabaseBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.Database;

@Repository
public class DatabaseBaseBaseDAOImpl extends QuartzBaseBaseDAOImpl<Database, Integer> implements DatabaseBaseDAO {

	public DatabaseBaseBaseDAOImpl() {
		super();
	}

	@Override
	public List<Order> findAllOrder(CriteriaBuilder builder, Root<Database> root) {
		return Arrays.asList(builder.asc(root.get("name")));
	}
}