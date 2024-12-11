///////////////////////////////////////////////////////////////////////////////
//Titel:        SnowPack Visualization
//Version:
//Copyright:    Copyright (c) 2001
//Author:       M. Steiniger (method BigProperty() by G. Spreitzhofer
//Organization: SLF
//Description:  Java-Version of SnowPack.
//Integrates the C++-Version of M. Steiniger
//       and the IDL-Version of M. Lehning/P.Bartelt.
///////////////////////////////////////////////////////////////////////////////
// IniFile: used to read, write and get information about files in
//          the Windows INI-file format
///////////////////////////////////////////////////////////////////////////////
//does not write SOIL_ALBEDO and BARE_SOIL_z0 (Schirmer 06.06)

package ProWin;

import java.io.*;

import java.util.*;


/**

 *
 * @author  Manfred Steiniger (SLF)
 * @version 0.1  21.02.2001
*/

public class IniFile
{

   /** The file name of the INI file */
   protected   String      fileName;

   /** all sections of the file */
   protected   ArrayMap sectionList = new ArrayMap();

   /** the current selected section entries */
   protected   ArrayMap actEntryList = null;


   /**
    * Class Constructor.
    * Reads the INI file
    *
    * @param   iniFileName    filename of the ini file
    * @exception  IOException, if an error occurs during reading
    */
   public IniFile(String iniFileName) throws IOException
   {
      setFileName(iniFileName);
      read();
   }


   /**
    * Class Constructor.
    * Creates a new, empty INI file
    *
    */
   public IniFile()
   {
      setFileName("");
   }


   /**
    * Clears all entries of the INI file
    *
    */
   public void clear()
   {

      for (Iterator secIt = sectionList.entrySet().iterator(); secIt.hasNext(); )
      {
        Map.Entry secEntry = (Map.Entry) secIt.next();
        Map entryList = (Map) secEntry.getValue();
        entryList.clear();
      }

      // Alle Sektionsnamen loeschen
      sectionList.clear();
      actEntryList = null;
   }


   /**
    * Sets the filename of the INI file
    *
    * @param   iniFileName    filename of the INI file
    */
   public void setFileName(String iniFileName)
   {
      this.fileName = iniFileName;
   }



   /**
    * Set the active section [section] in the INI-File.
    * If it doesn't exist create a new section
    *
    * @param   sectionName    name of the [section] in the INI file
    */
   public void setSection(String sectionName)
   {
      actEntryList = (ArrayMap) sectionList.get(sectionName);
      if ( actEntryList == null )
      {
         // Sektion existiert nicht => Neue erzeugen
         actEntryList = new ArrayMap();
         sectionList.put(sectionName, actEntryList);
      }
   }


   /**
    * Sets an entry in the INI file
    *
    * @param   sectionName  name of the [section] in the INI file
    * @param   key          the key part of an entry "Key = Value"
    * @param   value        the value part of an entry "Key = Value"
    */
   public void setEntry(String sectionName, String key, String value)
   {
      setSection(sectionName);
      setEntry(key, value);
   }



   /**
    * Sets a entry in the INI file.
    * It is necessary to call setSection first.
    *
    * @param   key          the key part of an entry "Key = Value"
    * @param   value        the value part of an entry "Key = Value"
    */
   public void setEntry(String key, String value)
   {
      actEntryList.put(key, value);
   }


   /**
    * Get an entry
    *
    * @param   sectionName  name of the [section] in the INI file
    * @param   key          the key part of an entry "Key = Value"
    * @param   defaultValue the return value if the key wasn't found
    * @return  the value which belongs to the key or defaultValue
    */
   public String getEntry(String sectionName, String key, String defaultValue)
   {
      setSection(sectionName);
      return getEntry(key, defaultValue);
   }



   /**
    * Get an entry
    * It is necessary to call setSection first.
    *
    * @param   key          the key part of an entry "Key = Value"
    * @param   defaultValue the return value if the key wasn't found
    * @return  the value which belongs to the key or defaultValue
    * @exception  NullPointerException if setSection have not called
    */
   public String getEntry(String key, String defaultValue)
   throws NullPointerException
   {
      if (actEntryList == null)
         // setSection haven't been called
         throw new NullPointerException();
      else
      {
        String retStr = (String) actEntryList.get(key);
        if (retStr == null)
          return defaultValue;
        else
          return retStr;
      }
   }

