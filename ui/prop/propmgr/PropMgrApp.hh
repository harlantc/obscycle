
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObSCycle

* DESCRIPTION:

        This file contains the definition for the GUI Proposal Planning -
        PROPOSAL MANAGER application.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#ifndef PROPMGRAPP_HH
#define PROPMGRAPP_HH

#include "ap_common.hh"
#include <string>

#include "GFW_Application.hh"
#include "Logger.hh"
#include "connect_db.hh"

class Cmd;
class PropMgrPasswordCmd;

class PropMgrApp : public GFW_Application {

  public:

    PropMgrApp (char *name); 
    ~PropMgrApp (); 

    XConnection  *GetDBConnection() { return xconn; }
    void  ConnectDB(char *passwd,char *user=NULL);
    const char  *GetPassword() { return pwd.c_str(); }
    const char  *GetLogname()  { return logname.c_str(); }
    const char  *GetUser()     { return dbuser.c_str(); }

    void   Log(string &,Boolean time_stamp=TRUE);
    void   Log(char *,Boolean time_stamp=TRUE);
    void   Log(const char *,Boolean time_stamp=TRUE);
 
    virtual const char *const className() {return ("PropMgrApp");}

  protected:

    virtual FW_Error MySetup();
    virtual FW_Error PostMySetup();

    virtual StringsList SetMyAppResources();

  private:
 
    XConnection *xconn;
    string       logname;
    string       dbuser;
    Logger       *plog;
    PropMgrPasswordCmd *pw;
    string        pwd;

};

extern PropMgrApp *theApplication;

#endif
