package com.topsoft.jscheduler.job.quartz.job.impl;

import java.io.File;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionFileRun;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.topframework.base.exception.BusinessException;

@Service
public class FileRunJob extends LazJob {

	private static Logger log = LoggerFactory.getLogger(FileRunJob.class);

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		if (LazJobExecutionFileRun.class.isAssignableFrom(job.getJobConfig().getExecutionConfig().getClass())) {

			LazJobExecutionFileRun config = (LazJobExecutionFileRun) job.getJobConfig().getExecutionConfig();
			String comando = "cmd /c start " + config.getFilePath() + " " + config.getArgs();

			File file = new File(config.getFilePath());

			if (file.exists()) {

				log.info("Running command: " + comando);

				try {

					Runtime.getRuntime().exec(comando, null, file.getParentFile());
				}
				catch (Exception e) {
					throw new BusinessException("Error while executing command: " + comando, e);
				}
			}
			else {

				throw new BusinessException("File " + config
					.getFilePath() + " does not found. Error while executing commmand: " + comando);
			}
		}
	}

	@Override
	public List<LazJobParam> getJobParams() {
		return null;
	}
}