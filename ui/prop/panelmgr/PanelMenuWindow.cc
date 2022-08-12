 
/*H****************************************************************************
* Copyright (c) 1995-2016, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Planning -
        PANEL MANAGER Menu Window.  This class contains access to all the
        pulldown menu command classes.  It also instantiates the matrix
        classes which displays the proposal and panelist data for a
        specified panel.



* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>
#include <iomanip>
#include <Xm/Xm.h> 
#include <Xm/Form.h> 
#include <Xm/PanedW.h> 
#include <X11/cursorfont.h> 

#include "ap_common.hh"
#include "ToggleButton.hh"
#include "BaseMessageArea.hh"
#include "MenuBar.hh"
#include "ErrorMessage.hh"
#include "ToolTip.hh"
#include "HelpMessage.hh"

#include "PropHelpCodes.h"

#include "ProposalDefs.hh"
#include "PanelDefs.hh"

#include "PanelMenuWindow.hh"
#include "PanelApp.hh"
#include "PanelCommands.hh"
#include "PanelExterns.hh"


static const char *PANEL_LBL = "PANELS :";
static const char *PANEL_LIST_LBL =  "Panel     Subject Categories";

static const char *MEMBER_LBL = "PANELISTS";
static const char *MEMBER_LIST_LBL = "Panel Name                 Type            #Pri #Sec  Score           Conflicts";

static const char *PROPOSAL_LBL = "PROPOSALS";
static const char *PROPOSAL_LIST_LBL = 
"                            Obs.   No.  No. w/   Exp.     Primary          Secondary       \nProposal#  PI               Type  Tgts. Constr.  Time     Reviewer         Reviewer        Score  Joint      Gratings  P.I. Institution                        Title";



static Cursor cursor = 0;
static const char myTranslations[] =
 "#augment \
  <Btn3Down> : PanelPopupMenuCB(0) \n";


static XtActionsRec myActions[] =
{
  {(char *)"PanelPopupMenuCB",(XtActionProc)&PanelMenuWindow::PopupMenuCB},
};

static  XtTranslations parsed_trans = 0;




// -------------------------------------------------------------
// Constructor
// -------------------------------------------------------------
PanelMenuWindow :: PanelMenuWindow (char *name) 
		: MenuWindow(name,PANEL_MAIN_HTML,True,False,False)
{ 
  string str;
  string cmd;

  // File commands
  app_print    = new PanelPrintCmd(True,NULL,PANEL_PRINT_HTML);
  app_exit     = new ExitCmd(True);


  // Edit commands
  app_clump    = new PanelClumpCmd(True);
  app_link     = new PanelSelectLinked(True);
  
  app_sort     = new PanelSortCmd();
  app_refresh  = new PanelRefreshCmd(True);
  app_findprop  = new PanelFindProp();

  //app_select_all = new PanelPropSelectCmd((char *)"Select All ",True,True);
  app_deselect_all = new PanelPropSelectCmd((char *)"Deselect All Proposals",False,True);
  app_find     = new PanelFindCmd(True);
  app_findnext = new FindNextCmd(True,app_find);


  // View commands
  app_view_proposal  = new PanelViewProposal(True);
  app_view_conflicts = new PanelViewConflicts(True,"Proposal Conflict File");
  app_view_cois      = new PanelViewCoIs(True);
  app_view_detectors = new PanelViewDetectors((char *)"Detector/Gratings List",True,
	(char *)"prop_detector_for_proposals.pl");
  app_view_linked  = new PanelViewChecks("Linked Proposals",True,"prop_view_linked.pl");
  app_view_propkey  = new PanelViewChecks("Keywords by Proposal",True,"prop_view_tgtkeyword.pl");
  app_view_keyprop  = new PanelViewChecks("Proposals by Keyword",True,"prop_view_tgtkeyword.pl -k");

  app_view_allmembers = new PanelAllMembers(True,"All Panelists");
  app_view_panelist  = new PanelViewMember(True);

  str = getenv("PANEL_CLUMP_CONFLICT");
  app_view_clumps  = new PanelViewLog(True,"Conflict Clumps",str.c_str());


  // Assign commands
  app_remove          = new PanelRemoveMember((char *)"Remove Panelist",True);
  app_removeprop      = new PanelRemoveProposal((char *)"Remove Proposal",True);
  app_assign_primary  = new PanelAssignReviewer((char *)"Primary Reviewer",PRI_REV,True);
  app_assign_secondary= new PanelAssignReviewer((char *)"Secondary Reviewer",SEC_REV,True);

  app_assign_panelist = new PanelAssignMember(True);
  app_assign_proposal = new PanelAssignProposal(True,
			(char *)"Unassigned Proposals");

  // Tools Command
  app_log           = new PanelLogCmd(True,"Notes");
  app_logmsg        = new PanelLogMsgCmd(True,"View/Edit Notes");
  app_view_log      = new PanelViewLog(True,"View Log");
  app_view_checks   = new PanelViewChecks("Panel Checks",True,
			"prop_panel_checks.pl ");
  app_propmgr       = new PanelPropMgrCmd(True);
  app_panel_lists   = new PanelPeerList(True);
  app_stat_panel    = new PanelViewChecks("Panel Statistics",True,"prop_panel_stats.pl");
  app_pre_conflicts = new PanelViewChecks("PAS Conflict/Grade Status",True,"prop_pre_conflicts.pl");
  app_reassignment  = new PanelViewChecks("Reassignment Requests",True,"peer_reassignments.pl");

  // Help
  app_helpg     = new PropHelpMsg((char *)"prop.hlp","General Proposal Application Help");
  app_help      = new PropHelpMsg((char *)"prop_panel_gui.hlp","On prop_panel_gui");
  app_helpo     = new PropHelpSelect(TRUE);

  
  panlbl  = NULL;
  panlbl2  = NULL;
  panlist = NULL;
  plist = NULL;
  plbl  = NULL;
  plbl2 = NULL;
  mlist = NULL;
  mlbl  = NULL;
  mlbl2 = NULL;

  popup = NULL;
  save_panel = FALSE;
  parray = NULL;

  app_move = NULL;
  move_cnt = 0;
  move_w = NULL;
  move_pulldown = NULL;
  search_pos1 = 0;
  search_pos2 = 0;
  search_pos3 = 0;
}


// -------------------------------------------------------------
// Destructor
// -------------------------------------------------------------
PanelMenuWindow :: ~PanelMenuWindow ()
{
  int ii;

  delete app_print;
  delete app_exit;

  delete app_clump;
  delete app_link;
  delete app_remove;
  delete app_removeprop;
  delete app_sort;
  delete app_select_all;
  delete app_deselect_all;

  for (ii=0; ii<move_cnt;ii++) {
    delete app_move[ii];
  }
  free(app_move);
  delete app_findprop;
  delete app_find;
  delete app_findnext;
  delete app_refresh;

  delete app_view_panelist;
  delete app_view_proposal;
  delete app_view_cois;
  delete app_view_allmembers;
  delete app_view_linked;
  delete app_view_log;
  delete app_view_checks;
  delete app_view_clumps;
  delete app_view_detectors;
  delete app_view_conflicts;

  delete app_assign_primary;
  delete app_assign_secondary;
  delete app_assign_panelist;
  delete app_assign_proposal;

  delete app_log;
  delete app_logmsg;
  delete app_panel;
  delete app_propmgr;
  delete app_stat_panel;

  delete app_help;
  delete app_helpo;
  delete app_helpg;


  delete plist;
  delete panlist;
  delete mlist;
  delete panlbl;
  delete panlbl2;
  delete plbl;
  delete plbl2;
  delete mlbl;
  delete mlbl2;

  if (cursor)
    XFreeCursor(theApplication->display(),cursor);

  delete parray;
  delete popup;


  if (move_w)
    free (move_w);
}

// -------------------------------------------------------------
// Create the workarea.  
// -------------------------------------------------------------
void PanelMenuWindow :: createWorkArea(Widget parent) 
{
  Widget paned_w;
  Widget form1_w,form2_w,form3_w;

  // initialize application specific files
  theHelpMessage->ReadAppFile((char *)PROP_HELP_FILE);

  // add the popup menu translation
  if (parsed_trans == 0)
    {
    parsed_trans = XtParseTranslationTable(myTranslations);
    XtAppAddActions(XtWidgetToApplicationContext(base_w),
                    myActions,XtNumber(myActions));
    }


  paned_w = XtVaCreateManagedWidget("PanelMgrPane",
        xmPanedWindowWidgetClass,parent,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);


  form1_w = XtVaCreateManagedWidget("PanelPanelForm",
        xmFormWidgetClass,paned_w,
        NULL);
  form2_w = XtVaCreateManagedWidget("PanelForm",
        xmFormWidgetClass,paned_w,
        NULL);
  form3_w = XtVaCreateManagedWidget("PanelForm",
        xmFormWidgetClass,paned_w,
        NULL);

  // PANEL LIST
  panlbl2 = new Label(form1_w,(char *)PANEL_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(panlbl2->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
	NULL);
  panlbl = new Label(form1_w,(char *)PANEL_LIST_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(panlbl->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,panlbl2->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        NULL);
  panlist = new PanelPanelList(form1_w,this);
  theToolTipManager->NewToolTip((char *)"List of all panels for current cycle",
	panlist->baseWidget());
  XtVaSetValues(XtParent(panlist->baseWidget()),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,panlbl->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);

   
  // PANEL MEMBER LIST WINDOW
  mlbl2 = new Label(form2_w,(char *)MEMBER_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(mlbl2->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
	NULL);
  mlbl = new Label(form2_w,(char *)MEMBER_LIST_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(mlbl->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,mlbl2->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        NULL);
  mlist = new PanelMemberList(form2_w,this);
  theToolTipManager->NewToolTip((char *)"List of all members for current panel.",
        mlist->baseWidget());

  XtVaSetValues(XtParent(mlist->baseWidget()),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,mlbl->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);

  // PROPOSAL LIST WINDOW
  plbl2 = new Label(form3_w,(char *)PROPOSAL_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(plbl2->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
	NULL);
  plbl = new Label(form3_w,(char *)PROPOSAL_LIST_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(plbl->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,plbl2->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        NULL);
  plist = new PanelPropList(form3_w,this);
  theToolTipManager->NewToolTip(
	(char *)"List of all proposals for current panel.",
        plist->baseWidget());
  XtVaSetValues(XtParent(plist->baseWidget()),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,plbl->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        XmNuserData,this,
        NULL);

  XtOverrideTranslations(plist->baseWidget(),parsed_trans);
  XtVaSetValues(plist->baseWidget(),
        XmNuserData,this,
        NULL);

  XtOverrideTranslations(mlist->baseWidget(),parsed_trans);
  XtVaSetValues(mlist->baseWidget(),
        XmNuserData,this,
        NULL);
  popup = new PanelPopupMenu(plist->baseWidget());

  
}

//----------------------------------------------------------------
// Popup menu
//----------------------------------------------------------------
void PanelMenuWindow::PopupMenuCB( Widget w,XEvent *event)
{
  PanelMenuWindow *obj;

  XtVaGetValues(w,XmNuserData,&obj,NULL);

  if (obj && obj->popup) {

    XmMenuPosition(obj->popup->baseWidget(),(XButtonPressedEvent *)event);
    XtManageChild(obj->popup->baseWidget());
  }
}



// -------------------------------------------------------------
// Create the pulldown menus
// -------------------------------------------------------------
void PanelMenuWindow::createMenuPanes()
{

  Panel_CreateFileMenu(main_menuBar,this);
  Panel_CreateEditMenu(main_menuBar,this,False);
  Panel_CreateViewMenu(main_menuBar,this,False);
  move_pulldown = Panel_CreateAssignMenu(main_menuBar,this,False);
  Panel_CreateToolMenu(main_menuBar,this);
  Panel_CreateHelpMenu(main_menuBar,this);

}

void PanelMenuWindow::AddMoveMenu()
{
  PanelEntry *pe;
  int ii;
  string tmp;

  // delete any existing move proposal submenus
  for (ii=0; ii < move_cnt;ii++) {
    XtDestroyWidget(move_w[ii]);
    delete app_move[ii];
  }
  if (move_w)
    free(move_w);
  if (app_move)
    free(app_move);

  

  // first create the move proposals menu command for each panel;
  move_cnt = parray->GetSize();
  move_w = (Widget *)calloc(move_cnt,sizeof(Widget));
  app_move = (Cmd **)calloc(move_cnt,sizeof(Cmd *)); 
  for (ii=0;ii<move_cnt;ii++) {
    pe = parray->GetRecordbyIndex(ii);
    tmp=" "; 
    tmp.append( pe->GetPanelName());
    tmp.append("   ");
    app_move[ii] = new PanelMoveProp((char *)tmp.c_str(),pe->GetPanelName(),TRUE);
    move_w[ii] = main_menuBar->AddPushButtonInterface(
	move_pulldown,move_prop_cmd(ii),0,0,0);

  }
  

}

// -------------------------------------------------------------
// Return True if file needs to be saved.
// -------------------------------------------------------------
Boolean PanelMenuWindow :: GetNeedSave()
{
  
  return save_panel;
}

// -------------------------------------------------------------
// Set True if file needs to be saved.  If True, the
// user may have made changes to the file.
// -------------------------------------------------------------
void PanelMenuWindow::SetNeedSave(Boolean flag)
{
  save_panel = flag;

}

// -------------------------------------------------------------
// print options
// -------------------------------------------------------------
void PanelMenuWindow::Print(ostream &oss,Boolean all_panels,
		Boolean current_panel, Boolean proposal_list,
		Boolean list_title, Boolean all_list_title)
{
  PanelEntry *pe;

  if (all_panels) {
    oss << PANEL_LIST_LBL << "\n";
    oss << setfill('-') << setw(145) << "\n";
    panlist->Print(oss);
    oss << "^L" << endl;
  }
  else if (current_panel || proposal_list || list_title) {
    pe = this->GetCurrentPanel();
    if (pe == NULL) {
       theErrorMessage->DisplayMessage("Please select a panel.");
       return;
    }
  }

  if (current_panel) {
    oss << "PANEL: " << this->GetCurrentPanel()->GetPanelName() << endl;
    oss << MEMBER_LIST_LBL << "\n";
    oss << setfill('-') << setw(80) << "\n";
    mlist->Print(oss);
    oss << "^L" << endl;
  }
  if (proposal_list) {
    oss << "PANEL: " << this->GetCurrentPanel()->GetPanelName() << endl;
    oss << PROPOSAL_LIST_LBL << "\n";
    oss << setfill('-') << setw(145) << "\n";
    plist->Print(oss);
    oss << "^L" << endl;
  }

  if (list_title) {
    oss << "PANEL: " << this->GetCurrentPanel()->GetPanelName() << endl;
    plist->GetProposals()->PrintPanelList(oss,-1,TRUE);
    oss << "^L" << endl;
  }
  if (all_list_title) {
    parray->PrintProposalList(oss);
  }
}

// -------------------------------------------------------------
// Find the string in the data areas
// -------------------------------------------------------------
Boolean PanelMenuWindow::Find(char *str,Boolean case_flag,Boolean start_flag)
{
  Boolean retval;
  Boolean retval2;
  Boolean retval3;

  if (start_flag) {
    search_pos1 = 0;
    search_pos2 = 0;
    search_pos3 = 0;
  }

  retval = panlist->SetMatchingItem(str,&search_pos1,False,case_flag);
  retval2 = mlist->SetMatchingItem(str,&search_pos2,False,case_flag);
  retval3 = plist->SetMatchingItem(str,&search_pos3,False,case_flag);

  return (retval | retval2 | retval3);
}

// -------------------------------------------------------------
// return current selected panel entry
// -------------------------------------------------------------
PanelEntry *PanelMenuWindow::GetCurrentPanel()
{
  int cnt;
  int *pos;
  PanelEntry *pe = NULL;

  pos = panlist->GetSelectedItems(&cnt);
  if (cnt == 1 ) {
    pe = parray->GetRecordbyIndex(pos[0] - 1);
  }

  return pe;

}
// -------------------------------------------------------------
// -------------------------------------------------------------
PersonEntry *PanelMenuWindow::GetCurrentMember()
{

  return mlist->GetCurrent();
}

// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelMenuWindow::RemoveProposals()
{
  PanelEntry *pe;

  if (plist) {
    pe = this->GetCurrentPanel();

    plist->RemoveProposals(pe);

    // recalculate statistics for old,new panels
    pe->LoadStatistics();

    // redisplay the proposal list for the current panel
    plist->BuildList(pe);

    // redisplay the member info 
    mlist->BuildList(pe);

    // redisplay all the panels
    DisplayPanels(FALSE);


  }
}

// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelMenuWindow::RemoveCurrentMember()
{
  string msg;
  int cnt;
  int *pos;
  PanelEntry *pan;
  PersonEntry *pers;

  pos = mlist->GetSelectedItems(&cnt);
  pan = this->GetCurrentPanel();
  pers = mlist->GetCurrent();

  if (cnt == 1) {
    msg = "Panel member ";
    msg += pers->GetLast();
    msg += " removed from panel ";
    msg += pan->GetPanelName();
    if (pan->RemoveMember(pos[0] - 1)) {
      theApplication->Log(msg);
      if (((PanelLogCmd *)(thePanelWindow->log_cmd()))->IsActive()) {
        thelogmsg_cmd()->SetMessage(msg);
        thelogmsg_cmd()->execute();
      }

      mlist->BuildList(pan);
      plist->BuildList(pan);
    }
    else {
      theErrorMessage->DisplayMessage("Error occurred removing panel member from panel.");
    }
  }
  else {
      theErrorMessage->DisplayMessage("Please select one member at a time when removing a panel member.");
  }
}

// -------------------------------------------------------------
// return current selected proposal number
// -------------------------------------------------------------
string PanelMenuWindow::GetCurrentProposalNumber()
{
  char    propno[PROP_NBR_SIZE];
  string prop_str;
  char   *tmp=NULL;
  

  memset(propno,0,PROP_NBR_SIZE);
  if (plist->GetCurrentItem(&tmp) > -1 ) {
    sscanf(tmp,"%8s",propno);
  }

  prop_str = propno;

  if (tmp)
    free(tmp);
  return prop_str;

}


// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelMenuWindow::MoveProposals(const char *panel_id,Boolean remove_rev)
{
  PanelEntry *newpe = parray->GetRecordbyId((char *)panel_id);
  PanelEntry *pe = GetCurrentPanel();

  if (pe && newpe) {
    if (pe != newpe) {
      // remove proposal from existing, add to new panel
      if (plist) {
        if (plist->MoveProposals(pe,newpe,remove_rev)) {
  
          // recalculate statistics for old,new panels
          pe->LoadStatistics();
          newpe->LoadStatistics();
  
          // redisplay the proposal list for the current panel
          plist->BuildList(pe);
          // redisplay member list
          mlist->BuildList(pe);
  
          // redisplay all the panels
          DisplayPanels(FALSE);
        }
      }
    }
  }
}

// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelMenuWindow::AssignReviewer(int type,PersonEntry *reviewer,int *props,int pcnt)
{
/*
  PanelEntry *pe = GetCurrentPanel();
*/

  plist->AssignReviewer(type,reviewer,props,pcnt);

