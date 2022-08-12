
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrLoadSelectCmd.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	class. This is associated with the 'Load from File'
	button on the File pulldown menu after the user has verified
	possible loss of data from a previously opened file.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#ifndef PROPMGRLOADSELECTCMD_HH
#define PROPMGRLOADSELECTCMD_HH


#include "SelectFileCmd.hh"

class Label;
class RowColumn;
class ToggleButton;

class PropMgrLoadSelectCmd : public SelectFileCmd {
    
  protected:
    
    
    virtual void fileSelected (char *);
    //virtual void CreateForm(Widget);

     
    Boolean    add_flag;
    string     iopen_file;

    // widget stuff
    Label        *type_w;

  public:
    
    // Constructor 
    PropMgrLoadSelectCmd (  int active,int window_help,Boolean aflag=FALSE,
                        FileCallback callback = NULL, void *clientData = NULL,
                        Widget parent = NULL );

    // Destructor 
    ~PropMgrLoadSelectCmd ( );

    // Accessor functions
    static void yes_cb(void *);
    static void no_cb(void *);

    void Extract(Boolean);


    virtual const char *const className () { return "PropMgrLoadSelectCmd"; }
};
#endif
