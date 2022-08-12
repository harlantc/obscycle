
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSReceiverApp.hh
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
        This application incorporates the new mail for the 
	RPS receiver and copies the incoming mail file 
	to the ASCDS_PROP_RPS_RECV, ASCDS_PROP_TOO_RECV, or 
	ASCDS_PROP_RPS_ERR directories.  In addition a log 
	file is kept of all processed files.
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef RPSRECEIVERAPP_HH
#define RPSRECEIVERAPP_HH

#include <string>
#include "FW_Application.hh"
#include "FW_Error.hh"
#include "Logger.hh"
#include "RPSReceiverFile.hh"

class RPSReceiverApp : public FW_Application
{
public:
  // Constructor
  RPSReceiverApp(const char * name);

  // Destructor
  ~RPSReceiverApp();

  // Executes the application
  virtual FW_Error Execute();

  virtual FW_Error HandleSignal(int);

  void Exit();

protected:
  // Objects

  // The virtual function to allow the developer to perform any internal
  // setup functions before executing the application
  virtual FW_Error Setup();

  // process the files in the mail directory
  void RPS_ProcessMail();
  void RPS_ProcessDir();

  Logger *log;           // log file for the mail processs
  string  logname;       // log file name
  string  dirname;       // mail directory path
  string  indirname;     // input directory name

  RPSReceiverFile *file; // instance for processing individual files
};

// Pointer to single global instance
extern RPSReceiverApp *theApplication;

#endif
