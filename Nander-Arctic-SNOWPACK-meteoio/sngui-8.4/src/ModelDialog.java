///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualisierung
//Version:
//Copyright:    Copyright (c) 1999
//Autor:       Spreitzhofer
//Organisation:      SLF
//Beschreibung:  Java-Version von SnowPack.
//Integriert die C++-Version von M. Steiniger und die IDL-Version von M. Lehning.
///////////////////////////////////////////////////////////////////////////////
// ModelDialog: Input of data needed to operate the SNOWPACK model
///////////////////////////////////////////////////////////////////////////////
package ProWin;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;

public class ModelDialog extends JDialog
{
  JPanel panel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JPanel jPanel2 = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout3 = new XYLayout();
  JButton jB_Run = new JButton();
  JButton jButton_Cancel = new JButton();
  JFileChooser jfc = new JFileChooser();

  MenuFrame mFrame;
  boolean reading_ok;
  boolean let_run = false;
  String userInputFile; // CONSTANTS_User.INI or user defined filename

  String MODEL_DIRECTORY = null;
  String SNOWFILE = null;
  String METEOFILE = null;
  String METEO_STEP_LENGTH = null;
  String MEAS_TSS = null;
  String ENFORCE_MEASURED_SNOW_HEIGHTS = null;
  String SW_REF = null;
  String INCOMING_LONGWAVE = null;
  String T_INTERNAL = null;
  String HEIGHT_OF_WIND_VALUE = null;
  String HEIGHT_OF_METEO_VALUES = null;
  String NEUTRAL = null;
  String ROUGHNESS_LENGTH = null;
  String THRESH_CHANGE_BC = null;
  String GEO_HEAT = null;
  String SOIL_ALBEDO = null;
  String BARE_SOIL_z0 = null;
  String CALCULATION_STEP_LENGTH = null;
  String CHANGE_BC = null;
  String SNP_SOIL = null;
  String SOIL_FLUX = null;
  String DEPTH_1 = null;
  String DEPTH_2 = null;
  String DEPTH_3 = null;
  String DEPTH_4 = null;
  String DEPTH_5 = null;
  String TS_WRITE = null;
  String TS_START = null;
  String TS_DAYS_BETWEEN = null;
  String PROF_WRITE = null;
  String PROF_START = null;
  String PROF_DAYS_BETWEEN = null;
  String OUTPATH = null;
  String RESEARCH_STATION = null;
  String EXPERIMENT = null;
  String CANOPY = null;
  String SNOW_RED = null; //Schirmer

  String PROF_EVAL = null;
  String PROF_FILE = null;

  String CHANGE_BC_labels[] =
  { "Neumann throughout",
    "Dirichlet if Ts < 0, Neumann else" };
  String SOIL_FLUX_labels[] =
  { "Neumann (fixed GeoThermalHeatFlux)",
    "Dirichlet (fixed Temperature)" };

  JLabel jLabel_Error = new JLabel();
  JPanel jPanel4 = new JPanel();
  XYLayout xYLayout5 = new XYLayout();
  JPanel jPanel5 = new JPanel();
  XYLayout xYLayout6 = new XYLayout();
  JPanel jPanel6 = new JPanel();
  XYLayout xYLayout7 = new XYLayout();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel7 = new JPanel();
  XYLayout xYLayout8 = new XYLayout();
  JPanel jPanel8 = new JPanel();
  XYLayout xYLayout9 = new XYLayout();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JButton jButton_TS_WRITE = new JButton();
  JButton jButton_PROF_WRITE = new JButton();
  JTextField jText_TS_START = new JTextField(6);
  JTextField jText_PROF_START = new JTextField(6);
  JTextField jText_TS_DAYS_BETWEEN = new JTextField(6);
  JTextField jText_PROF_DAYS_BETWEEN = new JTextField(6);
  JPanel jPanel9 = new JPanel();
  XYLayout xYLayout10 = new XYLayout();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JTextField jText_OUTPATH = new JTextField(12);
  JTextField jText_RESEARCH_STATION = new JTextField(12);
  JTextField jText_EXPERIMENT = new JTextField(12);
  JPanel jPanel10 = new JPanel();
  XYLayout xYLayout11 = new XYLayout();
  JPanel jPanel11 = new JPanel();
  XYLayout xYLayout12 = new XYLayout();
  JPanel jPanel12 = new JPanel();
  XYLayout xYLayout13 = new XYLayout();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JPanel jPanel14 = new JPanel();
  XYLayout xYLayout15 = new XYLayout();
  JTextField jText_METEO_STEP_LENGTH = new JTextField(6);
  JLabel jLabel14 = new JLabel();
  JLabel jLabel15 = new JLabel();
  JTextField jText_HEIGHT_OF_WIND_VALUE = new JTextField(6);
  JTextField jText_HEIGHT_OF_METEO_VALUES = new JTextField(6);
  JPanel jPanel15 = new JPanel();
  XYLayout xYLayout16 = new XYLayout();
  JPanel jPanel16 = new JPanel();
  XYLayout xYLayout17 = new XYLayout();
  JPanel jPanel17 = new JPanel();
  XYLayout xYLayout18 = new XYLayout();
  JLabel jLabel16 = new JLabel();
  JPanel jPanel18 = new JPanel();
  JLabel jLabel17 = new JLabel();
  JTextField jText_CALCULATION_STEP_LENGTH = new JTextField(6);
  XYLayout xYLayout19 = new XYLayout();
  JPanel jPanel19 = new JPanel();
  XYLayout xYLayout20 = new XYLayout();
  JLabel jLabel18 = new JLabel();
  JLabel jLabel19 = new JLabel();
  JLabel jLabel20 = new JLabel();
  JLabel jLabel21 = new JLabel();
  JComboBox jComboBox_CHANGE_BC = new JComboBox(CHANGE_BC_labels);
  JComboBox jComboBox_SOIL_FLUX = new JComboBox(SOIL_FLUX_labels);
  JButton jButton_SNP_SOIL = new JButton();
  JLabel jLabel25 = new JLabel();
  JLabel jLabel26 = new JLabel();
  JLabel jLabel27 = new JLabel();
  JLabel jLabel28 = new JLabel();
  JButton jButton_Restore = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel32 = new JLabel();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel21 = new JPanel();
  XYLayout xYLayout4 = new XYLayout();
  JPanel jPanel22 = new JPanel();
  XYLayout xYLayout22 = new XYLayout();
  JLabel jLabel33 = new JLabel();
  JTextField jText_SNOWFILE = new JTextField(15);
  JTextField jText_METEOFILE = new JTextField(15);
  XYLayout xYLayout23 = new XYLayout();
  JPanel jPanel23 = new JPanel();
  XYLayout xYLayout24 = new XYLayout();
  JLabel jLabel34 = new JLabel();
  JPanel jPanel24 = new JPanel();
  XYLayout xYLayout25 = new XYLayout();
  JRadioButton jRadioButton_SW_REF = new JRadioButton();
  JLabel jLabel_SW_REF  = new JLabel();//Schirmer
  JTextField jText_SW_REF = new JTextField(4);//Schirmer
  JRadioButton jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS = new JRadioButton();
  JPanel jPanel13 = new JPanel();
  JRadioButton jRadioButton_MEAS_TSS = new JRadioButton();
  JRadioButton jRadioButton_INCOMING_LONGWAVE = new JRadioButton();
  XYLayout xYLayout14 = new XYLayout();
  JPanel jPanel25 = new JPanel();
  XYLayout xYLayout26 = new XYLayout();
  JLabel jLabel35 = new JLabel();
  JTextField jText_ROUGHNESS_LENGTH = new JTextField(6);
  JLabel jLabel36 = new JLabel();
  JPanel jPanel26 = new JPanel();
  XYLayout xYLayout27 = new XYLayout();
  JLabel jLabel37 = new JLabel();
  JLabel jLabel38 = new JLabel();
  JTextField jText_SOIL_ALBEDO = new JTextField(6);
  JTextField jText_GEO_HEAT = new JTextField(6);
  JTextField jText_BARE_SOIL_z0 = new JTextField(6);
  JLabel jLabel39 = new JLabel();
  JLabel jLabel40 = new JLabel();
  JTextField jText_NEUTRAL = new JTextField();
  JLabel jLabel_NEUTRAL = new JLabel();
  JTextField jText_THRESH_CHANGE_BC = new JTextField(6);
  JLabel jLabel41 = new JLabel();
  JTextField jText_MODEL_DIRECTORY = new JTextField(12);
  JButton jButton_Run = new JButton();
  JButton jButton_Save = new JButton();
  JPanel jPanel20 = new JPanel();
  JTextField jText_DEPTH_1 = new JTextField(6);
  JTextField jText_DEPTH_2 = new JTextField(6);
  JTextField jText_DEPTH_3 = new JTextField(6);
  XYLayout xYLayout21 = new XYLayout();
  JLabel jLabel22 = new JLabel();
  JLabel jLabel23 = new JLabel();
  JLabel jLabel24 = new JLabel();
  JLabel jLabel42 = new JLabel();
  JLabel jLabel43 = new JLabel();
  JTextField jText_DEPTH_4 = new JTextField(6);
  JPanel jPanel27 = new JPanel();
  XYLayout xYLayout28 = new XYLayout();
  JLabel jLabel9 = new JLabel();
  JTextField jText_DEPTH_5 = new JTextField(6);
  JPanel jPanel28 = new JPanel();
  XYLayout xYLayout29 = new XYLayout();
  JLabel jLabel29 = new JLabel();
  JTextField jText_T_INTERNAL = new JTextField(4);
  JLabel jLabel30 = new JLabel();
  JLabel jLabel31 = new JLabel();
  JPanel jPanel29 = new JPanel();
  XYLayout xYLayout211 = new XYLayout();
  JButton jButton_CANOPY = new JButton();
  JLabel jLabel110 = new JLabel();
  //Schirmer
  JButton jButton_SNOW_RED = new JButton();
  JLabel jLabel_SNOW_RED = new JLabel();
  JPanel jPanel_SNOW_RED = new JPanel();

  JButton jButton_PROF_EVAL = new JButton();
  JTextField jText_PROF_FILE = new JTextField(6);
  JPanel jPanel30 = new JPanel();
  JLabel jLabel47 = new JLabel();
  JLabel jLabel48 = new JLabel();
  JLabel jLabel49 = new JLabel();
  XYLayout xYLayout30 = new XYLayout();
  JLabel jLabelAtmosphericStab = new JLabel();


