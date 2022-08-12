
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel manager to 
	display all the current panel members.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PANELALLMEMBERS_HH
#define PANELALLMEMBERS_HH


#include "GeneralDialogCmd.hh"
#include "PanelMemberList.hh"
#include "PropPrintFile.hh"

class Label;
class Pushbutton;

class PanelAllMembers : public GeneralDialogCmd {
    
  public:
    
    // Constructor 
    PanelAllMembers ( int , const char *name="All members...");

    // Destructor 
    ~PanelAllMembers ( );

    // Accessor methods


    static void yesCallback(void *);
    static void PrintCB(Widget,XtPointer ,XtPointer );


    virtual const char *const className () { return "PanelAllMembers"; }


  protected:
    

    // Called to create the specific fields
    virtual void CreateGeneralForm ();

    // Called to Update data window
    virtual void UpdateGeneralForm ();

    // Update the display 
    virtual void ok_cb (void *);
    virtual void AddActionButtons ();

 
    PanelMemberList *plist;
    Label *plbl;
    PushButton    *print_w;
    PropPrintFile *printcmd;


    
};
#endif
