
//Titel:        SnowPack Visualisierung
//Version:
//Copyright:    Copyright (c) 1999
//Autor:       Spreitzhofer
//Organisation:      SLF
//Beschreibung:  Java-Version von SnowPack.
//Integriert die C++-Version von M. Steiniger und die IDL-Version von M. Lehning.

package ProWin;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;

public class YesNoDialog extends JDialog
{
  JPanel panel1 = new JPanel();
  JPanel jPanel1 = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  XYLayout xYLayout3 = new XYLayout();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout4 = new XYLayout();
  JLabel jLabel1;
  JLabel jLabel2;
  JButton jButton_Yes = new JButton();
  JButton jButton_No = new JButton();
  String line1, line2, label1_text, label2_text;
  boolean action1 = false;
  boolean action2 = false;

  public YesNoDialog(Frame frame, String title, String line1, String line2,
                     String label1_text, String label2_text, boolean modal)
  {
    super(frame, title, modal);

    this.line1 = line1;
    this.line2 = line2;
    this.label1_text = label1_text;
    this.label2_text = label2_text;

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

  public YesNoDialog()
  {
    this(null, "", "", "", "", "", false);
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(xYLayout2);
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setLayout(xYLayout3);
    xYLayout2.setHeight(137);
    xYLayout2.setWidth(320);
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setLayout(xYLayout4);
    jButton_Yes.setText(label1_text);
    jButton_Yes.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_Yes_actionPerformed(e);
      }
    });
    jButton_No.setText(label2_text);
    jButton_No.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton_No_actionPerformed(e);
      }
    });
    jLabel1 = new JLabel(line1, JLabel.CENTER);
    jLabel2 = new JLabel(line2, JLabel.CENTER);
    getContentPane().add(panel1);
    panel1.add(jPanel1, new XYConstraints(2, 2, 317, 91));
    jPanel1.add(jLabel1, new XYConstraints(14, 13, 289, 27));
    jPanel1.add(jLabel2, new XYConstraints(13, 44, 289, 29));
    panel1.add(jPanel3, new XYConstraints(3, 93, 316, 43));
    jPanel3.add(jButton_Yes, new XYConstraints(42, 8, 95, 24));
    jPanel3.add(jButton_No, new XYConstraints(171, 8, 99, 25));
  }

  void jButton_Yes_actionPerformed(ActionEvent e)
  {
    action1 = true;
    dispose();
  }

  void jButton_No_actionPerformed(ActionEvent e)
  {
    action2 = true;
    dispose();
  }
}

