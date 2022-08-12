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

import gov.sao.asc.obsed.event.AboutListener;
import gov.sao.asc.obsed.event.HelpListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/******************************************************************************/

/**
 * The <code>HelpMenu</code> class sets up the menus for the ObsCat
 * GUI applet and application.
 *
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class HelpMenu extends JMenu
{

  /****************************************************************************/

  public HelpMenu( HelpListener helpListener,
                   AboutListener aboutListener )
  {
    super("Help");

    setMnemonic('h');

    JMenuItem helpMenuItem = new JMenuItem( "Help..." );

    helpMenuItem.addActionListener( helpListener );
    helpMenuItem.setEnabled( true );

    add( helpMenuItem );

    // About dialog.
    JMenuItem aboutMenuItem = new JMenuItem( "About..." );

    aboutMenuItem.addActionListener( aboutListener );
    aboutMenuItem.setEnabled( true );

    add( aboutMenuItem );
  }

  /****************************************************************************/

}
  
/******************************************************************************/
