package com.topsoft.jscheduler.job.quartz.bo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.quartz.JobKey;
import org.quartz.TriggerUtils;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;
import com.topsoft.jscheduler.job.quartz.bo.LazJobExecutionBO;
import com.topsoft.jscheduler.job.quartz.dao.LazJobExecutionBaseDAO;
import com.topsoft.jscheduler.job.quartz.dao.LazVetoTriggerBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;
import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;

@Service
@Transactional(rollbackFor = Throwable.class)
public class LazJobExecutionBOImpl implements LazJobExecutionBO{
	
	@Autowired
	private LazJobExecutionBaseDAO dao;
	
	@Autowired 
	private LazVetoTriggerBaseDAO vetoDAO;
	
	@Override
	public LazJobExecution insert( LazJobExecution dto ){
		return dao.insert( dto );
	}
	
	@Override
	public List<LazTrigger> findAllNextJobExecutions( LazJobDetail job, int qtde ){
		
		List<LazTrigger> triggers = getNextJobExecutions( job, qtde );
		
		for( LazTrigger trigger : triggers ){
			
			List<LazVetoTrigger> vetos = vetoDAO.findVetoExecutions( trigger.getKey() );
		
			for( LazVetoTrigger veto : vetos ){
				
				if( veto.getVetoTime() == trigger.getNextFireTime().getTime() ){
					
					trigger.setState( LazTriggerState.SUSPENDED );
					trigger.setReason( veto.getReason() );
					
					break;
				}
			}
		}
		
		return triggers;
	}	

	private List<LazTrigger> getNextJobExecutions( LazJobDetail job, int qtde ){
		
		List<LazTrigger> triggers = new ArrayList<LazTrigger>();
		
		for( LazTrigger trigger : job.getTriggers() ){
			
			for( Date date : TriggerUtils.computeFireTimes( (OperableTrigger) trigger, null, qtde ) ){

				LazTrigger bean = new LazTrigger();
				bean.setKey( trigger.getKey() );
				bean.setState( trigger.getState() );
				bean.setNextFireTime( date );
				triggers.add( bean );
			}
		}
		
		Collections.sort( triggers );
		
		if( triggers.size() > qtde )
			return triggers.subList( 0, qtde ); 
		
		return triggers;
	}	
	
	@Override
	public List<LazJobExecution> findAllLastJobExecutions( LazJobDetail job, int qtde ){
		return dao.findAllLastJobExecutions( job, qtde );
	}
	
	@Override
	public DataPage<LazJobExecution> findPageLastJobExecutions( LazJobDetail job, Page page ){
		return dao.findPageLastJobExecutions( job, page );
	}

	@Override
	public void deleteAllHistory( JobKey key ){
		dao.deleteAllHistory( key );
	}
}