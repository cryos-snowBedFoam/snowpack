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
// MenuFrame_AboutBox: Provides basic information about the package
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.control.BevelPanel;
import com.borland.jbcl.control.ImageControl;

public class MenuFrame_AboutBox extends JDialog implements ActionListener
{

  BevelPanel panel1 = new BevelPanel();
  BevelPanel panel2 = new BevelPanel();
  BevelPanel insetsPanel1 = new BevelPanel();
  BevelPanel insetsPanel2 = new BevelPanel();
  BevelPanel insetsPanel3 = new BevelPanel();
  JButton button1 = new JButton();
  ImageControl imageControl1 = new ImageControl();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  String product = "SnowPack Visualization";
  String version = "1.0";
  String copyright = "Copyright (c) 2001";
  String comments = "Java-Version of SnowPack.";
  public MenuFrame_AboutBox(Frame parent)
  {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
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

  private void jbInit() throws Exception 
  {
    this.setTitle("Info");
    setResizable(false);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel1.setBevelInner(BevelPanel.FLAT);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setMargins(new Insets(10, 10, 10, 10));
    insetsPanel2.setBevelInner(BevelPanel.FLAT);
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setText(product);
    label2.setText(version);
    label3.setText("Copyright (c) 2001");
    label4.setText(comments);
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setMargins(new Insets(10, 60, 10, 10));
    insetsPanel3.setBevelInner(BevelPanel.FLAT);
    button1.setText("OK");
    button1.addActionListener(this);
    imageControl1.setImageName("");
    insetsPanel2.add(imageControl1, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
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