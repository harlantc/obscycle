/*
  Copyrights:
 
  Copyright (c) 1998, 2000 Smithsonian Astrophysical Observatory
 
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

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.TableConfiguration;
import gov.sao.asc.obsed.view.form.FormView;
import gov.sao.asc.obsed.view.form.NewView;
import java.awt.Color;
import java.text.Format;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/******************************************************************************/
/**
 * Base class for table and form components.
 */

public abstract class ComponentEntry
{

  /**
   * Borders for decorating GUI elements.
   */
  static public final Border OPAQUE =
    BorderFactory.createEmptyBorder();
  static public final Border MODIFIED =
    BorderFactory.createLineBorder( Color.blue );

  /**
   * The database object reference.
   */
  protected Database database;

  /**
   * The database table and column identifier.
   */
  protected DatabaseKey databaseKey;

  /**
   * Convenience variable.
   */
  protected DatabaseModel model;

  /**
   * The string representation of a null value.
   */
  protected String nullString;

  /**
   * The object representation of a null value.
   */
  protected Object nullValue;

  /**
   * The GUI component which generates events in response to User
   * changes.
   */
  protected JComponent eventComponent;

  /**
   * The GUI component used to decorate the element.
   */
  protected JComponent viewableComponent;

  /**
   * Support for "indirect" components, i.e. components in the view
   * that are contained in a different database table.  The
   * joinDatabaseKey ....?
   */
  protected DatabaseKey joinDatabaseKey;

  /**
   * ?
   */
  protected String tag;

  /**
   * The current view (form or table) reference.
   */
  protected View view;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public ComponentEntry()
  {
    // Set up the convenience variables.
    Editor editor = Editor.getInstance();
    database = editor.getDatabase();
    model = database.getDatabaseModel();
    view = editor.getCurrentView();
  }

  /****************************************************************************/
  /**
   * Return the column entry object that describes this component.
   *
   * @return The associated column entry object.
   * 
   * @throws ConfigurationException if the component configuration did
   * not specify a column entry.
   */

  public ColumnEntry getColumnEntry()
    throws ConfigurationException
  {
    // Predispose the result to be empty.
    ColumnEntry result = null;

    // Determine that there is an association with a database
    // table/column pair.
    if ( databaseKey != null )
    {
      // There is.  Get the associated column entry.
      DatabaseConfiguration databaseConfiguration = 
        DatabaseConfiguration.getInstance();
      result = databaseConfiguration.getColumnEntry( databaseKey );
    }

    // Return the result.
    return(result);
  }

  /****************************************************************************/
  /* 
   * Add user entered choice (from NEW option) to the list
  */
  public void addChoice(Object newValue, int index)
    throws ConfigurationException
   {
   
    ColumnEntry result = null;
    result = getColumnEntry();
    Vector<Object> choices = result.getChoices();

    choices.addElement(newValue); 

   } 

  /****************************************************************************/

  public DatabaseKey getDatabaseKey()
  {
    return databaseKey;
  }

  /****************************************************************************/

  public Format getDisplayFormat()
    throws ConfigurationException
  {
    Format result = null;

    DatabaseConfiguration configuration = DatabaseConfiguration.getInstance();

    ColumnEntry columnEntry = configuration.getColumnEntry( databaseKey );

    result = columnEntry.getFormat();
      
    return result;
  }

  /****************************************************************************/
  /**
   * Return the GUI component that generates events in response to
   * User generated changes.
   *
   * @returns The GUI event component for this entry.
   */
  public JComponent getEventComponent()
  {
    return eventComponent;
  }

  /****************************************************************************/
  /**
   * Return the database key for the "join" entry.
   */

  public DatabaseKey getJoinDatabaseKey()
  {
    return joinDatabaseKey;
  }

  /****************************************************************************/
  /**
   * Get the string representation of a null value for this component.
   *
   * @returns The string representing a null value.
   */

  public String getNullString()
  {
    return nullString;
  }

  /****************************************************************************/
  /**
   * Get the specified value associated with a null value for this component.
   *
   * @returns The string representing a null value.
   */

  public Object getNullValue()
  {
    return nullValue;
  }

  /****************************************************************************/

  public TableConfiguration getTableConfiguration()
    throws ConfigurationException
  {
    TableConfiguration result;

    DatabaseConfiguration databaseConfiguration = 
      DatabaseConfiguration.getInstance();

    String tableName = databaseKey.getTableName();

    result = databaseConfiguration.getTableConfiguration( tableName );

    return(result);
  }

  /****************************************************************************/

  public String getTag()
  {
    return tag;
  }

  /****************************************************************************/

  public String getTooltip()
    throws ConfigurationException
  {
    DatabaseConfiguration database = DatabaseConfiguration.getInstance();

    ColumnEntry columnEntry = database.getColumnEntry( databaseKey );

    String result = columnEntry.getTooltip();

    return result;
  }

  /****************************************************************************/
  /**
   * Return the GUI component that is used for viewing, as opposed to
   * the component that generates events in response to User generated
   * changes.  Generally these are one in the same, but in some cases,
   * like a text area component, this is not the case.
   *
   * @returns The GUI event component for this entry.
   */
  public JComponent getViewableComponent()
  {
    return viewableComponent;
  }

  /****************************************************************************/
  /**
   * Return an indication of whether or not the component is
   * modifiable.  A component is modifiable under the following
   * conditions:
   *
   * 1) in a FormView if the database key indicates a writable
   * column.
   *
   * 2) in a FormView if the component is a "joined" entry and the
   * join table has a row corresponding to keyValue,
   *
   * 3) in a NewView where the table name of the configuration
   * database key and the component entry database key match,
   *
   * 4) in a NewView when the component entry database key indicates
   * that it is a secondary table entry, i.e. the database key table
   * name is different from the configuration database key table name,
   * the field is modifiable if the join column contains the value
   * "New" in the data model.
   *
   * @param keyValue The record ID corresponding to the joined table.
   */

