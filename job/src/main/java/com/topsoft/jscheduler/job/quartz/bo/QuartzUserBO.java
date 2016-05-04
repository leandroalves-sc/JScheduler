package com.topsoft.jscheduler.job.quartz.bo;

import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;
import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;

public interface QuartzUserBO {
	
	public QuartzUser findByUserID( String userId );
	public DataPage<QuartzUser> findPageByName( String name, Page page );
}