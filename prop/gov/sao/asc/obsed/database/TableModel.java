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

import java.util.Hashtable;
import java.util.Vector;

/******************************************************************************/
/**
 * The model for storing all the data from a single database table.
 */

public class TableModel extends Object
{
  /**
   * Pointer to the Database object for convenience.
   */
  private Database database;

  /**
   * Contains the table name and the key column name.
   */
  private DatabaseKey databaseKey;

  /**
   * The set of keys represents the row indices.  The values are
   * Vectors containing all the values for a single row.
   */
  private Hashtable<Integer,Vector<Object>> rows;

  /**
   * The set of values from the key column in sorted order.
   */
  private Vector<Object> keys;

  /****************************************************************************/

  public TableModel( Database database, DatabaseKey databaseKey )
  {
    // Save the arguments and use the reload method to initialize.
    this.database = database;
    this.databaseKey = databaseKey;
    reload();
  }

  /****************************************************************************/
  /**
   * Add a new record with null initial values.
   *
   * @returns The key value identifying the new record.
   */

  public Object addNullRecord()
  {
    // Create an object to use as an index into the data model.  Store
    // this object into the keys vector.
    Object newKeyValue = new Object();
    keys.add( newKeyValue );

    // Use the zeroth row to obtain a null record.
    Vector<Object> row = database.getValues( databaseKey, new Integer( 0 ) );

    // Now clone each value in <i>row</i> and save the result in the
    // row cache.
    @SuppressWarnings("unchecked")
    Vector<Object> clone =  (Vector<Object>)row.clone();
    int rowIndex = getRowIndex( newKeyValue);

    // ASSERT: rowIndex != -1
    // because newKeyValue was added to keys at the start of this
    // method.
    setRow( rowIndex, clone);

    return newKeyValue;
  }

  /****************************************************************************/
  /**
   * Add a record to the data model.  Return the key value used to
   * index into this record.  Used to support creation/insertion of
   * new records into the database.  The first record in the database
   * is used to seed the new record.
   */

  public Object addRecord()
    throws RecordDoesNotExistException
  {
    // Clone the first record.
    return addRecord( new Integer( 1 ) );
  }

  /****************************************************************************/
  /**
   * Add a new record by cloning <i>keyValue</i>.  Return
   * <i>keyValue</i>.
   */

  public Object addRecord( Object keyValue )
    throws RecordDoesNotExistException
  {
    // Create an object to use as an index into the data model.  Store
    // this object into the keys vector.
    Object newKeyValue = new Object();
    keys.add( newKeyValue );

    // Copy the vector from <i>keyValue</i> to the new record.  Handle
    // the case where the <i>keyValue</i> does not exist by cloning
    // the first database record instead.
    Vector<Object> row = getRow( keyValue );

    // Deal with an empty cache line.
    if ( row == null )
    {
      // Inform the caller that the clone source does not exist.
      String tableName = databaseKey.getTableName();
      String message = "Record " + keyValue + " in " + tableName +
	" does not exist.";
      throw new RecordDoesNotExistException( message, tableName, keyValue );
    }

    // Now clone each value in <i>row</i> and save the result in the
    // row cache.
    @SuppressWarnings("unchecked")
    Vector<Object> clone =  (Vector<Object>)row.clone();
    int rowIndex = getRowIndex( newKeyValue );

    // ASSERT: rowIndex != -1
    // because newKeyValue was added to keys at the start of this
    // method.
    setRow( new Integer(rowIndex), clone );

    return newKeyValue;
  }

  /****************************************************************************/
  /**
   * Set the values in record <i>destKeyValue</i> to nulls.
   *
   * @param destKeyValue An object that can be mapped to a data model
   * address.
   */

  public void copyNull( Object destKeyValue )
    throws RecordDoesNotExistException
  {
    // Copy the sentinal value from the database to the destination.
    copyRecord( destKeyValue, new Integer( 0 ) );
  }

  /****************************************************************************/
  /**
   * Copy record <i>sourceKeyValue</i> to record <i>destKeyValue</i>.
   *
   * @param destKeyValue  The destination record address.
   * @param sourceKeyValue  The source record address.
   */

  public void copyRecord( Object destKeyValue, Object sourceKeyValue )
    throws RecordDoesNotExistException
  {
    String message;

    // In anticipation of an error, identify the table.
    String tableName = databaseKey.getTableName();

    // Validate that the records exist.
    if ( !hasRecord( destKeyValue ) )
    {
      // Inform the caller that the destination record does not exist.
      message = "Record " + destKeyValue + " in " + tableName +
	" does not exist.";
      throw new RecordDoesNotExistException( message, tableName,
					     destKeyValue );
    }
    if ( !hasRecord( sourceKeyValue ) )
    {
      // Inform the caller that the source does not exist.
      message = "Record " + sourceKeyValue + " in " + tableName +
	" does not exist.";
      throw new RecordDoesNotExistException( message, tableName,
					     sourceKeyValue );
    }

    // Copy the vector from <i>sourceKeyValue</i> to the new record.
    // Handle the case where the <i>keyValue</i> does not exist by
    // cloning the first database record instead.
    Vector<Object> row = getRow( sourceKeyValue );

    // Deal with an empty cache line.
    if ( row == null )
    {
      // Inform the caller that the clone source does not exist.
      message = "Record " + sourceKeyValue + " in " + tableName +
	" does not exist.";
      throw new RecordDoesNotExistException( message, tableName,
					     sourceKeyValue );
    }

    // Now clone each value in <i>row</i> and save the result in the
    // row cache.
    @SuppressWarnings("unchecked")
    Vector<Object> clone = (Vector<Object>) row.clone();
    int rowIndex = keys.indexOf( destKeyValue );
    rows.put( new Integer( rowIndex ), clone );
  }

