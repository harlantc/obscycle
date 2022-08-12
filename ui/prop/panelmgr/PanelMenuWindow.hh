
/*H****************************************************************************
* Copyright (c) 1995-2016, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelMenuWindow.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning - 
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
#ifndef PANELMENUWINDOW_HH
#define PANELMENUWINDOW_HH


#include "ap_common.hh"
#include "MenuWindow.hh"
#include "Label.hh"

#include "PanelPropList.hh"
#include "PanelPanelList.hh"
#include "PanelMemberList.hh"
#include "PanelPopupMenu.hh"

class Cmd;
class DBSubcat;
class PanelArray;
class PanelLogMsgCmd;

class PanelMenuWindow : public MenuWindow {

  public:

    PanelMenuWindow (char *name); 
    ~PanelMenuWindow (); 

    // commands for the menubar items
    Cmd *print_cmd ()    { return (app_print) ; }
    Cmd *exit_cmd ()     { return (app_exit) ; }

    Cmd *remove_cmd ()   { return (app_remove) ; }
    Cmd *removeprop_cmd ()   { return (app_removeprop) ; }
    Cmd *move_prop_cmd (int idx)   { return (app_move[idx]) ; }
    Cmd *clump_cmd ()    { return (app_clump) ; }
    Cmd *link_cmd ()     { return (app_link)  ; }

    Cmd *sort_cmd ()     { return (app_sort) ; }
    Cmd *findprop_cmd ()     { return (app_findprop) ; }
    Cmd *find_cmd ()     { return (app_find) ; }
    Cmd *findnext_cmd () { return (app_findnext) ; }
    Cmd *refresh_cmd ()  { return (app_refresh) ; }
    
    Cmd *select_all_cmd()   { return (app_select_all); }
    Cmd *deselect_all_cmd() { return (app_deselect_all); }

    Cmd *view_panelist_cmd ()   { return (app_view_panelist) ; }
    Cmd *view_proposal_cmd ()   { return (app_view_proposal) ; }
    Cmd *view_cois_cmd ()       { return (app_view_cois) ; }
    Cmd *view_allmembers_cmd () { return (app_view_allmembers) ; }
    Cmd *view_linked_cmd ()     { return (app_view_linked) ; }
    Cmd *view_propkey_cmd ()    { return (app_view_propkey) ; }
    Cmd *view_keyprop_cmd ()    { return (app_view_keyprop) ; }
    Cmd *view_log_cmd()         { return (app_view_log) ; }
    Cmd *view_checks_cmd()      { return (app_view_checks) ; }
    Cmd *view_clumps_cmd()      { return (app_view_clumps) ; }
    Cmd *view_detectors_cmd()   { return (app_view_detectors) ; }
    Cmd *view_conflicts_cmd()   { return (app_view_conflicts) ; }

    Cmd *assign_primary_cmd()   { return (app_assign_primary) ; }
    Cmd *assign_secondary_cmd() { return (app_assign_secondary) ; }
    Cmd *assign_panelist_cmd()  { return (app_assign_panelist) ; }
    Cmd *assign_proposal_cmd()  { return (app_assign_proposal) ; }

    Cmd *panel_cmd()            { return (app_panel) ; }
    Cmd *log_cmd()              { return (app_log) ; }
    Cmd *logmsg_cmd()           { return (app_logmsg) ; }
    PanelLogMsgCmd *thelogmsg_cmd() { return ((PanelLogMsgCmd *)app_logmsg); }
    Cmd *panel_lists_cmd()      { return (app_panel_lists) ; }
    Cmd *pre_conflicts_cmd()    { return (app_pre_conflicts) ; }

    Cmd *propmgr_cmd()          { return (app_propmgr) ; }
    Cmd *stat_panel_cmd()       { return (app_stat_panel) ; }
    Cmd *reassignment_cmd()     { return (app_reassignment) ; }

    Cmd *help_cmd()          { return (app_help); }
    Cmd *helpg_cmd()         { return (app_helpg); }
    Cmd *helpo_cmd()         { return (app_helpo); }

    // track possible modification to current panel
    Boolean GetNeedSave();
    void SetNeedSave(Boolean);


    // return current selected proposal number
    string  GetCurrentProposalNumber();
    int  *GetSelectedProposals(int *);
    void MoveProposals(const char *panel_id,Boolean remove_revs);
    PanelPropList *GetPropList() { return plist; }

    // return current selected panel
    PanelEntry *GetCurrentPanel();
    void DisplayCurrentPanelData(int);
    PanelArray *GetPanelArray() { return parray; }

    // return single selection of member
    PersonEntry *GetCurrentMember();
    int  *GetSelectedMembers(int *);
    PanelMemberList *GetMemberList() { return mlist; };
    void  RemoveCurrentMember();
    void  RemoveProposals();

    // assign reviewer to selected proposals
    void AssignReviewer(int review_type,PersonEntry *,int *proposals,int prop_cnt);

    // print options
    void Print(ostream &,Boolean all_panels, Boolean current_panel,
		Boolean proposal_list,Boolean title_list, 
		Boolean all_title_list);

    // sort panel member/proposal lists
    //void Sort(int type);

    // Find the string in any of the data areas
    Boolean Find(char *,Boolean ,Boolean);
   
    int GetMoveCnt() { return move_cnt; }


    void AddMoveMenu();

    // redisplay all the panels;
    void DisplayPanels(Boolean cb_flg=TRUE,Boolean refresh_flag=FALSE); 
  

    static void PopupMenuCB( Widget w,XEvent *event);

    virtual const char *const className() {return ("PanelMenuWindow");}

  protected:

    virtual void createWorkArea(Widget);
    virtual void createMenuPanes();

    // pulldown menu commands
    Cmd *app_print;
    Cmd *app_exit;

    Cmd *app_refresh;
    Cmd *app_remove;
    Cmd *app_removeprop;
    Cmd **app_move;
    Cmd *app_clump;
    Cmd *app_link;

    Cmd *app_sort;

    Cmd *app_findprop;
    Cmd *app_find;
    Cmd *app_findnext;

    Cmd *app_select_all;
    Cmd *app_deselect_all;

    Cmd *app_view_panelist;
    Cmd *app_view_proposal;
    Cmd *app_view_cois;
    Cmd *app_view_allmembers;
    Cmd *app_view_linked;
    Cmd *app_view_propkey;
    Cmd *app_view_keyprop;
    Cmd *app_view_log;
    Cmd *app_view_checks;
    Cmd *app_view_clumps;
    Cmd *app_view_detectors;
    Cmd *app_view_conflicts;

    Cmd *app_assign_primary;
    Cmd *app_assign_secondary;
    Cmd *app_assign_panelist;
    Cmd *app_assign_proposal;

    Cmd *app_panel;
    Cmd *app_panel_lists;
    Cmd *app_pre_conflicts;
    Cmd *app_log;
    Cmd *app_logmsg;

    Cmd *app_propmgr;

    Cmd *app_help;
    Cmd *app_helpg;
    Cmd *app_helpo;

    Cmd *app_stat_panel;
    Cmd *app_reassignment;

    // the layout
    PanelPanelList  *panlist;
    Label           *panlbl;
    Label           *panlbl2;
    PanelPropList   *plist;
    Label           *plbl;
    Label           *plbl2;
    PanelMemberList *mlist;
    Label           *mlbl;
    Label           *mlbl2;
    
    Widget  move_pulldown;
    Widget *move_w;
    int     move_cnt;

    int search_pos1;       // used by Find, Find Next
    int search_pos2;       // used by Find, Find Next
    int search_pos3;       // used by Find, Find Next

    PanelPopupMenu *popup;
    Boolean save_panel;   // flag used for keeping track if panel changed

    PanelArray *parray;
};

extern PanelMenuWindow *thePanelWindow;


#endif
