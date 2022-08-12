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

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.ForeignKey;
import gov.sao.asc.obsed.database.TableConfiguration;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.Pair;
import gov.sao.asc.util.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JComponent;

/******************************************************************************/

public abstract class Configuration
{
  protected Vector<ComponentEntry> componentEntries;
  protected boolean forceMutable;
  protected DatabaseKey keyEntry;
  protected String title;

  /**
   * Indicates whether or not this view supports the creation of a new
   * record.
   */
  protected boolean supportsNew;

  /**
   * Indicates whether or not this view supports the cloning operation.
   */
  protected boolean supportsClone;

  /**
   * Indicates whether or not this view supports the createion of a
   * new record using default values.
   */
  protected boolean supportsDefault;

  /**
   * Indicates whether or not this view supports the creation of a new
   * record using null values.
   */
  protected boolean supportsNull;

  /**
   * Indicates whether or not this view supports the Delete function.
   */
  protected boolean supportsDelete;

  /****************************************************************************/

  public Configuration()
  {
    componentEntries = new Vector<ComponentEntry>();
  }

  /****************************************************************************/
  /**
   * Returns the index of the table column with the associated
   * <i>databaseKey</i> in the database table.
   */

  public int getColumnIndex( DatabaseKey databaseKey )
  {
    int result = -1;

    for (int i = 0; i < componentEntries.size(); i++)
    {
      ComponentEntry componentEntry = 
        (ComponentEntry) componentEntries.elementAt(i);

      DatabaseKey currentKey = componentEntry.getDatabaseKey();

      if ( ( currentKey != null ) && ( currentKey.equals( databaseKey ) ) )
      {
        result = i;
        break;
      }
    }

    return(result);
  }

  /****************************************************************************/
  /**
   * Returns a <I>Vector</I> of <I>Pair</I>'s with the key being a
   * <I>DatabaseKey</I> and the value being a column label.
   */

  public Vector<Pair> getColumnNames()
  {
    /**
     * The TreeSet object provides a unique sorted set.
     */
    TreeSet<Pair> set = new TreeSet<Pair>();

    try
    {
      Iterator componentEntryIterator = componentEntries.iterator();

      while ( componentEntryIterator.hasNext() )
      {
        ComponentEntry componentEntry = 
          (ComponentEntry) componentEntryIterator.next();
        
        ColumnEntry columnEntry = componentEntry.getColumnEntry();
        
        if ( columnEntry.hasForeignKeyTables() )
        {
          DatabaseConfiguration databaseConfiguration = 
            DatabaseConfiguration.getInstance();

          Iterator foreignKeyTableIterator = columnEntry.getForeignKeyTables();

          while ( foreignKeyTableIterator.hasNext() )
          {
            String foreignKeyTable = (String) foreignKeyTableIterator.next();

            TableConfiguration tableConfiguration = 
              databaseConfiguration.getTableConfiguration( foreignKeyTable );
          
            Vector<String> columnNames = tableConfiguration.getColumnNames();

            Iterator columnNameIterator = columnNames.iterator();

            while ( columnNameIterator.hasNext() )
            {
              String columnName = (String) columnNameIterator.next();

              ForeignKey foreignKey = new ForeignKey( foreignKeyTable, 
                                                      columnName,
                                                      columnEntry.getTableName(), 
                                                      columnEntry.getName() );

              String label = foreignKeyTable + "." + columnName;

              Pair pair = new Pair( foreignKey, label );

              set.add( pair );
            }
          }
        }

        String label = columnEntry.getTableName() + "." + columnEntry.getName();

        Pair pair = new Pair( componentEntry.getDatabaseKey(), label );
        
        set.add( pair );
      }
    }
    catch ( ConfigurationException exception )
    {
      LogClient.printStackTrace( exception );
    }

    Vector<Pair> result = new Vector<Pair>( set );

    return( result );
  }

  /****************************************************************************/

  public Vector<ComponentEntry> getComponentEntries()
  {
    return( componentEntries );
  }

