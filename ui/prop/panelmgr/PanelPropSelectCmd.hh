
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPropSelectCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the Panel Manager class
	which manages the selection of the proposal list.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PANELPROPSELECTCMD_HH
#define PANELPROPSELECTCMD_HH


#include "NoUndoCmd.hh"

class PanelPropSelectCmd : public NoUndoCmd {
    
  protected:
    Boolean  select_opt;
    
    virtual void doit();
    
  public:
    
    // Constructor 
    PanelPropSelectCmd ( char *,Boolean, int );

    // Destructor 
    ~PanelPropSelectCmd ( );


    virtual const char *const className () { return "PanelPropSelectCmd"; }
};
#endif
