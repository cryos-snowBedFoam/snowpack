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
// SnowPackView: Draws the SnowPack graph (left side of SnowPackFrame)
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JPanel;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;


public class SnowPackView extends JPanel implements C_DataFile
{

SnowPackDoc spDoc;

XYLayout xYLayout1 = new XYLayout();

ColorTab m_ColorTab = null;
int m_ColorTabIndex = 0;
float m_ColorTabStartValue = 0;
float m_ColorTabEndValue = 0;
int m_IdCode;

Rectangle m_leftRect; // comprises total area on left side of splitPane
Dimension leftRectSize = new Dimension();

// Construction
public SnowPackView(SnowPackDoc spDoc, int IdCode)
{
  this.spDoc = spDoc;
  this.m_IdCode = IdCode;
  setBackground(spDoc.m_Background);
  setForeground(spDoc.m_Foreground);
  this.setLayout(xYLayout1);
  this.setOpaque(true); // probably not necessary, might increase performance

}

protected void paintComponent(Graphics g)
// for a Swing component, "paintComponent" instead of paint is needed
{
  // Get default paint behaviour
  // important, without this DrawBackground etc. does not work
  super.paintComponent(g);

  // Retrieving some key DataFile parameters from SnowPackDoc if not first
  // of window (spDoc.SaveDataFile() was not called before
  if (spDoc.datafile_m_ActStartTime != null)
     spDoc.RetrieveDataFile();

/*
  if (Setup.m_SliderDisplay)
  {
    int w = 0, h = 0;
    spDoc.spframe.setBounds(0, 0, w, h); // w, h is set, needed for controls
    removeAll();
    InsertControlObjects();
    spDoc.spframe.setBounds(0, 0, w, h); // w, h is set, needed for controls
  }
*/

  DrawSnowPackView(g);
}


void DrawSnowPackView(Graphics g)
{
  // System.out.println("DrawSnowPackView, IdCode=" + m_IdCode);

  // Set background and foreground colors
  setBackground(spDoc.m_Background);
  setForeground(spDoc.m_Foreground);

  // Determine the size of the SnowPackView frame
  leftRectSize.width = this.getSize().width;
  leftRectSize.height = this.getSize().height;
  m_leftRect = new Rectangle(0, 0, leftRectSize.width, leftRectSize.height);

  if ( m_IdCode >= 500) // layer parameters
  {
     if (DrawSnowpackGraph(g, m_IdCode))
     // = color column graph, upper part of SnowPackView frame
     {
          // Explanation of used color code,
          // lower part of SnowPackView frame
          if ((m_IdCode == ID_CODE_GRAIN_CLASS) || (m_IdCode == 514))
            DrawGrainFormBar(g);
          else
            DrawColorBar(g);
     }
  }
  else // non-layer parameters
  {
     DrawTimeProfile(g); // line graph
  }

  // InsertControlObjects() could be added at this point instead of after/before
  // SetBounds(), but slider pointer sometimes not visible

  //System.out.println("after DrawSnowpackGraph");
}


void setIdCode(int IdCode) { m_IdCode = IdCode; }



boolean DrawSnowpackGraph(Graphics g, int IdCode)
// Draws the upper part of the left side graph (color columns)

// Originally this routine was designed for the display of two sort of data:
// IdCode 513... grain type data, related to layers
// IdCode 514... surface hoar data, related to interfaces (only 0,1)
// Now surface hoar data are included in IdCode 513 (related to layers);
// hoar on surface is given as additional number (appended at IdCode 513) and does not
// include thickness.
// Old code (related to IdCode 514) is marked by // old //.
{
   // Set debug to "true" to output printed values (otherwise "false")
   boolean debug = false;

   Graph graph = new Graph(spDoc);
   if ( spDoc == null ) {return false; }

   // Set foreground to original color
   // (to prevent errors due to color resetting by other methods)
   g.setColor(getForeground());
   spDoc.m_CurrentColor = getForeground();

   // Input variables are determined
   String StationName        = spDoc.m_StationName;
   String StationAltStr      = spDoc.m_StationAltStr;
   String StationAspectStr   = spDoc.m_StationAspectStr;
   String StationSlopeStr    = spDoc.m_StationSlopeStr;
   String StationLatStr      = spDoc.m_StationLatStr;
   String StationLonStr      = spDoc.m_StationLonStr;
   m_ColorTabIndex           = spDoc.m_ColorTab;
   m_ColorTabStartValue      = spDoc.m_ColorStartValue;
   m_ColorTabEndValue        = spDoc.m_ColorEndValue;
   GregorianCalendar StartTime = spDoc.m_StartTime;
   int TimeRange               = spDoc.m_TimeRange1;
   int TimeStep                = spDoc.m_TimeStep;
   int xNrOfGrids              = spDoc.m_TimeGrid;   // initialized in spDoc
   int yNrOfGrids              = spDoc.m_yNrOfGrids; // initialized in spDoc
   float YMinValue             = spDoc.m_YMinValue;  // initialized in spDoc
   float YMaxValue             = spDoc.m_YMaxValue;  // initialized in spDoc
   int NrOfXValues; // calculated below; times with measurements
   // old // boolean hoarPaint = false;

   // System.out.println("YMinValue="+YMinValue);

   if ( TimeStep != 0 && TimeRange != 0 )
      NrOfXValues = TimeRange / TimeStep;
   else
      return false;

   if ( NrOfXValues <= 0 ) // Time Range < TimeStep
      NrOfXValues = 1;

   // Set graph size to default values
   Rectangle ClientRect = new Rectangle(
     m_leftRect.x, m_leftRect.y, m_leftRect.width, m_leftRect.height * 4 / 5 );
   graph.SetClientAreaDP(g, ClientRect, 7, 3, 4, 1);

   // Set x and y axis
   graph.SetXAxis( StartTime, TimeRange, xNrOfGrids );
   graph.SetYAxis( YMinValue, YMaxValue, yNrOfGrids );

   // Calculate coordinate system
   graph.CalcCoordinateSystem();
   graph.DrawCoordinateAxes(g);
   graph.DrawGrids(g, true);

   // Draw x axis text
   graph.DrawXAxisDateText(g, xNrOfGrids, "FULL_DATE");

   // Draw y axis text
   graph.DrawYAxisText(g, yNrOfGrids, 1, "ALIGN_LEFT", "ALIGN_TOP",
                       spDoc.GetAxisText(ID_CODE_LAYER_HEIGHT),
                       spDoc.GetAxisUnit(ID_CODE_LAYER_HEIGHT));
   // Draw title
   graph.DrawAboveBelowText(g, 4, "ALIGN_CENTER", "BOLD", 11,
     StationName + ", " + StationAltStr + " m", false);

   if ((Setup.m_ResearchMode) && (!StationName.equals("Undefined Station")))
   {
      // Draw slope angle
      if (!StationSlopeStr.equals(""))
        graph.DrawAboveBelowText(g, 4, "ALIGN_LEFT", "PLAIN", 10,
          "Slope = " + StationSlopeStr + " deg", false);

      // Draw aspect
      if (!StationAspectStr.equals(""))
        graph.DrawAboveBelowText(g, (float) 4.8, "ALIGN_LEFT", "PLAIN", 10,
          "Aspect = " + StationAspectStr + " deg", false);

      // Draw latitude
      if (!StationLatStr.equals(""))
        graph.DrawAboveBelowText(g, 4, "ALIGN_RIGHT", "PLAIN", 10,
          "Latitude = " + StationLatStr, false);

      // Draw longitude
      if (!StationLonStr.equals(""))
        graph.DrawAboveBelowText(g, (float) 4.8, "ALIGN_RIGHT", "PLAIN", 10,
          "Longitude = " + StationLonStr, false);
   }

   // old // if(IdCode == 514) IdCode = ID_CODE_GRAIN_CLASS;
   // old // // IdCode 514 passed to method if plot with hoar data is desired;
   // old // // hoar data (0, 1) are stored in the input data under IdCode 514;
   // old // // general grain data (without hoar info) are stored under IdCode 513;


   // old // do { // first loop is standard plot; in second loop (if necessary) hoar is added

   // Draw plot with date values on x axis
   GregorianCalendar xTime;
   GregorianCalendar NextTime;
   boolean hasMoreValues, hasMoreValuesNext;
   float LayerHeight, StartLayerHeight;
   float zValue = 0;
   float NextZValue = 0;
   ProDataEntry ActDataEntry;
   ProDataEntry NextDataEntry;
   Point StartPoint = new Point();
   Point EndPoint = new Point();
   Color ActColor = new Color(0,0,0);
   Color NextColor = new Color(0,0,0);
   int NrOfLayers = 0; // number of values in line with active Idcode
   int TotalNrOfLayers = 0; // number of values in line containing layer height
   int NrOfSnowLayers = 0;
   int NrOfSoilLayers = 0;
   int StartLayer = 0; // index of start layer for drawing

   // Load Color Table
   m_ColorTab = new ColorTab();
   m_ColorTab.LoadPredefinedColorTable(
   m_ColorTabIndex, m_ColorTabStartValue, m_ColorTabEndValue);

   // Calculate first ActTime and ActDataEntry
   hasMoreValues = spDoc.GetFirstValue(IdCode);
   xTime = spDoc.m_ActTime; // just needs initial value
   NextTime = spDoc.m_ActTime;
   ActDataEntry = (ProDataEntry) spDoc.m_ActDataEntry;
   boolean lastColumnSkipped = false; // last column was drawn (no optimization)
   boolean firstBox;

   // Loop over all data entries contained in input file
   int count = 1;
   for ( int i = 0; i < NrOfXValues; i++ )

   {
      if (debug) System.out.println("================= Start Column " + i + " ======================");

     /* //schirmer: for debugging
     GregorianCalendar searchdate = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
     searchdate.set(2008, 10-1, 12, 12, 0, 0);
     searchdate.set(Calendar.MILLISECOND, 0);
     //System.out.println(searchdate.getTime().toString());
     //System.out.println(NextTime.getTime().toString());
     //System.out.println(NextTime.getTime().compareTo(searchdate.getTime()));

     if (NextTime.getTime().compareTo(searchdate.getTime())==0){
       System.out.println(searchdate.getTime().toString());
     }
     //end schirmer*/



      firstBox = true;

      // Calculate width of plot on x axis according to time span
      if (!lastColumnSkipped) xTime = (GregorianCalendar) NextTime.clone();

      // Get next time
      hasMoreValuesNext = spDoc.GetNextValue(IdCode);
      NextTime = spDoc.m_ActTime;
      NextDataEntry = (ProDataEntry) spDoc.m_ActDataEntry;


      GregorianCalendar drawTime = (GregorianCalendar) xTime.clone();
      if (!lastColumnSkipped) {
        drawTime.add(Calendar.SECOND, TimeStep);
        count = 1;
      }
      else {
        count++;
        drawTime.add(Calendar.SECOND, count*TimeStep);
      }



      if ( !hasMoreValuesNext ) // Calculate end time for last entry
      {
         GregorianCalendar xTimeClone = (GregorianCalendar) xTime.clone();
                           xTimeClone.add(Calendar.SECOND, TimeStep);
         GregorianCalendar EndTimeClone = (GregorianCalendar) StartTime.clone();
                           EndTimeClone.add(Calendar.SECOND, TimeRange);

         if ( xTimeClone.before(EndTimeClone))
            NextTime = xTimeClone;
         else
            NextTime = EndTimeClone;
         NextDataEntry = null;
      }

      if ( hasMoreValues )  // Current Data Available
      {

       StartPoint.x = graph.CalcXPosLP(xTime); // Rectangle drawn right of time to which it refers
       EndPoint.x = graph.CalcXPosLP(drawTime);
       //System.out.println(graph.CalcXPosLP(drawTime));



       // Optimization: don't draw if time step is too small
       if (EndPoint.x - StartPoint.x >= 1)
       {

        // draw z values
        TotalNrOfLayers = ActDataEntry.GetTotalNrOfLayers();
        NrOfSnowLayers = ActDataEntry.GetNrOfSnowLayers();
        NrOfSoilLayers = TotalNrOfLayers - NrOfSnowLayers;
        NrOfLayers = ActDataEntry.GetNrOfLayers(IdCode);

        // ID_CODE_GRAIN_CLASS: one additional layer on top contains surface hoar info
        // (existent if value > 0)
        if ((IdCode == ID_CODE_GRAIN_CLASS) && (NrOfLayers > 0)) NrOfLayers--;

        if (Setup.m_SoilDataDisplay || (TotalNrOfLayers != NrOfLayers))
          // Display snow layers plus soil layers (if available and demanded)
          StartLayer = 0;
        else
          // Display only snow layers
          StartLayer = NrOfSoilLayers; // can also be 0

        if (ActDataEntry.SoilDataExist(IdCode) && Setup.m_SoilDataDisplay)
           StartLayerHeight = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, 0); // lowest soil node
        else
           StartLayerHeight = 0; // soil/snow interface

        StartPoint.y = graph.CalcYPosLP(StartLayerHeight);

        if (( NrOfLayers > 0 ) && (Setup.m_SoilDataDisplay || (NrOfSnowLayers > 0)))
        // if no soil data display, snow layers must exist (otherwise error reading
        // z-data)
        {
         // Get following values for j = 0
         NextZValue = ActDataEntry.GetZData(IdCode, StartLayer);
         if (IdCode == ID_CODE_GRAIN_CLASS)
           NextColor = GrainForm.GetColor((int) NextZValue);
         else
           NextColor = m_ColorTab.GetColor(NextZValue, m_ColorTabIndex);
        }

        if (debug) System.out.println("StartPoint.x, EndPoint.x: " + StartPoint.x + " " + EndPoint.x);

        for ( int j = StartLayer; j < NrOfLayers; j++ )
        {
         // Get color for plot
         zValue = NextZValue;
         ActColor = NextColor;

         // Optimization: check if next colors are almost identical, then
         // draw with same brush. Changing brushes is time intensive.
         while ( j + 1 < NrOfLayers )
         {
            NextZValue = ActDataEntry.GetZData(IdCode, j+1);
            // old // if (hoarPaint) break; // only NextZValue needed for hoar painting

            if (IdCode == ID_CODE_GRAIN_CLASS)
            {
              NextColor = GrainForm.GetColor((int) NextZValue);

              // Refrozen layers never skipped
              if (((zValue % 10) == 2) || ((NextZValue % 10) == 2)) break;
            }
            else
              NextColor = m_ColorTab.GetColor(NextZValue, m_ColorTabIndex);

            if ( Math.abs( ActColor.getRed()   - NextColor.getRed()   ) < 8 &&
                 Math.abs( ActColor.getGreen() - NextColor.getGreen() ) < 8 &&
                 Math.abs( ActColor.getBlue()  - NextColor.getBlue()  ) < 8 )
            {
               // next color is almost identical
               // draw next value(s) with same color

               if (debug) System.out.println("i="+i+": layer "+j+" skipped; " +
                    "z = "+zValue+", Next z = " + NextZValue);
               j++;
            }
            else
            {
               // next color is different
               break;
            }
         } // while


         // Get layer height
         if (TotalNrOfLayers == NrOfSnowLayers)
            // ID_CODE_LAYER_HEIGHT does not contain soil heights
            // --> also other IDCodes do not contain soil data
            LayerHeight = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j);

         else if (NrOfLayers == NrOfSnowLayers)
            // ID_CODE_LAYER_HEIGHT does not contain soil heights
            // 1 added because 1st layer is lowest soil layer
            LayerHeight = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j + 1 + NrOfSoilLayers);

         else
            LayerHeight = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j + 1);

         if ( LayerHeight > YMaxValue ) // don't draw beyond maximum axis value
         {
            LayerHeight = YMaxValue; // Clipping on top
            j = NrOfLayers; // in order to finish for(j) loop after drawing
         }

         // Variable firstBox used to prevent errors in plotting for cases where
         // YMinValue > 0 and all layers from 0 up to >YminValue are skipped by
         // the optimization code (in this case, columns below YMinValue would be drawn)
         if ( firstBox && (LayerHeight > YMinValue) && (StartLayerHeight < YMinValue))
         {
            StartPoint.y = graph.CalcYPosLP(YMinValue);
         }
         firstBox = false;

         if ( LayerHeight < YMinValue ) // don't draw beyond minimum axis value
         {
            LayerHeight = YMinValue; // Clipping on bottom
            // Clipping works in next for(j) loop
         }

         EndPoint.y = graph.CalcYPosLP(LayerHeight);


         if ( LayerHeight > YMinValue )
         {
             /* // old //
             if ((IdCode == 514) && (zValue > 0))
             // Draw hoar (if available) at interface between layers
             // (thickness of line: 1 pixel)
             {
                g.setColor(GrainForm.GetColor(660)); // hoar color
                g.fillRect(StartPoint.x, EndPoint.y, EndPoint.x - StartPoint.x, 1);
             }
             else if (IdCode != 514) // standard plot // old // */

             if (IdCode != 514) // standard plot
             {

                // Draw rectangle, using g.fillRect(left, top, width, height)
                g.setColor(ActColor);

                if ((IdCode == ID_CODE_GRAIN_CLASS) && (StartPoint.y - EndPoint.y < 1))
                // draw grain type anyway at least at the height of one pixel
                     g.fillRect(StartPoint.x, EndPoint.y,
                                EndPoint.x - StartPoint.x, 1);
                else
                // all other cases
                     g.fillRect(StartPoint.x, EndPoint.y,
                        EndPoint.x - StartPoint.x, StartPoint.y - EndPoint.y);




                // Grain class painted, last digit of code indicates melted
                // and refrozen grains --> vertical lines in cyan across box
                if ((IdCode == ID_CODE_GRAIN_CLASS) && (zValue % 10 == 2))
                {
                   g.setColor(Color.cyan);
                   int StartPointX = StartPoint.x + 1;
                   // not start at first pixel line --> less lines
                   // + 2 is possible, but no lines at all will be displayed
                   //    if data of a whole year are shown on half a screen
                   while(StartPointX < EndPoint.x)
                   {
                      g.drawLine(StartPointX, StartPoint.y,
                                 StartPointX, EndPoint.y);
                      StartPointX += 5; // vertical line every 5th pixel
                   }
                }

                // Highest layer: also draw surface hoar if available
                // >>>MS TODO: InfoBox haben keinen Reif-Layer
                if ((IdCode == ID_CODE_GRAIN_CLASS)
                   && (j == NrOfLayers - 1)
                   && (ActDataEntry.GetZData(IdCode, j+1) > 0))
                {
                  g.setColor(GrainForm.GetColor(660)); // hoar color
                  g.fillRect(StartPoint.x, EndPoint.y-1, EndPoint.x - StartPoint.x, 2);
                }

                // Reset color
                g.setColor(ActColor);
             }
         }

         if (debug)
         {
           System.out.println("**** Layer j = "+j+": height = " + LayerHeight);
           System.out.println("     StartPoint.y, EndPoint.y: "+
                     StartPoint.y + " " + EndPoint.y + " z = " + zValue);
           System.out.println("RGB: " + ActColor.getRed() + " " +
                  ActColor.getGreen() + " " + ActColor.getBlue());
         }

         StartPoint.y = EndPoint.y;

        } // for j

