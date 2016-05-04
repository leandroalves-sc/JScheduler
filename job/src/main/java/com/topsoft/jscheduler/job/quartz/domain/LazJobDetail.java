package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobDetail;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.utils.Key;

import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzJobType;
import com.topsoft.jscheduler.job.quartz.job.impl.JobInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LazJobDetail extends JobDetailImpl implements Comparable<LazJobDetail>{

	private static final long serialVersionUID = -593244972032828511L;
	
	private List<LazTrigger> triggers;
	private LazJobConfig config;
	private LazJobGroup jobGroup;

	public LazJobDetail(){
		
		super();
		
		this.setJobClass( JobInterceptor.class );
		this.setDurability( true );
		this.triggers = new ArrayList<LazTrigger>();
	}
	
	public LazJobConfig getJobConfig(){
		
		if( config == null ){
			
			Object json = getJobDataMap().get( "config" );
			
			if( json == null )
				config = new LazJobConfig();
			else{

				try{
					
					ObjectMapper mapper = new ObjectMapper();
					mapper.disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
					config = mapper.readValue( json.toString(), LazJobConfig.class );
				}
				catch( Exception e ){
					e.printStackTrace();
					return null;
				}
			}
		}

		return config;
	}
	
	public Set<LazJobParam> getJobParams(){
		
		Set<LazJobParam> params = new LinkedHashSet<LazJobParam>();
		
		LazJobExecutionConfig config = getJobConfig().getExecutionConfig();
		
		if( config != null )
			params.addAll( config.getJobParams() );
		
		return params;
	}
	
	public void setJobConfig( LazJobConfig jobConfig ){
		
		try{
			
			ObjectMapper mapper = new ObjectMapper();
			getJobDataMap().put( "config", mapper.writeValueAsString( jobConfig ) );
		}
		catch( Exception e ){}
	}
	
	public List<LazTrigger> getTriggers(){
		return triggers;
	}
	
	public LazTrigger getTrigger( TriggerKey key ){
		
		for( LazTrigger trigger : triggers )
			if( trigger.getKey().compareTo( key ) == 0 )
				return trigger;
		
		return null;
	}
	
	public void setTriggers( List<LazTrigger> triggers ){
		this.triggers = triggers;
		Collections.sort( triggers );
	}
	
	public LazJobGroup getJobGroup(){
		return jobGroup;
	}
	
	public void setJobGroup( LazJobGroup jobGroup ){
		this.jobGroup = jobGroup;
	}

	public boolean isGroup(){
		return getJobConfig().getJobType() == QuartzJobType.GROUP;
	}
	
	public boolean isDefaultGroup(){
		return Key.DEFAULT_GROUP.equals( getGroup() );
	}
	
	public boolean isJobOfGroup(){
		return getJobConfig().getJobType() == QuartzJobType.JOB_OF_GROUP;
	}
	
	public boolean isStandAloneJob(){
		return getJobConfig().getJobType() == QuartzJobType.STAND_ALONE;
	}	
	
	public LazTriggerState getState(){
		
		if( isAllTriggersPaused() )
			return LazTriggerState.PAUSED;
		else if( isJobOfGroup() && getJobGroup() != null && getJobGroup().getState() == LazTriggerState.PAUSED )
			return LazTriggerState.PAUSED;
		else if( getJobDataMap().containsKey( "state" ) ){
			
			Object objState = getJobDataMap().get( "state" );
			
			if( LazTriggerState.class.isAssignableFrom( objState.getClass() ) )
				return (LazTriggerState) objState;
			else
				return LazTriggerState.valueOf( getJobDataMap().get( "state" ).toString() );
		}
		else{
		
			LazTrigger trigger = getNextTrigger();
			
			if( trigger != null )
				return trigger.getState();
		}
		
		return LazTriggerState.WAITING;
	}
	
	public void setState( LazTriggerState state ){
		
		if( state == null )
			getJobDataMap().remove( "state" );
		else
			getJobDataMap().put( "state", state );
	}
	
	public Icon getStateIcon(){
		return isGroup() && isDefaultGroup() ? null : getState().getIcon();
	}
	
	public String getStateTooltip(){
		return isGroup() && isDefaultGroup() ? null : getState().toString();
	}	
	
	public LazTrigger getNextTrigger(){
		
		if( triggers != null && !triggers.isEmpty() )
			return triggers.get( 0 );
		
		return null;
	}
	
	private Date getPreviousFireTime(){
		
		Date previousFireTime = null;
		
		if( triggers != null && !triggers.isEmpty() ){
		
			List<Date> dates = new ArrayList<Date>();
			
			for( LazTrigger trigger : triggers )
				if( trigger.getPreviousFireTime() != null )
					dates.add( trigger.getPreviousFireTime() );
			
			Collections.sort( dates );
			
			if( !dates.isEmpty() )
				previousFireTime = dates.get( dates.size() - 1 );
		}
		
		return previousFireTime;
	}
	
	private Date getNextFireTime(){
		
		Date nextFireTime = null;
		
		if( triggers != null && !triggers.isEmpty() ){
		
			List<Date> dates = new ArrayList<Date>();
			
			for( LazTrigger trigger : triggers )
				if( trigger.getNextFireTime() != null )
					dates.add( trigger.getNextFireTime() );
			
			Collections.sort( dates );
			
			if( !dates.isEmpty() )
				nextFireTime = dates.get(0);
		}
		
		return nextFireTime;
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
	
	private boolean isAllTriggersPaused(){
		
		if( triggers == null || triggers.isEmpty() )
			return false;
		
		for( LazTrigger trigger : triggers )
			if( trigger.getState() != LazTriggerState.PAUSED )
				return false;
		
		return true;
	}
	
	public String getNameCompleted(){
		
		if( isGroup() )
			return getGroup();
		else
			return ( isDefaultGroup() ? "" : getGroup() + "." ) + getName();
	}
	
	public List<LazJobMonitor> getMonitors(){
		
		Set<LazJobMonitor> monitors = new LinkedHashSet<LazJobMonitor>();
		
		monitors.addAll( getJobConfig().getMonitors() );
		
		if( isJobOfGroup() && jobGroup != null )
			monitors.addAll( jobGroup.getMonitors() );
		
		return new ArrayList<LazJobMonitor>( monitors );
	}

	@Override
	public int compareTo( LazJobDetail job ){
		
		if( job != null ){
			
			if( isJobOfGroup() && job.isJobOfGroup() )
				return getJobConfig().getJobSequence().compareTo( job.getJobConfig().getJobSequence() );
			else
				return getKey().compareTo( job.getKey() );
		}

		return 0;
	}
	
	@Override
	public boolean equals( Object obj ){
		
		if( obj != null && LazJobDetail.class.isAssignableFrom( obj.getClass() ) ){
			
			LazJobDetail job = (LazJobDetail) obj;
			
			if( getKey() != null )
				return getKey().equals( job.getKey() );
		}

		return false;
	}
	
	public static LazJobDetail newInstance( JobDetail job ){

		LazJobDetail lazJob = null;
		
		if( job != null ){
			
			try{
		
				BeanUtilsBean.getInstance().getConvertUtils().register( false, true, -1 );
				BeanUtils.copyProperties( lazJob = new LazJobDetail(), job );
			}
			catch( Exception e ){
				return null;
			}
		}
		
		return lazJob;
    }
	
	public List<LazJobDetail> getJobs(){
		return Arrays.asList( this );
	}	
	
	@Override
	public String toString(){
		return getKey() == null ? "" : getKey().getName();
	}
}