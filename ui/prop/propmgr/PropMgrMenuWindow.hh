
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning - 
	PROPOSAL MANAGER Menu Window.  This class contains access to all the 
	pulldown menu command classes.  


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%




*H******************************************************/
#ifndef PROPMGRMENUWINDOW_HH
#define PROPMGRMENUWINDOW_HH


#include <string>
#include "MenuWindow.hh"
#include "PropMgrPropList.hh"

class Cmd;
class Label;
class PropMgrPopupMenu;

class PropMgrMenuWindow : public MenuWindow {

  public:

    PropMgrMenuWindow (const char *name); 
    ~PropMgrMenuWindow (); 

    // commands for the menubar items
    Cmd *retrieve_cmd () { return (app_retrieve) ; }
    Cmd *file_cmd ()     { return (app_file) ; }
    Cmd *print_cmd ()    { return (app_print) ; }
    Cmd *exit_cmd ()     { return (app_exit) ; }

    Cmd *layout_cmd ()   { return (app_layout) ; }
    Cmd *select_cmd ()   { return (app_select) ; }
    Cmd *deselect_cmd () { return (app_deselect) ; }
    Cmd *sort_cmd ()     { return (app_sort) ; }
    Cmd *sort_t_cmd ()   { return (app_sort_t) ; }
    Cmd *remove_cmd ()   { return (app_remove) ; }
    Cmd *refresh_cmd ()  { return (app_refresh) ; }
    Cmd *find_cmd ()     { return (app_find) ; }
    Cmd *findnext_cmd () { return (app_findnext) ; }

    Cmd *review_primary_cmd ()   { return (app_review_primary) ; }
    Cmd *review_secondary_cmd () { return (app_review_secondary) ; }
    Cmd *review_peer_cmd ()      { return (app_review_peer) ; }
    Cmd *review_final_cmd ()      { return (app_review_final) ; }

    Cmd *view_proposal_cmd ()    { return (app_view_proposal) ; }
    Cmd *view_cois_cmd ()        { return (app_view_cois) ; }
    Cmd *view_conflicts_cmd ()   { return (app_view_conflicts) ; }
    Cmd *view_panel_cmd ()       { return (app_view_panel) ; }
    Cmd *view_log_cmd ()         { return (app_view_log) ; }


    Cmd *stat_proposed_cmd()     { return (app_stat_proposed) ; }
    Cmd *stat_peer_cmd()         { return (app_stat_peer) ; }
    Cmd *stat_all_cmd()          { return (app_stat_all) ; }
    Cmd *stat_cal_cmd()          { return (app_stat_cal) ; }
    Cmd *stat_gto_cmd()          { return (app_stat_gto) ; }
    Cmd *stat_joint_cmd()        { return (app_stat_joint) ; }
    Cmd *stat_lpvlp_cmd()        { return (app_stat_lpvlp) ; }
    Cmd *stat_final_cmd()        { return (app_stat_final) ; }


    Cmd *help_cmd()          { return (app_help); }
    Cmd *helpg_cmd()         { return (app_helpg); }
    Cmd *helpo_cmd()         { return (app_helpo); }


    // information access methods

    int *GetCurrent(int *cnt) { return plist->GetSelectedItems(cnt); }
    string  GetCurrentProposalNumber();

    PropMgrPropList *GetPropList() { return plist; }

    // Find the string in any of the data areas
    Boolean Find(char *,Boolean ,Boolean);

    void Print(ostream &);

    void SetHeader(Boolean prop_hdr);

    static void PopupMenuCB( Widget w,XEvent *event);

    virtual const char *const className() {return ("PropMgrMenuWindow");}

  protected:

    virtual void createWorkArea(Widget);
    virtual void createMenuPanes();

    // pulldown menu commands
    Cmd *app_retrieve;
    Cmd *app_file;
    Cmd *app_print;
    Cmd *app_exit;

    Cmd *app_remove;
    Cmd *app_select;
    Cmd *app_deselect;
    Cmd *app_layout;
    Cmd *app_refresh;
    Cmd *app_sort;
    Cmd *app_sort_t;
    Cmd *app_find;
    Cmd *app_findnext;

    Cmd *app_view_conflicts;
    Cmd *app_view_panel;
    Cmd *app_view_log;
    Cmd *app_view_proposal;
    Cmd *app_view_cois;


    Cmd *app_review_primary;
    Cmd *app_review_secondary;
    Cmd *app_review_peer;
    Cmd *app_review_final;

    Cmd *app_stat_proposed;
    Cmd *app_stat_peer;
    Cmd *app_stat_all;
    Cmd *app_stat_cal;
    Cmd *app_stat_joint;
    Cmd *app_stat_lpvlp;
    Cmd *app_stat_final;
    Cmd *app_stat_gto;

    Cmd *app_target_checker;

    Cmd *app_help;
    Cmd *app_helpg;
    Cmd *app_helpo;


    PropMgrPopupMenu *popup;
    PropMgrPropList *plist;
    Label           *plbl;
    Label           *plbl2;

    int search_pos;       // used by Find, Find Next

};

extern PropMgrMenuWindow *thePropMgrWindow;


#endif
