package com;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.rootpane.WebFrame;

public class TestWindow extends WebFrame {
	
	public TestWindow() {
		
		setUndecorated( true );
		setSize( 800, 600 );
		setVisible(true);
	}

	public static void main(String[] args) {
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		WebLookAndFeel.install(true);
		WebLookAndFeel.setMnemonicHidden(false);
		WebLookAndFeel.setDecorateAllWindows(true);
		WebLookAndFeel.setDecorateFrames(true);
		
		new TestWindow();
	}
}