        lastColumnSkipped = false;

       } // end if (EndPoint.x - StartPoint.x > 1)
       else
       {
         lastColumnSkipped = true;
       }


      } // end if hasMoreValues

      if (!lastColumnSkipped)
      {
        ActDataEntry = NextDataEntry;
        hasMoreValues = hasMoreValuesNext;
      }


   } // for i



   // Set foreground to original color
   g.setColor(getForeground());

   if ((TotalNrOfLayers != NrOfSnowLayers) && (NrOfLayers == TotalNrOfLayers)
   // =if (ActDataEntry.SoilDataExist()), but ActDataEntry = null here
     &&(Setup.m_SoilDataDisplay))
   {
           graph.DrawGroundSurface(g);

           graph.DrawYAxisText(g, yNrOfGrids, 1, "ALIGN_LEFT", "ALIGN_BOTTOM",
                       "Soil", spDoc.GetAxisUnit(ID_CODE_LAYER_HEIGHT));
   }

   /* // old //
   if (IdCode == 514)
   // method launched with IdCode 513; another loop with IdCode 514 to paint
   // the hoar was already processed
   {

      break;
   }

   if (IdCode == ID_CODE_GRAIN_CLASS)
   {
      if (IdCode != m_IdCode)
      {  // method was launched with IdCode = 514 (grain forms, hoar to be painted);
         //   above (before the proper painting) IdCode was set to 513
         // another loop with IdCode=514 (containing hoar data) to paint hoar
         hoarPaint = true;
         IdCode = 514;
         System.out.println("SnowPackView: 2nd run necessary (hoar paint)");
      }
      else
      {  // method was launched with IdCode = 513 = ID_CODE_GRAIN_CLASS (no hoar)
         hoarPaint = false;
      }
   }

   } while (hoarPaint);
   */ // old //

   DrawMarker(g, IdCode);

   return true;
}


