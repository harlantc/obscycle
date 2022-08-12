
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPasswordCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the 
	class. 


* NOTES:

* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <Xm/Xm.h>
#include <Xm/Label.h>
#include <Xm/TextF.h>

#include "ap_common.hh"
#include "PanelPasswordCmd.hh"
#include "PanelApp.hh"
#include "PanelMenuWindow.hh"



// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelPasswordCmd::PanelPasswordCmd (char *name,int active,Widget parent,
	Boolean needuser) : 
                     PasswordPopup (name,active,parent,needuser)
{

    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelPasswordCmd::~PanelPasswordCmd()
{
}

// ----------------------------------------------------------
// OK callback
// ----------------------------------------------------------
void PanelPasswordCmd::ok_cb(void *clientData)
{

  if (!uflag)
    theApplication->ConnectDB(pwd.chars());
  else {
    string usr = user_w->Get();
    theApplication->ConnectDB(pwd.chars(),(char *)usr.c_str());
  }

  thePanelWindow->DisplayPanels();
  thePanelWindow->manage();
   

}

