
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager 
	find class.  The will search through all the data on the main 
	window panes, and highlight the matching entries.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef PANELFINDCMD_HH
#define PANELFINDCMD_HH


#include "FindCmd.hh"

class PanelFindCmd : public FindCmd {
    
  public:
    
    // Constructor 
    PanelFindCmd ( int active,Boolean type = FALSE);

    // Destructor 
    ~PanelFindCmd ( );

    // Perform search
    virtual void ok_cb(void *client_data);


    virtual const char *const className () { return "PanelFindCmd"; }


  protected:

    
    Boolean user_find;

};

#endif
