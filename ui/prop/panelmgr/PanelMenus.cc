
/*H****************************************************************************
* Copyright (c) 1995-2016, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code to create the pulldown menus
	for the Proposal Planning PANEL MANAGER application .

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#include <Xm/Separator.h> 

// guiext
#include "ap_common.hh"
#include "MenuBar.hh"
#include "ToolTip.hh"

#include "PanelExterns.hh"
#include "PanelMenuWindow.hh"


// ----------------------------------------------------------
// File Pulldown Menu
// ----------------------------------------------------------
void Panel_CreateFileMenu(MenuBar *menubar,PanelMenuWindow *obj)
{ 
  Widget pulldown;
  Widget btn_w;
  
  pulldown = menubar->AddPulldown(menubar->baseWidget(),"File",'F',0,0,0,
	"File menu options");
/*
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->panel_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
    	(char *)"Modify subject categories,allotted time for panels.",
  	btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->calc_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip((char *)
  	"Recalculate allotted times for all panels.", btn_w);


  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);

*/

  //btn_w = menubar->AddPushButtonInterface(pulldown,obj->email_cmd(),
//	0,0,0);
  //theToolTipManager->NewToolTip(
    	//(char *)"Email forms for selected panel/proposal data.",
  	//btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->print_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip( (char *)"Print panel data.", btn_w);
  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->exit_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
    	(char *)"Exit the panel manager application .",
  	btn_w);


}

// ----------------------------------------------------------
// Edit Pulldown Menu
// returns widget for Move Proposals
// ----------------------------------------------------------
Widget Panel_CreateEditMenu(MenuBar *menubar,PanelMenuWindow *obj,
	Boolean popup)
{ 
  Widget pulldown;
  Widget btn_w;
  Widget move_w = NULL;
  
  if (!popup) 
    pulldown = menubar->AddPulldown(menubar->baseWidget(),"Edit",'E',0,0,0,
	"Edit menu options");
  else
    pulldown = menubar->baseWidget();
  if (!popup) {
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->refresh_cmd(),
		0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Retrieve the proposal data from the database again.", btn_w);

    btn_w = menubar->AddPushButtonInterface(pulldown,obj->clump_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip((char *)"Highlight clumps for selected proposal.",
	btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->link_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip((char *)"Highlight linked proposals for selected proposal(s).",
	btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->deselect_all_cmd(),
 	0,0,0);
  theToolTipManager->NewToolTip((char *)"Unhighlight(deselect) all proposals.",
	btn_w);

    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);

    btn_w = menubar->AddPushButtonInterface(pulldown,obj->sort_cmd(),
		0,0,0);
    theToolTipManager->NewToolTip((char *)"Sort the proposals.",
	btn_w);


    btn_w = menubar->AddPushButtonInterface(pulldown,obj->findprop_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Find the Panel for specified proposal number.",
	btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->find_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Find the first occurrence of string in the display.",
	btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->findnext_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Find the next occurrence of string in the display.",
	btn_w);
  }

  return move_w;
}


// ----------------------------------------------------------
// View Pulldown Menu
// ----------------------------------------------------------
void Panel_CreateViewMenu(MenuBar *menubar,PanelMenuWindow *obj,Boolean popup)
{ 
  Widget pulldown;
  Widget btn_w;
  
  if (!popup)
    pulldown = menubar->AddPulldown(menubar->baseWidget(),"View",'V',
         0,0,0,"View menu options");
  else
    pulldown = menubar->baseWidget();


  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_proposal_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View data for the selected proposal.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_conflicts_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View conflict file for selected proposal.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_cois_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View all the Co-Investigators for the selected proposals.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_detectors_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View all detector combinations for proposals in current panel.",btn_w);
  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_linked_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View all linked proposals.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_propkey_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View Proposals and their target keywords.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_keyprop_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View Target Keywords and their associated proposals.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_clumps_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View conflict clumps for all proposals.",btn_w);
  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_panelist_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View data for the selected panel member.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_allmembers_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View all members currently assigned to panels.",btn_w);


}

// ----------------------------------------------------------
// Assign Pulldown Menu
// ----------------------------------------------------------
Widget Panel_CreateAssignMenu(MenuBar *menubar,PanelMenuWindow *obj,
	Boolean popup)
{ 
  Widget pulldown;
  Widget btn_w;
  Widget move_w = NULL;
  
  if (!popup)
    pulldown = menubar->AddPulldown(menubar->baseWidget(),"Assign",'A',
	0,0,0,"Assign menu options");

  else
    pulldown = menubar->baseWidget();

  if (!popup) {
    move_w = menubar->AddPulldown(pulldown,"Move Proposals to ",'M',
          	0,0,0,"Move proposals between panels.");

    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  }
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->assign_primary_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
      (char *)"Assign the selected member as the primary reviewer for the selected proposal(s).",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->assign_secondary_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
      (char *)"Assign the selected member as the secondary reviewer for the selected proposal(s).",btn_w);

  if (!popup) {
    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->assign_panelist_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
      (char *)"Assign member(s) to the current panel.",btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->remove_cmd(),
 	0,0,0);
    theToolTipManager->NewToolTip((char *)"Remove a panel member.",
	btn_w);
    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->removeprop_cmd(),
 	0,0,0);
    theToolTipManager->NewToolTip((char *)"Remove a proposal from a panel.",
	btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->assign_proposal_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
      (char *)"Assign proposal(s) to the current panel.",btn_w);
  }
  return move_w;

}

