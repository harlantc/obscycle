
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the code for the RPS Error GUI Refresh
       command class. This class refreshes the list of proposal entries.

* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"
#include "RPSErrRefreshCmd.hh"
#include "RPSErrMenuWindow.hh"
#include "RPSErrPropList.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
RPSErrRefreshCmd::RPSErrRefreshCmd ( int active, const char *name) : 
                     NoUndoCmd ( name, active )
{
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
RPSErrRefreshCmd::~RPSErrRefreshCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void RPSErrRefreshCmd::doit()
{
  RPSErrPropList *obj;

  GUI_SetBusyCursor(theRPSErrWindow->baseWidget(),True);
  obj = theRPSErrWindow->GetPropList();
  obj->BuildList();

  theRPSErrWindow->SetListLabel();
  GUI_SetBusyCursor(theRPSErrWindow->baseWidget(),False);
  
  return;	
}
