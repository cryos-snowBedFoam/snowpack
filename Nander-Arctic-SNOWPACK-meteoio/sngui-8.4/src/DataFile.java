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
// DataFile: Reads a data file which contains all data from one station
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;
import java.util.*;
import java.lang.*;

public abstract class DataFile implements C_DataFile
{

/////////////////////////////////////////////////////////////////////////////
// member variables
protected static String ModulName = "DataFile";

static GregorianCalendar m_EmptyTime =
        new GregorianCalendar(1990, 0, 1, 0, 0, 0);
        // Calendar.MONTH = 0: January

Vector m_IdCodeEntries = null;
Vector m_DataEntries = null;
protected int m_NrOfDataEntries;

protected int m_GetStartTimeIndex; // use only after GetStartTime()
protected int m_GetEndTimeIndex; // use only after GetEndTime()

// Attributes for SetTimeRange and Move... functions
protected int m_ActDataEntryIndex;
protected DataEntry m_ActDataEntry;

protected int m_ActStartTimeIndex;
protected int m_ActEndTimeIndex;
GregorianCalendar m_ActStartTime;
GregorianCalendar m_ActEndTime;
int LineNr;

int m_TimeStepsWithoutData = 0;

// Calculated in CalculateMinMaxValue(IdCode):
float m_MinValue = 0;  // used for left and right graph y scale
float m_MaxValue = 0;  // ""
float m_IdMinValue = 0; // minimum over all values (layers and entries) for certain Id
float m_IdMaxValue = 0; // max. ""
boolean m_IdSoilDataExist; // if soil data for certain IdCode exist
boolean m_SoilDataExist; // if soil data exist at all

// DataFile construction
DataFile()
{
        // Initialization, necessary before every call to ReadDataFile!
        m_ActDataEntry = null;

        m_GetStartTimeIndex = 0;
        m_GetEndTimeIndex = 0;
        m_NrOfDataEntries = 0;

        m_ActDataEntryIndex = 0;
        m_ActStartTimeIndex = 0;
        m_ActEndTimeIndex = 0;

        m_EmptyTime.setTimeZone(new SimpleTimeZone(3600000,"MEZ"));
        m_ActStartTime = (GregorianCalendar) m_EmptyTime.clone();
        m_ActEndTime = (GregorianCalendar) m_EmptyTime.clone();

        LineNr = 0;
}


/**
* Constructor.
* Set all entries directly without ReadDataFile.
* (used if data are read from database)
*/
DataFile(int NrOfDataEntries, Vector DataEntries, Vector IdCodeEntries)
{
        this();

        m_NrOfDataEntries = NrOfDataEntries;
        m_DataEntries = DataEntries;
        m_IdCodeEntries = IdCodeEntries;

        SetTimeRangeAll();
}


// Read the file
// return:  true if ok, false if error
    public boolean ReadDataFile(File file) throws IOException {
        String ProcName = ModulName + ".ReadDataFile ";

        int LineNr = 0;
        int NrOfErrors = 0;
        String LineBuf = null;
        DataLine pDataLine;

        m_NrOfDataEntries = 0;
        m_ActDataEntryIndex = 0;
        m_IdCodeEntries = new Vector(10, 10);
        m_DataEntries = new Vector(24, 24);

        // ErrorFile pErr = new ErrorFile(); (not necessary for static methods)
        // ErrorFile.reset(); // creates an empty error file

        BufferedReader DataFile =
                new BufferedReader(new FileReader(file.getPath()));
        // getPath(): full path + name

        // Read file line by line
        //while ((LineBuf = DataFile.readLine()) != null &&
        //        NrOfErrors < MAX_READ_ERRORS)

        // If a header is present, skip it
        String LineBuf1 = DataFile.readLine(); // Read first line
        LineNr++;
        boolean readNextLine = false;
        if (LineBuf1.startsWith("[STATION_PARAMETERS]"))
        // Header is supposed to exist and consist of nine lines
        //(Schirmer) what a shit!! better: read till [DATA]
        {
            /*
                             readNextLine = true;

                             // Read 2nd to 9th line.
                             for (int i = 2; i <= 9; i++) {
                    LineBuf1 = DataFile.readLine();
                    LineNr++;
                             }*/


            //(Schirmer) search the line with "[DATA]"
            readNextLine = true;

            LineNumberReader searchDATA = new LineNumberReader(new FileReader(file.getPath()));
            boolean Found = false;
            int NrOfLastHeaderLine = 1000000;

            while (!Found) {
                LineBuf1 = searchDATA.readLine();

                if (LineBuf1.startsWith("[DATA]")) {
                    NrOfLastHeaderLine = searchDATA.getLineNumber();
                    Found = true;
                }

            }
            // Read 2nd to  NrOfLastHeaderLine. kept method, although it is not nice
            for (int i = 2; i <= NrOfLastHeaderLine; i++) {
                LineBuf1 = DataFile.readLine();
                LineNr++;
            }




        }

        // Start reading proper data area
        while (true) {
            try {
                // OutOfMemoryError occurs in the following line,
                // but is not caught. Why??
                if (readNextLine)
                    LineBuf = DataFile.readLine();
                else { // no header exists
                    LineBuf = LineBuf1;
                    LineNr--;
                    readNextLine = true;
                }
            }
                catch (RuntimeException e)
                {
                  System.out.println("DataFile: out of memory; "+e);
                  break;
                }

                if (LineBuf == null) break;
                if (NrOfErrors >= MAX_READ_ERRORS) break;


                LineNr++;
                ErrorFile.SetLineNr(LineNr); // set line number for error treatment

                //System.out.println(ProcName + ">>>read line = " + LineNr + " : "
                // + NrOfErrors + " : " + LineBuf);

                if (CheckIfNewDataEntry(LineBuf))
                {
                        // Create new data entry
                        m_ActDataEntry = NewDataEntry();

                        if (m_ActDataEntry == null)
                        {
                          ErrorFile.write("ERR_MEMORY", 1, ProcName);
                          return false;
                        }

                        //Insert new DateEntry object in vector
                        //DataEntries with errors are also added
                        //System.out.println("New Data Entry added: LineNr="+LineNr);

                        m_DataEntries.insertElementAt(
                                m_ActDataEntry, m_NrOfDataEntries++);

                        if (m_ActDataEntry.ParseDataLine(LineBuf) == null)
                        {
                            //Schirmer
                            return false;

                            //NrOfErrors++;

                                // Write current number of errors in message box
                                // FileStatus.writeError(NrOfErrors);
                        }
                        // System.out.println(ProcName + ">>>ParseDataLine1 " );
                }
                else if (m_ActDataEntry != null)
                {
                        pDataLine = m_ActDataEntry.ParseDataLine(LineBuf);
                        // System.out.println(ProcName + ">>>ParseDataLine2 " + pDataLine);

                        if (pDataLine == null)
                        {
                                NrOfErrors++;

                                // Write current number of errors in message box
                                // FileStatus.writeError(NrOfErrors);
                        }
                        else
                        {
                                if (!IdCodeExists(pDataLine.GetIdCode()))
                                {
                                // Add Id code to list
                                m_IdCodeEntries.addElement(
                                  new Integer(pDataLine.GetIdCode()));
                                }
                        }
                }
                else
                {
                      // NrOfErrors++;
                      // errors before first DateTimeDataLine might be considered
                }

        } // while


        System.out.println(ProcName +">>>finished: number of lines = " + LineNr +
                " / number of errors = " + NrOfErrors);

        // Write current number of errors in message box
        FileStatus.writeError(NrOfErrors);

        DataFile.close();


        // Set time range to start and end of file
        SetTimeRangeAll();

        if (m_NrOfDataEntries == 0 || NrOfErrors >= MAX_READ_ERRORS)
        {
                ErrorFile.write("ERR_READ_INPUT_FILE", 2, ProcName);
                // C++: >>>pError.SetError( ERR_READ_INPUT_FILE, ProcName, 3, FileName );
                return false;
        }

        return true;
}


// Get time resolution of the data file in seconds
// MinTimeStep: minimum time between two data file entries
// return MinTimeStep (0 if error)
public int GetTimeResolution(int IdCode)
{
        int MinTimeStep = 0;

        int i = 0;
        int DiffTime;
        GregorianCalendar pLastTime = null;
        GregorianCalendar pNextTime = null;
        DataEntry dataEntry;

        // Get first time
        //Schirmer , first time step problem, i = 0 to i = 1, define MinTimeStep from the second entry
        for (i = 1; pLastTime == null && i < m_NrOfDataEntries; i++)
        {
                dataEntry = (DataEntry) m_DataEntries.elementAt(i);
                pLastTime = dataEntry.GetTime();
        }

        if (pLastTime == null)
        {
                return MinTimeStep; // =0, all DataEntries contain incorrect time
        }

        for (; i < m_NrOfDataEntries; i++)
        {
                dataEntry = (DataEntry) m_DataEntries.elementAt(i);

                pNextTime = dataEntry.GetTime();
                if (pNextTime != null &&
                        pNextTime.after(pLastTime) &&
                        dataEntry.GetDataLine(IdCode) != null)
                {
                        DiffTime = (int)
                                   ((pNextTime.getTime().getTime() -
                                    pLastTime.getTime().getTime()) / 1000);
                                 //getTimeInMillis() is protected, cannot be used here
                        if (MinTimeStep == 0 || MinTimeStep > DiffTime)
                                 {
                                    MinTimeStep = DiffTime;
                                 }
                        pLastTime = pNextTime;
                }
        } // for

        return MinTimeStep;
}


// Round time step (in seconds) to time division in seconds
// TimeDivision = 1800 = 30 minutes
// e.g. if TimeDivision = 30 minutes then time steps between 45 .. 74 minutes
//      would be rounded to 60 minutes
public int RoundTime(int TimeStep, int TimeDivision) throws IOException
{
    String ProcName = ModulName + ".RoundTime";
    try
    {
        int roundedTime = (int) ( (TimeStep / (double) TimeDivision) + 0.5 ) * TimeDivision;
        if (roundedTime == 0) roundedTime = TimeDivision; // prevent a zero return
        return roundedTime;
    }
    catch (ArithmeticException e)
    {
        ErrorFile.write("ERR_DIV_BY_ZERO", 1, ProcName);
        return 0;
    }
}


// Get first valid time entry
// Return EmptyTime if error
public GregorianCalendar GetStartTime()
{
        return GetStartTime(ID_CODE_DATE_TIME);
}

public GregorianCalendar GetStartTime(int IdCode)
{
        int i;
        GregorianCalendar startTime = null;
        DataEntry dataEntry;

        m_GetStartTimeIndex = 0;

        // Get first time
        for (i = 0;
                startTime == null && i < m_NrOfDataEntries; i++)
        {
                dataEntry = (DataEntry) m_DataEntries.elementAt(i);
                if (dataEntry.GetDataLine(IdCode) != null)
                {
                        startTime = dataEntry.GetTime();
                        m_GetStartTimeIndex = i;
                }
        }

        if (startTime == null)
                return ((GregorianCalendar) m_EmptyTime.clone());
        else
                return ((GregorianCalendar) startTime.clone());
}


// Get last valid time entry
// Return EmptyTime if error
public GregorianCalendar GetEndTime()
{
        return GetEndTime(ID_CODE_DATE_TIME);
}

public GregorianCalendar GetEndTime(int IdCode)
{
        int i;
        GregorianCalendar endTime = null;
        DataEntry dataEntry;

        m_GetEndTimeIndex = 0;

        // Get first time (looping from the end down)
        for (i = m_NrOfDataEntries - 1;
                endTime == null && i >= 0; i--)
        {
                dataEntry = (DataEntry) m_DataEntries.elementAt(i);
                if (dataEntry.GetDataLine(IdCode) != null)
                {
                        endTime = dataEntry.GetTime();
                        m_GetEndTimeIndex = i;
                }
        }

        if (endTime == null)
                return ((GregorianCalendar) m_EmptyTime.clone());
        else
                return ((GregorianCalendar) endTime.clone());
}


public GregorianCalendar GetEmptyTime()
   { return (GregorianCalendar) m_EmptyTime.clone(); }


// Set time range. Moves to the first data entry
// If EndTime = null then set end time to last data file entry.
// Return:  0     OK
//         -1     error
//          1     Start time not found
public int SetTimeRange(GregorianCalendar StartTime, GregorianCalendar EndTime)
{
        int i;
        GregorianCalendar pTime = null;
        DataEntry dataEntry;

        if (StartTime == null) return -1; // error

        // Get first time
        for (i = 0; i < m_NrOfDataEntries; i++)
        {
                dataEntry = (DataEntry) m_DataEntries.elementAt(i);
                if (dataEntry != null && (pTime = dataEntry.GetTime()) != null)
                {
                        if (pTime.after(StartTime)||pTime.equals(StartTime))
                        {
                                m_ActStartTime = pTime;
                                m_ActStartTimeIndex = i;
                                break; // time found => exit for loop
                        }
                }
        } // for

        if (i == m_NrOfDataEntries)
        {
                // Time not found: set to data file start and end time
                SetTimeRangeAll();
                return 1; // Start time not found
        }

        if (EndTime == null)
        {
                // Set end time to file end
                m_ActEndTime = GetEndTime();
                m_ActEndTimeIndex = m_GetEndTimeIndex;

                m_ActDataEntry = (DataEntry) m_DataEntries.elementAt(i);
                m_ActDataEntryIndex = i;
        }
        else
        {
                m_ActEndTime = EndTime;
                m_ActEndTimeIndex = 0;
                GregorianCalendar endTime = null;

                // Get end time index
                for (i = m_NrOfDataEntries - 1; i >= 0; i--)
                {
                        dataEntry = (DataEntry) m_DataEntries.elementAt(i);
                        if (dataEntry != null)
                        {
                                endTime = dataEntry.GetTime();
                                if (endTime != null &&
                                        !endTime.after(m_ActEndTime) &&
                                        !endTime.before(m_ActStartTime))
                                {
                                        m_ActEndTimeIndex = i;
                                        m_ActEndTime = endTime;

                                        m_ActDataEntry = (DataEntry) m_DataEntries.elementAt(i);
                                        m_ActDataEntryIndex = i;

                                        break; // exit for
                                }
                        }
                } // for
        } // else

        return 0;
}


// Set time range to start time and end time.
// Set end time = start time + TimeRange
// TimeRange is time in seconds
// Return:  0     OK
//         -1     error
//          1     Start time not found (same numbers returned by SetTimeRange())
// TimeRange was long before (C++-version).
// Changed to int because add() just works with int.
// Max. value for int: 2,147,483,647 sec (around 68 years)
public int SetTimeRange(GregorianCalendar StartTime, int TimeRange)
{
        GregorianCalendar EndTime = (GregorianCalendar) StartTime.clone();
        EndTime.add(Calendar.SECOND, TimeRange);

        return SetTimeRange(StartTime, EndTime);
}


// Set time range to start time and end time.
// Set start time = end time - TimeRange
// TimeRange is time in seconds
// Return:  0     OK
//         -1     error
//          1     Start time not found (same numbers returned by SetTimeRange())
public int SetTimeRange(int TimeRange, GregorianCalendar EndTime)
{
        GregorianCalendar StartTime = (GregorianCalendar) EndTime.clone();
        StartTime.add(Calendar.SECOND, -TimeRange);

        return SetTimeRange(StartTime, EndTime);
}


// Set time range to start time and end time of the data file
public void SetTimeRangeAll()
{
        m_ActStartTime = GetStartTime();
        m_ActEndTime = GetEndTime();

        m_ActDataEntry = (DataEntry) m_DataEntries.elementAt(m_GetEndTimeIndex); // was m_GetStartTimeIndex
        m_ActDataEntryIndex = m_GetEndTimeIndex; // was m_GetStartTimeIndex
        m_ActStartTimeIndex = m_GetStartTimeIndex;
        m_ActEndTimeIndex = m_GetEndTimeIndex;

}


// Calculate time range in seconds (set by SetTimeRange)
public int GetTimeRange()
{
        int ts = (int) ( (m_ActEndTime.getTime().getTime() -
                          m_ActStartTime.getTime().getTime()) / 1000);
        return ts;
}


public GregorianCalendar GetTimeRangeStartTime()
     { return ((GregorianCalendar) m_ActStartTime.clone()); }


public GregorianCalendar GetTimeRangeEndTime()
     { return ((GregorianCalendar) m_ActEndTime.clone());  }


// Move to previous data entry selected by SetTimeRange.
// Return   0     if previous data entry is in time range
//         -1     error
//          1     if previous data entry is not in time range
public int MoveToPrevDataEntry()
{
        DataEntry dataEntry;
        int DataEntryIndex = m_ActDataEntryIndex;

        if (DataEntryIndex < 0)
        {
               return -1; // error
        }

        // Search next valid time entry
        do
        {
               if (DataEntryIndex == 0)
               {
                       return 1; // Begin of data file reached
               }

               DataEntryIndex--;
               dataEntry = (DataEntry)
                             m_DataEntries.elementAt(DataEntryIndex);

               if (dataEntry == null)
               {
                       return -1; // error
               }
        }
        while (dataEntry.GetTime() == null); //error if == null--> another loop

        if (!(dataEntry.GetTime()).after(m_ActEndTime) &&
                !(dataEntry.GetTime()).before(m_ActStartTime))
        {
                m_ActDataEntry = dataEntry;
                m_ActDataEntryIndex = DataEntryIndex;
                return 0; // OK
        }
        else
        {
                return 1; // End of time range reached
        }
}


// Move to previous data entry selected by SetTimeRange.
// If MaxCatchTime < 0 then no MaxCatchTime check is made.
// previous data entry time <= act data entry time - TimeStep + MinCatchTime
// previous data entry time <= act data entry time - TimeStep - MaxCatchTime
// times in seconds.
// Return   0     if previous data entry is in time range
//         -1     error
//          1     if next data entry is not in time range
// TimeRange, MinCatchTime and MaxCatchTime were long before (C++-version).
// Changed to int because add() just works with int.
// Max. value for int: 2,147,483,647 sec (around 68 years).
public int MoveToPrevDataEntry(int TimeStep, int MinCatchTime, int MaxCatchTime)
{
        int RetVal;

        if (m_ActDataEntry.GetTime() == null)
        {
               return -1;
        }

        //C++: CTime MinNextTime =
        //*m_pActDataEntry->GetTime() - CTimeSpan(TimeStep - MinCatchTime);

        GregorianCalendar MinNextTime =
          (GregorianCalendar) (m_ActDataEntry.GetTime()).clone();
        MinNextTime.add(Calendar.SECOND, -(TimeStep + MinCatchTime));

        GregorianCalendar MaxNextTime = (GregorianCalendar)
          (m_ActDataEntry.GetTime()).clone();
        MaxNextTime.add(Calendar.SECOND, -(TimeStep - MaxCatchTime));

        do
        {
                RetVal = MoveToPrevDataEntry();
                if (RetVal == 0 && MaxCatchTime >= 0)
                {
                        if ((m_ActDataEntry.GetTime()).before(MinNextTime))
                        {
                          return 1; // Previous data entry not in time range
                        }
                }
        }
        while (RetVal == 0 && (m_ActDataEntry.GetTime()).after(MaxNextTime));
         //Repeat while RetVal-error

        return RetVal;
}


// Move to next data entry selected by SetTimeRange.
// Return   0     if next data entry is in time range
//         -1     error
//          1     if next data entry is not in time range
public int MoveToNextDataEntry()
{
        DataEntry dataEntry;
        int DataEntryIndex = m_ActDataEntryIndex;

        if (DataEntryIndex >= m_NrOfDataEntries)
        {
            return -1; // error
        }

        // Search next valid time entry
        do
        {
             if ((DataEntryIndex + 1) >= m_NrOfDataEntries)
             {
                return 1; // End of data file reached
             }

             DataEntryIndex++;
             dataEntry = (DataEntry) m_DataEntries.elementAt(DataEntryIndex);

             if (dataEntry == null)
             {
                 return -1; // error
             }
        } while (dataEntry.GetTime() == null);

        if (!(dataEntry.GetTime()).after(m_ActEndTime) &&
                !(dataEntry.GetTime()).before(m_ActStartTime))
        {
                       m_ActDataEntry = dataEntry;
                       m_ActDataEntryIndex = DataEntryIndex;
                       return 0; // OK
        }
        else
        {
                       return 1; // End of time range reached
        }
}


/*
// Calculates how many times TimeStep has to be added to StartTime to reach ActTime
public int HowMany(GregorianCalendar StartTime, GregorianCalendar ActTime, int TimeStep)
{
  int k = 0;

  while(k < 100000)
  {
        GregorianCalendar NextTime = (GregorianCalendar) StartTime.clone();
        NextTime.add(Calendar.SECOND, (int) (TimeStep * (k + 0.5)));
        // 0.5: to prevent rounding errors. Accumulating errors in TimeStep still possible.

        if (NextTime.after(ActTime)) return k;

        k++;
  }
  return 0;

}
*/


// Move to next data entry selected by SetTimeRange.
// If MaxCatchTime < 0 then no MaxCatchTime check is made.
// next data entry time >= act data entry time + TimeStep - MinCatchTime
// next data entry time <= act data entry time + TimeStep + MaxCatchTime
// times in seconds.
// Return   0     if next data entry is in time range
//         -1     error
//          1     if next data entry is not in time range
public int MoveToNextDataEntry(int TimeStep, int MinCatchTime, int MaxCatchTime)
{
        int RetVal;

        if (m_ActDataEntry.GetTime() == null)
        {
               return -1;
        }

        int SavDataEntryIndex = m_ActDataEntryIndex;

        // C++: GregorianCalendar MinNextTime =
        //     m_ActDataEntry.GetTime() + CTimeSpan(TimeStep - MinCatchTime);

        GregorianCalendar MinNextTime = (GregorianCalendar)
           (m_ActDataEntry.GetTime()).clone();
        MinNextTime.add(Calendar.SECOND,
               (TimeStep * (1 + m_TimeStepsWithoutData) - MinCatchTime));

        GregorianCalendar MaxNextTime = (GregorianCalendar)
           (m_ActDataEntry.GetTime()).clone();
        MaxNextTime.add(Calendar.SECOND,
               (TimeStep * (1 + m_TimeStepsWithoutData) + MaxCatchTime));

        // ActTime: no time step added yet
        GregorianCalendar ActTime = m_ActDataEntry.GetTime();
        do
        {
                RetVal = MoveToNextDataEntry(); // 0 if ok
                // ActTime: time step now included

                if (RetVal == 0 && MaxCatchTime >= 0)
                {
                        if ((m_ActDataEntry.GetTime()).after(MaxNextTime))
                        {
                        // Set end time index to last valid index within time step
                               m_ActDataEntryIndex = SavDataEntryIndex;
                               m_ActDataEntry = (DataEntry) m_DataEntries.elementAt(SavDataEntryIndex);
                               m_TimeStepsWithoutData++;

                               return 1; // Next data entry is not in time range
                        }
                 }
        }
        while (RetVal == 0 && (m_ActDataEntry.GetTime()).before(MinNextTime));

        m_TimeStepsWithoutData = 0;

        if (RetVal == 1 && (m_ActDataEntry.GetTime()).before(MinNextTime))
        // not sure if statement reached!
        {
                // Set end time index to last valid index within time step
                m_ActEndTimeIndex = SavDataEntryIndex;
                MoveToLastDataEntry(); // taken from C++; not clear why necessary!
        }

        return RetVal;
}


// Move to first data entry selected by SetTimeRange.
// Return   0     if first data entry is in time range
//         -1     error
//          1     if first data entry is not in time range
public int MoveToFirstDataEntry()
{
        DataEntry dataEntry;

        if (m_ActStartTimeIndex < 0 || m_ActStartTimeIndex >= m_NrOfDataEntries)
        {
                return -1; // error
        }

        dataEntry = (DataEntry) m_DataEntries.elementAt(
                       m_ActStartTimeIndex);

        if (dataEntry == null || dataEntry.GetTime() == null)
        {
                return -1; // error
        }

        if (!(dataEntry.GetTime()).after(m_ActEndTime) &&
                !(dataEntry.GetTime()).before(m_ActStartTime))
        {
                m_ActDataEntryIndex = m_ActStartTimeIndex;
                m_ActDataEntry = dataEntry;
                return 0; // OK
        }
        else
        {
                return 1; // not in time range
        }
}


// Move to last data entry selected by SetTimeRange.
// Return   0     if last data entry is in time range
//         -1     error
//          1     if last data entry is not in time range
// nearly identical to MoveToFirstDataEntry()
public int MoveToLastDataEntry()
{
        DataEntry dataEntry;

        if (m_ActEndTimeIndex < 0 || m_ActEndTimeIndex >= m_NrOfDataEntries)
        {
                return -1; // error
        }

        dataEntry = (DataEntry) m_DataEntries.elementAt(m_ActEndTimeIndex);

        if (dataEntry == null || dataEntry.GetTime() == null)
        {
                return -1; // error
        }

        if (!(dataEntry.GetTime()).after(m_ActEndTime) &&
                !(dataEntry.GetTime()).before(m_ActStartTime))
        {
                m_ActDataEntryIndex = m_ActEndTimeIndex;
                m_ActDataEntry = dataEntry;
                return 0; // OK
        }
        else
        {
                return 1; // not in time range
        }
}



// Set act data entry to new data entry.
// Return true    if data entry is ok
//        false   if error
public boolean SetActDataEntry(DataEntry NewDataEntry)
{
        if (NewDataEntry == null) return false;

        if (NewDataEntry == m_ActDataEntry) return true; // is already set

        int Index;
        for (Index = 0; Index < m_NrOfDataEntries; Index ++)
        {
                if ((DataEntry) m_DataEntries.elementAt(Index) == NewDataEntry)
                       break; // entry found
        }

        if (Index == m_NrOfDataEntries) return false; // not found

        // Limit new data entry to actual time range
        if (Index < m_ActStartTimeIndex)    Index = m_ActStartTimeIndex;
        else if (Index > m_ActEndTimeIndex) Index = m_ActEndTimeIndex;

        m_ActDataEntryIndex = Index;
        m_ActDataEntry = (DataEntry) m_DataEntries.elementAt(Index);

        return true;
}


// Search data entry with given time and set act data entry
// Min/max catch times in seconds.
// Return   true  if data entry is ok
//          false if error
public boolean SetActDataEntry(GregorianCalendar NewTime,
       int MinCatchTime, int MaxCatchTime)
{
        if (NewTime == null || m_ActDataEntry.GetTime() == null)
               return false;

        int Index;
        DataEntry dataEntry;

        GregorianCalendar MinNextTime = (GregorianCalendar) NewTime.clone();
        MinNextTime.add(Calendar.SECOND, -MinCatchTime);

        GregorianCalendar MaxNextTime = (GregorianCalendar) NewTime.clone();
        MaxNextTime.add(Calendar.SECOND, MaxCatchTime);

        if (!MinNextTime.after(m_ActDataEntry.GetTime()) &&
                !MaxNextTime.before(m_ActDataEntry.GetTime()))
                       return true; // is already set


        if ((m_ActDataEntry.GetTime()).before(MinNextTime))
                       Index = m_ActDataEntryIndex;
        else
                       Index = m_ActStartTimeIndex;

        while (Index < m_NrOfDataEntries && Index <= m_ActEndTimeIndex)
        // Start index below range, iteration upwards
        {
                dataEntry = (DataEntry) m_DataEntries.elementAt(Index);
                if (!MinNextTime.after(dataEntry.GetTime()) &&
                        !MaxNextTime.before(dataEntry.GetTime()))
                {
                        // entry found
                        m_ActDataEntryIndex = Index;
                        m_ActDataEntry = dataEntry;
                        return true;
                }

                Index++;
        }

        return false; // not found
}


// Get information about the actual data entry selected by MoveToNextDataEntry
public DataEntry GetActDataEntry()        { return m_ActDataEntry; }


public GregorianCalendar GetActDataTime()
     { return ((GregorianCalendar) m_ActDataEntry.GetTime().clone()); }


// Check if data entry retrieved by GetActDataEntry is ok.
// Use after MoveToNextDataEntry
//   to avoid null pointer exceptions and access to wrong layer data
// Return   true    if data entry is ok
//          false   if error
public boolean CheckActDataValid(int IdCode)
{
       if (m_ActDataEntry == null)
       {
          System.out.println
          ("Warning: DataFile, method CheckActDataValid: m_ActDataEntry == null\n");
          return false;
       }

       if (m_ActDataEntry.GetTime() == null)
       {
          System.out.println
          ("Warning: DataFile, method CheckActDataValid: m_ActDataEntry.GetTime() == null\n");
          return false;
       }

       if (!m_ActDataEntry.CheckDataValid(IdCode))
       {  /*Schirmer
          System.out.println
          ("Warning: DataFile, method CheckActDataValid: m_ActDataEntry.CheckDataValid(IdCode) == false\n");*/
          return false;
       }

       return true;
}


// Return  true if IdCode is in data file
//         false if not
// IdCode must be greater ID_CODE_DATE_TIME
public boolean IdCodeExists(int IdCode)
{
        for (int i = 0; i < m_IdCodeEntries.size(); i++)
        {
              if (IdCode == ((Integer) m_IdCodeEntries.elementAt(i)).intValue())
                               return true;
        }
        return false;
}


// Calculates the minimum and maximum values.
// m_MinValue, m_MaxValue: min/max snow depth (if IdCode >= 500, layer parameters)
//                         min/max parameter value (non-layer parameters)
// m_IdMinValue, m_IdMaxValue: min/max for certain Id over all layers and data entries
//                         (only for layer parameters)
// Returns true if values for a given IdCode exist,
//         false else
public boolean CalculateMinMaxValue(int IdCode)
{
        DataEntry dataEntry;
        boolean valueExists = false;
        float Value;
        m_IdSoilDataExist = false; // info if soil data in IdCode layer exist
        m_SoilDataExist = false; // info if soil data in ID_CODE_LAYER_HEIGHT exist

        // Loop over all data entries
        for (int i=0; i<m_NrOfDataEntries; i++)
        {
          dataEntry = (DataEntry) m_DataEntries.elementAt(i);

          if (dataEntry != null && dataEntry.GetTime() != null)
          {

             if (IdCode >= 500)
             // ===== layer parameters =====
             {
               if (valueExists == false)
               { // Position reached if first valid data entry
                  if (((ProDataEntry) dataEntry).CheckDataValid(IdCode))
                  {
                    // Get m_MinValue, m_MaxValue
                    m_MinValue = ((ProDataEntry) dataEntry).GetZData
                     (ID_CODE_LAYER_HEIGHT, 0);

                    if (((ProDataEntry) dataEntry).GetNrOfSnowLayers() ==
                        ((ProDataEntry) dataEntry).GetTotalNrOfLayers()) // no soil data
                        m_MaxValue = ((ProDataEntry) dataEntry).GetZData
                     (ID_CODE_LAYER_HEIGHT, ((ProDataEntry) dataEntry).GetTotalNrOfLayers()-1);
                    else
                        m_MaxValue = ((ProDataEntry) dataEntry).GetZData
                     (ID_CODE_LAYER_HEIGHT, ((ProDataEntry) dataEntry).GetTotalNrOfLayers());

                    // Get m_IdMinValue, m_IdMaxValue
                    for (int j=0; j<((ProDataEntry) dataEntry).GetNrOfLayers(IdCode); j++)
                    {
                       if (j==0)
                       {
                          m_IdMinValue = ((ProDataEntry) dataEntry).GetZData(IdCode, 0);
                          m_IdMaxValue = m_IdMinValue;
                       }
                       else
                       {
                          Value = ((ProDataEntry) dataEntry).GetZData(IdCode, j);
                          m_IdMinValue = Math.min(m_IdMinValue, Value);
                          m_IdMaxValue = Math.max(m_IdMaxValue, Value);
                       }
                    }

                    valueExists = true;

                    if (((ProDataEntry) dataEntry).SoilDataExist(IdCode))
                      m_IdSoilDataExist = true;
                    if (((ProDataEntry) dataEntry).SoilDataExist())
                      m_SoilDataExist = true;
                  }
               }

               else
               { // All data entries after the first valid one
                  if (((ProDataEntry) dataEntry).CheckDataValid(IdCode))
                  {
                     // Get m_MinValue, m_MaxValue
                     Value = ((ProDataEntry)
                          dataEntry).GetZData(ID_CODE_LAYER_HEIGHT, 0);
                     m_MinValue = Math.min(m_MinValue, Value);
                     if (m_MinValue > 0) m_MinValue = 0; // no soil data present

                     if (((ProDataEntry) dataEntry).GetNrOfSnowLayers() ==
                        ((ProDataEntry) dataEntry).GetTotalNrOfLayers()) // no soil data

                          Value = ((ProDataEntry)
                             dataEntry).GetZData(ID_CODE_LAYER_HEIGHT,
                             ((ProDataEntry) dataEntry).GetTotalNrOfLayers() - 1);
                     else
                          Value = ((ProDataEntry)
                             dataEntry).GetZData(ID_CODE_LAYER_HEIGHT,
                             ((ProDataEntry) dataEntry).GetTotalNrOfLayers());

                     m_MaxValue = Math.max(m_MaxValue, Value);

                     // Get m_IdMinValue, m_IdMaxValue
                     for (int j=0; j<((ProDataEntry) dataEntry).GetNrOfLayers(IdCode); j++)
                     {
                           Value = ((ProDataEntry) dataEntry).GetZData(IdCode, j);
                           m_IdMinValue = Math.min(m_IdMinValue, Value);
                           m_IdMaxValue = Math.max(m_IdMaxValue, Value);
                     }

                     if (((ProDataEntry) dataEntry).SoilDataExist(IdCode))
                      m_IdSoilDataExist = true;
                     if (((ProDataEntry) dataEntry).SoilDataExist())
                      m_SoilDataExist = true;
                  }
               }
             }

             else
             // ==== non-layer parameters, IdCode < 500 =====
             {
               if (valueExists == false)
               { // First valid data entry
                  if (((MetDataEntry) dataEntry).CheckDataValid(IdCode))
                  {
                    Value = ((MetDataEntry) dataEntry).GetMetData(IdCode);
                    m_MinValue = Value;
                    m_MaxValue = Value;
                    valueExists = true;
                  }
               }
               else
               { // All data entries after the first valid one
                  if (((MetDataEntry) dataEntry).CheckDataValid(IdCode))
                  {
                     Value = ((MetDataEntry) dataEntry).GetMetData(IdCode);
                     m_MinValue = Math.min(m_MinValue, Value);
                     m_MaxValue = Math.max(m_MaxValue, Value);
                  }
               }
             } // end if layer/non-layer parameter
           } // end if (dataEntry != null && dataEntry.GetTime() != null)
        } // end for

        return valueExists;
}

/*
public void LoopTest()
// Just to test if correct times are stored in m_DataEntries
{
        GregorianCalendar endTime = null;
        DataEntry dataEntry;

        for (int i = m_NrOfDataEntries - 1; i >= 0; i--)
        {
              dataEntry = (DataEntry) m_DataEntries.elementAt(i);
              endTime = dataEntry.GetTime();

              int year = endTime.get(Calendar.YEAR);
              int month = endTime.get(Calendar.MONTH)+1;
              int day = endTime.get(Calendar.DAY_OF_MONTH);
              int hour = endTime.get(Calendar.HOUR_OF_DAY);
              int minute = endTime.get(Calendar.MINUTE);
              int second = endTime.get(Calendar.SECOND);
        } // for

        return;
}
*/


// Abstract methods:

public abstract DataEntry NewDataEntry();

public abstract boolean CheckIfNewDataEntry(String Line) throws IOException;

//public abstract String GetStationId1();

//public abstract String GetStationId2();

public abstract String GetStatAbbrev();

public abstract int GetIdCode();

public abstract String GetSnowStatID();

} // end class DataFile



