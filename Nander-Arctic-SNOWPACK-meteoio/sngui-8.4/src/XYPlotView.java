///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualization
//Version:
//Copyright:    Copyright (c) 2001
//Author:       G. Spreitzhofer
//Organization: SLF
//Description:  Java-Version of .
//Integrates the C++-Version of M. Steiniger
//       and the IDL-Version of M. Lehning/P.Bartelt.
///////////////////////////////////////////////////////////////////////////////
// XYPlotView: Draws the XYPlotView graph (right side of SnowPackFrame)
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class XYPlotView extends JPanel implements C_DataFile
{

SnowPackDoc spDoc;
int m_IdCode;
boolean firstActivation = true;

Rectangle m_rightRect; // comprises total area on right side of splitPane
Dimension rightRectSize = new Dimension();


// Construction
public XYPlotView(SnowPackDoc spDoc, int IdCode)
{
  this.spDoc = spDoc;
  this.m_IdCode = IdCode;

  this.setOpaque(true); // probably not necessary, might increase performance
}


protected void paintComponent(Graphics g)
{
   // Get default paint behaviour
   // important, without this DrawBackground etc. does not work
   super.paintComponent(g);

   // Retrieving some key DataFile parameters from SnowPackDoc if not first
   // of window (spDoc.SaveDataFile() was not called before
   if (spDoc.datafile_m_ActStartTime != null)
     spDoc.RetrieveDataFile();

   // Calling DrawXYPlot() instead of repaint() prevents that the whole area
   // XYPlotView is repainted.
   DrawXYPlot(g);
}


void DrawXYPlot(Graphics g)
{
  // System.out.println("DrawXYPlot");

  // Set background and foreground colors
  setBackground(spDoc.m_Background);
  setForeground(spDoc.m_Foreground);

  // Calculate the size of the XYPlotView frame
  rightRectSize.width = this.getSize().width;
  rightRectSize.height = this.getSize().height;
  m_rightRect = new Rectangle(0, 0, rightRectSize.width, rightRectSize.height);

  if (m_IdCode == ID_CODE_GRAIN_CLASS) // old // || (m_IdCode == 514))
     DrawGrainHeightProfile(g);
  else if (m_IdCode >= 500)
     DrawHeightProfile(g);
  else
     DrawSingleValue(g);

  //System.out.println("after DrawHeightProfile");
  //System.out.println("===================================");
}



void DrawHeightProfile(Graphics g)
// Draw right side plot
// x-axis: parameter (float values)
// y-axis: snow depth
// A number of different parameters can be plotted on the x-axis. Method works
// fine for multiple parameter display, but some adjustments still have to be
// processed (see the comment in the line after GetMarkerValue(m_IdCode)).
{
   Graph graph = new Graph(spDoc);
   if ( spDoc == null ) return;

   int NrOfParameters; // Number of parameters plotted
   int IdCode[] = new int[3]; // IdCode, max. parameters per plot
   ProDataEntry ActDataEntry = null;

/* if (m_IdCode == ID_CODE_SNOWPACK_TEMPERATURE)
   {
        NrOfParameters = 3;
        IdCode[0] = ID_CODE_SNOWPACK_TEMPERATURE;
        IdCode[1] = ID_CODE_RHO;
        IdCode[2] = ID_CODE_WATER_CONTENT;
   }
   else
*/ {
        NrOfParameters = 1;
        IdCode[0] = m_IdCode;
   }

   // Set graph sizes (rectangle comprised by axes)
   Rectangle ClientRect = new Rectangle(
     m_rightRect.x, m_rightRect.y, m_rightRect.width, m_rightRect.height * 4 / 5 );
   if (Setup.m_2Columns) graph.SetClientAreaDP(g, ClientRect, 0, 1, 4, 1);
   else                  graph.SetClientAreaDP(g, ClientRect, 5, 1, 4, 1);

   // Set y axis
   int yNrOfGrids  = spDoc.m_yNrOfGrids; // initialized in spDoc
   float YMinValue = spDoc.m_YMinValue;  // initialized in spDoc
   float YMaxValue = spDoc.m_YMaxValue;  // initialized in spDoc

   graph.SetYAxis( YMinValue, YMaxValue, yNrOfGrids );

   // Loop over parameters
   for ( int i = 0; i < NrOfParameters; i++)
   {
      // Code has to be adjusted if more than one parameter is used!!

      // Set x axis

      // 1st guess for XMinValue, XMaxValue (min and max value of x-axis):
      //  start and end value for plots without soil data
      float XMinValue = spDoc.m_StartValue;
      float XMaxValue = spDoc.m_EndValue;
      int xNrOfGrids  = spDoc.m_xNrOfGrids;

      // Use start and end value for plots with soil data if available and wished
      if (spDoc.GetMarkerValue(m_IdCode))
      {
         ActDataEntry = (ProDataEntry) spDoc.m_ActDataEntry;
         if (ActDataEntry.SoilDataExist(IdCode[i]) && Setup.m_SoilDataDisplay)
         {
           XMinValue = spDoc.m_SoilStartValue;
           XMaxValue = spDoc.m_SoilEndValue;
         }
      }

      // Possiblity to use the adjusted maximum and minimum value for that
      // parameter over all layers and data entries:
      // XMinValue = spDoc.m_XMinValue;
      // XMaxValue = spDoc.m_XMaxValue;

      graph.SetXAxis(XMinValue, XMaxValue, xNrOfGrids);

      if (i == 0)
      {
        // Calculate coordinate system
        graph.CalcCoordinateSystem();

        // Erase area enclosed by x- and y-axis
        g.setColor(spDoc.m_Background);
        g.fillRect(graph.m_XAxisStartPos.x,
              graph.m_YAxisEndPos.y,
              graph.m_XAxisEndPos.x - graph.m_XAxisStartPos.x,
              graph.m_YAxisStartPos.y - graph.m_YAxisEndPos.y);
        g.setColor(getForeground()); // Color for coordinate system and y-axis text
        spDoc.m_CurrentColor = getForeground();

        // Draw coordinate system
        graph.DrawCoordinateAxes(g);
        graph.DrawGrids(g, false);

        // Draw y axis text (snow depth)
        if (!Setup.m_2Columns)
        {
           graph.DrawYAxisText(g, yNrOfGrids, 1, "ALIGN_LEFT", "ALIGN_TOP",
                       spDoc.GetAxisText(ID_CODE_LAYER_HEIGHT),
                       spDoc.GetAxisUnit(ID_CODE_LAYER_HEIGHT));
        }
      }
      else
      {
         graph.CalcCoordinateSystem();
      }

      // Print parameter + unit and polygon in specific color
      g.setColor(spDoc.GetParameterColor(IdCode[i]));
      spDoc.m_CurrentColor = (spDoc.GetParameterColor(IdCode[i]));

      // Draw x axis text (x-axis = parameter)
      if ((IdCode[i] == ID_CODE_DENDRICITY) || (IdCode[i] == ID_CODE_SPHERICITY))
        graph.DrawXAxisText(g, 2 * i + 1, xNrOfGrids, 2); // 2 digits
      else
        graph.DrawXAxisText(g, 2 * i + 1, xNrOfGrids, 1); // 1 digit

      graph.DrawAboveBelowText(g, (float) (2 * i + 1.7), "ALIGN_RIGHT", "BOLD", 10,
                            spDoc.GetAxisText(IdCode[i]) + " (" +
                            spDoc.GetAxisUnit(IdCode[i]) + ")", false);


      // ====== Draw plot ==========

      float xValue, yValue;
      int PrevStartPointY = 0;
      Point StartPoint = new Point();
      Point EndPoint = new Point();
      GregorianCalendar xTime;
      int NrOfLayers; // number of values in line with active Idcode
      int TotalNrOfLayers; // number of values in line containing layer height
      int NrOfSnowLayers;
      int NrOfSoilLayers;
      int StartLayer; // index of start layer for drawing

      if (spDoc.GetMarkerValue(m_IdCode))
      // IdCode[i] not used here; just one active time for all parameters.
      // xTime should be removed from loop; error handling if no data for
      // second parameter etc. are available.
      {
         xTime = spDoc.m_ActTime;
         ActDataEntry = (ProDataEntry) spDoc.m_ActDataEntry;

         TotalNrOfLayers = ActDataEntry.GetTotalNrOfLayers();
         NrOfSnowLayers = ActDataEntry.GetNrOfSnowLayers();
         NrOfSoilLayers = TotalNrOfLayers - NrOfSnowLayers;
         NrOfLayers = ActDataEntry.GetNrOfLayers(IdCode[i]);

         if (Setup.m_SoilDataDisplay || (TotalNrOfLayers != NrOfLayers))
             // Display snow layers plus soil layers (if available and demanded)
             StartLayer = 0;
         else
             // Display only snow layers
             StartLayer = NrOfSoilLayers; // can also be 0

         if (ActDataEntry.SoilDataExist(IdCode[i]) && Setup.m_SoilDataDisplay)
           yValue = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, 0); // lowest soil node
         else
           yValue = 0; // soil/snow interface

         if (StartLayer < NrOfLayers)
         // excludes cases where only soil and no snow data available
         {
           xValue = ActDataEntry.GetZData( IdCode[i], StartLayer);
           //StartPoint = graph.CalcPointLP(xValue, yValue);
           StartPoint.x = graph.CalcXPosLP(xValue);
           StartPoint.y = graph.CalcYPosLP(yValue);
         }

         for ( int j = StartLayer; j < NrOfLayers; j++ )
         {
            xValue = ActDataEntry.GetZData( IdCode[i], j );

            // Get layer height
            if (TotalNrOfLayers == NrOfSnowLayers)
               // ID_CODE_LAYER_HEIGHT does not contain soil heights
               // --> also other IDCodes do not contain soil data
               yValue = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j);
            else if (NrOfLayers == NrOfSnowLayers)
               // ID_CODE_LAYER_HEIGHT does not contain soil heights
               // 1 added because 1st layer is lowest soil layer
               yValue = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j + 1 + NrOfSoilLayers);
            else
               yValue = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j + 1);

            EndPoint.x = graph.CalcXPosLP(xValue);
            EndPoint.y = graph.CalcYPosLP(yValue);

            //System.out.println("yValue, YMaxValue, YMinValue: "+yValue+" "+YMaxValue+" "+YMinValue);
            //System.out.println("StartPoint.y, EndPoint.y: "+StartPoint.y+" "+EndPoint.y);

            if (( yValue <= YMaxValue ) &&
                ( StartPoint.y <= graph.CalcYPosLP(YMinValue)) &&
                ( EndPoint.y <= graph.CalcYPosLP(YMinValue)) &&
                ( EndPoint.y >= graph.CalcYPosLP(YMaxValue)) &&
                // No line is drawn if StartPoint.y or EndPoint.y out of y-axis-range.
                ( StartPoint.x >= graph.m_XAxisStartPos.x) &&
                ( StartPoint.x <= graph.m_XAxisEndPos.x) &&
                ( EndPoint.x >= graph.m_XAxisStartPos.x) &&
                ( EndPoint.x <= graph.m_XAxisEndPos.x) )
                // No line is drawn if StartPoint.x or EndPoint.x out of x-axis-range.

            // No break because yValue needed below.
            {
              if ( IdCode[i] == ID_CODE_RHO ||
                   IdCode[i] == ID_CODE_WATER_CONTENT ||
                   IdCode[i] == ID_CODE_GRAIN_DIA ||
                   IdCode[i] == 531 || IdCode[i] == 532 || // Stability parameters
                   IdCode[i] == 533 || IdCode[i] == 534 )
              {
                 // Draw graph stepwise (first horizontal, then vertical line)
		             g.drawLine(StartPoint.x, StartPoint.y, EndPoint.x, StartPoint.y);
                 g.drawLine(EndPoint.x, StartPoint.y, EndPoint.x, EndPoint.y);
              }
              else
              {
                 // Draws line from middle of previous layer to middle of current layer
                 if ((j > StartLayer) && // at first layer no previous point is known
                     (PrevStartPointY <= graph.CalcYPosLP(YMinValue)))
                 {
                    //System.out.println("x1,y1,x2,y2, (x/y): "+a+" " +b+" "+c+" "+ d + " "+xValue+" "+yValue);
                    g.drawLine(StartPoint.x, (PrevStartPointY + StartPoint.y ) / 2,
                               EndPoint.x, (StartPoint.y + EndPoint.y) / 2 );
                 }
              }
            }

            PrevStartPointY = StartPoint.y;
            StartPoint.x = EndPoint.x;
            StartPoint.y = EndPoint.y;

         } // end for j

         // Output of date/time and snow depth for each parameter i at the same
         // position --> information still there if one parameter is missing
         g.setColor(getForeground());
         spDoc.m_CurrentColor = getForeground();

         if (ActDataEntry.SoilDataExist(IdCode[i]) && Setup.m_SoilDataDisplay)
         {       graph.DrawGroundSurface(g);

                 if (!Setup.m_2Columns)
                   graph.DrawYAxisText(g, yNrOfGrids, 1, "ALIGN_LEFT", "ALIGN_BOTTOM",
                   "Soil", spDoc.GetAxisUnit(ID_CODE_LAYER_HEIGHT));
         }

         // Draw date/time and snow depth
         graph.DrawAboveBelowText(g, 2 * NrOfParameters + 1,
          "ALIGN_LEFT", "PLAIN", 10, graph.TimeToString(xTime).substring(0,14), true);
         graph.DrawAboveBelowText(g, 2 * NrOfParameters + 2,
          "ALIGN_LEFT", "PLAIN", 10, "Snow Depth: " + yValue + " cm", true);

         // If stability profiles (IdCodes 531 - 534) or grain type
         // (IdCode 513) is drawn:
         // insert some additional parameters contained in IdCode 530
         if ((IdCode[i] == 531) ||(IdCode[i] == 532) ||
             (IdCode[i] == 533) || (IdCode[i] == 534) ||
             (IdCode[i] == 513))
         {
            // Insert stability arrows and text
            InsertStabilityValues(g, graph, ActDataEntry, NrOfParameters,
                                  IdCode[i], XMinValue, XMaxValue);
         }

      } // end if spDoc.getMarkerValue

   } // end for i
}


