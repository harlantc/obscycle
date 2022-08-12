
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
        This application creates the Panels for the current AO cycle.
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef PANELCREATEAPP_HH
#define PANELCREATEAPP_HH

#include <string>
#include "FW_Application.hh"
#include "FW_Error.hh"
#include "Logger.hh"

class PanelCreateApp : public FW_Application
{
public:
  // Constructor
  PanelCreateApp(char *name);

  // Destructor
  ~PanelCreateApp();

  // Executes the application
  FW_Error Execute();


protected:
  // Objects

  // The virtual function to allow the developer to perform any internal
  // setup functions before executing the application
  FW_Error Setup();

  void CopyDefaultFile(const char *);

  string      logname;
  Logger      *plog;
  XConnection *xconn;

};

// Pointer to single global instance
extern PanelCreateApp *theApplication;

#endif
