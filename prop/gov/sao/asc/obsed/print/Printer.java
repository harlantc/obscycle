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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.security.AccessController;
import java.security.PrivilegedAction;

/******************************************************************************/

public class Printer implements Printable
{
  double paperHeight;
  double paperWidth;
  int columns;
  Book book;
  Component component;
  Dimension size;
  PageFormat pageFormat;
  PrinterJob printerJob;

  /****************************************************************************/

  public Printer()
  {
    System.setProperty("java.awt.printerjob",
                       "gov.sao.asc.obsed.print.PSPrinterJob");

    book = new Book();

    //printerJob = PrinterJob.getPrinterJob();
    printerJob = getPrinterJob();

    pageFormat = printerJob.defaultPage();
  }

  /****************************************************************************/

  public static PrinterJob getPrinterJob() 
  {
    SecurityManager security = System.getSecurityManager();

    if (security != null) 
    {
      security.checkPrintJobAccess();
    }

    return (PrinterJob) java.security.AccessController.doPrivileged(
	    new java.security.PrivilegedAction() 
            {
              public Object run() 
                {
                  String nm = System.getProperty("java.awt.printerjob", null);

                  try 
                  {
		    return (PrinterJob)Class.forName(nm).newInstance();
                  } 
                  catch (ClassNotFoundException e) 
                  {
		    throw new java.awt.AWTError("PrinterJob not found: " + nm);
                  } 
                  catch (InstantiationException e) 
                  {
                    throw new java.awt.AWTError("Could not instantiate PrinterJob: " + nm);
                  }
                  catch (IllegalAccessException e) 
                  {
		    throw new java.awt.AWTError("Could not access PrinterJob: " + nm);
                  }
                }
            });
  }

  /****************************************************************************/

  public void print(Component component)
  {
    this.component = component;

    pageFormat = printerJob.pageDialog(pageFormat);

    // Set quarter inch margins.
    Paper paper = pageFormat.getPaper();

    paperWidth = paper.getWidth() - 36.0;
    paperHeight = paper.getHeight() - 36.0;
    
    paper.setImageableArea(18.0, 18.0, paperWidth, paperHeight);

    pageFormat.setPaper(paper);

    size = component.getSize();

    for (int i = 0; i < size.getHeight(); i += paperHeight)
      {
        for (int j = 0; j < size.getWidth(); j += paperWidth)
          {
            book.append(this, pageFormat);
          }
      }
    
    columns = (int) Math.ceil(size.getWidth() / paperWidth);

    printerJob.setPageable(book);

    if ( printerJob.printDialog() ) 
      {
	try 
	  {
	    printerJob.print();  
	  }
	catch (Exception ex) 
	  {
	    ex.printStackTrace();
	  }
      }
  }

  /****************************************************************************/

  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) 
    throws PrinterException 
  {
    Graphics2D graphics2D = (Graphics2D) graphics;

    int row = pageIndex / columns;
    int column = pageIndex % columns;

    graphics2D.translate(-1.0 * column * paperWidth, -1.0 * row * paperHeight);

    component.print(graphics2D);

    return(Printable.PAGE_EXISTS);
  }

  /****************************************************************************/

}

/******************************************************************************/
