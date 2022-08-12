
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrRemoveCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Manager GUI 
	remove proposal/target item from list.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PROPMGRREMOVECMD_HH
#define PROPMGRREMOVECMD_HH


#include "AskFirstCmd.hh"

class PropMgrRemoveCmd : public AskFirstCmd {
    
  protected:
    

    virtual void doit();
    virtual void undoit() {return;}

    
  public:
    
    // Constructor 
    PropMgrRemoveCmd ( char *, int );

    // Destructor 
    ~PropMgrRemoveCmd ( );

    virtual void updateQuestion();

    virtual const char *const className () { return "PropMgrRemoveCmd"; }
};
#endif
