
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelSelectLinked.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Panel Manager GUI 

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PANELSELECTLINKED_HH
#define PANELSELECTLINKED_HH


#include "NoUndoCmd.hh"

class PanelSelectLinked : public NoUndoCmd {
    
  protected:
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PanelSelectLinked ( int active,const char *name="Select Linked Proposals");

    // Destructor 
    ~PanelSelectLinked ( );

    virtual const char *const className () { return "PanelSelectLinked"; }
};
#endif