  /****************************************************************************/
  /**
   * Create a new record at <i>keyValue</i>.  Initialize the values in
   * the record to nulls.
   *
   * @param keyValue A value that can be mapped to a data model record
   * address.
   */

  public void createRecord( Object keyValue )
    throws AlreadyExistsException
  {
    String message;

    // In anticipation of an error, identify the table.
    String tableName = databaseKey.getTableName();

    // Determine if the object already exists.
    if ( hasRecord( keyValue ) )
    {
      // It does.  Throw an exception.
      message = "Record " + keyValue + " already exists.";
      throw new AlreadyExistsException( message, tableName, keyValue );
    }
    else
    {
      // Add the key value to the list for this table.  Then seed the
      // values in the model with nulls.
      keys.add( keyValue );
      Vector<Object> row = database.getValues( databaseKey, new Integer( 0 ) );
      int rowIndex = keys.indexOf( keyValue );
      rows.put( new Integer( rowIndex ), row );
    }
  }

  /****************************************************************************/

  public Object getKeyValueAt( int rowIndex )
  {
    return( keys.elementAt( rowIndex ) );
  }

  /****************************************************************************/
  /**
   * Return a column vector for the record at <i>keyValue</i>.  The
   * result may be null, indicating that no record is cached for
   * <i>keyValue</i>.
   */

  public Vector<Object> getRow( Object keyValue )
    throws RecordDoesNotExistException
  {
    // Determine that the record associated with keyValue exists.
    int rowIndex = getRowIndex( keyValue );
    if ( rowIndex == -1 )
    {
      // It doesn't.  Throw an exception.
      String tableName = databaseKey.getTableName();
      String message = "Record " + keyValue + " in " + tableName +
	" does not exist.";
      throw new RecordDoesNotExistException( message, tableName, keyValue );
    }

    // Get the row index as an integer to use as a key to fetch the
    // column vector.
    Integer rowKey = new Integer( rowIndex );
    Vector<Object> result = (Vector<Object>) rows.get( rowKey );

    // Detect a empty cache line.
    if ( result == null )
    {
      // Load this cache line.
      result = setRow( rowIndex );
    }

    return result;
  }

  /****************************************************************************/

  public int getRowCount()
  {
    int result = keys.size();

    return( result );
  }

  /****************************************************************************/
  /**
   * Map a key value object to a row index.
   */

