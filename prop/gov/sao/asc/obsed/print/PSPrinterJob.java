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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sun.java2d.PeekGraphics;
import sun.java2d.PeekMetrics;
import sun.java2d.RasterPrinterJob;

/******************************************************************************/
/** 
 * A class which initiates and executes a PostScript printer job.
 * Currently this class only rasterizes all of the imaging before it
 * is printed.
 */

public class PSPrinterJob extends RasterPrinterJob 
{

  /* Class Constants */
  public final static int PRINTER = 0;
  public final static int FILE = 1;

  /** 
   * Passed to the <code>setFillMode</code> method this value forces
   * fills to be done using the even-odd fill rule.
   */
  protected static final int FILL_EVEN_ODD = 1;
  
  /** 
   * Passed to the <code>setFillMode</code> method this value forces
   * fills to be done using the non-zero winding rule.
   */
  protected static final int FILL_WINDING = 2;
  
  // PostScript has a 64K maximum on its strings.
  private static final int MAX_PSSTR = (1024 * 64 - 1);
  
  private static final int RED_MASK = 0x00ff0000;
  private static final int GREEN_MASK = 0x0000ff00;
  private static final int BLUE_MASK = 0x000000ff;
  
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;
  private static final int BLUE_SHIFT = 0;

  private static final int LOWNIBBLE_MASK = 0x0000000f;
  private static final int HINIBBLE_MASK =  0x000000f0;
  private static final int HINIBBLE_SHIFT = 4;
  private static final byte hexDigits[] = 
  {
    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
    (byte)'8', (byte)'9', (byte)'A', (byte)'B',
    (byte)'C', (byte)'D', (byte)'E', (byte)'F'
  };

  private static final int PS_XRES = 75;
  private static final int PS_YRES = 75;
  
  private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
  private static final String EOF_COMMENT = "%%EOF";
  private static final String PAGE_COMMENT = "%%Page: ";
  
  private static final String READIMAGEPROC = ("/imStr 0 def /heximageSrc" +
                                               "{currentfile imStr" +
                                               " readhexstring pop} def");

  private static final String COPIES = "/#copies exch def";
  private static final String SHOWPAGE = "showpage";
  private static final String IMAGE_SAVE = "/imSave save def";
  private static final String IMAGE_STR = " string /imStr exch def";
  private static final String IMAGE_RESTORE = "imSave restore";
  
  private static final String COORD_PREP = (" 0 exch translate " +
                                            "1 -1 scale" +
                                            "[72 " + PS_XRES + " div " +
                                            "0 0 " +
                                            "72 " + PS_YRES + " div " +
                                            "0 0]concat");

  /**
   * The PostScript invocation to fill a path using the even-odd rule.
   */
  private static final String EVEN_ODD_FILL_STR = "eofill";
  
  /**
   * The PostScript invocation to fill a path using the non-zero
   * winding rule.
   */
  private static final String WINDING_FILL_STR = "fill";

  /**
   * The PostScript to set the clip to be the current path using the
   * even odd rule.
   */
  private static final String EVEN_ODD_CLIP_STR = "eoclip";

  /**
   * The PostScript to set the clip to be the current path using the
   * non-zero winding rule.
   */
  private static final String WINDING_CLIP_STR = "clip";

  /**
   * Expecting two numbers on the PostScript stack, this invocation
   * moves the current pen position.
   */
  private static final String MOVETO_STR = " moveto";
  /**
   * Expecting two numbers on the PostScript stack, this invocation
   * draws a PS line from the current pen position to the point on the
   * stack.
   */
  private static final String LINETO_STR = " lineto";

  /**
   * This PostScript operator takes two control points and an ending
   * point and using the current pen position as a starting point adds
   * a bezier curve to the current path.
   */
  private static final String CURVETO_STR = " curveto";

  /**
   * The PostScript to pop a state off of the printer's gstate stack.
   */
  private static final String GRESTORE_STR = "grestore";
  /**
   * The PostScript to push a state on to the printer's gstate stack.
   */
  private static final String GSAVE_STR = "gsave";
  
  /**
   * Make the current PostScript path an empty path.
   */
  private static final String NEWPATH_STR = "newpath";
  
  /**
   * Close the current subpath by generating a line segment from the
   * current position to the start of the subpath.
   */
  private static final String CLOSEPATH_STR = "closepath";
  
  /**
   * Use the three numbers on top of the PS operator stack to set the
   * rgb color.
   */
  private static final String SETRGBCOLOR_STR = " setrgbcolor";
  
