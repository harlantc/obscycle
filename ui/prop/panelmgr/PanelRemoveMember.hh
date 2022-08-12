
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelRemoveMember.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager GUI 

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PANELREMOVEMEMBER_HH
#define PANELREMOVEMEMBER_HH


#include "AskFirstCmd.hh"

class PanelRemoveMember : public AskFirstCmd {
    
  protected:
    

    virtual void doit();
    virtual void undoit() {return;}

    
  public:
    
    // Constructor 
    PanelRemoveMember ( char *, int );

    // Destructor 
    ~PanelRemoveMember ( );

    virtual void updateQuestion();

    virtual const char *const className () { return "PanelRemoveMember"; }
};
#endif
