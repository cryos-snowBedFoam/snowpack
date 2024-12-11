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
// MessageBox: Display of a message box
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.control.BevelPanel;
import com.borland.jbcl.control.ImageControl;
import com.borland.jbcl.layout.*;

public class MessageBox extends JDialog implements ActionListener
{

  BevelPanel panel1 = new BevelPanel();
  JButton button1 = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  String line1, line2;
  JLabel jLabel1;
  JLabel jLabel2;

  public MessageBox(Frame parent, String title, String line1, String line2)
  {
    super(parent, title, true); // modal = true
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.line1 = line1;
    this.line2 = line2;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    pack();
  }

  public MessageBox(Frame parent, String title, String line1, String line2, int millisec)
  {
    super(parent, title, true); // modal = true
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.line1 = line1;
    this.line2 = line2;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    pack();

    try {
      //?? Tried to display the MessageBox for some seconds, then automatically disappear
      // not successful (setVisible etc.)

      //System.out.println("Start of pause");
      //Thread.currentThread().sleep(millisec);
      //dispose();
      }
    catch(Exception e) {}
  }

  private void jbInit() throws Exception
  {
    //this.setTitle("Info");
    setResizable(false);
    panel1.setLayout(borderLayout1);
    button1.setText("OK");
    button1.addActionListener(this);
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setLayout(xYLayout1);
    jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel1 = new JLabel(line1, JLabel.CENTER);
    jLabel2 = new JLabel(line2, JLabel.CENTER);
    this.getContentPane().add(panel1, null);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(button1, null);
    panel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jPanel3, new XYConstraints(169, 8, -1, -1));
    jPanel2.add(jLabel1, new XYConstraints(13, 13, 345, 31));
    jPanel2.add(jLabel2, new XYConstraints(12, 47, 342, 30));
    panel1.add(jPanel4, BorderLayout.NORTH);
  }

  protected void processWindowEvent(WindowEvent e)
  {
    if(e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      cancel();
    }
    super.processWindowEvent(e);
  }

  void cancel()
  {
    dispose();
  }

  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource() == button1)
    {
      cancel();
    }
  }
}