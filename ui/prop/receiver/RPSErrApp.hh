
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrApp.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the GUI Proposal Planning -
	RPS Error GUI application.  


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#ifndef RPSERRAPP_HH
#define RPSERRAPP_HH


#include <string>
#include "GFW_Application.hh"
#include "Logger.hh"
#include "connect_db.hh"
#include "RPSErrIngest.hh"
#include "RPSErrPasswordCmd.hh"

class XConnection;
class Cmd;

class RPSErrApp : public GFW_Application {

  public:

    RPSErrApp (char *name); 
    ~RPSErrApp (); 

    // accessor functions
    Logger *GetLog() { return plog; }
    void   Log(string &,Boolean time_stamp);
    void   Log(char *,Boolean time_stamp);
    void   Log(const char *,Boolean time_stamp);
    XConnection  *GetDBConnection() { return xconn; }
    RPSErrIngest *GetIngest() { return prop; }

    // create database connection and initialize db classes
    void ConnectDB(char *pwd,char *user=NULL);

    virtual const char *const className() {return ("RPSErrApp");}

  protected:

    virtual FW_Error MySetup();
    virtual FW_Error PostMySetup();

    virtual StringsList SetMyAppResources();

  private:
    
    string       logname;
    Logger       *plog;
    XConnection  *xconn;
    RPSErrIngest *prop;
    RPSErrPasswordCmd *pw;
};

extern RPSErrApp *theApplication;

#endif
