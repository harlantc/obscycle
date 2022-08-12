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

import gov.sao.asc.obsed.constraint.ConstraintViolationException;
import gov.sao.asc.obsed.database.AlreadyExistsException;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.IngestedException;
import gov.sao.asc.obsed.database.NewRecordResult;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.database.RecordInsertionException;
import gov.sao.asc.obsed.database.UpdateFailedException;
import gov.sao.asc.obsed.view.ChangeList;
import gov.sao.asc.obsed.view.ComponentEntry;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.ControlPanel;
import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.obsed.view.event.ChangeListEvent;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LogClient;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

/******************************************************************************/
/**
 * Shows the target table from the axafocat database in a Swing JTable.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class NewView extends FormView
  implements ActionListener, ChangeListListener
{
  /**
   * Button to apply the new record creation.
   */
  private JButton applyButton;

  // A flag which if true means the view can be refreshed.
  protected boolean canRefresh;

  // The panel containing the GUI controls.
  protected JPanel form;

  // The control panel
  protected JPanel newControlPanel;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public NewView()
  {
    super();

    // Use a private change list for the NewView object.
    changeList = new ChangeList( database.getDatabaseModel() );
    changeList.addChangeListListener( this );

    suffix = "NewView.xml";

    navPanel = null;
  }

  /****************************************************************************/
  /**
   * Handle a control button action: Apply or Dismiss.
   */

  public void actionPerformed( ActionEvent event )
  {
    String command = event.getActionCommand();

    if ( command.equals( "Apply" ) )
    {
      processApply();
    }
    else if ( command.equals( "Dismiss" ) )
    {
      processDismiss();
    }
  }

  /****************************************************************************/
  /**
   *  Return the refresh capability for this view.
   */

  public boolean canRefresh()
  {
    return canRefresh;
  }

  /****************************************************************************/
  /**
   * This method is responding to change on the private NewView change
   * list.  It will enable the apply button if the user has made any
   * changes since the last apply.  Unlike other views, the NewView
   * does not care about what is changing on any other views.  It has
   * its own independent change list and responds to events only on
   * that change list.
   */

  public void changeListChanged( ChangeListEvent event )
  {

    Object keyValue = getSelectedKeyValue();

    // If change list is dirty for this key value in *any* table, then
    // set the apply button to enabled...  Else, clear the apply button.
    applyButton.setEnabled( changeList.isDirty( keyValue ) ); 
  }

  /****************************************************************************/
  /**
   * The new view descendants will not be creating new record views so
   * stub a nop.
   */

  public void createNewRecordView()
  {
  }

  /****************************************************************************/

  public void delete()
  {
  }

  /****************************************************************************/
  /**
   * Return the currently selected record in this view.
   */

  public Object getSelectedKeyValue()
  {
    return newKeyValue;
  }

  /****************************************************************************/
  /**
   *  Do some one-time initialization.
   */

  public void init() 
  {
    // Create the controller for this view.
    controller = new FormViewController( this );

    // Create the configuration object and force it to make the
    // component entries mutable.
    configuration = new FormViewConfiguration( viewName, controller );
    configuration.setForceMutable( true );

    // Create the data model.
    model = new FormViewModel( this, (FormViewConfiguration) configuration );

    // Finish setting up the controller.
    controller.setConfiguration( (FormViewConfiguration) configuration );
    controller.setModel( (FormViewModel) model );
    changeList.addChangeListListener( controller );

    // Create the control panel.
    newControlPanel = new JPanel();
    newControlPanel.setLayout( new FlowLayout() );
    newControlPanel.setBorder( new TitledBorder( "Controls:" ) );

    // Setup the Apply button.  Initialize it disabled to force the
    // User to select new values.
    applyButton = new JButton( "Apply" );
    applyButton.addActionListener( this );
    Properties properties = editor.getProperties();
    String toolTipText =
      properties.getProperty( "NewView.applyButton.toolTipText" );
    applyButton.setToolTipText( toolTipText );
    applyButton.setEnabled( false );

    // Setup the Dismiss button.
    JButton dismissButton = new JButton( "Dismiss" );
    dismissButton.addActionListener( this );
    toolTipText = 
      properties.getProperty( "NewView.dismissButton.toolTipText" );
    dismissButton.setToolTipText( toolTipText );

    // Add the buttons  to the control panel.
    newControlPanel.add( applyButton );
    newControlPanel.add( dismissButton );

    // Update the screen.
    refresh();

    // Popup a message dialog to coax the User to modify a value to
    // enable the "Apply" button.
    String message = "Modify at least one value to enable the APPLY button.";
    String title = "New View Help Dialog";
    JOptionPane.showMessageDialog( null, message, title, 
				   JOptionPane.WARNING_MESSAGE );
  }

  /****************************************************************************/
  /**
   * Insert a new record into the database.
   */

  public Vector newRecord()
    throws RecordInsertionException
  {
    Object result = null;
    DatabaseKey dbKey = configuration.getKeyEntry();
    String tableName = dbKey.getTableName();

    // Insert a new record and return the new record's value.
    return database.createNewRecord( tableName, newKeyValue );
  }

  /****************************************************************************/
  /**
   * Attempt to create a new record based on values in the form
   * backing this view.  Three things can happen.  The creation
   * succeeds, the new record already exists in the table (no new
   * record is creeated) or the creation fails.  On failure a record
   * insertion exception is thrown.
   */

  public void processApply()
  {
    boolean clearChanges = false;
    DatabaseKey dbKey = configuration.getKeyEntry();
    String errorMessage = null;
    Object keyValue;
    String resultMessage = "";
    NewRecordResult result;
    int resultCode = -1;
    String tableName = dbKey.getTableName();
    String columnName = null;

    try
    {
      // Create a new record as long as all the constraints are
      // satisfied.  Extract the new record ID from the insertion
      // results.
      checkConstraints( tableName, newKeyValue );
      Vector results = newRecord();
      result = (NewRecordResult) results.elementAt( 0 );
      Object newRecordKeyValue = result.getNewKeyValue();
      Object oldKeyValue;

      // Process each result.  Most tables have only a single result.
      // The target table can have two results.
      Iterator iterator = results.iterator();
      while ( iterator.hasNext() )
      {
	// Get the table name and result code.
	result = (NewRecordResult) iterator.next();
	tableName = result.getTableName();
	columnName = result.getColumnName();
	resultCode = result.getResultCode();
	keyValue = result.getNewKeyValue();
	oldKeyValue = result.getKeyValue();

        if (columnName == null) {
          columnName = "record ID";
        }

	// Set up the output meesage based on the result codes.
	if ( resultCode >= 0 )
	{
	  // Generate the result message, insure any changes will be
	  // cleared and update interested views.
	  resultMessage += "New entry inserted into the " + tableName +
	      " table with \n  " + columnName + ": " + keyValue + "\n";
	  clearChanges = true;
	  editor.getChangeList().newRecord( tableName );
	}
	else if ( resultCode == -107 )
	{
	  resultMessage += "A match (" + columnName + " " + keyValue +
	    ") was found in the " + tableName + " table and has been reused.\n";
	  clearChanges = true;
	}
	else if ( resultCode == -108 )
	{
	  resultMessage += "Record already exists in the " + tableName +
	    " table at\n  " + columnName + " " + keyValue + ".\n";
	  clearChanges = true;
	}
	else if ( resultCode == -9999 )
	{
	  resultMessage += "An SQL failure occurred trying to insert a " +
	    "new record into the " + tableName + " table.\n";
	  clearChanges = false;
	}
	else if ( resultCode == -1 )
	{
	  // This is a dummy code inserted to make sure that no result
	  // message is generated but that the fields in the table are
	  // updated.  For example, the anc_target field.
	  clearChanges = true;
	}
	else
	{
	  resultMessage += "An unhandled result code (" + resultCode +
	    ") occurred trying to insert a new record into the " +
	    tableName + " table.\n";
	  clearChanges = false;
	}

	if ( editor.isDebug() )
	{
	  System.out.println( "Processed result: " + resultMessage );
	  System.out.println( "   table: " + tableName + ", record ID: " +
			      keyValue + " with result code: " + 
			      resultCode );
	}

	// Determine if the modified fields, if any, should be cleared.
	if ( clearChanges && changeList.isRowDirty( tableName, oldKeyValue ) )
	{
	  // They do.
	  Vector columnNames = 
	    changeList.getColumnsChanged( tableName, oldKeyValue );
	  changeList.clearChanges( tableName, oldKeyValue );
	  repaintBorders( tableName, columnNames );
	}

	// Update the data model from the database.
	DatabaseModel databaseModel =
	  editor.getDatabase().getDatabaseModel();
	databaseModel.reload( tableName );
      }

      // Output the result message.
      JOptionPane.showMessageDialog( this, resultMessage,
				     "Table Insertion Result",
				     JOptionPane.INFORMATION_MESSAGE );

      // Create another entry in the model using the current values
      // and update the view.  Make sure that the key value is
      // initialized to null.
      newKeyValue = model.addRecord( newRecordKeyValue );
      model.setValueAt( null, dbKey, newKeyValue );
      refresh();

      // Gray out the apply button, until the user makes a another
      // change.
      applyButton.setEnabled( false );
    }
    catch ( ConstraintViolationException constraintException )
    {
      errorMessage = constraintException.getMessage();
    }
    catch ( ConfigurationException configurationException )
    {
      errorMessage = configurationException.getMessage();
    }
    catch ( RecordInsertionException recordInsertionException )
    {
      errorMessage = recordInsertionException.getMessage();
    }
    catch ( RecordDoesNotExistException noRecordException )
    {
      errorMessage = noRecordException.getMessage();
      LogClient.printStackTrace( noRecordException );
    }

    // Report on any errors.
    if ( errorMessage != null )
    {
      JOptionPane.showMessageDialog( this, errorMessage,
				     "Insertion Failed",
				     JOptionPane.ERROR_MESSAGE );
    }

  }

  /****************************************************************************/
  /**
   * 
   */

  public void processDismiss()
  {
    // Clear out any pending changes.
    DatabaseKey dbKey = configuration.getKeyEntry();
    String tableName = dbKey.getTableName();
    changeList.clearChanges( tableName, newKeyValue );

    // Remove change list listeners.
    changeList.removeChangeListListener( this );
    changeList.removeChangeListListener( controller );

    editor.removeView( this );
  }

  /****************************************************************************/
  /**
   *  Regenerate the view.  Overload this method to tailor refresh behavior.
   */

  public void refresh()
  {
    // Initialize the row, column indices.
    int columnIndex = 0;
    int rowIndex = 0;

    // Paint the border with some status information.
    setBorder();

    // Set the cursor for possibly long operations.
    showLoadingPanel();
    
    // Process the configuration file and reset the title used by the
    // Editor.
    configuration.reload();
    String title = configuration.getTitle();
    if ( title != null )
    {
      editor.setTitle( this, title );
    }

    // Create the main panel containing the form and add the
    // configured components.
    form = new Form( configuration );

    // ?
    controller.reload();

    // Put the form into a scrollpane and make the scrollpane the main
    // GUI for the view.
    JScrollPane scrollPane = new JScrollPane( form );
    scrollPane.setViewportBorder( new TitledBorder( "Form: " ) );

    // Clean out the loading panel, set up the new view and show it.
    hideLoadingPanel();
    add( scrollPane, BorderLayout.CENTER );
    add( newControlPanel, BorderLayout.SOUTH );

    // Update the values.
    update();

    // Repaint the screen.
    validate();
    repaint();
  }

  /****************************************************************************/
  /** 
   * Save all changes back to the database.
   */

  public void saveAllChanges()
  {

  }

  /****************************************************************************/
  /**
   *  Write back all the changes in the change cache to the database.
   *
   *  Generate an update statement of the form: UPDATE <table> SET
   *  <column> = <value> WHERE <Key-column> = <Key-value>
   */

  public void saveCurrentChanges() 
  {

  }

  /****************************************************************************/
  /**
   * Set the selected key value.
   */

  public void setSelectedKeyValue( Object keyValue )
  {
    newKeyValue = keyValue;
  }

  /****************************************************************************/
  /**
   * Update the record currently selected in the navigation panel.
   */

  public void update()
  {
    controller.updateGUI( newKeyValue );
  }

  /****************************************************************************/
  /**
   * Update an arbitrary record.
   */

  public void update( Object keyValue )
  {
    controller.updateGUI( keyValue );
  }

  /****************************************************************************/

}

/******************************************************************************/
