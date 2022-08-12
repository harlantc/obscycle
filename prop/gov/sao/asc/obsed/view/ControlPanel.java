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

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.ViewMenu;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.UpdateFailedException;
import gov.sao.asc.obsed.constraint.ConstraintViolationException;
import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.obsed.view.event.ChangeListEvent;
import gov.sao.asc.obsed.view.form.FormView;
import gov.sao.asc.obsed.view.form.event.NavChangeEvent;
import gov.sao.asc.obsed.view.form.event.NavChangeListener;
import gov.sao.asc.obsed.view.event.ControlEvent;
import gov.sao.asc.obsed.view.event.ControlListener;
import gov.sao.asc.util.LogClient;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/******************************************************************************/

public class ControlPanel extends JPanel
  implements ActionListener, ChangeListListener, 
	     ListSelectionListener, NavChangeListener, ControlListener
{
  // Cancel buttons.
  private JButton cancelAllButton;
  private JButton cancelButton;

  // Save buttons.
  private JButton saveAllButton;
  private JButton saveButton;

  /**
   * Delete button.
   */
  private JButton deleteButton;

  /**
   * The sub-panel for New record buttons.
   */
  private JPanel newPanel;

  /**
   * Clone, Default and Null buttons.
   */
  private JButton cloneButton;
  private JButton defaultButton;
  private JButton nullButton;

  // The event handler.
  private View view;

  /****************************************************************************/
  /**
   * Build the controls.
   */

  public ControlPanel( Properties properties, View view )
  {
    // Set the view controller object and register to find out about
    // modified components.
    this.view = view;

    // add a control listener for this control panel
    Editor editor = Editor.getInstance();
    editor.addControlListener( this );

    // Keep this panel simple.
    setLayout( new FlowLayout() );
    setBorder( new TitledBorder( "Controls: " ) );

    // Build the Current Record sub-panel.
    JPanel panel = new JPanel();
    panel.setLayout( new FlowLayout() );
    panel.setBorder( new TitledBorder( "Current Record" ) ); 

    // Set up the save button.
    saveButton = new JButton( "Save" );
    String toolTipText = properties.getProperty( "save-button.toolTipText" );
    saveButton.setToolTipText( toolTipText );
    saveButton.setActionCommand( "SAVE" );
    saveButton.addActionListener( this );
    saveButton.setEnabled( false );

    // Set up the cancel button.
    cancelButton = new JButton( "Cancel" );
    toolTipText = properties.getProperty( "cancel-button.toolTipText" );
    cancelButton.setToolTipText( toolTipText );
    cancelButton.setActionCommand( "CANCEL" );
    cancelButton.addActionListener( this );
    cancelButton.setEnabled( false );

    // Set up delete button (formerly "remove" button).
    deleteButton = new JButton( "Delete..." );
    toolTipText = properties.getProperty( "delete-button.toolTipText" );
    deleteButton.setToolTipText( toolTipText );
    deleteButton.setActionCommand( "DELETE" );
    deleteButton.addActionListener( this );

    // Add buttons to sub-panel; add to this control panel.
    panel.add( saveButton );
    panel.add( cancelButton );
    panel.add( deleteButton );
    add( panel );

    // Build the All Records sub-panel.
    panel = new JPanel();
    panel.setLayout( new FlowLayout() );
    panel.setBorder( new TitledBorder( "All Records" ) );

    // Set up the save all button.
    saveAllButton = new JButton( "Save All" );
    toolTipText = properties.getProperty( "save-all-button.toolTipText" );
    saveAllButton.setToolTipText( toolTipText );
    saveAllButton.setActionCommand( "SAVE_ALL" );
    saveAllButton.addActionListener( this );
    saveAllButton.setEnabled( false );

    // Set up the cancel all button.
    cancelAllButton = new JButton( "Cancel All" );
    toolTipText = properties.getProperty( "cancel-all-button.toolTipText" );
    cancelAllButton.setToolTipText( toolTipText );
    cancelAllButton.setActionCommand( "CANCEL_ALL" );
    cancelAllButton.addActionListener( this );
    cancelAllButton.setEnabled( false );

    // Add buttons to sub-panel; add to this control panel.
    panel.add( saveAllButton );
    panel.add( cancelAllButton );
    add( panel );

    // Build the New Record sub-panel
    newPanel = new JPanel();
    newPanel.setLayout( new FlowLayout() );
    newPanel.setBorder( new TitledBorder( "New Record, use values:" ) );

    // Set up the clone button.
    cloneButton = new JButton( "Clone This" );
    toolTipText = properties.getProperty( "clone-button.toolTipText" );
    cloneButton.setToolTipText( toolTipText );
    cloneButton.setActionCommand( "CLONE" );
    cloneButton.addActionListener( this );

    // Set up the default button.
    //defaultButton = new JButton( "Default" );
    //toolTipText = properties.getProperty( "default-button.toolTipText" );
    //defaultButton.setToolTipText( toolTipText );
    //defaultButton.setActionCommand( "DEFAULT" ); // ???
    //defaultButton.addActionListener( this );

    // Set up the null button.
    nullButton = new JButton( "Null" );
    toolTipText = properties.getProperty( "null-button.toolTipText" );
    nullButton.setToolTipText( toolTipText );
    nullButton.setActionCommand( "NULL" );
    nullButton.addActionListener( this );

    // Add buttons to sub-panel; add to this control panel.
    newPanel.add( cloneButton );
    //newPanel.add( defaultButton );
    newPanel.add( nullButton );
    add( newPanel );
  }

  /****************************************************************************/
  /**
   *  Return the refresh capability for this view.
   */

  public void actionPerformed( ActionEvent event )
  {
    Editor editor = Editor.getInstance();
    // Case on the command.
    String command = event.getActionCommand();
    if ( command.equals( "SAVE" ) )
    {
      try
      {
	// Save the changes for the current observation.
	view.saveCurrentChanges();

	// Tell listeners about the save event.
	ControlEvent controlEvent = new ControlEvent( "SAVE" );
	editor.fireControlEvent( controlEvent );
      }
      catch ( ConfigurationException configurationException )
      {
	JOptionPane.showMessageDialog( editor.getFrame(), 
				       configurationException.getMessage(), 
				       "Configuration Error",
				       JOptionPane.ERROR_MESSAGE );
      }
      catch ( ConstraintViolationException constraintViolationException )
      {
	JOptionPane.showMessageDialog( editor.getFrame(), 
				       constraintViolationException.getMessage(), 
				       "Constraint Error",
				       JOptionPane.ERROR_MESSAGE );
	
	DatabaseKey databaseKey = constraintViolationException.getDatabaseKey();

	// Select the component where the violation occurred.
	//FIXME
      }
      catch ( UpdateFailedException updateFailedException )
      {
	JOptionPane.showMessageDialog( editor.getFrame(), 
				       updateFailedException.getMessage(), 
				       "Database Update Error",
				       JOptionPane.ERROR_MESSAGE );
      }
      catch ( Exception exc )
      {
	// tbd
	LogClient.logMessage( exc.getMessage() );
	LogClient.printStackTrace( exc );
      }
    }
    else if ( command.equals( "SAVE_ALL" ) )
    {
      try
      {
	// Save the changes for all observations.
	view.saveAllChanges();

	// Tell listeners about the save all event.
	ControlEvent controlEvent = new ControlEvent( "SAVE_ALL" );
	editor.fireControlEvent( controlEvent );
      }
      catch ( Exception exc )
      {
	// tbd
	LogClient.logMessage( exc.getMessage() );
	LogClient.printStackTrace( exc );
      }
    }
    else if ( command.equals( "CANCEL" ) )
    {
      // Clear out the change cache for this observation and update
      // the form to undo changes.
      view.cancelCurrentChanges();

      // Tell listeners about the cancel event.
      ControlEvent controlEvent = new ControlEvent( "CANCEL" );
      editor.fireControlEvent( controlEvent );
    }
    else if ( command.equals( "CANCEL_ALL" ) )
    {
      // Clear out all change data and update the form for this
      // observation if it is dirty.
      view.cancelAllChanges();

      // Tell listeners about the cancel all event.
      ControlEvent controlEvent = new ControlEvent( "CANCEL_ALL" );
      editor.fireControlEvent( controlEvent );
    }
    else if ( command.equals( "CLONE" ) )
    {
      view.createNewRecord( Constants.NEWCLONE );
    }
    else if ( command.equals( "DEFAULT" ) )
    {
      view.createNewRecord( Constants.NEWDEFAULT );
    }
    else if ( command.equals( "NULL" ) )
    {
      view.createNewRecord( Constants.NEWNULL );
    }
    else if ( command.equals( "DELETE" ) )
    {
      // Delete the current observation.
      view.delete();
      view.update();
    }
    else
    {
      LogClient.logMessage( "ControlPanel.actionPerformed(): Internal error." );
    }
  }

  /****************************************************************************/
  /**
   * Handle a change list event by setting or clearing the save and
   * cancel buttons.
   */

  public void changeListChanged( ChangeListEvent event )
  {
    if ( event.getAddFlag() == true )
    {
      // This is an `add' operation.  Determine if the event's record
      // is the same as the current record.  If so, enable all the
      // buttons.  If not, then set the `Save all' and `cancel all'
      // buttons to enabled and don't touch the `save' and `cancel'.
      String tableName = event.getTableName();
      int rowIndex = event.getRowIndex();
      if ( ( rowIndex < 0 ) || ( tableName == null ) )
      {
	return;			// shouldn't happen on an `add'!!!
      }
      ChangeList changeList = view.getChangeList();
      Object keyColumnValue = changeList.getKeyFor( tableName, rowIndex );
      String viewStringKeyValue = view.getSelectedKeyValue().toString();

      Object tmp_obj = view.getSelectedKeyValue();
      Class cl = tmp_obj.getClass();
      String tmp_cl = (cl.getName()).toLowerCase();
      int tmp_idx = tmp_cl.indexOf("double");
      if (tmp_idx < 0) {
        Integer viewKeyValue = new Integer ( viewStringKeyValue.toString() );
        if ( keyColumnValue.equals( viewKeyValue ) )
        {
	  setButtons( true );
        }
        else
        {
	  setAllRecordsButtonsEnabled( true );
        }
      }
      else {
        Double viewKeyValue = new Double ( viewStringKeyValue.toString() );
        if ( keyColumnValue.equals( viewKeyValue ) )
        {
	  setButtons( true );
        }
        else
        {
	  setAllRecordsButtonsEnabled( true );
        }
      }
      
    }

  }

  /****************************************************************************/
  /**
   * Implement controlCommandPerformed to satisfy ControlListener
   * interface. Set the control panel buttons correctly depending on
   * the control command performed.  For `save all' and `cancel all'
   * disable all the buttons.  For `save' and `cancel' only clear if
   * the current view is displaying the same record as this view (or
   * this view *is* the current view).
   */

  public void controlCommandPerformed( ControlEvent event )
  {
    String command = event.getControlCommand();
    if ( command.equals( "CANCEL_ALL" ) || command.equals( "SAVE_ALL" ) )
    {
      setAllRecordsButtonsEnabled( false );
      setCurrentRecordButtonsEnabled( false );
    }
    else
    if ( command.equals( "CANCEL" ) || command.equals( "SAVE" ) )
    {
      Editor editor = Editor.getInstance();
      JTabbedPane tabbedPanel = editor.getTabbedPanel();
      Component c = tabbedPanel.getSelectedComponent();

      // If this view is the current view, then disable all of the
      // control buttons.
      if ( c.equals( this ) )
      {
	setButtons( false );
      }
      else
      {
	// If this view has same record as current view; then
	// disable the control panel buttons.
	if ( c instanceof gov.sao.asc.obsed.view.form.FormView )
	{
	  Object currentKeyValue = ((FormView) c).getSelectedKeyValue();
	  Object keyValue = view.getSelectedKeyValue();
	  if ( keyValue.equals( currentKeyValue ) )
	  {
	    setButtons( false );
	  }
	}
      }
    }
    else
    {
      return;
    }
  }

  /****************************************************************************/
  /**
   * Perform miscellaneous operations based on the current state of
   * the configuration and the database.
   */

  public void refresh()
  {
    // Remove the New Panel if the configuration does not specify
    // support for it.  Also, disable the "New Record" menu item.
    Configuration configuration = view.getConfiguration();
    if( !configuration.isNewSupported() )
    {
      remove( newPanel );
      Editor editor = Editor.getInstance();
      ViewMenu viewMenu = editor.getViewMenu();
      viewMenu.setNewRecordMenu( false );
    }
    else
    {
      System.out.println( "Setting new buttons." );
      // Set up the individual new buttons.
      cloneButton.setEnabled( configuration.isNewCloneSupported() );
      //defaultButton.setEnabled( configuration.isNewDefaultSupported() );
      nullButton.setEnabled( configuration.isNewNullSupported() );
    }

    // Disable the Remove button if the database has no records
    // associated with this view.
    if ( view.getNumberOfRecords() > 0 ) {
       deleteButton.setEnabled( configuration.isDeleteSupported() );
    }
    else {
       deleteButton.setEnabled( false);
    }
  }

  /****************************************************************************/
  /**
   * Helper function to manage the state of the cancel and save buttons.
   */

  private void setButtons( boolean state )
  {
    // Set the state of the Save/Cancel buttons.
    setCurrentRecordButtonsEnabled( state );

    // Disable the Save All/Cancel All buttons if all changes have
    // been dealt with.
    setAllRecordsButtonsEnabled( view.getChangeList().isDirty() );
  }

  /****************************************************************************/
  /**
   * Enable/disable the save all and cancel all buttons.
   */

  private void setAllRecordsButtonsEnabled( boolean enabled )
  {
    saveAllButton.setEnabled( enabled );
    cancelAllButton.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the save and cancel buttons.
   */

  private void setCurrentRecordButtonsEnabled( boolean enabled )
  {
    saveButton.setEnabled( enabled );
    cancelButton.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Handle a navigation change by enabling or disabling the save,
   * cancel, save all and cancel all buttons appropriately.
   */

  public void stateChanged( NavChangeEvent event )
  {
    ChangeList changeList = view.getChangeList();

    String tableName = event.getTableName();
    Object keyValue = event.getKeyValue();

    setCurrentRecordButtonsEnabled( changeList.isRowDirty( tableName, 
                                                           keyValue ) );
    setAllRecordsButtonsEnabled( changeList.isDirty() );
  }

  /****************************************************************************/
  /**
   * Handle a row selection change by enabling or disabling the save,
   * cancel, save all and cancel all buttons appropriately.
   */

  public void valueChanged( ListSelectionEvent event ) 
  {
    ChangeList changeList = view.getChangeList();

    DatabaseKey keyEntry = view.getConfiguration().getKeyEntry();

    String tableName = keyEntry.getTableName();
    int rowIndex = event.getFirstIndex();

    setCurrentRecordButtonsEnabled( changeList.isRowDirty( tableName, 
                                                           rowIndex ) );
    setAllRecordsButtonsEnabled( changeList.isDirty() );
  }

  /****************************************************************************/

}

/******************************************************************************/
