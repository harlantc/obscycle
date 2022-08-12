/*H****************************************************************************
* Copyright (c) 1995,2019 Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropRepParameter.cc
 
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

#include "PropRepParameter.hh"
#include "ProposalDefs.hh"

// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
PropRepParameter::PropRepParameter() : ProposalParameter() 
{ 
  Initialize();
}

PropRepParameter::PropRepParameter(const int argc,const char **argv)
	: ProposalParameter(argc,argv) 
{ 
  Initialize();
}

// -----------------------------------------------------------
void PropRepParameter::Initialize()
{
   tstat_flag   = TRUE;
   a_flag       = FALSE;
   p_flag       = FALSE;
   s_flag       = FALSE;
   t_flag       = FALSE;
   pt_flag      = FALSE;
   all_flag     = FALSE;
   peer_flag    = FALSE;
   final_flag   = FALSE;
   letter_flag  = FALSE;
   fairshare_flag  = FALSE;
   app_flag     = FALSE;
   supfile_flag = FALSE;
   confirm_flag = FALSE;
   appsumm_flag = FALSE;
   conflict_flag = FALSE;
   outdir ="";
}

// -----------------------------------------------------------
// Virtual function for Printing the command line argument
// format for the derived class
// -----------------------------------------------------------
void PropRepParameter::MyAdditionalFormat(string &str)
{
  string tmp;


  tmp.append("\nUse proposals that match the following criteria.\n\
If no option is entered, the default is all proposals with a\n\
status of PROPOSED for the current proposal AO cycle.\n");
  tmp.append("-panel [panel id ]       OPTIONAL  - specific panel id\n" );
  tmp.append("-prop  [proposal number] OPTIONAL  - specific proposal number\n" );
  tmp.append("-f     [filename]        OPTIONAL  - file of proposal numbers\n" );
  tmp.append("-all                     OPTIONAL  - all proposals from any cycle\n");
  tmp.append("                                     with any status.\n");

  tmp.append("\nLimit proposals to the following specified values:\n");
  tmp.append("-ao     [AO ]            OPTIONAL  - AO cycle\n");
  tmp.append("-status [status ]        OPTIONAL  - status PROPOSED,APPROVED,REJECTED\n");
  tmp.append("-type   [observer type ] OPTIONAL  - type of observer, default is all\n" );
  tmp.append("-targstat                OPTIONAL  - use all targets, accepted or not\n" );

  tmp.append("\nReport Types: \n");
  tmp.append("-abstract  proposal summary with abstract\n" );
  tmp.append("-titles    proposals list with titles\n");
  tmp.append("-appsumm   Summary with abstract for approved proposals,accepted targets only\n");
  tmp.append("-confirm   Proposal confirmation format\n");
  tmp.append("-pri       primary reviews\n" );
  tmp.append("-sec       secondary reviews\n" );
  tmp.append("-tech      technical evaluations\n" );
  tmp.append("-peer      peer reviews\n" );
  tmp.append("-final     final reviews\n" );
  tmp.append("-approved  Print final format files ONLY for accepted proposals: \n");
  tmp.append("           accept letter, technical evaluations and peer review\n" );
  tmp.append("-letter    Print confirmation/reject letter,\n");
  tmp.append("           technical evaluations and peer/final review\n" );
  tmp.append("-fairshare Print fairshare letter for accepted proposals\n");
  tmp.append("-conflict  Print user conflict information based on reviewers\n");
  tmp.append("-supfile   <directory>  Retrieve supporting files to the specified directory\n");
  tmp.append("           Set an environment variable ASCDS_USE_PINAME to use  PI name in the filename.\n");
  tmp.append("\n");

  str.append(tmp);
}

// -----------------------------------------------------------
// Virtual member function to process the derived parameters
// -----------------------------------------------------------
FW_Param_Error
PropRepParameter::ProcessAdditionalParameters(int &ii,
        const int ,const char **argv)
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
      ao = argv[ii];
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
  else if(strcmp(argv[ii],"-targstat") == 0) 
    tstat_flag = FALSE;
  else if(strcmp(argv[ii],"-approved") == 0) 
    app_flag = TRUE;
  else if(strcmp(argv[ii],"-confirm") == 0) 
    confirm_flag = TRUE;
  else if(strcmp(argv[ii],"-final") == 0) 
    final_flag = TRUE;
  else if(strcmp(argv[ii],"-letter") == 0) 
    letter_flag = TRUE;
  else if(strncmp(argv[ii],"-fair",5) == 0) 
    fairshare_flag = TRUE;
  else if(strcmp(argv[ii],"-titles") == 0) 
    pt_flag = TRUE;
  else if(strcmp(argv[ii],"-pri") == 0) 
    p_flag = TRUE;
  else if(strcmp(argv[ii],"-sec") == 0) 
    s_flag = TRUE;
  else if(strcmp(argv[ii],"-tech") == 0) 
    t_flag = TRUE;
  else if(strcmp(argv[ii],"-peer") == 0) 
    peer_flag = TRUE;
  else if(strcmp(argv[ii],"-appsumm") == 0) 
    appsumm_flag = TRUE;
  else if(strncmp(argv[ii],"-conflict",6) == 0) 
    conflict_flag = TRUE;
  else if(strcmp(argv[ii],"-all") == 0) 
    all_flag = TRUE;
  else if(strcmp(argv[ii],"-abstract") == 0) 
    a_flag = TRUE;
  else if(strncmp(argv[ii],"-sup",4) == 0)  {
    supfile_flag = TRUE;
    ii++;
    if (argv[ii])
      outdir = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }

  return error;
}

// -----------------------------------------------------------
// -----------------------------------------------------------
// validate required parameters
FW_Param_Error  
PropRepParameter::ValidateAdditionalParameters()
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  // check required parameters
  if ( a_flag == FALSE &&
       p_flag == FALSE &&
       s_flag == FALSE &&
       t_flag == FALSE &&
       supfile_flag == FALSE &&
       appsumm_flag == FALSE &&
       conflict_flag == FALSE &&
       pt_flag == FALSE &&
       final_flag == FALSE &&
       letter_flag == FALSE &&
       fairshare_flag == FALSE &&
       confirm_flag == FALSE &&
       app_flag == FALSE &&
       peer_flag == FALSE)
  {
    error = FW_Param_Error::FW_Param_Missing;
  }

  // default is PROPOSED proposals for the current cycle
/*
  if (panelid.length() == 0 && propno.length()==0 &&
      filename.length() == 0 && !all_flag) {
    error = FW_Param_Error::FW_Param_Missing;
  }
*/

  return error;
}


