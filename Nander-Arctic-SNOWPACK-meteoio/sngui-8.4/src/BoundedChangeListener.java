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
// BoundedChangeListener: Listens to changes in the slider position
//   Code mostly from Java Swing Book (Zukowski).
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import javax.swing.*;
import javax.swing.event.*;

public class BoundedChangeListener implements ChangeListener
{
       SnowPackDoc spDoc;

       public BoundedChangeListener(SnowPackDoc spDoc)
       {
         this.spDoc = spDoc;
       }

       public BoundedChangeListener()
       {
       }

       public void stateChanged(ChangeEvent changeEvent)
       {
         Object source = changeEvent.getSource();
         if (source instanceof BoundedRangeModel)
         // Changes in the data model. Not used here.
         {
           BoundedRangeModel aModel = (BoundedRangeModel)source;
           if (!aModel.getValueIsAdjusting())
           {
             System.out.println("Changed: " + aModel.getValue());
           }
         }
         else if (source instanceof JSlider)
         // Changes in the slider position. Can be caused by jSlider.setValue() or
         // by dragging the slider marker with the mouse. In the first case,
         // activeSliderMovement was set to false before executing setValue().
         {
           JSlider theJSlider = (JSlider)source;

           if (!theJSlider.getValueIsAdjusting())
           {
              // System.out.println("BoundedChangeListener: Slider changed: " + theJSlider.getValue());

              if (spDoc.activeSliderMovement)
              {
                spDoc.mFrame.MarkerMovement("Slider", theJSlider.getValue());
                //spDoc.spframe.SliderMovement(theJSlider.getValue());
                //jLabel1.setText("Slider changed by mouse dragging: " + theJSlider.getValue());
                return;
              }
              else
              { // System.out.println("    no active slider movement");
                return;
              }
           }
         }
         else
         {
           System.out.println("Something changed: " + source);
         }
       }
}