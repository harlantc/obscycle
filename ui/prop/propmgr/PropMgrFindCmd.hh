
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Manager 
	find class.  The will search through all the data on the main 
	window panes, and highlight the matching entries.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef PROPMGRFINDCMD_HH
#define PROPMGRFINDCMD_HH


#include "FindCmd.hh"

class PropMgrFindCmd : public FindCmd {
    
  public:
    
    // Constructor 
    PropMgrFindCmd ( int active);

    // Destructor 
    ~PropMgrFindCmd ( );

    // Perform search
    virtual void ok_cb(void *client_data);


    virtual const char *const className () { return "PropMgrFindCmd"; }


  protected:

    

};

#endif
