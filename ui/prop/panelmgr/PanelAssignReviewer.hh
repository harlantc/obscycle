
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the assigning of proposals to
	a primary/secondary reviewer command.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PANELASSIGNREVIEWER_HH
#define PANELASSIGNREVIEWER_HH


#include "NoUndoCmd.hh"
#include "ProposalDefs.hh"

class PanelAssignReviewer : public NoUndoCmd {
    
  protected:
    
    int rev_type;
    virtual void doit();
    
  public:
    

    // Constructor 
    PanelAssignReviewer ( char *,int , int );

    // Destructor 
    ~PanelAssignReviewer ( );

    virtual const char *const className () { return "PanelAssignReviewer"; }
};
#endif
