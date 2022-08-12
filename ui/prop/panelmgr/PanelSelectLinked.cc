
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Management
	class to display "clumps" from the propconflict list for
	a selected proposal.  This is dependent on files created
	outside the datasystems software.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include "ap_common.hh"
#include <stdlib.h>
#include <vector>
#include <string>

#include "NoUndoCmd.hh"
#include "ScrollText.hh"
#include "ErrorMessage.hh"

#include "ProposalArray.hh"
#include "ProposalEntry.hh"
#include "PanelSelectLinked.hh"
#include "PanelMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelSelectLinked::PanelSelectLinked ( int active,const char *name)
        : NoUndoCmd((char *)name,active)

{

  SetMenuFields ( 'C',NULL,NULL);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelSelectLinked::~PanelSelectLinked()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelSelectLinked::doit()
{

  thePanelWindow->GetPropList()->SelectLinked();
  
}
