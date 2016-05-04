package com.topsoft.jscheduler.job.quartz.form;

import java.io.File;

import net.miginfocom.swing.MigLayout;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazCheckBox;
import com.topsoft.topframework.swing.LazPathField;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionFile;

@Lazy
@Component
public class LazJobConfigFileForm extends LazJobConfigForm<LazJobExecutionFile>{

	private static final long serialVersionUID = 522455396318570985L;
	
	private LazPathField lpfSource, lpfDestination;
	private LazCheckBox chbRecursive, chbDeleteFiles;

	@Override
	protected void createForm(){

		setLayout( new MigLayout( "fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]" ) );
		
		add( "Source: ", lpfSource = new LazPathField() );
		add( "Destination: ", lpfDestination = new LazPathField() );
		add( "Recursive copy: ", chbRecursive = new LazCheckBox(), "split 3" );
		add( "Delete source file(s) after copy: ", "tag right", chbDeleteFiles = new LazCheckBox(), "w pref!, tag right" );
	}

	@Override
	protected void loadForm(){
		
		lpfSource.setSelectedPath( new File( dto.getSourcePath() ) );
		lpfDestination.setSelectedPath( new File( dto.getDestinationPath() ) );
		chbRecursive.setSelected( dto.isRecursive() );
		chbDeleteFiles.setSelected( dto.isDeleteAfterCopy() );
	}

	@Override
	protected void saveForm(){

		if( dto == null )
			dto = new LazJobExecutionFile();
		
		dto.setSourcePath( lpfSource.getSelectedPath().getPath() );
		dto.setDestinationPath( lpfDestination.getSelectedPath().getPath() );
		dto.setRecursive( chbRecursive.isSelected() );
		dto.setDeleteAfterCopy( chbDeleteFiles.isSelected() );
	}
	
	@Override
	public void resetForm(){
		dto = new LazJobExecutionFile();
		loadForm();
	}

	@Override
	protected LazButtonType[] getButtons(){
		return null;
	}
}