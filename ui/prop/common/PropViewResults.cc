
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PropViewResults class.  


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include "ap_common.hh"

#include <fstream>
#include <stdio.h>
#include <string>
#include <sys/stat.h>

#include "ScrollText.hh"
#include "PushButton.hh"
#include "XConnection.hh"
#include "ObsUtil.hh"
#include "ProposalDefs.hh"
#include "PropHelpCodes.h"

#include "PropViewResults.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropViewResults::PropViewResults ( int active,const char *name,Boolean findBtn)
        : ViewFileCmd ((char *)name,active,True,RPSERR_VIEW_HTML,
	                  False,0,NULL,TRUE,False,False,findBtn)

{
  printcmd = NULL;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropViewResults::~PropViewResults()
{
  if (printcmd) {
     delete printcmd;
  }
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PropViewResults::SetUp()
{
  string pfilename;
  string tmp;
  char  ofilename[FILENAME_MAX];
  string pwd;

  gc_apply_w->SetLabel((char *)"Print...");

  GetCommand();
  
  get_tmppath(tmp);
  sprintf(ofilename,"%s/propXXXXXXXX",tmp.c_str());
  int filedes = mkstemp(ofilename);
  if (filedes > 0) {
    close(filedes);
  }

  cmd += " -o ";
  cmd += ofilename;

  if (xconn) {
    pfilename = getenv(PROP_LOG_ENV);
    pfilename += "/.pgui_";
    pfilename += getenv("LOGNAME");
    ofstream pfile (pfilename.c_str(),ios::out);
    pfile << xconn->get_password(); 
    pfile.close(); 
    chmod(pfilename.c_str(),0600);
  
  
    cmd += " -U ";
    cmd += xconn->get_user();
    cmd += " -q ";
    cmd += pfilename;
    cmd += " -S ";
    cmd += xconn->get_server();
    
  }
  if (ao.length() > 0) {
    cmd += " -a ";
    cmd += ao;
  }
  if (pstatus.length() > 0) {
    cmd += " -s ";
    cmd += pstatus;
  }
  
  if (getenv("ASCDS_PROP_TEST_MODE")) {
    cerr << cmd << endl;
  }

  system(cmd.c_str());
  if (pfilename.length() > 0) {
    unlink(pfilename.c_str());
  }
  SetFilename(ofilename);
  DisplayFile();

  unlink(ofilename);
}

void PropViewResults::apply_cb(void *)
{
  char tname[FILENAME_MAX];
  string  str;
  string  tmp;

  get_tmppath(tmp);
  sprintf(tname,"%s/propXXXXXXXX",tmp.c_str());
  int filedes = mkstemp(tname);
  if (filedes > 0) {
    close(filedes);
  }

  ofstream ofile(tname,ios::out);
  ofile << st->Get();
  ofile.close();

  if (!printcmd) {
    printcmd = new PropPrintFile(True,NULL,0);
  }
  printcmd->SetFilename((const char *)tname,TRUE);
  printcmd->execute();


}

void PropViewResults::GetCommand()
{
}

