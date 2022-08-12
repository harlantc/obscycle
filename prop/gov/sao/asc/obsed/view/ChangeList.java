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

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.view.event.ChangeListEvent;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.util.LogClient;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.event.EventListenerList;

/******************************************************************************/
/**
 * The ChangeList contains all the current changes for the Editor.
 * Each key in the encapsulated Hashtable is a table name (String).
 * The value associated with each table name is a TableChangeList.
 */

public class ChangeList extends Object
{
  /**
   * Convenience variable.
   */
  private DatabaseModel databaseModel;

  /**
   * Implements the storage for the change list.  Each entry maps a
   * table to a collection of change information.
   */
  private Hashtable<String,Object> tableCache;

  /**
   * Maintains the list of listeners, who will be notified on changes
   * that get registered to this object.
   */
  private EventListenerList listenerList;

  /****************************************************************************/
  /**
   * Construct a new change list object.
   */

  public ChangeList( DatabaseModel databaseModel )
  {
    this.databaseModel = databaseModel;

    tableCache = new Hashtable<String,Object>();
    listenerList = new EventListenerList();
  }

  /****************************************************************************/
  /**
   * Process a changed component.  Add the change to the change list.
   *
   * @param databsseKey The database table/column id.
   * @param rowIndex A mapped record id.
   * @param newValue The modified value.
   */

  private void addChange( DatabaseKey databaseKey, int rowIndex,
			  Object newValue )
  {
    // Get the table change list and add this entry.
    String tableName = databaseKey.getTableName();
    String columnName = databaseKey.getColumnName();
    TableChangeList tableChangeList = getTableChangeList( tableName );
    tableChangeList.addChange( rowIndex, columnName, newValue );

    // Fire an event to flag the change.
    ChangeListEvent event =
      new ChangeListEvent( databaseKey, rowIndex, newValue, true );
    fireChangeListChanged( event );
  }

  /****************************************************************************/
  /**
   * Process a changed component.  Add the change to the change list
   * and to the data model.
   *
   * @param databsseKey The database table/column id.
   * @param keyColumnValue A pre-mapped record id.
   * @param newValue The modified value.
   */

  public void addChange( DatabaseKey databaseKey, Object keyColumnValue,
			 Object newValue )
  {
    // Convert the key column value to a row index.
    String tableName = databaseKey.getTableName();
    TableChangeList tableChangeList = getTableChangeList( tableName );
    int rowIndex = tableChangeList.getRowIndexFor( keyColumnValue );

    if ( Editor.getInstance().isDebug() ) {
      System.out.println( "Row index: " + rowIndex );
    }

    // Add the change to the data model.
    try
    {
      databaseModel.setValueAt( newValue, keyColumnValue, databaseKey );
    }
    catch ( RecordDoesNotExistException exception )
    {
      LogClient.logMessage( "Unexpected failure processing a changed component." );
      LogClient.printStackTrace( exception );
    }

    // Let the overloaded method finish the task.
    addChange( databaseKey, rowIndex, newValue );
  }

  /****************************************************************************/

  public void addChangeListListener( ChangeListListener listener )
  {
    listenerList.add( ChangeListListener.class, listener);
  }

  /****************************************************************************/
  /**
   * Remove all entries for table <i>tableName</i>, in <i>rowIndex</i>
   * from this cache.
   */

  public void clearChanges( String tableName, int rowIndex )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    tableChangeList.clearChanges( rowIndex );

    ChangeListEvent event = new ChangeListEvent( tableName, rowIndex, false );