//=================================================================
//Methods defined in DataFile and related classes (not up-to-date):

//DataFile
//public boolean ReadDataFile(String FileName) throws IOException
//public abstract DataEntry NewDataEntry();
//public abstract boolean CheckIfNewDataEntry(String Line);
//public int GetTimeResolution(int IdCode)
//public int RoundTime(int TimeStep, int TimeDivision)
//public GregorianCalendar GetStartTime()
//public GregorianCalendar GetStartTime(int IdCode)
//public GregorianCalendar GetEndTime()
//public GregorianCalendar GetEndTime(int IdCode)
//public GregorianCalendar GetEmptyTime()     { return m_EmptyTime; };
//public int SetTimeRange(GregorianCalendar StartTime, GregorianCalendar EndTime)
//public int SetTimeRange(GregorianCalendar StartTime, int TimeRange)
//public int SetTimeRange(int TimeRange, GregorianCalendar EndTime)
//public void SetTimeRangeAll()
//public int GetTimeRange()
//public GregorianCalendar GetTimeRangeStartTime()  { return &m_ActStartTime; };
//public GregorianCalendar GetTimeRangeEndTime()    { return &m_ActEndTime; };
//public int MoveToPrevDataEntry
//public int MoveToPrevDataEntry(int TimeStep, int MinCatchTime, int MaxCatchTime)
//public int MoveToNextDataEntry()
//public int MoveToNextDataEntry(int TimeStep,  int MinCatchTime, int MaxCatchTime)
//public int MoveToFirstDataEntry()
//public int MoveToLastDataEntry()
//public boolean SetActDataEntry(DataEntry NewDataEntry)
//public boolean SetActDataEntry(GregorianCalendar NewTime,  int MinCatchTime, int MaxCatchTime)
//public DataEntry GetActDataEntry()        { return m_ActDataEntry; };
//public GregorianCalendar GetActDataTime() { return m_ActDataEntry.GetTime(); };
//public boolean CheckActDataValid(int IdCode)
//public boolean IdCodeExists(int IdCode)
//
//ProDataFile
//public boolean ReadDataFile( String file ) throws IOException
//public DataEntry NewDataEntry()     { return new ProDataEntry(); };
//public boolean   CheckIfNewDataEntry( String Line)
//public static void main(String args[]) throws IOException
//
//DataEntry
//public abstract DataLine ParseDataLine( String Line);
//public abstract DataLine GetDataLine( int IdCode );
//public abstract GregorianCalendar GetTime();
//public abstract boolean CheckDataValid( int IdCode );
//
//ProDataEntry
//public DataLine ParseDataLine(String Line)
//public DataLine GetDataLine(int IdCode)
//public GregorianCalendar GetTime()
//public int GetNrOfLayers()
//public boolean GetZData(int IdCode, int LayerNr, float Value)
//public boolean GetYData(int IdCode, int SubId, float Value)
//public boolean CheckDataValid(int IdCode)
//
//DataLine
//public abstract boolean  ParseLine( String Line);
//public void SetIdCode( int IdCode ) { m_IdCode = IdCode; };
//public  int  GetIdCode( )           { return m_IdCode; };
//public abstract int  GetDataType();
//public abstract boolean HasDate();
//
//ZValuesDataLine
//public boolean ParseLine(String Line)
//public int  GetDataType()             { return Z_VALUES_DATA_LINE; }
//public boolean HasDate()              { return false; }; (not used)
//public int GetNrOfValues()            { return m_NrOfValues; };
//public float GetZValue( int LayerNr ) { return m_ValueArray[LayerNr]; };
//
//DateTimeDataLine
//protected static int m_StationId[] = new int[NR_OF_STATION_ID];
//public boolean ParseLine(String Line)
//public int     GetDataType()        { return DATE_TIME_DATA_LINE; };
//public boolean HasDate()            { return true; }; (not used)
//public int GetStationId(int Index)  { return m_StationId[Index]; };
//public GregorianCalendar GetTime()  { return m_Time; };
//int ScanDate(String Line, GregorianCalendar Time)




