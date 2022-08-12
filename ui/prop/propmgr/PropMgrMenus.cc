
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code to create the pulldown menus
	for the Proposal Planning PROPOSAL MANAGER application .

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <Xm/Separator.h> 

#include "ap_common.hh"
#include "MenuBar.hh"
#include "ToolTip.hh"

#include "PropMgrMenuWindow.hh"
#include "PropMgrExterns.hh"

// global button (ugh!)
Widget LAYOUT_W;

// ----------------------------------------------------------
// File Pulldown Menu
// ----------------------------------------------------------
void PropMgr_CreateFileMenu(MenuBar *menubar,PropMgrMenuWindow *obj)
{ 
  Widget pulldown;
  Widget btn_w;
  
  pulldown = menubar->AddPulldown(menubar->baseWidget(),"File",'F',0,0,0,
	"File menu options.");

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->retrieve_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Retrieve proposals by user specified parameters.", btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->file_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Retrieve proposals by proposal number in selected file.", btn_w);

  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);

/*
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->genform_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Email specified forms for selected proposals.",
        btn_w);
*/
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->print_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Print specified format(s) or screen.",
        btn_w);

  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->exit_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Exit the Proposal Manager Application.",
        btn_w);


}

// ----------------------------------------------------------
// Edit Pulldown Menu
// ----------------------------------------------------------
void PropMgr_CreateEditMenu(MenuBar *menubar,PropMgrMenuWindow *obj,Boolean popup)
{ 
  Widget pulldown;
  Widget btn_w;
  
  if (!popup)  {
    pulldown = menubar->AddPulldown(menubar->baseWidget(),"Edit",'E',0,0,0,
	"Edit menu options.");
  }
  else
    pulldown = menubar->baseWidget();

  if (!popup) {
    LAYOUT_W = menubar->AddPushButtonInterface(pulldown,obj->layout_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Toggle between proposal/target information.",LAYOUT_W);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->refresh_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Refresh the proposal list from the database.",btn_w);

    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->sort_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Sort the proposal list.",btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->sort_t_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Sort the target list.",btn_w);
  }


  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->select_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Select(hilight) all the proposals/targets.",btn_w);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->deselect_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Deselect(unhilight) all the proposals/targets.",btn_w);


  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->remove_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Remove the selected proposal from the list.",btn_w);


  if (!popup) {
    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->find_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Find the first occurrence of string in the display.",btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->findnext_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Find the next occurrence of string in the display.",btn_w);
  }
}



// ----------------------------------------------------------
// View Pulldown Menu
// ----------------------------------------------------------
void PropMgr_CreateViewMenu(MenuBar *menubar,PropMgrMenuWindow *obj,Boolean popup)
{ 
  Widget pulldown;
  Widget btn_w;
  
  if (!popup) {
    pulldown = menubar->AddPulldown(menubar->baseWidget(),"View",'V',
         0,0,0,"View menu options");
  }
  else
    pulldown = menubar->baseWidget();

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_proposal_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View the selected proposal.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_cois_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View all the CoInvestigators for the selected proposals.",btn_w);
  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->review_primary_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View the primary review form.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->review_secondary_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View the secondary review form.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->review_peer_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View/edit the peer review form.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->review_final_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View/edit the final review form.",btn_w);
 

}


// ----------------------------------------------------------
// Statistics Pulldown Menu
// ----------------------------------------------------------
void PropMgr_CreateStatMenu(MenuBar *menubar,PropMgrMenuWindow *obj)
{ 
  Widget pulldown;
  Widget btn_w;
  
  pulldown = menubar->AddPulldown(menubar->baseWidget(),"Statistics",'S',
	0,0,0);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_proposed_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Statistics for current cycle with status=PROPOSED.",btn_w);
  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_peer_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Statistics for proposals approved at the peer review.",btn_w);
  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_joint_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Statistics for Joint proposals for the current cycle.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_lpvlp_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Statistics for LP/VLP proposals for the current cycle.",btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_gto_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Statistics for GTO proposals for the current cycle.",btn_w);
  XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->stat_final_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Final Proposal Checks for the current cycle.",btn_w);

}

// ----------------------------------------------------------
// Help Pulldown Menu
// ----------------------------------------------------------
void PropMgr_CreateHelpMenu(MenuBar *menubar,PropMgrMenuWindow *obj)
{
  Widget pulldown;
  Widget btn_w;

  pulldown = menubar->AddPulldown(menubar->baseWidget(),"Help",'H',0,0,0,
        "Help menu options");
  btn_w = menubar->Cascades(4);
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

