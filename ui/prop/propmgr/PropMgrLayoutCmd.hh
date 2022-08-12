
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Proposal Manager class
	which manages the alternate layout of the proposal and/or
	target lists.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PROPMGRLAYOUTCMD_HH
#define PROPMGRLAYOUTCMD_HH


#include "NoUndoCmd.hh"

class PropMgrLayoutCmd : public NoUndoCmd {
    
  protected:
    Boolean  layout_prop;
    Boolean  firsttime;
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PropMgrLayoutCmd ( char *,Boolean, int );

    // Destructor 
    ~PropMgrLayoutCmd ( );

    Boolean GetLayout() { return layout_prop; }

    void IsNewRetrieve() { firsttime = TRUE; }

    static void yesCallback(void *); 

    virtual const char *const className () { return "PropMgrLayoutCmd"; }
};
#endif
