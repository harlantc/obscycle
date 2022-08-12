
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for assigning selected proposals to
	a selected panel member as either a primary or secondary 
	reviewer.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I		%G%


*H******************************************************/

#include <stdlib.h>
#include "ap_common.hh"

//guiext
#include "GUIDefs.h"
#include "ErrorMessage.hh"

#include "PanelAssignReviewer.hh"
#include "PanelMenuWindow.hh"
#include "PanelApp.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelAssignReviewer::PanelAssignReviewer ( char *name,int type, 
		int active) : 
                NoUndoCmd ( name, active )
{
  rev_type = type;
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelAssignReviewer::~PanelAssignReviewer()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelAssignReviewer::doit()
{
  int pcnt;
  int *props;
  PersonEntry *reviewer;

  props =  thePanelWindow->GetSelectedProposals(&pcnt);
  reviewer =  thePanelWindow->GetCurrentMember();

  if (pcnt > 0 && reviewer) {
      thePanelWindow->AssignReviewer(rev_type,reviewer,props,pcnt);
  }
  else {
    if (!reviewer) 
      theErrorMessage->DisplayMessage(
	"One and only one panel member must be selected to assign as a reviewer.");
    if (pcnt <= 0)
      theErrorMessage->DisplayMessage(
	"At least one proposal must be selected to assign to a reviewer.");
  }

  return;	
}
