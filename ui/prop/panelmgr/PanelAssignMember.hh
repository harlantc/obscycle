
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Panel manager to 
	assign users to a panel.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PANELASSIGNMEMBER_HH
#define PANELASSIGNMEMBER_HH


#include "GeneralDialogCmd.hh"
#include "PanelUserList.hh"
#include "PushButtonInterface.hh"
#include "PropPrintFile.hh"



class PanelFindCmd;
class Label;
class Pushbutton;

class PanelAssignMember : public GeneralDialogCmd {
    
  public:
    
    // Constructor 
    PanelAssignMember ( int , char *name=(char *)"Panelists...");

    // Destructor 
    ~PanelAssignMember ( );

    // Accessor methods


    static void yesCallback(void *);

    static void AssignChairCB(Widget,XtPointer,XtPointer);
    static void AssignDeputyChairCB(Widget,XtPointer,XtPointer);
    static void AssignMemberCB(Widget,XtPointer,XtPointer);
    static void RefreshCB(Widget,XtPointer,XtPointer);
    static void PrintCB(Widget,XtPointer,XtPointer);

    // find string in scrolled text area
    Boolean Find(char *str,Boolean case_flag,Boolean start_flag);


    virtual const char *const className () { return "PanelAssignMember"; }



  protected:
    

    // Called to create the specific fields
    virtual void CreateGeneralForm ();
    virtual void AddActionButtons ();

    // Called to Update data window
    virtual void UpdateGeneralForm ();

    // Update the display 
    virtual void ok_cb (void *);
 
    string         type_update;
    PanelUserList *ulist;
    int            ucnt;
    Label         *ulbl;
    PushButton    *chair_w;
    PushButton    *member_w;
    PushButton    *vicechair_w;
    PushButton    *refresh_w;
    PushButton    *print_w;
    PushButtonInterface    *find_w;
    int            search_pos;

    PanelFindCmd *find_cmd;
    PropPrintFile *printcmd;

    
};
#endif
