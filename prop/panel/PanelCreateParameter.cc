/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PanelCreateParameter.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal
        Panel creation application.

 
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

#include "PanelCreateParameter.hh"
#include "PanelDefs.hh"
#include "ProposalDefs.hh"

#include "find_file.h"
// ---------------------------------------------------------
// Constructors
// ---------------------------------------------------------
PanelCreateParameter::PanelCreateParameter() : ProposalParameter() 
{ 
  use_default  = FALSE;
  do_sql       = FALSE;
  do_proposals = FALSE;
  do_print     = FALSE;
}

PanelCreateParameter::PanelCreateParameter(const int argc,const char **argv)
	: ProposalParameter(argc,argv) 
{ 
}

// -----------------------------------------------------------
// Virtual function for Printing the command line argument
// format for the derived class
// -----------------------------------------------------------
void PanelCreateParameter::MyAdditionalFormat(string &str)
{

  str.append ("\n");
  str.append   (  "-l [filename]         OPTIONAL  - produce list of proposals assigned to panel.\n");
  str.append   (  "-a                    OPTIONAL  - assign proposals to panels based\n                                  on single subject category.\n" );
  str.append   (  "-s                    OPTIONAL  - assign proposals to panels using SQL files\n");             
  str.append   (  "                                  produced by 'prop_panel_assign.pl'.  These files must be\n                                  in $ASCDS_PROP_DIR/reports/assign_proposals/assign*list.\n");  
  str.append   (  "                                  See prop_panel_create.hlp.\n");
  str.append   (  "-f  [filename]        OPTIONAL  - filename for default panels\n" );
  str.append   (  "-d                    OPTIONAL  - use default file for panels\n" );

  str.append   (  "-ao [AO]              OPTIONAL  - use panels from specified AO\n"); 
  str.append   (  "-g  [output filename] OPTIONAL  - get the default file and copy   \n                                  it to the given filename.\n");

}

// -----------------------------------------------------------
// Virtual member function to process the derived parameters
// -----------------------------------------------------------
FW_Param_Error
PanelCreateParameter::ProcessAdditionalParameters(int &ii,
        const int ,const char **argv)
{
  FW_Param_Error error(FW_Error::FW_GOOD);

  if(strcmp(argv[ii],"-d") == 0) {
    use_default = TRUE;
  }
  else if(strcmp(argv[ii],"-g") == 0) {
    ii++;
    if (argv[ii])
      out_file = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-ao") == 0) {
    ii++;
    if (argv[ii])
      use_ao = argv[ii];
    else
      error = FW_Param_Error::FW_Param_Match;
  }
  else if(strcmp(argv[ii],"-a") == 0) {
    do_proposals = TRUE;
  }
  else if(strcmp(argv[ii],"-s") == 0) {
    do_sql = TRUE;
  }
  else if(strcmp(argv[ii],"-l") == 0) {
    do_print = TRUE;
    ii++;
    if (argv[ii])
      listfile = argv[ii];
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

  return error;
}

// -----------------------------------------------------------
// -----------------------------------------------------------
// validate required parameters
FW_Param_Error  
PanelCreateParameter::ValidateAdditionalParameters()
{
  FW_Param_Error error(FW_Error::FW_GOOD);
  char *ptr;
  char  buffer[FILENAME_MAX];

  // check required parameters
  if (use_default) {
    ptr = getenv("ASCDS_DATA");
    if (find_file(ptr,PANEL_CREATE_DEFAULT_FNAME,R_OK,buffer))
      filename = buffer; 
    else
      error = FW_Param_Error::FW_Param_Open;
  }


  return error;
}


// -----------------------------------------------------------
// Print to the output stream
// -----------------------------------------------------------
void PanelCreateParameter::PrintAdditional(ostream &oss)
{
  oss << "\nFilename : " << filename << endl;
  oss << "\nAO       : " << use_ao << endl;
  oss << "\nOutput filename : " << out_file << endl;
  oss << "\nList filename : " << listfile << endl;
}


// -----------------------------------------------------------
PanelCreateParameter *theParameters = new PanelCreateParameter();
