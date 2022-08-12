
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	error processing find class.  The will search through all
	the data on the main window panes, and highlight the
	matching entries.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef RPSERRFINDCMD_HH
#define RPSERRFINDCMD_HH


#include "FindCmd.hh"

class RPSErrFindCmd : public FindCmd {
    
  public:
    
    // Constructor 
    RPSErrFindCmd ( int active);

    // Destructor 
    ~RPSErrFindCmd ( );

    // Perform search
    virtual void ok_cb(void *client_data);


    virtual const char *const className () { return "RPSErrFindCmd"; }


  protected:

    

};

#endif