boolean DrawColorBar(Graphics g)
// Draws the lower part of the left side graph if layer parameters are displayed;
// Color scale is drawn (all parameters beside grain form)
{
   Graph ColorBarGraph = new Graph(spDoc);

   Rectangle ClientRect = new Rectangle(
       m_leftRect.x, m_leftRect.y + m_leftRect.height * 13 / 15, // = top
       m_leftRect.width, m_leftRect.height * 2 / 15);

   // Characters to the left, right, bottom, top
   ColorBarGraph.SetClientAreaDP(g, ClientRect, 7, 1, 3, 0 );

   if ( m_ColorTab == null ) return false;

   int xNrOfGrids;  // one bar section per color range:
   xNrOfGrids = m_ColorTab.NrOfColors[m_ColorTabIndex] - 1;
   while ( xNrOfGrids < 5 )  {xNrOfGrids *= 2;}

   ColorBarGraph.SetXAxis( m_ColorTabStartValue, m_ColorTabEndValue, xNrOfGrids );

   // Set y axis range (YStart+EndValue, divisions)
   ColorBarGraph.SetYAxis( 0, 10, 1 );

   // Calculate and draw coordinate system
   ColorBarGraph.CalcCoordinateSystem();
   ColorBarGraph.DrawCoordinateAxes(g);

   //g.setColor(Color.red);
   ColorBarGraph.DrawGrids(g, false);
   g.setColor(getForeground());

   // Draw x axis text (numbers expressing color bar range)
   ColorBarGraph.DrawXAxisText(g, 1, xNrOfGrids, 1);

   // Draw parameter name and unit
   ColorBarGraph.DrawAboveBelowText(g, 2, "ALIGN_CENTER", "PLAIN", 10,
          spDoc.GetAxisText(m_IdCode) + " (" +
          spDoc.GetAxisUnit(m_IdCode) + ")", false);

   Color ActColor = new Color(0,0,0);
   Point StartPoint = new Point();
   Point EndPoint = new Point();

   int NrOfColRects = 100; // Color bar composed of NrOfSteps color boxes
   float Offset = (m_ColorTabEndValue - m_ColorTabStartValue) / NrOfColRects;

   StartPoint.y = ColorBarGraph.CalcYPosLP(0); // y-Range 0..10, see setYAxis()
   EndPoint.y = ColorBarGraph.CalcYPosLP(10);
   StartPoint.x = ColorBarGraph.CalcXPosLP(m_ColorTabStartValue);

   float ActValue;

   // Draw rectangles composing color bar
   for ( int i = 0; i < NrOfColRects; i++ )
   {
      // Calculate width of color range on x axis
      ActValue = m_ColorTabStartValue + i * Offset;
      EndPoint.x = ColorBarGraph.CalcXPosLP(ActValue + Offset);

      // ActColor according to ActValue at middle of box
      ActColor = m_ColorTab.GetColor(ActValue + Offset / 2, m_ColorTabIndex);

      // Draw rectangles
      g.setColor(ActColor);
      if (StartPoint.y > EndPoint.y)
           g.fillRect(StartPoint.x, EndPoint.y,
                       EndPoint.x - StartPoint.x, StartPoint.y - EndPoint.y);
      else
           g.fillRect(StartPoint.x, StartPoint.y,
                       EndPoint.x - StartPoint.x, EndPoint.y - StartPoint.y);

      StartPoint.x = EndPoint.x;
   } // for i


   // Set foreground to original color
   g.setColor(getForeground());

   return true;
}




