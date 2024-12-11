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
// Setup: Handles basic settings
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;


public class Setup
{
  // Parameters of SETUP.INI file
  static String m_ParameterMenuFile;
  static String m_IniFilePath;
  static String m_SnowPackPath = "";
  static String m_DataFilePath;
  static String m_ErrorFilePath; // currently not used
  static String m_SnowFilePath;
  static boolean m_SoilDataDisplay;
  static boolean m_ResearchMode;
  static String m_DisplayMode;
  static int m_Parameter1;
  static int m_Parameter2;
  static int m_Parameter3;
  static int m_Parameter4;
  static float m_YMaxValue;
  static String m_Printer;
  static String m_SnowFile;
  static float m_MaxTimeResolution;
  static int m_SynLetters;

  static boolean m_AlwaysBestResolution;
  static boolean modelRunning;
  static boolean framePlotting;
  static int m_animationSpeed;
  static boolean m_SliderDisplay;
  static boolean m_Synchronization;
  static boolean m_DrawXYPlot;
  static boolean m_2Columns;
  static int m_DefaultAnimationSteps;
  static boolean m_FileUpdate;
  static boolean m_FileDialogActive;
  static boolean m_OperationalStartupActive;
  static boolean m_PrintMode;

  public Setup()
  {
  }


  static void SetSnowpackPath(String SnowPackPath)
  {
    m_SnowPackPath = SnowPackPath;
  }


  static boolean ReadSetupFile()
     // Read setup parameters from SETUP.INI (has to be located in current directory)
     {
        try
        {
          IniFile SetupIni = new IniFile("SETUP.INI");

          m_IniFilePath = SetupIni.getEntry("Program", "IniFilePath", "");
          m_DataFilePath = SetupIni.getEntry("Program", "DataFilePath", "");
          m_ErrorFilePath = SetupIni.getEntry("Program", "ErrorFilePath", "");
          m_SnowFilePath = SetupIni.getEntry("Program", "SnowFilePath", "");
          String SoilDataDisplay = SetupIni.getEntry("Program", "SoilDataDisplay", "OFF");
          String ResearchStr = SetupIni.getEntry("Program", "ResearchMode", "OFF");
          String AlwaysBestResolution = SetupIni.getEntry("Program", "AlwaysBestResolution", "OFF");
          m_DisplayMode = SetupIni.getEntry("Program", "DisplayMode", "FullScreen");
          m_Printer = SetupIni.getEntry("Program", "Printer", "");
          try {m_SynLetters =
            Integer.parseInt(SetupIni.getEntry("Program", "SynLetters", "0")); }
          catch (NumberFormatException nfe) { m_SynLetters = 0; }
          try {m_YMaxValue = (float)
            Integer.parseInt(SetupIni.getEntry("Program", "MaxSnowDepth", "")); }
          catch (NumberFormatException nfe) { m_YMaxValue = (float) 400.0; }
          try {m_MaxTimeResolution = (float)
            Float.parseFloat(SetupIni.getEntry("Program", "MaxTimeResolution", "30")); }
          catch (NumberFormatException nfe) { m_YMaxValue = (float) 400.0; }
          try {m_Parameter1 =
            Integer.parseInt(SetupIni.getEntry("Program", "Parameter1", "")); }
          catch (NumberFormatException nfe) { m_Parameter1 = -1; }
          try {m_Parameter2 =
            Integer.parseInt(SetupIni.getEntry("Program", "Parameter2", "")); }
          catch (NumberFormatException nfe) { m_Parameter2 = -1; }
          try {m_Parameter3 =
            Integer.parseInt(SetupIni.getEntry("Program", "Parameter3", "")); }
          catch (NumberFormatException nfe) { m_Parameter3 = -1; }
          try {m_Parameter4 =
            Integer.parseInt(SetupIni.getEntry("Program", "Parameter4", "")); }
          catch (NumberFormatException nfe) { m_Parameter4 = -1; }

          if (SoilDataDisplay.equals("ON"))
             m_SoilDataDisplay = true;
          else
             m_SoilDataDisplay = false;

          if (ResearchStr.equals("ON"))
          {
             m_ResearchMode = true;
             m_ParameterMenuFile = "PARMENU1.INI";
          }
          else
          {
             m_ResearchMode = false;
             m_ParameterMenuFile = "PARMENU2.INI";
          }

          if (AlwaysBestResolution.equals("ON"))
             m_AlwaysBestResolution = true;
          else
             m_AlwaysBestResolution = false;

          m_FileDialogActive = false;
          m_OperationalStartupActive = false;
          m_PrintMode = false;
          m_SnowFile = "";

/*
          System.out.println("m_ParameterMenuFile  = "+ m_ParameterMenuFile);
          System.out.println("m_IniFilePath  = "+ m_IniFilePath);
          System.out.println("m_DataFilePath  = "+ m_DataFilePath);
          System.out.println("m_ErrorFilePath  = "+ m_ErrorFilePath);
          System.out.println("m_SnowFilePath = "+ m_SnowFilePath);
          System.out.println("m_SoilDataDisplay  = "+ m_SoilDataDisplay);
          System.out.println("m_ResearchMode  = "+ m_ResearchMode);
          System.out.println("m_DisplayMode = "+ m_DisplayMode);
          System.out.println("m_YMaxValue  = "+ m_YMaxValue);
          System.out.println("m_Printer  = "+ m_Printer);
          System.out.println("m_Parameter1 = "+ m_Parameter1);
          System.out.println("m_Parameter2 = "+ m_Parameter2);
          System.out.println("m_Parameter3 = "+ m_Parameter3);
          System.out.println("m_Parameter4 = "+ m_Parameter4);
          System.out.println("m_FileDialogActive = "+m_FileDialogActive);
          System.out.println("m_OperationalStartupActive = "+m_OperationalStartupActive);
          System.out.println("m_MaxTimeResolution = "+m_MaxTimeResolution);
          System.out.println("m_AlwaysBestResolution = "+m_AlwaysBestResolution);
*/
           System.out.println("user.dir: "  + System.getProperty("user.dir"));
           System.out.println("user.home: " + System.getProperty("user.home"));
           System.out.println("java.home: " +System.getProperty("java.home"));
           System.out.println("");
           System.out.println("SETUP.INI parameters read.");
        } // end try
        catch (IOException e)
        {
          System.out.println("Setup.java: " + e);
          return false;
        }

        modelRunning = false;
        framePlotting = false;
        m_animationSpeed = 3; // 1...5, 5=highest
        m_SliderDisplay = true;
        m_2Columns = false; // if 2 columns are displayed, DrawYAxis text is
                            // skipped in the right graph
        m_DrawXYPlot = true;
        m_DefaultAnimationSteps = 30;
        m_FileUpdate = true;

        if (m_ResearchMode)
          m_Synchronization = false;
        else
          m_Synchronization = true;

        return true;
     }

}