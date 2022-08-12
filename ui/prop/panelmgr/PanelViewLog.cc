
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrViewLog.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

	This file contains the code for the Proposal Planning
        RPSErrViewLog class.  This displays the receiver log
        produced by the prop_rps_receiver executable.


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


#include "PanelViewLog.hh"
#include "PanelApp.hh"
#include "PanelMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelViewLog::PanelViewLog ( int active,const char *name,const char *iname)
        : ViewFileCmd ((char *)name,active,True,-1,
                       False,0,NULL,False,False,TRUE,TRUE)


{
  if (iname) 
    _fname = iname;
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelViewLog::~PanelViewLog()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelViewLog::SetUp()
{

  if (_fname.size() <= 0) {
    _fname = theApplication->GetLogfileName();
    theToolTipManager->NewToolTip( 
	"View Panel Manager log file for current user.",
	st->baseWidget());
  }


  SetFilename((char *)_fname.c_str());

  DisplayFile();
}
