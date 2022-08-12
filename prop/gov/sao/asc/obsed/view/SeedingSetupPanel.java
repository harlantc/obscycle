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

import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.event.ApplyButtonListener;
import gov.sao.asc.obsed.event.ApplyInterface;
import gov.sao.asc.obsed.event.DismissButtonListener;
import gov.sao.asc.obsed.event.DismissInterface;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/******************************************************************************/
/**
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class SeedingSetupPanel extends JPanel
  implements ActionListener
{
  public static final int DEFAULT = 0;
  public static final int CLONE = 1;
  public static final int NULL = 2;

  private JComboBox cloneIDComboBox;
  private JTextField proposalNumberTextField;
  private JTextField sequenceNumberTextField;
  private JTextField sqlTextField;

  /**
   * The seeding mode.
   */
  private int seedingMode;

  /**
   * The invoking view.
   */
  private View view;

  /****************************************************************************/
  /**
   * Construct the panel using the key value to permit clone
   * selection.
   */

  public SeedingSetupPanel( View view )
  {
    super();
    this.view = view;

    setLayout( new GridBagLayout() );

    // Assemble the mode selection panel.
    JPanel modeSelectionPanel = initModeSelectionPanel();

    GridBagLayoutUtil.addComponent( this, modeSelectionPanel,
				    0, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5);

    // Assemble the form panel.
    JPanel clonePanel = initClonePanel();

    GridBagLayoutUtil.addComponent( this, clonePanel,
				    0, 1, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5);
  }

  /****************************************************************************/

  public void actionPerformed( ActionEvent event )
  {
    // Set the current mode.
    String command = event.getActionCommand();
    if ( command.equals( "Clone" ) )
    {
      seedingMode = CLONE;
    }
    else if ( command.equals( "Default" ) )
    {
      seedingMode = DEFAULT;
    }
    else if ( command.equals( "Null" ) )
    {
      seedingMode = NULL;
    }
  }

  /****************************************************************************/

  public void enableForm(boolean state)
  {
    if (state)
    {
    }
    else
    {
    }
  }

  /****************************************************************************/
  /**
   * Return the selected seeding mode.
   */

  public int getSeedingMode()
  {
    return seedingMode;
  }

  /****************************************************************************/
  /**
   * Return the selected clone key value.
   */

  public Object getCloneKeyValue()
  {
    return cloneIDComboBox.getSelectedItem();
  }

  /****************************************************************************/

  public JPanel initClonePanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );
    result.setBorder( new TitledBorder( "Clone Preferences" ) );

    GridBagLayoutUtil.addComponent( result,
				    new JLabel( "Current Observation: " ),
				    0, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.EAST,
				    1.0, 0.25, 5, 5, 5, 5 );

    // Set up the clone combo box.
    Database database = Editor.getInstance().getDatabase();
    cloneIDComboBox = new JComboBox( view.getKeys() );
    cloneIDComboBox.setSelectedItem( view.getSelectedKeyValue() );
    cloneIDComboBox.setToolTipText( "The currently selected observation." );

    GridBagLayoutUtil.addComponent( result, cloneIDComboBox,
				    1, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.WEST,
				    1.0, 0.25, 5, 5, 5, 5 );

    return(result);
  }

  /****************************************************************************/

  public JPanel initModeSelectionPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );
    result.setBorder( new BevelBorder( BevelBorder.RAISED ) );

    // Generate the selection label.
    JLabel label = new JLabel( "Select creation mode: " );
    GridBagLayoutUtil.addComponent( result, label,
				    0, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    // Create the seeding button group.
    ButtonGroup group = new ButtonGroup();

    // Set up the clone button and add it to the form
    JRadioButton cloneButton = new JRadioButton( "Clone" );
    cloneButton.setSelected( true );
    seedingMode = CLONE;
    String text = "Use the current observation values to seed a new observation.";
    cloneButton.setToolTipText( text );
    group.add( cloneButton );
    cloneButton.addActionListener( this );
    GridBagLayoutUtil.addComponent( result, cloneButton,
				    1, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    // Set up the default button and add it to the form.
    JRadioButton defaultButton = new JRadioButton( "Default" );
    text = "Use default values to seed a new observation.";
    defaultButton.setToolTipText( text );
    group.add( defaultButton );
    defaultButton.addActionListener( this );
    defaultButton.setEnabled( false );
    GridBagLayoutUtil.addComponent( result, defaultButton,
				    2, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    // Set up the null button and add it to the form.
    JRadioButton nullButton = new JRadioButton( "Null" );
    text = "Specify complete set of values for the new observation.";
    nullButton.setToolTipText( text );
    group.add( nullButton );
    nullButton.addActionListener( this );
    nullButton.setEnabled( false );
    GridBagLayoutUtil.addComponent( result, nullButton,
				    3, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    return result;
  }

  /****************************************************************************/

}

/******************************************************************************/