  /**
   * Use the top number on the stack to set the printer's current gray
   * value.
   */
  private static final String SETGRAY_STR = " setgray";
  
  /****************************************************************************/
  /* Instance Variables */

  int destinationType;
  int orientation;
  String file;
  String printCommand;

  /**
   * The output stream to which the generated PostScript is written.
   */
  PrintStream printStream;

  /**
   * This string holds the PostScript operator to be used to fill a
   * path. It can be changed by the <code>setFillMode</code> method.
   */
  private String fillOpStr = WINDING_FILL_STR;
  
  /**
   * This string holds the PostScript operator to be used to clip to a
   * path. It can be changed by the <code>setFillMode</code> method.
   */
  private String clipOpStr = WINDING_CLIP_STR;
  
  /**
   * Keep track of the number of gstates we have placed on to the
   * printer's gstate stack. In later versions of this class we'll
   * actually track the contents of the gstates.
    */
  private int gStateCount;
  
  /**
   * The x coordinate of the current pen position.
   */
  private float penX;
  
  /**
   * The y coordinate of the current pen position.
   */
  private float penY;
  
  /**
   * The x coordinate of the starting point of the current subpath.
   */
  private float startPathX;
  
  /**
   * The y coordinate of the starting point of the current subpath.
   */
  private float startPathY;

  /****************************************************************************/

  public PSPrinterJob()
  {
  }

  /****************************************************************************/
  /**
   * Called to mark the start of a new path.
   */

  protected void beginPath() 
  {	
    printStream.println(NEWPATH_STR);

    penX = 0;
    penY = 0;
  }

  /****************************************************************************/
  /**
   * Add to the current path a bezier curve formed by the current pen
   * position and the method parameters which are two control points
   * and an ending point.
   */

  protected void bezierTo(float control1x, float control1y,
                          float control2x, float control2y,
                          float endX, float endY) 
  {
    printStream.println(control1x + " " + control1y +
                        " " + control2x + " " + control2y +
                        " " + endX + " " + endY +
                        CURVETO_STR);
	
    penX = endX;
    penY = endY;
  }

  /****************************************************************************/
  /**
   * Close the current subpath by appending a straight
   * line from the current point to the subpath's
   * starting point.
   */

  protected void closeSubpath() 
  {	
    printStream.println(CLOSEPATH_STR);

    penX = startPathX;
    penY = startPathY;
  }

  /****************************************************************************/
  /**
   * Examine the metrics captured by the <code>PeekGraphics</code>
   * instance and if capable of directly converting this print job to
   * PostScript, then return a <code>PSPathGraphics</code> to perform
   * that conversion. If there is not an object capable of the
   * conversion then return <code>null</code>. Returning
   * <code>null</code> causes the print job to be rasterized.
   */
    
  protected Graphics2D createPathGraphics(PeekGraphics peekGraphics,
                                          PrinterJob printerJob) 
  {
    PSPathGraphics pathGraphics;
    PeekMetrics metrics = peekGraphics.getMetrics();

    // If the application has drawn anything that out PathGraphics
    // class can not handle then return a null PathGraphics.
    if (metrics.hasNonSolidColors() ||
        metrics.hasCompositing() ||
        metrics.hasImages()) 
    {
      pathGraphics = null;
    }
    else 
    {
      BufferedImage bufferedImage = 
        new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);

      Graphics2D bufferedGraphics = bufferedImage.createGraphics();

      pathGraphics = new PSPathGraphics(bufferedGraphics, printerJob);
    }