void InsertStabilityValues(Graphics g, Graph graph, ProDataEntry ActDataEntry,
     int NrOfParameters, int IdCode, float XMinValue, float XMaxValue)
// Used to insert stability parameters in the right-hand graph (arrows and text
// below the proper x-axis text).
// Called by DrawHeightProfile() or DrawGrainHeightProfile().
{
   // Define stability parameters
   int S_class1 = 0; // Profile Type
   int S_class2 = 0; // ASI-classification
   float S_d = 0; // Deformation Stability Index (= Direct Action Stability Index)
   float S_n = 0; // Natural Stability Index
   float S_s = 0; // Alternative Stability Index (= Skier Stability Index)
   float S_d_height = 0;
   float S_n_height = 0;
   float S_s_height = 0;

   if (ActDataEntry.GetNrOfValues(530) == 8)
   // 8 data values in line 530 needed, otherwise error assumed
   // (no plot of stability parameters)
   {

    if (ActDataEntry.CheckDataValid(530))
    {
      S_class1   = (int)  ActDataEntry.GetZData(530, 0);
      S_class2   = (int)  ActDataEntry.GetZData(530, 1);
      S_d_height = (float) ActDataEntry.GetZData(530, 2);
      S_d        = (float) ActDataEntry.GetZData(530, 3);
      S_n_height = (float) ActDataEntry.GetZData(530, 4);
      S_n        = (float) ActDataEntry.GetZData(530, 5);
      S_s_height = (float) ActDataEntry.GetZData(530, 6);
      S_s        = (float) ActDataEntry.GetZData(530, 7);
    }

    // Print values of stability parameters at bottom and insert arrows in graph
    //Schirmer: changed some minor settings
    graph.DrawAboveBelowText(g, (float) (2 * NrOfParameters + 3.5),
                             "ALIGN_LEFT", "BOLD", 11,"Stability Class: " + S_class2, true);
    graph.DrawAboveBelowText(g, (float) (2 * NrOfParameters + 4.8),
      "ALIGN_LEFT", "PLAIN", 10, "Profile Type: " + S_class1, true);

    g.setColor(Color.magenta);
    spDoc.m_CurrentColor = Color.magenta; // needed for DrawAboveBelowText
    if ((IdCode == 531) ||(IdCode == 513))
        graph.InsertArrow(g, XMinValue, XMaxValue, S_d_height);
    graph.DrawAboveBelowText(g, (float) (2 * NrOfParameters + 6.2),
      "ALIGN_LEFT", "PLAIN", 10, "S_d:       " +
                                 S_d + " at " + S_d_height + " cm", true);
     /* Schirmer
    g.setColor(Color.blue);
    spDoc.m_CurrentColor = Color.blue;
    if (IdCode == 532)
      graph.InsertArrow(g, XMinValue, XMaxValue, S_n_height);
   graph.DrawAboveBelowText(g, (float) (2 * NrOfParameters + 5.2),
      "ALIGN_LEFT", "PLAIN", 10, "S_n:       " +
                                 S_n + " at " + S_n_height + " cm", true);*/

    g.setColor(new Color(0,127,0)); // dark green
    spDoc.m_CurrentColor = new Color(0,127,0);
    if ((IdCode == 533) ||(IdCode == 513))
      graph.InsertArrow(g, XMinValue, XMaxValue, S_s_height);
    graph.DrawAboveBelowText(g, (float) (2 * NrOfParameters + 7.1),
      "ALIGN_LEFT", "PLAIN", 10, "S_s:       " +
                                 S_s + " at " + S_s_height + " cm", true);
    g.setColor(getForeground());
   }

}


