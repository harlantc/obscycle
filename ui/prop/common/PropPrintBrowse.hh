
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	PropPrintOpenSelect class. This is associated with the 'Browse'
	button on the Print form where user wishes to save output to a file.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef PROPPRINTBROWSE_HH
#define PROPPRINTBROWSE_HH

#include <string>
#include "SelectFileCmd.hh"
#include "TextField.hh"
#include <unistd.h>


class PropPrintBrowse : public SelectFileCmd {
    
  protected:
    
    
    virtual void fileSelected (char *);
     
    string  open_file;
    TextField *printer_w;

  public:
    
    // Constructor 
    PropPrintBrowse (  int active,int window_help,TextField *pw,
                        FileCallback callback = NULL, void *clientData = NULL,
                        Widget parent = NULL );

    // Destructor 
    ~PropPrintBrowse ( );

    const char *GetCurrentFilename() { return open_file.c_str(); }
    void  SetCurrentFilename(char *fname) { open_file = fname; }

    virtual const char *const className () { return "PropPrintBrowse"; }
};
#endif
