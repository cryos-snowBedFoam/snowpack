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
// MenuFrame: Handles menu bar, symbol bar and status bar
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.layout.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
import java.util.*;


public class MenuFrame extends DecoratedFrame implements C_DataFile
{
  MenuBar menuBar = new MenuBar();
  Menu menu_File = new Menu();
  static StatusBar statusBar = new StatusBar();
  BorderLayout borderLayout1 = new BorderLayout();
  JDesktopPane desktopPane = new JDesktopPane();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JButton jButton_Open = new JButton();
  JButton jButton_JPG = new JButton();
  JToggleButton jToggleButton_Soil = new JToggleButton();
  JButton jButton_Colors = new JButton();
  JButton jButton_yZoomDec = new JButton();
  JButton jButton_yZoomInc = new JButton();
  MenuItem menu_Open = new MenuItem();
  MenuItem menu_Close = new MenuItem();
  MenuItem menu_CloseAll = new MenuItem();
  MenuItem menu_Print = new MenuItem();
  MenuItem menu_CreateJPG = new MenuItem();
  MenuItem menu_CreatePostscript = new MenuItem();
  MenuItem menu_ErrorFile = new MenuItem();
  MenuItem menu_Memory = new MenuItem();
  MenuItem menu_Seperator = new MenuItem("-");
  MenuItem menu_Exit = new MenuItem();
  Menu menu_Display = new Menu();
  MenuItem menu_ValueRangeX = new MenuItem();
  MenuItem menu_ValueRangeY = new MenuItem();
  MenuItem menu_IncrZoomY = new MenuItem();
  MenuItem menu_DecrZoomY = new MenuItem();
  MenuItem menu_MoveYHigher = new MenuItem();
  MenuItem menu_MoveYLower = new MenuItem();
  MenuItem menu_ColorTable = new MenuItem();
  Menu menu_Backgrnd = new Menu();
  MenuItem menu_BackgrndWhite = new MenuItem();
  MenuItem menu_BackgrndGray = new MenuItem();
  MenuItem menu_BackgrndBlack = new MenuItem();
  Menu menu_Station = new Menu();
  CheckboxMenuItem chckbxmenu_SoilData = new CheckboxMenuItem();
  CheckboxMenuItem chckbxmenu_SpeedBar = new CheckboxMenuItem();
  CheckboxMenuItem chckbxmenu_DrawXYPlot = new CheckboxMenuItem();
  CheckboxMenuItem chckbxmenu_Slider = new CheckboxMenuItem();
  CheckboxMenuItem chckbxmenu_StatusBar = new CheckboxMenuItem();
  CheckboxMenuItem chckbxmenu_ResearchMenu = new CheckboxMenuItem();
  Menu menu_Time = new Menu();
  MenuItem menu_3Days = new MenuItem();
  MenuItem menu_7Days = new MenuItem();
  MenuItem menu_14Days = new MenuItem();
  MenuItem menu_30Days = new MenuItem();
  MenuItem menu_60Days = new MenuItem();
  MenuItem menu_MaxTime = new MenuItem();
  MenuItem menu_OtherTime = new MenuItem();
  CheckboxMenuItem chckbxmenu_Synchronization = new CheckboxMenuItem();
  Menu menu_Window = new Menu();
  MenuItem menu_SideASide = new MenuItem();
  MenuItem menu_Pile = new MenuItem();
  Menu menu_Help = new Menu();
  MenuItem menu_Hlp = new MenuItem();
  MenuItem menu_About = new MenuItem();
  Component component1;
  Vector parMenu;
  Vector UsedFilesMenu = new Vector(8,2);
  Vector UsedWindowsMenu = new Vector(8,2);
  Vector UsedFramesMenu = new Vector(8,2);
  Vector FilesToPrint = new Vector(8,2);
  MenuItem menuItem1 = new MenuItem();
  JButton jButton_Home = new JButton();
  JButton jButton_End = new JButton();
  JButton jButton_Time = new JButton();
  JButton jButton_ySetup = new JButton();
  JButton jButton_xSetup = new JButton();
  JPanel jPanel1 = new JPanel();
  MenuItem menuItem2 = new MenuItem();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  GridLayout gridLayout3 = new GridLayout();
  MenuItem menu_run = new MenuItem();
  MenuItem menu_ModelInput = new MenuItem();
  StartFrame startFrame = null;
  MenuItem menu_start = new MenuItem();
  Menu menu_run1 = new Menu();
  MenuItem menu_ModelLogFile = new MenuItem();
  MenuItem menu_stopRun = new MenuItem();
  JPanel jPanel4 = new JPanel();
  JButton jButton_Play = new JButton();
  JButton jButton_Left = new JButton();
  JButton jButton_Right = new JButton();
  Menu menu_Animation = new Menu();
  MenuItem menu_Play = new MenuItem();
  MenuItem menu_AnimSettings = new MenuItem();
  JPanel jPanel6 = new JPanel();
  JToggleButton jToggleButton_Sync = new JToggleButton();
  JPanel jPanel13 = new JPanel();
  GridLayout gridLayout10 = new GridLayout();
  JPanel jPanel14 = new JPanel();
  GridLayout gridLayout4 = new GridLayout();
  GridLayout gridLayout5 = new GridLayout();
  GridLayout gridLayout6 = new GridLayout();
  GridLayout gridLayout7 = new GridLayout();
  JButton jButton_Close = new JButton();
  GridLayout gridLayout1 = new GridLayout();
  JPanel jPanel5 = new JPanel();
  JButton jButton_SideASide = new JButton();
  GridLayout gridLayout8 = new GridLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  String JPGfile = "";



  // Construct frame
  public MenuFrame(StartFrame startFrame)
  {
    this.startFrame = startFrame;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }


  //Initialisierung der Komponente
  private void jbInit() throws Exception
  {

    // Read parameters of SETUP.INI file
    if (!Setup.ReadSetupFile())
    {
        MessageBox mBox = new MessageBox(this, "Error", "File SETUP.INI not found or",
           "setup parameter input incorrect!");
        mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
    }
    ErrorFile.SetPath(Setup.m_ErrorFilePath);

    for (int i=0; i<8; i++) UsedFilesMenu.addElement(new MenuItem(""));
    for (int i=0; i<8; i++) UsedWindowsMenu.addElement(new MenuItem(""));
    for (int i=0; i<8; i++) UsedFramesMenu.addElement(null);

    // Disable some menu items
    if (!Setup.m_ResearchMode)
    {
       menu_ModelInput.setEnabled(false);
       menu_run.setEnabled(false);
       menu_start.setEnabled(false);
       menu_ModelLogFile.setEnabled(false);
       menu_stopRun.setEnabled(false);
    }


    component1 = Box.createHorizontalStrut(8);
    this.setLayout(borderLayout1);
    this.setTitle("SNOWPACK");
    this.addWindowListener(new java.awt.event.WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        this_windowClosing(e);
      }
    });
    statusBar.setText("Ready");

    jPanel7.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel7.setMinimumSize(new Dimension(655, 40));
    jPanel7.setPreferredSize(new Dimension(300, 35));
    jPanel7.setLayout(flowLayout1);
    jPanel8.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel8.setMinimumSize(new Dimension(80, 27));
    jPanel8.setPreferredSize(new Dimension(118, 32));
    jPanel8.setLayout(gridLayout1);
    jButton_Open.setBackground(Color.lightGray);
    jButton_Open.setMaximumSize(new Dimension(100, 55));
    jButton_Open.setPreferredSize(new Dimension(55, 28));
    jButton_Open.setToolTipText("Open a new file");
    jButton_Open.setMargin(new Insets(0, 0, 0, 0));
    jButton_Open.setText(" Open ");
    jButton_Open.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        // File choser box
        menu_Open_actionPerformed();

        /* Use this code for fast loading of preset files
        File file = new File(Setup.m_DataFilePath + "T1DVF1.met");
        NewFrame(file, ID_CODE_AIR_TEMPERATURE);

        //File file = new File(Setup.m_DataFilePath + "T1DVF1.pro");
        //NewFrame(file, ID_CODE_SNOWPACK_TEMPERATURE);
        */
      }
    });
    jButton_JPG.setBackground(Color.lightGray);
    jButton_JPG.setMaximumSize(new Dimension(100, 55));
    jButton_JPG.setMinimumSize(new Dimension(43, 23));
    jButton_JPG.setPreferredSize(new Dimension(55, 28));
    jButton_JPG.setToolTipText("Create JPG file enclosing all open frames");
    jButton_JPG.setMargin(new Insets(0, 0, 0, 0));
    jButton_JPG.setText("JPG");
    jButton_JPG.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_JPG_actionPerformed(e);
      }
    });
    jToggleButton_Soil.setToolTipText("Soil display switch (ON/OFF)");
    jToggleButton_Soil.setPreferredSize(new Dimension(55, 28));
    jToggleButton_Soil.setMaximumSize(new Dimension(100, 55));
    jToggleButton_Soil.setMargin(new Insets(0, 0, 0, 0));
    jToggleButton_Soil.setSelected(Setup.m_SoilDataDisplay);
    jToggleButton_Soil.setBackground(Color.lightGray);
    jToggleButton_Soil.setMinimumSize(new Dimension(43, 23));
    jToggleButton_Soil.setText("Soil");
    jToggleButton_Soil.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        chckbxmenu_SoilData_itemStateChanged();
      }
    });
    jButton_Colors.setBackground(Color.lightGray);
    jButton_Colors.setMaximumSize(new Dimension(100, 55));
    jButton_Colors.setPreferredSize(new Dimension(55, 28));
    jButton_Colors.setToolTipText("Choice of a color table");
    jButton_Colors.setMargin(new Insets(0, 0, 0, 0));
    jButton_Colors.setText("Colors");
    jButton_Colors.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ColorTable_actionPerformed();
      }
    });
    jButton_yZoomDec.setBackground(Color.lightGray);
    jButton_yZoomDec.setMaximumSize(new Dimension(100, 55));
    jButton_yZoomDec.setMinimumSize(new Dimension(80, 23));
    jButton_yZoomDec.setPreferredSize(new Dimension(80, 28));
    jButton_yZoomDec.setToolTipText("Decrease y-axis value range");
    jButton_yZoomDec.setMargin(new Insets(0, 0, 0, 0));
    jButton_yZoomDec.setText("-Zoom");
    jButton_yZoomDec.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
         menu_ChangeZoomY_actionPerformed("Decrease");
      }
    });
    jButton_yZoomInc.setBackground(Color.lightGray);
    jButton_yZoomInc.setMaximumSize(new Dimension(100, 55));
    jButton_yZoomInc.setMinimumSize(new Dimension(80, 23));
    jButton_yZoomInc.setPreferredSize(new Dimension(80, 28));
    jButton_yZoomInc.setToolTipText("Increase y-axis value range");
    jButton_yZoomInc.setMargin(new Insets(0, 0, 0, 0));
    jButton_yZoomInc.setText("+Zoom");
    jButton_yZoomInc.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
         menu_ChangeZoomY_actionPerformed("Increase");
      }
    });

    menu_File.setLabel("File");
    menu_Open.setLabel("Open ...");
    menu_Open.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Open_actionPerformed();
      }
    });
    menu_Close.setLabel("Close");
    menu_Close.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Close_actionPerformed();
      }
    });
    menu_CloseAll.setLabel("Close All");
    menu_CloseAll.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_CloseAll_actionPerformed();
      }
    });
    menu_Print.setLabel("Print ...");
    menu_Print.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Print_actionPerformed(e);
      }
    });
    menu_CreateJPG.setLabel("Create JPG");
    menu_CreateJPG.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        // no automated printing, no default output file
        menu_CreateJPG_actionPerformed(false, "");
      }
    });
    menu_CreatePostscript.setLabel("Create JPG + PS");
    menu_CreatePostscript.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_CreatePostscript_actionPerformed();
      }
    });

    menu_ErrorFile.setLabel("Display Reading Errors");
    menu_ErrorFile.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ErrorFile_actionPerformed(e);
      }
    });

    menu_Memory.setLabel("Memory Check");
    menu_Memory.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Memory_actionPerformed();
      }
    });


    menu_Exit.setLabel("Exit");
    menu_Exit.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Exit_actionPerformed(e);
      }
    });
    menu_Display.setLabel("Display");
    menu_IncrZoomY.setLabel("Zoom Y-Axis +");
    menu_IncrZoomY.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menu_ChangeZoomY_actionPerformed("Increase");
      }
    });
    menu_DecrZoomY.setLabel("Zoom Y-Axis -");
    menu_DecrZoomY.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ChangeZoomY_actionPerformed("Decrease");
      }
    });
    menu_MoveYHigher.setLabel("Y-Interval Up");
    menu_MoveYHigher.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ChangeZoomY_actionPerformed("Move Higher");
      }
    });
    menu_MoveYLower.setLabel("Y-Interval Down");
    menu_MoveYLower.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ChangeZoomY_actionPerformed("Move Lower");
      }
    });
    menu_ValueRangeY.setLabel("Value Range Y-Axis ...");
    menu_ValueRangeY.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ValueRangeY_actionPerformed();
      }
    });
    menu_ColorTable.setLabel("Color Table ...");
    menu_ColorTable.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ColorTable_actionPerformed();
      }
    });
    menu_Backgrnd.setLabel("Background");
    menu_BackgrndWhite.setLabel("White");
    menu_BackgrndWhite.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_BackgrndWhite_actionPerformed(e);
      }
    });
    menu_BackgrndGray.setLabel("Light Gray");
    menu_BackgrndGray.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_BackgrndGray_actionPerformed(e);
      }
    });
    menu_BackgrndBlack.setLabel("Black");
    menu_BackgrndBlack.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_BackgrndBlack_actionPerformed(e);
      }
    });
    menu_Station.setLabel("Station");
    // Station and parameter menus are created dynamically; see below
    menu_3Days.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_3Days_actionPerformed(e);
      }
    });
    menu_7Days.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_7Days_actionPerformed(e);
      }
    });
    menu_14Days.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_14Days_actionPerformed(e);
      }
    });
    menu_30Days.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_30Days_actionPerformed(e);
      }
    });
    menu_60Days.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_60Days_actionPerformed(e);
      }
    });
    menu_MaxTime.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_MaxTime_actionPerformed(e);
      }
    });
    menu_Hlp.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Hlp_actionPerformed(e);
      }
    });


    // Dynamic creation of the station menu
