///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualization
//Version:
//Copyright:    Copyright (c) 2001
//Author:       G. Spreitzhofer (based on a version from M. Steiniger)
//Organization: SLF
//Description:  Java-Version of SnowPack.
//Integrates the C++-Version of M. Steiniger
//       and the IDL-Version of M. Lehning/P.Bartelt.
///////////////////////////////////////////////////////////////////////////////
// PS_Printer: prints a postscript file under Unix
///////////////////////////////////////////////////////////////////////////////

// NOT TESTED AND USED!!
// just to execute the command line with the printing command

package ProWin;

import java.io.*;


public class PS_Printer implements Runnable //, C_DataFile
{
  Thread runThread = null;
  MenuFrame mFrame;
  String PSfile;

  public PS_Printer(MenuFrame mFrame, String PSfile)
  {
      this.mFrame = mFrame;
      this.PSfile = PSfile;

      if (System.getProperty("os.name").substring(0,3).equals("Win"))
      {
        MessageBox mBox = new MessageBox(mFrame, "Note",
              "Does not work under Windows.",
              "Sorry.");
        mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
        mBox.setVisible(true);
      }
      else
      {
        runThread = new Thread(this, "PS_Printer");
        runThread.setPriority(Thread.NORM_PRIORITY);
        runThread.start(); // Initiates the run() method
      }
  }


  public void run()
  {

      MessageBox mBox;

      // Check if postscript file exists
      File file = new File(PSfile);

      if (!file.exists())
      {
          mBox = new MessageBox(mFrame, "Error",
              "Postscript file does not exist: ", PSfile);
          mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
          mBox.setVisible(true);

          return;
       }

      String commandLine[] =
      {
            "lp","-d" + Setup.m_Printer, PSfile
      };


      try
      {
         Runtime r = Runtime.getRuntime();
         Process p = null;

         p = r.exec(commandLine);

         mBox = new MessageBox(mFrame, "Note",
              "Printing command executed.",
              "You will be notified upon termination.");
         mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
         mBox.setVisible(true);

         //System.out.println("SNOWPACK model started; command line = "
         //  + commandLine[0] + " " + commandLine[1] + " " + commandLine[2]);

         p.waitFor(); // wait till called program finished;
                      // refers just to Thread and does not block other parts of program
                      // without this: "IllegalThreadStateEception"

         if (p.exitValue() != 0)
         // Termination with errors
         {
            mBox = new MessageBox(mFrame, "Note",
              "Errors occurred during printing;",
              "exit value = " + p.exitValue() + "!");
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);
         }
         else
         // Termination without errors
         {
            mBox = new MessageBox(mFrame, "Note",
              "Printing terminated successfully.",
              "No errors (exit value = 0).");
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);
         }

      }
      catch (Exception e1)
      {
         mBox = new MessageBox(mFrame, "Error",
              "Error occurred during printing:", e1.toString());
         mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
         mBox.setVisible(true);
         // java.io.IOException: error = 2 .... file not found
         //                              3 .... directory not found

         System.out.println("Error during printing: " + e1);
      }

  }
}