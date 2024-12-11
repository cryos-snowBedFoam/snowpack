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
// ColorDialog: Dialog box to chose colors from a color table and to
//              lay down minimum and maximum values
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;


public class ColorDialog extends JDialog
{
  JPanel mainpanel = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JPanel jPanel1 = new JPanel();
  JButton jBtn_OK = new JButton();
  JButton jBtn_Cancel = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField jText_StartValue = new JTextField();
  JTextField jText_EndValue = new JTextField();
  Choice tablesChoice = new Choice();
  ColorTab colorTab = new ColorTab();
  JLabel jLabel_Error = new JLabel("", JLabel.CENTER);
  int selColorTab;
  float startValue, endValue;


  public ColorDialog(Frame frame, String title,
                     float startValue, float endValue, int selColorTab, boolean modal)
  {
    super(frame, title, modal);

    this.startValue = startValue;
    this.endValue = endValue;
    this.selColorTab = selColorTab;

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


  void jbInit() throws Exception
  {
    mainpanel.setLayout(xYLayout1);
    jPanel1.setLayout(gridLayout1);
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jBtn_OK.setMinimumSize(new Dimension(51, 50));
    jBtn_OK.setPreferredSize(new Dimension(51, 50));
    jBtn_OK.setText("OK");
    jBtn_OK.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jBtn_OK_actionPerformed(e);
      }
    });
    jBtn_Cancel.setText("Cancel");
    jBtn_Cancel.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jBtn_Cancel_actionPerformed(e);
      }
    });
    jLabel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText("Start Value:");
    jLabel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setText("End Value:");

    Float startValueFloat = new Float(startValue);
    Float endValueFloat   = new Float(endValue);
    String startValueStr = (String) startValueFloat.toString();
    String endValueStr   = (String) endValueFloat.toString();

    jText_StartValue.setText(startValueStr);
    jText_EndValue.setText(endValueStr);
    jLabel_Error.setForeground(Color.red);
    jLabel_Error.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_Error.setText("");
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setLayout(xYLayout3);
    gridLayout1.setRows(2);
    gridLayout1.setVgap(5);
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setLayout(xYLayout2);
    jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel4.setLayout(xYLayout4);
    getContentPane().add(mainpanel);
    mainpanel.add(jPanel2, new XYConstraints(5, 236, 396, 63));
    jPanel2.add(jBtn_OK, new XYConstraints(65, 16, 77, 26));
    jPanel2.add(jBtn_Cancel, new XYConstraints(235, 16, 77, 26));
    mainpanel.add(jPanel3, new XYConstraints(1, 2, 398, 67));
    jPanel3.add(tablesChoice, new XYConstraints(52, 19, 309, 25));
    mainpanel.add(jPanel4, new XYConstraints(3, 70, 395, 165));
    jPanel4.add(jLabel_Error, new XYConstraints(56, 136, 282, 24));
    jPanel4.add(jPanel1, new XYConstraints(108, 25, 173, 106));
    jPanel1.add(jLabel1, null);
    jPanel1.add(jText_StartValue, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jText_EndValue, null);

    for (int i=0; i<colorTab.LAST_TABLE_INDEX+1; i++)
      {
      tablesChoice.add(colorTab.TableNames[i]);
      }
    tablesChoice.select(colorTab.TableNames[selColorTab]);

    selColorTab = -1;
    // remains -1 if this dialog box is not exited with correctly chosen data;
    // needed for check in MenuFrame; overwritten if input data are OK    
  }

  void jBtn_OK_actionPerformed(ActionEvent e)
  {
    // Determine selected start and end value

    // Current text in text fields:
    String startValueStr = jText_StartValue.getText();
    String endValueStr   = jText_EndValue.getText();

    // Check for empty boxes
    if (startValueStr.length()<1 || endValueStr.length()<1)
    {
      jLabel_Error.setText("No empty boxes permitted!");
      return; // Edit box not filled yet
    }

    // Start or end value too long
    if (startValueStr.length()>7 || endValueStr.length()>7)
    {
      jLabel_Error.setText("Error (Start or End Value): too many characters!");
      return;
    }

    // Check if Float countained in input string
    try
    {
       startValue = Float.parseFloat(startValueStr);
       endValue   = Float.parseFloat(endValueStr);
       dispose();
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error (Start or End Value): Input not valid!");
      return;
    }

    // Determine selected color table
    String selColorTabStr = tablesChoice.getSelectedItem();
    for (int i=0; i<colorTab.LAST_TABLE_INDEX+1; i++)
    {
      if (colorTab.TableNames[i] == selColorTabStr)
         {
         selColorTab = i;
         break;
         }
    }

  }

  void jBtn_Cancel_actionPerformed(ActionEvent e)
  {
    dispose();
  }
  JPanel jPanel2 = new JPanel();
  XYLayout xYLayout3 = new XYLayout();
  GridLayout gridLayout1 = new GridLayout();
  JPanel jPanel3 = new JPanel();
  XYLayout xYLayout2 = new XYLayout();
  JPanel jPanel4 = new JPanel();
  XYLayout xYLayout4 = new XYLayout();

}