boolean DrawGrainFormBar(Graphics g)
// Draws the lower part of the left side graph for grain form display
{
   int NR_OF_ROWS = 3;
   int NR_OF_COLUMNS = 7;
   int NR_OF_GRAIN_FORMS = NR_OF_ROWS * NR_OF_COLUMNS;

   int Index;
   int GrainFormCodes[] = {
         0,   0, 230, 330, 370, 770, 880,
       110, 120, 220, 340, 470, 570, 002,
         0,   0, 240, 440, 450, 550, 660
   };

   Graph ColorBarGraph = new Graph(spDoc);

   Rectangle ClientRect = new Rectangle(
       m_leftRect.x, m_leftRect.y + m_leftRect.height * 13 / 15, // = top
       m_leftRect.width, m_leftRect.height * 2 / 15);

   // Characters to the left, right, bottom, top
   ColorBarGraph.SetClientAreaDP(g, ClientRect, 7, 1, 1, 0 );

   // Set x/y axis range
   ColorBarGraph.SetXAxis( 0, NR_OF_COLUMNS, NR_OF_COLUMNS );
   ColorBarGraph.SetYAxis( 0, NR_OF_ROWS, NR_OF_ROWS );

   ColorBarGraph.CalcCoordinateSystem();

   // Print "Grain Type:" in upper left corner of coordinate system
   g.setColor(getForeground());
   Font font = new Font("Dialog", Font.PLAIN, 10);
   g.setFont(font);
   FontMetrics fm = g.getFontMetrics();
   g.drawString("Grain Type", ColorBarGraph.m_XAxisStartPos.x,
                               ColorBarGraph.m_YAxisStartPos.y + 2); //+ fm.getAscent());

   Color ActColor = new Color(0,0,0);
   Point StartPoint = new Point(); // lower left
   Point EndPoint = new Point(); // upper right
   Point SymbolStartPoint = new Point(); // upper left!!
   Point SymbolEndPoint = new Point(); // lower right!!

   int ActValue;

   Index = 0;
   for ( float y = NR_OF_ROWS; y > 0; y-- )
   {
      StartPoint.y = ColorBarGraph.CalcYPosLP(y-1);
      EndPoint.y = ColorBarGraph.CalcYPosLP(y);

      for ( float x = 0; x < NR_OF_COLUMNS; x++ )
      {
         StartPoint.x = ColorBarGraph.CalcXPosLP(x);
         EndPoint.x   = ColorBarGraph.CalcXPosLP(x+1);

         // Calculate width of color range on x axis
         ActValue = GrainFormCodes[Index];

         if ( ActValue > 0 )
         {
            // Drawing the background color
            // (white for melted/refrozen forms, Code 002)
            ActColor = GrainForm.GetColor(ActValue);
            g.setColor(ActColor);
            g.fillRect(StartPoint.x, EndPoint.y,
                       EndPoint.x - StartPoint.x, StartPoint.y - EndPoint.y);

            // Drawing vertical cyan lines for melted/refrozen forms
            if (ActValue == 2)
            {
                g.setColor(Color.cyan);
                int StartPointX = StartPoint.x + 1;
                while(StartPointX < EndPoint.x)
                {
                   g.drawLine(StartPointX, StartPoint.y,
                              StartPointX, EndPoint.y);
                   StartPointX += 5; // vertical line every 5th pixel
                }
            }
            g.setColor(ActColor); // reset color


            // Drawing the symbol box

            // Maximum y-size of symbol box
            int SymbolMaxSize = 12;

            // Position of rectangle containing the grain form symbols
            SymbolStartPoint.y = EndPoint.y;
            SymbolEndPoint.y = SymbolStartPoint.y +
                Math.min(SymbolMaxSize, StartPoint.y - EndPoint.y);
            SymbolStartPoint.x = StartPoint.x;
            SymbolEndPoint.x = StartPoint.x +
                2 * (SymbolEndPoint.y - SymbolStartPoint.y);

            GrainForm.DrawSymbol(g, ActValue, SymbolStartPoint, SymbolEndPoint);

            // Set color to original value (necessary after DrawSymbol)
            g.setColor(getForeground());

         } // if ActValue > 0

         Index++;
      } // for x
   } // for y

   // Set foreground to original color
   g.setColor(getForeground());

   return true;
}




