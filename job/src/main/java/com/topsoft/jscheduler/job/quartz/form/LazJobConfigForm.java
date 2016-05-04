package com.topsoft.jscheduler.job.quartz.form;

import com.topsoft.topframework.swing.LazForm;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;

public abstract class LazJobConfigForm<T extends LazJobExecutionConfig> extends LazForm<T>{

	private static final long serialVersionUID = 7281714367548162797L;
	
	public void initForm(){
		super.initForm();
	}
	
	@Override
	public T getDTO(){
		return super.getDTO();
	}
}