/* functional, but not used
    try
    {
    IniFile statList = new IniFile(Setup.m_IniFilePath + "STATLIST.INI");

    for (int i=0; i<statList.getSectionSize(); i++)
    {
      String kanton = statList.getSection(i); // e.g. "BE"
      Menu menu_Kanton = new Menu(kanton);
      menu_Station.add(menu_Kanton);
      statList.setSection(kanton);

      for (int j=0; j<statList.getKeySize(); j++)
      {
        String station = statList.getEntry(statList.getKey(j));
        MenuItem statItem = new MenuItem(station);
        menu_Kanton.add(statItem);

        statItem.addActionListener(new java.awt.event.ActionListener()
        {
           public void actionPerformed(ActionEvent e)
          {
            String station_label = (String) e.getActionCommand();
            statItem_actionPerformed(e, station_label);
          }
        });
       }
    }

    } //end try
    catch (IOException e)
    {
       System.out.println("Catch 1: " + e);
    }
*/

    // Dynamic creation of the parameter menus
    getMenuVector();

    chckbxmenu_SoilData.setLabel("Soil Data");
    chckbxmenu_SoilData.setState(Setup.m_SoilDataDisplay);
    chckbxmenu_SoilData.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_SoilData_itemStateChanged();
      }
    });

    chckbxmenu_DrawXYPlot.setLabel("Right-Hand Graph");
    chckbxmenu_DrawXYPlot.setState(Setup.m_DrawXYPlot);
    chckbxmenu_DrawXYPlot.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_DrawXYPlot_itemStateChanged(e);
      }
    });

    chckbxmenu_Slider.setLabel("Slider + Buttons");
    chckbxmenu_Slider.setState(Setup.m_SliderDisplay);
    chckbxmenu_Slider.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_Slider_itemStateChanged(e);
      }
    });

    chckbxmenu_SpeedBar.setLabel("Speed Bar");
    chckbxmenu_SpeedBar.setState(true);
    chckbxmenu_SpeedBar.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_SpeedBar_itemStateChanged(e);
      }
    });
    chckbxmenu_StatusBar.setLabel("Status Bar");
    chckbxmenu_StatusBar.setState(true);
    chckbxmenu_StatusBar.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_StatusBar_itemStateChanged(e);
      }
    });
    chckbxmenu_ResearchMenu.setLabel("Research Menu");
    if (Setup.m_ResearchMode)
      chckbxmenu_ResearchMenu.setState(true);
    else
      chckbxmenu_ResearchMenu.setState(false);
    chckbxmenu_ResearchMenu.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_ResearchMenu_itemStateChanged(e);
      }
    });
    menu_Time.setLabel("Time");
    menu_3Days.setLabel("3 Days");
    menu_7Days.setLabel("7 Days");
    menu_14Days.setLabel("14 Days");
    menu_30Days.setLabel("30 Days");
    menu_60Days.setLabel("60 Days");
    menu_MaxTime.setLabel("Maximum Time");
    menu_OtherTime.setLabel("Other Time ...");
    menu_OtherTime.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_OtherTime_actionPerformed();
      }
    });

    chckbxmenu_Synchronization.setLabel("Synchronization");
    chckbxmenu_Synchronization.setState(Setup.m_Synchronization);
    chckbxmenu_Synchronization.addItemListener(new java.awt.event.ItemListener()
    {

      public void itemStateChanged(ItemEvent e)
      {
        chckbxmenu_Synchronization_itemStateChanged();
      }
    });

    menu_Window.setLabel("Window");
    menu_SideASide.setLabel("Side a Side");
    menu_SideASide.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_SideASide_actionPerformed();

      }
    });
    menu_Pile.setLabel("Pile");
    menu_Pile.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Pile_actionPerformed(e);

      }
    });
    menu_Help.setLabel("Help");
    menu_Hlp.setLabel("Help");
    menu_Hlp.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Hlp_actionPerformed(e);
      }
    });

    menu_About.setLabel("About");
    menu_About.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_About_actionPerformed();
      }
    });

    this.setMenuBar(menuBar);
    jButton_Home.setBackground(Color.lightGray);
    jButton_Home.setMaximumSize(new Dimension(25, 23));
    jButton_Home.setMinimumSize(new Dimension(25, 23));
    jButton_Home.setPreferredSize(new Dimension(30, 28));
    jButton_Home.setToolTipText("Move to start or to previous time interval (= HOME-button)");
    jButton_Home.setMargin(new Insets(0, 0, 0, 0));
    jButton_Home.setText("<<");
    jButton_Home.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
           MarkerMovement("Home", 0);
      }
    });
    jButton_End.setBackground(Color.lightGray);
    jButton_End.setMaximumSize(new Dimension(25, 23));
    jButton_End.setMinimumSize(new Dimension(25, 23));
    jButton_End.setPreferredSize(new Dimension(30, 28));
    jButton_End.setToolTipText("Move to end or to next time interval (= END-button)");
    jButton_End.setMargin(new Insets(0, 0, 0, 0));
    jButton_End.setText(">>");
    jButton_End.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
           MarkerMovement("End", 0);
      }
    });
    jButton_Time.setBackground(Color.lightGray);
    jButton_Time.setMaximumSize(new Dimension(100, 55));
    jButton_Time.setMinimumSize(new Dimension(43, 23));
    jButton_Time.setPreferredSize(new Dimension(55, 28));
    jButton_Time.setToolTipText("Set time interval for data display");
    jButton_Time.setMargin(new Insets(0, 0, 0, 0));
    jButton_Time.setText("Time");
    jButton_Time.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_OtherTime_actionPerformed();
      }
    });
    jButton_ySetup.setBackground(Color.lightGray);
    jButton_ySetup.setMaximumSize(new Dimension(100, 55));
    jButton_ySetup.setMinimumSize(new Dimension(80, 23));
    jButton_ySetup.setPreferredSize(new Dimension(80, 28));
    jButton_ySetup.setToolTipText("Set the y-axis value range");
    jButton_ySetup.setMargin(new Insets(0, 0, 0, 0));
    jButton_ySetup.setText("yRange");
    jButton_ySetup.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ValueRangeY_actionPerformed();
      }
    });
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setMinimumSize(new Dimension(240, 27));
    jPanel1.setPreferredSize(new Dimension(236, 32));
    jPanel1.setLayout(gridLayout4);
    menu_ValueRangeX.setLabel("Value Range X-Axis ...");
    menu_ValueRangeX.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ValueRangeX_actionPerformed(e);
      }
    });
    jButton_xSetup.setBackground(Color.lightGray);
    jButton_xSetup.setMaximumSize(new Dimension(100, 55));
    jButton_xSetup.setMinimumSize(new Dimension(80, 23));
    jButton_xSetup.setPreferredSize(new Dimension(80, 28));
    jButton_xSetup.setToolTipText("Set the x-axis value range (right-side graph)");
    jButton_xSetup.setMargin(new Insets(0, 0, 0, 0));
    jButton_xSetup.setText("xRange");
    jButton_xSetup.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_ValueRangeX_actionPerformed(e);
      }
    });

    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setMinimumSize(new Dimension(40, 27));
    jPanel2.setPreferredSize(new Dimension(59, 32));
    jPanel2.setLayout(gridLayout2);
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setMinimumSize(new Dimension(40, 27));
    jPanel3.setLayout(gridLayout3);

    menu_ModelInput.setLabel("Create Input File");
    menu_ModelInput.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        startFrame.jButton_InputFile_actionPerformed(e);
      }
    });

    menu_run.setLabel("Model Settings"); //Schirmer
    menu_run.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        startFrame.jButton_Run_actionPerformed(e);
      }
    });
    menu_start.setLabel("Goto Start Frame");
    menu_start.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        //this.setVisible(false);
        startFrame.setVisible(true);
      }
    });
    menu_run1.setLabel("Model");
    menu_ModelLogFile.setLabel("Model Log File");
    menu_ModelLogFile.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        //menu_ErrorFile_actionPerformed(e);
        menu_ModelLogFile_actionPerformed(e);
      }
    });
    menu_stopRun.setLabel("Stop Execution");
    menu_stopRun.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_stopRun_actionPerformed(e);
      }
    });
    jPanel4.setLayout(gridLayout6);
    jButton_Play.setBackground(Color.lightGray);
    jButton_Play.setMaximumSize(new Dimension(100, 55));
    jButton_Play.setMinimumSize(new Dimension(43, 23));
    jButton_Play.setPreferredSize(new Dimension(55, 28));
    jButton_Play.setToolTipText("Time-lapse of data records, using Settings (Menu Animation)");
    jButton_Play.setMargin(new Insets(0, 0, 0, 0));
    jButton_Play.setText("Play");
    jButton_Play.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Play_actionPerformed();
      }
    });
    jButton_Left.setBackground(Color.lightGray);
    jButton_Left.setMaximumSize(new Dimension(25, 23));
    jButton_Left.setMinimumSize(new Dimension(25, 23));
    jButton_Left.setPreferredSize(new Dimension(30, 28));
    jButton_Left.setToolTipText("Move to previous date (= PageUp-button)");
    jButton_Left.setMargin(new Insets(0, 0, 0, 0));
    jButton_Left.setText("<");
    jButton_Left.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
          MarkerMovement("Left", 0);
      }
    });
    jButton_Right.setBackground(Color.lightGray);
    jButton_Right.setMaximumSize(new Dimension(25, 23));
    jButton_Right.setMinimumSize(new Dimension(25, 23));
    jButton_Right.setPreferredSize(new Dimension(30, 28));
    jButton_Right.setToolTipText("Move to following date (= PageDown-button)");
    jButton_Right.setMargin(new Insets(0, 0, 0, 0));
    jButton_Right.setText(">");
    jButton_Right.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
          MarkerMovement("Right", 0);
      }
    });
    menu_Animation.setLabel("Animation");
    menu_Play.setLabel("Play");
    menu_Play.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_Play_actionPerformed();
      }
    });
    menu_AnimSettings.setLabel("Settings ...");
    menu_AnimSettings.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        menu_AnimSettings_actionPerformed(e);
      }
    });
    jPanel6.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel6.setMaximumSize(new Dimension(278, 31));
    jPanel6.setMinimumSize(new Dimension(200, 27));
    jPanel6.setPreferredSize(new Dimension(300, 32));
    jPanel6.setLayout(gridLayout7);
    jToggleButton_Sync.setMargin(new Insets(0, 0, 0, 0));
    jToggleButton_Sync.setToolTipText("Synchronization mode switch (ON/OFF)");
    jToggleButton_Sync.setPreferredSize(new Dimension(55, 28));
    jToggleButton_Sync.setMaximumSize(new Dimension(100, 55));
    jToggleButton_Sync.setSelected(Setup.m_Synchronization);
    jToggleButton_Sync.setBackground(Color.lightGray);
    jToggleButton_Sync.setMinimumSize(new Dimension(43, 23));
    jToggleButton_Sync.setText("Sync");
    jToggleButton_Sync.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        chckbxmenu_Synchronization_itemStateChanged();
      }
    });
    jPanel13.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel13.setMinimumSize(new Dimension(40, 27));
    jPanel13.setPreferredSize(new Dimension(59, 32));
    jPanel13.setLayout(gridLayout10);

    jPanel14.setMinimumSize(new Dimension(100, 27));
    jPanel14.setPreferredSize(new Dimension(177, 32));
    jPanel14.setLayout(gridLayout5);
    jButton_Close.setBackground(Color.lightGray);
    jButton_Close.setMaximumSize(new Dimension(100, 55));
    jButton_Close.setMinimumSize(new Dimension(43, 23));
    jButton_Close.setPreferredSize(new Dimension(55, 28));
    jButton_Close.setToolTipText("Close the currently active frame");
    jButton_Close.setMargin(new Insets(0, 0, 0, 0));
    jButton_Close.setText("Close");
    jButton_Close.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Close_actionPerformed();
      }
    });
    jPanel5.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel5.setMinimumSize(new Dimension(40, 27));
    jPanel5.setLayout(gridLayout8);
    jButton_SideASide.setBackground(Color.lightGray);
    jButton_SideASide.setMaximumSize(new Dimension(100, 55));
    jButton_SideASide.setMinimumSize(new Dimension(43, 23));
    jButton_SideASide.setPreferredSize(new Dimension(55, 28));
    jButton_SideASide.setToolTipText("Draw loaded frames side a side");
    jButton_SideASide.setMargin(new Insets(0, 0, 0, 0));
    jButton_SideASide.setText("SiASi");
    jButton_SideASide.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_SideASide_actionPerformed();
      }
    });
    jPanel4.setMinimumSize(new Dimension(100, 27));
    jPanel4.setPreferredSize(new Dimension(123, 32));
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(3);
    flowLayout1.setVgap(0);
    this.add(statusBar, BorderLayout.SOUTH);
    this.add(desktopPane, BorderLayout.CENTER);
    this.add(jPanel7, BorderLayout.NORTH);
    jPanel7.add(jPanel8, null);
    jPanel8.add(jButton_Open, null);
    jPanel8.add(jButton_Close, null);
    jPanel7.add(jPanel13, null);
    jPanel13.add(jButton_JPG, null);
    jPanel7.add(jPanel3, null);
    jPanel3.add(jButton_Colors, null);
    jPanel7.add(jPanel1, null);
    jPanel1.add(jButton_xSetup, null);
    jPanel1.add(jButton_ySetup, null);
    jPanel1.add(jButton_yZoomDec, null);
    jPanel1.add(jButton_yZoomInc, null);
    jPanel7.add(jPanel6, null);
    jPanel6.add(jPanel14, null);
    jPanel14.add(jToggleButton_Sync, null);
    jPanel14.add(jButton_Time, null);
    jPanel14.add(jButton_Play, null);
    jPanel6.add(jPanel4, null);
    jPanel4.add(jButton_Home, null);
    jPanel4.add(jButton_Left, null);
    jPanel4.add(jButton_Right, null);
    jPanel4.add(jButton_End, null);
    jPanel7.add(jPanel5, null);
    jPanel5.add(jButton_SideASide, null);
    jPanel7.add(jPanel2, null);
    jPanel2.add(jToggleButton_Soil, null);

    desktopPane.putClientProperty("JDesktopPane.dragMode","outline");
    // when dragged just outlines of the frames are repainted


    // *** Creation of the menu bar ***
    menu_File.add(menu_Open);
    menu_File.add(menu_Close);
    menu_File.add(menu_CloseAll);
    menu_File.addSeparator();
    menu_File.add(menu_Print);
    menu_File.add(menu_CreateJPG);
    menu_File.add(menu_CreatePostscript);
    menu_File.addSeparator();
    //menu_File.add(menu_ErrorFile);  not used
    //menu_File.addSeparator();
    menu_File.add(menu_Memory);
    menu_File.addSeparator();
    menu_File.add(menu_start);
    menu_File.addSeparator();
    menu_File.add(menu_Exit);
    menu_run1.add(menu_ModelInput);
    menu_run1.add(menu_run);
    /*Schirmer
    menu_run1.add(menu_stopRun);
    menu_run1.add(menu_ModelLogFile);*/
    menu_Display.add(menu_ValueRangeX);
    menu_Display.add(menu_ValueRangeY);
    menu_Display.add(menu_IncrZoomY);
    menu_Display.add(menu_DecrZoomY);
    menu_Display.add(menu_MoveYHigher);
    menu_Display.add(menu_MoveYLower);
    menu_Display.addSeparator();
    menu_Display.add(menu_ColorTable);
    menu_Display.addSeparator();
    menu_Display.add(menu_Backgrnd);
    menu_Backgrnd.add(menu_BackgrndWhite);
    menu_Backgrnd.add(menu_BackgrndGray);
    menu_Backgrnd.add(menu_BackgrndBlack);
    menu_Display.addSeparator();
    menu_Display.add(chckbxmenu_SoilData);
    menu_Display.addSeparator();
    menu_Display.add(chckbxmenu_DrawXYPlot);
    menu_Display.add(chckbxmenu_Slider);
    menu_Display.add(chckbxmenu_SpeedBar);
    menu_Display.add(chckbxmenu_StatusBar);
    menu_Display.add(chckbxmenu_ResearchMenu);
    menu_Time.add(menu_3Days);
    menu_Time.add(menu_7Days);
    menu_Time.add(menu_14Days);
    menu_Time.add(menu_30Days);
    menu_Time.add(menu_60Days);
    menu_Time.add(menu_MaxTime);
    menu_Time.add(menu_OtherTime);
    menu_Time.addSeparator();
    menu_Time.add(chckbxmenu_Synchronization);
    menu_Window.add(menu_SideASide);
    menu_Window.add(menu_Pile);
    menu_Help.add(menu_Hlp);
    menu_Help.addSeparator();
    menu_Help.add(menu_About);
    menu_Animation.add(menu_Play);
    menu_Animation.add(menu_AnimSettings);

    menuBar.add(menu_File);
    menuBar.add(menu_run1);
    menuBar.add(menu_Display);

    // ** Automatic construction of station menu still works.
    // ** Display of station menu currently not desired.
    //if (!Setup.m_ResearchMode)
    //  menuBar.add(menu_Station); // add station menu just if not research mode

    Enumeration parEnum2 = parMenu.elements();
    while (parEnum2.hasMoreElements())
       menuBar.add((Menu) parEnum2.nextElement());

    menuBar.add(menu_Time);
    menuBar.add(menu_Animation);
    menuBar.add(menu_Window);
    menuBar.add(menu_Help);

    // Disable some menu items (state: no open frames)
    Menu_AnyOpenFrames(false);


    // *** Reaction to key pressed events ***

    addKeyListener(new java.awt.event.KeyListener()
    {
      public void keyPressed(KeyEvent ke)
      // Method not platform independent. Especially arrow-keys were found
      // not to work in specific situations, or just after any action listener
      // function was processed (reason unknown).
      {
         // System.out.println("MenuFrame: key pressed");

         int key = ke.getKeyCode();
         //if (ke.isActionKey()) System.out.println("   is action key");
         //else                   System.out.println("   no action key");

         if ((key == KeyEvent.VK_LEFT) || (key == KeyEvent.VK_PAGE_UP)
                                       || (key == KeyEvent.VK_B))
             MarkerMovement("Left", 0);
         else if ((key == KeyEvent.VK_RIGHT) || (key == KeyEvent.VK_PAGE_DOWN)
                                             || (key == KeyEvent.VK_N))
             MarkerMovement("Right", 0);
         else if (key == KeyEvent.VK_HOME)
             MarkerMovement("Home", 0);
         else if (key == KeyEvent.VK_END)
             MarkerMovement("End", 0);
         // The right and the left arrow keys were found not to work at a number
         // of systems.

         return;
      }

      public void keyTyped(KeyEvent ke)
      // Only this method is said to be platform-independent, but it just refers
      // to characters, not to arrow-buttons etc.
      // Does not work properly. Keys not recognized??
      {
         // System.out.println("MenuFrame: key pressed");

         SnowPackFrame spframe = getActiveFrame(desktopPane);
           if (spframe == null) return;

         int key = ke.getKeyCode();

         if      (key == KeyEvent.VK_B) MarkerMovement("Left", 0);
         else if (key == KeyEvent.VK_N) MarkerMovement("Right", 0);
         else if (key == KeyEvent.VK_V) MarkerMovement("Home", 0);
         else if (key == KeyEvent.VK_M) MarkerMovement("End", 0);
      }



      public void keyReleased(KeyEvent ke) {}

    });

    requestFocus(); // request input focus

  } // end jbinit()


