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
// MetValuesTimeDataLine: Handles one line of the met data file
//                        (non-layer parameters)
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.util.*;
import java.io.*;


public class MetValuesDataLine extends DataLine implements C_DataFile
{

String m_ValString = "";
protected static String ModulName = "MetValuesDataLine";
protected float m_ValueArray[];
GregorianCalendar m_Time = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
int m_NrOfValues;
//String m_RegCode;
//String m_StaoCode;
String m_StatAbbrev;
String m_WindStat;
String m_SnowStat;

int m_IdCode;

// MetValuesDataLine construction
public MetValuesDataLine()
{
   super();

   m_NrOfValues = 0;
   //m_RegCode = "";
   //m_StaoCode = "";
   m_StatAbbrev = "";
   m_IdCode = 200;
}


// parse the data line
// Formats:
// 1. Not research version
//    <ID-Code>,<Station-ID1>,<Station-ID2>,<Date> <Time>,<Value 1>,<Value 2> ...<last Value>
//    Example:     0100,0130,0053,19.01.1999 07:31,1.3,2.8,161,-2.9,59,0,128.0,,0,25,50,100,0.4,-0.9,-1.8,-5.3,-14.6
// 2. Research version
//    <ID-Code>,<Station-Abbrev.+SnowStationCode>,<Date> <Time>,<Value 1>,<Value 2> ...<last Value>
//    Example:     0100,KLO3,19.01.1999 07:31,1.3,2.8,161,-2.9,59,0,128.0,,0,25,50,100,0.4,-0.9,-1.8,-5.3,-14.6

// return:  true  if ok
//          false if error
public boolean ParseLine(String Line) throws IOException
{
      String ProcName = ModulName + ".ParseLine";

      if ( Line == null )
      {
         ErrorFile.write("ERR_PAR_FUNCTION", 1, ProcName);
         return false;
      }

      StringTokenizer st = new StringTokenizer(Line, ",", true);
      // true: comma is also returned


      // Read ID code
      if ( !st.hasMoreTokens() )
      {
         // no data available
         ErrorFile.write("ERR_LINE_SYNTAX", 2, ProcName);
         return false;
      }

      // IdCode to determine which sort of PARDATA*.INI file is used
      try { m_IdCode = Integer.parseInt(st.nextToken());}
      catch (NumberFormatException e)
      {
         // Station ID data are non-numerical characters
         ErrorFile.write("ERR_LINE_SYNTAX", 3, ProcName);
         return false;
      }

      if ( st.hasMoreTokens() ) st.nextToken(); // get comma
      else { ErrorFile.write("Comma missing", 4, ProcName); return false; }


/*
      if (Setup.m_ResearchMode) // read m_StatAbbrev, m_SnowStat;
      {

         // Read station abbreviation
         if ( !st.hasMoreTokens() )
         {
            // Station abbreviation doesn't exist
            ErrorFile.write("ERR_LINE_SYNTAX", 5, ProcName);
            return false;
         }
         String tempStr = st.nextToken().trim().toUpperCase();

         Possibility to read 3-character station abbreviation and snow station ID;
         might be used to access station name and altitude from STATION.INI-file

         if ( tempStr.length() != 4)
         {
            // not four characters in String
            ErrorFile.write("ERR_LINE_SYNTAX", 4, ProcName);
            return false;
         }

         m_StatAbbrev = tempStr.substring(0, 3);
         m_SnowStat = tempStr.substring(3, 4); // should be 2 or 3



      }
      else // no research mode, read m_RegCode, m_StaoCode
      {

         // Read region code
         if ( !st.hasMoreTokens() )
         {
            // Region code doesn't exist
            ErrorFile.write("ERR_LINE_SYNTAX", 10, ProcName);
            return false;
         }
         m_RegCode = st.nextToken();

         if ( st.hasMoreTokens() ) st.nextToken(); // get comma
         else { ErrorFile.write("Comma missing", 11, ProcName); return false; }

         // Read station code
         if ( !st.hasMoreTokens() )
         {
            // Station code doesn't exist
            ErrorFile.write("ERR_LINE_SYNTAX", 12, ProcName);
            return false;
         }
         m_StaoCode = st.nextToken();

      } // end else (no research mode)


      if ( st.hasMoreTokens() ) st.nextToken(); // get comma
      else { ErrorFile.write("Comma missing", 13, ProcName); return false; }
*/
      // Read date/time information
      if ( !st.hasMoreTokens() )
      {
         // Date/time info does not exist
         ErrorFile.write("ERR_LINE_SYNTAX", 14, ProcName);
         return false;
      }

      String DateLine = st.nextToken();
      GregorianCalendar xTime = DateTimeDataLine.ScanDate(DateLine);
      if (xTime.equals(m_Time))
      {
         // Format error in date/time
         ErrorFile.write("ERR_LINE_SYNTAX", 15, ProcName);
         return false;
      }
      else
      {
         m_Time = (GregorianCalendar) xTime.clone();
      }

      // Write current date in message box
      MenuFrame.statusBar.setText("File reading: Current input date: " + DateLine);

      if ( st.hasMoreTokens() )
         st.nextToken(); // get comma
      else { ErrorFile.write("Comma missing", 16, ProcName); return false; }

      boolean prevWasComma = true; // indicates if previous token was comma
      String NextToken;


      // Calculate number of parameters to be read
      // Not necessary for every data line!!
      try {
        IniFile ParDataIni = new IniFile(Setup.m_IniFilePath +
           "PARDATA" + (new Integer(m_IdCode)).toString() + ".INI");
        m_NrOfValues = ParDataIni.getSectionSize(); // entries of *.ini-file
      }
      catch (IOException e) {System.out.println("MetValuesDataLine.ParseLine: " + e); }


      // Allocate memory for the float values
      // catch MemoryException could be implemented here??
      m_ValueArray = new float[m_NrOfValues];

      int i = 0;
      while (st.hasMoreTokens() && i < m_NrOfValues)
      {
         NextToken = st.nextToken();
         if (NextToken.equals(","))
         {
             if (prevWasComma)
             {   // second comma in a row, value is missing
                 m_ValueArray[i] = (float) -999.9;
                 // value not used because of CheckDataValid()
                 i++;
             }
             else
             {
                 prevWasComma = true;
             }
         }
         else // token is value, not comma
         {
           try
           {
                m_ValueArray[i] = (Float.valueOf(NextToken)).floatValue();
           }
           catch (NumberFormatException e)
           {
               // Layer values are non-numeric characters
               ErrorFile.write("ERR_LINE_SYNTAX", 17, ProcName);
               m_ValueArray[i] = (float) -999.9;
               // value not used because of CheckDataValid()
           }

           i++;
           prevWasComma = false;

         } // end if NextToken = ","

         // System.out.println("ZValuesDataLine, value: " + m_ValueArray[i]);
         // Individual layers could be checked here for error thresholds (depending on ID-Code)!!

      }

      if (prevWasComma && (i == m_NrOfValues - 1))
      { // second comma in a row, value is missing
         m_ValueArray[i] = (float) -999.9;
         // value not used because of CheckDataValid()
         i++;
      }

      if (st.hasMoreTokens() || i < m_NrOfValues)
      {
         // too few or too many layer values

         ErrorFile.write("ERR_LINE_SYNTAX", 18, ProcName);

         return false;
      }


      return true;
}


public GregorianCalendar GetTime() {return ((GregorianCalendar) m_Time.clone());}

public int GetNrOfValues() { return m_NrOfValues; };

public boolean HasDate()   { return true; }

public int  GetDataType()  { return MET_VALUES_DATA_LINE; }

public float GetMetValue( int Index ) { return m_ValueArray[Index]; }

//public String GetRegCode() { return m_RegCode; }

//public String GetStaoCode(){ return m_StaoCode; }

public String GetStatAbbrev() {return m_StatAbbrev; }

public String GetWindStatID() {return m_WindStat; }

public String GetSnowStatID() {return m_SnowStat; }

public int GetIdCode(){ return m_IdCode; }

public String GetMetValueString( int Index )
{
   float Value = GetMetValue(Index);
   Float FloatValue = new Float(Value);
   m_ValString = FloatValue.toString();
   return m_ValString;
}

}
