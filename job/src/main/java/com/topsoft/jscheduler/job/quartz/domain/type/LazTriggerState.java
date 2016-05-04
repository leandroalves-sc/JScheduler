package com.topsoft.jscheduler.job.quartz.domain.type;

import javax.swing.Icon;

import com.topsoft.topframework.base.util.LazImage;

public enum LazTriggerState { 
	
	NONE( "Unknown" ),
	NORMAL( "Normal" ), 
	PAUSED( "Paused" ), 
	COMPLETE( "Completed" ), 
	ERROR( "Error" ), 
	SUSPENDED( "Suspended execution" ),
	WAITING( "Scheduled" ),
	RUNNING( "Running" ),
	BLOCKED( "Blocked" );
	
	private String description;
	
	LazTriggerState( String description ){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return description;
	}
	
	public Icon getIcon(){
		
		if( this == SUSPENDED )
			return LazImage.YELLOW.getIcon();
		else if( this == PAUSED || this == ERROR )
			return LazImage.RED.getIcon();
		else if( this == RUNNING )
			return LazImage.RUNNING.getIcon();
			
		return LazImage.GREEN.getIcon();
	}
}	