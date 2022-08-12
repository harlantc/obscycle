 
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Planning -
        PROPOSAL MANAGER Menu Window.  This class contains access to all the
        pulldown menu command classes.  It also instantiates the matrix
        classes which displays the proposal and target data.



* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include "ap_common.hh"
#include <stdlib.h>
#include <iomanip>
#include <Xm/Xm.h> 
#include <Xm/Form.h> 
#include <Xm/PanedW.h> 
#include <Xm/ScrolledW.h>
#include <X11/cursorfont.h> 

// guiext
#include "BaseMessageArea.hh"
#include "MenuBar.hh"
#include "Label.hh"
#include "HelpMessage.hh"
#include "ToolTip.hh"

#include "PropHelpCodes.h"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPopupMenu.hh"
#include "PropMgrApp.hh"
#include "PropMgrCommands.hh"
#include "PropMgrExterns.hh"
#include "PropMgrPropList.hh"



static const char *prop_hdr = 
"                         Norm.   Exp.     App.   No.  Obs.           Primary     Secondary   ";

static const char *prop_hdr2 = 
"Proposal#  PI            Rank    Time     Time   Tgts Type    Panel  Reviewer    Reviewer    Title";

static const char *tgt_hdr = 
"                                                         De-    Grat-   Exp.     App. ";
static const char *tgt_hdr2 = 
"Proposal#   R.A.          Dec.         Target Name       tector ing     Time     Time    PI";

static Cursor cursor = 0;
static char myTranslations[] =
 "#augment \
  <Btn3Down> : PropMgrPopupMenuCB(0) \n";


static XtActionsRec myActions[] =
{
  {(char *)"PropMgrPopupMenuCB",(XtActionProc)&PropMgrMenuWindow::PopupMenuCB},
};

static  XtTranslations parsed_trans = 0;


// -------------------------------------------------------------
// Constructor
// -------------------------------------------------------------
PropMgrMenuWindow :: PropMgrMenuWindow (const char *name) 
		: MenuWindow(name,PROPMGR_MAIN_HTML,True,False,False)
{ 
  // File commands
  app_retrieve = new PropMgrLoadCmd(True);
  app_file     = new PropMgrLoadFileCmd(True,0);
  app_print    = new PropMgrPrintCmd(TRUE,NULL,PROPMGR_PRINT_HTML);
  app_exit     = new ExitCmd(True);

  //app_new->addToActivationList (app_save);

  // Edit commands
  app_layout       = new PropMgrLayoutCmd((char *)"Layout by Target",True,True);
  app_select       = new PropMgrSelectCmd((char *)"Select All",True,True);
  app_deselect     = new PropMgrSelectCmd((char *)"Deselect All",False,True);
  app_remove       = new PropMgrRemoveCmd((char *)"Remove Proposal",True);
  app_refresh      = new PropMgrRefreshCmd(True);
  app_sort         = new PropMgrSortCmd(True,(char *)"Sort Proposals...",True);
  app_sort_t       = new PropMgrSortCmd(False,(char *)"Sort Targets...",True);
  app_find         = new PropMgrFindCmd(True);
  app_findnext     = new FindNextCmd(True,app_find);

  // Review commands
  app_review_primary    = new PropMgrReviewsCmd("Primary Review...",PRI_REV,'r',True);
  app_review_secondary  = new PropMgrReviewsCmd("Secondary Review...",SEC_REV,'S',True);
  app_review_peer  = new PropMgrReviewsCmd("Peer Review...",PEER_REV,'e',True);
  app_review_final  = new PropMgrReviewsCmd("Final Review...",FINAL_REV,'f',True);

  // View commands
  app_view_proposal  = new PropMgrViewProposal(True);
  app_view_cois      = new PropMgrViewCoIs(True);

  app_view_panel     = NULL;
  app_view_conflicts = NULL;
  app_view_log       = NULL;

  app_stat_proposed   = new PropMgrViewChecks("Proposed Statistics",True,"prop_all_stats.pl");
  app_stat_peer   = new PropMgrViewChecks("Approved Statistics",True,"prop_all_stats.pl -w ");
  app_stat_joint   = new PropMgrViewChecks("Joint Proposal Statistics",True,"prop_joint_stats.pl");
  app_stat_lpvlp   = new PropMgrViewChecks("LP/VLP Proposal Statistics",True,"prop_lp_vlp_stats.pl");
  app_stat_gto   = new PropMgrViewChecks("GTO Proposal Statistics",True,"prop_gto_stats.pl");
  app_stat_final   = new PropMgrViewChecks("Final Proposal Checks",True,"prop_final_checks.pl");

  
  // Help
  app_helpg     = new PropHelpMsg("prop.hlp","General Proposal Application Help");
  app_help      = new PropHelpMsg("prop_manager_gui.hlp","On prop_manager_gui");
  app_helpo     = new PropHelpSelect(TRUE);


  plist = NULL;
  plbl  = NULL;
  plbl2 = NULL;
  popup = NULL;

}


// -------------------------------------------------------------
// Destructor
// -------------------------------------------------------------
PropMgrMenuWindow :: ~PropMgrMenuWindow ()
{
  delete app_retrieve;
  delete app_file;
  delete app_print;
  delete app_exit;

  delete app_layout;
  delete app_select;
  delete app_deselect;
  delete app_remove;
  delete app_sort;
  delete app_find;
  delete app_findnext;

  delete app_review_primary;
  delete app_review_secondary;
  delete app_review_peer;
  delete app_review_final;

  delete app_view_proposal;
  delete app_view_panel;
  delete app_view_log;
  delete app_view_conflicts;
  delete app_view_cois;

  delete app_stat_proposed;
  delete app_stat_peer;
  delete app_stat_joint;
  delete app_stat_lpvlp;
  delete app_stat_gto;



  delete app_help;
  delete app_helpo;
  delete app_helpg;


  delete popup;
  delete plist;
  delete plbl;
  delete plbl2;

  if (cursor)
    XFreeCursor(theApplication->display(),cursor);
}

