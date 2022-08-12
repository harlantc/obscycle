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

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import gov.sao.asc.obsed.database.DatabaseKey;

/******************************************************************************/

public class ChangeListEvent
{
  public static final int ROW_DELETED = 0;
  public static final int ROW_ADDED = 1;
  public static final int ROW_MODIFIED = 2;

  public int state;

  private int rowIndex;
  private String columnName;
  private String tableName;

  // If the addFlag is set to true, this change event indicates an
  // addition to the change list; otherwise this is a removal or
  // `clear' from the change list.
  boolean addFlag;

  /**
   * The value associated with this event.
   */
  private Object value;

  /**
   * The record ID.
   */
  private Object keyValue;

  /****************************************************************************/

  public ChangeListEvent( DatabaseKey databaseKey, int rowIndex,
			  Object newValue, boolean addFlag )
  {
    if ( databaseKey == null )
    {
      this.tableName = null;
      this.columnName = null;
    }    
    else
    {
      this.tableName = databaseKey.getTableName();
      this.columnName = databaseKey.getColumnName();
    }
    this.value = newValue;
    this.rowIndex = rowIndex;
    this.addFlag = addFlag;
    setState();
  }

  /****************************************************************************/

  public ChangeListEvent( String tableName, int rowIndex, boolean addFlag )
  {
    this.tableName = tableName;
    this.columnName = null;
    this.value = null;
    this.rowIndex = rowIndex;
    this.addFlag = addFlag;
    setState();
  }

  /****************************************************************************/
  /**
   * Construct an event for a row deletion.
   *
   * @param  The table from which the row was deleted.
   * @param  The delete row ID.
   */

  public ChangeListEvent( String tableName, Object keyValue )
  {
    this.tableName = tableName;
    this.keyValue = keyValue;
    this.columnName = null;
    this.value = null;
    this.rowIndex = -2;
    this.addFlag = false;
    setState();
  }

  /****************************************************************************/

  public ChangeListEvent( boolean addFlag )
  {
    this( null, -1, null, addFlag );
  }

  /****************************************************************************/

  public boolean getAddFlag()
  {
    return( addFlag );
  }
  
  /****************************************************************************/

  public String getColumnName()
  {
    return( columnName );
  }
  
  /****************************************************************************/
  /**
   * Return the record ID corresponding to the event.
   */

  public Object getKeyValue()
  {
    return keyValue;
  }
  
  /****************************************************************************/
  /**
   * Return the change state.
   */

  public int getState()
  {
    return state;
  }
  
  /****************************************************************************/

  public int getRowIndex()
  {
    return( rowIndex );
  }
  
  /****************************************************************************/

  public String getTableName()
  {
    return( tableName );
  }
  
  /****************************************************************************/
  /**
   * Return the value associated with this event.
   */

  public Object getValue()
  {
    return value;
  }
  
  /****************************************************************************/
  /**
   * Set the current state based on the value of the row index.
   */

  public void setState()
  {
    switch ( rowIndex )
    {
    case -1:
      state = ChangeListEvent.ROW_ADDED;
      break;
      
    case -2:
      state = ChangeListEvent.ROW_DELETED;
      break;
      
    default:
      state = ChangeListEvent.ROW_MODIFIED;
      break;
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
