package com.topsoft.jscheduler.job.quartz.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.jscheduler.job.quartz.bo.HolidayBO;
import com.topsoft.jscheduler.job.quartz.domain.Holiday;
import com.topsoft.jscheduler.job.quartz.form.HolidayForm;
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
public class HolidaysView extends LazFormList<Holiday> implements ActionListener, LazFormListener{
	
	private static final long serialVersionUID = 5391857787349300458L;
	
	@Autowired
	private HolidayBO holidayBO;
	
	private LazTableModel<Holiday> tableModel;
	private LazTable<Holiday> table;
	
	@Override
	protected void createForm(){
	
		setLayout( new MigLayout( "fill, ins 10", "[grow,fill]" ) );

		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add( new LazTableNestedColumn( "Date", "formattedDate", SwingConstants.CENTER ) );
		columns.add( new LazTableNestedColumn( "Description", "name" ) );
		columns.add( new LazTableNestedColumn( "Fixed", "recurrentFlag", SwingConstants.CENTER ) );
		columns.add( new LazTableImageActionColumn( LazImage.REMOVE, "Excluir feriado", "!recurrent" ) );
		
		tableModel = new LazTableModel<Holiday>( columns );
		
		LazTextField txf = new LazTextField();
		txf.setRequired( true );
		
		table = new LazTable<Holiday>( tableModel );
		table.setColumnWidths( new double[]{ .3, .7, 65, 25 } );

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
		refreshFor( holidayBO.findAll() );
	}	
	
	@Override
	public void tableEvent( LazTableEvent event ){
	
		if( event.getID() == LazTableEvent.IMAGE_CLICKED ){
			
			Holiday holiday = table.getSelectedItem();
			
			if( holiday != null ){
				
				if( holiday.isRecurrent() ){

					LazAlert.showWarning( "Fixed holiday. Could not be deleted." );
				}
				else if( LazAlert.showQuestion( "Delete holiday?" ) == JOptionPane.YES_OPTION ){
				
					holidayBO.remove( holiday );
					
					LazAlert.showInfo( "Holiday deleted" );
					refresh();
				}
			}
		}
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
		
		Object source = event.getSource();
		
		if( LazButton.class.isAssignableFrom( source.getClass() ) ){
			
			LazButton button = (LazButton) source;
			
			if( button.getType() == LazButtonType.ADD )
				LazFormView.openForm( this, LazJobContext.getBean( HolidayForm.class ), new Holiday() );
		}
	}
}