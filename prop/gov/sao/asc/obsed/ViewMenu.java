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

package gov.sao.asc.obsed;

/******************************************************************************/

import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.event.CancelAllListener;
import gov.sao.asc.obsed.event.CancelListener;
import gov.sao.asc.obsed.event.CreateViewInterface;
import gov.sao.asc.obsed.event.CreateViewListener;
import gov.sao.asc.obsed.event.HistoryListener;
import gov.sao.asc.obsed.event.NewRecordInterface;
import gov.sao.asc.obsed.event.NewRecordListener;
import gov.sao.asc.obsed.event.PrintViewListener;
import gov.sao.asc.obsed.event.RefreshListener;
import gov.sao.asc.obsed.event.RemoveViewListener;
import gov.sao.asc.obsed.event.RenameViewListener;
import gov.sao.asc.obsed.event.SaveAllListener;
import gov.sao.asc.obsed.event.SaveListener;
import gov.sao.asc.obsed.view.ChangeList;
import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.ViewMessage;
import gov.sao.asc.obsed.view.event.ChangeListEvent;
import gov.sao.asc.obsed.view.event.ChangeListListener;
import gov.sao.asc.obsed.view.event.ControlEvent;
import gov.sao.asc.obsed.view.event.ControlListener;
import gov.sao.asc.obsed.view.form.NewView;
import gov.sao.asc.obsed.view.form.event.NavChangeEvent;
import gov.sao.asc.obsed.view.form.event.NavChangeListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/******************************************************************************/
/**
 * The <code>ViewMenu</code> class sets up the View menu for the
 * ObsCat GUI applet and application.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class ViewMenu extends JMenu
  implements ChangeListListener, ListSelectionListener, NavChangeListener,
             ControlListener, ChangeListener
{
  JMenuItem cancelAllMenuItem;
  JMenuItem cancelMenuItem;
  JMenuItem saveAllMenuItem;
  JMenuItem saveMenuItem;
  JMenuItem refreshMenuItem;
  JMenuItem historyMenuItem;
  JMenuItem renameMenuItem;
  JMenuItem removeMenuItem;
  JMenu newRecordMenu;

  /****************************************************************************/

  public ViewMenu( CreateViewInterface createViewInterface,
                   HistoryListener historyListener, 
                   PrintViewListener printViewListener,
                   RefreshListener refreshListener,
		   RemoveViewListener removeViewListener,
		   RenameViewListener renameViewListener,
                   SaveListener saveListener,
                   CancelListener cancelListener,
                   SaveAllListener saveAllListener,
                   CancelAllListener cancelAllListener,
		   NewRecordInterface newRecordInterface )
  {
    super("View");

    // Add forms menu item to menu.
    JMenu formsViewMenu = initObsFormsMenu( createViewInterface );
    add( formsViewMenu );

    // Add instrument forms to menu.
    JMenu instrumentFormsMenu = initInstrumentFormsMenu( createViewInterface );
    add( instrumentFormsMenu );

    JMenu miscFormsMenu = initMiscFormsMenu( createViewInterface );
    add( miscFormsMenu );

    add( new JSeparator() );


    // Add `History' menu item.  Not initially enabled.
    historyMenuItem = new JMenuItem( "History" );
    historyMenuItem.setEnabled( false );
    historyMenuItem.addActionListener( historyListener );
    add(historyMenuItem);


    // Add separator.
    add( new JSeparator() );

    // Refresh operation.
    refreshMenuItem = new JMenuItem( "Refresh View" );
    refreshMenuItem.setEnabled( false );
    refreshMenuItem.addActionListener( refreshListener );
    add(refreshMenuItem);

    // Set up `Rename' menu item.
    renameMenuItem = new JMenuItem("Rename View...");
    renameMenuItem.setEnabled( false );
    renameMenuItem.addActionListener( renameViewListener );
    add(renameMenuItem);

    // Set up `Remove' menu item.
    removeMenuItem = new JMenuItem("Remove View");
    removeMenuItem.setEnabled( false );
    removeMenuItem.addActionListener( removeViewListener );
    add(removeMenuItem);

  }

  /****************************************************************************/
  /**
   * Handle a change list event.
   */

  public void changeListChanged( ChangeListEvent event )
  {
    // The menu only cares about `add' change list events.
    if ( event.getAddFlag() == true )
    {

      Editor editor = Editor.getInstance();
      JTabbedPane tabbedPanel = editor.getTabbedPanel();
      Component view = tabbedPanel.getSelectedComponent();
      if ( view instanceof NewView ) 
      {
	// Never enable the `cancel' and `save' menu items on a
	// NewView since they don't apply there.
	setAllRecordsMenuItemsEnabled( true );
      }
      else
      {
	// If this is an `add' operation, enable all the buttons.
	setAllRecordsMenuItemsEnabled( true );
	setCurrentRecordMenuItemsEnabled( true );
      }
    }
  }

  /****************************************************************************/
  /**
   * Implement controlCommandPerformed to satisfy ControlListener interface.
   */

  public void controlCommandPerformed( ControlEvent event )
  {
    // Set the control command menu items correctly depending on the
    // control command performed.  Since the current view is all that
    // this menu is concerned about (since there is only one instance
    // of ViewMenu), then dsiable all the menu items when any `save
    // all', `cancel all', `save' or `cancel' is performed.

    setAllRecordsMenuItemsEnabled( false );
    setCurrentRecordMenuItemsEnabled( false );

  }

  /****************************************************************************/

  private JMenu initObsFormsMenu( CreateViewInterface createViewInterface )
  {
    JMenu result = new JMenu( "Obs Forms" );

    // add Forms menu item "OBS Form"
    JMenuItem targetFormMenuItem = new JMenuItem( "Obs Form" );
    CreateViewListener formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "target" );
    targetFormMenuItem.addActionListener( formViewListener );
    result.add( targetFormMenuItem );

    // add Forms menu item "Prop Info Form"
    JMenuItem propInfoFormMenuItem = new JMenuItem( "Prop Info Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView",
			      "prop_info" );
    propInfoFormMenuItem.addActionListener( formViewListener );
    result.add( propInfoFormMenuItem );

    // add Forms menu item "TOO Form"
    JMenuItem tooFormMenuItem = new JMenuItem( "TOO Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView",
			      "too" );
    tooFormMenuItem.addActionListener( formViewListener );
    result.add( tooFormMenuItem );

    // add Forms menu item "Dither Form"
    JMenuItem ditherFormMenuItem = new JMenuItem( "Dither Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView",
			      "dither" );
    ditherFormMenuItem.addActionListener( formViewListener );
    result.add( ditherFormMenuItem );

    // add Forms menu item "Time Req Form"
    JMenuItem timeReqFormMenuItem = new JMenuItem( "Time Req Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "timereq" );
    timeReqFormMenuItem.addActionListener( formViewListener );
    result.add( timeReqFormMenuItem );

    // add Forms menu item "Roll Req Form"
    JMenuItem rollReqFormMenuItem = new JMenuItem( "Roll Req Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "rollreq" );
    rollReqFormMenuItem.addActionListener( formViewListener );
    result.add( rollReqFormMenuItem );

    // add Forms menu item "Phase Req Form"
    JMenuItem phaseReqFormMenuItem = new JMenuItem( "Phase Req Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "phasereq" );
    phaseReqFormMenuItem.addActionListener( formViewListener );
    result.add( phaseReqFormMenuItem );

    // add Forms menu item "Orbit Form"
    JMenuItem orbitFormMenuItem = new JMenuItem( "Orbit Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", "mt_ephem" );
    orbitFormMenuItem.addActionListener( formViewListener );
    result.add( orbitFormMenuItem );

    // add Forms menu item "Sim Form"
    JMenuItem simFormMenuItem = new JMenuItem( "SIM Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", "sim" );
    simFormMenuItem.addActionListener( formViewListener );
    result.add( simFormMenuItem );


    // *************UNUSED ************************************************
    // result.add( new JSeparator() );

    // add Forms menu item "Mission Planning Form"
    //JMenuItem mpFormMenuItem = new JMenuItem( "MP Form" );
    //formViewListener = 
      //new CreateViewListener( createViewInterface,
		      //"gov.sao.asc.obsed.view.form.FormView", "mp" );
    //mpFormMenuItem.addActionListener( formViewListener );
    //result.add( mpFormMenuItem );

    // add Forms menu item "SOE Form"
    //JMenuItem soeFormMenuItem = new JMenuItem( "SOE Form" );
    //formViewListener = 
      //new CreateViewListener( createViewInterface,
		      //"gov.sao.asc.obsed.view.form.FormView", "soe" );
    //soeFormMenuItem.addActionListener( formViewListener );
    //result.add( soeFormMenuItem );

    return result;
  }

  /****************************************************************************/

  private JMenu initInstrumentFormsMenu( CreateViewInterface createViewInterface )
  {
    JMenu result = new JMenu( "Instrument Forms" );

    // add Instrument menu item "HRC Form"
    JMenuItem hrcFormMenuItem = new JMenuItem( "HRC Form" );
    CreateViewListener formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "hrcparam" );
    hrcFormMenuItem.addActionListener( formViewListener );
    result.add( hrcFormMenuItem );

    // add Instrument menu item "ACIS Form"
    JMenuItem acisFormMenuItem = new JMenuItem( "ACIS Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "acisparam" );
    acisFormMenuItem.addActionListener( formViewListener );
    result.add( acisFormMenuItem );

    // add Instrument menu item "ACISWin Form"
    JMenuItem aciswinFormMenuItem = new JMenuItem( "ACISWin Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "aciswin" );
    aciswinFormMenuItem.addActionListener( formViewListener );
    result.add( aciswinFormMenuItem );

    return result;
  }
  /****************************************************************************/

  private JMenu initMiscFormsMenu( CreateViewInterface createViewInterface )
  {
    JMenu result = new JMenu( "Misc Forms" );

    // add Forms menu item "Observing Cycle Form"
    JMenuItem observingCycleFormMenuItem = new JMenuItem( "Observing Cycle Form" );
    CreateViewListener formViewListener = 
      new CreateViewListener( createViewInterface,
	      "gov.sao.asc.obsed.view.form.FormView", "observing_cycle" );
    observingCycleFormMenuItem.addActionListener( formViewListener );
    result.add( observingCycleFormMenuItem );

    JMenuItem grantInfoFormMenuItem = new JMenuItem( "Grant Info Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "grant_info" );
    grantInfoFormMenuItem.addActionListener( formViewListener );
    result.add( grantInfoFormMenuItem );

    JMenuItem grantRecipientFormMenuItem = new JMenuItem( "Grant Recipient Form" );
    formViewListener = 
      new CreateViewListener( createViewInterface,
			      "gov.sao.asc.obsed.view.form.FormView", 
			      "grant_recipient" );
    grantRecipientFormMenuItem.addActionListener( formViewListener );
    result.add( grantRecipientFormMenuItem );

    return result;
  }


  /****************************************************************************/

  private JMenu initNewRecordMenu( NewRecordInterface newRecordInterface )
  {
    JMenu result = new JMenu( "New Record" );
    result.setEnabled( false );

    // add New Record menu item "Clone This"
    JMenuItem cloneCurrentMenuItem = new JMenuItem( "Clone This" );
    cloneCurrentMenuItem.setEnabled( true );
    NewRecordListener newRecordListener =
      new NewRecordListener( newRecordInterface, Constants.NEWCLONE );
    cloneCurrentMenuItem.addActionListener( newRecordListener );
    result.add( cloneCurrentMenuItem );

    // add New Record menu item "Default"
    JMenuItem defaultMenuItem = new JMenuItem( "Default Current" );
    defaultMenuItem.setEnabled( false );
    newRecordListener =
      new NewRecordListener( newRecordInterface, Constants.NEWDEFAULT );
    defaultMenuItem.addActionListener( newRecordListener );
    result.add( defaultMenuItem );

    // add New Record menu item "Null"
    JMenuItem nullMenuItem = new JMenuItem( "Null Current" );
    nullMenuItem.setEnabled( false );
    newRecordListener =
      new NewRecordListener( newRecordInterface, Constants.NEWNULL );
    nullMenuItem.addActionListener( newRecordListener );
    result.add( nullMenuItem );
    
    return( result );

  }

  /****************************************************************************/
  /**
   * Enable/disable the History menu item.
   */
  public void setHistoryMenuItem( boolean enabled )
  {
    historyMenuItem.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the Refresh menu item.
   */
  public void setRefreshMenuItem( boolean enabled )
  {
    refreshMenuItem.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the Rename menu item.
   */
  public void setRenameMenuItem( boolean enabled )
  {
    renameMenuItem.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the Remove menu item.
   */
  public void setRemoveMenuItem( boolean enabled )
  {
    removeMenuItem.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the New Record menu.
   */
  public void setNewRecordMenu( boolean enabled )
  {
    //newRecordMenu.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the save all and cancel all menu items.
   */

  private void setAllRecordsMenuItemsEnabled( boolean enabled )
  {
    //saveAllMenuItem.setEnabled( enabled );
    //cancelAllMenuItem.setEnabled( enabled );
  }

  /****************************************************************************/
  /**
   * Enable/disable the save and cancel menu items.
   */

  private void setCurrentRecordMenuItemsEnabled( boolean enabled )
  {
    //saveMenuItem.setEnabled( enabled );
    //cancelMenuItem.setEnabled(enabled  );
  }

  /****************************************************************************/
  /**
   * Handle a navigation change by enabling or disabling the save,
   * cancel, save all and cancel all menu items appropriately.
   */

  public void stateChanged( NavChangeEvent event )
  {
    View view = Editor.getInstance().getCurrentView();

    ChangeList changeList = view.getChangeList();

    String tableName = event.getTableName();
    Object keyValue = event.getKeyValue();

    setCurrentRecordMenuItemsEnabled( changeList.isRowDirty( tableName, 
							     keyValue ) );
    setAllRecordsMenuItemsEnabled( changeList.isDirty() );
  }

  /****************************************************************************/
  /**
   * Handle a state changed event from the JTabbedPane.  Set the save
   * cancel, save all and cancel all menu items appropriately when the
   * view changes.
   */

  public void stateChanged( ChangeEvent event )
  {
    View view = Editor.getInstance().getCurrentView();
    if ( view == null )
    {
      setCurrentRecordMenuItemsEnabled( false );
      setAllRecordsMenuItemsEnabled( false );
      setHistoryMenuItem( false );
      setRefreshMenuItem( false );
      setRenameMenuItem( false );
      setRemoveMenuItem( false );
      setNewRecordMenu( false );
      return;
    }
    else
    if ( view instanceof gov.sao.asc.obsed.view.form.NewView )
    {
      ChangeList changeList = view.getChangeList();
      // Current record menu items are not relevant to NewView.
      setCurrentRecordMenuItemsEnabled( false );
      // Set All Records menu items based on global changes list.
      setAllRecordsMenuItemsEnabled( changeList.isDirty() );

      // Set other menu items appropriately.
      setHistoryMenuItem( true );
      setRefreshMenuItem( true );
      setRenameMenuItem( true );
      setRemoveMenuItem( false );
      setNewRecordMenu( false );
    }      
    else
    if ( view instanceof gov.sao.asc.obsed.view.form.FormView )
    {
      setRefreshMenuItem( true );
      setHistoryMenuItem( true );
      setRenameMenuItem( true );
      setRemoveMenuItem( true );

      Object keyValue = view.getSelectedKeyValue();
      if ( keyValue == null )
      {
	// This can happen when the view is first created and not yet
	// populated with a record or when a table does not yet have a
	// selcted row.
	setNewRecordMenu( false );
	return;
      }
      setNewRecordMenu( view.getConfiguration().isNewSupported() );
      ChangeList changeList = view.getChangeList();
      setCurrentRecordMenuItemsEnabled( changeList.isDirty( keyValue ) );
      setAllRecordsMenuItemsEnabled( changeList.isDirty() );
    }
  }

  /****************************************************************************/
  /**
   * Handle a row selection change by calling the tableChanged() method.
   */

  public void valueChanged( ListSelectionEvent event ) 
  {

  }

  /****************************************************************************/

}

/******************************************************************************/