    fireChangeListChanged( event );
  }

  /****************************************************************************/
  /**
   * Remove all entries for table <i>tableName</i>,
   * <i>keyColumnValue</i> from this cache.
   */

  public void clearChanges( String tableName, Object keyColumnValue )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    tableChangeList.clearChanges( keyColumnValue );

    // getRowIndexFor may return -1 which indicates that the key
    // column value had no matching rowIndex in the table change list.
    // In this case, don't fire an event, since nothing changed!!
    int rowIndex = tableChangeList.getRowIndexFor( keyColumnValue );

    if ( rowIndex != -1 ) 
    {
      ChangeListEvent event = new ChangeListEvent( tableName, rowIndex, false );
      fireChangeListChanged( event );
    }
  }

  /****************************************************************************/
  /**
   * Remove all entries from this cache.
   */

  public void clearChanges()
  {
    // Clear the table cache and notify interested listeners.
    tableCache.clear();
    fireChangeListChanged( new ChangeListEvent( false ) );
  }

  /****************************************************************************/
  /**
   * Handle a row deletion.
   *
   * @param tableName  The table from which the row has been deleted.
   * @param keyValue  The ID for the deleted row.
   */
  public void delete( String tableName, Object keyValue )
  {
    // Clear any changes that might be registered against this view.
    clearChanges( tableName, keyValue );

    // Generate a delete change event notifying interested listeners.
    ChangeListEvent event = new ChangeListEvent( tableName, keyValue );
    fireChangeListChanged( event );
  }

  /****************************************************************************/

  public void fireChangeListChanged( ChangeListEvent changeListEvent )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) 
    {
      if (listeners[i] == ChangeListListener.class) 
      {
        ( (ChangeListListener) listeners[i+1] ).changeListChanged( changeListEvent );
      }
    }
  }

  /****************************************************************************/
  /**
   * Returns list of tableNames for the changes that have occured in
   * <I>rowIndex</I>.
   */

  public Vector<String> getColumnsChanged( String tableName, int rowIndex )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    Vector<String> result = tableChangeList.getColumnsChanged( rowIndex );

    return( result );
  }

  /****************************************************************************/
  /**
   * Returns list of tableNames for the changes that have occured in
   * <I>rowIndex</I>.
   */

  public Vector<String> getColumnsChanged( String tableName, Object keyColumnValue )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    Vector<String> result = tableChangeList.getColumnsChanged( keyColumnValue );

    return( result );
  }

  /****************************************************************************/
  /**
   * Returns the key column value for the <I>rowIndex</I> row in the
   * <I>tableName</I> table.  Note: should not be used when
   * <I>tableName</I> comes from a ColumnEntry with a non-null
   * joinEntry.  In that case the join has to be processes properly.
   * See cancelCurentChanges() in the View class for an example.
   */

  public Object getKeyFor( String tableName, int rowIndex )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    Object result = tableChangeList.getKeyFor( rowIndex );

    return( result );
  }

  /****************************************************************************/
  /**
   * Returns the row index for the <I>keyColumnValue</I> in the
   * <I>tableName</I> table.  Returns -1 if index not found in
   * specified table.
   */

  public int getRowIndexFor( String tableName, Object keyColumnValue )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    int result = tableChangeList.getRowIndexFor( keyColumnValue );

    return( result );
  }

  /****************************************************************************/
  /**
   * Returns list of rowIndices where changes have occured in a given table.
   */

  public Enumeration getRowsChanged( String tableName )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    Enumeration result = tableChangeList.getRowsChanged();

    return( result );
  }

  /****************************************************************************/

  public Enumeration getTablesChanged()
  {
    return tableCache.keys();
  }

  /****************************************************************************/
  /**
   * Get the table change list for the given table name.
   *
   * @param tableName The table of interest.
   *
   * @returns The TableChangeList object associated with <i>tableName</i>.
   */

  private TableChangeList getTableChangeList( String tableName )
  {
    // Determine if a table change list object exists for the given
    // table.
    TableChangeList result = (TableChangeList) tableCache.get( tableName );
    if ( result == null )
    {
      // It doesn't.  Create and save it.
      result = new TableChangeList( tableName );
      tableCache.put( tableName, result );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Get the value associated with a particular column in a given
   * table and row.
   *
   * @param keyColumnValue The row specifier.
   * @param columnName The column identifier.
   *
   * @returns The new value for the specified cell.
   */

  public Object getValueAt( String tableName, Object keyColumnValue,
			    String columnName )
  {
    // Convert the key column value to a suitable index value.
    TableChangeList tableChangeList =
      (TableChangeList) tableCache.get( tableName );

    // Return the desired object.
    return tableChangeList.getValueAt( keyColumnValue, columnName );
  }

  /****************************************************************************/
  /**
   *  Return an indication that a given cell in a given table
   *  observation has been modified.
   */

  public boolean isCellDirty( DatabaseKey databaseKey, int rowIndex )
  {
    TableChangeList tableChangeList = 
      getTableChangeList( databaseKey.getTableName() );

    boolean result = tableChangeList.isCellDirty( rowIndex, 
                                                  databaseKey.getColumnName() );

    return result;
  }

  /****************************************************************************/
  /**
   *  Return an indication that a given cell in a given table
   *  observation has been modified.
   */

  public boolean isCellDirty( DatabaseKey databaseKey, Object keyColumnValue )
  {
    TableChangeList tableChangeList = 
      getTableChangeList( databaseKey.getTableName() );

    boolean result = tableChangeList.isCellDirty( keyColumnValue, 
                                                  databaseKey.getColumnName() );

    return result;
  }

  /****************************************************************************/
  /**
   *  Return an indication that an observation has been modified.
   */

  public boolean isDirty()
  {
    // Predispose the result.
    boolean result = false;
    String tableName;

    // Return true iff at least one table is dirty.
    if ( !tableCache.isEmpty() )
    {
      for ( Enumeration e = tableCache.keys(); e.hasMoreElements(); )
      {
	// Return immediately if we find a dirty table.
	if ( isDirty( (String) e.nextElement() ) )
	{
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /****************************************************************************/
  /**
   *  Return an indication that a row in a given table observation has
   *  been modified.  Returns true iff at least one row in
   *  <i>tableName</i> has been modified.
   */

  public boolean isDirty( String tableName )
  {
    // Predispose the result
    boolean result = false;

    // The cache is dirty if it contains any values.
    TableChangeList tableChangeList = getTableChangeList( tableName );
    return tableChangeList.isDirty();
  }

  /****************************************************************************/
  /**
   *  Return an indication that a row (in *any* table) has been
   *  modified for specified key value.  Returns true iff at least one
   *  row in any table has been modified.
   */

  public boolean isDirty( Object keyValue )
  {
    // Predispose the result
    boolean result = false;

    // The cache is dirty if it contains any values.

    if( !tableCache.isEmpty() )
    {
      for ( Enumeration e = tableCache.keys(); e.hasMoreElements(); )
      {
	// Return immediately if we find a dirty row for this key in
	// this table.
	if ( isRowDirty ( (String) e.nextElement(), keyValue ) )
	{
	  result = true;
	  break;
	}
      }
    }
    return result;
  }

  /****************************************************************************/
  /**
   *  Return an indication that a given row in a given table
   *  observation has been modified.  Returns true iff <i>rowIndex</i>
   *  has at least one modified column.
   */

  public boolean isRowDirty( String tableName, int rowIndex )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    boolean result = tableChangeList.isRowDirty( rowIndex );

    return( result );
  }

  /****************************************************************************/
  /**
   *  Return an indication that a given row in a given table
   *  observation has been modified.  Returns true iff
   *  <i>keyColumnValue</i> has at least one modified column.
   */

  public boolean isRowDirty( String tableName, Object keyColumnValue )
  {
    TableChangeList tableChangeList = getTableChangeList( tableName );

    boolean result = tableChangeList.isRowDirty( keyColumnValue );

    return( result );
  }

  /****************************************************************************/
  /**
   * Handle a row addition.
   *
   * @param tableName  The table from which the row has been deleted.
   * @param keyValue  The ID for the deleted row.
   */
  public void newRecord( String tableName )
  {
    // Update the database model.
    databaseModel.reload( tableName );

    // Generate a new record change event notifying interested listeners.
    ChangeListEvent event = new ChangeListEvent( tableName, -1, false );
    fireChangeListChanged( event );
  }

  /****************************************************************************/

  public void removeChangeListListener( ChangeListListener listener )
  {
    listenerList.remove( ChangeListListener.class, listener );
  }

  /****************************************************************************/

}

/******************************************************************************/
