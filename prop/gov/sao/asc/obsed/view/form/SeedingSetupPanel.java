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

import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.obsed.event.ApplyButtonListener;
import gov.sao.asc.obsed.event.ApplyInterface;
import gov.sao.asc.obsed.event.DismissButtonListener;
import gov.sao.asc.obsed.event.DismissInterface;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LocationUtil;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
{
  public static final int DEFAULT = 0;
  public static final int CLONE = 1;
  public static final int FULL = 2;

  private Navigator navPanel;
  private JTextField obsIDTextField;
  private JTextField proposalNumberTextField;
  private JTextField sequenceNumberTextField;
  private JTextField sqlTextField;

  /****************************************************************************/
  /**
   * Construct the panel using the key value to permit clone
   * selection.
   */

  public SeedingSetupPanel( Navigator navPanel )
  {
    super();
    this.navPanel = navPanel;

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
    // tbd
    return CLONE;
  }
  /****************************************************************************/

  public JPanel initClonePanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );
    result.setBorder( new TitledBorder( "Clone Preferences" ) );

    GridBagLayoutUtil.addComponent( result, new JLabel( "Current Observation: " ),
				    0, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.EAST,
				    1.0, 0.25, 5, 5, 5, 5 );

    obsIDTextField = new JTextField(8);
    obsIDTextField.setText( navPanel.getKeyValue().toString() );
    obsIDTextField.setEditable( false );
    obsIDTextField.setToolTipText( "The currently selected observation." );

    GridBagLayoutUtil.addComponent( result, obsIDTextField,
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

    ButtonGroup group = new ButtonGroup();

    JRadioButton cloneButton = new JRadioButton( "Clone" );
    cloneButton.setSelected( true );

    //unfilteredButton.addActionListener( new UnfilteredButtonListener(this) );
    String text = "Use the current observation values to seed a new observation.";
    cloneButton.setToolTipText( text );

    group.add( cloneButton );

    GridBagLayoutUtil.addComponent( result, cloneButton,
				    1, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    JRadioButton defaultButton = new JRadioButton( "Default" );

    // Disable temporarily.
    defaultButton.setEnabled( false );

    //filterByFormButton.addActionListener( new FilterByFormButtonListener(this) );
    text = "Use default values to seed a new observation.";
    defaultButton.setToolTipText( text );

    group.add( defaultButton );

    GridBagLayoutUtil.addComponent( result, defaultButton,
				    2, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    JRadioButton fullButton = new JRadioButton( "Full" );

    // Disable temporarily.
    fullButton.setEnabled( false );

    //filterBySQLButton.addActionListener( new FilterBySQLButtonListener(this) );
    text = "Specify complete set of values for the new observation.";
    fullButton.setToolTipText( text );

    group.add( fullButton );

    GridBagLayoutUtil.addComponent( result, fullButton,
				    3, 0, 1, 1,
				    GridBagConstraints.NONE, 
				    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    return result;
  }

  /****************************************************************************/

}

/******************************************************************************/
