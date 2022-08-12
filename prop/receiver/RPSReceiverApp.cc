/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSReceiverApp.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:

        This application incorporates the new mail for the
        RPS receiver and copies the incoming mail file
        to the ASCDS_PROP_RPS_RECV or ASCDS_PROP_RPS_ERR
        directories.  In addition a log file is kept of
        all processed files.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include <fstream>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string>
#include <vector>

#include "RPSReceiverApp.hh"
#include "RPSReceiverParameter.hh"
#include "File_Utils.hh"
#include "ProposalDefs.hh"

//-------------------------------------------------
// Constructor
//-------------------------------------------------
RPSReceiverApp::RPSReceiverApp(const char * name) 
	: FW_Application(name) 
{ 
  log  = NULL;
  file = NULL;
}

//-------------------------------------------------
// Destructor
//-------------------------------------------------
RPSReceiverApp::~RPSReceiverApp() 
{ 
  if (log) 
    delete log;

  if (file)
    delete file;

}

//-------------------------------------------------
// Executes the application
//-------------------------------------------------
FW_Error RPSReceiverApp::Execute()
{
  FW_Error  error = FW_Error::FW_GOOD;
  int       duration;
  string   msg;
  FILE      *fd;
  char      buff[1000];
 

  
  // open the log file and append the start message
  logname = (char *)getenv(RPS_LOG_ENV);
  logname.append("/");
  if (!theParameters->IsTOO()) {
    logname.append(RPS_LOG_FILENAME);
    indirname = (char *)getenv("ASCDS_PROP_IN_RECV");
  }
  else {
    logname.append(RPS_TOO_LOG_FILENAME);
    indirname = (char *)getenv("ASCDS_PROP_IN_TOO_RECV");
  }
  log = new Logger(logname.c_str(),'\n',TRUE);
  
  msg = "*** ";
  msg = app_name.chars();
  msg += "  LOGNAME: ";
  if (getenv("LOGNAME"))
    msg.append(getenv("LOGNAME"));
  msg += "   VERSION:  ";
  if (getenv("ASCDS_VERSION"))
    msg.append(getenv("ASCDS_VERSION"));


  log->LogMessage(msg.c_str(),TRUE);


  // get the input filename and read the file
  duration    = theParameters->GetDuration();

  // get the mail directory path
  dirname = (char *)getenv("HOME");
  dirname.append(RPS_MAIL_DIR);

  // forever
  while (error==FW_Error::FW_GOOD) {

    // initiate class to process individual files
    file = new RPSReceiverFile(log,theParameters->IsTOO());
    if (file->GetState() != GOOD) {
      error = FW_Error::FW_BAD;
      break;
    }

    // incorporate mail - the output from this command is appended to the log
    msg.clear();
    fd = popen("prop_rpsinc.pl","r");
    if (fd) {
       memset(buff,0,sizeof(buff));
       while (fgets( buff,sizeof(buff),fd) != NULL)
         msg.append(buff);
       pclose(fd);
    }

    // only log the message if mail was found.  Otherwise the log
    // file would get too large!
    if (strstr(msg.c_str(),"no mail to incorporate") == 0 &&
        msg.length() != 0) {
      log->LogMessage(msg.c_str(),TRUE);
    }

    // always check the mail directory.  They may have stopped the
    // process before all mail was processed on a previous run.
    RPS_ProcessMail();
    if (file->GetState() != GOOD)
      error = FW_Error::FW_BAD;
    RPS_ProcessDir();
    if (file->GetState() != GOOD)
      error = FW_Error::FW_BAD;
   
    // check for overdue status receipt for TOO messages
    if (theParameters->IsTOO())
      file->CheckOverdueStatus();
    // clean up and wait for the specified duration
    delete file;

    sleep (duration);
  }

  if (error == FW_Error::FW_GOOD) {
  }
  else {
    msg = "FATAL ERROR OCCURRED.";
    log->LogMessage(msg.c_str(),TRUE);
    Exit();

  }

  return error;
}

//---------------------------------------------------------------------
// The virtual function to allow the developer to perform any internal
// setup functions before executing the application
//---------------------------------------------------------------------
FW_Error RPSReceiverApp::Setup() 
{ 
  return FW_Error::FW_GOOD;
}

//-------------------------------------------------
// read in the mail directory and process each
// file found.  Ignore all .* files or files that
// begin with '#'.
//-------------------------------------------------
void RPSReceiverApp::RPS_ProcessMail()
{
  string   msg;
  string   fullpath;

  File_Utils fu(dirname);
  vector<string>dlist;
  fu.Directory(dlist);

  for (size_t ii=0;ii<dlist.size();ii++) {
    if ((dlist[ii].at(0) != '#') &&
        (dlist[ii].at(0) != '.') &&
        (dlist[ii].at(0) != ',') ) {
      fullpath = dirname;
      fullpath.append("/");
      fullpath.append(dlist[ii]);

      // process receipt of this file
      file->ProcessFile(fullpath,1);
    }
  }

}
//-------------------------------------------------
// process all files in the pre receiver queue
//-------------------------------------------------
void RPSReceiverApp::RPS_ProcessDir()
{
  string   msg;
  string   fullpath;
  struct  stat sbuf;
  time_t  now;


  File_Utils fu(indirname);
  vector<string>dlist;
  fu.Directory(dlist);

  for (size_t ii=0;ii<dlist.size();ii++) {
    if ((dlist[ii].at(0) != '.')) {
      fullpath = indirname;
      fullpath.append("/");
      fullpath.append(dlist[ii]);
     
      // make sure file at least a minute old so we don't clash
      if (stat(fullpath.c_str(),&sbuf) == 0)  {
        now = time(NULL);
        if ((now - sbuf.st_mtime) > 60)   {
          // try to find the tag number in the file and move
          // the file to the appropriate RPS proposal directory
          file->ProcessFile(fullpath,0);
        }
      }
    }
  }

}


//---------------------------------------------------------------------
// virtual function for processing signals
//---------------------------------------------------------------------
FW_Error RPSReceiverApp::HandleSignal(int sig)
{
  theApplication->Exit(); 
  return FW_Error::FW_BAD;
}

//---------------------------------------------------------------------
// print terminated timestamp message to log
//---------------------------------------------------------------------
void RPSReceiverApp::Exit()
{
  string msg;

  msg = app_name.chars();
  msg.append(" terminated. *******************************\n");
  log->LogMessage(msg.c_str(),TRUE);

  log->Close();

  // mail message to someone
  
  msg = "prop_rps_receiver_died.pl ";
  msg.append(logname.c_str());
  system(msg.c_str());
}
//-------------------------------------------------
// Pointer to single global instance
//-------------------------------------------------
RPSReceiverApp *theApplication = new RPSReceiverApp("RPS Receiver");


