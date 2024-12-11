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
// SnowPackDoc: Preprocesses the data obtained by reading DataFile.
//              Extraction of data from the *.ini-files.
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;
import java.util.*;
import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class SnowPackDoc
{
    public SnowPackDoc() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Member variables
   DataFile dataFile;

   String m_StationFileName = null;
   String m_StationFilePath = null;
   String m_StationName = null;
   String m_StationAltStr = null;
   String m_StationAspectStr = null;
   String m_StationSlopeStr = null;
   String m_StationLatStr = null;
   String m_StationLonStr = null;

   //Schirmer
   String[] depthTemps;


   int m_ColorTab = 7;
   float m_ColorStartValue = (float) 0.0;
   float m_ColorEndValue = (float) 0.0;
   float m_StartValue = (float) 0.0;
   float m_EndValue = (float) 0.0;
   float m_SoilStartValue = (float) 0.0;
   float m_SoilEndValue = (float) 0.0;
   float m_YMinValue;
   float m_YMaxValue;
   float m_YMinValue_org; // original YMinValue
   float m_YMaxValue_org; // original YMaxValue
   int m_xNrOfGrids = 4;
   int m_yNrOfGrids;
   float m_XMinValue = (float) 0.0;
   float m_XMaxValue = (float) 0.0;
   float m_MinValue; // calculated by Adjust()
   float m_MaxValue; // ""
   int m_NrOfGrids; // ""

   float MAX_Y_ZOOM_VALUE;
   float MIN_Y_ZOOM_DIFFERENCE;

   int DAY = 86400;
   int HOUR = 3600;
   int MINUTE = 60;
   GregorianCalendar m_StartTime = null;
   GregorianCalendar m_Time = null;
   GregorianCalendar m_ActTime = null;
   GregorianCalendar m_AnimationStartTime = null;
   GregorianCalendar m_AnimationEndTime = null;

   boolean m_EvenTime = true;
   int m_TimeRange = 3 * DAY;
   int m_TimeRange1 = 0; // determined in GetXAxisRange()
   int m_MinTimeStep = 0; // determined in CalculateTimeSteps()
   int m_MinTimeStepInData = 0; // minimum time resolution of data
   int m_TimeStep = 0; // TimeStep, adjusted in GetXAxisRange()
   int m_TimeGrid = 6;
   int m_MinCatchTime = 15 * MINUTE;

   JSlider jSlider = new JSlider();
   boolean activeSliderMovement = true;
   ChangeListener changeListener;

   JButton jButton_Prev  = new JButton("<");
   JButton jButton_Next  = new JButton(">");
   JButton jButton_Home  = new JButton("<<");
   JButton jButton_End   = new JButton(">>");
   JButton jButton_Remove= new JButton("X");
   JButton jButton_Up    = new JButton(" Up ");
   JButton jButton_Down  = new JButton(" Dn ");

   DataEntry m_ActDataEntry = null;
   DataEntry m_MarkerDataEntry = null;

   // Default colors for new frames
   Color m_Background = new Color(255, 255, 255); // white
   Color m_Foreground = new Color(0, 0, 0);       // black
   Color m_CurrentColor = new Color (0, 0, 0);

   MenuFrame mFrame;
   SnowPackFrame spframe;
   String PARDATA_Filename ="";

   int NrOfParameters;
   int IdCode[] = new int[30]; // IdCode, max. parameters per plot
   String Name2[] = new String[30];

   // DataFile members which have to be temporarily saved in SnowPackDoc
   int datafile_m_GetStartTimeIndex;
   int datafile_m_GetEndTimeIndex;
   int datafile_m_ActDataEntryIndex;
   DataEntry datafile_m_ActDataEntry;
   int datafile_m_ActStartTimeIndex;
   int datafile_m_ActEndTimeIndex;
   GregorianCalendar datafile_m_ActStartTime;
   GregorianCalendar datafile_m_ActEndTime;
   int datafile_m_TimeStepsWithoutData;
   float datafile_m_IdMinValue;
   float datafile_m_IdMaxValue;
   boolean datafile_m_IdSoilDataExist;
   boolean datafile_m_SoilDataExist;


   // Construction
   public SnowPackDoc(File FileName, DataFile dataFile,
                      MenuFrame mFrame, SnowPackFrame spframe) throws IOException
   {
        this.dataFile = dataFile;
        this.mFrame = mFrame;
        this.spframe = spframe;
        m_StationFileName = FileName.getName();
        m_StationFilePath = FileName.getPath();

        // Minimum data time step, needed in SnowPackView.DrawTimeProfile()
        //Schirmer, Steiniger. Wrong time Resolution for .pro files with IDCode = 0, wrote 500 instead
        //m_MinTimeStepInData = dataFile.GetTimeResolution(0);
        m_MinTimeStepInData = dataFile.GetTimeResolution(500);

        // Calculate TimeStep and m_MinCatchTime
        CalculateTimeSteps();

        // Set time range depending on data file
        m_TimeRange = dataFile.GetTimeRange();
        if ( m_TimeRange < 4 * DAY )
           OnTimeRange3(); // if 4 days in data file: show 3 days
        else if ( m_TimeRange < 8 * DAY )
           OnTimeRange7();
        else if ( m_TimeRange < 15 * DAY )
           OnTimeRange14();
        else if ( m_TimeRange < 32 * DAY )
           OnTimeRange30();
        else if ( m_TimeRange < 64 * DAY )
           OnTimeRange60();
        else
           OnTimeRangeAll();

        // Create name of the file containing parameter specifications
        if (dataFile.GetIdCode()>=500)
          PARDATA_Filename = "PARDATA.INI";
        else
        {
          Integer Id = new Integer(dataFile.GetIdCode());
          PARDATA_Filename = "PARDATA" + Id.toString() + ".INI";
        }

        // Add ChangeListener and ActionListener
        AddListeners();

        //jSlider.setBorder(BorderFactory.createRaisedBevelBorder());
     }

     SnowPackFrame GetSnowPackFrame() {return spframe;}

     float GetStartValue() {return m_StartValue; }

     float GetEndValue() {return m_EndValue; }

     int GetColorTab() {return m_ColorTab; }

     void AddListeners()
     {
        jButton_Prev.setMargin(new Insets(0, 0, 0, 0));
        jButton_Next.setMargin(new Insets(0, 0, 0, 0));
        jButton_Home.setMargin(new Insets(0, 0, 0, 0));
        jButton_End.setMargin(new Insets(0, 0, 0, 0));
        jButton_Remove.setMargin(new Insets(0, 0, 0, 0));
        jButton_Up.setMargin(new Insets(0, 0, 0, 0));
        jButton_Down.setMargin(new Insets(0, 0, 0, 0));

        jButton_Prev.setToolTipText("Move to previous date (= PageUp-button)");
        jButton_Next.setToolTipText("Move to following date (= PageDown-button)");
        jButton_Home.setToolTipText("Move to start or to previous time interval (= HOME-button)");
        jButton_End.setToolTipText("Move to end or to next time interval (= END-button)");
        jButton_Remove.setToolTipText("Remove the Slider");
        jButton_Up.setToolTipText("Move to higher value range");
        jButton_Down.setToolTipText("Move to lower value range");

        jSlider.setToolTipText("Drag the pointer to the desired date");

        // Set ChangeListener for slider
        changeListener = new BoundedChangeListener(this);
        jSlider.addChangeListener(changeListener);

        // Set ActionListeners for buttons
        jButton_Prev.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
            mFrame.MarkerMovement("Left", 0);
         }
        });

        jButton_Next.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
            mFrame.MarkerMovement("Right", 0);
         }
        });

        jButton_Home.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
            mFrame.MarkerMovement("Home", 0);
         }
        });

        jButton_End.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
            mFrame.MarkerMovement("End", 0);
         }
        });

        jButton_Up.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
            mFrame.menu_ChangeZoomY_actionPerformed("Move Higher");
         }
        });

        jButton_Down.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
            mFrame.menu_ChangeZoomY_actionPerformed("Move Lower");
         }
        });

        jButton_Remove.addActionListener(new java.awt.event.ActionListener()
        {

          public void actionPerformed(ActionEvent e)
         {
           SnowPackFrame spframe = GetSnowPackFrame();
           if (spframe == null) return;

           spframe.snowPackView.removeAll();
           spframe.repaint();
         }
        });
     }