// -----------------------------------------------------------
// Print to the output stream
// -----------------------------------------------------------
void PropRepParameter::PrintAdditional(ostream &oss)
{
  oss << "\nPanel Name      : " << panelid << endl;
  oss << "\nFilename        : " << filename << endl;
  oss << "\nProposal Number : " << propno << endl;
  if (all_flag)
    oss << "\nAll Proposals   : Y\n";
  else
    oss << "\nAll Proposals   : N\n";
  oss << "\nObserving Type  : " << type << endl;
  oss << "\nProposal Status : " << prop_status << endl;
  oss << "\nAO              : " << ao << endl;
  oss << "\nReport Types : " << endl;
  if (a_flag)
    oss << "  Proposal Summary w/Abstract\n";
  if (pt_flag)
    oss << "  Proposal List w/Titles\n";
  if (p_flag)
    oss << "  Primary Reviews\n";
  if (s_flag)
    oss << "  Secondary Reviews\n";
  if (t_flag)
    oss << "  Technical Evaluations\n";
  if (peer_flag)
    oss << "  Peer Reviews\n";
  if (final_flag)
    oss << "  Final Reviews\n";
  if (confirm_flag)
    oss << "Print Confirmation format\n";
  if (supfile_flag)
    oss << "  Supporting Files\n";
  if (app_flag)
    oss << "Use final format for ONLY approved proposals.\n";
  if (letter_flag)
    oss << "Use accept/reject letter format (don't print reviewer names).\n";
  if (fairshare_flag)
    oss << "Use fairshare letter format.\n";
  if (conflict_flag)
    oss << "Display panel member conflicts .\n";
  oss << endl;
}


// -----------------------------------------------------------
PropRepParameter *theParameters = new PropRepParameter();
