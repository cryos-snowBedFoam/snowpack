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
// GrainForm: Implementation of functions to determine
//            a color for snow grain form
///////////////////////////////////////////////////////////////////////////////


package ProWin;

import java.awt.*;


public class GrainForm
{
        // Definition of colours for various grain forms

        // 0 Graupel  (momentan nicht benutzt = grau/grey)
        static Color GRAIN_COLOR_0 = new Color(128,128,128);

        // 1 Neuschnee/fresh snow (+), hellgr\uFFFDn/bright green
        static Color GRAIN_COLOR_1 = new Color(  0,255,  0);

        // 2 filzig/decomposing and fragmented p.particles(/), dunkelgruen/dark green
        static Color GRAIN_COLOR_2 = new Color( 34 ,139,  34);

        // 3 Rundkoernig/rounded grains (*),  hellrosa/bright pink
        static Color GRAIN_COLOR_3 = new Color(255,182,193);

        // 4 Kantigkoernig/faceted crystals ([]), hellblau/bright blue
        static Color GRAIN_COLOR_4 = new Color(173,216,230);

        // 5 Schwimmschnee/cup-shaped, depth hoar (/\), blau/blue
        static Color GRAIN_COLOR_5 = new Color(  0,  0,255);

        // 6 Oberflaechenreif/surface hoar (\/), magenta
        static Color GRAIN_COLOR_6 = new Color(255,  0,255);

        // 7 Schmelzformen/wet grains    (o),  rot/red
        static Color GRAIN_COLOR_7 = new Color(255,  0,  0);

        // 8 Eislammelle/ice masses      (-),  cyan
        static Color GRAIN_COLOR_8 = new Color(  0,255,255);

        // 9 Mischformen/mixed forms      hellblau/bright blue
        static Color GRAIN_COLOR_9 = new Color(192,192,255);

        // color returned in the case of error
        static Color ERROR_COLOR   = new Color(255,255,255);


        static Color GrainColors[] = {
           GRAIN_COLOR_0, GRAIN_COLOR_1, GRAIN_COLOR_2, GRAIN_COLOR_3,
           GRAIN_COLOR_4, GRAIN_COLOR_5, GRAIN_COLOR_6, GRAIN_COLOR_7,
           GRAIN_COLOR_8, GRAIN_COLOR_9 };

        // no specific constructor




