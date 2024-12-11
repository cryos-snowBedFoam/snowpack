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
// DataEntry: Handles set of data lines referring to a specific date and time
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.util.*;
import java.io.*;


public abstract class DataEntry implements Cloneable
{
   protected  int      m_IdCode;

   // DataEntry construction
   DataEntry() { }

   // Read the data line
   public abstract DataLine ParseDataLine( String Line) throws IOException;

   // Get data function
   public abstract DataLine GetDataLine( int IdCode );

   // Get Time/Date
   public abstract GregorianCalendar GetTime();

   // Verifies if the data values exists and has reasonable values (e.g. NrOfLayers are ok).
   public abstract boolean CheckDataValid( int IdCode );

} // end class DataEntry


