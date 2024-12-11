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
// FileUpdate:
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;
import javax.swing.*;

public class FileUpdate implements Runnable
{
  File firstFile = null;
  File nextFile = null;
  Thread updateThread = null;
  MenuFrame mFrame;

  public FileUpdate(MenuFrame mFrame, File firstFile)
  {
      this.mFrame = mFrame;
      this.firstFile = firstFile;
      if (firstFile != null)
      {
        updateThread = new Thread(this, "FileUpdate");
        updateThread.setPriority(Thread.NORM_PRIORITY - 1);
        updateThread.start(); // Initiates the run() method
      }
  }


  public void run()
  {
      int k=0;
      boolean errorAlreadyDisplayed = false;
      do
      {
        // Pause
        try { Thread.sleep(1000); } // pause 1000 ms
        catch (InterruptedException e)
        { System.out.println("FileUpdate(): problems with Thread.sleep"); }

        nextFile = mFrame.FindStartFile();
        if (nextFile == null)
        {
           if (!errorAlreadyDisplayed)
           {
              if (!Setup.m_FileDialogActive)
              {
                MessageBox mBox = new MessageBox(mFrame, "Error",
                "Start file: name not found", "or file does not exist!");
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);

                errorAlreadyDisplayed = true;
              }
           }
        }
        else
        {
         if (!nextFile.equals(firstFile))
         {
          errorAlreadyDisplayed = false;
          k++;
          System.out.println("File change #"+k);

          // StartFile has changed since last check
          // Close all SnowPackFrames and restart operational startup procedures
          /*
           JInternalFrame iframes[] = mFrame.desktopPane.getAllFrames();
           for (int i = 0; i < iframes.length; i++)
           {
              SnowPackFrame spframe = (SnowPackFrame) iframes[i];
              if (spframe != null)
              {
                mFrame.desktopPane.remove(spframe);
                spframe.dispose();
              }
           }
           */
           mFrame.menu_CloseAll_actionPerformed();

           mFrame.OperationalStartup(mFrame.FindStartFile());
           String f = firstFile.getPath();
           String n = nextFile.getPath();
           System.out.println(f+" "+n);
           firstFile = nextFile;
          }

         }
        } while(Setup.m_FileUpdate);

  }


}