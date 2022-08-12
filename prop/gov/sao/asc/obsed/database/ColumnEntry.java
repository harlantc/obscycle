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

import gov.sao.asc.obsed.Constants;
import java.text.Format;
import java.util.Iterator;
import java.util.Vector;

/******************************************************************************/

public class ColumnEntry
{
  /**
   * Set if the column has a reasonably small number of valid values.
   */
  private Vector<Object> choices;

  /**
   * The index into the database model.
   */
  private int columnIndex;

  private Vector<String> constraintEntries;

  /**
   * The default value to be used when creating a new record.
   */
  private Object defaultValue;

  /**
   * The names of tables that this column is also a key for.
   */
  private Vector<String> foreignKeyTables;

  /**
   * Set if the column has display constraints.
   */
  private Format format;

  /**
   * Set if the column has a numeric maximum constraint.
   */
  private Number maximum;

  /**
   * Set if the column has a numeric minimum constraint.
   */
  private Number minimum;

  /**
   * Set to true if the column can be modified by the current database user.
   */
  private boolean mutableFlag;

  /**
   * An optional tag identifying the column.
   */
  private String name;

  /**
   * The database type.
   */
  private int sqlType;

  /**
   * The name of the table containing this column.
   */
  private String tableName;

  /**
   * The toolip displayed on multiple views.
   */
  private String tooltip;

  /**
   * The column type.
   */
  private int type;
  
  /****************************************************************************/

  public ColumnEntry(String name)
  {
    this.name = name;
  }

  /****************************************************************************/

  public ColumnEntry(String name, String tableName)
  {
    this( name );
    
    this.tableName = tableName;
  }

  /****************************************************************************/
  /**
   * Add a constraint object.
   */

  public void addConstraintCheck( String type )
  {
    if ( constraintEntries == null )
    {
      // Create the constraint list.
      constraintEntries = new Vector<String>();
    }

    // Append another type.
    constraintEntries.addElement( type );
  }

  /****************************************************************************/

  public void addForeignKeyTable( String foreignKeyTable )
  {
    if ( foreignKeyTables == null )
    {
      foreignKeyTables = new Vector<String>();
    }

    foreignKeyTables.addElement( foreignKeyTable );
  }

  /****************************************************************************/

  public Vector<Object> getChoices()
  {
    return(choices);
  }

  /****************************************************************************/
  /**
   * Return the column index for this column.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  public int getColumnIndex()
  {
    return columnIndex;
  }

  /****************************************************************************/

  public Vector<String> getConstraintEntries()
  {
    return constraintEntries;
  }

  /****************************************************************************/
  /**
   * Return the default value.
   */

  public Object getDefaultValue()
  {
    return( defaultValue );
  }

  /****************************************************************************/

  public Format getFormat()
  {
    return( format );
  }

  /****************************************************************************/

  public Iterator getForeignKeyTables()
  {
    return( foreignKeyTables.iterator() );
  }

  /****************************************************************************/

  public Number getMaximum()
  {
    Number result = maximum;

    switch (type)
    {
    case (Constants.INTEGER):
      {
        if (maximum == null)
        {
          result = new Integer(Integer.MAX_VALUE);
        }
        break;
      }
    case (Constants.DOUBLE):
      {
        if (maximum == null)
        {
          result = new Double(Double.MAX_VALUE);
        }
        break;
      }
    
    }
    
    return(result);
  }

  /****************************************************************************/

  public Number getMinimum()
  {
    Number result = minimum;

    switch (type)
    {
    case (Constants.INTEGER):
      {
        if (minimum == null)
        {
          result = new Integer(-1 * Integer.MIN_VALUE);
        }
        break;
      }
    case (Constants.DOUBLE):
      {
        if (minimum == null)
        {
          result = new Double(-1.0 * Double.MIN_VALUE);
        }
        break;
      }
    
    }
    return( result );
  }

  /****************************************************************************/

  public String getName()
  {
    return( name );
  }

  /****************************************************************************/

  public String getTableName()
  {
    return( tableName );
  }

