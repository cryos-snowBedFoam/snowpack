///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualization
//Version:
//Copyright:    Copyright (c) 2001
//Author:       G. Spreitzhofer
//Organization: SLF
//Description:  Java-Version of SnowPack.
//Integrates the C++-Version of M. Steiniger
//       and the IDL-Version of M. Lehning/P.Bartelt.
///////////////////////////////////////////////////////////////////////////////
// ErrorFile: Writes an error file
///////////////////////////////////////////////////////////////////////////////

package ProWin;

import java.io.*;
import java.util.*;


public class ErrorFile
{
        static String Path = ""; // Later set to ErrorFilePath given in SETUP.INI
        static String FileName = "ErrorFile";
        static int LineNr;

        ErrorFile() {}

        // Writes error messages (issued by DataFile, ProDataEntry, MetDataEntry,
        // ZValuesDataLine, MetValuesDataLine and DateTimeDataLine)
        // to output file and to the console
        public static void write(String ErrorExpr, int ErrNr, String ProcName) throws IOException
        {
                String MessageLine = "Line " + LineNr + ">>>" +
                         ErrorExpr + " " + ErrNr + ", " + ProcName;

                // Console output
                System.out.println(MessageLine);

        /* // First version for file output, using FileWriter.
                // Carriage return problem unsolved
                char buffer[] = new char[MessageLine.length()];

                // MessageLine transferred to Character-field buffer
                MessageLine.getChars(0, MessageLine.length(), buffer, 0);

                FileWriter f = new FileWriter(Path + FileName, true);
                // true: append mode

                f.write(buffer);
                f.close();
        */

        /* // Output of errors to file deactivated
                FileOutputStream fout = new FileOutputStream(Path+FileName, true);
                PrintWriter pout = new PrintWriter(fout);
                pout.println(MessageLine);
                pout.close();
                fout.close();
        */

                // System.out.println(Path + FileName);

        }

        public static void reset() throws IOException
        // new empty ErrorFile is created
        {
                FileWriter f = new FileWriter(Path + FileName);
                f.close();
        }

        public static void SetLineNr(int NewLineNr)
        // LineNr is set to active line number of calling function
        {
                LineNr = NewLineNr;
        }

        public static void SetPath(String SetupPath)
        // Set path of the error file
        {
                Path = SetupPath;
        }

}

/*
Example data input file:
0500,0130,0053,0130,0053,05.10.1998 12:01
0501,5,1.5,5.1,8,16.2,24.7
0502,5,171,169,143,126,119
0503,5,-.6,-2.6,-4,-5.2,-4.2
ad
0400,5,1,0,0,0,0
0503,5,.4,.4,.4,.4,.4
X1235
0513,5,122,122,211,121,110
0500,0130,0313,0130,0053,06.10.1998 12:01
0501,
0502,-1,149,210,137,56,57
0503,X4,0,0,.4,0
0506,4,3X,3,3,4
0512,4,.4,.4,.4,.4,555
0513,4,121,122,121,121

Example for the error messages produced by the sample data above:
H:\main\datafile>       java ProDataFile
=== Start ReadDataFile(test.pro): Thu Mar 22 12:24:06 GMT+01:00 2001
Line 5>>>ERR_LINE_SYNTAX 2, ProDataEntry.ParseDataLine
Line 6>>>ERR_LINE_SYNTAX 3, ProDataEntry.ParseDataLine
Line 7>>>ERR_LINE_SYNTAX 5, ProDataEntry.ParseDataLine
Line 8>>>ERR_LINE_SYNTAX 7, ProDataEntry.ParseDataLine
Line 11>>>ERR_LINE_SYNTAX 3, ZValuesDataLine.ParseLine
Line 11>>>ERR_LINE_SYNTAX 6, ProDataEntry.ParseDataLine
Line 12>>>ERR_LINE_SYNTAX 4, ZValuesDataLine.ParseLine
Line 12>>>ERR_LINE_SYNTAX 6, ProDataEntry.ParseDataLine
Line 13>>>ERR_LINE_SYNTAX 5, ZValuesDataLine.ParseLine
Line 13>>>ERR_LINE_SYNTAX 6, ProDataEntry.ParseDataLine
Line 14>>>ERR_LINE_SYNTAX 6, ZValuesDataLine.ParseLine
Line 14>>>ERR_LINE_SYNTAX 6, ProDataEntry.ParseDataLine
Line 15>>>ERR_LINE_SYNTAX 7, ZValuesDataLine.ParseLine
Line 15>>>ERR_LINE_SYNTAX 6, ProDataEntry.ParseDataLine
*/


/*      // Read error file into String
        public static String read() throws IOException
        {
                try
                {
                  FileInputStream fin = new FileInputStream(Path+FileName);


                  ArrayList al = new ArrayList();
                  int i;
                  do {
                    i = fin.read();
                    if(i != -1) al.add(new Integer(i));
                    if(i != -1) al.add(new Character((char) i));
                    //if(i != -1) al.add("C");
                  } while(i != -1);
                  fin.close();

                  if (al.size()<1)
                    return "No Errors!";

                  Object ob[] = al.toArray();
                  char ch[] = new char[ob.length];
//                for (int j = 0; j < ob.length; j++)
//                hier Fehler! Obj kann nicht in Char umgewandelt werden!
                  ch[j] = ((char) ob[j]).charValue();
                  //  ch[j] = ((char) ob[j]);

//                  return new String(ch);
                  return "Das ist der String!";

                }
                catch(FileNotFoundException e)
                {
                  return "File "+Path+FileName+ " not found!";
                }
        }
*/



