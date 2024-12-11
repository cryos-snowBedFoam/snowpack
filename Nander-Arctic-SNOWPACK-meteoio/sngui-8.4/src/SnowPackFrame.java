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
// SnowPackFrame: Handles the frame related to one data file.
//                Includes the graphs SnowPackView and XYPlotView.
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.layout.*;
import ProWin.*;
import javax.swing.event.*;


public class SnowPackFrame extends JInternalFrame implements C_DataFile {

  DataFile dataFile = null;
  SnowPackView snowPackView = null;
  SnowPackDoc spDoc = null;
  File file;
  int IdCode = 0;
  private int yAxis;
  BorderLayout borderLayout1 = new BorderLayout();
  SplitPanel splitPanel1 = new SplitPanel();
  PaneLayout paneLayout1 = new PaneLayout();
  PaneLayout paneLayout2 = new PaneLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  XYPlotView xyPlotView;
  MenuFrame mFrame;
  boolean created = true;


  public SnowPackFrame(File file, int IdCode, MenuFrame mFrame)
  {
    // New frame is resizable, closable, maximizable, iconifiable
    // Title = file name without path
    super(file.getName(), true, true, true, true);
    this.mFrame = mFrame;
    this.setVisible(true); // without this, SnowPackFrame is not visible in Java 1.3
    this.file = file;
    this.IdCode = IdCode; // first parameter to be drawn

    try {

      // Check if "file" has already been loaded previously.
      // In this case, the dataFile from the relevant frame is used also in this new frame.
      if (Setup.modelRunning)
      // Model just finished. Read new file anyway, even if same name as already loaded frame.
        dataFile = null;
      else
        dataFile = mFrame.NewDataFile(file);

      if (dataFile == null)
        // dataFile has not been used in other frames, necessary to load data
      {
        if (file.getName().endsWith(".pro") || file.getName().endsWith(".PRO"))
           dataFile = (DataFile) new ProDataFile();
        else if (file.getName().endsWith(".met") || file.getName().endsWith(".MET"))
           dataFile = (DataFile) new MetDataFile();

        //mFrame.setStatusText("Reading data file ... this may take a while. Please wait!");
        //  mFrame.statusBar.repaint();
        //  String text=MenuFrame.statusBar.getText(); text ok!
        //  System.out.println("Status text: "+text); --> text ok, but status bar is actually
        //   not being updated!!

        FileStatus fileStatus = new FileStatus(mFrame,
          "Please wait ... reading file " + file.getName() + "!", false, file.getName());
        fileStatus.setLocation(mFrame.DialogCorner(fileStatus, mFrame));
        if (!Setup.m_PrintMode) fileStatus.setVisible(true);

        Date curDate = new Date();
        long mSec = curDate.getTime();
        System.out.println("=== Start ReadDataFile(" + file.getName() + "): " + curDate);

        try
        {
            //Schirmer
            if (!dataFile.ReadDataFile(file)) {
                MessageBox mBox = new MessageBox(mFrame,
                                                 "Error",
                                                 "Not a correct amount of parameters? In file:",
                                                 getInputFile().getPath());
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);

                // Remove this SnowPackFrame from the desktopPane
                mFrame.RemoveSnowPackFrame(this);
                created = false;
                dispose(); // dissolve this object
                return;

            }//end Schirmer

          curDate = new Date();
          mSec = curDate.getTime() - mSec;
          System.out.println("=== End ReadDataFile(" + file.getName() + "): " + curDate);
          //System.out.println("ms = " + mSec);
        }
/*        catch(OutOfMemoryException em)
        {
          MessageBox mBox = new MessageBox(mFrame,
                "Error", "Error reading " + file.getName() + ":",
                "Out of Memory!");
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);
        }
*/        catch(Exception e)
        {
          MessageBox mBox = new MessageBox(mFrame,
                "Error", "Error reading " + file.getName() + ":",
                e + "!");
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);
        }

        //mFrame.statusBar.setText("File reading finished. Drawing graph ...");
        //Status bar is not updated!

        fileStatus.dispose();
      }


      // Check if data for given IdCode exist
      if (!dataFile.CalculateMinMaxValue(IdCode))
      {   String IdStr = (new Integer(IdCode)).toString();
          MessageBox mBox = new MessageBox(mFrame,
                "Error", "No (correct) data for sel. parameter (Id="+IdStr+") in file",
                getInputFile().getPath() + "!");
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);

