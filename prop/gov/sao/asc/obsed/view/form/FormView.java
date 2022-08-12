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
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.IngestedException;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.database.UpdateFailedException;
import gov.sao.asc.obsed.view.ChangeList;
import gov.sao.asc.obsed.view.ComponentEntry;
import gov.sao.asc.obsed.view.Configuration;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.HistoryViewer;
import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.event.ChangeListEvent;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.obsed.view.event.ControlEvent;
import gov.sao.asc.obsed.view.event.ControlListener;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LogClient;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalComboBoxButton;

/******************************************************************************/
/**
 * Shows the target table from the axafocat database in a form view.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class FormView extends View implements ControlListener
{

  protected FormViewController controller;

  // A flag which if true means the view can be refreshed.
  protected boolean canRefresh;

  // The panel containing the GUI controls.
  protected JPanel form;

  // The navigation object.
  protected Navigator navPanel;

  // When the editor is initialized, a key value is specified.  But at
  // view creation time there is no nav panel in which to set the
  // initial key value.  `pendingKeyValue' provides a placeholder for
  // this value.  Additionally, during a refresh, the currently
  // selected key value (if any) needs to be saved.  `pendingKeyValue'
  // is used for this as well.  This hack represents a debatable
  // design flaw.  A more effective design is tbd.
  protected Object pendingKeyValue;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public FormView()
  {
    super();
    suffix = "FormView.xml";
    pendingKeyValue = null;
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
   * Implement controlCommandPerformed to satisfy ControlListener
   * interface.  Update the view on control events.  For `save all'
   * and `cancel all' always update the view.  For `save' and `cancel'
   * only update if the current view is displaying the same record as
   * this view.
   */

  public void controlCommandPerformed( ControlEvent event )
  {
    String command = event.getControlCommand();
    if ( command.equals( "CANCEL_ALL" ) || command.equals( "SAVE_ALL" ) )
    {
      update();
    }
    else
    if ( command.equals( "CANCEL" ) || command.equals( "SAVE" ) )
    {
      JTabbedPane tabbedPanel = editor.getTabbedPanel();
      Component c = tabbedPanel.getSelectedComponent();
      // If this view is the current view, just update it.
      if ( c.equals( this ) )
      {
	update();
      }
      else
      {
	// See if this view has same record as current view; if so,
	// update this view.
	if ( c instanceof gov.sao.asc.obsed.view.form.FormView )
	{
	  Object currentKeyValue = ((FormView) c).getSelectedKeyValue();
	  Object keyValue = getSelectedKeyValue();
	  if ( keyValue.equals( currentKeyValue ) )
	  {
	    update();
	  }
	}
      }
    }
    else
    {
      return;
    }
  }

  /****************************************************************************/
  /**
   * Create a new view that will be initialized to a set of values.
   * if the user selects OK, the currently selected values will be
   * used to (possibly) insert a new record into the associated table.
   */

  public void createNewRecordView()
  {
    // Force the NewView class to be compiled at build time.
    NewView dummy; 

    // Create a new view to deal with creating a new record.
    editor.createView( "gov.sao.asc.obsed.view.form.NewView",
		       viewName, newKeyValue );
  }

  /****************************************************************************/
  /**
   * Delete the selected row from the database.
   */
  public void delete()
  {
    // Determine that the User really wants to delete this row.
    int confirmation = 
      JOptionPane.showConfirmDialog(editor.getFrame(), 
                                    "Are you sure you want to delete the " +
                                    "row from the database?",
                                    "Delete Confirmation",
                                    JOptionPane.OK_CANCEL_OPTION);
    if (confirmation == JOptionPane.OK_OPTION)
    {
      try
      {
	//  Delete the row and update the nav panel.
        DatabaseKey keyEntry = configuration.getKeyEntry();
	String tableName = keyEntry.getTableName();
	String columnName = keyEntry.getColumnName();
	Object keyValue = navPanel.getKeyValue();
        database.deleteRow( tableName, columnName, keyValue );

	// Use the change list object to notify other views of the
	// deletion.
	changeList.delete( tableName, keyValue );
      }
      catch (IngestedException ingestedException)
      {
        JOptionPane.showMessageDialog(editor.getFrame(),
                                      ingestedException.getMessage(),
                                      "Delete Failed",
                                      JOptionPane.INFORMATION_MESSAGE);
      }
      catch (Exception exception)
      {
        JOptionPane.showMessageDialog(editor.getFrame(),
                                      exception.getMessage(),
                                      "Delete Failed",
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  /****************************************************************************/
  /**
   * Return the currently selected record in this view.  Return null
   * if the navPanel is not yet constructed.
   */

  public Object getSelectedKeyValue()
  {
    if ( navPanel == null )
    {
      return null;
    }
    else
    {
      return navPanel.getKeyValue();
    }
  }

  /****************************************************************************/
  /**
   * Return the nav panel object.
   */

  public Navigator getNavPanel()
  {
    return navPanel;
  }

  /****************************************************************************/
  /**
   *  Do some one-time initialization.
   */

  public void init() 
  {
    super.init();

    controller = new FormViewController( this );

    // Create the configuration object.
    configuration = 
      new FormViewConfiguration( viewName, (FormViewController) controller );

    model = new FormViewModel( this, (FormViewConfiguration) configuration );

    // Finish setting up the controller.
    controller.setConfiguration( (FormViewConfiguration) configuration );
    controller.setModel( (FormViewModel) model );

    // set up the change list listeners
    changeList.addChangeListListener( controlPanel );
    changeList.addChangeListListener( editor.getViewMenu() );
    changeList.addChangeListListener( controller );

    // set up the control event listener
    editor.addControlListener( this );

    // Update the screen.
    refresh();
  }

  /****************************************************************************/
  /**
   *  Regenerate the view.  Overload this method to tailor refresh behavior.
   */

  public void refresh()
  {
    // Paint the border with some status information.
    setBorder();

    // Set the cursor for possibly long operations.
    showLoadingPanel();

    // Process the configuration file and reset the title used by the
    // Editor on the currently selected tab.
    configuration.reload();
    String title = configuration.getTitle();
    if ( title != null )
    {
      editor.setTitle( this, title );
    }

    // Decorate the control panel.
    controlPanel.refresh();

    // Reload the model.
    model.reload();

    // Update any modified cells from the change list by looping
    // through the set of changed elements.
    if ( navPanel != null )
    {
      DatabaseKey dbKey = configuration.getKeyEntry();
      String tableName = dbKey.getTableName();
      Object keyValue = navPanel.getKeyValue();
      Vector<String> changes = changeList.getColumnsChanged( tableName, keyValue );
      Iterator i = changes.iterator();
      while ( i.hasNext() )
      {
	// Get the value from the change list and set it into the data
	// model.
	String columnName = (String) i.next();
	Object changedValue = 
	  changeList.getValueAt( tableName, keyValue, columnName );
	DatabaseKey databaseKey = new DatabaseKey( tableName, columnName );
	try
	{
	  model.setValueAt( changedValue, databaseKey, keyValue );
	}
	catch ( RecordDoesNotExistException exc )
	{
	  // This exception should not occur!
	  System.err.println( "Unexpected exception during refresh operation" );
	}
      }
    }

    // Reset the controller.
    controller.reload();

    // Start up the navigator iff there are any rows to navigate.
    JPanel panel;
    if ( getNumberOfRecords() > 0 )
    {
      // If a nav panel exists, capture the current key value to use
      // in seeding the new nav panel instance.
      if ( navPanel != null )
      {
	pendingKeyValue = navPanel.getKeyValue();
      }
	
      // Construct the navigator and set up the event handlers.
      navPanel = new Navigator( (FormViewConfiguration) configuration );
      navPanel.addChangeListener( (FormViewController) controller );
      navPanel.addChangeListener( controlPanel );
      navPanel.addChangeListener( editor.getViewMenu() );

      // Re-start the navigator and add it to the view.  As part of
      // the navigator initialization, force an update to the GUI.
      navPanel.init();
      navPanel.setSelectedKeyValue( pendingKeyValue );
      controller.updateGUI();
      panel = navPanel;

      // Create the main panel containing the form and add the
      // configured components.
      form = new Form( configuration );
    }
    else
    {
      // Use the navigator to inform the user that there are no
      // records to display. Use a blank panel for the form.
      panel = new JPanel();
      panel.setBorder( new TitledBorder( "Navigator: " ) );
      panel.add( new JLabel( "No records to navigate." ) );
      form = new JPanel();
    }

    // Put the form into a scrollpane and make the scrollpane the main
    // GUI for the view.
    JScrollPane scrollPane = new JScrollPane( form );
    scrollPane.setViewportBorder( new TitledBorder( "Form: " ) );

    // Clean out the loading panel, set up the new view and show it.
    hideLoadingPanel();
    add( panel, BorderLayout.NORTH );
    add( scrollPane, BorderLayout.CENTER );
    add( controlPanel, BorderLayout.SOUTH );

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
    // Used to jump to the record containing the problem if an
    // exception occurs.
    Object keyColumnValue = null;

    try
    {
      Enumeration tableNames = changeList.getTablesChanged();

      while ( tableNames.hasMoreElements() )
      {
        String tableName = (String) tableNames.nextElement();

        Enumeration rows = changeList.getRowsChanged( tableName );

        while ( rows.hasMoreElements() )
        {
          Integer rowKey = (Integer) rows.nextElement();

          int rowIndex = rowKey.intValue();

          keyColumnValue = changeList.getKeyFor( tableName, rowIndex );

          checkConstraints( keyColumnValue );

          // Walk the entries in the change cache for <i>keyColumnValue</i>
          Vector<String> columns = changeList.getColumnsChanged( tableName, rowIndex );

          String keyColumnName = 
            DatabaseConfiguration.getInstance().getKeyColumnName( tableName );

          database.update( tableName, columns, keyColumnName, keyColumnValue );

          // Remove the changes that have been written back to the database.
	  changeList.clearChanges( tableName, keyColumnValue );
        }
      }
    }
    catch (ConfigurationException configurationException)
    {
      JOptionPane.showMessageDialog( editor.getFrame(), 
                                     configurationException.getMessage(), 
                                     "Configuration Error",
                                     JOptionPane.ERROR_MESSAGE );

      LogClient.printStackTrace( configurationException );
    }
    catch (ConstraintViolationException constraintViolationException)
    {
      JOptionPane.showMessageDialog( editor.getFrame(), 
                                     constraintViolationException.getMessage(), 
                                     "Constraint Error",
                                     JOptionPane.ERROR_MESSAGE );

      DatabaseKey databaseKey = constraintViolationException.getDatabaseKey();

      // Jump to the record containing the problem.
      navPanel.setSelectedKeyValue( keyColumnValue );

      LogClient.printStackTrace( constraintViolationException );
    }
    catch (UpdateFailedException updateFailedException)
    {
      JOptionPane.showMessageDialog( editor.getFrame(), 
                                     updateFailedException.getMessage(), 
                                     "Database Update Error",
                                     JOptionPane.ERROR_MESSAGE );

      LogClient.printStackTrace( updateFailedException );
    }
  }

  /****************************************************************************/
  /**
   *  Write back all the changes in the change cache to the database.
   *
   *  Generate an update statement of the form: UPDATE <table> SET
   *  <column> = <value> WHERE <Key-column> = <Key-value>
   */

  public void saveCurrentChanges() 
    throws ConfigurationException, ConstraintViolationException, 
    UpdateFailedException
  {
    Object keyColumnValue = navPanel.getKeyValue();

    saveCurrentChanges( keyColumnValue );
    update( keyColumnValue );
  }

  /****************************************************************************/
  /** 
   * Save the changes for the selected row back to the database.
   *
   * @param keyColumnValue The row ID for the primary table.
   */

  public void saveCurrentChanges( Object keyColumnValue )
    throws ConfigurationException, ConstraintViolationException, 
           UpdateFailedException
  {
    Hashtable<String,Vector<Object>> tableCache = new Hashtable<String,Vector<Object>>();
    Vector<Object> rowCache;
    ComponentEntry entry;
    DatabaseKey primaryKey;
    DatabaseKey secondaryKey;
    String tableName;
    String columnName;
    Object keyValue;
    Vector<String> columnNames;

    if ( editor.isDebug() )
    {
      System.out.println( "Saving changes for " + keyColumnValue );
    }

    // Walk the set of components to build a hashtable holding the
    // entries to be written back to the database on a per table
    // basis.
    Vector<ComponentEntry> entries = configuration.getComponentEntries();
    Iterator i = entries.iterator();
    while ( i != null && i.hasNext() )
    {
      // Determine if this entry is backed by the database.
      entry = (ComponentEntry) i.next();
      primaryKey = entry.getDatabaseKey();
      secondaryKey = entry.getJoinDatabaseKey();
      if ( primaryKey != null )
      {
	// It is.  Determine if the component is a primary or
	// secondary component.
	if ( secondaryKey != null )
	{
	  // It is a secondary component.  Compute the indirected key
	  // value.
	  keyValue = model.getValueAt( secondaryKey, keyColumnValue );
	}
	else
	{
	  // It is a primary component.  Use the give key value.
	  keyValue = keyColumnValue;
	}

	// Determine if the given cell is dirty.
	if ( changeList.isCellDirty( primaryKey, keyValue ) )
	{
	  // It is.  Get the value and encache the table, row and set
	  // of columns changed.  Note that this only needs to be done
	  // once per table because there can be only one key value
	  // associated with a given table and all the modified
	  // columns are inserted simultaneously.
	  tableName = primaryKey.getTableName();
	  if ( !tableCache.containsKey( tableName ) )
	  {
	    // Create a data collector for this table using the
	    // current key value.
	    columnNames =
	      changeList.getColumnsChanged( tableName, keyValue );
	    rowCache = new Vector<Object>();
	    rowCache.add( (Object)keyValue );
	    rowCache.add( columnNames );
	    tableCache.put( tableName, rowCache );
	  }
	}
      }
    }

    // Now use the table cache to update the database.
    Set keySet = tableCache.keySet();
    i = keySet.iterator();
    while ( i != null && i.hasNext() )
    {
      // Check the constraints on this table at the given row.
      tableName = (String) i.next();
      DatabaseConfiguration config = DatabaseConfiguration.getInstance();
      String keyColumnName = config.getKeyColumnName( tableName );
      rowCache = (Vector<Object>) tableCache.get( tableName );
      keyValue = rowCache.elementAt( 0 );
      checkConstraints( tableName, keyValue );

      // Update this table.
      columnNames = (Vector<String>)(rowCache.elementAt(1 ));

      database.update( tableName, columnNames, keyColumnName, keyValue );

      // Remove the changes that have been written back to the database.
      changeList.clearChanges( tableName, keyValue );
    }
  }

  /****************************************************************************/
  /**
   * Record the fact that a record is locked (just let the navigation
   * panel know so it can update itself).
   *
   * @param locked Indicates whether or not the current record is
   * locked in the database.
   */

  public void setLocked( boolean locked )
  {
    navPanel.setLocked( locked );
  }

  /****************************************************************************/
  /**
   * Select a new record.
   *
   * @param keyValue  The record ID.
   */

  public void setSelectedKeyValue( Object keyValue )
  {
    if ( navPanel != null )
    {
      navPanel.setSelectedKeyValue( keyValue );
    }
    else
    {
      pendingKeyValue = keyValue;
    }
  }

  /****************************************************************************/
  /**
   * Update the record currently selected in the navigation panel.
   */

  public void update()
  {
    Object record = navPanel.getKeyValue();
    ((FormViewController) controller).updateGUI( record );
  }

  /****************************************************************************/
  /**
   * Update an arbitrary record.
   */

  public void update( Object record )
  {
    ((FormViewController) controller).updateGUI( record );
  }

  /****************************************************************************/
  /**
   * Show history for the current GUI field.
   */

  public void showHistory(int ival)
  {
    String columnName = null;
    String title = null;
    Integer keyInteger;

    // This FormView object can be used to find the keyValue needed to
    // populate the history object.
    String keyValue = getSelectedKeyValue().toString();

    if (keyValue.indexOf("Object") < 0 ) {
      keyInteger = new Integer(keyValue.toString());

    

     if (ival == 0) {
      // Get the GUI component on which the history has been requested
      // (saved when the mouse event occurred).
      JComponent historyComponent = controller.getMouseEventComponent();

      // Determine if the component is a MetalComboBoxButton, and if so,
      // find it's parent combo box.  The parent combo box is needed
      // because it is what corresponds to a field in the database (for
      // which we can get history information).
      if ( historyComponent instanceof javax.swing.plaf.metal.MetalComboBoxButton )
      {
        JComboBox jcb = ((MetalComboBoxButton) historyComponent).getComboBox();
        historyComponent = ( JComponent ) jcb;
      }

      // If the component is a JTextArea, then find it's parent's parent
      // container, which should be a JScrollPane, since the
      // FormViewConfiguration.processTextArea method wraps a JTextArea
      // in a JScrollPane (which puts the JTextArea into a JViewPort
      // which is a child of the JScrollPane), and saves the JScrollPane
      // as the FormViewComponent's GUI component.  However, the mouse
      // event that initiates this history function occurs on the
      // JTextArea...
      if ( historyComponent instanceof javax.swing.JTextArea )
      {
        Container parent = historyComponent.getParent().getParent();
        if( parent instanceof JScrollPane )
        {
          // doesn't seem to be needed jan 04
	  //historyComponent = (JComponent) parent;
        }
      }
      // Get the FormComponentEntry object associated with the given GUI
      // component.
      FormComponentEntry entry =
      (FormComponentEntry) configuration.getEntry( historyComponent );

      // The FormComponentEntry can be used to find the database key and
      // from that the associated column name for the component.  The
      // FormComponentEntry is also used to find the associated label
      // for the GUI component, from which can derive the title for the
      // history view.
      if (entry != null) {
        columnName = entry.getDatabaseKey().getColumnName();

        // Strip out the colon from the label, and replace with a space,
        // trim and make it the title for the history view.
        title = entry.getLabel().getText().replace( ':', ' ');
      }
     }
     else {
       String tmpStr = getNavPanel().getKeyTableName();
       if (tmpStr.equalsIgnoreCase("target")) {
         columnName = "%";
         title = "Observation " + keyValue;
       }
     }
    }
    else {
      keyInteger = new Integer(0);
    }


    // Now go show the history viewer.
    if (title != null)  {
      new HistoryViewer( title, columnName, keyInteger );
    }
    else {
      String tmpStr = "History not available for current form or field.";
      JOptionPane.showMessageDialog(editor.getFrame(),
                                      tmpStr, "",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
