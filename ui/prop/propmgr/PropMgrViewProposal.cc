
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Management
        View Proposal class.  This displays the associated data
        for the specified proposal number.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

#include "ap_common.hh"
#include "ViewMsgCmd.hh"
#include "ScrollText.hh"
#include "ErrorMessage.hh"
#include "ToolTip.hh"

#include "ProposalDefs.hh"
#include "ProposalEntry.hh"

#include "PropHelpCodes.h"

#include "PropMgrViewProposal.hh"
#include "PropMgrApp.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrViewProposal::PropMgrViewProposal ( int active)
        : ViewMsgCmd ((char *)"View Proposal ...",active,PANEL_VIEW_PROP_HTML)

{

  SetMenuFields ( 'P',NULL,NULL);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrViewProposal::~PropMgrViewProposal()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PropMgrViewProposal::SetUp()
{
  string   propno;
  strstream str;
  char *ptr = NULL;

  theToolTipManager->NewToolTip(
        (char *)"View proposal data for selected proposal.", st->baseWidget());

  propno = thePropMgrWindow->GetCurrentProposalNumber();
  if (propno.length() > 0) {
    thePropMgrWindow->GetPropList()->PrintProposal(str,propno.c_str());
    str << ends;
    str.flush();
    
    ptr = str.str();
    SetMsg(ptr);
  
    if (ptr)
      delete ptr;
  }
  else {
    gc_error_flag = TRUE;
    theErrorMessage->DisplayMessage("Please select a proposal and try again.");
  }
  
}
