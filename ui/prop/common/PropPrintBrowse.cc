
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropPrintBrowse.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the GUI PropPrintBrowse class.
        This routine is used when user wishes to save Print command output
        to a file.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include "ap_common.hh"
#include <stdlib.h>
#include <iostream>

// general library
#include "Error.h"

// gui extensions
#include "ErrorMessage.hh"
#include "GUIDefs.h"

#include "ProposalDefs.hh"

#include "PropPrintBrowse.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropPrintBrowse::PropPrintBrowse (  
                int          active,
                int          window_help,
		TextField   *print_w, 
                FileCallback callback,
                void        *clientData,
		Widget       parent)
		: SelectFileCmd ("Browse",active,window_help,callback,
			clientData, parent,FALSE)
{
  char *env;
  string ext;

  printer_w = print_w;

  env = getcwd(NULL,PATH_MAX);
  if (env) {
    ext = "*";
    SetDirectoryPath(env,(char *)ext.c_str());
  }
    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropPrintBrowse::~PropPrintBrowse()
{
}

// ----------------------------------------------------------
// Open the existing profile.   Delete existing profile objects/lists.
// Clear the drawing canvas.
// ------------------------------------------------------------
void PropPrintBrowse::fileSelected(char *filename)
{

  // show selected filename in printer window
  open_file = filename;
  printer_w->Set(filename);


} 
