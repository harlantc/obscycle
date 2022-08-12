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
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.TableModel;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/******************************************************************************/
/**
 * The TableChangeList contains all the current changes for a given table.
 */

public class TableChangeList extends Object
{
  /**
   * Implements the storage for the table change list.  Each entry in
   * the table associates a row index with another hashtable, which,
   * in turn, maps a column name to a value.
   */
  private Hashtable<Integer,HashMap<String,Object>> changeCache;

  /**
   * Contains the table name.
   */
  private String tableName;

  /****************************************************************************/
  /**
   * Construct a TableChangeList object for the given table.
   *
   * @param tableName The table.
   */

  public TableChangeList( String tableName )
  {
    // Set the instance variables.
    this.tableName = tableName;
    changeCache = new Hashtable<Integer,HashMap<String,Object>>();
  }

  /****************************************************************************/
  /**
   * Update the row change cache.  Update the column name, value pair
   * if it exists, otherwise add it.
   *
   * @param rowIndex The index into the row change cache.
   * @param columnName The key which associates a column name with a value.
   * @param newValue The non-null value part of the association.
   */

  public void addChange( int rowIndex, String columnName, Object newValue )
  {
    // Map the row index to an object suitable for probing the row
    // change cache.
    Integer rowKey = new Integer( rowIndex );

    // Determine if we have any changes for the given row.
    HashMap<String,Object> rowCache = (HashMap<String,Object>)changeCache.get(rowKey );
    if ( rowCache == null )
    {
      // There are none.  Create an object to store the change data.
      rowCache = new HashMap<String,Object>();
      changeCache.put( rowKey, rowCache );
    }

    if ( Editor.getInstance().isDebug() )
    {
      System.out.println( "Add change to: " + rowCache + ", " +
			  " column: " + columnName + 
			  " new value: " + newValue );
    }

    // Update the row cache with the new value (or add a
    // columnName/newValue pair to the row cache.
    Object oldValue = rowCache.put( columnName, newValue );
  }

  /****************************************************************************/
  /**
   * Update the row change cache.  Update the column name, value pair
   * if it exists, otherwise add it.
   *
   * @param keyColumnValue The index into the row change cache.
   * @param columnName The key which associates a column name with a value.
   * @param newValue The non-null value part of the association.
   */

  public void addChange( Object keyColumnValue, String columnName,
			 Object newValue )
  {
    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    int rowIndex = tableModel.getRowIndex( keyColumnValue );

    // Add the change for this row index.
    addChange( rowIndex, columnName, newValue );
  }

  /****************************************************************************/
  /**
   * Remove all entries for <i>rowIndex</i>.
   *
   * @param rowIndex The row to remove from the change cache.
   */

  public void clearChanges( int rowIndex )
  {
    // Map the row index into a real object and remove the associated
    // key, if one exists.
    Integer rowKey = new Integer( rowIndex );
    changeCache.remove( rowKey );

    if ( Editor.getInstance().isDebug() )
    {
      System.out.println( "Clear change from cache: " + rowKey );
    }
  }

  /****************************************************************************/
  /**
   * Remove all entries for the row that has <I>keyColumnValue</I> in
   * the key column.
   */

  public void clearChanges( Object keyColumnValue )
  {
    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    int rowIndex = tableModel.getRowIndex( keyColumnValue );

    clearChanges( rowIndex );
  }

  /****************************************************************************/
  /**
   * Remove all entries from this cache.
   */

  public void clearChanges()
  {
    changeCache.clear();
  }

  /****************************************************************************/
  /**
   * Get a list of columms that have been modified for a given row.
   *
   * @param keyColumnValue The row specifier.
   *
   * @returns A vector containing the names of the modified columns
   * for row denoted by <i>keyColumnValue</i>
   */

