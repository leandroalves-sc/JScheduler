package com.topsoft.jscheduler.job.quartz.domain.type;

public enum QuartzPeriod{

	START( "Beginning" ),
	END( "Ending" ),
	START_END( "Begining/Ending" );
	
	private String description;
	
	QuartzPeriod( String description ){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return description;
	}
}