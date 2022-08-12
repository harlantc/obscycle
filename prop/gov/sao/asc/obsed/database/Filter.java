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

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

/******************************************************************************/
/**
 * The <code>Filter</code> object encapsulates all the conditions for
 * a single WHERE clause.
 *
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class Filter extends Object
{
  Vector<Condition> conditions;
    
  /****************************************************************************/
  /**
   * The default constructor.
   */

  public Filter()
  {
    conditions = new Vector<Condition>();
  }

  /****************************************************************************/

  public void addCondition( Condition condition )
  {
    conditions.addElement( condition );
  }

  /****************************************************************************/

  public Vector<Condition> getConditions()
  {
    return( conditions );
  }

  /****************************************************************************/
  /**
   * Returns a Collection of table names with no duplicates.
   */

  public Collection getTableNames()
  {
    HashSet<String> result = new HashSet<String>();

    for ( int i = 0; i < conditions.size(); i++ )
    {
      Condition condition = (Condition) conditions.elementAt( i );

      result.add( condition.getDatabaseKey().getTableName() );
    }

    return( result );
  }

  /****************************************************************************/

  public String toSQL()
  {
    StringBuffer result = new StringBuffer();

    int size = conditions.size();

    if ( size > 0 )
    {
      result.append( " WHERE " );
    }

    for ( int i = 0; i < size; i++ )
    {
      Condition condition = (Condition) conditions.elementAt( i );

      result.append( condition.toSQL() );
    }

    return( result.toString() );
  }

  /****************************************************************************/
  
  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append( "Conditions: " );

    for (int i = 0; i < conditions.size(); i++)
    {
      result.append( conditions.elementAt( i ) + "\n");
    }

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