  public ModelDialog(MenuFrame mFrame, String title, String userInputFile, boolean modal)
  {
    super(mFrame, title, modal);
    this.mFrame = mFrame;
    this.userInputFile = userInputFile;

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


  void jbInit() throws Exception
  {
    jText_ROUGHNESS_LENGTH.setEnabled(true);

    if (!readParameters(userInputFile))
    {
      // Input file not found --> ModelDialog not displayed
      this.setVisible(false);
      this.dispose();
      reading_ok = false;
      return;
    }
    else
    {
      reading_ok = true;
    }

    // Check the data of the default file
    checkData();

    // If input file is existent and checked data are not ok, the ModelDialog
    // will still be visible and "Error" will appear in jLabel_Error.
    // Corrections can be processed by the user (jTextFields).

    // If input file errors concerning jButtons or jComboBoxes exist, the errors are
    // detected by the ModelDialog and default values prescribed by the program
    // are used.
    if ((MEAS_TSS == "Error") || (ENFORCE_MEASURED_SNOW_HEIGHTS == "Error") ||
        (SW_REF == "Error")   || (INCOMING_LONGWAVE == "Error") ||
        (CHANGE_BC == "Error") ||
        (SNP_SOIL == "Error") || (SOIL_FLUX == "Error") ||
        (TS_WRITE == "Error") || (PROF_WRITE == "Error"))
    {
       jLabel_Error.setText("Some errors reading the input file were corrected automatically!");
    }

    reading_ok = true;

    panel1.setLayout(xYLayout1);
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setLayout(xYLayout2);
    jPanel3.setLayout(xYLayout3);
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    xYLayout1.setHeight(607);
    xYLayout1.setWidth(687);
    jButton_Run.setToolTipText("Save the current parameter values in CONSTANTS_User.INI"); //Schirmer
    jButton_Run.setText("Save for Run");//Schirmer
    jButton_Run.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Run_actionPerformed(e);
      }
    });
    jButton_Save.setToolTipText("Store current parameter values in an user defined file");
    jButton_Save.setText("Save User Settings");
    jButton_Save.addActionListener(new java.awt.event.ActionListener()
    {

        public void actionPerformed(ActionEvent e)
        {
            jButton_Save_actionPerformed(e);
        }
    });

    jButton_Cancel.setToolTipText("Exit this dialog without further actions");
    jButton_Cancel.setText("Cancel");
    jButton_Cancel.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Cancel_actionPerformed(e);
      }
    });
    jLabel_Error.setForeground(Color.red);
    jLabel_Error.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_Error.setHorizontalAlignment(SwingConstants.CENTER);
    jPanel4.setLayout(xYLayout5);
    jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel5.setLayout(xYLayout6);
    jPanel5.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel6.setLayout(xYLayout7);
    jPanel6.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel2.setText("OUTPUT");
    jPanel7.setLayout(xYLayout8);
    jPanel7.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel8.setLayout(xYLayout9);
    jPanel8.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel4.setToolTipText("Switch ON or OFF the output of time series or snow profile results");
    jLabel4.setText("Output Writing:");
    jLabel5.setToolTipText("Hour of first time series or snow profile " +
    "output");
    jLabel5.setText("Start Hour:");
    jLabel6.setToolTipText("Time spacing for time series or " +
    "snow profile output (in days)");
    jLabel6.setText("Days Between:");
    jLabel7.setText("Time Series");
    jLabel8.setText("Profile");
    jButton_TS_WRITE.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_TS_WRITE.setToolTipText("Switch ON or OFF the output of time series results");
    jButton_TS_WRITE.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_TS_WRITE_actionPerformed(e);
      }
    });
    jButton_PROF_WRITE.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_PROF_WRITE.setToolTipText("Switch ON or OFF the output of snow profile results");
    jButton_PROF_WRITE.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_PROF_WRITE_actionPerformed(e);
      }
    });

    jPanel9.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel9.setLayout(xYLayout10);
    jLabel10.setToolTipText("Name string for station data used");
    jLabel10.setText("Research Station:");
    jLabel11.setToolTipText("Name string for specifying the run");
    jLabel11.setText("Experiment:");
    jPanel10.setLayout(xYLayout11);
    jPanel10.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel11.setLayout(xYLayout12);
    jPanel11.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel12.setLayout(xYLayout13);
    jPanel12.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel12.setText("INPUT");
    jLabel13.setToolTipText("Time resolution of meteo input data (minutes)");
    jLabel13.setText("Meteo Step Length:");
    jPanel14.setLayout(xYLayout15);
    jPanel14.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel14.setToolTipText("Height of the wind speed sensor above ground (m)");
    jLabel14.setText("Height of Wind Value:");
    jLabel15.setToolTipText("Height of meteorological sensors above ground (m)");
    jLabel15.setText("Height of Meteo Values:");
    jPanel15.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel15.setLayout(xYLayout16);
    jPanel16.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel16.setLayout(xYLayout17);
    jPanel17.setLayout(xYLayout18);
    jPanel17.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel16.setText("MODEL");
    jPanel18.setLayout(xYLayout19);
    jPanel18.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel17.setToolTipText("Internal computation time step (minutes)");
    jLabel17.setText("Calculation Step Length:");
    jPanel19.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel19.setLayout(xYLayout20);
    jLabel18.setText("Boundary Conditions:");
    jLabel19.setToolTipText("If ON: Run the model incl. soil layers. Setting MUST agree with contents " +
    "of Snow & Soil Data File.  ");
    jLabel19.setText("Inclusion of Soil Data:");
    jLabel20.setToolTipText("Choice: Neumann or mixed Neumann-Dirichlet - depending on the measured " +
    "surf. temp. - upper boundary condition");
    jLabel20.setText("Surface");
    jLabel21.setToolTipText("Choice: Neumann or Dirichlet lower boundary condition (if Inclusion " +
    "of Soil Data = ON)");
    jLabel21.setText("Bottom");

    jButton_SNP_SOIL.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_SNP_SOIL_actionPerformed(e);
      }
    });


    jLabel25.setText("min");
    jLabel26.setText("m");
    jLabel27.setText("m");
    jLabel28.setText("min");
    jButton_Restore.setToolTipText("Refresh window with parameters stored in CONSTANTS.INI");
    jButton_Restore.setText("Restore Defaults");
    jButton_Restore.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Restore_actionPerformed(e);
      }
    });
    jLabel1.setToolTipText("File which to read the snow and soil input data from  (abs. path " +
    "or path rel. to the Model Directory)");
    jLabel1.setText("Snow & Soil Data:");
    jLabel32.setToolTipText("File which to read the meteorological input data from  (abs. path " +
    "or path rel. to the Model Directory)");
    jLabel32.setText("Meteo Data:");
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setLayout(xYLayout23);
    jPanel21.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel21.setLayout(xYLayout4);
    jPanel22.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel22.setLayout(xYLayout22);
    jLabel33.setToolTipText("Abs. path to Dir. where SNOWPACK executable resides (or path relative " +
    "to SN_GUI directory)");
    jLabel33.setText("Model Directory:");
    jText_METEO_STEP_LENGTH.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_CALCULATION_STEP_LENGTH.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_TS_START.setToolTipText("Hour of first time series output");
    jText_TS_START.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_PROF_START.setToolTipText("Hour of first snow profile output");
    jText_PROF_START.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_TS_DAYS_BETWEEN.setToolTipText("Time spacing for time series output (in days)");
    jText_TS_DAYS_BETWEEN.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_PROF_DAYS_BETWEEN.setToolTipText("Time spacing for snow profile output (in days)");
    jText_PROF_DAYS_BETWEEN.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_HEIGHT_OF_METEO_VALUES.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_HEIGHT_OF_WIND_VALUE.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_T_INTERNAL.setHorizontalAlignment(SwingConstants.RIGHT);
    jPanel23.setLayout(xYLayout24);
    jPanel23.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel34.setToolTipText("Path to write the output files (absolute path or path relative to " +
    "the Model Directory)");
    jLabel34.setText("Output File Path:");
    jPanel24.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel24.setLayout(xYLayout25);

    //Schirmer
    jLabel_SW_REF.setText("Define SW Radiation");
    jLabel_SW_REF.setToolTipText("0:incoming SW Rad. available, 1: reflected SW.Rad. avail.; 2: measured albedo avail.; 10: both avail., but use only incoming; 11: both avail., but use only reflected");
    jText_SW_REF.setText("1");
    jText_SW_REF.setToolTipText("0: incoming SW Rad. available, 1: reflected SW.Rad. avail.; 2: measured albedo avail.; 10: both avail., but use only incoming; 11: both avail., but use only reflected");
    jText_SW_REF.setHorizontalAlignment(SwingConstants.RIGHT);

    jRadioButton_SW_REF.setToolTipText("If selected: Measured shortwave radiation is reflected (incoming " +
    "else)");
    jRadioButton_SW_REF.setText("Measured SW Radiation is Reflected");
    jRadioButton_SW_REF.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jRadioButton_SW_REF_actionPerformed(e);
      }
    });
    jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS.setToolTipText("If selected: Use measured snow depth to drive snow fall ");
    jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS.setText("Enforce Measured Snow Heights");
    jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS_actionPerformed(e);
      }
    });
    jPanel13.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel13.setLayout(xYLayout14);
    jRadioButton_MEAS_TSS.setToolTipText("If selected: Measured surface temperatures are available ");
    jRadioButton_MEAS_TSS.setText("Measured Surface Temperatures Available");
    jRadioButton_MEAS_TSS.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jRadioButton_MEAS_TSS_actionPerformed(e);
      }
    });
    jRadioButton_INCOMING_LONGWAVE.setToolTipText("If selected: Measured incoming longwave radiation available ");
    jRadioButton_INCOMING_LONGWAVE.setText("Measured Incoming LW Radiation Available");
    jRadioButton_INCOMING_LONGWAVE.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jRadioButton_INCOMING_LONGWAVE_actionPerformed(e);
      }
    });
    jPanel25.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel25.setLayout(xYLayout26);
    jLabel35.setToolTipText("Best estimate of an average roughness length over snow on site");
    jLabel35.setText("Roughness Length:");
    jLabel36.setText("m");
    jPanel26.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel26.setLayout(xYLayout27);
    jLabel37.setToolTipText("Albedo value for the case of no snow");
    jLabel37.setText("Soil Albedo (Default):");
    jLabel38.setToolTipText("Best estimate of an average roughness length without snow on site");
    jLabel38.setText("Bare Soil z0:");
    jLabel39.setText("m");
    jLabel_NEUTRAL.setText("NEUTRAL");
    jLabel_NEUTRAL.setToolTipText("0 : Standard MO iteration with Paulson and Stearns & Weidner;" +
                                  " -1: Simplified Richardson number stability correction;" +
                                  " 1 : Assume neutral stratification");
    jText_NEUTRAL.setToolTipText("0 : Standard MO iteration with Paulson and Stearns & Weidner;" +
                                  " -1: Simplified Richardson number stability correction;" +
                                  " 1 : Assume neutral stratification");
    jText_NEUTRAL.setHorizontalAlignment(
            SwingConstants.RIGHT);
    jLabelAtmosphericStab.setText("Atmospheric Stability");
    jLabel40.setToolTipText(
            "Threshold for measured surf. temp. to autom. switch to Neumann BC " +
            "when melting is approached");
    jLabel40.setText("Threshold");
    jText_THRESH_CHANGE_BC.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_SOIL_ALBEDO.setEnabled(false);
    jText_SOIL_ALBEDO.setToolTipText("Albedo of soil");
    jText_SOIL_ALBEDO.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_BARE_SOIL_z0.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_ROUGHNESS_LENGTH.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel41.setText("\uFFFDC");
    jButton_Run.setText("Save for Run"); //Schirmer
    jPanel20.setLayout(xYLayout21);
    jPanel20.setBorder(BorderFactory.createRaisedBevelBorder());
    jText_DEPTH_1.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_DEPTH_2.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_DEPTH_3.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_DEPTH_4.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_DEPTH_5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel22.setText("1:");
    jLabel23.setText("2:");
    jLabel24.setText("3:");
    jLabel42.setToolTipText("Distance from surface (+ snow / - soil) for a temperature time series " +
    "output (5 levels)");
    jLabel42.setText("Internal Temperature Depths (in m):");
    jLabel43.setText("4:");
    jPanel27.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel27.setLayout(xYLayout28);
    jLabel9.setText("5:");
    jPanel28.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel28.setLayout(xYLayout29);
    jLabel29.setToolTipText("Related to Internal Temp. Depth (from left, consecutively)");
    jLabel29.setText("No. of Measured Snow/Soil Temp.:");
    jLabel30.setText("Geothermal Heat Flux:");
    jLabel31.setText("W/m2");
    jText_GEO_HEAT.setHorizontalAlignment(SwingConstants.RIGHT);
    jButton_SNP_SOIL.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_SNP_SOIL.setMaximumSize(new Dimension(35, 17));
    jButton_SNP_SOIL.setMinimumSize(new Dimension(35, 17));
    jButton_SNP_SOIL.setPreferredSize(new Dimension(35, 17));
    jComboBox_SOIL_FLUX.setToolTipText("");
    jPanel29.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel29.setLayout(xYLayout211);
    jButton_CANOPY.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_CANOPY_actionPerformed(e);
      }
    });
    jButton_CANOPY.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_CANOPY.setMaximumSize(new Dimension(35, 17));
    jButton_CANOPY.setMinimumSize(new Dimension(35, 17));
    jButton_CANOPY.setPreferredSize(new Dimension(35, 17));
    jLabel110.setToolTipText("If ON: Run the model incl. canopy model. ");
    jLabel110.setText("Canopy Model:");
    //Schirmer
    jButton_SNOW_RED.addActionListener(new java.awt.event.ActionListener() {
       public void actionPerformed(ActionEvent e){
           jButton_SNOW_RED_actionPerformed(e);
       }
    });
    jButton_SNOW_RED.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton_SNOW_RED.setMaximumSize(new Dimension(35, 17));
    jButton_SNOW_RED.setMinimumSize(new Dimension(35, 17));
    jButton_SNOW_RED.setPreferredSize(new Dimension(35, 17));
    jLabel_SNOW_RED.setText("Blowing Snow:");
    jPanel_SNOW_RED.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel_SNOW_RED.setLayout(xYLayout211);

    panel1.setPreferredSize(new Dimension(740, 610));
    jButton_PROF_EVAL.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_PROF_EVAL_actionPerformed(e);
      }
    });
    jButton_PROF_EVAL.setToolTipText("Switch ON or OFF the output of time series results");
    jButton_PROF_EVAL.setBorder(BorderFactory.createRaisedBevelBorder());
    jText_PROF_FILE.setText(PROF_FILE);
    jPanel30.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel30.setLayout(xYLayout30);
    jLabel47.setToolTipText("File name of observed snow profile used for evaluation of simulation");
    jLabel47.setText("Profile Data File:");
    jLabel48.setToolTipText("Switch ON or OFF the snow profile evaluation");
    jLabel48.setText("Profile Evaluation:");
    getContentPane().add(panel1);
    panel1.add(jPanel6, new XYConstraints(2, 3, 742, 182));
    jPanel6.add(jPanel11, new XYConstraints(74, 6, 660, 169));
    jPanel11.add(jPanel24, new XYConstraints(3, 3, 288, 158));
    jPanel24.add(jPanel21, new XYConstraints(4, 2, 275, 24));
    jPanel21.add(jLabel13, new XYConstraints(1, 0, -1, -1));
    jPanel21.add(jText_METEO_STEP_LENGTH, new XYConstraints(113, 0, 49, 18));
    jPanel21.add(jLabel25, new XYConstraints(175, 0, -1, -1));
    jPanel24.add(jPanel13, new XYConstraints(4, 28, 275, 96));
    jPanel13.add(jRadioButton_MEAS_TSS, new XYConstraints(7, 2, -1, -1));
    jPanel13.add(jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS, new XYConstraints(7, 24, -1, -1));
    //jPanel13.add(jRadioButton_SW_REF, new XYConstraints(7, 47, -1, -1));//Schirmer
    jPanel13.add(jRadioButton_INCOMING_LONGWAVE, new XYConstraints(7, 47, -1, -1)); //Schirmer
    jPanel13.add(jLabel_SW_REF, new XYConstraints(7, 75, -1, -1)); //Schirmer
    jPanel13.add(jText_SW_REF, new XYConstraints(227, 73, 35, 19)); //Schirmer
    jPanel24.add(jPanel28, new XYConstraints(4, 126, 275, 23));
    jPanel28.add(jLabel29, new XYConstraints(2, 0, -1, -1));
    jPanel28.add(jText_T_INTERNAL, new XYConstraints(226, 0, 35, 19));
    jPanel11.add(jPanel14, new XYConstraints(294, 3, 359, 158));
    jPanel14.add(jPanel22, new XYConstraints(3, 2, 350, 74));
    jPanel22.add(jLabel33, new XYConstraints(7, 4, -1, -1));
    jPanel22.add(jLabel1, new XYConstraints(6, 21, 103, 22));
    jPanel22.add(jLabel32, new XYConstraints(7, 46, 100, -1));
    jPanel22.add(jText_MODEL_DIRECTORY, new XYConstraints(122, 0, 222, -1));
    jPanel22.add(jText_SNOWFILE, new XYConstraints(122, 23, 221, -1));
    jPanel22.add(jText_METEOFILE, new XYConstraints(122, 45, 221, -1));
    jPanel14.add(jPanel25, new XYConstraints(3, 127, 289, 26));
    jPanel25.add(jLabel35, new XYConstraints(4, 3, -1, -1));
    jPanel25.add(jText_ROUGHNESS_LENGTH, new XYConstraints(139, 0, 51, -1));
    jPanel25.add(jLabel36, new XYConstraints(196, 3, -1, -1));
    jPanel14.add(jPanel1, new XYConstraints(3, 77, 349, 50));
    jPanel1.add(jLabel14, new XYConstraints(4, 3, -1, -1));
    jPanel1.add(jText_HEIGHT_OF_WIND_VALUE, new XYConstraints(139, 1, 50, -1));
    jPanel1.add(jLabel26, new XYConstraints(196, 4, -1, -1));
    jPanel1.add(jLabel15, new XYConstraints(3, 26, -1, -1));
    jPanel1.add(jText_HEIGHT_OF_METEO_VALUES, new XYConstraints(139, 24, 50, -1));
    jPanel1.add(jLabel27, new XYConstraints(195, 28, -1, -1));
    jPanel6.add(jPanel12, new XYConstraints(2, 6, 70, 168));
    jPanel12.add(jLabel12, new XYConstraints(13, 63, -1, -1));
    panel1.add(jPanel5, new XYConstraints(2, 191, 741, 153));
    jPanel5.add(jPanel15, new XYConstraints(1, 2, 70, 142));
    jPanel15.add(jLabel16, new XYConstraints(12, 56, -1, -1));
    jPanel5.add(jPanel16, new XYConstraints(73, 2, 303, 142));
    jPanel16.add(jPanel19, new XYConstraints(2, 33, 294, 103));
    jPanel19.add(jLabel18, new XYConstraints(4, 3, -1, -1));
    jPanel19.add(jLabel20, new XYConstraints(4, 23, -1, -1));
    jPanel19.add(jLabel21, new XYConstraints(5, 74, 43, -1));
    jPanel19.add(jComboBox_CHANGE_BC, new XYConstraints(50, 22, 237, 20));
    jPanel19.add(jLabel40, new XYConstraints(50, 45, -1, -1));
    jPanel19.add(jText_THRESH_CHANGE_BC, new XYConstraints(110, 45, 53, -1));
    jPanel19.add(jLabel41, new XYConstraints(167, 47, 15, -1));
    jPanel19.add(jComboBox_SOIL_FLUX, new XYConstraints(50, 72, 237, 20));
    jPanel16.add(jPanel18, new XYConstraints(2, 3, 293, 28));
    jPanel18.add(jLabel28, new XYConstraints(214, 3, -1, -1));
    jPanel18.add(jText_CALCULATION_STEP_LENGTH, new XYConstraints(154, 1, 58, -1));
    jPanel18.add(jLabel17, new XYConstraints(4, 5, -1, -1));
    jPanel5.add(jPanel17, new XYConstraints(380, 2, 351, 143));
    jPanel17.add(jPanel26, new XYConstraints(4, 3, 208, 67));
    //jPanel26.add(jLabel37, new XYConstraints(4, 8, -1, -1));to delete soil albedo and bare soil
    jPanel23.add(jLabel30, new XYConstraints(5, 35, -1, 22));
    //jPanel26.add(jLabel38, new XYConstraints(5, 40, -1, -1));to delete soil albedo and bare soil
    //jPanel26.add(jText_SOIL_ALBEDO, new XYConstraints(129, 6, 37, -1)); to delete soil albedo and bare soil
    jPanel23.add(jText_GEO_HEAT, new XYConstraints(150, 37, 37, -1));
    jPanel26.add(jText_NEUTRAL, new XYConstraints(150, 36, 37, -1));
    jPanel26.add(jLabel_NEUTRAL, new XYConstraints(5, 40, -1, -1));
    jPanel26.add(jLabelAtmosphericStab, new XYConstraints(5, 5, -1, -1));
    //jPanel26.add(jText_BARE_SOIL_z0, new XYConstraints(130, 36, 37, -1));
    //jPanel26.add(jLabel39, new XYConstraints(172, 39, -1, -1));to delete soil albedo and bare soil
    jPanel26.add(jLabel31, new XYConstraints(169, 70, -1, -1));
    jPanel17.add(jPanel23, new XYConstraints(4, 70, 208, 67));
    jPanel23.add(jLabel19, new XYConstraints(5, 7, -1, -1));
    jPanel23.add(jButton_SNP_SOIL, new XYConstraints(145, 6, 51, 19));
    jPanel17.add(jPanel29, new XYConstraints(213, 70, 130, 67));
    jPanel29.add(jLabel110, new XYConstraints(7, 7, -1, -1));
    jPanel29.add(jButton_CANOPY, new XYConstraints(30, 32, 51, 19));
    //Schirmer
    jPanel_SNOW_RED.add(jButton_SNOW_RED, new XYConstraints(30, 32, 51, 19));
    jPanel_SNOW_RED.add(jLabel_SNOW_RED, new XYConstraints(7, 7, -1, -1));
    jPanel17.add(jPanel_SNOW_RED, new XYConstraints(213, 3, 130, 67));

    panel1.add(jPanel2, new XYConstraints(2, 349, 740, 158));
    jPanel2.add(jPanel7, new XYConstraints(75, 2, 658, 151));
    jPanel7.add(jPanel20, new XYConstraints(2, 111, 649, 34));
    jPanel20.add(jLabel42, new XYConstraints(8, 8, 203, -1));
    jPanel20.add(jPanel27, new XYConstraints(213, 2, 378, -1));
    jPanel27.add(jLabel22, new XYConstraints(3, 3, -1, -1));
    jPanel27.add(jText_DEPTH_1, new XYConstraints(16, 0, 42, -1));
    jPanel27.add(jText_DEPTH_5, new XYConstraints(325, 0, 42, 21));
    jPanel27.add(jLabel9, new XYConstraints(308, 3, 12, -1));
    jPanel27.add(jText_DEPTH_4, new XYConstraints(245, 0, 42, 21));
    jPanel27.add(jLabel43, new XYConstraints(230, 3, -1, -1));
    jPanel27.add(jText_DEPTH_3, new XYConstraints(167, 0, 42, 21));
    jPanel27.add(jLabel24, new XYConstraints(153, 3, -1, 16));
    jPanel27.add(jText_DEPTH_2, new XYConstraints(94, 0, 42, 21));
    jPanel27.add(jLabel23, new XYConstraints(78, 3, 12, -1));
    jPanel7.add(jPanel8, new XYConstraints(5, 4, 252, 104));
    jPanel8.add(jLabel3, new XYConstraints(9, 6, -1, -1));
    jPanel8.add(jButton_TS_WRITE, new XYConstraints(106, 21, 66, 21));
    jPanel8.add(jButton_PROF_WRITE, new XYConstraints(178, 21, 66, 21));
    jPanel8.add(jText_TS_START, new XYConstraints(106, 47, -1, -1));
    jPanel8.add(jText_PROF_START, new XYConstraints(177, 47, -1, -1));
    jPanel8.add(jText_TS_DAYS_BETWEEN, new XYConstraints(106, 69, -1, -1));
    jPanel8.add(jText_PROF_DAYS_BETWEEN, new XYConstraints(177, 68, -1, -1));
    jPanel8.add(jLabel7, new XYConstraints(106, 2, -1, -1));
    jPanel8.add(jLabel6, new XYConstraints(5, 68, -1, -1));
    jPanel8.add(jLabel4, new XYConstraints(4, 22, -1, -1));
    jPanel8.add(jLabel5, new XYConstraints(5, 45, -1, -1));
    jPanel8.add(jLabel8, new XYConstraints(195, 3, -1, -1));
    jPanel7.add(jPanel9, new XYConstraints(394, 4, 252, 104));
    jPanel9.add(jLabel11, new XYConstraints(4, 69, -1, -1));
    jPanel9.add(jLabel34, new XYConstraints(4, 11, -1, -1));
    jPanel9.add(jText_EXPERIMENT, new XYConstraints(105, 68, 133, -1));
    jPanel9.add(jText_OUTPATH, new XYConstraints(106, 10, 133, -1));
    jPanel9.add(jText_RESEARCH_STATION, new XYConstraints(105, 39, 133, -1));
    jPanel9.add(jLabel10, new XYConstraints(1, 41, -1, -1));
    jPanel7.add(jPanel30, new XYConstraints(260, 4, 131, 104));
    jPanel30.add(jLabel49, new XYConstraints(9, 6, -1, -1));
    jPanel30.add(jText_PROF_FILE, new XYConstraints(4, 76, 115, -1));
    jPanel30.add(jButton_PROF_EVAL, new XYConstraints(32, 28, 66, 21));
    jPanel30.add(jLabel48, new XYConstraints(13, 6, -1, -1));
    jPanel30.add(jLabel47, new XYConstraints(14, 55, -1, -1));
    jPanel2.add(jPanel10, new XYConstraints(2, 2, 70, 150));
    jPanel10.add(jLabel2, new XYConstraints(10, 62, -1, -1));
    panel1.add(jPanel3, new XYConstraints(4, 510, 738, 59));
    jPanel3.add(jButton_Run, new XYConstraints(50, 14, -1, -1));
    jPanel3.add(jButton_Save, new XYConstraints(200, 14, 129, -1));
    jPanel3.add(jButton_Restore, new XYConstraints(350, 14, 129, -1));
    //jPanel3.add(jButton_Run, new XYConstraints(95, 14, -1, -1)); //why two times? (Schirmer)
    jPanel3.add(jButton_Cancel, new XYConstraints(500, 14, 129, -1));
    panel1.add(jPanel4, new XYConstraints(2, 573, 739, 31));
    jPanel4.add(jLabel_Error, new XYConstraints(3, 2, 730, 24));

    refresh();
  }


  public boolean readParameters(String filename)
  // Read default parameters from input file.
  // filename: user input file, can be CONSTANTS_User.INI or another file,
  // selected by the user before calling ModelDialog
  {
    String defaultfile = Setup.m_IniFilePath + "CONSTANTS.INI";

    try
    // Look for user-adjustable file first
    {
        IniFile Constants = new IniFile(filename);
        getParameters(Constants);

        return true;
    }
    catch (IOException e)
    {
        try
        // User-adjustable file does not exist, look for default file
        {
            IniFile Constants_Default = new IniFile(defaultfile);
            getParameters(Constants_Default);

            // Copy CONSTANTS.INI to CONSTANTS_User.INI
            File Constants = new File(filename);
            Constants.delete(); // delete existing CONSTANTS_User.INI
            Constants_Default.setFileName(filename);
            Constants_Default.writeWithBlanks();

            return true;
        }
        catch (IOException e1)
        // Both user-adjustable file and default file not found
        {
            MessageBox mBox = new MessageBox(mFrame,
              "Error", "File not found: ",
              defaultfile + "!");
              mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
              mBox.setVisible(true);

              //System.out.println(e1);
        }

     }
     return false;
  }

  void refresh()
  // Replaces jTextFields etc. with the actual values of the member variables
  {
    jText_SNOWFILE.setText(SNOWFILE);
    jText_METEOFILE.setText(METEOFILE);
    jText_METEO_STEP_LENGTH.setText(METEO_STEP_LENGTH);
    if (MEAS_TSS.equals("0")) jRadioButton_MEAS_TSS.setSelected(false);
    else
    {    jRadioButton_MEAS_TSS.setSelected(true);
         MEAS_TSS = "1"; // necessary because MEAS_TSS could be "Error" at this point
                         // (if input file does not contain MEAS_TSS = ...)
    }
    if (ENFORCE_MEASURED_SNOW_HEIGHTS.equals("0")) jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS.setSelected(false);
    else
    {
         jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS.setSelected(true);
         ENFORCE_MEASURED_SNOW_HEIGHTS = "1";
    }
    /* (Schirmer)
    if (SW_REF.equals("1")) jRadioButton_SW_REF.setSelected(true);
    else
    {
         jRadioButton_SW_REF.setSelected(false);
         SW_REF = "0";
    }*/
    jText_SW_REF.setText(SW_REF);//Schirmer

    if (INCOMING_LONGWAVE.equals("0")) jRadioButton_INCOMING_LONGWAVE.setSelected(false);
    else
    {
        jRadioButton_INCOMING_LONGWAVE.setSelected(true);
        INCOMING_LONGWAVE = "1";
    }
    jText_T_INTERNAL.setText(T_INTERNAL);
    jText_HEIGHT_OF_WIND_VALUE.setText(HEIGHT_OF_WIND_VALUE);
    jText_HEIGHT_OF_METEO_VALUES.setText(HEIGHT_OF_METEO_VALUES);
    jText_ROUGHNESS_LENGTH.setText(ROUGHNESS_LENGTH);
    jText_THRESH_CHANGE_BC.setText(THRESH_CHANGE_BC);
    jText_GEO_HEAT.setText(GEO_HEAT);
    jText_SOIL_ALBEDO.setText(SOIL_ALBEDO);
    jText_BARE_SOIL_z0.setText(BARE_SOIL_z0);
    jText_NEUTRAL.setText(NEUTRAL);
    jText_CALCULATION_STEP_LENGTH.setText(CALCULATION_STEP_LENGTH);
    if (CHANGE_BC.equals("0")) jComboBox_CHANGE_BC.setSelectedIndex(0);
    else
    {
         jComboBox_CHANGE_BC.setSelectedIndex(1);
         CHANGE_BC = "1";
    }
    if (SOIL_FLUX.equals("1")) jComboBox_SOIL_FLUX.setSelectedIndex(0);
    else
    {
         jComboBox_SOIL_FLUX.setSelectedIndex(1);
         SOIL_FLUX = "0";
    }
    if (SNP_SOIL.equals("1"))
    { jButton_SNP_SOIL.setText("ON");
      jComboBox_SOIL_FLUX.setEnabled(true);
      jText_GEO_HEAT.setEnabled(true);
      //jText_SOIL_ALBEDO.setEnabled(true); // currently always disabled
      jText_BARE_SOIL_z0.setEnabled(true);
    }
    else
    {
      SNP_SOIL = "0";
      jButton_SNP_SOIL.setText("OFF");
      jComboBox_SOIL_FLUX.setEnabled(false);
      jText_GEO_HEAT.setEnabled(false);
      jText_SOIL_ALBEDO.setEnabled(false);
      jText_BARE_SOIL_z0.setEnabled(false);
    }

    if (CANOPY.equals("1"))
    { jButton_CANOPY.setText("ON");
    }
    else
    {
      CANOPY = "0";
      jButton_CANOPY.setText("OFF");
    }

    //Schirmer
    if (this.SNOW_RED.equals("1"))
    { this.jButton_SNOW_RED.setText("ON");
    }
    else
    {
      this.SNOW_RED = "0";
      jButton_SNOW_RED.setText("OFF");
    }


    jText_DEPTH_1.setText(DEPTH_1);
    jText_DEPTH_2.setText(DEPTH_2);
    jText_DEPTH_3.setText(DEPTH_3);
    jText_DEPTH_4.setText(DEPTH_4);
    jText_DEPTH_5.setText(DEPTH_5);
    if (TS_WRITE.equals("0"))
    {
      jButton_TS_WRITE.setText("OFF");
      jText_TS_START.setEnabled(false);
      jText_TS_DAYS_BETWEEN.setEnabled(false);
    }
    else
    {
      jButton_TS_WRITE.setText("ON");
      jText_TS_START.setEnabled(true);
      jText_TS_DAYS_BETWEEN.setEnabled(true);
      TS_WRITE = "1";
    }
    if (PROF_WRITE.equals("0"))
    {
      jButton_PROF_WRITE.setText("OFF");
      jText_PROF_START.setEnabled(false);
      jText_PROF_DAYS_BETWEEN.setEnabled(false);
    }
    else
    {
      jButton_PROF_WRITE.setText("ON");
      jText_PROF_START.setEnabled(true);
      jText_PROF_DAYS_BETWEEN.setEnabled(true);
      PROF_WRITE = "1";
    }

    if (PROF_EVAL.equals("0"))
    {
      jButton_PROF_EVAL.setText("OFF");
      jText_PROF_FILE.setEnabled(false);
    }
    else
    {
      jButton_PROF_EVAL.setText("ON");
      jText_PROF_FILE.setEnabled(true);
      PROF_EVAL="1";
    }



    jText_TS_START.setText(TS_START);
    jText_PROF_START.setText(PROF_START);
    jText_TS_DAYS_BETWEEN.setText(TS_DAYS_BETWEEN);
    jText_PROF_DAYS_BETWEEN.setText(PROF_DAYS_BETWEEN);
    jText_OUTPATH.setText(OUTPATH);
    jText_RESEARCH_STATION.setText(RESEARCH_STATION);
    jText_EXPERIMENT.setText(EXPERIMENT);
    jText_MODEL_DIRECTORY.setText(MODEL_DIRECTORY);


    jText_PROF_FILE.setText(PROF_FILE);
  }

  void getParameters(IniFile Constants)
  {
      Constants.setSection("Parameters");

      if (Setup.m_SnowFile.equals(""))
      // no snow file was created in this session
         SNOWFILE = Constants.getEntry("SNOWFILE", "Error");
      else
      // snowfile was created, use the name of that file as default
         SNOWFILE = Setup.m_SnowFile;

      METEOFILE = Constants.getEntry("METEOFILE", "Error");
      METEO_STEP_LENGTH = Constants.getEntry("METEO_STEP_LENGTH", "Error");
      MEAS_TSS = Constants.getEntry("MEAS_TSS", "Error");
      ENFORCE_MEASURED_SNOW_HEIGHTS = Constants.getEntry("ENFORCE_MEASURED_SNOW_HEIGHTS", "Error");
      SW_REF = Constants.getEntry("SW_REF", "Error");
      INCOMING_LONGWAVE = Constants.getEntry("INCOMING_LONGWAVE", "Error");
      T_INTERNAL = Constants.getEntry("T_INTERNAL", "Error");
      HEIGHT_OF_WIND_VALUE = Constants.getEntry("HEIGHT_OF_WIND_VALUE", "Error");
      HEIGHT_OF_METEO_VALUES = Constants.getEntry("HEIGHT_OF_METEO_VALUES", "Error");
      NEUTRAL = Constants.getEntry("NEUTRAL", "Error");
      ROUGHNESS_LENGTH = Constants.getEntry("ROUGHNESS_LENGTH", "Error");
      THRESH_CHANGE_BC = Constants.getEntry("THRESH_CHANGE_BC", "Error");
      GEO_HEAT = Constants.getEntry("GEO_HEAT", "Error");
      SOIL_ALBEDO = Constants.getEntry("SOIL_ALBEDO", "Error");
      BARE_SOIL_z0 = Constants.getEntry("BARE_SOIL_z0", "Error");
      CALCULATION_STEP_LENGTH = Constants.getEntry("CALCULATION_STEP_LENGTH", "Error");
      CHANGE_BC = Constants.getEntry("CHANGE_BC", "Error");
      SNP_SOIL = Constants.getEntry("SNP_SOIL", "Error");
      SOIL_FLUX = Constants.getEntry("SOIL_FLUX", "Error");
      DEPTH_1 = Constants.getEntry("DEPTH_1", "Error");
      DEPTH_2 = Constants.getEntry("DEPTH_2", "Error");
      DEPTH_3 = Constants.getEntry("DEPTH_3", "Error");
      DEPTH_4 = Constants.getEntry("DEPTH_4", "Error");
      DEPTH_5 = Constants.getEntry("DEPTH_5", "Error");
      TS_WRITE = Constants.getEntry("TS_WRITE", "Error");
      TS_START = Constants.getEntry("TS_START", "Error");
      TS_DAYS_BETWEEN = Constants.getEntry("TS_DAYS_BETWEEN", "Error");
      PROF_WRITE = Constants.getEntry("PROF_WRITE", "Error");
      PROF_START = Constants.getEntry("PROF_START", "Error");
      PROF_DAYS_BETWEEN = Constants.getEntry("PROF_DAYS_BETWEEN", "Error");
      CANOPY = Constants.getEntry("CANOPY","Error");
      //Schirmer
      SNOW_RED = Constants.getEntry("SNOW_REDISTRIBUTION","Error");

      PROF_EVAL = Constants.getEntry("PROF_EVAL","Error");
      PROF_FILE = Constants.getEntry("PROF_FILE","Error");
      OUTPATH = Constants.getEntry("OUTPATH", "Error");
      RESEARCH_STATION = Constants.getEntry("RESEARCH_STATION", "Error");
      EXPERIMENT = Constants.getEntry("EXPERIMENT", "Error");
      MODEL_DIRECTORY = Constants.getEntry("MODEL_DIRECTORY","Error");


      Setup.SetSnowpackPath(MODEL_DIRECTORY);
  }

  void grabData()
  // Grab the data currently resident in the ModelDialog frame.
  {
     // JTextField Data, also remove front and back blanks
     SNOWFILE = jText_SNOWFILE.getText().trim();
     METEOFILE = jText_METEOFILE.getText().trim();
     METEO_STEP_LENGTH = jText_METEO_STEP_LENGTH.getText().trim();
     T_INTERNAL = jText_T_INTERNAL.getText().trim();
     HEIGHT_OF_WIND_VALUE = jText_HEIGHT_OF_WIND_VALUE.getText().trim();
     HEIGHT_OF_METEO_VALUES = jText_HEIGHT_OF_METEO_VALUES.getText().trim();
     NEUTRAL = jText_NEUTRAL.getText().trim();
     ROUGHNESS_LENGTH = jText_ROUGHNESS_LENGTH.getText().trim();
     THRESH_CHANGE_BC = jText_THRESH_CHANGE_BC.getText().trim();
     GEO_HEAT = jText_GEO_HEAT.getText().trim();
     SOIL_ALBEDO = jText_SOIL_ALBEDO.getText().trim();
     BARE_SOIL_z0 = jText_BARE_SOIL_z0.getText().trim();
     CALCULATION_STEP_LENGTH = jText_CALCULATION_STEP_LENGTH.getText().trim();
     DEPTH_1 = jText_DEPTH_1.getText().trim();
     DEPTH_2 = jText_DEPTH_2.getText().trim();
     DEPTH_3 = jText_DEPTH_3.getText().trim();
     DEPTH_4 = jText_DEPTH_4.getText().trim();
     DEPTH_5 = jText_DEPTH_5.getText().trim();
     TS_START = jText_TS_START.getText().trim();
     TS_DAYS_BETWEEN = jText_TS_DAYS_BETWEEN.getText().trim();
     PROF_START = jText_PROF_START.getText().trim();
     PROF_DAYS_BETWEEN = jText_PROF_DAYS_BETWEEN.getText().trim();
     OUTPATH = jText_OUTPATH.getText().trim();
     RESEARCH_STATION = jText_RESEARCH_STATION.getText().trim();
     EXPERIMENT = jText_EXPERIMENT.getText().trim();
     MODEL_DIRECTORY = jText_MODEL_DIRECTORY.getText().trim();
     SW_REF = jText_SW_REF.getText().trim();//Schirmer


     PROF_FILE= jText_PROF_FILE.getText().trim();

     Setup.SetSnowpackPath(MODEL_DIRECTORY);

     // JComboBox Data
     if (jComboBox_CHANGE_BC.getSelectedIndex() == 1) CHANGE_BC = "1";
     else                                             CHANGE_BC = "0";

     if (jComboBox_SOIL_FLUX.getSelectedIndex() == 1) SOIL_FLUX = "0";
     else                                             SOIL_FLUX = "1";

     // JButton Data
     // Member variables connected to JButtons are actualized immediately
     // after the button is pressed (don't have to be grabbed).
  }

  boolean checkData()
  {
    // Check if empty parameter strings exist
    if (
       SNOWFILE.length()<1 ||
       METEOFILE.length()<1 ||
       METEO_STEP_LENGTH.length()<1 ||
       MEAS_TSS.length()<1 ||
       ENFORCE_MEASURED_SNOW_HEIGHTS.length()<1 ||
       SW_REF.length()<1 ||
       INCOMING_LONGWAVE.length()<1 ||
       T_INTERNAL.length()<1 ||
       HEIGHT_OF_WIND_VALUE.length()<1 ||
       HEIGHT_OF_METEO_VALUES.length()<1 ||
       NEUTRAL.length()<1 ||
       ROUGHNESS_LENGTH.length()<1 ||
       THRESH_CHANGE_BC.length()<1 ||
       GEO_HEAT.length()<1 ||
       SOIL_ALBEDO.length()<1 ||
       BARE_SOIL_z0.length()<1 ||
       CALCULATION_STEP_LENGTH.length()<1 ||
       CHANGE_BC.length()<1 ||
       SNP_SOIL.length()<1 ||
       SOIL_FLUX.length()<1 ||
       DEPTH_1.length()<1 ||
       DEPTH_2.length()<1 ||
       DEPTH_3.length()<1 ||
       DEPTH_4.length()<1 ||
       DEPTH_5.length()<1 ||
       TS_WRITE.length()<1 ||
       TS_START.length()<1 ||
       TS_DAYS_BETWEEN.length()<1 ||
       PROF_WRITE.length()<1 ||
       PROF_START.length()<1 ||
       PROF_DAYS_BETWEEN.length()<1 ||
       OUTPATH.length()<1 ||
       RESEARCH_STATION.length()<1 ||
       EXPERIMENT.length()<1 ||
       MODEL_DIRECTORY.length()<1 ||
       PROF_FILE.length()<1 )

    {
       jLabel_Error.setText("No empty edit boxes permitted");
       return false;
    }

    // Check boolean parameters
    if (!(MEAS_TSS.equals("0") || MEAS_TSS.equals("1")) ||
        !(ENFORCE_MEASURED_SNOW_HEIGHTS.equals("0") || ENFORCE_MEASURED_SNOW_HEIGHTS.equals("1")) ||
        //!(SW_REF.equals("0") || SW_REF.equals("1")) || (Schirmer)
        !(INCOMING_LONGWAVE.equals("0") || INCOMING_LONGWAVE.equals("1")) ||
        !(CHANGE_BC.equals("0") || CHANGE_BC.equals("1")) ||
        !(SNP_SOIL.equals("0") || SNP_SOIL.equals("1")) ||
        !(SOIL_FLUX.equals("0") || SOIL_FLUX.equals("1")) ||
        !(TS_WRITE.equals("0") || TS_WRITE.equals("1")) ||
        !(PROF_WRITE.equals("0") || PROF_WRITE.equals("1")) ||
        !(CANOPY.equals("0") || CANOPY.equals("1")) ||
    //Schirmer
        !(SNOW_RED.equals("0") || SNOW_RED.equals("1")) ||

        !(PROF_EVAL.equals("0") || PROF_EVAL.equals("1")))
    {
       jLabel_Error.setText("Boolean internal parameter has values other than 0 or 1.");
       return false;
    }

    // Check if number-strings currently contained in the JTextFields are valid float numbers
    try  {Float.parseFloat(T_INTERNAL); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: No. of Measured Snow/Soil Temp. not a valid number!");
      return false;
    }
    //Schirmer
    try {Integer.parseInt(SW_REF); }
    catch (NumberFormatException nfe)
    {
        jLabel_Error.setText("Error: SW_REF is not a valid number!");
        return false;
    }

    try  {Float.parseFloat(METEO_STEP_LENGTH); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Meteo Step Length not a valid number!");
      return false;
    }
    try  {Float.parseFloat(HEIGHT_OF_WIND_VALUE); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Height of Wind Value not a valid number!");
      return false;
    }
    try  {Float.parseFloat(HEIGHT_OF_METEO_VALUES); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Height of Meteo Values not a valid number!");
      return false;
    }
    try  {Integer.parseInt(NEUTRAL); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: NEUTRAL not a valid number!");
      return false;
    }
    try  {Float.parseFloat(ROUGHNESS_LENGTH); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Roughness Length not a valid number!");
      return false;
    }
    try  {Float.parseFloat(THRESH_CHANGE_BC); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: BC Threshold not a valid number!");
      return false;
    }
    try  {Float.parseFloat(GEO_HEAT); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Geothermal Heat Flux not a valid number!");
      return false;
    }
    /*  to delete soil albedo and bare soil
    try  {Float.parseFloat(SOIL_ALBEDO); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Soil Albedo not a valid number!");
      return false;
    }
    try  {Float.parseFloat(BARE_SOIL_z0); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Bare Soil z0 not a valid number!");
      return false;
    }*/
    try  {Float.parseFloat(CALCULATION_STEP_LENGTH); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Calculation Step Length not a valid number!");
      return false;
    }
    try  {Float.parseFloat(DEPTH_1);
          Float.parseFloat(DEPTH_2);
          Float.parseFloat(DEPTH_3);
          Float.parseFloat(DEPTH_4);
          Float.parseFloat(DEPTH_5);
     }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Temperature Depth not a valid number!");
      return false;
    }
    try  {Float.parseFloat(TS_START);
          Float.parseFloat(PROF_START);
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Start Hour not a valid number!");
      return false;
    }
    try  {Float.parseFloat(TS_DAYS_BETWEEN);
          Float.parseFloat(PROF_DAYS_BETWEEN);
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Days Between not a valid number!");
      return false;
    }
    // Check if parameters are within allowed thresholds
    // To make code shorter, subroutine from SnowInputDialog could be used.
    try {
      float x, x1, x2, x3, x4, x5;
      int n;

      x = Float.parseFloat(T_INTERNAL);
      if (!(x == 0.0 || x == 1.0 || x == 2.0 || x == 3.0 || x == 4.0 || x == 5.0 ))
      {
         jLabel_Error.setText("Error: No. of Measured Snow/Soil Temp.: valid values 0, 1, 2, 3, 4 or 5!");
         return false;
      }

      x = Float.parseFloat(METEO_STEP_LENGTH);
      if (x < 30.0 || x > 24*60.0)
      {
         jLabel_Error.setText("Error: Meteo Step Length: valid range 30 min - 1 day!");
         return false;
      }

      x = Float.parseFloat(HEIGHT_OF_WIND_VALUE);
      if (x < 1.0 || x > 50.0)
      {
         jLabel_Error.setText("Error: Height of Wind Value: valid range 1 - 50 m!");
         return false;
      }

      x = Float.parseFloat(HEIGHT_OF_METEO_VALUES);
      if (x < 1.0 || x > 50.0)
      {
         jLabel_Error.setText("Error: Height of Meteo Values: valid range 1 - 50 m!");
         return false;
      }
      //Schirmer
      n = Integer.parseInt(SW_REF);
      if (n != 0 && n!= 1 && n != 2 && n != 10  && n != 11 && n != 12) {
          jLabel_Error.setText("Error: SW Radiation: valid is 0, 1, 2, 10, 11, 12!");
          return false;
      }

      n = Integer.parseInt(NEUTRAL);
      if (n != -1 && n != 0 && n != 1)
      {
         jLabel_Error.setText("Error: NEUTRAL: valid is -1, 0, 1!");
         return false;
      }


      x = Float.parseFloat(ROUGHNESS_LENGTH);
      if (x < 0.0005 || x > 0.5)
      {
         jLabel_Error.setText("Error: Roughness Length: valid range 0.0005 - 0.5 m!");
         return false;
      }

      x = Float.parseFloat(THRESH_CHANGE_BC);
      if (x < -3.0 || x > 0.0)
      {
         jLabel_Error.setText("Error: BC Threshold: valid range -3 - 0 C!");
         return false;
      }

      x = Float.parseFloat(GEO_HEAT);
      if (x < 0.01 || x > 0.1)
      {
         jLabel_Error.setText("Error: Geothermal Heat Flux: valid range 0.01 - 0.1!");
         return false;
      }

      x = Float.parseFloat(SOIL_ALBEDO);
      if (x < 0.0 || x > 1.0)
      {
         jLabel_Error.setText("Error: Soil Albedo: valid range 0 - 1!");
         return false;
      }

      x = Float.parseFloat(BARE_SOIL_z0);
      if (x < 0.0005 || x > 0.5)
      {
         jLabel_Error.setText("Error: Bare Soil z0: valid range 0.0005 - 0.5 m!");
         return false;
      }

      x = Float.parseFloat(CALCULATION_STEP_LENGTH);
      if (x < 1.0 || x > 60.0)
      {
         jLabel_Error.setText("Error: Calculation Step Length: valid range 1 - 60 min!");
         return false;
      }

      x1 = Float.parseFloat(DEPTH_1);
      x2 = Float.parseFloat(DEPTH_2);
      x3 = Float.parseFloat(DEPTH_3);
      x4 = Float.parseFloat(DEPTH_4);
      x5 = Float.parseFloat(DEPTH_5);

      if (x1 < -50 || x2 < -50 || x3 < -50 || x4 < -50 || x5 < -50
       || x1 > 10  || x2 > 10  || x3 > 10  || x4 > 10  || x5 > 10 )
      {
         jLabel_Error.setText("Error: Temperature Depth < -50 m or > 10 m!");
         return false;
      }

      x1 = Float.parseFloat(TS_START);
      x2 = Float.parseFloat(PROF_START);
      if (x1 < 0.0 || x2 < 0.0 || x1 >= 24.0 || x2 >= 24.0)
      {
         jLabel_Error.setText("Error: Start Hour: valid range >=0, <24!");
         return false;
      }

      x1 = Float.parseFloat(TS_DAYS_BETWEEN);
      x2 = Float.parseFloat(PROF_DAYS_BETWEEN);
      if (x1 <= 0.0 || x2 <= 0.0 || x1 > 31.0 || x2 > 31.0)
      {
         jLabel_Error.setText("Error: Days Between: valid range >0, <=31!");
         return false;
      }
    } catch (NumberFormatException nfe) {}

    // Remove blanks from begin and end of string
    RESEARCH_STATION.trim();
    EXPERIMENT.trim();
    SNOWFILE.trim();
    METEOFILE.trim();
    OUTPATH.trim();
    MODEL_DIRECTORY.trim();
    PROF_FILE.trim();

    // String StringToTest[] = {RESEARCH_STATION, EXPERIMENT,
    //                          SNOWFILE, METEOFILE, OUTPATH, MODEL_DIRECTORY};
    // Working with this String might make the code shorter!

    // Check of the real string parameters;
    // should just contain letters, numbers or "_"
    if (!stringFormatCheck(RESEARCH_STATION, false))
    {
          jLabel_Error.setText("Error: " + RESEARCH_STATION +
            ": only letters a-z, numbers, underline (_) and dot (.) allowed!");
          return false;
    }
    if (!stringFormatCheck(EXPERIMENT, false))
    {
          jLabel_Error.setText("Error: " + EXPERIMENT +
            ": only letters a-z, numbers, underline (_) and dot (.) allowed!");
          return false;
    }

    // Conversion of slashes to backslashes (and vice versa)
    MODEL_DIRECTORY = SlashConverted(MODEL_DIRECTORY);
    SNOWFILE        = SlashConverted(SNOWFILE);
    METEOFILE       = SlashConverted(METEOFILE);
    OUTPATH         = SlashConverted(OUTPATH);
    PROF_FILE   = SlashConverted(PROF_FILE);

    // Check for correct path names
    if (!pathFormatCheck(MODEL_DIRECTORY))
    {  jLabel_Error.setText("Error in Path Name: " + MODEL_DIRECTORY); return false; }
    if (!pathFormatCheck(SNOWFILE))
    {  jLabel_Error.setText("Error in Path Name: " + SNOWFILE); return false; }
    if (!pathFormatCheck(METEOFILE))
    {  jLabel_Error.setText("Error in Path Name: " + METEOFILE); return false; }
    if (!pathFormatCheck(OUTPATH))
    {  jLabel_Error.setText("Error in Path Name: " + OUTPATH); return false; }
    if(PROF_EVAL.equals("1"))
      {
      if (!pathFormatCheck(PROF_FILE))
      {  jLabel_Error.setText("Error in Path Name: " + PROF_FILE); return false; }
    }


         // Check if paths exist
         if (!pathExists(SNOWFILE)) return false;
         if (!pathExists(METEOFILE)) return false;
         if (!pathExists(OUTPATH)) return false;
     if(PROF_EVAL.equals("1"))
      {
        if (!pathExists(PROF_FILE)) return false;
      }

         // Check if MODEL_DIRECTORY exists
         File modeldir = new File(MODEL_DIRECTORY);
         if (!modeldir.exists())
         {
         jLabel_Error.setText("File does not exist: " + modeldir.getPath());
         return false;
         }

    // Consistency checks
    // Check if Canopy Data is on, although Soil Data is OFF

    if (CANOPY.equals("1") && SNP_SOIL.equals("0")) {

        jLabel_Error.setText("Warning: Canopy model requires soil data - Please switch Inclusion of Soil data = ON!");
        return false;
    }
    // Check if reference height for meteorological data is above canopy height



    // Check if Dirichlet upper boundary condition is ON, although no measured
    // surface temperatures are available
    System.out.println("MEAS_TSS: "+MEAS_TSS);
    System.out.println("CHANGE_BC: " + CHANGE_BC);

    if (MEAS_TSS.equals("0") && CHANGE_BC.equals("1"))
    {
       MessageBox mBox = new MessageBox(mFrame,
         "Warning", "Measured surface temperatures not available,",
         "but surface Dirichlet boundary conditions used?!");
       mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
       mBox.setVisible(true);

       jLabel_Error.setText("Warning: Measured surf. temp. not available , but surface Dirichlet BC used?!");
       //return false;
    }

    return true;
  }

  /**
   * Schirmer
   * Does the same like checkData, only opens ConfirmBoxes when paths or files do not exist
   *
   * @return boolean
   */
  boolean checkDataForSave()
  {
    // Check if empty parameter strings exist
    if (
       SNOWFILE.length()<1 ||
       METEOFILE.length()<1 ||
       METEO_STEP_LENGTH.length()<1 ||
       MEAS_TSS.length()<1 ||
       ENFORCE_MEASURED_SNOW_HEIGHTS.length()<1 ||
       SW_REF.length()<1 ||
       INCOMING_LONGWAVE.length()<1 ||
       T_INTERNAL.length()<1 ||
       HEIGHT_OF_WIND_VALUE.length()<1 ||
       HEIGHT_OF_METEO_VALUES.length()<1 ||
       NEUTRAL.length()<1 ||
       ROUGHNESS_LENGTH.length()<1 ||
       THRESH_CHANGE_BC.length()<1 ||
       GEO_HEAT.length()<1 ||
       SOIL_ALBEDO.length()<1 ||
       BARE_SOIL_z0.length()<1 ||
       CALCULATION_STEP_LENGTH.length()<1 ||
       CHANGE_BC.length()<1 ||
       SNP_SOIL.length()<1 ||
       SOIL_FLUX.length()<1 ||
       DEPTH_1.length()<1 ||
       DEPTH_2.length()<1 ||
       DEPTH_3.length()<1 ||
       DEPTH_4.length()<1 ||
       DEPTH_5.length()<1 ||
       TS_WRITE.length()<1 ||
       TS_START.length()<1 ||
       TS_DAYS_BETWEEN.length()<1 ||
       PROF_WRITE.length()<1 ||
       PROF_START.length()<1 ||
       PROF_DAYS_BETWEEN.length()<1 ||
       OUTPATH.length()<1 ||
       RESEARCH_STATION.length()<1 ||
       EXPERIMENT.length()<1 ||
       MODEL_DIRECTORY.length()<1 ||
       PROF_FILE.length()<1 )

    {
       jLabel_Error.setText("No empty edit boxes permitted");
       return false;
    }

    // Check boolean parameters
    if (!(MEAS_TSS.equals("0") || MEAS_TSS.equals("1")) ||
        !(ENFORCE_MEASURED_SNOW_HEIGHTS.equals("0") || ENFORCE_MEASURED_SNOW_HEIGHTS.equals("1")) ||
        //!(SW_REF.equals("0") || SW_REF.equals("1")) || (Schirmer)
        !(INCOMING_LONGWAVE.equals("0") || INCOMING_LONGWAVE.equals("1")) ||
        !(CHANGE_BC.equals("0") || CHANGE_BC.equals("1")) ||
        !(SNP_SOIL.equals("0") || SNP_SOIL.equals("1")) ||
        !(SOIL_FLUX.equals("0") || SOIL_FLUX.equals("1")) ||
        !(TS_WRITE.equals("0") || TS_WRITE.equals("1")) ||
        !(PROF_WRITE.equals("0") || PROF_WRITE.equals("1")) ||
        !(CANOPY.equals("0") || CANOPY.equals("1")) ||
    //Schirmer
        !(SNOW_RED.equals("0") || SNOW_RED.equals("1")) ||

        !(PROF_EVAL.equals("0") || PROF_EVAL.equals("1")))
    {
       jLabel_Error.setText("Boolean internal parameter has values other than 0 or 1.");
       return false;
    }

    // Check if number-strings currently contained in the JTextFields are valid float numbers
    try  {Float.parseFloat(T_INTERNAL); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: No. of Measured Snow/Soil Temp. not a valid number!");
      return false;
    }
    //Schirmer
    try {Integer.parseInt(SW_REF); }
    catch (NumberFormatException nfe)
    {
        jLabel_Error.setText("Error: SW_REF is not a valid number!");
        return false;
    }

    try  {Float.parseFloat(METEO_STEP_LENGTH); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Meteo Step Length not a valid number!");
      return false;
    }
    try  {Float.parseFloat(HEIGHT_OF_WIND_VALUE); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Height of Wind Value not a valid number!");
      return false;
    }
    try  {Float.parseFloat(HEIGHT_OF_METEO_VALUES); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Height of Meteo Values not a valid number!");
      return false;
    }
    try  {Integer.parseInt(NEUTRAL); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: NEUTRAL not a valid number!");
      return false;
    }
    try  {Float.parseFloat(ROUGHNESS_LENGTH); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Roughness Length not a valid number!");
      return false;
    }
    try  {Float.parseFloat(THRESH_CHANGE_BC); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: BC Threshold not a valid number!");
      return false;
    }
    try  {Float.parseFloat(GEO_HEAT); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Geothermal Heat Flux not a valid number!");
      return false;
    }
    /*  to delete soil albedo and bare soil
    try  {Float.parseFloat(SOIL_ALBEDO); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Soil Albedo not a valid number!");
      return false;
    }
    try  {Float.parseFloat(BARE_SOIL_z0); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Bare Soil z0 not a valid number!");
      return false;
    }*/
    try  {Float.parseFloat(CALCULATION_STEP_LENGTH); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Calculation Step Length not a valid number!");
      return false;
    }
    try  {Float.parseFloat(DEPTH_1);
          Float.parseFloat(DEPTH_2);
          Float.parseFloat(DEPTH_3);
          Float.parseFloat(DEPTH_4);
          Float.parseFloat(DEPTH_5);
     }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Temperature Depth not a valid number!");
      return false;
    }
    try  {Float.parseFloat(TS_START);
          Float.parseFloat(PROF_START);
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Start Hour not a valid number!");
      return false;
    }
    try  {Float.parseFloat(TS_DAYS_BETWEEN);
          Float.parseFloat(PROF_DAYS_BETWEEN);
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Days Between not a valid number!");
      return false;
    }
    // Check if parameters are within allowed thresholds
    // To make code shorter, subroutine from SnowInputDialog could be used.
    try {
      float x, x1, x2, x3, x4, x5;
      int n;

      x = Float.parseFloat(T_INTERNAL);
      if (!(x == 0.0 || x == 1.0 || x == 2.0 || x == 3.0 || x == 4.0 || x == 5.0 ))
      {
         jLabel_Error.setText("Error: No. of Measured Snow/Soil Temp.: valid values 0, 1, 2, 3, 4 or 5!");
         return false;
      }

      x = Float.parseFloat(METEO_STEP_LENGTH);
      if (x < 30.0 || x > 24*60.0)
      {
         jLabel_Error.setText("Error: Meteo Step Length: valid range 30 min - 1 day!");
         return false;
      }

      x = Float.parseFloat(HEIGHT_OF_WIND_VALUE);
      if (x < 1.0 || x > 50.0)
      {
         jLabel_Error.setText("Error: Height of Wind Value: valid range 1 - 50 m!");
         return false;
      }

      x = Float.parseFloat(HEIGHT_OF_METEO_VALUES);
      if (x < 1.0 || x > 50.0)
      {
         jLabel_Error.setText("Error: Height of Meteo Values: valid range 1 - 50 m!");
         return false;
      }
      //Schirmer
      n = Integer.parseInt(SW_REF);
      if (n != 0 && n!= 1 && n != 2 && n != 10  && n != 11 && n != 12) {
          jLabel_Error.setText("Error: SW Radiation: valid is 0, 1, 2, 10, 11, 12!");
          return false;
      }

      n = Integer.parseInt(NEUTRAL);
      if (n != -1 && n != 0 && n != 1)
      {
         jLabel_Error.setText("Error: NEUTRAL: valid is -1, 0, 1!");
         return false;
      }


      x = Float.parseFloat(ROUGHNESS_LENGTH);
      if (x < 0.0005 || x > 0.5)
      {
         jLabel_Error.setText("Error: Roughness Length: valid range 0.0005 - 0.5 m!");
         return false;
      }

      x = Float.parseFloat(THRESH_CHANGE_BC);
      if (x < -3.0 || x > 0.0)
      {
         jLabel_Error.setText("Error: BC Threshold: valid range -3 - 0 C!");
         return false;
      }

      x = Float.parseFloat(GEO_HEAT);
      if (x < 0.01 || x > 0.1)
      {
         jLabel_Error.setText("Error: Geothermal Heat Flux: valid range 0.01 - 0.1!");
         return false;
      }

      x = Float.parseFloat(SOIL_ALBEDO);
      if (x < 0.0 || x > 1.0)
      {
         jLabel_Error.setText("Error: Soil Albedo: valid range 0 - 1!");
         return false;
      }

      x = Float.parseFloat(BARE_SOIL_z0);
      if (x < 0.0005 || x > 0.5)
      {
         jLabel_Error.setText("Error: Bare Soil z0: valid range 0.0005 - 0.5 m!");
         return false;
      }

      x = Float.parseFloat(CALCULATION_STEP_LENGTH);
      if (x < 1.0 || x > 60.0)
      {
         jLabel_Error.setText("Error: Calculation Step Length: valid range 1 - 60 min!");
         return false;
      }

      x1 = Float.parseFloat(DEPTH_1);
      x2 = Float.parseFloat(DEPTH_2);
      x3 = Float.parseFloat(DEPTH_3);
      x4 = Float.parseFloat(DEPTH_4);
      x5 = Float.parseFloat(DEPTH_5);

      if (x1 < -50 || x2 < -50 || x3 < -50 || x4 < -50 || x5 < -50
       || x1 > 10  || x2 > 10  || x3 > 10  || x4 > 10  || x5 > 10 )
      {
         jLabel_Error.setText("Error: Temperature Depth < -50 m or > 10 m!");
         return false;
      }

      x1 = Float.parseFloat(TS_START);
      x2 = Float.parseFloat(PROF_START);
      if (x1 < 0.0 || x2 < 0.0 || x1 >= 24.0 || x2 >= 24.0)
      {
         jLabel_Error.setText("Error: Start Hour: valid range >=0, <24!");
         return false;
      }

      x1 = Float.parseFloat(TS_DAYS_BETWEEN);
      x2 = Float.parseFloat(PROF_DAYS_BETWEEN);
      if (x1 <= 0.0 || x2 <= 0.0 || x1 > 31.0 || x2 > 31.0)
      {
         jLabel_Error.setText("Error: Days Between: valid range >0, <=31!");
         return false;
      }
    } catch (NumberFormatException nfe) {}

    // Remove blanks from begin and end of string
    RESEARCH_STATION.trim();
    EXPERIMENT.trim();
    SNOWFILE.trim();
    METEOFILE.trim();
    OUTPATH.trim();
    MODEL_DIRECTORY.trim();
    PROF_FILE.trim();

    // String StringToTest[] = {RESEARCH_STATION, EXPERIMENT,
    //                          SNOWFILE, METEOFILE, OUTPATH, MODEL_DIRECTORY};
    // Working with this String might make the code shorter!

    // Check of the real string parameters;
    // should just contain letters, numbers or "_"
    if (!stringFormatCheck(RESEARCH_STATION, false))
    {
          jLabel_Error.setText("Error: " + RESEARCH_STATION +
            ": only letters a-z, numbers, underline (_) and dot (.) allowed!");
          return false;
    }
    if (!stringFormatCheck(EXPERIMENT, false))
    {
          jLabel_Error.setText("Error: " + EXPERIMENT +
            ": only letters a-z, numbers, underline (_) and dot (.) allowed!");
          return false;
    }

    // Conversion of slashes to backslashes (and vice versa)
    MODEL_DIRECTORY = SlashConverted(MODEL_DIRECTORY);
    SNOWFILE        = SlashConverted(SNOWFILE);
    METEOFILE       = SlashConverted(METEOFILE);
    OUTPATH         = SlashConverted(OUTPATH);
    PROF_FILE   = SlashConverted(PROF_FILE);

    // Check for correct path names
    if (!pathFormatCheck(MODEL_DIRECTORY))
    {  jLabel_Error.setText("Error in Path Name: " + MODEL_DIRECTORY); return false; }
    if (!pathFormatCheck(SNOWFILE))
    {  jLabel_Error.setText("Error in Path Name: " + SNOWFILE); return false; }
    if (!pathFormatCheck(METEOFILE))
    {  jLabel_Error.setText("Error in Path Name: " + METEOFILE); return false; }
    if (!pathFormatCheck(OUTPATH))
    {  jLabel_Error.setText("Error in Path Name: " + OUTPATH); return false; }
    if(PROF_EVAL.equals("1"))
      {
      if (!pathFormatCheck(PROF_FILE))
      {  jLabel_Error.setText("Error in Path Name: " + PROF_FILE); return false; }
    }

    /*Schirmer
         // Check if paths exist
         if (!pathExists(SNOWFILE)) return false;
         if (!pathExists(METEOFILE)) return false;
         if (!pathExists(OUTPATH)) return false;
     if(PROF_EVAL.equals("1"))
      {
        if (!pathExists(PROF_FILE)) return false;
      }

         // Check if MODEL_DIRECTORY exists
         File modeldir = new File(MODEL_DIRECTORY);
         if (!modeldir.exists())
         {
         jLabel_Error.setText("File does not exist: " + modeldir.getPath());
         return false;
         }
     */

    //Schirmer
    //Check if paths exists and open a warning message box
    File modeldir = new File(MODEL_DIRECTORY);
    if (!modeldir.exists()) {
        int opt;

        opt = JOptionPane.showConfirmDialog(this,"Model Directory does not exist: " + MODEL_DIRECTORY +
                                            "! Save anyway?","",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.ERROR_MESSAGE);

        if (opt == JOptionPane.NO_OPTION) {
            return false;
        }
    }
    if (!pathExists(SNOWFILE)) {
        int opt;

        opt = JOptionPane.showConfirmDialog(this,
                "File or path of 'Snow and Soil Data' does not exist: " +
                                            SNOWFILE +
                                            "! Save anyway?", "",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.ERROR_MESSAGE);

        if (opt == JOptionPane.NO_OPTION) {
            return false;
        }
    }
    if (!pathExists(METEOFILE)) {
        int opt;

        opt = JOptionPane.showConfirmDialog(this,
                "File or path of 'Meteo Data' does not exist: " + METEOFILE +
                                            "! Save anyway?", "",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.ERROR_MESSAGE);

        if (opt == JOptionPane.NO_OPTION) {
            return false;
        }
    }
    if (!pathExists(OUTPATH)) {
        int opt;

        opt = JOptionPane.showConfirmDialog(this,
                "'Output File'-Path does not exist: " + OUTPATH +
                                            "! Save anyway?", "",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.ERROR_MESSAGE);

        if (opt == JOptionPane.NO_OPTION) {
            return false;
        }
    }
    if (PROF_EVAL.equals("1")) {
        if (!pathExists(PROF_FILE)) {
            int opt;

            opt = JOptionPane.showConfirmDialog(this,
                    "File or path of 'Profile Data File' does not exist: " +
                                                PROF_FILE +
                                                "! Save anyway?", "",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.ERROR_MESSAGE);

            if (opt == JOptionPane.NO_OPTION) {
                return false;
            }
        }
    }//end Schirmer



    // Consistency checks
    // Check if Canopy Data is on, although Soil Data is OFF

    if (CANOPY.equals("1") && SNP_SOIL.equals("0")) {

        jLabel_Error.setText("Warning: Canopy model requires soil data - Please switch Inclusion of Soil data = ON!");
        return false;
    }
    // Check if reference height for meteorological data is above canopy height



    // Check if Dirichlet upper boundary condition is ON, although no measured
    // surface temperatures are available
    System.out.println("MEAS_TSS: "+MEAS_TSS);
    System.out.println("CHANGE_BC: " + CHANGE_BC);

    if (MEAS_TSS.equals("0") && CHANGE_BC.equals("1"))
    {
       MessageBox mBox = new MessageBox(mFrame,
         "Warning", "Measured surface temperatures not available,",
         "but surface Dirichlet boundary conditions used?!");
       mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
       mBox.setVisible(true);

       jLabel_Error.setText("Warning: Measured surf. temp. not available , but surface Dirichlet BC used?!");
       //return false;
    }

    return true;
  }



  static String SlashConverted(String PATH)
  // Slashes in String PATH are converted to backslashes if the
  // program is started under Windows.
  // Backslashes are converted to slashes if program started under Unix.
  {
      String ConvertedString;
      ConvertedString = PATH;

      for (int i=0; i<PATH.length(); i++)
      {

        if (System.getProperty("os.name").substring(0,3).equals("Win")) // Windows used
        {
           if (PATH.substring(i,i+1).equals("/"))
           {  ConvertedString =
              ConvertedString.substring(0,i) + "\\" + PATH.substring(i+1, PATH.length());
           }
        }
        else // Unix used
        {
           if (PATH.substring(i,i+1).equals("\\"))
           {  ConvertedString =
              ConvertedString.substring(0,i) + "/" + PATH.substring(i+1, PATH.length());
           }
        }
      }

      return ConvertedString;
  }


  boolean pathExists(String PATH)
  {
  // Check if PATH exists
      File file;
      if ( (PATH.startsWith("/")) ||   // Absolute path, Unix
           (PATH.startsWith("\\")) ||  // Absolute path, Windows
          ((PATH.substring(1,2).equals(":")) && PATH.substring(2,3).equals("\\")))
          file = new File(PATH);
      else
          file = new File(MODEL_DIRECTORY + "/" + PATH);

      if (!file.exists())
      {
           //jLabel_Error.setText("File or path does not exist: " + file.getPath());Schirmer
           return false;
      }

      return true;
  }


  static boolean stringFormatCheck(String StringToTest, boolean isPath)
  {
    // Check if StringToTest is a String (should just contain letters, numbers or "_").
    // If isPath is true --> input string is path, also \ / : allowed

      String LetterString = "0123456789abcdefghijklmnopqrstuvwxyz_.";

      for (int i=0; i<StringToTest.length(); i++) // loop over letters of string
      {
        exitpoint:
        {
          for (int j=0; j<LetterString.length(); j++) // loop over permitted letters
          {
            if (StringToTest.substring(i, i+1).equalsIgnoreCase(LetterString.substring(j, j+1)))
              break exitpoint;

          } // end j

          // Allow / \ : in path names
          if (isPath)
          {
             if ((StringToTest.substring(i, i+1).equals("/"))
              || (StringToTest.substring(i, i+1).equals("\\")))
             {
               break exitpoint;
             }
             if ((i==1) && (System.getProperty("os.name").substring(0,3).equals("Win")) &&
                           (StringToTest.substring(i, i+1).equals(":")))
             {
               break exitpoint;
             }
          }

          return false;

        } // end exitpoint
      } // end i

      return true;
  }


  static boolean pathFormatCheck(String pathToCheck)
  {
    // Check
    //schirmer
    //if (!stringFormatCheck(pathToCheck, true)) return false;
    //end schirmer

    // Check for correct path names
    // Conversion of backslash into slash just processed her, not known outside
    String prevletter = null;
    String letter = pathToCheck.substring(0, 1);

    // Check if only one letter (slash or backslash)
    if ((letter.equals("\\") || letter.equals("/")) && (pathToCheck.length()==1))
    {
          return false;
    }

    if (letter.equals("\\"))
    {  pathToCheck = "/" + pathToCheck.substring(1, pathToCheck.length());
       letter = "/";
    }

    // Check if two slashes are behind each other
    for (int i=1; i<pathToCheck.length(); i++)
    {
      prevletter = letter;
      letter = pathToCheck.substring(i, i+1);
      if (letter.equals("\\"))
      { pathToCheck =
        pathToCheck.substring(0, i) + "/" + pathToCheck.substring(i+1, pathToCheck.length());
        letter = "/";
      }
      if ((letter.equals(prevletter)) && (letter.equals("/")))
      {
          return false;
      }
    }

    return true;
  }



  /**
   * Does the same as saveDate, but saves the data under the specified filename File
   * (Schirmer)
   *
   * @param file File
   * @return boolean
   */
  boolean saveData(File file) {
          // Get the current values of the SNOWPACK parameters
     grabData();

     // Check the data for errors.
     // If errors exist, they are described in jLabel_Error and can be corrected.
     if (!checkDataForSave()) return false;

     try {
       // Store the data in file
       File test = new File(file.toString());
       IniFile Constants = new IniFile();
       Constants.setFileName(file.toString());
       Constants.clear(); // clear contents of previous values
       Constants.setSection("Parameters");
       Constants.setEntry("SNOWFILE", SNOWFILE);
       Constants.setEntry("METEOFILE", METEOFILE);
       Constants.setEntry("METEO_STEP_LENGTH", METEO_STEP_LENGTH);
       Constants.setEntry("MEAS_TSS", MEAS_TSS);
       Constants.setEntry("ENFORCE_MEASURED_SNOW_HEIGHTS", ENFORCE_MEASURED_SNOW_HEIGHTS);
       Constants.setEntry("SW_REF", SW_REF);
       Constants.setEntry("INCOMING_LONGWAVE", INCOMING_LONGWAVE);
       Constants.setEntry("T_INTERNAL", T_INTERNAL);
       Constants.setEntry("HEIGHT_OF_WIND_VALUE", HEIGHT_OF_WIND_VALUE);
       Constants.setEntry("HEIGHT_OF_METEO_VALUES", HEIGHT_OF_METEO_VALUES );
       Constants.setEntry("NEUTRAL", NEUTRAL);
       Constants.setEntry("ROUGHNESS_LENGTH", ROUGHNESS_LENGTH);
       //Schirmer
       Constants.setEntry("SNOW_REDISTRIBUTION", SNOW_RED);

       Constants.setEntry("CALCULATION_STEP_LENGTH",CALCULATION_STEP_LENGTH );
       Constants.setEntry("CHANGE_BC", CHANGE_BC);
       Constants.setEntry("THRESH_CHANGE_BC", THRESH_CHANGE_BC);
       Constants.setEntry("SNP_SOIL", SNP_SOIL);
       Constants.setEntry("SOIL_FLUX", SOIL_FLUX);
       Constants.setEntry("GEO_HEAT", GEO_HEAT);
       Constants.setEntry("SOIL_ALBEDO", SOIL_ALBEDO);
       Constants.setEntry("BARE_SOIL_z0", BARE_SOIL_z0);
       Constants.setEntry("DEPTH_1", DEPTH_1);
       Constants.setEntry("DEPTH_2", DEPTH_2);
       Constants.setEntry("DEPTH_3", DEPTH_3);
       Constants.setEntry("DEPTH_4", DEPTH_4);
       Constants.setEntry("DEPTH_5", DEPTH_5);
       Constants.setEntry("TS_WRITE",TS_WRITE );
       Constants.setEntry("TS_START", TS_START);
       Constants.setEntry("TS_DAYS_BETWEEN", TS_DAYS_BETWEEN);
       Constants.setEntry("PROF_WRITE", PROF_WRITE);
       Constants.setEntry("PROF_START", PROF_START);
       Constants.setEntry("PROF_DAYS_BETWEEN", PROF_DAYS_BETWEEN);
       Constants.setEntry("CANOPY", CANOPY);
       Constants.setEntry("PROF_EVAL", PROF_EVAL);
       Constants.setEntry("PROF_FILE", PROF_FILE);
       Constants.setEntry("OUTPATH", OUTPATH);
       Constants.setEntry("RESEARCH_STATION", RESEARCH_STATION);
       Constants.setEntry("EXPERIMENT", EXPERIMENT);
       Constants.setEntry("MODEL_DIRECTORY", MODEL_DIRECTORY);


       Constants.writeWithBlanks();

     }
     catch (IOException e1)
     {
       // File CONSTANTS_User.INI should exist at this point, was already created at the
       // Start of ModelDialog if it did not exist before.
       // In theory, the following code should never be executed.
       jLabel_Error.setText("Error: File CONSTANTS_User.INI not found!");
       return false;
     }
     return true;

   }







  boolean saveData()
  // Store current values in CONSTANTS_User.INI
  {
    // Get the current values of the SNOWPACK parameters
    grabData();

    // Check the data for errors.
    // If errors exist, they are described in jLabel_Error and can be corrected.
    if (!checkDataForSave()) return false;

    try {
      // Store the data in CONSTANTS_User.INI
      IniFile Constants = new IniFile(Setup.m_IniFilePath + "CONSTANTS_User.INI");
      Constants.clear(); // clear contents of previous values
      Constants.setSection("Parameters");
      Constants.setEntry("SNOWFILE", SNOWFILE);
      Constants.setEntry("METEOFILE", METEOFILE);
      Constants.setEntry("METEO_STEP_LENGTH", METEO_STEP_LENGTH);
      Constants.setEntry("MEAS_TSS", MEAS_TSS);
      Constants.setEntry("ENFORCE_MEASURED_SNOW_HEIGHTS", ENFORCE_MEASURED_SNOW_HEIGHTS);
      Constants.setEntry("SW_REF", SW_REF);
      Constants.setEntry("INCOMING_LONGWAVE", INCOMING_LONGWAVE);
      Constants.setEntry("T_INTERNAL", T_INTERNAL);
      Constants.setEntry("HEIGHT_OF_WIND_VALUE", HEIGHT_OF_WIND_VALUE);
      Constants.setEntry("HEIGHT_OF_METEO_VALUES", HEIGHT_OF_METEO_VALUES );
      Constants.setEntry("NEUTRAL", NEUTRAL);
      Constants.setEntry("ROUGHNESS_LENGTH", ROUGHNESS_LENGTH);
      //Schirmer
      Constants.setEntry("SNOW_REDISTRIBUTION", SNOW_RED);

      Constants.setEntry("CALCULATION_STEP_LENGTH",CALCULATION_STEP_LENGTH );
      Constants.setEntry("CHANGE_BC", CHANGE_BC);
      Constants.setEntry("THRESH_CHANGE_BC", THRESH_CHANGE_BC);
      Constants.setEntry("SNP_SOIL", SNP_SOIL);
      Constants.setEntry("SOIL_FLUX", SOIL_FLUX);
      Constants.setEntry("GEO_HEAT", GEO_HEAT);
      Constants.setEntry("SOIL_ALBEDO", SOIL_ALBEDO);
      Constants.setEntry("BARE_SOIL_z0", BARE_SOIL_z0);
      Constants.setEntry("DEPTH_1", DEPTH_1);
      Constants.setEntry("DEPTH_2", DEPTH_2);
      Constants.setEntry("DEPTH_3", DEPTH_3);
      Constants.setEntry("DEPTH_4", DEPTH_4);
      Constants.setEntry("DEPTH_5", DEPTH_5);
      Constants.setEntry("TS_WRITE",TS_WRITE );
      Constants.setEntry("TS_START", TS_START);
      Constants.setEntry("TS_DAYS_BETWEEN", TS_DAYS_BETWEEN);
      Constants.setEntry("PROF_WRITE", PROF_WRITE);
      Constants.setEntry("PROF_START", PROF_START);
      Constants.setEntry("PROF_DAYS_BETWEEN", PROF_DAYS_BETWEEN);
      Constants.setEntry("CANOPY", CANOPY);
      Constants.setEntry("PROF_EVAL", PROF_EVAL);
      Constants.setEntry("PROF_FILE", PROF_FILE);
      Constants.setEntry("OUTPATH", OUTPATH);
      Constants.setEntry("RESEARCH_STATION", RESEARCH_STATION);
      Constants.setEntry("EXPERIMENT", EXPERIMENT);
      Constants.setEntry("MODEL_DIRECTORY", MODEL_DIRECTORY);


      Constants.writeWithBlanks();

    }
    catch (IOException e1)
    {
      // File CONSTANTS_User.INI should exist at this point, was already created at the
      // Start of ModelDialog if it did not exist before.
      // In theory, the following code should never be executed.
      jLabel_Error.setText("Error: File CONSTANTS_User.INI not found!");
      return false;
    }
    return true;

  }


  void jButton_TS_WRITE_actionPerformed(ActionEvent e)
  {
    if (TS_WRITE.equals("1"))
    { jButton_TS_WRITE.setText("OFF");
      TS_WRITE="0";
      jText_TS_START.setEnabled(false);
      jText_TS_DAYS_BETWEEN.setEnabled(false);
    }
    else
    { jButton_TS_WRITE.setText("ON");
      TS_WRITE="1";
      jText_TS_START.setEnabled(true);
      jText_TS_DAYS_BETWEEN.setEnabled(true);
    }
  }

  void jButton_PROF_WRITE_actionPerformed(ActionEvent e)
  {
    if (PROF_WRITE.equals("1"))
    { jButton_PROF_WRITE.setText("OFF");
      PROF_WRITE="0";
      jText_PROF_START.setEnabled(false);
      jText_PROF_DAYS_BETWEEN.setEnabled(false);
    }
    else
    { jButton_PROF_WRITE.setText("ON");
      PROF_WRITE="1";
      jText_PROF_START.setEnabled(true);
      jText_PROF_DAYS_BETWEEN.setEnabled(true);
    }
  }

  void jButton_SNP_SOIL_actionPerformed(ActionEvent e)
  {
    if (SNP_SOIL.equals("1"))
    { jButton_SNP_SOIL.setText("OFF");
      SNP_SOIL="0";
      jComboBox_SOIL_FLUX.setEnabled(false);
      jText_GEO_HEAT.setEnabled(false);
      jText_SOIL_ALBEDO.setEnabled(false);
      jText_BARE_SOIL_z0.setEnabled(false);
       }
    else
    { jButton_SNP_SOIL.setText("ON");
      SNP_SOIL="1";
      jComboBox_SOIL_FLUX.setEnabled(true);
      jText_GEO_HEAT.setEnabled(true);
      //jText_SOIL_ALBEDO.setEnabled(true); // Currently always disabled
      jText_BARE_SOIL_z0.setEnabled(true);
    }
  }


  void jButton_Run_actionPerformed(ActionEvent e) {
      // Starts the SNOWPACK model
      //was never working so changed into: Save for Run Button
     // which stores the data in CONSTANTS_User.INI (Schirmer)


      if (saveData()) {
          this.jLabel_Error.setText("Saved parameter values in CONSTANTS_User.INI");
      }



      //was never working (Schirmer)
          /*
            if (saveData()) // Store current values in CONSTANTS_User.INI
            {
                 let_run = true;
                 dispose();
               }*/
      }

  void jButton_Save_actionPerformed(ActionEvent e){
      //Saves the settings in CONSTANTS_User.INI

      jfc.setCurrentDirectory(new File(Setup.m_IniFilePath));


      if (JFileChooser.APPROVE_OPTION == jfc.showSaveDialog(this)) {
          if (saveData(jfc.getSelectedFile())) {
              this.jLabel_Error.setText(
                      "Saved parameter values in " + jfc.getSelectedFile().toString());
          }

      }


  }



  void jButton_Restore_actionPerformed(ActionEvent e)
  {
      /* Schirmer
    // Remove previous error messages
    jLabel_Error.setText("");

    try
    {
      // Copy CONSTANTS.INI to CONSTANTS_User.INI
      IniFile Constants_Default = new IniFile(Setup.m_IniFilePath + "CONSTANTS.INI");

      File Constants = new File(Setup.m_IniFilePath + "CONSTANTS_User.INI");
      Constants.delete(); // delete existing CONSTANTS_User.INI

      Constants_Default.setFileName(Setup.m_IniFilePath + "CONSTANTS_User.INI");
      Constants_Default.writeWithBlanks();

      try
      {
        // Read data from changed file
        if (!readParameters(userInputFile))
        {
          // Input file not found --> ModelDialog not displayed
          this.setVisible(false);
          this.dispose();
          reading_ok = false;
          return;
        }
        else
        {
          reading_ok = true;
        }

        // Check the data of the default file
        checkData();

        // Adjust jTextFields etc.
        refresh();

      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }

    }
    catch (IOException e1)
    {
      jLabel_Error.setText("Error: File " +
                      Setup.m_IniFilePath + "CONSTANTS.INI not found!");
      return;
    }*/

        //Schirmer
        //read only the parameters in CONSTANTS.INI to the Dialog
  // Remove previous error messages
  jLabel_Error.setText("");

  try
  {
    // Copy CONSTANTS.INI to CONSTANTS_User.INI
    IniFile Constants_Default = new IniFile(Setup.m_IniFilePath + "CONSTANTS.INI");

    //File Constants = new File(Setup.m_IniFilePath + "CONSTANTS_User.INI");
    //Constants.delete(); // delete existing CONSTANTS_User.INI

    //Constants_Default.setFileName(Setup.m_IniFilePath + "CONSTANTS_User.INI");
    Constants_Default.writeWithBlanks();

    try
    {
      // Read data from changed file
      if (!readParameters(userInputFile))
      {
        // Input file not found --> ModelDialog not displayed
        this.setVisible(false);
        this.dispose();
        reading_ok = false;
        return;
      }
      else
      {
        reading_ok = true;
      }

      // Check the data of the default file
      checkData();

      // Adjust jTextFields etc.
      refresh();

    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }

  }
  catch (IOException e1)
  {
    jLabel_Error.setText("Error: File " +
                    Setup.m_IniFilePath + "CONSTANTS.INI not found!");
    return;
  }


  }

  void jButton_Cancel_actionPerformed(ActionEvent e)
  // Exit ModelDialog without further actions
  {
    dispose();
  }

  void jRadioButton_MEAS_TSS_actionPerformed(ActionEvent e)
  {
    if (MEAS_TSS.equals("0"))
      MEAS_TSS = "1";
    else
      MEAS_TSS = "0";
  }

  void jRadioButton_ENFORCE_MEASURED_SNOW_HEIGHTS_actionPerformed(ActionEvent e)
  {
    if (ENFORCE_MEASURED_SNOW_HEIGHTS.equals("0"))
      ENFORCE_MEASURED_SNOW_HEIGHTS = "1";
    else
      ENFORCE_MEASURED_SNOW_HEIGHTS = "0";
  }

  void jRadioButton_SW_REF_actionPerformed(ActionEvent e)
  {
    if (SW_REF.equals("0"))
      SW_REF = "1";
    else
      SW_REF = "0";
  }

  void jRadioButton_INCOMING_LONGWAVE_actionPerformed(ActionEvent e)
  {
    if (INCOMING_LONGWAVE.equals("0"))
      INCOMING_LONGWAVE = "1";
    else
      INCOMING_LONGWAVE = "0";
  }
   void jButton_PROF_EVAL_actionPerformed(ActionEvent e)
  {
  if (PROF_EVAL.equals("1"))
    { jButton_PROF_EVAL.setText("OFF");
      PROF_EVAL="0";
      jText_PROF_FILE.setEnabled(false);
       }
    else
    { jButton_PROF_EVAL.setText("ON");
      PROF_EVAL="1";
      jText_PROF_FILE.setEnabled(true);
    }
  }

  void jButton_CANOPY_actionPerformed(ActionEvent e)
  {
 if (CANOPY.equals("1"))
    { jButton_CANOPY.setText("OFF");
      CANOPY="0";
       }
    else
    { jButton_CANOPY.setText("ON");
      CANOPY="1";
      }
  }

  //Schirmer
  void jButton_SNOW_RED_actionPerformed(ActionEvent e) {
      if (this.SNOW_RED.equals("1")) {
          jButton_SNOW_RED.setText("OFF");
          SNOW_RED = "0";
      }
      else {
          jButton_SNOW_RED.setText("ON");
          SNOW_RED = "1";
      }
  }

}












