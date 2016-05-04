package com.topsoft.jscheduler.job.quartz.bo.impl;

import java.util.List;

import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.jscheduler.job.quartz.bo.LazVetoTriggerBaseBO;
import com.topsoft.jscheduler.job.quartz.dao.LazVetoTriggerBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;
import com.topsoft.topframework.base.bo.impl.BaseBaseBOImpl;
import com.topsoft.topframework.base.security.SecurityContext;

@Service(value = "vetoFacade")
@Transactional(rollbackFor = Throwable.class)
public class LazVetoTriggerBaseBaseBOImpl extends BaseBaseBOImpl<LazVetoTrigger, Integer> implements LazVetoTriggerBaseBO {

	@Autowired
	private SecurityContext security;

	private LazVetoTriggerBaseDAO dao;

	@Autowired
	public LazVetoTriggerBaseBaseBOImpl(LazVetoTriggerBaseDAO dao) {
		super(dao);

		this.dao = dao;
	}

	@Override
	public LazVetoTrigger insert(LazVetoTrigger veto) {

		veto.setUserId(security.getLoggedUser().getUsername());

		return super.insert(veto);
	}

	@Override
	public List<LazVetoTrigger> findVetoExecutions(TriggerKey key) {
		return dao.findVetoExecutions(key);
	}

	@Override
	public LazVetoTrigger findVetoExecution(TriggerKey key, long fireTime) {
		return dao.findVetoExecution(key, fireTime);
	}

	@Override
	public void deleteAllVetos(TriggerKey key) {
		dao.deleteAllVetos(key);
	}
}