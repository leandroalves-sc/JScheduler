package com.topsoft.jscheduler.job.quartz.domain;

import com.topsoft.jscheduler.job.quartz.domain.type.QuartzLevel;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzPeriod;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobMonitor{

	private QuartzUser user;
	private boolean smsMonitor, emailMonitor, errorMonitor;
	private QuartzLevel smsLevel, emailLevel;
	private QuartzPeriod smsPeriod, emailPeriod;
	
	public LazJobMonitor(){}
	
	public QuartzUser getUser(){
		return user;
	}
	
	public void setUser( QuartzUser user ){
		this.user = user;
	}

	public boolean isSmsMonitor(){
		return smsMonitor;
	}
	
	public void setSmsMonitor( boolean smsMonitor ){
		this.smsMonitor = smsMonitor;
	}
	
	public boolean isEmailMonitor(){
		return emailMonitor;
	}
	
	public void setEmailMonitor( boolean emailMonitor ){
		this.emailMonitor = emailMonitor;
	}
	
	public boolean isErrorMonitor(){
		return errorMonitor;
	}
	
	public void setErrorMonitor( boolean errorMonitor ){
		this.errorMonitor = errorMonitor;
	}

	public QuartzLevel getSmsLevel(){
		return smsLevel;
	}
	
	public void setSmsLevel( QuartzLevel smsLevel ){
		this.smsLevel = smsLevel;
	}

	public QuartzLevel getEmailLevel(){
		return emailLevel;
	}
	
	public void setEmailLevel( QuartzLevel emailLevel ){
		this.emailLevel = emailLevel;
	}
	
	public QuartzPeriod getSmsPeriod(){
		return smsPeriod;
	}
	
	public void setSmsPeriod( QuartzPeriod smsPeriod ){
		this.smsPeriod = smsPeriod;
	}

	public QuartzPeriod getEmailPeriod(){
		return emailPeriod;
	}
	
	public void setEmailPeriod( QuartzPeriod emailPeriod ){
		this.emailPeriod = emailPeriod;
	}
	
	@JsonIgnore
	public String getMonitoring(){
		
		StringBuilder str = new StringBuilder();
		
		if( isErrorMonitor() )
			str.append( "Erros: Always" );
		
		if( isSmsMonitor() ){
			
			str.append( ( str.length() == 0 ? "" : ", " ) + "SMS: " );
			str.append( smsPeriod.toString() );
			
			if( smsLevel != null )
				str.append( smsLevel == QuartzLevel.GROUP ? " group execution" : " on every job service" );
		}
		
		if( isEmailMonitor() ){
			
			str.append( ( str.length() == 0 ? "" : ", " ) + "Email: " );
			str.append( emailPeriod.toString() );
			
			if( emailLevel != null )
				str.append( emailLevel == QuartzLevel.GROUP ? " group execution" : " on every job service" );
		}
		
		return str.toString();
	}
	
	@Override
	public boolean equals( Object monitor ){
	
		if( user == null || monitor == null || !LazJobMonitor.class.isAssignableFrom( monitor.getClass() ) )
			return false;
		
		if( ( (LazJobMonitor) monitor ).getUser() == null )
			return false;
		
		return getUser().equals( ( (LazJobMonitor) monitor ).getUser() );
	}
}