/////////////////////////////////////////////////////////////////////////////
// Draw plot:
//   x-axis: time
//   y-axis: single parameter
void DrawTimeProfile(Graphics g)
{
   Graph graph = new Graph(spDoc);
   if ( spDoc == null ) return;

   // Set foreground to original color
   // (to prevent errors due to color resetting by other methods)
   g.setColor(getForeground());
   spDoc.m_CurrentColor = getForeground();

   // Extraction of information about with which parameter the current parameter
   // m_IdCode should be printed together
   spDoc.AssociatedIdCodes(m_IdCode);
   int NrOfParameters = spDoc.NrOfParameters; // Number of parameters to be plotted
   int IdCode[] = new int[NrOfParameters]; // IdCodes to be plotted on this plot
   String Name2[] = new String[NrOfParameters]; // specific parameter info
   for (int i=0; i<NrOfParameters; i++)
      {IdCode[i] = spDoc.IdCode[i]; Name2[i] = spDoc.Name2[i]; }

   // set graph sizes (rectangle comprised by axes)
   Rectangle ClientRect = new Rectangle(
     m_leftRect.x, m_leftRect.y, m_leftRect.width, m_leftRect.height * 4 / 5 );
   graph.SetClientAreaDP(g, ClientRect, 7, 3, 4, 1);

   // Get StartTime, TimeRange; TimeStep (not needed)
   // To calculate this, the first IdCode is used
   try {
     if ( !spDoc.GetXAxisRange(IdCode[0])) return;
   }
   catch (Exception e) {return;};


   // Input variables are determined:
   String StationName          = spDoc.m_StationName;
   String StationAltStr        = spDoc.m_StationAltStr;
   String StationAspectStr     = spDoc.m_StationAspectStr;
   String StationSlopeStr      = spDoc.m_StationSlopeStr;
   String StationLatStr        = spDoc.m_StationLatStr;
   String StationLonStr        = spDoc.m_StationLonStr;
   GregorianCalendar xTime;
   GregorianCalendar StartTime = spDoc.m_StartTime;
   int TimeRange               = spDoc.m_TimeRange1;
   int TimeStep                = spDoc.m_TimeStep;
   int xNrOfGrids              = spDoc.m_TimeGrid;   // initialized in spDoc
   int yNrOfGrids              = spDoc.m_yNrOfGrids; // initialized in spDoc
   float YMinValue             = spDoc.m_YMinValue;
   float YMaxValue             = spDoc.m_YMaxValue;
   Rectangle GraphRect         = graph.m_GraphRect;
   int NrOfXValues;

   if ( TimeStep != 0 && TimeRange != 0 )
      NrOfXValues = TimeRange / TimeStep;
   else
      return;

   if ( NrOfXValues <= 0 ) // Time Range < TimeStep
      NrOfXValues = 1;

   // Set X and Y Axis
   graph.SetXAxis( StartTime, TimeRange, xNrOfGrids );
   graph.SetYAxis( YMinValue, YMaxValue, yNrOfGrids );

   // Calculate and draw coordinate system
   graph.CalcCoordinateSystem();
   graph.DrawCoordinateAxes(g);
   graph.DrawGrids(g, true);

   // Draw x axis text (date/time)
   graph.DrawXAxisDateText(g, xNrOfGrids, "FULL_DATE");

   // Draw title
   graph.DrawAboveBelowText(g, 5, "ALIGN_CENTER", "BOLD", 11,
     StationName + ", " + StationAltStr + " m", false);

   if ((Setup.m_ResearchMode) && (!StationName.equals("Undefined Station")))
   {
      // Draw slope angle
      if (!StationSlopeStr.equals(""))
        graph.DrawAboveBelowText(g, 5, "ALIGN_LEFT", "PLAIN", 10,
          "Slope = " + StationSlopeStr + " deg", false);

      // Draw aspect
      if (!StationAspectStr.equals(""))
        graph.DrawAboveBelowText(g, (float) 5.8, "ALIGN_LEFT", "PLAIN", 10,
          "Aspect = " + StationAspectStr + " deg", false);

      // Draw latitude
      if (!StationLatStr.equals(""))
        graph.DrawAboveBelowText(g, 5, "ALIGN_RIGHT", "PLAIN", 10,
          "Latitude = " + StationLatStr, false);

      // Draw longitude
      if (!StationLonStr.equals(""))
        graph.DrawAboveBelowText(g, (float) 5.8, "ALIGN_RIGHT", "PLAIN", 10,
          "Longitude = " + StationLonStr, false);
   }

   // Draw y axis text (parameter)
   // If just one parameter, use parameter color, else standard foreground color
   if (NrOfParameters == 1) g.setColor(spDoc.GetParameterColor(IdCode[0]));

   // Digits for YAxisText
   int NrOfDigits;
   if (YMaxValue - YMinValue > 50)
     NrOfDigits = 0;
   else if (YMaxValue - YMinValue > 5)
     NrOfDigits = 1;
   else
     NrOfDigits = 2;

   graph.DrawYAxisText(g, yNrOfGrids, NrOfDigits, "ALIGN_LEFT", "ALIGN_TOP",
                       spDoc.GetAxisText(IdCode[0]),
                       spDoc.GetAxisUnit(IdCode[0]));

   // Loop over parameters plotted
   for ( int i = 0; i < NrOfParameters; i++)
   {

     // Draw graph in specific color
     g.setColor(spDoc.GetParameterColor(IdCode[i]));
     spDoc.m_CurrentColor = spDoc.GetParameterColor(IdCode[i]);

     // Draw additional parameter information
     if (NrOfParameters > 1)
     {
         //Schirmer
         String space = "";
               if (i == 1 || i == 10 || i == 19  || i ==  28) space = "                              ";
          else if (i == 2 || i == 11 || i ==  20 || i ==  29) space = "                                                            ";
          else if (i == 3 || i == 12 || i ==  21 || i ==  30) space = "                                                                                          ";
          else if (i == 4 || i == 13 || i ==  22) space = "                                                                                                                        ";
          else if (i == 5 || i == 14 || i ==  23) space = "                                                                                                                                                      ";
          else if (i == 6 || i == 15 || i ==  24) space = "                                                                                                                                                                                    ";
          else if (i == 7 || i == 16 || i ==  25) space = "                                                                                                                                                                                                                  ";
          else if (i == 8 || i == 17 || i ==  26) space = "                                                                                                                                                                                                                                                ";

          if ( i < 9 ) {
              graph.DrawAboveBelowText(g, (float) 7,
                                       "ALIGN_LEFT", "PLAIN", 10, space + "-- " + Name2[i], false);
          }
          else if (i < 18) {
              graph.DrawAboveBelowText(g, (float) 8,
                                       "ALIGN_LEFT", "PLAIN", 10, space + "-- " + Name2[i], false);
          }
          else if (i < 27){
              graph.DrawAboveBelowText(g, (float) 9,
                                       "ALIGN_LEFT", "PLAIN", 10, space + "-- " + Name2[i], false);
          }
          else{
              graph.DrawAboveBelowText(g, (float) 10,
                                       "ALIGN_LEFT", "PLAIN", 10, space + "-- " + Name2[i], false);
          }

          //end Schirmer

     }

     // Draw plot
     boolean symbolDraw = false; // if symbols are drawn at data point x/y-positions
     int symbolSize = 5; // size of symbols (in pixels)
     float yValue;
     MetDataEntry ActDataEntry;
     Point StartPoint = new Point();
     Point EndPoint = new Point();

     // Calculate first ActTime and ActDataEntry
     spDoc.GetFirstValue(IdCode[i]);
     xTime = spDoc.m_ActTime;
     ActDataEntry = (MetDataEntry) spDoc.m_ActDataEntry;

     yValue = ActDataEntry.GetMetData(IdCode[i]);
     StartPoint = graph.CalcPointLP(xTime,yValue);

     // Check how many pixels the minimum x distance between data points is
     GregorianCalendar xClosestNextTime = (GregorianCalendar) xTime.clone();
     xClosestNextTime.add(Calendar.SECOND, spDoc.m_MinTimeStepInData);
     EndPoint = graph.CalcPointLP(xClosestNextTime,0);
     if (EndPoint.x - StartPoint.x < symbolSize + 1)
       symbolDraw = false;
     else
       symbolDraw = true;

     int j=0;
     boolean firstValueAfterGap = false;

     do{
       if ( spDoc.GetNextValue(IdCode[i]))
       {
         xTime = spDoc.m_ActTime;
         ActDataEntry = (MetDataEntry) spDoc.m_ActDataEntry;
         yValue = ActDataEntry.GetMetData(IdCode[i]);

         if (firstValueAfterGap)
            StartPoint = graph.CalcPointLP(xTime,yValue);
         else
         {
            EndPoint = graph.CalcPointLP(xTime,yValue);

            if ((StartPoint.y <= graph.CalcYPosLP(YMinValue)) &&
                (EndPoint.y <= graph.CalcYPosLP(YMinValue)))
            // Start and end point are on or above the lower boundary of the
            // painting area
            {
              // Draw Line
              g.drawLine(StartPoint.x, StartPoint.y, EndPoint.x, EndPoint.y);

              // Draw symbols at data points
              // Same point may be painted two times; easier for data gaps etc.
              if (symbolDraw)
              {
                 g.fillRect(StartPoint.x - symbolSize / 2,
                            StartPoint.y - symbolSize / 2,
                            symbolSize, symbolSize);
                 g.fillRect(EndPoint.x - symbolSize / 2,
                            EndPoint.y - symbolSize / 2,
                            symbolSize, symbolSize);
              }
            }

            else if ((StartPoint.y < graph.CalcYPosLP(YMinValue)) &&
                (EndPoint.y > graph.CalcYPosLP(YMinValue)))
            // Start point is above, end point is below the lower boundary of the
            // painting area; right part of line (below painting area) not drawn
            {
                int EndPointX = StartPoint.x + (EndPoint.x - StartPoint.x) *
                               ((graph.CalcYPosLP(YMinValue) - StartPoint.y) /
                                (EndPoint.y - StartPoint.y));
                g.drawLine(StartPoint.x, StartPoint.y,
                           EndPointX, graph.CalcYPosLP(YMinValue));
            }

            else if ((StartPoint.y > graph.CalcYPosLP(YMinValue)) &&
                (EndPoint.y < graph.CalcYPosLP(YMinValue)))
            // Start point is below, end point is above the lower boundary of the
            // painting area; left part of line (below painting area) not drawn
            {
               int StartPointX = StartPoint.x + (EndPoint.x - StartPoint.x) *
                               ((graph.CalcYPosLP(YMinValue) - StartPoint.y) /
                                (EndPoint.y - StartPoint.y));
               g.drawLine(StartPointX, graph.CalcYPosLP(YMinValue),
                          EndPoint.x, EndPoint.y);
            }

            else
            // No actions of both start and end point are outside the drawing area
            {}



            StartPoint.x = EndPoint.x;
            StartPoint.y = EndPoint.y;
         }
         firstValueAfterGap = false;
       }
/*
         {
            EndPoint = graph.CalcPointLP(xTime,yValue);

            if ((StartPoint.y < graph.CalcYPosLP(YMinValue)) &&
                (EndPoint.y < graph.CalcYPosLP(YMinValue)))
            {
              // Draw Line
              g.drawLine(StartPoint.x, StartPoint.y, EndPoint.x, EndPoint.y);

              // Draw symbols at data points
              // Same point may be painted two times; easier for data gaps etc.
              if (symbolDraw)
              {
                 g.fillRect(StartPoint.x - symbolSize / 2,
                            StartPoint.y - symbolSize / 2,
                            symbolSize, symbolSize);
                 g.fillRect(EndPoint.x - symbolSize / 2,
                            EndPoint.y - symbolSize / 2,
                            symbolSize, symbolSize);
              }
            }

            StartPoint.x = EndPoint.x;
            StartPoint.y = EndPoint.y;
         }
         firstValueAfterGap = false;
       }
*/

       else
       {
         firstValueAfterGap = true;
       }

       j++;

     } while (j< NrOfXValues);

   } // end for i (parameter loop)

   // Set color back to foreground
   g.setColor(getForeground());
   spDoc.m_CurrentColor = getForeground();

   DrawMarker(g, m_IdCode);
}


