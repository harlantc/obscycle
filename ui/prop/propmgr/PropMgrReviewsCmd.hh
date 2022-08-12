
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the proposal manager reviews
       command class. This class manages display of review(s) for
       the selected proposal.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef PROPMGRREVIEWSCMD_HH
#define PROPMGRREVIEWSCMD_HH

#include <vector>
#include "NoUndoCmd.hh"
#include "PropMgrReviewCmd.hh"
#include "PropMgrPeerCmd.hh"

class PropMgrReviewsCmd : public NoUndoCmd {
    
  protected:

    vector<PropMgrReviewCmd *> revcmds;
    vector<PropMgrPeerCmd *> peercmds;
    
    virtual void doit();
    int rev_type;
    char mn;
    
  public:
    
    // Constructor 
    PropMgrReviewsCmd (const char *name, int type, char imn,int active=TRUE);

    // Destructor 
    ~PropMgrReviewsCmd ( );

    virtual const char *const className () { return "PropMgrReviewsCmd"; }
};
#endif
