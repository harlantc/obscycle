
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel manager to 
	view CoIs for all proposals assigned to a panel.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PANELVIEWCOIS_HH
#define PANELVIEWCOIS_HH


#include "GeneralDialogCmd.hh"
#include "PropViewCoIs.hh"

class Label;
class Pushbutton;

class PanelViewCoIs : public PropViewCoIs {
    
  public:
    
    // Constructor 
    PanelViewCoIs ( int , const char *name="Co-Investigators...");

    // Destructor 
    ~PanelViewCoIs ( );

    // Accessor methods


    virtual const char *const className () { return "PanelViewCoIs"; }


  protected:
    
    // Called to create the specific fields
    virtual ProposalArray *GetProposals ();

};
#endif
