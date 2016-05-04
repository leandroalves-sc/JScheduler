package com.topsoft.jscheduler.job.quartz.domain.type;

import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigDBForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigDelegateForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigFTPForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigFileForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigFileRunForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigJavaForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobConfigSleepForm;
import com.topsoft.jscheduler.job.util.LazJobContext;

public enum QuartzExecutionType {

	JAVA("Java Object") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigJavaForm.class);
		}
	},
	SLEEP("Sleep task") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigSleepForm.class);
		}
	},
	DELEGATE("Delegate Job") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigDelegateForm.class);
		}
	},
	DB("Database object") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigDBForm.class);
		}
	},
	FTP("FTP transfer") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigFTPForm.class);
		}
	},
	FILE("Local file copy") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigFileForm.class);
		}
	},
	RUN_FILE("Execute local file") {

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LazJobConfigForm getForm() {
			return LazJobContext.getBean(LazJobConfigFileRunForm.class);
		}
	};

	private String description;

	QuartzExecutionType(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public abstract LazJobConfigForm<LazJobExecutionConfig> getForm();
}