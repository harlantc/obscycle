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
import java.sql.SQLException;

/******************************************************************************/

public class DatabaseKey extends Object
  implements Comparable
{
  private String tableName;
  private String columnName;
  
  /****************************************************************************/

  public DatabaseKey( String tableName, String columnName )
  {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  /****************************************************************************/

  public int compareTo( Object object ) 
  {
    return( compareTo( (DatabaseKey) object ) );
  }

  /****************************************************************************/

  public int compareTo( DatabaseKey databaseKey ) 
  {
    int result = databaseKey.getTableName().compareTo( tableName );

    if ( result == 0 )
    {
      result = databaseKey.getColumnName().compareTo( columnName );
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Returns true if the two DatabaseKey's have the same table name
   * and column name.
   */

  public boolean equals( DatabaseKey databaseKey )
  {
    boolean result = false;

    if ( databaseKey.getTableName().equals( tableName ) &&
         databaseKey.getColumnName().equals( columnName ) )
    {
      result = true;
    }

    return( result );
  }

  /****************************************************************************/

  public String getColumnName()
  {
    return( columnName );
  }

  /****************************************************************************/

  public String getTableName()
  {
    return( tableName );
  }

  /****************************************************************************/

  public void setColumnName( String columnName )
  {
    this.columnName = columnName;
  }

  /****************************************************************************/

  public void setTableName( String tableName )
  {
    this.tableName = tableName;
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append( "Table Name: " + tableName + "\n" );
    result.append( "Column Name: " + columnName + "\n" );

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