   public String getEntry(String key)
   throws NullPointerException
   {
      return getEntry(key, null);
   }


   /**
    * Removes an entry from the INI file
    *
    * @param   sectionName  name of the [section] in the INI file
    * @param   key          the key part of an entry "Key = Value"
    */
   public void removeEntry(String sectionName, String key)
   {
      setSection(sectionName);
      removeEntry(key);
   }



   /**
    * Removes an entry from the INI file
    * It is necessary to call setSection first.
    *
    * @param   key          the key part of an entry "Key = Value"
    */
   public void removeEntry(String key)
   {
      actEntryList.remove(key);
   }


   /**
    * Removes all entries from a section
    *
    * @param   sectionName  name of the [section] in the INI file
    */
   public void removeEntries(String sectionName)
   {
      actEntryList = (ArrayMap) sectionList.get(sectionName);
      if ( actEntryList != null )
         actEntryList.clear();
   }

  /**
    * Removes all entries from a section and the section
    *
    * @param   sectionName  name of the [section] in the INI file
    */
   public void removeSection(String sectionName)
   {
      actEntryList = (ArrayMap) sectionList.get(sectionName);
      if ( actEntryList != null )
      {
        actEntryList.clear();
        sectionList.remove(sectionName);
      }
   }


   /**
    * Rewrites the INI file
    *
    * @exception  IOException, if error while writing
    */
   public void write() throws IOException
   {
      write(this.fileName);
   }


  /**
    * Rewrites the INI file (blanks around =)
    *
    * @exception  IOException, if error while writing
    */
   public void writeWithBlanks() throws IOException
   {
      writeWithBlanks(this.fileName);
   }


   /**
    * rewrites the INI file
    * does not write SOIL_ALBEDO and BARE_SOIL_z0 (Schirmer 06.06)
    *
    * @param   iniFileName    filename of the INI file
    * @exception  IOException, if error while writing
    */
   public void write(String iniFileName) throws IOException
   {
      OutputStream outFile = new FileOutputStream(iniFileName);
      PrintWriter prnt = new PrintWriter(outFile);

      String section;
      String key;

      for (int i=0; i < getSectionSize(); i++ )
      {
         // get next section and writes [section] line
         section = getSection(i);

         prnt.println();
         prnt.println("[" + section + "]");

         setSection(section);

         // write the entries of the section.
         for (int j=0; j < getKeySize(); j++)
         {
             if(!getKey(j).equals("SOIL_ALBEDO") &&
                !getKey(j).equals("BARE_SOIL_z0")) {

                 key = getKey(j);
                 prnt.print(key);
                 prnt.write('=');
                 prnt.println(getEntry(key, ""));
             }
         }
      }

      prnt.flush();
      outFile.close();
   }

   /**
    * rewrites the INI file
    * blanks around =, otherwise the same as write(String)
    * does not write SOIL_ALBEDO and BARE_SOIL_z0 (Schirmer 06.06)
    *
    * @param   iniFileName    filename of the INI file
    * @exception  IOException, if error while writing
    */
   public void writeWithBlanks(String iniFileName) throws IOException
   {
      OutputStream outFile = new FileOutputStream(iniFileName);
      PrintWriter prnt = new PrintWriter(outFile);

      String section;
      String key;

      for (int i=0; i < getSectionSize(); i++ )
      {
         // get next section and writes [section] line
         section = getSection(i);

         prnt.println();
         prnt.println("[" + section + "]");

         setSection(section);

         // write the entries of the section.
         for (int j=0; j < getKeySize(); j++)
         {
             if(!getKey(j).equals("SOIL_ALBEDO") &&   //(Schirmer)
                !getKey(j).equals("BARE_SOIL_z0")) {

                 key = getKey(j);
                 prnt.print(key);
                 prnt.write(" = ");
                 prnt.println(getEntry(key, ""));
             }
         }
      }

      prnt.flush();
      outFile.close();

   }



