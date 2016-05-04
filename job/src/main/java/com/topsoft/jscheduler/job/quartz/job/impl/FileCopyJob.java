package com.topsoft.jscheduler.job.quartz.job.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionFile;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.topframework.base.exception.BusinessException;

@Service
public class FileCopyJob extends LazJob {

	private static Logger log = LoggerFactory.getLogger(FileCopyJob.class);

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		if (LazJobExecutionFile.class.isAssignableFrom(job.getJobConfig().getExecutionConfig().getClass())) {

			LazJobExecutionFile config = (LazJobExecutionFile) job.getJobConfig().getExecutionConfig();

			log.info("Local Folder: " + config.getSourcePath());
			log.info("Remote Folder: " + config.getDestinationPath());
			log.info("Recursive: " + config.isRecursive());
			log.info("Delete after copy: " + config.isDeleteAfterCopy());

			File localFile = new File(config.getSourcePath());

			if (!localFile.exists())
				throw new BusinessException("Local directory (" + config.getSourcePath() + ") does not exist.");

			copyFolder(config.getSourcePath(), config.getDestinationPath(), config.isRecursive(), config
				.isDeleteAfterCopy());
		}
	}

	private void copyFolder(String fromPath, String toPath, boolean recursive, boolean deleteAfterCopy) throws BusinessException {

		File toFile;
		File fromFile = new File(fromPath);

		for (File file : fromFile.listFiles()) {

			if (file.isDirectory() && recursive) {

				copyFolder(file.getPath(), toPath + file.getPath().replace(fromPath, ""), recursive, deleteAfterCopy);
			}
			else if (file.isFile()) {

				try {

					toFile = new File(toPath + file.getPath().replace(fromPath, ""));

					File toFolder = new File(toFile.getParent());

					if (!toFolder.exists())
						toFolder.mkdirs();

					log.info("Copying FROM: " + file + " - TO: " + toFile);
					FileUtils.copyFile(file, toFile);

					if (deleteAfterCopy)
						file.delete();
				}
				catch (Exception e) {
					throw new BusinessException("Error while copying files", e);
				}
			}
		}
	}

	@Override
	public List<LazJobParam> getJobParams() {
		return null;
	}
}