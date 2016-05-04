package com.topsoft.jscheduler.job.quartz.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionJava;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.SchedulerJob;

@Lazy
@Component
public class LazJobConfigJavaForm extends LazJobConfigForm<LazJobExecutionJava>{

	private static final long serialVersionUID = -6005262584377303210L;
	
	@Autowired
	private QuartzBO quartzBO;
	
	private LazComboBox<String> cmbJava;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fillx, ins 0", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]" ) );
		
		String schedulerName = quartzBO.getSchedulerName();
		Map<String,LazJob> beans = appContext.getBeansOfType( LazJob.class );
		
		List<String> list = new ArrayList<String>();
		
		for( String key : beans.keySet() ){
			
			LazJob job = beans.get( key );
			SchedulerJob annotation = job.getAnnotation( SchedulerJob.class );
			
			if( annotation != null && schedulerName.equals( annotation.scheduler() ) )
				list.add( key );
		}
		
		Collections.sort( list );
		
		add( "Java Job: ", cmbJava = new LazComboBox<String>( list ) );
		
		cmbJava.setFirstRowText( "" );
		cmbJava.setRequired( true );
	}

	@Override
	protected void loadForm(){
		cmbJava.setSelectedItem( dto.getJavaClass() );
	}

	@Override
	protected void saveForm(){
		
		if( dto == null )
			dto = new LazJobExecutionJava();
		
		dto.setJavaClass( cmbJava.getSelectedItem() );
	}
	
	@Override
	public void resetForm(){
		cmbJava.setSelectedIndex(0);	
	}

	@Override
	protected LazButtonType[] getButtons(){
		return null;
	}
}