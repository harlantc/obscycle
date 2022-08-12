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
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.Filter;
import gov.sao.asc.util.Pair;
import java.util.Vector;

/******************************************************************************/
/**
 * Provides a mapping between the sequential table view row indices
 * and the potentially sparse database table row indices returned from
 * a SQL SELECT with a WHERE clause.
 */

public class FilterMap
{
  private Vector<Pair> pairs;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public FilterMap( DatabaseKey keyEntry, Filter filter )
  {
    pairs = new Vector<Pair>();

    Database database = Editor.getInstance().getDatabase();

    // Get the keys for all the rows
    Vector<Object> keys = database.getKeys( keyEntry );

    // Get the keys for the rows that match the filter
    Vector<Object> filteredKeys = database.getFilteredKeys( keyEntry, filter );

    // Store the actual indices for the keys that are in the set of
    // filtered keys
    for (int i = 0; i < filteredKeys.size(); i++)
    {
      Object filteredKey = filteredKeys.elementAt( i );

      int actualIndex = keys.indexOf( filteredKey );

      Pair pair = new Pair( new Integer( i ), new Integer( actualIndex ) );

      pairs.addElement( pair );
    }
  }

  /****************************************************************************/
  /**
   * Map a sequential table view row index to a database table row index.
   */

  public int getMappedRowIndex( int rowIndex )
  {
    int result = rowIndex;

    for (int i = 0; i < pairs.size(); i++)
    {
      Pair pair = (Pair) pairs.elementAt( i );

      Integer key = (Integer) pair.getKey();

      if ( rowIndex == key.intValue() )
      {
        Integer value = (Integer) pair.getValue();

        result = value.intValue();

        break;
      }
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Map a database table row index to a sequential table view row
   * index.
   */

  public int getReverseMappedRowIndex( int actualRowIndex )
  {
    int result = actualRowIndex;

    for (int i = 0; i < pairs.size(); i++)
    {
      Pair pair = (Pair) pairs.elementAt( i );

      Integer value = (Integer) pair.getValue();

      if ( actualRowIndex == value.intValue() )
      {
        Integer key = (Integer) pair.getKey();

        result = key.intValue();

        break;
      }
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Return the number of sequential table view row indices.
   */

  public int getSize()
  {
    return( pairs.size() );
  }

  /****************************************************************************/

}

/******************************************************************************/
