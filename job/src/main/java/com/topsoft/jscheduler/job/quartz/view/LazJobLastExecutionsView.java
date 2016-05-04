package com.topsoft.jscheduler.job.quartz.view;

import java.awt.Dimension;
import java.util.Vector;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.jscheduler.job.quartz.bo.LazJobExecutionBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;
import com.topsoft.jscheduler.job.util.LazJobContext;
import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazFormView;
import com.topsoft.topframework.swing.LazPaginator;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.event.LazPageEvent;
import com.topsoft.topframework.swing.event.LazTableEvent;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableImageColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class LazJobLastExecutionsView extends LazForm<LazJobDetail> implements LazViewCapable<JobDetail>{
	
	private static final long serialVersionUID = 5391857787349300458L;
	
	@Autowired
	private LazJobExecutionBO executionBO;
	
	private LazTableModel<LazJobExecution> tableModel;
	private LazTable<LazJobExecution> table;
	private LazPaginator paginator;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill, wrap 1", "[grow,fill]", "[grow,fill][baseline,nogrid]" ) );

		add( getPanelParams(), "grow" );
		add( paginator = new LazPaginator( 10 ), "growx" );
	}
	
	private LazPanel getPanelParams(){

		LazPanel panel = new LazPanel( new MigLayout( "fill, ins 0", "[grow,fill]" ) );
		
		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add( new LazTableNestedColumn( "Execution start time", "formattedFiredTime" ) );
		columns.add( new LazTableNestedColumn( "Execution end time", "formattedEndTime" ) );
		columns.add( new LazTableImageColumn( LazImage.LOG, "Execution log" ) );
		columns.add( new LazTableImageColumn( "imgStatus", "toolTipStatus" ) );
		
		tableModel = new LazTableModel<LazJobExecution>( columns );
			
		table = new LazTable<LazJobExecution>( tableModel );
		table.setColumnWidths( new double[]{ .5, .5, 25, 25 } );

		panel.add( new LazScrollPane( table ), "grow" );

		return panel;
	}
	
	@Override
	protected void loadForm(){
		
		DataPage<LazJobExecution> dataPage = executionBO.findPageLastJobExecutions( dto, paginator.getPage() );
		
		if( dataPage != null ){
		
			tableModel.setData( dataPage.getData() );
			paginator.refreshFor( dataPage );
		}
		else{
			
			tableModel.removeAll();
			paginator.refreshFor( null );
		}
	}
	
	private void onViewLog(){
		
		LazJobExecution jobExecution = table.getSelectedItem();
		
		if( jobExecution != null )
			LazFormView.openForm( LazJobContext.getBean( LazJobLogView.class ), jobExecution.getJobLog() );
	}

	@Override
	public String getTitle(){
		
		String title = "Execution log history";
		
		if( dto != null )
			return ": " + dto.getKey().getName();

		return title;
	}

	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 800, 500 );
	}

	@Override
	public LazButtonType[] getButtons(){
		return null;
	}
	
	@Override
	public void tableEvent( LazTableEvent event ){
	
		if( event.getID() == LazTableEvent.IMAGE_CLICKED  && event.getColumn() == 2 )
			onViewLog();		
	}
	
	@Override
	public void pageEvent( LazPageEvent e ){
		refresh();
	}

	@Override protected void saveForm(){}
}