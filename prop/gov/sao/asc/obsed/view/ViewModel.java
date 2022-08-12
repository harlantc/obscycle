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
import gov.sao.asc.obsed.database.AlreadyExistsException;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.Filter;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.database.TableConfiguration;
import gov.sao.asc.util.LogClient;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/******************************************************************************/
/**
 * A generic interface for getting values from a TableView or FormView
 * model.
 *
 * @author Paul Michael Reilly
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class ViewModel
{
  protected Configuration configuration;

  /**
   * Database access convenience variable.
   */
  protected Database database;

  /**
   * Database model convenience variable.
   */
  protected DatabaseModel databaseModel;

  /**
   * Convenience variable.
   */
  private Editor editor;

  protected FilterMap filterMap;
  protected View view;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public ViewModel( View view, Configuration configuration )
  {
    this.view = view;
    this.configuration = configuration;
    editor = Editor.getInstance();
    database = editor.getDatabase();
    databaseModel = database.getDatabaseModel();
  }

  /****************************************************************************/
  /**
   * Create a new record and initialize the column values to the
   * defaults specified in the configuration object.
   */

  public Object addDefaultRecord()
  {
    ColumnEntry columnEntry;
    Object value;
    Object newKeyValue = null;

    try
    {
      // Get an iterator to use to walk the column entries for the
      // database table presented by this view.  Create a new record
      // as part of the process.
      DatabaseKey key = configuration.getKeyEntry();
      String tableName = key.getTableName();
      newKeyValue = databaseModel.addRecord( tableName );
      DatabaseConfiguration databaseConfiguration =
	DatabaseConfiguration.getInstance();
      TableConfiguration tableConfiguration = 
	databaseConfiguration.getTableConfiguration( tableName );
      Collection columnEntries = tableConfiguration.getColumnEntries();
      Iterator i = columnEntries.iterator();
      while ( i != null && i.hasNext() )
      {
	columnEntry = (ColumnEntry) i.next();
	value = columnEntry.getDefaultValue();
	databaseModel.setValueAt( value, newKeyValue, columnEntry );
      }
    }
    catch ( ConfigurationException exception )
    {
      LogClient.logMessage( exception.getMessage() );
      LogClient.printStackTrace( exception );
    }
    catch ( RecordDoesNotExistException noRecordException )
    {
      LogClient.logMessage( noRecordException.getMessage() );
      LogClient.printStackTrace( noRecordException );
    }

    return newKeyValue;
  }

  /****************************************************************************/
  /**
   * Set the values for the "new" record to the null values.
   */

  public Object addNullRecord()
  {
    DatabaseKey key = configuration.getKeyEntry();
    String table = key.getTableName();
    return databaseModel.addNullRecord( table );
  }

  /****************************************************************************/
  /**
   * Copy the values from the record at <i>keyValue</i> to the new
   * record.  Return the key value which can subsequently be used to
   * access this new record.
   *
   * @param keyValue The data model source row for the copy.
   *
   * @returns A data model index to the copied record.
   */

  public Object addRecord( Object keyValue )
    throws RecordDoesNotExistException
  {
    DatabaseKey key = configuration.getKeyEntry();
    String table = key.getTableName();
    return databaseModel.addRecord( table, keyValue );
  }

  /****************************************************************************/

  public FilterMap getFilterMap()
  {
    return( filterMap );
  }

  /****************************************************************************/
  /**
   * Return the number of rows in the model.
   */

  public int getRowCount( String tableName )
  {
    int result = -1;

    try
    {
      result = databaseModel.getRowCount( tableName );
    }
    catch ( ConfigurationException exception )
    {
      LogClient.logMessage( "Internal consistency error --- ignoring." );
      LogClient.printStackTrace( exception );
      result = 0;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the value of the component identified by the triple: table
   * name, row index and column identifier.
   *
   * @param databaseKey  The key containing database table and column name.
   * @param rowIndex  The record index.  Its interpretation is
   *                  implementation dependent.
   */

  public Object getValueAt( DatabaseKey databaseKey, int rowIndex )
  {
    // Initialize the result.
    Object result = null;

    int mappedRowIndex = rowIndex;

    if ( filterMap != null )
    {
      mappedRowIndex = filterMap.getMappedRowIndex( rowIndex );
    }

    DatabaseKey keyEntry = configuration.getKeyEntry();
    Object keyColumnValue = 
      databaseModel.getKeyValueAt( keyEntry.getTableName(), mappedRowIndex );

    result = getValueAt( databaseKey, keyColumnValue );

    return result;
  }

  /****************************************************************************/
  /**
   * Return the value of the component identified by the database key
   * (which contains the table name and column name) and key column
   * value.
   *
   * @param databaseKey The key containing database table and column name.
   * @param keyColumnValue The value used to identify the row.
   */

  public Object getValueAt( DatabaseKey databaseKey, Object keyColumnValue )
  {
    // Initialize the result.
    Object result = null;

    // Get the column index and fetch the result from the database model.
    String tableName = databaseKey.getTableName();
    String columnName = databaseKey.getColumnName();
    int columnIndex = database.getColumnIndex( tableName, columnName );
    result =
      databaseModel.getValueAt( tableName, keyColumnValue, columnIndex );

    return result;
  }

  /****************************************************************************/
  /**
   * Return an indication that a record at <i>keyValue</i> exists in
   * this table in the database.
   *
   * @param keyValue  The record index.
   */

  public boolean hasRecord( String tableName, Object keyValue )
  {
    return databaseModel.hasRecord( tableName, keyValue );
  }

  /****************************************************************************/
  /**
   * Return the datbase type for the component identified by the pair table
   * name and columnID.
   *
   * @param tableName  The containing database table name.
   * @param columnName  The column name.
   */

  public boolean isMutable( String tableName, String columnName )
  {
    return databaseModel.isMutable( tableName, columnName );
  }

  /****************************************************************************/
  /**
   * Reload the database model.
   */

  public void reload()
  {
    DatabaseKey key = configuration.getKeyEntry();
    String tableName = key.getTableName();
    databaseModel.reload( tableName );
    if (Editor.getInstance().isDebug()) {
      System.out.println("Refreshing " + tableName);
    }

    ComponentEntry entry;
    DatabaseKey primaryKey;
    DatabaseKey secondaryKey;
    Hashtable<String,String> tableCache = new Hashtable<String,String>();
    tableCache.put( tableName, tableName );


    Vector<ComponentEntry> entries = configuration.getComponentEntries();
    Iterator i = entries.iterator();
    while ( i != null && i.hasNext() )
    {
      entry = (ComponentEntry) i.next();
      primaryKey = entry.getDatabaseKey();
      if (primaryKey != null) {
         tableName = primaryKey.getTableName();
         if ( !tableCache.containsKey( tableName ) ) {
           databaseModel.reload( tableName );
           if (Editor.getInstance().isDebug()) {
             System.out.println("Refreshing primary " + tableName);
           }
           tableCache.put( tableName, tableName );
         }
      }
      secondaryKey = entry.getJoinDatabaseKey();
      if (secondaryKey != null) {
         tableName = secondaryKey.getTableName();
         if ( !tableCache.containsKey( tableName ) ) {
           databaseModel.reload( tableName );
           if (Editor.getInstance().isDebug()) {
             System.out.println("Refreshing secondary " + tableName);
           }
           tableCache.put( tableName, tableName );
         }
      }
    }


}
   

  /****************************************************************************/

  public void setFilterMap( FilterMap filterMap )
  {
    this.filterMap = filterMap;
  }

  /****************************************************************************/
  /**
   * Save a value in the model for the cell specified by the database
   * key and row index.
   *
   * @param value  The value to be stored in the model.
   * @param databaseKey The database key for the table and column.
   * @param rowIndex  The index of the record.
   */

  public void setValueAt( Object value, DatabaseKey databaseKey, int rowIndex )
    throws RecordDoesNotExistException
  {
    int reverseMappedRowIndex = rowIndex;

    if ( filterMap != null )
    {
      reverseMappedRowIndex = filterMap.getReverseMappedRowIndex( rowIndex );
    }

    Object keyColumnValue = 
      databaseModel.getKeyValueAt( databaseKey.getTableName(), 
                                   reverseMappedRowIndex );

    databaseModel.setValueAt( value, keyColumnValue, databaseKey );
  }

  /****************************************************************************/
  /**
   * Save a value in the model for the cell specified by the database
   * key and key column value.
   *
   * @param value  The value to be stored in the model.
   * @param databaseKey The database key for the table and column.
   * @param keyColumnValue The value of the key column for the record.
   */

  public void setValueAt( Object value, DatabaseKey databaseKey, 
                          Object keyColumnValue )
    throws RecordDoesNotExistException
  {
    databaseModel.setValueAt( value, keyColumnValue, databaseKey );
  }

  /****************************************************************************/

}

/******************************************************************************/
