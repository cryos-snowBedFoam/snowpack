///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualisierung
//Version:
//Copyright:    Copyright (c) 1999
//Autor:       Spreitzhofer
//Organisation:      SLF
//Beschreibung:  Java-Version von SnowPack.
//Integriert die C++-Version von M. Steiniger und die IDL-Version von M. Lehning.
///////////////////////////////////////////////////////////////////////////////
// SnowInputDialog: Input of soil and snow layer data used to start SNOWPACK
///////////////////////////////////////////////////////////////////////////////
//Merged the two Versions of Spreitzhofer and Gustafsson by Schirmer 06.06
//did some changes signed with "Schirmer"
package ProWin;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;

public class SnowInputDialog extends JDialog
{

    /*
        Basic output variables:
        FILENAME
        SNOWDEPTH
        NSNOW
        NSOIL
        YEAR0
        MONTH0
        DAY0
        HOUR0
        MINUTE0
        STATIONNAME
        LON
        LAT
        SLOPE
        AZI
        ALT
        CANOPY_HEIGHT
        CANOPY_LAI
        CANOPY_DIRECT_THROUGHFALL
        BARESOIL_z0
        SOILALBEDO

        Layer output variables:
        DEPTH
        TEMPERATURE
        VOL_ICE
        VOL_WATER
        VOL_SOIL
        VOL_VOID
        SOIL_RHO
        SOIL_K
        SOIL_C
        YEAR
        MONTH
        DAY
        HOUR
        MINUTE
        GRAINRADIUS
        SPHERICITY
        DENDRICITY
        BONDRADIUS
        GRAINMARKER
        HOAR
        N_ELEMENTS

     */
    MenuFrame mFrame;

    String FILENAME = null;
    String SNOWDEPTH = null;
    String STATIONNAME = null;
    String LON = null;
    String LAT = null;
    String SLOPE = null;
    String AZI = null;
    String ALT = null;
    String BARESOIL_z0 = null;
    String SOILALBEDO = null;

    String NSNOW = null;
    String NSOIL = null;
    int NSOILorig;  //Schirmer: handle negative NSOIL
    String YEAR0 = null;
    String MONTH0 = null;
    String DAY0 = null;
    String HOUR0 = null;
    String MINUTE0 = null;
    String CANOPY_HEIGHT = null;
    String CANOPY_LAI = null;
    String CANOPY_DIRECT_THROUGHFALL = null;


    Vector DEPTH = null;
    Vector TEMPERATURE = null;
    Vector VOL_ICE = null;
    Vector VOL_WATER = null;
    Vector VOL_SOIL = null;
    Vector VOL_VOID = null;
    Vector SOIL_RHO = null;
    Vector SOIL_K = null;
    Vector SOIL_C = null;
    Vector YEAR = null;
    Vector MONTH = null;
    Vector DAY = null;
    Vector HOUR = null;
    Vector MINUTE = null;
    Vector GRAINRADIUS = null;
    Vector SPHERICITY = null;
    Vector DENDRICITY = null;
    Vector BONDRADIUS = null;
    Vector GRAINMARKER = null;
    Vector HOAR = null;
    Vector N_ELEMENTS = null;

    Vector edited = null;
    boolean basicInputFinished = false;

    // Number of snow, soil and total layers
    int nSNOW, nSOIL, nTOTAL;
    int nSNOW_prev, nSOIL_prev, nTOTAL_prev; // previous values

    // Index of currently active layer (displayed in lower part of dialog window).
    // Runs over soil and snow layers;  1, 2, .... nSOIL, .... nTOTAL.
    int actLayerIndex;

    JPanel panel1 = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    JPanel jPanel1 = new JPanel();
    XYLayout xYLayout2 = new XYLayout();
    JLabel jLabel1 = new JLabel();
    JTextField jText_FILENAME = new JTextField();
    JPanel jPanel3 = new JPanel();
    XYLayout xYLayout4 = new XYLayout();
    JLabel jLabel2 = new JLabel();
    JTextField jText_YEAR0 = new JTextField();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JPanel jPanel4 = new JPanel();
    XYLayout xYLayout5 = new XYLayout();
    JLabel jLabel5 = new JLabel();
    JTextField jText_SNOWDEPTH = new JTextField();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JTextField jText_SLOPE = new JTextField();
    JTextField jText_AZI = new JTextField();
    JTextField jText_ALT = new JTextField();
    JTextField jText_LON = new JTextField();
    JTextField jText_LAT = new JTextField();
    JTextField jText_STATIONNAME = new JTextField();
    JLabel jLabel8 = new JLabel();
    JPanel jPanel7 = new JPanel();
    XYLayout xYLayout8 = new XYLayout();
    JPanel jPanel9 = new JPanel();
    XYLayout xYLayout10 = new XYLayout();
    JButton jButton_CreateFile = new JButton();
    JButton jButton_Cancel = new JButton();
    JLabel jLabel_Error = new JLabel();
    JLabel jLabel10 = new JLabel();
    JLabel jLabel11 = new JLabel();
    JTextField jText_NSNOW = new JTextField();
    JTextField jText_NSOIL = new JTextField();
    JLabel jLabel12 = new JLabel();
    JLabel jLabel13 = new JLabel();
    JLabel jLabel14 = new JLabel();
    JTextField jText_MONTH0 = new JTextField();
    JTextField jText_HOUR0 = new JTextField();
    JTextField jText_MINUTE0 = new JTextField();
    JTextField jText_DAY0 = new JTextField();
    JLabel jLabel15 = new JLabel();
    JLabel jLabel16 = new JLabel();
    JLabel jLabel17 = new JLabel();
    JButton jButton_Continue = new JButton();
    JPanel jPanel10 = new JPanel();
    XYLayout xYLayout11 = new XYLayout();
    JPanel jPanel11 = new JPanel();
    XYLayout xYLayout12 = new XYLayout();
    JLabel jLabel18 = new JLabel();
    JLabel jLabel19 = new JLabel();
    JLabel jLabel20 = new JLabel();
    JLabel jLabel21 = new JLabel();
    JPanel jPanel12 = new JPanel();
    XYLayout xYLayout13 = new XYLayout();
    JPanel jPanel14 = new JPanel();
    XYLayout xYLayout15 = new XYLayout();
    JPanel jPanel15 = new JPanel();
    XYLayout xYLayout16 = new XYLayout();
    JPanel jPanel13 = new JPanel();
    XYLayout xYLayout14 = new XYLayout();
    JTextField jText_YEAR = new JTextField();
    JTextField jText_MONTH = new JTextField();
    JTextField jText_DAY = new JTextField();
    JTextField jText_HOUR = new JTextField();
    JTextField jText_MINUTE = new JTextField();
    JLabel jLabel22 = new JLabel();
    JLabel jLabel23 = new JLabel();
    JLabel jLabel24 = new JLabel();
    JLabel jLabel25 = new JLabel();
    JLabel jLabel26 = new JLabel();
    JLabel jLabel27 = new JLabel();
    JLabel jLabel28 = new JLabel();
    JLabel jLabel29 = new JLabel();
    JLabel jLabel30 = new JLabel();
    JLabel jLabel31 = new JLabel();
    JLabel jLabel32 = new JLabel();
    JLabel jLabel33 = new JLabel();
    JLabel jLabel34 = new JLabel();
    JPanel jPanel16 = new JPanel();
    XYLayout xYLayout17 = new XYLayout();
    JLabel jLabel35 = new JLabel();
    JLabel jLabel36 = new JLabel();
    JLabel jLabel37 = new JLabel();
    JLabel jLabel38 = new JLabel();
    JLabel jLabel39 = new JLabel();
    JTextField jText_VOL_ICE = new JTextField();
    JTextField jText_VOL_WATER = new JTextField();
    JTextField jText_VOL_VOID = new JTextField();
    JTextField jText_VOL_SOIL = new JTextField();
    JPanel jPanel17 = new JPanel();
    XYLayout xYLayout18 = new XYLayout();
    JLabel jLabel40 = new JLabel();
    JLabel jLabel41 = new JLabel();
    JLabel jLabel42 = new JLabel();
    JLabel jLabel43 = new JLabel();
    JTextField jText_SOIL_RHO = new JTextField();
    JTextField jText_SOIL_K = new JTextField();
    JTextField jText_SOIL_C = new JTextField();
    JLabel jLabel44 = new JLabel();
    JLabel jLabel45 = new JLabel();
    JLabel jLabel46 = new JLabel();
    JPanel jPanel18 = new JPanel();
    XYLayout xYLayout19 = new XYLayout();
    JLabel jLabel47 = new JLabel();
    JLabel jLabel48 = new JLabel();
    JTextField jText_DEPTH = new JTextField();
    JTextField jText_TEMPERATURE = new JTextField();
    JLabel jLabel49 = new JLabel();
    JLabel jLabel50 = new JLabel();
    JPanel jPanel20 = new JPanel();
    XYLayout xYLayout21 = new XYLayout();
    JLabel jLabel52 = new JLabel();
    JLabel jLabel53 = new JLabel();
    JLabel jLabel54 = new JLabel();
    JLabel jLabel55 = new JLabel();
    JLabel jLabel56 = new JLabel();
    JLabel jLabel57 = new JLabel();
    JTextField jText_GRAINRADIUS = new JTextField();
    JTextField jText_SPHERICITY = new JTextField();
    JTextField jText_DENDRICITY = new JTextField();
    JTextField jText_BONDRADIUS = new JTextField();
    JTextField jText_GRAINMARKER = new JTextField();
    JTextField jText_HOAR = new JTextField();
    JTextField jText_N_ELEMENTS = new JTextField();
    JLabel jLabel58 = new JLabel();
    JLabel jLabel60 = new JLabel();
    JButton jButton_PrevLayer = new JButton();
    JButton jButton_NextLayer = new JButton();
    JLabel jLabel51 = new JLabel();
    JLabel jLabel61 = new JLabel();
    JLabel jLabel62 = new JLabel();
    JLabel jLabel63 = new JLabel();
    JLabel jLabel64 = new JLabel();
    JLabel jLabel65 = new JLabel();
    JLabel jLabel_activeLayer = new JLabel();
    JLabel jLabel67 = new JLabel();
    JPanel jPanel8 = new JPanel();
    XYLayout xYLayout9 = new XYLayout();
    JLabel jLabel9 = new JLabel();

    JLabel jLabel66 = new JLabel();
    JLabel jLabel59 = new JLabel();
    JLabel jLabel68 = new JLabel();
    JLabel jLabel69 = new JLabel();
    JLabel jLabel70 = new JLabel();
    JLabel jLabel71 = new JLabel();
    JLabel jLabel72 = new JLabel();
    JLabel jLabel73 = new JLabel();
    JLabel jLabel74 = new JLabel();
    JPanel jPanel5 = new JPanel();
    XYLayout xYLayout6 = new XYLayout();
    JLabel jLabel75 = new JLabel();
    JLabel jLabel76 = new JLabel();
    JLabel jLabel77 = new JLabel();
    JLabel jLabel78 = new JLabel();
    JLabel jLabel610 = new JLabel();
    JLabel jLabel611 = new JLabel();
    JLabel jLabel612 = new JLabel();
    JTextField jText_CANOPY_HEIGHT = new JTextField();
    JTextField jText_CANOPY_LAI = new JTextField();
    JTextField jText_CANOPY_DIRECT_THROUGHFALL = new JTextField();
    JButton jButton_ReeditBasicData = new JButton();
    JLabel jLabel79 = new JLabel();
    JLabel jLabel80 = new JLabel();
    JTextField jText_BARESOIL = new JTextField();
    JTextField jText_SOILALBEDO = new JTextField();
    JLabel jLabel81 = new JLabel();
    JLabel jLabel710 = new JLabel();
    JLabel jLabel711 = new JLabel();
    JLabel jLabel82 = new JLabel();