  /****************************************************************************/
  /**
   * Locate and return a component entry with a given GUI component.
   * The given GUI component is generated by some User instigated
   * event.
   *
   * @param guiComponent  The GUI component being matched.
   *
   * @returns The component entry which contains the GUI component or
   * null if no match is found.
   */

  public ComponentEntry getEntry( JComponent guiComponent )
  {
    ComponentEntry result = null;
    ComponentEntry entry;

    // Walk the list of component entries in this configuration.
    Iterator i = componentEntries.iterator();
    while ( i != null && i.hasNext() )
    {
      // Determine if this component is the matching component.
      entry = (ComponentEntry) i.next();
      if ( guiComponent == entry.getEventComponent() )
      {
	result = entry;
	break;
      }
    }

    return result;
  }

  /****************************************************************************/

  public ComponentEntry getEntry( int index )
  {
    ComponentEntry result = 
      (ComponentEntry) componentEntries.elementAt( index );

    return( result );
  }

  /****************************************************************************/
  /**
   * Locate and return a component entry with the given <i>key</i>.
   */

  public ComponentEntry getEntry( DatabaseKey key )
  {
    ComponentEntry result = null;

    for ( int i = 0; i < componentEntries.size(); i++ )
    {
      ComponentEntry entry = (ComponentEntry) componentEntries.elementAt( i );
      DatabaseKey testKey = entry.getDatabaseKey();

      if ( testKey != null && key.equals( testKey ) )
      {
	result = entry;
	break;
      }
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Locate and return a component entry using <i>tableName</i> and
   * <i>columnID</i>
   */

  public ComponentEntry getEntry( String tableName, String columnID )
  {
    DatabaseKey key = new DatabaseKey( tableName, columnID );
    
    ComponentEntry result = getEntry( key );

    return( result );
  }

  /****************************************************************************/
  /**
   * Locate and return a component entry with the given <i>key</i>.
   */

  public ComponentEntry getEntry( String key )
  {
    // Initialize the result.
    ComponentEntry result = null;

    // Loop through all the component entries.
    for ( int i = 0; i < componentEntries.size(); i++ )
    {
      // Get a component entry object to compare to <i>key</i>.
      ComponentEntry entry = (ComponentEntry) componentEntries.elementAt( i );

      // Compare key to the object contained in this entry.  Ignore
      // entries with no database entry.
      DatabaseKey dbKey = entry.getDatabaseKey();
      if ( dbKey != null && 
	   key.equalsIgnoreCase( dbKey.getTableName() + "." +
				 dbKey.getColumnName() ) )
      {
	// This entry is a match.  Return it immediately.
	result = entry;
	break;
      }
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Return the table name for a component not derived from a join.
   */

  public DatabaseKey getKeyEntry()
  {
    return( keyEntry );
  }

  /****************************************************************************/
  /**
   * Return the primary key value, i.e. the main table name and column name.
   *
   * @param databaseElement The element wrapper.
   *
   * @returns The primary table name, column name pair.
   */

  public DatabaseKey getPrimaryKey( Element databaseElement )
  {
    DatabaseKey result;

    // Load the entries from the XML input file.
    String tableName =
      XML.getTextForChildElement( databaseElement, "tableName" );
    String columnName =
      XML.getTextForChildElement( databaseElement, "columnName" );
    result = new DatabaseKey( tableName, columnName );
    if ( tableName == null )
    {
      result = null;
    }
    else
    {
      result = new DatabaseKey( tableName, columnName );
    }
    return result;
  }      

  /****************************************************************************/
  /**
   * Return the secondary key value, i.e. the secondary table name and
   * column name.  This pair is used to resolve an indirection link
   * where two tables are effectively being "join"ed.
   *
   * @param databaseElement The element wrapper.
   *
   * @returns The secondary table name, column name pair.
   */

  public DatabaseKey getSecondaryKey( Element databaseElement )
  {
    DatabaseKey result;

    // Load the entries from the XML input file.
    String tableName =
      XML.getTextForChildElement( databaseElement, "joinTableName" );
    String columnName =
      XML.getTextForChildElement( databaseElement, "joinColumnName" );
    if ( tableName == null )
    {
      result = null;
    }
    else
    {
      result = new DatabaseKey( tableName, columnName );
    }
    return result;
  }      

  /****************************************************************************/

  public Vector<String> getTableNames()
  {
    Vector<String> result = new Vector<String>();

    for (int i = 0; i < componentEntries.size(); i++)
    {
      ComponentEntry componentEntry = 
        (ComponentEntry) componentEntries.elementAt(i);

      DatabaseKey databaseKey = componentEntry.getDatabaseKey();

      if ( databaseKey != null )
      {
        String tableName = databaseKey.getTableName();

        if (! result.contains(tableName) )
        {
          result.addElement(tableName);
        }
      }
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Return the title string, if any, specified in the configuration file.
   */

  public String getTitle()
  {
    return title;
  }

  /****************************************************************************/
  /**
   * Return boolean, specified in the configuration file, indicating
   * whether a "new" view is supported.
   */

  public boolean isNewSupported()
  {
    return supportsNew;
  }

  /****************************************************************************/
  /**
   * Return boolean, specified in the configuration file, indicating
   * whether a record can be cloned in this view.
   */

  public boolean isNewCloneSupported()
  {
    return supportsClone;
  }

  /****************************************************************************/
  /**
   * Return boolean, specified in the configuration file, indicating
   * whether a record can be created with default values.
   */

  public boolean isNewDefaultSupported()
  {
    return supportsDefault;
  }

  /****************************************************************************/
  /**
   * Return boolean, specified in the configuration file, indicating
   * whether a record can be created with null values.
   */

  public boolean isNewNullSupported()
  {
    return supportsNull;
  }

  
  /****************************************************************************/
  /**
   * Return boolean, specified in the configuration file, indicating
   * whether a record can be deleted.
   */
  public boolean isDeleteSupported()
  {
    return supportsDelete;
  }

  /****************************************************************************/

  public void processKey( Node entryNode )
  {
    // Fetch the table name and column name and buld the key entry.
    String table = XML.getTextForChildElement( entryNode, "tableName" );
    String column = XML.getTextForChildElement( entryNode, "columnName" );

    keyEntry = new DatabaseKey( table, column );
  }
 
  /****************************************************************************/

  public void processTitle( Node node )
  {
    // Load the entries from the XML input file.

    Node firstChild = node.getFirstChild();
    if ( firstChild != null )
    {
      title = firstChild.getNodeValue();
    }

  }

  /****************************************************************************/
  /**
   * Process an info node for this view.  Currently this consists of the specification of the new record capabilities.
   *
   * @param node The XML info node.
   */
  public void processInfo( Node node )
  {
    // Load the info entries from the XML input file and initialize
    // the state variables.
    String newString = XML.getTextForChildElement( node, "supportsNew" );
    supportsNew = false;
    supportsClone = false;
    supportsDefault = false;
    supportsNull = false;
    supportsDelete = true;

    // Determine if there is any data to process.
    if ( newString != null )
    {
      // There is.  Check for a clone specification.
      if( newString.indexOf( "clone" ) != -1 )
      {
	supportsNew = true;
	supportsClone = true;
      }
      
      // Check for a default specification.
      if ( newString.indexOf( "default" ) != -1 )
      {
	supportsNew = true;
	supportsDefault = true;
      }
      
      // Check for a null specification.
      if ( newString.indexOf( "null" ) != -1 )
      {
	supportsNew = true;
	supportsNull = true;
      }
      if( newString.indexOf( "noDelete" ) != -1 )
      {
	supportsDelete = false;
      }
    }
  }

  /****************************************************************************/
  /**
   * Rebuild the configuration object by reloading the configuration
   * file.
   */

  public abstract void reload();

  /****************************************************************************/
  /**
   * Force component entries to be mutable when created.
   */

  public void setForceMutable( boolean flag )
  {
    forceMutable = flag;
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append("\n  Key Entry:" + keyEntry + "\n");

    for (int i = 0; i < componentEntries.size(); i++)
    {
      result.append( componentEntries.elementAt(i) );
    }

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
