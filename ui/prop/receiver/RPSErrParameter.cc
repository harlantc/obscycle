/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSErrParameter.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The RPSErrParameter class provides the application with the
        set of specific parameters needed to execute the Proposal
        application RPS Error Processing GUI function.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include "ap_common.hh"
#include <strings.h>
#include <stdlib.h>
#include <fstream>

#include "RPSErrParameter.hh"
#include "ProposalDefs.hh"
#include "ObsUtil.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
RPSErrParameter::RPSErrParameter() : FW_Parameter() 
{ 
  Initialize();
}

RPSErrParameter::RPSErrParameter(const int argc,const char **argv)
	: FW_Parameter(argc,argv) 
{ 
  Initialize();
}
// -----------------------------------------------------------
// -----------------------------------------------------------
void RPSErrParameter::Initialize()
{

  printit = FALSE;
  all_proposals = FALSE;
  sqlserver = getenv(PROP_SQL_SERVER);
  server  = getenv(PROP_ARCH_SERVER);
  logenv  = getenv(RPS_LOG_ENV);
  errenv  = getenv(RPS_ERR_ENV);
  archenv = getenv(RPS_ARCH_ENV);
  status = PROPOSED_STATUS;
  ao = (char *)getenv(PROP_AO_ENV);
}


// -----------------------------------------------------------
// Virtual function for Printing the command line argument 
// format for the derived class
// -----------------------------------------------------------
Strings RPSErrParameter::MyFormat()
{
  Strings tmp;

  tmp.append("\nThe default proposals are all proposals with a status of PROPOSED\n");  
  tmp.append("for the current AO cycle. Use '%' as the wildcard.\nUser may limit proposals by:\n");
  tmp.append("-ao     [cycle]  OPTIONAL  - AO cycle, default is ASCDS_PROP_AO\n");
  tmp.append("-status [status] OPTIONAL  - proposal status, default is PROPOSED\n");
  tmp.append("                             options: PROPOSED,APPROVED,REJECTED\n");

  tmp.append("\n-U  [user]       OPTIONAL  - user name for database access");
  tmp.append("\n-S  [server]     OPTIONAL  - database server");
  tmp.append("\n                             default is $");
  tmp.append(PROP_SQL_SERVER);
  tmp.append("\n\nNOTE:  Files that are processed are moved to the RPS directories\n defined by the environment variables:\n");
  tmp.append("\n   ASCDS_PROP_RPS_ARCH  - all proposals successfully ingested into\n\
                          the Proposal database.");
  tmp.append("\n   ASCDS_PROP_RPS_ERR   - all submitted proposals that require human\n\
                          intervention. ");
  //tmp.append("\n   ASCDS_PROP_RPS_LOG   - log files.\n");
  //tmp.append("\n   ");
  //tmp.append(PROP_ARCH_SERVER);
  //tmp.append("   - archive server for submitting proposal files.");
  //tmp.append("\n   ");
  //tmp.append(PROP_SQL_SERVER);
  //tmp.append("   - SQL server for retrieving the proposal list.");
  tmp.append("\n   ");


  return tmp;

}

// -----------------------------------------------------------
// Virtual member function to process the derived parameters
// -----------------------------------------------------------
FW_Param_Error 
RPSErrParameter::ProcessMyParameters(int &ii,const int argc,const char **argv)
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  if(strcmp(argv[ii],"-U") == 0) {
    ii++;
    if (argv[ii])
      user = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-S") == 0) {
    ii++;
    if (argv[ii])
      sqlserver = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-status") == 0) {
    ii++;
    if (argv[ii])
      status = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-ao") == 0) {
    ii++;
    if (argv[ii])
      ao = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-p") == 0) {
    printit = TRUE;
  }
      

  return error;
}

// validate required parameters
FW_Param_Error  
RPSErrParameter::ValidateRequiredParameters()
{
  FW_Param_Error error(FW_Error::FW_GOOD);


  // check required parameters
  if (server.length()==0 || sqlserver.length()==0) {
    cerr << "\nERROR: Either specify the -AS,-S parameters or set the appropriate environment variables.\n" << endl;
    error = FW_Param_Error::FW_Param_Missing;
  }
 

  // check environment variables
  if (!(getenv(RPS_LOG_ENV) && 
      getenv(RPS_ARCH_ENV) &&
      getenv(RPS_ERR_ENV))) {
  
      error = FW_Param_Error::FW_Param_BadEnv;
  }


  if (printit)
    Print(cerr);

  return error;
}

// -----------------------------------------------------------
// Print to the output stream
// -----------------------------------------------------------
void RPSErrParameter::Print(ostream &oss)
{
  FW_Parameter::Print(oss);
  oss << "\nUser             : " << user << endl;
  oss << "\nArchive Server   : " << server << endl;
  oss << "\nSQL Server       : " << sqlserver << endl;
  oss << "\nAO               : " << ao << endl;
  oss << "\nStatus           : " << status << endl;
}

// -----------------------------------------------------------
RPSErrParameter *theParameters = new RPSErrParameter();