// -------------------------------------------------------------
// Create the workarea.  
// -------------------------------------------------------------
void PropMgrMenuWindow :: createWorkArea(Widget parent) 
{
  Widget paned_w;
  Widget form1_w;

  // initialize application specific files
  theHelpMessage->ReadAppFile((char *)PROP_HELP_FILE);

 // add the popup menu translation
  if (parsed_trans == 0)
    {
    parsed_trans = XtParseTranslationTable(myTranslations);
    XtAppAddActions(XtWidgetToApplicationContext(base_w),
                    myActions,XtNumber(myActions));
    }


  paned_w = XtVaCreateManagedWidget("PropMgrPane",
        xmPanedWindowWidgetClass,parent,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);


  form1_w = XtVaCreateManagedWidget("PropMgrForm",
        xmFormWidgetClass,paned_w,
        NULL);

/*
  Widget sw;
  sw = XtVaCreateManagedWidget("ScrolledWindow",
                xmScrolledWindowWidgetClass,form1_w,
                XmNtopAttachment,XmATTACH_FORM,
                XmNrightAttachment,XmATTACH_FORM,
                XmNleftAttachment,XmATTACH_FORM,
                XmNborderWidth,0,
                XmNbottomAttachment,XmATTACH_FORM,
                XmNscrollBarDisplayPolicy,XmAS_NEEDED,
                XmNscrollingPolicy,XmAUTOMATIC,
                NULL);

  Widget form2_w;
  form2_w = XtVaCreateManagedWidget("Form",
        xmFormWidgetClass,sw,
	XmNwidth,2000,
	XmNheight,2000,
        NULL);
*/

  plbl2 = new Label(form1_w,(char *)prop_hdr,
	0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(plbl2->baseWidget(),
	XmNtopAttachment,XmATTACH_FORM,
	XmNhighlightThickness,0,
	XmNleftAttachment,XmATTACH_FORM,
	NULL);
  plbl = new Label(form1_w,(char *)prop_hdr2,
	0,0,0,(char*)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(plbl->baseWidget(),
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,plbl2->baseWidget(),
	XmNtopOffset,0,
	XmNleftAttachment,XmATTACH_FORM,
	NULL);
  plist = new PropMgrPropList(form1_w,this);
  theToolTipManager->NewToolTip(
	(char *)"View scrollable list of proposals/targets.",
   	plist->baseWidget());
  XtVaSetValues(XtParent(plist->baseWidget()),
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,plbl->baseWidget(),
	XmNleftAttachment,XmATTACH_FORM,
	XmNrightAttachment,XmATTACH_FORM,
	XmNbottomAttachment,XmATTACH_FORM,
	NULL);

  XtVaSetValues(plist->baseWidget(),
        XmNuserData,this,
	NULL);

  XtOverrideTranslations(plist->baseWidget(),parsed_trans);



  popup = new PropMgrPopupMenu(plist->baseWidget());
  
}


// -------------------------------------------------------------
// Create the pulldown menus
// -------------------------------------------------------------
void PropMgrMenuWindow::createMenuPanes()
{

  PropMgr_CreateFileMenu(main_menuBar,this);
  PropMgr_CreateEditMenu(main_menuBar,this,False);
  PropMgr_CreateViewMenu(main_menuBar,this,False);
  PropMgr_CreateStatMenu(main_menuBar,this);
  PropMgr_CreateHelpMenu(main_menuBar,this);


}

//----------------------------------------------------------------
// Popup menu
//----------------------------------------------------------------
void PropMgrMenuWindow::PopupMenuCB( Widget w,XEvent *event)
{
  PropMgrMenuWindow *obj;

  XtVaGetValues(w,XmNuserData,&obj,NULL);

  if (obj && obj->popup) {

    XmMenuPosition(obj->popup->baseWidget(),(XButtonPressedEvent *)event);
    XtManageChild(obj->popup->baseWidget());
  }
}


// -------------------------------------------------------------
// return current selected proposal number
// -------------------------------------------------------------
string PropMgrMenuWindow::GetCurrentProposalNumber()
{
  string prop_str;

  if (plist)
    prop_str = plist->GetCurrentProposalNumber();

  return prop_str;

}


// -------------------------------------------------------------
// -------------------------------------------------------------
void PropMgrMenuWindow::SetHeader(Boolean hdr_flag)
{
  const char *ptr;
  const char *ptr2;

  if (hdr_flag) {
    ptr = prop_hdr;
    ptr2 = prop_hdr2;
  }
  else {
    ptr = tgt_hdr;
    ptr2 = tgt_hdr2;
  }

  plbl2->SetLabel((char *)ptr);
  plbl->SetLabel((char *)ptr2);
}
// -------------------------------------------------------------
// Find the string in the data areas
// -------------------------------------------------------------
Boolean PropMgrMenuWindow::Find(char *str,Boolean case_flag,Boolean start_flag)
{
  Boolean retval;

  if (start_flag)
    search_pos = 0;

  retval = plist->SetMatchingItem(str,&search_pos,False,case_flag);

  return (retval);
}

// -------------------------------------------------------------
// Print the current scrolled list window of proposals or targets
// -------------------------------------------------------------
void PropMgrMenuWindow::Print(ostream &oss)
{
  char *ptr;

  ptr = plbl2->GetLabel();
  oss << ptr << "\n";
  ptr = plbl->GetLabel();
  oss << ptr << "\n";
  oss << setfill('-') << setw(strlen(ptr)) << "\n";
  plist->Print(oss);
}


