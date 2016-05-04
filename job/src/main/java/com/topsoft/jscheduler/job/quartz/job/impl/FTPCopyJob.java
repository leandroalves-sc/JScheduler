package com.topsoft.jscheduler.job.quartz.job.impl;

import java.io.File;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionFTP;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.ftp.FTPClient;
import com.topsoft.topframework.ftp.domain.FTPConfiguration;

@Service
public class FTPCopyJob extends LazJob {

	private static Logger log = LoggerFactory.getLogger(FTPCopyJob.class);

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		if (LazJobExecutionFTP.class.isAssignableFrom(job.getJobConfig().getExecutionConfig().getClass())) {

			LazJobExecutionFTP config = (LazJobExecutionFTP) job.getJobConfig().getExecutionConfig();
			FTPConfiguration ftp = config.getFtpConfig();

			log.info("Server: " + ftp.getPrintBaseURL());
			log.info("Local Folder: " + config.getSourcePath());
			log.info("Remote Folder: " + config.getDestinationPath());
			log.info("Recursive: " + config.isRecursive());
			log.info("Delete after copy: " + config.isDeleteAfterCopy());

			File localFile = new File(config.getSourcePath());

			if (!localFile.exists())
				throw new BusinessException("Local directory (" + config.getSourcePath() + ") does not exist.");

			FTPClient client = new FTPClient();
			client.transfer(config.getFtpConfig(), config.getSourcePath(), config.getDestinationPath(), config
				.isRecursive(), config.isDeleteAfterCopy());
		}
	}

	@Override
	public List<LazJobParam> getJobParams() {
		return null;
	}
}