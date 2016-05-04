package com.topsoft.jscheduler.job.view;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.springframework.stereotype.Component;

import com.alee.laf.WebLookAndFeel;
import com.topsoft.jscheduler.job.quartz.view.QuartzView;
import com.topsoft.jscheduler.job.util.LazJobContext;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazView;

import net.miginfocom.swing.MigLayout;

@Component
public class JobManagerView extends LazView{

	private static final long serialVersionUID = -4391066062440076278L;
	
	@Override
	public void createView() {
		
		setTitle("JScheduler");
		setLayout(new MigLayout("fill, ins 5", "[grow,fill]"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		QuartzView quartzView = context.getBean(QuartzView.class);
		add(quartzView, "grow");
		quartzView.refreshJobs();

		setVisible(true);
		setBounds(0, 0, getToolkit().getScreenSize().width, getToolkit().getScreenSize().height);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private static boolean isPortUsed() {

		ServerSocket socket = null;

		try {

			socket = new ServerSocket(1099);
			return false;
		}
		catch (IOException e) {
			return true;
		}
		finally {

			if (socket != null) {

				try {
					socket.close();
				}
				catch (IOException e) {

				}
			}
		}
	}

	public static void main(String[] args) {

		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		WebLookAndFeel.install(true);
		WebLookAndFeel.setMnemonicHidden(false);
		WebLookAndFeel.setDecorateAllWindows(true);
		WebLookAndFeel.setDecorateFrames(true);

		if (isPortUsed()) {

			LazAlert.showError("Port 1099 already in use. Initialization stopped.");
			System.exit(1);
		}

		if (args == null || args.length != 1) {

			LazAlert.showError("Error while initiating application. Please provide QuartzSchedulerName to be used.");
			System.exit(1);
		}

		LazJobContext.initialize(args[0]);
	}
}