/*
     boolean FileInDataFilePath()
     // Check if file from current spframe is in the data file path (defined
     // in SETUP.INI-file
     {
        // Current *.pro/met-file
        String ActCompleteFilename = spframe.getInputFile().getPath(); // path + name
        String ActFilename = spframe.getInputFile().getName(); // name only
        String ActPath = ActCompleteFilename.substring
           (0, ActCompleteFilename.length() - ActFilename.length()); // path only

        if (ModelDialog.SlashConverted(ActPath).equals
           (ModelDialog.SlashConverted(Setup.m_DataFilePath)))
           return true;
        else
           return false;
     }
*/

     boolean GetStationParameters()
     // Get station name, altitude, aspect, slope, latitude, longitude.
     // These are read as default from the header of *.pro or *.met files.
     // If no information is found there, a *.sno file is searched for
     // relevant data.
     {

        // Current *.pro/met file which needs the display of
        // station parameters for the graphics
        String ActCompleteFilename = spframe.getInputFile().getPath(); // path + name
        String ActFilename = spframe.getInputFile().getName(); // name only
        String ActPath = ActCompleteFilename.substring
           (0, ActCompleteFilename.length() - ActFilename.length()); // path only

        // File containing specific station data like name, altitude, ...
        String StationDataFile;

        // Check if current *.pro/met file contains the desired data as file
        // header. If the file contains the section name "STATION_PARAMETERS"
        // and a valid entry for "StationName", this is assumed to be the case.
        String checkName = "";
        String sectionName = "";
        try
        {
          IniFile StationDataIni = new IniFile(ActCompleteFilename);
          checkName = StationDataIni.getEntry
               ("STATION_PARAMETERS", "StationName", "");
        }
        catch (IOException e)
        { return false; } // *.pro/met file not found
        if (!checkName.equals(""))
        {
          // Station data searched in *.pro/*.met file.
          StationDataFile = ActCompleteFilename;
          sectionName = "STATION_PARAMETERS";
        }

        else if (ModelDialog.SlashConverted(ActPath).equals
           (ModelDialog.SlashConverted(Setup.m_DataFilePath)))
        // If no relevant header exists in the *pro/met file, and the file to be
        // displayed is in the operationally used data file path:
        // search for the station data file in the SnowFilePath.
        {
          StationDataFile = Setup.m_SnowFilePath
             + ActFilename.substring(0, ActFilename.length()-4) + ".sno";
          sectionName = "SNOWPACK_INITIALIZATION";
        }

        else
        // look for the station data file in the directory where the file
        // to be displayed is located;
        {
          StationDataFile = ActCompleteFilename.substring
             (0, ActCompleteFilename.length()-4) + ".sno";
          sectionName = "SNOWPACK_INITIALIZATION";
        }

        // Read the relevant parameters from the StationDataFile
        // (*.pro, *.met or *.sno-file).
        try
        {
             IniFile StationDataIni = new IniFile(StationDataFile);
             m_StationName = StationDataIni.getEntry
               (sectionName, "StationName", "Undefined Station");
             m_StationAltStr = StationDataIni.getEntry
               (sectionName, "Altitude", "?");
             m_StationAspectStr = StationDataIni.getEntry
               (sectionName, "SlopeAzi", "?");
             m_StationSlopeStr = StationDataIni.getEntry
               (sectionName, "SlopeAngle", "?");
             m_StationLatStr = StationDataIni.getEntry
               (sectionName, "Latitude", "?");
             m_StationLonStr = StationDataIni.getEntry
               (sectionName, "Longitude", "?");

    //Schirmer
    String DepthTemp = StationDataIni.getEntry(sectionName, "DepthTemp", "?");

    depthTemps = DepthTemp.split(",");




             //if (!m_StationName.equals("Undefined Station") && !m_StationAltStr.equals("?") &&
             //    !m_StationAspectStr.equals("?") && !m_StationSlopeStr.equals("?"))
             //  // valid names are found, exit method
        }
        catch (IOException e)
        {
             // File not found or incorrect station data in file
             // --> see if station name can be inferred directly from the data file
             System.out.println("SnowPackDoc.GetStationParameters(): "+
                                "File containing station data not found: " + e);
             m_StationName = "Undefined Station";
             m_StationAltStr = "?";
             m_StationAspectStr = "?";
             m_StationSlopeStr = "?";
             m_StationLatStr = "?";
             m_StationLonStr = "?";
        }

        return true;

/* // Old version of station data finding for non-research version

        // Determine station abbreviation
        String ShortName;
        if (Setup.m_ResearchMode)
           // e.g. "KLO" (from data lines) + "2"
           ShortName = dataFile.GetStatAbbrev() + dataFile.GetSnowStatID();
        else
           // e.g. jj5klo2p.pro --> KLO2
           ShortName = m_StationFileName.substring(3, 7).toUpperCase();

        // Reading *.INI-files
        try
        {
        IniFile StatlistIni = new IniFile(Setup.m_IniFilePath + "STATLIST.INI");
        IniFile StationIni = new IniFile(Setup.m_IniFilePath + "STATION.INI");

        // Extracting station name through abbreviation
        // e.g. KLO2 --> "Klosters Gatschiefer"
        search_Station:
        {
          for (int i=0; i<StatlistIni.getSectionSize(); i++)
          {  String kanton = StatlistIni.getSection(i);
             StatlistIni.setSection(kanton);

             for (int j=0; j<StatlistIni.getKeySize(); j++)
             {
                 if (StatlistIni.getKey(j).trim().substring(1,5).equals(ShortName))
                 {
                    m_StationName = StatlistIni.getEntry(StatlistIni.getKey(j));
                    break search_Station;
                 } // end if
             } //end j loop

          }; // end i loop
          m_StationName = "ErrorName";
        }

        // Extracting station altitude
        //if (Setup.m_ResearchMode)
        {
          // Station altitude is taken from entry which contains the abbreviated
          // station name as found in the file name
          m_StationAltStr = "Error";

          for (int i=0; i<StationIni.getSectionSize(); i++)
          {  String section = StationIni.getSection(i);
             StationIni.setSection(section);
             // Get station abbreviation of current section
             String StatAbbrev = StationIni.getEntry(section, "STAT","").trim();
             if ((StatAbbrev.equals(dataFile.GetStatAbbrev()))
                 && (section.substring(7, 8).equals(dataFile.GetSnowStatID())))
                    // altitude of snow station
                    m_StationAltStr = StationIni.getEntry(section, "HOEHE", "Error");
          }; // end i loop

        }
        //  else
        //{
        //  String StationId = dataFile.GetStationId1() + dataFile.GetStationId2();
        //  m_StationAltStr = StationIni.getEntry(StationId, "HOEHE", "Error");
        //  //int StationAlt = StringToInt(m_StationAltStr);
        //}

        } // end try
        catch (IOException e)
        {
          System.out.println("Catch 1: " + e);
          return false;
        }
*/

     }


     boolean GetDefaultIdParameters(int IdCode)
     // Get some parameters related to the IdCode:
     // ColorStartValue, ColorEndValue: start and end value for color tables
     // ColorTab: code of used color table
     // StartValue, EndValue: start and end value of y-axis (non-layer parameters),
     //    start and end value of x-axis (layer parameters, no soil)
     // SoilStartValue, SoilEndValue: start and end value of x-axis
     //    (layer parameters, soil)
     {
        // Reading *.INI-files
        try
        {

        IniFile ParDataIni;
        ParDataIni = new IniFile(Setup.m_IniFilePath + PARDATA_Filename);

        Integer IdInteger = new Integer(IdCode);
        String IdCodeStr = (String) IdInteger.toString();

        // Extraction of ID-related parameters
        String ColorTabStr = ParDataIni.getEntry
                 (IdCodeStr, "ColorTab",  "ColorTabError");
        try { m_ColorTab = Integer.parseInt(ColorTabStr); } // Code of color table
        catch (NumberFormatException e) {m_ColorTab = 7; }

        String StartValueStr = ParDataIni.getEntry
                 (IdCodeStr, "StartWert", "StartValueError");
        m_ColorStartValue = m_StartValue = StringToFloat(StartValueStr);

        String EndValueStr = ParDataIni.getEntry
                 (IdCodeStr, "EndWert", "EndValueError");
        m_ColorEndValue = m_EndValue = StringToFloat(EndValueStr);

        String SoilStartValueStr = ParDataIni.getEntry
                 (IdCodeStr, "SoilStartWert", "SoilStartValueError");
        m_SoilStartValue = StringToFloat(SoilStartValueStr);

        String SoilEndValueStr   = ParDataIni.getEntry
                 (IdCodeStr, "SoilEndWert", "SoilEndValueError");
        m_SoilEndValue = StringToFloat(SoilEndValueStr);
        } // end try
        catch (IOException e) {
          System.out.println("SnowPackDoc.GetDefaultIdParameters: Catch 1: " + e);
          return false;
        }
        return true;
     }


     String GetAxisText(int IdCode)
     // Get ParameterName, related to the IdCode
     {
         String ParameterName;

         try {
             IniFile ParDataIni;
             ParDataIni = new IniFile(Setup.m_IniFilePath + PARDATA_Filename);

             Integer IdInteger = new Integer(IdCode);
             String IdCodeStr = (String) IdInteger.toString();

             ParameterName = ParDataIni.getEntry(IdCodeStr, "NameE",
                                                 "ParameterNameError");
         } catch (IOException e) {
             if (!Setup.m_FileDialogActive)
                 System.out.println("SnowPackDoc.GetAxisText: Catch 1: " + e);
             return "ParameterNameError";
         }

         //try
         //{   Schirmer
         if ((IdCode == C_DataFile.ID_CODE_TEMPERATURE1) ||
             (IdCode == C_DataFile.ID_CODE_TEMPERATURE2) ||
             (IdCode == C_DataFile.ID_CODE_TEMPERATURE3) ||
             (IdCode == C_DataFile.ID_CODE_TEMPERATURE4) ||
             (IdCode == C_DataFile.ID_CODE_TEMPERATURE5))
         // Recover depth of temperature measurement
         {

             // Current *.pro/met file
             String ActCompleteFilename = spframe.getInputFile().getPath(); // path + name
             String ActFilename = spframe.getInputFile().getName(); // name only
             String ActPath = ActCompleteFilename.substring
                              (0,
                               ActCompleteFilename.length() - ActFilename.length()); // path only

             // File containing heights of snow measurements
             String SnowHeightsFile;

             if (ModelDialog.SlashConverted(ActPath).equals
                 (ModelDialog.SlashConverted(Setup.m_DataFilePath)))
                 // If file to be displayed is in the operationally used data file path:
                 // search for CONSTANTS_User.INI.
                 SnowHeightsFile = Setup.m_DataFilePath + "CONSTANTS_User.INI";

             /*   (Schirmer) the information about temperature depth is now written in the .met file
                        else


               // Search for a file with the same name as the loaded file, but
               // extension *.ini.
                 SnowHeightsFile = ActCompleteFilename.substring
                    (0, ActCompleteFilename.length()-4) + ".ini";

               IniFile ConstantsIni;

               ConstantsIni = new IniFile(SnowHeightsFile);
               String Depth = "??";

               if (IdCode == C_DataFile.ID_CODE_TEMPERATURE1)
                   Depth = ConstantsIni.getEntry("Parameters","DEPTH_1", "??");
               else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE2)
                   Depth = ConstantsIni.getEntry("Parameters","DEPTH_2", "??");
               else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE3)
                   Depth = ConstantsIni.getEntry("Parameters","DEPTH_3", "??");
               else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE4)
                   Depth = ConstantsIni.getEntry("Parameters","DEPTH_4", "??");
               else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE5)
                   Depth = ConstantsIni.getEntry("Parameters","DEPTH_5", "??");

               if (Depth.startsWith("-"))
                   ParameterName += " (Soil, " + Depth + " m)";
               else
                   ParameterName += " (Snow, " + Depth + " m)";
              */


             //} // end try (Schirmer)

             /* Schirmer
                      catch (IOException e) {;
               //System.out.println("SnowPackDoc.GetAxisText: Catch 2: " + e);
               //ParameterName = ParameterName + " (depth not found)";
               //return "ParameterNameError";
                      }*/



             //Schirmer: actual code to detect the temperature depth and wether if its soil or snow
             //information is written down in .met file
             else {
                 String Depth = "??";

                 if (!this.depthTemps[0].equals("?")) {

                     String snp = this.depthTemps[0];

                     if (IdCode == C_DataFile.ID_CODE_TEMPERATURE1)
                         Depth = this.depthTemps[1];
                     else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE2)
                         Depth = this.depthTemps[2];
                     else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE3)
                         Depth = this.depthTemps[3];
                     else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE4)
                         Depth = this.depthTemps[4];
                     else if (IdCode == C_DataFile.ID_CODE_TEMPERATURE5)
                         Depth = this.depthTemps[5];

                     if (snp.equals("1")) {

                         if (Depth.startsWith("-"))
                             ParameterName += " (Soil, " + Depth + " m)";
                         else
                             ParameterName += " (Snow, " + Depth + " m)";
                     }

                     else {

                         ParameterName += " (Snow, " + Depth + " m)";
                     }
                 }
             }
         }

         return ParameterName;

    }
     String GetAxisUnit(int IdCode)
     // Get Unit, related to the IdCode
     {
        String Unit;

        try
        {
        IniFile ParDataIni;
        ParDataIni = new IniFile(Setup.m_IniFilePath + PARDATA_Filename);

        Integer IdInteger = new Integer(IdCode);
        String IdCodeStr = (String) IdInteger.toString();

        Unit = ParDataIni.getEntry(IdCodeStr, "Einheit", "UnitError");
        } // end try
        catch (IOException e) {
          if (!Setup.m_FileDialogActive)
            System.out.println("SnowPackDoc.GetAxisUnit: Catch 1: " + e);
          return "UnitError" ;
        }
        return Unit;
     }


     Color GetParameterColor(int IdCode)
     // Get ParameterColor, related to the IdCode (used for some plots)
     {
        Color ParameterColor = new Color(0,0,0); // Error color: white
        String ColorStr;

        try
        {
        IniFile ParDataIni;
        ParDataIni = new IniFile(Setup.m_IniFilePath + PARDATA_Filename);

        Integer IdInteger = new Integer(IdCode);
        String IdCodeStr = (String) IdInteger.toString();

        ColorStr = ParDataIni.getEntry(IdCodeStr, "Color", "ColorError");

        if (ColorStr.equals("black"))
          ParameterColor = ColorTab.BLK;
        else if (ColorStr.equals("blue"))
          ParameterColor = ColorTab.BLU;
        else if (ColorStr.equals("darkGreen"))
          ParameterColor = ColorTab.D_GRN; // not a Java color constant
        else if (ColorStr.equals("red"))
          ParameterColor = ColorTab.RED;

        } // end try
        catch (IOException e) {
          if (!Setup.m_FileDialogActive)
            System.out.println("SnowPackDoc.GetParameterColor: Catch 1: " + e);
        }
        return ParameterColor;
     }


     public float StringToFloat(String string)
     {
        float f;
        try {
          f = Float.parseFloat(string);  }
        catch (NumberFormatException e) {
          f = (float) -999.9; }
        return f;
     }


     public int StringToInt(String string)
     {
        int i;
        try {
          i = Integer.parseInt(string);  }
        catch (NumberFormatException e) {
          i = -999; }
        return i;
     }