// ----------------------------------------------------------
// Statistics Pulldown Menu
// ----------------------------------------------------------
void Panel_CreateStatMenu(MenuBar *,PanelMenuWindow *)
{ 
  



}

// ----------------------------------------------------------
// Tool Pulldown Menu
// ----------------------------------------------------------
void Panel_CreateToolMenu(MenuBar *menubar,PanelMenuWindow *obj)
{ 
  Widget pulldown;
  Widget btn_w;
  
  pulldown = menubar->AddPulldown(menubar->baseWidget(),"Tools",'T',
	0,0,0,"Tools menu options");
  btn_w = menubar->AddToggleButtonInterface(pulldown,obj->log_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip((char *)
    "Turn on History function when assigning reviewers or moving proposals.",
    btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->logmsg_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip((char *)
    "View and/or edit notes file.", btn_w);


  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_log_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"View the Panel Manager log file.",btn_w);
  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->reassignment_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"Display reassignment requests.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_panel_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"Execute the Panel Statistics and view the results .",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_checks_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"Execute the Panel checks and view the results .",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->panel_lists_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"Execute the peer_lists.pl command to create the panel/reviewer lists.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->pre_conflicts_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
 	(char *)"Execute the Pre Conflict status check and view the results .",btn_w);

  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->propmgr_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip((char *)
    "Execute the proposal manager (prop_manager_gui)",btn_w);

}
// ----------------------------------------------------------
// Help Pulldown Menu
// ----------------------------------------------------------
void Panel_CreateHelpMenu(MenuBar *menubar,PanelMenuWindow *obj)
{
  Widget pulldown;
  Widget btn_w;

  pulldown = menubar->AddPulldown(menubar->baseWidget(),"Help",'H',0,0,0,
        "Help menu options");
  btn_w = menubar->Cascades(6);
  XtVaSetValues(menubar->baseWidget(),XmNmenuHelpWidget, btn_w,NULL);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->helpg_cmd(), 0,0,0);
  theToolTipManager->NewToolTip(
        (char *)"Generic help for proposal applications", btn_w);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->helpo_cmd(), 0,0,0);
  theToolTipManager->NewToolTip(
        (char *)"Help for other proposal applications", btn_w);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->help_cmd(), 0,0,0);
  theToolTipManager->NewToolTip(
        (char *)"Help for Proposal application", btn_w);

}

