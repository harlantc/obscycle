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

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.view.ComponentEntry;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.form.ParseViolationException;
import gov.sao.asc.util.LogClient;
import java.awt.GridBagConstraints;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.text.JTextComponent;

/******************************************************************************/

public class FormComponentEntry extends ComponentEntry
{
  private int anchor;
  private int column;
  private int columnWidth;
  private int fill;
  private int row;
  private int rowHeight;
  private int size;
  private int type;
  private JLabel label;
  private DatabaseKey keyDatabaseKey;

  /**
   * The string to be used to query the database when setting up initial values.
   */
  private String queryString;

  /****************************************************************************/

  public FormComponentEntry()
  {
    super();

    // Set default values.
    anchor = GridBagConstraints.WEST;
    columnWidth = 1;
    rowHeight = 1;
    fill = GridBagConstraints.NONE;
  }

  /****************************************************************************/

  public FormComponentEntry( int type )
  {
    this();
    this.type = type;
  }

  /****************************************************************************/

  public boolean equals( ComponentEntry entry )
  {
    return tag.equalsIgnoreCase( entry.getTag() );
  }

  /****************************************************************************/

  public int getAnchor()
  {
    return(anchor);
  }

  /****************************************************************************/

  public int getColumn()
  {
    return(column);
  }

  /****************************************************************************/

  public int getColumnWidth()
  {
    return(columnWidth);
  }

  /****************************************************************************/

  public int getFill()
  {
    return fill;
  }

  /****************************************************************************/

  public JLabel getLabel()
  {
    return label;
  }

  /****************************************************************************/
  /**
   * Return the query string used to obtain initial values.
   *
   * @returns The query string.
   */

  public String getQueryString()
  {
    return queryString;
  }

  /****************************************************************************/

  public int getRow()
  {
    return(row);
  }

  /****************************************************************************/

  public int getRowHeight()
  {
    return(rowHeight);
  }

  /****************************************************************************/

  public int getSize()
  {
    return(size);
  }

  /****************************************************************************/

  public String getTag()
  {
    return tag;
  }

  /****************************************************************************/

  public int getType()
  {
    return type;
  }

  /****************************************************************************/

  public DatabaseKey getKeyDatabaseKey ()
  {
    return keyDatabaseKey;
  }

  /****************************************************************************/
  /**
   * Return the value of a GUI component.  The returned value will be
   * a representation of the value that is sufficient to repaint the
   * value.
   */

