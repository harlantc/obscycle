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

package gov.sao.asc.obsed;

/******************************************************************************/

import gov.sao.asc.event.ExitListener;
import gov.sao.asc.event.PrintListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/******************************************************************************/
/**
 * The <code>FileMenu</code> class sets up the menus for the
 *  GUI applet and application.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class FileMenu extends JMenu
{

  /****************************************************************************/

  public FileMenu(ExitListener exitListener, PrintListener printListener)
  {
    super("File");

    //JMenuItem printMenuItem = new JMenuItem("Print...");

    //printMenuItem.addActionListener(printListener);
    //printMenuItem.setEnabled(false);

    //add(printMenuItem);

    //add(new JSeparator());

    JMenuItem exitMenuItem = new JMenuItem("Exit");

    exitMenuItem.setEnabled(true);
    exitMenuItem.addActionListener(exitListener);

    add(exitMenuItem);
  }

  /****************************************************************************/

}
  
/******************************************************************************/
