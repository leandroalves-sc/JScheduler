package com.topsoft.jscheduler.job.quartz.domain.type;

public enum QuartzLevel{

	GROUP( "Per group" ),
	SERVICE( "Per service" );
	
	private String description;
	
	QuartzLevel( String description ){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return description;
	}
}