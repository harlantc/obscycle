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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSeparator;

/******************************************************************************/
/**
 * Provide an editor for a JDBC escape formatted date time string.
 */

public class TimestampEditor extends JPanel
  implements ItemListener
{


  /**
   * The months are displayed as integer values for compatibility with
   * JDBC escape format.
   */
  private String[] months =
    new String[] { "01", "02", "03", "04", "05", "06",
		   "07", "08", "09", "10", "11", "12" };

  /**
   * The base calendar.
   */
  private Calendar calendar;

  /**
   * Used to clear the selection.
   */
  private JCheckBox checkBox;

  /**
   * Year combo box.
   */
  private JComboBox yearComboBox;

  /**
   * Month combo box.
   */
  private JComboBox monthComboBox;

  /**
   * Day combo box.
   */
  private JComboBox dayComboBox;

  /**
   * Hour combo box.
   */
  private JComboBox hoursComboBox;

  /**
   * Millisecond combo box.
   */
  private JComboBox millisecondsComboBox;

  /**
   * Minute combo box.
   */
  private JComboBox minutesComboBox;

  /**
   * Second combo box.
   */
  private JComboBox secondsComboBox;

  /**
   * Display component for the result value.
   */
  private JTextField selectedValue;

  /**
   * The base timestamp used to calculate the various components.
   */
  private Timestamp baseTimestamp;

  /****************************************************************************/
  /**
   * Construct a DateTime object from a JDBC escape formatted date time string.
   *
   * @param s The JDBC escape formatted input string.
   */
  public TimestampEditor( String s )
  {
    // Create the calendar object and initialize it.
    calendar = Calendar.getInstance();
    setCalendar( s );

    // Initialize the layout.
    setLayout( new GridBagLayout() );
    int row = 0;
    int column = 0;

    // Layout the instruction message.
    JLabel label =
      new JLabel( "Choose a new value by selecting the following:" );
    GridBagLayoutUtil.addComponent( this, label, column++, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

	//TODO: why 25
    // Create the year combo box using a 25 year span from launch and
    // lay it out with a label starting on a new line.
    Vector<String> items = new Vector<String>();
    int year;
    for ( year = 1998; year < 1998 + 50; year++ )
    {
      items.add( Integer.toString( year ) );
    }
    yearComboBox = new JComboBox( items );
    year = calendar.get( Calendar.YEAR );
    yearComboBox.setSelectedItem( Integer.toString( year ) );
    label = new JLabel( "Year: " );
    column = 0;
    GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, yearComboBox, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Create the month combo box and lay it out with a label.
    items = new Vector<String>( Arrays.asList( months ) );
    monthComboBox = new JComboBox( items );
    int month = calendar.get( Calendar.MONTH ) + 1;
    monthComboBox.setSelectedItem( normalize( month ) );
    label = new JLabel( "Month: " );
    GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, monthComboBox, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Create the day combo box and lay it out with a label.
    dayComboBox = new JComboBox();
    label = new JLabel( "Day: " );
    GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, dayComboBox, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Create the hours combo box and lay it out with a label starting
    // on a new line.
    items = new Vector<String>();
    int time;
    for ( time = 0; time < 24; time++ )
    {
      items.add( normalize( time ) );
    }
    hoursComboBox = new JComboBox( items );
    time = calendar.get( Calendar.HOUR_OF_DAY );
    hoursComboBox.setSelectedItem( normalize( time ) );
    label = new JLabel( "Hour: " );
    row++;
    column = 0;
    GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, hoursComboBox, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Create the minutes combo box and lay it out with a label.
    items = new Vector<String>();
    for ( time = 0; time < 60; time++ )
    {
      items.add( normalize( time ) );
    }
    minutesComboBox = new JComboBox( items );
    time = calendar.get( Calendar.MINUTE );
    minutesComboBox.setSelectedItem( normalize( time ) );
    label = new JLabel( "Minute: " );
    GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, minutesComboBox, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Create the seconds combo box and lay it out with a label.
    secondsComboBox = new JComboBox( items );
    time = calendar.get( Calendar.SECOND );
    secondsComboBox.setSelectedItem( normalize( time ) );
    label = new JLabel( "Second: " );
    GridBagLayoutUtil.addComponent( this, label, column++, row, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, secondsComboBox, column++, row++, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Add a checkbox to clear the values.
    checkBox = new JCheckBox( "Clear selection" );
    checkBox.addItemListener( this );
    GridBagLayoutUtil.addComponent( this, checkBox, 1, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Add a separator.
    JSeparator sep = new JSeparator();
    GridBagLayoutUtil.addComponent( this, sep, 0, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.HORIZONTAL,
				    GridBagConstraints.CENTER,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Show the selected value.
    selectedValue = new JTextField( 16 );
    selectedValue.setText( s );
    selectedValue.setEditable( false );
    label = new JLabel( "Selected value: " );
    column = 0;
    GridBagLayoutUtil.addComponent( this, label, column++, row, 2, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.0, 5, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( this, selectedValue, 2, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Place a separator between the selected value and the controls.
    sep = new JSeparator();
    GridBagLayoutUtil.addComponent( this, sep, 0, row++,
				    GridBagConstraints.REMAINDER, 1,
				    GridBagConstraints.HORIZONTAL,
				    GridBagConstraints.CENTER,
				    1.0, 0.0, 5, 5, 5, 5 );

    // Lastly, set up the combo box listeners and the day items.
    yearComboBox.addItemListener( this );
    monthComboBox.addItemListener( this );
    dayComboBox.addItemListener( this );
    hoursComboBox.addItemListener( this );
    minutesComboBox.addItemListener( this );
    secondsComboBox.addItemListener( this );
    setDayItems( s );
  }

  /****************************************************************************/
  /**
   * Return the current value of the timestamp.
   *
   * @returns The timestamp value determined from the combo box
   * selections or null if the user has selected the check box.
   */

  public String getTimestamp()
  {
    String result = null;

    if ( checkBox.isSelected() )
    {
      result = null;
    }
    else
    {
      // Construct a JDBC formatted string from the current GUI
      // component values.
      String year = (String) yearComboBox.getSelectedItem();
      String month = (String) monthComboBox.getSelectedItem();
      String day = (String) dayComboBox.getSelectedItem();
      String hours = (String) hoursComboBox.getSelectedItem();
      String minutes = (String) minutesComboBox.getSelectedItem();
      String seconds = (String) secondsComboBox.getSelectedItem();
      //String milliseconds = (String) millisecondsTextField.getText();
      String milliseconds = "0";

      result = year + "-" + month + "-" + day + " " +
	hours + ":" + minutes + ":" + seconds + "." + milliseconds;
    }

    return result;
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
      // Disable the various combo boxes.
      yearComboBox.setEnabled( false );
      monthComboBox.setEnabled( false );
      dayComboBox.setEnabled( false );
      hoursComboBox.setEnabled( false );
      minutesComboBox.setEnabled( false );
      secondsComboBox.setEnabled( false );
    }
    else if ( source == checkBox && state == ItemEvent.DESELECTED )
    {
      // Disable the various combo boxes.
      yearComboBox.setEnabled( true );
      monthComboBox.setEnabled( true );
      dayComboBox.setEnabled( true );
      hoursComboBox.setEnabled( true );
      minutesComboBox.setEnabled( true );
      secondsComboBox.setEnabled( true );
    }
    else if ( ( source == yearComboBox ||
		source == monthComboBox ) &&
	      state == ItemEvent.SELECTED )
    {
      // Recompute the days combo box items.
      setDayItems( getTimestamp() );
    }
    
    String timestamp = getTimestamp();
    if ( timestamp == null )
    {
      // Clear the timestamp selection.
      selectedValue.setText( "" );
    }
    else
    {
      selectedValue.setText( timestamp );
    }
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

  private String normalize( int value )
  {
    String result = Integer.toString( value );

    // Determine if a leading "0" is necessary.
    if ( value < 10 )
    {
      result = "0" + result;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Establish the calendar.
   *
   * @param The timestamp or null.
   */

  public void setCalendar( String s )
  {
    // Establish the initial combo box selections via setting the
    // current date of our calendar.
    if ( s == null || s.equals( "" ) || (s.indexOf( "NONE" ) >= 0) )
    {
      baseTimestamp = new Timestamp( System.currentTimeMillis() );
    }
    else
    {
      baseTimestamp = Timestamp.valueOf( s );
    }
    calendar.setTime( baseTimestamp );
  }

  /****************************************************************************/
  /**
   * Set the items in the day combo box.
   *
   * @param A timestamp to use in generating a calendar.
   */

  public void setDayItems( String timestamp )
  {
    System.out.println( "Setting the day items" );

    Vector<String> items = new Vector<String>();
    String dayString;

    setCalendar( timestamp );
    int N = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );

    System.out.println( "Days in month: " + N );

    int day;
    for ( day = 1; day <= N; day++ )
    {
      dayString = normalize( day );
      items.add( dayString );
    }
    
    dayComboBox.setModel( new DefaultComboBoxModel( items ) );
    day = calendar.get( Calendar.DAY_OF_MONTH );
    dayString = normalize( day );
    dayComboBox.setSelectedItem( dayString );
  }

  /****************************************************************************/

}

/******************************************************************************/
