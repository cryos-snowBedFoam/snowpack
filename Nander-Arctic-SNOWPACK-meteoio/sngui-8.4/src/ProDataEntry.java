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
// ProDataEntry: Handles a set of data lines referring
//               to a specific date and time
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.util.*;
import java.io.*;


public class ProDataEntry extends DataEntry implements C_DataFile, Cloneable
{
        protected static String ModulName = "ProDataEntry";

        // Attributes
        protected Hashtable m_DataLines = new Hashtable(7);

        // Construction
        ProDataEntry()   {super();}

        // Read the data line
        public DataLine ParseDataLine(String Line) throws IOException
        {
                String ProcName = ModulName + ".ParseDataLine";

                if (Line == null)
                {
                        ErrorFile.write("ERR_PAR_FUNCTION", 1, ProcName);
                        return null;
                }

                int IdCode;
                DataLine pDataLine = null;

                // Read ID code
                if (Line.length() < 4)
                {
                        // ID code too short
                        ErrorFile.write("ERR_LINE_SYNTAX", 2, ProcName);
                        return null;
                }

                try {
                if (Line.substring(3, 4) == ",") // IdCode starts without "0"
                    IdCode = Integer.parseInt(Line.substring(0, 3));
                else                             // IdCode starts with "0"
                    IdCode = Integer.parseInt(Line.substring(0, 4));

                // Create the right DataLine object
                if (IdCode == ID_CODE_DATE_TIME)// =500
                        pDataLine = (DataLine) new DateTimeDataLine();
                else if (IdCode > ID_CODE_DATE_TIME && IdCode < (ID_CODE_DATE_TIME + 499))
                        pDataLine = (DataLine) new ZValuesDataLine();
                else
                {
                        // ID Code too big or too small
                        ErrorFile.write("ERR_LINE_SYNTAX", 3, ProcName);
                        return null; // no line with id code
                }

                if (pDataLine == null)
                {
                        // Memory problems
                        ErrorFile.write("ERR_MEMORY", 4, ProcName);
                        return null;
                }

                // Check if line with the same id code already exists
                Integer IntIdCode = new Integer(IdCode);
                if (m_DataLines.containsKey(IntIdCode))
                {
                        // Lines with the same id code already exist in the actual date entry
                        ErrorFile.write("ERR_LINE_SYNTAX", 5, ProcName);
                        return null;
                }

                // Parse the line
                if (pDataLine.ParseLine(Line))
                {

                        // Add DataLine object if line is ok
                        m_DataLines.put(IntIdCode, pDataLine);
                }
                else
                {
                        // DataLine could not be parsed
                        // This error occurs together with another error message
                        //   in ZValuesDataLine or DateTimeDataLine
                        // Delete DataLine object if line is not ok

                        //ErrorFile.write("ERR_LINE_SYNTAX", 6, ProcName);

                        return null;
                }

                } // end try
                catch (NumberFormatException e)
                {
                        // Non-numerical characters at position of ID-Code (Line 1:4)
                        ErrorFile.write("ERR_LINE_SYNTAX", 7, ProcName);
                        return null;
                }

                return pDataLine;

        }


        /**
         * Set all values directly without ParseDataLine.
         * (used if data are read from database)
         */
        public DataLine addDataLine(int IdCode, DataLine pDataLine)
        {
            Integer IntIdCode = new Integer(IdCode);
            if (m_DataLines.containsKey(IntIdCode))
                return null;

            m_DataLines.put(IntIdCode, pDataLine);

            return pDataLine;
        }


        // get data function
        public DataLine GetDataLine(int IdCode)
        {
                Integer IntIdCode = new Integer(IdCode);
                DataLine dataLine = (DataLine) m_DataLines.get(IntIdCode);
                return dataLine;
        }


        public GregorianCalendar GetTime()
        {
                DataLine pDataLine = (DataLine)
                         m_DataLines.get(new Integer(ID_CODE_DATE_TIME));
                // get, applied to Hashtable, delivers null if key (ID_CODE_DATE_TIME) does not exist
                //      otherwise it delivers the value (DataLine)

                if (pDataLine == null)
                {
                        return null; // not found
                }

                return ((DateTimeDataLine) pDataLine).GetTime();
        }


