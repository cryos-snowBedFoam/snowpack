
//Titel:        SnowPack Visualisierung
//Version:
//Copyright:    Copyright (c) 1999
//Autor:       Spreitzhofer
//Organisation:      SLF
//Beschreibung:  Java-Version von SnowPack.
//Integriert die C++-Version von M. Steiniger und die IDL-Version von M. Lehning.

package ProWin;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;

public class StartFrame extends JFrame implements C_DataFile
{
  MenuFrame mFrame= null;
  ModelRun modelRun = null;

  JPanel jPanel7 = new JPanel();
  JButton jButton_Run = new JButton();
  JButton jButton_Visualize = new JButton();
  JButton jButton_Exit = new JButton();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JPanel jPanel2 = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout3 = new XYLayout();
  JButton jButton_ModelLogFile = new JButton();
  JButton jButton_StopRun = new JButton();
  JPanel jPanel4 = new JPanel();
  XYLayout xYLayout4 = new XYLayout();
  JButton jButton_Info = new JButton();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel5 = new JPanel();
  XYLayout xYLayout5 = new XYLayout();
  JButton jButton_InputFile = new JButton();
  JLabel jLabel2 = new JLabel();
  GridLayout gridLayout1 = new GridLayout();

  public StartFrame()
  {
    //super(frame, title, modal);
    try
    {
      jbInit();
      pack();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }


  public static void main(String[] args)
  {
    try
    {

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      StartFrame startFrame = new StartFrame();
      positionFrame(startFrame, 40, 50, Setup.m_DisplayMode);

      //startFrame.setSize(1094, 777);
    }
    catch(Exception e)
    {
    }
  }

  void jbInit() throws Exception
  {

    jPanel7.setLayout(gridLayout1);
    jPanel7.setForeground(Color.blue);
    jPanel7.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_Run.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_Run.setText("Model Settings");
    jButton_Run.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Run_actionPerformed(e);
      }
    });
    jButton_Visualize.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_Visualize.setMaximumSize(new Dimension(113, 27));
    jButton_Visualize.setMinimumSize(new Dimension(113, 27));
    jButton_Visualize.setPreferredSize(new Dimension(113, 27));
    jButton_Visualize.setText("Visualization");
    jButton_Visualize.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Visualize_actionPerformed(e);
      }
    });
    this.setTitle("SN_GUI: Start Frame");
    this.addWindowListener(new java.awt.event.WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        this_windowClosing(e);
      }
    });
    this.getContentPane().setLayout(borderLayout1);
    jButton_Exit.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_Exit.setMaximumSize(new Dimension(113, 27));
    jButton_Exit.setMinimumSize(new Dimension(113, 27));
    jButton_Exit.setPreferredSize(new Dimension(113, 27));
    jButton_Exit.setText("Exit");
    jButton_Exit.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Exit_actionPerformed(e);
      }
    });
    jLabel3.setText("Define model parameters");
    jLabel4.setText("Visualize the output of the SNOWPACK model");
    jLabel5.setText("Exit this program");
    jPanel1.setForeground(Color.blue);
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setLayout(xYLayout1);
    jPanel2.setForeground(Color.blue);
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setLayout(xYLayout2);
    jPanel3.setForeground(Color.blue);
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setLayout(xYLayout3);
    jButton_ModelLogFile.setText("Model Log File");
    jButton_ModelLogFile.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_ModelLogFile_actionPerformed(e);
      }
    });
    jButton_StopRun.setText("Stop Execution");
    jButton_StopRun.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_StopRun_actionPerformed(e);
      }
    });
    jPanel4.setForeground(Color.blue);
    jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel4.setLayout(xYLayout4);
    jButton_Info.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_Info.setMaximumSize(new Dimension(113, 27));
    jButton_Info.setMinimumSize(new Dimension(113, 27));
    jButton_Info.setPreferredSize(new Dimension(113, 27));
    jButton_Info.setText("Info");
    jButton_Info.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Info_actionPerformed(e);
      }
    });
    jLabel1.setText("Information about the SNOWPACK model");
    jPanel5.setForeground(Color.blue);
    jPanel5.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel5.setLayout(xYLayout5);
    jButton_InputFile.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_InputFile.setMaximumSize(new Dimension(113, 27));
    jButton_InputFile.setMinimumSize(new Dimension(113, 27));
    jButton_InputFile.setPreferredSize(new Dimension(113, 27));
    jButton_InputFile.setText("Input File");
    jButton_InputFile.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_InputFile_actionPerformed(e);
      }
    });
    jLabel2.setText("Create/Edit snow and soil input file for model run");
    gridLayout1.setHgap(5);
    gridLayout1.setRows(5);
    gridLayout1.setVgap(3);
    this.getContentPane().add(jPanel7, BorderLayout.CENTER);
        jPanel7.add(jPanel5, null);
        jPanel7.add(jPanel3, null);
    jPanel3.add(jButton_Run, new XYConstraints(5, 5, 120, 50));
    jPanel3.add(jLabel3, new XYConstraints(146, 8, -1, -1));
    //jPanel3.add(jButton_ModelLogFile, new XYConstraints(291, 44, 117, 28)); //Schirmer
    //jPanel3.add(jButton_StopRun, new XYConstraints(143, 44, 117, 28)); //Schirmer
    jPanel7.add(jPanel2, null);
    jPanel2.add(jLabel4, new XYConstraints(150, 9, -1, -1));
    jPanel2.add(jButton_Visualize, new XYConstraints(5, 5, 120, 50));
        jPanel5.add(jButton_InputFile, new XYConstraints(5, 5, 120, 50));
    jPanel5.add(jLabel2, new XYConstraints(144, 7, -1, -1));
    jPanel7.add(jPanel4, null);
    jPanel4.add(jButton_Info, new XYConstraints(5, 5, 120, 50));
    jPanel4.add(jLabel1, new XYConstraints(147, 9, -1, -1));
    jPanel7.add(jPanel1, null);
    jPanel1.add(jLabel5, new XYConstraints(148, 9, -1, -1));
    jPanel1.add(jButton_Exit, new XYConstraints(5, 5, 120, 50));


    // Construct MenuFrame
    try
    {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    mFrame = new MenuFrame(this);
    mFrame.setVisible(false);



    if (Setup.m_ResearchMode)
    {
       positionFrame(mFrame, 97, 97, Setup.m_DisplayMode);

       // If research mode, set StartFrame visible

       if (Setup.m_DisplayMode.equals("LowerHalf"))
         mFrame.setVisible(true);
       else
         this.setVisible(true);
    }
    else
    {
       positionFrame(mFrame, 100, 100, Setup.m_DisplayMode);

       // StartFile checked: if line Print=.. contains names of files:
       // *.jpg-files are constructed from the images
       if (mFrame.FindFilesToPrint())
       {
         Setup.m_PrintMode = true;
         mFrame.JPG_Construction();
         mFrame.Exit(mFrame); // program termination
       }

       // If operational mode: construct default startup windows
       mFrame.CheckExit(); // Error message if "exit" in StartFile already at program start
       File startFile = mFrame.FindStartFile();
       mFrame.OperationalStartup(startFile);
       FileUpdate fupd = new FileUpdate(mFrame, startFile);

       // Without the following, under Unix the start frames don't pop up
       if (!(System.getProperty("os.name").substring(0,3).equals("Win")))
         mFrame.SideASide(true);

       // mFrame.setVisible(true): in OperationalStartup,
       // otherwise display problems under Unix
    }

    }
    catch(Exception e1)
    {
    }

    // System.out.println("StartFrame: initialization finished");

  }


  static void positionFrame(Frame frame, int width, int height, String DisplayMode)
  // Puts the frame in a central position on the screen
  // width, height: relative width and height of frame in % (0...100)
  // if DisplayMode = "FullScreen": full screen used
  //                = "RightHalf": right half only is used
  //                = "LowerHalf": lower half only is used
  {
    boolean packFrame = false;
    //Frames validieren, die eine voreingestellte Gr\uFFFD\uFFFDe besitzen
    //Frames packen, die n\uFFFDtzliche bevorzugte Infos \uFFFDber die Gr\uFFFD\uFFFDe besitzen, z.B. aus ihrem Layout
    if (packFrame)
    {
      frame.pack();
    }
    else
    {
      frame.validate();
    }

    // Get size of total screen
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Get relative and absolute frame size
    Dimension relativeFrameSize = new Dimension() ; // Size of frame relative to screen (in %)
    relativeFrameSize.width = width;
    relativeFrameSize.height = height;

    Dimension frameSize = new Dimension(); // Absolute frame size
    frameSize.height = (int) (screenSize.height * relativeFrameSize.height / 100);
    frameSize.width = (int) (screenSize.width * relativeFrameSize.width / 100);

    // Maximum frame size = screen size
    if (frameSize.height > screenSize.height)
       frameSize.height = screenSize.height;
    if (frameSize.width > screenSize.width)
       frameSize.width = screenSize.width;

    // Set frame size
    if (DisplayMode.equals("RightHalf"))
      frameSize.width = frameSize.width / 2;
    if (DisplayMode.equals("LowerHalf")) frameSize.height = frameSize.height / 2;
    frame.setSize(frameSize);

    // Set frame location (upper left corner of frame)
    // Frame is centered within screen

    int left = (screenSize.width - frameSize.width) / 2;
    if (DisplayMode.equals("RightHalf")) left = left + screenSize.width / 4;
    int upper = (screenSize.height - frameSize.height) / 2;
    if (DisplayMode.equals("LowerHalf")) upper = upper + screenSize.height / 4;
    frame.setLocation(left, upper);
  }


  void jButton_Run_actionPerformed(ActionEvent e)
  {
    if (Setup.modelRunning == true)
    {
      MessageBox mBox = new MessageBox(mFrame, "Note",
            "SNOWPACK model currently running.", "Wait until termination, then try again!");
            mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
            mBox.setVisible(true);
      return;
    }

    // Ask if the user wants to read the input data for the model run from
    // the CONSTANTS.INI file or from a specific file which he can choose.
    YesNoDialog ynd = new YesNoDialog(mFrame, "Parameters for SNOWPACK run",
     "Do you want to read the default parameters for the",
     "SNOWPACK run from default file or from user-defined file?",
     "Default File", "User-def. File", true);
    ynd.setLocation(mFrame.DialogCorner(ynd, mFrame));
    ynd.setVisible(true);

    String userInputFile = Setup.m_IniFilePath + "CONSTANTS.INI";

    if (ynd.action2)
    // User-defined File: default data for SNOWPACK model should be read
    // from a file defined by the user. This file must have a format similar
    // to that of CONSTANTS.INI.
    {
      Setup.m_FileDialogActive = true;

      FileDialog fileDialog = new FileDialog
        (this, "Choice of Input File for SNOWPACK parameters", FileDialog.LOAD);
      //fileDialog.setFile("cover.soil0"); // default file
      fileDialog.setLocation(mFrame.DialogCorner(fileDialog, this));
      fileDialog.setVisible(true);

      userInputFile = fileDialog.getDirectory() + fileDialog.getFile();
      File file = new File(userInputFile);
      // String FileName = fileDialog.getDirectory() + fileDialog.getFile();
      // String Path = file.getPath(); = path + name
      // String Name = file.getName(); = name

      Setup.m_FileDialogActive = false;

      if (!file.exists())
      {
         if(!file.getName().startsWith("null")) // Cancel not pressed (else file="nullnull")
         {
           MessageBox mBox = new MessageBox(this, "Error", "File not found:",
              file.getPath());
           mBox.setLocation(mFrame.DialogCorner(mBox, this)); mBox.setVisible(true);
         }

         return;
      }

    }


    if ((ynd.action1) || (ynd.action2))
    {

      ModelDialog md;
      md = new ModelDialog(mFrame,
                           "SN_GUI: Model Settings",
                           userInputFile, true);

      if (md.reading_ok == true) {
        md.setLocation(mFrame.DialogCorner(md, mFrame));
        md.setVisible(true);

        // Start the model execution
        if (md.let_run) {
          File outfile = new File(md.MODEL_DIRECTORY + "/" + md.OUTPATH + "/"
                                  + md.RESEARCH_STATION + "_" + md.EXPERIMENT +
                                  ".pro");

          modelRun = new ModelRun(mFrame, md.MODEL_DIRECTORY, outfile);
        }
      }
    }
  }


  void jButton_StopRun_actionPerformed(ActionEvent e)
  {
     if (modelRun == null)
     {
       MessageBox mBox = new MessageBox(mFrame, "Note",
              "Nothing to do.", "SNOWPACK model has not been started yet!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);
     }
     else if (!modelRun.runThread.isAlive())
     {
       MessageBox mBox = new MessageBox(mFrame, "Note",
              "Nothing to do.", "SNOWPACK model currently not executed!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);
     }
     else
     {
       MessageBox mBox = new MessageBox(mFrame, "Note",
              "Not implemented yet.", "Take it easy!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);
       /*
       modelRun.runThread.stop(); //destroy();
       MessageBox mBox = new MessageBox(mFrame, "Note",
              "SNOWPACK model execution interrupted", "before completion!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);

       Setup.modelRunning = false;
       */
     }

  }

  void jButton_ModelLogFile_actionPerformed(ActionEvent e)
  {
      if (modelRun == null)
      {
         MessageBox mBox = new MessageBox(mFrame, "Note",
              "Nothing to do.", "SNOWPACK model has not been started yet!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);
      }
      else
      {
         modelRun.ModelLogFileDisplay(false);
      }
  }

  void jButton_Visualize_actionPerformed(ActionEvent e)
  {
    mFrame.setVisible(true);
  }

  void jButton_Exit_actionPerformed(ActionEvent e)
  {
    mFrame.Exit(mFrame);
/*
    if (Setup.modelRunning)
    {
      MessageBox mBox = new MessageBox(mFrame,
         "Note", "SNOWPACK model still running.",
         "Output can be displayed after restart of SN_GUI!");
      mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
    }

    System.exit(0);
*/
  }

  void this_windowClosing(WindowEvent e)
  {
    //mFrame.closeWindow(); // Close MenuFrame, then exit from StartFrame
    if (Setup.modelRunning)
    {
      MessageBox mBox = new MessageBox(mFrame,
         "Note", "SNOWPACK model still running.", "Output can be displayed after restart of SN_GUI!");
      mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
    }

    System.exit(0);
  }

  void jButton_Info_actionPerformed(ActionEvent e)
  {
    mFrame.menu_About_actionPerformed();

    MessageBox mBox = new MessageBox(mFrame, "Note",
              "Detailed infos about the SNOWPACK model","not yet available at this place.");
    mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
    mBox.setVisible(true);
  }

  void jButton_InputFile_actionPerformed(ActionEvent e)
  {
    // Ask the user if snow and soil input data should be read from file
    YesNoDialog ynd = new YesNoDialog(mFrame, "Soil and Snow Input Data",
     "Do you want to create a new file", "or to edit an existing one?",
     "Create New", "Edit File", true);
    ynd.setLocation(mFrame.DialogCorner(ynd, mFrame));
    ynd.setVisible(true);

    String fileName = "";

    if (ynd.action2) // Create New: default data for SnowInputDialog should be read from file
    {
      Setup.m_FileDialogActive = true;

      FileDialog fileDialog = new FileDialog
        (this, "Choice of Snow/Soil Input File", FileDialog.LOAD);
      //fileDialog.setFile("cover.soil0"); // default file
      fileDialog.setLocation(mFrame.DialogCorner(fileDialog, this));
      fileDialog.setVisible(true);

      fileName = fileDialog.getDirectory() + fileDialog.getFile();
      File file = new File(fileName);
      // String FileName = fileDialog.getDirectory() + fileDialog.getFile();
      // String Path = file.getPath(); = path + name
      // String Name = file.getName(); = name

      Setup.m_FileDialogActive = false;

      if (!file.exists())
      {
         if(!file.getName().startsWith("null")) // Cancel not pressed (else file="nullnull")
         {
           MessageBox mBox = new MessageBox(this, "Error", "File not found:",
              file.getPath());
           mBox.setLocation(mFrame.DialogCorner(mBox, this)); mBox.setVisible(true);
         }
         return;
      }

    }

    if ((ynd.action1) || (ynd.action2))
    {
      SnowInputDialog sid;
      sid = new SnowInputDialog(mFrame, "Snow and Soil Input File Creation",
                                fileName, true);

      sid.setLocation(mFrame.DialogCorner(sid, mFrame));
      sid.setVisible(true);
    }
  }

}


