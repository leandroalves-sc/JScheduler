package com.topsoft.jscheduler.job.quartz.dao;

import com.topsoft.topframework.base.dao.BaseDAO;
import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;
import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;

public interface QuartzUserBaseDAO extends BaseDAO<QuartzUser, Integer> {
	
	public QuartzUser findByUserID( String userId );
	public DataPage<QuartzUser> findPageByName( String name, Page page );
}