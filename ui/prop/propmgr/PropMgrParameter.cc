/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal
        Manager GUI application.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#include <strings.h>
#include <stdlib.h>
#include <fstream>

#include "PropMgrParameter.hh"
#include "ProposalDefs.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
PropMgrParameter::PropMgrParameter() : ProposalParameter(TRUE) 
{ 
}

PropMgrParameter::PropMgrParameter(const int argc,const char **argv)
	: ProposalParameter(argc,argv,TRUE) 
{ 
}

// -----------------------------------------------------------
PropMgrParameter *theParameters = new PropMgrParameter();