/*   // Conversion of a string to an integer. Not used. Easier using parseInt().
     // Input string is only supposed to consist of digits (check using isDigit() could be implemented)
     // a minus sign (-) can be at the first position
     {
        int value=0;
        int factor=1;
        int len = string.length();
        int i = len - 1;
        do {
          if(string.charAt(i)=='-') return (-1)*value;
          value += factor  * ((int)string.charAt(i)-48);
          factor *= 10;
          i--;
        } while(i>-1);
        return value;
     }
*/


// Sets StartTime, Time (in proDataFile), TimeRange and TimeStep
// Return true,  if ok
// Return false, if error
boolean GetXAxisRange(int IdCode) throws IOException
{
   m_TimeStep = 0;
   m_TimeRange1 = m_TimeRange;

   if ( dataFile == null )
      return false;

   // Reset time range (based on C++-Code, not sure if necessary!!)
   if ( dataFile.GetTimeRange() > m_TimeRange )
   {
      m_TimeRange = dataFile.GetTimeRange(); // Start to end time of input file
      m_TimeRange1 = m_TimeRange;
   }
   /* Schirmer: problem with short files and no data at the end, now as long files
   if ( m_EvenTime )
   {
   // round end time to 12:00 or 00:00
      //m_Time = (GregorianCalendar) dataFile.GetTimeRangeEndTime(); (in C++)

      GetMarkerValue(IdCode);
      m_Time = (GregorianCalendar) m_ActTime.clone();

      if ( m_Time.get(Calendar.HOUR_OF_DAY) < 12 ||
          (m_Time.get(Calendar.HOUR_OF_DAY) == 12 && m_Time.get(Calendar.MINUTE) == 0) )
      {
         // Set end time to 12:00:00
         m_Time.set(Calendar.HOUR_OF_DAY, 12);
         m_Time.set(Calendar.MINUTE, 0);
         m_Time.set(Calendar.SECOND, 0);
      }
      else
      {
         // Set end time to 00:00:00 of next day
         m_Time.set(Calendar.HOUR_OF_DAY, 0);
         m_Time.set(Calendar.MINUTE, 0);
         m_Time.set(Calendar.SECOND, 0);
         m_Time.add(Calendar.DAY_OF_MONTH, 1);
         // ((DataFile) dataFile).LoopTest();
      }

      m_StartTime = (GregorianCalendar) m_Time.clone();
      m_StartTime.add(Calendar.SECOND, -m_TimeRange);

      // recalculate time range
      dataFile.SetTimeRange(m_StartTime, m_Time);
   }
   else // not EvenTime
   {
      m_StartTime = dataFile.GetTimeRangeStartTime();
   }*/
    //Schirmer instead above
    m_StartTime = dataFile.GetTimeRangeStartTime();
    //end Schirmer

   // Calculate time step
   if (dataFile.GetTimeResolution( IdCode ) == 0) // MinTimeStep = 0
      return false;

   // Minimum time step available in the input data
   int MinTimeStep = dataFile.GetTimeResolution (IdCode);
   int MinTimeStepRounded = dataFile.RoundTime
     ( MinTimeStep, (int) ( Setup.m_MaxTimeResolution * MINUTE ));
   //System.out.println("MinTimeStepRounded, m_MinTimeStep:"+ MinTimeStepRounded + " " +m_MinTimeStep);

   // Time step used for data display is maximum of rounded data availabe and
   // m_MinTimeStep (this value is set according to the time range in
   // spDoc.DefineTimeRange)
   m_TimeStep = Math.max(MinTimeStepRounded, m_MinTimeStep);

   // Code from C++-version. Not sure if needed!!
   // Corrections in OnMarkerBackward/Forward and spFrame.timeRangeSet
   //   (time step added) could be done here.
   if ( !m_EvenTime )
       m_TimeRange1 += m_TimeStep; // add time step to display last entry

/*
   // Slider handling
   jSlider.setMinimum(0);
   // Total number of time steps
   jSlider.setMaximum(m_TimeRange1 / m_TimeStep);
   // Time steps from start time till active time
   jSlider.setValue(dataFile.HowMany(m_StartTime, m_ActTime, m_TimeStep));

   //m_SliderPortion = (dataFile.m_ActDataEntryIndex - dataFile.m_ActStartTimeIndex) /
   //                  (dataFile.m_ActEndTimeIndex - dataFile.m_ActStartTimeIndex);
*/
   return true;
}


