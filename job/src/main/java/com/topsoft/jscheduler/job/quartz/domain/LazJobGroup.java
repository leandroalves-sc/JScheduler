package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.quartz.JobDetail;

import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;

public class LazJobGroup extends LazJobDetail{

	private static final long serialVersionUID = 6155013214138214961L;
	
	private List<LazJobDetail> jobs;

	public LazJobGroup(){
		super();
		
		this.jobs = new ArrayList<LazJobDetail>();
	}
	
	@Override
	public List<LazJobDetail> getJobs(){
		return jobs;
	}
	
	public void setJobs( List<LazJobDetail> jobs ){
		
		this.jobs = jobs;
		Collections.sort( jobs );
	}

	public void addJob( LazJobDetail job ){
		
		job.setJobGroup( this );
		this.jobs.add( job );
		Collections.sort( jobs );
	}
	
	public static LazJobGroup newInstance( JobDetail job ){

		LazJobGroup lazGroup = null;
		
		if( job != null ){
			
			try{
		
				BeanUtilsBean.getInstance().getConvertUtils().register( false, true, -1 );
				BeanUtils.copyProperties( lazGroup = new LazJobGroup(), job );
			}
			catch( Exception e ){
				return null;
			}
		}
		
		return lazGroup;
    }
	
	@Override
	public LazTriggerState getState(){
		
		if( isDefaultGroup() )
			return null;

		return super.getState();
	}
	
	public boolean isAllJobsPaused(){
		
		if( jobs == null || jobs.isEmpty() )
			return false;
		
		for( LazJobDetail job : jobs )
			if( job.getState() != LazTriggerState.PAUSED )
				return false;
		
		return true;
	}
	
	@Override
	public Set<LazJobParam> getJobParams(){
		
		Set<LazJobParam> params = new LinkedHashSet<LazJobParam>();
		
		for( LazJobDetail job : getJobs() ) 
			params.addAll( job.getJobParams() );
	
		return params;
	}
	
	@Override
	public int compareTo( LazJobDetail job ){
		
		if( getKey() == null )
			return 0;
		
		if( isDefaultGroup() )
			return -1;		
		
		if( job != null && job.getKey() != null )
			return getKey().getGroup().compareTo( job.getKey().getGroup() );

		return 0;
	}
}