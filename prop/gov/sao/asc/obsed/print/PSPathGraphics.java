/*
      Copyrights:
 
      Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
      Permission to use, copy, modify, distribute, and  sell  this
      software  and  its  documentation  for any purpose is hereby
      granted without  fee,  provided  that  the  above  copyright
      notice  appear  in  all  copies and that both that copyright
      notice and this permission notice appear in supporting docu-
      mentation,  and  that  the  name  of the  Smithsonian Astro-
      physical Observatory not be used in advertising or publicity
      pertaining to distribution of the software without specific,
      written  prior  permission.   The Smithsonian  Astrophysical
      Observatory makes no representations about  the  suitability
      of  this  software for any purpose.  It is provided  "as is"
      without express or implied warranty.
      THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
      PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
      THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
      ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
      INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
      BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
      OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
      OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
      OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
      THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/
/******************************************************************************/

package gov.sao.asc.obsed.print;

/******************************************************************************/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.PathIterator;
import java.awt.print.PrinterJob;

import sun.java2d.PathGraphics;

/******************************************************************************/
/**
 * This class converts paths into PostScript by breaking all graphics
 * into fills and clips of paths.
 */

class PSPathGraphics extends PathGraphics 
{

  /****************************************************************************/

  public PSPathGraphics(Graphics2D graphics, PrinterJob printerJob) 
  {
    super(graphics, printerJob);
  }

  /****************************************************************************/
  /**
   * Creates a new <code>Graphics</code> object that is a copy of this
   * <code>Graphics</code> object.
   *
   * @return     a new graphics context that is a copy of 
   *                       this graphics context.
   * @since      JDK1.0
   */

  public Graphics create() 
  {
    return new PSPathGraphics((Graphics2D) getDelegate().create(),
                              getPrinterJob());
  }

  /****************************************************************************/
  /*
   * Fill the path defined by <code>pathIter</code> with the specified
   * color.  The path is provided in device coordinates.
   */

  protected void deviceFill(PathIterator pathIter, Color color) 
  {
    PSPrinterJob psPrinterJob = (PSPrinterJob) getPrinterJob();

    convertToPSPath(pathIter);
    psPrinterJob.setColor(color);
    psPrinterJob.fillPath();
  }

  /****************************************************************************/
  /*
   * Set the printer device's clip to be the path defined by
   * <code>pathIter</code> The path is provided in device coordinates.
   */

  protected void deviceClip(PathIterator pathIter) 
  {
    PSPrinterJob psPrinterJob = (PSPrinterJob) getPrinterJob();

    /* If a gsave is in effect then we want to
     * pop it so that the previous clip has no
     * effect on the clip we are about to set.
     * Priot to setting our new clip do a gsave
     * so that we can get rid of the clip later.
     */
    psPrinterJob.grestore();
    psPrinterJob.gsave();

    convertToPSPath(pathIter);
    psPrinterJob.selectClipPath();
  }

  /****************************************************************************/
  /**
   * Given a Java2D <code>PathIterator</code> instance, this method
   * translates that into a PostScript path..
   */

  private void convertToPSPath(PathIterator pathIter) 
  {
    float[] segment = new float[6];
    int segmentType;

    PSPrinterJob psPrinterJob = (PSPrinterJob) getPrinterJob();

    /* Map the PathIterator's fill rule into the PostScript
     * fill rule.
     */
    int fillRule;

    if (pathIter.getWindingRule() == PathIterator.WIND_EVEN_ODD) 
    {
      fillRule = PSPrinterJob.FILL_EVEN_ODD;
    } 
    else 
    {
      fillRule = PSPrinterJob.FILL_WINDING;
    }

    psPrinterJob.setFillMode(fillRule);

    psPrinterJob.beginPath();

    while (pathIter.isDone() == false) 
    {
      segmentType = pathIter.currentSegment(segment);

      switch (segmentType) 
      {
      case PathIterator.SEG_MOVETO:
        {
          psPrinterJob.moveTo(segment[0], segment[1]);
          break;
        }
      case PathIterator.SEG_LINETO:
        {
          psPrinterJob.lineTo(segment[0], segment[1]);
          break;
        }
      case PathIterator.SEG_QUADTO:
        {
          // Convert the quad path to a bezier.

          float lastX = psPrinterJob.getPenX();
          float lastY = psPrinterJob.getPenY();
          float c1x = lastX + (segment[0] - lastX) * 2 / 3;
          float c1y = lastY + (segment[1] - lastY) * 2 / 3;
          float c2x = segment[2] - (segment[2] - segment[0]) * 2/ 3;
          float c2y = segment[3] - (segment[3] - segment[1]) * 2/ 3;

          psPrinterJob.bezierTo(c1x, c1y,
                                c2x, c2y,
                                segment[2], segment[3]);
          break;
        }
      case PathIterator.SEG_CUBICTO:
        {
          psPrinterJob.bezierTo(segment[0], segment[1],
                                segment[2], segment[3],
                                segment[4], segment[5]);
          break;
        }
      case PathIterator.SEG_CLOSE:
        {
          psPrinterJob.closeSubpath();
          break;
        }
      }

      pathIter.next();
    }

  }

  /****************************************************************************/

}

/******************************************************************************/
