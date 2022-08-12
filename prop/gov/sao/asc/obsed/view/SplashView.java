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

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.DatabaseKey;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.util.Properties;

/******************************************************************************/

/**
 * Build a screen suitable to use as a "splash" screen.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class SplashView extends JPanel
{

  /****************************************************************************/
  /**
   * Build a GUI screen suitable to use as a splash screen.
   *
   * @param canRefresh the view can be refreshed if this true.
   */

  public SplashView()
  {
    refresh();
  }

  /****************************************************************************/

  public void init()
  {
    refresh();
  }

  /****************************************************************************/
  /**
   * Build a GUI screen suitable to use as a splash screen.
   *
   * @param canRefresh the view can be refreshed if this true.
   */

  public void refresh()
  {
    removeAll();

    JPanel panel = new JPanel();

    Properties props = Editor.getInstance().getProperties();
    String message = props.getProperty( "splash.message", "ObsCat Editor" );
    Integer rows = new Integer( props.getProperty( "splash.message.rows", "5" ) );
    Integer cols = new Integer( props.getProperty( "splash.message.cols", "40" ) );

    JTextArea splashMessage = new JTextArea(message, 
                                            rows.intValue(), 
                                            cols.intValue() );

    splashMessage.setEditable( false );

    panel.add(splashMessage);

    add( panel, BorderLayout.CENTER );
  }

  /****************************************************************************/

}

/******************************************************************************/