  public boolean isMutable( Object primaryKeyValue )
  {
    String tableName = null;
    String columnName = null;
    String joinTableName = null;
    String joinColumnName = null;
    int columnIndex;

    // Predispose the result;
    boolean result = false;

    // Determine if this component has a database key entry.
    if ( databaseKey != null )
    {
      // It does.  Determine if the current view is a form view or a new view.
      if ( view instanceof NewView && databaseKey != null )
      {
	// It's a NewView.  Determine if this is a "joined" entry.
	if ( joinDatabaseKey == null )
	{
	  // The entry is not a joined entry.  Make it mutable.
	  result = true;
	}
	else
	{
	  // The entry is a joined entry.  Only allow the entry to be
	  // modified when its join field has a "NEW" value.

	  // Get the join field value.
	  joinTableName = joinDatabaseKey.getTableName();
	  joinColumnName = joinDatabaseKey.getColumnName();
	  columnIndex = database.getColumnIndex( joinTableName, joinColumnName );
	  DatabaseModel databaseModel = database.getDatabaseModel();
	  Object joinKeyValue =
	    databaseModel.getValueAt( joinTableName, primaryKeyValue, columnIndex );
	  if ( joinKeyValue instanceof String &&
	       Constants.NEW.equals( (String) joinKeyValue ) )
	  {
	    result = true;
	  }
	}
      }
      else if ( view instanceof FormView && databaseKey != null &&
		joinDatabaseKey == null )
      {
	// It is a form view containing a primary component.  Fetch the
	// state from the database.
	result = database.isWritable( databaseKey.getTableName(),
				      databaseKey.getColumnName() );
	columnName = databaseKey.getColumnName();
        if (columnName.equals(Constants.SPADDITIONAL))
        {
          result = false;
        }
      }
      else if ( view instanceof FormView && databaseKey != null )
      {
	// It is a form view containing a secondary component.  The
	// component is mutable iff the secondary table has a row
	// corresponding to the indicated key value and the secondary
	// table is writable for the given column.  First get the
	// indirected key value, then determine if a suitable row
	// exists.
	tableName = databaseKey.getTableName();
	columnName = databaseKey.getColumnName();
	joinTableName = joinDatabaseKey.getTableName();
	joinColumnName = joinDatabaseKey.getColumnName();
	columnIndex = database.getColumnIndex( joinTableName, joinColumnName );
	Object indirectedKeyValue =
	  model.getValueAt( joinTableName, primaryKeyValue, columnIndex );
	result = model.hasRecord( tableName, indirectedKeyValue ) &&
	  database.isWritable( tableName, columnName );
        if (columnName.equals("medium"))
        {
          result = false;
        }
      }
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Paint a component with a specified border.
   *
   * @param border The border.  Can be either empty or a particular
   * style, such as modified.
   */

  public void paintBorder( Border border )
  {
    viewableComponent.setBorder( border );
  }

  /****************************************************************************/
  /**
   * Set both the event component and the status component for this
   * entry.  In this case the GIU component both generates events and
   * is used to display status information via border decorations.
   *
   * @param guiComponent The GUI component used for both event
   * generation and displaying status.
   */
  public void setComponents( JComponent guiComponent )
  {
    this.eventComponent = guiComponent;
    this.viewableComponent = guiComponent;
  }

  /****************************************************************************/
  /**
   * Set the event component for this entry.
   *
   * @param guiComponent The GUI component which will generate events
   * in response to User generated changes.
   */
  public void setEventComponent( JComponent guiComponent )
  {
    this.eventComponent = guiComponent;
  }

  /****************************************************************************/
  /**
   * Set the database key value for this component.
   *
   * @param databaseKey The table name, column name pair.
   */

  public void setDatabaseKey( DatabaseKey databaseKey )
  {
    this.databaseKey = databaseKey;
  }

  /****************************************************************************/
  /**
   * Set the join database key value for this component.
   *
   * @param databaseKey The table name, column name pair.
   */

  public void setJoinDatabaseKey( DatabaseKey databaseKey )
  {
    this.joinDatabaseKey = databaseKey;
  }

  /****************************************************************************/
  /**
   * Set the string representation of a null value for this component.
   *
   * @param nullString  The string representing a null value.
   */

  public void setNullString( String nullString )
  {
    this.nullString = nullString;
  }

  /****************************************************************************/
  /**
   * Set the object associated with a null value for this component.
   *
   * @param nullValue  The object representing a null value.
   */

  public void setNullValue( Object nullValue )
  {
    this.nullValue = nullValue;
  }

  /****************************************************************************/
  /**
   * Set the paintable GUI component for this entry.
   *
   * @param guiComponent The GUI component which will be decorated to
   * convey visual status to the User.
   */
  public void setViewableComponent( JComponent guiComponent )
  {
    this.viewableComponent = guiComponent;
  }

  /****************************************************************************/

  public void setTag( String tag )
  {
    this.tag = tag;
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append("Database Key: " + databaseKey + "\n");
    result.append("Tag: " + tag + "\n");

    try
    {
      result.append("Column Entry: " + getColumnEntry() + "\n");
    }
    catch (ConfigurationException exception)
    {
      exception.printStackTrace();
    }

    return result.toString();
  }

  /****************************************************************************/

}

/******************************************************************************/
