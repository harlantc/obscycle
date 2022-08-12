
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
#include "PanelClumpCmd.hh"
#include "PanelMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelClumpCmd::PanelClumpCmd ( int active,const char *name)
        : NoUndoCmd((char *)name,active)

{

  SetMenuFields ( 'C',NULL,NULL);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelClumpCmd::~PanelClumpCmd()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelClumpCmd::doit()
{
  int            pno;
  string         errmsg;
  string         propno;
  ProposalArray *pa = NULL;
  ProposalEntry *pe = NULL;
  vector<string> clump_list;

  propno = thePanelWindow->GetCurrentProposalNumber();
  pno = atoi(propno.c_str());
  if (pno > 0) {
    pa = thePanelWindow->GetPropList()->GetProposals();
    if (pa)
      pe = pa->GetRecordbyProposal(pno);
  
    if (pe) {
      if ( pe->GetClumps(clump_list,errmsg)) {
         thePanelWindow->GetPropList()->SelectProposals(clump_list);
      }
      else  {
        theErrorMessage->DisplayMessage(errmsg.c_str());
      }
    }
    else {
      theErrorMessage->DisplayMessage("Please select one and only one  proposal and try again.");
    }
  } 
  else {
    theErrorMessage->DisplayMessage("Please select a proposal and try again.");
  } 
  
}
