
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelClumpCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Panel Manager GUI 

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PANELCLUMPCMD_HH
#define PANELCLUMPCMD_HH


#include "NoUndoCmd.hh"

class PanelClumpCmd : public NoUndoCmd {
    
  protected:
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PanelClumpCmd ( int active,const char *name="Select Clumped Proposals");

    // Destructor 
    ~PanelClumpCmd ( );

    virtual const char *const className () { return "PanelClumpCmd"; }
};
#endif
