package com.topsoft.jscheduler.job.quartz.job.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alee.utils.ThreadUtils;
import com.topsoft.jscheduler.job.quartz.bo.DatabaseBaseBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionDB;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.topframework.base.exception.BusinessException;

@Service
public class DatabaseJob extends LazJob {

	private static Logger log = LoggerFactory.getLogger(DatabaseJob.class);

	@Autowired
	private DatabaseBaseBO databaseBO;

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		if (LazJobExecutionDB.class.isAssignableFrom(job.getJobConfig().getExecutionConfig().getClass())) {

			LazJobExecutionDB config = (LazJobExecutionDB) job.getJobConfig().getExecutionConfig();

			log.info("Database: " + config.getDatabase());
			log.info("Owner: " + config.getOwner());
			log.info("Object: " + config.getObject());

			try {

				ThreadUtils.sleepSafely(5000);

				String console = databaseBO.callObject(config, context.getMergedJobDataMap());

				if (!StringUtils.isBlank(console))
					log.info("Execution log: \n" + console);
			}
			catch (BusinessException e) {
				throw e;
			}
			catch (Exception e) {
				throw new BusinessException("Error while executing DB Object: " + config.getDatabase() + ":" + config
					.getOwner() + ":" + config.getObject());
			}
		}
	}

	@Override
	public List<LazJobParam> getJobParams() {
		return null;
	}
}