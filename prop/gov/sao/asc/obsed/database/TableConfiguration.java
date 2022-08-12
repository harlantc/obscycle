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
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.Pair;
import gov.sao.asc.util.XML;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/******************************************************************************/

public class TableConfiguration extends Object
{
  private Hashtable<String,ColumnEntry> columns;
  private Hashtable<String,ConstraintEntry> constraints;
  private Database database;
  private String tableName;
  private String keyColumnName;

  /****************************************************************************/

  public TableConfiguration(String tableName)
  {
    this.tableName = tableName;

    database = Editor.getInstance().getDatabase();

    columns = new Hashtable<String,ColumnEntry>();

    processConfigurationFile();
  }

  /****************************************************************************/

  public ColumnEntry getColumnEntry( String columnName )
  {
    ColumnEntry result = (ColumnEntry) columns.get( columnName );

    return( result );
  }

  /****************************************************************************/
  /**
   * Return the collection of column entries for this table.
   */

  public Collection getColumnEntries()
  {
    return( columns.values() );
  }

  /****************************************************************************/
  /**
   * Returns a <I>Vector</I> of column names.
   */

  public Vector<String> getColumnNames()
  {
    Vector<String> result = new Vector<String>();

    Enumeration keys = columns.keys();

    while ( keys.hasMoreElements() )
    {
      Object key = keys.nextElement();

      ColumnEntry columnEntry = (ColumnEntry) columns.get( key );

      result.addElement( columnEntry.getName() );
    }

    return( result );
  }

  /****************************************************************************/

  public ConstraintEntry getConstraintEntry( String constraintName )
  {
    ConstraintEntry result = (ConstraintEntry) constraints.get(constraintName);

    return( result );
  }

  /****************************************************************************/

  public String getKeyColumnName()
  {
    return( keyColumnName );
  }

  /****************************************************************************/

  public Vector<Object> processChoices( Node choicesNode )
  {
    Vector<Object> result = new Vector<Object>();

    NodeList itemNodeList = choicesNode.getChildNodes();

    for ( int i = 0; i < itemNodeList.getLength(); i++ )
    {
      Node item = itemNodeList.item( i );

      if ( item instanceof Element )
      {
        result.addElement( item.getFirstChild().getNodeValue() );
      }
    }

    return(result);
  }

  /****************************************************************************/

  public void processConfigurationFile()
  {
    URL url = null;
    URI uri = null;

    try
    {
      url = Editor.getInstance().getConfigurationURL( tableName + "Table.xml" );
      uri = new URI(url.toString());

      Document document = null;
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      document =  builder.parse(uri.toString());


      Element root = document.getDocumentElement();

      // Process the tableName.
      NamedNodeMap attributes = root.getAttributes();
      Node tableNameNode = attributes.getNamedItem("name");
      tableName = attributes.getNamedItem("name").getNodeValue();

      // Process the keyColumnName
      Node keyColumnNameNode = attributes.getNamedItem("key");
      keyColumnName = attributes.getNamedItem("key").getNodeValue();

      // Process the column nodes.
      NodeList columnNodeList = root.getElementsByTagName("column"); 
      processColumns(columnNodeList);

    }
    catch ( MalformedURLException malformedURLException )
    {
      System.out.println("URL: " + url);
      malformedURLException.printStackTrace();
      LogClient.printStackTrace(malformedURLException);
    }
    catch ( IOException ioException )
    {
      System.out.println("URL: " + url);
      ioException.printStackTrace();
      LogClient.printStackTrace(ioException);
    }
    catch ( SAXParseException saxParseException )
    {
      System.out.println("** Parsing error" +
                          ", line " + saxParseException.getLineNumber() +
                          ", uri " + saxParseException.getSystemId());
      System.out.println("   " + saxParseException.getMessage());
    }
    catch ( SAXException saxException )
    {
      Exception	x = saxException.getException();
      
      ((x == null) ? saxException : x).printStackTrace();
    }
    catch ( Exception exc ) {
         exc.printStackTrace();
    }
  }

  /****************************************************************************/

