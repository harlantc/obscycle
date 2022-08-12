
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelRemoveProposal.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager GUI 
	to remove a proposal from the current panel

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PANELREMOVEPROPOSAL_HH
#define PANELREMOVEPROPOSAL_HH


#include "AskFirstCmd.hh"

class PanelRemoveProposal : public AskFirstCmd {
    
  protected:
    

    virtual void doit();
    virtual void undoit() {return;}

    
  public:
    
    // Constructor 
    PanelRemoveProposal ( char *, int );

    // Destructor 
    ~PanelRemoveProposal ( );

    virtual void updateQuestion();

    virtual const char *const className () { return "PanelRemoveProposal"; }
};
#endif
