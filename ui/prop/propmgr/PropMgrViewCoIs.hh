
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
#ifndef PROPMGRVIEWCOIS_HH
#define PROPMGRVIEWCOIS_HH


#include "GeneralDialogCmd.hh"
#include "PropViewCoIs.hh"


class PropMgrViewCoIs : public PropViewCoIs {
    
  public:
    
    // Constructor 
    PropMgrViewCoIs ( int , const char *name="Co-Investigators...");

    // Destructor 
    ~PropMgrViewCoIs ( );

    // Accessor methods


    virtual const char *const className () { return "PropMgrViewCoIs"; }


  protected:
    

    // Called to create the specific fields
    virtual ProposalArray *GetProposals ();

};
#endif
