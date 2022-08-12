
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrFindCmd.cc

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

#include <Xm/Xm.h>


// gui extension
#include "ap_common.hh"
#include "TextField.hh"
#include "MessageAreaList.hh"

#include "PropMgrFindCmd.hh"
#include "PropMgrMenuWindow.hh"
#include "PropHelpCodes.h"


// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrFindCmd::PropMgrFindCmd (  
        int    active)
        : FindCmd (active,this,0,NULL,True)

{
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrFindCmd::~PropMgrFindCmd()
{

}

// ------------------------------------------------------------
// Search through the tool objects looking for a match between
// search criteria and text display
// ------------------------------------------------------------
void PropMgrFindCmd::ok_cb(void *clientdata)
{
  char *search_string;
  Boolean check_case;

  // get search criteria
  search_string = Get();

  // is the search case sensitive?
  check_case = CaseSensitive();

  // search the text and list table
  found_flag = thePropMgrWindow->Find(search_string,check_case,start_flag);

}

