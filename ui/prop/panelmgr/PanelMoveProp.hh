
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelMoveProp.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager GUI Move 
	proposal class.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PANELMOVEPROP_HH
#define PANELMOVEPROP_HH

#include <string>
#include "NoUndoCmd.hh"

class PanelMoveProp : public NoUndoCmd {
    
  protected:
    

    string  panel_id;
    virtual void doit();
    
  public:
    
    // Constructor 
    PanelMoveProp ( char *, char *, int );

    // Destructor 
    ~PanelMoveProp ( );

    static void yes_cb(void *);
    static void no_cb(void *);

    virtual const char *const className () { return "PanelMoveProp"; }
};
#endif
