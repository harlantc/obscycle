/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSReceiverParameter.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The RPSReceiverParameter class provides the application with the
        set of specific parameters needed to execute the Proposal
        application RPS Receiver function.

 
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

#include "RPSReceiverParameter.hh"
#include "ProposalDefs.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
RPSReceiverParameter::RPSReceiverParameter() : FW_Parameter() 
{ 
  Initialize();
}

RPSReceiverParameter::RPSReceiverParameter(const int argc,const char **argv)
	: FW_Parameter(argc,argv) 
{ 
  Initialize();
}

void RPSReceiverParameter::Initialize()
{
  printit = FALSE;
  rps_too = FALSE;
  duration = 60;  // check the mail every 1 minute
}
// -----------------------------------------------------------
// Virtual function for Printing the command line argument 
// format for the derived class
// -----------------------------------------------------------
Strings RPSReceiverParameter::MyFormat()
{
  Strings myformat;

  myformat   =   (  "-d [duration]   OPTIONAL  - sleep duration for incorporating mail \n"); 
  myformat.append(  "-too            OPTIONAL  - used if this is receiving out of cycle TOO proposals \n"); 
  myformat.append("\nNOTE:  Output files are written to the official Proposal RPS Receiver \n       directories defined by the environment variables:\n");
  myformat.append("\n   ASCDS_PROP_RPS_RECV  - all submitted proposals that passed the\n\
                          initial check of valid keywords found. ");
  myformat.append("\n   ASCDS_PROP_TOO_RECV  - all submitted TOO proposals that passed the\n\
                          initial check of valid keywords found. ");
  myformat.append("\n   ASCDS_PROP_RPS_ERR   - all submitted proposals that require human\n\
                          intervention.  ");
  myformat.append("\n   ASCDS_PROP_RPS_LOG   - log file for the RPS receiver.\n");
  myformat.append("\n   ASCDS_PROP_RPS_USER  - valid user for receiving RPS submitted proposals.\n");
  myformat.append("\n   ASCDS_RPS_TOO_USER   - valid user for receiving out-of-cycle RPS TOO\n\
                          submitted proposals.\n");


  return myformat;

}

// -----------------------------------------------------------
// Virtual member function to process the derived parameters
// -----------------------------------------------------------
FW_Param_Error 
RPSReceiverParameter::ProcessMyParameters(int &ii,const int argc,
                                          const char **argv)
{
  FW_Param_Error error(FW_Error::FW_GOOD);
  Strings tmp;

  if(strcmp(argv[ii],"-d") == 0) {
    ii++;
    if(argv[ii]) {
      tmp = argv[ii];
      if (tmp.isNumeric())
        duration = atoi(tmp.chars());
      else
        error = FW_Param_Error::FW_Param_Match;
    }
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-too") == 0) {
    rps_too = TRUE;
  }
  else if(strcmp(argv[ii],"-p") == 0) {
    printit = TRUE;
  }

  return error;
}
FW_Param_Error
RPSReceiverParameter::ValidateRequiredParameters()
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  // check environment variables
  if (!(getenv(RPS_LOG_ENV) && getenv(RPS_PROP_USER) &&
        getenv(RPS_RECV_ENV) && getenv(RPS_ERR_ENV) &&
        getenv(RPS_PROP_TOO_USER) )) {
  
      error = FW_Param_Error::FW_Param_BadEnv;
  }


  // make sure the right user is running this program
  // this check isn't foolproof, but I'm lazy.
  if (!rps_too) {
    if (strstr(getenv("USER"),getenv(RPS_PROP_USER)) == 0) {
        error = FW_Param_Error::FW_Param_BadEnv;
        cerr << "ERROR: ***** Current user should be " << getenv(RPS_PROP_USER) << " *****" << endl;
    }
  }
  else {
    if (strstr(getenv("USER"),getenv(RPS_PROP_TOO_USER)) == 0) {
        error = FW_Param_Error::FW_Param_BadEnv;
        cerr << "ERROR: ***** Current user should be " << getenv(RPS_PROP_TOO_USER) << " *****" << endl;
    }
  }

  if (printit)
    Print(cerr);

  return error;
}



// -----------------------------------------------------------
// Print to the output stream
// -----------------------------------------------------------
void RPSReceiverParameter::Print(ostream &oss)
{
  FW_Parameter::Print(oss);
  oss << "\nDuration : " << duration << endl;
}

// -----------------------------------------------------------
RPSReceiverParameter *theParameters = new RPSReceiverParameter();