boolean GetYAxisRange(int IdCode)
// determines:  m_YMinValue, m_YMaxValue, m_yNrOfGrids,
//              MAX_Y_ZOOM_VALUE, MIN_Y_ZOOM_DIFFERENCE
{
   // 1st guess, no research mode
   m_yNrOfGrids = 4;
   if (IdCode >= 500) // layer parameters
   {
     m_YMinValue = 0;
     m_YMaxValue = Setup.m_YMaxValue;
     m_XMinValue = m_StartValue; // Read from PARDATA.INI in GetDefaultIdParameters()
     m_XMaxValue = m_EndValue;
   }
   else // non-layer parameters
   {
     m_YMinValue = m_StartValue; // Read from PARDATA*.INI in GetDefaultIdParameters()
     m_YMaxValue = m_EndValue;
   }
   m_YMinValue_org = m_YMinValue;
   m_YMaxValue_org = m_YMaxValue;

   // Calculate number of parameters to be plotted
   if (IdCode < 500) AssociatedIdCodes(IdCode);
   else NrOfParameters = 1;

   // Automated adjustment if research mode and just one parameter plotted per graph
   if (Setup.m_ResearchMode &&  NrOfParameters==1 &&
       dataFile.CalculateMinMaxValue(IdCode)) // data for IdCode exist, also calculates m_IdSoilDataExist
   {
     // Get m_YMinValue, m_YMaxValue, m_yNrOfGrids:
     m_YMaxValue = dataFile.m_MaxValue;

     if ((Setup.m_SoilDataDisplay && dataFile.m_IdSoilDataExist)||(IdCode < 500))
       m_YMinValue = dataFile.m_MinValue;
     else
       m_YMinValue = 0;


     m_YMinValue_org = m_YMinValue;
     m_YMaxValue_org = m_YMaxValue;

     if (GetAxisText(IdCode).indexOf("Wind Direction")>=0)
         // wind direction: fixed scale
         { m_YMinValue = 0; m_YMaxValue = 360; m_yNrOfGrids = 4; }
     else
     {
       Adjust(m_YMinValue, m_YMaxValue);
       // --> adjust m_YMinValue, m_YMaxValue, m_yNrOfGrids to "even" numbers
       m_YMinValue = m_MinValue;
       m_YMaxValue = m_MaxValue;
       m_yNrOfGrids = m_NrOfGrids;
     }

     if (IdCode >= 500) // layer parameters
     {  // Get m_XMinValue, m_XMaxValue:
       m_XMinValue = dataFile.m_IdMinValue;
       m_XMaxValue = dataFile.m_IdMaxValue;
       Adjust(m_XMinValue, m_XMaxValue);
         // --> adjust m_XMinValue, m_YXaxValue to "even" numbers
       m_XMinValue = m_MinValue;
       m_XMaxValue = m_MaxValue;
       //m_xNrOfGrids = m_NrOfGrids;
     }

   }


   // Allow application of zoom out function at least three times
   MAX_Y_ZOOM_VALUE = m_YMinValue + 8 * (m_YMaxValue - m_YMinValue);

   // Allow application of zoom in function at least three times
   MIN_Y_ZOOM_DIFFERENCE = (m_YMaxValue - m_YMinValue) / 8;



   return true;
}


