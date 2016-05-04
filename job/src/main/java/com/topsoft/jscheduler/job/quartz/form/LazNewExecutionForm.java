package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazSpinner;
import com.topsoft.topframework.swing.LazTextArea;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.model.LazSpinnerDateModel;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;

@Lazy
@Component
public class LazNewExecutionForm extends LazForm<JobDetail> implements LazViewCapable<JobDetail>, ChangeListener{

	private static final long serialVersionUID = 5062438853934320840L;
	
	@Autowired
	private QuartzBO quartzBO;
	
	private LazSpinnerDateModel<Date> model;
	private LazSpinner<Date> spinner;
	private LazTextArea txaObservacao;
	private LazLabel lblDate;

	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "ins 0, wrap 2", "[right][grow,fill]", "[][][grow,fill]" ) );

		add( new LazLabel( "Execution at:" ) );
		add( spinner = new LazSpinner<Date>( model = new LazSpinnerDateModel<Date>() ), "w 150!" );
		
		add( lblDate = new LazLabel(), "skip" );
		
		add( new LazLabel( "Notes:", SwingConstants.RIGHT, SwingConstants.TOP ), "gaptop 5" );
		add( new LazScrollPane( txaObservacao = new LazTextArea() ), "grow" );
		
		model.setCalendarField( Calendar.YEAR );
		
		spinner.setRequired( true );
		txaObservacao.setRequired( true );
	}
	
	@Override
	protected void loadForm(){
	
		model.setValue( new Date() );
		lblDate.setText( DateFormatUtils.format( (Date) spinner.getValue(), "EEEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss" ) );
		txaObservacao.setText( "" );
	}

	@Override
	protected void saveForm(){
		
		Date date = (Date) spinner.getValue();
		date = DateUtils.truncate( date, Calendar.MINUTE );
		
		quartzBO.scheduleTrigger( dto, txaObservacao.getText(), date );
	}
	
	@Override
	public String getTitle(){
		return "New execution";
	}
	
	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 600, 270 );
	}
	
	@Override
	public LazButtonType[] getButtons(){
		return new LazButtonType[]{ LazButtonType.SAVE, LazButtonType.CANCEL };
	}

	@Override
	public void stateChanged( ChangeEvent e ){

		Object value = spinner.getValue();
		
		if( value != null && value instanceof Date )
			lblDate.setText( DateFormatUtils.format( (Date) value, "EEEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss" ) );
	}
}