        public int GetTotalNrOfLayers()
        // Total number of layers
        {
                DataLine pDataLine = (DataLine)
                         m_DataLines.get(new Integer(ID_CODE_LAYER_HEIGHT));

                if (pDataLine == null)
                {
                        return 0; // not found
                }

                // Get nr of layers from layer heights
                // 1 substracted because first value is soil depth
                int TotalNrOfLayers = ((ZValuesDataLine) pDataLine).GetNrOfValues();
                if (TotalNrOfLayers == GetNrOfSnowLayers()) // all values >0
                  return TotalNrOfLayers;
                else
                  return TotalNrOfLayers - 1; // 1st value is the soil depth
        }


        public int GetNrOfSnowLayers()
        // Number of layers above surface
        {
                DataLine pDataLine = (DataLine)
                         m_DataLines.get(new Integer(ID_CODE_LAYER_HEIGHT));

                if (pDataLine == null)
                {
                        return 0; // not found
                }

                // Get nr of layers from layer heights
                return ((ZValuesDataLine) pDataLine).GetNrOfSnowValues();
        }


        public int GetNrOfLayers(int IdCode)
        // Number of layers above surface
        {
                DataLine pDataLine = (DataLine)
                         m_DataLines.get(new Integer(IdCode));

                if (pDataLine == null)
                {
                        return 0; // not found
                }

                // Get nr of layers from layer heights
                return ((ZValuesDataLine) pDataLine).GetNrOfValues();
        }


        public boolean SoilDataExist(int IdCode)
        // Checks if soil data exist for the given IdCode
        {
            if (GetTotalNrOfLayers() == GetNrOfSnowLayers())
              return false; // no soil data exist in file
            else if (GetNrOfLayers(IdCode) == GetNrOfSnowLayers())
              return false; // soil data exist, but not for current IdCode
            else if (GetNrOfLayers(IdCode) == GetTotalNrOfLayers())
              return true;
            else
              return false;
        }


        public boolean SoilDataExist()
        // Checks if soil data exist in the ID_CODE_LAYER_HEIGHT data line
        {
            if (GetTotalNrOfLayers() == GetNrOfSnowLayers())
              return false;
            else
              return true;
        }


        public float GetZData(int IdCode, int LayerNr)
        // Extracts data for a given IdCode and layer number
        {
                DataLine pDataLine = GetDataLine(IdCode);

                //Steiniger, Schirmer: added && LayerNr >=0, because some .pro files had value -1.
                if (pDataLine != null && LayerNr >= 0)
                {
                        float Value = ((ZValuesDataLine) pDataLine).GetZValue(LayerNr);

                        return Value;
                }
                else
                {
                        return (float) -999.9; // error
                }
        }


        public int GetNrOfValues(int IdCode)
        // Extracts number of values in line for givien IdCode
        {
                DataLine pDataLine = GetDataLine(IdCode);

                if (pDataLine != null)
                {
                        int Value = ((ZValuesDataLine) pDataLine).m_NrOfValues;

                        return Value;
                }
                else
                {
                        return (int) -999; // error
                }
        }




        // Verifies if the data values exist and if values are reasonable (e.g. NrOfLayers are ok).
        // return   true     if ok
        //          false    if error
        public boolean CheckDataValid(int IdCode)
        {
                DataLine pDataLine = GetDataLine(IdCode);

                if (pDataLine == null)
                {
                   return false; // Id code doesn't exist
                }

/*              GetNrOfSnowLayers delivers 0 because pDataLine is null in that method!!
                if (pDataLine.GetDataType() == Z_VALUES_DATA_LINE)
                {
                   // actual layer number of data line must be either the number
                   // of snow layers or the total number of layers ID_CODE_LAYER_HEIGHT data line
                   if (( ((ZValuesDataLine) pDataLine).GetNrOfValues() != GetNrOfSnowLayers() ) &&
                       ( ((ZValuesDataLine) pDataLine).GetNrOfValues() != GetTotalNrOfLayers()) )
                   {
                       return false;
                   }
                }
*/
                return true;
        }

} // end class DataEntry




