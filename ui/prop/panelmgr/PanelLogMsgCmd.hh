
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelLogMsgCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  Panel Manager to
	add comments to the log file.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELLOGMSGCMD_HH
#define PANELLOGMSGCMD_HH


#include <string>
#include "GeneralDialogCmd.hh"
#include "ScrollText.hh"
#include "Logger.hh"


class PanelLogMsgCmd : public GeneralDialogCmd {
    
  public:
    
    // Constructor 
    PanelLogMsgCmd ( int active, const char *name = "Notes...");


    // Destructor 
    ~PanelLogMsgCmd ( );

    void SetMessage(char *str);
    void SetMessage(const char *str) ;
    void SetMessage(string str) ;


    virtual const char *const className () { return "PanelLogMsgCmd"; }


  protected:
    ScrollText   *st;        // scrolled text widget for displaying message
    string logname;
    Logger *plog;

    

    // form methods
    virtual void CreateGeneralForm ();
    virtual void UpdateGeneralForm ();

    // Virtual callback function for ok
    virtual void ok_cb(void *);

    // Virtual callback function for cancel
    virtual void cancel_cb(void *);

 


};

#endif
