package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.spi.OperableTrigger;
import org.quartz.utils.Key;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.base.util.Constants;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.base.util.SystemUtil;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonGroup;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazCheckBox;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazRadioButton;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazSpinner;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.event.LazFormEvent;
import com.topsoft.topframework.swing.model.LazSpinnerDateModel;
import com.topsoft.topframework.swing.model.LazSpinnerNumberModel;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;
import com.topsoft.jscheduler.job.quartz.domain.LazCronExpression;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class LazTriggerForm extends LazForm<LazTrigger> implements LazViewCapable<LazTrigger>, ActionListener, ChangeListener{

	private static final long serialVersionUID = -2925137724836506934L;
	
	private LazSpinner<Integer> spnYearlyStart, spnYearlyEnd, spnYearlyFrom, spnEveryEvent, spnEveryDay, spnYearlyStep, spnEventStart, spnEventEnd;
	private LazRadioButton rbEveryEvent, rbFixedHour, rbYearlyStartEnd, rbYearlyFrom, rbYearly;
	private LazCheckBox chbTime, chbAllDays, chbAllWeekDays, chbAllMonths, chbEveryDay;
	private LazButton btnUseExpression, btnBuildExpression;
	private LazTableModel<String> tableModelExecutions;
	private LazComboBox<Integer> cmbNExecutions;
	private LazComboBox<String> cmbEveryEvent;
	private LazTable<String> tableExecutions;
	private LazCheckBox[] chbDays, chbMonths;
	private LazSpinner<Date> spnFixedHour;
	private LazTextField txfExpression;
	private LazLabel lblBaseDt;

	@Override
	protected void createForm(){
	
		setLayout( new MigLayout( "fill", "[][10][grow,fill]", "[top]" ) );
		
		add( getPanelLeft() );
		add( new JSeparator( JSeparator.VERTICAL ), "alignx center, growy" );
		add( getPanelRight(), "grow" );
	}
	
	@Override
	public String getTitle(){
		return "Chronological expression";
	}
	
	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 1000, 650 );
	}
	
	public LazPanel getPanelLeft(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, wrap 1", "[grow,fill]" ) );
		
		panel.add( getPanelReplay() );
		panel.add( getPanelWeek() );
		panel.add( getPanelMonth() );
		panel.add( getPanelYear() );
		panel.add( btnBuildExpression = new LazButton( LazImage.RUN, "Build expression" ), "tag right" );
		
		return panel;
	}
	
	public LazPanel getPanelRight(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill", "[grow,fill]", "[][grow,fill][baseline,nogrid]" ) );
		
		panel.add( getPanelExpression(), "wrap" );
		panel.add( getPanelExecutions(), "grow, wrap" );
		
		panel.add( btnUseExpression = new LazButton( LazImage.OK, "Confirm" ), "tag right" );		
		
		return panel;
	}
	
	private LazPanel getPanelReplay(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, wrap 4", "[][]20[][]" ) );

		panel.addSeparator( "Repetition" );

		panel.add( rbFixedHour = new LazRadioButton( "at" ) );
		panel.add( spnFixedHour = new LazSpinner<Date>( new LazSpinnerDateModel<Date>(), "HH:mm" ), "w 70!" );

		panel.add( rbEveryEvent = new LazRadioButton( "Each" ) );
		panel.add( spnEveryEvent = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( 1, 1, 1000, 1 ) ), "split 2, w 50!" );
		panel.add( cmbEveryEvent = new LazComboBox<String>( new String[]{ "minute(s)", "hour(s)" } ) );
		
		panel.add( chbEveryDay = new LazCheckBox( "Every day" ), "newline" );
		panel.add( spnEveryDay = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( 1, 1, 30, 1, true ) ), "w 50!" );
			
		panel.add( chbTime = new LazCheckBox( "Between" ) );
		panel.add( spnEventStart = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( 0, 0, 23, 1, true ) ), "split 4, w 50!" );
		panel.add( new LazLabel( "and" ) );
		panel.add( spnEventEnd = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( 0, 0, 23, 1, true ) ), "w 50!" );
		panel.add( new LazLabel( "hours" ) );
		
		LazButtonGroup.groupButtons( rbFixedHour, rbEveryEvent );
		
		return panel;
	}
	
	private LazPanel getPanelWeek(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, wrap 4", "", "[30][30]" ) );
		
		panel.addSeparator( "Days" );
		panel.add( chbAllDays = new LazCheckBox( "All days" ), "newline, gapbottom 5" );
		panel.add( chbAllWeekDays = new LazCheckBox( "All weekdays" ), "span 3, gapbottom 5" );
		
		chbDays = new LazCheckBox[ Constants.weekNamesEN.length ];
		
		for( int i=0; i<Constants.weekNamesEN.length; i++ ){
			
			panel.add( chbDays[i] = new LazCheckBox( Constants.weekNamesEN[i] ) );
			chbDays[i].setName( Constants.weekNamesEN[i].substring( 0, 3 ).toUpperCase() );
		}
		
		return panel;
	}
	
	private LazPanel getPanelMonth(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, wrap 6", "", "[30][30]" ) );
		
		panel.addSeparator( "Month(s)" );
		panel.add( chbAllMonths = new LazCheckBox( "All months" ), "newline, span 6, gapbottom 5" );
		
		chbMonths = new LazCheckBox[ Constants.monthNamesEN.length ];
		
		for( int i=0; i<Constants.monthNamesEN.length; i++ ){
			
			panel.add( chbMonths[i] = new LazCheckBox( Constants.monthNamesEN[i] ) );
			chbMonths[i].setName( Constants.monthNamesEN[i].substring( 0, 3 ).toUpperCase() ); 
		}
		
		return panel;
	}
	
	private LazPanel getPanelYear(){
		
		Calendar cal = Calendar.getInstance();
		int ano = cal.get( Calendar.YEAR );
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, nogrid" ) );
		
		panel.addSeparator( "Year(s)" );
		panel.add( rbYearly = new LazRadioButton( "indefinitely" ), "newline, wrap" );
		
		panel.add( rbYearlyStartEnd = new LazRadioButton( "From" ) );
		panel.add( spnYearlyStart = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( ano, ano, ano + 100, 1 ) ), "w 70!" );
		panel.add( new LazLabel( "to" ) );
		panel.add( spnYearlyEnd = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( ano, ano, ano + 100, 1 ) ), "w 70!, wrap" );
		
		panel.add( rbYearlyFrom = new LazRadioButton( "From " ) );
		panel.add( spnYearlyFrom = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( ano, ano, ano + 100, 1 ) ), "w 70!" );
		panel.add( new LazLabel( "on every" ) );
		panel.add( spnYearlyStep = new LazSpinner<Integer>( new LazSpinnerNumberModel<Integer>( 1, 1, 100, 1 ) ), "w 70!" );
		panel.add( new LazLabel( "years(s)" ) );
		
		LazButtonGroup.groupButtons( panel );
		
		return panel;
	}	
	
	private LazPanel getPanelExpression(){
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, wrap 2", "[][grow,fill]" ) );
		
		panel.add( new LazLabel( "Expression:" ) );
		panel.add( txfExpression = new LazTextField( "" ), "grow" );
		
		panel.add( new LazLabel( "Base date:" ) );
		panel.add( lblBaseDt = new LazLabel( DateFormatUtils.format( Calendar.getInstance(), "EEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss" ) ) );
		
		panel.add( new LazLabel( "Executions:" ) );
		panel.add( cmbNExecutions = new LazComboBox<Integer>( new Integer[]{ 15, 30, 60, 120 } ), "w 100!" );
		
		txfExpression.setEditable( false );
		
		return panel;
	}
	
	private LazPanel getPanelExecutions(){

		LazPanel panel = new LazPanel( new MigLayout( "fill", "[grow,fill]" ) );
		
		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add( new LazTableNestedColumn( "Execution", "this" ) );
		
		tableExecutions = new LazTable<String>( tableModelExecutions = new LazTableModel<String>( columns ) );
		tableExecutions.setColumnWidths( new double[]{ 1 } );

		panel.add( new LazScrollPane( tableExecutions ), "grow" );
		
		return panel;
	}
	
	@Override
	public LazButtonType[] getButtons(){
		return null;
	}
	
	private void onAllMonthsClicked(){
		
		for( LazCheckBox chb : chbMonths )
			chb.setSelectedQuiet( chbAllMonths.isSelected() );
	}	
	
	private boolean isAllDaysSelected(){
		
		for( int i=0; i<chbDays.length; i++ )
			if( !chbDays[i].isSelected() )
				return false;
		
		return true;
	}	
	
	private boolean isAllWeekDaysSelected(){
		
		for( int i=0; i<chbDays.length; i++ )
			if( ( !chbDays[i].isSelected() && i <= 4 ) || ( chbDays[i].isSelected() && i > 4 ) )
				return false;
		
		return true;
	}
	
	private boolean isAllMonthsSelected(){
		
		for( LazCheckBox chb : chbMonths )
			if( !chb.isSelected() )
				return false;
		
		return true;
	}
	
	private boolean isAnyMonthSelected(){
		
		for( LazCheckBox chb : chbMonths )
			if( chb.isSelected() )
				return true;
		
		return false;
	}
	
	private boolean isAnyDaySelected(){
		
		for( LazCheckBox chb : chbDays )
			if( chb.isSelected() )
				return true;
		
		return false;
	}
	
	private boolean isSelectionValid() throws BusinessException{
		
		if( !isAnyDaySelected() && !chbEveryDay.isSelected() )
			throw new BusinessException( "Required to select at least one week day for execution" );
		else if( !isAnyMonthSelected() )
			throw new BusinessException( "Required to select at least one month for execution" );
		
		return true;
	}
	
	@Override
	protected void loadForm(){
		
		if( dto.getKey() == null )
			dto.setKey( new TriggerKey( Key.createUniqueName( Key.DEFAULT_GROUP ), Key.DEFAULT_GROUP ) );
		
		LazCronExpression cronExpression = new LazCronExpression( dto );
		
		StringTokenizer tokens = new StringTokenizer( cronExpression.getExpression(), " " );
		String token = null;
		
		//**********************************************************************************************
		//Seconds
		tokens.nextToken();
		
		//**********************************************************************************************
		//Repetition
		tokens.nextToken(); //Minutes
		tokens.nextToken(); //Hours
		
		rbFixedHour.setSelected( cronExpression.isFixedHour() );
		spnFixedHour.setValue( cronExpression.getFixedHour() );
		rbEveryEvent.setSelected( cronExpression.isEveryEvent() );
		spnEveryEvent.setValue( cronExpression.getEveryEvent() );
		cmbEveryEvent.setSelectedIndex( cronExpression.isEveryHour() ? 1 : 0 );
		chbTime.setSelected( cronExpression.hasStartEndTime() );
		spnEventStart.setValue( cronExpression.getStartTime() ); 
		spnEventEnd.setValue( cronExpression.getEndTime() );
		
		//**********************************************************************************************
		//Month days
		token = tokens.nextToken();
		chbEveryDay.setSelected( cronExpression.isEveryFixedDay() );
		spnEveryDay.setValue( cronExpression.getEveryDay() );
		
		
		//**********************************************************************************************
		//Months
		token = tokens.nextToken();
		
		if( cronExpression.isEveryMonth() )
			chbAllMonths.setSelected( true );
		else{
			
			String[] months = cronExpression.getMonths();
			
			for( LazCheckBox chb : chbMonths )
				chb.setSelected( ArrayUtils.contains( months, chb.getName() ) );
		}
		
		
		//**********************************************************************************************
		//Week days
		token = tokens.nextToken();
		
		if( token.equals( "?" ) )
			chbAllDays.setSelected( true );
		else if( token.equals( "MON-FRI" ) )
			chbAllWeekDays.setSelected( true );
		else if( token.contains( "," ) ){
			
			for( LazCheckBox chb : chbDays )
				chb.setSelected( token.contains( chb.getName() ) );
		}
		
		
		//**********************************************************************************************
		//Year
		token = tokens.nextToken();
		
		if( token.equals( "*" ) ){
			
			rbYearly.setSelected( true );
		}
		else if( SystemUtil.isIntOk( token ) || token.contains( "-" ) ){
			
			String years[] = token.contains( "-" ) ? token.split( "-" ) : new String[]{ token, token };
			
			rbYearlyStartEnd.setSelected( true );
			spnYearlyStart.setValue( years[0] );
			spnYearlyEnd.setValue( years[1] );
		}
		else if( token.contains( "/" ) ){
			
			String years[] = token.split( "/" );

			rbYearlyFrom.setSelected( true );
			spnYearlyFrom.setValue( years[0] );
			spnYearlyStep.setValue( years[1] );
		}
		
		if( cmbNExecutions.getSelectedItem() == null )
			cmbNExecutions.setSelectedIndex(0);
		
		if( cronExpression.getExpression().trim().length() > 0 ){
			
			txfExpression.setText( cronExpression.getExpression() );
			onBuildExpression();
		}
	}
	
	private void onBuildExpression(){
		
		try{
			isSelectionValid();
		}
		catch( BusinessException e ){
			
			LazAlert.showWarning( e.getMessage() );
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		
		Calendar calFixedHour = DateUtils.toCalendar( spnFixedHour.getValue() );
		
		//Segundos
		builder.append( "0" );
		builder.append( " " );
		
		//Minutos
		if( rbFixedHour.isSelected() )
			builder.append( calFixedHour.get( Calendar.MINUTE ) );
		else if( rbEveryEvent.isSelected() && cmbEveryEvent.getSelectedIndex() == 0 )
			builder.append( "0/" + spnEveryEvent.getValue() );
		else
			builder.append( "0" );
		
		builder.append( " " );
		
		//Horas
		if( rbFixedHour.isSelected() )
			builder.append( calFixedHour.get( Calendar.HOUR_OF_DAY ) );
		else if( rbEveryEvent.isSelected() && cmbEveryEvent.getSelectedIndex() == 0 )
			builder.append( ( chbTime.isSelected() ? spnEventStart.getValue() + "-" + spnEventEnd.getValue() : "*" ) );
		else if( rbEveryEvent.isSelected() && cmbEveryEvent.getSelectedIndex() == 1 )
			builder.append( ( chbTime.isSelected() ? spnEventStart.getValue() + "-" + spnEventEnd.getValue() : "*" ) + "/" + spnEveryEvent.getValue() );			
		else
			builder.append( "*" );
		
		builder.append( " " );
		
		//Dia do mes
		if( chbEveryDay.isSelected() )
			builder.append( spnEveryDay.getValue() );
		else
			builder.append( !( chbAllDays.isSelected() || chbEveryDay.isSelected() ) ? "?" : "*" );
		
		builder.append( " " );
		
		//Mes
		if( chbAllMonths.isSelected() )
			builder.append( "*" );
		else{
			
			int size = builder.length();
			
			for( int i=0; i<chbMonths.length; i++ )
				if( chbMonths[i].isSelected() )
					builder.append( ( builder.length() == size ? "" : "," ) + chbMonths[i].getName() );
		}
		
		builder.append( " " );
		
		//Dia da semana
		if( chbAllDays.isSelected() || chbEveryDay.isSelected() )
			builder.append( "?" );
		else if( chbAllWeekDays.isSelected() )
			builder.append( "MON-FRI" );		
		else{
			
			int size = builder.length();
			
			for( int i=0; i<chbDays.length; i++ )
				if( chbDays[i].isSelected() )
					builder.append( ( builder.length() == size ? "" : "," ) + chbDays[i].getName() );
		}
		
		builder.append( " " );
		
		//Ano
		if( rbYearlyStartEnd.isSelected() )
			builder.append( spnYearlyStart.getValue() + ( spnYearlyStart.getValue().compareTo( spnYearlyEnd.getValue() ) != 0 ? "-" + spnYearlyEnd.getValue() : "" ) );
		else if( rbYearlyFrom.isSelected() )
			builder.append( spnYearlyFrom.getValue() + "/" + spnYearlyStep.getValue() );
		else
			builder.append( "*" );
		
		lblBaseDt.setText( DateFormatUtils.format( Calendar.getInstance(), "EEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss" ) );
		txfExpression.setText( "" );
		tableModelExecutions.removeAll();
		
		if( CronExpression.isValidExpression( builder.toString() ) ){
			
			txfExpression.setText( builder.toString() );
			
			List<String> triggers = new ArrayList<String>();
			
			for( Date date : TriggerUtils.computeFireTimes( (OperableTrigger) CronScheduleBuilder.cronSchedule( builder.toString() ).build(), null, cmbNExecutions.getSelectedItem() ) )
				triggers.add( DateFormatUtils.format( date, "EEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss" ) );
			
			tableModelExecutions.setData( triggers );		
		}
	}	
	
	@Override
	public void isFormValid() throws BusinessException{
		
		super.isFormValid();
		
		if( !CronExpression.isValidExpression( txfExpression.getText() ) )
			throw new BusinessException( "Invalid chronological expression" );
	}
	
	@Override 
	protected void saveForm(){

		onBuildExpression();
		
		if( !CronExpression.isValidExpression( txfExpression.getText() ) )
			throw new BusinessException( "Invalid chronological expression" );
			
		try{
			
			LazCronExpression cronExpression = new LazCronExpression( txfExpression.getText() );
		
			dto.setCronExpression( cronExpression.getExpression() );
			dto.setDescription( cronExpression.getDescription() );
			
			dispatchFormEvent( LazFormEvent.SAVE );
		}
		catch( ParseException e ){
			e.printStackTrace();
		}			
	}	
	
	private void refreshStates(){
		
		Calendar cal = Calendar.getInstance();
		int ano = cal.get( Calendar.YEAR );
		
		Date date = SystemUtil.truncDate( cal.getTime() );
		
		//**********************************************************************************************
		//Repetition
		chbEveryDay.setSelectedQuiet( rbFixedHour.isSelected() ? chbEveryDay.isSelected() : false );
		chbTime.setSelectedQuiet( rbEveryEvent.isSelected() ? chbTime.isSelected() : false );
		
		spnFixedHour.setEnabled( rbFixedHour.isSelected() );
		chbEveryDay.setEnabled( rbFixedHour.isSelected() );
		spnEveryDay.setEnabled( chbEveryDay.isSelected() );
		
		spnEveryEvent.setEnabled( rbEveryEvent.isSelected() );
		cmbEveryEvent.setEnabled( rbEveryEvent.isSelected() );
		chbTime.setEnabled( rbEveryEvent.isSelected() );
		spnEventStart.setEnabled( rbEveryEvent.isSelected() && chbTime.isSelected() );
		spnEventEnd.setEnabled( rbEveryEvent.isSelected() && chbTime.isSelected() );
		
		spnFixedHour.setValue( rbFixedHour.isSelected() ? spnFixedHour.getValue() : date );
		spnEveryDay.setValue( chbEveryDay.isSelected() ? spnEveryDay.getValue() : 1 );
		spnEveryEvent.setValue( rbEveryEvent.isSelected() ? spnEveryEvent.getValue() : 1 );
		cmbEveryEvent.setSelectedIndex( rbEveryEvent.isSelected() ? cmbEveryEvent.getSelectedIndex() : 0 );
		spnEventStart.setValue( chbTime.isSelected() ? spnEventStart.getValue() : 0 );
		spnEventEnd.setValue( chbTime.isSelected() ? spnEventEnd.getValue() : 0 );
		
		//**********************************************************************************************
		//Dias
		chbAllDays.setSelectedQuiet( isAllDaysSelected() );
		chbAllWeekDays.setSelectedQuiet( isAllWeekDaysSelected() );
		
		chbAllDays.setEnabled( !chbEveryDay.isSelected() );
		chbAllWeekDays.setEnabled( !chbEveryDay.isSelected() );
		
		for( LazCheckBox chb : chbDays ){
			
			if( chbEveryDay.isSelected() )
				chb.setSelectedQuiet( false );
			
			chb.setEnabled( !chbEveryDay.isSelected() );
		}

		//**********************************************************************************************
		//Meses
		chbAllMonths.setSelectedQuiet( isAllMonthsSelected() );
		
		//**********************************************************************************************
		//Ano
		spnYearlyStart.setValue( rbYearlyStartEnd.isSelected() ? spnYearlyStart.getValue() : ano );
		spnYearlyEnd.setValue( rbYearlyStartEnd.isSelected() ? spnYearlyEnd.getValue() : ano );
		spnYearlyFrom.setValue( rbYearlyFrom.isSelected() ? spnYearlyFrom.getValue() : ano );
		spnYearlyStep.setValue( rbYearlyFrom.isSelected() ? spnYearlyStep.getValue() : 1 );
		
		spnYearlyStart.setEnabled( rbYearlyStartEnd.isSelected() );
		spnYearlyEnd.setEnabled( rbYearlyStartEnd.isSelected() );
		spnYearlyFrom.setEnabled( rbYearlyFrom.isSelected() );
		spnYearlyStep.setEnabled( rbYearlyFrom.isSelected() );
	}
	
	@Override
	public void stateChanged( ChangeEvent event ){
	
		Object source = event.getSource();
		
		if( source == spnYearlyStart && spnYearlyStart.getValue().compareTo( spnYearlyEnd.getValue() ) > 0 )
			spnYearlyEnd.setValue( spnYearlyStart.getValue() );
		else if( source == spnYearlyEnd && spnYearlyEnd.getValue().compareTo( spnYearlyStart.getValue() ) < 0 )
			spnYearlyStart.setValue( spnYearlyEnd.getValue() );
	}

	@Override
	public void actionPerformed( ActionEvent event ){
		
		Object source = event.getSource();
		
		if( source == btnBuildExpression || source == cmbNExecutions )
			onBuildExpression();
		else if( source == btnUseExpression )
			save();
		else if( source == chbAllMonths )
			onAllMonthsClicked();
		else if( source == chbAllDays ){
			
			for( int i=0; i<chbDays.length; i++ )
				chbDays[i].setSelectedQuiet( chbAllDays.isSelected() );
		}
		else if( source == chbAllWeekDays ){
			
			for( int i=0; i<chbDays.length; i++ )
				chbDays[i].setSelectedQuiet( chbAllWeekDays.isSelected() && i <= 4 );
		}
		
		refreshStates();
	}
}