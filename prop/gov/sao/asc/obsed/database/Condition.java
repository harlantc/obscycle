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

package gov.sao.asc.obsed.database;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import java.sql.Types;
import java.util.Vector;

/******************************************************************************/
/**
 * The <code>Condition</code> encapsulates the information needed for
 * a single WHERE clause condition.
 *
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class Condition extends Object
{
  public static final int NONE = -1;
  public static final int AND = 0;
  public static final int OR = 1;

  private static final int EQUALTO = 0;
  private static final int GREATERTHAN = 1;
  private static final int LESSTHAN = 2;
  private static final int NOTEQUALTO = 3;
  private static final int LIKE = 4;

  private static Vector<String> expressions;

  static
  {
    expressions = new Vector<String>();

    expressions.addElement("equal to");
    expressions.addElement("greater than");
    expressions.addElement("less than");
    expressions.addElement("not equal to");
    expressions.addElement("like");
  }

  protected int combinationOperator;
  protected int conditionOperator;
  protected int type;
  protected DatabaseKey databaseKey;
  protected String value;

  /****************************************************************************/

  public Condition( DatabaseKey databaseKey, int conditionOperator, 
                    String value, int type, int combinationOperator )
  {
    this.databaseKey = databaseKey;
    this.conditionOperator = conditionOperator;
    this.value = value;
    this.type = type;
    this.combinationOperator = combinationOperator;
  }

  /****************************************************************************/

  public Condition( DatabaseKey databaseKey, int conditionOperator, 
                    String value, int type )
  {
    this( databaseKey, conditionOperator, value, type, NONE );
  }

  /****************************************************************************/

  public DatabaseKey getDatabaseKey()
  {
    return( databaseKey );
  }

  /****************************************************************************/

  public int getCombinationOperator()
  {
    return( combinationOperator );
  }

  /****************************************************************************/

  public int getConditionOperator()
  {
    return( conditionOperator );
  }

  /****************************************************************************/

  public static Vector<String> getExpressions()
  {
    return( expressions );
  }

  /****************************************************************************/

  public String getValue()
  {
    return( value );
  }

  /****************************************************************************/

  public int getType()
  {
    return( type );
  }

  /****************************************************************************/

  protected String lookupCombinationOperatorString()
  {
    String result = null;

    switch ( combinationOperator )
    {
    case ( AND ):
      {
        result = " AND ";
        break;
      }
    case ( OR ):
      {
        result = " OR ";
        break;
      }
    }

    return( result );
  }

  /****************************************************************************/

  protected String lookupConditionOperatorString()
  {
    String result = null;

    switch (conditionOperator)
    {
    case ( EQUALTO ):
      {
        result = " = ";
        break;
      }
    case ( GREATERTHAN ):
      {
        result = " > ";
        break;
      }
    case ( LESSTHAN ):
      {
        result = " < ";
        break;
      }
    case ( NOTEQUALTO ):
      {
        result = " <> ";
        break;
      }
    case ( LIKE ):
      {
        result = " LIKE ";
        break;
      }
    }

    return( result );
  }

  /****************************************************************************/

  public String toSQL()
  {
    String result;

    if ( databaseKey instanceof ForeignKey )
    {
      result = toSQL( (ForeignKey) databaseKey );
    }
    else
    {
      result = toSQL( databaseKey );
    }

    return( result );
  }

  /****************************************************************************/

  public String toSQL( DatabaseKey databaseKey )
  {
    StringBuffer result = new StringBuffer();

    result.append( databaseKey.getTableName() );
    result.append( "." );
    result.append( databaseKey.getColumnName() );
    result.append( lookupConditionOperatorString() );

    switch ( type )
    {
    case ( Types.CHAR ):
    case ( Types.VARCHAR ):
    case ( Types.LONGVARCHAR ):
    case ( Types.DATE ):
    case ( Types.TIME ):
    case ( Types.TIMESTAMP ):
      {
        result.append( "'" + value + "'" );
        break;
      }
    default:
      {
        result.append( value );
        break;
      }
    }

    if (combinationOperator >= 0)
    {
      result.append( lookupCombinationOperatorString() );
    }

    return( result.toString() );
  }

  /****************************************************************************/

  public String toSQL( ForeignKey foreignKey )
  {
    StringBuffer result = new StringBuffer();

    result.append( foreignKey.getTableName() );
    result.append( "." );
    result.append( foreignKey.getColumnName() );
    result.append( lookupConditionOperatorString() );

    switch ( type )
    {
    case ( Types.CHAR ):
    case ( Types.VARCHAR ):
    case ( Types.LONGVARCHAR ):
    case ( Types.DATE ):
    case ( Types.TIME ):
    case ( Types.TIMESTAMP ):
      {
        result.append( "'" + value + "'" );
        break;
      }
    default:
      {
        result.append( value );
        break;
      }
    }

    result.append( " AND " );
    result.append( foreignKey.getForeignTableName() );
    result.append( "." );
    result.append( foreignKey.getForeignColumnName() );
    result.append( " = " );
    result.append( foreignKey.getTableName() );
    result.append( "." );
    result.append( foreignKey.getForeignColumnName() );

    if (combinationOperator >= 0)
    {
      result.append( lookupCombinationOperatorString() );
    }

    return( result.toString() );
  }

  /****************************************************************************/

  public String toString()
  {
    return( toString( databaseKey ) );
  }
  
  /****************************************************************************/
  
  public String toString( DatabaseKey databaseKey )
  {
    StringBuffer result = new StringBuffer();

    result.append( "DatabaseKey: " + databaseKey + "\n" );

    result.append( "Condition Operator: " );
    result.append( lookupConditionOperatorString() );
    result.append( "\n" );

    result.append( "Value: " + value + "\n" );

    result.append( "Combination Operator: " );
    result.append( lookupCombinationOperatorString() );

    result.append( "\n" );
    result.append( "\n" );

    return( result.toString() );
  }

  /****************************************************************************/
  
  public String toString( ForeignKey foreignKey )
  {
    StringBuffer result = new StringBuffer();

    result.append( "ForeignKey: " + foreignKey + "\n" );

    result.append( "Condition Operator: " );
    result.append( lookupConditionOperatorString() );
    result.append( "\n" );

    result.append( "Value: " + value + "\n" );

    result.append( "Combination Operator: " );
    result.append( lookupCombinationOperatorString() );

    result.append( "\n" );
    result.append( "\n" );

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
