
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelLogCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager select


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"


#include "PanelLogCmd.hh"
#include "PanelMenuWindow.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelLogCmd::PanelLogCmd ( int active,const char *name) : 
                     NoUndoCmd ( (char *)name, active )
{
  log_flag = FALSE;

    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelLogCmd::~PanelLogCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelLogCmd::doit()
{

  if (log_flag) {
     log_flag = FALSE;
  } else {
     log_flag = TRUE;
  }

  return;	
}
