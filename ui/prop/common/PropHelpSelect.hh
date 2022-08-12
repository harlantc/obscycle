
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropHelpSelect.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	Help class. This sets up selection/viewing of the 
	associated prop_ *.hlp files in ASCDS_DOC.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef PROPHELPSELECT_HH
#define PROPHELPSELECT_HH

#include <string>

#include "SelectFileCmd.hh"

class PropHelpMsg;

class PropHelpSelect : public SelectFileCmd {
    
  protected:
    
    
    virtual void fileSelected (char *);

    PropHelpMsg  *msg_cmd;
    string curfile;


  public:
    
    // Constructor 
    PropHelpSelect (  int active);

    // Destructor 
    ~PropHelpSelect ( );


    virtual const char *const className () { return "PropHelpSelect"; }
};
#endif
