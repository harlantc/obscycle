
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrLoadFileCmd.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning 
	class. This is associated with the 'Load from File'
	button on the File pulldown menu.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef PROPMGRLOADFILECMD_HH
#define PROPMGRLOADFILECMD_HH


#include "FileOpenVerifyCmd.hh"

class PropMgrLoadSelectCmd;

class PropMgrLoadFileCmd : public FileOpenVerifyCmd {
    
  protected:
    
    Boolean add_flag;
    int whelp;                       // window help
    PropMgrLoadSelectCmd *select_cmd;  // select command (file selection box)
    
    virtual void SelectFile ();
     

  public:
    
    // Constructor 
    PropMgrLoadFileCmd (  int active,int window_help,const char *name="Retrieve by File...",
          Boolean aflag = FALSE);

    // Destructor 
    ~PropMgrLoadFileCmd ( );


    virtual const char *const className () { return "PropMgrLoadFileCmd"; }
};
#endif