  public Vector<String> getColumnsChanged( int rowIndex )
  {
    // Get the row cache and create the result vector.
    System.out.println( "Row index: " + rowIndex );
    Integer rowKey = new Integer( rowIndex );
    HashMap<String,Object> rowCache = (HashMap<String,Object>) changeCache.get( rowKey );
    Vector<String> result = new Vector<String>();

    // Walk through the set of keys to build the result vector.
    if (rowCache != null) {
    Set s = rowCache.keySet();
    Iterator i = s.iterator();
    while ( i != null && i.hasNext() )
    {
      result.add( (String) i.next() );
    }
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Get a list of columms that have been modified for a given row.
   *
   * @param keyColumnValue The row specifier.
   *
   * @returns A vector containing the names of the modified columns
   * for row denoted by <i>keyColumnValue</i>
   */

  public Vector<String> getColumnsChanged( Object keyColumnValue )
  {
    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );

    int rowIndex = tableModel.getRowIndex( keyColumnValue );

    // Use the overloaded method to finish the job.
    return getColumnsChanged( rowIndex );
  }

  /****************************************************************************/

  public Object getKeyFor( int rowIndex )
  {
    // Get the result from the table model object.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    return tableModel.getKeyValueAt( rowIndex );
  }

  /****************************************************************************/
  /**
   * Returns list of column names for row with key value equal to
   * <I>keyColumnValue</I>.
   */

  public Enumeration getRowsChanged()
  {
    return changeCache.keys();
  }

  /****************************************************************************/
  /**
   * Get the database record ID corresponding to a given key column value.
   *
   * @param keyColumnValue The pre-mapped record selector.
   *
   * @returns The result of the mapping.
   */

  public int getRowIndexFor( Object keyColumnValue )
  {
    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    return tableModel.getRowIndex( keyColumnValue );
  }

  /****************************************************************************/
  /**
   * Get the value associated with a particular column in a given row.
   *
   * @param keyColumnValue The row specifier.
   * @param columnName The column identifier.
   *
   * @returns The new value for the specified cell.
   */

  public Object getValueAt( Object keyColumnValue, String columnName )
  {
    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    int rowIndex = tableModel.getRowIndex( keyColumnValue );

    // Get the set of mappings for the given row from the change
    // cache.
    Integer rowKey = new Integer( rowIndex );
    HashMap<String,Object> rowCache = (HashMap<String,Object>) changeCache.get( rowKey );

    // Return the desired object.
    return rowCache.get( columnName );
  }

  /****************************************************************************/
  /**
   *  Determines if a particular column in a particular row has been
   *  modified.
   *
   * @param rowIndex The particular row.
   * @param columnName The particular column.
   *
   * @returns <b>true</b> iff column <i>columnName</i> in row
   * <i>rowIndex</i> has been modified.
   */

  public boolean isCellDirty( int rowIndex, String columnName )
  {
    // Fetch the row cache and determine if it contains columnName.
    Integer rowKey = new Integer( rowIndex );
    HashMap<String,Object> rowCache = (HashMap<String,Object>) changeCache.get( rowKey );
    return ( rowCache != null && rowCache.containsKey( columnName ) );
  }

  /****************************************************************************/
  /**
   *  Determines if a particular column in a particular row has been
   *  modified.
   *
   * @param keyColumnValue The particular row.
   * @param columnName The particular column.
   *
   * @returns <b>true</b> iff column <i>columnName</i> in row
   * <i>rowIndex</i> has been modified.
   */

  public boolean isCellDirty( Object keyColumnValue, String columnName )
  {
    boolean result = false;

    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    int rowIndex = tableModel.getRowIndex( keyColumnValue );

    // Use the overloaded version to finish the job.
    return isCellDirty( rowIndex, columnName );
  }

  /****************************************************************************/
  /**
   *  Determines if a any rows have modified columna.
   *
   * @returns <b>true</b> iff at least one row has modified data.
   */

  public boolean isDirty()
  {
    return ( changeCache != null && changeCache.size() > 0 );
  }

  /****************************************************************************/
  /**
   *  Determines if a particular row has been modified.
   *
   * @param rowIndex The particular row.
   *
   * @returns <b>true</b> iff row <i>rowIndex</i> has been modified.
   */

  public boolean isRowDirty( int rowIndex )
  {
    // The row is dirty if the row cache exists.
    Integer rowKey = new Integer( rowIndex );
    HashMap<String,Object> rowCache = (HashMap<String,Object>) changeCache.get( rowKey );
    return ( rowCache != null );
  }

  /****************************************************************************/
  /**
   *  Determines if a particular row has been modified.
   *
   * @param rowIndex The particular row.
   *
   * @returns <b>true</b> iff row <i>rowIndex</i> has been modified.
   */

  public boolean isRowDirty( Object keyColumnValue )
  {
    // Convert the key column value to a suitable index value.
    Editor editor = Editor.getInstance();
    DatabaseModel databaseModel = editor.getDatabase().getDatabaseModel();
    TableModel tableModel = databaseModel.getTableModel( tableName );
    int rowIndex = tableModel.getRowIndex( keyColumnValue );

    // Use the overloaded version to finish the job.
    return isRowDirty( rowIndex );
  }

  /****************************************************************************/

}

/******************************************************************************/