boolean PrepareGraph(Graph graph)
// Basic settings for graph. Thus, slider or marker movements can be handled in a better way.
// Axis settings are calculated, thus CalcXPosLP() can be used to refer times to
// axis positions. Similar to first part of DrawSnowpackGraph() and DrawTimeProfile().
{
   // Set foreground to original color
   // (to prevent errors due to color resetting by other methods)
   Graphics g = getGraphics();
   //Color color = new Color(0,0,0);
   //color=   spDoc.m_Foreground;
   //g.setColor(spDoc.m_Foreground);

   // Input variables are determined
   GregorianCalendar StartTime = spDoc.m_StartTime;
   int TimeRange               = spDoc.m_TimeRange1;
   int TimeStep                = spDoc.m_TimeStep;
   int xNrOfGrids              = spDoc.m_TimeGrid;   // initialized in spDoc
   int yNrOfGrids              = spDoc.m_yNrOfGrids; // initialized in spDoc
   float YMinValue             = spDoc.m_YMinValue;  // initialized in spDoc
   float YMaxValue             = spDoc.m_YMaxValue;  // initialized in spDoc
   int NrOfXValues; // calculated below; times with measurements

   if ( TimeStep != 0 && TimeRange != 0 )
      NrOfXValues = TimeRange / TimeStep;
   else
      return false;

   if ( NrOfXValues <= 0 ) // Time Range < TimeStep
      NrOfXValues = 1;

   // Set graph size to default values
   Rectangle ClientRect = new Rectangle(
     m_leftRect.x, m_leftRect.y, m_leftRect.width, m_leftRect.height * 4 / 5 );
   graph.SetClientAreaDP(g, ClientRect, 7, 3, 4, 1);

   // Set x and y axis
   graph.SetXAxis( StartTime, TimeRange, xNrOfGrids );
   graph.SetYAxis( YMinValue, YMaxValue, yNrOfGrids );

   // Calculate coordinate system
   graph.CalcCoordinateSystem();

   return true;
}