// C++ implementation of DataFile (incomplete):

// get time resolution, start time and end time of the data file
// C++: public int     GetTimeResolution(int IdCode, long *MinTimeStep );
// C++: public long    RoundTime( long TimeStep, long TimeDivision = 1800L );
// C++: public CTime  GetStartTime(int IdCode = ALL_IDCODES);
// C++: public CTime  GetEndTime(int IdCode = ALL_IDCODES);
// C++: public CTime  GetEmptyTime()     { return &m_EmptyTime; };

// set time range. moves to the first data entry
// C++: public int     SetTimeRange(CTime StartTime, CTime EndTime = null);
// C++: public int     SetTimeRange(CTime StartTime, long TimeRange );
// C++: public int     SetTimeRange(long TimeRange,   CTime EndTime );
// C++: public int     SetTimeRangeAll();

// get time range functions
// C++: public long    GetTimeRange();
// C++: public CTime  GetTimeRangeStartTime()  { return &m_ActStartTime; };
// C++: public CTime  GetTimeRangeEndTime()    { return &m_ActEndTime; };

// move to next data entry selected by SetTimeRange.
// Return 0 if next data entry is in time range
// C++: public int     MoveToNextDataEntry();
// C++: public int     MoveToNextDataEntry(long TimeStep, long MinCatchTime, long MaxCatchTime = -1 );

// move to previous data entry selected by SetTimeRange.
// Return 0 if next data entry is in time range
// C++: public int     MoveToPrevDataEntry();
// C++: public int     MoveToPrevDataEntry(long TimeStep, long MinCatchTime, long MaxCatchTime = -1 );

// C++: public int     MoveToFirstDataEntry();
// C++: public int     MoveToLastDataEntry();

// set act data entry to new data entry. Return 0 if ok.
// C++: public int     SetActDataEntry(DataEntry NewDataEntry);

// C++: public int     SetActDataEntry(CTime NewTime, long MinCatchTime = 0L, long MaxCatchTime = 0L );


// get informations about the actual data entry which is selected by MoveToNextDataEntry
// C++: public DataEntry GetActDataEntry()     { return m_ActDataEntry; };
// C++: public CTime     GetActDataTime()      { return m_ActDataEntry.GetTime(); };

// verifies if the data values exists and has reasonable values (e.g. NrOfLayers are ok).
// Use after MoveToNextDataEntry and before calls to GetYData or GetZData to avoid
// null pointer exceptions and access to wrong layer data
// C++: public int     CheckActDataValid( int IdCode );







