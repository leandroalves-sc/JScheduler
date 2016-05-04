package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazDateField;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.jscheduler.job.quartz.bo.HolidayBO;
import com.topsoft.jscheduler.job.quartz.domain.Holiday;

@Lazy
@Component
public class HolidayForm extends LazForm<Holiday> implements LazViewCapable<Holiday>{
	
	private static final long serialVersionUID = 5391857787349300458L;
	
	@Autowired
	private HolidayBO holidayBO;
	
	private LazDateField txfDate;
	private LazTextField txfDescription;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill, wrap 1", "[grow,fill]", "[grow,fill][baseline,nogrid]" ) );

		add( getPanelHoliday(), "grow" );
	}
	
	private LazPanel getPanelHoliday(){

		LazPanel panel = new LazPanel( new MigLayout( "fill, ins 0, wrap 2", "[right][grow,fill]" ) );
		
		panel.add( new LazLabel( "Date:" ) );
		panel.add( txfDate = new LazDateField(), "w 150!" );
		
		panel.add( new LazLabel( "Description:" ) );
		panel.add( txfDescription = new LazTextField(), "growx" );
		
		txfDate.setRequired( true );
		txfDescription.setRequired( true );

		return panel;
	}
	
	@Override protected void loadForm(){
		
		txfDate.setDate( dto.getDate() );
		txfDescription.setText( dto.getName() );
	}
	
	@Override
	public String getTitle(){
		return "Feriados";
	}

	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 600, 200 );
	}

	@Override
	public LazButtonType[] getButtons(){
		return new LazButtonType[]{ LazButtonType.SAVE, LazButtonType.CANCEL };
	}
	
	@Override 
	protected void saveForm(){
		
		dto.setDate( txfDate.getDate() );
		dto.setName( txfDescription.getText() );
		
		holidayBO.insert( dto );
	}
}