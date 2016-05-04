package com;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.rootpane.WebRootPane;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.fonts.LazFonts;
import com.topsoft.topframework.swing.util.LazSwingConstants;
import com.topsoft.topframework.swing.util.LazSwingUtils;
import com.topsoft.topframework.swing.util.SystemColors;

import net.miginfocom.swing.MigLayout;

public class MyRootPane extends WebRootPane implements ActionListener, MouseMotionListener, MouseListener{


	LazPanel title, body;
	LazLabel lbTitle;
	private int dX, dY;
	private LazLabel lbMaximize;
	private LazLabel lbMinimize;
	private LazLabel lbClose;
	private Point restoreLocation;
	private Dimension restoreSize;
	private WebFrame frame;
	
	public MyRootPane( WebFrame frame ) {
		
		this.frame = frame;
		
		LazPanel panel = new LazPanel( new MigLayout( "fill, ins 0, wrap 1", "[grow,fill]", "[top]1[grow,fill]" ) );
		panel.setBackground( Color.white );
		panel.setBorder( BorderFactory.createLineBorder( SystemColors.GREEN_1 ) );
		
		lbTitle = new LazLabel( "Title" );
		lbTitle.setForeground( Color.white );
		lbTitle.setFont( LazFonts.BASE_FONT_BOLD );
		
		title = new LazPanel( new MigLayout( "fill, ins 2 0 0 0, wrap 4", "5[grow,fill][pref!]2[pref!]2[pref!]5" ) );
		title.setBackground( Color.white );
		title.add( lbTitle, "grow" );
		title.add( lbMinimize = new LazLabel( LazImage.MINIMIZE, this ) );
		title.add( lbMaximize = new LazLabel( LazImage.MAXIMIZE, this ) );
		title.add( lbClose = new LazLabel( LazImage.CLOSE, this ) );
		
		panel.add( title, "h 28!" );

		body = new LazPanel();
		body.setBackground( Color.white );
		panel.add( body, "grow" );
		
		setContentPane( panel );
		
		setVisible( true );
		
		frame.addMouseListener( this );
		frame.addMouseMotionListener( this );
		
		title.addMouseListener( this );
		title.addMouseMotionListener( this );
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		if( e.getSource() == title ){
		
            dX = e.getLocationOnScreen().x - frame.getX();
            dY = e.getLocationOnScreen().y - frame.getY();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		if( e.getSource() == title ){
			
			frame.setLocation( e.getLocationOnScreen().x - dX, e.getLocationOnScreen().y - dY );
            dX = e.getLocationOnScreen().x - frame.getX();
            dY = e.getLocationOnScreen().y - frame.getY();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		Object source = e.getSource();
		
		if( source == lbMinimize ){
			frame.setState(JFrame.ICONIFIED);
		}
		else if( source == lbMaximize ){
			
			if( lbMaximize.getIcon() == LazImage.MAXIMIZE.getIcon() ){
				
				lbMaximize.setIcon( LazImage.RESTORE.getIcon() );
				
				restoreLocation = frame.getLocation();
				restoreSize = frame.getSize();
				frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
			}
			else{
				
				lbMaximize.setIcon( LazImage.MAXIMIZE.getIcon() );
				frame.setExtendedState(JFrame.NORMAL);
				frame.setLocation( restoreLocation );
				frame.setSize( restoreSize);
			}
		}
		else if( source == lbClose ){
			System.exit(0);
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {
		
		Cursor cursor = null;
		Point point = e.getPoint();
		
		if( ( frame.getSize().getWidth() - point.getX() ) <= 3 && ( frame.getSize().getHeight() - point.getY() ) <= 3 ){
			cursor = LazSwingConstants.SE_RESIZE_CURSOR;	
		}

		LazSwingUtils.showCursor( this, cursor != null ? cursor : LazSwingConstants.DEFAULT_CURSOR );
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
}