// Return  true if IdCode is in data file
//         false if not
boolean IdCodeExists(int IdCode)
{
   if ( dataFile == null )
      return false;
   else
      return dataFile.IdCodeExists( IdCode );
}


// Return true,  if ok
// Return false, if error
// Delivers ActDataEntry and ActTime (first valid time for ID code)
boolean GetFirstValue(int IdCode)
{
   if ( dataFile == null )
      return false;

   dataFile.MoveToFirstDataEntry();

   // search next line with valid ID code
   while (!dataFile.CheckActDataValid( IdCode )) // data not valid
   {
      if ( dataFile.MoveToNextDataEntry() != 0 ) // data entry in time range
      {
         return false;
      }
   }

   m_ActDataEntry = dataFile.GetActDataEntry();


   if (m_ActDataEntry != null)
   {
      m_ActTime = dataFile.GetActDataTime();
      return true;
   }
   else
   {
      return false;
   }
}


// Return true,  if ok
// Return false, if error
// Delivers ActDataEntry and ActTime (first valid time for ID code)
boolean GetNextValue(int IdCode)
{
   if ( dataFile == null )
      return false;

   // search next line with valid id code
   do
   {
      if ( dataFile.MoveToNextDataEntry(m_TimeStep, m_MinCatchTime, m_MinCatchTime) != 0 )
      {
         return false;
      }
   } while (!dataFile.CheckActDataValid( IdCode ));


   m_ActDataEntry = dataFile.GetActDataEntry();


   if (m_ActDataEntry != null)
   {
      m_ActTime = dataFile.GetActDataTime();
      return true;
   }
   else
   {
      return false;
   }
}


// Return true,  if ok
// Return false, if error or no marker is set
// m_ActTime and m_ActDataEntry are set to the current marker position.
//
boolean GetMarkerValue(int IdCode)
{
   if ( dataFile == null )
      return false;

   if ( m_MarkerDataEntry == null )
   // m_MarkerDataEntry is set to null in the methods where the marker position
   // is changed (marker(...)).
   // In these methods, the m_ActDataEntry (in dataFile) is set to a new value.
   // This value is set to the m_MarkerDataEntry in the method GetNewMarkerValue().
   {
      return GetNewMarkerValue(IdCode);
   }
   else
   {
      m_ActTime = m_MarkerDataEntry.GetTime();
      m_ActDataEntry = m_MarkerDataEntry;

      return true;
   }
}


// Return true,  if ok
// Return false, if error or no marker is set
boolean GetNewMarkerValue(int IdCode)
{
   if ( dataFile == null )
      return false;

   m_MarkerDataEntry = dataFile.GetActDataEntry(); // save new marker position

   if (m_MarkerDataEntry != null && dataFile.CheckActDataValid(IdCode))
   {
      m_ActTime = m_MarkerDataEntry.GetTime();
      m_ActDataEntry = m_MarkerDataEntry;

      return true;
   }
   else
   {
      return false;
   }
}


// Calculate new time range depending on the current marker position.
// Sets the TimeRange.
// Return true,  if ok
// Called by OnTimeRange() etc.
boolean CalculateTimeRange()
{
   if ( dataFile != null )
   {
      // set the new time range:
      // the current marker position is the END TIME of the new time range
      GregorianCalendar TimeRangeEndTime = dataFile.GetActDataTime();
      GregorianCalendar DataFileStartTime = dataFile.GetStartTime();

      //C++: if ( (TimeRangeTime - CTimeSpan((time_t) TimeRange)) < DataFileStartTime )
      GregorianCalendar TimeClone = (GregorianCalendar) TimeRangeEndTime.clone();
      TimeClone.add(Calendar.SECOND, -m_TimeRange);
      if (TimeClone.before(DataFileStartTime))
      // ...meaning time range passed to this method is bigger than the
      // difference between start and end time, available in the actual settings
      {
         // start time of new time range must be >= data file start time
         dataFile.SetTimeRange(DataFileStartTime, m_TimeRange );
         // now end time is after the previous end time
      }
      else
      {
         // start time of new time range must be >= data file start time
         dataFile.SetTimeRange(m_TimeRange, TimeRangeEndTime);
         // now start time is after the previous start time
      }

      return true;
   }

   return false;
}


