
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPropMgrCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the command which executes 
	the proposal manager GUI (prop_manager_gui).

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PANELPROPMGRCMD_HH
#define PANELPROPMGRCMD_HH


#include "ExecuteCmd.hh"


class PanelPropMgrCmd : public ExecuteCmd {
    
  protected:

    virtual void doit();

    virtual Boolean Setup();

    // initialize parameters to null
    void Initialize();
    
  public:
    
    // Constructor 
    PanelPropMgrCmd ( int );

    // Destructor 
    ~PanelPropMgrCmd ( );


    virtual const char *const className () { return "PanelPropMgrCmd"; }
};
#endif
