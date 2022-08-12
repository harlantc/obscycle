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

package gov.sao.asc.obsed.database;

/******************************************************************************/

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.view.Configuration;
import gov.sao.asc.obsed.view.ConfigurationException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/******************************************************************************/
/**
 * The model for storing all the data from the database.
 */

public class DatabaseModel
{
  /**
   * Pointer to the Database object for convenience.
   */
  private Database database;

  /**
   * The set of keys represent the table names.  The values are
   * TableModel instances.
   */
  private Hashtable<String,TableModel> tableModels;

  /****************************************************************************/

  public DatabaseModel( Database database )
  {
    this.database = database;

    tableModels = new Hashtable<String,TableModel>();
  }

  /****************************************************************************/
  /**
   * Add a new record to the database model using the set of values
   * from the first database record as the initial values for the
   * record.
   *
   * @param tableName  The table to clone in.
   */

  public Object addRecord( String tableName )
    throws RecordDoesNotExistException
  {
    TableModel tableModel = (TableModel) tableModels.get( tableName );
    return tableModel.addRecord();
  }

  /****************************************************************************/
  /**
   * Add a new record to the database model using nulls for the
   * initial values.
   *
   * @param tableName  The table in which to create a new record.
   */

  public Object addNullRecord( String tableName )
  {
    TableModel tableModel = (TableModel) tableModels.get( tableName );
    return tableModel.addNullRecord();
  }

  /****************************************************************************/
  /**
   * Add a new record to the database model using the set of values
   * from the record at <i>keyValue</i> as the initial values for the
   * record.
   *
   * @param tableName  The table to clone in.
   * @param keyValue  The database model index to clone from.
   */

  public Object addRecord( String tableName, Object keyValue )
    throws RecordDoesNotExistException
  {
    TableModel tableModel = (TableModel) tableModels.get( tableName );
    return tableModel.addRecord( keyValue );
  }

  /****************************************************************************/
  /**
   * Copy the record at <i>sourceKeyValue</i> to the record at
   * <i>destKeyValue</i> for table <i>tableName</i>.
   *
   * @param tableName The table identifier.
   * @param destKeyValue  The destination record address.
   * @param sourceKeyValue The source record address.
   */

  public void copyNull( String tableName, Object destKeyValue )
    throws RecordDoesNotExistException
  {
    TableModel tableModel = (TableModel) tableModels.get( tableName );
    tableModel.copyNull( destKeyValue );
  }

  /****************************************************************************/
  /**
   * Copy the record at <i>sourceKeyValue</i> to the record at
   * <i>destKeyValue</i> for table <i>tableName</i>.
   *
   * @param tableName The table identifier.
   * @param destKeyValue  The destination record address.
   * @param sourceKeyValue The source record address.
   */

  public void copyRecord( String tableName, Object destKeyValue,
			  Object sourceKeyValue )
    throws RecordDoesNotExistException
  {
    TableModel tableModel = (TableModel) tableModels.get( tableName );
    tableModel.copyRecord( destKeyValue, sourceKeyValue );
  }

  /****************************************************************************/
  /**
   * Create a new record in the model for table <i>tableName</i> at
   * record <i>keyValue</i>.
   *
   * @param tableName The table identifier.
   * @param keyValue  Maps to a database model row.
   */

  public void createRecord( String tableName, Object keyValue )
    throws AlreadyExistsException
  {
    TableModel tableModel = (TableModel) tableModels.get( tableName );
    tableModel.createRecord( keyValue );
  }

  /****************************************************************************/
  /**
   * Return the key value from the <I>rowIndex</I> row in the
   * <I>tableName</I> table.
   *
   * @param tableName  The table from which to get the value.
   * @param rowIndex  The database record index.
   */

  public Object getKeyValueAt( String tableName, int rowIndex )
  {
    TableModel tableModel = getTableModel( tableName );

    Object result = tableModel.getKeyValueAt( rowIndex );

    return( result );
  }

  /****************************************************************************/

  public int getRowCount( String tableName )
    throws ConfigurationException
  {
    TableModel tableModel = getTableModel( tableName );

    int result = tableModel.getRowCount();

    return( result );
  }

  /****************************************************************************/