  public int getRowIndex( Object keyValue )
  {
    int result;

    // Determine if the search key is a string.
    if ( keyValue instanceof String )
    {
      // It is.  Convert the string to an integer object before
      // attempting a seach.  If the conversion fails then attempt a
      // match based on the literal string value.
      try
      {
	Integer value = new Integer( (String) keyValue );
	result = keys.indexOf( value );
      }
      catch ( NumberFormatException exception )
      {
	result = keys.indexOf( keyValue );
      }
    }
    else
    {
      result = keys.indexOf( keyValue );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the value from the cell with the location,
   * (<I>rowIndex</I>, <I>columnIndex</I>).
   *
   * @param rowIndex  The database record index.
   * @param columnIndex  The database column index.
   */

  public Object getValueAt( int rowIndex, int columnIndex )
  {
    // Get the vector of cell values for the given row from the
    // collection of rows.
    Integer rowKey = new Integer( rowIndex );
    Vector<Object> row = (Vector<Object>) rows.get( rowKey );

    // Detect a empty cache line.
    if ( row == null )
    {
      // Load this cache line.
      row = setRow( rowIndex );
    }

    // Return the element from the vector, which is guaranteed to
    // exist.
    Object result = row.elementAt( columnIndex );

    return( result );
  }

  /****************************************************************************/
  /**
   * Return the value from the cell with <I>columnIndex</I> and
   * <I>keyValue</I> in the key column.
   *
   * @param keyColumnValue The value in the key column in the row with
   *                       the desired value.
   * @param columnIndex  The database column index.
   */

  public Object getValueAt( Object keyValue, int columnIndex )
  {
    Object result = null;

    // Map the keyValue to a record index; fetch a value and return
    // it.
    int rowIndex = getRowIndex( keyValue );
    result = getValueAt( rowIndex, columnIndex );
    return result;
  }

  /****************************************************************************/
  /**
   * Return a boolean indicator that the record corresponding to
   * <i>keyValue</i> exists in this table.
   *
   * @param keyValue The record index.
   */

  public boolean hasRecord( Object keyValue )
  {
    int rowIndex = getRowIndex( keyValue );
    return ( rowIndex != -1 );
  }

  /****************************************************************************/
  /**
   * Mark the cache entry invalid such that a subsequent access to the
   * row will force a re-load operaion.
   *
   * @param rowIndex  The database record index.
   */

  public void invalidateRow( Object keyColumnValue )
  {
    // Remove the row if it exists.
    int rowIndex = getRowIndex( keyColumnValue );
    if ( rowIndex != -1 )
    {
      rows.remove( new Integer( rowIndex ) );
    }
  }

  /****************************************************************************/
  /**
   * Load all the data for the database table, because it isn't
   * efficient to get each row one at a time using the
   * <I>getValueAt()</I> method.  Appropriate for the table view.
   *
   * #see getValueAt
   */

  public void loadData( )
  {
    Vector<Vector<Object>> rowVectors = database.getTable( databaseKey );

    for (int i = 0; i < rowVectors.size(); i++)
    {
      Vector<Object> row = (Vector<Object>) rowVectors.elementAt( i );

      Integer rowKey = new Integer( i );

      if ( ! rows.containsKey( rowKey ) )
      {
	setRow( rowKey, row );
      }
    }
  }

  /****************************************************************************/
  /**
   * Flush the current set of key/row values and obtain new values
   * from the database.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public void reload()
  {
    // Get the set of unique key values for this table.
    keys = database.getKeys( databaseKey );

    // Construct the data container.
    rows = new Hashtable<Integer,Vector<Object>>();
  }

  /****************************************************************************/
  /**
   * Remove a row and its key from the model.
   *
   * @param keyColumnValue  The row ID.
   */

  public void removeRow( Object keyColumnValue )
  {
    // Remove the row from the cache and the key from the set of keys.
    int rowIndex = getRowIndex( keyColumnValue );
    removeRow( rowIndex );
  }

  /****************************************************************************/
  /**
   * Remove a row and its key from the model.
   *
   * @param rowIndex  The row ID.
   */

  public void removeRow( int rowIndex )
  {
    // Remove the row from the cache and the key from the set of keys.
    rows.remove( new Integer( rowIndex ) );
    keys.remove( rowIndex );
  }

  /****************************************************************************/
  /**
   * Set and return the row at <i>rowIndex</i> from the database.
   * Essentially loads the cache line with the values stored in the
   * database value, then returns these values.
   *
   * @param rowIndex  The database record index.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public Vector<Object> setRow( int rowIndex )
  {
    Object keyValue = null;

    // Detect an invalid record index.
    if ( rowIndex == -1 )
    {
      // Set the key value to force the database to return a row of
      // null values.
      keyValue = new Integer( 0 );
    }
    else
    {
      keyValue = keys.elementAt( rowIndex );
    }

    // Fetch a set of values from the database and return it.
    Vector<Object> row = database.getValues( databaseKey, keyValue );
    Integer rowKey = new Integer( rowIndex );
    rows.put( rowKey, row );
    return row;
  }

  /****************************************************************************/
  /**
   * Set the row at <i>rowIndex</i>.  Essentially loads the cache line.
   *
   * @param rowIndex  The database record index.
   * @param row  The values to be stored.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public void setRow( int rowIndex, Vector<Object> row )
  {
    Integer rowKey = new Integer( rowIndex );
    rows.put( rowKey, row );
  }

  /****************************************************************************/
  /**
   * Store a value into the database cache.
   *
   * @param value  The value to be stored.
   * @param rowIndex  The database record index.
   * @param columnIndex  The database column index.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public void setValueAt( Object value, int rowIndex, int columnIndex )
  {
    Integer rowKey = new Integer( rowIndex );

    // Get the column vector for the given row.
    Vector<Object> row = (Vector<Object>) rows.get( rowKey );
    
    // Detect an empty cache line.
    if ( row == null )
    {
      // Load this cache line from the database model.
      row = setRow( rowIndex );
    }

    // Store the value in the cache.
    row.setElementAt( value, columnIndex );
  }

  /****************************************************************************/
  /**
   * Store a value into the database cache.
   *
   * @param value  The value to be stored.
   * @param keyColumnValue The value of the key column for the targeted row.
   * @param columnIndex  The database column index.
   */

  public void setValueAt( Object value, Object keyColumnValue, int columnIndex )
    throws RecordDoesNotExistException
  {
    // Get the column vector for the given row.
    Vector<Object> row = getRow( keyColumnValue );
    
    // Detect an empty cache line.
    if ( row == null )
    {
      // Load the cache line from the database.
      row = setRow( getRowIndex( keyColumnValue ) );
    }

    // Store the value in the cache.
    row.setElementAt( value, columnIndex );
  }

  /****************************************************************************/

}

/******************************************************************************/
