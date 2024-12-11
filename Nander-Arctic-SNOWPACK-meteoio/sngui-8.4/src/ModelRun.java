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
// ModelRun: Manages the execution of the SNOWPACK model
///////////////////////////////////////////////////////////////////////////////

package ProWin;

import java.io.*;
import java.awt.*;

public class ModelRun implements Runnable, C_DataFile
{
  Thread runThread = null;
  Process p = null;
  MenuFrame mFrame;
  String MODEL_DIRECTORY;
  File outfile;

  public ModelRun(MenuFrame mFrame, String MODEL_DIRECTORY, File outfile)
  {
      this.mFrame = mFrame;
      this.outfile = outfile;
      this.MODEL_DIRECTORY = MODEL_DIRECTORY;
      runThread = new Thread(this, "ModelRun");
      runThread.setPriority(Thread.NORM_PRIORITY + 1);
      runThread.start(); // Initiates the run() method
  }



  public void run()
  {
      Setup.modelRunning = true;
      System.out.println("OS = " + System.getProperty("os.name"));

      MessageBox mBox;
      String programName;
      String scriptName;
      String slash;

      String osName = System.getProperty("os.name").toLowerCase();
      String commandLine[] = null;

      if (osName.startsWith("win") || osName.startsWith("nt"))
      {
        // Windows

        // SN_GUI start under Windows currently does not work
        /* >>>
        mBox = new MessageBox(mFrame, "Note",
              "Sorry. SNOWPACK under Windows cannot be started",
              "from within the SN_GUI yet. Use the command line!");
        mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
        mBox.setVisible(true);
        Setup.modelRunning = false;
        return;
        */



// !! RETURN

// !! Statements below are valid, if RETURN above is removed

        // only works if MODEL_DIRECTORY and SN_GUI share the same parent directory
        /*
        mBox = new MessageBox(mFrame, "Note",
              "SNOWPACK run under Windows just possible if SN_GUI",
              "and MODEL_DIRECTORY share same parent directory");
        mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
        mBox.setVisible(true);
        */

        programName = "snowpack.exe";
//      scriptName = "C:\\JBuilder3\\myprojects\\ProWin\\Model_Start.bat";
        scriptName = "Model_Start.bat";
        slash = "\\";

        commandLine = new String[4];
        commandLine[0] = "cmd.exe"; // evtl. "command.exe" bei Windows 98 aufrufen
        commandLine[1] = "/C";
        commandLine[2] = scriptName;
        commandLine[3] = MODEL_DIRECTORY; // Abs. path of directory in which snowpack executable file resides


        System.out.println(System.getProperty("user.dir")); // Print current path
        // String GPATH = "";
        //System.setProperty(GPATH, System.getProperty("user.dir")); // Current path in env. variable??

      }
      else
      {
        // Unix
        programName = "snowpack";
        scriptName = "Model_Start";
        slash = "/";


        commandLine = new String[2];
        commandLine[0] = scriptName;
        commandLine[1] = MODEL_DIRECTORY; // Abs. path of directory in which snowpack executable file resides
      }

      /*
      // Construct batch file to start SNOWPACK
      try {
        FileOutputStream fout = new FileOutputStream(scriptName, false);
        PrintWriter pout = new PrintWriter(fout);
        pout.println("#!/bin/sh");
        pout.println("cd ../" + MODEL_DIRECTORY);
        pout.println(programName);
        pout.println("cd ../SN_GUI");
        pout.close();
        fout.close(); }
      catch (IOException e1)
      { System.out.println("IOException in ModelDialog, jButton_Run_actionPerformed");}
      */

//      String commandLine[] =
//      {
//        "dir",""
//          "C:\\JBuilder3\\myprojects\\Prowin\\notepad",""
//               funktioniert nicht, file not found (falls ohne Pfadangabe: ruft notepad auf, durch PATH definiert)
//          "C:\\JBuilder3\\myprojects\\Prowin\\test.bat",""
//               funktioniert nicht, keine Fehlermeldung (test ruft KLEIN oder CALL KLEIN oder AbsPath/KLEIN
//                                                        oder copy AbsPath/file1 AbsPath/file2)
//          "C:\\JBuilder3\\myprojects\\Prowin\\KLEIN.EXE", ""
//               funktioniert
//          "C:\\JBuilder3\\myprojects\\Prowin\\test.bat", "C:\\JBuilder3\\myprojects\\snowpack"
//               funktioniert nicht (keine Fehlermeldung)
//          "C:\\JBuilder3\\myprojects\\snowpack\\snowpack C:\\JBuilder3\\myprojects\\ProWin\\SETUP\\CONSTANTS_User.INI"
//               Direktstart von Snowpack: IO-Error #123??
//          "C:\\JBuilder3\\myprojects\\snowpack\\snowpack", ""
//               Successful termination (but without commandline argument)
//           "cmd.exe","/C","C:\\JBuilder3\\myprojects\\Prowin\\test.bat","C:\\JBuilder3\\myprojects\\snowpack"
//               no reaction, also if test.bat only contains a call of KLEIN.EXE; funktioniert bei Direkteingabe in
//               command line!
//           "cmd.exe","/C",""
//               cmd Program executes and terminates correctly
//           "cmd.exe","/C","C:\\JBuilder3\\myprojects\\ProWin\\test.bat"
//               keine termination, keine Reaktion (test enthält KLEIN.EXE)
//           "cmd.exe","C:\\JBuilder3\\myprojects\\ProWin\\test.bat",""
//               keine termination, keine Reaktion (test enthält KLEIN.EXE)
//             "cmd.exe","/C","C:\\JBuilder3\\myprojects\\ProWin\\KLEIN.EXE",""

//            "cmd.exe", "/C",

//            scriptName,
//            MODEL_DIRECTORY // Abs. path of directory in which snowpack executable file resides
        // or path relative to SN_GUI-directory
//      };


      // Check if script file exists
      File scriptFile = new File(scriptName);
      if (!scriptFile.exists())
      {
          mBox = new MessageBox(mFrame, "Error",
                "Script file does not exist: ", scriptName);
          mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
          mBox.setVisible(true);

          Setup.modelRunning = false;
          return;
      }

      // Check if program file exists
      File programFile;
      programFile = new File(MODEL_DIRECTORY + slash + programName);


      if (!programFile.exists())
      {
          mBox = new MessageBox(mFrame, "Error",
              "Program file does not exist: ", MODEL_DIRECTORY + slash + programName);
          mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
          mBox.setVisible(true);

          Setup.modelRunning = false;
          return;
       }


      try
      {
         // String environment[] = {"path=" + "../" + MODEL_DIRECTORY + "/" + programName};
         // p = r.exec(commandLine, environment); // not working properly

         Runtime r = Runtime.getRuntime();
         Process p = null;

         p = r.exec(commandLine);

         mBox = new MessageBox(mFrame, "Note",
              "SNOWPACK model successfully started.",
              "You will be notified upon termination.");
         mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
         mBox.setVisible(true);

         System.out.println("SNOWPACK model started; command line = "
           + commandLine[0] + " " + commandLine[1] + " " + commandLine[2]);

         p.waitFor(); // wait till called program finished;
                      // refers just to Thread and does not block other parts of program
                      // without this: "IllegalThreadStateEception"

         Toolkit.getDefaultToolkit().beep();

         if (p.exitValue() != 0)
         // Model run terminated with errors
         {
            mBox = new MessageBox(mFrame, "Note",
              "SNOWPACK model run terminated.",
              "Runtime errors, exit value = " + p.exitValue() + "!");
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);
         }
         else
         // Model run terminated without errors
         {
            mBox = new MessageBox(mFrame, "Note",
              "SNOWPACK model run terminated.",
              "No runtime errors (exit value = 0).");
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);

            // Display of the model log file
            ModelLogFileDisplay(true);

            if (!outfile.exists())
            {
              mBox = new MessageBox(mFrame, "Note",
                "Display not possible, model output file not found: ",
                outfile.getPath() + "!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);
            }
            else
            {
              // Ask the user if model output should be visualized
              YesNoDialog ynd = new YesNoDialog(mFrame, "Model Output Display",
                "Do you want to visualize the output", "of the model run right now?",
                "Yes", "No, later", true);
              ynd.setLocation(mFrame.DialogCorner(ynd, mFrame));
              ynd.setVisible(true);

              if (ynd.action1) // Immediate visualization of the model output desired
              {
                mFrame.setVisible(true);
                mFrame.NewFrame(outfile, ID_CODE_SNOWPACK_TEMPERATURE);
              }
              else
              {
                mBox = new MessageBox(mFrame, "Info",
                  "Output display later: Close all open frames with same",
                  "name as constructed file. Otherwise no data refresh!");
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);
              }

            }
         }

