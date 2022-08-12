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

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

/******************************************************************************/

public class ChangeList extends Object
{
  /**
   * The <I>list</I> is a Hashtable with each key being a rowIndex
   * (Integer) and each value being another Hashtable with each key
   * being a tableName (String) and each value being a Vector of
   * columnIndices (Integer).
   */
  private Hashtable changeList;

  /****************************************************************************/

  public ChangeList()
  {
    changeList = new Hashtable();
  }

  /****************************************************************************/
  /**
   * Adds <I>column</I> to the Vector of changes for <I>tableName</I>
   * in the <I>rowIndex</I>.  This is done by looking up the
   * rowChangeList in the changeList for the particular <I>row</I>.  Then
   * looking up the Vector of columnIndices from the rowChangeList for
   * the tableName associated with the changed cell.  Then adding the
   * column to this Vector.  If either the rowChangeList or the
   * columnIndices doesn't exist yet, it is created.
   *
   * @see #changeList
   */

  public void addChange( Integer rowIndex, String tableName, 
                         Integer columnIndex)
  {
    Hashtable rowChangeList = (Hashtable) changeList.get(rowIndex);

    if ( rowChangeList == null )
    {
      rowChangeList = new Hashtable();
      
      changeList.put(rowIndex, rowChangeList);
    }

    Vector columnIndices = (Vector) rowChangeList.get(tableName);
    
    if ( columnIndices == null )
    {
      columnIndices = new Vector();
      
      rowChangeList.put(tableName, columnIndices);
    }

    if (! columnIndices.contains( columnIndex ) )
    {
      columnIndices.addElement( columnIndex );
    }
  }

  /****************************************************************************/

  public void clearRowChanges(Integer rowIndex)
  {
    changeList.remove( rowIndex );
  }

  /****************************************************************************/

  public void clearChanges( Object keyValue )
  {
    //changeCache.remove( findUpdateCacheKey( keyValue ) );

    // Handle the per observation Save/Cancel buttons
    //if ( keyValue.toString().equals( navPanel.getKeyValue().toString() ) )
    {
      // Disable the Save/Cancel buttons.
      //saveButton.setEnabled( false );
      //cancelButton.setEnabled( false );
    }

    // See if all changes have been saved.
    if ( true ) //changeCache.size() == 0 )
    {
      //saveAllButton.setEnabled( false );
      //cancelAllButton.setEnabled( false );
    }
  }

  /****************************************************************************/

  public void clearAllChanges()
  {
    changeList = new Hashtable();
  }

  /****************************************************************************/
  /**
   * Returns list of rowIndices where changes have occured.
   */

  public Vector getChanges()
  {
    Vector result = new Vector();

    Enumeration rowIndices = changeList.keys();

    while ( rowIndices.hasMoreElements() )
    {
      result.addElement( rowIndices.nextElement() );
    }
    
    return(result);
  }

  /****************************************************************************/
  /**
   * Returns list of tableNames for the changes that have occured in
   * <I>rowIndex</I>.
   */

  public Vector getChangesForRow(Integer rowIndex)
  {
    Vector result = new Vector();

    Hashtable rowChangeList = (Hashtable) changeList.get(rowIndex);

    Enumeration tableNames = rowChangeList.keys();

    while ( tableNames.hasMoreElements() )
    {
      result.addElement( tableNames.nextElement() );
    }
    
    return(result);
  }

  /****************************************************************************/
  /**
   * Returns list of columnNames for the changes that have occured in
   * <I>rowIndex</I> for <I>tableName</I>.
   */

  public Vector getChangesForTable(Integer rowIndex, String tableName)
  {
    Vector result = new Vector();

    Hashtable tableNames = (Hashtable) changeList.get(rowIndex);

    result = (Vector) tableNames.get(tableName);

    return(result);
  }

  /****************************************************************************/
  /**
   * Helper function used to locate an entry in the change cache for
   * the given key value.
   */

  public Properties findUpdateCache( Object keyValue )
  {
    Properties result = null;

    Object key = findUpdateCacheKey( keyValue );
    if ( key != null )
    {
      // result = (Properties) changeCache.get( key );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Helper function used to locate an entry in the change cache for
   * the given key value.
   */

  public Object findUpdateCacheKey( Object keyValue )
  {
    Object result = null;

    // Use a string based comparison against the keys in the change
    // cache to locate a suitable entry.
    /*
    for ( Enumeration e = changeCache.keys(); e.hasMoreElements(); )
    {
      Object key = e.nextElement();
      String keyStr = key.toString();
      String keyValueStr = keyValue.toString();
      System.err.println( "Comparing {" + keyStr + "} and {" + keyValueStr + "}" );
      if ( keyStr.equalsIgnoreCase( keyValueStr ) )
      {
	// Found the right key. Return it, immediately.
	result = key;
	break;
      }
    }
    */
    return result;
  }

  /****************************************************************************/
  /**
   *  Return an indication that an observation has been modified.
   *
   *  Return true iff the observation has been modified.
   */

  public boolean isDirty( Object keyValue )
  {
    Object key = findUpdateCacheKey( keyValue );
    return ( key != null);
  }

  /****************************************************************************/
  /**
   *  Return an indication that an observation has been modified.
   */

  public boolean isDirty()
  {
    // tbd
    return ( false ); // key != null );
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    Vector rowIndices = getChanges();

    for (int i = 0; i < rowIndices.size(); i++)
    {
      Integer rowIndex = (Integer) rowIndices.elementAt(i);

      result.append("Row index: " + rowIndex + "\n");

      Vector tableNames = getChangesForRow(rowIndex);

      for (int j = 0; j < tableNames.size(); j++)
      {
        String tableName = (String) tableNames.elementAt(j);
        
        result.append("Table name: " + tableName + "\n");
        
        Vector columnIndices = 
          (Vector) getChangesForTable(rowIndex, tableName);
        
        for (int k = 0; k < columnIndices.size(); k++)
        {
          Integer columnIndex = (Integer) columnIndices.elementAt(k);
          
          result.append("Column index: " + columnIndex + "\n");
        }
      }
    }

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
