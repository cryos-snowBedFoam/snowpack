
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
import java.awt.event.*;
import javax.swing.event.*; // new
import com.borland.jbcl.layout.*;

public class SliderTest extends JDialog
{
  JPanel panel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  XYLayout xYLayout1 = new XYLayout();
  JSlider jSlider1 = new JSlider();
  JButton jButton1 = new JButton();
  JSlider jSlider2 = new JSlider();

  public SliderTest(Frame frame, String title, boolean modal)
  {
    super(frame, title, modal);
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

  public SliderTest()
  {
    this(null, "", false);
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(xYLayout1);
    jLabel1.setText("jLabel1");
    jSlider1.setValue(20);
    jSlider1.setBorder(BorderFactory.createRaisedBevelBorder());
    jButton1.setToolTipText("text");
    jButton1.setMargin(new Insets(0, 0, 0, 0));
    jButton1.setText(">");
    jButton1.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton1_actionPerformed(e);
      }
    });
    getContentPane().add(panel1);
    //panel1.add(jSlider1, new XYConstraints(0, 0, -1, -1));
    panel1.add(jLabel1, new XYConstraints(-1, 11, 206, 48));
    //panel1.add(jSlider1, new XYConstraints(9, 1, 202, 25));
    panel1.add(jButton1, new XYConstraints(30, 71, 19, 16));
    panel1.add(jSlider2, new XYConstraints(88, 155, -1, -1));

    ChangeListener changeListener = new BoundedChangeListener();
    jSlider1.addChangeListener(changeListener);
/*
    jSlider1.addChangeListener(new java.awt.event.ChangeListener
    {
       public void stateChanged(ChangeEvent changeEvent)
       {
         Object source = changeEvent.getSource();
         if (source instanceof BoundedRangeModel)
         {
           BoundedRangeModel aModel = (BoundedRangeModel)source;
           if (!aModel.getValueIsAdjusting())
           {
             System.out.println("Changed: " + aModel.getValue());
             jLabel1.setText("Changed: " + aModel.getValue());
           }
         }
         else if (source instanceof JSlider)
         {
           JSlider theJSlider = (JSlider)source;
           if (!theJSlider.getValueIsAdjusting())
           {
              System.out.println("Slider changed: " + theJSlider.getValue());
              jLabel1.setText("Slider changed: " + theJSlider.getValue());
           }
         }
         else
         {
           System.out.println("Something changed: " + source);
         }
       }

    });
*/





  }

  void jButton1_actionPerformed(ActionEvent e)
  {

  }
}



