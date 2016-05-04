package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.TableCellEditor;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alee.utils.swing.WebDefaultCellEditor;
import com.topsoft.topframework.base.util.SystemUtil;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazDateField;
import com.topsoft.topframework.swing.LazFormList;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;
import com.topsoft.topframework.swing.text.LazTextDocument;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam.JobParamType;

@Lazy
@Component
public class LazJobParameterForm extends LazFormList<LazJobParam> implements LazViewCapable<LazJobDetail>, ActionListener{
	
	private static final long serialVersionUID = -8598042789155628281L;
	
	@Autowired
	private QuartzBO quartzBO;
	
	private LazTableModel<LazJobParam> tableModel;
	private LazTable<LazJobParam> table;
	private LazJobDetail job;
	
	private SimpleDateFormat dtFormat = new SimpleDateFormat( "dd/MM/yyyy" );
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill, wrap 1", "[grow,fill]", "[top][grow,fill]" ) );
		
		add( new LazLabel( "If needed, please inform the job execution parameters below" ) );
		add( getPanelTable(), "grow" );
	}
	
	private LazPanel getPanelTable(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, ins 0", "[grow,fill]" ) );
		
		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add( new LazTableNestedColumn( "Parameter", "key" ) );
		columns.add( new LazTableNestedColumn( "Description", "description" ) );
		columns.add( new LazTableNestedColumn( "Value", "formattedValue" ) );
		
		tableModel = new LazTableModel<LazJobParam>( columns ){
			
			private static final long serialVersionUID = 4010295557912466824L;

			@Override
			public boolean isCellEditable( int rowIndex, int columnIndex ){
				return columnIndex == 2;
			}
			
			@Override
			public void setAttributeValueAt( LazJobParam param, Object value, int column ){
				
				if( value != null && !StringUtils.isBlank( value.toString() ) ){
					
					if( param.getType() == JobParamType.DATE ){
					
						try{
						
							Date date = dtFormat.parse( value.toString() );
						
							if( date != null )
								param.setValue( date );
						}
						catch( ParseException e ){}
					}
					else if( param.getType() == JobParamType.DOUBLE )
						param.setValue( SystemUtil.isDoubleOk( value ) ? Double.parseDouble( value.toString() ) : null );
					else if( param.getType() == JobParamType.INTEGER )
						param.setValue( SystemUtil.isIntOk( value ) ? Integer.parseInt( value.toString() ) : null );
					else
						param.setValue( value.toString() );
				}
			}
		};
			
		table = new LazTable<LazJobParam>( tableModel ){
			
			private static final long serialVersionUID = 7835650599025529198L;

			@Override
			public TableCellEditor getCellEditor( int row, int column ){
				
				LazJobParam param = tableModel.getRowElementAt( row );
				
				if( param.getType() == JobParamType.DATE )
					return new WebDefaultCellEditor<LazDateField>( new LazDateField() );
				else if( param.getType() == JobParamType.DOUBLE )
					return new WebDefaultCellEditor<LazDateField>( new LazTextField( "", new LazTextDocument( 10, true, true ) ) );
				else if( param.getType() == JobParamType.INTEGER )
					return new WebDefaultCellEditor<LazTextField>( new LazTextField( "", new LazTextDocument( 10, true, false ) ) );
				
				return super.getCellEditor( row, column );
			}
		};

		panel.add( new LazScrollPane( table ), "grow" );
		table.setColumnWidths( new double[]{ 150, 1, 250 } );

		return panel;
	}
	
	@Override 
	protected void loadForm(){
		tableModel.setData( dto );
	}
	
	private void onExecutarClicked(){
		
		JobDataMap map = new JobDataMap();
		
		for( LazJobParam param : tableModel.getData() )
			if( param.getValue() != null && !StringUtils.isBlank( param.getValue().toString() ) )
				map.put( param.getKey(), param.getValue() );
		
		quartzBO.runJob( job, map );
		dispose();
	}
	
	@Override
	public String getTitle(){
		
		String title = "Job parameters";
		
		if( job != null )
			return ": " + job.getName();

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
		return new LazButtonType[]{ LazButtonType.RUN, LazButtonType.CANCEL };
	}
	
	public LazJobDetail getJob(){
		return job;
	}
	
	public void setJob( LazJobDetail job ){
		this.job = job;
	}

	@Override
	public void actionPerformed( ActionEvent event ){
		
		super.actionPerformed( event );
		
		if( event.getSource() instanceof LazButton ){

			LazButton button = (LazButton) event.getSource();
			
			if( button.getType() != null && button.getType() == LazButtonType.RUN )
				onExecutarClicked();
		}
	}
	
	@Override protected void saveForm(){}
}