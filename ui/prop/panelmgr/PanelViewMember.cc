
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Management
        View Member class.  This displays the associated data
        for the specified panel member.


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
#include "PropHelpCodes.h"

#include "PanelViewMember.hh"
#include "PanelApp.hh"
#include "PanelMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelViewMember::PanelViewMember ( int active)
        : ViewMsgCmd ((char *)"Selected Panelist ...",active,PANEL_VIEW_MEMBER_HTML)

{

  SetMenuFields ( 'M',NULL,NULL);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelViewMember::~PanelViewMember()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelViewMember::SetUp()
{
  strstream str;
  char *ptr = NULL;
  PersonEntry *pe;

  theToolTipManager->NewToolTip(
	(char *)"View additional information for the selected member",
	st->baseWidget());

  pe = thePanelWindow->GetCurrentMember();

  if (pe) {
    pe->Print(str);
    str << ends;
    str.flush();
    
    ptr = str.str();
    SetMsg(ptr);
  
    if (ptr)
      delete ptr;
  }
  else  {
    theErrorMessage->DisplayMessage("One and only one member should be selected to view user information.");
    gc_error_flag = TRUE;
  }
   
  
}
