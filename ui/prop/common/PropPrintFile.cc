
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:	This contains the code for the Print option on the
		File pulldown menubar for the Proposal Planning - 


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>
#include <unistd.h>
#include <fstream>
#include <Xm/Xm.h>
#include <Xm/Label.h>
#include <Xm/RowColumn.h>

#include "TextField.hh"
#include "ToggleButton.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "PropPrintFile.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropPrintFile::PropPrintFile (  
        int  active,
	void *client_data,
        int  window_help)
	: PrintFileCmd (active,client_data,window_help)
{

  delete_flg = FALSE;

}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropPrintFile::~PropPrintFile()
{
  
}

// ------------------------------------------------------------
// Virtual callback - Print text and/or screen dump
// ------------------------------------------------------------
void PropPrintFile::ok_cb(void *)
{
  

  Print((char *)filename.c_str());


}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropPrintFile::cancel_cb(void *)
{
  if (delete_flg) {
    unlink(filename.c_str());
    filename = "";
    delete_flg = FALSE;
  }
}
