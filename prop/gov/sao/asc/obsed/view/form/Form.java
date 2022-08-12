/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting docu- mentation, and that the name of the Smithsonian
  Astro- physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO THIS
  SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT- ABILITY AND
  FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR THE
  SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import gov.sao.asc.obsed.view.Configuration;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/******************************************************************************/
/**
 * Creates a panel containing configured components.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class Form extends JPanel
{
  /****************************************************************************/
  /**
   * Construct the panel using the supplied configuration.
   */

  public Form( Configuration configuration )
  {
    setLayout( new GridBagLayout() );

    // Add the configured components.
    Iterator i = configuration.getComponentEntries().iterator();
    while ( i.hasNext() )
    {
      FormComponentEntry entry = (FormComponentEntry) i.next();
      int row = entry.getRow();
      int column = entry.getColumn();
      int width = entry.getColumnWidth();
      int height = entry.getRowHeight();
      int anchor = entry.getAnchor();
      int fill = entry.getFill();

      // Check for the existance of a label.
      JLabel label = entry.getLabel();
      if ( label != null )
      {
	// Layout the label.
	GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
					GridBagConstraints.NONE,
					GridBagConstraints.EAST,
					1.0, 0.0, 5, 5, 5, 5 );
      }

      // Layout the component.
      JComponent component = entry.getViewableComponent();
      GridBagLayoutUtil.addComponent( this, component, column, row,
				      width, height, fill, anchor,
				      1.0, 0.0, 5, 5, 5, 5 );
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
