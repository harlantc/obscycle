
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PanelViewChecks class.  This displays the linked proposals 
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
#include "PanelViewChecks.hh"
#include "PanelMenuWindow.hh"
#include "PanelApp.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelViewChecks::PanelViewChecks ( const char *name,int active,string icmd,
	Boolean findBtn)
        : PropViewResults (active,name,findBtn)

{
  cmd = icmd;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelViewChecks::~PanelViewChecks()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelViewChecks::GetCommand()
{


  xconn = theApplication->GetDBConnection();
  ao = getenv(PROP_AO_ENV);
}


