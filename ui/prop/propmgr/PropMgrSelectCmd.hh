
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Proposal Manager class
	which manages the selection of the proposal and/or
	target lists.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PROPMGRSELECTCMD_HH
#define PROPMGRSELECTCMD_HH


#include "NoUndoCmd.hh"

class PropMgrSelectCmd : public NoUndoCmd {
    
  protected:
    Boolean  select_opt;
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PropMgrSelectCmd ( char *,Boolean, int );

    // Destructor 
    ~PropMgrSelectCmd ( );


    virtual const char *const className () { return "PropMgrSelectCmd"; }
};
#endif