  public Object getValue()
    throws ConfigurationException, ParseException, ParseViolationException
  {
    Object result = null;

    // Case on the component type.
    switch ( type )
    {

    case Constants.OBSIDBUTTON:
      {
        JButton button = (JButton) eventComponent;
	String text = button.getText();
	if ( text != null && !text.equals( Constants.DEFAULT ) )
	{
	  result = new Integer( button.getText() );
	}
	else
	{
	  result = null;
	}
        break;
      }

    case Constants.SEQNBRBUTTON:
      {
        JButton button = (JButton) eventComponent;
	result = button.getText();
        break;
      }

    case Constants.TIMESTAMPBUTTON:
      {
        JButton button = (JButton) eventComponent;
	String timestamp = button.getText();
	if ( timestamp != null && !timestamp.equals( Constants.NONE ) )
	{
	  result = Timestamp.valueOf( button.getText() );
	}
	else
	{
	  result = null;
	}
        break;
      }

    case Constants.COMBOBOX:
    case Constants.KEYCOMBOBOX:
    case Constants.OBSIDCOMBOBOX:
    case Constants.TRIPLEBOX:
    case Constants.PREFERREDBOX:
      {
	// Return the selected item unless it is the sentinel value
	// "NONE" in which case NULL is returned.
        JComboBox comboBox = (JComboBox) eventComponent;
        result = comboBox.getSelectedItem();
	if ( result instanceof String && ((String) result).equals( nullString ) )
	{
	  result = null;
	}
        break;
      }

    case Constants.TEXTAREA:
      {
	// Get the value from the text area component converting the
	// empty string to a null.
	JTextComponent textComponent = (JTextComponent) eventComponent;
        result = textComponent.getText().trim();
	if ( result.equals( "" ) )
	{
	  result = null;
	}
        break;
      }

    case Constants.TEXTFIELD:
      {
	result = getTextFieldValue( (JTextComponent) eventComponent );
	break;
      }

    default:
      // Add error handling.
      break;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Procedural abstraction to handle the processing of a text field change.
   *
   * A text field is a catch-all type.  Each sub-type should
   * eventually have its own processor.
   */

  private Object getTextFieldValue( JTextComponent textComponent )
    throws ConfigurationException, ParseViolationException
  {
    Object result = null;

    // Get the value from the component checking for an empty string,
    // which is returned as a null.
    String text = textComponent.getText().trim();
    if ( text.equals( "" ) )
    {
      result = null;
    }
    else
    {
      // Process a non-empty string based on the SQL type.
      switch( getColumnEntry().getType() )
      {
      case ( Constants.FLOAT ):
      case ( Constants.DOUBLE ):
	try
	{
	  // Convert float and double types on the fly.
          
          
          result = new Double(Double.parseDouble( text ));
       
	}
	catch ( NumberFormatException numberFormatException )
	{
	  // Allow a minus sign.
	  if ( text.equals( "-" ) )
	  {
	    result = "-";
	  }
	  else
	  {
	    result = null;
	    throw new ParseViolationException( (String)"Illegal number" );
	  }
	}
	break;

      case ( Constants.LONG ):
      case ( Constants.INTEGER ):
	// An integer value is straightforward.
        try
        {
	  result = new Integer( text );
        }
	catch ( NumberFormatException numberFormatException )
	{
	  result = null;
	  throw new ParseViolationException( "Illegal number"  );
	}
	break;

      case ( Constants.OBJECT ):
      case ( Constants.STRING ):
	result = text;
	break;

      default:
	// This is an error.
	result = null;
	Throwable t = new Throwable();
	LogClient.logMessage( "Internal error in getValue()" );
	LogClient.printStackTrace( t );
	break;
      }
    }

    return result;
  }

  /****************************************************************************/

  public void setAnchor(int anchor)
  {
    this.anchor = anchor;
  }

  /****************************************************************************/

  public void setColumn(int column)
  {
    this.column = column;
  }

  /****************************************************************************/

  public void setColumnWidth(int columnWidth)
  {
    this.columnWidth = columnWidth;
  }

  /****************************************************************************/

  public void setDatabaseEntry( DatabaseKey databaseKey )
  {
    this.databaseKey = databaseKey;
  }


  /****************************************************************************/
  public void setKeyDatabaseKey ( DatabaseKey databaseKey )
  {
    this.keyDatabaseKey = databaseKey;
  }

  /****************************************************************************/

  public void setFill( int fill )
  {
    this.fill = fill;
  }

  /****************************************************************************/

  public void setLabel( JLabel label )
  {
    this.label = label;
  }

  /****************************************************************************/
  /**
   * Set the query string used when initializing a component.
   *
   * @param query The string passed to the database when initializing
   * a component.
   */

  public void setQueryString( String queryString )
  {
    this.queryString = queryString;
  }

  /****************************************************************************/

  public void setRow(int row)
  {
    this.row = row;
  }

  /****************************************************************************/

  public void setRowHeight(int rowHeight)
  {
    this.rowHeight = rowHeight;
  }

  /****************************************************************************/

  public void setSize(int size)
  {
    this.size = size;
  }

  /****************************************************************************/

  public void setTag( String tag )
  {
    this.tag = tag;
  }

  /****************************************************************************/

  public void setType( int type )
  {
    this.type = type;
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append("Row: " + row + "\n");
    result.append("Row Height: " + rowHeight + "\n");
    result.append("Column: " + column + "\n");
    result.append("Column Width: " + columnWidth + "\n");
    result.append("Event Component: " + eventComponent + "\n");
    result.append("Status Component: " + viewableComponent + "\n");
    result.append("Database Entry: " + databaseKey + "\n");
    result.append("Label: " + label + "\n");
    result.append("Tag: " + tag + "\n");
    result.append("Type: " + type + "\n");
    result.append("Anchor: " + anchor + "\n");

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
