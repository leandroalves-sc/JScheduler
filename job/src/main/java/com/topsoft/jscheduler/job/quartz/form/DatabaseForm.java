package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazPasswordField;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.jscheduler.job.quartz.bo.DatabaseBaseBO;
import com.topsoft.jscheduler.job.quartz.domain.Database;
import com.topsoft.jscheduler.job.quartz.domain.type.DatabaseType;

@Lazy
@Component
public class DatabaseForm extends LazForm<Database> implements LazViewCapable<Database>{
	
	private static final long serialVersionUID = 4576296735621860209L;

	@Autowired
	private DatabaseBaseBO databaseBO;
	
	private LazTextField txfName, txfURL, txfUsername;
	private LazComboBox<DatabaseType> cmbType;
	private LazPasswordField txfPassword;

	private LazButton btnTestConnection;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fillx, wrap 2", "[left][grow,fill]" ) );

		add( "Description: ", txfName = new LazTextField() );
		add( "Type: ", cmbType = new LazComboBox<DatabaseType>( DatabaseType.values() ) );
		add( "URL: ", txfURL = new LazTextField() );
		add( "User: ", txfUsername = new LazTextField() );
		add( "Password: ", txfPassword = new LazPasswordField() );
		
		txfName.setRequired( true );
		cmbType.setRequired( true );
		txfURL.setRequired( true );
		txfUsername.setRequired( true );
		txfPassword.setRequired( true );
		
		cmbType.setFirstRowText( "" );
	}
	
	@Override
	protected LazPanel getPanelButtons( LazButtonType[] buttonTypes ){
		
		LazPanel panel = super.getPanelButtons( buttonTypes );
		panel.add( btnTestConnection = new LazButton( LazImage.DATABASE_TEST, "Test connection" ), "tag left" );
		
		return panel;
	}	
	
	@Override 
	protected void loadForm(){

		txfName.setText( dto.getName() );
		cmbType.setSelectedItem( dto.getType() );
		txfURL.setText( dto.getUrl() );
		txfUsername.setText( dto.getUsername() );
		txfPassword.setText( dto.getPassword() );
	}
	
	@Override
	public String getTitle(){
		return "Databases";
	}

	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 600, 280 );
	}

	@Override
	public LazButtonType[] getButtons(){
		return new LazButtonType[]{ LazButtonType.SAVE, LazButtonType.CANCEL };
	}
	
	@Override 
	protected void saveForm(){
		
		dto.setName( txfName.getText() );
		dto.setType( cmbType.getSelectedItem() );
		dto.setUrl( txfURL.getText() );
		dto.setUsername( txfUsername.getText() );
		dto.setPassword( new String( txfPassword.getPassword() ) );
		
		databaseBO.insertOrUpdate( dto );
	}
	
	private void onTypeChanged(){
		
		DatabaseType type = cmbType.getSelectedItem();
		txfURL.setText( type == null ? "" : type.getUrlExample() );
	}
	
	@Override
	public void isFormValid() throws BusinessException{
	
		super.isFormValid();
		testConnection();
	}
	
	private void onTestConnectionClicked(){
		
		try{
			
			isFormValid();
			LazAlert.showInfo( "Connection successfully made!" );
		}
		catch( BusinessException e ){
			LazAlert.showWarning( e.getMessage() );
		}
	}
	
	private void testConnection() throws BusinessException{
		
		Connection connection = null;
		
		try{
			
			DatabaseType dbType = cmbType.getSelectedItem();
			
			Class.forName( dbType.getDriverClass() );
			connection = DriverManager.getConnection( txfURL.getText(), txfUsername.getText(), new String( txfPassword.getPassword() ) );
			
			PreparedStatement pstm = connection.prepareStatement( dbType.getTestQuery() );
			ResultSet rs = pstm.executeQuery();
			
			if( !rs.next() )
				throw new BusinessException( "Error while testing connection. Please validate all data!" );	
		}
		catch( Exception e ){
			throw new BusinessException( "Error while testing connection.\n" + e.getMessage() );
		}
		finally{
			
			try{
				
				if( connection != null && !connection.isClosed() )
					connection.close();
			}
			catch( SQLException e ){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
		
		super.actionPerformed( event );
		
		Object source = event.getSource();
		
		if( source == cmbType )
			onTypeChanged();
		else if( source == btnTestConnection ){
			onTestConnectionClicked();
		}
	}
}