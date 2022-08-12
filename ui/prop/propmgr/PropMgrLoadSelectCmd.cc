
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrLoadSelectCmd.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the GUI PropMgrLoadSelectCmd class.
	This is the class called to actually open the file.  The user
	may have needed to verify possible loss of data from a previously
	opened file.


* NOTES: 



* REVISION HISTORY:


        Ref. No.
        --------
        @V(#) @V(#)

        Date
        ----
        @D(#) @D(#)
 

*H******************************************************/

#include <string>
#include <stdlib.h>
#include <iostream>


#include "ap_common.hh"
#include "ErrorMessage.hh"


// proposal gui
#include "PropMgrLoadSelectCmd.hh"
#include "PropMgrPropList.hh"
#include "PropMgrApp.hh"
#include "PropMgrMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrLoadSelectCmd::PropMgrLoadSelectCmd (  
                int          active,
                int          window_help,
		Boolean      aflag,
                FileCallback callback,
                void        *clientData,
		Widget       parent ) 
		: SelectFileCmd ("Load from File",active,window_help,callback,clientData,
		                  parent)
{
  add_flag = aflag;
    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrLoadSelectCmd::~PropMgrLoadSelectCmd()
{


}

// ----------------------------------------------------------
// ------------------------------------------------------------
void PropMgrLoadSelectCmd::fileSelected(char *ifilename)
{
  string smsg;

  iopen_file = ifilename;

  if (theApplication->GetDBConnection() == NULL) {
    smsg = "No database connection exists.  Please check parameters for\nuser, password and server.\n";
    theApplication->Log(smsg);
    theErrorMessage->DisplayMessage((char *)smsg.c_str());
  }
  else {
    thePropMgrWindow->GetPropList()->RebuildList(LOAD_FILE,ifilename);
  }



} 