void DrawGrainHeightProfile(Graphics g)
// Vertical profile of grain forms is drawn. On the left side of the graph,
// a column of grain form color boxes is drawn. On the right side, symbol boxes
// are drawn.
// Originally this routine was designed for the display of two sort of data:
// IdCode 513... grain type data, related to layers
// IdCode 514... surface hoar data, related to interfaces (only 0,1)
// Now surface hoar data are included in IdCode 513 (related to layers);
// hoar on surface is given as additional number (appended at IdCode 513) and does not
// include thickness.
// Old code (related to IdCode 514) is marked by // old //.
{
   // set debug to "true" to output printed values (otherwise "false")
   boolean debug = false;

   Graph graph = new Graph(spDoc);
   if ( spDoc == null ) {return; }

   int yNrOfGrids  = spDoc.m_yNrOfGrids; // initialized in spDoc
   float YMinValue = spDoc.m_YMinValue;  // initialized in spDoc
   float YMaxValue = spDoc.m_YMaxValue;  // initialized in spDoc
   float XMinValue = 0; // scale needed to place column of color boxes
   float XMaxValue = 1;

   // Set graph sizes (rectangle comprised by axes)
   Rectangle ClientRect = new Rectangle(
     m_rightRect.x, m_rightRect.y, m_rightRect.width, m_rightRect.height * 4 / 5 );
   if (Setup.m_2Columns) graph.SetClientAreaDP(g, ClientRect, 0, 1, 4, 1);
   else                  graph.SetClientAreaDP(g, ClientRect, 5, 1, 4, 1);

   // Set x and y axis
   graph.SetXAxis( XMinValue, XMaxValue, 0); // xNrOfGrids = 0
   graph.SetYAxis( YMinValue, YMaxValue, yNrOfGrids );

   // Calculate and draw coordinate system
   graph.CalcCoordinateSystem();

   // Erase area enclosed by x- and y-axis
   g.setColor(spDoc.m_Background);
   g.fillRect(graph.m_XAxisStartPos.x,
              graph.m_YAxisEndPos.y,
              graph.m_XAxisEndPos.x - graph.m_XAxisStartPos.x,
              graph.m_YAxisStartPos.y - graph.m_YAxisEndPos.y);
   g.setColor(getForeground()); // Set foreground to original color
   spDoc.m_CurrentColor = getForeground();

   // Draw coordinate system
   graph.DrawCoordinateAxes(g);
   graph.DrawGrids(g, false);

   // Draw y axis text
   if (!Setup.m_2Columns)
   {
      graph.DrawYAxisText(g, yNrOfGrids, 1, "ALIGN_LEFT", "ALIGN_TOP",
                          spDoc.GetAxisText(ID_CODE_LAYER_HEIGHT),
                          spDoc.GetAxisUnit(ID_CODE_LAYER_HEIGHT));
   }

   int IdCode = ID_CODE_GRAIN_CLASS;
   // old // If plot with hoar data is desired: m_IdCode = 514;
   // old // hoar data (0, 1) are stored in the input data under IdCode 514;
   // old // general grain data (without hoar info) are stored under IdCode 513;

   // old // boolean run2 = false;
   ProDataEntry ActDataEntry;
   int NrOfLayers;
   int TotalNrOfLayers;
   int NrOfSnowLayers;
   int NrOfSoilLayers;

   // old // do { First loop is standard plot; in second loop (if necessary) hoar is added

   // Preparation for plot of column
   float xStart = (float) 0; // Start position of column on x-axis (0 to 1)
   float xEnd = (float) 0.1; // Column end position (10% of axis length)
   float LayerHeight = 0;
   float zValue = 0;
   float NextZValue = 0;
   Point StartPoint = new Point(); // lower left
   Point EndPoint = new Point(); // upper right
   Color ActColor = new Color(0,0,0);
   Color NextColor = new Color(0,0,0);

   // Preparation for plot of symbol boxes
   Point SymbolStartPoint = new Point(); // upper left corner of symbol box!!
   Point SymbolEndPoint = new Point(); // lower right corner of symbol box!!
   Dimension MinSpace = new Dimension(5, 6); // minimum horizontal and vertical
                                             // space between two symbol boxes
   Dimension SymbolSize = new Dimension(24, 12); // symbol box width and height (was 16/8)

   int PrevEndPointX = 0;
   int PrevEndPointY = graph.CalcYPosLP(YMinValue) + SymbolSize.height + MinSpace.height;
   int HorizPos = 0; // counting the number of the horizontal position of
   // the symbol box (1=directly to the right of the column, 2= next to the right etc.)

   // Calculate ActDataEntry
   spDoc.GetMarkerValue(IdCode);
   ActDataEntry = (ProDataEntry) spDoc.m_ActDataEntry;

   TotalNrOfLayers = ActDataEntry.GetTotalNrOfLayers();
   NrOfSnowLayers = ActDataEntry.GetNrOfSnowLayers();
   NrOfSoilLayers = TotalNrOfLayers - NrOfSnowLayers;
   NrOfLayers = ActDataEntry.GetNrOfLayers(IdCode);

   // ID_CODE_GRAIN_CLASS: one additional layer on top contains surface hoar info
   // (existent if value > 0)
   if (NrOfLayers > 0) NrOfLayers--;

   StartPoint.x = graph.CalcXPosLP(xStart);
   EndPoint.x = graph.CalcXPosLP(xEnd);
   StartPoint.y = graph.CalcYPosLP(YMinValue);

   if ( NrOfLayers > 0 )
   {
      // Get starting values for the j-loop
      NextZValue = ActDataEntry.GetZData(IdCode, 0); // 0 = layer number
      NextColor = GrainForm.GetColor((int) NextZValue);
   }

   if (debug) System.out.println("StartPoint.x, EndPoint.x: " + StartPoint.x + " " + EndPoint.x);

   for ( int j = 0; j < NrOfLayers; j++ )
   {
      boolean hoarboxpaint = false; // old //

      // Get color for plot
      zValue = NextZValue;
      ActColor = NextColor;

      // Optimization: check if grain form (plus color) of following layer is
      // identical; then draw with same brush. Changing brushes is time intensive.
      // In SnowPackView: Same procedure is applied, but if grain form colors
      // differ by less than 8.
      while ( j < NrOfLayers )
      {
         // old //  Hoar boxes are painted if wished (m_IdCode = 514) if adequate data are available
         // old // hoarboxpaint = ((m_IdCode != IdCode) && (ActDataEntry.GetZData(514, j) == 1));

         if (j + 1 == NrOfLayers) break;

         NextZValue = ActDataEntry.GetZData(IdCode, j+1);
         // old // if (run2) break; // only NextZValue needed for hoar painting

         NextColor = GrainForm.GetColor((int) NextZValue);

         // Refrozen layers never skipped
         if (((zValue % 10) == 2) || ((NextZValue % 10) == 2)) break;

         // old // If surface hoar boxes shall be painted, draw the other boxes anyway,
         // old // even if layer above and below the hoar layer are almost identical.
         // old // if (hoarboxpaint) break;

         if ( Math.abs( ActColor.getRed()   - NextColor.getRed()   ) < 8 &&
                Math.abs( ActColor.getGreen() - NextColor.getGreen() ) < 8 &&
                Math.abs( ActColor.getBlue()  - NextColor.getBlue()  ) < 8 )
                // next color is nearly identical
                // <8: not very useful for colors, check for equality better;
                // used because also use in standard routine DrawSnowpackGraph (SnowPackView);
                // thus colors on left and right side graph are identical
         {
            // draw next value(s) with same color
            if (debug) System.out.println("layer "+j+" skipped; " +
                    "z = "+zValue+", Next z = " + NextZValue);
            j++;
         }
         else
         {
            break; // next color is different
         }

      } // while


      // Get layer height
      if (TotalNrOfLayers == NrOfSnowLayers)
         // ID_CODE_LAYER_HEIGHT does not contain soil heights
         // --> also other IDCodes do not contain soil data
         LayerHeight = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j);
      else if (NrOfLayers == NrOfSnowLayers)
         // ID_CODE_LAYER_HEIGHT contains soil heights
         // 1 added because 1st layer is lowest soil layer
         LayerHeight = ActDataEntry.GetZData( ID_CODE_LAYER_HEIGHT, j + 1 + NrOfSoilLayers);


      if ( LayerHeight > YMaxValue ) // don't draw beyond maximum axis value
      {
         LayerHeight = YMaxValue; // Clipping on top
         j = NrOfLayers; // in order to finish for(j) loop after drawing
      }
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
                g.fillRect(StartPoint.x, EndPoint.y,
                           EndPoint.x - StartPoint.x, 1);
         }
         */ // old //


         // old // else if (IdCode != 514) // standard plot
         if (IdCode != 514) // standard plot
         {
           g.setColor(ActColor);

           // Draw rectangle, using g.fillRect(left, top, width, height)
           // Error in Java (the Complete Reference)-book: (top <--> left)!!!
           if ((IdCode == ID_CODE_GRAIN_CLASS) && (StartPoint.y - EndPoint.y < 1))
             // draw grain type anyway at least at the height of one pixel
             g.fillRect(StartPoint.x, EndPoint.y,
                   EndPoint.x - StartPoint.x, 1);
           else
             // all other cases
             g.fillRect(StartPoint.x, EndPoint.y,
                   EndPoint.x - StartPoint.x, StartPoint.y - EndPoint.y);

           // If last digit of code indicates melted and refrozen grains
           // --> vertical lines in cyan across box
           if (zValue % 10 == 2)
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

            // Highest layer: also draw surface hoar if available
            if ((j == NrOfLayers - 1)
             && (ActDataEntry.GetZData(IdCode, j+1) > 0))
            {
                g.setColor(GrainForm.GetColor(660)); // hoar color
                g.fillRect(StartPoint.x, EndPoint.y-1, EndPoint.x - StartPoint.x, 2);
            }

            g.setColor(ActColor); // reset color

           // *************** //
           // Color rectangle (forming the column on the left side of the plot)
           // is finished. Below the symbol boxes are drawn.


            // old // Two loops. First loop draws regular boxes, second loop draws
            // old // surface hoar boxes (if hoar painting is wished, m_IdCode = 514)
            // old // for (int k = 0; k < 2; k++)
            // old // {
            // old //  if ((k == 1) && (!hoarboxpaint)) break;

             if ( (j == NrOfLayers - 1 ) ||
                  (Math.abs(zValue - NextZValue) > 2 ) ||
                  ((Math.abs(zValue - NextZValue) > 0 )
                       && (((zValue % 10) == 2) || ((NextZValue % 10) == 2)))
                )
             // Symbol box is drawn if:
             // - highest layer
             // - grain form pair (F1, F2) of current and next layer is different
             // - grain form pair (F1, F2) is equal, and either the current or
             //   the next layer are refrozen (F3 = 2)

             { // Start if draw symbol box

              // Position of rectangle containing the grain form symbols
              if  ((EndPoint.y > PrevEndPointY - SymbolSize.height - MinSpace.height)
                  // vertical overlapping with the previous symbol box occurs
                  && (PrevEndPointX + MinSpace.width + SymbolSize.width
                      <= graph.CalcXPosLP(XMaxValue)))
                  // Next symbol box would not reach ouside the grid margins
              {
                  HorizPos ++; // Next symbol box is drawn to the right of the last one
                  SymbolStartPoint.x = EndPoint.x + MinSpace.width +
                       (HorizPos - 1) * (SymbolSize.width + MinSpace.width);
              }
              else
              // Place box just right of the color bar, even if vertical overlapping appears
              {
                  HorizPos = 1;
                  SymbolStartPoint.x = EndPoint.x + MinSpace.width;

                  PrevEndPointY = EndPoint.y;
              }

              SymbolStartPoint.y = EndPoint.y;
              SymbolEndPoint.x = SymbolStartPoint.x + SymbolSize.width;
              SymbolEndPoint.y = SymbolStartPoint.y + SymbolSize.height;

              // old // Set color for hoar box painting
              // old // if ((k == 1) && (hoarboxpaint)) g.setColor(GrainForm.GRAIN_COLOR_6);

              // Draw line from upper right corner of color box (in column) to
              // upper left corner of symbol box
              g.drawLine(EndPoint.x, EndPoint.y, SymbolStartPoint.x, SymbolStartPoint.y);

              // old // // Draw grain form symbol box
              // old // if ((k == 1) && (hoarboxpaint))
              // old //     GrainForm.DrawSymbol(g, 660, SymbolStartPoint, SymbolEndPoint);
              // old // else
              GrainForm.DrawSymbol(g, (int) zValue, SymbolStartPoint, SymbolEndPoint);

              // Resetting of colors necessary after DrawSymbol
              // old // if ((k == 1) && (hoarboxpaint))
              // old //   g.setColor(GrainForm.GRAIN_COLOR_6);
              // old // else
              g.setColor(ActColor);

              // Colored frame around box
              //schirmer
              /*g.drawRect(SymbolStartPoint.x, SymbolStartPoint.y,
                         SymbolSize.width, SymbolSize.height);*/
              g.drawLine(SymbolStartPoint.x, SymbolStartPoint.y,
                             SymbolStartPoint.x,SymbolStartPoint.y + SymbolSize.height);
              //end schirmer

              PrevEndPointX = SymbolEndPoint.x;

              // Highest layer: draw connection line, surface hoar symbol box
              // plus a frame around it. In contrast to the other symbols,
              // this symbol is drawn above the connection line to the left column.
              if ((j == NrOfLayers - 1)
               && (ActDataEntry.GetZData(IdCode, j+1) > 0))
              {
                  SymbolStartPoint.x = EndPoint.x + MinSpace.width;
                  SymbolStartPoint.y = EndPoint.y-3 - SymbolSize.height; // different;
                  SymbolEndPoint.x = SymbolStartPoint.x + SymbolSize.width;
                  SymbolEndPoint.y = SymbolStartPoint.y + SymbolSize.height; // different
                  g.setColor(GrainForm.GRAIN_COLOR_6);
                  g.drawLine(EndPoint.x, EndPoint.y, SymbolStartPoint.x, SymbolEndPoint.y + 1);
                  GrainForm.DrawSymbol(g, 660, SymbolStartPoint, SymbolEndPoint);
                  g.setColor(GrainForm.GRAIN_COLOR_6);
                  //schirmer
                  /*g.drawRect(SymbolStartPoint.x, SymbolStartPoint.y,
                             SymbolSize.width, SymbolSize.height);*/
                  g.drawLine(SymbolStartPoint.x, SymbolStartPoint.y,
                             SymbolStartPoint.x,SymbolStartPoint.y + SymbolSize.height);
                  //end schirmer
              }


            } // end if draw symbol box

           // old // } // end k loop

         } // end else if (standard plot)

      }

      if (debug)
      {  System.out.println("**** Layer j = "+j+": height = " + LayerHeight);
         System.out.println("     StartPoint.y, EndPoint.y: "+
              StartPoint.y + " " + EndPoint.y + " z = " + zValue);
         System.out.println("RGB: " + ActColor.getRed() + " " +
             ActColor.getGreen() + " " + ActColor.getBlue());
      }

      StartPoint.y = EndPoint.y;

   } // for j


   /* // old //
   if (IdCode == 514)
   // Method launched with IdCode 513; another loop with IdCode 514
   // to paint the hoar layers in the snow column was already processed
   {
      break;
   }

   if (IdCode == ID_CODE_GRAIN_CLASS)
   {
      if (IdCode != m_IdCode)
      {  // m_IdCode = 514 (grain forms, hoar to be painted);
         // above (before the proper painting) IdCode was set to 513;
         // till here the basic snow column (without hoar) and all the symbol
         // boxes (also hoar) were drawn;
         // another loop with IdCode=514 (containing hoar data) follows
         // to paint hoar layers in snow column;
         run2 = true;
         IdCode = 514;
         System.out.println("SnowPackView: 2nd run necessary (hoar paint)");
      }
      else
      {  // Method was launched with IdCode = 513 = ID_CODE_GRAIN_CLASS (no hoar)
         run2 = false;
      }
   }

   } while (run2);

   if (IdCode == 514)
   {  //Print "Surface Hoar"
      g.setColor(GrainForm.GetColor(660)); // hoar color
      graph.DrawAboveBelowText(g, (float) 5.5, "ALIGN_LEFT", "PLAIN", 10, "-- Surface Hoar", false);
   }
   */ // old //

   // Set foreground to original color
   g.setColor(getForeground());

   // Print "Grain Type"
   graph.DrawAboveBelowText(g, (float) 1.5, "ALIGN_RIGHT", "BOLD", 10, "Grain Type", false);

   // Print time of currently active data entry
   graph.DrawAboveBelowText(g, 3,
      "ALIGN_LEFT", "PLAIN", 10, graph.TimeToString(spDoc.m_ActTime).substring(0,14), true);

   // Print total snow depth
   float SnowDepth;
   if (TotalNrOfLayers == NrOfSnowLayers)
     // no soil data
     SnowDepth = ActDataEntry.GetZData(ID_CODE_LAYER_HEIGHT, TotalNrOfLayers-1);
   else
     SnowDepth = ActDataEntry.GetZData(ID_CODE_LAYER_HEIGHT, TotalNrOfLayers);

   graph.DrawAboveBelowText(g, 4,
      "ALIGN_LEFT", "PLAIN", 10, "Snow Depth: " + SnowDepth + " cm", true);

   // Insert stability arrows and text
   InsertStabilityValues(g, graph, ActDataEntry, 1,
                                  IdCode, XMinValue, XMaxValue);

   return;
}



