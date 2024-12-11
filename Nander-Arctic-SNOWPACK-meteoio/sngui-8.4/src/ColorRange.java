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
// ColorRange: Treatment of color ranges (areas from start to end color)
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;


public class ColorRange
{
        float m_StartValue, m_MinValue, m_MaxValue;
        float m_ValueRange;
        int m_StartRed, m_StartGreen, m_StartBlue;
        float m_MulRed, m_MulGreen, m_MulBlue;
        
        // ColorRange construction
        ColorRange(Color StartRGB, Color EndRGB, 
                   float StartValue, float EndValue)
        {
            m_StartValue = StartValue;
            m_ValueRange = EndValue - StartValue; // refer to specific color interval
                        
            // Get min/max values (used for check if value within range)
            m_MinValue = StartValue < EndValue ? StartValue : EndValue; 
            m_MaxValue = EndValue > StartValue ? EndValue   : StartValue;
            
            // Extract the byte values of the single colors from the 32-bit RGB-colors
            m_StartRed   = StartRGB.getRed();
            m_StartGreen = StartRGB.getGreen();
            m_StartBlue  = StartRGB.getBlue();
                        
            // Get multiplicators for the GetColor method
            if (m_ValueRange != 0 )
            {
                m_MulRed   = (EndRGB.getRed() - m_StartRed) / m_ValueRange;
                m_MulGreen = (EndRGB.getGreen() - m_StartGreen) / m_ValueRange;
                m_MulBlue  = (EndRGB.getBlue() - m_StartBlue) / m_ValueRange;
            }
            else
            {
                m_MulRed = 0;
                m_MulGreen = 0;
                m_MulBlue = 0;
            }
        }
        
        
        boolean IsInRange (float Value)
        // Check if Value (parameter) is within the color range
        {
                if (Value >= m_MinValue && Value <= m_MaxValue) return true;
                return false;
        }
       

        public Color GetColor( float Value )
        // Determines the color for a given value
        {
            float NormValue;
            int RValue, GValue, BValue;
        
            NormValue = Value - m_StartValue;
            
            // RValue = (int) (m_StartRed +
            //          ((NormValue/m_ValueRange)* (m_EndRed - m_StartRed));
            RValue = (int) (m_StartRed   + (NormValue * m_MulRed));
            GValue = (int) (m_StartGreen + (NormValue * m_MulGreen));
            BValue = (int) (m_StartBlue  + (NormValue * m_MulBlue));
                
            return new Color(RValue, GValue, BValue);
        }
        

/* test        
        public void TracePalette( float StartValue, float Increment ) 
        // for a number of values (start value, incremented) color components are printed
        {
              Color OutColor;
              float Value = StartValue;

              System.out.println("TracePalette: ");
              
              for (int i = 0; i < 256; i++ )
              {
                OutColor = this.GetColor(Value);
                                    
                System.out.println("Value, red, green, blue="+
                  OutColor.getRed()+","+OutColor.getBlue()+","+OutColor.getGreen());
        
                Value += Increment;
              }
        }
*/        
}         

