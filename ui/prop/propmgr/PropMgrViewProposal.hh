
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrViewProposal.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  Proposal Management
        View Proposal class.  This displays the associated
	proposal data from the database.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRVIEWPROPOSAL_HH
#define PROPMGRVIEWPROPOSAL_HH


#include "ViewMsgCmd.hh"



class PropMgrViewProposal : public ViewMsgCmd {
    
  public:
    
    // Constructor 
    PropMgrViewProposal ( int active);


    // Destructor 
    ~PropMgrViewProposal ( );


    virtual const char *const className () { return "PropMgrViewProposal"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();

};

#endif
