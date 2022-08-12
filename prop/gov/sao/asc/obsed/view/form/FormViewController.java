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
import gov.sao.asc.obsed.constraint.ConstraintChecker;
import gov.sao.asc.obsed.constraint.ConstraintViolationException;
import gov.sao.asc.obsed.database.AlreadyExistsException;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.database.UpdateFailedException;
import gov.sao.asc.obsed.event.HistoryListener;
import gov.sao.asc.obsed.view.ChangeList;
import gov.sao.asc.obsed.view.ComponentEntry;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.ControlPanel;
import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.obsed.view.event.ChangeListEvent;
import gov.sao.asc.obsed.view.form.ParseViolationException;
import gov.sao.asc.obsed.view.form.event.NavChangeEvent;
import gov.sao.asc.obsed.view.form.event.NavChangeListener;
import gov.sao.asc.obsed.view.form.event.TextChangeListener;
import gov.sao.asc.obsed.view.form.event.MouseInterface;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.TimestampEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.lang.NumberFormatException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.Format;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/******************************************************************************/
/**
 * 
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class FormViewController extends Object
  implements ActionListener, ChangeListListener, NavChangeListener, MouseInterface
{
  private Color borderColor;
  private FormViewConfiguration configuration;

  /**
   * Convenence variable.
   */
  private Editor editor;

  private Color foregroundColor;
  private HistoryListener historyListener;

  /**
   * Maintain the lock state of the current record.
   */
  private boolean recordLocked;

  private Color lockedColor;
  private FormViewModel model;
  private JComponent mouseEventComponent;
  private JPopupMenu popupMenu;
  private boolean updatingGUI;
  private FormView view;

  /****************************************************************************/
  /**
   * Default constructor.
   *
   * @param view The associated FormView object.
   *
   */

  public FormViewController( FormView view )
  {
    this.view = view;
    editor = Editor.getInstance();
    this.historyListener = new HistoryListener( editor );

    // initialize popup menu for `View History' function.
    initPopupMenu();

    // Read colors from properties file and save for later use.  The
    // color properties should be a hex number.
    lockedColor = getColor( "FormViewController.locked.color", Color.red );
    foregroundColor = getColor( "FormViewController.textForeground.color",
				( new Color ( 102, 102, 153 ) ) );
    Color disabledColor = getColor( "FormViewController.disabledForeground.color",
	Color.gray);
    UIManager.put("ComboBox.disabledForeground", disabledColor);
    UIManager.put("TextField.inactiveForeground", disabledColor);
    UIManager.put("TextArea.inactiveForeground", disabledColor);



  }

  /****************************************************************************/
  /**
   * Process a changed component.
   */

  public void actionPerformed( ActionEvent event )
  {
    boolean processChange = false;

    // Use the action command to determine how to handle the event.
    String command = event.getActionCommand();
    if ( "TIMESTAMP".equals( command ) )
    {
      // Process a TIMESTAMPbutton type GUI control.  Popup a dialog
      // box editor to get the various components and assemble them
      // into a date-time string.

      // Get the current value used to seed the panel used to obtain
      // the new values.
      JButton button = (JButton) event.getSource();
      String currentText = button.getText();
      TimestampEditor form = new TimestampEditor( currentText );

      // Popup the dialog.
      String title = "Date/Time Editor Dialog";
      int result = JOptionPane.showConfirmDialog( null, form, title,
				     JOptionPane.OK_CANCEL_OPTION );

      // Process the result.
      String newTimestamp = form.getTimestamp();
      if ( newTimestamp == null )
      {
	newTimestamp = Constants.NONE;
      }

      if ( editor.isDebug() )
      {
	System.out.println( "New timestamp: " + newTimestamp +
			    "; original timestamp: " + currentText );
      }

      if ( result == JOptionPane.OK_OPTION &&
	   !newTimestamp.equals( currentText ) )
      {
	// There exists a new string.  Set it's value into the component.
	button.setText( newTimestamp );
	processChange = true;
      }
    }
    else if ( "OBSID".equals( command ) )
    {
      // Process an ObsID button change.
      JButton button = (JButton) event.getSource();
      String currentText = button.getText();
      ObsIDEditor form = new ObsIDEditor( currentText );

      // Popup the dialog.
      String title = "ObsID Editor Dialog";
      int result = JOptionPane.showConfirmDialog( null, form, title,
				     JOptionPane.OK_CANCEL_OPTION );

      // Process the result.
      String newText = form.getObsID();
      if ( newText == null )
      {
	newText = Constants.DEFAULT;
      }

      if ( editor.isDebug() )
      {
	System.out.println( "New ObsID: " + newText +
			    "; original ObsID: " + currentText );
      }

      if ( result == JOptionPane.OK_OPTION &&
	   !newText.equals( currentText ) )
      {
	// There exists a new string.  Set it's value into the component.
	button.setText( newText );
	processChange = true;
      }
    }
    else if ( "SEQNBR".equals( command ) )
    {
      // Process an ObsID button change.
      JButton button = (JButton) event.getSource();
      String currentText = button.getText();
      SequenceNumberEditor form = new SequenceNumberEditor( currentText );

      // Popup the dialog.
      String title = "Sequence Number Editor Dialog";
      int result = JOptionPane.showConfirmDialog( null, form, title,
				     JOptionPane.OK_CANCEL_OPTION );

      // Process the result.
      String newText = form.getSequenceNumber();
      if ( newText == null )
      {
	newText = Constants.DEFAULT;
      }

      if ( editor.isDebug() )
      {
	System.out.println( "New Sequence Number: " + newText +
			    "; original sequence number: " + currentText );
      }

      if ( result == JOptionPane.OK_OPTION &&
	   !newText.equals( currentText ) )
      {
	// There exists a new string.  Set it's value into the component.
	button.setText( newText );
	processChange = true;
      }
    }
    else
    {
      processChange = true;
    }
      
    // Determine if we should go ahead and process the change.
    if ( processChange == true )
    {
      // Do it.
      processGUIChange( (JComponent) event.getSource() );
    }
  }

  /****************************************************************************/
  /**
   * Handle a change list event.  Simple modifications update the
   * border to convey the status.  Deletion events generate a complete
   * update.
   */

  public void changeListChanged( ChangeListEvent event )
  {
    String tableName = event.getTableName();
    String columnName = event.getColumnName();
    int rowIndex = event.getRowIndex();

    // Handle the event based on the state.
    switch ( event.getState() )
    {
    case ChangeListEvent.ROW_MODIFIED:
      // `Add' events (adding entries to the change list) are only of
      // interest to this view IF the event matches the row in this
      // view.  If not, then the event is not relevant to this view, so
      // just return.  Note that *all* `clear' events are of interest.
      if( event.getAddFlag() )
      {
	// Determine if the current view is looking at the changed record.
	ChangeList changeList = view.getChangeList();
	Object keyColumnValue = changeList.getKeyFor( tableName, rowIndex );
	Object keyValue = getSelectedKeyValue();
	if ( keyValue instanceof Integer &&
	     keyColumnValue.equals( keyValue ) )
	{
	  // It is, but don't process any changes if we are just
	  // updating the GUI from the database.
	  if ( ! updatingGUI )
	  {
	    // Get the component entry.
	    DatabaseKey databaseKey = new DatabaseKey( tableName, columnName );
	    FormComponentEntry componentEntry = 
	      (FormComponentEntry) configuration.getEntry( databaseKey );
	    
	  // Insure that the affected component is in this view.
	    if ( componentEntry != null )
	    {
	      // It is.  Mark the component as modified.
	      componentEntry.paintBorder( ComponentEntry.MODIFIED );
	      
	      // Update the component such that we insure against an
	      // infinite loop.
	      updatingGUI = true;

	      // Update the GUI field with the value from the data model.
	      Object value = model.getValueAt( databaseKey, keyValue );
	      boolean mutable =
		!recordLocked && componentEntry.isMutable( keyValue );
	      updateGUIField( componentEntry, value, mutable );
	      
	      // Re-enable change updating.
	      updatingGUI = false;
	    }
	  }
	}
      }
      break;

    case ChangeListEvent.ROW_ADDED:
    case ChangeListEvent.ROW_DELETED:
      // Ignore updates for NewView views.
      if ( !(view instanceof NewView) )
      { 
	// Determine if the row added or deleted is in the primary table
	// for this view.
	tableName = event.getTableName();
	if ( tableName != null &&
	     tableName.equals( configuration.getKeyEntry().getTableName() ) )
	{
	  // It is.  Reload the navigator and update the view.
	  Navigator navPanel = view.getNavPanel();
	  navPanel.reload();
	  view.update();
	}
	else
	{
	  // Update the view to reload secondary values.
	  view.update();
	}
      }
      break;

    default:
      break;
    }
  }
  
  /****************************************************************************/
  /**
   *  Return component on which `view history' request was made.
   */

  public JComponent getMouseEventComponent()
  {
    return mouseEventComponent;
  }

  /****************************************************************************/
  /**
   * Process a text change.  Cache the fact that the user changed this
   * component.
   */

  public Object getSelectedKeyValue()
  {
    return view.getSelectedKeyValue();
  }

  /****************************************************************************/
  /** 
   * Get a color from the properties file.  Return the default color
   * if there are problems with the color from the properties file.
   * The color property should be a string representing a hex value.
   * The form of the string should be: RRGGBB, where RR is the red
   * component of the color, GG represents the green component and BB
   * represends the blue component.  So, the color red would be
   * represented as: FF0000.
   *
   * @param propertyString The string property name, for which to find
   * the color value.
   * @param defaultColor The default color to use if the requested
   * property color cannot be obtained.
   */
  public Color getColor( String propertyString, Color defaultColor )
  {
    Color returnColor;
    Properties properties = editor.getProperties();
    String colorText = properties.getProperty( propertyString );
    try
    {
      returnColor = new Color ( Integer.parseInt ( colorText, 16 ) );
    }
    catch( NumberFormatException exc )
    {
      returnColor = defaultColor;
    }

    return returnColor;

  }

  /****************************************************************************/

  public FormView getFormView()
  {
    return( view );
  }

  /****************************************************************************/
  /**
   *  Initialize the popup menu invoked from right-mouse-button click.
   */

  public void initPopupMenu()
  {
    popupMenu = new JPopupMenu();
    JMenuItem viewHistoryMenuItem = new JMenuItem( "View History" );
    viewHistoryMenuItem.addActionListener( historyListener );
    popupMenu.add( viewHistoryMenuItem );
  }

  /****************************************************************************/
  /**
   * Return true iff the GUI is being updated from the database.
   */

  public boolean isUpdatingGUI()
  {
    return updatingGUI;
  }

  /****************************************************************************/
  /**
   *  Process mousePressed event.
   */

  public void mousePressed( MouseEvent event )
  {
    mouseEventComponent = ( JComponent ) event.getSource();
    popupMenu.show( mouseEventComponent, event.getX(), event.getY() );
  }

  /****************************************************************************/
  /**
   * Process a combo box change.
   *
   * @param comboBox the combo box component.
   * @param value the new value for the combo box.
   * @param isMutable flag indicates whether combo box is mutable.
   *
   * Make a particular value in a combo box the selected value.  The
   * value null will map to the string "NONE".
   */

  public void processComboBox( ComponentEntry entry, JComboBox comboBox,
			       Object value, boolean mutable )
  {
    String nullString = entry.getNullString();
    String valueStr;

    // Generate the value string representation.
    if ( value == null )
    {
      valueStr = nullString;
    }
    else
    {
      valueStr = value.toString();
    }

    // Walk the elements of the combo box looking for a match with
    // <i>value</i>. 
    int N = comboBox.getItemCount();
    int index = -1;
    for ( int j = 0; j < N; j++ )
    {
      // convert comboBox.getItemAt to a string since it may or
      // may not already be one
      Object obj = comboBox.getItemAt(j);
      String objStr;
      if ( obj == null )
      {
	objStr = nullString;
      }
      else
      {
	objStr = obj.toString();
      }
      
      // Determine if the value to be selected is at the current
      // combo box index.
      if ( objStr.equals( valueStr ) )
      {
	// Found the match.  Make it the selected item and break
	// out of the loop.
	comboBox.setSelectedIndex( j );
	index = j;
	break;
      }
    }
    
    if ( index == -1 )
    {
      // Not found.  Hmmmm.  This should not be.  Log a message and
      // print a stack trace to enable dealing with the bug.
      String message = "Unexpected combo box item: " + valueStr;
      Throwable trace = new Throwable( message );
      LogClient.printStackTrace( trace );
    }

    // If this record is currently locked, disable the combo box.
    if ( mutable )
    {
      comboBox.setEnabled( true );
    }
    else
    {
      comboBox.setEnabled( false );
    }
  }

  /****************************************************************************/
  /**
   * Process a GUI component change.  Cache the fact that the user
   * changed this component by inserting the current component value
   * into the change cache.  Also process key combo boxes by updating
   * any associated GUI elements.  This includes setting the key combo
   * box to new if an associated value is itself modified.
   *
   * @param component The modified GUI widget.
   */

  public void processGUIChange( JComponent component )
  {
    if ( ! updatingGUI )
    {
      updatingGUI = true;

      try
      {
	// Establish the change cache database key.
	FormComponentEntry entry =
	  (FormComponentEntry) configuration.getEntry( component );
	DatabaseKey databaseKey = entry.getDatabaseKey();

	// Generate the record key value for the modified field.  The
	// first step is to determine if the field is a primary cell
	// (the current key value is the record id) or a secondary
	// cell (a chain of indirection must be followed to get the
	// record id).
	Object keyValue;
	Object primaryKeyValue = view.getSelectedKeyValue();
	DatabaseKey joinDatabaseKey = entry.getJoinDatabaseKey();
	if ( joinDatabaseKey == null )
	{
	// The currently selected key value is the answer.
	  keyValue = primaryKeyValue;
	}
	else
	{
	  // Follow the indirection chain to get the right record id.
	  // Use the primary key value to access the join field's value.
	  keyValue = model.getValueAt( joinDatabaseKey, primaryKeyValue );
	}

	// Read the value from the component.
	Object value = entry.getValue();

	// If the view is a new view then skip the changelist
	// processing.
	//if ( ! (view instanceof NewView) )
	{
	  view.getChangeList().addChange( databaseKey, keyValue, value );
	}
	  
	// Mark the component as modified.
	entry.paintBorder( ComponentEntry.MODIFIED );

	// Update joined components if the entry is a keycombobox.
	if ( entry.getType() == Constants.KEYCOMBOBOX )
	{
	  // Detect and convert a null selection (NONE) to the
	  // sentinel value "0".  Then complete the update on the
	  // secondary components.
	  Object joinKeyValue;
	  if ( value == Constants.NONE )
	  {
	    joinKeyValue = new Integer( 0 );
	  }
	  else
	  {
	    joinKeyValue = value;
	  }
	  String joinTableName = entry.getKeyDatabaseKey().getTableName();
	  updateSecondaryComponents( joinTableName, joinKeyValue, keyValue );
	}
      }
      catch (ConfigurationException configurationException)
      {
        LogClient.printStackTrace( configurationException );
      }
      catch ( NumberFormatException numberFormatException )
      {
        // This should be handled before getting to this method.
	LogClient.printStackTrace( numberFormatException );
      }
      catch ( ParseException parseException )
      {
        // This should be handled before getting to this method.
	LogClient.printStackTrace( parseException );
      }
      catch ( ParseViolationException parseViolationException )
      {
      JOptionPane.showMessageDialog( editor.getFrame(),
                                     parseViolationException.getMessage(),
                                     "Parsing Error",
                                     JOptionPane.ERROR_MESSAGE );


      }

      updatingGUI = false;
    }
  }

  /****************************************************************************/
  /**
   * Re-initialize the controller.  Accept a new navigotor and clear
   * the database types information.
   */

  public void reload()
  {
  }

  /****************************************************************************/
  /**
   * Set the configuration object specifying this view.
   */

  public void setConfiguration( FormViewConfiguration configuration )
  {
    this.configuration = configuration;
  }

  /****************************************************************************/
  /**
   * Set the model object specifying this view.
   */

  public void setModel( FormViewModel model )
  {
    this.model = model;
  }

  /****************************************************************************/
  /**
   * Set to true to indicate that the form is being updated from the
   * database and changes to GUI components can be ignore.
   */

  public void setUpdatingGUI( boolean state )
  {
    updatingGUI = state;
  }

  /****************************************************************************/
  /**
   *  Handle a navigation change event.  These occur whenever the
   *  navigator is selecting another observation to display.
   */

  public void stateChanged( NavChangeEvent event )
  {
    // A new record has been selected.  Update the state of the locked
    // flag.
    Object keyValue = event.getKeyValue();
    String tableName = event.getTableName();
    Database database = editor.getDatabase();
    recordLocked = database.isRowLocked( tableName, keyValue );
    view.setLocked( recordLocked );
    updateGUI();
  }

  /****************************************************************************/
  /**
   *  Update the values in the GUI components using the currently
   *  selected key value for this view.
   */

  public void updateGUI()
  {
    // Simply update the GUI
    Object keyValue = view.getSelectedKeyValue();
    updateGUI( keyValue );
  }

  /****************************************************************************/
  /**
   * Update the GUI components for this form with the value from the
   * data model.
   *
   * @param keyValue  A record ID.
   */

  public void updateGUI( Object primaryKeyValue )
  {
    if ( editor.isDebug() )
    {
      System.out.println( "Updating record at " + primaryKeyValue );
    }

    try
    {
      ChangeList changeList;
      ColumnEntry columnEntry;
      FormComponentEntry componentEntry;
      JComponent component;
      DatabaseKey databaseKey;
      DatabaseKey joinKey;
      Object keyValue = null;

      // Update the values in the form from the database, but disable
      // change listener handling.
      updatingGUI = true;

      // Walk the entries on this form.
      Vector entries = configuration.getComponentEntries();
      Iterator iterator = entries.iterator();
      while ( iterator.hasNext() )
      {
	// Fetch a GUI component.  Filter out GUI controls not backed
	// by the database.
	componentEntry = (FormComponentEntry) iterator.next();
	databaseKey = componentEntry.getDatabaseKey();
        if ( editor.isDebug() )
        {
          System.out.println( "Updating component check: " +
                                componentEntry.getTag());
        }


        // Set the border of the component to an empty border.
        componentEntry.paintBorder( ComponentEntry.OPAQUE );

	// Update the GUI field as long as it is backed by the
	// database.
	if ( databaseKey != null )
	{
	  // Determine the DB key value.
	  joinKey = componentEntry.getJoinDatabaseKey();
	  if ( joinKey == null )
	  {
	    keyValue = primaryKeyValue;
	  }
	  else
	  {
	    keyValue = model.getValueAt( joinKey, primaryKeyValue );
	  }

	  // Repaint the border if the cell has been modified.
	  changeList = view.getChangeList();
	  if ( changeList.isCellDirty( databaseKey, keyValue ) )
	  {
	    // Repaint the border.
	    componentEntry.paintBorder( ComponentEntry.MODIFIED );
	  }

	  // Update the GUI field with the value from the data model.
	  Object value = model.getValueAt( databaseKey, keyValue );
	  boolean mutable =
	    !recordLocked && componentEntry.isMutable( primaryKeyValue );
	  if ( editor.isDebug() )
	  {
	    System.out.println( "Updating component: " +
				componentEntry.getTag() +
				" for record " + keyValue +
				" with value " + value +
				"; mutable: " + mutable );
	  }
	  updateGUIField( componentEntry, value, mutable );
	}
      }
    }
    catch ( Exception exc )
    {
      // catch anything.
      LogClient.printStackTrace( exc );
    }

    // Enable normal processing of user generated changes.
    updatingGUI = false;
  }

  /****************************************************************************/
  /**
   *  Update the value in a particular GUI component.
   *
   * @param guiType  The type of gui component that was defined in the
   *                 FormView.xml file.
   * @param component  The swing component for this field.
   * @param sqlType  One of the constants in java.sql.Types.
   * @param value  The value to be displayed by the component.
   * @param displayFormat  The format that the value is to be displayed in.
   * @param isMutable Flag indicating whether the field is mutable.
   */

  private void updateGUIField( FormComponentEntry componentEntry,
			       Object value, boolean mutable )
  {
    ColumnEntry columnEntry;
    int sqlType;

    try
    {
      // Set the color on the component label.
      JLabel label = componentEntry.getLabel();
      if( label != null )
      {
	Color color = mutable ? foregroundColor : lockedColor;
	label.setForeground( color );
      }

      // Convert the value to a suitable string.
      String valueStr;
      if ( value instanceof String && value != null )
      {
	valueStr = (String) value;
      }
      else if ( value == null )
      {
	valueStr = "";
      }
      else
      {
	valueStr = value.toString();
      }

      // Enable the component if it is modifiable.
      JComponent comp = componentEntry.getViewableComponent();
      comp.setEnabled( mutable );

      // Process the component based on its SQL and GUI type
      // classifications.
      columnEntry = componentEntry.getColumnEntry();
      sqlType = columnEntry.getSQLType();
      switch ( componentEntry.getType() )
      {
      case Constants.TIMESTAMPBUTTON:
	JButton button = (JButton) comp;
	if ( value == null )
	{
	  button.setText( Constants.NONE );
	}
	else
	{
	  button.setText( valueStr );
	}
	break;

      case Constants.OBSIDBUTTON:
	button = (JButton) comp;
	if ( value == null )
	{
	  button.setText( Constants.DEFAULT );
	}
	else
	{
	  button.setText( valueStr );
	}
	break;

      case Constants.SEQNBRBUTTON:
	button = (JButton) comp;
	button.setText( valueStr );
	break;

      case Constants.KEYCOMBOBOX:
      case Constants.COMBOBOX:
	{
	  // Prepare a combo box to select value and paint it based on
	  // whether or not it is mutable.
	  processComboBox( componentEntry, (JComboBox) comp,
			   value, mutable );
	}
	break;

      case Constants.TEXTAREA:
	JScrollPane pane = (JScrollPane) comp;
	JTextArea textArea = (JTextArea) pane.getViewport().getView();
	textArea.setEnabled( mutable );
    
	switch ( sqlType )
	{
	default:
	case Types.CHAR:
	case Types.VARCHAR:
	  textArea.setText( valueStr );
	  break;
	}
	break;
	
      case Constants.TEXTFIELD:
	JTextField textField = (JTextField) comp;

	switch ( sqlType )
	{
	default:
	case Types.CHAR:
	case Types.VARCHAR:
	case Types.INTEGER:
	  textField.setText( valueStr );
	  break;

	case Types.FLOAT:
	case Types.DOUBLE:
	  if ( value != null )
	  {
	    DecimalFormat formatter =
	      (DecimalFormat) componentEntry.getDisplayFormat();
	    if ( formatter != null )
	    {
	      valueStr = formatter.format( (Double) value );
	    }
	  }
	  textField.setText( valueStr );
	  break;
	}
	break;
	
      case Constants.TRIPLEBOX:
	{
	  JComboBox comboBox = (JComboBox) comp;

	  if ( value == null )
	  {
	    comboBox.setSelectedItem( Constants.NULL );
	  }
	  else if ( valueStr.equalsIgnoreCase( "Y" ) )
	  {
	    comboBox.setSelectedItem( Constants.YES );
	  }
	  else if ( valueStr.equalsIgnoreCase( "N" ) )
	  {
	    comboBox.setSelectedItem( Constants.NO );
	  }
	  else
	  {
	    // Not found.  Hmmmm.  This should not be.  Log a message
	    // and add the item to the combo box, making it the
	    // selected item.
	    LogClient.logMessage( "New triple box item: " + valueStr );
	    comboBox.addItem( valueStr );
	    comboBox.setSelectedItem( valueStr );
	  }
	}
	break;

      case Constants.PREFERREDBOX:
	{
	  JComboBox comboBox = (JComboBox) comp;

	  if ( value == null )
	  {
	    comboBox.setSelectedItem( Constants.NULL );
	  }
	  else if ( valueStr.equalsIgnoreCase( "Y" ) )
	  {
	    comboBox.setSelectedItem( Constants.YES );
	  }
	  else if ( valueStr.equalsIgnoreCase( "P" ) )
	  {
	    comboBox.setSelectedItem( Constants.PREFERRED );
	  }
	  else if ( valueStr.equalsIgnoreCase( "N" ) )
	  {
	    comboBox.setSelectedItem( Constants.NO );
	  }
	  else
	  {
	    // Not found.  Hmmmm.  This should not be.  Log a message
	    // and add the item to the combo box, making it the
	    // selected item.
	    LogClient.logMessage( "New preferred box item: " + valueStr );
	    comboBox.addItem( valueStr );
	    comboBox.setSelectedItem( valueStr );
	  }
	}
	break;

      default:
	break;
      }
    }
    catch ( ConfigurationException ce )
    {
      LogClient.printStackTrace( ce );
    }
  }

  /****************************************************************************/
  /**
   * Update GUI components that belong to secondary components.
   *
   */

  public void updateSecondaryComponents( String joinTableName, 
					 Object joinKeyValue,
					 Object keyValue )
  {
    // Get the database model.
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();

    // Walk the configuration entries updating components from the
    // same table.
    FormComponentEntry componentEntry;
    DatabaseKey databaseKey;
    Vector entries = configuration.getComponentEntries();
    Iterator iterator = entries.iterator();
    while ( iterator.hasNext() )
    {
      // Fetch a component entry.  Filter out GUI controls not
      // in the same table as the key combo box.
      componentEntry = (FormComponentEntry) iterator.next();
      databaseKey = componentEntry.getDatabaseKey();
      if ( databaseKey != null &&
	   databaseKey.getTableName().equals( joinTableName ) &&
	   databaseModel.hasRecord( joinTableName, joinKeyValue ) )
      {
	// Set the border of the component to an empty border.
	componentEntry.paintBorder( ComponentEntry.OPAQUE );
	  
	// Update the GUI component with the component's new value.
	Object value = model.getValueAt( databaseKey, joinKeyValue );
	boolean mutable = componentEntry.isMutable( keyValue );
	updateGUIField( componentEntry, value, mutable );
      }
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
