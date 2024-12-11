///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualization
//Version:
//Copyright:    Copyright (c) 2001
//Author:       G. Spreitzhofer (based on a version from M. Steiniger)
//Organization: SLF
//Description:  Java-Version of SnowPack.
//Integrates the C++-Version of M. Steiniger
//       and the IDL-Version of M. Lehning/P.Bartelt.
///////////////////////////////////////////////////////////////////////////////
// ZValuesDataLine: Handles one line of the data file
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.util.StringTokenizer;
import java.io.*;

public class ZValuesDataLine extends DataLine implements C_DataFile
{

   protected static String ModulName = "ZValuesDataLine";

   protected int      m_NrOfValues;
   protected float    m_ValueArray[];

   // construction
   ZValuesDataLine()
   {
      super();
      m_NrOfValues = 0;
   }


   /**
    * Constructor.
    * Set all values directly without ParseLine.
    * (used if data are read from database)
    */
   ZValuesDataLine(int IdCode, int NrOfValues, float values[])
   {
      this();
      SetIdCode(IdCode);
      m_NrOfValues = NrOfValues;
      m_ValueArray = new float[m_NrOfValues];
      System.arraycopy(values,0,m_ValueArray,0,m_NrOfValues);
   }


   // Parse the data line with z-values
   // Format: <ID-Code>,<Nr Of Values>,<Value 1>, <Value 2> ...
   // Example:     0501,42,1.5,2.5,3.5,4.5,5.5,6.5...
   //
   // return:  true     ok
   //          false  error
   public boolean ParseLine(String Line) throws IOException
   {
      String ProcName = ModulName + ".ParseLine";

      if ( Line == null )
      {
         ErrorFile.write("ERR_PAR_FUNCTION", 1, ProcName);
         return false;
      }

      StringTokenizer st = new StringTokenizer(Line, ",");

      // Read ID code
      if ( !st.hasMoreTokens() )
      {
         // No tokens; this error should already have been filtered out before
         ErrorFile.write("ERR_LINE_SYNTAX", 2, ProcName);
         return false;
      }
      SetIdCode(Integer.parseInt(st.nextToken()));
      // already checked for number format exception in ProDataEntry

      // Read number of values
      if ( !st.hasMoreTokens() )
      {
         // Line contains just ID-Code or ID-Code+comma
         ErrorFile.write("ERR_LINE_SYNTAX", 3, ProcName);
         return false;
      }

      try
      {
      m_NrOfValues = Integer.parseInt(st.nextToken());

      if ( m_NrOfValues <= 0 || m_NrOfValues > MAX_NR_OF_LAYERS )
      {
         // Number of layers too small or too big
         if (m_NrOfValues != 0) ErrorFile.write("ERR_LINE_SYNTAX", 4, ProcName);
         return false;
      }

      } // end try
      catch (NumberFormatException e)
      {
         // Layer value contains non-numerical characters
         ErrorFile.write("ERR_LINE_SYNTAX", 5, ProcName);
         return false;
      }

      // Allocate memory for the float values
      // Catch MemoryException could be implemented here!!
      m_ValueArray = new float[m_NrOfValues];

      int i = 0;
      while (st.hasMoreTokens() && i < m_NrOfValues)
      {
         try {m_ValueArray[i] = (Float.valueOf(st.nextToken())).floatValue(); }
         catch (NumberFormatException e)
         {
             // Layer values are non-numeric characters
             ErrorFile.write("ERR_LINE_SYNTAX", 6, ProcName);
             return false;
         }

         //System.out.println("ZValuesDataLine, value: " + m_ValueArray[i]);
         i++;
      }

      if (st.hasMoreTokens() || i < m_NrOfValues)
      {
         // too few or too many layer values
         ErrorFile.write("ERR_LINE_SYNTAX", 7, ProcName);
         return false;
      }

      // Individual layers could be checked here for error thresholds (depending on ID-Code)!!

      return true;
   }


   public int  GetDataType()             { return Z_VALUES_DATA_LINE; }

   public boolean HasDate()              { return false; };

   public int GetNrOfValues()            { return m_NrOfValues; };

   public int GetNrOfSnowValues()
   // Number of values > 0
   {
      int NrOfSnowValues=0;
      for (int i=0; i<m_NrOfValues; i++)
      {
         if (m_ValueArray[i]>0) NrOfSnowValues++;
      }

      return NrOfSnowValues;
   }

   public float GetZValue( int LayerNr ) {
       /*//Schirmer
       if (LayerNr == -1) {
           return 100;
       }//end Schirmer*/
       return m_ValueArray[LayerNr];
   };

} // end class ZValuesDataLine



