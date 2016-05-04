package com.topsoft.jscheduler.job.quartz.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazTextArea;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class LazJobLogView extends LazForm<String> implements LazViewCapable<LazJobExecution>{

	private static final long serialVersionUID = 2410875079179182161L;
	private LazTextArea textArea;
	private LazScrollPane scroll;

	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill" ,"[grow,fill]" ) );
		
		add( scroll = new LazScrollPane( textArea = new LazTextArea() ), "grow" );
		textArea.setFont( textArea.getFont().deriveFont( 11f ) );
		textArea.setEditable( false );
	}

	@Override
	public String getTitle(){
		return "Execution log";
	}
	
	@Override
	protected void loadForm(){
		
		textArea.setText( dto );
		scroll.getVerticalScrollBar().setValue(0);
	}	

	@Override
	public boolean isResizable(){
		return false;
	}

	@Override
	public LazButtonType[] getButtons(){
		return null;
	}
	
	@Override
	public Dimension getSize(){
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension( (int) screenSize.getWidth() - 50, (int) screenSize.getHeight() - 200 );
	}

	@Override protected void saveForm(){}
}