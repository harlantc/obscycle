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

package gov.sao.asc.util;

/******************************************************************************/

import gov.sao.asc.obsed.Editor;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/******************************************************************************/
/**
 * Switches the GUI between the Windows, Motif, Mac, and Metal models.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class ToggleLookAndFeel extends Object implements ItemListener
{

  /****************************************************************************/
  /**
   * Changes the look and feel for a User Interface tree.
   *
   * @param e an ItemEvent object that identifies the selected radio button.
   */

  public void itemStateChanged(ItemEvent event)
  {
    Container root = Editor.getInstance().getFrame().getContentPane();

    Cursor cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

    root.setCursor(cursor);

    JRadioButtonMenuItem rb = (JRadioButtonMenuItem) event.getSource();

    try
    {
      if (rb.isSelected() && rb.getText().equals("Windows Style Look and Feel"))
      {
	/*
	LookAndFeel windozeLookAndFeel =
	  new com.sun.java.swing.plaf.windows.WindowsLookAndFeel() {
	    public boolean isSupportedLookAndFeel() {return true;}
	  };
	UIManager.setLookAndFeel(windozeLookAndFeel);
	SwingUtilities.updateComponentTreeUI(root);
	*/
      }
      else if (rb.isSelected() && rb.getText().equals("Macintosh Look and Feel"))
      {
	/*
	LookAndFeel macLookAndFeel =
 	  new com.sun.java.swing.plaf.windows.WindowsLookAndFeel() {
	    public boolean isSupportedLookAndFeel() {return true;}
	  };
	UIManager.setLookAndFeel(macLookAndFeel);
	SwingUtilities.updateComponentTreeUI(root);
	*/
      } else if (rb.isSelected() && rb.getText().equals("Motif Look and Feel")) {
	UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
	SwingUtilities.updateComponentTreeUI(root);
      } else if (rb.isSelected() && rb.getText().equals("Metal Look and Feel")) {
	UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	SwingUtilities.updateComponentTreeUI(root);
      } 
    } 
    catch ( UnsupportedLookAndFeelException exc )
    {
      // Error - unsupported L&F
      rb.setEnabled(false);
      LogClient.logMessage( "Unsupported LookAndFeel: " + rb.getText() );
		
      // Set L&F to Metal
      try
      {
	//metalMenuItem.setSelected(true);
	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	//tabPlacement.setEnabled(true);
	SwingUtilities.updateComponentTreeUI(root);
      }
      catch (Exception exc2)
      {
	LogClient.logMessage( "Could not load LookAndFeel: " + exc2 );
      }
    }
    catch (Exception exc)
    {
      rb.setEnabled(false);
      LogClient.logMessage( "Could not load LookAndFeel: " + rb.getText() );
    }

    root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /****************************************************************************/

}

/******************************************************************************/