    public SnowInputDialog(MenuFrame mFrame, String title, String fileName,
                           boolean modal)
    // fileName ... Name of the file from which the default data should be read;
    //              blank if a new file should be created
    {
        super(mFrame, title, modal);
        this.mFrame = mFrame;
        this.FILENAME = fileName;
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    void jbInit() throws Exception {
        panel1.setLayout(xYLayout1);
        jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel1.setLayout(xYLayout2);
        jLabel1.setToolTipText(
                "Path to write the output file (abs. path or path rel. to SN_GUI-Dir.)");
        jLabel1.setText("File Name:");
        jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel3.setLayout(xYLayout4);
        jLabel2.setToolTipText("");
        jLabel2.setText("Date of Profile:");
        jLabel3.setText("-");
        jLabel4.setText("-");
        jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel4.setLayout(xYLayout5);
        jLabel5.setToolTipText("Approximate snow depth measured");
        jLabel5.setText("Snow Depth:");
        jLabel6.setText("cm");
        jLabel7.setText("Slope Angle:");
        jLabel8.setText("deg");
        jPanel7.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel7.setLayout(xYLayout8);
        jPanel9.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel9.setLayout(xYLayout10);
        jButton_CreateFile.setToolTipText(
                "Create file containing basic and layer data");
        jButton_CreateFile.setText("Create File");
        jButton_CreateFile.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton_CreateFile_actionPerformed(e);
            }
        });
        jButton_Cancel.setToolTipText(
                "Exit this dialog without further actions");
        jButton_Cancel.setText("Cancel");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton_Cancel_actionPerformed(e);
            }
        });
        jLabel10.setText("Number of Snow Layers:");
        jLabel11.setText("Number  of Soil Layers:");
        jLabel12.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel12.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel12.setText("Year");
        jLabel13.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel13.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel13.setText("Month");
        jLabel14.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel14.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel14.setText("Day");
        jLabel15.setText(":");
        jLabel16.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel16.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel16.setText("Hour");
        jLabel17.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel17.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel17.setText("Minute");
        jButton_Continue.setToolTipText(
                "Go to layer data input and save current basic data");
        jButton_Continue.setText("Continue");
        jButton_Continue.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton_Continue_actionPerformed(e);
            }
        });
        jPanel10.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel10.setLayout(xYLayout11);
        jPanel11.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel11.setLayout(xYLayout12);
        jLabel18.setText("Input of");
        jLabel19.setText("BASIC DATA");
        jLabel20.setText("Step 1:");
        jLabel21.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel21.setText("Add values,");
        jPanel12.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel12.setLayout(xYLayout13);
        jPanel14.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel14.setLayout(xYLayout15);
        xYLayout1.setHeight(650);
        xYLayout1.setWidth(724);
        jPanel15.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel15.setLayout(xYLayout16);
        jPanel13.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel13.setLayout(xYLayout14);
        jText_HOUR.setHorizontalAlignment(SwingConstants.CENTER);
        jText_MINUTE.setHorizontalAlignment(SwingConstants.CENTER);
        jText_YEAR0.setHorizontalAlignment(SwingConstants.CENTER);
        jText_MONTH0.setHorizontalAlignment(SwingConstants.CENTER);
        jText_DAY0.setHorizontalAlignment(SwingConstants.CENTER);
        jText_HOUR0.setHorizontalAlignment(SwingConstants.CENTER);
        jText_MINUTE0.setHorizontalAlignment(SwingConstants.CENTER);
        jText_SNOWDEPTH.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_LON.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_LAT.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_SLOPE.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_AZI.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_ALT.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_BARESOIL.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_SOILALBEDO.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel22.setText("Month");
        jLabel23.setText("Hour");
        jLabel24.setText(":");
        jLabel25.setText("-");
        jLabel26.setText("-");
        jLabel27.setText("Minute");
        jLabel28.setText("Year");
        jLabel29.setText("Day");
        jLabel30.setToolTipText(
                "Date when layer was deposited during snowfall event");
        jLabel30.setText("Date of Layer Formation:");
        jLabel31.setText("Step 2:");
        jLabel32.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel32.setText("Add values");
        jLabel33.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel33.setText("layer by layer,");
        jLabel34.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel34.setText("\"Create File\".");
        jPanel16.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel16.setLayout(xYLayout17);
        jLabel35.setText("Volumetric Fractions (Total = 100%):");
        jLabel36.setText("Ice");
        jLabel37.setText("Water");
        jLabel38.setText("Void");
        jLabel39.setText("Soil");
        jPanel17.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel17.setLayout(xYLayout18);
        jLabel40.setText("Soil Properties:");
        jLabel41.setText("Density:");
        jLabel42.setText("Conductivity:");
        jLabel43.setText("Specific Heat:");
        jLabel44.setText("kg / m3");
        jLabel45.setText("W / m K");
        jLabel46.setText("J / kg K");
        jPanel18.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel18.setLayout(xYLayout19);
        jLabel47.setText("Layer Depth:");
        jLabel48.setText("Layer Temperature:");
        jLabel49.setText("cm");
        jLabel50.setText("deg C");
        jPanel20.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel20.setLayout(xYLayout21);
        jLabel52.setText("Snow");
        jLabel53.setText("Grain Radius:");
        jLabel54.setText("Sphericity:");
        jLabel55.setText("Dendricity:");
        jLabel56.setText("Bond Radius:");
        jLabel57.setText("Grain Marker:");
        jLabel58.setText("mm");
        jLabel60.setText("mm");
        jButton_PrevLayer.setToolTipText(
                "Go to previous layer and save current layer data (= PageUp - key) ");
        jButton_PrevLayer.setText("Prev. Layer");
        jButton_PrevLayer.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton_PrevLayer_actionPerformed(e);
            }
        });
        jButton_NextLayer.setToolTipText(
                "Go to next layer and save current layer data (= PageDown - key)");
        jButton_NextLayer.setText("Next Layer");
        jButton_NextLayer.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton_NextLayer_actionPerformed(e);
            }
        });
        jLabel51.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel51.setText("then press ");
        jLabel61.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel61.setText("\"Continue\".");
        jLabel62.setText("Input of");
        jLabel63.setText("LAYER DATA");
        jLabel64.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabel64.setText("then press");
        jText_NSNOW.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_NSOIL.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_DEPTH.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_TEMPERATURE.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_VOL_ICE.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_VOL_WATER.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_VOL_VOID.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_VOL_SOIL.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_GRAINRADIUS.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_SPHERICITY.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_DENDRICITY.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_BONDRADIUS.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_GRAINMARKER.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_N_ELEMENTS.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_SOIL_RHO.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_SOIL_K.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_SOIL_C.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_YEAR.setHorizontalAlignment(SwingConstants.CENTER);
        jText_MONTH.setHorizontalAlignment(SwingConstants.CENTER);
        jText_DAY.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel_Error.setForeground(Color.red);
        jLabel_Error.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel_Error.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel_activeLayer.setBorder(BorderFactory.createRaisedBevelBorder());
        jLabel_activeLayer.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel_activeLayer.setHorizontalTextPosition(SwingConstants.CENTER);
        jPanel8.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel8.setLayout(xYLayout9);
        jLabel67.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel67.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel65.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel65.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel9.setText("Surface Hoar:");
        jLabel66.setText("kg / m2");
        jText_HOAR.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel59.setText("No. of Elements:");
        jLabel68.setText("Properties:");
        jLabel69.setText("Slope Azimuth:");
        jLabel70.setText("Altitude:");
        jLabel71.setText("Longitude:");
        jLabel72.setText("Latitude:");
        jLabel73.setText("Station Name:");
        jLabel74.setText("Station Characteristics:");
        jPanel5.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel5.setLayout(xYLayout6);
        jText_AZI.setText("jTextField1");
        jText_ALT.setText("jTextField2");
        jText_LON.setText("jTextField3");
        jText_LAT.setText("jTextField4");
        jText_STATIONNAME.setText("jTextField5");
        jLabel75.setText("deg");
        jLabel76.setText("deg");
        jLabel77.setText("m");
        jLabel78.setText("deg");
        jLabel610.setText("Canopy Height:");
        jLabel611.setText("Leaf Area Index:");
        jLabel612.setText("Direct Throughfall:");
        jText_CANOPY_HEIGHT.setText(CANOPY_HEIGHT);
        jText_CANOPY_HEIGHT.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_CANOPY_LAI.setText(CANOPY_LAI);
        jText_CANOPY_LAI.setHorizontalAlignment(SwingConstants.RIGHT);
        jText_CANOPY_DIRECT_THROUGHFALL.setText(CANOPY_DIRECT_THROUGHFALL);
        jText_CANOPY_DIRECT_THROUGHFALL.setHorizontalAlignment(SwingConstants.
                RIGHT);
        jLabel79.setText("m");
        jLabel710.setText("m2 / m2");
        jLabel711.setText("[fraction]");
        jButton_ReeditBasicData.setToolTipText("Return to input of basic data");
        jButton_ReeditBasicData.setMargin(new Insets(2, 2, 2, 2));
        jButton_ReeditBasicData.setText("Edit Basic Data");
        jButton_ReeditBasicData.addActionListener(new java.awt.event.
                                                  ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_ReeditBasicData_actionPerformed(e);
            }
        });
        jLabel79.setText("Bare Soil z0:");
        jLabel80.setText("Soil Albedo:");
        jText_BARESOIL.setText("");
        jLabel81.setText("m");
        jLabel82.setText("m");
        getContentPane().add(panel1);
        panel1.add(jPanel11, new XYConstraints(3, 3, 719, 289));
        jPanel11.add(jPanel10, new XYConstraints(2, 2, 126, 281));
        jPanel10.add(jLabel20, new XYConstraints(39, 20, -1, -1));
        jPanel10.add(jLabel19, new XYConstraints(25, 67, -1, -1));
        jPanel10.add(jLabel18, new XYConstraints(40, 50, -1, -1));
        jPanel10.add(jLabel51, new XYConstraints(33, 163, -1, -1));
        jPanel10.add(jLabel21, new XYConstraints(30, 147, -1, -1));
        jPanel10.add(jLabel61, new XYConstraints(32, 180, -1, -1));
        jPanel11.add(jPanel7, new XYConstraints(134, 3, 578, 281));
        jPanel1.add(jText_FILENAME, new XYConstraints(75, 3, 185, -1));
        jPanel1.add(jText_STATIONNAME, new XYConstraints(75, 32, 185, -1));
        jPanel1.add(jLabel1, new XYConstraints(8, 6, -1, -1));
        jPanel1.add(jLabel73, new XYConstraints(8, 34, -1, -1));
        jPanel7.add(jPanel3, new XYConstraints(1, 71, 275, 74));
        jPanel3.add(jText_MONTH0, new XYConstraints(56, 43, 37, 21));
        jPanel3.add(jLabel14, new XYConstraints(106, 26, 37, -1));
        jPanel3.add(jLabel13, new XYConstraints(55, 26, 39, -1));
        jPanel3.add(jText_DAY0, new XYConstraints(106, 43, 37, 21));
        jPanel3.add(jText_HOUR0, new XYConstraints(164, 43, 37, 21));
        jPanel3.add(jText_MINUTE0, new XYConstraints(218, 43, 37, 21));
        jPanel3.add(jLabel16, new XYConstraints(165, 26, 38, -1));
        jPanel3.add(jLabel17, new XYConstraints(215, 26, 43, -1));
        jPanel3.add(jLabel2, new XYConstraints(12, 6, -1, -1));
        jPanel3.add(jLabel12, new XYConstraints(6, 26, 37, -1));
        jPanel3.add(jText_YEAR0, new XYConstraints(6, 43, 37, -1));
        jPanel3.add(jLabel3, new XYConstraints(45, 45, 8, -1));
        jPanel3.add(jLabel4, new XYConstraints(97, 46, 7, -1));
        jPanel3.add(jLabel15, new XYConstraints(207, 46, 7, -1));
        jPanel4.add(jLabel5, new XYConstraints(9, 4, -1, -1));
        jPanel4.add(jLabel10, new XYConstraints(9, 28, -1, -1));
        jPanel4.add(jText_NSNOW, new XYConstraints(154, 27, 49, 21));
        jPanel4.add(jLabel11, new XYConstraints(9, 53, -1, -1));
        jPanel4.add(jText_NSOIL, new XYConstraints(154, 52, 49, -1));
        jPanel4.add(jText_SNOWDEPTH, new XYConstraints(154, 2, 49, -1));
        jPanel4.add(jLabel6, new XYConstraints(210, 7, -1, -1));
        jPanel7.add(jButton_Continue, new XYConstraints(110, 242, -1, -1));
        jPanel7.add(jPanel4, new XYConstraints(1, 151, 275, 81));
        panel1.add(jPanel9, new XYConstraints(2, 621, 719, 26));
        jPanel9.add(jLabel_Error, new XYConstraints(10, 2, 706, 21));
        panel1.add(jPanel12, new XYConstraints(4, 295, 718, 324));
        jPanel12.add(jPanel15, new XYConstraints(132, 2, 578, 316));
        jPanel15.add(jPanel18, new XYConstraints(2, 2, 275, 79));
        jPanel18.add(jLabel47, new XYConstraints(8, 5, -1, -1));
        jPanel18.add(jLabel49, new XYConstraints(219, 6, -1, -1));
        jPanel18.add(jText_DEPTH, new XYConstraints(176, 5, 36, -1));
        jPanel18.add(jLabel48, new XYConstraints(7, 52, -1, -1));
        jPanel18.add(jText_TEMPERATURE, new XYConstraints(176, 50, 36, 21));
        jPanel18.add(jLabel50, new XYConstraints(217, 53, -1, -1));
        jPanel18.add(jText_N_ELEMENTS, new XYConstraints(176, 27, 36, 21));
        jPanel18.add(jLabel59, new XYConstraints(7, 29, -1, -1));
        jPanel15.add(jPanel16, new XYConstraints(285, 3, 285, 79));
        jPanel16.add(jLabel35, new XYConstraints(6, 7, -1, -1));
        jPanel16.add(jText_VOL_VOID, new XYConstraints(222, 28, 37, 21));
        jPanel16.add(jText_VOL_SOIL, new XYConstraints(222, 51, 37, 21));
        jPanel16.add(jText_VOL_WATER, new XYConstraints(121, 52, 37, 21));
        jPanel16.add(jText_VOL_ICE, new XYConstraints(121, 28, 37, -1));
        jPanel16.add(jLabel37, new XYConstraints(80, 51, -1, -1));
        jPanel16.add(jLabel36, new XYConstraints(79, 29, -1, -1));
        jPanel16.add(jLabel39, new XYConstraints(190, 52, -1, -1));
        jPanel16.add(jLabel38, new XYConstraints(190, 32, -1, -1));
        jPanel15.add(jPanel13, new XYConstraints(3, 89, 274, 93));
        jPanel13.add(jText_YEAR, new XYConstraints(10, 60, 38, -1));
        jPanel13.add(jText_MONTH, new XYConstraints(61, 61, 38, 21));
        jPanel13.add(jText_DAY, new XYConstraints(109, 61, 38, 21));
        jPanel13.add(jLabel25, new XYConstraints(50, 62, 8, -1));
        jPanel13.add(jLabel26, new XYConstraints(101, 62, -1, -1));
        jPanel13.add(jText_HOUR, new XYConstraints(168, 61, 38, 21));
        jPanel13.add(jLabel24, new XYConstraints(208, 63, -1, -1));
        jPanel13.add(jText_MINUTE, new XYConstraints(216, 61, 38, 21));
        jPanel13.add(jLabel28, new XYConstraints(10, 39, -1, -1));
        jPanel13.add(jLabel22, new XYConstraints(62, 39, -1, -1));
        jPanel13.add(jLabel29, new XYConstraints(115, 39, -1, -1));
        jPanel13.add(jLabel23, new XYConstraints(172, 39, -1, -1));
        jPanel13.add(jLabel30, new XYConstraints(7, 8, -1, -1));
        jPanel13.add(jLabel27, new XYConstraints(217, 40, -1, -1));
        jPanel15.add(jPanel17, new XYConstraints(286, 89, 283, 93));
        jPanel17.add(jLabel44, new XYConstraints(222, 11, -1, -1));
        jPanel17.add(jText_SOIL_RHO, new XYConstraints(180, 11, 36, -1));
        jPanel17.add(jText_SOIL_K, new XYConstraints(180, 35, 36, -1));
        jPanel17.add(jText_SOIL_C, new XYConstraints(180, 59, 36, -1));
        jPanel17.add(jLabel46, new XYConstraints(225, 63, -1, -1));
        jPanel17.add(jLabel45, new XYConstraints(225, 39, -1, -1));
        jPanel17.add(jLabel42, new XYConstraints(100, 37, -1, -1));
        jPanel17.add(jLabel41, new XYConstraints(101, 11, -1, -1));
        jPanel17.add(jLabel43, new XYConstraints(101, 61, -1, -1));
        jPanel17.add(jLabel40, new XYConstraints(4, 11, -1, -1));
        jPanel15.add(jPanel20, new XYConstraints(3, 188, 567, 65));
        jPanel20.add(jLabel53, new XYConstraints(109, 7, -1, 21));
        jPanel20.add(jText_GRAINRADIUS, new XYConstraints(191, 7, 36, 21));
        jPanel20.add(jLabel56, new XYConstraints(109, 34, -1, -1));
        jPanel20.add(jText_BONDRADIUS, new XYConstraints(191, 32, 36, 21));
        jPanel20.add(jText_HOAR, new XYConstraints(476, 32, 36, 21));
        jPanel20.add(jLabel66, new XYConstraints(516, 34, -1, -1));
        jPanel20.add(jText_GRAINMARKER, new XYConstraints(476, 7, 36, 21));
        jPanel20.add(jLabel58, new XYConstraints(228, 7, -1, 21));
        jPanel20.add(jLabel60, new XYConstraints(229, 34, -1, -1));
        jPanel20.add(jText_SPHERICITY, new XYConstraints(343, 7, 36, 21));
        jPanel20.add(jText_DENDRICITY, new XYConstraints(343, 32, 36, 21));
        jPanel20.add(jLabel57, new XYConstraints(398, 7, -1, 21));
        jPanel20.add(jLabel55, new XYConstraints(282, 34, -1, -1));
        jPanel20.add(jLabel54, new XYConstraints(282, 7, -1, 21));
        jPanel20.add(jLabel9, new XYConstraints(397, 34, -1, -1));
        jPanel20.add(jLabel68, new XYConstraints(3, 29, -1, -1));
        jPanel20.add(jLabel52, new XYConstraints(2, 10, -1, 21));
        jPanel15.add(jButton_PrevLayer, new XYConstraints(17, 271, -1, -1));
        jPanel15.add(jButton_NextLayer, new XYConstraints(129, 271, 91, 25));
        jPanel15.add(jButton_Cancel, new XYConstraints(465, 270, 91, 25));
        jPanel15.add(jButton_CreateFile, new XYConstraints(353, 270, 91, 25));
        jPanel15.add(jButton_ReeditBasicData,
                     new XYConstraints(241, 271, 91, 25));
        jPanel12.add(jPanel14, new XYConstraints(0, 2, 126, 317));
        jPanel14.add(jLabel31, new XYConstraints(40, 23, -1, -1));
        jPanel14.add(jLabel62, new XYConstraints(40, 53, -1, -1));
        jPanel14.add(jLabel63, new XYConstraints(25, 71, -1, -1));
        jPanel14.add(jLabel32, new XYConstraints(28, 102, -1, -1));
        jPanel14.add(jLabel33, new XYConstraints(21, 119, -1, -1));
        jPanel14.add(jLabel64, new XYConstraints(29, 136, -1, -1));
        jPanel14.add(jLabel34, new XYConstraints(23, 152, -1, -1));
        jPanel14.add(jPanel8, new XYConstraints(6, 198, 112, 86));
        jPanel8.add(jLabel65, new XYConstraints(2, 6, 103, 24));
        jPanel8.add(jLabel_activeLayer, new XYConstraints(3, 32, 103, 22));
        jPanel8.add(jLabel67, new XYConstraints(3, 58, 102, 20));
        jPanel5.add(jLabel74, new XYConstraints(70, 4, -1, -1));
        jPanel7.add(jPanel1, new XYConstraints(1, 3, 275, 61));
        jPanel5.add(jLabel72, new XYConstraints(22, 25, 78, -1));
        jPanel5.add(jText_LAT, new XYConstraints(168, 25, 48, -1));
        jPanel5.add(jLabel75, new XYConstraints(226, 25, -1, -1)); //jPanel5.add(jLabel79, new XYConstraints(22, 209, -1, -1));
        jPanel7.add(jPanel5, new XYConstraints(280, 3, 291, 272));
        jPanel5.add(jLabel71, new XYConstraints(22, 49, 78, -1));
        jPanel5.add(jText_LON, new XYConstraints(168, 49, 48, -1));
        jPanel5.add(jLabel76, new XYConstraints(226, 49, -1, -1));
        jPanel5.add(jLabel70, new XYConstraints(22, 73, 78, -1));
        jPanel5.add(jText_ALT, new XYConstraints(168, 73, 48, -1));
        jPanel5.add(jLabel77, new XYConstraints(226, 73, -1, -1));
        jPanel5.add(jLabel7, new XYConstraints(22, 97, 78, -1));
        jPanel5.add(jText_SLOPE, new XYConstraints(168, 97, 48, -1));
        jPanel5.add(jLabel8, new XYConstraints(226, 97, -1, -1));
        jPanel5.add(jLabel69, new XYConstraints(22, 121, 99, -1));
        jPanel5.add(jText_AZI, new XYConstraints(168, 121, 48, -1));
        jPanel5.add(jLabel78, new XYConstraints(226, 121, -1, -1));
        jPanel5.add(jText_SOILALBEDO, new XYConstraints(168, 145, 48, -1));
        jPanel5.add(jText_BARESOIL, new XYConstraints(168, 169, 48, -1));
        jPanel5.add(jLabel82, new XYConstraints(226, 169, -1, -1));
        jPanel5.add(jLabel81, new XYConstraints(226, 193, -1, -1));
        jPanel5.add(jLabel710, new XYConstraints(226, 217, -1, -1));
        jPanel5.add(jText_CANOPY_HEIGHT, new XYConstraints(168, 193, 48, -1));
        jPanel5.add(jText_CANOPY_LAI, new XYConstraints(168, 217, 48, -1));
        jPanel5.add(jText_CANOPY_DIRECT_THROUGHFALL,
                    new XYConstraints(168, 241, 48, -1));
        jPanel5.add(jLabel711, new XYConstraints(226, 241, -1, -1));
        jPanel5.add(jLabel80, new XYConstraints(22, 145, -1, -1));
        jPanel5.add(jLabel79, new XYConstraints(22, 169, -1, -1));
        jPanel5.add(jLabel610, new XYConstraints(22, 193, 99, -1));
        jPanel5.add(jLabel611, new XYConstraints(22, 217, -1, -1));
        jPanel5.add(jLabel612, new XYConstraints(22, 241, 131, -1));
        jLabel_activeLayer.setText("Not active yet.");

        // *** Reaction to key pressed events ***
        addKeyListener(new java.awt.event.KeyListener() {
            public void keyPressed(KeyEvent ke) {
                System.out.println("SnowInputDialog: key pressed");
                if (basicInputFinished == false)return;

                int key = ke.getKeyCode();

                if (key == KeyEvent.VK_PAGE_UP) {
                    if (actLayerIndex > 1) prevLayer();
                } else if (key == KeyEvent.VK_PAGE_DOWN) {
                    if (actLayerIndex < nTOTAL) nextLayer();
                }

                return;
            }

            public void keyTyped(KeyEvent ke) {}

            public void keyReleased(KeyEvent ke) {}
        });
        requestFocus(); // request input focus

        // Disable text edit boxes and buttons not needed when handling data of the
        // basic frame
        enableLayerFrame(false);
        jButton_CreateFile.setEnabled(false);
        jButton_NextLayer.setEnabled(false);
        jButton_PrevLayer.setEnabled(false);
        jButton_ReeditBasicData.setEnabled(false);

        if (FILENAME.equals("")) { // define default parameters through hardcoded values
            readParameters();
        } else { // read default parameters from input file
            // System.out.println("Snow File: "+FILENAME);
            if (!readSnowFile(FILENAME))return;
        }

        refresh();
        actLayerIndex = 1;
    }


    void enableBasicFrame(boolean enable)
    // Enables/disables all fields of basic frame
    {
        jText_FILENAME.setEnabled(enable);
        jText_SNOWDEPTH.setEnabled(enable);
        jText_STATIONNAME.setEnabled(enable);
        jText_LON.setEnabled(enable);
        jText_LAT.setEnabled(enable);
        jText_SLOPE.setEnabled(enable);
        jText_AZI.setEnabled(enable);
        jText_ALT.setEnabled(enable);
        jText_BARESOIL.setEnabled(enable);
        jText_SOILALBEDO.setEnabled(enable);
        jText_NSNOW.setEnabled(enable);
        jText_NSOIL.setEnabled(enable);
        jText_YEAR0.setEnabled(enable);
        jText_MONTH0.setEnabled(enable);
        jText_DAY0.setEnabled(enable);
        jText_HOUR0.setEnabled(enable);
        jText_MINUTE0.setEnabled(enable);
        jText_CANOPY_HEIGHT.setEnabled(enable);
        jText_CANOPY_LAI.setEnabled(enable);
        jText_CANOPY_DIRECT_THROUGHFALL.setEnabled(enable);
    }


    void enableLayerFrame(boolean enable)
    // Enables/disables all fields of layer frame
    {
        jText_DEPTH.setEnabled(enable);
        jText_TEMPERATURE.setEnabled(enable);
        jText_VOL_ICE.setEnabled(enable);
        jText_VOL_WATER.setEnabled(enable);
        jText_VOL_SOIL.setEnabled(enable);
        jText_VOL_VOID.setEnabled(enable);
        jText_SOIL_RHO.setEnabled(enable);
        jText_SOIL_K.setEnabled(enable);
        jText_SOIL_C.setEnabled(enable);
        jText_YEAR.setEnabled(enable);
        jText_MONTH.setEnabled(enable);
        jText_DAY.setEnabled(enable);
        jText_HOUR.setEnabled(enable);
        jText_MINUTE.setEnabled(enable);
        jText_GRAINRADIUS.setEnabled(enable);
        jText_SPHERICITY.setEnabled(enable);
        jText_DENDRICITY.setEnabled(enable);
        jText_BONDRADIUS.setEnabled(enable);
        jText_GRAINMARKER.setEnabled(enable);
        jText_HOAR.setEnabled(enable);
        jText_N_ELEMENTS.setEnabled(enable);
    }


    void enableSnowLayerFrame(boolean enable)
    // Enables/disables fields only available for snow layers
    {
        //Schirmer
        /*jText_YEAR.setEnabled(enable);
        jText_MONTH.setEnabled(enable);
        jText_DAY.setEnabled(enable);
        jText_HOUR.setEnabled(enable);
        jText_MINUTE.setEnabled(enable);
        jText_GRAINRADIUS.setEnabled(enable);*/
        jText_SPHERICITY.setEnabled(enable);
        jText_DENDRICITY.setEnabled(enable);
        jText_BONDRADIUS.setEnabled(enable);
        jText_GRAINMARKER.setEnabled(enable);
        jText_HOAR.setEnabled(enable);
    }


    void enableSoilLayerFrame(boolean enable)
    // Enables/disables fields only available for soil layers
    {

        jText_VOL_SOIL.setEnabled(enable);
        jText_SOIL_RHO.setEnabled(enable);
        jText_SOIL_K.setEnabled(enable);
        jText_SOIL_C.setEnabled(enable);

        //Schirmer
        jText_SPHERICITY.setEnabled(!enable);
        jText_DENDRICITY.setEnabled(!enable);
        jText_BONDRADIUS.setEnabled(!enable);
        jText_GRAINMARKER.setEnabled(!enable);
        jText_HOAR.setEnabled(!enable);

    }


  boolean readSnowFile(String FILENAME) throws IOException
  // Reads the contents of the snow and soil input file for SNOWPACK runs.
  // The values of some member variables of this dialog window are set.
  {

    // ======== Read basic parameters of the file in Inifile-format =========

    try{

      IniFile SnowIni = new IniFile(FILENAME);

      STATIONNAME = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "StationName", "Error");
      NSNOW = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "nSnowLayerData", "Error");
      NSOIL = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "nSoilLayerData", "Error");

      //Schirmer. handle negative NSOIL intern as 0, but write out the original value in file
      NSOILorig = Integer.parseInt(NSOIL);
      if (Integer.parseInt(NSOIL) < 0)     {
          NSOIL = "0";
      }

      LON   = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "Longitude", "Error");
      LAT   = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "Latitude", "Error");
      SLOPE = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "SlopeAngle", "Error");
      AZI   = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "SlopeAzi", "Error");
      ALT   = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "Altitude", "Error");
      BARESOIL_z0 = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "BareSoil_z0", "Error");
      SOILALBEDO= SnowIni.getEntry("SNOWPACK_INITIALIZATION", "SoilAlbedo", "Error");
      this.CANOPY_HEIGHT = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "CanopyHeight", "Error");
      this.CANOPY_LAI = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "CanopyLeafAreaIndex", "Error");
      this.CANOPY_DIRECT_THROUGHFALL = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "CanopyDirectThroughfall", "Error");


      String Date = SnowIni.getEntry("SNOWPACK_INITIALIZATION", "ProfileDate", "Error");
      if (Date.equals("Error"))
      {
        YEAR0 = "Error";
        MONTH0= "Error";
        DAY0  = "Error";
        HOUR0 = "Error";
        MINUTE0= "Error";
      }
      else
      {
        YEAR0   = Date.substring(0, 4);
        MONTH0  = Date.substring(5, 7);
        DAY0    = Date.substring(8, 10);
        HOUR0   = Date.substring(11,13);
        MINUTE0 = Date.substring(14,16);
      }

    } // end try
    catch (IOException e)
    {
      MessageBox mBox = new MessageBox(mFrame, "Error", "Error reading file: ",
        FILENAME);
      mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
      return false;
    }

    // Default number of snow/soil layers as integer values
    nSNOW = nSNOW_prev = Integer.parseInt(NSNOW);
    nSOIL = nSOIL_prev = Integer.parseInt(NSOIL);
    nTOTAL = nTOTAL_prev = nSNOW + nSOIL;

    initializeLayers(nTOTAL);


    // ====================== Read layer parameters =========================

    LineNumberReader DataFile = new LineNumberReader(new FileReader(FILENAME));

    int LineNr = 0; // number of line of the input file
    String LineBuf = null; // one line of the input file

    //int NrOfFirstLayerLine = 18; // first line containing layer data


    // search for YYYY, NrOfFirstLayerLine = line(YYYY) + 1 (Schirmer)
    int NrOfFirstLayerLine = 1000000;




    // Read file line by line
    while(true) {

    try {
      LineBuf = DataFile.readLine();
    }
    catch (Exception e)
    {
      MessageBox mBox = new MessageBox(mFrame, "Error", "Error reading lines of ",
        FILENAME);
      mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
      DataFile.close();
      return false;
    }

    if (LineBuf == null) // e.g. end of file
    {
      DataFile.close();
      return false;
    }


    // search for YYYY, NrOfFirstLayerLine = line(YYYY) + 1 (Schirmer)
    if (LineBuf.startsWith("YYYY")) {
        NrOfFirstLayerLine = DataFile.getLineNumber() + 1;
    }


    //kept this method, also it is not nice (Schirmer)
    LineNr++;

    if (LineNr < NrOfFirstLayerLine)
        continue; // still no lines with layer information reached in input file;

    // read next line


    //this must be before the next if (Schirmer)
    if (LineNr == (NrOfFirstLayerLine + nSNOW + nSOIL)) { // Last line of layer data reached, reading of more lines not necessary
        break;                                            // deleted -1 (Schirmer)
    }

    else if (!ParseLine(LineBuf, LineNr - NrOfFirstLayerLine + 1)) { // and this an else if not an if (Schirmer)
        MessageBox mBox = new MessageBox(mFrame, "Error",
                                         "Error parsing line #" + LineNr +
                                         " of",
                                         FILENAME);
        mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
        mBox.setVisible(true);
        DataFile.close();
        return false;
    }



    } // end of the line-by-line loop

    DataFile.close();

    // Mark that the elements have been "edited" before.
    for (int i = 0; i < nTOTAL; i++) edited.insertElementAt(new Boolean(true), i);

    // Calculate total snow depth.
    float sDepth = 0;
    for (int i = nSOIL; i < nTOTAL; i++)
    {
      sDepth += Float.parseFloat((String) DEPTH.elementAt(i));
    }
    SNOWDEPTH = (new Float(sDepth)).toString();

    return true;
  }


  public boolean ParseLine(String Line, int layerNr) throws IOException
  // Parse a line of the snow and soil data input file, containing layer data.
  // Format + example:
  // YYYY MM DD HH MI Layer_Thick T Vol_Frac_I Vol_Frac_W Vol_Frac_V Vol_Frac_S Rho_S Conduc_S HeatCapac_S rg rb dd sp mk mass_hoar ne
  // 2000 01 01 00 00  0.1 273.15  0.0 0.15 0.05 0.8  2400.0 3.8 900.0  0.0 0.0 0.0 0.0 0 0.0 1
  {
     int fieldIndex = layerNr - 1;
     if ( Line == null ) return false;

     StringTokenizer st = new StringTokenizer(Line, " ");

     // Read parameter values from line
     YEAR.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     MONTH.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     DAY.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     HOUR.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     MINUTE.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     DEPTH.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     TEMPERATURE.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     VOL_ICE.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     VOL_WATER.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     VOL_VOID.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     VOL_SOIL.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     SOIL_RHO.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     SOIL_K.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     SOIL_C.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     GRAINRADIUS.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     BONDRADIUS.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     DENDRICITY.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     SPHERICITY.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     GRAINMARKER.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     HOAR.insertElementAt(st.nextToken(), fieldIndex);
     if (!st.hasMoreTokens()) return false;
     N_ELEMENTS.insertElementAt(st.nextToken(), fieldIndex);

     // Process some conversions
     float temp = 0;

     // Conversion of m to cm
     // Only three digits of the converted value are used in order to get rid of
     // rounding errors. These digits (if ".000") are removed by the refresh()-
     // methods.
     temp = Float.parseFloat((String) DEPTH.elementAt(fieldIndex));
     DEPTH.set(fieldIndex, Graph.ValueToString(new Float(temp * 100.0f), 3));

     // Temperature in C (instead of K)
     temp = Float.parseFloat((String) TEMPERATURE.elementAt(fieldIndex));
     TEMPERATURE.set(fieldIndex, Graph.ValueToString(new Float(temp - 273.15f), 3));
     // Volumetric fractions in % (instead of [1])
     temp = Float.parseFloat((String) VOL_ICE.elementAt(fieldIndex));
     VOL_ICE.set(fieldIndex, Graph.ValueToString(new Float(temp * 100.0f), 3));
     temp = Float.parseFloat((String) VOL_WATER.elementAt(fieldIndex));
     VOL_WATER.set(fieldIndex, Graph.ValueToString(new Float(temp * 100.0f), 3));
     temp = Float.parseFloat((String) VOL_VOID.elementAt(fieldIndex));
     VOL_VOID.set(fieldIndex, Graph.ValueToString(new Float(temp * 100.0f), 3));
     temp = Float.parseFloat((String) VOL_SOIL.elementAt(fieldIndex));
     VOL_SOIL.set(fieldIndex, Graph.ValueToString(new Float(temp * 100.0f), 3));

     return true;
  }


  void readParameters()
  // Set hardcoded default values of principal parameters
  {
    // ===== Basic Parameters =====

    // If during current session a valid snow file was constructed, the name
    // of that file is set as a default value
    if (Setup.m_SnowFile.equals(""))
      FILENAME = getModelDirectory() + ModelDialog.SlashConverted("/DATA/input/template.sno"); //Schirmer
    else
      FILENAME = Setup.m_SnowFile;
    FILENAME = ModelDialog.SlashConverted(FILENAME); // Slash/backslash conversion

    SNOWDEPTH = "50";
    STATIONNAME = "STN";
    ALT = "2000";
    BARESOIL_z0 = "0.02";
    SOILALBEDO = "0.2";
    LON = "10";
    LAT = "45";
    SLOPE = "0";
    AZI = "0";
    NSNOW = "5";
    NSOIL = "5";
    YEAR0 = "2000";
    MONTH0 = "01";
    DAY0 = "01";
    HOUR0 = "00";
    MINUTE0 = "00";
    CANOPY_HEIGHT ="0";
    CANOPY_LAI= "0";
    CANOPY_DIRECT_THROUGHFALL = "1";

    // Default number of snow/soil layers as integer values
    nSNOW = nSNOW_prev = Integer.parseInt(NSNOW);
    nSOIL = nSOIL_prev = Integer.parseInt(NSOIL);
    nTOTAL = nTOTAL_prev = nSNOW + nSOIL;

    // ===== Layer Parameters =====
    // Initialize fields
    initializeLayers(nTOTAL);

    // Mark that the elements have not been edited before.
    //for (int i = 0; i < nTOTAL; i++) edited.insertElementAt(new Boolean(false), i);
    // Not necessary here, since this is done later.

    // Fill the layer vectors with default parameters
    for (int i=0; i < nTOTAL; i++)
    {
      addLayer(i + 1, false);
    }

  }


  void initializeLayers(int nTOTAL)
  {
    DEPTH       = new Vector(nTOTAL, 5); // 5 fields will be added if vector
    TEMPERATURE = new Vector(nTOTAL, 5); //   capacity is incremented
    VOL_ICE     = new Vector(nTOTAL, 5);
    VOL_WATER   = new Vector(nTOTAL, 5);
    VOL_SOIL    = new Vector(nTOTAL, 5);
    VOL_VOID    = new Vector(nTOTAL, 5);
    SOIL_RHO    = new Vector(nTOTAL, 5);
    SOIL_K      = new Vector(nTOTAL, 5);
    SOIL_C      = new Vector(nTOTAL, 5);
    YEAR        = new Vector(nTOTAL, 5);
    MONTH       = new Vector(nTOTAL, 5);
    DAY         = new Vector(nTOTAL, 5);
    HOUR        = new Vector(nTOTAL, 5);
    MINUTE      = new Vector(nTOTAL, 5);
    GRAINRADIUS = new Vector(nTOTAL, 5);
    SPHERICITY  = new Vector(nTOTAL, 5);
    DENDRICITY  = new Vector(nTOTAL, 5);
    BONDRADIUS  = new Vector(nTOTAL, 5);
    GRAINMARKER = new Vector(nTOTAL, 5);
    HOAR        = new Vector(nTOTAL, 5);
    N_ELEMENTS  = new Vector(nTOTAL, 5);

    edited      = new Vector(nTOTAL);
  }


  void readSoilLayerDefaultParameters()
  // Set default parameters for soil layers
  {
    DEPTH.insertElementAt(      "10", 0);
    TEMPERATURE.insertElementAt("0", 0);
    VOL_ICE.insertElementAt(    "0", 0);
    VOL_WATER.insertElementAt(  "15", 0);
    VOL_SOIL.insertElementAt(   "80", 0);
    VOL_VOID.insertElementAt(   "5", 0);
    SOIL_RHO.insertElementAt(   "2400", 0);
    SOIL_K.insertElementAt(     "3.8", 0);
    SOIL_C.insertElementAt(     "900", 0);
    YEAR.insertElementAt(       YEAR0, 0);
    MONTH.insertElementAt(      MONTH0, 0);
    DAY.insertElementAt(        DAY0, 0);
    HOUR.insertElementAt(       HOUR0, 0);
    MINUTE.insertElementAt(     MINUTE0, 0);
    GRAINRADIUS.insertElementAt("0", 0);
    SPHERICITY.insertElementAt( "0", 0);
    DENDRICITY.insertElementAt( "0", 0);
    BONDRADIUS.insertElementAt( "0", 0);
    GRAINMARKER.insertElementAt("0", 0);
    HOAR.insertElementAt(       "0", 0);
    N_ELEMENTS.insertElementAt( "1", 0);

    edited.insertElementAt(new Boolean(false), 0);
  }


  void readSnowLayerDefaultParameters(int layerNr)
  // Set default parameters for snow layers
  {
    int fieldIndex = layerNr - 1;

    DEPTH.insertElementAt(      "10", fieldIndex);
    TEMPERATURE.insertElementAt("0", fieldIndex);
    VOL_ICE.insertElementAt(    "10", fieldIndex);
    VOL_WATER.insertElementAt(  "0", fieldIndex);
    VOL_SOIL.insertElementAt(   "0", fieldIndex);
    VOL_VOID.insertElementAt(   "90", fieldIndex);
    SOIL_RHO.insertElementAt(   "0", fieldIndex);
    SOIL_K.insertElementAt(     "0", fieldIndex);
    SOIL_C.insertElementAt(     "0", fieldIndex);
    YEAR.insertElementAt(       YEAR0, fieldIndex);
    MONTH.insertElementAt(      MONTH0, fieldIndex);
    DAY.insertElementAt(        DAY0, fieldIndex);
    HOUR.insertElementAt(       HOUR0, fieldIndex);
    MINUTE.insertElementAt(     MINUTE0, fieldIndex);
    GRAINRADIUS.insertElementAt("0.5", fieldIndex);
    SPHERICITY.insertElementAt( "0.5", fieldIndex);
    DENDRICITY.insertElementAt( "1.0", fieldIndex);
    BONDRADIUS.insertElementAt( "0.1", fieldIndex);
    GRAINMARKER.insertElementAt("0", fieldIndex);
    HOAR.insertElementAt(       "0", fieldIndex);
    N_ELEMENTS.insertElementAt( "1", fieldIndex);

    edited.insertElementAt(new Boolean(false), fieldIndex);
  }


  void copyPrevLayer(int layerNr)
  // Copy parameters of layerNr-1 to layerNr
  {
    int fieldIndex = layerNr - 1;

    DEPTH.insertElementAt(DEPTH.elementAt(fieldIndex - 1), fieldIndex);
    TEMPERATURE.insertElementAt(TEMPERATURE.elementAt(fieldIndex - 1), fieldIndex);
    VOL_ICE.insertElementAt(VOL_ICE.elementAt(fieldIndex - 1), fieldIndex);
    VOL_WATER.insertElementAt(VOL_WATER.elementAt(fieldIndex - 1), fieldIndex);
    VOL_SOIL.insertElementAt(VOL_SOIL.elementAt(fieldIndex - 1), fieldIndex);
    VOL_VOID.insertElementAt(VOL_VOID.elementAt(fieldIndex - 1), fieldIndex);
    SOIL_RHO.insertElementAt(SOIL_RHO.elementAt(fieldIndex - 1), fieldIndex);
    SOIL_K.insertElementAt(SOIL_K.elementAt(fieldIndex - 1), fieldIndex);
    SOIL_C.insertElementAt(SOIL_C.elementAt(fieldIndex - 1), fieldIndex);
    YEAR.insertElementAt(YEAR.elementAt(fieldIndex - 1), fieldIndex);
    MONTH.insertElementAt(MONTH.elementAt(fieldIndex - 1), fieldIndex);
    DAY.insertElementAt(DAY.elementAt(fieldIndex - 1), fieldIndex);
    HOUR.insertElementAt(HOUR.elementAt(fieldIndex - 1), fieldIndex);
    MINUTE.insertElementAt(MINUTE.elementAt(fieldIndex - 1), fieldIndex);
    GRAINRADIUS.insertElementAt(GRAINRADIUS.elementAt(fieldIndex - 1), fieldIndex);
    SPHERICITY.insertElementAt(SPHERICITY.elementAt(fieldIndex - 1), fieldIndex);
    DENDRICITY.insertElementAt(DENDRICITY.elementAt(fieldIndex - 1), fieldIndex);
    BONDRADIUS.insertElementAt(BONDRADIUS.elementAt(fieldIndex - 1), fieldIndex);
    GRAINMARKER.insertElementAt(GRAINMARKER.elementAt(fieldIndex - 1), fieldIndex);
    HOAR.insertElementAt(HOAR.elementAt(fieldIndex - 1), fieldIndex);
    N_ELEMENTS.insertElementAt(N_ELEMENTS.elementAt(fieldIndex - 1), fieldIndex);

    edited.insertElementAt(new Boolean(false), fieldIndex);
  }


  void addLayer(int layerNr, boolean prevLayerCopy)
  // Inserts a new snow or soil layer with default parameters.
  // prevLayerCopy: true, if previous layer data should be used as default
  //                      for current layer
  {
    if (layerNr == 1)
    // first layer
    {
      if (nSOIL == 0) readSnowLayerDefaultParameters(1); // no soil data available
      else readSoilLayerDefaultParameters(); // soil data available
    }
    else if (layerNr <= nSOIL)
    // soil layer
    {
      if (prevLayerCopy) copyPrevLayer(layerNr);
      else readSoilLayerDefaultParameters();
    }
    else if (layerNr == nSOIL + 1)
    // first snow layer
      readSnowLayerDefaultParameters(layerNr);
    else
    // rest of snow layers
    {
      if (prevLayerCopy) copyPrevLayer(layerNr);
      else readSnowLayerDefaultParameters(layerNr);
    }
  }


  void removeLayer(int layerNr)
  // Removes a soil or snow layer
  {
    int fieldIndex = layerNr - 1;

    DEPTH.removeElementAt(fieldIndex);
    TEMPERATURE.removeElementAt(fieldIndex);
    VOL_ICE.removeElementAt(fieldIndex);
    VOL_WATER.removeElementAt(fieldIndex);
    VOL_SOIL.removeElementAt(fieldIndex);
    VOL_VOID.removeElementAt(fieldIndex);
    SOIL_RHO.removeElementAt(fieldIndex);
    SOIL_K.removeElementAt(fieldIndex);
    SOIL_C.removeElementAt(fieldIndex);
    YEAR.removeElementAt(fieldIndex);
    MONTH.removeElementAt(fieldIndex);
    DAY.removeElementAt(fieldIndex);
    HOUR.removeElementAt(fieldIndex);
    MINUTE.removeElementAt(fieldIndex);
    GRAINRADIUS.removeElementAt(fieldIndex);
    SPHERICITY.removeElementAt(fieldIndex);
    DENDRICITY.removeElementAt(fieldIndex);
    BONDRADIUS.removeElementAt(fieldIndex);
    GRAINMARKER.removeElementAt(fieldIndex);
    HOAR.removeElementAt(fieldIndex);
    N_ELEMENTS.removeElementAt(fieldIndex);

    edited.removeElementAt(fieldIndex);
  }


  void refresh()
  // Replaces jTextFields of the basic data input frame with the actual values
  // of the member variables
  {
    jText_FILENAME.setText(FILENAME);
    jText_SNOWDEPTH.setText(removePointZero(SNOWDEPTH));
    jText_STATIONNAME.setText(STATIONNAME);
    jText_ALT.setText(removePointZero(ALT));
    jText_BARESOIL.setText(BARESOIL_z0);
    jText_SOILALBEDO.setText(SOILALBEDO);
    jText_SLOPE.setText(removePointZero(SLOPE));
    jText_LON.setText(removePointZero(LON));
    jText_LAT.setText(removePointZero(LAT));
    jText_AZI.setText(removePointZero(AZI));
    jText_NSNOW.setText(NSNOW);
    jText_NSOIL.setText(Integer.toString(NSOILorig)); //Schirmer, handle negative NSOIL intern as 0, but write out the original value in file
    jText_YEAR0.setText(YEAR0);
    jText_MONTH0.setText(MONTH0);
    jText_DAY0.setText(DAY0);
    jText_HOUR0.setText(HOUR0);
    jText_MINUTE0.setText(MINUTE0);
    jText_CANOPY_HEIGHT.setText(CANOPY_HEIGHT);
    jText_CANOPY_LAI.setText(CANOPY_LAI);
    jText_CANOPY_DIRECT_THROUGHFALL.setText(CANOPY_DIRECT_THROUGHFALL);
  }


  void refreshLayer(int layerNr)
  // Replaces jTextFields with the actual values of the member variables
  // for the Layer Input frame.
  {
    int fieldIndex = layerNr - 1;

    jText_DEPTH.setText(removePointZero((String) DEPTH.elementAt(fieldIndex)));
    jText_TEMPERATURE.setText(removePointZero((String) TEMPERATURE.elementAt(fieldIndex)));
    jText_VOL_ICE.setText(removePointZero((String) VOL_ICE.elementAt(fieldIndex)));
    jText_VOL_WATER.setText(removePointZero((String) VOL_WATER.elementAt(fieldIndex)));
    jText_VOL_SOIL.setText(removePointZero((String) VOL_SOIL.elementAt(fieldIndex)));
    jText_VOL_VOID.setText(removePointZero((String) VOL_VOID.elementAt(fieldIndex)));
    jText_SOIL_RHO.setText(removePointZero((String) SOIL_RHO.elementAt(fieldIndex)));
    jText_SOIL_K.setText((String) SOIL_K.elementAt(fieldIndex));
    jText_SOIL_C.setText(removePointZero((String) SOIL_C.elementAt(fieldIndex)));
    jText_YEAR.setText((String) YEAR.elementAt(fieldIndex));
    jText_MONTH.setText((String) MONTH.elementAt(fieldIndex));
    jText_DAY.setText((String) DAY.elementAt(fieldIndex));
    jText_HOUR.setText((String) HOUR.elementAt(fieldIndex));
    jText_MINUTE.setText((String) MINUTE.elementAt(fieldIndex));
    jText_GRAINRADIUS.setText((String) GRAINRADIUS.elementAt(fieldIndex));
    jText_SPHERICITY.setText((String) SPHERICITY.elementAt(fieldIndex));
    jText_DENDRICITY.setText((String) DENDRICITY.elementAt(fieldIndex));
    jText_BONDRADIUS.setText((String) BONDRADIUS.elementAt(fieldIndex));
    jText_GRAINMARKER.setText((String) GRAINMARKER.elementAt(fieldIndex));
    jText_HOAR.setText((String) HOAR.elementAt(fieldIndex));
    jText_N_ELEMENTS.setText((String) N_ELEMENTS.elementAt(fieldIndex));
  }


  void grabData()
  // Grab the data currently resident in the Basic Data Input frame.
  {
     // JTextField Data, also remove front and back blanks
     FILENAME = jText_FILENAME.getText().trim();
     SNOWDEPTH = jText_SNOWDEPTH.getText().trim();
     STATIONNAME = jText_STATIONNAME.getText().trim();
     LON = jText_LON.getText().trim();
     LAT = jText_LAT.getText().trim();
     SLOPE = jText_SLOPE.getText().trim();
     AZI = jText_AZI.getText().trim();
     ALT = jText_ALT.getText().trim();
     BARESOIL_z0 = jText_BARESOIL.getText().trim();
     SOILALBEDO = jText_SOILALBEDO.getText().trim();
     NSNOW = jText_NSNOW.getText().trim();

     //Schirmer, handle negative NSOIL intern as 0, but write out the original value in file
     NSOILorig = Integer.parseInt(jText_NSOIL.getText().trim());
     if (NSOILorig < 0) {
         NSOIL = "0";
     }
     else {
         NSOIL = jText_NSOIL.getText().trim();
     }

     YEAR0 = jText_YEAR0.getText().trim();
     MONTH0 = jText_MONTH0.getText().trim();
     DAY0 = jText_DAY0.getText().trim();
     HOUR0 = jText_HOUR0.getText().trim();
     MINUTE0 = jText_MINUTE0.getText().trim();
     CANOPY_HEIGHT = jText_CANOPY_HEIGHT.getText().trim();
     CANOPY_LAI = jText_CANOPY_LAI.getText().trim();
     CANOPY_DIRECT_THROUGHFALL = jText_CANOPY_DIRECT_THROUGHFALL.getText().trim();
  }


  void grabLayerData(int layerNr)
  // Grab the data currently resident in the Layer Data Input frame.
  {
    int fieldIndex = layerNr - 1;

    DEPTH.set(fieldIndex,       jText_DEPTH.getText().trim());
    TEMPERATURE.set(fieldIndex, jText_TEMPERATURE.getText().trim());
    VOL_ICE.set(fieldIndex,     jText_VOL_ICE.getText().trim());
    VOL_WATER.set(fieldIndex,   jText_VOL_WATER.getText().trim());
    VOL_SOIL.set(fieldIndex,    jText_VOL_SOIL.getText().trim());
    VOL_VOID.set(fieldIndex,    jText_VOL_VOID.getText().trim());
    SOIL_RHO.set(fieldIndex,    jText_SOIL_RHO.getText().trim());
    SOIL_K.set(fieldIndex,      jText_SOIL_K.getText().trim());
    SOIL_C.set(fieldIndex,      jText_SOIL_C.getText().trim());
    YEAR.set(fieldIndex,        jText_YEAR.getText().trim());
    MONTH.set(fieldIndex,       jText_MONTH.getText().trim());
    DAY.set(fieldIndex,         jText_DAY.getText().trim());
    HOUR.set(fieldIndex,        jText_HOUR.getText().trim());
    MINUTE.set(fieldIndex,      jText_MINUTE.getText().trim());
    GRAINRADIUS.set(fieldIndex, jText_GRAINRADIUS.getText().trim());
    SPHERICITY.set(fieldIndex,  jText_SPHERICITY.getText().trim());
    DENDRICITY.set(fieldIndex,  jText_DENDRICITY.getText().trim());
    BONDRADIUS.set(fieldIndex,  jText_BONDRADIUS.getText().trim());
    GRAINMARKER.set(fieldIndex, jText_GRAINMARKER.getText().trim());
    HOAR.set(fieldIndex,        jText_HOAR.getText().trim());
    N_ELEMENTS.set(fieldIndex,  jText_N_ELEMENTS.getText().trim());
  }


  void setToPrevLayer(int layerNr)
  // Setting the values of the current layer to the previous layer
  {
    int fieldIndex = layerNr - 1;

    DEPTH.set(fieldIndex,       DEPTH.elementAt(fieldIndex - 1));
    TEMPERATURE.set(fieldIndex, TEMPERATURE.elementAt(fieldIndex - 1));
    VOL_ICE.set(fieldIndex,     VOL_ICE.elementAt(fieldIndex - 1));
    VOL_WATER.set(fieldIndex,   VOL_WATER.elementAt(fieldIndex - 1));
    VOL_SOIL.set(fieldIndex,    VOL_SOIL.elementAt(fieldIndex - 1));
    VOL_VOID.set(fieldIndex,    VOL_VOID.elementAt(fieldIndex - 1));
    SOIL_RHO.set(fieldIndex,    SOIL_RHO.elementAt(fieldIndex - 1));
    SOIL_K.set(fieldIndex,      SOIL_K.elementAt(fieldIndex - 1));
    SOIL_C.set(fieldIndex,      SOIL_C.elementAt(fieldIndex - 1));
    YEAR.set(fieldIndex,        YEAR.elementAt(fieldIndex - 1));
    MONTH.set(fieldIndex,       MONTH.elementAt(fieldIndex - 1));
    DAY.set(fieldIndex,         DAY.elementAt(fieldIndex - 1));
    HOUR.set(fieldIndex,        HOUR.elementAt(fieldIndex - 1));
    MINUTE.set(fieldIndex,      MINUTE.elementAt(fieldIndex - 1));
    GRAINRADIUS.set(fieldIndex, GRAINRADIUS.elementAt(fieldIndex - 1));
    SPHERICITY.set(fieldIndex,  SPHERICITY.elementAt(fieldIndex - 1));
    DENDRICITY.set(fieldIndex,  DENDRICITY.elementAt(fieldIndex - 1));
    BONDRADIUS.set(fieldIndex,  BONDRADIUS.elementAt(fieldIndex - 1));
    GRAINMARKER.set(fieldIndex, GRAINMARKER.elementAt(fieldIndex - 1));
    HOAR.set(fieldIndex,        HOAR.elementAt(fieldIndex - 1));
    N_ELEMENTS.set(fieldIndex,  N_ELEMENTS.elementAt(fieldIndex - 1));
  }


  boolean checkData()
  {
    // Remove blanks from begin and end of string:
    // not necessary, already done in grabData()

    // Check if number-strings currently contained in the JTextFields are
    // valid float/int numbers and are within allowed thresholds
    if (!checkString(SNOWDEPTH, "Snow Depth", "Float", 0, 2000))
      return false;

    if (!checkString(ALT, "Altitude", "Float", 0, 10000))
      return false;

    if (!checkString(SOILALBEDO, "Soil Albedo", "Float", 0, 1))
      return false;

    if (!checkString(BARESOIL_z0, "Bare Soil", "Float", (float) 0.0, (float) 0.2))
      return false;

    if (!checkString(LON, "Longitude", "Float", -180, 180))
      return false;

    if (!checkString(LAT, "Latitude", "Float", -90, 90))
      return false;

    if (!checkString(SLOPE, "Slope Angle", "Float", 0, 90))
      return false;

    if (!checkString(AZI, "Slope Azimuth", "Float", 0, 360))
      return false;

    if (!checkString(NSNOW, "Number of Snow Layers", "Integer", 0, 1000))
      return false;

    if (!checkString(NSOIL, "Number of Soil Layers", "Integer", 0, 1000))
      return false;

    if (!checkString(CANOPY_HEIGHT, "Canopy Height", "Float", 0, 50))
      return false;

    if (!checkString(CANOPY_LAI, "Canopy Leaf Area Index", "Float", 0, 10))
     return false;

    if (!checkString(CANOPY_DIRECT_THROUGHFALL, "Canopy Direct Throughfall", "Float", 0, 1))
      return false;

    if (!timeCheck(YEAR0, MONTH0, DAY0, HOUR0, MINUTE0)) return false;

    // Conversion of slashes to backslashes (and vice versa)
    FILENAME = ModelDialog.SlashConverted(FILENAME);

    // Consistency checks
    //if ((Integer.parseInt(NSNOW) + Integer.parseInt(NSOIL)) == 0)
    //{  jLabel_Error.setText("Error: Number of Layers = 0"); return false; }

    // Check for correct path names
    if (!ModelDialog.pathFormatCheck(FILENAME))
    {  jLabel_Error.setText("Error in Path Name: " + FILENAME); return false; }

    // Check if directories exist
    File file = new File(FILENAME);
    File directory = new File(file.getParent());
    if (!directory.exists())
    {
         jLabel_Error.setText("Directory does not exist: " + directory.getName());
         return false;
    }

    // Check if file exists
    if (file.exists())
    {
         jLabel_Error.setText("");

         YesNoDialog ynd = new YesNoDialog(mFrame, "Note",
           "File exists: " + FILENAME + "!", "Overwrite this file?",
           "Yes", "No", true);
         ynd.setLocation(mFrame.DialogCorner(ynd, mFrame));
         ynd.setVisible(true);

         if (ynd.action1) // File overwriting desired
         {
           return true;
         }
         else
         {
           jLabel_Error.setText("Please change the File Name!");
           return false;
         }
    }

    return true;
  }


  boolean checkLayerData(int layerNr)
  // Check if number-strings currently contained in the JTextFields are
  // valid float/int numbers and are within allowed thresholds
  {
    int fieldIndex = layerNr - 1;

    if (!checkString((String) DEPTH.elementAt(fieldIndex), "Layer Depth", "Float", 0, 1000))
      return false;

    if (!checkString((String) TEMPERATURE.elementAt(fieldIndex), "Layer Temperature", "Float", -50, 100))
      return false;

    if (layerNr > nSOIL)
    {
      if (!checkString((String) TEMPERATURE.elementAt(fieldIndex), "Snow Layer Temperature", "Float", -50, 0))
      return false;
    }

    if (!checkString((String) VOL_SOIL.elementAt(fieldIndex), "Volumetric Soil Fraction", "Float", 0, 100))
      return false;

    if (!checkString((String) VOL_WATER.elementAt(fieldIndex), "Volumetric Water Fraction", "Float", 0, 100))
      return false;

    if (!checkString((String) VOL_ICE.elementAt(fieldIndex), "Volumetric Ice Fraction", "Float", 0, 100))
      return false;

    if (!checkString((String) VOL_VOID.elementAt(fieldIndex), "Volumetric Void Fraction", "Float", 0, 100))
      return false;

    if (!checkString((String) VOL_SOIL.elementAt(fieldIndex), "Volumetric Soil Fraction", "Float", 0, 100))
      return false;

    if (!checkString((String) SOIL_RHO.elementAt(fieldIndex), "Soil Density", "Float", 0, 5000))
      return false;

    if (!checkString((String) SOIL_K.elementAt(fieldIndex), "Soil Conductivity", "Float", 0, 10))
      return false;

    if (!checkString((String) SOIL_C.elementAt(fieldIndex), "Soil Specific Heat", "Float", 0, 5000))
      return false;

    if (!timeCheck((String) YEAR.elementAt(fieldIndex),
                   (String) MONTH.elementAt(fieldIndex),
                   (String) DAY.elementAt(fieldIndex),
                   (String) HOUR.elementAt(fieldIndex),
                   (String) MINUTE.elementAt(fieldIndex)))
      return false;


  //if soil edited, grain radius could be very big (100000) (Schirmer)
  if (actLayerIndex <= nSOIL) {

      if (!checkString((String) GRAINRADIUS.elementAt(fieldIndex),
                       "Grain Radius", "Float", 0, 100000))
          return false;
  }
  else {
      if (!checkString((String) GRAINRADIUS.elementAt(fieldIndex),
                       "Grain Radius", "Float", 0, 10))
          return false;

  }
    if (!checkString((String) SPHERICITY.elementAt(fieldIndex), "Sphericity", "Float", 0, 1))
      return false;

    if (!checkString((String) DENDRICITY.elementAt(fieldIndex), "Dendricity", "Float", 0, 1))
      return false;

    if (!checkString((String) BONDRADIUS.elementAt(fieldIndex), "Bond Radius", "Float", 0, 10))
      return false;

    if (!checkString((String) GRAINMARKER.elementAt(fieldIndex), "Grain Marker", "Integer", 0, 30))
      return false;

    if (!checkString((String) HOAR.elementAt(fieldIndex), "Surface Hoar", "Float", 0, 10)) //thresholds??
      return false;

    if (!checkString((String) N_ELEMENTS.elementAt(fieldIndex), "No. of Elements", "Integer", 0, 100))
      return false;

    // Consistency checks

    // Check if sum of volumetric fractions is 100.
    if ((Float.parseFloat((String) VOL_ICE.elementAt(fieldIndex)) +
         Float.parseFloat((String) VOL_WATER.elementAt(fieldIndex)) +
         Float.parseFloat((String) VOL_SOIL.elementAt(fieldIndex)) +
         Float.parseFloat((String) VOL_VOID.elementAt(fieldIndex))) != 100.0) {
        jLabel_Error.setText(
                "Error: Sum of Volumetric Fractions should be 100%");
        return false;
    }

    // Check if sum of current layer snow depths exceeds total snow depth.
    // Sum calculation will not include higher layers (if going back to previous layer).
    float cumulDepth = (float) 0.0;
    float SnowDepth = (new Float(Float.parseFloat(SNOWDEPTH))).floatValue();
    for (int i = nSOIL; i <= fieldIndex; i++) {
        cumulDepth += (new Float(Float.parseFloat((String) DEPTH.elementAt(i)))).
                floatValue();
        // System.out.println("SNOWDEPTH, CurrentSum" + SnowDepth +","+cumulDepth);
    }
    if (cumulDepth > SnowDepth) {
        //Schirmer: do the same like below in checkSnowDepth()
        jLabel_Error.setText("Error: Sum of Snow Layer Depths = " + cumulDepth +
                             " cm, Total Snow Depth = " + SnowDepth + " cm");
        //jLabel_Error.setText("Error: Current Sum of Snow Layer Depths exceeds Total Snow Depth. Correct the data!");
        return false;
    }

    return true;
}

  boolean checkSnowDepth()
  // Called when the file creation is requested.
  // Checks if the total snow depth provided in section 1 equals the sum of
  // the layer snow depths, given in section 2.
  {
    float cumulDepth = (float) 0.0;
    float SnowDepth = (new Float(Float.parseFloat(SNOWDEPTH))).floatValue();
    for (int i= nSOIL; i < nTOTAL; i++)
    {
      cumulDepth += (new Float(Float.parseFloat((String) DEPTH.elementAt(i)))).floatValue();
      //System.out.println("SNOWDEPTH, CurrentSum" + SnowDepth +","+cumulDepth);
    }
    if (cumulDepth != SnowDepth)
    {
      jLabel_Error.setText("Error: Sum of Snow Layer Depths = " + cumulDepth +
        " cm, Total Snow Depth = " + SnowDepth + " cm");
      return false;
    }

    return true;
  }


  boolean checkString(String stringToCheck, String stringName, String type,
    float startValue, float endValue)
  // It is checked if the numerical value of stringToCheck is a valid number
  // and between startValue and endValue
  // stringName: parameter name that is listed in the dialog window
  // type: Float or Integer
  {
    float x = 0;

    try  {
      if      (type == "Float")   x = Float.parseFloat(stringToCheck);
      else if (type == "Integer") x = (float) Integer.parseInt(stringToCheck);
      else
      {  jLabel_Error.setText("Error: " + stringName + ": type not valid!");
         return false;
      }

      if ((type == "Float") && ((x < startValue) || (x > endValue)))
      {
        jLabel_Error.setText("Error: " + stringName + ": valid values " +
          startValue + " ... " + endValue + "!");
        return false;
      }

      if ((type == "Integer") && ((x < startValue) || (x > endValue)))
      {
        jLabel_Error.setText("Error: " + stringName + ": valid values " +
          (int) startValue + " ... " + (int) endValue + "!");
        return false;
      }
    }
    catch (NumberFormatException nfe)
    {
      if ( type == "Float") {

          //for canopy set the default values  Schirmer
          if (stringName.startsWith("Canopy") ||
                  stringName.equals("Soil Albedo") ||
                   stringName.equals("Bare Soil")) {

              int opt;

              opt = JOptionPane.showConfirmDialog(this, "Error: " + stringName +
                                                  " is not a valid Float number! Take default value?", "",
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.ERROR_MESSAGE);

              if (opt == JOptionPane.NO_OPTION) {
                  return false;
              } else if (opt == JOptionPane.YES_OPTION) {

                  if (stringName.equals("Canopy Height")) {
                      CANOPY_HEIGHT = "0";

                      this.jText_CANOPY_HEIGHT.setText(this.CANOPY_HEIGHT);

                  } else if (stringName.equals("Canopy Leaf Area Index")) {
                      CANOPY_LAI = "0";

                      this.jText_CANOPY_LAI.setText(this.CANOPY_LAI);

                  } else if (stringName.equals("Canopy Direct Throughfall")) {
                      CANOPY_DIRECT_THROUGHFALL = "1";

                      this.jText_CANOPY_DIRECT_THROUGHFALL.setText(this.
                              CANOPY_DIRECT_THROUGHFALL);

                  } else if (stringName.equals("Soil Albedo")) {
                      this.SOILALBEDO = "0.2";

                      this.jText_SOILALBEDO.setText(this.SOILALBEDO);
                  } else if (stringName.equals("Bare Soil")) {
                      BARESOIL_z0 = "0.02";

                      this.jText_BARESOIL.setText(this.BARESOIL_z0);
                  }
              }return true;
          }

          else {
              jLabel_Error.setText("Error: " + stringName +
                                   " is not a valid Float number!");
          }
      }
      else if (type == "Integer")
        jLabel_Error.setText("Error: " + stringName + " is not a valid Integer number!");
      return false;
    }

    return true;
  }


  boolean timeCheck(String year, String month, String day, String hour, String minute)
  // Check if the input parameters define a valid date
  {
    int n1, n2;

    if (!checkString(year, "Year", "Integer", 1900, 2100))
      return false;

    try  {
      n1 = Integer.parseInt(month);
      if ((n1 < 1) || (n1 > 12))
      {
        jLabel_Error.setText("Error: Month < 1 or > 12");
        return false;
      }
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Month not a valid Integer number!");
      return false;
    }

    try  {
      n2 = Integer.parseInt(day);
      if ( (n2 < 1)  ||
           (n2 > 31) ||
          ((n2 > 30) && ((n1==4) || (n1==6) || (n1==9) || (n1==11))) ||
          ((n2 > 28) && (n1==2)))
      {
        jLabel_Error.setText("Error: Day < 1 or > 28, 30 or 31");
        return false;
      }
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Day is not a valid Integer number!");
      return false;
    }

    if (!checkString(hour, "Hour", "Integer", 0, 23))
      return false;

    if (!checkString(minute, "Minute", "Integer", 0, 59))
      return false;

    return true;
  }


  String addPointZero(String floatString)
  // If the input float String is an integer number and does not end with ".0",
  // ".0" is added (e.g. 47 --> 47.0)
  {
    boolean containsFloat = false;

    // Check if floatString contains "."
    for (int i=0; i<floatString.length(); i++)
    {
        if (floatString.substring(i, i+1).equals("."))
             containsFloat = true;
    }

    if (containsFloat == true)
    {
        return floatString;
    }
    else
    {
        return floatString + ".0";
    }

  }


  void jButton_Continue_actionPerformed(ActionEvent e)
  {
    // Get the current values of the Basic Data Input frame
    grabData();

    // Check the data for errors.
    // If errors exist, they are described in jLabel_Error and can be corrected.
    if (!checkData()) return;
    jLabel_Error.setText("Basic Data Input successful!");
    basicInputFinished = true;

    // Save file name. Will be default for next start of SnowInputDialog and
    // ModelRun.
    Setup.m_SnowFile = FILENAME;

    // Number of snow/soil layers as displayed in the basic frame before last user
    // intervention
    nSNOW_prev = nSNOW;
    nSOIL_prev = nSOIL;
    nTOTAL_prev = nTOTAL;

    // Number of snow and soil layers after user possibly processed changes
    nSNOW = Integer.parseInt(NSNOW);
    nSOIL =  Integer.parseInt(NSOIL);
    nTOTAL = nSNOW + nSOIL;

    if (nSOIL < nSOIL_prev) // number of soil layers was decreased
    { // remove some existing soil layers from the top towards bottom
      for (int i = nSOIL_prev; i > nSOIL; i--)
      {
        removeLayer(i);
        if (actLayerIndex > i) actLayerIndex--;
      }
    }

    if (nSOIL > nSOIL_prev) // number of soil layers was increased
    { // add some soil layers to the top
      for (int i = nSOIL_prev + 1; i <= nSOIL; i++)
      {
        addLayer(i, true); // prevLayerCopy = true
        if (actLayerIndex >= i) actLayerIndex++;
      }
    }

    int startIndex, endIndex;
    if (nSNOW < nSNOW_prev) // number of snow layers was decreased
    { // remove some snow layers from top towards botton
      startIndex = nSOIL + nSNOW_prev;
      endIndex = nSOIL + nSNOW;
      for (int i = startIndex; i > endIndex; i--)
      {
        removeLayer(i);
        if (actLayerIndex == i) actLayerIndex--;
      }
    }

    if (nSNOW > nSNOW_prev) // number of snow layers was increased
    { // add some snow layers from top to botton
      startIndex = nSOIL + nSNOW_prev + 1;
      endIndex = nSOIL + nSNOW;
      for (int i = startIndex; i <= endIndex; i++)
      {
        addLayer(i, true); // prevLayerCopy = true
        // actLayerIndex can be kept as it is
      }
    }

    // Create File immediately, if no snow/soil layers should be created
    if (nTOTAL == 0)
    {
      createFile();
      return;
    }

    // Enable/disable buttons
    jButton_ReeditBasicData.setEnabled(true);
    if (actLayerIndex == nTOTAL) // last selectable layer is active
    {
      jButton_CreateFile.setEnabled(true);
      jButton_NextLayer.setEnabled(false);
      if (nTOTAL > 1) jButton_PrevLayer.setEnabled(true);
      else jButton_PrevLayer.setEnabled(false);
    }
    else
    {
      jButton_CreateFile.setEnabled(false);
      jButton_NextLayer.setEnabled(true);
      if (actLayerIndex == 1) jButton_PrevLayer.setEnabled(false);
      else jButton_PrevLayer.setEnabled(true);
    }

    enableBasicFrame(false);
    jButton_Continue.setEnabled(false);

    enableLayerFrame(true);
    if (actLayerIndex <= nSOIL) enableSnowLayerFrame(false);
    else enableSoilLayerFrame(false);

    // Text indicating the number of the currently active layer
    jLabel65.setText("Currently active:");
    jLabel67.setText("(#1 = lowest)");
    displayActiveLayerNumber(actLayerIndex);
    jLabel_activeLayer.setForeground(Color.blue);

    // Display layer parameters in jTextFields
    refreshLayer(actLayerIndex);
  }


  void jButton_NextLayer_actionPerformed(ActionEvent e) { nextLayer(); }
  void nextLayer()
  // Reaction to clicking the "Next Layer"-button or pressing the PageDown button
  {
    // Handle data of current layer, available in Edit Boxes
    grabLayerData(actLayerIndex);
    if (!checkLayerData(actLayerIndex)) return;
    jLabel_Error.setText("");
    setEdited(actLayerIndex);

    // Next layer
    actLayerIndex += 1;
    displayActiveLayerNumber(actLayerIndex);

    // If next layer has not been edited before (this is the case if the file
    // is beeing created new or if new layers have been inserted after stepping
    // back to the basic data frame), it is copied from the current layer.
    if ((actLayerIndex != nSOIL+1) && (!isEdited(actLayerIndex)))
      setToPrevLayer(actLayerIndex);

    refreshLayer(actLayerIndex);

    // Enable/disable buttons and layer text fields
    if (actLayerIndex == nSOIL + 1)
    {
        enableSnowLayerFrame(true);
        enableSoilLayerFrame(false);
    }

    if (actLayerIndex == nTOTAL)
    {
      jButton_CreateFile.setEnabled(true);
      jButton_NextLayer.setEnabled(false);
    }
    jButton_PrevLayer.setEnabled(true);
  }


  void jButton_PrevLayer_actionPerformed(ActionEvent e) { prevLayer(); }
  void prevLayer()
  // Reaction to clicking the "Prev. Layer"-button or pressing the PageUp button
  {
    grabLayerData(actLayerIndex);
    if (!checkLayerData(actLayerIndex)) return;
    jLabel_Error.setText("");
    setEdited(actLayerIndex);

    actLayerIndex -= 1;
    displayActiveLayerNumber(actLayerIndex);

    // Unlike than in the case of stepping forward to the next layers, stepping
    // back no copy possibilities of the current layer are included.
    // This is because stepping forward all previous layers have been reviewed
    // and edited (except some soil layers inserted afterwards).
    refreshLayer(actLayerIndex);
    if (actLayerIndex == nSOIL)
    {
        enableSnowLayerFrame(false);
        enableSoilLayerFrame(true);
    }

    if (actLayerIndex == 1)
    {
      jButton_PrevLayer.setEnabled(false);
    }
    jButton_NextLayer.setEnabled(true);
  }


  void jButton_CreateFile_actionPerformed(ActionEvent e) { createFile(); }
  void createFile()
  // Construction of the output file
  {

    if (nTOTAL > 0) // layers available
    {
      grabLayerData(actLayerIndex);
      if (!checkLayerData(actLayerIndex)) return;
      if (!checkSnowDepth()) return;
      setEdited(actLayerIndex);
    }

    try
    {
      // If name of file to write exists: delete it.
      File file = new File(FILENAME);
      if (file.exists()) file.delete();
      // refresh() poses a question if overwriting is desired.

      FileOutputStream fout = new FileOutputStream(FILENAME, false);
      PrintWriter pout = new PrintWriter(fout);

      // Write standard data

      // Conversion of cm to m
      SNOWDEPTH = (new Float(Float.parseFloat(SNOWDEPTH) / 100.0)).toString();

      // Add .0 for float numbers
      SNOWDEPTH = addPointZero(SNOWDEPTH);
      //ALT = addPointZero(ALT);
      LON = addPointZero(LON);
      LAT = addPointZero(LAT);
      //SLOPE = addPointZero(SLOPE);
      //AZI = addPointZero(AZI);

      CANOPY_HEIGHT=addPointZero(CANOPY_HEIGHT);
      CANOPY_LAI=addPointZero(CANOPY_LAI);
      CANOPY_DIRECT_THROUGHFALL=addPointZero(CANOPY_DIRECT_THROUGHFALL);

      pout.println("[SNOWPACK_INITIALIZATION]");
      pout.println("StationName=" + STATIONNAME);
      pout.println("ProfileDate="
         + YEAR0 + " " + MONTH0 + " " + DAY0 + " " + HOUR0 + " " + MINUTE0);
      pout.println("HS_Last=" + SNOWDEPTH);
      pout.println("Latitude=" + LAT);
      pout.println("Longitude=" + LON);
      pout.println("Altitude=" + ALT);
      pout.println("SlopeAngle=" + SLOPE);
      pout.println("SlopeAzi=" + AZI);

      //Schirmer, handle negative NSOIL intern as 0, but write out the original value in file
      if (NSOILorig < 0) {
          pout.println("nSoilLayerData=" + NSOILorig);
      }
      else {
          pout.println("nSoilLayerData=" + NSOIL);
      }

      pout.println("nSnowLayerData=" + NSNOW);
      pout.println("SoilAlbedo=" + SOILALBEDO);
      pout.println("BareSoil_z0=" + BARESOIL_z0);
      //Write Canopy Data
      pout.println("CanopyHeight=" + CANOPY_HEIGHT);
      pout.println("CanopyLeafAreaIndex=" + CANOPY_LAI);
      pout.println("CanopyDirectThroughfall=" + CANOPY_DIRECT_THROUGHFALL);

      // Write layer data

      // Write header
      pout.println("YYYY MM DD HH MI Layer_Thick T " +
         "Vol_Frac_I Vol_Frac_W Vol_Frac_V Vol_Frac_S Rho_S Conduc_S HeatCapac_S " +
         "rg rb dd sp mk mass_hoar ne");

      if (nTOTAL > 0)
      {
       for (int i=0; i < nTOTAL; i++)
       {
        // Conversion of cm to m
        DEPTH.set(i,
          (new Float(Float.parseFloat((String) DEPTH.elementAt(i)) / 100.0)).toString());

        // Temperature in K (instead of C)
        TEMPERATURE.set(i,
          (new Float(Float.parseFloat((String) TEMPERATURE.elementAt(i)) + 273.15)).toString());

        // Volumetric fractions in [1] (instead of %)
        VOL_ICE.set(i,
          (new Float(Float.parseFloat((String) VOL_ICE.elementAt(i)) / 100.0)).toString());
        VOL_WATER.set(i,
          (new Float(Float.parseFloat((String) VOL_WATER.elementAt(i)) / 100.0)).toString());
        VOL_VOID.set(i,
          (new Float(Float.parseFloat((String) VOL_VOID.elementAt(i)) / 100.0)).toString());
        VOL_SOIL.set(i,
          (new Float(Float.parseFloat((String) VOL_SOIL.elementAt(i)) / 100.0)).toString());

        // Add .0 for float numbers
        DEPTH.set(i, addPointZero((String) DEPTH.elementAt(i)));
        TEMPERATURE.set(i, addPointZero((String) TEMPERATURE.elementAt(i)));
        VOL_ICE.set(i, addPointZero((String) VOL_ICE.elementAt(i)));
        VOL_WATER.set(i, addPointZero((String) VOL_WATER.elementAt(i)));
        VOL_SOIL.set(i, addPointZero((String) VOL_SOIL.elementAt(i)));
        VOL_VOID.set(i, addPointZero((String) VOL_VOID.elementAt(i)));
        SOIL_RHO.set(i, addPointZero((String) SOIL_RHO.elementAt(i)));
        SOIL_K.set(i, addPointZero((String) SOIL_K.elementAt(i)));
        SOIL_C.set(i, addPointZero((String) SOIL_C.elementAt(i)));
        GRAINRADIUS.set(i, addPointZero((String) GRAINRADIUS.elementAt(i)));
        SPHERICITY.set(i, addPointZero((String) SPHERICITY.elementAt(i)));
        DENDRICITY.set(i, addPointZero((String) DENDRICITY.elementAt(i)));
        BONDRADIUS.set(i, addPointZero((String) BONDRADIUS.elementAt(i)));
        HOAR.set(i, addPointZero((String) HOAR.elementAt(i)));

        pout.print(YEAR.elementAt(i) + " ");
        pout.print(MONTH.elementAt(i) + " ");
        pout.print(DAY.elementAt(i) + " ");
        pout.print(HOUR.elementAt(i) + " ");
        pout.print(MINUTE.elementAt(i) + "  ");
        pout.print(DEPTH.elementAt(i) + " ");
        pout.print(TEMPERATURE.elementAt(i) + "  ");
        pout.print(VOL_ICE.elementAt(i) + " ");
        pout.print(VOL_WATER.elementAt(i) + " ");
        pout.print(VOL_VOID.elementAt(i) + " ");
        pout.print(VOL_SOIL.elementAt(i) + "  ");
        pout.print(SOIL_RHO.elementAt(i) + " ");
        pout.print(SOIL_K.elementAt(i) + " ");
        pout.print(SOIL_C.elementAt(i) + "  ");
        pout.print(GRAINRADIUS.elementAt(i) + " ");
        pout.print(BONDRADIUS.elementAt(i) + " ");
        pout.print(DENDRICITY.elementAt(i) + " ");
        pout.print(SPHERICITY.elementAt(i) + " ");
        pout.print(GRAINMARKER.elementAt(i) + " ");
        pout.print(HOAR.elementAt(i) + " ");
        pout.print(N_ELEMENTS.elementAt(i) + " ");
        pout.println("");
       }
      }

      // Last part of file is always the same
      pout.println("SurfaceHoarIndex");
      for (int i=0; i<48; i++) pout.print("0.0 ");
      pout.println("");

      pout.println("DriftIndex");
      for (int i=0; i<48; i++) pout.print("0.0 ");
      pout.println("");

      pout.println("ThreeHourNewSnow");
      for (int i=0; i<144; i++) pout.print("0.0 ");
      pout.println("");

      pout.println("TwentyFourHourNewSnow");
      for (int i=0; i<144; i++) pout.print("0.0 ");
      pout.println("");

      pout.close();
      fout.close();
    }
    catch (IOException e1)
    {
        System.out.println("IOException in SnowInputDialog, jButton_CreateFile_actionPerformed");
        jLabel_Error.setText("Error: Output file " + FILENAME + " cannot be constructed!");
        return;
    }

    // Check if output file exists
    File outfile = new File(FILENAME);
    if (!outfile.exists())
    {
       MessageBox mBox = new MessageBox(mFrame, "Error", "File does not exist: ",
         FILENAME);
       mBox.setLocation(mFrame.DialogCorner(mBox, mFrame)); mBox.setVisible(true);
       return;
    }

    // Message: file construction finished
    MessageBox mBox = new MessageBox(mFrame,
        "Note", "File construction successful.", "Click OK to display file contents!");
    mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
    mBox.setVisible(true);

    // Display contents of the file
    FileDisplay fd = new FileDisplay(mFrame, FILENAME, true, FILENAME);
    fd.setLocation(mFrame.DialogCorner(fd, mFrame));
    fd.setVisible(true);

    dispose();
  }


  void jButton_Cancel_actionPerformed(ActionEvent e)
  // Exit ModelDialog without further actions
  {
    dispose();
  }


  void jButton_ReeditBasicData_actionPerformed(ActionEvent e)
  // When editing layer data, this button can be used to return to the basic
  // data input frame.
  {
    // Handle data of current layer, available in Edit Boxes
    grabLayerData(actLayerIndex);
    //if (!checkLayerData(actLayerIndex)) return; wichtig? Schirmer
    jLabel_Error.setText("");
    setEdited(actLayerIndex);

    // Disable text edit boxes and buttons not needed when handling data of the
    // basic frame
    enableLayerFrame(false);
    jButton_CreateFile.setEnabled(false);
    jButton_NextLayer.setEnabled(false);
    jButton_PrevLayer.setEnabled(false);
    jButton_ReeditBasicData.setEnabled(false);

    // Enable some edit boxes and buttons
    enableBasicFrame(true);
    jButton_Continue.setEnabled(true);

  }


  void displayActiveLayerNumber(int layerNr)
  {
      String layerType;

      if (layerNr <= nSOIL)
         layerType = "Soil Layer";
      else if (layerNr <= nTOTAL)
      {  layerType = "Snow Layer";
         layerNr -= nSOIL;
      }
      else layerType = "Error";

      jLabel_activeLayer.setText(layerType + " #" + layerNr);
  }


  String removePointZero(String oldString)
  { // Removes ".0" if found at the end of the input string
    String newString = oldString;
    if (oldString.endsWith(".0"))
      newString = oldString.substring(0,oldString.length()-2);
    else if (oldString.endsWith(".00"))
      newString = oldString.substring(0,oldString.length()-3);
    else if (oldString.endsWith(".000"))
      newString = oldString.substring(0,oldString.length()-4);
    return newString;
  }


  String getModelDirectory()
  // Read snowpack path from CONSTANTS_USER.INI //changed to constants.ini (Schirmer)
  {
     String constantsFile = Setup.m_IniFilePath + "CONSTANTS.INI";
     try
     {
       IniFile Constants = new IniFile(constantsFile);
       Constants.setSection("Parameters");
       String MODEL_DIRECTORY = Constants.getEntry("MODEL_DIRECTORY", "");
       return MODEL_DIRECTORY;
     }
     catch (IOException e)
     {
       return "";
     }
   }


   void setEdited(int layerNr)
   { edited.set(layerNr - 1, new Boolean(true));}


   boolean isEdited(int layerNr)
   { return ((Boolean) edited.elementAt(layerNr - 1)).booleanValue(); }

}





