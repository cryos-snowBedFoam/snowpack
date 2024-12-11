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
// MetDataFile: Handles a met data file containing all non-layer parameters of
//              one station
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;
import java.util.*;


/////////////////////////////////////////////////////////////////////////////
public class MetDataFile extends DataFile
{

protected static String ModulName = "MetDataFile";
//String m_StationId1, m_StationId2;
String m_StatAbbrev;
String m_WindStat;
String m_SnowStat;
int m_IdCode;

// MetDataFile construction
public MetDataFile()
{
  //m_StationId1 = "";
  //m_StationId2 = "";
  m_StatAbbrev = "";
  m_WindStat = "";
  m_SnowStat = "";
}


public boolean ReadDataFile(File FileName) throws IOException
{
   boolean RetVal;
   RetVal = super.ReadDataFile(FileName); // Calls ReadDataFile from the base class

   MetValuesDataLine pMetDataLine;

   if ( m_ActDataEntry != null &&
       (pMetDataLine = ((MetValuesDataLine) ((MetDataEntry)
                         m_ActDataEntry).GetDataLine(100))) != null )
   {
      m_IdCode = pMetDataLine.GetIdCode();
      //m_StationId1 = pMetDataLine.GetRegCode();
      //m_StationId2 = pMetDataLine.GetStaoCode();
      m_StatAbbrev = pMetDataLine.GetStatAbbrev();
      m_WindStat = pMetDataLine.GetWindStatID();
      m_SnowStat = pMetDataLine.GetSnowStatID();
   }

   return RetVal;
}


// Check if data entry retrieved by GetActDataEntry is ok.
// Return true  if data entry is ok
//        false if error
public boolean CheckActDataValid( int IdCode )
{
   DataEntry m_pActDataEntry = (MetDataEntry) GetActDataEntry();

   if ( m_pActDataEntry == null )
   {
      //Schirmer:deleted
       //System.out.println("Warning: MetDataFile.CheckActDataValid(): " +
         //                "m_pActDataEntry == NULL");
      return false;
   }

   if ( m_pActDataEntry.GetTime() == null )
   {
      //Schirmer:deleted
       //System.out.println("Warning: MetDataFile.CheckActDataValid(): " +
         //                "m_pActDataEntry.GetTime() == NULL");
      return false;
   }

   if ( !m_pActDataEntry.CheckDataValid(IdCode) )
   {
      //Schirmer:deleted
       //System.out.println("Warning: MetDataFile.CheckActDataValid(): " +
         //                "m_pActDataEntry.CheckDataValid(IdCode) == NULL");
      return false;
   }

   return true;
}


public boolean CheckIfNewDataEntry(String Line) throws IOException
{
String ProcName = ModulName + ".CheckIfNewDataEntry";

   if ( Line == null )
   {
      ErrorFile.write("ERR_PAR_FUNCTION", 1, ProcName);
      return false;
   }

   return true;
}


public String GetStatAbbrev() {return m_StatAbbrev; }

//public String GetStationId1() { return m_StationId1; }

//public String GetStationId2() { return m_StationId2; }

public DataEntry NewDataEntry() { return new MetDataEntry(); }

public int GetIdCode() {return m_IdCode;}

public String GetSnowStatID() {return m_SnowStat; }

}
