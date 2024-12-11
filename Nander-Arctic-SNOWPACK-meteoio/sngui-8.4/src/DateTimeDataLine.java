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
// DateTimeDataLine: Handles one line of the data file
//                   (the header line of each data entry)
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.util.*;
import java.io.*;


public class DateTimeDataLine extends DataLine implements C_DataFile
{

   protected static String ModulName = "DateTimeDataLine";
   String m_StatAbbrev = "";
   String m_SnowStat = "";

   GregorianCalendar m_Time = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
   // Calendar.MONTH = 0: January
   // Used SimpleTimeZone Constructor does not use Daylight Saving (important!).
   // Using GregorianCalendar(year, month, day) would use the default time zone taken from the
   // host computer (problems if outside Central Europe), and Daylight Saving Time.
   // This constructor for GregorianCalendar is just necessary at this place of the package.
   // (but it is also used at other parts).
   // 3600000: 1 hour difference to UTC

// protected static String m_StationId[] = new String[NR_OF_STATION_ID];


   // Construction
   DateTimeDataLine()
   {
     super();
   }


   /**
    * Constructor.
    * Set all values directly without ParseLine.
    * (used if data are read from database)
    */
   DateTimeDataLine(int IdCode, GregorianCalendar Time)
   {
      this();
      SetIdCode(IdCode);
      m_Time = (GregorianCalendar) Time.clone();
   }


   // Parse the data line with z-values
   // Formats
   // 1. Not research version
   //    <ID-Code>,<Station-ID1>,<St-ID2>,<St-ID3>,<St-ID4>,<Date/Time>
   //    Example:     0500,0130,0053,0130,0054,13.04.1998 11:31
   // 2. Research version
   //    <ID-Code>,<Station-Abbrev.+SnowStationCode>,<Date/Time>
   //    Example:     0500,KLO3,13.04.1998 11:31
   //
   // return:  true if ok, false if error
   public boolean ParseLine(String Line) throws IOException
   {
      String ProcName = ModulName + ".ParseLine";

      if ( Line == null )
      {
         ErrorFile.write("ERR_PAR_FUNCTION", 1, ProcName);
         return false;
      }

      StringTokenizer st = new StringTokenizer(Line, ",");
      int countTokens = st.countTokens();

      // Read ID code
      if ( !st.hasMoreTokens() )
      {
         // no data available
         ErrorFile.write("ERR_LINE_SYNTAX", 2, ProcName);
         return false;
      }
      SetIdCode(Integer.parseInt(st.nextToken()));
      // already checked for number format exception in ProDataEntry


      // MS: 15.05.2003: Skip all fields between id code and date
      // Necessary because of infobox files
      if (countTokens > 2)
      {
        for (int i=2; i<countTokens; i++)
            st.nextToken();
      }

/*
      if (Setup.m_ResearchMode)
      { // Research version: read station abbrev. + snow station code

         if ( !st.hasMoreTokens() )
         {
            // Station abbreviation does not exist
            ErrorFile.write("ERR_LINE_SYNTAX", 3, ProcName);
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

      else
      { // No research mode; read Id's of snow station, wind station

         for (int NrOfStationId = 0; NrOfStationId < NR_OF_STATION_ID; NrOfStationId++ )
         {
            if ( !st.hasMoreTokens() )
            {
               // Station ID data don't exist
               ErrorFile.write("ERR_LINE_SYNTAX", 5, ProcName);
               return false;
            }

            try {m_StationId[NrOfStationId] = st.nextToken(); }
            catch (NumberFormatException e)
            {
               // Station ID data are non-numerical characters
               ErrorFile.write("ERR_LINE_SYNTAX", 6, ProcName);
               return false;
            }
         }

      } // end if research mode
*/

      // Read date/time information
      if ( !st.hasMoreTokens() )
      {
         // Date/time info does not exist
         ErrorFile.write("ERR_LINE_SYNTAX", 5, ProcName);
         return false;
      }

      String DateLine = st.nextToken();
      GregorianCalendar xTime = ScanDate(DateLine);
      if (xTime.equals(m_Time))
      {
         // Format error in date/time
         ErrorFile.write("ERR_LINE_SYNTAX", 6, ProcName);
         return false;
      }
      else
      {
         m_Time = (GregorianCalendar) xTime.clone();
      }

      // Write current date in message box
      MenuFrame.statusBar.setText("File reading: Current input date: " + DateLine);

      return true;
   }


