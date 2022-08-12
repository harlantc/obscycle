 
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the Proposal Planning -
	RPS Error Menu Window.  This class contains access to all the
        pulldown menu command classes.  It also instantiates the
        scrolled text window for displaying the current proposal file
        and a scrolled list class for displaying exis


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include "ap_common.hh"
#include <iomanip>
#include <stdlib.h>
#include <stdio.h>
#include <Xm/Xm.h> 
#include <Xm/Form.h> 
#include <Xm/PanedW.h> 
#include <X11/cursorfont.h> 

#include "File_Utils.hh"

// guiext
#include "Label.hh"
#include "ScrollText.hh"
#include "BaseMessageArea.hh"
#include "MenuBar.hh"
#include "ErrorMessage.hh"
#include "HelpMessage.hh"
#include "GUIDefs.h"
#include "ToolTip.hh"

#include "PropHelpCodes.h"
#include "ProposalDefs.hh"

#include "RPSErrMenuWindow.hh"
#include "RPSErrPopupMenu.hh"
#include "RPSErrApp.hh"
#include "RPSErrPropList.hh"
#include "RPSErrCommands.hh"
#include "RPSErrExterns.hh"


static Cursor cursor = 0;
static char myTranslations[] =
 "#augment \
  <Btn3Down> : RPSErrPopupMenuCB(0) \n";


static XtActionsRec myActions[] =
{
  {(char *)"RPSErrPopupMenuCB",(XtActionProc)&RPSErrMenuWindow::PopupMenuCB},
};

static  XtTranslations parsed_trans = 0;



// -------------------------------------------------------------
// Constructor
// -------------------------------------------------------------
RPSErrMenuWindow :: RPSErrMenuWindow (const char *name) 
		: MenuWindow(name,RPSERR_MAIN_HTML,True,False,False)
{ 

  // File commands
  app_deleteprop   = new RPSErrDeleteProposal(True);

  app_exit     = new ExitCmd(True);

  // Edit commands
  app_sort_propno  = new RPSErrSortCmd((char *)"By Proposal Number",
	RPSErrPropList::SORT_PROPNO,True);
  app_sort_last4  = new RPSErrSortCmd((char *)"By Last 4 Proposal Number",
	RPSErrPropList::SORT_LAST_4,True);
  app_sort_reverse4  = new RPSErrSortCmd((char *)"By Reverse Last 4",
	RPSErrPropList::SORT_REVERSE_4,True);
  app_sort_pi      = new RPSErrSortCmd((char *)"By PI",
	RPSErrPropList::SORT_PI,True);
  app_sort_title   = new RPSErrSortCmd((char *)"By Title",
	RPSErrPropList::SORT_TITLE,True);
  app_sort_type    = new RPSErrSortCmd((char *)"By Type",
	RPSErrPropList::SORT_TYPE,True);
  app_refresh      = new RPSErrRefreshCmd(True);
  app_find         = new RPSErrFindCmd(True);
  app_findnext     = new FindNextCmd(True,app_find);

  // View commands
  app_view_proposal  = new RPSErrViewProposal(True);
  app_view_error_log = new RPSErrViewErrorLog(True);
  app_view_deleted = new RPSErrViewChecks("Deleted Proposals",True,"prop_list_deleted_props.pl",FALSE);

  // Statistics commands
  app_checks   = new RPSErrViewChecks("Proposal Checks",True,"prop_rps_checks.pl");
  app_recvstats = new RPSErrViewChecks("Receiver Statistics",True,"prop_receiver_stats.pl ");
  app_allstats = new RPSErrViewChecks("All PROPOSED Statistics",True,"prop_all_stats.pl");
  app_gtostats = new RPSErrViewChecks("GTO Statistics",True,"prop_gto_stats.pl -t ");
  app_jointstats = new RPSErrViewChecks("Joint Statistics",True,"prop_joint_stats.pl");
  app_lpstats = new RPSErrViewChecks("LP/VLP Statistics",True,"prop_lp_vlp_stats.pl");
  app_lists = new RPSErrViewChecks("Proposal Lists",True,"proposal_lists.pl ");

  // Help
  app_helpg     = new PropHelpMsg((char *)"prop.hlp",
	(char *)"General Proposal Application Help");
  app_help      = new PropHelpMsg((char *)"prop_rps_gui.hlp",
	(char *)"On prop_rps_gui");
  app_helpo     = new PropHelpSelect(TRUE);

  // initialize variables
  search_pos = 0;

  err_w  = NULL;
  rps_w  = NULL;
  prop_w = NULL;
  popup  = NULL;

  for (int ii=0;ii<NUM_LABELS;ii++) {
    label_w[ii] = NULL;
  }

 
}


// -------------------------------------------------------------
// Destructor
// -------------------------------------------------------------
RPSErrMenuWindow :: ~RPSErrMenuWindow ()
{

  delete app_open;
  delete app_retrieve;
  delete app_print;
  delete app_deleteprop;
  delete app_exit;

  delete app_sort_propno;
  delete app_sort_last4;
  delete app_sort_reverse4;
  delete app_sort_pi;
  delete app_sort_title;
  delete app_sort_type;
  delete app_refresh;
  delete app_find;
  delete app_findnext;

  delete app_view_proposal;
  delete app_view_error_log;

  delete app_checks;
  delete app_allstats;
  delete app_gtostats;
  delete app_jointstats;
  delete app_lpstats;
  //delete app_upload;
  delete app_recvstats;

  delete app_help;
  delete app_helpo;
  delete app_helpg;


  delete err_w;
  delete rps_w;
  delete prop_w;

  delete popup;

  for (int ii=0;ii<NUM_LABELS;ii++) {
    delete label_w[ii];
  }

  if (cursor)
    XFreeCursor(theApplication->display(),cursor);
}

