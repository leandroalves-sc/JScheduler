package com.topsoft.jscheduler.job.quartz.form;

import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazSpinner;
import com.topsoft.topframework.swing.model.LazSpinnerNumberModel;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionSleep;

@Lazy
@Component
public class LazJobConfigSleepForm extends LazJobConfigForm<LazJobExecutionSleep>{

	private static final long serialVersionUID = -2617690290770342080L;

	private LazSpinner<Integer> spnSleep;

	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]" ) );
		
		add( "Time in minutes: ", spnSleep = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( 1, 1, 120, 1 ) ) );
	}

	@Override
	protected void loadForm(){
		
		spnSleep.setValue( dto.getMinutos() );
	}

	@Override
	protected void saveForm(){
		
		if( dto == null )
			dto = new LazJobExecutionSleep();
		
		dto.setMinutos( spnSleep.getValue() );
	}
	
	@Override
	public void resetForm(){
		spnSleep.setValue(1);
	}	

	@Override
	protected LazButtonType[] getButtons(){
		return null;
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
		super.actionPerformed( event );
	}
}