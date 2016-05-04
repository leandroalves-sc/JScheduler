package com.topsoft.jscheduler.job.quartz.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazFormView;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazMenuItem;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazPopupMenu;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.event.LazFormEvent;
import com.topsoft.topframework.swing.event.LazMenuItemEvent;
import com.topsoft.topframework.swing.event.LazMenuItemListener;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableImageColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;
import com.topsoft.jscheduler.job.quartz.bo.LazJobExecutionBO;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;
import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;
import com.topsoft.jscheduler.job.quartz.form.LazNewExecutionForm;
import com.topsoft.jscheduler.job.quartz.form.LazSuspendedTriggerForm;
import com.topsoft.jscheduler.job.util.LazJobContext;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class LazJobNextExecutionsView extends LazForm<LazJobDetail> implements LazViewCapable<JobDetail>, LazMenuItemListener, PopupMenuListener{
	
	private static final long serialVersionUID = 5391857787349300458L;
	
	@Autowired
	private QuartzBO quartzBO;
	
	@Autowired
	private LazJobExecutionBO executionBO;
	
	private LazMenuItem mnuSuspend, mnuReschedule;
	private LazTableModel<LazTrigger> tableModel;
	private LazTable<LazTrigger> table;
	private LazComboBox<Integer> cmbNExecutions;
	private LazButton btnNewExecution;

	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill, wrap 1", "[grow,fill]", "[grow,fill][baseline,nogrid]" ) );

		add( getPanelExecutions(), "grow" );
		add( getPanelExecutionsControl(), "grow" );
	}
	
	private LazPanel getPanelExecutions(){

		LazPanel panel = new LazPanel( new MigLayout( "fill, ins 0", "[grow,fill]" ) );
		
		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add( new LazTableNestedColumn( "Start time", "fullFormattedNextFireTime" ) );
		columns.add( new LazTableImageColumn( "stateIcon", "stateTooltip" ) );
		
		tableModel = new LazTableModel<LazTrigger>( columns );
			
		table = new LazTable<LazTrigger>( tableModel );
		table.setColumnWidths( new double[]{ 1, 25 } );

		panel.add( new LazScrollPane( table ), "grow" );

		return panel;
	}
	
	private LazPanel getPanelExecutionsControl(){

		LazPanel panel = new LazPanel( new MigLayout( "fill, ins 0, nogrid" ) );
		
		panel.add( btnNewExecution = new LazButton( LazImage.ADD, "New execution" ), "tag left" );
		
		panel.add( new LazLabel( "Executions" ), "tag right" );
		panel.add( cmbNExecutions = new LazComboBox<Integer>( new Integer[]{ 5, 15, 50, 75, 100 } ), "w 80!, tag right" );
		
		cmbNExecutions.setSelectedItem(0);
		
		return panel;
	}
	
	private LazPopupMenu getMenu(){
		
		LazPopupMenu menu = new LazPopupMenu();
		menu.addMenuItemListener( this );
		menu.addPopupMenuListener( this );
		
		menu.add( mnuSuspend = new LazMenuItem( "Suspend execution" ) );
		menu.add( mnuReschedule = new LazMenuItem( "Reschedule execution" ) );
		
		return menu;
	}
	
	@Override
	protected void loadForm(){
		
		table.setComponentPopupMenu( !isReadOnly() && table.getComponentPopupMenu() == null ? getMenu() : null );
		
		cmbNExecutions.setEnabled( true );
		
		if( cmbNExecutions.getSelectedIndex() == -1 )
			cmbNExecutions.setSelectedIndex( 0 );
		
		tableModel.setData( executionBO.findAllNextJobExecutions( dto, cmbNExecutions.getSelectedItem() ) );
	}
	
	private void onSuspend(){
		
		LazTrigger trigger = table.getSelectedItem();
		
		if( trigger != null )
			LazFormView.openForm( LazJobContext.getBean( LazSuspendedTriggerForm.class ), trigger );
	}
	
	public void onReschedule(){
		
		LazTrigger trigger = table.getSelectedItem();
	
		if( trigger != null && LazAlert.showQuestion( "Cancel Job suspension?" ) == JOptionPane.YES_OPTION ){
			
			quartzBO.resumeTrigger( trigger );
			refresh();
		}
	}

	@Override
	public String getTitle(){
		
		String title = "Next Job executions";
		
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
		return new Dimension( 500, 500 );
	}

	@Override
	public LazButtonType[] getButtons(){
		return null;
	}
	
	private void onNewExecution(){
		
		LazNewExecutionForm newExecutionView = LazJobContext.getBean( LazNewExecutionForm.class );
		
		if( !newExecutionView.hasEvent( this ) )
			newExecutionView.addActionListener( this );
			
		LazFormView.openForm( newExecutionView, dto );
	}

	@Override
	public void popupMenuWillBecomeVisible( PopupMenuEvent e ){
	
		LazTrigger trigger = table.getSelectedItem();
        
        if( trigger != null ){
        
        	mnuSuspend.setEnabled( trigger.getState() != LazTriggerState.SUSPENDED );
        	mnuReschedule.setEnabled( trigger.getState() == LazTriggerState.SUSPENDED );
        }
	}
	
	@Override
	public void menuItemClicked( LazMenuItemEvent event ){
		
		Object source = event.getSource();
		
		if( source == mnuSuspend )
			onSuspend();
		else if( source == mnuReschedule )
			onReschedule();
	}	
	
	@Override
	public void actionPerformed( ActionEvent event ){
	
		if( event.getSource() == cmbNExecutions )
			refresh();
		else if( event.getSource() == btnNewExecution )
			onNewExecution();
	}	
	
	@Override
	public void onSave( LazFormEvent event ){
		refresh();
	}
	
	@Override protected void saveForm(){}
	@Override public void popupMenuWillBecomeInvisible( PopupMenuEvent e ){}
	@Override public void popupMenuCanceled( PopupMenuEvent e ){}
}