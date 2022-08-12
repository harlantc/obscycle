/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal
        background application.

 
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
#include <unistd.h>
#include <stdio.h>

#include "PropReadRevParameter.hh"
#include "ProposalDefs.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
PropReadRevParameter::PropReadRevParameter() : ProposalParameter() 
{ 
  Initialize();
  is_override = FALSE;
}

PropReadRevParameter::PropReadRevParameter(const int argc,const char **argv)
	: ProposalParameter(argc,argv) 
{ 
  Initialize();
  is_override = FALSE;
}

// -----------------------------------------------------------
void PropReadRevParameter::Initialize()
{
}

// -----------------------------------------------------------
// Virtual function for Printing the command line argument
// format for the derived class
// -----------------------------------------------------------
void PropReadRevParameter::MyAdditionalFormat(string &str)
{

  str.append   (  "-in   <input directory>   OPTIONAL  - path for input review files\n");
  str.append   (  "                                      Default is ASCDS_PROP_REVIEW\n");
  str.append   (  "-override                 OPTIONAL  - automatically override existing reviews.\n");
  str.append   (  "                                      Default is interactive Y/N\n");
  str.append   (  "\n");

}

// -----------------------------------------------------------
// Virtual member function to process the derived parameters
// -----------------------------------------------------------
FW_Param_Error
PropReadRevParameter::ProcessAdditionalParameters(int &ii,
        const int argc,const char **argv)
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  if(strcmp(argv[ii],"-in") == 0) {
    ii++;
    if (argv[ii])
      dirname = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strncmp(argv[ii],"-over",5) == 0) {
    is_override = TRUE;
  }

  return error;
}

// -----------------------------------------------------------
// -----------------------------------------------------------
// validate required parameters
FW_Param_Error  
PropReadRevParameter::ValidateAdditionalParameters()
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  // check required parameters
  if (dirname.length() == 0) {
    dirname = (char *)getenv("ASCDS_PROP_REVIEW");
  }

  return error;
}


// -----------------------------------------------------------
// Print to the output stream
// -----------------------------------------------------------
void PropReadRevParameter::PrintAdditional(ostream &oss)
{
  oss << "\nDirectory        : " << dirname << endl;
  oss << "\nOverride Reviews : ";
  if (is_override) {
    oss << "yes" << endl;
  }
  else  {
    oss << "no" << endl;
  }
  oss << endl;
}


// -----------------------------------------------------------
PropReadRevParameter *theParameters = new PropReadRevParameter();
