
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PropMgrViewChecks class.  This displays the linked proposals 
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
#include "PropMgrViewChecks.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrApp.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrViewChecks::PropMgrViewChecks ( const char *name,int active,string icmd)
        : PropViewResults (active,name)

{
  cmd = icmd;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrViewChecks::~PropMgrViewChecks()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PropMgrViewChecks::GetCommand()
{


  xconn = theApplication->GetDBConnection();
  ao = getenv(PROP_AO_ENV);
}