  public ColumnEntry processColumn( Node columnNode )
  {
    NamedNodeMap attributes = columnNode.getAttributes();

    Node columnNameNode = attributes.getNamedItem("name");

    String columnName = attributes.getNamedItem("name").getNodeValue();

    ColumnEntry result = new ColumnEntry(columnName);

    result.setTableName( tableName );

    // Get and set the column index for this column.  Supply the key
    // column name so the database does not blow out the stack
    // recursing infinitely.
    int columnIndex = database.getColumnIndex( tableName, columnName );

    result.setColumnIndex( columnIndex );

    String typeString = XML.getTextForChildElement(columnNode, "type");
          
    result.setType( typeString );

    String tooltip = XML.getTextForChildElement(columnNode, "tooltip");
          
    result.setTooltip( tooltip );

    processForeignKeyTables( XML.getChildNodes( columnNode, "foreignKeyTable"), 
                             result );

    int sqlType = database.getSQLType( tableName, columnName );

    result.setSQLType( sqlType );

    if (result.getType() == Constants.CHOICE)
    {
      Element choicesNode = XML.getChildElement(columnNode, "choices");
        
      Vector<Object> choices;

      if ( choicesNode != null )
      {
	choices = processChoices(choicesNode);
      }
      else
      {
	choices = database.getChoices( tableName, columnName );
      }

      result.setChoices(choices);
    }
    else if ( (result.getType() == Constants.DOUBLE) ||
             ( result.getType() == Constants.INTEGER) )
    {
      Element numberNode = XML.getChildElement(columnNode, "number");

      if (numberNode != null)
      {
	boolean hasConstraint = false;

        String maximumString = XML.getTextForChildElement(numberNode, "maximum");

        if (maximumString != null)
        {
          result.setMaximum(maximumString);
	  hasConstraint = true;
        }
        
        String minimumString = XML.getTextForChildElement(numberNode, "minimum");
        
        if (minimumString != null)
        {
          result.setMinimum(minimumString);
	  hasConstraint = true;
        }
      
        String formatString = XML.getTextForChildElement( numberNode, "format" );
      
        if ( formatString != null )
        {
          result.setFormat( new DecimalFormat( formatString ) );
        }

	// Register a bounds constraint check for this column.
	if ( hasConstraint )
	{
	  int type = result.getType();
	  switch ( type )
	  {
	  case Constants.FLOAT:
	  case Constants.DOUBLE:
	    result.addConstraintCheck( "boundsf" );
	    break;
	    
	  case Constants.INTEGER:
	    result.addConstraintCheck( "boundsi" );
	    break;
	    
	  default:
	    // TBD, ignore errors for now.
	  }
	}
      }
    }

    processConstraints( XML.getChildNodes( columnNode, "constraint"), result );

    return(result);
  }

  /****************************************************************************/

  public void processColumns( NodeList columnNodeList )
  {
    for ( int i = 0; i < columnNodeList.getLength(); i++ )
    {
      Node columnNode = columnNodeList.item( i );

      ColumnEntry columnEntry = processColumn( columnNode );

      columns.put( columnEntry.getName(), columnEntry );
    }
  }

  /****************************************************************************/

  public void processConstraints( Vector<Node> constraintElements,
				  ColumnEntry columnEntry )
  {
    // Loop through each constraint spec.
    for (int i = 0; i < constraintElements.size(); i++)
    {
      // Process a constraint.
      Node node = (Node) constraintElements.elementAt(i);

      // Add a constraint type to the column entry.
      String type = node.getFirstChild().getNodeValue();

      columnEntry.addConstraintCheck( type );
    }
  }

  /****************************************************************************/

  public void processForeignKeyTables( Vector<Node> foreignKeyTableElements,
                                       ColumnEntry columnEntry )
  {
    // Loop through each foreign key table
    for (int i = 0; i < foreignKeyTableElements.size(); i++)
    {
      // Process a foreign key table.
      Node node = (Node) foreignKeyTableElements.elementAt(i);

      // Add a foreign key table to the column entry.
      String foreignKeyTable = node.getFirstChild().getNodeValue();

      columnEntry.addForeignKeyTable( foreignKeyTable );
    }
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    Enumeration myenum = columns.keys();

    while ( myenum.hasMoreElements() )
    {
      String key = (String) myenum.nextElement();

      ColumnEntry column = (ColumnEntry) columns.get(key);

      result.append("Column: " + column + "\n");
    }

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
