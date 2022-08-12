

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsVis

* DESCRIPTION:

        This file contains the code for the GUI Proposal Planning -
        PROPOSAL MANAGER application.



* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>


#include "PropMgrApp.hh"
#include "PropMgrParameter.hh"
#include "PropMgrMenuWindow.hh"

#include "StatusAreaList.hh"
#include "ProposalDefs.hh"
#include "PropMgrDefs.hh"
#include "PropMgrPasswordCmd.hh"
#include "PropMgrViewCoIs.hh"


PropMgrApp *theApplication = new PropMgrApp((char *)"ProposalPlanning");

PropMgrMenuWindow *thePropMgrWindow = new PropMgrMenuWindow("Proposal Manager");

// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrApp :: PropMgrApp(char *name) : GFW_Application (name)
{
  
  xconn = NULL;
  plog  = NULL;
  pw    = NULL;


}

// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrApp :: ~PropMgrApp()
{
  delete pw;
  delete xconn;
  delete plog;
  delete thePropMgrWindow;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
FW_Error PropMgrApp::MySetup()
{
  FW_Error err = FW_Error::FW_GOOD;
  string  msg;

  static os_helper_toolkit init_helper;
  static os_file_toolkit init_file;
  static os_time_toolkit init_time;
  static os_streaming_toolkit init_stream;
  static os_unix_toolkit init_unix;
  static os_network_toolkit init_network;
 
  // open log for processed proposal data
  logname = (char *)getenv(PROP_LOG_ENV);
  logname.append("/");
  logname.append(getenv("LOGNAME"));
  logname.append("_");
  logname.append(PROPMGR_LOG_FILENAME);
  plog = new Logger(logname.c_str(),'\n',TRUE,FALSE);
  if (plog->IsReadOnly()) {
    cerr <<  "\nFATAL ERROR! " << "Unable to open ";
    cerr << logname << " log file for writing!\n" << endl ;
    err = FW_Error::FW_BAD;
  }

  else {
    msg = "*** prop_manager_gui  ";
    msg += "  LOGNAME: ";
    if (getenv("LOGNAME"))
      msg.append(getenv("LOGNAME"));
    msg += "   VERSION:  ";
    if (getenv("ASCDS_VERSION"))
      msg.append(getenv("ASCDS_VERSION"));

    Log(msg);
    msg.clear();

  }


  return err;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropMgrApp::ConnectDB(char *passwd,char *user)
{

  string  msg;

  pwd = passwd;  // needed for background reports
  if (!user)
    dbuser = theParameters->GetUser();
  else
    dbuser = user;

  // get the database connection
  // if no connection, create temporary database connection
  xconn= connect_db(msg,dbuser.c_str(),pwd.c_str(),theParameters->GetServer());
  if (!xconn) {
    Log(msg);
    cerr << "\n" << msg << endl;
    exit(0);
  }
  else {
    msg = "prop_manager_gui  successfully connected to SYBASE.";
    Log(msg);
  }

}

// ----------------------------------------------------------
// ----------------------------------------------------------
FW_Error PropMgrApp::PostMySetup()
{
  FW_Error err = FW_Error::FW_GOOD;
  string usr;
  Boolean needuser = FALSE;
 
  usr = theParameters->GetUser();
  if (usr.length() == 0)
    needuser = TRUE;


  thePropMgrWindow->iconify();
  pw = new PropMgrPasswordCmd((char *)"Database Password",TRUE,base_w,needuser);
  pw->execute();

  ((PropMgrViewCoIs *)(thePropMgrWindow->view_cois_cmd()))->SetMain(thePropMgrWindow->baseWidget());
  return err;


}

// ----------------------------------------------------------
// Log routines
// ----------------------------------------------------------
void PropMgrApp::Log(char *msg,Boolean tstamp)
{
  Log((const char *)msg,tstamp);
}
void PropMgrApp::Log(string &msg,Boolean tstamp)
{
  Log(msg.c_str(),tstamp);
}
void PropMgrApp::Log(const char *msg,Boolean tstamp)
{

  if (msg) {
    plog->LogMessage(msg,tstamp);
  }
}

// ----------------------------------------------------------
// ----------------------------------------------------------
StringsList PropMgrApp :: SetMyAppResources()
{
  StringsList res;

  res.Append("ProposalPlanning.*.foreground       : black");
  res.Append("ProposalPlanning.*.background       : LightSteelBlue2");
  res.Append("ProposalPlanning.*.fontList : -adobe-courier-bold-r-normal-*-12-*-*");
  res.Append("ProposalPlanning*.selectColor : Yellow");


  res.Append( "ProposalPlanning*.HeaderLabel.fontList    : -adobe-courier-bold-r-normal-*-14-*-*");


  res.Append("ProposalPlanning*.TextOptions.entryBorder  : 1");
  res.Append("ProposalPlanning*.TextOptions.borderWidth  : 0");
  res.Append("ProposalPlanning*.TextOptions.orientation  : XmVERTICAL");
  res.Append("ProposalPlanning*.RetrievePanel.set        : True");
  res.Append("ProposalPlanning*.LoadRC.packing           : XmPACK_TIGHT");
  res.Append("ProposalPlanning*.EmailRC.packing          : XmPACK_COLUMN");
  res.Append("ProposalPlanning*.EmailRC.numColumns       : 3");
  res.Append("ProposalPlanning*.EmailForm.width          : 300");
  res.Append("ProposalPlanning*.PrintRC.packing          : XmPACK_COLUMN");
  res.Append("ProposalPlanning*.PrintRC.numColumns       : 2");
  res.Append("ProposalPlanning*.PrintAll.set          : True");

  res.Append("ProposalPlanning*.SortList*.background      : LightSteelBlue3");
  res.Append("ProposalPlanning*.SortRC.packing            : XmPACK_COLUMN");
  res.Append("ProposalPlanning*.SortRC.numColumns         :  3");
  res.Append("ProposalPlanning*.SortList.visibleItemCount :  6");

  res.Append("ProposalPlanning*.*ReviewText*.rows       : 05");
  res.Append("ProposalPlanning*.*ReviewText*.columns    : 80");
  res.Append("ProposalPlanning*.*ReviewText*.background : LightSteelBlue3");

  res.Append("ProposalPlanning*.*PropMgrForm.width   : 800");
  res.Append("ProposalPlanning*.*PropMgrForm.height  : 450");
  res.Append("ProposalPlanning*.*ListLabel*.fontList    : -misc-fixed-bold-r-normal-*-13-*-*");
  res.Append("ProposalPlanning*.*PropMgrList*.fontList  : -misc-fixed-bold-r-normal-*-13-*-*");
  res.Append("ProposalPlanning*.*PropMgrList*.visibleItemCount   : 20");
  res.Append("ProposalPlanning*.*PropMgrList*.listSpacing   : 5");
  res.Append("ProposalPlanning*.*PropCoIForm.width     : 450");
  res.Append("ProposalPlanning*.LoadRC.packing          : XmPACK_COLUMN");
  res.Append("ProposalPlanning*.LoadRC.numColumns       : 3");
  res.Append("ProposalPlanning*.*CoIBtn.leftPosition    : 20");
  res.Append("ProposalPlanning*.*InstBtn.leftPosition   : 55");
  res.Append("ProposalPlanning*.*PropCoIList*.fontList : -misc-fixed-bold-r-normal-*-13-*-*");
  res.Append("ProposalPlanning*.*PropCoIList*.visibleItemCount   : 10");






  return res;
}