   public int     GetDataType()        { return DATE_TIME_DATA_LINE; }

   public boolean HasDate()            { return true; }

   //public String GetStationId(int Index)  { return m_StationId[Index]; }

   public String GetStatAbbrev() { return m_StatAbbrev; }

   public String GetSnowStat() { return m_SnowStat; }

   public GregorianCalendar GetTime()
   {
     //int year = m_Time.get(Calendar.YEAR);
     //int month = m_Time.get(Calendar.MONTH)+1;
     //int day = m_Time.get(Calendar.DAY_OF_MONTH);
     //int hour = m_Time.get(Calendar.HOUR_OF_DAY);
     //int minute = m_Time.get(Calendar.MINUTE);
     //int second = m_Time.get(Calendar.SECOND);

     return ((GregorianCalendar) m_Time.clone());
   }


   static GregorianCalendar ScanDate(String Line)
   // Reads date and time in String-format "DD.MM.YYYY hh:mm" into object xTime
   // xTime is returned
   {
       GregorianCalendar xTime = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
       // start (error) value: 1 hour after 01/01/1970

       // Check of formats
       if ( Line.length() < 16) return xTime;
       if ( Line.charAt(2)!='.' || Line.charAt(5)!='.' ) return xTime;
       if ( Line.charAt(10)!=' ' ) return xTime;
       if ( Line.charAt(13)!=':' ) return xTime;

       // Check if rest of characters are numbers
       // Implementation of NumberFormatException instead would also be possible
       for(int i=0; i<16; i++)
       {
           if ( !(i==2 || i==5 || i==10 || i==13))
           {
               exitpoint: {
                                  for(int j=48; j<58; j++)
                                  {
                                        if ((int) Line.charAt(i) == j) break exitpoint;
                                  } // end for
                                  return xTime; // Character is not a number
                           } // end exitpoint
           } // end if
       } // end for

       // Value input
       // (int) Line.charAt() returns ASCII-code of character
       // 0-9 are ASCII-numbers 48 to 57 --> subtract 48
       int Day    = 10*((int)Line.charAt(0)-48) + ((int)Line.charAt(1)-48);
       int Month  = 10*((int)Line.charAt(3)-48) + ((int)Line.charAt(4)-48);
       int Year   = 1000*((int)Line.charAt(6)-48) +
                     100*((int)Line.charAt(7)-48) +
                      10*((int)Line.charAt(8)-48) +
                         ((int)Line.charAt(9)-48);
       int Hour   = 10*((int)Line.charAt(11)-48) + ((int)Line.charAt(12)-48);
       int Minute = 10*((int)Line.charAt(14)-48) + ((int)Line.charAt(15)-48);

       // Check range of values
       if ( Day<1    || Day>31 )    return xTime;
       if ( Month<1  || Month>12 )  return xTime;
       if ( Year<1   || Year>9999 ) return xTime;
       if ( Hour<0   || Hour>24 )   return xTime;
       if ( Minute<0 || Minute>59 ) return xTime;

       if (Hour == 24)
       {
           Hour = 0;
           xTime.set(Year, Month-1, Day, Hour, Minute, 0);
           xTime.add(Calendar.HOUR, 24);
       }
       else
       {
           xTime.set(Year, Month-1, Day, Hour, Minute, 0);
       }

       xTime.set(Calendar.MILLISECOND, 0);
       // Month-1: because Calendar.MONTH = 0 for January
       // This has also to be considered when the constructor of GregorianCalendar
       // is called.
       // To get the number of the month, use get(calendar.MONTH)+1.
       // Errors only occur if Calendar.MONTH is set to 12. Then 12/1999 becomes 00/2000.

       return xTime;
    }

} // end class DateTimeDataLine