boolean DrawMarker(Graphics g, int IdCode)
// Draws the marker above the graph in a XOR-mode (first call adds the marker,
// second call removes the marker). PrepareGraph has to be called before to
// provide adequate settings.
{
   Graph graph = new Graph(spDoc);
   if ( spDoc == null ) return false;

   if (!PrepareGraph(graph)) return false;

   //Draw marker
   if (spDoc.GetMarkerValue(IdCode))
    {
        GregorianCalendar xTime = spDoc.m_ActTime;
        Point StartPointMarker = new Point();
        Point EndPointMarker = new Point();
        StartPointMarker.x = graph.CalcXPosLP(xTime);
        StartPointMarker.y = graph.CalcYPosLP(spDoc.m_YMinValue);
        EndPointMarker.x = StartPointMarker.x;
        EndPointMarker.y = graph.CalcYPosLP(spDoc.m_YMaxValue);

        // Set XOR mode
        g.setXORMode(getBackground());

        // Draw proper marker line
        g.drawLine(StartPointMarker.x, StartPointMarker.y,
                   EndPointMarker.x, EndPointMarker.y);
        // Draw arrow
        g.drawLine(EndPointMarker.x - 3, EndPointMarker.y - 6,
                   EndPointMarker.x, EndPointMarker.y);
        g.drawLine(EndPointMarker.x + 3, EndPointMarker.y - 6,
                   EndPointMarker.x, EndPointMarker.y);
        g.drawLine(EndPointMarker.x - 3, EndPointMarker.y - 6,
                   EndPointMarker.x + 3, EndPointMarker.y - 6);

        // Reset to paint mode
        g.setPaintMode();

        // Slider handling
        // Values coincide about with screen x-pixels. Not exactly, because
        // slider bar is a little bit bigger than x-axis.
        spDoc.activeSliderMovement = false;
        spDoc.jSlider.setMinimum(graph.m_XAxisStartPos.x);
        spDoc.jSlider.setMaximum(graph.m_XAxisEndPos.x); // Total number of time steps
        spDoc.jSlider.setValue(StartPointMarker.x);
        spDoc.activeSliderMovement = true;

        //System.out.println("SnowPackView (marker): jSlider.setValue()");
    }

  return true;
}


