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
// Graph: Management of graphics parameters
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
//import com.borland.jbcl.util.DottedLine; // not used any longer

public class Graph {
    public Graph() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Position and size of graph (coordinate system area)
   Rectangle m_GraphRect = new Rectangle();

   float m_XMinValue = 0;
   float m_XMaxValue = 0;

   float m_YMinValue = 0;
   float m_YMaxValue = 0;

   GregorianCalendar m_StartTime = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
   int m_TimeRange = 0;

   float m_XCalcPointFactor = 0;
   float m_YCalcPointFactor = 0;

   Point m_XAxisStartPos = new Point();
   Point m_XAxisEndPos   = new Point();
   Point m_YAxisStartPos = new Point();
   Point m_YAxisEndPos   = new Point();

   int m_XNrOfGrids = 0; // subdivision of grid
   int m_YNrOfGrids = 0;

   int m_AxisOverlap = 5;

   int m_SliderHeight = 10; // Set in SetClientAreaDP
   Font m_Font;

   SnowPackDoc spDoc; // needed just to know current background color
                      // to draw dotted line in DrawGrids; currently not used


   // Constructor
   public Graph(SnowPackDoc spDoc)
   {
      this.spDoc = spDoc;

      m_Font = new Font("Dialog", Font.PLAIN, 10);
   }


// ClientRect:   Pos + Size of Graph including axis text and border in device coordinates
// GraphRect:    Pos + Size of Graph in device coordinates
// GraphRect is determined by processing the input parameters
void SetClientAreaDP(Graphics g, Rectangle ClientRect,
                                 int NrOfYAxisCharLeft, int NrOfYAxisCharRight,
                                 int NrOfBottomLines,   int NrOfTopLines )
{

   m_GraphRect = ClientRect; // first guess for m_GraphRect

   g.setFont(m_Font);

   FontMetrics fm = g.getFontMetrics();

   //int yBorderLeft  = (NrOfYAxisCharLeft  + 1) * tm.tmAveCharWidth + tm.tmHeight; // x char + 1 blank + axis description height
   //int yBorderRight = (NrOfYAxisCharRight + 1) * tm.tmAveCharWidth + tm.tmHeight; // x char + 1 blank + axis description height
   //int xBorder = tm.tmHeight + tm.tmExternalLeading;

   // x char + 1 blank + axis description height
   int yBorderLeft  = (NrOfYAxisCharLeft  + 1) * fm.charWidth('a') + fm.getHeight();
   int yBorderRight = (NrOfYAxisCharRight + 1) * fm.charWidth('a') + fm.getHeight();
   int xBorder = fm.getHeight(); // (possibly + fm.fmExternalLeading)

   // Reduction of GraphRect:
   //   to the left/right: by yBorderLeft, yBorderRight
   //   to the top/bottom: by NrOfTopLines * xBorder, NrOfBottomLines * xBorder
   // x,y is upper left corner

   m_GraphRect.x += yBorderLeft;
   m_GraphRect.y += NrOfTopLines * xBorder;
   m_GraphRect.height -= (NrOfTopLines + NrOfBottomLines) * xBorder;
   m_GraphRect.width  -= yBorderLeft + yBorderRight;

   // Set Height of Slider
   m_SliderHeight = xBorder;
}


int SetXAxis(float MinValue, float MaxValue, int NrOfGrids )
{
   m_XMinValue = MinValue;
   m_XMaxValue = MaxValue;
   m_XNrOfGrids = NrOfGrids;

   return 0;
}


// TimeRange in seconds
int SetXAxis(GregorianCalendar StartTime, int TimeRange, int NrOfGrids )
{
	m_StartTime = StartTime;
	m_TimeRange = TimeRange;

  //float test = (float) m_StartTime.getTime().getTime();

	return SetXAxis((float) (m_StartTime.getTime().getTime() / 1000),
         (float) (m_StartTime.getTime().getTime()/1000 + TimeRange), NrOfGrids);
}


int SetYAxis(float MinValue, float MaxValue, int NrOfGrids )
{
   m_YMinValue = MinValue;
   m_YMaxValue = MaxValue;
   m_YNrOfGrids = NrOfGrids;

   return 0;
}


// calculate the dimensions of the x and y axis
void CalcCoordinateSystem()
{
  int LenXAxis = 0;
  int LenYAxis = 0;
  int LenXAxisNegativ = 0;	// negativ part of XAxis
  int LenYAxisNegativ = 0;	// negativ part of YAxis

	// **** Calculate axis dimension and positions
	LenXAxis = m_GraphRect.width;
	LenYAxis = m_GraphRect.height;

	if ( m_XMinValue != m_XMaxValue && m_XMinValue < 0 )
	{
    // + 0.5 because of rounding
    // m_XMinValue, m_XMaxValue: min- and max-value on x-axis
		LenXAxisNegativ = (int) ((0 - m_XMinValue) / ( m_XMaxValue - m_XMinValue) * LenXAxis + 0.5);
	}

	if ( m_YMinValue != m_YMaxValue && m_YMinValue < 0 )
	{
		LenYAxisNegativ = (int) ((0 - m_YMinValue) / ( m_YMaxValue - m_YMinValue) * LenYAxis + 0.5);
	}

	// Note: m_GraphRect.x/y defines left/top-corner.
  //       m_YAxisEndPos.y (at top) < m_YAxisStartPos.y (at bottom)
	m_XAxisStartPos.x = m_GraphRect.x;
	m_XAxisStartPos.y = m_GraphRect.y + LenYAxis - LenYAxisNegativ;

	m_XAxisEndPos.x = m_XAxisStartPos.x + LenXAxis;
	m_XAxisEndPos.y = m_XAxisStartPos.y;

	m_YAxisStartPos.x = m_XAxisStartPos.x  + LenXAxisNegativ;
	m_YAxisStartPos.y = m_GraphRect.y + LenYAxis;

	m_YAxisEndPos.x = m_YAxisStartPos.x;
	m_YAxisEndPos.y = m_GraphRect.y;

	// Calculate factors for CalcPointLP function:
  // Factor expresses how many pixels one unit on the x/y-axis represents
	if ( m_XMinValue != m_XMaxValue )
	{
		m_XCalcPointFactor = LenXAxis / ( m_XMaxValue - m_XMinValue);
	}

	if ( m_YMinValue != m_YMaxValue )
	{
		m_YCalcPointFactor = LenYAxis / ( m_YMaxValue - m_YMinValue);
	}

  return;
}


void DrawGrids(Graphics g, boolean sliderSpace)
// Draw division of coordinate system (dotted lines)
// sliderSpace = true: vertical dotted lines below x-axis are drawn longer
//   (JSlider is put over them)
{
  Point GridStartPos = new Point();
  Point GridEndPos = new Point();
  float	   GridOffset;
  //DottedLine dl = new DottedLine();
  //dl.setDefaultStyle(DottedLine.STYLE_2DOT_2SPACE);

	// Draw x-grids (vertical lines)
	if (m_XNrOfGrids > 0)
	{
		GridStartPos.x = m_XAxisStartPos.x;
		GridStartPos.y = m_YAxisStartPos.y;
		GridEndPos.y = m_YAxisEndPos.y;
		GridOffset = (m_XAxisEndPos.x - m_XAxisStartPos.x) / (float) m_XNrOfGrids;

		for (int i=0; i<=m_XNrOfGrids; i++ )
		{
			if (GridStartPos.x - 1 > m_YAxisStartPos.x ||
			 	  GridStartPos.x + 1 < m_YAxisStartPos.x )
			{
				// do not draw lines at y-axis position
				// GridStartPos +1 / -1 because of GridOffset round errors

        int space = 0;
        if (sliderSpace) space = m_SliderHeight;

        for (int j=GridStartPos.y + m_AxisOverlap + space;
                 j>GridEndPos.y - m_AxisOverlap; j-=4)
        {
            g.drawLine(GridStartPos.x, j, GridStartPos.x, j-1);
        }
			}

			GridStartPos.x = m_XAxisStartPos.x + (int) ((i+1) * GridOffset + 0.5);

		} // for
	} // if

	// Draw y-grids (horizontal lines)
	if (m_YNrOfGrids > 0)
	{
		GridStartPos.y = m_YAxisStartPos.y;
		GridStartPos.x = m_XAxisStartPos.x;
		GridEndPos.x = m_XAxisEndPos.x;
		GridOffset = (m_YAxisEndPos.y - m_YAxisStartPos.y) / (float) m_YNrOfGrids;

		for (int i=0; i<=m_YNrOfGrids; i++ )
		{
			if (GridStartPos.y - 1 > m_XAxisStartPos.y ||
				 GridStartPos.y + 1 < m_XAxisStartPos.y )
			{
				// do not draw lines at x-axis position
				// GridStartPos +1 / -1 because of GridOffset round errors

        for (int j=GridStartPos.x - m_AxisOverlap;
                 j<GridEndPos.x + m_AxisOverlap; j+=4)
        {
            g.drawLine(j, GridStartPos.y, j+1, GridStartPos.y);
        }

			}
			GridStartPos.y = m_YAxisStartPos.y + (int) ((i+1) * GridOffset + 0.5);
		} // for
	} // if

  return;
}


/* Version using the DottedLine class; lines are just drawn if program started
   //within JBuilder environment
void DrawGrids(Graphics g)
// Draw division of coordinate system (dotted lines)
{
  Point GridStartPos = new Point();
  Point GridEndPos = new Point();
  float	   GridOffset;
  DottedLine dl = new DottedLine();
  dl.setDefaultStyle(DottedLine.STYLE_2DOT_2SPACE);

	// Draw x-grids (vertical lines)
	if (m_XNrOfGrids > 0)
	{
		GridStartPos.x = m_XAxisStartPos.x;
		//GridEndPos.x = m_XAxisStartPos.x; //only used for g.drawLine
		GridStartPos.y = m_YAxisStartPos.y;
		GridEndPos.y = m_YAxisEndPos.y;
		GridOffset = (m_XAxisEndPos.x - m_XAxisStartPos.x) / (float) m_XNrOfGrids;

		for (int i=0; i<=m_XNrOfGrids; i++ )
		{
			if (GridStartPos.x - 1 > m_YAxisStartPos.x ||
			 	  GridStartPos.x + 1 < m_YAxisStartPos.x )
			{
				// do not draw lines at y-axis position
				// GridStartPos +1 / -1 because of GridOffset round errors

        if (spDoc.m_Background == Color.black)
        // following line is drawn in white
        // dotted line (always drawn in black) is overlaid afterwards
        // --> white dotted line appears
        {
            g.drawLine(GridStartPos.x, GridStartPos.y + m_AxisOverlap,
                       GridStartPos.x, GridEndPos.y - m_AxisOverlap);
        }

        // dl always draws in black
        dl.drawVLine(g, GridStartPos.x,
           GridStartPos.y + m_AxisOverlap, GridEndPos.y - m_AxisOverlap);

        //solid line:
        //g.drawLine(GridStartPos.x, GridStartPos.y + m_AxisOverlap,
        //           GridEndPos.x,   GridEndPos.y - m_AxisOverlap);
			}

			GridStartPos.x = m_XAxisStartPos.x + (int) ((i+1) * GridOffset + 0.5);
			//GridEndPos.x = GridStartPos.x; //only used for g.drawLine

		} // for
	} // if

	// Draw y-grids (horizontal lines)
	if (m_YNrOfGrids > 0)
	{
		GridStartPos.y = m_YAxisStartPos.y;
		//GridEndPos.y = m_YAxisStartPos.y; //only used for g.drawLine
		GridStartPos.x = m_XAxisStartPos.x;
		GridEndPos.x = m_XAxisEndPos.x;
		GridOffset = (m_YAxisEndPos.y - m_YAxisStartPos.y) / (float) m_YNrOfGrids;

		for (int i=0; i<=m_YNrOfGrids; i++ )
		{
			if (GridStartPos.y - 1 > m_XAxisStartPos.y ||
				 GridStartPos.y + 1 < m_XAxisStartPos.y )
			{
				// do not draw lines at x-axis position
				// GridStartPos +1 / -1 because of GridOffset round errors

        if (spDoc.m_Background == Color.black)
        {
           g.drawLine(GridStartPos.x - m_AxisOverlap, GridStartPos.y,
                      GridEndPos.x + m_AxisOverlap, GridStartPos.y);
        }

        // dl always draws in black
        dl.drawHLine(g, GridStartPos.x - m_AxisOverlap,
                     GridEndPos.x + m_AxisOverlap, GridStartPos.y);

				//g.drawLine(GridStartPos.x - m_AxisOverlap, GridStartPos.y,
        //           GridEndPos.x + m_AxisOverlap, GridEndPos.y); //solid line
			}
			GridStartPos.y = m_YAxisStartPos.y + (int) ((i+1) * GridOffset + 0.5);
			//GridEndPos.y = GridStartPos.y; //only used for g.drawLine
		} // for
	} // if

  return;
}
*/

void DrawCoordinateAxes(Graphics g) {

	// **** Draw coordinate system
	// Draw x axis
	g.drawLine(m_XAxisStartPos.x - m_AxisOverlap, m_XAxisStartPos.y,
               m_XAxisEndPos.x + m_AxisOverlap, m_XAxisEndPos.y);

  // Draw y axis
	g.drawLine(m_YAxisStartPos.x,  m_YAxisStartPos.y + m_AxisOverlap,
             m_YAxisEndPos.x,   m_YAxisEndPos.y - m_AxisOverlap);

 	return;
}


void DrawGroundSurface(Graphics g)
{
/*// Draw thick x axis (thickness = 2 pixel)
	g.drawLine(m_XAxisStartPos.x - m_AxisOverlap, m_XAxisStartPos.y,
               m_XAxisEndPos.x + m_AxisOverlap, m_XAxisEndPos.y);
	g.drawLine(m_XAxisStartPos.x - m_AxisOverlap, m_XAxisStartPos.y + 1,
               m_XAxisEndPos.x + m_AxisOverlap, m_XAxisEndPos.y + 1);
*/
	g.drawLine(m_XAxisStartPos.x - m_AxisOverlap, m_XAxisStartPos.y,
               m_XAxisEndPos.x + m_AxisOverlap, m_XAxisEndPos.y);

  // Draw slanted lines
  int HoriDist = 10; // horizontal distance between lines
  int VertLength = 10; // vertical distance between bottom and top of lines
  int Displacement = -7; // horizontal displacement between bottom and top of lines

  for (int i=m_XAxisStartPos.x; i<m_XAxisEndPos.x+10; i+=HoriDist)
  {
	  g.drawLine(i,                m_XAxisStartPos.y,
               i + Displacement, m_XAxisStartPos.y + VertLength);
  }

}


// Draw x-axis text. Used e.g. for color bar and right side graph.
// Line: number of line below the x-axis, in which numbers are plotted
// Called after DrawCoordinateAxis.
//
void DrawXAxisText(Graphics g, float Line, int NrOfDiv, int DigitsAfterDecPoint)
{
	// Draw x axis text. Algorithm like draw grid.
  if ( NrOfDiv > 0 )
	{
	int	i;
	Point	 GridStartPos = new Point();
	float	 GridOffset;
	String AxisValueStr;
	Point	 SizeText = new Point(); // Dimension might be used instead of Point!
  int    TextStartPosX;
  int    LastTextEndPosX;
  int    Ascent;

	Float AxisValue = new Float(m_XMinValue);
	float ValueOffset = (m_XMaxValue - m_XMinValue) / NrOfDiv;

	GridStartPos.x = m_XAxisStartPos.x;
  GridOffset = (m_XAxisEndPos.x - m_XAxisStartPos.x) / (float) NrOfDiv;

  g.setFont(m_Font);
  FontMetrics fm = g.getFontMetrics();
  SizeText.y = fm.getHeight();
  Ascent = fm.getAscent();

  GridStartPos.y = m_YAxisStartPos.y + m_AxisOverlap + Ascent +
                      (int) ((Line - 1) * SizeText.y + 0.5);

  LastTextEndPosX = 0;

	for (i=0; i<=NrOfDiv; i++ )
		{
      // Calculate AxisValueStr from AxisValue
      AxisValueStr = ValueToString(AxisValue, DigitsAfterDecPoint);

			// Get text size in logical units
			SizeText.x = fm.stringWidth(AxisValueStr);

      // Don't draw if text overlaps
      TextStartPosX = GridStartPos.x - SizeText.x / 2;

      if ( TextStartPosX > LastTextEndPosX )
      {
         // g.drawString with logical coordinates
			   g.drawString(AxisValueStr, TextStartPosX, GridStartPos.y);

         LastTextEndPosX = (int) (TextStartPosX + SizeText.x * 1.1 + 0.5);
      }
			GridStartPos.x = m_XAxisStartPos.x + (int) ((i+1) * GridOffset + 0.5);
			AxisValue = new Float (AxisValue.floatValue() + ValueOffset);
		} // for

	} // if (NrOfDiv > 0 )
}


// Draw x-axis text as date and time.
// Called after DrawCoordinateAxes.
// AxisTextStyleFlags: SHORT_DATE, FULL_DATE.
// SHORT_DATE: Time = "21:51",    Date = "31.12."
// FULL_DATE:  Time = "21:51:00", Date = "31.12.98"
void DrawXAxisDateText(Graphics g, int NrOfDiv, String AxisTextStyleFlags)
{
	// Draw x axis text. Algorithm like draw grid.
	if ( NrOfDiv > 0 )
	{
	int		 i;
	Point  GridStartPos = new Point();
	float  GridOffset;
	String AxisValueStr = null;
	Point	 SizeText = new Point();
  int    TextStartPosX;
  int    LastTextEndPosX;
  int    LastTextSizeX;
  int    Ascent;

	GregorianCalendar AxisTime = (GregorianCalendar) m_StartTime.clone();  // = approx. m_XMinValue
	int ValueOffset = (int) (m_TimeRange / NrOfDiv + 0.5);

  GridStartPos.x = m_XAxisStartPos.x;
	GridStartPos.y = m_YAxisStartPos.y;
	GridOffset = (m_XAxisEndPos.x - m_XAxisStartPos.x) / (float) NrOfDiv;

  g.setFont(m_Font);
  FontMetrics fm = g.getFontMetrics();
  SizeText.y = fm.getHeight();
  Ascent = fm.getAscent();

  LastTextEndPosX = 0;
  LastTextSizeX = 0;

	for ( i = 0; i <= NrOfDiv; i++ )
	{
			// Format time as e.g. 13:51:00
			if ( AxisTextStyleFlags.equals("SHORT_DATE")) // hh:mm (hour, minute)
				AxisValueStr = TimeToString(AxisTime).substring(9,14);
			else // hh.mm.ss (hour, minute, second)
        AxisValueStr = TimeToString(AxisTime).substring(9,17);


			// Get text size in logical units
			SizeText.x = fm.stringWidth(AxisValueStr);

      // Don't draw if text overlaps
      TextStartPosX = GridStartPos.x - SizeText.x / 2;
      LastTextSizeX = SizeText.x;

      if ( TextStartPosX > LastTextEndPosX )
      {
			   // Draw time in first line
			   g.drawString(AxisValueStr, TextStartPosX, GridStartPos.y
              + m_AxisOverlap + Ascent + m_SliderHeight); // was 2x m_AxisOverlap before

			   // Format date as e.g. 31.12.98
			   if ( AxisTextStyleFlags.equals("SHORT_DATE"))  // dd.mm (day, month)
				   AxisValueStr = TimeToString(AxisTime).substring(0,5);
			   else  // dd.mm.yy (day, month, year)
				   AxisValueStr = TimeToString(AxisTime).substring(0,8);

			   // Get text size in logical units
			   SizeText.x = fm.stringWidth(AxisValueStr);

         if ( SizeText.x > LastTextSizeX )  LastTextSizeX = SizeText.x;

			   // Draw date in second line
			   g.drawString(AxisValueStr, GridStartPos.x - SizeText.x / 2,
           GridStartPos.y + m_AxisOverlap + SizeText.y + Ascent + m_SliderHeight); //was 3 x AxisOverlap

         LastTextEndPosX = (int) (TextStartPosX + LastTextSizeX * 1.1 + 0.5);
      }
			GridStartPos.x = m_XAxisStartPos.x + (int) ((i+1) * GridOffset + 0.5);
			AxisTime.add(Calendar.SECOND, ValueOffset);
		} // for

	} // if (NrOfDiv > 0 )
}



// Draw centered date below the coordinate system.
// Called after DrawCoordinateAxes.
// AxisTextStyleFlags: SHORT_DATE, FULL_DATE.
// SHORT_DATE: Time = "21:51",    Date = "31.12."
// FULL_DATE:  Time = "21:51:00", Date = "31.12.98"
void DrawCenteredDate(Graphics g, GregorianCalendar xTime, String AxisTextStyleFlags)
{
	Point  GridStartPos = new Point();
	String AxisValueStr = null;
	Point	 SizeText = new Point();
  int    Ascent;

  GridStartPos.x = m_XAxisStartPos.x + (m_XAxisEndPos.x - m_XAxisStartPos.x) / 2;
	GridStartPos.y = m_YAxisStartPos.y;

  g.setFont(m_Font);
  FontMetrics fm = g.getFontMetrics();
  SizeText.y = fm.getHeight();
  Ascent = fm.getAscent();

  // Format time as e.g. 13:51:00
 	if ( AxisTextStyleFlags.equals("SHORT_DATE")) // hh:mm (hour, minute)
				AxisValueStr = "Time: " + TimeToString(xTime).substring(9,14);
 	else // hh.mm.ss (hour, minute, second)
        AxisValueStr = "Time: " + TimeToString(xTime).substring(9,17);

	// Get text size in logical units
 	SizeText.x = fm.stringWidth(AxisValueStr);

  // Erase time characters from previous drawing
  g.setColor(spDoc.m_Background);
  g.fillRect(m_XAxisStartPos.x, GridStartPos.y + m_AxisOverlap,
              m_XAxisEndPos.x - m_XAxisStartPos.x,
              SizeText.y);
  g.setColor(spDoc.m_CurrentColor);

  // Draw time in first line
  g.drawString(AxisValueStr, GridStartPos.x - SizeText.x / 2,
               GridStartPos.y + m_AxisOverlap + Ascent); // was 2 x AxisOverlap

  // Format date as e.g. 31.12.98
  if ( AxisTextStyleFlags.equals("SHORT_DATE"))  // dd.mm (day, month)
				   AxisValueStr = "Date:  " + TimeToString(xTime).substring(0,5);
  else  // dd.mm.yy (day, month, year)
				   AxisValueStr = "Date:  " + TimeToString(xTime).substring(0,8);

  // Get text size in logical units
  SizeText.x = fm.stringWidth(AxisValueStr);

  // Erase date characters from previous drawing
  g.setColor(spDoc.m_Background);
  g.fillRect(m_XAxisStartPos.x, GridStartPos.y + m_AxisOverlap + SizeText.y,
             m_XAxisEndPos.x - m_XAxisStartPos.x,
             SizeText.y);
  g.setColor(spDoc.m_CurrentColor);

  // Draw date in second line
  g.drawString(AxisValueStr, GridStartPos.x - SizeText.x / 2,
               GridStartPos.y + m_AxisOverlap + SizeText.y + Ascent); // was 3 x AxisOverlap
}


// Draw y-axis text.
// Called after DrawCoordinateAxes.
// AxisTextStyleFlag1: ALIGN_LEFT, ALIGN_RIGHT (text left or right of proper plot)
// AxisTextStyleFlag2: ALIGN_TOP, ALIGN_BOTTOM (related to ParameterName and Unit)
void DrawYAxisText(Graphics g, int NrOfDiv, int DigitsAfterDecPoint,
     String AxisTextStyleFlag1, String AxisTextStyleFlag2,
		 String ParameterName, String Unit)
{
	// Draw y axis text. Same algorithm as draw grid.
	if ( NrOfDiv > 0 )
	{
	int MaxTextExtent = 0;
  int TextXOffset;
	Point GridStartPos = new Point();
	float GridOffset;
	String AxisValueStr;
	Point	SizeText = new Point();
  int Ascent;

	Float AxisValue = new Float(m_YMinValue);
	float ValueOffset = (m_YMaxValue - m_YMinValue) / NrOfDiv;

  if ( AxisTextStyleFlag1.equals("ALIGN_LEFT"))
	{
			GridStartPos.x = m_XAxisStartPos.x;
	}
	else // ALIGN_RIGHT
	{
			GridStartPos.x = m_XAxisEndPos.x;
	}

	GridStartPos.y = m_YAxisStartPos.y;
	GridOffset = (m_YAxisEndPos.y - m_YAxisStartPos.y) / (float) NrOfDiv;

  g.setFont(m_Font);
  FontMetrics fm = g.getFontMetrics();

  SizeText.x = fm.stringWidth("0");
  TextXOffset = SizeText.x / 3 + m_AxisOverlap;

  for (int i=0; i <= NrOfDiv; i++ )
	{
      // Calculate AxisValueStr from AxisValue
      AxisValueStr = ValueToString(AxisValue, DigitsAfterDecPoint);

			// Get text size in logical units
			SizeText.x = fm.stringWidth(AxisValueStr);

      // Character height: max. (Ascent+Decent) of all characters would be
      //                   more precise (but numbers have no Decent)
      SizeText.y = fm.getAscent(); //+ fm.getDescent();

			if ( SizeText.x > MaxTextExtent )
			{
				MaxTextExtent = SizeText.x;
			}

			// If first: position lifted by half character height.
			if (i==0) SizeText.y = 0;

			if ( AxisTextStyleFlag1.equals("ALIGN_LEFT"))
			{
				g.drawString(AxisValueStr, GridStartPos.x - SizeText.x - TextXOffset,
				        GridStartPos.y + SizeText.y / 2 );
			}
			else  // ALIGN_RIGHT
			{
				g.drawString(AxisValueStr, GridStartPos.x + TextXOffset,
				        GridStartPos.y + SizeText.y / 2);
			}

			GridStartPos.y = m_YAxisStartPos.y + (int) ((i+1) * GridOffset + 0.5);
      AxisValue = new Float (AxisValue.floatValue() + ValueOffset);
  } // for

	// finished with y-axis text-values



	// ***************************************
	// draw ParameterName and Unit (vertically)

  if ( ParameterName != null &&  Unit != null)
			AxisValueStr = ParameterName + " (" + Unit + ")";
	else
      AxisValueStr = "";

  fm = g.getFontMetrics(); //same font as before
  SizeText.x = fm.stringWidth(AxisValueStr);
  SizeText.y = fm.getHeight();

  // Rotate font by 90 degrees
  AffineTransform at = new AffineTransform();
  at.setToRotation(3 * Math.PI / 2);
  Font verticalFont = (Font) m_Font.deriveFont(at);
  g.setFont(verticalFont);

  //System.out.println("SizeText: " + SizeText.x + " " + SizeText.y);

	// Draw text at left or right side of x axis
	if ( AxisTextStyleFlag1.equals("ALIGN_LEFT"))
  {
				GridStartPos.x = m_XAxisStartPos.x - MaxTextExtent - 2 * TextXOffset;
	}
	else // ALIGN_RIGHT
	{
				GridStartPos.x = m_XAxisEndPos.x + MaxTextExtent + 2 * TextXOffset;
	}

  // Plot text at the top, center or bottom of the left/right side
	if ( AxisTextStyleFlag2.equals("ALIGN_TOP"))
	{
				GridStartPos.y = m_YAxisEndPos.y + SizeText.x;
 	}
	else if ( AxisTextStyleFlag2.equals("ALIGN_BOTTOM"))
	{
				GridStartPos.y = m_YAxisStartPos.y - m_AxisOverlap;
	}
	else // align in center of y-axis
	{
				GridStartPos.y = m_YAxisStartPos.y + (m_YAxisEndPos.y - m_YAxisStartPos.y) / 2;
				GridStartPos.y = GridStartPos.y - SizeText.x / 2;
	}

  g.drawString(AxisValueStr, GridStartPos.x, GridStartPos.y);

	} // if (NrOfDiv > 0 )
}




// Draw text below x axis or above the proper plot
// If n > 0: line n below x axis
//    n < 0: above the plot (e.g. n = -2 means second line above plot)
// Normally the first and second line are used for axis description.
// AxisTextStyleFlags: ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT.
// AxisTextStyleFlags1: BOLD, PLAIN, ITALIC
// FontSize other than 10 not very clean because GraphRect is calculated with constant height
// Font could be set by SetFont before
// ValueStr = e.g."Klosters, 1300 m", "Schneetemperatur [C]"
// erase = true, if characters from previous drawing should be erased before
void DrawAboveBelowText(Graphics g, float Line, String AxisTextStyleFlags,
     String AxisTextStyleFlags1, int FontSize, String ValueStr, boolean erase)
{
	 Point	GridStartPos = new Point();
	 Point	SizeText = new Point();
   int Ascent;
   Font font = null;

   if (AxisTextStyleFlags1.equals("BOLD"))
     font = new Font("Dialog", Font.BOLD, FontSize);
   else if (AxisTextStyleFlags1.equals("ITALIC"))  //Schirmer
     font = new Font("Dialog", Font.ITALIC, FontSize);
   else
     font = new Font("Dialog", Font.PLAIN, FontSize);

   g.setFont(font);
   FontMetrics fm = g.getFontMetrics();
   SizeText.y = fm.getHeight();
   SizeText.x = fm.stringWidth(ValueStr);
   Ascent = fm.getAscent();

   if ( AxisTextStyleFlags.equals("ALIGN_RIGHT"))
   {
	   GridStartPos.x = m_XAxisEndPos.x - SizeText.x;
   }
   else if ( AxisTextStyleFlags.equals("ALIGN_CENTER"))
   {
     // Go to center position, than subtract half text size
	   GridStartPos.x = m_XAxisStartPos.x + (m_XAxisEndPos.x - m_XAxisStartPos.x) / 2;
	   GridStartPos.x = GridStartPos.x - SizeText.x / 2;
   }
   else // default ALIGN_LEFT
   {
	   GridStartPos.x = m_XAxisStartPos.x;
   }

   if (Line > 0) // text below plot
      GridStartPos.y = m_YAxisStartPos.y + Ascent +
                       m_AxisOverlap + (int) ((Line - 1) * SizeText.y + 0.5); // 0.5 for rounding
   else // text above plot, Line < 0 //Schirmer: changed from m_YAxisEndPos.y to m_YAxisStartPos.y
      GridStartPos.y = m_YAxisStartPos.y -
                       m_AxisOverlap + (int) ((Line+1) * SizeText.y + 0.5);

   // Erase characters from previous drawing
   if (erase)
   {
     g.setColor(spDoc.m_Background);
     g.fillRect(m_XAxisStartPos.x, GridStartPos.y - Ascent,
                2 * (m_XAxisEndPos.x - m_XAxisStartPos.x),
                SizeText.y);

     g.setColor(spDoc.m_CurrentColor);
   }

   // Draw the string
   g.drawString(ValueStr, GridStartPos.x, GridStartPos.y);

}


// Calculate the logical coordinates from the x and y value
Point CalcPointLP(float x, float y)
{
  Point RetPoint = new Point();
  RetPoint.x = CalcXPosLP( x );
  RetPoint.y = CalcYPosLP( y );

	// C++: RetPoint.x = (long) ((x - m_XMinValue) * m_XCalcPointFactor + m_XAxisStartPos.x);
	//      RetPoint.y = (long) (-(y - m_YMinValue) * m_YCalcPointFactor + m_YAxisStartPos.y);

	return RetPoint;
}


int CalcXPosLP(GregorianCalendar xTime)
{
/* tests
   Date m_StartDate = m_StartTime.getTime();
   long m_Datemillisec = m_StartDate.getTime();
   Date d = new Date(1000);
   long dmillis = d.getTime();

   int year1 = m_StartTime.get(Calendar.YEAR);
   int month1 = m_StartTime.get(Calendar.MONTH)+1;
   int day1 = m_StartTime.get(Calendar.DAY_OF_MONTH);
   int hour1 = m_StartTime.get(Calendar.HOUR_OF_DAY);
   int minute1 = m_StartTime.get(Calendar.MINUTE);
   int second1 = m_StartTime.get(Calendar.SECOND);

   int year = xTime.get(Calendar.YEAR);
   int month = xTime.get(Calendar.MONTH)+1;
   int day = xTime.get(Calendar.DAY_OF_MONTH);
   int hour = xTime.get(Calendar.HOUR_OF_DAY);
   int minute = xTime.get(Calendar.MINUTE);
   int second = xTime.get(Calendar.SECOND);

   long xTimeint = xTime.getTime().getTime();
   long m_StartTimeint = m_StartTime.getTime().getTime();
   long Differenz = xTimeint - m_StartTimeint;

   float x = m_XCalcPointFactor;
   int y = m_XAxisStartPos.x;
*/
   return (int) ((xTime.getTime().getTime() - m_StartTime.getTime().getTime())
                * m_XCalcPointFactor / 1000 + m_XAxisStartPos.x);
}


// Calculate the logical coordinate from the x value
int CalcXPosLP(float x)
{
   return (int) ((x - m_XMinValue) * m_XCalcPointFactor + m_XAxisStartPos.x + 0.5);
}


// Calculate the logical coordinate from the y value
int CalcYPosLP(float y)
{
   return (int) (-(y - m_YMinValue) * m_YCalcPointFactor + m_YAxisStartPos.y + 0.5); //?? -0.5
}


// Calculate the logical coordinates from the date and y value
// The x-axis must be set by SetXAxis(GregorianCalendar StartTime, int TimeRange, int NrOfGrids)
Point CalcPointLP(GregorianCalendar xTime, float y)
{
   Point RetPoint = new Point();

	 RetPoint.x = CalcXPosLP( xTime);
	 // more precise:
	 // RetPoint.x = (long) ((xTime.GetTime() - m_StartTime) /
   //     (double) m_TimeRange * (double) LenXAxis + m_XAxisStartPos.x);

   RetPoint.y = CalcYPosLP( y );

	return RetPoint;
}


static String ValueToString(Float AxisValue, int DigitsAfterDecPoint)
  {
      // Convert float AxisValue into String, show DigitsAfterDecPoint digits
      // If DigitsAfterDecPoint <=0: Integer displayed
      // Only up to four digits useful, due to elimination of values < 0.0001.
      int j;
      boolean isInteger = true;
      String AxisValueStr;
      Float AxisValue1 = new Float(AxisValue.floatValue());

      // Prevent errors due to scientific notation, e.g. 1.6e-6.
      // Not checked if such a notation may be used by Float also for numbers >1.
      if ((AxisValue.floatValue()<0.0001) && (AxisValue.floatValue()>-0.0001))
          AxisValue1 = new Float(0.0);

      String AxisValueStr1 = AxisValue1.toString();

      loop: {
        for (j=0; j<AxisValueStr1.length(); j++)
        {
           if (AxisValueStr1.substring(j,j+1).equals("."))
           {
              isInteger = false;
              break loop;
           }
           // Case not handled: point at last position of string
        }
      } // end loop

      if (isInteger)
      // No decimal point in input string encountered
         AxisValueStr = AxisValueStr1;
      else if (DigitsAfterDecPoint <=0)
      // Decimal point encountered, but none wished for display
         AxisValueStr = AxisValueStr1.substring(0, j);
      else if (AxisValueStr1.length() > j + 1 + DigitsAfterDecPoint)
      // Input string contains more digits than wished for display.
      // Round to last digit which is wished for display.
      {
         float temp;
         temp = Float.parseFloat(AxisValueStr1.substring(0, j+DigitsAfterDecPoint+2));
         temp = temp * (float) Math.pow(10.0, DigitsAfterDecPoint);
         temp = Math.round(temp);
         temp = temp / (float) Math.pow(10.0, DigitsAfterDecPoint);

         AxisValueStr = (new Float(temp)).toString();

         // Old version without rounding:
         //AxisValueStr = AxisValueStr1.substring(0, j+DigitsAfterDecPoint+1);
      }
      else if (AxisValueStr1.length() == j + 1 + DigitsAfterDecPoint)
      // Input string contains as many digits as wished for display
         AxisValueStr = AxisValueStr1;
      else
      // Input string contains less digits as wished for display; add zeros
      {
         AxisValueStr = AxisValueStr1;
         for (int k=0; k < j + 1 + DigitsAfterDecPoint - AxisValueStr1.length(); k++)
         {
            AxisValueStr = AxisValueStr + "0";
         }
      }
      return AxisValueStr;
   }


