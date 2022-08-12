/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropTgtRepParameter.cc
 
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

#include "PropTgtRepParameter.hh"
#include "ProposalDefs.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
PropTgtRepParameter::PropTgtRepParameter() : ProposalParameter() 
{ 

  Initialize();
}

PropTgtRepParameter::PropTgtRepParameter(const int argc,const char **argv)
	: ProposalParameter(argc,argv) 
{ 
  Initialize();
}

void PropTgtRepParameter::Initialize()
{
  pi         = FALSE;
  pno        = FALSE;
  conflicts  = FALSE;
  mp_flag    = FALSE;
  app_flag   = FALSE;
  all_flag   = FALSE;
}
// -----------------------------------------------------------
// Virtual function for Printing the command line argument
// format for the derived class
// -----------------------------------------------------------
void PropTgtRepParameter::MyAdditionalFormat(string &str)
{
  string tmp;



  tmp.append("\nUse targets that match the following criteria.\n\
If no option is entered, the default is all targets for all proposals\n\
with a status of PROPOSED for the current proposal AO cycle.\n");
  tmp.append("-tf    [target filename] OPTIONAL  - file of target database id's\n" );
  tmp.append("-f     [filename]        OPTIONAL  - file of proposal numbers\n");
  tmp.append("-prop  [proposal number] OPTIONAL  - specific proposal number\n");
  tmp.append("-panel [panel id ]       OPTIONAL  - specific panel id\n" );
  tmp.append("-all                     OPTIONAL  - all proposals from any cycle\n");
  tmp.append("                                     with any status.\n");

  tmp.append("\nLimit proposals to the following specified values:\n");
  tmp.append("-ao     [AO ]            OPTIONAL  - AO cycle\n");
  tmp.append("-status [status ]        OPTIONAL  - status PROPOSED or APPROVED\n");
  tmp.append("-tstatus [target status] OPTIONAL  - target status \'accepted\' or\'rejected\'\n");
  tmp.append("-type   [observer type ] OPTIONAL  - type of observer, default is all\n" );

  tmp.append("\nUse the following format options:\n");
  tmp.append("-pno                     OPTIONAL  - append proposal number to output format\n" );
  tmp.append("-pi                      OPTIONAL  - append PI to output format\n" );
  tmp.append("\n");

  tmp.append("\nReport Types:  The default is the standard target list.\n");
  tmp.append("-app                     OPTIONAL  - print approved target list \n");
  tmp.append("-mp                      OPTIONAL  - print special mission planning report \n");
  tmp.append("-c                       OPTIONAL  - print conflicts \n\n");

  str.append(tmp);
}

// -----------------------------------------------------------
// Virtual member function to process the derived parameters
// -----------------------------------------------------------
FW_Param_Error
PropTgtRepParameter::ProcessAdditionalParameters(int &ii,
        const int argc,const char **argv)
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  if(strcmp(argv[ii],"-type") == 0) {
    ii++;
    if (argv[ii])
      type = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-ao") == 0) {
    ii++;
    if (argv[ii])
      prop_ao = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-tstatus") == 0) {
    ii++;
    if (argv[ii])
      tgt_status = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-status") == 0) {
    ii++;
    if (argv[ii])
      prop_status = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-tf") == 0) {
    ii++;
    if (argv[ii])
      tgtfilename = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-f") == 0) {
    ii++;
    if (argv[ii])
      filename = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-panel") == 0) {
    ii++;
    if (argv[ii])
      panelid = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-prop") == 0) {
    ii++;
    if (argv[ii])
      propno = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }

  else if(strcmp(argv[ii],"-pno") == 0) {
    propno = TRUE;
  }
  else if(strcmp(argv[ii],"-pi") == 0) {
    pi = TRUE;
  }
  else if(strcmp(argv[ii],"-c") == 0) {
    conflicts = TRUE;
  }
  else if(strcmp(argv[ii],"-app") == 0) {
    app_flag = TRUE;
  }
  else if(strcmp(argv[ii],"-mp") == 0) {
    mp_flag = TRUE;
  }
  else if(strcmp(argv[ii],"-all") == 0)
    all_flag = TRUE;


  return error;
}

// -----------------------------------------------------------
// -----------------------------------------------------------
// validate required parameters
FW_Param_Error  
PropTgtRepParameter::ValidateAdditionalParameters()
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  // check required parameters

  return error;
}


// -----------------------------------------------------------
// Print to the output stream
// -----------------------------------------------------------
void PropTgtRepParameter::PrintAdditional(ostream &oss)
{


  oss << "\nTarget Filename:  " << tgtfilename << endl;
  oss << "\nFilename       :  " << filename << endl;
  oss << "\nPanel Name     :  " << panelid << endl;
  oss << "\nProposal Number:  " << propno << endl;
  if (all_flag)
    oss << "\nAll Proposals   : Y\n";
  else
    oss << "\nAll Proposals   : N\n";

  oss << "\nAO              : " << prop_ao << endl;
  oss << "\nObserving Type  : " << type << endl;
  oss << "\nProposal Status : " << prop_status << endl;
  oss << "\nTarget Status   : " << tgt_status << endl;

  if (app_flag)
    oss << "\nPrint approved targets: Yes " << endl;
  if (conflicts)
    oss << "\nInclude Conflicts in output :  Yes" << endl;
  if (pno)
    oss << "\nInclude Proposal Number in output :  Yes" << endl;
  if (pi)
    oss << "\nInclude PI in output :  Yes" << endl;
  if (mp_flag)
    oss << "\nPrint Mission Planning report :  Yes" << endl;
}


// -----------------------------------------------------------
PropTgtRepParameter *theParameters = new PropTgtRepParameter();
