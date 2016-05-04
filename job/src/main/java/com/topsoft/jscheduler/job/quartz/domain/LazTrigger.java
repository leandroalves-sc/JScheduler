package com.topsoft.jscheduler.job.quartz.domain;

import javax.swing.Icon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;

public class LazTrigger extends CronTriggerImpl{

	private static final long serialVersionUID = 3394238803829930069L;
	
	private String reason;
	
	public LazTrigger(){
		
		super();
		setMisfireInstruction( MISFIRE_INSTRUCTION_DO_NOTHING );
	}
	
	public String getReason(){
		return reason;
	}
	
	public void setReason( String reason ){
		this.reason = reason;
	}

	public LazTriggerState getState(){
		
		LazTriggerState state = LazTriggerState.NONE;
		
		if( getJobDataMap().containsKey( "state" ) ){
			
			Object objState = getJobDataMap().get( "state" );
			
			if( LazTriggerState.class.isAssignableFrom( objState.getClass() ) )
				state = (LazTriggerState) objState;
			else
				state = LazTriggerState.valueOf( getJobDataMap().get( "state" ).toString() );
		}
		
		return state;
	}
	
	public Icon getStateIcon(){
		return getState().getIcon();
	}
	
	public String getStateTooltip(){
		
		if( getState() == LazTriggerState.SUSPENDED )
			return getReason();
		
		return getState().toString();
	}
	
	public void setState( LazTriggerState state ){
		getJobDataMap().put( "state", state );
	}
	
	public static LazTrigger newInstance( Trigger trigger ){

		LazTrigger lazTrigger = null;
		
		if( trigger != null ){
			
			try{
		
				BeanUtilsBean.getInstance().getConvertUtils().register( false, true, -1 );
				BeanUtils.copyProperties( lazTrigger = new LazTrigger(), trigger );
			}
			catch( Exception e ){
				return null;
			}
		}
		
		return lazTrigger;
    }
	
	@Override
	public int compareTo( Trigger trigger ){
		
		if( getNextFireTime() != null && trigger != null && trigger.getNextFireTime() != null )
			if( LazTrigger.class.isAssignableFrom( trigger.getClass() ) )
				return getNextFireTime().compareTo( trigger.getNextFireTime() );

		return super.compareTo( trigger );
	}	
	
	public String getFullFormattedNextFireTime(){
		return DateFormatUtils.format( getNextFireTime(), "EEE, dd 'de' MMMM 'de' yyyy ' at ' HH:mm:ss" );
	}
	
	public String getFormattedNextFireTime(){
		return DateFormatUtils.format( getNextFireTime(), "dd/MM/yyyy HH:mm:ss" );
	}
	
	public String getFormattedPreviousFireTime(){
		return getPreviousFireTime() == null ? "" : DateFormatUtils.format( getPreviousFireTime(), "dd/MM/yyyy HH:mm:ss" );
	}	
}