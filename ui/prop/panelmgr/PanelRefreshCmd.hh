
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelRefreshCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Panel Manager GUI Refresh
       command class. This class refreshes the list of proposal entries.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PANELREFRESHCMD_HH
#define PANELREFRESHCMD_HH


#include "NoUndoCmd.hh"

class PanelRefreshCmd : public NoUndoCmd {
    
  protected:
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PanelRefreshCmd ( int active,char *name=(char *)"Refresh Current Proposals");

    // Destructor 
    ~PanelRefreshCmd ( );

    virtual const char *const className () { return "PanelRefreshCmd"; }
};
#endif
