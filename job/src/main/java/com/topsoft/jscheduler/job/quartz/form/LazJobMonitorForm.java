package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazSearchInput;
import com.topsoft.topframework.swing.LazSwitch;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.event.LazFormEvent;
import com.topsoft.jscheduler.job.quartz.domain.LazJobMonitor;
import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzLevel;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzPeriod;
import com.topsoft.jscheduler.job.quartz.lov.QuartzUserLov;
import com.topsoft.jscheduler.job.util.LazJobContext;

@Lazy
@Component
public class LazJobMonitorForm extends LazForm<LazJobMonitor> implements LazViewCapable<LazJobMonitor>{
	
	private static final long serialVersionUID = 4576296735621860209L;

	@Autowired
	private LazJobDetailForm configView;
	
	private LazComboBox<QuartzPeriod> cmbSMSPeriod, cmbEmailPeriod;
	private LazComboBox<QuartzLevel> cmbSMSLevel, cmbEmailLevel;
	private LazSearchInput<QuartzUser> cmbUser;
	private LazSwitch swtSMS, swtEmail, swtErrors;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fillx, wrap 2", "[left][grow,fill]" ) );
		
		add( "User: ", cmbUser = new LazSearchInput<QuartzUser>( LazJobContext.getBean( QuartzUserLov.class ) ), "grow" );
		
		addSeparator( "Monitoramento de erros" );
		add( "Active: ",  swtErrors = new LazSwitch(), "w pref!" );

		addSeparator( "Monitoramento via SMS" );
		add( "Active: ",  swtSMS = new LazSwitch(), "w pref!" );
		add( "Warning level: ", "w pref!", cmbSMSLevel = new LazComboBox<QuartzLevel>( QuartzLevel.values() ), "grow" );
		add( "Periodicity: ", cmbSMSPeriod = new LazComboBox<QuartzPeriod>( QuartzPeriod.values() ), "grow" );
		
		addSeparator( "Monitoramento via Email" );
		add( "Active: ",  swtEmail = new LazSwitch(), "w pref!" );
		add( "Warning level: ", "w pref!", cmbEmailLevel = new LazComboBox<QuartzLevel>( QuartzLevel.values() ), "grow" );
		add( "Periodicity: ", cmbEmailPeriod = new LazComboBox<QuartzPeriod>( QuartzPeriod.values() ), "grow" );
		
		cmbSMSLevel.setFirstRowText( "" );
		cmbEmailLevel.setFirstRowText( "" );
		cmbSMSPeriod.setFirstRowText( "" );
		cmbEmailPeriod.setFirstRowText( "" );
		
		cmbUser.setCodeMethod( "userId" );
		cmbUser.setDescriptionMethod( "nameAndCellPhone" );
		cmbUser.setCodeType( String.class );
		cmbUser.setRequired( true );
		
		setSize( new Dimension( 650, 380 ) );
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 650, configView != null && configView.getDTO().isGroup() ? 450 : 390 );
	}
	
	@Override 
	protected void loadForm(){
		
		cmbSMSLevel.setVisible( configView.getDTO().isGroup() );
		cmbEmailLevel.setVisible( configView.getDTO().isGroup() );

		swtSMS.setSelected( dto.isSmsMonitor(), false );
		swtEmail.setSelected( dto.isEmailMonitor(), false );
		swtErrors.setSelected( dto.isErrorMonitor(), false );
		cmbSMSLevel.setSelectedItem( dto.getSmsLevel() );
		cmbEmailLevel.setSelectedItem( dto.getEmailLevel() );
		cmbSMSPeriod.setSelectedItem( dto.getSmsPeriod() );
		cmbEmailPeriod.setSelectedItem( dto.getEmailPeriod() );
		cmbUser.setSelectedItem( dto.getUser() );
	}
	
	@Override 
	protected void saveForm(){
		
		dto.setSmsMonitor( swtSMS.isSelected() );
		dto.setEmailMonitor( swtEmail.isSelected() );
		dto.setErrorMonitor( swtErrors.isSelected() );
		dto.setSmsLevel( !cmbSMSLevel.isVisible() ? QuartzLevel.SERVICE : cmbSMSLevel.getSelectedItem() );
		dto.setEmailLevel( !cmbEmailLevel.isVisible() ? QuartzLevel.SERVICE : cmbEmailLevel.getSelectedItem() );
		dto.setSmsPeriod( cmbSMSPeriod.getSelectedItem() );
		dto.setEmailPeriod( cmbEmailPeriod.getSelectedItem() );
		dto.setUser( cmbUser.getSelectedItem() );
		
		dispatchFormEvent( LazFormEvent.SAVE );
	}	
	
	@Override
	protected boolean showSavedMessage(){
		return false;
	}
	
	@Override
	public String getTitle(){
		return "Monitor";
	}

	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public LazButtonType[] getButtons(){
		return new LazButtonType[]{ LazButtonType.SAVE, LazButtonType.CANCEL };
	}
	
	@Override
	public void isFormValid() throws BusinessException{
	
		super.isFormValid();
		
		if( swtEmail.isSelected() && !cmbUser.getSelectedItem().hasEmailAdded() )
			throw new BusinessException( "Usuer with no e-mail. E-mail monitoring can not be used." );

		if( swtSMS.isSelected() && !cmbUser.getSelectedItem().hasCellPhoneAdded() )
			throw new BusinessException( "User with no mobile phone. SMS monitoring can not be used." );
		
		if( !swtSMS.isSelected() && !swtEmail.isSelected() && !swtErrors.isSelected() )
			throw new BusinessException( "Required to choose at least one monitoring option." );
	}
	
	private void onMonitoringSMSClicked(){
		
		boolean monitoringSMS = swtSMS.isSelected();
		
		cmbSMSLevel.setEnabled( monitoringSMS );
		cmbSMSPeriod.setEnabled( monitoringSMS );
		
		cmbSMSLevel.setRequired( monitoringSMS );
		cmbSMSPeriod.setRequired( monitoringSMS );
		
		if( !swtSMS.isSelected() ){
			
			cmbSMSLevel.setSelectedIndex( -1 );	
			cmbSMSPeriod.setSelectedIndex( -1 );
		}
	}
	
	private void onMonitoringEmailClicked(){
		
		boolean monitoringEmail = swtEmail.isSelected();

		cmbEmailLevel.setEnabled( monitoringEmail );
		cmbEmailPeriod.setEnabled( monitoringEmail );
		
		cmbEmailLevel.setRequired( monitoringEmail );
		cmbEmailPeriod.setRequired( monitoringEmail );
		
		if( !swtEmail.isSelected() ){
			
			cmbEmailLevel.setSelectedIndex( -1 );	
			cmbEmailPeriod.setSelectedIndex( -1 );
		}
	}	
	
	@Override
	public void actionPerformed( ActionEvent event ){
		
		super.actionPerformed( event );
		
		Object source = event.getSource();
		
		if( source == cmbSMSLevel )
			cmbSMSPeriod.setSelectedIndex(-1);
		else if( source == cmbEmailLevel )
			cmbEmailPeriod.setSelectedIndex(-1);
		else if( source == swtSMS )
			onMonitoringSMSClicked();
		else if( source == swtEmail )
			onMonitoringEmailClicked();
	}
}