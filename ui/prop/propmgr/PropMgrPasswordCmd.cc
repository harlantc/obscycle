
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

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
#include "PropMgrPasswordCmd.hh"
#include "PropMgrLoadCmd.hh"
#include "PropMgrApp.hh"
#include "PropMgrMenuWindow.hh"



// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrPasswordCmd::PropMgrPasswordCmd (char *name,int active,
		Widget parent,Boolean needuser) : 
                     PasswordPopup (name,active,parent,needuser)
{

    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrPasswordCmd::~PropMgrPasswordCmd()
{
}

// ----------------------------------------------------------
// OK callback
// ----------------------------------------------------------
void PropMgrPasswordCmd::ok_cb(void *clientData)
{

  if (!uflag)
    theApplication->ConnectDB(pwd.chars());
  else {
    string usr = user_w->Get();
    theApplication->ConnectDB(pwd.chars(),(char *)usr.c_str());
  }

  thePropMgrWindow->manage();
  ((PropMgrLoadCmd*)(thePropMgrWindow->retrieve_cmd()))->execute();
   

}

