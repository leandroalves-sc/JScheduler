package com.topsoft.jscheduler.job.sample;

import java.util.Arrays;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alee.utils.ThreadUtils;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam.JobParamType;
import com.topsoft.jscheduler.job.quartz.job.LazJob;
import com.topsoft.jscheduler.job.quartz.job.SchedulerJob;
import com.topsoft.topframework.base.exception.BusinessException;

@Service(value = "AcselTeste2Job")
@SchedulerJob(scheduler = "LazScheduler")
public class TesteTask2Job extends LazJob {

	private static Logger log = LoggerFactory.getLogger(TesteTask2Job.class);
	private static final String PARAM_EOD_DATE = "eodDate";

	@Override
	public void execute(LazJobDetail job, JobExecutionContext context) throws BusinessException {

		log.info("Job AcselTeste2Job executando");
		ThreadUtils.sleepSafely(5000);
		log.info("Job AcselTeste2Job executado");
	}

	@Override
	public List<LazJobParam> getJobParams() {

		return Arrays
			.asList(new LazJobParam(PARAM_EOD_DATE, "Data base de execu��o da rotina", JobParamType.DATE), new LazJobParam("strParam", "Parametro de texto", JobParamType.STRING));
	}
}