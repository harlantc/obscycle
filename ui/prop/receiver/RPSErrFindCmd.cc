
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrFindCmd.cc

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

#include <Xm/Xm.h>

#include "ap_common.hh"
#include "TextField.hh"
#include "MessageAreaList.hh"

#include "RPSErrFindCmd.hh"
#include "RPSErrMenuWindow.hh"
#include "PropHelpCodes.h"


// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
RPSErrFindCmd::RPSErrFindCmd (  
        int    active)
        : FindCmd (active,this,RPSERR_FIND_HTML,NULL,True)

{
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
RPSErrFindCmd::~RPSErrFindCmd()
{

}

// ------------------------------------------------------------
// Search through the tool objects looking for a match between
// search criteria and text display
// ------------------------------------------------------------
void RPSErrFindCmd::ok_cb(void *clientdata)
{
  char *search_string;
  Boolean check_case;

  // get search criteria
  search_string = Get();

  // is the search case sensitive?
  check_case = CaseSensitive();

  // search the text and list table
  found_flag = theRPSErrWindow->Find(search_string,check_case,start_flag);

}

