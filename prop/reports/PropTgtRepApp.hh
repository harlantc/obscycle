
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
        This application produces 
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef PROPTGTREPAPP_HH
#define PROPTGTREPAPP_HH

#include "FW_Application.hh"
#include "FW_Error.hh"
#include "ProposalArray.hh"

class PropTgtRepApp : public FW_Application
{
public:
  // Constructor
  PropTgtRepApp(const char *name);

  // Destructor
  ~PropTgtRepApp();

  // Executes the application
  FW_Error Execute();

protected:
  // Objects

  // The virtual function to allow the developer to perform any internal
  // setup functions before executing the application
  FW_Error Setup();

  XConnection *xconn;

};

// Pointer to single global instance
extern PropTgtRepApp *theApplication;

#endif