// calculate time steps after TimeRange has been set
// Return true,  if ok
boolean CalculateTimeSteps()
{
   if ( m_TimeRange <= (7 * DAY) )
   {
      // If time range <= 7 days, then set time step to 1 hour.
      // = 24 values per day * 7 days = 168 values
      m_MinTimeStep = HOUR;
      m_MinCatchTime = 5 * MINUTE;  // -5 minutes
   }
   else if ( m_TimeRange <= (14 * DAY) )
   {
      // If time range <= 14 days, then set time step to 3 hours.
      // = 8 values per day * 14 days = 112 values
      m_MinTimeStep = 3 * HOUR;
      m_MinCatchTime = 15 * MINUTE;  // -15 minutes
   }
   else if ( m_TimeRange <= (30 * DAY) )
   {
      // If time range <= 30 days, then set time step to 6 hours.
      // = 4 values per day * 30 days = 120 values
      m_MinTimeStep = 6 * HOUR;
      m_MinCatchTime = 15 * MINUTE;  // -15 minutes
   }
   else if ( m_TimeRange <= (60 * DAY) )
   {
      // If time range <= 60 days, then set time step to 12 hours.
      // = 2 values per day * 60 days = 120 values
      m_MinTimeStep = 12 * HOUR;
      m_MinCatchTime = 15 * MINUTE;  // -15 minutes
   }
   else
   {
      // If time range > 60 days, then set time step to 24 hours.
      // = 1 value per day
      m_MinTimeStep = DAY;
      m_MinCatchTime = 15 * MINUTE;  // -15 minutes
   }



   //added by Schirmer and Steiniger
   //reason: did not painted .met files if first (smallest) time step is not consistent (+-15 min) with the other time steps
   if (m_MinTimeStepInData / 2 > m_MinCatchTime) {
       m_MinCatchTime = m_MinTimeStepInData / 2;
}


   // If always best resolution is wished, do not use time range-dependent
   // minimum time steps.
   if (Setup.m_AlwaysBestResolution) m_MinTimeStep = 1; // (= 1 second)

   return true;
}


// returns true if SnowpackView has to be repainted (already at start of time range)
//         false otherwise
// button = "Home" or "LeftArrow"
boolean OnMarkerBackward(String button)
{
   if ( dataFile != null )
   {
      GregorianCalendar TimeRangeStartTime = dataFile.GetTimeRangeStartTime();
      GregorianCalendar DataFileStartTime = dataFile.GetStartTime();

      dataFile.SetActDataEntry(m_MarkerDataEntry); // set act data entry to marker

      // C++: if ( dataFile.GetActDataTime() >= (TimeRangeStartTime + CTimeSpan((time_t) m_TimeStep)) )

      GregorianCalendar ActDataTimeClone = dataFile.GetActDataTime();
      ActDataTimeClone.add(Calendar.SECOND, -m_TimeStep); // One time step before marker position
      if (!ActDataTimeClone.before(TimeRangeStartTime))
      {
      // Marker not at start of time range yet

         if (button == "Home")
         {  // Move to first entry in time range
            if ( dataFile.MoveToFirstDataEntry() == 0 )
            {
               // m_MarkerDataEntry = null; // marker set to null in SPFrame (method marker("Home"))
               return false;
            }
         }
         else if (button == "LeftArrow")
         {
            // Move to previous data entry containing valid data.
            // Time range is not quitted.
            int StepsBackward = 1;
            do {
              int SavActDataEntryIndex = dataFile.m_ActDataEntryIndex;

              if ( dataFile.MoveToPrevDataEntry
                  (StepsBackward * m_TimeStep, m_MinCatchTime, m_MinCatchTime) == 0 )
                   return false;

              dataFile.m_ActDataEntryIndex = SavActDataEntryIndex;
              dataFile.m_ActDataEntry = (DataEntry)
                  dataFile.m_DataEntries.elementAt(SavActDataEntryIndex);

              ActDataTimeClone.add(Calendar.SECOND, -m_TimeStep);
              StepsBackward++;
            } while(!ActDataTimeClone.before(TimeRangeStartTime));
         }
      }
      else if ( TimeRangeStartTime.after(DataFileStartTime) ) // not equal!
      {
         if (button == "Home")
            // Move range back by a time span (scrolling)
            TimeRangeStartTime.add(Calendar.SECOND, -m_TimeRange); // was m_TimeRange/2 before
         else if (button == "LeftArrow")
            // Move range back by a time step
            TimeRangeStartTime.add(Calendar.SECOND, -m_TimeStep);

         // Necessary because in GetXAxisRange() time range start/end is rounded upward.
         TimeRangeStartTime.add(Calendar.SECOND, -m_TimeStep);
         DataFileStartTime.add(Calendar.SECOND, -m_TimeStep);

         if ( TimeRangeStartTime.before(DataFileStartTime))
            dataFile.SetTimeRange(DataFileStartTime, m_TimeRange);
            // C++: - CTimeSpan((time_t) DAY);
         else
            dataFile.SetTimeRange(TimeRangeStartTime, m_TimeRange);

         // Set start and end time for animation
         m_AnimationStartTime = GetAnimationStartTime();
         m_AnimationEndTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();

         m_MarkerDataEntry = null; // marker not set
         return true;
      }
   }

   // Beep because file start is reached or other problems
   //Toolkit.getDefaultToolkit().beep();
   return false;
}


// returns true if SnowpackView has to be repainted, otherwise false
// button = "End" or "RightArrow"
boolean OnMarkerForward(String button)
{
   if ( dataFile != null )
   {
      GregorianCalendar TimeRangeEndTime = dataFile.GetTimeRangeEndTime();
      GregorianCalendar DataFileEndTime = dataFile.GetEndTime();

      dataFile.SetActDataEntry(m_MarkerDataEntry); // set act data entry to marker

      // C++: if ( proDataFile.GetActDataTime() <= (TimeRangeEndTime - CTimeSpan((time_t) m_TimeStep)) )
      // Marker not at end of time range yet
      GregorianCalendar ActDataTimeClone = dataFile.GetActDataTime();
      ActDataTimeClone.add(Calendar.SECOND, m_TimeStep);
      if (!ActDataTimeClone.after(TimeRangeEndTime))
      {
         if (button == "End")
         {  // move to last entry in time range
            if ( dataFile.MoveToLastDataEntry() == 0 )
            {
               //m_MarkerDataEntry = null; // marker set to null in SPFrame (method marker("End"))
               return false;
            }
         }
         else if (button == "RightArrow")
         {
            // Move to next data entry containing valid data.
            // Time range is not quitted.
            int StepsForward = 1;
            do {
              if ( dataFile.MoveToNextDataEntry
                  (StepsForward * m_TimeStep, m_MinCatchTime, m_MinCatchTime) == 0 )
                   return false;
              ActDataTimeClone.add(Calendar.SECOND, m_TimeStep);
              StepsForward++;
            } while(!ActDataTimeClone.after(TimeRangeEndTime));
         }
      }
      else if ( TimeRangeEndTime.before(DataFileEndTime))
      {
         if (button == "End")
           // Move range forward by a time span
           TimeRangeEndTime.add(Calendar.SECOND, m_TimeRange);
         else if (button == "RightArrow")
           // Move range forward by a time step
           TimeRangeEndTime.add(Calendar.SECOND, m_TimeStep);

         if ( TimeRangeEndTime.after(DataFileEndTime))
            dataFile.SetTimeRange(m_TimeRange, DataFileEndTime);
         else
            dataFile.SetTimeRange(m_TimeRange, TimeRangeEndTime);

         // Set start and end time for animation
         m_AnimationStartTime = GetAnimationStartTime();
         m_AnimationEndTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();

         m_MarkerDataEntry = null; // marker not set
         return true;
      }
   }

   // Beep because file end is reached or other problems
   //Toolkit.getDefaultToolkit().beep();
   return false;
}


