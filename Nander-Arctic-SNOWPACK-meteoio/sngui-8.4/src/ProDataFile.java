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
// ProDataFile: Reads a data file which contains all data from one station
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;
import java.util.*;


public class ProDataFile extends DataFile
{
   protected String m_SnowStationId1;
   protected String m_SnowStationId2;
   protected String m_WindStationId1;
   protected String m_WindStationId2;
   String m_StatAbbrev;
   String m_SnowStat;


   // ProDataFile construction
   ProDataFile()
   {
      super();

      //m_SnowStationId1 = null;
      //m_SnowStationId2 = null;
      //m_WindStationId1 = null;
      //m_WindStationId2 = null;

      m_StatAbbrev = "";
      m_SnowStat = "";
   }


   // Read the file
   // return: true     ok
   //         false    error
   public boolean ReadDataFile( File FileName ) throws IOException
   {

      boolean RetVal;

      RetVal = super.ReadDataFile( FileName );
      if ( RetVal )
      {
         DateTimeDataLine pDateTimeDataLine = (DateTimeDataLine) (GetActDataEntry().GetDataLine( ID_CODE_DATE_TIME ));
         if ( pDateTimeDataLine != null )
         {
            //m_SnowStationId1 = (String) pDateTimeDataLine.GetStationId(0);
            //m_SnowStationId2 = (String) pDateTimeDataLine.GetStationId(1);
            //m_WindStationId1 = (String) pDateTimeDataLine.GetStationId(2);
            //m_WindStationId2 = (String) pDateTimeDataLine.GetStationId(3);

            m_StatAbbrev  = (String) pDateTimeDataLine.GetStatAbbrev();
            m_SnowStat = (String) pDateTimeDataLine.GetSnowStat();
         }
      }
      return RetVal;
   }

   public boolean CheckIfNewDataEntry( String Line)
   {
      return Line.startsWith("0500");
   }

   public DataEntry NewDataEntry() { return new ProDataEntry(); }

   //public String GetStationId1() { return m_SnowStationId1; }

   //public String GetStationId2() { return m_SnowStationId2; }

   public String GetStatAbbrev() { return m_StatAbbrev; }

   public String GetSnowStatID() {return m_SnowStat; }

   public int GetIdCode() {return 500;}
   // needed in SnowPackDoc just to see if this is a ProDataFile