   // Converts a GregorianCalendar into a String.
   // Output format: "dd.mm.yy hh:mm:ss" (day.month.year hour:minute:second)
   //        position 01234567890123456
   static public String TimeToString(GregorianCalendar Time) {

      String YearStr, MonthStr, DayStr, HourStr, MinuteStr, SecondStr;

      Integer CalYear = new Integer(Time.get(Calendar.YEAR)%100);
      Integer CalMonth = new Integer(Time.get(Calendar.MONTH)+1); // MONTH = 0: January
      Integer CalDay = new Integer(Time.get(Calendar.DAY_OF_MONTH));
      Integer CalHour = new Integer(Time.get(Calendar.HOUR_OF_DAY));
      Integer CalMinute = new Integer(Time.get(Calendar.MINUTE));
      Integer CalSecond = new Integer(Time.get(Calendar.SECOND));

      if (CalYear.intValue() < 10)  YearStr = "0"+CalYear.toString();
                                else YearStr = CalYear.toString();
      if (CalMonth.intValue() < 10) MonthStr = "0"+CalMonth.toString();
                                else MonthStr = CalMonth.toString();
      if (CalDay.intValue() < 10)   DayStr = "0"+CalDay.toString();
                                else DayStr = CalDay.toString();
      if (CalHour.intValue() < 10)   HourStr = "0"+CalHour.toString();
                                else HourStr = CalHour.toString();
      if (CalMinute.intValue() < 10) MinuteStr = "0"+CalMinute.toString();
                                else MinuteStr = CalMinute.toString();
      if (CalSecond.intValue() < 10) SecondStr = "0"+CalSecond.toString();
                                else SecondStr = CalSecond.toString();

      return DayStr + "." + MonthStr + "." + YearStr + " " +
             HourStr + ":" + MinuteStr + ":" + SecondStr;
   }


