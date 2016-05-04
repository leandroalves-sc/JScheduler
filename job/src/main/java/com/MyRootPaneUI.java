package com;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.JComponent;

import com.alee.laf.rootpane.WebRootPaneUI;
import com.alee.utils.GraphicsUtils;

public class MyRootPaneUI extends WebRootPaneUI{
	
    @Override
    public void paint ( final Graphics g, final JComponent c )
    {
        final Graphics2D g2d = ( Graphics2D ) g;
        final Object aa = GraphicsUtils.setupAntialias ( g2d );
        final boolean max = isFrameMaximized ();

        if ( max )
        {
            // Background
            g2d.setPaint ( new GradientPaint ( 0, 0, topBg, 0, 30, middleBg ) );
            g2d.fillRect ( 0, 0, c.getWidth (), c.getHeight () );

            // Border
            g2d.setPaint ( borderColor );
            g2d.drawRect ( 0, 0, c.getWidth () - 1, c.getHeight () - 1 );
            g2d.setPaint ( innerBorderColor );
            g2d.drawRect ( 1, 1, c.getWidth () - 3, c.getHeight () - 3 );

            // Watermark
            if ( drawWatermark )
            {
                final Shape old = GraphicsUtils.intersectClip ( g2d, getWatermarkClip ( c ) );
                g2d.drawImage ( getWatermark ().getImage (), 2, 2, null );
                GraphicsUtils.restoreClip ( g2d, old );
            }
        }
        else
        {
            // Shade
            if ( shadeWidth > 0 )
            {
                final int diff = isActive ( c ) ? 0 : shadeWidth - inactiveShadeWidth;
                getShadeIcon ( c ).paintIcon ( g2d, diff, diff, c.getWidth () - diff * 2, c.getHeight () - diff * 2 );
            }

            // Background
            g2d.setPaint ( Color.red );// new GradientPaint ( 0, shadeWidth, topBg, 0, shadeWidth + 30, middleBg ) );
            g2d.fillRoundRect ( shadeWidth, shadeWidth, c.getWidth () - shadeWidth * 2, c.getHeight () - shadeWidth * 2, round * 2,
                    round * 2 );

            // Border
            g2d.setPaint ( borderColor );
            g2d.drawRoundRect ( shadeWidth, shadeWidth, c.getWidth () - shadeWidth * 2 - 1, c.getHeight () - shadeWidth * 2 - 1,
                    round * 2 - 2, round * 2 - 2 );
            g2d.setPaint ( innerBorderColor );
            g2d.drawRoundRect ( shadeWidth + 1, shadeWidth + 1, c.getWidth () - shadeWidth * 2 - 3, c.getHeight () - shadeWidth * 2 - 3,
                    round * 2 - 4, round * 2 - 4 );

            // Watermark
            if ( drawWatermark )
            {
                final Shape old = GraphicsUtils.intersectClip ( g2d, getWatermarkClip ( c ) );
                g2d.drawImage ( getWatermark ().getImage (), shadeWidth + 2, shadeWidth + 2, null );
                GraphicsUtils.restoreClip ( g2d, old );
            }
        }

//        GraphicsUtils.restoreAntialias ( g2d, aa );
    }
}