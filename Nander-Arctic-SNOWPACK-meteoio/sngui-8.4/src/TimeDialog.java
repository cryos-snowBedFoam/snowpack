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
// TimeDialog: Dialog box for basic time settings
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.util.*;

public class TimeDialog extends JDialog
{
  JPanel panel1 = new JPanel();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  XYLayout xYLayout2 = new XYLayout();
  XYLayout xYLayout3 = new XYLayout();
  XYLayout xYLayout4 = new XYLayout();
  JButton jButton_OK = new JButton();
  JButton jButton_Cancel = new JButton();
  XYLayout xYLayout1 = new XYLayout();
  int daysBack;
  JLabel jLabel_Error = new JLabel("", JLabel.CENTER);
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  XYLayout xYLayout5 = new XYLayout();
  XYLayout xYLayout6 = new XYLayout();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  XYLayout xYLayout7 = new XYLayout();
  XYLayout xYLayout8 = new XYLayout();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JTextField jText_Year1 = new JTextField();
  JTextField jText_Month1 = new JTextField();
  JTextField jText_Day1 = new JTextField();
  JTextField jText_Hour1 = new JTextField();
  JTextField jText_Year2 = new JTextField();
  JTextField jText_Month2 = new JTextField();
  JTextField jText_Day2 = new JTextField();
  JTextField jText_Hour2 = new JTextField();

