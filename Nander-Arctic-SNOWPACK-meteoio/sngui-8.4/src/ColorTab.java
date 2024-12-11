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
// ColorTab: Definition of color tables
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.util.*;


public class ColorTab
{
        float m_MinValue, m_MaxValue;
        Color m_MinValueColor;
        Color m_MaxValueColor;

        static Color BLK   = new Color(0,0,0);       // black
        static Color L_BLK = new Color(64,64,64);    // dark-grey
        static Color WHT   = new Color(255,255,255); // white
        static Color L_WHT = new Color(240,240,240); // light-grey

        static Color RED   = new Color(255,0,0);     // red
        static Color GRN   = new Color(0,255,0);     // green
        static Color D_GRN = new Color(0,127,0);     // dark-green
        static Color BLU   = new Color(0,0,255);     // blue
        static Color D_BLU = new Color(0,0,127);     // dark-blue

        static Color YEL   = new Color(255,255,0);   // yellow
        static Color MAG   = new Color(255,0,255);   // magenta
        static Color CYA   = new Color(0,255,255);   // cyan

        static Color TableColors[][] = {
        {L_BLK, L_WHT},                       // 0
        {L_WHT, BLU},                         // 1
        {L_WHT, RED},                         // 2
        {BLU,   RED},                         // 3
        {GRN,   MAG},                         // 4
        {BLU,   GRN, YEL, RED},               // 5
        {L_BLK, BLU, GRN, YEL, RED, MAG},     // 6
        {D_BLU, BLU, CYA, L_WHT, RED},        // 7
        {L_WHT, CYA, BLU, D_BLU},             // 8
        {GRN,   YEL, RED, MAG, BLU },         // 9
        {D_GRN, YEL, RED, BLU, L_WHT},        // 10
        {L_BLK, RED, YEL, L_WHT},             // 11
        {RED, RED, YEL, GRN, GRN, BLU, BLU  } // 12
        };

        static int NrOfColors[] = {2, 2, 2, 2, 2, 4, 6, 5, 4, 5, 5, 4, 7};
        // C++: m_NrOfColorRanges ( +1 )

        static String TableNames[] = {
        "Black - White",                                     // 0
        "White - Blue",                                      // 1
        "White - Red",                                       // 2
        "Blue - Red",                                        // 3
        "Green - Magenta",                                   // 4
        "Blue - Green - Yellow - Red",                       // 5
        "Black - Blue - Green - Yellow - Red - Magenta",     // 6
        "Snow Temperature: Blue - White - Red",              // 7
        "White - Cyan - Dark-blue",                          // 8
        "Green - Yellow - Red - Magenta - Blue",             // 9
        "Snow density: Green - Yellow - Red - Blue - White", // 10
        "Black - Red - Yellow - White",                      // 11
        "Red - Red - Yellow - Green - Green - Blue - Blue"   // 12
        };

        static int LAST_TABLE_INDEX = 12;

        Vector m_ColorRangeList;

        // ColorTab construction
        ColorTab()
        {
            m_MinValue = (float) 3.4e+038;     // maximum float value
            m_MaxValue = (float) -3.4e+038;    // minimum float value
            m_MinValueColor = new Color(0,0,0);
            m_MaxValueColor = new Color(0,0,0);

            m_ColorRangeList = new Vector(6,6);
        }

        // Add a new color range to the Vector m_ColorRangeList
        // Return:  true if successful
        //          false if error
        public boolean AddColorRange(Color StartRGB, Color EndRGB,
                                 float StartValue, float EndValue)
        {
            //try
            {
                m_ColorRangeList.addElement(
                    new ColorRange(StartRGB, EndRGB, StartValue, EndValue) );

                // Set min/max color ranges
                if ( StartValue < EndValue )
                {
                   if ( StartValue < m_MinValue )
                   {
                      m_MinValue = StartValue;
                      m_MinValueColor = StartRGB;
                   }
                   if ( EndValue > m_MaxValue )
                   {
                      m_MaxValue = EndValue;
                      m_MaxValueColor = EndRGB;
                   }
                }
                else // StartValue >= EndValue
                {
                   if ( EndValue < m_MinValue )
                   {
                      m_MinValue = EndValue;
                      m_MinValueColor = EndRGB;
                   }
                   if ( StartValue > m_MaxValue )
                   {
                      m_MaxValue = StartValue;
                      m_MaxValueColor = StartRGB;
                   }
                }

            }
            // catch (MemoryException e) {return false;}

            return true;
        }


        // Create the color table by using a predefined color table
        // Return:  true if successful
        //          false if error
        public boolean LoadPredefinedColorTable(int TableIndex,
                       float StartValue, float EndValue )
        {
           if ( TableIndex > LAST_TABLE_INDEX )
              return false;

           if ( NrOfColors[TableIndex] < 2 )
              return false;

           // Portion of total parameter range (e.g. Temperature -20 to 0)
           //   that falls within one color range
           float Offset = (EndValue - StartValue) / (NrOfColors[TableIndex] - 1);

           // Delete all color ranges stored in vector;
           m_ColorRangeList.removeAllElements();

           for ( int i = 0; i < (NrOfColors[TableIndex] - 1); i++ )
           {
              if ( AddColorRange( TableColors[TableIndex][i],   // StartRGB
                                  TableColors[TableIndex][i+1], // EndRGB
                                  StartValue,              // StartValue,
                                  StartValue + Offset ) )  // EndValue
                 StartValue += Offset; // start value for next color range
              else
                 return false;
            }

           return true;
        }


        public Color GetColor( float Value, int TableIndex )
        {
            // Check one by one if value is within the color ranges
            for (int i = 0; i < (NrOfColors[TableIndex] -1); i++ )
            {
                if ( ((ColorRange) m_ColorRangeList.elementAt(i)).IsInRange(Value) )
                  return ((ColorRange) m_ColorRangeList.elementAt(i)).GetColor(Value);
            }

            // Color not in any color range
            // m_MinValue = smallest value of all ranges, calculated by AddColorRange
            if ( Value < m_MinValue )      return m_MinValueColor;
            else if ( Value > m_MaxValue ) return m_MaxValueColor;
            else                           return new Color(0,0,0);  //no color range found, return black
        }
}


/* test of ColorTable
                ColorTab ct = new ColorTab(1);
                int StartValue = -20; int EndValue = 0;
                ct.LoadPredefinedColorTable(StartValue, EndValue);
                Color Col = new Color(0,0,0);
                Col = ct.GetColor(-10);
                System.out.println("ColorTable test:");
                System.out.println("Red:" + Col.getRed());
                System.out.println("Green: " + Col.getGreen());
                System.out.println("Blue: " + Col.getBlue());
*/

/* test: print color of background
               //Color col = new Color(0,0,0);
               //col = getBackground();
               //System.out.println("Background:");
               //System.out.println("    Red:" + col.getRed());
               //System.out.println("    Green: " + col.getGreen());
               //System.out.println("    Blue: " + col.getBlue());
*/
