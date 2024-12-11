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
// FileStatus: Dialog box showing the state of reading a file
///////////////////////////////////////////////////////////////////////////////

// not used, problems:
//   if modal=true, file is not read
//   if modal=false, display is not correct


package ProWin;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;

public class FileStatus extends JDialog
{
  JPanel panel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  XYLayout xYLayout1 = new XYLayout();
  static JLabel jLabel2 = new JLabel();
  static JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  static String FileName;

  public FileStatus(Frame frame, String title, boolean modal, String FileName)
  {
    super(frame, title, modal);
    this.FileName = FileName;

    try
    {
      jbInit();
      pack();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  //public FileStatus()
  //{
  //  this(null, "", false);
  //}

  void jbInit() throws Exception
  {
    panel1.setLayout(xYLayout1);
    jLabel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel1.setText("Please wait! Reading file " + FileName + " ... ");
    jLabel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel2.setText("Current input date: not available yet");
    jLabel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel3.setText("Number of format errors: not available yet");
    xYLayout1.setHeight(0);
    xYLayout1.setWidth(400);
    jLabel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel4.setText("Check the error file for details.");
    getContentPane().add(panel1);
/*  panel1.add(jLabel2, new XYConstraints(70, 57, 268, 30));
    panel1.add(jLabel3, new XYConstraints(69, 101, 270, 22));
    panel1.add(jLabel1, new XYConstraints(68, 20, 270, 22));
    panel1.add(jLabel4, new XYConstraints(69, 121, 270, -1));
*/  }

  static void writeDate(String date)
  {
    // MS: not used: jLabel2.setText("File reading: Current input date: " + date);
  }

  static void writeError(int ErrorNumber)
  {
    // MS: not used: jLabel3.setText("Number of format errors: " + ErrorNumber);
  }
}