  GregorianCalendar StartTime = null; // remains null if this dialog box is not
              // exited with correct chosen data; needed for check in MenuFrame
  GregorianCalendar EndTime = null;
  GregorianCalendar defaultStartTime = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
  GregorianCalendar defaultEndTime = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));

  public TimeDialog(Frame frame, String title, boolean modal,
                    GregorianCalendar defaultStartTime, GregorianCalendar defaultEndTime)
  {
    super(frame, title, modal);
    this.defaultStartTime = defaultStartTime; // Default time in edit boxes
    this.defaultEndTime   = defaultEndTime;
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

  public TimeDialog()
  {
    this(null, "", false, null, null);
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(xYLayout1);
    jLabel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel1.setText("Chose the data display period:");
    jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel1.setLayout(xYLayout3);
    jPanel2.setLayout(xYLayout2);
    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel3.setLayout(xYLayout4);
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
    jLabel_Error.setForeground(Color.red);
    jLabel_Error.setBorder(BorderFactory.createRaisedBevelBorder());
    jLabel_Error.setText("");
    jPanel4.setLayout(xYLayout6);
    jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel5.setLayout(xYLayout5);
    jPanel5.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel7.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel7.setLayout(xYLayout7);
    jPanel6.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel6.setLayout(xYLayout8);
    jLabel4.setText("Year");
    jLabel5.setText("Month");
    jLabel6.setText("Day");
    jLabel7.setText("Hour");
    jLabel8.setText("Start");
    jLabel9.setText("End");

    // Set default start time, appearing in edit boxes
    String defaultYear1Str  = (new Integer(defaultStartTime.get(Calendar.YEAR))).toString();
    String defaultMonth1Str = (new Integer(defaultStartTime.get(Calendar.MONTH)+1)).toString();
    String defaultDay1Str   = (new Integer(defaultStartTime.get(Calendar.DAY_OF_MONTH))).toString();
    String defaultHour1Str  = (new Integer(defaultStartTime.get(Calendar.HOUR_OF_DAY))).toString();

    // Add half a day to the default end time (just one hour would also be possible).
    // Otherwise, last default data entry will not be displayed because minutes
    // cannot be entered in the edit boxes.
    defaultEndTime.add(Calendar.HOUR_OF_DAY, 12);

    // Set default end time, appearing in edit boxes
    String defaultYear2Str  = (new Integer(defaultEndTime.get(Calendar.YEAR))).toString();
    String defaultMonth2Str = (new Integer(defaultEndTime.get(Calendar.MONTH)+1)).toString();
    String defaultDay2Str   = (new Integer(defaultEndTime.get(Calendar.DAY_OF_MONTH))).toString();
    String defaultHour2Str  = (new Integer(defaultEndTime.get(Calendar.HOUR_OF_DAY))).toString();

    if (defaultMonth1Str.length()==1) defaultMonth1Str = "0"+ defaultMonth1Str;
    if (defaultDay1Str.length()==1) defaultDay1Str = "0"+ defaultDay1Str;
    if (defaultHour1Str.length()==1) defaultHour1Str = "0"+ defaultHour1Str;
    if (defaultMonth2Str.length()==1) defaultMonth2Str = "0"+ defaultMonth2Str;
    if (defaultDay2Str.length()==1) defaultDay2Str = "0"+ defaultDay2Str;
    if (defaultHour2Str.length()==1) defaultHour2Str = "0"+ defaultHour2Str;

    jText_Year1.setText("   "+defaultYear1Str);
    jText_Month1.setText("   "+defaultMonth1Str);
    jText_Day1.setText("   "+defaultDay1Str);
    jText_Hour1.setText("   "+defaultHour1Str);
    jText_Year2.setText("   "+defaultYear2Str);
    jText_Month2.setText("   "+defaultMonth2Str);
    jText_Day2.setText("   "+defaultDay2Str);
    jText_Hour2.setText("   "+defaultHour2Str);

    getContentPane().add(panel1);
    panel1.add(jPanel1, new XYConstraints(4, 0, 394, 92));
    jPanel1.add(jLabel1, new XYConstraints(114, 35, -1, -1));
    panel1.add(jPanel3, new XYConstraints(3, 91, 395, 159));
    jPanel3.add(jPanel4, new XYConstraints(33, 12, 330, 96));
    jPanel4.add(jPanel5, new XYConstraints(3, 3, 322, 25));
    jPanel5.add(jLabel3, new XYConstraints(9, 0, -1, -1));
    jPanel5.add(jLabel4, new XYConstraints(66, 2, 33, 17));
    jPanel5.add(jLabel5, new XYConstraints(129, 2, -1, -1));
    jPanel5.add(jLabel6, new XYConstraints(195, 2, 33, -1));
    jPanel5.add(jLabel7, new XYConstraints(261, 3, 33, -1));
    jPanel4.add(jPanel7, new XYConstraints(1, 62, 324, 29));
    jPanel7.add(jText_Year2, new XYConstraints(61, 1, 40, 21));
    jPanel7.add(jLabel9, new XYConstraints(10, 2, -1, -1));
    jPanel7.add(jText_Hour2, new XYConstraints(257, 1, 40, -1));
    jPanel7.add(jText_Day2, new XYConstraints(190, 1, 40, -1));
    jPanel7.add(jText_Month2, new XYConstraints(126, 1, 40, -1));
    jPanel4.add(jPanel6, new XYConstraints(2, 31, 323, 28));
    jPanel6.add(jLabel8, new XYConstraints(9, 2, -1, -1));
    jPanel6.add(jText_Year1, new XYConstraints(60, 1, 40, 21));
    jPanel6.add(jText_Month1, new XYConstraints(125, 1, 40, 21));
    jPanel6.add(jText_Day1, new XYConstraints(191, 1, 40, 21));
    jPanel6.add(jText_Hour1, new XYConstraints(256, 1, 40, 21));
    jPanel3.add(jLabel_Error, new XYConstraints(74, 120, 239, 22));
    panel1.add(jPanel2, new XYConstraints(3, 251, 395, 49));
    jPanel2.add(jButton_OK, new XYConstraints(73, 10, 73, -1));
    jPanel2.add(jButton_Cancel, new XYConstraints(233, 9, -1, -1));
  }

  void jButton_Cancel_actionPerformed(ActionEvent e)
  {
    dispose();
  }

  void jButton_OK_actionPerformed(ActionEvent e)
  {
    int Year1, Month1, Day1, Hour1;
    int Year2, Month2, Day2, Hour2;

    // Current text in textField
    String Year1Str  = jText_Year1.getText().trim(); // trim(): remove whitespace
    String Month1Str = jText_Month1.getText().trim();
    String Day1Str   = jText_Day1.getText().trim();
    String Hour1Str  = jText_Hour1.getText().trim();
    String Year2Str  = jText_Year2.getText().trim();
    String Month2Str = jText_Month2.getText().trim();
    String Day2Str   = jText_Day2.getText().trim();
    String Hour2Str  = jText_Hour2.getText().trim();

    // Check if Integer countained in input String
    try
    {
       Year1  = Integer.parseInt(Year1Str);
       Month1 = Integer.parseInt(Month1Str);
       Day1   = Integer.parseInt(Day1Str);
       Hour1  = Integer.parseInt(Hour1Str);
       Year2  = Integer.parseInt(Year2Str);
       Month2 = Integer.parseInt(Month2Str);
       Day2   = Integer.parseInt(Day2Str);
       Hour2  = Integer.parseInt(Hour2Str);
    }
    catch (NumberFormatException nfe)
    {
      jLabel_Error.setText("Error: Input not a valid number!");
      return;
    }

    // Check range of values
    if ( Day1<1    || Day1>31    ||  Day2<1   || Day2>31    ||
         Month1<1  || Month1>12  ||  Month2<1 || Month2>12  ||
         Year1<1   || Year1>9999 ||  Year2<1  || Year2>9999 ||
         Hour1<0   || Hour1>23   ||  Hour2<0  || Hour2>23   )
    {
      jLabel_Error.setText("Error: Input out of valid range!");
      return;
    }

    StartTime = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));
    EndTime = new GregorianCalendar(new SimpleTimeZone(3600000,"MEZ"));

    StartTime.set(Year1, Month1-1, Day1, Hour1, 0, 0); // MONTH = 0: January
    EndTime.set(Year2, Month2-1, Day2, Hour2, 0, 0);

    dispose();
  }
}