void InsertControlObjects()
// Insert slider and buttons underneath the time series or profile graph
{
    // Set background and foreground colors
    setBackground(spDoc.m_Background);
    setForeground(spDoc.m_Foreground);

    // Determine the size of the SnowPackView frame
    leftRectSize.width = this.getSize().width;
    leftRectSize.height = this.getSize().height;
    m_leftRect = new Rectangle(0, 0, leftRectSize.width, leftRectSize.height);


    Graph graph = new Graph(spDoc);
    if ( spDoc == null ) return;
    if (!PrepareGraph(graph)) return;

    //System.out.println("Insert: x1: "+ graph.m_XAxisStartPos.x);
    //System.out.println("Insert: x2: "+ graph.m_XAxisEndPos.x);

    //spDoc.jSlider.setVisible(false);
    this.removeAll(); // remove all controls

    // Different button positions for Windows and Unix.
    // Windows: all buttons horizontally right and left of slider
    // Unix: buttons vertocally arranged left and right of slider;
    //       buttons have to be set bigger to appear in the same size as in Windows

    if (System.getProperty("os.name").substring(0,3).equals("Win"))
    // Windows
    {

    // Insert slider
    this.add(spDoc.jSlider, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (0.5 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      graph.m_XAxisEndPos.x - graph.m_XAxisStartPos.x + graph.m_SliderHeight,
      graph.m_SliderHeight
      ));

    // Insert buttons
    this.add(spDoc.jButton_Remove, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (4.0 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 1.0),
      graph.m_SliderHeight
      ));

    this.add(spDoc.jButton_Home, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (2.9 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 1.4),
      graph.m_SliderHeight
      ));

    this.add(spDoc.jButton_Prev, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (1.5 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 1.0),
      graph.m_SliderHeight
      ));

    this.add(spDoc.jButton_Next, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (0.5 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 1.0),
      graph.m_SliderHeight
      ));
    this.add(spDoc.jButton_End, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (1.5 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 1.4),
      graph.m_SliderHeight
      ));
    this.add(spDoc.jButton_Up, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (0.5 * graph.m_SliderHeight),
      (graph.m_YAxisStartPos.y + graph.m_YAxisEndPos.y) / 2
         - (int) (1.5 * graph.m_SliderHeight),
      (int) (graph.m_SliderHeight * 2.0),
      graph.m_SliderHeight
      ));
    this.add(spDoc.jButton_Down, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (0.5 * graph.m_SliderHeight),
      (graph.m_YAxisStartPos.y + graph.m_YAxisEndPos.y) / 2
         + (int) (0.5 * graph.m_SliderHeight),
      (int) (graph.m_SliderHeight * 2.0),
      graph.m_SliderHeight
      ));
    }

    else // not Windows

    {

    // Insert slider
    this.add(spDoc.jSlider, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      graph.m_XAxisEndPos.x - graph.m_XAxisStartPos.x + 2 * graph.m_SliderHeight,
      graph.m_SliderHeight
      ));

    // Insert buttons
    this.add(spDoc.jButton_Remove, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (4.0 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y - (int) (graph.m_SliderHeight * 2.0),
      (int) (graph.m_SliderHeight * 2.5),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    this.add(spDoc.jButton_Home, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (4.0 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y - (int) (graph.m_SliderHeight * 1.0),
      (int) (graph.m_SliderHeight * 2.5),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    this.add(spDoc.jButton_Prev, new XYConstraints(
      graph.m_XAxisStartPos.x - (int) (4.0 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 2.5),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    this.add(spDoc.jButton_Next, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (1.0 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y,
      (int) (graph.m_SliderHeight * 2.5),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    this.add(spDoc.jButton_End, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (1.0 * graph.m_SliderHeight),
      graph.m_YAxisStartPos.y - (int) (graph.m_SliderHeight * 1.0),
      (int) (graph.m_SliderHeight * 2.5),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    this.add(spDoc.jButton_Up, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (0.2 * graph.m_SliderHeight),
      (graph.m_YAxisStartPos.y + graph.m_YAxisEndPos.y) / 2
         - (int) (3 * graph.m_SliderHeight),
      (int) (graph.m_SliderHeight * 3.2),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    this.add(spDoc.jButton_Down, new XYConstraints(
      graph.m_XAxisEndPos.x + (int) (0.2 * graph.m_SliderHeight),
      (graph.m_YAxisStartPos.y + graph.m_YAxisEndPos.y) / 2 + graph.m_SliderHeight,
      (int) (graph.m_SliderHeight * 3.2),
      (int) (graph.m_SliderHeight * 2.0)
      ));

    }

    //
    spDoc.jSlider.setVisible(true); // no reaction?

    /* To draw slider immediately. Does not work, infinite loop.
    this.paintImmediately(
      graph.m_XAxisStartPos.x, graph.m_YAxisStartPos.y,
      graph.m_XAxisEndPos.x - graph.m_XAxisStartPos.x,
      graph.m_SliderHeight);
   */

}



}


