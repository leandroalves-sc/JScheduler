package com.topsoft.jscheduler.job.quartz.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazTabbedPane;
import com.topsoft.topframework.swing.LazView;
import com.topsoft.jscheduler.job.quartz.bo.DatabaseBaseBO;
import com.topsoft.jscheduler.job.quartz.bo.HolidayBO;
import com.topsoft.jscheduler.job.util.LazJobContext;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class QuartzSettingsView extends LazView{
	
	private static final long serialVersionUID = 5391857787349300458L;
	
	@Autowired
	private QuartzView view;
	
	@Autowired
	private HolidayBO holidayBO;
	
	@Autowired
	private DatabaseBaseBO databaseBO;
	
	@Override
	public void createView(){
	
		setLayout( new MigLayout( "fill, wrap 1", "[grow,fill]" ) );
		setTitle( "Settings" );
		setResizable( false );
		setSize( 800, 500 );
		
		HolidaysView holidayForm = LazJobContext.getBean( HolidaysView.class );
		DatabasesView dbForm = LazJobContext.getBean( DatabasesView.class );
		
		LazTabbedPane tabPane = new LazTabbedPane();
		tabPane.addTab( "Holidays", holidayForm );
		tabPane.addTab( "Databases", dbForm );
		add( tabPane, "grow" );
		
		holidayForm.refreshFor( holidayBO.findAll(), view.isReadOnly() );
		dbForm.refreshFor( databaseBO.findAll(), view.isReadOnly() );
		
		setVisible( true );
	}
}