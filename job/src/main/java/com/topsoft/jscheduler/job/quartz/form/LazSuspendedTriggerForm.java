package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazTextArea;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.util.LazSwingUtils;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.view.LazJobNextExecutionsView;

@Lazy
@Component
public class LazSuspendedTriggerForm extends LazForm<Trigger> implements LazViewCapable<Trigger>, ActionListener{
	
	private static final long serialVersionUID = -8598042789155628281L;
	
	@Autowired
	private QuartzBO quartzBO;
	
	@Autowired
	private LazJobNextExecutionsView view;

	private LazTextArea textArea;
	private Trigger trigger;
	
	@Override
	protected void createForm(){
		
		setLayout( new MigLayout( "fill, wrap 1", "[grow,fill]", "[top][grow,fill]" ) );
		
		add( new LazLabel( "Notes:" ) );
		add( new LazScrollPane( textArea = new LazTextArea() ), "grow" );
	}
	
	@Override  
	protected void loadForm(){
		
		this.trigger = dto;
		
		textArea.setText( "" );
	}
	
	private void onSuspend(){
		
		if( textArea.getText().length() != 0 ){
		
			if( LazAlert.showQuestion( "Suspend execution?" ) == JOptionPane.YES_OPTION ){
				
				quartzBO.suspendTrigger( trigger, textArea.getText() );
				LazSwingUtils.getParent( this, JFrame.class ).dispose();
				view.refresh();
			}
		}
		else{
			LazAlert.showWarning( "Required to inform a note" );
		}
	}
	
	@Override
	public String getTitle(){
		return "Suspend execution";
	}

	@Override
	public boolean isResizable(){
		return false;
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension( 400, 300 );
	}

	@Override
	public LazButtonType[] getButtons(){
		return new LazButtonType[]{ LazButtonType.SAVE, LazButtonType.CANCEL };
	}
	
	@Override
	public void actionPerformed( ActionEvent event ){
		
		if( event.getSource() instanceof LazButton ){

			LazButton button = (LazButton) event.getSource();
			
			if( button.getType() != null && button.getType() == LazButtonType.SAVE )
				onSuspend();
		}
	}
	
	@Override protected void saveForm(){}
}