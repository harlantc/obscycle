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

/******************************************************************************/
/**
 * The NewRecordResult class encapsulates information collected while
 * attempting to create a new record.
 */

public class NewRecordResult extends Object
{
  /**
   * The original key value, i.e. the one used to access the data
   * model to fetch parameter values.
   */
  private Object keyValue;

  /**
   * The new key value.  Either null or the ID of a newly created record.
   */
  private Object newKeyValue;

  /**
   * The table name.
   */
  private String tableName;

  /**
   * The column name for the key field.  (optional)
   */
  private String columnName;

  /**
   * The result code generate by the stored procedure trying to create
   * the new record.
   */
  private int resultCode;

  /****************************************************************************/
  /**
   * Construct a NewRecordResult object.
   */

  public NewRecordResult()
  {
  }

  /****************************************************************************/
  /**
   * Construct a NewRecordResult object.
   */

  public NewRecordResult( String tableName, Object newKeyValue,
			  Object keyValue, int resultCode )
  {
    this.tableName = tableName;
    this.keyValue = keyValue;
    this.newKeyValue = newKeyValue;
    this.resultCode = resultCode;
  }

  public NewRecordResult( String tableName, String columnName,
                          Object newKeyValue,
			  Object keyValue, int resultCode )
  {
    this.tableName = tableName;
    this.columnName = columnName;
    this.keyValue = keyValue;
    this.newKeyValue = newKeyValue;
    this.resultCode = resultCode;
  }

  /****************************************************************************/
  /**
   * Return the key value.
   */

  public Object getKeyValue()
  {
    return keyValue;
  }

  /****************************************************************************/
  /**
   * Return the new key value.
   */

  public Object getNewKeyValue()
  {
    return newKeyValue;
  }

  /****************************************************************************/
  /**
   * Return the result code;
   */

  public int getResultCode()
  {
    return resultCode;
  }

  /****************************************************************************/
  /**
   * Return the table name..
   */

  public String getTableName()
  {
    return tableName;
  }

  /****************************************************************************/
  /**
   * Return the column name..
   */

  public String getColumnName()
  {
    return columnName;
  }


  /****************************************************************************/
  /**
   * Mutator for the key value..
   */

  public void setKeyValue( Object keyValue )
  {
    this.keyValue = keyValue;
  }

  /****************************************************************************/
  /**
   * Mutator for the key value..
   */

  public void setResultCode( int resultCode )
  {
    this.resultCode = resultCode;
  }

  /****************************************************************************/
  /**
   * Mutator for the table name.
   */

  public void setTableName( String tableName )
  {
    this.tableName = tableName;
  }

  /****************************************************************************/
  /**
   * Mutator for the column name.
   */

  public void setColumnName( String columnName )
  {
    this.columnName = columnName;
  }

  /****************************************************************************/


  /****************************************************************************/

}

/******************************************************************************/
