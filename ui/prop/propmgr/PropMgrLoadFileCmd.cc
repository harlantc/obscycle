
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrLoadFileCmd.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the GUI PropMgrLoadFileCmd class.
	This command is for the File->Load from File menu .


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <string>
#include <stdlib.h>
#include <iostream>

// general library
#include "ap_common.hh"
#include "Error.h"
#include "WarnDialogManager.hh"

#include "PropMgrLoadFileCmd.hh"
#include "PropMgrLoadSelectCmd.hh"
#include "PropMgrMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrLoadFileCmd::PropMgrLoadFileCmd (  
                int     active,
                int     window_help,
		const char   *name,
		Boolean aflag)
		: FileOpenVerifyCmd (active,name)
{

  add_flag = aflag;
  whelp = window_help;
  select_cmd = NULL;
  SetMenuFields ( 'F',NULL,NULL);

    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrLoadFileCmd::~PropMgrLoadFileCmd()
{
  if (select_cmd)
    delete select_cmd;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropMgrLoadFileCmd::SelectFile()
{
  if (!select_cmd)
    select_cmd = new PropMgrLoadSelectCmd(True,whelp,add_flag,NULL,NULL,
		thePropMgrWindow->baseWidget());

  select_cmd->execute();

}