    return(pathGraphics);
  }

  /****************************************************************************/
  /**
   * The passed in PageFormat will be altered to describe the default
   * page size and orientation of the PrinterJob's current printer.
   */

  public PageFormat defaultPage(PageFormat page) 
  {
    return(page);
  }
  
  /****************************************************************************/
  /**
   * Invoked by the RaterPrintJob super class this method is called
   * after that last page has been imaged.
   */

  protected void endDoc() throws PrinterException 
  {
    if (printStream != null) 
    {
      printStream.println(EOF_COMMENT);
      printStream.close();
    }
  }

  /****************************************************************************/
  /**
   * The RastePrintJob super class calls this method at the end of
   * each page.
   */

  protected void endPage(PageFormat format, Printable painter, int index)
    throws PrinterException
  {
    printStream.println(SHOWPAGE);
  }

  /****************************************************************************/
  /**
   * Fill the current path using the current fill mode and color.
   */

  protected void fillPath() 
  {
    printStream.println(fillOpStr);
  }

  /****************************************************************************/
  /**
   * Returns how many times each page in the book should be
   * consecutively printed by PrintJob.  If the printer makes copies
   * itself then this method should return 1.
   */

  protected int getNoncollatedCopies() 
  {
    return(1);
  }

  /****************************************************************************/    
  /**
   * Return the x coordinate of the pen in the current path.
   */

  protected float getPenX() 
  {	
    return(penX);
  }

  /****************************************************************************/
  /**
   * Return the y coordinate of the pen in the current path.
   */

  protected float getPenY() 
  {
    return(penY);
  }

  /****************************************************************************/
  /**
   * Return the x resolution of the coordinates to be rendered.
   */

  protected double getXRes() 
  {
    return(PS_XRES);
  }

  /****************************************************************************/
  /**
   * Return the y resolution of the coordinates to be rendered.
   */

  protected double getYRes() 
  {
    return(PS_YRES);
  }

  /****************************************************************************/
  /**
   * Push the current gstate on to the printer's gstate stack.
   */

  protected void gsave() 
  {
    printStream.println(GSAVE_STR);
    ++gStateCount;
  }

  /****************************************************************************/
  /**
   * Pop the previous gsave off of the PostScript gstate stack. If we
   * did not generate a gsave prior to this call then this method does
   * nothing.
   */

  protected void grestore() 
  {	
    if (gStateCount > 0) 
    {
      printStream.println(GRESTORE_STR);
      --gStateCount;
    }
  }

  /****************************************************************************/
  /**
   * Generate PostScript to draw a line from the current pen position
   * to <code>(x, y)</code>.
   */

  protected void lineTo(float x, float y) 
  {
    printStream.println(x + " " + y + LINETO_STR);

    penX = x;
    penY = y;
  }

  /****************************************************************************/
  /**
   * Generate PostScript to move the current pen position to <code>(x,
   * y)</code>.
   */

  protected void moveTo(float x, float y) 
  {
    printStream.println(x + " " + y + MOVETO_STR);

    /* moveto marks the start of a new subpath and we need to remember
     * that starting position so that we know where the pen returns to
     * with a close path.
     */
    startPathX = x;
    startPathY = y;

    penX = x;
    penY = y;
  }

  /****************************************************************************/
  /**
   * See if 'destFile' exists and if it does confirm with the user
   * that the file should be replaced. Return true if the file is okay
   * to write to and false otherwise.
   */

  private boolean okayToCreateFile(JFrame parentFrame, String filename) 
  {
    boolean createFile = true;
    File destFile = new File(filename);

    if ( destFile.exists() )
    {
      int value = 
        JOptionPane.showConfirmDialog(parentFrame,
                                      "File exists: " + filename + 
                                      ".\nOverwrite it?",
                                      "Print Warning",
                                      JOptionPane.OK_CANCEL_OPTION,
                                      JOptionPane.WARNING_MESSAGE);

      if (value == JOptionPane.NO_OPTION) 
      {
        createFile = false;
      }
    }

    return(createFile);
  }

  /****************************************************************************/
  /**
   * Display a dialog to the user allowing the modification of a
   * PageFormat instance.  The <code>page</code> argument is used to
   * initialize controls in the page setup dialog.  If the user
   * cancels the dialog, then the method returns the original
   * <code>page</code> object unmodified.  If the user okays the
   * dialog then the method returns a new PageFormat object with the
   * indicated changes.  In either case the original <code>page</code>
   * object will not be modified.
   *
   * @param     page    the default PageFormat presented to the user
   *                    for modification
   * @return    the original <code>page</code> object if the dialog
   *            is cancelled, or a new PageFormat object containing
   *            the format indicated by the user if the dialog is
   *            acknowledged
   * @since     JDK1.2
   */

  public PageFormat pageDialog(PageFormat page) 
  {
    PageFormat result;

    PageDialog pageDialog = new PageDialog(new JFrame(), "Print Setup");

    pageDialog.setPageFormat(page);

    pageDialog.show();

    result = pageDialog.getPageFormat();

    return(result);
  }
  
  /****************************************************************************/
  /**
   * Prints the contents of the array of ints, 'data' to the current
   * page. The band is placed at the location (x, y) in device
   * coordinates on the page. The width and height of the band is
   * specified by the caller. Currently the data is 32 bits per pixel
   * in XRGB format.
   */

  protected void printBand(int[] data, int x, int y,
                           int width, int height)
    throws PrinterException    
  {
    printStream.println(IMAGE_SAVE);

    // Create a PS string big enought to hold a row of pixels.
    int psBytesPerRow = 3 * width;

    while (psBytesPerRow > MAX_PSSTR) 
    {
      psBytesPerRow /= 2;
    }

    printStream.println(psBytesPerRow + IMAGE_STR);

    // Scale and translate the unit image.
    printStream.println("[" + width + " 0 " +
                        "0 " + height +
                        " " + x + " " + y +
                        "]concat");

    // Color Image invocation.
    printStream.println(width + " " + height + " " + 8 + "[" +
                        width + " 0 " +
                        "0 " + -height +
                        " 0 " + height + "]" +
                        "/heximageSrc load false 3 colorimage");

    // Image data.
    int index = 0;
    byte[] psData = new byte[width * 6];

    try 
    {
      for(int i = 0; i < height; i++) 
      {
        index = toHex(data, index, psData);
        printStream.write(psData);
        printStream.println("");
      }

      /*
       * If there is an IOError we subvert it to a PrinterException.
       * Fix: There has got to be a better way, maybe define a
       * PrinterIOException and then throw that?
       */
    } catch (IOException e) 
    {
      throw new PrinterException(e.toString());
    }

    printStream.println(IMAGE_RESTORE);
  }

  /****************************************************************************/
  /**
   * Presents the user a dialog for changing properties of the print
   * job interactively.
   *
   * @returns false if the user cancels the dialog and true otherwise.
   */

  public boolean printDialog() 
  {
    // A security check has already been performed in the
    // java.awt.print.printerJob.getPrinterJob method.  So by the time
    // we get here, it is OK for the current thread to print either to
    // a file (from a Dialog we control!) or to a chosen printer.
   
    // We raise privilege when we put up the dialog, to avoid
    // the "warning applet window" banner.
    PrivilegedAction privilegedAction = 
      new PrivilegedAction() 
    {
      public Object run() 
      {
        Boolean result;
        
        JFrame frame = new JFrame();

        PrintDialog dialog = new PrintDialog(frame, "Print");
        
        dialog.show();
        
        /* If the user hit print then we want to gather the print
         * dialog settings.
         */
        if (dialog.getPrintStatus() != dialog.PRINT) 
        {
          result = Boolean.FALSE;
        }
        else
        {
          destinationType = dialog.getDestinationType();

          if (destinationType == PRINTER)
          {
            printCommand = dialog.getPrintCommand();
          }
          else
          {
            file = dialog.getFile();
          }
          
          setCollated(false);
          
          if (destinationType == FILE) 
          {
            return new Boolean( okayToCreateFile(frame, file) );
          } 
          else 
          {
            result = Boolean.TRUE;
          }
        }

        return(result);
      }
    };

    Boolean doPrint =
      (Boolean) java.security.AccessController.doPrivileged(privilegedAction);
    
    return( doPrint.booleanValue() );
  }
  
  /****************************************************************************/
  /**
   * Intersect the gstate's current path with the current clip and
   * make the result the new clip.
   */

  protected void selectClipPath() 
  {
    printStream.println(clipOpStr);
  }

  /****************************************************************************/

  public void setCollated(boolean collate) 
  {
    super.setCollated(collate);
  }
  
  /****************************************************************************/
  /**
   * Set the printer's current color to be that defined by <code>color</color>.
   */

  protected void setColor(Color color) 
  {
    float[] rgb = color.getColorComponents(null);

    // If the color is a gray value then use setgray.
    if (rgb[0] == rgb[1] && rgb[1] == rgb[2]) 
    {
      printStream.println(rgb[0] + SETGRAY_STR);
      
      // It's not gray so use setrgbcolor.
    } 
    else 
    {
      printStream.println(rgb[0] + " " + rgb[1] + " " + rgb[2] + " " +
                          SETRGBCOLOR_STR);
    }
  }

  /****************************************************************************/
  /**
   * Set the current path rule to be either
   * <code>FILL_EVEN_ODD</code> (using the
   * even-odd file rule) or <code>FILL_WINDING</code>
   * (using the non-zero winding rule.)
   */

  protected void setFillMode(int fillRule) 
  {
    switch (fillRule) 
    {
    case FILL_EVEN_ODD:
      {
        fillOpStr = EVEN_ODD_FILL_STR;
        clipOpStr = EVEN_ODD_CLIP_STR;
        break;
      }
    case FILL_WINDING:
      {
        fillOpStr = WINDING_FILL_STR;
        clipOpStr = WINDING_CLIP_STR;
        break;
      }
    default:
      {
        throw new IllegalArgumentException();
      }
    }
  }

  /****************************************************************************/
  /**
   * Invoked by the RasterPrinterJob super class this method is called
   * to mark the start of a document.
   */

  protected void startDoc() throws PrinterException 
  {   
    // A security check has been performed in the
    // java.awt.print.printerJob.getPrinterJob method.  We use an
    // inner class to execute the privilged open operations.  Note
    // that we only open a file if it has been nominated by the
    // end-user in a dialog that we ouselves put up.
    
    PrivilegedExceptionAction privilegedAction = 
      new PrivilegedExceptionAction()
    {
      public Object run() throws PrinterException
      {
        OutputStream outputStream;

        try 
        {
          if (destinationType == PRINTER) 
          {
            Process process = Runtime.getRuntime().exec(printCommand);
            
            outputStream = process.getOutputStream();
          }
          else 
          {
            File destFile = new File(file);
            
            outputStream = new FileOutputStream(destFile);
          }

          printStream = new PrintStream( new BufferedOutputStream(outputStream) );
        }
        catch (IOException ioException) 
        {
          // If there is an IOError we subvert it to a PrinterException.
          throw( new PrinterException( ioException.toString() ) );
        }
        
        return null;
      }
    };
    
    try
    {
      java.security.AccessController.doPrivileged(privilegedAction);
    }
    catch(PrivilegedActionException privilegedActionException)
    {
      throw( (PrinterException) privilegedActionException.getException() );
    }

    printStream.println(ADOBE_PS_STR);
    printStream.println(READIMAGEPROC);
    
    // PostScript printers can always gnerate uncollated copies.
    int copies = isCollated() ? 1 : getCopies();

    printStream.println(copies + COPIES);
  }
  
  /****************************************************************************/
  /**
   * The RastePrintJob super class calls this method at the start of
   * each page.
   */

  protected void startPage(PageFormat pageFormat, Printable painter, int index)
    throws PrinterException
  {	    
    double paperHeight = pageFormat.getPaper().getHeight();
    int pageNumber = index + 1;

    // We should use the name page here but first we need to implement
    // a routine that quotes PS strings correctly.
    printStream.println(PAGE_COMMENT + pageNumber + " " + pageNumber);

    printStream.println(paperHeight + COORD_PREP);
  }

  /****************************************************************************/
  /**
   * Take the 32 byte xRGB values out of 'image' and turn them into
   * hexified RGB values in 'dest'. 'index' specified where in 'image'
   * to start reading data. The caller must make sure that dest.length
   * is a multiple of 6 (2 bytes per R, G, and B) and that
   * image.length is a multiple of length / 6 so that dest is
   * completely filled each time this routine returns.
   */

  private static int toHex(int[] image, int index, byte[] dest) 
  {
    int value;
    int component;
    int destIndex = 0;

    while(index < image.length && destIndex < dest.length) 
    {
      value = image[index++];

      // Red
      component = (value & RED_MASK) >>> RED_SHIFT;
      dest[destIndex++] = hexDigits[(component & HINIBBLE_MASK)
                                   >>> HINIBBLE_SHIFT];
      dest[destIndex++] = hexDigits[component & LOWNIBBLE_MASK];

      // Green
      component = (value & GREEN_MASK) >>> GREEN_SHIFT;
      dest[destIndex++] = hexDigits[(component & HINIBBLE_MASK)
                                   >>> HINIBBLE_SHIFT];
      dest[destIndex++] = hexDigits[component & LOWNIBBLE_MASK];

      // Blue
      component = (value & BLUE_MASK) >>> BLUE_SHIFT;	    
      dest[destIndex++] = hexDigits[(component & HINIBBLE_MASK)
                                   >>> HINIBBLE_SHIFT];
      dest[destIndex++] = hexDigits[component & LOWNIBBLE_MASK];
    }

    return(index);
  }

  /****************************************************************************/
  /**
   * The passed in PageFormat is altered to be usable on the
   * PrinterJob's current printer.
   */

  public PageFormat validatePage(PageFormat page) 
  {
    return(page);
  }
  
  /****************************************************************************/

}

/******************************************************************************/
