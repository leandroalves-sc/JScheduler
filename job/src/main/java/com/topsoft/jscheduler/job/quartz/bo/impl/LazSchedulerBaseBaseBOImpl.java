package com.topsoft.jscheduler.job.quartz.bo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.jscheduler.job.quartz.bo.LazSchedulerBaseBO;
import com.topsoft.jscheduler.job.quartz.dao.LazSchedulerBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazScheduler;
import com.topsoft.topframework.base.bo.impl.BaseBaseBOImpl;

@Service
@Transactional(rollbackFor = Throwable.class)
public class LazSchedulerBaseBaseBOImpl extends BaseBaseBOImpl<LazScheduler, String> implements LazSchedulerBaseBO {

	@Autowired
	public LazSchedulerBaseBaseBOImpl(LazSchedulerBaseDAO dao) {
		super(dao);
	}

	@Override
	public List<String> findAllNames() {

		List<String> list = new ArrayList<String>();

		for (LazScheduler scheduler : findAll())
			list.add(scheduler.getId());

		return list;
	}
}