          // Remove this SnowPackFrame from the desktopPane
          mFrame.RemoveSnowPackFrame(this);
          created = false;
          dispose(); // dissolve this object
      }
      else
      {
         // Initialize SnowPackDoc and calculate some parameters
         spDoc = new SnowPackDoc(file, dataFile, mFrame, this);

         if ( spDoc.GetStationParameters())    // Get StationName, StationAltStr
           if (spDoc.GetDefaultIdParameters(IdCode))   //  Get ColorStartValue, ColorEndValue,
                // ColorTab, StartValue, EndValue, SoilStartValue, SoilEndValue
             if (spDoc.GetYAxisRange(IdCode))     // Get YMinValue, YMaxValue etc.
               if (spDoc.GetXAxisRange(IdCode))    // Get StartTime, TimeRange, TimeStep
               {
                  snowPackView = new SnowPackView(spDoc, IdCode);
                  xyPlotView = new XYPlotView(spDoc, IdCode);
                  //Schirmer added next line:
                  this.timeRangeAll();
                   //MenuFrame.statusBar.setText(""); funktioniert
                   mFrame.statusBar.setText(""); // funktioniert auch
               }
               else
               {
                  MessageBox mBox = new MessageBox(mFrame,
                    "Error", "Just one data record in file!",
                   getInputFile().getPath());
                  mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                  mBox.setVisible(true);
               }

         setBackground(spDoc.m_Background); // Background of SnowPackFrame
         created = true;
         jbInit();

         if ((!dataFile.m_SoilDataExist) && Setup.m_SoilDataDisplay && IdCode >= 500)
         // Treatment for IdCode < 500 could also be done here (no soil temperatures)
         {     MessageBox mBox = new MessageBox(mFrame,
               "Note", "No soil data in file",
                getInputFile().getPath() + "!");
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);
         }

         /*  Error message: Methode addWindowListener nicht gefunden??
         enableEvents(AWTEvent.WINDOW_EVENT_MASK);
         this.addWindowListener(new java.awt.event.WindowAdapter()
         {

         public void windowClosing(WindowEvent e)
         {
           System.out.println("activated");
         }
         });
         */

      }
    }
    catch (Exception e)
    {
      e.printStackTrace();

      MessageBox mBox = new MessageBox(mFrame,
                "Error", e + "!",
                getInputFile().getPath());
                mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
                mBox.setVisible(true);

          // Remove this SnowPackFrame from the desktopPane
          mFrame.RemoveSnowPackFrame(this);
          created = false;
          dispose(); // dissolve this object
    }

  }

/* Does not work.
      addWindowListener(new java.awt.event.WindowListener()
      {
         public void windowActivated(WindowEvent we)
         {
             System.out.println("funktioniert");
             return;
         }

         public void keyTyped(KeyEvent ke) {}
         public void windowClosed(WindowEvent we) {}
         public void windowClosing(WindowEvent we) {}
         public void windowDeactivated(WindowEvent we) {}
         public void windowDeiconified(WindowEvent we) {}
         public void windowIconified(WindowEvent we) {}
         public void windowOpened(WindowEvent we) {}
      });
*/

