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
// GraphicsPrinter: basic functions needed for printing
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;
import java.awt.print.*;
import java.io.*;


public class GraphicsPrinter implements Printable
{

  PrinterJob pjob;
  PageFormat pageformat;

  public GraphicsPrinter()
  {
    this.pjob = PrinterJob.getPrinterJob();
  }

  public boolean setupPageFormat()
  {
    PageFormat defaultPF = pjob.defaultPage();
    pageformat = pjob.pageDialog(defaultPF);
    pjob.setPrintable(this, pageformat); // ?? print in dieser Klasse
    return (pageformat != defaultPF);
  }

  public boolean setupJobOptions()
  {
    return pjob.printDialog();
  }

  public void printGraphics() throws PrinterException, IOException
  {
    pjob.print();
  }

  // Implementation of Printable
  public int print(Graphics g, PageFormat pf, int page) throws PrinterException
  {
     int ypos = (int) pf.getImageableY();
     int xpos = (int) pf.getImageableX();
     g.drawString("Druck", xpos, ypos);

     return PAGE_EXISTS; // or NO_SUCH_PAGE
  }

                                      
}