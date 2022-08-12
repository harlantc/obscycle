
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        RPSErrViewProposal class.  This displays the associated
        name/value pair file from the RPS archive directory area
        for the specified proposal number.


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
#include "RPSErrViewChecks.hh"
#include "RPSErrMenuWindow.hh"
#include "RPSErrApp.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
RPSErrViewChecks::RPSErrViewChecks ( const char *name,int active,
	string icmd,Boolean pflag)
        : PropViewResults (active,name)

{
  cmd = icmd;
  param_flag = pflag;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
RPSErrViewChecks::~RPSErrViewChecks()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void RPSErrViewChecks::GetCommand()
{


  if (param_flag) {
    xconn = theApplication->GetDBConnection();
    ao = getenv(PROP_AO_ENV);
  }
}


