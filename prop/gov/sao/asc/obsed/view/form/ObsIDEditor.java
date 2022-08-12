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

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSeparator;

/******************************************************************************/
/**
 * Provide an editor for a new Obs ID value.
 */

public class ObsIDEditor extends JPanel
  implements ItemListener
{
  /****************************************************************************/
  /**
   * Construct a DateTime object from a JDBC escape formatted date time string.
   *
   * @param s The initial value.
   */
  public ObsIDEditor( String s )
  {
    // Initialize the layout.
    setLayout( new GridBagLayout() );
    int row = 0;
    int column = 0;

    // Layout the instruction message.
    JLabel label =
      new JLabel( "Choose a new OBS ID value:" );
    GridBagLayoutUtil.addComponent( this, label, column++, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Add a checkbox to clear the values.
    checkBox = new JCheckBox( "Accept default" );
    checkBox.addItemListener( this );
    GridBagLayoutUtil.addComponent( this, checkBox, 0, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Show the selected value.
    obsIDTextField = new JTextField( 8 );
    if ( !s.equals( Constants.DEFAULT ) )
    {
      obsIDTextField.setText( s );
    }
    label = new JLabel( "OBS ID: " );
    column = 0;
    GridBagLayoutUtil.addComponent( this, label, 0, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, obsIDTextField, 1, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Place a separator between the selected value and the controls.
    JSeparator sep = new JSeparator();
    GridBagLayoutUtil.addComponent( this, sep, 0, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.HORIZONTAL,
				    GridBagConstraints.CENTER,
				    1.0, 0.0, 5, 5, 5, 5 );

  }

  /****************************************************************************/
  /**
   * Return the current value of the timestamp.
   *
   * @returns The timestamp value determined from the combo box
   * selections or null if the user has selected the check box.
   */

  public String getObsID()
  {
    return obsIDTextField.getText();
  }

  /****************************************************************************/
  /**
   * Convert the given integer to a string insuring that a leading "0"
   * is generated if the value is less than 10.
   *
   * @param value The int number to convert.
   *
   * @returns A two digit string with a leading "0" as necessary.
   */

  public void itemStateChanged( ItemEvent event )
  {
    // Determine how to handle this event based on the source.
    Object source = event.getItemSelectable();
    int state = event.getStateChange();
    if ( source == checkBox && state == ItemEvent.SELECTED )
    {
      // Indicate that the default stored procedure value will be
      // used.
      obsIDTextField.setText( Constants.DEFAULT );
    }
    else if ( source == checkBox && state == ItemEvent.DESELECTED )
    {
      // Initialize the Obs ID text to empty.
      obsIDTextField.setText( "" );
    }
  }

  /**
   * Boolean default/specific value indication.
   */
  private JCheckBox checkBox;

  /**
   * Display component for the result value.
   */
  private JTextField obsIDTextField;

  /****************************************************************************/

}

/******************************************************************************/
