package com.topsoft.jscheduler.job.quartz.form;

import java.io.File;

import net.miginfocom.swing.MigLayout;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alee.global.GlobalConstants;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazFileChooserField;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionFileRun;

@Lazy
@Component
public class LazJobConfigFileRunForm extends LazJobConfigForm<LazJobExecutionFileRun>{

	private static final long serialVersionUID = 522455396318570985L;
	
	private LazFileChooserField lpfFile;
	private LazTextField txfArgs;
	
	@Override
	protected void createForm(){

		setLayout( new MigLayout( "fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]" ) );
		
		add( "File: ", lpfFile = new LazFileChooserField() );
		add( "Arguments: ", txfArgs = new LazTextField() );
		
		lpfFile.getWebFileChooser().addChoosableFileFilter( GlobalConstants.ALL_FILES_FILTER );
	}

	@Override
	protected void loadForm(){
		
		lpfFile.setSelectedFile( new File( dto.getFilePath() ) );
		txfArgs.setText( dto.getArgs() );
	}

	@Override
	protected void saveForm(){

		if( dto == null )
			dto = new LazJobExecutionFileRun();
		
		dto.setFilePath( lpfFile.getSelectedFiles().get( 0 ).getAbsolutePath() );
		dto.setArgs( txfArgs.getText() );
	}
	
	@Override
	public void resetForm(){
		dto = new LazJobExecutionFileRun();
		loadForm();
	}

	@Override
	protected LazButtonType[] getButtons(){
		return null;
	}
}