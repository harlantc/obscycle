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
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.AlreadyExistsException;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.form.FormViewController;
import gov.sao.asc.util.LogClient;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/******************************************************************************/
/**
 * A ComboBoxController handles events on the combo box.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class ComboBoxController extends Object
  implements ActionListener, ItemListener
{
  /**
   * The combo box reference.
   */

  private JComboBox comboBox;

  /**
   * The configuration spec.
   */

  private FormComponentEntry componentEntry;

  /**
   * The controller reference.
   */

  private FormViewController controller;

  /**
   * For a key combo box, the last selected value.
   */

  private Object keyValue;

  /****************************************************************************/
  /**
   * Construct ...
   */

  ComboBoxController( JComboBox comboBox, FormComponentEntry componentEntry,
		      FormViewController controller )
  {
    this.comboBox = comboBox;
    this.componentEntry = componentEntry;
    this.controller = controller;
  }

  /****************************************************************************/
  /**
   * 
   */

  public void actionPerformed( ActionEvent event )
  {

  }

  /****************************************************************************/
  /**
   * Add an item to this combo box.  Pop up a dialog box to select the
   * item from the user.
   *
   * @returns A boolean.  true means to continue processing.  false
   * indicates a cancel operation.
   */

  public boolean addNewItem()
  {
    // Predispose the result to continue.
    boolean result = true;

    // Popup a dialog to get another choice.
    String newValue = JOptionPane.showInputDialog( " Enter a new value: " );

    // Determine if the new choice was cancelled.
    if ( newValue != null )
    {
      // Walk the current entries to see if the user entered an
      // existing item.
      int N = comboBox.getItemCount();
      for ( int i = 0; i < N; i++ )
      {
	// Determine if the new value matches the current list item.
	if ( newValue.equalsIgnoreCase( comboBox.getItemAt( i ).toString() ) )
	{
	  // It does.  Inform the User and reject the new value.
	  JOptionPane.showMessageDialog( null, "Value already exists --- ignored" );
	  newValue = null;
	  break;
	}
      }
    }

    // Check for a cancellation, either explicit or implicit.
    if ( newValue == null )
    {
      // Restore the deselected choice effectively cancelling the
      // "New" change.
      result = false;
      controller.setUpdatingGUI( true );
      controller.updateGUI();
      controller.setUpdatingGUI( false );
    }
    else
    {
      // Replace the "New" choice with the new value and add
      // another "New" choice.
      int index = comboBox.getSelectedIndex();
      comboBox.addItem( newValue );
      comboBox.setSelectedItem( newValue );
      //comboBox.insertItemAt( newValue, index );
      //comboBox.removeItemAt( index + 1 );
      //comboBox.insertItemAt( Constants.NEW, index + 1 );

      try 
      {
        componentEntry.addChoice(newValue,index);
      }
      catch (ConfigurationException exception) 
      {
        LogClient.printStackTrace( exception );
      }
 

      // Inform the view controller that the component has changed.
      controller.processGUIChange( comboBox );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Handle a combo box selection.  For normal combobox elements this
   * entails adding a new item to the combobox list.  In the case of a
   * keycombobox GUI element, the handling depends on the selected
   * value.  A "NEW" selection will cause the form to be set up to
   * handle a new record.  A "NONE" selection will insure that the
   * data model is ready to handle a null value.
   *
   * @param event The event descriptor.
   */

  public void itemStateChanged( ItemEvent event )
  {
    // Predispose processing to take place.
    boolean processChange = true;


    // Nop during GUI updates.
    if ( !controller.isUpdatingGUI() )
    {
      int state = event.getStateChange();

      if ( state == ItemEvent.SELECTED )
      {
	// Case on the combo box type:
	Object choice = comboBox.getSelectedItem();
	switch ( componentEntry.getType() )
	{
	case Constants.COMBOBOX:
	  // Determine if the selection is "NEW".
	  if ( choice instanceof String && choice.equals( Constants.NEW ) )
	  {
	    // It is.  For a regular combo box.  Simply add a new, user
	    // specified, item to the list.
	    processChange = addNewItem();
	  }
	  break;

	case Constants.KEYCOMBOBOX:
	  if ( choice instanceof String )
	  {
	    if ( choice.equals( Constants.NEW ) )
	    {
	      // It is a new item for a key combo box.  Make sure that the
	      // data model is prepared to handle a new record.
	      processNew();
	    }
	    else if ( choice.equals( Constants.NONE ) )
	    {
	      // It is a null entry.  Make sure that the data model
	      // has a dummy slot for a null entry.
	      processNone();
	    }
	  }
	  break;

	case Constants.OBSIDCOMBOBOX:
	default:
	  break;
	}

	// The view controller will take care of the remaining
	// processing, if the change processing has been enabled.
	if ( processChange )
	{
	  controller.processGUIChange( comboBox );
	}
      }
      else if ( state == ItemEvent.DESELECTED )
      {
	// Save the key value to potentially seed a "NEW" selection.
	keyValue = event.getItem();
      }
    }
  }

  /****************************************************************************/
  /**
   * Process a change to a "joined" field, i.e. lay the groundwork for
   * a new record.
   */

  public void processNew()
  {
    try
    {
      // Determine if the joined table does not have a "NEW"
      // record in the data model cache.
      String keyTableName =
	componentEntry.getKeyDatabaseKey().getTableName();
      DatabaseModel databaseModel = 
	Editor.getInstance().getDatabase().getDatabaseModel();
      if ( !databaseModel.hasRecord( keyTableName, Constants.NEW ) )
      {
	// It does not.  Create the record for handling the "New"
	// value.
	databaseModel.createRecord( keyTableName, Constants.NEW );
      }

      if ( keyValue == null ||
	   (keyValue instanceof String &&
	    Constants.NONE.equals( (String) keyValue )) )
      {
	// Clear the entries in the model.
	databaseModel.copyNull( keyTableName, Constants.NEW );
      }
      else
      {
	// Copy the last selected value to the data model "NEW" entry.
	databaseModel.copyRecord( keyTableName, Constants.NEW, keyValue );
      }
    }
    catch ( RecordDoesNotExistException recordDoesNotExistException )
    {
      LogClient.logMessage( "Unexpected exception." );
      LogClient.printStackTrace( recordDoesNotExistException );
    }
    catch ( AlreadyExistsException alreadyExistsException )
    {
      LogClient.logMessage( "Unexpected exception." );
      LogClient.printStackTrace( alreadyExistsException );
    }
  }

  /****************************************************************************/
  /**
   * Insure that a null field exists for this table.
   */

  public void processNone()
  {
    try
    {
      // Determine if the joined table does not have an empty record
      // in the data model cache.
      Object keyValue = new Integer( 0 );
      String keyTableName =
	componentEntry.getKeyDatabaseKey().getTableName();
      DatabaseModel databaseModel = 
	Editor.getInstance().getDatabase().getDatabaseModel();
      if ( !databaseModel.hasRecord( keyTableName, keyValue ) )
      {
	// It does not.  Create the record for handling the null case.
	databaseModel.createRecord( keyTableName, keyValue );
      }
    }
    catch ( AlreadyExistsException alreadyExistsException )
    {
      LogClient.logMessage( "Unexpected exception." );
      LogClient.printStackTrace( alreadyExistsException );
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