/*
  // File exit
  public void fileExit_actionPerformed(ActionEvent e)
  {
    System.exit(0);
  }
*/

  void getMenuVector()
  {
    // Dynamic creation of the research parameter menus

    parMenu = new Vector(5,2); // will be filled with menus to be added to the menu bar

    try
    {
    IniFile parameter = new IniFile(Setup.m_IniFilePath + Setup.m_ParameterMenuFile);
    Menu menu1 = new Menu();

    for (int i=0; i<parameter.getSectionSize(); i++)
    {
      String bracketString = parameter.getSection(i); // String within brackets

      if (bracketString.startsWith("MENU"))
      // Has a form like [MENU xxx]. "xxx" is to be drawn on the main menu bar.
      {
         String menu1_label = bracketString.substring(
               "MENU".length() + 1, bracketString.length());
         // Label: substring to the right of "MENU "
         menu1 = new Menu(menu1_label);
         parMenu.addElement(menu1);
      }
      else if (bracketString.indexOf("=") != -1)
      // String contains "=". There is no other submenu below.
      {
         String menu2_label = bracketString.substring(
                 bracketString.indexOf("=") + 1, bracketString.length());
         // Label: string to the right of "="
         MenuItem menu2 = new MenuItem(menu2_label);
         menu1.add(menu2);

         menu2.addActionListener(new java.awt.event.ActionListener()
         {
           public void actionPerformed(ActionEvent e)
           {
             String menu2_label = (String) e.getActionCommand();
             parameter_actionPerformed(e, menu2_label);
           }
         });
      }
      else
      // String does not contain "=". It is the base for another submenu.
      {
         Menu menu2 = new Menu(bracketString);
         menu1.add(menu2);

         parameter.setSection(bracketString);

         for (int j=0; j<parameter.getKeySize(); j++)
         // Use information of the lines following the strings enclosed in brackets
         {
           String menu3_label = parameter.getEntry(parameter.getKey(j)); // right of "="
           MenuItem menu3 = new MenuItem(menu3_label);
           menu2.add(menu3);

           menu3.addActionListener(new java.awt.event.ActionListener()
           {
              public void actionPerformed(ActionEvent e)
             {
               String menu3_label = (String) e.getActionCommand();
               parameter_actionPerformed(e, menu3_label);
             }
           });
          }
        } // end if
    } // end for

    } //end try
    catch (IOException e)
    {
       System.out.println("MenuFrame.jbinit: Catch 1: " + e);
    }
  }

  // Status Bar on/off
  void chckbxmenu_StatusBar_itemStateChanged(ItemEvent e)
  {
    if (e.getStateChange() == e.SELECTED)
      this.add(statusBar, BorderLayout.SOUTH);
    else
      this.remove(statusBar);
    this.repaint();
  }

  // Switch between operational and research menu
  void chckbxmenu_ResearchMenu_itemStateChanged(ItemEvent e)
  {
    Enumeration parEnum;
    String changedMenuFile;

    // Necessary to avaoid flickering of the screen when menus are added/removed
    this.setVisible(false);

    if (e.getStateChange() == e.SELECTED)
      // Remove operational menu, add research menu
      changedMenuFile = "PARMENU1.INI";
    else
      // Remove research menu, add operational menu
      changedMenuFile = "PARMENU2.INI";

    // Remove items behind parameter menu
    menuBar.remove(menu_Time);
    menuBar.remove(menu_Animation);
    menuBar.remove(menu_Window);
    menuBar.remove(menu_Help);

    // Remove parameter menu items
    parEnum = parMenu.elements();
    while (parEnum.hasMoreElements())
      menuBar.remove((Menu) parEnum.nextElement());

    // Change the parameter menu file and read the new menu vector
    Setup.m_ParameterMenuFile = changedMenuFile;
    getMenuVector();

    // Add the new parameter menu to the menu bar
    parEnum = parMenu.elements();
    while (parEnum.hasMoreElements())
      menuBar.add((Menu) parEnum.nextElement());

    // Add items again behind parameter menu
    menuBar.add(menu_Time);
    menuBar.add(menu_Animation);
    menuBar.add(menu_Window);
    menuBar.add(menu_Help);

    // Disable some menu items if no more frames are present
    JInternalFrame iframes[] = desktopPane.getAllFrames();
    if (iframes.length < 1) Menu_AnyOpenFrames(false);

    this.setVisible(true);
  }


  // Soil Data on/off
  void chckbxmenu_SoilData_itemStateChanged()
  {
    Setup.m_SoilDataDisplay = !Setup.m_SoilDataDisplay;

    // Resetting the state of the related menu item
    // (just necessary when called by clicking the speed button)
    chckbxmenu_SoilData.setState(Setup.m_SoilDataDisplay);
    jToggleButton_Soil.setSelected(Setup.m_SoilDataDisplay);

    JInternalFrame iframes[] = desktopPane.getAllFrames();

    for (int i = 0; i < iframes.length; i++)
    {
        SnowPackFrame spframe = (SnowPackFrame) iframes[i];
        spframe.spDoc.GetYAxisRange(spframe.IdCode);
        spframe.repaint();
    }
  }


  // Right-hand graph (XY-Plot) on/off
  void chckbxmenu_DrawXYPlot_itemStateChanged(ItemEvent e)
  {
    Setup.m_DrawXYPlot = !Setup.m_DrawXYPlot;
    boolean CurrentSliderDisplay = Setup.m_SliderDisplay;

    JInternalFrame iframes[] = desktopPane.getAllFrames();

    for (int i = 0; i < iframes.length; i++)
    {
        SnowPackFrame spframe = (SnowPackFrame) iframes[i];

        if (Setup.m_DrawXYPlot)
          spframe.adjustSplitPaneDivider((float) 0.25);
        else
        {
          spframe.adjustSplitPaneDivider((float) 0.0);

          // Remove slider
          if (CurrentSliderDisplay)
          {
            if (i == 0)
            {
              Setup.m_SliderDisplay = false;
              chckbxmenu_Slider.setState(false);

              MessageBox mBox = new MessageBox(this, "Note",
               "Display of slider is disabled!", "(Menu Display - Slider)");
              mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
            }

            spframe.getSnowPackView().removeAll();
          }
        }

    }

    // Necessary to visualize the change; repaint() does not do this:
    SideASide(true);
  }


  // Slider on/off
  void chckbxmenu_Slider_itemStateChanged(ItemEvent e)
  {
    if (e.getStateChange() == e.SELECTED)
    // Slider display was set to on
    {
      Setup.m_SliderDisplay = true;

      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return;

      for (int i = 0; i < iframes.length; i++)
      {
          ((SnowPackFrame) iframes[i]).getSnowPackView().InsertControlObjects();
      }
    }
    else
    // Slider display was shut off
    {
      Setup.m_SliderDisplay = false;

      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return;

      for (int i = 0; i < iframes.length; i++)
      {
          ((SnowPackFrame) iframes[i]).getSnowPackView().removeAll();
      }
    }

    repaint();
  }


  // Speed Bar on/off
  void chckbxmenu_SpeedBar_itemStateChanged(ItemEvent e)
  {
    if (e.getStateChange() == e.SELECTED)
    {
      //this.add(statusBar, BorderLayout.SOUTH);
      //this.add(splitPanel1, BorderLayout.CENTER);
      this.add(jPanel7, BorderLayout.NORTH);
    }
    else
    {
      //this.remove(statusBar);
      //this.remove(splitPanel1);
      this.remove(jPanel7);
      //this.add(statusBar, BorderLayout.SOUTH);
      //this.add(splitPanel1, BorderLayout.CENTER);
    }
    this.repaint();
  }

  // Synchronization on/off
  void chckbxmenu_Synchronization_itemStateChanged()
  {
    if (Setup.m_Synchronization)
    // Synchronization was on before clicking
    {
       Setup.m_Synchronization = false;
       chckbxmenu_Synchronization.setState(false);
       jToggleButton_Sync.setSelected(false);
    }
    else
    // Synchronization was off before clicking
    {
       if (OneFile())
       {
         // Currently active SnowPackFrames all stem from one file.
         Setup.m_Synchronization = true;
         chckbxmenu_Synchronization.setState(true);
         jToggleButton_Sync.setSelected(true);

         // Get active frame and all frames of desktopPane
         SnowPackFrame spframe0 = getActiveFrame(desktopPane);
         if (spframe0 == null) return;
         JInternalFrame iframes[] = desktopPane.getAllFrames();

         // Set the Y-range of all *.pro-frames to that of the first found
         // *.pro-frame
         float YMin = (float) 0.0;
         float YMax = (float) 400.0;
         int yNr = 4;
         boolean first = true;

         if (spframe0.IdCode >=500) // act. frame is *.pro, take Y-data from there
         {
            YMin = spframe0.spDoc.m_YMinValue;
            YMax = spframe0.spDoc.m_YMaxValue;
            yNr  = spframe0.spDoc.m_yNrOfGrids;
            first = false;
         }

         for (int i = 0; i < iframes.length; i++)
         {
            SnowPackFrame spframe = (SnowPackFrame) iframes[i];
            if (spframe.IdCode >= 500)
            {
                if (first)
                  // active frame is *.met,
                  // take Y-data from first found *.pro-frame
                {
                  YMin = spframe.spDoc.m_YMinValue;
                  YMax = spframe.spDoc.m_YMaxValue;
                  yNr  = spframe.spDoc.m_yNrOfGrids;
                  first = false;
                }

                spframe.setYAxis(YMin, YMax, yNr);
            }
         }

         // Set all frames to maximum time range.
         for (int i = 0; i < iframes.length; i++)
         {
            SnowPackFrame spframe = (SnowPackFrame) iframes[i];
            spframe.timeRangeAll();
         }

         // Move marker for all frames to end position.
         MarkerMovement("End", 0);

         // Arrange frames side a side
         SideASide(true);
       }
       else
       {
         MessageBox mBox = new MessageBox(this, "Note",
           "Currently used frames stem from different",
           "files. Synchronization not possible!");
         mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
       }
    }
  }


  // System exit
  void menu_Exit_actionPerformed(ActionEvent e)
  {
    Exit(this);
  }

  void Exit(MenuFrame mFrame)
  {
    //this.setVisible(false);
    if (Setup.modelRunning)
    {
      MessageBox mBox = new MessageBox(mFrame,
         "Note", "SNOWPACK model still running.",
         "Output can be displayed after restart of SN_GUI!");
      mBox.setLocation(DialogCorner(mBox, mFrame)); mBox.setVisible(true);
    }

    System.exit(0);
    //closeWindow();
  }

  /*
  void closeWindow()
  {
    System.out.println("MenuFrame closed");

    if (Setup.modelRunning)
    {
      MessageBox mBox = new MessageBox(this,
         "Note", "SNOWPACK model still running.",
         "Output can be displayed after restart of SN_GUI!");
      mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
    }

    this.setVisible(false);
    this.dispose();
    System.exit(0);
  }
  */

  void this_windowClosing(WindowEvent e)
  {

    if (Setup.modelRunning)
    {
      MessageBox mBox = new MessageBox(this,
         "Note", "SNOWPACK model still running.",
         "Output can be displayed after restart of SN_GUI!");
      mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
    }

    this.dispose();
     //?? StartFrame is also exited at this point, reason unknown
  }


  // Stop the model execution
  void menu_stopRun_actionPerformed(ActionEvent e)
  {
    startFrame.jButton_StopRun_actionPerformed(e);
  }


  // Menu Open
  void menu_Open_actionPerformed()
  {
     Setup.m_FileDialogActive = true;

     FileDialog fileDialog = new FileDialog
       (this, "Choice of Process Data File", FileDialog.LOAD);
     fileDialog.setFile("test.pro");

     fileDialog.setLocation(DialogCorner(fileDialog, this));

     fileDialog.setVisible(true);
     // This initiates a repaint of the currently loaded frames and a number of
     // error messages (if not blocked). These occur since Java does not find
     // correct path names in this stage

     //fileDialog.setLocation(DialogCorner(fileDialog, this));  no reaction

     //fileDialog.setLocation(ScreenLocator.getCenterLocation(fileDialog.getSize()));
     // no reaction to this command, Dialog box not centered
     // also tried: instead of fileDialog.getSize menuFrame.get.Size
     // also tried: call after setVisible

     //fileDialog.setDirectory("/JBuilder/");  //no effect
     //fileDialog.setDirectory("\\JBuilder\\");  no effect
     //fileDialog.setDirectory(Setup.m_DataFilePath);  no effect

     //FilenameFilter proFilter = new OnlyExt("java");
     //fileDialog.setFilenameFilter(proFilter);
     // no reaction to these commands; FilenameFilter not set
     //just if the following two lines are executed, the method boolean of OnlyExt is executed:
     //    File f1 = new File(fileDialog.getDirectory());
     //    String s[] = f1.list(proFilter);
     // display of s:
     // for (int i=0; i < s.length; i++) {System.out.println(s[i]);}
     //
     // debugging function paramString() of dialog box cannot be used (is protected)

     File file = new File(fileDialog.getDirectory() + fileDialog.getFile());
     // String FileName = fileDialog.getDirectory() + fileDialog.getFile();
     // String Path = file.getPath(); = path + name
     // String Name = file.getName(); = name

     Setup.m_FileDialogActive = false;

     if (file.exists() &&
        (file.getName().endsWith(".pro") || file.getName().endsWith(".PRO")))
            NewFrame(file, ID_CODE_SNOWPACK_TEMPERATURE);
     else if (file.exists() &&
        (file.getName().endsWith(".met") || file.getName().endsWith(".MET")))
            NewFrame(file, ID_CODE_AIR_TEMPERATURE);
     else if (!file.getName().startsWith("null")) // Cancel not pressed (else file="nullnull")
     {
        MessageBox mBox = new MessageBox(this, "Error", "File not found or",
           "incorrect file extension!");
        mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
     }
  }


  // Menu Close
  void menu_Close_actionPerformed()
  {
      SnowPackFrame spframe = getActiveFrame(desktopPane);
      if (spframe != null)
      {
         Close(spframe);
      }
  }


  // Menu Close All
  void menu_CloseAll_actionPerformed()
  {
      desktopPane.setVisible(false);

      JInternalFrame iframes[] = desktopPane.getAllFrames();

      for (int i = 0; i < iframes.length; i++)
      {
        SnowPackFrame spframe = (SnowPackFrame) iframes[i];
        Close((SnowPackFrame) iframes[i]);
      }

      desktopPane.setVisible(true);
  }

  void Close(SnowPackFrame spframe)
  {
        // Adjust the menu list of open frames (remove frame)
        AdjustWindowsMenu(spframe, spframe.file, "", -1);

        // Remove the SnowPackFrame from the desktop
        desktopPane.remove(spframe);

        // Remove all controls (slider, buttons)
        spframe.snowPackView.removeAll();

        // Free the memory

        // Tries to 'wrap' the dataFile reference into a WeakReference (java.lang.ref)
        // were not successful (some obscure error messages occurred).
        // spDoc contains dataFile as member and has therefore also to be set
        // to null.
        spframe.spDoc = null;

        // Decoupling of the dataFile reference from the object it points to. Despite
        // setting dataFile = null, the links from other SnowPackFrames (those with the
        // same filename) to this dataFile object still remain alive.
        // (OnlyOneDatafile(spframe.file)): not needed
        spframe.dataFile = null;

        spframe.snowPackView.spDoc = null;
        spframe.xyPlotView.spDoc = null;
        spframe = null;
        //spframe.snowPackView = null;
        //spframe.xyPlotView = null;
        //spframe.dispose(); causes error message

        // Run the garbage collector (not necessarily processed immediately)
        Runtime r = Runtime.getRuntime();
        r.gc();

        repaint();

        // Sets one of the remaining frames to be the active one
        SetActiveWindow();

        // Disable some menu items if no more frames are present
        JInternalFrame iframes[] = desktopPane.getAllFrames();
        if (iframes.length < 1) Menu_AnyOpenFrames(false);
  }


  // Sets the last window added to JInternalFrame to be the active window
  void SetActiveWindow()
  {
        JInternalFrame iframes[] = desktopPane.getAllFrames();
        if (iframes == null) return;
        for (int i = 0; i < iframes.length; i++)
        {
                if (i == 0)
                {
                // Activate this frame
                  //desktopPane.getDesktopManager().activateFrame(iframes[i]);
                  try {iframes[i].setSelected(true);}  // without this: problems in Unix
                  catch (PropertyVetoException e) {};
                }

                else
                {
                // Deactivate the rest of the frames
                  //desktopPane.getDesktopManager().deactivateFrame(iframes[i]);
                  try {iframes[i].setSelected(false);} // without this: problems in Unix
                  catch (PropertyVetoException e) {};
                }
        }

  }


  // Print menu
  void menu_Print_actionPerformed(ActionEvent e)
  {
        Runtime r = Runtime.getRuntime();
        long mem1 = r.totalMemory();
        long mem2 = r.freeMemory();
        MessageBox mBox = new MessageBox(this, "Note",
        "Memory of Java Virtual Machine (in Bytes): ", "Total (varies): " + mem1 + "; Free: " + mem2);
        mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
/*
        MessageBox mBox = new MessageBox(this, "Note", "Not implemented yet!",
           "");
        mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
*/
  }


  // Menu Error File
  void menu_ErrorFile_actionPerformed(ActionEvent e)
  {
     FileDisplay fd = new FileDisplay
       (this, "ErrorFile (data reading errors)", true, ErrorFile.Path + ErrorFile.FileName);
     //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     //efd.setSize((float) screenSize.width * 0.5, (float) screenSize.height * 0.9);
     fd.setLocation(DialogCorner(fd, this));
     fd.setVisible(true);
  }


  // Menu Memory Check
  void menu_Memory_actionPerformed()
  {
      Runtime r = Runtime.getRuntime();
      long totmem = r.totalMemory();
      long freemem = r.freeMemory();
      MessageBox mBox = new MessageBox(this, "Note",
        "Memory of Java Virtual Machine (in Bytes): ",
        "Total (varies): " + totmem + "; Free: " + freemem);
      mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
  }


  // Menu Model Logfile
  void menu_ModelLogFile_actionPerformed(ActionEvent e)
  {
    startFrame.jButton_ModelLogFile_actionPerformed(e);
  }


  // Changes the y-axis range
  // Action = "Decrease" --> zoom in
  // Action = "Increase" --> zoom out
  // Action = "Move Higher"  --> next Y-interval (higher than the current one)
  // Action = "Move Lower" -> next Y-interval (lower than the current one)
  void menu_ChangeZoomY_actionPerformed(String Action)
  {
    // Get currently active frame
    SnowPackFrame spframe0 = getActiveFrame(desktopPane);
    if (spframe0 == null) return;

    if (Setup.m_Synchronization && spframe0.IdCode >= 500)
    // If synchronization is on and currently active frame is *.pro-file:
    // Switch to different y-axis range for all (*.pro-)frames.
    {
       JInternalFrame iframes[] = desktopPane.getAllFrames();

       for (int i = 0; i < iframes.length; i++)
       {
           SnowPackFrame spframe = (SnowPackFrame) iframes[i];
           if (spframe == null) return;

           if (spframe.IdCode >= 500)
           {
             if (Action.equals("Decrease")) spframe.zoomOut();
             if (Action.equals("Increase")) spframe.zoomIn();
             if (Action.equals("Move Higher")) spframe.moveHigher();
             if (Action.equals("Move Lower")) spframe.moveLower();
           }
       }
    }
    else
    // Synchronization is off or currently active frame is *.met-file:
    // Change y-axis range just for active frame.
    {
           if (Action.equals("Decrease")) spframe0.zoomOut();
           if (Action.equals("Increase")) spframe0.zoomIn();
           if (Action.equals("Move Higher")) spframe0.moveHigher();
           if (Action.equals("Move Lower")) spframe0.moveLower();
    }
  }


  // Choose the y-axis value range
  void menu_ValueRangeY_actionPerformed()
  {
     SnowPackFrame spframe0 = getActiveFrame(desktopPane);
     if (spframe0 == null) return;

     // Create dialog box and lay down the relevant parameters
     AxisDialog yad;
     if (spframe0.IdCode >= 500)
              yad = new AxisDialog(this, "Y-Axis Settings", true,
                spframe0.spDoc.m_YMinValue, spframe0.spDoc.m_YMaxValue,
                spframe0.spDoc.m_yNrOfGrids, "cm", "Snow Depth");
     else
              yad = new AxisDialog(this, "Y-Axis Settings", true,
                spframe0.spDoc.m_YMinValue, spframe0.spDoc.m_YMaxValue, // startValue, endValue
                spframe0.spDoc.m_yNrOfGrids,                            // partitions
                spframe0.spDoc.GetAxisUnit(spframe0.IdCode),            // Unit
                spframe0.spDoc.GetAxisText(spframe0.IdCode));           // ParameterName

     yad.setLocation(DialogCorner(yad, this));

     yad.setVisible(true);

     // System.out.println("YAxisDialog:");
     // System.out.println("     endValue = " + yad.endValue);
     // System.out.println("     startValue = " + yad.startValue);
     // System.out.println("     partitions = " + yad.partitions);

     if (yad.partitions < 0) return;

     if (Setup.m_Synchronization && spframe0.IdCode >= 500)
     // Synchronization is on and active frame is *.pro-File:
     // Switch to different y-axis ranges in all *.pro-frames.
     {
       JInternalFrame iframes[] = desktopPane.getAllFrames();

       for (int i = 0; i < iframes.length; i++)
       {
           SnowPackFrame spframe = (SnowPackFrame) iframes[i];
           if (spframe == null) return;

           if (spframe.IdCode >= 500)
             spframe.setYAxis(yad.startValue, yad.endValue, yad.partitions);
       }
     }
     else
     // Synchronization is off or active frame is *.met-file.
     // Change y-axis range just for active frame.
     {
           spframe0.setYAxis(yad.startValue, yad.endValue, yad.partitions);
           //                      = YMinValue, YMaxValue, yNrOfGrids
     }
  }


  // Choose the x-axis value range
  void menu_ValueRangeX_actionPerformed(ActionEvent e)
  {
     SnowPackFrame spframe = getActiveFrame(desktopPane);
     if (spframe != null)
     {
            AxisDialog xad;
            if ((spframe.IdCode >= 500) && (spframe.IdCode !=513) && (spframe.IdCode != 514))
            {
              xad = new AxisDialog(this, "X-Axis Settings", true,
                spframe.spDoc.m_StartValue, spframe.spDoc.m_EndValue,
                                // default = startValue, endValue for non-soil data
                spframe.spDoc.m_xNrOfGrids,                           // partitions
                spframe.spDoc.GetAxisUnit(spframe.IdCode),            // Unit
                spframe.spDoc.GetAxisText(spframe.IdCode));           // ParameterName

              xad.setLocation(DialogCorner(xad, this));

              xad.setVisible(true);

              // System.out.println("XAxisDialog:");
              // System.out.println("     endValue = " + xad.endValue);
              // System.out.println("     startValue = " + xad.startValue);
              // System.out.println("     partitions = " + xad.partitions);

              if (xad.partitions > -1)
                 spframe.setXAxis(xad.startValue, xad.endValue, xad.partitions);
              // This new setting effects both the displays with or without soil data.
            }
            else
            {
              MessageBox mBox = new MessageBox(this, "Note", "Menu item just available for layer parameters",
                 "(except for grain type)!");
              mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
            }
     }
  }


  // Choose a color table
  void menu_ColorTable_actionPerformed()
  {
      SnowPackFrame spframe = getActiveFrame(desktopPane);
      if (spframe != null)
      {
          if ((spframe.IdCode >= 500) && (spframe.IdCode !=513) && (spframe.IdCode != 514))
          {

            ColorDialog cd = new ColorDialog(this, "Set Colors",
               spframe.spDoc.m_ColorStartValue,
               spframe.spDoc.m_ColorEndValue,
               spframe.spDoc.m_ColorTab, true);

            cd.setLocation(DialogCorner(cd, this));

            cd.setVisible(true);

            System.out.println("ColorDialog: startValue: " + cd.startValue);
            System.out.println("ColorDialog: endValue: " + cd.endValue);
            System.out.println("ColorDialog: ColorTable: " + cd.selColorTab);

            if (cd.selColorTab > -1)
               spframe.setColorParameters(cd.startValue, cd.endValue, cd.selColorTab);
          }
          else
          {
            MessageBox mBox = new MessageBox(this, "Note", "Menu item just available for layer parameters",
               "(except for grain type)!");
            mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
          }

      }
  }


  // Draw a white background
  void menu_BackgrndWhite_actionPerformed(ActionEvent e)
  {
      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return;

      for (int i = 0; i < iframes.length; i++)
      {
          ((SnowPackFrame) iframes[i]).setBackGrnd("white");
      }
      return;
  }


  // Draw a grey background
  void menu_BackgrndGray_actionPerformed(ActionEvent e)
  {
      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return;

      for (int i = 0; i < iframes.length; i++)
      {
          ((SnowPackFrame) iframes[i]).setBackGrnd("lightGray");
      }
      return;
  }


  // Draw a black background
  void menu_BackgrndBlack_actionPerformed(ActionEvent e)
  {
      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return;

      for (int i = 0; i < iframes.length; i++)
      {
          ((SnowPackFrame) iframes[i]).setBackGrnd("black");
      }
      return;
  }


  // Reaction to clicking station names
  void statItem_actionPerformed(ActionEvent e, String station_label)
  {
    System.out.println("Clicked: " + station_label);

    try
    {
       IniFile StatlistIni = new IniFile(Setup.m_IniFilePath + "STATLIST.INI");
       Properties StatName = StatlistIni.BigProperty(true);
       Set StatSet = StatName.keySet();
       Iterator itrStatName = StatSet.iterator();

       while (itrStatName.hasNext())
       {
          if (station_label.equals((String) itrStatName.next()))
          {
             // Using the file "STATLIST.INI", from the station name (as clicked
             // in the menu) the station abbreviation (NameCode) is inferred
             // e.g. "Klosters Gatschiefer" --> "5KLO3"
             String NameCode = StatName.getProperty(station_label, "").toLowerCase();
             System.out.println("   Station Code: " + NameCode);
             if (NameCode.length()>0)
             {
                // Composition of the file name
                String m_TimeRange = "jj";
                String m_DataType;
                File profile, metfile;

                m_DataType = "pro";
                profile = new File(Setup.m_DataFilePath + m_TimeRange + NameCode +
                                     "p." + m_DataType);

                // See if *.pro-file for clicked station exists
                if (profile.exists())
                {
                      NewFrame(profile, ID_CODE_SNOWPACK_TEMPERATURE);
                      return;
                }

                // See if *.met-file for clicked station exists
                m_DataType = "met";
                metfile = new File(Setup.m_DataFilePath + m_TimeRange + NameCode +
                                     "p." + m_DataType);
                if (metfile.exists())
                {
                      NewFrame(metfile, ID_CODE_AIR_TEMPERATURE);

                      MessageBox mBox = new MessageBox(this,
                        "Note", "File not found:", profile.getPath());
                      mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);

                      return;
                }

                MessageBox mBox = new MessageBox(this,
                        "Error", "Files not found:", profile.getPath()+" & *.met");
                mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);

             }
          }
       }
    }
    catch (IOException e1)
    {
       System.out.println("MenuFrame.statItem_actionPerformed: Catch 1: " + e1);
    }
  }


  // Reaction to clicking parameter menu items
  void parameter_actionPerformed(ActionEvent e, String parameter_label)
  {
    // parameter_label is the clicked menu item, e.g. "Temperature"
    // IdCodeStr, the appropriate IdCode (e.g. "502"), is inferred from m_ParameterMenuFile
    System.out.println("Clicked parameter item: " + parameter_label);

    try{
       IniFile ParIni = new IniFile(Setup.m_IniFilePath + Setup.m_ParameterMenuFile);
       Properties ParName = ParIni.BigProperty(true);
       Set ParSet = ParName.keySet();
       Iterator itrParName = ParSet.iterator();

       while (itrParName.hasNext())
       {
          if (parameter_label.equals((String) itrParName.next()))
          {
             String IdCodeStr = ParName.getProperty(parameter_label, "");
             System.out.println("IdCode: " + IdCodeStr);

             try {
                 int IdCode = Integer.parseInt(IdCodeStr);

                 SnowPackFrame spframe = getActiveFrame(desktopPane);
                 if (spframe == null)
                 {
                      MessageBox mBox = new MessageBox(this, "Note",
                        "Select a station (file)", "before selecting a parameter!");
                      mBox.setLocation(DialogCorner(mBox, this));
                      mBox.setVisible(true);
                      return;
                 }

                 String ActFilename = spframe.getInputFile().getPath();

                 if (
                     (( ActFilename.endsWith("pro")||ActFilename.endsWith("PRO"))
                        && IdCode >= 500)
                    ||
                     (( ActFilename.endsWith("met")||ActFilename.endsWith("MET"))
                        && IdCode < 500))
                 {  // Both the currently active SnowPackFrame and the chosen
                    // parameter refer to the same type of file (layer parameter
                    // or single value parameter). Construction of new
                    // SnowPackFrame is not necessary.

                    if (spframe != null) spframe.changeId(IdCode, this);
                    return;
                 }

                 String SearchedFilename1 = "";
                 String SearchedFilename2 = "";
                 if ( IdCode >= 500)
                 {
                    // *.met-file is active, adequate *.pro-file is searched
                    SearchedFilename1 = ActFilename.substring(0,
                                        ActFilename.length()-3) + "PRO";
                    SearchedFilename2 = ActFilename.substring(0,
                                        ActFilename.length()-3) + "pro";
                 }
                 else if ( IdCode < 500)
                    // *.pro-file is active, adequate *.met-file is searched
                 {
                    SearchedFilename1 = ActFilename.substring(0,
                                        ActFilename.length()-3) + "MET";
                    SearchedFilename2 = ActFilename.substring(0,
                                        ActFilename.length()-3) + "met";
                 }
                 File searchedFile1 = new File(SearchedFilename1);
                 File searchedFile2 = new File(SearchedFilename2);

                 // Look for the SnowPackFrame which is related to SearchedFilename1
                 spframe = getFrame(desktopPane, searchedFile1);

                 if (spframe != null)
                 // Adequate "sister" SnowPackFrame exists in the desktop pane.
                 // Construction of new SnowPackFrame not necessary.
                 // Sister frame is set active, parameter change is processed on
                 // that frame.
                 {
                    //obviously works without framejobs (activate, deactivate...):
                    spframe.changeId(IdCode, this);
                    return;
                 }

                 // Same procedure for SearchedFilename2
                 spframe = getFrame(desktopPane, searchedFile2);
                 if (spframe != null)
                 {
                    spframe.changeId(IdCode, this);
                    return;
                 }

                 // See if searchedFile exists.
                 if (searchedFile1.exists())
                 {
                    NewFrame(searchedFile1, IdCode);
                    return;
                 }
                 if (searchedFile2.exists())
                 {
                    NewFrame(searchedFile2, IdCode);
                    return;
                 }

                 /*
                 // See if SearchedFilename with lower case extension exists.
                 searchedFile = new File(
                   SearchedFilename.substring(0, SearchedFilename.length()-3) +
                   SearchedFilename.substring(SearchedFilename.length()-3,
                                SearchedFilename.length()).toLowerCase());
                 if (searchedFile.exists())
                 {
                    NewFrame(searchedFile, IdCode);
                    return;
                 }
                 */

                 {
                    MessageBox mBox = new MessageBox(this,
                        "Error", "File not found:", searchedFile2.getPath());
                    mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
                    return;
                 }

             }
             catch (NumberFormatException nfe)
             {
               return;
             }

          } // end if
       } // end while

       MessageBox mBox = new MessageBox(this,
            "Error", "Parameter "+ parameter_label, "not found in file " +
            Setup.m_ParameterMenuFile + "!");
       mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);

    }
    catch (IOException e1)
    {
       System.out.println("MenuFrame.parameter_actionPerformed: Catch 1: " + e1);
    }

  }


  // Time ranges 3, 7, 14, 30,60 days clicked
  void menu_3Days_actionPerformed(ActionEvent e)  { TimeRange(3); }
  void menu_7Days_actionPerformed(ActionEvent e)  { TimeRange(7); }
  void menu_14Days_actionPerformed(ActionEvent e) { TimeRange(14);}
  void menu_30Days_actionPerformed(ActionEvent e) { TimeRange(30);}
  void menu_60Days_actionPerformed(ActionEvent e) { TimeRange(60);}


  // Display of maximum time range
  void menu_MaxTime_actionPerformed(ActionEvent e)
  {
      TimeRange(1000);
  }


  // Choice of the time range
  void menu_OtherTime_actionPerformed()
  {

      SnowPackFrame spframe = getActiveFrame(desktopPane);
      if (spframe != null) // --> no processing if no frames are open
      {
         GregorianCalendar defaultStartTime =
            (GregorianCalendar) spframe.dataFile.m_ActStartTime.clone();
         GregorianCalendar defaultEndTime =
            (GregorianCalendar) spframe.dataFile.m_ActEndTime.clone();

         TimeDialog td = new TimeDialog(this, "Time Chooser", true,
                                        defaultStartTime, defaultEndTime);
         td.setLocation(DialogCorner(td, this));
         td.setVisible(true);

         if(td.StartTime != null) TimeRange(td.StartTime, td.EndTime);
      }
  }


  // Frames are drawn on the desktop without overlapping
  void menu_SideASide_actionPerformed()
  {
     SideASide(true);
  }


  BufferedImage SideASide(boolean paint)
  // paint = true: drawing on screen
  // paint = false: use for creating jpg graphics
  {

      int startX, startY;
      int iframeWidth, iframeHeight;

      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return null; // not reached even if no frames in desktop
      // i=0 always seems to be the currently active frame
      if (iframes.length < 1) return null;

      int desktopWidth = desktopPane.getSize().width;
      int desktopHeight = desktopPane.getSize().height;

      Graphics2D gDesktop = null;
      BufferedImage desktopImage = null;
      if (!paint)
      {
        // Arrange frames side a side. This is necessary because if they where not
        // arranged in this mode before (for instance if one frame was closed)
        // errors occur (in the frame size).
        SideASide(true);

        // Create buffered image
        desktopImage = new BufferedImage
           (desktopWidth, desktopHeight, BufferedImage.TYPE_INT_RGB);
        gDesktop = desktopImage.createGraphics();
      }

      // Three frames or less: Draw frames in single column from top to bottom
      // If more than three frames: Draw frames in two columns from top to bottom,
      // second column may contain one more frame than right one (if i not even)
      if (iframes.length < 4) Setup.m_2Columns = false;
      else                    Setup.m_2Columns = true;

      for (int i=0; i < iframes.length; i++)
      {

         // /* If research mode: Don't show right side of graph.
         // Too small, if two columns of frames are painted.
         //if (Setup.m_ResearchMode)
         //  ((SnowPackFrame) iframes[i]).adjustSplitPaneDivider((float) 0.0);
         //

         int ColNr; // Column number of current frame i
         if ((i < iframes.length / 2) || (iframes.length < 4))
            ColNr = 1;
         else
            ColNr = 2;

         int RowNr; // Column number of current frame i
         int RowsInColumn; // Number of rows in column of current frame i
         if (iframes.length < 4)
         {   RowsInColumn = iframes.length;
             RowNr = i + 1; }
         else if (i < iframes.length / 2)
         {   RowsInColumn = iframes.length / 2;
             RowNr = i + 1; }
         else
         {   RowsInColumn = iframes.length - iframes.length / 2;
             RowNr = i + 1 - iframes.length / 2; }

         startX = (ColNr - 1) * desktopWidth / 2;
         startY = (RowNr - 1) * desktopHeight / RowsInColumn;
         if (iframes.length < 4) iframeWidth = desktopWidth;
         else iframeWidth = desktopWidth / 2;
         iframeHeight = desktopHeight / RowsInColumn;

         if (paint)
         // Draw the frames on the desktop
         {
           iframes[i].setBounds(startX, startY, iframeWidth, iframeHeight);

           if (Setup.m_SliderDisplay)
           {
             // Insert and paint control objects
             ((SnowPackFrame) iframes[i]).getSnowPackView().InsertControlObjects();
             iframes[i].setBounds(startX, startY, iframeWidth, iframeHeight);
           }
         }
         else
         // Add frame to BufferedImage of desktop pane
         {
           BufferedImage SPViewImage =
             new BufferedImage((int) (iframeWidth * 0.75), iframeHeight, BufferedImage.TYPE_INT_RGB);
           BufferedImage XYPlotImage =
             new BufferedImage((int) (iframeWidth * 0.25), iframeHeight, BufferedImage.TYPE_INT_RGB);
           Graphics2D g2_SPView = SPViewImage.createGraphics();
           Graphics2D g2_XYPlot = XYPlotImage.createGraphics();
           g2_SPView.setBackground(Color.white);
           g2_SPView.clearRect(0, 0, iframeWidth, iframeHeight);
           g2_XYPlot.setBackground(Color.white);
           g2_XYPlot.clearRect(0, 0, iframeWidth, iframeHeight);
           ((SnowPackFrame) iframes[i]).snowPackView.DrawSnowPackView(g2_SPView);
           ((SnowPackFrame) iframes[i]).xyPlotView.DrawXYPlot(g2_XYPlot);
           gDesktop.drawImage(SPViewImage, startX, startY, null);
           gDesktop.drawImage(XYPlotImage, startX + (int) (iframeWidth * 0.75), startY, null);
         }

      } // end for

      // Screen was painted
      if (paint) return null;

      // *.jpg was constructed
      return desktopImage;
  }



  // Piling of frames on the desktop
  void menu_Pile_actionPerformed(ActionEvent e)
  {
      Setup.m_2Columns = false;

      // Turn synchronization off
      if (Setup.m_Synchronization)
      {
           MessageBox mBox = new MessageBox(this, "Note",
           "Synchronization (Time menu)", "is switched off!");
           mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);

           Setup.m_Synchronization = false;
           chckbxmenu_Synchronization.setState(false);
      }

      JInternalFrame iframes[] = desktopPane.getAllFrames();

      if (iframes == null) return;

      int desktopWidth = desktopPane.getSize().width;
      int desktopHeight = desktopPane.getSize().height;

      // Length (width) of piled frames relativ to desktop length (width)
      float pileFactor = (float) 0.8;
      int iframeWidth = (int) (desktopWidth * pileFactor);
      int iframeHeight = (int) (desktopHeight * pileFactor);

      // Loop over existing frames
      // i=0 always seems to be the currently active frame
      //    --> (iframes.length - 1 - i) is used; frame painted on lower right
      //    of desktop
      for (int i = iframes.length - 1; i > -1; i--)
      {
         // Set devider location between left and right side of graph to 0.25.
         // Necessary, because might have been set to 0 by
         // menu_SideASide_actionPerformed().
         ((SnowPackFrame) iframes[i]).adjustSplitPaneDivider((float) 0.25);

         int startX = (int) ((float) (iframes.length - 1 - i) / (iframes.length - 1)
                             * (1 - pileFactor) * desktopWidth);
         int startY = (int) ((float) (iframes.length - 1 - i) / (iframes.length - 1)
                             * (1 - pileFactor) * desktopHeight);

         iframes[i].setBounds(startX, startY, iframeWidth, iframeHeight);

         if (Setup.m_SliderDisplay)
         {
           // Insert and paint control objects
           ((SnowPackFrame) iframes[i]).getSnowPackView().InsertControlObjects();
           iframes[i].setBounds(startX, startY, iframeWidth, iframeHeight);
         }

         //System.out.println("PILING: i=" + i);
         //System.out.println("startX, startY, iframeWidth, iframeHeight: " +
         //   startX + " " + startY + " " + iframeWidth + " " + iframeHeight);
      }

  }


  // Reaction to ordering the change of the currently active time.
  // Can be done by mouse-clicking the relevant buttons, by pressing the
  // adequate keys or by dragging the slider marker.
  // type: "Home","End","Right","Left" (key pressing or button clicking, in this
  //                                    case it is always called with value = 0),
  //       "Slider" (dragging of marker, value refers to the new marker position).
  //
  void MarkerMovement(String type, int value)
  {
    if (Setup.m_Synchronization)
    // Synchronization is on. Process movement for all frames.
    {
       JInternalFrame iframes[] = desktopPane.getAllFrames();

       for (int i = 0; i < iframes.length; i++)
       {
           SnowPackFrame spframe = (SnowPackFrame) iframes[i];
           if (spframe == null) return;

           spframe.spDoc.RetrieveDataFile();

           if (type == "Slider") spframe.SliderMovement(value);
           else spframe.marker(type);

           spframe.spDoc.SaveDataFile();
       }

    }
    else
    // Synchronization is off. Process movement just for active frame.
    {
           SnowPackFrame spframe = getActiveFrame(desktopPane);
           if (spframe == null) return;

           if (type == "Slider") spframe.SliderMovement(value);
           else spframe.marker(type);

    }

  }


  // Called after the choice of a time range
  void TimeRange(int days)
  {
    if (Setup.m_Synchronization)
    // Synchronization is on. Switch to different time range in all frames.
    {
       JInternalFrame iframes[] = desktopPane.getAllFrames();

       for (int i = 0; i < iframes.length; i++)
       {
           SnowPackFrame spframe = (SnowPackFrame) iframes[i];
           if (spframe == null) return;

           if (days < 1000) spframe.timeRangeSet(days);
           else spframe.timeRangeAll();
       }

    }
    else
    // Synchronization is off. Switch to different time range just for active frame.
    {
           SnowPackFrame spframe = getActiveFrame(desktopPane);
           if (spframe == null) return;

           if (days < 1000) spframe.timeRangeSet(days);
           else spframe.timeRangeAll();
    }
  }


  void TimeRange(GregorianCalendar StartTime, GregorianCalendar EndTime)
  {
    if (Setup.m_Synchronization)
    // Synchronization is on. Switch to different time range in all frames.
    {
       JInternalFrame iframes[] = desktopPane.getAllFrames();

       for (int i = 0; i < iframes.length; i++)
       {
           SnowPackFrame spframe = (SnowPackFrame) iframes[i];
           if (spframe == null) return;

           spframe.timeRange(StartTime, EndTime);
       }

    }
    else
    // Synchronization is off. Switch to different time range just for active frame.
    {
           SnowPackFrame spframe = getActiveFrame(desktopPane);
           if (spframe == null) return;

           spframe.timeRange(StartTime, EndTime);
    }
  }


  // Menu Help
  void menu_Hlp_actionPerformed(ActionEvent e)
  {
        MessageBox mBox = new MessageBox(this, "Note", "Not implemented yet!",
           "");
        mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
  }


  // Menu About
  void menu_About_actionPerformed()
  {
     MessageBox mBox = new MessageBox(this, "Info", "Visualization of the SNOWPACK model (WSL/SLF).",
       "Copyright(c). Version 8.2 2008.");
     mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
    /*
    MenuFrame_AboutBox dlg = new MenuFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width)/2 + loc.x, (frmSize.height - dlgSize.height)/2 + loc.y);
    dlg.setModal(true);
    dlg.show();
    */
  }


  // Printing
  void jButton_JPG_actionPerformed(ActionEvent e)
  {
     menu_CreateJPG_actionPerformed(false, "");

     /*
     SliderTest sd = new SliderTest(this, "Slider", true);
     sd.setLocation(DialogCorner(sd, this));
     sd.setVisible(true);
     */

/*        GraphicsPrinter gp = new GraphicsPrinter();
        if (gp.setupPageFormat()) {
          if (gp.setupJobOptions()) {
            try
            {
              gp.printGraphics();
            }
            catch (Exception e1)
            {
              System.out.println(e1.toString());
            }
          }
        }
*/
  }


  // Get active frame
  SnowPackFrame getActiveFrame(JDesktopPane desktopPane)
  {

      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null)
      {
         return null;
      }

      for (int i = 0; i < iframes.length; i++)
      {
         //System.out.println("in getActiveFrame:"); //
         //System.out.println("frame nr: "+i); //
         //System.out.println("isSelected: " + iframes[i].isSelected());


         if (iframes[i].isSelected())
         {
             SnowPackFrame spframe = (SnowPackFrame) iframes[i];
             return spframe;
         }
      }

      return null;
  }


  // Look for the SnowPackFrame which is related to searched file;
  // return null if no adequate SnowPackFrame is found,
  //   else SnowPackFrame
  SnowPackFrame getFrame(JDesktopPane desktopPane, File searchedFile)
  {
      JInternalFrame iframes[] = desktopPane.getAllFrames();
      if (iframes == null) return null;

      for (int i = 0; i < iframes.length; i++)
      {
         if ( ((SnowPackFrame) iframes[i]).getInputFile().equals(searchedFile))
         {
             SnowPackFrame spframe = (SnowPackFrame) iframes[i];

             for (int j = 0; j < iframes.length; j++)
             {
                if (j == i)
                {
                // Activate the frame which is related to searchedFile
                  //desktopPane.getDesktopManager().activateFrame(iframes[j]);
                  try {iframes[j].setSelected(true);}  // without this: problems in Unix
                  catch (PropertyVetoException e) {};
                }

                else
                {
                // Deactivate the rest of the frames
                  //desktopPane.getDesktopManager().deactivateFrame(iframes[j]);
                  try {iframes[j].setSelected(false);} // without this: problems in Unix
                  catch (PropertyVetoException e) {};
                }
             }

             return spframe;
         }
      }

      return null; // no adequate frame found
  }


  // Process some frame jobs
  void frameJobs()
  {
      //System.out.println("in frameJobs");

      JInternalFrame iframes[] = desktopPane.getAllFrames();

      for (int i = 0; i < iframes.length; i++)
      {
         // All frames in same layer (no one is "behind" although active)
         iframes[i].setLayer(new Integer(1));
/*
         // Activate last frame added, deactivate the rest
         if (i == iframes.length - 1)
         {
           desktopPane.getDesktopManager().activateFrame(iframes[i]);
           try {iframes[i].setSelected(true);}
           catch (PropertyVetoException e) {};
         }
         else
         {
           desktopPane.getDesktopManager().deactivateFrame(iframes[i]);
           try {iframes[i].setSelected(false);} // without this: problems in Unix
           catch (PropertyVetoException e) {};
         }

         // System.out.println("in frameJobs:");
         // System.out.println("frame nr: "+i);
         // System.out.println("isSelected: " + iframes[i].isSelected());
*/
      }

  }


  // Removes iframe from the desktopPane
  void RemoveSnowPackFrame(SnowPackFrame iframe)
  {
      desktopPane.remove(iframe); // no effect!!
      frameJobs(); // uses same number of frames as if called before prev. statement!! ??
  }


  Point DialogCorner(Dialog dialog, Frame frame)
  // Finds the upper left corner of the dialog box if centered in its parent frame.
  // Called like:
  //        dialogInstance.setLocation(DialogCorner(dialogInstance, this));
  // where dialogInstance stands for an instance of a dialog box.
  // This setLocation() places the component relative to the parent.
  // The parent is (for unclear reasons) not MenuFrame, but the screen.
  // Therefore, in this method the start point of the frame is added before
  // returning DialogCorner.
  {
         Dimension frameSize = frame.getSize();
         Dimension dialogSize = dialog.getSize();

         // Location of upper left frame corner on screen
         Point FrameCorner = frame.getLocation();

         // If dialog box bigger than frame
         //  --> upper left corner of dialog set to coincide with that of frame
         if(dialogSize.height>frameSize.height)
            dialogSize.height = frameSize.height;
         if(dialogSize.width>frameSize.width)
            dialogSize.width = frameSize.width;

         Point DialogCorner = new Point(
                       (frameSize.width - dialogSize.width)   / 2,
                       (frameSize.height - dialogSize.height) / 2);

         // Start point of frame is added
         DialogCorner.x += FrameCorner.x;
         DialogCorner.y += FrameCorner.y;

         return DialogCorner;
  }


  DataFile NewDataFile(File file)
  // Checks if one of the open SnowPackFrames is related to the file "file".
  // If yes, the DataFile related to this file is returned.
  // If no, null is returned
  {
    JInternalFrame iframes[] = desktopPane.getAllFrames();

    for (int i = 0; i < iframes.length; i++)
    {
        SnowPackFrame spframe = (SnowPackFrame) iframes[i];
        String path1 = AbsolutePath(spframe.file.getPath());
        String path2 = AbsolutePath(        file.getPath());

        if (path1.equals(path2))
           return spframe.dataFile;
    }

    return null;
  }


  boolean OnlyOneDatafile(File file)
  // Checks if file exists twice as an open SnowPackFrame
  {
    JInternalFrame iframes[] = desktopPane.getAllFrames();
    int NrOfFiles = 0;

    for (int i = 0; i < iframes.length; i++)
    {
        SnowPackFrame spframe = (SnowPackFrame) iframes[i];
        if (spframe.file.equals(file)) NrOfFiles++;
    }

    if (NrOfFiles > 1)  return false;
    else                return true;
  }


  // Open a file
  void NewFrame(File file, int IdCode)
  {
      // Construction of a new SnowPackFrame
      SnowPackFrame spFrame = new SnowPackFrame(file, IdCode, this);
      if (!spFrame.created)
      {
        spFrame = null;
        return;
      }

      AdjustFileMenu(file); // Adjust the File menu, if necessary
      Setup.m_2Columns = false; // Draw also right side of graph
      desktopPane.add(spFrame); // Add the frame to the desktop pane
      frameJobs();
      SetActiveWindow();
      Menu_AnyOpenFrames(true); // Sets relevant menu items enabled

      // Add a new menu item to the Windows menu
      String Id = spFrame.spDoc.GetAxisText(IdCode);
      AdjustWindowsMenu(spFrame, file, Id, 1);

      int w = desktopPane.getSize().width;
      int h = desktopPane.getSize().height;
      spFrame.setBounds(0, 0, w, h);

      // if (Setup.m_OperationalStartupActive)
      if (Setup.m_SliderDisplay)
      {
        // Insert and paint control objects
        spFrame.getSnowPackView().InsertControlObjects();
        spFrame.setBounds(0, 0, w, h);
      }


      if (Setup.m_Synchronization)
      {
         if (OneFile())
         // New file is the same as that on which rest of the frames are based upon
         {
            // Set all frames to maximum time range
            JInternalFrame iframes[] = desktopPane.getAllFrames();
            for (int i = 0; i < iframes.length; i++)
            {
                SnowPackFrame spframe = (SnowPackFrame) iframes[i];
                //if (spframe == null) return;
                spframe.timeRangeAll();
            }

            // Move marker for all frames to end position.
            MarkerMovement("End", 0);

            // Adds the new frame in a side-a-side mode
            if (!Setup.m_OperationalStartupActive) SideASide(true);
         }
         else
         // New file is different
         {
           MessageBox mBox = new MessageBox(this, "Note",
             "New frame based on other file than rest of",
             "present frames. Synchronization disabled!");
           mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);

           Setup.m_Synchronization = false;
           chckbxmenu_Synchronization.setState(false);
         }
      }


  }


  // Check if currently open SnowPackFrames were all spawned by one file.
  // File name or at least the first couple of letters of the filename
  // (given by Setup.m_SynLetters) must be the same, while the extension can
  // be different (*.met or *.pro).
  // This is the case if UsedFilesMenu contains just one file entry.
  boolean OneFile()
  {
         String firstFileName = "";
         String FileName = "";

         JInternalFrame iframes[] = desktopPane.getAllFrames();

         for (int i = 0; i < iframes.length; i++)
         {
           SnowPackFrame spframe = (SnowPackFrame) iframes[i];

           if (Setup.m_SynLetters == 0)
           { // Full file name is checked (including path).
             FileName = spframe.file.getPath(); // complete file name
             FileName = FileName.substring(0, FileName.length() - 4); // remove extension
           }
           else
           { // Just the first couple of letters of the filename are checked.
             FileName = spframe.file.getName(); // file name without path
             FileName = FileName.substring(0, Setup.m_SynLetters); // remove extension
           }

           if (i==0) firstFileName = FileName; // Save name of first file
           else if (!AbsolutePath(FileName).equals(AbsolutePath(firstFileName)))
              return false;
         }

         return true;
  }


  String AbsolutePath(String FileName)
  // Input string should be relative filename, starting with "." and related
  // to the Java user directory from which SN_GUI was started.
  // Filenames starting with ".." are not considered.
  {
    if (FileName.startsWith("."))
    {
      FileName = FileName.substring(1, FileName.length()); // remove "."
      FileName = System.getProperty("user.dir") + FileName;
    }

    return FileName;

  }


  // Adjust the recently used files at the end of the "File" menu
  void AdjustFileMenu(File file)
  {
      String NewFileStr = file.getPath();

      String label[] = new String[8]; // max. 8 used windows listed in File menu

      Enumeration menuEnum = UsedFilesMenu.elements();
      int j=0;
      while (menuEnum.hasMoreElements())
      {
         MenuItem menuItem = (MenuItem) menuEnum.nextElement();
         label[j] = menuItem.getLabel();

         if (j==0 && label[j].equals("")) menu_File.addSeparator();

         if (label[j].length()>0)
         {
              label[j] = label[j].substring(4, label[j].length()); // no number in front of label
         }
         if (!label[j].equals(""))
         {
              menu_File.remove(menuItem);
         }

         j++;
      }


      // Check if NewFile already exists in label
      int position = 0;
      for (int i=0; i<8; i++)
      {
        if (NewFileStr.equals(label[i]))
        {
           position = i+1;
           break;
        }
      }

      // New sorting of used file names
      if (position == 0)
      { // file has not been opened yet
         for (int i=8-1; i>0; i--) label[i] = label[i-1];
      }
      else
      {
         for (int i=position-1; i>0; i--) label[i] = label[i-1];
      }
      label[0] = NewFileStr;  // New file moved/put to first position
      //for (int i=0; i<8; i++) System.out.println(label[i]);

      // Empty the vector containing the used files menu items
      UsedFilesMenu.removeAllElements();


      for (int i=0; i<8; i++)
      {
         Integer iInt = new Integer(i+1);
         MenuItem menuItem = new MenuItem(iInt.toString() + " - " + label[i]);
         UsedFilesMenu.addElement(menuItem);

         if (!label[i].equals(""))
         {
             menu_File.add(menuItem);

             menuItem.addActionListener(new java.awt.event.ActionListener()
             {
                public void actionPerformed(ActionEvent e)
               {
                 String FileName = (String) e.getActionCommand();
                 FileName = FileName.substring(4, FileName.length()); // remove number at start
                 File file = new File(FileName);
                 if (file.getName().endsWith("pro")||file.getName().endsWith("PRO"))
                   NewFrame(file, ID_CODE_SNOWPACK_TEMPERATURE);
                 else if (file.getName().endsWith("met")||file.getName().endsWith("MET"))
                   NewFrame(file, ID_CODE_AIR_TEMPERATURE);

                 //statItem_actionPerformed(e, station_label);
               }
             });
         }
      }

   }


  // Adjust the menu items containing the currently used windows at the end of the "Windows" menu
  void AdjustWindowsMenu(SnowPackFrame spFrame, File file, String param, int add)
  {
  // add =  1 ... new menu item added, called if new file is opened
  //     =  0 ... IdCode of one of the existing SnowPackFrames has changed
  //     = -1 ... menu item is removed, called whenever one of the frames is closed
  // Parallel usage of the vectors UsedWindowsMenu (containing menu items) and
  // UsedFramesMenu, probably not necessary.
      MenuItem menuItem;
      SnowPackFrame frame;
      boolean frameIsZero = false;

      String NewFileStr;
      if (param.length() > 12)
        NewFileStr = file.getName() + " (" + param.substring(0, 11) + "..)";
      else
        NewFileStr = file.getName() + " (" + param + ")";

      String label[] = new String[8]; // max. 8 currently used windows listed in Windows menu

      Enumeration menuEnum = UsedWindowsMenu.elements();
      Enumeration frameEnum = UsedFramesMenu.elements();

      // Retrieve existing labels, remove existing menu items from Windows menu
      int j=0;
      while (menuEnum.hasMoreElements())
      {
         menuItem = (MenuItem) menuEnum.nextElement();
         label[j] = menuItem.getLabel(); // Get a field of labels from the existing menu items
         if (label[j].length()>0)
         {
              // remove number at front of label
              label[j] = label[j].substring(4, label[j].length());
         }
         if (!label[j].equals(""))
         {
              menu_Window.remove(menuItem); // Remove existing menu items
         }

         j++;
      }

      //System.out.println("Labels before resorting:");
      //for (int i=0; i<8; i++) System.out.println(i+". label: "+label[i]);

      // If menu item has to be deleted (window closed), or if the ID-code
      // of a specific frame has changed: state its position
      int position = 8-1;
      if (add < 1)
      {
         j=0;
         while (frameEnum.hasMoreElements())
         {
            frame = (SnowPackFrame) frameEnum.nextElement();
            if (frame == null)
            {
              // May happen if method is called twice with same parameters or
              // if frame is not found. E.g. if Menu File-Close is chosen.

              frameIsZero = true;
              break;
            }

            if (frame.equals(spFrame))
            {
              position = j;
              break;
            }
            j++;
            if (j==8) System.out.println("AdjustWindowsMenu: menu item to remove not found");
         }
      }

/*
      // Older version of getting the position by using label names. Not good since
      // some label names might be the same.
      int position = 8-1;
      if (!add)
      {
        for (int i=0; i<8; i++)
        {
          //System.out.println("i,NewFileStr,label: "+i+" "+NewFileStr+" "+label[i]+"!");

          if (NewFileStr.equals(label[i]))
          {
             position = i;
             break;
          }
          if (i==8-1) System.out.println("AdjustWindowsMenu: menu item to remove not found");
        }
      }
*/

      // New sorting of menu items
      if (!frameIsZero)
      {
      if (add == 1)
      // New window opened, new menu item put to first position
      {

         for (int i=8-1; i>0; i--)
         {
           label[i] = label[i-1];

           menuItem = (MenuItem) UsedWindowsMenu.elementAt(i-1);
           UsedWindowsMenu.setElementAt(menuItem, i);

           frame = (SnowPackFrame) UsedFramesMenu.elementAt(i-1);
           UsedFramesMenu.setElementAt(frame, i);
         }
         label[0] = NewFileStr;

         menuItem = new MenuItem(NewFileStr);

         MaximizeActionListener al = new MaximizeActionListener();
         al.spFrame = spFrame;
         al.desktopPane = desktopPane;
         menuItem.addActionListener(al);

         UsedWindowsMenu.setElementAt(menuItem, 0);
         UsedFramesMenu.setElementAt(spFrame, 0);

      }
      else if (add == -1)
      // Window closed, menu item at position is removed, following items move up
      {
         for (int i=position; i<8-1; i++)
         {
           label[i] = label[i+1];

           menuItem = (MenuItem) UsedWindowsMenu.elementAt(i+1);
           UsedWindowsMenu.setElementAt(menuItem, i);

           frame = (SnowPackFrame) UsedFramesMenu.elementAt(i+1);
           UsedFramesMenu.setElementAt(frame, i);

         }
         label[8-1] = "";

         UsedWindowsMenu.setElementAt(new MenuItem(""), 8-1);
         UsedFramesMenu.setElementAt(null, 8-1);
      }
      else if (add == 0)
      // ID-code has changed
      {
         label[position] = NewFileStr;
      }

      } // end if (!frameIsZero)

      //System.out.println("Menu labels after resorting:");
      //for (int i=0; i<8; i++) System.out.println(i+". label: "+label[i]);

      // Recreate the Windows menu
      for (int i=0; i<8; i++)
      {
         Integer iInt = new Integer(i+1);
         if (i==0)
         {
           if (label[i].equals("")) menu_Window.remove(menu_Seperator);
           else menu_Window.add(menu_Seperator);
         }

         if (!label[i].equals(""))
         {
             ((MenuItem) UsedWindowsMenu.elementAt(i)).setLabel(iInt.toString() + " - " + label[i]);
             menu_Window.add((MenuItem) UsedWindowsMenu.elementAt(i));
         }
      }

      // Next statement to prevent the following bug: If a frame is maximized by
      // clicking the frame name in the Windows menu, and this maximized frame is
      // tried to be closed using File-Close, an unexplainable Property-related error
      // occurs, and the closed frame is still visible. Error message still
      // present, though.
      if (frameIsZero) repaint();

   }


   // !! status bar does not work yet
   static void setStatusText(String text)
   {
     statusBar.setText(text);
   }


  void menu_Play_actionPerformed()
  {
      // Process the animation
      SnowPackFrame spframe = getActiveFrame(desktopPane);
      if (spframe != null)
      {
          spframe.Animation();
      }
  }


  void menu_AnimSettings_actionPerformed(ActionEvent e)
  {
      SnowPackFrame spframe = getActiveFrame(desktopPane);
      if (spframe != null)
      {
         //GregorianCalendar defaultStartTime =
         //   (GregorianCalendar) spframe.dataFile.m_ActStartTime.clone();
         //GregorianCalendar defaultEndTime =
         //   (GregorianCalendar) spframe.dataFile.m_ActEndTime.clone();

         AnimationDialog ad = new AnimationDialog(this, "Animation Settings", true,
             spframe.spDoc.m_AnimationStartTime,
             spframe.spDoc.m_AnimationEndTime,
             Setup.m_animationSpeed);
         ad.setLocation(DialogCorner(ad, this));
         ad.setVisible(true);

         if(ad.animationExit > 0)
         {
            Setup.m_animationSpeed = ad.Speed;
            spframe.spDoc.m_AnimationStartTime = ad.StartTime;
            spframe.spDoc.m_AnimationEndTime = ad.EndTime;
         }

         if(ad.animationExit == 2)
         {
            /*
            // Button Ok + Run. Settings of AnimationDialog should be stored,
            // followed by an immediate start of the animation.
            // Problem: ad-dialog box remains on the screen during the animation.

            // spframe.repaint(); is just executed after the animation

            // Below: tries to overpaint ad-box in background color
            Dimension adSize = ad.getSize();
            Point adCorner = new Point(DialogCorner(ad, this));
            Graphics g = this.getGraphics();

            g.setColor(spframe.spDoc.m_Background);
            g.fillRect(adCorner.x, adCorner.y, adSize.width, adSize.height);
            g.setColor(getForeground());

            spframe.RedrawFrame(); // would have to be applied to all open frames

            menu_Play_actionPerformed();
            */
         }
      }
  }


  void CheckExit()
  { // Error message if "exit" in StartFile at already at program start
     try
     {
        IniFile StartFileIni = new IniFile(Setup.m_IniFilePath + "StartFile");
        String StartFileStr = StartFileIni.getEntry("StartFile", "Name", "");
        if (StartFileStr.equals("exit"))
        {
          MessageBox mBox = new MessageBox(this, "Error",
           "Program will terminate immediately because", "StartFile contains string <exit>!");
          mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
        }
     } // end try
     catch (IOException e)  { }
     return;
  }


  File FindStartFile()
  {
     // Find name of data file used for default data display.
     // StartFile contains "Name = <FileName>" or "Name = exit" (the latter
     // causes the program to exit).
     File file;
     try
     {
        IniFile StartFileIni = new IniFile(Setup.m_IniFilePath + "StartFile");
        String StartFileStr = StartFileIni.getEntry("StartFile", "Name", "");
        if (StartFileStr.equals(""))
        {
          return null;
        }
        else
        {
          file = new File(StartFileStr);
        }
     } // end try
     catch (IOException e)
     {
          return null;
     }

     // If string read in StartFile equals "Name=exit": exit SN_GUI
     if (file.getPath().toLowerCase().equals("exit"))
     {
        System.out.println("Program exited because StartFile contains <Name=exit>");
        Exit(this);
     }

     // Check if file exists
     if (!file.exists())
     {
        return null;
     }

     return file;
  }


  boolean FindFilesToPrint()
  {
     // Find names of files to print. N
     // StartFile contains "Print = <File1,File2,...FileN>" or "Print =".
     // In the first case, *.jpg-files are constructed from the listed files,
     // thereafter the program is exited.
     // return true: files found
     //        false: no files found or error
     File file;
     try
     {
        IniFile StartFileIni = new IniFile(Setup.m_IniFilePath + "StartFile");
        String StartFileStr = StartFileIni.getEntry("StartFile", "Print", "");

        StringTokenizer st = new StringTokenizer(StartFileStr, ",");
        if (!st.hasMoreTokens()) return false;
        do
        {
           String NextFile = st.nextToken().trim();
           FilesToPrint.addElement(NextFile);
        } while (st.hasMoreTokens());

        // no more files to print available
        return true;

     } // end try
     catch (IOException e)
     {
        System.out.println("MenuFrame.FindFilesToPrint: problems reading StartFile");
        return false;
     }
  }


  void JPG_Construction()
  // Construction of *.jpg-files from the information given in the Print= - line
  // of the StartFile
  {
     Enumeration filesEnum = FilesToPrint.elements();
     // FilesToPrint is a vector containing the names of the files to be printed

     while (filesEnum.hasMoreElements())
     {
         String ProFileName = (String) filesEnum.nextElement();
         String CompleteProFileName = Setup.m_DataFilePath + ProFileName; // adding the path to the file name
         //String CompleteProFileName = System.getProperty("user.dir") + "\\Data\\" + ProFileName;

         File file = new File(CompleteProFileName);
         if (file.exists())
         {
            OperationalStartup(file);
            String JPGFileName = ProFileName.substring(0, ProFileName.length()-3) + "jpg";
            String JPG = System.getProperty("user.dir") + "\\Data\\" + JPGFileName;

            menu_CreateJPG_actionPerformed(true, JPG);
            menu_CloseAll_actionPerformed();
         }
         else
         {
            System.out.println("File to be printed does not exist: " + file.getPath());
         }
     }
  }


  void OperationalStartup(File file)
  // This procedure is started when ResearchMode = OFF in file SETUP.INI.
  // It is also started if *.jpg-files are to be constructed automatically.
  // file: is a default file read from the "StartFile".
  // A number of SnowPackFrames are produced (using different parameters,
  // listed in SETUP.INI) and finally rearranged on the screen.
  {
     this.setVisible(false);

     // Needed to suppress Side-a-Side command in NewFrame():
     Setup.m_OperationalStartupActive = true;

     if (file == null)
     {
       MessageBox mBox = new MessageBox(this, "Error",
        "Start file: name not found", "or file does not exist!");
       mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
     }
     else
     {

       // Creating SnowPackFrames
       if (Setup.m_Parameter4 > -1) NewFrame(file, Setup.m_Parameter4);
       if (Setup.m_Parameter3 > -1) NewFrame(file, Setup.m_Parameter3);
       if (Setup.m_Parameter2 > -1) NewFrame(file, Setup.m_Parameter2);
       if (Setup.m_Parameter1 > -1) NewFrame(file, Setup.m_Parameter1);

       // Arrange SnowPackFrames
       SideASide(true);
     }

     if (!Setup.m_PrintMode)
     {
        this.setVisible(true);
        //SetActiveWindow(); // after setVisible()!
     }
     SetActiveWindow();

     Setup.m_OperationalStartupActive = false;

     return;
  }


  boolean menu_CreateJPG_actionPerformed(boolean autoPrint, String JPG)
  // Creation of *.JPG-files from the currently used SnowPackFrames
  // autoPrint: true if automated *.jpg-construction job is active
  // JPG is name of JPGfile or empty. In the latter case the user can chose the name.
  {
        //String number = (new Integer(i+1)).toString();
        JPGfile = "";

        try
        {
          BufferedImage image;
          image = SideASide(false);
          if (image == null)
          {
             if (!autoPrint)
             {
               MessageBox mBox = new MessageBox(this, "Info",
                 "Currently no frames present.", "*.jpg could not be constructed!");
               mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
               return false;
             }
             else
             {
               System.out.println("Currently no frames present; *.jpg could not be constructed!");
               return false;
             }
          }

          // File name for the output file
          if (JPG.equals(""))
          {
            // Dialog Box required
            Setup.m_FileDialogActive = true;
            FileDialog fileDialog = new FileDialog
              (this, "Choice of output file. Extension should be *.jpg.", FileDialog.SAVE);
            fileDialog.setFile("SN_GUI.jpg");
            fileDialog.setLocation(DialogCorner(fileDialog, this));
            fileDialog.setVisible(true);
            File file = new File(fileDialog.getDirectory() + fileDialog.getFile());

            if (file.getName().startsWith("null")) // Cancel pressed, file="nullnull"
            {
               return false;
            }

            JPGfile = file.getPath(); // = path + name
            Setup.m_FileDialogActive = false;

            // Tried: YesNoDialog-box asking if overwriting is desired
            // Not necessary because this is usually asked by the operation system
          }
          else
          {
            // JPG-file was handed over to the parameter list of this method
            JPGfile = JPG;
          }


          if (writeJPG(image, JPGfile))
          {
            if (!autoPrint)
            {
              MessageBox mBox = new MessageBox(this,
              "Info", "File construction completed:", JPGfile);
              mBox.setLocation(DialogCorner(mBox, this)); mBox.setVisible(true);
            }

            System.out.println(JPGfile + " was constructed!");
            // Construction successfully completed

            return true;
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          JPGfile = "";
        }

        return false;
  }


  private BufferedImage createOffscreenImage(SnowPackFrame spframe)
  {
        // Create a BufferedImage the same size as this component.
        Dimension d = spframe.getSize();
        int w = d.width, h = d.height;
        BufferedImage mImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Obtain the Graphics2D for the offscreen image.
        Graphics2D g2 = mImage.createGraphics();

        // Fill with the background color
        // g2.setBackground(new Color(0xF0, 0xF0, 0xF0));
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, w, h);
        // g2.setPaint(Color.white);
        // g2.fill(rect);

        spframe.snowPackView.DrawSnowPackView(g2);
        spframe.xyPlotView.DrawXYPlot(g2);

        return mImage;
  }


  private boolean writeJPG(BufferedImage img, String filename)
  throws IOException
  {
     try
     {
        FileOutputStream fout = new FileOutputStream(filename);
        com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fout);
        com.sun.image.codec.jpeg.JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
        param.setQuality(1.0f, true);
        encoder.setJPEGEncodeParam( param );
        encoder.encode(img);
        img.flush();
        fout.close();
        return true;
     }
     catch (Exception e)
     {
        e.printStackTrace();
        return false;
     }
  }



  void menu_CreatePostscript_actionPerformed()
  // Creation of JPG, then conversion of JPG to postscript format
  {
     // Create *.JPG-files from currently active SnowPackFrames
     if (menu_CreateJPG_actionPerformed(false, ""))
     {
       // FramePlotter framePlotter = new FramePlotter(this);
       PS_Maker psm = new PS_Maker(this, JPGfile);
     }
  }

  void jButton_SideASide_actionPerformed()
  {
     menu_SideASide_actionPerformed();
  }

  void jButton_Close_actionPerformed()
  {
     menu_Close_actionPerformed();
  }



  void Menu_AnyOpenFrames(boolean enable)
  // Enable/disable menu items and buttons, depending on if SnowPackFrames are present or not
  // enable = true: any SnowPackFrames are open
  {
    //Menu items
    menu_Close.setEnabled(enable);
    menu_CloseAll.setEnabled(enable);
    menu_Print.setEnabled(enable);
    menu_CreateJPG.setEnabled(enable);
    menu_CreatePostscript.setEnabled(enable);

    menu_ValueRangeX.setEnabled(enable);
    menu_ValueRangeY.setEnabled(enable);
    menu_IncrZoomY.setEnabled(enable);
    menu_DecrZoomY.setEnabled(enable);
    menu_MoveYHigher.setEnabled(enable);
    menu_MoveYLower.setEnabled(enable);
    menu_ColorTable.setEnabled(enable);
    menu_Backgrnd.setEnabled(enable);
    menu_BackgrndWhite.setEnabled(enable);
    menu_BackgrndGray.setEnabled(enable);
    menu_BackgrndBlack.setEnabled(enable);
    chckbxmenu_DrawXYPlot.setEnabled(enable);
    chckbxmenu_Slider.setEnabled(enable);

    menu_Time.setEnabled(enable);
    menu_3Days.setEnabled(enable);
    menu_7Days.setEnabled(enable);
    menu_14Days.setEnabled(enable);
    menu_30Days.setEnabled(enable);
    menu_60Days.setEnabled(enable);
    menu_MaxTime.setEnabled(enable);
    menu_OtherTime.setEnabled(enable);
    chckbxmenu_Synchronization.setEnabled(enable);

    menu_Window.setEnabled(enable);
    menu_SideASide.setEnabled(enable);
    menu_Pile.setEnabled(enable);

    menu_Animation.setEnabled(enable);
    menu_Play.setEnabled(enable);
    menu_AnimSettings.setEnabled(enable);

    Enumeration parEnum2 = parMenu.elements();
    while (parEnum2.hasMoreElements())
       ((Menu) parEnum2.nextElement()).setEnabled(enable);


    // Buttons
    jButton_Close.setEnabled(enable);
    jButton_JPG.setEnabled(enable);
    jButton_Colors.setEnabled(enable);
    jButton_yZoomDec.setEnabled(enable);
    jButton_yZoomInc.setEnabled(enable);
    jButton_xSetup.setEnabled(enable);
    jButton_ySetup.setEnabled(enable);
    jButton_Time.setEnabled(enable);
    jToggleButton_Sync.setEnabled(enable);
    jButton_Play.setEnabled(enable);
    jButton_Home.setEnabled(enable);
    jButton_End.setEnabled(enable);
    jButton_Left.setEnabled(enable);
    jButton_Right.setEnabled(enable);
    jButton_SideASide.setEnabled(enable);

    // Some buttons always disabled
    // Printing for Windows always disabled (overwrites previous settings)
    if (System.getProperty("os.name").substring(0,3).equals("Win"))
    {
      menu_CreatePostscript.setEnabled(false);
    }
    menu_Print.setEnabled(false);
    menu_Hlp.setEnabled(false);
    menu_CreatePostscript.setEnabled(false);
  }

}

/*
    jButton_Print.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Print_actionPerformed(e);
      }
    });


    addKeyListener(new java.awt.event.KeyListener()
    {
      public void keyPressed(KeyEvent ke)
      {
         System.out.println("key pressed");

         SnowPackFrame spframe = getActiveFrame(desktopPane);
           if (spframe == null) return;

         int key = ke.getKeyCode();

         if (key == KeyEvent.VK_LEFT)
             spframe.markerLeft();
         return;
      }

      public void keyReleased(KeyEvent ke) {}

    });


*/
