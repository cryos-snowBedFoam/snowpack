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
// AxisDialog: Dialog box to chose the x- and y-axis settings
//             (minimum and maximum value, number of grids)
///////////////////////////////////////////////////////////////////////////////

package ProWin;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;


public class AxisDialog extends JDialog
{
  JPanel panel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  XYLayout xYLayout3 = new XYLayout();
  XYLayout xYLayout4 = new XYLayout();
  JLabel jLabel1 = new JLabel("", JLabel.CENTER);
  JPanel jPanel4 = new JPanel();
  JLabel jLabel_EndValue = new JLabel();
  JLabel jLabel_StartValue = new JLabel();
  JLabel jLabel_Partitions = new JLabel();
  JTextField jText_EndValue = new JTextField();
  JTextField jText_StartValue = new JTextField();
  JTextField jText_Partitions = new JTextField();
  JLabel jUnitLabel1 = new JLabel();
  JLabel jUnitLabel2 = new JLabel();
  JButton jButton_OK = new JButton();
  JButton jButton_Cancel = new JButton();
  JPanel jPanel7 = new JPanel();
  JLabel jLabel_Error = new JLabel("", JLabel.CENTER);
  XYLayout xYLayout8 = new XYLayout();
  float startValue, endValue;
  int partitions;
  String Unit;
  String ParameterName;
  JLabel jLabel2 = new JLabel();
  GridLayout gridLayout1 = new GridLayout();

  public AxisDialog(Frame frame, String title, boolean modal,
                     float startValue, float endValue, int partitions,
                     String Unit, String ParameterName)
  {
    super(frame, title, modal);
    this.startValue = startValue;
    this.endValue = endValue;
    this.partitions = partitions;
    this.Unit = Unit;
    this.ParameterName = ParameterName;
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

  public AxisDialog()
  {
    this(null, "", false, 0, 0, 0, "", "");
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(xYLayout1);
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setLayout(xYLayout2);
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setLayout(xYLayout3);
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setLayout(xYLayout4);
    jLabel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel1.setText(ParameterName);
    jPanel4.setLayout(gridLayout1);
    jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_EndValue.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_EndValue.setText("End Value:");
    jLabel_StartValue.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_StartValue.setText("Start Value:");
    jLabel_Partitions.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_Partitions.setText("Partition Lines:");
    jText_EndValue.setText((new Float(endValue)).toString());
    jText_EndValue.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_StartValue.setText((new Float(startValue)).toString());
    jText_StartValue.setHorizontalAlignment(SwingConstants.RIGHT);
    jText_Partitions.setText((new Integer(partitions)).toString());
    jText_Partitions.setHorizontalAlignment(SwingConstants.RIGHT);
    jUnitLabel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jUnitLabel1.setText(Unit);
    jUnitLabel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jUnitLabel2.setText(Unit);
    jButton_OK.setMinimumSize(new Dimension(51, 100));
    jButton_OK.setPreferredSize(new Dimension(51, 100));
    jButton_OK.setText("OK");
    jButton_OK.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_OK_actionPerformed(e);
      }
    });
    jButton_Cancel.setText("Cancel");
    jButton_Cancel.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Cancel_actionPerformed(e);
      }
    });
    jPanel7.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel7.setLayout(xYLayout8);
    jLabel_Error.setForeground(Color.red);
    jLabel_Error.setBorder(BorderFactory.createRaisedBevelBorder());
    gridLayout1.setColumns(3);
    gridLayout1.setRows(3);
    gridLayout1.setVgap(5);
    getContentPane().add(panel1);
    panel1.add(jPanel1, new XYConstraints(5, 2, 392, 74));
    jPanel1.add(jLabel1, new XYConstraints(21, 27, 349, 22));
    panel1.add(jPanel2, new XYConstraints(6, 78, 390, 162));
    jPanel2.add(jPanel4, new XYConstraints(49, 16, 290, 129));
    jPanel4.add(jLabel_EndValue, null);
    jPanel4.add(jText_EndValue, null);
    jPanel4.add(jUnitLabel1, null);
    jPanel4.add(jLabel_StartValue, null);
    jPanel4.add(jText_StartValue, null);
    jPanel4.add(jUnitLabel2, null);
    jPanel4.add(jLabel_Partitions, null);
    jPanel4.add(jText_Partitions, null);
    jPanel4.add(jLabel2, null);
    panel1.add(jPanel3, new XYConstraints(8, 263, 388, 37));
    jPanel3.add(jButton_Cancel, new XYConstraints(230, 4, 77, 27));
    jPanel3.add(jButton_OK, new XYConstraints(80, 4, 77, 27));
    panel1.add(jPanel7, new XYConstraints(7, 241, 390, 22));
    jPanel7.add(jLabel_Error, new XYConstraints(39, 0, 314, 17));

    partitions = -1;
    // remains -1 if this dialog box is not exited with correctly chosen data;
    // needed for check in MenuFrame; overwritten if input data are OK

  }

  void jButton_Cancel_actionPerformed(ActionEvent e)
  {
    dispose();
  }

  void jButton_OK_actionPerformed(ActionEvent e)
  {
    // current text in textFields
    String startValueStr = jText_StartValue.getText();
    String endValueStr   = jText_EndValue.getText();
    String partitionsStr = jText_Partitions.getText();

    if (partitionsStr.length()>2)
    {
      jLabel_Error.setText("Error (Partitions): Only 2 digits allowed!");
      return; // Input line too long
    }

    if (startValueStr.length()<1 || endValueStr.length()<1 ||
        partitionsStr.length()<1)
    {
      jLabel_Error.setText("No empty boxes permitted!");
      return; // Edit box not filled yet
    }

/* first version; better to use parseFloat() or parseInt()
    for (int i=0; i<startValueStr.length(); i++)
    {
         exitpoint: {
           for(int j=48; j<58; j++)
           {
           if ((int) startValueStr.charAt(i) == j) break exitpoint;
           }
           jLabel_Error.setText("Error (Start Value): Input not numeric or <0!");
           return; // Character is not a number
         } // end exitpoint
     } // end for
*/

    // Check if startValueStr can be converted to Float
    try  {Float.parseFloat(startValueStr); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Start Value not valid!");
      return;
    }

    // Check if endValueStr can be converted to Float
    try  {Float.parseFloat(endValueStr); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: End Value not valid!");
      return;
    }

    // Check if partitionsStr can be converted to Integer
    try {Integer.parseInt(partitionsStr); }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Partition Lines not valid!");
      return;
    }

    // Check if start value < end value
    if (Float.parseFloat(endValueStr)<Float.parseFloat(startValueStr))
    {
       jLabel_Error.setText("Error: End Value < Start Value. Change Input!");
       return;
    }

    // Check if at least one partition
    if (Integer.parseInt(partitionsStr)<1)
    {
       jLabel_Error.setText("Error: Partitions < 1. Change Input!");
       return;
    }

     startValue = Float.parseFloat(startValueStr);
     endValue   = Float.parseFloat(endValueStr);
     partitions = Integer.parseInt(partitionsStr);

     dispose();
  }

}

