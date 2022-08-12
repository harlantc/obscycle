
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelLogCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Panel Manager class

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PANELLOGCMD_HH
#define PANELLOGCMD_HH


#include "NoUndoCmd.hh"

class PanelLogCmd : public NoUndoCmd {
    
  protected:
    Boolean  log_flag;
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PanelLogCmd ( int,const char *);

    // Destructor 
    ~PanelLogCmd ( );

    Boolean IsActive() { return log_flag; }


    virtual const char *const className () { return "PanelLogCmd"; }
};
#endif
