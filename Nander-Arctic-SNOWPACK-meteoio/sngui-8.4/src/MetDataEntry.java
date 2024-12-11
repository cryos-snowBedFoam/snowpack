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
// MetDataEntry: Handles one met data entry (non-layer parameters)
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.util.*;
import java.io.*;

// Handles one met data line
public class MetDataEntry extends DataEntry implements C_DataFile
{

protected static String ModulName = "MetDataEntry";
DataLine m_pDataLine;

// MetDataEntry construction
public MetDataEntry()
{
   super();
   m_pDataLine = null;
}



// Check the Id-Code of the line and then create the DataLine object,
// which parses the line
//
// return:  new DataEntry if ok
//          null if error
public DataLine ParseDataLine(String Line) throws IOException
{
   String ProcName = ModulName + ".ParseDataLine";

   // create the right DataLine object
   m_pDataLine = new MetValuesDataLine();

   if ( m_pDataLine == null )
   {
      ErrorFile.write("ERR_MEMORY", 1, ProcName);
      return null;
   }


   // parse the line
   if ( m_pDataLine.ParseLine(Line) == false )
   {
      // delete DataLine object if line is not ok
      m_pDataLine = null;
   }

   return m_pDataLine;
}


public DataLine GetDataLine(int IdCode)
// IdCode not evaluated
{
   return m_pDataLine;
}


public GregorianCalendar GetTime()
{
   if ( m_pDataLine == null )
     return null;   // not found

   return ((MetValuesDataLine) m_pDataLine).GetTime();
}


public float GetMetData( int IdCode)
// Note: String version of method might be better
{
   int Index = IdCode - 1;
   float Value;
   if ( m_pDataLine != null &&
        Index < ((MetValuesDataLine) m_pDataLine).GetNrOfValues() )
   {
      Value = ((MetValuesDataLine) m_pDataLine).GetMetValue(Index);
   }
   else
   {
      Value = (float) -999.9;
   }

   return Value;
}


// Verifies if the data values exists and has reasonable values (e.g. NrOfLayers are ok).
// return   true  if ok
//          false if error
public boolean CheckDataValid( int IdCode )
{
   // >>>Keine Auswertung von IdCode
   if ( m_pDataLine == null )
      return false;

   if (((MetValuesDataLine) m_pDataLine).GetMetValue(IdCode - 1) == (float) -999.9) // error value
      return false;

   return true;
}

}