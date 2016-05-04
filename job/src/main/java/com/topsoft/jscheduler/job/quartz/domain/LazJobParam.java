package com.topsoft.jscheduler.job.quartz.domain;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobParam{
	
	public enum JobParamType{
		
		STRING,
		INTEGER,
		DOUBLE,
		FILE,
		DATE;
	}
	
	public LazJobParam(){}
	
	public LazJobParam( String key, String description, JobParamType type ){
		
		this.key = key;
		this.description = description;
		this.type = type;
	}

	private String key;
	private String description;
	private Object value;
	private JobParamType type;
	
	public String getKey(){
		return key;
	}
	
	public void setKey( String key ){
		this.key = key;
	}
	
	
	public void setTypeBySql( int sqlType ){
		
		if( sqlType == Types.DECIMAL )
			type = JobParamType.DOUBLE;
		else if( sqlType == Types.DATE || sqlType == Types.TIMESTAMP )
			type = JobParamType.DATE;
		else
			type = JobParamType.STRING;
	}

	public String getDescription(){
		return description;
	}
	
	public void setDescription( String description ){
		this.description = description;
	}
	
	@JsonIgnore
	public Object getValue(){
		return value;
	}
	
	public void setValue( Object value ){
		this.value = value;
	}
	
	public JobParamType getType(){
		return type;
	}
	
	public void setType( JobParamType type ){
		this.type = type;
	}

	@JsonIgnore
	public String getFormattedValue(){
		
		if( value != null ){
		
			if( value instanceof Date )
				return new SimpleDateFormat( "dd/MM/yyyy" ).format( (Date) value );
			else
				return value.toString();
		}
		
		return "";
	}
	
	@Override
	public boolean equals( Object param ){
		
		if( param == null || getKey() == null || !LazJobParam.class.isAssignableFrom( param.getClass() ) )
			return false;
		
		return getKey().equals( ( (LazJobParam) param ).getKey() );
	}

	@Override
	public int hashCode(){
		return new HashCodeBuilder( 17, 31 ).append( key ).append( type ).toHashCode();
	}
}