void OnTimeRange3() { DefinedTimeRange(3, 6);}    // 2 grids per day
void OnTimeRange7() { DefinedTimeRange(7, 7);}    // 1 grid per day
void OnTimeRange14() { DefinedTimeRange(14, 7);}  // 1 grid per 2 days
void OnTimeRange30() { DefinedTimeRange(30, 6);}  // 1 grid per 5 days
void OnTimeRange60() { DefinedTimeRange(60, 6);}  // 1 grid per 10 days


GregorianCalendar GetAnimationStartTime()
// Calculates the start time for the animation default
{
   int resolution = dataFile.GetTimeResolution(spframe.IdCode);
   GregorianCalendar TempTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();
   GregorianCalendar StartTime = (GregorianCalendar) dataFile.m_ActStartTime.clone();

   TempTime.add(Calendar.SECOND,
      (-1) * Math.max(m_MinTimeStepInData, m_MinTimeStep) * Setup.m_DefaultAnimationSteps);

   if (StartTime.after(TempTime))
     return (GregorianCalendar) StartTime.clone();
   else
     return TempTime;
}


void DefinedTimeRange(int days, int grids)
{
   m_EvenTime = true;
   m_TimeRange = days * DAY;
   m_TimeGrid = grids;
   CalculateTimeRange();
   CalculateTimeSteps();
   //m_AnimationStartTime = (GregorianCalendar) dataFile.m_ActStartTime.clone();
   m_AnimationStartTime = GetAnimationStartTime();
   m_AnimationEndTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();
}


void OnTimeRangeAll()
{
   m_EvenTime = false;
   dataFile.SetTimeRangeAll();
   m_TimeRange = dataFile.GetTimeRange();
   m_TimeGrid = 8;
   CalculateTimeSteps();
   m_AnimationStartTime = GetAnimationStartTime();
   m_AnimationEndTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();
}


void OnTimeRange(GregorianCalendar StartTime, GregorianCalendar EndTime)
{
   m_EvenTime = false;
   dataFile.SetTimeRange(StartTime, EndTime);
   m_TimeRange = dataFile.GetTimeRange();
   m_TimeGrid = 8;
   CalculateTimeSteps();
   m_AnimationStartTime = GetAnimationStartTime();
   m_AnimationEndTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();
}


void OnZoomIn()
// Display half of previous interval
{
   float YAxisDiff = m_YMaxValue - m_YMinValue;
   //if ( YAxisDiff > MIN_Y_ZOOM_DIFFERENCE )
     if (m_YMinValue == 0) // only decrease YMaxValue
     {
        m_YMaxValue = m_YMaxValue - YAxisDiff / 2;
     }
     else // decrease YMaxValue, increase YMinValue
     {
        m_YMaxValue = m_YMaxValue - YAxisDiff / 4;
        m_YMinValue = m_YMinValue + YAxisDiff / 4;
     }
   //else
   //  Toolkit.getDefaultToolkit().beep();

   // System.out.println("Doc, OnZoomIn: YMAX=" + m_YMaxValue);
}


void OnZoomOut()
// Double display interval
{
   float YAxisDiff = m_YMaxValue - m_YMinValue;
   //if ( m_YMaxValue <= MAX_Y_ZOOM_VALUE )
      if (m_YMinValue == 0) // only increase YMaxValue
        	m_YMaxValue = m_YMinValue + 2 * YAxisDiff;
      else // increase YMaxValue, decrease YMinValue
      {
        m_YMaxValue = m_YMaxValue + YAxisDiff / 2;
        m_YMinValue = m_YMinValue - YAxisDiff / 2;
      }
   //else
   //   Toolkit.getDefaultToolkit().beep();

   // System.out.println("Doc, OnZoomOut: YMAX=" + m_YMaxValue);
}


void OnMoveHigher()
// Move to higher interval on y-axis.
{
   float YAxisDiff = m_YMaxValue - m_YMinValue;
   if (m_YMaxValue > m_YMaxValue_org)
   {
     return;
   }
   else
   {
     m_YMaxValue = m_YMaxValue + YAxisDiff;
     m_YMinValue = m_YMinValue + YAxisDiff;
   }
 }


void OnMoveLower()
// Move to lower interval on y-axis.
{
   float YAxisDiff = m_YMaxValue - m_YMinValue;
   if (!Setup.m_SoilDataDisplay && (m_YMinValue < YAxisDiff) && (dataFile.GetIdCode()>=500))
   // Don't go below zero if no soil data display wished
   {
     m_YMinValue = 0;
     m_YMaxValue = YAxisDiff;
   }
   else if (m_YMinValue < m_YMinValue_org)
   {
     return;
   }
   else
   {
     m_YMaxValue = m_YMaxValue - YAxisDiff;
     m_YMinValue = m_YMinValue - YAxisDiff;
   }
}


void OnSetYAxis(float YMinValue, float YMaxValue, int yNrOfGrids )
{
      m_YMinValue = YMinValue;
      m_YMaxValue = YMaxValue;
      m_yNrOfGrids = yNrOfGrids;
}


void OnSetXAxis(float XMinValue, float XMaxValue, int xNrOfGrids)
{
      m_StartValue = m_SoilStartValue = XMinValue;
      m_EndValue = m_SoilEndValue = XMaxValue;
      m_xNrOfGrids = xNrOfGrids;
}


void OnSetColorParameters(float StartValue, float EndValue, int ColorTab)
{
      m_ColorStartValue = StartValue;
      m_ColorEndValue = EndValue;
      m_ColorTab = ColorTab;
}


void OnSetBackGrnd(String color)
{
      if (color.equals("white"))
      {
        m_Background = Color.white;
        m_Foreground = Color.black;
      }
      else if (color.equals("lightGray"))
      {
        m_Background = new Color(230, 230, 230);
        m_Foreground = Color.black;
      }
      else if (color.equals("black"))
      {
        m_Background = Color.black;
        m_Foreground = Color.white;
      }
}