/*// Various times executed??
  protected void paintComponent(Graphics g)
  // for a Swing component, "paintComponent" instead of paint is needed
  {
    // Get default paint behaviour
    // important, without this DrawBackground etc. does not work
    super.paintComponent(g);
    ((SnowPackView) snowPackView).DrawSnowPackView(snowPackView.getGraphics());
    ((XYPlotView) xyPlotView).DrawXYPlot(xyPlotView.getGraphics());

    //DrawSnowPackView(g);
  }
*/

  void RedrawFrame()
  // Alternative to calling repaint, without redrawing the whole background
  {
     ((SnowPackView) snowPackView).DrawSnowPackView(snowPackView.getGraphics());
     ((XYPlotView) xyPlotView).DrawXYPlot(xyPlotView.getGraphics());
  }


  SnowPackView getSnowPackView() { return snowPackView; }


  public void jbInit() throws Exception
  {
    this.setClosable(true);
    this.setIconifiable(true);
    this.setBorder(null);
    this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter()
    {

      public void internalFrameClosing(InternalFrameEvent e)
      {
        this_internalFrameClosing(e);
      }

      public void internalFrameDeactivated(InternalFrameEvent e)
      {
        this_internalFrameDeactivated(e);
      }

      public void internalFrameActivated(InternalFrameEvent e)
      {
        this_internalFrameActivated(e);
      }

    });
    this.setMaximizable(true);
    this.setResizable(true);
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setLayout(paneLayout1);
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setLayout(paneLayout2);
    this.getContentPane().add(splitPanel1, BorderLayout.CENTER);
    splitPanel1.add(jPanel1, new PaneConstraints("jPanel1", "jPanel1", PaneConstraints.ROOT, 0.5f));
    splitPanel1.add(jPanel2, new PaneConstraints("jPanel2", "jPanel1", PaneConstraints.RIGHT, 0.25f));
    jPanel1.add(snowPackView, new PaneConstraints("jPanel3", "jPanel3", PaneConstraints.ROOT, 0.5f));
    jPanel2.add(xyPlotView, new PaneConstraints("jPanel5", "jPanel5", PaneConstraints.ROOT, 0.5f));
  }


  void adjustSplitPaneDivider(float dividerLoc)
  // sets the divider between the left and the right side graph
  // if dividerLoc = 0.25 --> right graph covers 25% of the split pane
  {
       splitPanel1.remove(jPanel2);
       splitPanel1.add(jPanel2, new PaneConstraints(
                   "jPanel2", "jPanel1", PaneConstraints.RIGHT, dividerLoc));
  }


/*
  // protected void paintComponent(Graphics g) { super.paintComponent(g); }

  // protected void repaint()... -> Fehler: kann repaint() in Component nicht
  //mit schwaecheren Zugriffsrechten ueberschreiben
  void repaintSnowPackFrame()
  {
    int IdCode = ((SnowPackView) snowPackView).m_IdCode;
    snowPackView.DrawSnowpackGraph(snowPackView.getGraphics(), IdCode, false);
    xyPlotView.repaint();
  }
*/

  public void zoomIn()
  {
     //RepaintManager.currentManager(this);
     //setDoubleBufferingEnabled(false);
     spDoc.OnZoomIn();
     //snowPackView.repaint()
     //xyPlotView.repaint();
     repaint();
     //repaintSnowPackFrame(); // marker works correctly, but former graph remains
  }


  public void zoomOut()
  {
     spDoc.OnZoomOut();
     repaint();
  }


  public void moveHigher()
  {
     spDoc.OnMoveHigher();
     repaint();
  }


  public void moveLower()
  {
     spDoc.OnMoveLower();
     repaint();
  }


  public void setYAxis(float YMinValue, float YMaxValue, int yNrOfGrids)
  {
     spDoc.OnSetYAxis(YMinValue, YMaxValue, yNrOfGrids);
     repaint();
  }


  public void setXAxis(float XMinValue, float XMaxValue, int xNrOfGrids)
  {
     // Set start and end value of x-axis (for soil and non-soil display)
     spDoc.OnSetXAxis(XMinValue, XMaxValue, xNrOfGrids);
     repaint();
  }


  public float getStartValue() { return spDoc.m_StartValue; }

  public float getEndValue() { return spDoc.m_EndValue; }

  public int getColorTab() { return spDoc.m_ColorTab; }


  public void setBackGrnd(String colorStr)
  {
     spDoc.OnSetBackGrnd(colorStr);
     setBackground(spDoc.m_Background);
     repaint();
  }


  public void setColorParameters(float StartValue, float EndValue, int ColorTab)
  {
     spDoc.OnSetColorParameters(StartValue, EndValue, ColorTab);
     repaint();
  }


  public void changeId(int IdCode, MenuFrame mFrame)
  // Change of the IdCode of the currently active SnowPackFrame
  // No switch from IdCode < 500 to IdCode > 500 is allowed.
  {
       int IdCode_old = ((SnowPackView) snowPackView).m_IdCode;

       this.IdCode = IdCode;
       ((SnowPackView) snowPackView).m_IdCode = IdCode;
       ((XYPlotView) xyPlotView).m_IdCode = IdCode; // Compiler-error if no casting!

       if (spDoc.GetDefaultIdParameters(IdCode)   //  Get ColorStartValue, ColorEndValue,
                          // ColorTab, StartValue, EndValue, SoilStartValue, SoilEndValue
        && dataFile.CalculateMinMaxValue(IdCode)) // data for IdCode exist
       {
          // don't change y-axis settings if IdCode is switched within *.pro-file
          if (IdCode < 500) spDoc.GetYAxisRange(IdCode); // Get YMinValue, YMaxValue etc.

          spDoc.m_xNrOfGrids = 4;
          repaint();
       }
       else  // reached because CalculateMinMaxValue = false
       {   MessageBox mBox = new MessageBox(mFrame,
                        "Error", "No (correct) data for selected parameter in file",
                        getInputFile().getPath() + "!");
           mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
           mBox.setVisible(true);

           // repaint original plot (could be done just by repainting message box area?)
           this.IdCode = IdCode_old;
           ((SnowPackView) snowPackView).m_IdCode = IdCode_old;
           ((XYPlotView) xyPlotView).m_IdCode = IdCode_old;

           if (spDoc.GetDefaultIdParameters(IdCode_old)   //  Get ColorStartValue, ColorEndValue,
                               // ColorTab, StartValue, EndValue, SoilStartValue, SoilEndValue
           && spDoc.GetYAxisRange(IdCode_old)    // Get YMinValue, YMaxValue etc.
           && dataFile.CalculateMinMaxValue(IdCode_old)) // data for IdCode exist
           {
             spDoc.m_xNrOfGrids = 4;
             repaint();
           }
       }

       // Write the changed parameter to the adequate item of the Windows menu
       String Id = spDoc.GetAxisText(this.IdCode);
       mFrame.AdjustWindowsMenu(this, file, Id, 0);
  }

