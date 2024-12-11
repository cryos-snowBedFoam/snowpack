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
// FileDisplay: Displays the contents of a file
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.text.ComponentView;


// Displays the error file
public class FileDisplay extends JDialog
{
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jButton_OK = new JButton();
  String errorText;
  String filename;
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea = new JTextArea();


  public FileDisplay(Frame frame, String title, boolean modal, String filename)
  {
    super(frame, title, modal);
    this.filename = filename;
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


  public FileDisplay()
  {
    this(null, "", false, "");
  }


  void jbInit() throws Exception
  {
    panel1.setLayout(borderLayout1);
    jButton_OK.setText("OK");
    jButton_OK.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_OK_actionPerformed(e);
      }
    });
    jTextArea.setText("Loading File not successful.");
    jTextArea.setEditable(false);
    panel1.setMinimumSize(new Dimension(500, 500));
    panel1.setPreferredSize(new Dimension(500, 500));
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jButton_OK, null);
    panel1.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTextArea, null);

    FileReader reader = null;
    try {
      reader = new FileReader(filename);
      jTextArea.read(reader, filename);
      }
    catch (IOException exception)
      {
      jTextArea.setText("IOException reading File");
      }
    if (reader != null) reader.close();
  }


  void jButton_OK_actionPerformed(ActionEvent e)
  {
    dispose();
  }
}
