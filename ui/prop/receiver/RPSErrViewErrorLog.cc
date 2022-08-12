
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrViewErrorLog.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

	This file contains the code for the Proposal Planning
        RPSErrViewErrorLog class.  This displays the log for the
        GUI error processing executable.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include "ap_common.hh"
#include <stdlib.h>
#include <string>

#include "ToolTip.hh"
#include "ScrollText.hh"

#include "ProposalDefs.hh"

#include "PropHelpCodes.h"
#include "RPSErrViewErrorLog.hh"
#include "RPSErrMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
RPSErrViewErrorLog::RPSErrViewErrorLog ( int active)
        : ViewFileCmd ((char *)"RPS GUI Log ...",active,True,RPSERR_VIEW_HTML,
	                False,0,NULL,False,False,TRUE,TRUE) 

{
  string  logname;

  SetMenuFields ( 'E',NULL,NULL);
  
  logname = getenv(RPS_LOG_ENV);
  logname.append("/");
  logname.append(getenv("LOGNAME"));
  logname.append("_");
  logname.append(RPS_ERRGUI_LOG_FILENAME);
  
  SetFilename((char *)logname.c_str());
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
RPSErrViewErrorLog::~RPSErrViewErrorLog()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void RPSErrViewErrorLog::SetUp()
{
  theToolTipManager->NewToolTip( "View log file for this application.",
	st->baseWidget()); 
  DisplayFile();
}
