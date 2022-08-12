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

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.constraint.ConstraintChecker;
import gov.sao.asc.obsed.constraint.ConstraintViolationException;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.Filter;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.database.TableConfiguration;
import gov.sao.asc.obsed.database.UpdateFailedException;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.Pair;
import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/******************************************************************************/
/**
 * Shows a record from the axafocat database in a Swing JPanel.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public abstract class View extends JPanel
{
  protected ChangeList changeList;
  protected Configuration configuration;
  protected ControlPanel controlPanel;
  protected Database database;
  protected Filter filter;
  protected ViewModel model;

  // The view name is used to establish the configuration, etc.
  protected String viewName;

  private JPanel loadingPanel;

  protected Editor editor;

  /**
   * The view presented to the user before the current view was presented.
   */
  protected View invokingView;

  /**
   * The key value used to access the seeded data when creating a new
   * record.
   */
  protected Object newKeyValue;

  /**
   * The suffix used to form a view configuration file name.
   */
  protected String suffix;

  /****************************************************************************/
  /**
   * Set the configuration file and optionally register the file as
   * refreshable.
   */

  public View()
  {
    // Generate convenience variables for derived classes.
    editor = Editor.getInstance();
    database = editor.getDatabase();

    changeList = editor.getChangeList();
    changeList.addChangeListListener( editor.getViewMenu() );

    // Use a border layout.  Create the loading panel.
    setLayout( new BorderLayout() );
    loadingPanel = initLoadingPanel();
  }

  /****************************************************************************/
  /** 
   * Cancel all the changes.
   */

  public void cancelAllChanges()
  {
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();

    // Invalidate all the records in the database model that contain
    // changes.
    Enumeration tables = changeList.getTablesChanged();

    while ( tables.hasMoreElements() )
    {
      String tableName = (String) tables.nextElement();

      Enumeration rows = changeList.getRowsChanged( tableName );

      while ( rows.hasMoreElements() )
      {
        Integer rowKey = (Integer) rows.nextElement();

        Object keyColumnValue = 
          changeList.getKeyFor( tableName, rowKey.intValue() );

        databaseModel.invalidateRow( tableName, keyColumnValue );
      }
    }

    // Wipe out the change list contents.
    changeList.clearChanges();
  }

  /****************************************************************************/
  /**
   * Clear the change cache for the currently selected row.
   */

  public void cancelCurrentChanges()
  {
    cancelCurrentChanges( getSelectedKeyValue() );
  }

  /****************************************************************************/
  /** 
   * Cancel the changes for this view.
   *
   * @param keyColumnValue The currently selected record ID for this view.
   */

  public void cancelCurrentChanges( Object keyColumnValue ) 
  {
    Hashtable<String,Object> pairs = new Hashtable<String,Object>();

    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();

    // Loop through each component entry, storing the
    // tableName/keyColumnValue pair in the pairs Hashtable.  For
    // component entries based on a join, the join is processed to get
    // the joinKeyColumnValue, then tableName/joinKeyColumnValue pair
    // is stored in the pairs Hashtable.
    Vector<ComponentEntry> componentEntries = (Vector<ComponentEntry>)configuration.getComponentEntries();

    try
    {
      for ( int i = 0; i < componentEntries.size(); i++ )
      {
        ComponentEntry componentEntry = 
          (ComponentEntry) componentEntries.elementAt(i);

        ColumnEntry columnEntry = componentEntry.getColumnEntry();

        if ( columnEntry != null )
        {
	  // Get the name of the table affected by the cancel.
          String tableName = columnEntry.getTableName();

          // Determine if this component is a join.
          DatabaseKey joinKey = componentEntry.getJoinDatabaseKey();
          if ( joinKey != null )
          {
            // It is.  Obtain the key value for the joined table and
            // add it to the accumulating list.
	    String joinTableName = joinKey.getTableName();
	    String joinColumnName = joinKey.getColumnName();
	    int joinColumnIndex =
	      database.getColumnIndex( joinTableName, joinColumnName );
            Object joinKeyColumnValue =
	      databaseModel.getValueAt( joinTableName, keyColumnValue,
					joinColumnIndex );
	    pairs.put( tableName, joinKeyColumnValue );
          }
          else
          {
            pairs.put( tableName, keyColumnValue );
          }
        }
      }
    }
    catch ( ConfigurationException configurationException )
    {
      LogClient.printStackTrace( configurationException );
    }

    // For each tableName/keyColumnValue pair clear the changes from
    // the change list and the invalidate the row in the database
    // model.  Note: do not combine the two loops, otherwise if there
    // are multiple changes in the same row, each call to
    // getColumnEntry() will perform a new fetch of that row and then
    // remove it.
    Enumeration tableNames = pairs.keys();

    while ( tableNames.hasMoreElements() )
    {
      String tableName = (String) tableNames.nextElement();

      Object keyValue = pairs.get( tableName );

      changeList.clearChanges( tableName, keyValue );
      databaseModel.invalidateRow( tableName, keyValue );
    }
  }

  /****************************************************************************/
  /**
   * Check the constraints for the row that has <I>keyColumnValue</I>
   * in the key column.
   */

  public void checkConstraints( Object keyColumnValue )
    throws ConfigurationException, ConstraintViolationException
  {
    // Fetch the constraint checker.
    ConstraintChecker constraintChecker =
      Editor.getInstance().getConstraintChecker();

    Enumeration tableNames = changeList.getTablesChanged();

    while ( tableNames.hasMoreElements() )
    {
      String tableName = (String) tableNames.nextElement();

      // Walk the entries in the change cache for <i>keyColumnValue</i>
      Vector<String> columns = changeList.getColumnsChanged( tableName, keyColumnValue );

      if ( columns != null )
      {
        for (int i = 0; i < columns.size(); i++)
        {
          // Get the configuration object for the GUI component.
          String columnName = (String) columns.elementAt(i);

          ComponentEntry componentEntry = configuration.getEntry( tableName, 
                                                                  columnName );
          Vector<String> constraints = null;
          ColumnEntry columnEntry = null;
          if (componentEntry != null) {
            columnEntry = componentEntry.getColumnEntry();
        
             if (columnEntry != null) {
               // Get the constraints on this component.
               constraints = columnEntry.getConstraintEntries();
            }
          }
          
          // If there are any constraints to be checked, do so.
          if ( constraints != null )
          {
            // Check all the constraints for this component.
            Iterator iterator = constraints.iterator();
          
            while ( iterator.hasNext() )
            {
              // Process a constraint.
              constraintChecker.check( keyColumnValue, 
                                       (String) iterator.next(), 
                                       columnEntry );
            }
          }
        }
      }
    }
  }

  /****************************************************************************/
  /**
   * Check all the constraints in <i>tableName</i> for record
   * <I>keyColumnValue</I>.
   */

  public void checkConstraints( String tableName, Object keyColumnValue )
    throws ConfigurationException, ConstraintViolationException
  {
    // Fetch the constraint checker.
    ConstraintChecker constraintChecker =
      Editor.getInstance().getConstraintChecker();

    // Get the list of columns for this table.
    DatabaseConfiguration databaseConfiguration = 
      DatabaseConfiguration.getInstance();
    TableConfiguration tableConfiguration =
      databaseConfiguration.getTableConfiguration( tableName );
    Collection columns = tableConfiguration.getColumnEntries();
    Iterator columnIterator = columns.iterator();

    while ( columnIterator.hasNext() )
    {
      ColumnEntry columnEntry = (ColumnEntry) columnIterator.next();
        
      // Get the constraints for this column.
      Vector<String> constraints = columnEntry.getConstraintEntries();
          
      // If there are any constraints to be checked, do so.
      if ( constraints != null )
      {
	// Check all the constraints for this component.
	Iterator iterator = constraints.iterator();
          
	while ( iterator.hasNext() )
	{
	  // Process a constraint.
	  constraintChecker.check( keyColumnValue, 
				   (String) iterator.next(), 
				   columnEntry );
	}
      }
    }
  }

  /****************************************************************************/
  /**
   * Create a new record.  Set up the seeding mode and the initial values.
   * @param seedingMode the seeding mode to be used to create the new
   * record (should be one of the integer constants from the Constants
   * class: NEWCLONE, NEWDEFAULT, NEWNULL).
   */

  public void createNewRecord( int seedingMode )
  {
    // Get the table name
    DatabaseKey dbKey = configuration.getKeyEntry();
    String tableName = dbKey.getTableName();

    // Get the seeding mode and handle it appropriately.
    switch ( seedingMode )
    {
    default:
    case -1:
      // Cancel out.
      break;

    case Constants.NEWCLONE:
      // Get the record to be used as the source of the initial
      // values.
      System.out.println( "Seed using clone of current values." );
      Object keyValue = getSelectedKeyValue();
      try
      {
	newKeyValue = model.addRecord( keyValue );

	// Initialize the key value to null.
	model.setValueAt( null, dbKey, newKeyValue );
      }
      catch ( RecordDoesNotExistException noRecordException )
      {
	LogClient.logMessage( noRecordException.getMessage() );
	LogClient.printStackTrace( noRecordException );
      }
      break;

    case Constants.NEWDEFAULT:
      System.out.println( "Seed using default values." );
      newKeyValue = model.addDefaultRecord();
      break;

    case Constants.NEWNULL:
      System.out.println( "Seed using null values." );
      newKeyValue = model.addNullRecord();
      break;
    }

    // Clear the change cache for the just loaded record.
    changeList.clearChanges( tableName, newKeyValue );

    // Generate a new view to tailor and save the new record.
    createNewRecordView();
  }

  /****************************************************************************/
  /**
   * Create a new view to handle creating a new record.
   */

  protected abstract void createNewRecordView();

  /****************************************************************************/

  public abstract void delete();

  /****************************************************************************/

  public ChangeList getChangeList()
  {
    return( changeList );
  }

  /****************************************************************************/
  /**
   * Return the configuration object for this view.
   */

  public Configuration getConfiguration()
  {
    return( configuration );
  }

  /****************************************************************************/
  /**
   * Return the control panel object for this view.
   */

  public ControlPanel getControlPanel()
  {
    return( controlPanel );
  }

  /****************************************************************************/

  public Filter getFilter()
  {
    return( filter );
  }

  /****************************************************************************/
  /**
   * Return the view in place at the time this view was presented.
   */

  public View getInvokingView()
  {
    return invokingView;
  }

  /****************************************************************************/
  /**
   * Return the set of key values possible for this view.
   */

  public Vector<Object> getKeys()
  {
    Vector<Object> result;
    DatabaseKey dbKey = configuration.getKeyEntry();
    result = database.getKeys( dbKey );
    return result;
  }

  /****************************************************************************/
  /**
   * Return the number of records in the view.
   */

  public int getNumberOfRecords() 
  {
    DatabaseKey key = configuration.getKeyEntry();
    String tableName = key.getTableName();
    return model.getRowCount( tableName );
  }

  /****************************************************************************/
  /**
   * Return the currently selected record in this view.
   */

  public abstract Object getSelectedKeyValue();

  /****************************************************************************/
  /**
   * Return the suffix for this view.
   */

  public String getSuffix()
  {
    return suffix;
  }

  /****************************************************************************/
  /**
   * Return the view name.
   */

  public String getViewName()
  {
    return viewName;
  }

  /****************************************************************************/

  public void hideLoadingPanel()
  {
    // Restore the normal cursor; remove the loading panel and 
    removeAll();
    Editor.getInstance().setDefaultCursor();
  }

  /****************************************************************************/
  /**
   * Configure and paint the view.
   */

  public void init()
  {
    Properties properties = Editor.getInstance().getProperties();

    controlPanel = new ControlPanel( properties, this );

    add( controlPanel, BorderLayout.SOUTH );
  }

  /****************************************************************************/

  public JPanel initLoadingPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new BorderLayout() );

    JLabel label = new JLabel( "Loading ..." );

    label.setHorizontalAlignment( SwingConstants.CENTER );
    label.setVerticalAlignment( SwingConstants.CENTER );

    result.add( label, BorderLayout.CENTER );

    return(result);
  }

  /****************************************************************************/
  /**
   * Repaint the view.
   */

  public abstract void refresh();

  /****************************************************************************/
  /**
   * Repaint the borders for the component rendering the column in
   * <I>columnNames</I> in the table <I>tableName</I>.
   *
   * @param tableName The database table in which the component is
   * backed.
   * @param columnNames  The list of columns in the table to repaint.
   */

  public void repaintBorders( String tableName, Vector columnNames )
  {
    // Insure that there are some columns to repaint.
    if ( columnNames != null )
    {
      // Walk the list of columns.
      Iterator iterator = columnNames.iterator();
      while ( iterator.hasNext() )
      {
	// Get the component entry associated with this table/column
	// pair and make the border opaque.
        String columnName = iterator.next().toString();
        ComponentEntry entry =
          configuration.getEntry( tableName, columnName );
        entry.paintBorder( ComponentEntry.OPAQUE );
      }
    }
  }

  /****************************************************************************/
  /**
   * Save all changes back to the database.
   */

  public abstract void saveAllChanges();

  /****************************************************************************/
  /** 
   * Save the changes for the selected row back to the database.
   */

  public abstract void saveCurrentChanges()
    throws ConfigurationException, ConstraintViolationException, 
    UpdateFailedException;

  /****************************************************************************/
  /**
   * Provide a default implementation for setting the border to
   * display the database user and server configuration.
   */

  protected void setBorder()
  {
    // Repaint the status information in the border title.
    String user = database.getUser();
    String server = database.getServer();
    String databaseName = database.getDBName();
    setBorder( new TitledBorder( "User: " + user +
				 ", Server: " + server +
				 ", Database: " + databaseName ) );
  }

  /****************************************************************************/

  public void setFilter( Filter filter )
  {
    this.filter = filter;
  }

  /****************************************************************************/
  /** 
   * Save the view presented to the user prior to presenting this view.
   */

  public void setInvokingView( View view )
  {
    invokingView = view;
  }

  /****************************************************************************/

  public void setModel( ViewModel model )
  {
    this.model = model;
  }

  /****************************************************************************/
  /**
   * Set the selected key value.
   */

  public abstract void setSelectedKeyValue( Object keyValue );

  /****************************************************************************/

  public void setViewName( String viewName )
  {
    this.viewName = viewName;
  }

  /****************************************************************************/

  public void showLoadingPanel()
  {
    // Set the cursor for possibly long operations; clear the screen
    // and put up the loading screen.
    removeAll();
    Editor.getInstance().setWaitCursor();
    add( loadingPanel, BorderLayout.CENTER );
    validate();
    repaint();
  }

  /****************************************************************************/
  /** 
   * Repaint the screen for the current view.
   */

  public abstract void update();

  /****************************************************************************/

}

/******************************************************************************/
