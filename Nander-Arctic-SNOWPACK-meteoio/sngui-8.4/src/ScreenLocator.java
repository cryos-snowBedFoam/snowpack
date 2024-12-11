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
// ScreenLocator: Fits a container into the screen
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;

public class ScreenLocator
{
        private ScreenLocator(){}

        // returns the upper left corner of a container fitted centrally into the screen
        public static Point getCenterLocation (Dimension containerSize)
        {
                // Total size of the screen in pixel
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                if(containerSize.height>screenSize.height)
                  containerSize.height = screenSize.height;

                if(containerSize.width>screenSize.width)
                  containerSize.width = screenSize.width;

                return new Point((screenSize.width - containerSize.width) / 2,
                                 (screenSize.height - containerSize.height)/2);
         }
}