  public TableModel getTableModel( String tableName )
  {
    TableModel result = (TableModel) tableModels.get( tableName );

    if ( result == null )
    {
      DatabaseConfiguration databaseConfiguration =
        DatabaseConfiguration.getInstance();

      String keyColumnName = 
        databaseConfiguration.getKeyColumnName( tableName );

      DatabaseKey databaseKey = new DatabaseKey( tableName, keyColumnName );

      result = new TableModel( database, databaseKey );

      tableModels.put( tableName, result );
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Get a value from the data model for a given row and column entry.
   *
   * @param keyColumnValue  Maps to a database model row.
   * @param columnEntry  Maps to a table and column.
   *
   * @returns The value of the object at the given row and column.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public Object getValueAt( Object keyColumnValue, ColumnEntry columnEntry )
  {
    // Fetch and return the target value from the database model.
    String tableName = columnEntry.getTableName();
    TableModel tableModel = getTableModel( tableName );
    int columnIndex = columnEntry.getColumnIndex();
    return getValueAt( tableName, keyColumnValue, columnIndex );
  }

  /****************************************************************************/
  /**
   * Return the value from the cell with <I>columnIndex</I> and
   * <I>keyValue</I> in the column of <I>keyEntry</I> from the table
   * of <I>keyEntry</I>
   *
   * @param tableName The table to get the value from.
   * @param keyColumnValue The value in the key column in the row with
   *                       the desired value.
   * @param columnIndex  The database column index.
   *
   * @returns The value of the object at the given table, row and column.
   *
   * @author Paul Matthew Reilly
   *
   * @version %I%, %G%
   */

  public Object getValueAt( String tableName, Object keyValue, int columnIndex )
  {
    TableModel tableModel = getTableModel( tableName );
    return tableModel.getValueAt( keyValue, columnIndex );
  }

  /****************************************************************************/
  /**
   * Return a boolean indication that the record at <i>keyValue</i>
   * exists in the <i>tableName</i>
   *
   * @param tableName The table to get the value from.
   * @param keyValue The database record index.
   *
   * #see getValueAt
   */

  public boolean hasRecord( String tableName, Object keyValue )
  {
    TableModel tableModel = getTableModel( tableName );

    return tableModel.hasRecord( keyValue );
  }

  /****************************************************************************/
  /**
   * Mark the cache entry invalid such that a subsequent access to the
   * row will force a re-load operaion.
   *
   * @param tableName  The table containing the invalid record.
   * @param keyColumnValue The value of the key column for the invalid record.
   */

  public void invalidateRow( String tableName, Object keyColumnValue )
  {
    TableModel tableModel = getTableModel( tableName );
    tableModel.invalidateRow( keyColumnValue );
  }

  /****************************************************************************/
  /**
   * Return the read/write permission for the given table/column pair.
   *
   * @param tableName  The table to invalidate.
   * @param columnID  The database column name.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public boolean isMutable( String tableName, String columnID )
  {
    return database.isWritable( tableName, columnID );
  }

  /****************************************************************************/
  /**
   * Load all the data for <I>configuration</I> at once, because it
   * isn't efficient to get each row one at a time using the
   * <I>getValue</I> method.  Appropriate for the table view.
   *
   * @param configuration
   *
   * #see getValueAt
   */

  public void loadData( Configuration configuration )
  {
    Vector<String> tableNames = configuration.getTableNames();

    for (int i = 0; i < tableNames.size(); i++)
    {
      String tableName = (String) tableNames.elementAt( i );

      TableModel tableModel = getTableModel( tableName );

      tableModel.loadData();
    }
  }

  /****************************************************************************/
  /**
   * Refresh the model from the database.
   *
   * @param tableName  The name of the table to be refreshed.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public void reload( String tableName )
  {
    TableModel tableModel = getTableModel( tableName );
    tableModel.reload();
  }

  /****************************************************************************/
  /**
   * Remove a row from a table.
   *
   * @param tableName  The table.
   * @param keyColumnValue  The row ID.
   */

  public void removeRow( String tableName, Object keyColumnValue )
  {
    // Fetch the appropriate table model and do the removal.
    TableModel tableModel = getTableModel( tableName );
    tableModel.removeRow( keyColumnValue );
  }

  /****************************************************************************/
  /**
   * Remove a row from a table.
   *
   * @param tableName  The table.
   * @param rowIndex  The row ID.
   */

  public void removeRow( String tableName, int rowIndex )
  {
    // Fetch the appropriate table model and do the removal.
    TableModel tableModel = getTableModel( tableName );
    tableModel.removeRow( rowIndex );
  }

  /****************************************************************************/
  /**
   * Store a value into the database cache.
   *
   * @param value  The value to be stored.
   * @param keyColumnValue The value of the key column for the targeted row.
   * @param columnEntry  The database column index.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public void setValueAt( Object value, Object keyColumnValue, 
                          ColumnEntry columnEntry )
    throws RecordDoesNotExistException
  {
    // Fetch the table model object and the column index.
    String tableName = columnEntry.getTableName();
    TableModel tableModel = getTableModel( tableName );
    int columnIndex = columnEntry.getColumnIndex();

    // Store the data into the DB table cache.
    tableModel.setValueAt( value, keyColumnValue, columnIndex );
  }

  /****************************************************************************/
  /**
   * Store a value into the database cache.
   *
   * @param value  The value to be stored.
   * @param keyColumnValue The value of the key column for the targeted row.
   * @param databaseKey  The database table name, column name pair.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public void setValueAt( Object value, Object keyColumnValue, 
                          DatabaseKey databaseKey )
    throws RecordDoesNotExistException
  {
    // Fetch the table model object and the column index.
    String tableName = databaseKey.getTableName();
    String columnName = databaseKey.getColumnName();
    TableModel tableModel = getTableModel( tableName );
    int columnIndex = database.getColumnIndex( tableName, columnName );

    // Store the data into the DB table cache.
    tableModel.setValueAt( value, keyColumnValue, columnIndex );
  }

  /****************************************************************************/
  /**
   * Store a value into the database cache.
   *
   * @param value  The value to be stored.
   * @param tableName  The table in which to store <i>value</i>.
   * @param keyColumnValue The value of the key column for the targeted row.
   * @param columnIndex  The database column index.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  private void setValueAt( Object value, String tableName, 
			   Object keyColumnValue, int columnIndex )
    throws RecordDoesNotExistException
  {
    TableModel tableModel = getTableModel( tableName );

    tableModel.setValueAt( value, keyColumnValue, columnIndex );
  }

  /****************************************************************************/

}

/******************************************************************************/
