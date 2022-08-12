
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
        This application ingests primary,secondary, technical review data
        into the database.
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef PROPREADREVAPP_HH
#define PROPREADREVAPP_HH

#include "FW_Application.hh"
#include "FW_Error.hh"
#include "Logger.hh"
#include "ProposalReview.hh"

class PropReadRevApp : public FW_Application
{
public:
  // Constructor
  PropReadRevApp(const char *name);

  // Destructor
  ~PropReadRevApp();

  // Executes the application
  FW_Error Execute();

  void Exit();

protected:
  // Objects

  // The virtual function to allow the developer to perform any internal
  // setup functions before executing the application
  FW_Error Setup();

  XConnection *xconn;
  Logger *log;           // log file for the mail processs


};

// Pointer to single global instance
extern PropReadRevApp *theApplication;

#endif
