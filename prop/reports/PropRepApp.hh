
/*H****************************************************************************
* Copyright (c) 1995,2019  Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
        This application produces  reports for a list of proposals
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef PROPREPAPP_HH
#define PROPREPAPP_HH

#include "FW_Application.hh"
#include "FW_Error.hh"
#include "ProposalArray.hh"
#include "ProposalSupFiles.hh"

class PropRepApp : public FW_Application
{
public:
  // Constructor
  PropRepApp(const char *name);

  // Destructor
  ~PropRepApp();

  // Executes the application
  FW_Error Execute();

protected:
  // Objects

  // The virtual function to allow the developer to perform any internal
  // setup functions before executing the application
  FW_Error Setup();
  FW_Error process_conflicts(string panelid,string ao);
  FW_Error process_supporting_files(ProposalSupFiles *,string,ProposalEntry* );

  XConnection *xconn;

};

// Pointer to single global instance
extern PropRepApp *theApplication;

#endif