         System.out.println("SNOWPACK finished, exit value = " + p.exitValue());

      }
      catch (Exception e1)
      {
         mBox = new MessageBox(mFrame, "Error",
              "Error executing the SNOWPACK model:", e1.toString());
         mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
         mBox.setVisible(true);
         // java.io.IOException: error = 2 .... file not found
         //                              3 .... directory not found

         System.out.println("Error executing SNOWPACK: " + e1);
      }

      // Thread Running the model is finished now
      Setup.modelRunning = false;

  }


  // Display the Logfile created by the model run
  void ModelLogFileDisplay(boolean callFromRunThread)
  // callFromRunThread = true if called from run thread after successful run
  {
     System.out.println("ModelRun: in Model Log file");

     // Check if ModelDialog was started before during this SN_GUI session
     if (Setup.m_SnowPackPath == "")
     {
       MessageBox mBox = new MessageBox(mFrame, "Note", "Path of ModelLogFile unknown.",
          "Start the SNOWPACK model first!");
       mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
       return;
     }

     String SnowPackPath = Setup.m_SnowPackPath;

     // Check if ModelLogFile exists exists
     File modelLogFile = new File(SnowPackPath + "/ModelLogFile");
     if (!modelLogFile.exists())
     {
       MessageBox mBox = new MessageBox(mFrame, "Error", "File does not exist: ",
         SnowPackPath + "/ModelLogFile");
       mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
       return;
     }

     if (runThread.isAlive() && !callFromRunThread)
     {
       MessageBox mBox = new MessageBox(mFrame, "Note", "Model execution not terminated yet.",
         "Please try again later.");
       mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
       return;
     }
     else
     {
       FileDisplay fd = new FileDisplay
        (mFrame, "ModelLogFile (SNOWPACK model run information)", true, SnowPackPath + "/ModelLogFile");
       fd.setLocation(mFrame.DialogCorner(fd, mFrame));
       fd.setVisible(true);
     }
  }


}