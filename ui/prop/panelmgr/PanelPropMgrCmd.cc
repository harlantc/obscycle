
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPropMgrCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code that executes the proposal
	manager gui (prop_manager_gui).


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "ap_common.hh"
#include "PanelPropMgrCmd.hh"
#include "PanelParameter.hh"
#include "PanelApp.hh"



#define PROPMGR_EXEC "prop_manager_gui"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelPropMgrCmd::PanelPropMgrCmd ( int active) : 
          ExecuteCmd ( (char *)"Proposal Manager GUI", active,(char *)PROPMGR_EXEC )
{
    
    SetMenuFields ( 'P');
    Initialize();
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelPropMgrCmd::~PanelPropMgrCmd()
{
}
// ------------------------------------------------------------
// ------------------------------------------------------------
Boolean PanelPropMgrCmd::Setup()
{
  return TRUE;
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelPropMgrCmd::Initialize()
{
}
// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelPropMgrCmd::doit()
{
/*
  date = ((ORFBatchSFEDate *)theORFWindow->batch_cmd())->GetDate();
  cmd = BATCH_SFE_EXEC;
  cmd += " -in ";
  cmd += theORFWindow->GetCurrentFilename();
  cmd += " -date \"";
  cmd += date;
  cmd += "\" | tee -a ";
  cmd += theApplication->GetLogFilename();
  cmd += " &";
  system(cmd.c_str());
  
*/
  
  string params;
  params = "-U^";
  params += theApplication->GetUser();
  params += "^-S^";
  params += theParameters->GetServer();
      
  SetParameters((char *)params.c_str(),'^');
      
  ExecCommand();
}