        /******************************************************************************
        **
        **  METHODE:      GetColor
        **
        **  BESCHREIBUNG  Ermitteln der Farbe zu einer Schneekornform.
        **                Colors are assigned to grain forms.
        **
        **  PARAMETER:    GrainFormCode  wird als 3-stellige Nummer uebergeben:
        **                GrainFromCode is passed over as a three-digit number:
        **                     F1 F2 F3 (e.g. 120)
        **                F1    Kornform 1 (Wert = 1..8 laut Beobachterhandbuch)
        **                      value of first grain form
        **                F2    Kornform 2 (Wert = 1..8 laut Beobachterhandbuch)
        **                      value of second grain form
        **                F3=0: Kornform 1 und 2 ca. gleichhauefig
        **                      same frequency of graom forms 1 and 2
        **                F3=1: Kornform 1 ist hauefiger als Kornform 2
        **                      Seltenere Form wird im Schneeprofil in Klammern angegeben
        **                      grain form one is more frequent than grain form two
        **                F3=2: Geschmolzene und wiedergefrorenen Kornformen
        **                      grains melted and refrozen
        **
        **  RETURNWERT:   Farbe zu Kornform
        **
        **  AKTUELLE VER: V1.0  08.10.98  MS
        **
        ******************************************************************************/
        public static Color GetColor(int GrainFormCode)
        {
           int F1, F2, F3;
           float Mult;
           int RValue, GValue, BValue;

           F1 = GrainFormCode / 100;
           F2 = (GrainFormCode - (F1 * 100)) / 10;
           F3 = GrainFormCode % 10;

           // System.out.println("Grain form, GetColor(): F1,F2,F3= "+F1+F2+F3);

           if ( F1 < 1 || F1 > 9 ||    /* grain form 1 */
                F2 < 1 || F2 > 9 ||    /* grain form 2 */
                F3 < 0 || F3 > 2 )     /* out of valid range */
           {
              /* Currently grain forms 1-9 are permitted */
              return ERROR_COLOR;
           }

           if ( F1 == F2 )
           {
              /* No calculation of mixed colors necessary */
              return GrainColors[F1];
           }

           if ( F3 == 0 )
           {
              Mult = 50;  /* Color mixture F1:F2 = 50:50% */
           }
           else if ( F3 == 1 )
           {
              Mult = 80;  /* Color mixture F1:F2 = 80:20% */
           }
           else
           {
              // Melt/freeze-forms ( F3 == 2 ): treatment as if F3 = 0;
              // (but in left side graph cyan lines are added by another method)
              Mult = 50;
           }


           /* Calculate mixed color between F1 and F2 */

           /* Red = StartRed + Mult/100.0 * (EndRed - StartRed) */
           RValue = GrainColors[F2].getRed(); /* StartRed */
           RValue = (int) (RValue + Mult / 100.0 * (GrainColors[F1].getRed() - RValue));

           GValue = GrainColors[F2].getGreen(); /* StartGreen */
           GValue = (int) (GValue + Mult / 100.0 * (GrainColors[F1].getGreen() - GValue));

           BValue = GrainColors[F2].getBlue(); /* StartBlue */
           BValue = (int) (BValue + Mult / 100.0 * (GrainColors[F1].getBlue() - BValue));

           return new Color(RValue, GValue, BValue);
        }



static void DrawSymbol(Graphics g, int GrainFormCode,
                Point StartPoint, Point EndPoint)
// First a white rectangle is drawn, defined by StartPoint (upper left) and
// EndPoint (lower right). This rectangle is devided horizontally into two equal
// boxes. In each of these boxes one grain form symbol is drawn, defined by the
// first (second) number in the three-digit grain form code.
// The third digit is supposed to be zero (meaning the two grain forms are of
// the same frequency) or two (grains melted and refrozen). Number one is not
// produced by the snowpack model.
{
    // Three digits of GrainFormCode
    int Code[] = new int[3];
    Code[0] =  GrainFormCode / 100;
    Code[1] = (GrainFormCode - 100 * Code[0]) / 10;
      Code[2] = GrainFormCode - 100 * Code[0] - 10 * Code[1];

    // Distance between symbol and border of box
    int gap;
    if (Code[2] == 0)
      gap = (int) ((EndPoint.y - StartPoint.y) * 0.2);
    else
      // symbol smaller since additional circle around it is drawn
      gap = (int) ((EndPoint.y - StartPoint.y) * 0.25);

    // Height and width of box surrounding a symbol
    int BoxHeight = EndPoint.y - StartPoint.y;
    int BoxWidth = (EndPoint.x - StartPoint.x) / 2;

    // Draw white rectangle (symbols are overlaid below)
    g.setColor(Color.white);
    g.fillRect(StartPoint.x, StartPoint.y,
               EndPoint.x - StartPoint.x, EndPoint.y - StartPoint.y);

    g.setColor(Color.black);

    for (int i = 0; i < 2; i++) // loop over first and second digit
    {
      //schirmer
      //draw only first symbol if only one graintype available (Code[1] == (Code[])
      //except for crusts
      if (i == 1 && Code[i] == Code[0] && Code[2] != 2){
        break;
      }

       if (Code[i]==1)
       // draw +
       {
         g.drawLine(StartPoint.x + gap + i * BoxWidth,
                    StartPoint.y + BoxHeight / 2,
                    StartPoint.x - gap + (i + 1) * BoxWidth,
                    StartPoint.y + BoxHeight / 2);        // horizontal line
         g.drawLine(StartPoint.x + BoxWidth / 2 + i * BoxWidth,
                    StartPoint.y + gap,
                    StartPoint.x + BoxWidth / 2 + i * BoxWidth,
                    EndPoint.y - gap);                   // vertical line
       }

       else if (Code[i]==2)
       // draw /
         g.drawLine(StartPoint.x - gap + (i + 1) * BoxWidth,
                    StartPoint.y + gap,
                    StartPoint.x + gap + i * BoxWidth,
                    EndPoint.y - gap);

       else if (Code[i]==3)
       // draw filled circle
         g.fillOval(StartPoint.x + gap + i * BoxWidth,
                    StartPoint.y + gap,
                    BoxWidth - 2 * gap,
                    BoxHeight - 2 * gap);

       else if (Code[i]==4)
       // draw empty square (the only symbol not contained in the ASCII-code)
         g.drawRect(StartPoint.x + gap  + i * BoxWidth,
                    StartPoint.y + gap,
                    BoxWidth - 2 * gap,
                    BoxHeight - 2 * gap);

       else if (Code[i]==5)
       // draw ^
       {
         g.drawLine(StartPoint.x + BoxWidth / 2  + i * BoxWidth,
                    StartPoint.y + gap,
                    StartPoint.x + gap + i * BoxWidth,
                    EndPoint.y - gap); // line from top center to lower left
         g.drawLine(StartPoint.x + BoxWidth / 2 + i * BoxWidth,
                    StartPoint.y + gap,
                    StartPoint.x - gap + (i + 1) * BoxWidth,
                    EndPoint.y - gap); // line from top center to lower right
       }

       else if (Code[i]==6)
       // draw ^ (turned over)
       {
         g.drawLine(StartPoint.x + BoxWidth / 2 + i * BoxWidth,
                    EndPoint.y - gap,
                    StartPoint.x + gap + i * BoxWidth,
                    StartPoint.y + gap); // line from bottom center to upper left
         g.drawLine(StartPoint.x + BoxWidth / 2 + i * BoxWidth,
                    EndPoint.y - gap,
                    StartPoint.x - gap  + (i + 1) * BoxWidth,
                    StartPoint.y + gap); // line from bottom center to upper right
       }

       else if (Code[i]==7)
       // draw empty circle
         g.drawOval(StartPoint.x + gap + i * BoxWidth,
                    StartPoint.y + gap,
                    BoxWidth - 2 * gap,
                    BoxHeight - 2 * gap);

       else if (Code[i]==8)
       // draw -
         g.drawLine(StartPoint.x + i * BoxWidth,
                    StartPoint.y + BoxHeight / 2,
                    StartPoint.x + (i + 1) * BoxWidth,
                    StartPoint.y + BoxHeight / 2);           // horizontal line

       else if (Code[i]==9)
       // draw half empty square topped by half empty circle
       {
         g.drawLine(StartPoint.x + gap + i * BoxWidth,
                    EndPoint.y - gap,
                    StartPoint.x - gap + (i + 1) * BoxWidth,
                    EndPoint.y - gap);                   // base horizontal line
         g.drawLine(StartPoint.x + gap + i * BoxWidth,
                    StartPoint.y + BoxHeight / 2,
                    StartPoint.x + gap + i * BoxWidth,
                    EndPoint.y - gap);                   // left vertical line
         g.drawLine(StartPoint.x - gap + (i + 1) * BoxWidth,
                    StartPoint.y + BoxHeight / 2,
                    StartPoint.x - gap + (i + 1) * BoxWidth,
                    EndPoint.y - gap);                   // right vertical line
         g.drawArc (StartPoint.x + gap  + i * BoxWidth,
                    StartPoint.y + gap,
                    BoxWidth - 2 * gap,
                    BoxHeight - 2 * gap,
                    0, 180); // start and sweep angle    // half circle
       }

       // Grains melted and refrozen: draw circle around symbols
       if (Code[2] == 2) {

         // draw empty (big) circle
         g.drawOval(StartPoint.x, StartPoint.y, BoxWidth, BoxHeight);
         g.drawOval(StartPoint.x + BoxWidth, StartPoint.y, BoxWidth, BoxHeight);
       }

      //schirmer
     // brackets
       else if (i == 1){
         g.drawArc(StartPoint.x + (int) (1.5 * BoxWidth), StartPoint.y,
                   6, BoxHeight, 60, -120);                                 //  left
         g.drawArc(StartPoint.x + (int) (1 * BoxWidth), StartPoint.y,
                   6, BoxHeight, 240, -120);                                  //  right
       }//end schirmer

    }

}


}

/*
// Test of GrainForm ---------------------------------------------
                GrainForm gf = new GrainForm();
                Color Col2 = new Color(0,0,0);
                int GrainFormCode = 912;
                Col2 = gf.GetColor(GrainFormCode);
                System.out.println("GrainForm test:");
                System.out.println("Red:" + Col2.getRed());
                System.out.println("Green: " + Col2.getGreen());
                System.out.println("Blue: " + Col2.getBlue());
*/