// -------------------------------------------------------------
// Create the workarea.  
// -------------------------------------------------------------
void RPSErrMenuWindow :: createWorkArea(Widget parent) 
{
  Widget paned_w;
  Widget form3_w;
  char   buffer[200];


  // initialize application specific files
  theHelpMessage->ReadAppFile((char *)PROP_HELP_FILE);


  // add the popup menu translation
  if (parsed_trans == 0)
    {
    parsed_trans = XtParseTranslationTable(myTranslations);
    XtAppAddActions(XtWidgetToApplicationContext(base_w),
                    myActions,XtNumber(myActions));
    }

  // create the window pain container
  paned_w = XtVaCreateManagedWidget("RPSErrPane",
        xmPanedWindowWidgetClass,parent,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
	XmNuserData,this,
        NULL);

  

  // form for database list
  form3_w = XtVaCreateManagedWidget("RPSErrPropForm",
        xmFormWidgetClass,paned_w,
        NULL);


  label_w[PROP_LABEL] = new Label(form3_w,(char *)"Proposal Database :",
	0,0,0,(char *)"HeaderLabel" );
  XtVaSetValues(label_w[PROP_LABEL]->baseWidget(),
	XmNtopAttachment, XmATTACH_FORM,
	XmNtopOffset, GEN_FORM_OFFSET,
        XmNleftAttachment,XmATTACH_FORM,
	NULL);

  sprintf(buffer,RPS_SHORT_LIST,"Prop#","PI", "Type","R", "Resubmit",
	"L","Linked","Proposal Title                                          ");
  label_w[3] = new Label(form3_w,buffer,
	0,0,0,(char *)"RPSPropListLabel" );
  prop_w = new RPSErrPropList(form3_w,this);
  theToolTipManager->NewToolTip(
        (char *)"List of all PROPOSED proposals for current cycle from database",
        prop_w->baseWidget());


  XtVaSetValues(label_w[3]->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,label_w[PROP_LABEL]->baseWidget(),
        XmNtopOffset,GEN_FORM_OFFSET*3,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);
  XtVaSetValues(XtParent(prop_w->baseWidget()),
	XmNtopAttachment, XmATTACH_WIDGET,
	XmNtopWidget,label_w[3]->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        XmNbottomOffset,GEN_FORM_OFFSET,
	XmNuserData,this,
        NULL);
  this->SetListLabel(); 

  XtOverrideTranslations(prop_w->baseWidget(),parsed_trans);
  XtVaSetValues(prop_w->baseWidget(),
	XmNuserData,this,
	NULL);

  // create the popup menu widget
  //popup = new RPSErrPopupMenu(XtParent(prop_w->baseWidget()));
  popup = new RPSErrPopupMenu(prop_w->baseWidget());
}
// -------------------------------------------------------------
// -------------------------------------------------------------
void RPSErrMenuWindow::SetListLabel()
{
  char buffer[100];
  int  cnt;

  cnt = prop_w->GetCount();
  sprintf(buffer,"Proposal Database: %d proposals",cnt);
  label_w[PROP_LABEL]->SetLabel(buffer);

}

// -------------------------------------------------------------
// Create the pulldown menus
// -------------------------------------------------------------
void RPSErrMenuWindow::createMenuPanes()
{

  RPSErr_CreateFileMenu(main_menuBar,this);
  RPSErr_CreateEditMenu(main_menuBar,this,False);
  RPSErr_CreateViewMenu(main_menuBar,this,False);
  RPSErr_CreateStatMenu(main_menuBar,this);
  RPSErr_CreateHelpMenu(main_menuBar,this);

}

// -------------------------------------------------------------
// return the current selected proposal item from the scrolled
// list window pane
// -------------------------------------------------------------
string  RPSErrMenuWindow::GetCurrentItem()
{
  string prop_str;
  char *tmp = NULL;

  if (prop_w->GetCurrentItem(&tmp) > -1 ) {
    if (tmp) {
      prop_str = tmp;
      free(tmp);
    }
  }
  else
    prop_str.clear();

  return prop_str;
}

// -------------------------------------------------------------
// return the current selected proposal number from the scrolled
// list window panel
// -------------------------------------------------------------
string  RPSErrMenuWindow::GetCurrentProposalNumber()
{
  return prop_w->GetCurrentProposalNumber();
}

// -------------------------------------------------------------
// return the current selected proposal numbers from the scrolled
// list window panel
// -------------------------------------------------------------
vector<string>  RPSErrMenuWindow::GetCurrentProposalNumbers()
{
  return prop_w->GetCurrentProposalNumbers();
}

// -------------------------------------------------------------
// Find the string in the data areas
// -------------------------------------------------------------
Boolean RPSErrMenuWindow::Find(char *str,Boolean case_flag,Boolean start_flag)
{
  Boolean retval;
   
  if (start_flag)
    search_pos = 0;

  retval = prop_w->SetMatchingItem(str,&search_pos,False,case_flag);

  return retval;
}


//----------------------------------------------------------------
// Popup menu
//----------------------------------------------------------------
void RPSErrMenuWindow::PopupMenuCB( Widget w,XEvent *event)
{
  RPSErrMenuWindow *obj;

  XtVaGetValues(w,XmNuserData,&obj,NULL);

  if (obj && obj->popup) {

    XmMenuPosition(obj->popup->baseWidget(),(XButtonPressedEvent *)event);
    XtManageChild(obj->popup->baseWidget());
  }
}