  /****************************************************************************/

  public int getSQLType()
  {
    return( sqlType );
  }

  /****************************************************************************/

  public String getTooltip()
  {
    return( tooltip );
  }

  /****************************************************************************/

  public int getType()
  {
    return( type );
  }

  /****************************************************************************/

  public boolean hasForeignKeyTables()
  {
    boolean result = false;

    if ( foreignKeyTables != null )
    {
      result = true;
    }

    return( result );
  }

  /****************************************************************************/

  public boolean isMutable()
  {
    return( mutableFlag );
  }

  /****************************************************************************/

  public void setChoices( Vector<Object> choices )
  {
    this.choices = choices;
  }

  /****************************************************************************/

  public void setColumnIndex( int columnIndex )
  {
    this.columnIndex = columnIndex;
  }

  /****************************************************************************/
  /**
   * Set the default value.
   */

  public void setDefaultValue( Object value )
  {
    defaultValue = value;
  }

  /****************************************************************************/

  public void setFormat( Format format )
  {
    this.format = format;
  }

  /****************************************************************************/

  public void setMaximum( String maximumString )
  {
    switch (type)
    {
    case (Constants.INTEGER):
      {
        maximum = new Integer(maximumString);
        break;
      }
    case (Constants.DOUBLE):
      {
        maximum = new Double(maximumString);
        break;
      }
    }
  }

  /****************************************************************************/

  public void setMinimum(String minimumString)
  {
    switch ( type )
    {
    case (Constants.INTEGER):
      {
        minimum = new Integer(minimumString);
        break;
      }
    case (Constants.DOUBLE):
      {
        minimum = new Double(minimumString);
        break;
      }
    }
  }

  /****************************************************************************/

  public void setName( String name )
  {
    this.name = name;
  }

  /****************************************************************************/

  public void setSQLType( int sqlType )
  {
    this.sqlType = sqlType;
  }

  /****************************************************************************/

  public void setTableName( String tableName )
  {
    this.tableName = tableName;
  }

  /****************************************************************************/

  public void setTooltip(String tooltip)
  {
    this.tooltip = tooltip;
  }

  /****************************************************************************/

  public void setType(String typeString)
  {
    if ( typeString.equalsIgnoreCase("OBJECT") )
    {
      type = Constants.OBJECT;
    }
    else if ( typeString.equalsIgnoreCase("INTEGER") )
    {
      type = Constants.INTEGER;
    }
    else if ( typeString.equalsIgnoreCase("LONG") )
    {
      type = Constants.LONG;
    }
    else if ( typeString.equalsIgnoreCase("STRING") )
    {
      type = Constants.STRING;
    }
    else if ( typeString.equalsIgnoreCase("BOOLEAN") )
    {
      type = Constants.BOOLEAN;
    }
    else if ( typeString.equalsIgnoreCase("FLOAT") )
    {
      type = Constants.FLOAT;
    }
    else if ( typeString.equalsIgnoreCase("DOUBLE") )
    {
      type = Constants.DOUBLE;
    }
    else if ( typeString.equalsIgnoreCase("CHOICE") )
    {
      type = Constants.CHOICE;
    }
    else if ( typeString.equalsIgnoreCase("DATE") )
    {
      type = Constants.DATE;
    }
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append("Name: " + name + "\n");
    result.append("Type: " + type + "\n");
    result.append("Column Index: " + columnIndex + "\n");
    result.append("Tooltip: " + tooltip + "\n");

    if (maximum != null)
    {
      result.append("Maximum: " + maximum + "\n");
    }

    if (minimum != null)
    {
      result.append("Minimum: " + minimum + "\n");
    }

    if (format != null)
    {
      result.append("Format: " + format + "\n");
    }

    if (choices != null)
    {
      result.append("Choices: " + choices + "\n");
    }

    if ( constraintEntries != null )
    {
      for ( int i = 0; i < constraintEntries.size(); i++ )
      {
        result.append("Constraint: " + constraintEntries.elementAt( i ) + "\n");
      }
    }

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
