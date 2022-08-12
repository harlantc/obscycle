
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the proposal manager refresh
       command class. This class refreshes the list of proposal entries.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PROPMGRREFRESHCMD_HH
#define PROPMGRREFRESHCMD_HH


#include "NoUndoCmd.hh"

class PropMgrRefreshCmd : public NoUndoCmd {
    
  protected:
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PropMgrRefreshCmd ( int active,const char *name="Refresh Proposal List");

    // Destructor 
    ~PropMgrRefreshCmd ( );

    virtual const char *const className () { return "PropMgrRefreshCmd"; }
};
#endif
