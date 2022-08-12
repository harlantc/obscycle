
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PanelViewDetectors class.  This displays the linked proposals 
        in the database for the current cycle.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include "ap_common.hh"

#include <string>

#include "XConnection.hh"

#include "ProposalDefs.hh"
#include "PanelViewDetectors.hh"
#include "PanelMenuWindow.hh"
#include "PanelApp.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelViewDetectors::PanelViewDetectors ( const char *name,int active,string icmd)
        : PropViewResults (active,name)

{
  cmd = icmd;
  orig_cmd = icmd;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelViewDetectors::~PanelViewDetectors()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelViewDetectors::GetCommand()
{
  PanelEntry *pe;


  xconn = theApplication->GetDBConnection();
  ao = getenv(PROP_AO_ENV);
  cmd = orig_cmd;
  pe= thePanelWindow->GetCurrentPanel(); 
  if (pe) {
    cmd +=  " -p ";
    cmd += pe->GetPanelName();
  }

  

}


