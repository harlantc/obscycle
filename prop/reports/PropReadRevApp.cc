/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME:	PropReadRevApp.cc
 
* DEVELOPMENT:	ObsCycle
 
* DESCRIPTION:  
	This application reads all files found in the specified input 
	directory.   It determines the type of review, parses the information
	and updates the database.  If the file is successfully parsed,
	it moves the processed file to  a subdirectory of the specified
	input directory called "sav". 

* INPUT PARAMETERS: prop_read_reviews 

  -in   <input directory>   OPTIONAL  - path for input review files
                                        Default is ASCDS_PROP_REVIEW
  
  -U [user]       REQUIRED  - proposal database user name
  -S [server]     OPTIONAL  - proposal database server 
                              default is DB_PROP_SQLSRV
  -p              OPTIONAL  - print parameters

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include <fstream>
#include <stdlib.h>
#include <sys/stat.h>
#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>



#include "connect_db.hh"

#include "stringFcns.hh"
#include "File_Utils.hh"
#include "ObsUtil.hh"
#include "PropReadRevApp.hh"
#include "PropReadRevParameter.hh"
#include "ProposalDefs.hh"


//-------------------------------------------------
// Constructor
//-------------------------------------------------
PropReadRevApp::PropReadRevApp(const char *name) 
	: FW_Application(name) 
{ 

  log = NULL;


}

//-------------------------------------------------
// Destructor
//-------------------------------------------------
PropReadRevApp::~PropReadRevApp() 
{ 
  delete xconn;
  delete log;
}

//-------------------------------------------------
// Executes the application
//-------------------------------------------------
FW_Error PropReadRevApp::Execute()
{
  FW_Error error = FW_Error::FW_GOOD;
  string  dirname;
  string  fullpath;
  string  newname;
  string  errmsg;
  string  logname;
  char    buff[2];
  int     good_cnt=0;
  int     bad_cnt=0;
  int     retval;
  size_t  ii;

  // open the log file and append the start message
  logname = (char *)getenv(PROP_LOG_ENV);
  logname.append("/");
  logname.append("prop_read_reviews.log");
  log = new Logger(logname.c_str(),'\n',TRUE,FALSE);

  errmsg = app_name.chars();
  errmsg.append(" started. **********************************");
  log->LogMessage(errmsg.c_str(),TRUE);

  ProposalReview prev(xconn);
  
  dirname = theParameters->GetDirectory();

  File_Utils fu(dirname);
  vector<string> dlist;
  fu.Directory(dlist);

  for (ii=0; ii<dlist.size();ii++) {
    errmsg.clear();
    if ((dlist[ii].compare(0,1,"#",1) != 0) &&
        (dlist[ii].compare(0,1,".",1) !=0) &&
        (dlist[ii].compare(0,3,"sav",3) != 0) && 
        (dlist[ii].compare(0,3,"split",3) != 0) ) {
      fullpath = dirname;
      fullpath.append("/");
      newname = fullpath;
      newname.append("sav/");
      fullpath.append(dlist[ii]);
      newname.append(dlist[ii]);

      ifstream iss(fullpath.c_str(),ios::in);
      if (iss.good()) {
        retval = prev.ReadReviewForm(iss,errmsg); 
        if (retval == -1) {
          log->LogMessage(errmsg.c_str(),TRUE);
          cerr << errmsg << endl;
          if (!theParameters->isOverride()) {
            cerr << "Do you want to overwrite the existing review (y/n) ? " ;
            cerr.flush();
            memset(buff,0,sizeof(buff));
            cin >> buff;
            cerr << "\n";
            if (buff[0] == 'y' || buff[0] == 'Y') { 
              errmsg = "User requested overwrite of existing review.\n";
              log->LogMessage(errmsg.c_str(),TRUE);
              errmsg.clear();
              iss.clear();
              iss.seekg(0);
              retval = prev.ReadReviewForm(iss,errmsg,TRUE); 
            }
            else {
              errmsg = "Review data not overwritten.\n";
            }
          }
          else {
            errmsg.clear();
            iss.clear();
            iss.seekg(0);
            retval = prev.ReadReviewForm(iss,errmsg,TRUE); 
          }
        }
        if (retval == 0) {
           errmsg = "Ingest successful for file ";
           errmsg.append(fullpath);
           log->LogMessage(errmsg.c_str(),TRUE);
           File_Utils ftmp((char *)newname.c_str());
           string xstr = ftmp.File_UniqueName();
           if (rename(fullpath.c_str(),xstr.c_str()) != 0) {
             errmsg = "Error renaming ";
             errmsg.append(fullpath);
             errmsg.append(" to ");
             errmsg.append(xstr);
             errmsg.append("\n  ");
             perror(errmsg.c_str());
             log->LogMessage(errmsg.c_str(),TRUE);
             break;
           }
           good_cnt+=1;
        }
        else {
          string tmp;
          tmp ="ERROR occurred reading  ";
          tmp.append(fullpath);
   	  tmp.append("\n");
          tmp.append(errmsg);
          cerr << tmp << endl;
          log->LogMessage(tmp.c_str(),TRUE);
          bad_cnt += 1;
        }
      }
      else {
        errmsg = "Unable to open file: ";
        errmsg.append(fullpath);
        log->LogMessage(errmsg.c_str(),TRUE);
        cerr << errmsg << endl;
        bad_cnt += 1;
      }
    }
  }


  errmsg = "Successfully ingested ";
  errmsg.append(stringFcns::itoa(good_cnt));
  errmsg.append(" reviews.\n");
  log->LogMessage(errmsg.c_str(),TRUE);
  cerr << errmsg << endl;
  errmsg = "Failed to ingest ";
  errmsg.append(stringFcns::itoa(bad_cnt));
  errmsg.append(" reviews.\n");
  log->LogMessage(errmsg.c_str(),TRUE);
  cerr << errmsg << endl;
  
 
  
  return error;
}

//---------------------------------------------------------------------
// The virtual function to allow the developer to perform any internal
// setup functions before executing the application
//---------------------------------------------------------------------
FW_Error PropReadRevApp::Setup() 
{ 
  FW_Error err = FW_Error::FW_GOOD;
  string  msg;

  static os_helper_toolkit init_helper;
  static os_file_toolkit init_file;
  static os_time_toolkit init_time;
  static os_streaming_toolkit init_stream;
  static os_unix_toolkit init_unix;
  static os_network_toolkit init_network;

  // get the database connection
  // if no connection, create temporary database connection
  xconn= connect_db(msg,theParameters->GetUser(),theParameters->GetPassword(),
	theParameters->GetServer());
  if (!xconn) {
    cerr << "\n" << msg << endl;
    err = FW_Error::FW_BAD;
  }

  return err;

}
//---------------------------------------------------------------------
// print terminated timestamp message to log
//---------------------------------------------------------------------
void PropReadRevApp::Exit()
{
  string  msg;

  msg = app_name.chars();
  msg.append(" terminated. *******************************\n");
  log->LogMessage(msg.c_str(),TRUE);

  log->Close();

}


//-------------------------------------------------
// Pointer to single global instance
//-------------------------------------------------
PropReadRevApp *theApplication = new PropReadRevApp("Proposal Reviews");