void DrawSingleValue(Graphics g)
// Draw single value plus date
{
   Graph graph = new Graph(spDoc);
   if ( spDoc == null ) return;

   // Extraction of information about with which parameter the current parameter
   // m_IdCode should be printed together
   spDoc.AssociatedIdCodes(m_IdCode);
   int NrOfParameters = spDoc.NrOfParameters; // Number of parameters to be plotted
   int IdCode[] = new int[NrOfParameters]; // IdCodes to be plotted on this plot
   String Name2[] = new String[NrOfParameters]; // Specific parameter info
   for (int i=0; i<NrOfParameters; i++)
      {IdCode[i] = spDoc.IdCode[i]; Name2[i] = spDoc.Name2[i]; }

   if (spDoc.GetMarkerValue(m_IdCode))
   {
     // Set graph sizes (rectangle is vertical line)
     Rectangle ClientRect = new Rectangle(
       m_rightRect.x,
       m_rightRect.y + m_rightRect.height * 2 / 5, m_rightRect.width,
       m_rightRect.height * 2 / 5 );
     graph.SetClientAreaDP(g, ClientRect, 0, 0, 3, 1);

     GregorianCalendar xTime = spDoc.m_ActTime;
     MetDataEntry ActDataEntry = (MetDataEntry) spDoc.m_ActDataEntry;

     // graph.SetXAxis(xTime, 1, 1); // StartTime, TimeRange, xNrOfGrids;
     graph.SetYAxis(0, 1, 1); // YMinValue, YMaxValue, yNrOfGrids

     // Draw rectangle around ClientRect
     //g.setColor(getForeground());
     graph.CalcCoordinateSystem();
     //graph.DrawGrids(g, false);

     if (NrOfParameters == 1)
     {  // draw just one value, centered;
        // print parameter + value + unit in specific color
        g.setColor(spDoc.GetParameterColor(m_IdCode));
        spDoc.m_CurrentColor = spDoc.GetParameterColor(m_IdCode);
        graph.DrawAboveBelowText(g, (float) -3, "ALIGN_CENTER", "BOLD", 12,
                            spDoc.GetAxisText(m_IdCode) + ":", false); // parameter name

        float xValue = ActDataEntry.GetMetData( m_IdCode ); // m_IdCode = position in data sequence
        if (spDoc.GetAxisUnit(m_IdCode).equals("1"))  // unit: no dimension
          graph.DrawAboveBelowText(g, (float) -1.5, "ALIGN_CENTER", "BOLD", 12,
                                   xValue + "", true);
        else
        {
          if ( (new Float(xValue)).equals(new Float(-999.9)) )
            graph.DrawAboveBelowText(g, (float) -1.5, "ALIGN_CENTER", "BOLD", 12,
                                   "not available", true);
          else
            graph.DrawAboveBelowText(g, (float) -1.5, "ALIGN_CENTER", "BOLD", 12,
                                   xValue + " " + spDoc.GetAxisUnit(m_IdCode), true);
        }
     }
     else
     { // More than one parameter to be plotted
       // Parameter name not in specific parameter color
       //Schirmer -4.5 auf -12.5
       graph.DrawAboveBelowText(g, (float) (-NrOfParameters - 2.5) , "ALIGN_CENTER", "BOLD", 11,
                            spDoc.GetAxisText(m_IdCode) + ":", false);

       // Loop over values plotted
       for ( int i = 0; i < NrOfParameters; i++)
       {
          g.setColor(spDoc.GetParameterColor(IdCode[i]));
          spDoc.m_CurrentColor = spDoc.GetParameterColor(IdCode[i]);

          float xValue = ActDataEntry.GetMetData(IdCode[i]);
          if (spDoc.GetAxisUnit(IdCode[i]).equals("1"))  // unit: no dimension
            graph.DrawAboveBelowText(g, (float) (-NrOfParameters - 1 + i), "ALIGN_CENTER", "PLAIN", 11,
                  Name2[i] + ": " + xValue, true); // Name2: e.g. "model"
          else
          {
             if ( (new Float(xValue)).equals(new Float(-999.9)) )
               graph.DrawAboveBelowText(g, (float) (-NrOfParameters - 1 + i), "ALIGN_CENTER", "PLAIN", 11,
                  Name2[i] + ": not available", true);
             else
                 graph.DrawAboveBelowText(g, (float) (-NrOfParameters - 1 + i), "ALIGN_CENTER", "PLAIN", 11,
                  Name2[i] + ": " + xValue + " " + spDoc.GetAxisUnit(m_IdCode), true);
          }
       }
     }

     // Draw the date. Will be centered since ClientRect is vertical center line
     g.setColor(getForeground());
     spDoc.m_CurrentColor = getForeground();
     graph.DrawCenteredDate(g, xTime, "FULL_DATE");

   } // end if spDoc.getMarkerValue

}


}











