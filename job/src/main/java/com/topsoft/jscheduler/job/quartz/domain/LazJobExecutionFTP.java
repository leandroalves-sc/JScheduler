package com.topsoft.jscheduler.job.quartz.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.impl.FTPCopyJob;
import com.topsoft.topframework.ftp.domain.FTPConfiguration;

public class LazJobExecutionFTP implements LazJobExecutionConfig {

	private FTPConfiguration ftpConfig;
	private String sourcePath;
	private String destinationPath;
	private boolean recursive, deleteAfterCopy;

	public LazJobExecutionFTP() {

		super();

		ftpConfig = new FTPConfiguration();
		sourcePath = "C:\\";
		destinationPath = "FTP:\\";
		recursive = false;
		deleteAfterCopy = false;
	}

	public FTPConfiguration getFtpConfig() {
		return ftpConfig;
	}

	public void setFtpConfig(FTPConfiguration ftpConfig) {
		this.ftpConfig = ftpConfig;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public boolean isDeleteAfterCopy() {
		return deleteAfterCopy;
	}

	public void setDeleteAfterCopy(boolean deleteAfterCopy) {
		this.deleteAfterCopy = deleteAfterCopy;
	}

	@Override
	@JsonIgnore
	public List<LazJobParam> getJobParams() {
		return new ArrayList<LazJobParam>();
	}

	@Override
	@JsonIgnore
	public Class<? extends LazJob> getJobClass() {
		return FTPCopyJob.class;
	}
}