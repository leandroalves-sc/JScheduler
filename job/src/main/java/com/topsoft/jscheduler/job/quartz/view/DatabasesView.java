package com.topsoft.jscheduler.job.quartz.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.jscheduler.job.quartz.bo.DatabaseBaseBO;
import com.topsoft.jscheduler.job.quartz.domain.Database;
import com.topsoft.jscheduler.job.quartz.form.DatabaseForm;
import com.topsoft.jscheduler.job.util.LazJobContext;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazFormList;
import com.topsoft.topframework.swing.LazFormView;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.event.LazFormEvent;
import com.topsoft.topframework.swing.event.LazFormListener;
import com.topsoft.topframework.swing.event.LazTableEvent;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableImageActionColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class DatabasesView extends LazFormList<Database> implements ActionListener, LazFormListener{
	
	private static final long serialVersionUID = 5391857787349300458L;
	
	@Autowired
	private DatabaseBaseBO databaseBO;
	
	private LazTableModel<Database> tableModel;
	private LazTable<Database> table;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill, ins 10", "[grow,fill]" ) );

		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add( new LazTableNestedColumn( "Description", "name" ) );
		columns.add( new LazTableNestedColumn( "Type", "type" ) );
		columns.add( new LazTableNestedColumn( "URL", "url" ) );
		columns.add( new LazTableNestedColumn( "User", "username" ) );
		columns.add( new LazTableImageActionColumn( LazImage.EDIT, "Edtiar database" ) );
		columns.add( new LazTableImageActionColumn( LazImage.REMOVE, "Excluir database" ) );
		
		tableModel = new LazTableModel<Database>( columns );
		
		LazTextField txf = new LazTextField();
		txf.setRequired( true );
		
		table = new LazTable<Database>( tableModel );
		table.setColumnWidths( new double[]{ .3, 100, .7, 120, 25, 25 } );

		add( new LazScrollPane( table ), "grow" );
	}
	
	@Override 
	protected void loadForm(){
		tableModel.setData( getDTO() );
	}
	
	@Override
	public LazButtonType[] getButtons(){
		return new LazButtonType[]{ LazButtonType.ADD };
	}
	
	@Override
	public void onSave( LazFormEvent event ){
		refreshFor( databaseBO.findAll() );
	}
	
	private void onEditDatabase(){
		
		Database database = table.getSelectedItem();
		
		if( database != null )
			if( LazAlert.showQuestion( "Edit database?" ) == JOptionPane.YES_OPTION )
				LazFormView.openForm( LazJobContext.getBean( DatabaseForm.class ), database );
	}
	
	private void onDeleteDatabase(){
		
		Database database = table.getSelectedItem();
		
		if( database != null ){
			
			if( LazAlert.showQuestion( "Delete database configuration?" ) == JOptionPane.YES_OPTION ){
			
				databaseBO.remove( database );
				
				LazAlert.showInfo( "Database deleted" );
				refresh();
			}
		}
	}
	
	@Override
	public void tableEvent( LazTableEvent event ){
		
		if( event.getID() == LazTableEvent.IMAGE_CLICKED ){
			
			if( event.getColumn() == 4 )
				onEditDatabase();
			else if( event.getColumn() == 5 )
				onDeleteDatabase();
		}
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
		
		Object source = event.getSource();
		
		if( LazButton.class.isAssignableFrom( source.getClass() ) ){
			
			LazButton button = (LazButton) source;
			
			if( button.getType() == LazButtonType.ADD )
				LazFormView.openForm( this, LazJobContext.getBean( DatabaseForm.class ), new Database() );
		}
	}	
	
	@Override
	public Dimension getSize(){
		return new Dimension( 600, 200 );
	}
}