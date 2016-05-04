package com.topsoft.jscheduler.job.quartz.dao.impl;

import com.topsoft.topframework.base.dao.impl.BaseBaseDAOImpl;
import com.topsoft.topframework.base.domain.Entity;

public abstract class QuartzBaseBaseDAOImpl<T extends Entity<ID>, ID> extends BaseBaseDAOImpl<T, ID> {

	public QuartzBaseBaseDAOImpl() {
		super();
	}
}