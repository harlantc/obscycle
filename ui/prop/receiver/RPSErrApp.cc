

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrApp.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the GUI Proposal Planning -
        RPS Error GUI application.



* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#include <fstream>
#include <sys/stat.h>
#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>

#include "RPSErrApp.hh"
#include "RPSErrParameter.hh"
#include "RPSErrMenuWindow.hh"

#include "StatusAreaList.hh"
#include "ProposalDefs.hh"


RPSErrApp *theApplication = new RPSErrApp((char *)"ProposalPlanning");

RPSErrMenuWindow *theRPSErrWindow = new RPSErrMenuWindow("Proposal - RPS Errors");

// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
RPSErrApp :: RPSErrApp(char *name) : GFW_Application (name)
{
  plog = NULL;  
  xconn = NULL;
  prop = NULL;


}

// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
RPSErrApp :: ~RPSErrApp()
{
  delete pw;
  delete prop;
  delete xconn;
  delete theRPSErrWindow;
  delete plog;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
FW_Error RPSErrApp::MySetup()
{
  FW_Error err = FW_Error::FW_GOOD;
  string msg;

  static os_helper_toolkit init_helper;
  static os_file_toolkit init_file;
  static os_time_toolkit init_time;
  static os_streaming_toolkit init_stream;
  static os_unix_toolkit init_unix;
  static os_network_toolkit init_network;

  // open log for processed proposal data
  logname = (char *)getenv(RPS_LOG_ENV);
  logname.append("/");
  logname.append(getenv("LOGNAME"));
  logname.append("_");
  logname.append(RPS_ERRGUI_LOG_FILENAME);
  plog = new Logger(logname.c_str(),'\n',TRUE,FALSE);
  chmod(logname.c_str(),S_IRUSR|S_IWUSR|S_IRGRP|S_IWGRP);
  if (plog->IsReadOnly()) {
    cerr <<  "\nFATAL ERROR! " << "Unable to open ";
    cerr << logname << " log file for writing!\n" << endl ;
    err = FW_Error::FW_BAD;
  }

  else {
    msg = "*** prop_rps_gui  ";
    msg += "  LOGNAME: ";
    if (getenv("LOGNAME"))
      msg.append(getenv("LOGNAME"));
    msg += "   VERSION:  ";
    if (getenv("ASCDS_VERSION"))
      msg.append(getenv("ASCDS_VERSION"));

    Log(msg,TRUE);
  }

  return err;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
FW_Error RPSErrApp::PostMySetup()
{
  FW_Error err = FW_Error::FW_GOOD;
  string usr;
  Boolean needuser = FALSE;
 
  usr = theParameters->GetUser();
  if (usr.length() == 0)
    needuser = TRUE;

  theRPSErrWindow->iconify();
  pw = new RPSErrPasswordCmd((char *)"Database Password",TRUE,base_w,needuser);
  pw->execute();

  return err;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void RPSErrApp::ConnectDB(char *pwd,char *user)
{
  string msg;
  const char *usr;
 
  if (!user)
    usr = theParameters->GetUser();
  else 
    usr =user;

  // get the database connection
  // use default environment variable for SQL server
  // if no connection, create temporary database connection
  xconn= connect_db(msg,usr,pwd,theParameters->GetSQLServer());

  if (!xconn) {
    plog->LogMessage(msg.c_str(),TRUE);
    cerr << "\n" << msg << endl;
    exit(0);
  }
  else {
    msg = "prop_rps_gui  successfully connected to the SQL server ";
    msg.append(theParameters->GetSQLServer());
    msg.append(" as ");
    msg.append(usr);
    plog->LogMessage(msg.c_str(),TRUE);
  }
    

  prop = new RPSErrIngest(plog,pwd,usr);
  if (prop->GetState() == BAD) {
    cerr << "\nFATAL ERROR! " << "Unable to initialize proposal ingest class.  \nPlease see the error log, " << logname << ", for further information." << endl;
    exit(0);
  }
  else {
    msg = "prop_rps_gui  successfully connected to the archive server ";
    msg.append(theParameters->GetServer());
    msg.append(" as ");
    msg.append(usr);
    plog->LogMessage(msg.c_str(),TRUE);
  }
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void RPSErrApp :: Log(char *msg,Boolean tstamp)
{
  Log((const char *)msg,tstamp);
}
void RPSErrApp :: Log(string &msg,Boolean tstamp)
{
  Log(msg.c_str(),tstamp);
}
void RPSErrApp :: Log(const char *msg,Boolean tstamp)
{

  if (msg) {
    plog->LogMessage(msg,tstamp);
  }
}
// ----------------------------------------------------------
// ----------------------------------------------------------
StringsList RPSErrApp :: SetMyAppResources()
{
  StringsList res;

  res.Append("ProposalPlanning.*.foreground       : black");
  res.Append("ProposalPlanning.*.background       : LightSteelBlue2");
  res.Append("ProposalPlanning*.topShadowColor    : SlateGray1");
  res.Append("ProposalPlanning*.bottomShadowColor : LightSteelBlue4");
  res.Append("ProposalPlanning*.highlightColor    : LightSteelBlue1");
  res.Append("ProposalPlanning*.borderColor       : SlateGray4");
  res.Append("ProposalPlanning*.selectColor       : Yellow");
  res.Append("ProposalPlanning*.TextField*.background : LightSteelBlue3");
  res.Append("ProposalPlanning*.DateField*.background : LightSteelBlue3");
  res.Append("ProposalPlanning*.HeaderLabel.fontList  : -adobe-courier-bold-r-normal-*-12-*-*");
  res.Append("ProposalPlanning*.fontList    : -adobe-courier-bold-r-normal-*-12-*-*");

  res.Append("ProposalPlanning*.RPSErrorText.rows       : 4");
  res.Append("ProposalPlanning*.RPSErrorText.columns    : 80");
  res.Append("ProposalPlanning*.RPSFileText.rows        : 15");
  res.Append("ProposalPlanning*.RPSFileText.columns     : 80");
  res.Append("ProposalPlanning*.RPSErrForm*.background  : LightSteelBlue3");
  res.Append("ProposalPlanning*.RPSErrPropForm*.background  : LightSteelBlue3");
  res.Append("ProposalPlanning*.RPSPropList.visibleItemCount   : 14");
  res.Append("ProposalPlanning*.RPSList.visibleItemCount   : 15");
  res.Append("ProposalPlanning*.RPSList.fontList   : -adobe-courier-bold-r-normal-*-12-*-*");

  res.Append("ProposalPlanning*.RPSPropListSW.width     : 150");
  res.Append("ProposalPlanning*.RPSPropList.fontList   : -adobe-courier-bold-r-normal-*-12-*-*");
  res.Append("ProposalPlanning*.RPSPropListLabel.fontList   : -adobe-courier-bold-r-normal-*-12-*-*");

  res.Append("ProposalPlanning*.PrintList.set       : True");
  res.Append("ProposalPlanning*.PrintFile.set       : True");
  res.Append("ProposalPlanning*.PrintError.set      : True");

  res.Append("ProposalPlanning*.TextOptions.entryBorder  : 1");
  res.Append("ProposalPlanning*.TextOptions.borderWidth  : 0");
  res.Append("ProposalPlanning*.TextOptions.orientation  : XmVERTICAL");



  return res;
}
