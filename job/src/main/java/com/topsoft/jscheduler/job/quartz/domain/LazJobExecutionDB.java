package com.topsoft.jscheduler.job.quartz.domain;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.impl.DatabaseJob;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LazJobExecutionDB implements LazJobExecutionConfig{

	private Database database;
	private String owner, object;
	
	public Database getDatabase(){
		return database;
	}
	
	public void setDatabase( Database database ){
		this.database = database;
	}

	public String getOwner(){
		return owner;
	}

	public void setOwner( String owner ){
		this.owner = owner;
	}
	
	public String getObject(){
		return object;
	}
	
	public void setObject( String object ){
		this.object = object;
	}

	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams(){

		List<LazJobParam> params = new ArrayList<LazJobParam>();
		
		Connection connection = null;
		
		try{
			
			connection = getDatabase().getConnection();
			DatabaseMetaData metadata = connection.getMetaData();
			ResultSet rs = metadata.getProcedureColumns( null, getOwner(), getObject(), null );
			
			while( rs.next() ){
				
				int columnType = rs.getInt( "COLUMN_TYPE" );
				
				if( columnType == DatabaseMetaData.procedureColumnIn || columnType == DatabaseMetaData.procedureColumnInOut ){
				
					LazJobParam param = new LazJobParam();
					param.setKey( rs.getString( "COLUMN_NAME" ) );
					param.setDescription( param.getKey() );
					param.setTypeBySql( rs.getInt( "DATA_TYPE" ) );
					
					params.add( param );
				}
			}
			
			return params;
		}
		catch( Exception e ){
			return params;
		}
		finally{
			
			try{
				
				if( connection != null && !connection.isClosed() )
					connection.close();
			}
			catch( SQLException e ){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass(){
		return DatabaseJob.class;
	}
}