   /**
    * reads the INI file.
    * The file name must be specified via the constructor or setFileName
    *
    * @exception  IOException, while error during reading the file
    * @exception  FileNotFoundException, if the INI file couldn't be found
    */
   public void read() throws IOException
   {
      FileInputStream InFile = new FileInputStream(fileName);
      BufferedReader InReader = new BufferedReader(new InputStreamReader(InFile));
      int ch;
      String line, key, entry;

      // Elemente loeschen
      clear();

      // read line by line while no EOF
      do
      {
         line = InReader.readLine();
         if (line != null)
         {
            // remove blanks at start and end
            line = line.trim();

            // check for comment line
            if (!line.equals("") &&
                !line.startsWith(";") &&
                !line.startsWith("#"))
            {
               if (line.startsWith("[") &&
                   line.endsWith("]") &&
                   line.length() > 2 )
               {
                  // new section
                  // remove blank, "[", "]"
                  line = line.substring(1);
                  line = line.substring(0, line.length()-1);
                  line = line.trim();
                  setSection(line);
               }
               else if (line.indexOf("=") != -1)
               {
                  // new entry
                  key = line.substring(0, line.indexOf("="));
                  key = key.trim();
                  entry = line.substring(line.indexOf("=") + 1);
                  entry = entry.trim();

                  /*
                  // remove '...' or "..."
                  if ( entry.length() > 1 &&
                      (entry.startsWith("'") && entry.endsWith("'") ||
                       entry.startsWith("\"") && entry.endsWith("\"")))
                  {
                    entry = entry.substring(1);
                    entry = entry.substring(0, entry.length()-1);
                  }
                  */
                  setEntry(key, entry);
               }
            }
         } // if (line != null)
      } while (line != null);

      InReader.close();
      InFile.close();
   }




   /**
    * get the number of sections
    *
    * @return  number of sections
   */
   public int getSectionSize()
   {
     return sectionList.size();
   }


   /**
    * get the section[index]
    *
    * @return  the name of the section
   */
   public String getSection(int index)
   {
     return (String) sectionList.getKey(index);
   }


   /**
    * Get the number of keys in the current section.
    * It is necessary to call setSection first.
    *
    *
    * @return  number of sections
   */
   public int getKeySize()
   {
      return actEntryList.size();
   }


   /**
    * Get the key[index] of the current section.
    * It is necessary to call setSection first.
    *
    * @return  the name of the section
   */
   public String getKey(int index)
   {
     return (String) actEntryList.getKey(index);
   }



   /**
   * original Inifile structure:
   *   Hashtable, keys are assigned to objects, these objects are properties
   *   (key=value - structure)
   * method puts all property objects together to one single property object BigPropList;
   * if the keys themselves have property structure [key=value], they are also
   * added to BigPropList;
   * BigPropList is returned (before, optionally key and value are interchanged);
   */
   public Properties BigProperty(boolean interchange) throws IOException
   {
      Properties BigPropList = new Properties();

      String section;
      String key;
      String value;

      for (int i=0; i < getSectionSize(); i++ )
      {
         // get next [section]
         section = getSection(i);

         if (section.indexOf("=") != -1) // section has structure [key=value]
         {
            key = section.substring(0, section.indexOf("=")).trim();
            value = section.substring(section.indexOf("=") + 1, section.length()).trim();

            if (interchange)  BigPropList.put(value, key);
            else              BigPropList.put(key, value);
         }
         else // section does not contain "="
         {
            setSection(section);

            // write the entries of the section.
            for (int j=0; j < getKeySize(); j++)
            {
              key = getKey(j);
              value = getEntry(key, "");

              if (interchange)  BigPropList.put(value, key);
              else              BigPropList.put(key, value);
            }
          }
      }

      return BigPropList;
   }


   /* old version
   public Properties BigProperty(boolean interchange) throws IOException
   {
      Enumeration SectionKeys = sectionList.keys();
      Properties BigPropList = new Properties();
      Properties PropList;
      String SectionName;
      String Key;
      String Value;


      while (SectionKeys.hasMoreElements())
      {
              // Naechste Sektion holen und Sektionszeile schreiben
              SectionName = (String) SectionKeys.nextElement();
              PropList = (Properties) sectionList.get(SectionName);
              // prnt.println("\n[" + SectionName + "]");

              // Eintraege einer Sektion schreiben
              // Folgender Algorithmus aus Properties.save() uebernommen
              // jedoch ohne Sonderzeichenersetzung
              Enumeration PropKeys = PropList.keys();
              while (PropKeys.hasMoreElements())
              {
            Key = (String) PropKeys.nextElement();
            Value = (String) PropList.get(Key);

            if (interchange)
              BigPropList.put(Value, Key);
            else
              BigPropList.put(Key, Value);

              }
      }
      return BigPropList;
   }
 */
}
