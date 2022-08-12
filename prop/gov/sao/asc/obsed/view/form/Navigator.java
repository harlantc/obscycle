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

import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.view.form.event.NavChangeEvent;
import gov.sao.asc.obsed.view.form.event.NavChangeListener;
import gov.sao.asc.util.Pair;
import gov.sao.asc.util.LogClient;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;

/******************************************************************************/
/**
 * Provides navigation functionality for the form view.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class Navigator extends JPanel implements ItemListener, ActionListener
{

  // The configuration object.
  private FormViewConfiguration configuration;

  // The storage of the mapping entries.
  private Hashtable<String,DatabaseKey> entryHashtable;

  // Support for navigation change events.
  private EventListenerList listenerList;
  private Pair previousKeyValue;

  // The collection of navigation pairs used to display the current
  // value and to lookup the unique key paired with that value.
  private Vector pairs;

  // GUI Components
  private JButton maxButton;
  private JButton minButton;
  private JButton nextButton;
  private JButton prevButton;
  private JComboBox columnComboBox;
  private JComboBox rowComboBox;
  private JTextField idTextField;
  private JLabel idLabel;
  private JLabel idOrLabel;

  // Locked state of the currently selected row and associated icon
  // and text.
  private boolean locked;
  Icon lockedIcon;
  JLabel lockedIconLabel;

  /****************************************************************************/
  /**
   * Construct a navigator to manipulate the view.
   *
   * @param form  The parent view.
   */

  public Navigator( FormViewConfiguration configuration )
  {
    this.configuration = configuration;
    String toolTipText;
    Database database = Editor.getInstance().getDatabase();
    Properties properties = Editor.getInstance().getProperties();
    listenerList = new EventListenerList();

    // Initialize the collection of navigation pairs.
    pairs = null;

    // Set locked state initially to false.
    locked = false;

    // Seed the previous key value with a non-null value.
    previousKeyValue = new Pair ( new String("NULL"), new String("NULL") );

    // Deal with the GUI components.
    setLayout( new FlowLayout() );
    setBorder( new TitledBorder( "Navigator: " ) );

    toolTipText =
      properties.getProperty( "navigator.columnComboBox.toolTipText" );

    // Set up navigation text field
    idLabel = new JLabel("Enter key: ");
    idOrLabel = new JLabel(" -or- ");

    idTextField = new JTextField(16);
    idTextField.setToolTipText(toolTipText);
    idTextField.addActionListener( this );

    // Set up the navigation choice combo box.
    columnComboBox = new JComboBox();

    columnComboBox.setToolTipText( toolTipText );
    columnComboBox.addItemListener( this );


    // Set up the previous/next buttons.
    minButton = new JButton( "<<" );
    toolTipText =  properties.getProperty( "navigator.minButton.toolTipText" );
    minButton.setToolTipText( toolTipText );
    minButton.addActionListener( this );

    prevButton = new JButton( "<" );
    toolTipText =  properties.getProperty( "navigator.prevButton.toolTipText" );
    prevButton.setToolTipText( toolTipText );
    prevButton.addActionListener( this );

    nextButton = new JButton( ">" );
    toolTipText =  properties.getProperty( "navigator.nextButton.toolTipText" );
    nextButton.setToolTipText( toolTipText );
    nextButton.addActionListener( this );

    maxButton = new JButton( ">>" );
    toolTipText =  properties.getProperty( "navigator.maxButton.toolTipText" );
    maxButton.setToolTipText( toolTipText );
    maxButton.addActionListener( this );
    
    // Setup the combo box.
    rowComboBox = new JComboBox();
    rowComboBox.setMaximumRowCount(30);

    toolTipText =
      properties.getProperty( "navigator.rowComboBox.toolTipText" );

    rowComboBox.setToolTipText( toolTipText );
    rowComboBox.addItemListener( this );

    // Set up the locked state icon label.
    lockedIconLabel = new JLabel();
    toolTipText =
      properties.getProperty( "navigator.unlockIcon.toolTipText" );
    lockedIconLabel.setToolTipText( toolTipText );
    URL url = Editor.getInstance().getConfigurationURL( "unlock.gif" );
    lockedIcon = new ImageIcon( url );
    lockedIconLabel.setIcon( lockedIcon );

    // Layout the components.
    add( idLabel );
    add( idTextField );
    add( idOrLabel );
    add( columnComboBox );
    add( minButton );
    add( prevButton );
    add( rowComboBox );
    add( nextButton );
    add( maxButton );
    add( lockedIconLabel );

  }

  /****************************************************************************/
  /**
   * Handle button navigation.
   */

  public void actionPerformed( ActionEvent event )
  {
    String command = event.getActionCommand();

    if ( command.equals( "<<" ) )
    {
      selectFirstRow();
    }
    else if ( command.equals( "<" ) )
    {
      selectPreviousRow();
    }
    else if ( command.equals( ">" ) )
    {
      selectNextRow();
    }
    else if ( command.equals( ">>" ) )
    {
      selectLastRow();
    }
    else 
    {
      int  idx = getKeyValueIndex(command);
      if (idx >= 0) {
        rowComboBox.setSelectedIndex(idx);
      }
      else {
        String message = "Invalid record key.  Please try again.";
        String title = "Navigation Help Dialog";
        JOptionPane.showMessageDialog( null, message, title,
                                   JOptionPane.WARNING_MESSAGE );

      }
    }
  }

  /****************************************************************************/
  /**
   * Add an event listener for changes to the observation currently
   * selected for display.
   */

  public void addChangeListener( NavChangeListener l )
  {
    listenerList.add( NavChangeListener.class, l );
  }

  /****************************************************************************/
  /**
   * Tell the change event listeners that a new observation is now
   * being displayed.
   */

  protected void fireChangeEvent( NavChangeEvent changeEvent )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event:
    if ( changeEvent == null )
    {
      changeEvent = new NavChangeEvent( columnComboBox, null, null, null );
    }

    // Process the listeners last to first, notifying those that are
    // interested in this event
    for ( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if ( listeners[ i ] == NavChangeListener.class )
      {
	((NavChangeListener) listeners[ i + 1 ]).stateChanged( changeEvent);
      }              
    }
  } 

  /****************************************************************************/
  /**
   * Get the database key corresponding to the currently displayed
   * navigation column.
   *
   * @returns an object.
   */

  public DatabaseKey getKey()
  {
    return configuration.getKeyEntry();
  }

  /****************************************************************************/
  /**
   * Get the key value corresponding to the currently displayed
   * navigation column.
   *
   * @return an object.
   */

  public String getKeyTableName()
  {
    String result = null;

    DatabaseKey keyEntry = configuration.getKeyEntry();

    if ( keyEntry != null )
    {
      result = keyEntry.getTableName();
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Get the key value corresponding to the currently displayed
   * navigation column.
   *
   * @return an object.
   */

  public Object getKeyValue()
  {
    int index = rowComboBox.getSelectedIndex();

    Pair pair = (Pair) pairs.elementAt(index);

    return( pair.getKey() );
  }

  /****************************************************************************/
  /**
   * Find the key value associated with a given string.
   */

  public Object getKeyValue( String keyValue )
  {
    Object result = null;

    for ( int index = 0; index < pairs.size(); index++ )
    {
      // Use string comparison to find the key object.
      Pair pair = (Pair) pairs.elementAt( index );
      String target = pair.getKey().toString();
      if ( target.equals( keyValue ) )
      {
        result = rowComboBox.getItemAt( index );
        break;
      }
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Find the key value associated with a given string.
   */

  public int getKeyValueIndex( String keyValue )
  {
    int result = -1;

    for ( int index = 0; index < pairs.size(); index++ )
    {
      // Use string comparison to find the key object.
      Pair pair = (Pair) pairs.elementAt( index );
      String target = pair.getValue().toString();
      if ( target.equals( keyValue ) )
      {
        result = index;
        break;
      }
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Get the current selected column.
   *
   * @returns the current colum
   */

  public int getSelectedColumn()
  {
    return( columnComboBox.getSelectedIndex() );
  }

  /****************************************************************************/
  /**
   * Get the current selected row.
   *
   * @returns the current row
   */

  public int getSelectedRow()
  {
    return( rowComboBox.getSelectedIndex() );
  }

  /****************************************************************************/
  /**
   * Initialize the Navigator components.
   */

  public void init()
  {
    NavigationalEntry entry;
    String label;

    // Initialize the navigation hashtable.
    entryHashtable = new Hashtable<String,DatabaseKey>();

    // Initialze the column combo box.
    Vector displayEntries = configuration.getDisplayEntries();
    Iterator i = displayEntries.iterator();
    while ( i.hasNext() )
    {
      entry = (NavigationalEntry) i.next();
      label = entry.getName();
      DatabaseKey dbKey =
	new DatabaseKey( entry.getTable(), entry.getColumn() );
      entryHashtable.put( label, dbKey );
      columnComboBox.addItem( label );
    }

  }

  /****************************************************************************/
  /**
   * Handle an item selection.
   */

  public void itemStateChanged( ItemEvent event )
  {
    if ( event.getStateChange() == ItemEvent.SELECTED )
    {
      // Find out which combo box we are dealing with.
      JComboBox comboBox = (JComboBox) event.getItemSelectable();

      if ( comboBox == columnComboBox )
      {
        Object keyValue = null;

        if (pairs != null)
        {
          keyValue = getKeyValue();
        }

        setRowNamesForColumn( (String) event.getItem() );

        if (pairs != null)
        {
          setSelectedKeyValue(keyValue);
        }
      }
      else
      {
	// Tell the listeners that we've changed the navigation key
	// value.
	Pair pair = (Pair) event.getItem();
        idTextField.setText(pair.getValue().toString());
	Object keyValue = pair.getKey();

	// If the previous key value indicates the same unique
	// database record, then don't fire off the nav change event.
	if (! previousKeyValue.getKey().equals( keyValue ) )
	{
	  // A new record has been selected.  Notify consumers.
	  NavChangeEvent navEvent =
	    new NavChangeEvent( comboBox, previousKeyValue, 
				keyValue, getKeyTableName() );
	  fireChangeEvent( navEvent );
	}
      }
    }
    else if ( event.getStateChange() == ItemEvent.DESELECTED )
    {
      // Save the key value if this is the row combo box.
      JComboBox comboBox = (JComboBox) event.getItemSelectable();
      if ( comboBox == rowComboBox )
      {
	// It is.  Save it.
	previousKeyValue = (Pair) event.getItem();
      }
    }
  }

  /****************************************************************************/
  /**
   * Reload the value combo box values and the navigation key values
   * from the database.  Select the entry corresponding to the given
   * `keyValue'.
   */

  public void reload()
  {
    // Reload the combo box values.
    String label = (String) columnComboBox.getSelectedItem();

    setRowNamesForColumn( label );
  }

  /****************************************************************************/
  /**
   * Remove an event listener.
   */

  public void removeChangeListener( NavChangeListener l )
  {
    listenerList.remove( NavChangeListener.class, l );
  }


  /****************************************************************************/
  /**
   * Select the first value from the collection.
   */

  public void selectFirstRow()
  {
    rowComboBox.setSelectedIndex(0);
  }

  /****************************************************************************/
  /**
   * Select the last value from the collection.
   */

  public void selectLastRow()
  {
    rowComboBox.setSelectedIndex(pairs.size() - 1);
  }

  /****************************************************************************/
  /**
   * Select the next value from the collection.
   */

  public void selectNextRow()
  {
    int index = rowComboBox.getSelectedIndex();

    if ( index < (rowComboBox.getItemCount() - 1) )
    {
      rowComboBox.setSelectedIndex(index + 1);
    }
    else
    {
      selectFirstRow();
    }
  }

  /****************************************************************************/
  /**
   * Select the previous value from the collection.
   */

  public void selectPreviousRow()
  {
    int index = rowComboBox.getSelectedIndex();

    if ( index > 0 )
    {
      rowComboBox.setSelectedIndex(index - 1);
    }
    else
    {
      selectLastRow();
    }
  }

  /****************************************************************************/
  /**
   * Set the locked state for this row.  Update the GUI to reflect
   * that the row is locked.
   *
   * @param flag true if the current record is locked; false if it is
   * not.
   */
  public void setLocked( boolean flag )
  {
    String toolTipText;
    Database database = Editor.getInstance().getDatabase();
    Properties properties = Editor.getInstance().getProperties();
    URL url;
    if( flag )
    {
      url = Editor.getInstance().getConfigurationURL( "lock.gif" );
      toolTipText =
	properties.getProperty( "navigator.lockIcon.toolTipText" );
    }
    else
    {
      url = Editor.getInstance().getConfigurationURL( "unlock.gif" );
      toolTipText =
	properties.getProperty( "navigator.unlockIcon.toolTipText" );
    }
    lockedIcon = new ImageIcon( url );
    lockedIconLabel.setToolTipText( toolTipText );
    lockedIconLabel.setIcon( lockedIcon );
  }

  /****************************************************************************/
  /**
   * Set the row combo box items for the specified column.
   */

  public void setRowNamesForColumn( String label )
  {
    // Get the tableName and the columnName associated with the
    // specified label.
    DatabaseKey columnComboBoxDatabaseKey =
      (DatabaseKey) entryHashtable.get( label );

    // Fetch the pairs of values which comprise: 1) the set of valid
    // values for the new column combo box selected item (indicated by
    // the `label') and 2) the unique database identifier (object id)
    // for each of those valid values.
    Database database = Editor.getInstance().getDatabase();
    DatabaseKey keyEntry = configuration.getKeyEntry();
    pairs = database.getPairs( keyEntry.getTableName(), 
                               keyEntry.getColumnName(), 
                               columnComboBoxDatabaseKey.getColumnName() );

    // Remove any existing items.
    if ( rowComboBox.getItemCount() > 0 )
    {
      rowComboBox.removeAllItems();
    }
    
    // Insert the new values.
    rowComboBox.setModel( new DefaultComboBoxModel( pairs ) );
  }

  /****************************************************************************/

  public void setSelectedColumn( int columnIndex )
  {
    // Assert: 0 <= columnIndex <= columnComboBox.getItemCount()
    columnComboBox.setSelectedIndex( columnIndex );
    setRowNamesForColumn( (String) columnComboBox.getItemAt( columnIndex ) );
  }

  /****************************************************************************/
  /**
   * Set the selected value for this key
   *
   * @param keyValue The ID for the new row selection.
   */

  public void setSelectedKeyValue( Object keyValue )
  {
    // Determine if the pairs of navigational values have been
    // initialized.
    if ( pairs != null && keyValue != null )
    {
      // It has.  Walk the list of pairs looking for a pair which uses
      // keyValue as its key.
      for( int index = 0; index < pairs.size(); index++ )
      {
	// Determine if this pair is a match.
	Pair pair = (Pair) pairs.elementAt(index);
	String id = pair.getKey().toString();
	if ( id != null && id.equals( keyValue.toString() ) )
	{
	  // There is a match.  Select this row.
	  rowComboBox.setSelectedIndex( index );
          idTextField.setText(pair.getValue().toString());
	  break;
	}
      }
    }
  }

  /****************************************************************************/
  /**
   * Set the range of values based on the database content.
   */

  public void setSelectedRow( int rowIndex )
  {
    // Assert: 0 <= rowIndex <= rowComboBox.getItemCount()
    rowComboBox.setSelectedIndex( rowIndex );
  }

  /****************************************************************************/

}

/******************************************************************************/
