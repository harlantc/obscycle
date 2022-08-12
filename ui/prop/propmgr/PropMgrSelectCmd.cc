
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Manager select
	command.  This will select/deselect proposal and
	target items.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"


#include "PropMgrSelectCmd.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrSelectCmd::PropMgrSelectCmd ( char *name,Boolean type, int active) : 
                     NoUndoCmd ( name, active )
{
  select_opt = type;
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrSelectCmd::~PropMgrSelectCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrSelectCmd::doit()
{
  PropMgrPropList *obj;


  obj = thePropMgrWindow->GetPropList();

  if (select_opt)
    obj->SelectAll(FALSE);
  else
    obj->DeselectAll();

  return;	
}
