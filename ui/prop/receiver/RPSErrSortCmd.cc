
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the RPS Error GUI Sort  class.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	1.2		11/22/96



*H******************************************************/

#include <stdlib.h>

//guiext
#include "ap_common.hh"
#include "GUIDefs.h"


#include "RPSErrSortCmd.hh"
#include "RPSErrMenuWindow.hh"
#include "RPSErrPropList.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
RPSErrSortCmd::RPSErrSortCmd ( char *name,int type, int active) : 
                     NoUndoCmd ( name, active )
{
  sort_type = type;
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
RPSErrSortCmd::~RPSErrSortCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void RPSErrSortCmd::doit()
{
  RPSErrPropList *obj;

  GUI_SetBusyCursor(theRPSErrWindow->baseWidget(),True);

  obj = theRPSErrWindow->GetPropList();
  obj->SortList(sort_type);

  GUI_SetBusyCursor(theRPSErrWindow->baseWidget(),False);
  return;	
}
