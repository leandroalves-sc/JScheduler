package com.topsoft.jscheduler.job.quartz.form;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swing.MigLayout;

import org.quartz.JobKey;
import org.quartz.utils.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.jscheduler.job.quartz.bo.LazSchedulerBaseBO;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionDelegate;
import com.topsoft.jscheduler.job.util.LazJobContext;

@Lazy
@Component
public class LazJobConfigDelegateForm extends LazJobConfigForm<LazJobExecutionDelegate>{

	private static final long serialVersionUID = 522455396318570985L;
	
	@Autowired 
	private LazSchedulerBaseBO schedulerBO;
	
	@Autowired
	private QuartzBO quartzBO;

	private LazComboBox<String> cmbScheduler, cmbGroup, cmbJob;

	@Override
	protected void createForm(){

		setLayout( new MigLayout( "fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]" ) );
		
		add( "Scheduler: ", cmbScheduler = new LazComboBox<String>( schedulerBO.findAllNames() ) );
		add( "Group: ", cmbGroup = new LazComboBox<String>() );
		add( "Job: ", cmbJob = new LazComboBox<String>() );
		
		cmbScheduler.setFirstRowText( "" );
		cmbScheduler.setDescriptionMethod( "schedulerName" );
		cmbGroup.setFirstRowText( "" );
		cmbJob.setFirstRowText( "" );
		
		cmbScheduler.setRequired( true );
		cmbGroup.setRequired( true );
	}

	@Override
	protected void loadForm(){
		
		cmbScheduler.setSelectedItem( dto.getSchedulerName() );
		cmbGroup.setSelectedItem( dto.getGroupName() );
		cmbJob.setSelectedItem( dto.getJobName() );
	}
	
	@Override
	protected void saveForm(){
		
		if( dto == null )
			dto = new LazJobExecutionDelegate();
		
		dto.setSchedulerName( cmbScheduler.getSelectedItem() );
		dto.setGroupName( cmbGroup.getSelectedItem() );
		dto.setJobName( cmbJob.getSelectedItem() );
	}
	
	@Override
	public void isFormValid() throws BusinessException{
		
		super.isFormValid();
		
		if( Key.DEFAULT_GROUP.equals( cmbGroup.getSelectedItem() ) && cmbJob.getSelectedIndex() <= 0 )
			throw new BusinessException( "Select a specific Job for the DEFAULT Group." );
	}
	
	@Override
	public void resetForm(){
		cmbScheduler.setSelectedIndex(0);	
	}

	@Override
	protected LazButtonType[] getButtons(){
		return null;
	}
	
	private void onSchedulerChanged(){
		
		List<String> groups = new ArrayList<String>();
		
		if( cmbScheduler.getSelectedIndex() > 0 )
			for( LazJobDetail group : quartzBO.findAllJobs( cmbScheduler.getSelectedItem() ) )
				if( group.isGroup() )
					groups.add( group.getGroup() );
		
		cmbGroup.setModel( groups );
	}
	
	private void onGroupChanged(){
		
		List<String> jobs = new ArrayList<String>();
		
		if( cmbScheduler.getSelectedIndex() > 0 && cmbGroup.getSelectedIndex() > 0 ){
			
			LazJobDetail job = quartzBO.findJobByKey( cmbScheduler.getSelectedItem(), new JobKey( cmbGroup.getSelectedItem(), cmbGroup.getSelectedItem() ) );
			
			if( job.isGroup() ){
				
				LazJobDetailForm view = LazJobContext.getBean( LazJobDetailForm.class );
				
				for( LazJobDetail jobToExecute : job.getJobs() )
					if( view == null || view.getDTO().getKey() == null || jobToExecute.getKey().compareTo( view.getDTO().getKey() ) != 0 )
						jobs.add( jobToExecute.getName() );
			}
		}
		
		cmbJob.setModel( jobs );
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
	
		super.actionPerformed( event );
		
		Object source = event.getSource();
		
		if( source == cmbScheduler )
			onSchedulerChanged();
		else if( source == cmbGroup )
			onGroupChanged();
	}
}