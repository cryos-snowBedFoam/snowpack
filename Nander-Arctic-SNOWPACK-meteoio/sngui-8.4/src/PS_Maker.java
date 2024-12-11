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
// PS_Maker: converts a *.jpg-file to a *.ps-file (postscript)
///////////////////////////////////////////////////////////////////////////////


// Tested under Unix. Everything seems to work fine, command line is correct,
// message "*.ps constructed correctly" appears, but no *.ps file is where it
// should be.


package ProWin;

import java.io.*;

public class PS_Maker implements Runnable //, C_DataFile
{
  Thread runThread = null;
  MenuFrame mFrame;
  String JPGfile;

  public PS_Maker(MenuFrame mFrame, String JPGfile)
  {
      this.mFrame = mFrame;
      this.JPGfile = JPGfile;

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
        runThread = new Thread(this, "PS_Maker");
        runThread.setPriority(Thread.NORM_PRIORITY);
        runThread.start(); // Initiates the run() method
      }
  }


  public void run()
  {
      MessageBox mBox;

      // Check if *.jpg-file exists
      File JPG;
      JPG = new File(JPGfile);

      if (!JPG.exists())
      {
          mBox = new MessageBox(mFrame, "Error",
              "Input file *.JPG does not exist: ", JPGfile);
          mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
          mBox.setVisible(true);
          return;
      }

      // Construct name of *.ps-file
      String PSfile = JPGfile.substring(1, JPGfile.length() - 4) + ".ps";

      String commandLine[] = { "convert", "-page", "a4", JPGfile, PSfile };

      try
      {
         Runtime r = Runtime.getRuntime();
         Process p = null;

         p = r.exec(commandLine);

         System.out.println("Started: command line = "
           + commandLine[0] + " " + commandLine[1] + " " + commandLine[2]
               + " " + commandLine[3] + " " + commandLine[4]);
         p.waitFor(); // wait till called program finished;
                      // refers just to Thread and does not block other parts of program
                      // without this: "IllegalThreadStateEception"

         if (p.exitValue() != 0)
         // Termination with errors
         {
            mBox = new MessageBox(mFrame, "Error",
              "Error in *.jpg-to-*.ps conversion.",
              "Exit value = " + p.exitValue() + "!");
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);
         }
         else
         // Termination without errors
         {
            mBox = new MessageBox(mFrame, "Note",
              "*.ps-file was constructed:",
              PSfile);
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);
         }

         System.out.println("JPG->PS-conversion finished, exit value = " + p.exitValue());

      }
      catch (Exception e1)
      {
         mBox = new MessageBox(mFrame, "Error",
              "Error in *.jpg-to-*.ps conversion:", e1.toString());
         mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
         mBox.setVisible(true);
         // java.io.IOException: error = 2 .... file not found
         //                              3 .... directory not found

         System.out.println("Error in *.jpg-to-*.ps conversion: " + e1);
      }

  }
}