/*
  mlist->BuildList(pe);
  DisplayPanels(False,True);
*/

}

// -------------------------------------------------------------
// return selected items from proposal list
// -------------------------------------------------------------
int *PanelMenuWindow::GetSelectedProposals(int *sel_cnt)
{

  return (plist->GetSelectedItems(sel_cnt));
}

// -------------------------------------------------------------
// return selected items from member list
// -------------------------------------------------------------
int *PanelMenuWindow::GetSelectedMembers(int *sel_cnt)
{
  return (mlist->GetSelectedItems(sel_cnt));
}

void PanelMenuWindow::DisplayCurrentPanelData(int pos)
{
  PanelEntry *pe;
  char buffer[120];

  pe = parray->GetRecordbyIndex(pos);
  if (pe) {
    memset(buffer,0,sizeof(buffer));
    sprintf(buffer,"%s for %s",PROPOSAL_LBL,pe->GetPanelName());
    plbl2->SetLabel(buffer);
    plist->BuildList(pe);
    sprintf(buffer,"%s for %s",MEMBER_LBL,pe->GetPanelName());
    mlbl2->SetLabel(buffer);
    pe->ReloadMembers();
    mlist->BuildList(pe);
  }
  
}

  
// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelMenuWindow::DisplayPanels(Boolean cb_flag,Boolean refresh_flag)
{

  // reload stats to pick up changes from other users working on panels
  //if (refresh_flag && parray) {
    //parray->ReloadStatistics();
  //}
  parray = panlist->BuildList(parray,cb_flag); 
  AddMoveMenu();
}
