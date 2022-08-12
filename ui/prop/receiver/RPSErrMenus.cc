
/*H****************************************************************************
* Copyright (c) 1995,2019 Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code to create the pulldown menus
	for the Proposal Planning RPS Error application .

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

#include "RPSErrMenuWindow.hh"
#include "RPSErrExterns.hh"


// ----------------------------------------------------------
// File Pulldown Menu
// ----------------------------------------------------------
void RPSErr_CreateFileMenu(MenuBar *menubar,RPSErrMenuWindow *obj)
{ 
  Widget pulldown;
  Widget btn_w;
  
  pulldown = menubar->AddPulldown(menubar->baseWidget(),"File",'F',0,0,0,
	"File menu options.");

/*
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->deleteprop_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Delete selected proposal from database.", 
	btn_w);

 XtVaCreateManagedWidget("separator",
	xmSeparatorWidgetClass,pulldown,
	NULL);
*/
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->exit_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"Exit the prop_rps_gui application.", 
	btn_w);


}

// ----------------------------------------------------------
// Edit Pulldown Menu
// ----------------------------------------------------------
void RPSErr_CreateEditMenu(MenuBar *menubar,RPSErrMenuWindow *obj,Boolean popup)
{ 
  Widget pulldown;
  Widget pulldown2;
  Widget btn_w;
  
  if (!popup) 
    pulldown = menubar->AddPulldown(menubar->baseWidget(),"Edit",'E',0,0,0,
	"Edit menu options.");
  else
    pulldown = menubar->baseWidget();

  if (!popup) {
    pulldown2 = menubar->AddPulldown(pulldown,"Sort",'S',0,0,0,"Sort options");
    btn_w = menubar->AddPushButtonInterface(pulldown2,obj->sort_propno_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Sort on proposal number.", btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown2,obj->sort_last4_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Sort on last 4 digits of proposal number.",
	btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown2,obj->sort_reverse4_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Sort reverse order on last 4 digits of proposal number.", 
	btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown2,obj->sort_pi_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Sort on P.I. last name.", btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown2,obj->sort_title_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Sort on proposal title.", btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown2,obj->sort_type_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Sort on proposal type.", btn_w);

    XtVaCreateManagedWidget("separator",
	    xmSeparatorWidgetClass,pulldown,
	    NULL);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->refresh_cmd(),
	  0,0,0);
    theToolTipManager->NewToolTip( "Retrieve proposals from database again.",
	btn_w);
  }
  

  if (!popup) {
    XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);


    btn_w = menubar->AddPushButtonInterface(pulldown,obj->find_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	"Find the first occurrence of string in the display.",btn_w);
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->findnext_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip(
	"Find the next occurrance of string in the display.",btn_w);
  }
}



// ----------------------------------------------------------
// View Pulldown Menu
// ----------------------------------------------------------
void RPSErr_CreateViewMenu(MenuBar *menubar,RPSErrMenuWindow *obj,Boolean popup)
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
	"View the selected proposal.",btn_w);

  
  if (!popup) {
    btn_w = menubar->AddPushButtonInterface(pulldown,obj->view_error_log_cmd(),
	0,0,0);
    theToolTipManager->NewToolTip("View the RPS GUI log file.",btn_w);

cerr << "adding view menu" << endl;

  }


}
// ----------------------------------------------------------
// Statistics Pulldown Menu
// ----------------------------------------------------------
void RPSErr_CreateStatMenu(MenuBar *menubar,RPSErrMenuWindow *obj)
{ 
  Widget pulldown;
  Widget btn_w;
  
  pulldown = menubar->AddPulldown(menubar->baseWidget(),"Statistics",'S',
         0,0,0,"View menu options");
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->checks_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_rps_checks.pl script", 
	btn_w);
  //btn_w = menubar->AddPushButtonInterface(pulldown,obj->upload_cmd(),
	//0,0,0);
  //theToolTipManager->NewToolTip(
	//(char *)"View results of prop_check_upload.pl script", 
	//btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->recvstats_cmd(),
	0,0,0);
  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_receiver_stats.pl script", 
	btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->allstats_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_all_stats.pl script", 
	btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->gtostats_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_gto_stats.pl script", 
	btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->jointstats_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_joint_stats.pl script", 
	btn_w);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->lpstats_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_lp_vlp_stats.pl script", 
	btn_w);
  XtVaCreateManagedWidget("separator",
	  xmSeparatorWidgetClass,pulldown,
	  NULL);
  btn_w = menubar->AddPushButtonInterface(pulldown,obj->lists_cmd(),
	0,0,0);
  theToolTipManager->NewToolTip(
	(char *)"View results of prop_proposal_lists.pl script", 
	btn_w);
}

void RPSErr_CreateHelpMenu(MenuBar *menubar,RPSErrMenuWindow *obj)
{
  Widget pulldown;
  Widget btn_w;

  pulldown = menubar->AddPulldown(menubar->baseWidget(),"Help",'H',0,0,0,
        "Help menu options");
  btn_w = menubar->Cascades(5);
  XtVaSetValues(menubar->baseWidget(),XmNmenuHelpWidget, btn_w,NULL);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->helpg_cmd(), 0,0,0);
  theToolTipManager->NewToolTip(
        "Generic help for proposal applications", btn_w);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->helpo_cmd(), 0,0,0);
  theToolTipManager->NewToolTip(
        "Help for other proposal applications", btn_w);

  btn_w = menubar->AddPushButtonInterface(pulldown,obj->help_cmd(), 0,0,0);
  theToolTipManager->NewToolTip(
        "Help for Proposal application", btn_w);

}