void Adjust(float Min, float Max)
// Input Max, Min: maximum and minimum value found in data file (e.g. snow depth
//   or temperature found in any layer and data entry of the file)
// Output: "nicely" rounded minumum and maximum values for the x- or y-axis
//  (m_MinValue, m_MaxValue), number if separation lines (m_NrOfGrids)
{
      //System.out.println("Max, Min: "+Max+" "+Min);

      if (Max == Min) // Max = Min
      {
        m_MinValue = Min - 1;
        m_MaxValue = Max + 1;
        m_NrOfGrids = 2;
        return;
      }

      // Calculate magnitude
      int Mag = 0;
      for (int i = -10; i <= 10; i++)
      {
          if ((Max-Min) >= Math.pow(10.0, i) && (Max-Min) < Math.pow(10.0, i+1))
          { Mag = i; break; }
      }

      // Calculate adjusted minimum
      if (Min >= 0)
        Min = ((int) (Min / Math.pow(10.0, Mag))    ) * (float) Math.pow(10.0, Mag);
      else
      {
        if ((Min / Math.pow(10.0, Mag)) != (float) ((int) (Min / Math.pow(10.0, Mag))))
          Min = ((int) (Min / Math.pow(10.0, Mag)) -1 ) * (float) Math.pow(10.0, Mag);
      }

      // Calculate adjusted maximum
      float NewMax = Min;
      int intervals = 0;
      do {
        intervals++;
        if (intervals==11)
          // add one more interval, to avoid 11 partition lines (does not look good)
        {
          NewMax += (float) Math.pow(10.0, Mag) * 2;
          intervals = 12;
          Max = NewMax; break;
        }
        NewMax += (float) Math.pow(10.0, Mag);
        if (NewMax >= Max) {Max = NewMax; break; }

      } while(true);

      m_MinValue = Min;
      m_MaxValue = Max;

      // Determine the best number of y-grid partitions
      switch (intervals)
      {
        case 1: { m_NrOfGrids = 5; break; }
        case 2: { m_NrOfGrids = 4; break; }
        case 3: { m_NrOfGrids = 6; break; }
        case 4: { m_NrOfGrids = 4; break; }
        case 5: { m_NrOfGrids = 5; break; }
        case 6: { m_NrOfGrids = 6; break; }
        case 7: { m_NrOfGrids = 7; break; }
        case 8: { m_NrOfGrids = 4; break; }
        case 9: { m_NrOfGrids = 6; break; }
        case 10:{ m_NrOfGrids = 5; break; }
        case 11:{} // does not emerge
        case 12:{ m_NrOfGrids = 6; break; }
      }
      //System.out.println("Min, Max, Mag: " + Min + " " + Max + " " + Mag);

      return;
}


void AssociatedIdCodes(int IdCode0)
// Some graphs require more than one parameter to be painted.
// This method calculates for the parameter given by IdCode0 the following
// member variables:
// NrOfParameters: total number of parameters to be plotted in this graph
// IdCode[]: the Id codes of the parameters to be plotted in this graph
// Name2[]: the specific names of the parameters to be plotted

{
   NrOfParameters = 1; // Number of parameters to be plotted, changed below
   IdCode[0] = IdCode0;
   Name2[0] = "Param. info missing!";

   // Extraction of information with which parameter the current parameter
   // IdCode0 should be printed together
   try
   {
     IniFile ParDataIni = new IniFile(Setup.m_IniFilePath + PARDATA_Filename);
     String IdCodeStr = (String) (new Integer(IdCode0)).toString();
     String PrintedWith   = ParDataIni.getEntry(IdCodeStr, "PrintedWith", "");

     StringTokenizer st = new StringTokenizer(PrintedWith, ",");


     Name2[0] = ParDataIni.getEntry(IdCodeStr, "Name2", "Param. info missing!");
     while (st.hasMoreTokens() && NrOfParameters <= 30)
     {
       IdCode[NrOfParameters] = Integer.parseInt(st.nextToken());
       //String IdCodeStr = (String) (new Integer(IdCode[NrOfParameters])).toString();
       Name2[NrOfParameters] = ParDataIni.getEntry(
                     (String) (new Integer(IdCode[NrOfParameters])).toString(),
                     "Name2", "Param. info missing!");
       NrOfParameters++;
     }
   }
   catch (IOException e) {}

}

void SaveDataFile()
// Saving some key parameters of DataFile in SnowPackDoc
// Not all of them may actually be needed.
{
     datafile_m_GetStartTimeIndex = dataFile.m_GetStartTimeIndex;
     datafile_m_GetEndTimeIndex = dataFile.m_GetEndTimeIndex;
     datafile_m_ActDataEntryIndex = dataFile.m_ActDataEntryIndex;
     datafile_m_ActDataEntry =
      (DataEntry) dataFile.m_DataEntries.elementAt(dataFile.m_ActDataEntryIndex);
     datafile_m_ActStartTimeIndex = dataFile.m_ActStartTimeIndex;
     datafile_m_ActEndTimeIndex = dataFile.m_ActEndTimeIndex;
     datafile_m_ActStartTime = (GregorianCalendar) dataFile.m_ActStartTime.clone();
     datafile_m_ActEndTime = (GregorianCalendar) dataFile.m_ActEndTime.clone();
     datafile_m_TimeStepsWithoutData = dataFile.m_TimeStepsWithoutData;
     datafile_m_IdMinValue = dataFile.m_IdMinValue;
     datafile_m_IdMaxValue = dataFile.m_IdMaxValue;
     datafile_m_IdSoilDataExist = dataFile.m_IdSoilDataExist;
     datafile_m_SoilDataExist = dataFile.m_SoilDataExist;
}


void RetrieveDataFile()
// Retrieving some key DataFile parameters from SnowPackDoc
// Not all of them may actually be needed.
{
       dataFile.m_GetStartTimeIndex = datafile_m_GetStartTimeIndex;
       dataFile.m_GetEndTimeIndex = datafile_m_GetEndTimeIndex;
       dataFile.m_ActDataEntryIndex = datafile_m_ActDataEntryIndex;
       dataFile.m_ActDataEntry = (DataEntry) dataFile.m_DataEntries.elementAt(datafile_m_ActDataEntryIndex);
       dataFile.m_ActStartTimeIndex = datafile_m_ActStartTimeIndex;
       dataFile.m_ActEndTimeIndex = datafile_m_ActEndTimeIndex;
       if (datafile_m_ActStartTime != null)
         dataFile.m_ActStartTime = (GregorianCalendar) datafile_m_ActStartTime.clone();
       if (datafile_m_ActEndTime != null)
         dataFile.m_ActEndTime = (GregorianCalendar) datafile_m_ActEndTime.clone();
       dataFile.m_TimeStepsWithoutData = datafile_m_TimeStepsWithoutData;
       dataFile.m_IdMinValue = datafile_m_IdMinValue;
       dataFile.m_IdMaxValue = datafile_m_IdMaxValue;
       dataFile.m_IdSoilDataExist = datafile_m_IdSoilDataExist;
       dataFile.m_SoilDataExist = datafile_m_SoilDataExist;
}

    private void jbInit() throws Exception {
    }

}
