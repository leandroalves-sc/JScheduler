package com;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.alee.laf.WebLookAndFeel;

public class LAF extends WebLookAndFeel{

	@Override
	protected void initClassDefaults(UIDefaults table) {
		
		WebLookAndFeel.rootPaneUI = MyRootPaneUI.class.getCanonicalName ();
		
		super.initClassDefaults(table);
		
		table.put ( "RootPaneUI", MyRootPaneUI.class.getCanonicalName() );
	}
	
	public static boolean install ( final boolean updateExistingComponents )
    {
        try
        {
            // Installing LookAndFeel
            UIManager.setLookAndFeel ( new LAF () );

            // Updating already created components tree
            if ( updateExistingComponents )
            {
                updateAllComponentUIs ();
            }

            // LookAndFeel installed sucessfully
            return true;
        }
        catch ( final Throwable e )
        {
            // Printing exception
            e.printStackTrace ();

            // LookAndFeel installation failed
            return false;
        }
    }
}
