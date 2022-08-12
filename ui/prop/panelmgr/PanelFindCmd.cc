
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelFindCmd.cc

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

#include <Xm/Xm.h>


// gui extension
#include "ap_common.hh"
#include "TextField.hh"
#include "MessageAreaList.hh"

#include "PanelFindCmd.hh"
#include "PanelAssignMember.hh"
#include "PanelMenuWindow.hh"
#include "PropHelpCodes.h"


// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelFindCmd::PanelFindCmd (  int    active, Boolean type)
        : FindCmd (active,this,0,NULL,True)

{
   user_find = type;
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelFindCmd::~PanelFindCmd()
{

}

// ------------------------------------------------------------
// Search through the tool objects looking for a match between
// search criteria and text display
// ------------------------------------------------------------
void PanelFindCmd::ok_cb(void *clientdata)
{
  char *search_string;
  Boolean check_case;
  PanelAssignMember *cc;

  // get search criteria
  search_string = Get();

  // is the search case sensitive?
  check_case = CaseSensitive();

  // search the text and list table
  if (user_find) {
    cc = (PanelAssignMember *)thePanelWindow->assign_panelist_cmd();
    found_flag = cc->Find(search_string,check_case,start_flag);
  }
  else
    found_flag = thePanelWindow->Find(search_string,check_case,start_flag);

}

