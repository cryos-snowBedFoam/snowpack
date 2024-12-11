///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualization
//Version:
//Copyright:    Copyright (c) 2001
//Author:       G. Spreitzhofer
//Organization: SLF
//Description:  Java-Version of SnowPack.
//Integrates the C++-Version of M. Steiniger
//       and the IDL-Version of M. Lehning/P.Bartelt.
///////////////////////////////////////////////////////////////////////////////
// MaximizeActionListener:
// maximizes SnowPackFrames when corresponding menu items are clicked
///////////////////////////////////////////////////////////////////////////////

package ProWin;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;


public class MaximizeActionListener implements ActionListener
{
  public SnowPackFrame spFrame;
  public JDesktopPane desktopPane;

  public MaximizeActionListener()
  {
  }

  //public void set(SnowPackFrame spFrame) {this.spFrame = spFrame;}

  public void actionPerformed(ActionEvent e)
  {
     try {
           Setup.m_2Columns = false;

           if (Setup.m_Synchronization)
           {
             Setup.m_Synchronization = false;
             spFrame.mFrame.chckbxmenu_Synchronization.setState(false);

             MessageBox mBox = new MessageBox(spFrame.mFrame, "Note",
               "Synchronization (Time menu) is",
               "switched off on frame maximizing!");
             mBox.setLocation(spFrame.mFrame.DialogCorner(mBox, spFrame.mFrame));
             mBox.setVisible(true);
           }

           spFrame.setMaximum(true);

           // Insert control objects
           int w = desktopPane.getSize().width;
           int h = desktopPane.getSize().height;
           spFrame.setBounds(0, 0, w, h); // w, h is set, needed for controls

           if (Setup.m_SliderDisplay)
           {
             spFrame.getSnowPackView().InsertControlObjects();
             spFrame.setBounds(0, 0, w, h); // Called another time to repaint controls
           }

     }
     catch (Exception e1)
     {
        System.out.println("MaximizeActionListener: Exception " + e1);
     }
  }
}