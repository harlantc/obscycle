

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the GUI Proposal Planning -
        PANEL MANAGER application.



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


#include "PanelApp.hh"
#include "PanelParameter.hh"
#include "PanelMenuWindow.hh"
#include "PanelDefs.hh"
#include "PanelViewCoIs.hh"

#include "StatusAreaList.hh"
#include "ProposalDefs.hh"



PanelApp *theApplication = new PanelApp((char *)"ProposalPlanning");

PanelMenuWindow *thePanelWindow = new PanelMenuWindow((char *)"Proposal - Panel Manager");

// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelApp :: PanelApp(char *name) : GFW_Application (name)
{
  
  xconn = NULL;
  plog  = NULL;
  movelog  = NULL;
  pw    = NULL;


}

// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelApp :: ~PanelApp()
{
  delete xconn;
  delete plog;
  delete movelog;
  delete pw;
  delete thePanelWindow;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
FW_Error PanelApp::MySetup()
{
  FW_Error err = FW_Error::FW_GOOD;
  string  msg;
  string  mname;

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
  logname.append(PANEL_LOG_FILENAME);
  plog = new Logger(logname.c_str(),'\n',TRUE,FALSE);
  if (plog->IsReadOnly()) {
    cerr <<  "\nFATAL ERROR! " << "Unable to open ";
    cerr << logname << " log file for writing!\n" << endl ;
    err = FW_Error::FW_BAD;
  }

  else {
    msg = "*** prop_panel_gui  ";
    msg += "  LOGNAME: ";
    if (getenv("LOGNAME"))
      msg.append(getenv("LOGNAME"));
    msg += "   VERSION:  ";
    if (getenv("ASCDS_VERSION"))
      msg.append(getenv("ASCDS_VERSION"));

    plog->LogMessage(msg.c_str(),TRUE);
    msg.clear();
  }

  /* setup moved proposal log */
  mname = (char *)getenv(PROP_LOG_ENV);
  mname.append("/moved_proposals.log");
  movelog = new Logger(mname.c_str(),'\n',TRUE,FALSE);
  msg = "chmod 660 ";
  msg.append(mname);
  msg.append(" 2>>/dev/null");
  system(msg.c_str());
  msg.clear();
  


  return err;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelApp::ConnectDB(char *pwd,char *user)
{
  string  msg;

  passwd = pwd;
  if (!user)
    dbuser = theParameters->GetUser();
  else
    dbuser = user;


  // get the database connection
  // if no connection, create temporary database connection
  xconn= connect_db(msg,dbuser.c_str(),passwd.c_str(),theParameters->GetServer());
  if (!xconn) {
    Log(msg);
    cerr << "\n" << msg << endl;
    exit(0);
  }
  else {
    msg =  "prop_panel_gui  successfully connected to "; 
    msg +=  theParameters->GetServer();
    Log(msg);
  }
}

// ----------------------------------------------------------
// ----------------------------------------------------------
FW_Error PanelApp::PostMySetup()
{
  FW_Error err = FW_Error::FW_GOOD;
  string usr;
  Boolean needuser = FALSE;
 
  usr = theParameters->GetUser();
  if (usr.length() == 0)
    needuser = TRUE;


  thePanelWindow->iconify();
  pw = new PanelPasswordCmd((char *)"Database Password",TRUE,base_w,needuser);
  pw->execute();


  ((PanelViewCoIs *)(thePanelWindow->view_cois_cmd()))->SetMain(thePanelWindow->baseWidget());

  return err;
}

// ----------------------------------------------------------
// Log routines
// ----------------------------------------------------------
void PanelApp::Log(char *msg,Boolean tstamp)
{
  Log((const char *)msg,tstamp);
}
void PanelApp::Log(string &msg,Boolean tstamp)
{
  Log(msg.c_str(),tstamp);
}
void PanelApp::Log(const char *msg,Boolean tstamp)
{

  if (msg) {
    plog->LogMessage(msg,tstamp);
  }
}

void PanelApp::MoveLog(string &msg,Boolean tstamp)
{
  movelog->LogMessage(msg.c_str(),tstamp);
}

// ----------------------------------------------------------
// ----------------------------------------------------------
StringsList PanelApp :: SetMyAppResources()
{
  StringsList res;

  res.Append("ProposalPlanning.*.foreground       : black");
  res.Append("ProposalPlanning.*.background       : LightSteelBlue2");
  res.Append("ProposalPlanning*.selectColor : Yellow");
  res.Append("ProposalPlanning.*.fontList : -adobe-courier-bold-r-normal-*-14-*-*");
  res.Append( "ProposalPlanning*.HeaderLabel.fontList    : -adobe-courier-bold-r-normal-*-14-*-*");
  res.Append("ProposalPlanning*.SortList*.background      : LightSteelBlue3");
  res.Append("ProposalPlanning*.SortRC.packing            : XmPACK_COLUMN");
  res.Append("ProposalPlanning*.SortRC.numColumns         :  3");
  res.Append("ProposalPlanning*.SortList.visibleItemCount :  6");


  res.Append("ProposalPlanning*.PanelCurrent.set : True");
  res.Append("ProposalPlanning*.*PanelProposalForm.width : 600");
  res.Append("ProposalPlanning*.*PanelProposalList*.listSpacing : 2");
  res.Append("ProposalPlanning*.*PanelProposalList*.borderWidth : 2");
  res.Append("ProposalPlanning*.*PanelProposalList*.fontList    : -misc-fixed-bold-r-normal--13-*");
  res.Append("ProposalPlanning*.*PanelProposalList*.visibleItemCount   : 15");
  res.Append("ProposalPlanning*.*PanelPanelForm*.background : LightSteelBlue3");
  res.Append("ProposalPlanning*.PanelForm.width : 1000");
  res.Append("ProposalPlanning*.*PanelUserList*.listSpacing : 2");
  res.Append("ProposalPlanning*.*PanelUserList*.borderWidth : 2");
  res.Append("ProposalPlanning*.*PanelUserList*.fontList        : -misc-fixed-bold-r-normal--13-*");
  res.Append("ProposalPlanning*.*PanelUserList*.visibleItemCount   : 15");
  res.Append("ProposalPlanning*.*PanelList*.fontList            : -misc-fixed-bold-r-normal--13-*");
  res.Append("ProposalPlanning*.*PanelList*.visibleItemCount   : 8");
  res.Append("ProposalPlanning*.*PanelPropList*.fontList        : -misc-fixed-bold-r-normal--13-*");
  res.Append("ProposalPlanning*.*PanelPropList*.visibleItemCount   : 15");
  res.Append("ProposalPlanning*.*PanelMemberForm.width      : 1000");
  res.Append("ProposalPlanning*.*PanelMemberList*.visibleItemCount : 5");
  res.Append("ProposalPlanning*.PanelMatrix.*selectedBackground  : LightGray");
  res.Append("ProposalPlanning*.PanelMatrix.*selectedForeground  : Black");
  res.Append("ProposalPlanning*.PanelMatrix.oddRowBackground     : LemonChiffon");
  res.Append("ProposalPlanning*.PanelMatrix.evenRowBackground    : LightSteelBlue1");
  res.Append("ProposalPlanning*.PanelMatrix.leftOffset           : 25");
  res.Append("ProposalPlanning*.PanelMatrix.labelFont : -adobe-courier-bold-r-normal-*-14-*-*");
  res.Append("ProposalPlanning*.PanelMatrix.fontList : -adobe-courier-bold-r-normal-*-14-*-*");
  res.Append("ProposalPlanning*.PanelCreateForm.width            : 500");
  res.Append("ProposalPlanning*.*LabelText*.shadowThickness : 0");
  res.Append("ProposalPlanning*.*LabelText*.highlightThickness : 0");
  res.Append("ProposalPlanning*.*LabelText*.borderWidth : 0");
  res.Append("ProposalPlanning*.*EmailRC*.numColumns: 3");
  res.Append("ProposalPlanning*.*EmailRC*.packing: XmPACK_TIGHT");
  res.Append("ProposalPlanning*.StatusArea*background : LightSteelBlue3");
  res.Append("ProposalPlanning*.QuickHelp*background  : LightSteelBlue3");
  res.Append("ProposalPlanning*.*PropCoIForm.width     : 650");
  res.Append("ProposalPlanning*.*CoIBtn.leftPosition    : 20");
  res.Append("ProposalPlanning*.*InstBtn.leftPosition   : 55");
  res.Append("ProposalPlanning*.*PropCoIList*.fontList : -misc-fixed-bold-r-normal-*-13-*-*");
  res.Append("ProposalPlanning*PropCoIList*.visibleItemCount   : 20");
  res.Append("ProposalPlanning*.*ListLabel*.fontList : -misc-fixed-bold-r-normal-*-13-*-*");
  res.Append("*renderTable: DEF,RED");
  res.Append("*DEF.fontType: FONT_IS_FONT");
  //res.Append("*DEF.fontName: -adobe-courier-bold-r-normal-*-14-*-*");
  //res.Append("*DEF.fontName: -misc-fixed-bold-r-normal-*-15-*-*");
  res.Append("*DEF.fontName: -misc-fixed-bold-r-normal-*-13-*-*");
  res.Append("*DEF.renditionForeground: XmUNSPECIFIED_PIXEL");
  res.Append("*DEF.renditionBackground: XmUNSPECIFIED_PIXEL");
  res.Append("*RED.fontType: XmAS_IS");
  res.Append("*RED.fontName: XmAS_IS");
  res.Append("*RED.renditionForeground: #dd0000");
  res.Append("*RED.renditionBackground: XmUNSPECIFIED_PIXEL");
  res.Append("ProposalPlanning*.Automatic.set         : False");




  return res;
}
