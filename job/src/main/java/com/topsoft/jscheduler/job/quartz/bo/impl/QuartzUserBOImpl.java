package com.topsoft.jscheduler.job.quartz.bo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;
import com.topsoft.jscheduler.job.quartz.bo.QuartzUserBO;
import com.topsoft.jscheduler.job.quartz.dao.QuartzUserBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;

@Service
@Transactional(rollbackFor=Throwable.class)
public class QuartzUserBOImpl implements QuartzUserBO{
	
	@Autowired
	private QuartzUserBaseDAO dao;

	@Override
	public QuartzUser findByUserID( String userId ){
		return dao.findByUserID( userId );
	}

	@Override
	public DataPage<QuartzUser> findPageByName( String name, Page page ){
		return dao.findPageByName( name, page );
	}
}