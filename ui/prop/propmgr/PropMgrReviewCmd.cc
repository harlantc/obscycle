
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrReviewCmd.cc

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

#include "PropMgrReviewCmd.hh"
#include "PropMgrApp.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrReviewCmd::PropMgrReviewCmd ( const char *name, int rtype, 
	char mn,int active)
        : ViewMsgCmd ((char *)name,active,PROPMGR_REVIEWS_HTML)

{

  rev_type = rtype;
  SetMenuFields ( mn,NULL,NULL);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrReviewCmd::~PropMgrReviewCmd()
{
}
void PropMgrReviewCmd::SetReview(string pno, string pname)
{
   propno = pno; 
   panel_name = pname; 
}
  

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PropMgrReviewCmd::SetUp()
{
  ostrstream str;
  size_t cnt;
  char *ptr = NULL;

  thePropMgrWindow->GetPropList()->PrintReview(rev_type,str,propno.c_str(),
	panel_name.c_str());

  theToolTipManager->NewToolTip(
        "View the review form for the selected proposal.", st->baseWidget());

  // why do garbage characters get there?
  ptr = str.str();
  cnt= str.pcount();
  if (cnt < strlen(ptr))
    ptr[cnt] = '\0';

  SetMsg(ptr);
 
  if (ptr)
    delete ptr;
}
  
