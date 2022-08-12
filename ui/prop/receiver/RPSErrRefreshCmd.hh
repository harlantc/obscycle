
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the RPS Error GUI Refresh
       command class. This class refreshes the list of proposal entries.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef RPSERRREFRESHCMD_HH
#define RPSERRREFRESHCMD_HH


#include "NoUndoCmd.hh"

class RPSErrRefreshCmd : public NoUndoCmd {
    
  protected:
    
    virtual void doit();
    
  public:
    
    // Constructor 
    RPSErrRefreshCmd ( int active,const char *name="Refresh Proposal List");

    // Destructor 
    ~RPSErrRefreshCmd ( );

    virtual const char *const className () { return "RPSErrRefreshCmd"; }
};
#endif
