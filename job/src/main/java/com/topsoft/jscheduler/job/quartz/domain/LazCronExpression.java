package com.topsoft.jscheduler.job.quartz.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.redhogs.cronparser.CronExpressionDescriptor;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerUtils;
import org.quartz.spi.OperableTrigger;

import com.topsoft.topframework.base.util.Constants;
import com.topsoft.topframework.base.util.SystemUtil;

public class LazCronExpression{
	
	public static final int SECOND       = 0;
	public static final int MINUTE       = 1;
	public static final int HOUR         = 2;
	public static final int DAY_OF_MONTH = 3;
	public static final int MONTH        = 4;
	public static final int DAY_OF_WEEK  = 5;
	public static final int YEAR         = 6;
	
	private static final String DEFAULT_EXPRESSION = "0 0 0 * * ? *";

	private String[] tokens;
	private String expression;
	private Date baseTime;

	public LazCronExpression(){
		this( DEFAULT_EXPRESSION );
	}
	
	public LazCronExpression( CronTrigger cronTrigger ){
		this( cronTrigger.getCronExpression() == null ? DEFAULT_EXPRESSION : cronTrigger.getCronExpression() );
	}
	
	public LazCronExpression( Date date ){
		
		Calendar cal = DateUtils.toCalendar( date );
		
		StringBuilder cronExpression = new StringBuilder();
		cronExpression.append( "0" );
		cronExpression.append( " " + cal.get( Calendar.MINUTE ) );
		cronExpression.append( " " + cal.get( Calendar.HOUR_OF_DAY ) );
		cronExpression.append( " " + cal.get( Calendar.DAY_OF_MONTH ) );
		cronExpression.append( " " + Constants.monthNamesEN[cal.get( Calendar.MONTH )].substring( 0, 3 ).toUpperCase() );
		cronExpression.append( " ?" );
		cronExpression.append( " " + cal.get( Calendar.YEAR ) );
		
		this.setExpression( cronExpression.toString() );
		this.baseTime = SystemUtil.truncDate( Calendar.getInstance().getTime() );
	}
	
	public LazCronExpression( String expression ){
		
		this.setExpression( expression );
		this.baseTime = SystemUtil.truncDate( Calendar.getInstance().getTime() );
	}
	
	public String getExpression(){
		return expression == null || expression.trim().length() == 0 ? DEFAULT_EXPRESSION : expression;
	}
	
	public void setExpression( String expression ){
		this.expression = expression;
		this.tokens = expression.split( " " );
	}
	
	public String getDescription(){

		try{
			
			return CronExpressionDescriptor.getDescription( getExpression(), Locale.getDefault() );
		}
		catch( ParseException e ){
			return "Error while translating expression";
		}
	}
	
	public List<Date> computeFireTimes( int qtde ){
		
		if( CronExpression.isValidExpression( getExpression() ) )
			return TriggerUtils.computeFireTimes( (OperableTrigger) CronScheduleBuilder.cronSchedule( getExpression() ).build(), null, qtde );
		
		return new ArrayList<Date>();
	}
	
	public boolean isFixedHour(){
		return !tokens[MINUTE].contains( "/" ) && !tokens[HOUR].contains( "/" );
	}
	
	public boolean isEveryEvent(){
		return !isFixedHour();
	}
	
	public boolean isEveryFixedDay(){
		return !tokens[DAY_OF_MONTH].equals( "*" ) && !tokens[DAY_OF_MONTH].equals( "?" );
	}
	
	public Date getFixedHour(){
		
		if( isFixedHour() ){
		
			Calendar cal = Calendar.getInstance();
			cal.set( Calendar.HOUR_OF_DAY, Integer.parseInt( tokens[HOUR] ) );
			cal.set( Calendar.MINUTE, Integer.parseInt( tokens[MINUTE] ) );
			cal.set( Calendar.SECOND, Integer.parseInt( tokens[SECOND] ) );
			
			return cal.getTime();
		}
		
		return baseTime;
	}
	
	public String[] getTokens(){
		return tokens;
	}
	
	public boolean hasStartEndTime(){
		return tokens[HOUR].contains( "-" );
	}
	
	public int getStartTime(){
		return hasStartEndTime() ? Integer.parseInt( tokens[HOUR].split( "-" )[0] ) : 0;
	}
	
	public int getEndTime(){
		
		if( hasStartEndTime() ){
			
			String endTime = tokens[HOUR].split( "-" )[1];
			return Integer.parseInt( endTime.contains( "/" ) ? endTime.split( "/" )[0] : endTime );
		}
		
		return 0;
	}	
	
	public int getEveryEvent(){
		
		if( isEveryEvent() ){
		
			if( tokens[MINUTE].contains( "/" ) )
				return Integer.parseInt( tokens[MINUTE].split( "/" )[1]) ;
			else if( tokens[HOUR].contains( "/" ) )
				return Integer.parseInt( tokens[HOUR].split( "/" )[1] );
		}
		
		return 1;
	}
	
	public int getEveryDay(){
		return isEveryFixedDay() ? Integer.parseInt( tokens[DAY_OF_MONTH] ) : 1;
	}
	
	public boolean isEveryMonth(){
		return tokens[MONTH].equals( "*" );
	}
	
	public String[] getMonths(){
		return tokens[MONTH].contains( "," ) ? tokens[MONTH].split( "," ) : tokens[MONTH].length() != 0 ? new String[]{ tokens[MONTH] } : null;
	}
	
	public boolean isEveryHour(){
		return tokens[HOUR].contains( "/" );
	}
	
	@Override
	public String toString(){
		return getDescription();		
	}
}