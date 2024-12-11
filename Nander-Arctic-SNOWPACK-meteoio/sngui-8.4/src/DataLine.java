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
// DataLine: Handles one line of the data file
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.io.*;


public abstract class DataLine
{

   protected  int m_IdCode;

   // DataLine construction
   public void DataLine()
   {
      m_IdCode = 0;
   }


   // Read the line
   public abstract boolean ParseLine( String Line) throws IOException;


   // Data line type functions
   public void SetIdCode( int IdCode ) { m_IdCode = IdCode; };
   public  int  GetIdCode( )           { return m_IdCode; };

   public abstract int  GetDataType();
   public abstract boolean HasDate();

} // end class DataLine



