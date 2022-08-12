
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrMenuWindow.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning - 
	RPS Error Menu Window.  This class contains access to all the 
	pulldown menu command classes.  It also instantiates the 
	scrolled text window for displaying the current proposal file
	and a scrolled list class for displaying existing proposals.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%




*H******************************************************/
#ifndef RPSERRMENUWINDOW_HH
#define RPSERRMENUWINDOW_HH


#include <string>
#include <vector>

#include "ap_common.hh"
#include "MenuWindow.hh"
#include "Logger.hh"

#define NUM_LABELS   4
#define ERROR_LABEL  0   // error label is 1st in array
#define RPS_LABEL    1   // rps file label is 2nd
#define PROP_LABEL   2   // proposal labels are last


class Cmd;
class Label;
class ScrollText;
class RPSErrPropList;
class RPSErrPopupMenu;

class RPSErrMenuWindow : public MenuWindow {

  public:

    RPSErrMenuWindow (const char *name); 
    ~RPSErrMenuWindow (); 

    // commands for the menubar items
    Cmd *open_cmd ()         { return (app_open) ; }
    Cmd *retrieve_cmd ()     { return (app_retrieve) ; }
    Cmd *print_cmd ()        { return (app_print) ; }
    Cmd *deleteprop_cmd ()   { return (app_deleteprop) ; }
    Cmd *exit_cmd ()         { return (app_exit) ; }


    Cmd *sort_propno_cmd ()  { return (app_sort_propno) ; }
    Cmd *sort_reverse4_cmd (){ return (app_sort_reverse4) ; }
    Cmd *sort_last4_cmd ()   { return (app_sort_last4) ; }
    Cmd *sort_pi_cmd ()      { return (app_sort_pi) ; }
    Cmd *sort_title_cmd ()   { return (app_sort_title) ; }
    Cmd *sort_type_cmd ()    { return (app_sort_type) ; }
    Cmd *refresh_cmd ()      { return (app_refresh) ; }
    Cmd *find_cmd ()         { return (app_find) ; }
    Cmd *findnext_cmd ()     { return (app_findnext) ; }

    Cmd *view_proposal_cmd ()   { return (app_view_proposal) ; }
    Cmd *view_log_cmd ()        { return (app_view_log) ; }
    Cmd *view_error_log_cmd ()  { return (app_view_error_log) ; }
    Cmd *view_deleted_cmd ()         { return (app_view_deleted) ; }

    Cmd *checks_cmd ()       { return (app_checks) ; }
    Cmd *upload_cmd ()       { return (app_upload) ; }
    Cmd *recvstats_cmd ()    { return (app_recvstats) ; }
    Cmd *allstats_cmd ()     { return (app_allstats) ; }
    Cmd *gtostats_cmd ()     { return (app_gtostats) ; }
    Cmd *jointstats_cmd ()   { return (app_jointstats) ; }
    Cmd *lpstats_cmd ()      { return (app_lpstats) ; }
    Cmd *lists_cmd ()        { return (app_lists) ; }

    Cmd *help_cmd()          { return (app_help); }
    Cmd *helpg_cmd()         { return (app_helpg); }
    Cmd *helpo_cmd()         { return (app_helpo); }


    // get current data for selected proposal in proposal list
    string  GetCurrentProposalNumber();
    vector<string>  GetCurrentProposalNumbers();
    string  GetCurrentItem();

    // clear proposal,error files
    void    ClearCurrentFiles();
   

    // Find the string in any of the data areas
    Boolean Find(char *,Boolean ,Boolean);


    // set label of proposal list
    void    SetListLabel();

    RPSErrPropList *GetPropList() { return prop_w;}

    // callback for the popup menu action initiated by right most mouse button
    static void PopupMenuCB(Widget,XEvent*);


    virtual const char *const className() {return ("RPSErrMenuWindow");}

  protected:

    virtual void createWorkArea(Widget);
    virtual void createMenuPanes();

    // pulldown menu commands
    Cmd *app_open;
    Cmd *app_retrieve;
    Cmd *app_print;
    Cmd *app_deleteprop;
    Cmd *app_exit;


    Cmd *app_sort_propno;
    Cmd *app_sort_last4;
    Cmd *app_sort_reverse4;
    Cmd *app_sort_pi;
    Cmd *app_sort_title;
    Cmd *app_sort_type;
    Cmd *app_find;
    Cmd *app_findnext;
    Cmd *app_refresh;

    Cmd *app_view_proposal;
    Cmd *app_view_log;
    Cmd *app_view_error_log;
    Cmd *app_view_deleted;

    Cmd *app_checks;
    Cmd *app_upload;
    Cmd *app_recvstats;
    Cmd *app_allstats;
    Cmd *app_gtostats;
    Cmd *app_jointstats;
    Cmd *app_lpstats;
    Cmd *app_lists;

    Cmd *app_help;
    Cmd *app_helpg;
    Cmd *app_helpo;


    RPSErrPopupMenu *popup;

  private:

    RPSErrPropList *prop_w;
    ScrollText     *err_w;
    ScrollText     *rps_w;
    Label          *label_w[NUM_LABELS];
    int            search_pos;

};

extern RPSErrMenuWindow *theRPSErrWindow;


#endif