/*
  public void changeId(int IdCode, MenuFrame mFrame)
  // Change of the IdCode of the currently active SnowPackFrame
  // No switch from IdCode < 500 to IdCode > 500 is allowed.
  {
       int IdCode_old = ((SnowPackView) snowPackView).m_IdCode;

       this.IdCode = IdCode;
       ((SnowPackView) snowPackView).m_IdCode = IdCode;
       ((XYPlotView) xyPlotView).m_IdCode = IdCode; // Compiler-error if no casting!

       if (spDoc.GetDefaultIdParameters(IdCode)   //  Get ColorStartValue, ColorEndValue,
                          // ColorTab, StartValue, EndValue, SoilStartValue, SoilEndValue
        && dataFile.CalculateMinMaxValue(IdCode) // data for IdCode exist
        && spDoc.GetYAxisRange(IdCode))          // Get YMinValue, YMaxValue etc.
       {
          spDoc.m_xNrOfGrids = 4;
          repaint();
       }
       else  // reached because CalculateMinMaxValue = false
       {   MessageBox mBox = new MessageBox(mFrame,
                        "Error", "No (correct) data for selected parameter in file",
                        getInputFile().getPath() + "!");
           mBox.setLocation(mFrame.DialogCorner(mBox, mFrame));
           mBox.setVisible(true);

           // repaint original plot (could be done just by repainting message box area?)
           this.IdCode = IdCode_old;
           ((SnowPackView) snowPackView).m_IdCode = IdCode_old;
           ((XYPlotView) xyPlotView).m_IdCode = IdCode_old;

           if (spDoc.GetDefaultIdParameters(IdCode_old)   //  Get ColorStartValue, ColorEndValue,
                               // ColorTab, StartValue, EndValue, SoilStartValue, SoilEndValue
           && spDoc.GetYAxisRange(IdCode_old)    // Get YMinValue, YMaxValue etc.
           && dataFile.CalculateMinMaxValue(IdCode_old)) // data for IdCode exist
           {
             spDoc.m_xNrOfGrids = 4;
             repaint();
           }
       }

       // Write the changed parameter to the adequate item of the Windows menu
       String Id = spDoc.GetAxisText(this.IdCode);
       mFrame.AdjustWindowsMenu(this, file, Id, 0);
  }
*/

  public void timeRangeSet(int TimeRange)
  {
     // spDoc.CalculateTimeRange() (called by OnTimeRange..()) needs
     //   dataFile.m_ActDataEntry at marker position to work correctly

     dataFile.m_ActDataEntry = (DataEntry) spDoc.m_MarkerDataEntry;
     // Cloning would be safer!!

     if (TimeRange == 3)
        spDoc.OnTimeRange3();
     else if (TimeRange == 7)
        spDoc.OnTimeRange7();
     else if (TimeRange == 14)
        spDoc.OnTimeRange14();
     else if (TimeRange == 30)
        spDoc.OnTimeRange30();
     else if (TimeRange == 60)
        spDoc.OnTimeRange60();

     // spDoc.GetXAxisRange() sets the time range, using marker position as
     //   time range end time --> marker has to be set to the m_ActEndTime
     //   determined by the first call to SetTimeRange (from CalculateTimeRange())
     if (!dataFile.m_ActEndTime.equals(spDoc.m_MarkerDataEntry.GetTime()))
     {
        // Move to end of time range
        spDoc.OnMarkerForward("End");
        spDoc.m_MarkerDataEntry = null;

        // Correction because in GetXAxisRange() time range start/end is rounded upward.
        spDoc.OnMarkerBackward("LeftArrow");
        spDoc.m_MarkerDataEntry = null;
     }

     // Repainting
     int IdCode = ((SnowPackView) snowPackView).m_IdCode;
     try {
        if(spDoc.GetXAxisRange(IdCode)) repaint(); // readjust TimeRange, TimeStep
     }
     catch (Exception e) { e.printStackTrace(); }

     // Saving some key parameters of DataFile in SnowPackDoc
     spDoc.SaveDataFile();
  }


  public void timeRangeAll()
  {
     spDoc.OnTimeRangeAll();

     int IdCode = ((SnowPackView) snowPackView).m_IdCode;
     try{ if(spDoc.GetXAxisRange(IdCode)) repaint(); }
     catch (Exception e) { e.printStackTrace(); }

     // Saving some key parameters of DataFile in SnowPackDoc
     spDoc.SaveDataFile();
  }


  public void timeRange(GregorianCalendar StartTime, GregorianCalendar EndTime)
  {
     spDoc.OnTimeRange(StartTime, EndTime);
     spDoc.m_MarkerDataEntry = null;

     int IdCode = ((SnowPackView) snowPackView).m_IdCode;
     try{ if(spDoc.GetXAxisRange(IdCode)) repaint(); }
     catch (Exception e) { e.printStackTrace(); }

     // Saving some key parameters of DataFile in SnowPackDoc
     spDoc.SaveDataFile();
  }


  public void marker(String type)
  // Reaction to marker movements by buttons or keys
  // type = "Home", "End", "Left" or "Right"
  {
     boolean repaintSPView = true;
          if (type == "Home")  repaintSPView = spDoc.OnMarkerBackward("Home");
     else if (type == "End")   repaintSPView = spDoc.OnMarkerForward("End");
     else if (type == "Left")  repaintSPView = spDoc.OnMarkerBackward("LeftArrow");
     else if (type == "Right") repaintSPView = spDoc.OnMarkerForward("RightArrow");
     else System.out.println("SnowPackFrame, marker(): unknown argument");

     int IdCode = ((SnowPackView) snowPackView).m_IdCode;

     if (repaintSPView)
     {
       try{
            if(spDoc.GetXAxisRange(IdCode))
            {
               spDoc.SaveDataFile();
               repaint();
               if ((type.equals("Home")) || (type.equals("Left")))
               {
                 marker("Home");  // move marker to start of time range
                 spDoc.SaveDataFile();
               }
            }
       }
       catch (Exception e) { e.printStackTrace(); }
     }
     else
     {
       //OnMarkerBackward/Forward runs without setting m_MarkerDataEntry = null.
       //So, even after its first run (ActDataEntry is set to start of time range),
       //DrawSnowPackGraph can remove the marker from the former position.
       snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode); // Remove old marker

            if (type == "Home")  spDoc.OnMarkerBackward("Home");
       else if (type == "End")   spDoc.OnMarkerForward("End");
       else if (type == "Left")  spDoc.OnMarkerBackward("LeftArrow");
       else if (type == "Right") spDoc.OnMarkerForward("RightArrow");

       spDoc.m_MarkerDataEntry = null;
       spDoc.SaveDataFile();
       snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode); // Add new marker
       ((XYPlotView) xyPlotView).DrawXYPlot(xyPlotView.getGraphics());
     }
  }


  public void SliderMovement(int value)
  // Reaction to the manual dragging of the slider marker.
  // Called by BoundedChangeListener after slider was moved.
  // value: new slider value (= x-coord. of screen pixel)
  // Active data entry is set acording to new slider setting, right-hand graph
  // is updated.
  // The used algorithm is similar to Animation().
  {
     // System.out.println(value);

     // Remove marker from current position
     int IdCode = ((SnowPackView) snowPackView).m_IdCode;
     snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode);

     Graph graph = new Graph(spDoc);
     if ( spDoc == null ) return;
     snowPackView.PrepareGraph(graph);

     GregorianCalendar xTime;
     GregorianCalendar ActDataTimeClone, TimeRangeStartTime, TimeRangeEndTime;

     // Get currently active time
     spDoc.m_MarkerDataEntry = null;
     spDoc.GetMarkerValue(IdCode);
     xTime = spDoc.m_ActTime;

     // Substract one time step from active data time
     ActDataTimeClone = dataFile.GetActDataTime();
     ActDataTimeClone.add(Calendar.SECOND, -spDoc.m_TimeStep);
     TimeRangeStartTime = dataFile.GetTimeRangeStartTime();

     // OnMarkerBackward() just called if marker is not yet at the start of the
     // time range. Otherwise it will be moved to the previous time range.
     if (!ActDataTimeClone.before(TimeRangeStartTime))
     {
       // If not already at start of time range, move there.
       spDoc.OnMarkerBackward("Home");
       spDoc.m_MarkerDataEntry = null;
       spDoc.GetMarkerValue(IdCode);
       xTime = spDoc.m_ActTime;
     }

     // Add one time step to active data time (for while-comparison).
     // Because of this, the following OnForwardMarker() statement is only
     // executed, if after the statement the marker is still left of
     // the slider value position.
     ActDataTimeClone = dataFile.GetActDataTime();
     ActDataTimeClone.add(Calendar.SECOND, spDoc.m_TimeStep);

     // Gradually move towards end of time range, till marker is reached.
     while( graph.CalcXPosLP(ActDataTimeClone) < value )
     {
       // Add one time step to active data time
       ActDataTimeClone = dataFile.GetActDataTime();
       ActDataTimeClone.add(Calendar.SECOND, spDoc.m_TimeStep);
       TimeRangeEndTime = dataFile.GetTimeRangeEndTime();

       // Current time plus one time step must not be after end of time range.
       // Otherwise, OnMarkerForward() would move to next time interval.
       if (!ActDataTimeClone.after(TimeRangeEndTime))
       {
         // Move to next data record
         spDoc.OnMarkerForward("RightArrow");
         spDoc.m_MarkerDataEntry = null; // Next two statements: spDoc.m_ActDataEntry = dataFile.m_ActDataEntry
         spDoc.GetMarkerValue(IdCode);   // --> xTime correctly calculated

         xTime = spDoc.m_ActTime;

         // Add one time step to active data time (for while-comparison).
         ActDataTimeClone = dataFile.GetActDataTime();
         ActDataTimeClone.add(Calendar.SECOND, spDoc.m_TimeStep);
       }
       else
       // Exit loop if already at end of time range
       {
          break;
       }
     }

     // Following lines shall put slider marker exactly to the left end of
     // the column displaying the data record. But does not work. Why??
     spDoc.activeSliderMovement = false;
     spDoc.jSlider.setValue(graph.CalcXPosLP(xTime));
     //System.out.println("Slider value set: " + graph.CalcXPosLP(xTime));
     spDoc.activeSliderMovement = true;

     // Draw the marker
     snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode);
     ((XYPlotView) xyPlotView).DrawXYPlot(xyPlotView.getGraphics());
  }


  public void Animation()
  // Automated display of a sequence of data records, starting at
  // spDoc.m_AnimationStartTime and ending at spDoc.m_AnimationEndTime.
  // The animation speed is steered by Setup.m_animationSpeed.
  {
     int speed = Setup.m_animationSpeed; // values 1 to 5
     int delay = 0; // Delay time in ms
     if      (speed == 1) delay = 1000;
     else if (speed == 2) delay = 500;
     else if (speed == 3) delay = 200;
     else if (speed == 4) delay = 100;
     else if (speed == 5) delay = 50;

     Thread t = Thread.currentThread();

     // Remove marker from current position
     int IdCode = ((SnowPackView) snowPackView).m_IdCode;
     snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode);

     Graph graph = new Graph(spDoc);
     if ( spDoc == null ) return;
     snowPackView.PrepareGraph(graph);

     GregorianCalendar xTime;
     GregorianCalendar ActDataTimeClone, TimeRangeStartTime, TimeRangeEndTime;

     // Get currently active time
     spDoc.m_MarkerDataEntry = null;
     spDoc.GetMarkerValue(IdCode);
     xTime = spDoc.m_ActTime;

     // Substract one time step from active data time
     ActDataTimeClone = dataFile.GetActDataTime();
     ActDataTimeClone.add(Calendar.SECOND, -spDoc.m_TimeStep);
     TimeRangeStartTime = dataFile.GetTimeRangeStartTime();

     // OnMarkerBackward() just called if marker is not yet at the start of the
     // time range. Otherwise it will be moved to the previous time range.
     if (!ActDataTimeClone.before(TimeRangeStartTime))
     {
       // If not already at start of time range, move there.
       spDoc.OnMarkerBackward("Home");
       spDoc.m_MarkerDataEntry = null;
       spDoc.GetMarkerValue(IdCode);
       xTime = spDoc.m_ActTime;
     }

     // Gradually move towards end of animation time range
     while( !(xTime.after(spDoc.m_AnimationEndTime)))
     {

       if ( !(xTime.before(spDoc.m_AnimationStartTime)))
       {
         // Set slider value
         spDoc.activeSliderMovement = false;
         spDoc.jSlider.setValue(graph.CalcXPosLP(xTime));
         spDoc.activeSliderMovement = true;

         // Draw marker
         snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode);
         ((XYPlotView) xyPlotView).DrawXYPlot(xyPlotView.getGraphics());

         // Pause
         try {
           Thread.sleep(delay);
         }
         catch (InterruptedException e)
         {
           System.out.println("SnowPackView, Animation(): problems with Thread.sleep");
         }

         // Remove marker
         snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode);
       }

       // Add one time step to active data time
       ActDataTimeClone = dataFile.GetActDataTime();
       ActDataTimeClone.add(Calendar.SECOND, spDoc.m_TimeStep);
       TimeRangeEndTime = dataFile.GetTimeRangeEndTime();

       // Current time plus one time step must not be after end of time range.
       // Otherwise, OnMarkerForward() would move to next time interval.
       if (!ActDataTimeClone.after(TimeRangeEndTime) &&
           !ActDataTimeClone.after(spDoc.m_AnimationEndTime))
       {
         // Move to next data record
         spDoc.OnMarkerForward("RightArrow");
         spDoc.m_MarkerDataEntry = null; // Next two statements: spDoc.m_ActDataEntry = dataFile.m_ActDataEntry
         spDoc.GetMarkerValue(IdCode);   // --> xTime correctly calculated

         xTime = spDoc.m_ActTime;
       }
       else
       // Exit loop if already at end of time range or after animation end date
       {
          break;
       }
     }

     // Redraw last marker
     snowPackView.DrawMarker(snowPackView.getGraphics(), IdCode);

     // If synchronization is on: move all markers to the end
     if (Setup.m_Synchronization)
     {
        mFrame.MarkerMovement("End", 0);
     }
  }


  public File getInputFile() {return file; }


  void this_internalFrameClosing(InternalFrameEvent e)
  // Reaction to closing the SnowPackFrame
  {
    mFrame.Close(this);
  }

  void this_internalFrameDeactivated(InternalFrameEvent e)
  // Reaction to closing the SnowPackFrame
  {
     //System.out.println("Deactivated frame: "+file.getPath());

     // Saving some key parameters of DataFile in SnowPackDoc
     if (spDoc != null)
     {
        spDoc.SaveDataFile();

        //System.out.println("spDoc.datafile_m_ActStartTimeIndex: "+spDoc.datafile_m_ActStartTimeIndex);
        //System.out.println("spDoc.datafile_m_ActEndTimeIndex: "+spDoc.datafile_m_ActEndTimeIndex);
     }
  }


  void this_internalFrameActivated(InternalFrameEvent e)
  // Reaction to activation the SnowPackFrame.
  // Maybe obsolete since RetrieveDataFile is called by paintComponent()-methods
  // of SPView and SnowPackFrame.
  {
     //System.out.println("Activated frame: "+file.getPath());

     if (spDoc.datafile_m_ActStartTime != null)
     // spDoc.SaveDataFile was not called before
     {
       // Retrieving some key DataFile parameters from SnowPackDoc
       if (spDoc != null)
       {
         spDoc.RetrieveDataFile();

         //System.out.println("spDoc.datafile_m_ActStartTimeIndex: "+spDoc.datafile_m_ActStartTimeIndex);
         //System.out.println("spDoc.datafile_m_ActEndTimeIndex: "+spDoc.datafile_m_ActEndTimeIndex);
       }

     }

  }



}
