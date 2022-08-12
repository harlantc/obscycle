
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrViewProposal.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  
        View Proposal class.  This displays the associated
	proposal data from the database.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef RPSERRVIEWPROPOSAL_HH
#define RPSERRVIEWPROPOSAL_HH


#include "ViewMsgCmd.hh"


class RPSErrViewProposal : public ViewMsgCmd {
    
  public:
    
    // Constructor 
    RPSErrViewProposal ( int active);


    // Destructor 
    ~RPSErrViewProposal ( );


    virtual const char *const className () { return "RPSErrViewProposal"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();

};

#endif
