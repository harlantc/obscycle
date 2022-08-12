/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting documentation, and that the name of the Smithsonian
  Astrophysical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN
  ASTROPHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
  AND FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR
  THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

package gov.sao.asc.obsed.database;

/******************************************************************************/

import gov.sao.asc.util.LogClient;
import java.util.Hashtable;
import gov.sao.asc.obsed.view.ConfigurationException;

/******************************************************************************/

public class DatabaseConfiguration
{
  static DatabaseConfiguration instance;
  Hashtable<String,TableConfiguration> tableConfigurations;

  /****************************************************************************/

  public DatabaseConfiguration()
  {
    tableConfigurations = new Hashtable<String,TableConfiguration>();
  }

  /****************************************************************************/

  public static DatabaseConfiguration getInstance()
  {
    if (instance == null)
    {
      instance = new DatabaseConfiguration();
    }

    return(instance);
  }

  /****************************************************************************/

  public ColumnEntry getColumnEntry( DatabaseKey databaseKey )
    throws ConfigurationException
  {
    ColumnEntry result;

    TableConfiguration tableConfiguration = 
      getTableConfiguration( databaseKey.getTableName() );

    result = tableConfiguration.getColumnEntry( databaseKey.getColumnName() );

    return(result);
  }

  /****************************************************************************/
  /**
   * Return a key column name for a particular table.
   *
   * @param tableName  The table.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public String getKeyColumnName( String tableName )
  {
    // Initialize the result.
    String result = null;

    try
    {
      result = getTableConfiguration( tableName ).getKeyColumnName();
    }
    catch ( ConfigurationException ce )
    {
      LogClient.printStackTrace( ce );
    }

    return( result );
  }

  /****************************************************************************/

  public TableConfiguration getTableConfiguration( String tableName )
    throws ConfigurationException
  {
    TableConfiguration result;

    if ( tableConfigurations.containsKey( tableName ) )
    {
      result = (TableConfiguration) tableConfigurations.get( tableName );
    }
    else
    {
      result = new TableConfiguration( tableName );

      tableConfigurations.put( tableName, result );
    }

    return( result );
  }

  /****************************************************************************/

}

/******************************************************************************/
