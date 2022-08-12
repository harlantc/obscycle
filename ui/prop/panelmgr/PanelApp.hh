
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the GUI Proposal Planning -
        PANEL MANAGER application.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#ifndef PANELAPP_HH
#define PANELAPP_HH

#include "ap_common.hh"
#include <string>

#include "GFW_Application.hh"
#include "Logger.hh"
#include "connect_db.hh"
#include "PanelPasswordCmd.hh"

class Cmd;

class PanelApp : public GFW_Application {

  public:

    PanelApp (char *name); 
    ~PanelApp (); 

    XConnection  *GetDBConnection() { return xconn; }
    // create database connection and initialize db classes

    void ConnectDB(char *pwd,char *user=NULL);
    const char *GetPassword() { return passwd.c_str(); }


    void   Log(string &,Boolean time_stamp=TRUE);
    void   Log(char *,Boolean time_stamp=TRUE);
    void   Log(const char *,Boolean time_stamp=TRUE);
    void   MoveLog(string & ,Boolean time_stamp=TRUE);
 
    const char  *GetUser()     { return dbuser.c_str(); }
    const char  *GetLogfileName()     { return logname.c_str(); }


    virtual const char *const className() {return ("PanelApp");}

  protected:

    virtual FW_Error MySetup();
    virtual FW_Error PostMySetup();

    virtual StringsList SetMyAppResources();

  private:

    XConnection *xconn;
    string       logname;
    Logger       *plog;
    Logger       *movelog;
    PanelPasswordCmd *pw;
    string       passwd;
    string       dbuser;

};

extern PanelApp *theApplication;

#endif
