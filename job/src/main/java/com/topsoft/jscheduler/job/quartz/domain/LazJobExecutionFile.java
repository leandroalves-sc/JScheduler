package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.impl.FileCopyJob;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class LazJobExecutionFile implements LazJobExecutionConfig{

	private String sourcePath;
	private String destinationPath;
	private boolean recursive, deleteAfterCopy;
	
	public LazJobExecutionFile(){
		
		sourcePath = "C:\\";
		destinationPath = "C:\\";
		recursive = false;
		deleteAfterCopy = false;
	}
	
	public String getSourcePath(){
		return sourcePath;
	}
	
	public void setSourcePath( String sourcePath ){
		this.sourcePath = sourcePath;
	}
	
	public String getDestinationPath(){
		return destinationPath;
	}
	
	public void setDestinationPath( String destinationPath ){
		this.destinationPath = destinationPath;
	}
	
	public boolean isRecursive(){
		return recursive;
	}
	
	public void setRecursive( boolean recursive ){
		this.recursive = recursive;
	}
	
	public boolean isDeleteAfterCopy(){
		return deleteAfterCopy;
	}
	
	public void setDeleteAfterCopy( boolean deleteAfterCopy ){
		this.deleteAfterCopy = deleteAfterCopy;
	}

	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams(){
		return new ArrayList<LazJobParam>();
	}
	
	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass(){
		return FileCopyJob.class;
	}
}