   // Insert an arrow at the given height in the right hand graph
   void InsertArrow(Graphics g, float XMinValue, float XMaxValue, float height)
   {
      Point StartPoint = new Point();
      Point TopPoint = new Point();
      Point SidePoint1 = new Point();
      Point SidePoint2 = new Point();

      // Length of the arrow (in pixels), 20% of width of graph
      int ArrowLength = (int) (0.2 * (CalcXPosLP(XMaxValue) - CalcXPosLP(XMinValue)));

      // Lay down cruzial points for drawing the arrow
      StartPoint.x = CalcXPosLP(XMaxValue) - 1; // at right border of graph
      StartPoint.y = CalcYPosLP(height);

      TopPoint.x = CalcXPosLP(XMaxValue) - ArrowLength;
      TopPoint.y = StartPoint.y;

      SidePoint1.x = StartPoint.x - (int) (0.75 * ArrowLength);
      SidePoint1.y = StartPoint.y - (int) (0.15 * ArrowLength);
      SidePoint2.x = SidePoint1.x;
      SidePoint2.y = StartPoint.y + (int) (0.15 * ArrowLength);

      // Draw the arrow
      g.drawLine(StartPoint.x, StartPoint.y, TopPoint.x, TopPoint.y);
      g.drawLine(StartPoint.x, StartPoint.y-1,TopPoint.x,TopPoint.y-1); // thicker
      g.drawLine(SidePoint1.x, SidePoint1.y-1, TopPoint.x, TopPoint.y-1); // higher
      g.drawLine(SidePoint2.x, SidePoint2.y, TopPoint.x, TopPoint.y);
      g.drawLine(SidePoint1.x, SidePoint1.y, SidePoint2.x, SidePoint2.y);
   }

    private void jbInit() throws Exception {
    }

}
