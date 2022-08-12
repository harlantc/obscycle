
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelViewProposal.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  Panel Management
        View Proposal class.  This displays the associated
	proposal data from the database.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELVIEWPROPOSAL_HH
#define PANELVIEWPROPOSAL_HH


#include "ViewMsgCmd.hh"


class PanelViewProposal : public ViewMsgCmd {
    
  public:
    
    // Constructor 
    PanelViewProposal ( int active);


    // Destructor 
    ~PanelViewProposal ( );


    virtual const char *const className () { return "PanelViewProposal"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();

};

#endif
