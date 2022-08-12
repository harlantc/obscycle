
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPropSelectCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager select
	command.  This will select/deselect proposal items.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"


#include "PanelPropSelectCmd.hh"
#include "PanelMenuWindow.hh"
#include "PanelPropList.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelPropSelectCmd::PanelPropSelectCmd ( char *name,Boolean type, int active) : 
                     NoUndoCmd ( name, active )
{
  select_opt = type;
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelPropSelectCmd::~PanelPropSelectCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelPropSelectCmd::doit()
{
  PanelPropList *obj;


  obj = thePanelWindow->GetPropList();

  if (select_opt)
    obj->SelectAll(FALSE);
  else
    obj->DeselectAll();

  return;	
}
