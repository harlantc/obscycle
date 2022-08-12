
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel manager to 
	assign proposals to a panel.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PANELASSIGNPROPOSAL_HH
#define PANELASSIGNPROPOSAL_HH


#include "GeneralDialogCmd.hh"
#include "PanelProposalList.hh"

class Label;
class Pushbutton;

class PanelAssignProposal : public GeneralDialogCmd {
    
  public:
    
    // Constructor 
    PanelAssignProposal ( int , char *name=(char *)"Proposals...");

    // Destructor 
    ~PanelAssignProposal ( );

    // Accessor methods


    static void yesCallback(void *);


    virtual const char *const className () { return "PanelAssignProposal"; }


  protected:
    

    // Called to create the specific fields
    virtual void CreateGeneralForm ();

    // Called to Update data window
    virtual void UpdateGeneralForm ();

    // Update the display 
    virtual void ok_cb (void *);
 
    PanelProposalList *plist;
    Label *plbl;
    
};
#endif
