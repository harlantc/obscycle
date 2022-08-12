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
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/******************************************************************************/
/**
 * Provide an editor for a new sequence number.
 */

public class SequenceNumberEditor extends JPanel
  implements ItemListener
{
  /****************************************************************************/
  /**
   * Construct a sequence number editor.
   *
   * @param s The initial value.
   */
  public SequenceNumberEditor( String s )
  {
    // Get the notes text.
    Properties properties = Editor.getInstance().getProperties();
    subjectCategoryNotesText =
      properties.getProperty( "SubjectCategoryNotes.text" );

    // Initialize the layout.
    setLayout( new GridBagLayout() );
    int row = 0;
    int column = 0;

    // Layout the instruction message.
    JLabel label =
      new JLabel( "Either select by subject category or a choose an existing sequence number:" );
    GridBagLayoutUtil.addComponent( this, label, 0, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Layout the seed by subject category radio button and combo box.
    subjectCategoryRadioButton =
      new JRadioButton( "Select by Subject Category" );
    subjectCategoryRadioButton.setHorizontalTextPosition( SwingConstants.LEFT );
    subjectCategoryRadioButton.addItemListener( this );
    GridBagLayoutUtil.addComponent( this, subjectCategoryRadioButton,
				    0, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    subjectCategoryComboBox = new JComboBox( subjectCategories[1] );
    subjectCategoryComboBox.addItemListener( this );
    GridBagLayoutUtil.addComponent( this, subjectCategoryComboBox,
				    1, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Layout the existing sequence number category radio button and
    // combo box.
    existingSequenceNumberRadioButton =
      new JRadioButton( "Select by Sequence Number" );
    existingSequenceNumberRadioButton.setHorizontalTextPosition( SwingConstants.LEFT );
    existingSequenceNumberRadioButton.addItemListener( this );
    GridBagLayoutUtil.addComponent( this, existingSequenceNumberRadioButton,
				    0, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    Vector existingSequenceNumbers =
      Editor.getInstance().getDatabase().getChoices( "target", "seq_nbr" );
    existingSequenceNumberComboBox = new JComboBox( existingSequenceNumbers );
    existingSequenceNumberComboBox.addItemListener( this );
    GridBagLayoutUtil.addComponent( this, existingSequenceNumberComboBox,
				    1, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Add the buttons to a group.
    ButtonGroup group = new ButtonGroup();
    group.add( subjectCategoryRadioButton );
    group.add( existingSequenceNumberRadioButton );

    // Layout the text field.
    column = 0;
    label = new JLabel( "Selected Sequence Number:" );
    GridBagLayoutUtil.addComponent( this, label,
				    0, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    sequenceNumberTextField = new JTextField( 6 );
    sequenceNumberTextField.setEditable( false );
    GridBagLayoutUtil.addComponent( this, sequenceNumberTextField,
				    1, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Layout a notes field (which takes up two rows).
    sequenceNumberTextArea = new JTextArea( ROWS, 30 );
    sequenceNumberTextArea.setEditable( false );
    sequenceNumberTextArea.setLineWrap( true );
    sequenceNumberTextArea.setWrapStyleWord( true );
    GridBagLayoutUtil.addComponent( this, sequenceNumberTextArea,
				    2, row,
				    GridBagConstraints.REMAINDER, 2,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );
    row += ROWS;

    // Place a separator between the selected value and the controls.
    JSeparator sep = new JSeparator();
    GridBagLayoutUtil.addComponent( this, sep, 0, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.HORIZONTAL,
				    GridBagConstraints.CENTER,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Process the input to initialize the various fields.
    if ( s.endsWith( "TXXXX" ) )
    {
      // The initial value is a selection by subject category.
      subjectCategoryRadioButton.setSelected( true );
      int index = (Integer.valueOf( s.substring( 0, 1 ) )).intValue() - 1;
      subjectCategoryComboBox.setSelectedIndex( index );
      existingSequenceNumberComboBox.setEnabled( false );
    }
    else
    {
      // The initial value is selection by existing sequence number.
      existingSequenceNumberRadioButton.setSelected( true );
      existingSequenceNumberComboBox.setSelectedItem( s );
      subjectCategoryComboBox.setEnabled( false );
    }
  }

  /****************************************************************************/
  /**
   * Return the current value of the sequence number.
   *
   * @returns The sequence number value from the text field.
   */

  public String getSequenceNumber()
  {
    return sequenceNumberTextField.getText();
  }

  /****************************************************************************/
  /**
   * Process item change events.
   *
   * @param event The container for the event details.
   */

  public void itemStateChanged( ItemEvent event )
  {
    // Determine how to handle this event based on the source.
    Object source = event.getItemSelectable();
    int state = event.getStateChange();
    if ( source == subjectCategoryRadioButton &&
	 state == ItemEvent.SELECTED )
    {
      // Enable the subject category combo box.
      subjectCategoryComboBox.setEnabled( true );
      // Update the digit in the currently selected sequence number.
      int index = subjectCategoryComboBox.getSelectedIndex();
      sequenceNumberTextField.setText( subjectCategories[0][index] +
				       "TXXXX" );
      sequenceNumberTextArea.setText( subjectCategoryNotesText );
    }
    else if ( source == existingSequenceNumberRadioButton &&
	      state == ItemEvent.SELECTED )
    {
      // Disable editing on the sequence number text field and enable
      // the subject category combo box.
      existingSequenceNumberComboBox.setEnabled( true );
      String text = (String) existingSequenceNumberComboBox.getSelectedItem();
      sequenceNumberTextField.setText( text );
      sequenceNumberTextArea.setText( "Notes: " );
    }
    else if ( source == subjectCategoryRadioButton &&
	      state == ItemEvent.DESELECTED )
    {
      // Enable editing on the sequence number text field and disable
      // the subject category combo box.
      subjectCategoryComboBox.setEnabled( false );
    }
    else if ( source == existingSequenceNumberRadioButton &&
	      state == ItemEvent.DESELECTED )
    {
      existingSequenceNumberComboBox.setEnabled( false );
    }
    else if ( source == subjectCategoryComboBox &&
	      state == ItemEvent.SELECTED )
    {
      // Update the digit in the currently selected sequence number.
      int index = subjectCategoryComboBox.getSelectedIndex();
      sequenceNumberTextField.setText( subjectCategories[0][index] +
				       "TXXXX" );
      sequenceNumberTextArea.setText( subjectCategoryNotesText );
    }
    else if ( source == existingSequenceNumberComboBox &&
	      state == ItemEvent.SELECTED )
    {
      // Update the digit in the currently selected sequence number.
      String text = (String) existingSequenceNumberComboBox.getSelectedItem();
      sequenceNumberTextField.setText( text );
      sequenceNumberTextArea.setText( "Notes: " );
    }
    else
    {
      // Don't care condition.
    }
  }

  /**
   * Choice box for the first digit of the sequence number.
   */
  private JComboBox existingSequenceNumberComboBox;

  /**
   * When true, enable the selecion of an existing sequence number.
   */
  private JRadioButton existingSequenceNumberRadioButton;

  /**
   * Size of the sequence number notes text area.
   */
  private final int ROWS = 3;

  /**
   * Display notes associated with the sequence number representation.
   */
  private JTextArea sequenceNumberTextArea;

  /**
   * Display component for the result value.
   */
  private JTextField sequenceNumberTextField;

  /**
   * Choice box for the first digit of the sequence number.
   */
  private JComboBox subjectCategoryComboBox;

  /**
   * Size of the sequence number notes text area.
   */
  private String subjectCategoryNotesText;

  /**
   * When true, enable the subject category combo box.
   */
  private JRadioButton subjectCategoryRadioButton;

  /**
   * Map of subject categories to digit.
   */
  private String[][] subjectCategories =
  {{"1", "2", "3", "4", "5", "6", "7", "8", "9", "9"},
   {"(1) SOLAR SYSTEM AND MISC",
    "(2) NORMAL STARS AND WD",
    "(3) WD BINARIES AND CV",
    "(4) BH AND NS BINARIES",
    "(5) SN, SNR AND ISOLATED NS",
    "(6) NORMAL GALAXIES",
    "(7) ACTIVE GALAXIES AND QUASARS",
    "(8) CLUSTERS OF GALAXIES",
    "(9) EXTRAGALACTIC DIFFUSE EMISSION",
    "(9) GALACTIC DIFFUSE EMISSION"}};

  /****************************************************************************/

}

/******************************************************************************/