   public void TimePrint(String message, GregorianCalendar Time)
   {
        System.out.print(message);
        System.out.print(Time.get(Calendar.YEAR));
        System.out.print(" " + Time.get(Calendar.MONTH)+1);
        System.out.print(" " + Time.get(Calendar.DAY_OF_MONTH));
        System.out.println();           
   }   

/* tests by M. Steiniger
   public static void main(String args[]) throws IOException 
   {
        ProDataFile aFile = new ProDataFile();
        
        Date curDate = new Date();
        long mSec = curDate.getTime();

        //System.out.println("=== Start ReadDataFile(jj5klo3p.pro): " + curDate);
        System.out.println("=== Start ReadDataFile(test.pro): " + curDate);
        aFile.ReadDataFile("test.pro");

        curDate = new Date();
        mSec = curDate.getTime() - mSec;
        System.out.println("=== End  ReadDataFile(test.pro)" + curDate);
        System.out.println("ms = " + mSec);

        
        //Various tests

        // print time resolution

        System.out.println("GetTimeResolution, Idcode 502:" + aFile.GetTimeResolution(502) + " sec");  

        // round time (test ok)
        //System.out.println("Round Time (3600, 1800): " + aFile.RoundTime(3600, 1800)); // =3600
        //System.out.println("Round Time (2701, 1800): " + aFile.RoundTime(2701, 1800)); // =3600
        //System.out.println("Round Time (2700, 1800): " + aFile.RoundTime(2700, 1800)); // =3600
        //System.out.println("Round Time (2699, 1800): " + aFile.RoundTime(2699, 1800)); // =1800
        //System.out.println("Round Time (900 , 1800): " + aFile.RoundTime( 900, 1800)); // =1800
        
        // print start time
        aFile.TimePrint("\n" + "GetStartTime of file: ", (GregorianCalendar) aFile.GetStartTime());

        // print end time
        aFile.TimePrint("GetEndTime of file: ", aFile.GetEndTime());
        
        // print start time, code 502
        aFile.TimePrint("GetStartTime of file, IdCode 502: ", (GregorianCalendar) aFile.GetStartTime(502));
        
        // print end time, code 502
        aFile.TimePrint("GetEndTime of file, IdCode 502: ", (GregorianCalendar) aFile.GetEndTime(502));        
        
        // set active start and end time to earliest and latest available from the file
        aFile.SetTimeRangeAll();
        System.out.println("\n" + "SetTimeRangeAll executed.");
        System.out.println("GetTimeRange (active time range) in hours: " + (int) aFile.GetTimeRange()/3600);
        
        // print start and end time of active time range
        aFile.TimePrint("Start time of active time range: ", (GregorianCalendar) aFile.GetTimeRangeStartTime());
        aFile.TimePrint("End time of active time range: ", (GregorianCalendar) aFile.GetTimeRangeEndTime());
        
        // move around
        aFile.MoveToLastDataEntry();
        aFile.TimePrint("\n" + "MoveToLastDataEntry: active Date = ", (GregorianCalendar) aFile.GetActDataTime()); 
        aFile.MoveToFirstDataEntry();
        aFile.TimePrint("MoveToFirstDataEntry: active Date = ", (GregorianCalendar) aFile.GetActDataTime()); 
        aFile.MoveToNextDataEntry(); // second entry reached
        GregorianCalendar StartTime = (GregorianCalendar) aFile.GetActDataTime();
        aFile.TimePrint("Second DataEntry: active Date = ", StartTime); 
        aFile.MoveToNextDataEntry();        
        aFile.MoveToNextDataEntry(); // fourth entry reached
        GregorianCalendar EndTime = (GregorianCalendar) aFile.GetActDataTime();
        aFile.TimePrint("Fourth DataEntry: active Date = ", EndTime);
        aFile.MoveToPrevDataEntry(); // third entry reached
        aFile.TimePrint("Third DataEntry: active Date = ", (GregorianCalendar) aFile.GetActDataTime());
        
        // set active start and end time to StartTime and EndTime
        aFile.SetTimeRange(StartTime, EndTime);
        System.out.println("\n" + "Active StartTime and EndTime set to second and fourth DataEntry.");
        System.out.println("GetTimeRange (active time range) in days: " + (int) aFile.GetTimeRange()/86400);
        
        // print start and end time of active time range
        aFile.TimePrint("Start time of active time range: ", (GregorianCalendar) aFile.GetTimeRangeStartTime());
        aFile.TimePrint("End time of active time range: ", (GregorianCalendar) aFile.GetTimeRangeEndTime());
        
        // change end time
        aFile.SetTimeRange(StartTime, 86400);
        aFile.TimePrint("New act. end time, 1 day after prev. StartTime: ", (GregorianCalendar) aFile.GetTimeRangeEndTime());
        
        // change start time
        aFile.SetTimeRange(259200, EndTime);
        aFile.TimePrint("New act. start time, 3 days before prev. EndTime: ", (GregorianCalendar) aFile.GetTimeRangeStartTime());        
        
        // print some characteristics of third DataEntry (currently active)
        System.out.println("\n" + "Characteristics of third Data Entry:");
        aFile.MoveToFirstDataEntry();
        aFile.MoveToNextDataEntry();
        aFile.MoveToNextDataEntry();        
        aFile.TimePrint("Date of current entry: ", (GregorianCalendar) aFile.GetActDataEntry().GetTime());
        System.out.println("Number of layers: " + ((ProDataEntry) aFile.GetActDataEntry()).GetNrOfLayers() );
        System.out.println("Id=502, Layer #2. Value: " + ((ProDataEntry) aFile.GetActDataEntry()).GetZData(502, 2) );
//        System.out.println("Station-Id #2: " + aFile.GetActDataEntry().m_StationId[1]);         

      
   }
*/
   
} // end class ProDataFile


