package com.topsoft.jscheduler.job.quartz.form;

import java.io.File;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionFTP;
import com.topsoft.topframework.ftp.domain.FTPFile;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazCheckBox;
import com.topsoft.topframework.swing.LazFTPPathField;
import com.topsoft.topframework.swing.LazPathField;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class LazJobConfigFTPForm extends LazJobConfigForm<LazJobExecutionFTP> {

	private static final long serialVersionUID = 5673946028910848719L;

	private LazPathField lpfSource;
	private LazFTPPathField lpfDestination;
	private LazCheckBox chbRecursive, chbDeleteFiles;

	@Override
	protected void createForm() {

		setLayout(new MigLayout("fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]"));

		add("Source: ", lpfSource = new LazPathField());
		add("Destination: ", lpfDestination = new LazFTPPathField());
		add("Recursive copy: ", chbRecursive = new LazCheckBox(), "split 3");
		add("Delete source file(s) after copy: ", "tag right", chbDeleteFiles = new LazCheckBox(), "w pref!, tag right");
	}

	@Override
	protected void loadForm() {

		lpfDestination.setFtpConfig(dto.getFtpConfig());
		lpfSource.setSelectedPath(new File(dto.getSourcePath()));
		lpfDestination.setFtpConfig(dto.getFtpConfig());
		lpfDestination.setSelectedPath(new FTPFile(dto.getDestinationPath()));
		chbRecursive.setSelected(dto.isRecursive());
		chbDeleteFiles.setSelected(dto.isDeleteAfterCopy());
	}

	@Override
	protected void saveForm() {

		if (dto == null)
			dto = new LazJobExecutionFTP();

		dto.setFtpConfig(lpfDestination.getFtpConfig());
		dto.setSourcePath(lpfSource.getSelectedPath().getPath());
		dto.setDestinationPath(lpfDestination.getSelectedPath().getPath());
		dto.setRecursive(chbRecursive.isSelected());
		dto.setDeleteAfterCopy(chbDeleteFiles.isSelected());
	}

	@Override
	public void resetForm() {

		lpfDestination.disconnect();
		dto = new LazJobExecutionFTP();
	}

	@Override
	protected LazButtonType[] getButtons() {
		return null;
	}
}