
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	PanelPrintCmd class.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELPRINTCMD_HH
#define PANELPRINTCMD_HH


#include "PrintCmd.hh"

class ToggleButton;

class PanelPrintCmd : public PrintFileCmd {
    
    
  protected:
    
    // text option widgets
    ToggleButton *all_btn;
    ToggleButton *cur_btn;
    ToggleButton *plist_btn;
    ToggleButton *panlist_btn;
    ToggleButton *alllist_btn;

    // Called to create the specific fields
    virtual void CreateGeneralForm ();

    virtual void ok_cb(void *);
    virtual void cancel_cb(void *);


  public:
    
    // Constructor 
    PanelPrintCmd (  int active,void *client_data,int window_help);


    // Destructor 
    ~PanelPrintCmd ( );

    virtual const char *const className () { return "PanelPrintCmd"; }
};

#endif
