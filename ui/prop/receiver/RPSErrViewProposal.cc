/*H****************************************************************************
* Copyright (c) 2019, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the 
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

#include "RPSErrViewProposal.hh"
#include "RPSErrApp.hh"
#include "RPSErrMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
RPSErrViewProposal::RPSErrViewProposal ( int active)
        : ViewMsgCmd ((char *)"Selected Proposal ...",active,RPSERR_VIEW_HTML)

{

  SetMenuFields ( 'P',NULL,NULL);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
RPSErrViewProposal::~RPSErrViewProposal()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void RPSErrViewProposal::SetUp()
{
  string  propno;
  ostrstream str;
  char *ptr = NULL;

  propno = theRPSErrWindow->GetCurrentProposalNumber();

  theToolTipManager->NewToolTip(
	(char *)"View selected proposal information.",
	st->baseWidget());
  if (propno.length() > 0) {
    ProposalEntry pe(theApplication->GetDBConnection());
    pe.Retrieve((char *)propno.c_str(),FALSE);
    pe.Print(str);
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
