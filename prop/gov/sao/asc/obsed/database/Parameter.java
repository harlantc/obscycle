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
import java.util.Iterator;
import java.util.Vector;

/******************************************************************************/
/**
 * The Parameter class encapsulates information that will eventually
 * be processed by a stored procedure.
 */

public class Parameter extends Object
{
  // The set of parameter type values.
  public static final int IN = 0;
  public static final int OUT = 1;
  public static final int INOUT = 2;

  /**
   * Support for model access.
   */
  private Database database;
  private DatabaseModel model;
  private Object keyValue;
  private String tableName;

  /**
   * Contains the set of parameters.
   */
  private Vector<ParameterEntry> container;

  /****************************************************************************/
  /**
   * Construct a parameter object.
   */

  public Parameter()
  {
    container = new Vector<ParameterEntry>();
  }

  /****************************************************************************/
  /**
   * Construct a parameter object.
   */

  public Parameter( String tableName, Object keyValue )
  {
    this.tableName = tableName;
    this.keyValue = keyValue;

    database = Editor.getInstance().getDatabase();
    model = database.getDatabaseModel();

    container = new Vector<ParameterEntry>();
  }

  /****************************************************************************/
  /**
   * Add a parameter.
   */

  public void add( String name, int parameterType, int dbType, Object value )
  {
    ParameterEntry entry = 
      new ParameterEntry( name, parameterType, dbType, value );
    container.add( entry );
  }

  /****************************************************************************/
  /**
   * Add a parameter using the model support to fetch the value.
   */

  public void add( String parameterName, String columnName, int parameterType,
		   int dbType )
  {
    int columnIndex = database.getColumnIndex( tableName, columnName );
    Object value = model.getValueAt( tableName, keyValue, columnIndex );
    ParameterEntry entry = 
      new ParameterEntry( parameterName, parameterType, dbType, value );
    container.add( entry );
  }

  /****************************************************************************/
  /**
   * Add a parameter using the model support to fetch the value.  In
   * this case, the column name and parameter name are the same.
   */

  public void add( String name, int parameterType, int dbType )
  {
    add( name, name, parameterType, dbType );
  }

  /****************************************************************************/
  /**
   * Print out the parameters to the console.
   */

  public void dump()
  {
    for ( int i = 0; i < container.size(); i++ )
    {
      String valStr;
      ParameterEntry entry = (ParameterEntry) container.elementAt( i );
      Object value = entry.get();
      if ( value == null )
      {
	valStr = "NULL";
      }
      else
      {
	valStr = value.toString();
      }
      System.out.println( entry.getName() + ": " + valStr + " { " +
			  entry.getType() + ", " + entry.getDBType() + " }" );
    }
  }

  /****************************************************************************/
  /**
   * Return the database type.
   */

  public int getDBType( int index )
  {
    ParameterEntry entry = (ParameterEntry) container.elementAt( index );
    return entry.getDBType();
  }

  /****************************************************************************/
  /**
   * Return the parameter name..
   */

  public String getName( int index )
  {
    ParameterEntry entry = (ParameterEntry) container.elementAt( index );
    return entry.getName();
  }

  /****************************************************************************/
  /**
   * Return the parameter type.
   */

  public int getType( int index )
  {
    ParameterEntry entry = (ParameterEntry) container.elementAt( index );
    return entry.getType();
  }

  /****************************************************************************/
  /**
   * Return the parameter value..
   */

  public Object get( int index )
  {
    ParameterEntry entry = (ParameterEntry) container.elementAt( index );
    return entry.get();
  }

  /****************************************************************************/
  /**
   * Return the parameter value..
   */

  public Object get( String name )
  {
    Object result = null;
    ParameterEntry entry;

    int N = container.size();
    for ( int i = 0; i < N; i++ )
    {
      entry = (ParameterEntry) container.elementAt( i );
      if ( name.equals( entry.getName() ) )
      {
	result = entry.get();
	break;
      }
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Store a parameter value..
   */

  public void put( int index, Object value )
  {
    ParameterEntry entry = (ParameterEntry) container.elementAt( index );
    entry.put( value );
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
   * Return the number of parameters stored in this object.
   */

  public int size()
  {
    return container.size();
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

}

/******************************************************************************/
