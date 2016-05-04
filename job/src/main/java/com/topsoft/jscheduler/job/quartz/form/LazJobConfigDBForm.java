package com.topsoft.jscheduler.job.quartz.form;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.jscheduler.job.quartz.bo.DatabaseBaseBO;
import com.topsoft.jscheduler.job.quartz.domain.Database;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionDB;

@Lazy
@Component
public class LazJobConfigDBForm extends LazJobConfigForm<LazJobExecutionDB>{

	private static final long serialVersionUID = -2617690290770342080L;

	@Autowired
	private DatabaseBaseBO dbBO;
	
	private LazComboBox<String> cmbOwner, cmbObject;
	private LazComboBox<Database> cmbDB;

	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]" ) );
		
		add( "Database: ", cmbDB = new LazComboBox<Database>( dbBO.findAll() ) );
		add( "Owner: ", cmbOwner = new LazComboBox<String>() );
		add( "Object: ", cmbObject = new LazComboBox<String>() );
		
		cmbDB.setFirstRowText( "" );
		cmbOwner.setFirstRowText( "" );
		cmbObject.setFirstRowText( "" );

		cmbDB.setRequired( true );
		cmbOwner.setRequired( true );
		cmbObject.setRequired( true );
	}

	@Override
	protected void loadForm(){
		
		cmbDB.setModel( dbBO.findAll() );
		
		cmbDB.setSelectedItem( dto.getDatabase() );
		cmbOwner.setSelectedItem( dto.getOwner() );
		cmbObject.setSelectedItem( dto.getObject() );
	}

	@Override
	protected void saveForm(){
		
		if( dto == null )
			dto = new LazJobExecutionDB();
		
		dto.setDatabase( cmbDB.getSelectedItem() );
		dto.setOwner( cmbOwner.getSelectedItem() );
		dto.setObject( cmbObject.getSelectedItem() );
	}
	
	@Override
	public void resetForm(){
		cmbDB.setSelectedIndex(0);
	}	

	@Override
	protected LazButtonType[] getButtons(){
		return null;
	}
	
	private void onDBChanged(){
		
		List<String> owners = new ArrayList<String>();
		
		if( cmbDB.getSelectedIndex() > 0 )
			owners = dbBO.findAllOwners( cmbDB.getSelectedItem() );
		
		cmbOwner.setModel( owners );
	}
	
	private void onOwnerChanged(){
		
		List<String> objects = new ArrayList<String>();
		
		if( cmbDB.getSelectedIndex() > 0 && cmbOwner.getSelectedIndex() > 0 )
			objects = dbBO.findAllObjects( cmbDB.getSelectedItem(), cmbOwner.getSelectedItem() );
		
		cmbObject.setModel( objects );
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
	
		super.actionPerformed( event );
		
		Object source = event.getSource();
		
		if( source == cmbDB )
			onDBChanged();
		else if( source == cmbOwner )
			onOwnerChanged();
	}
}