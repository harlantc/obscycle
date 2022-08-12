
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the code for the Proposal manager GUI Refresh
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
#include "PropMgrRefreshCmd.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrRefreshCmd::PropMgrRefreshCmd ( int active, const char *name) : 
                     NoUndoCmd ( name, active )
{
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrRefreshCmd::~PropMgrRefreshCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrRefreshCmd::doit()
{
  PropMgrPropList *obj;

  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),True);
  obj = thePropMgrWindow->GetPropList();
  obj->Refresh();

  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),False);
  
  return;	
}
