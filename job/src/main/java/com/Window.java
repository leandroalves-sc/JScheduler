package com;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.alee.laf.rootpane.WebFrame;

public class Window extends WebFrame{
	
	private static final long serialVersionUID = 4348999963843016634L;
	
	private MyRootPane root;
	
	public Window() {
		
		setRootPane( root = new MyRootPane( this ) );
		
		setShadeWidth(10);
		setSize(800, 600);
		setLocation(300, 300);
		setVisible(true);
	}
	
	@Override
	public Component add(Component comp) {
		return root.add(comp);
	}
	
	public static void main(String[] args) {
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		LAF.install(true);
		LAF.setMnemonicHidden(false);
		LAF.setDecorateAllWindows(true);
		LAF.setDecorateFrames(true);
		
		new Window();
	}
}
