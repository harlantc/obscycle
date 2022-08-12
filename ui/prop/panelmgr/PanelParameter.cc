/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal
        Panel Manager GUI application.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#include <strings.h>
#include <stdlib.h>

#include "PanelParameter.hh"
#include "ProposalDefs.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
PanelParameter::PanelParameter() : ProposalParameter(TRUE) 
{ 
}

PanelParameter::PanelParameter(const int argc,const char **argv)
	: ProposalParameter(argc,argv,TRUE) 
{ 
}


// -----------------------------------------------------------
PanelParameter *theParameters = new PanelParameter();
