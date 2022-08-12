/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PanelCreateApp.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:

* INPUT PARAMETERS:

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include <fstream>
#include <stdlib.h>

#include "connect_db.hh"
#include "prop_dberror.hh"
#include "dbsubcat_list.hh"

#include "File_Utils.hh"
#include "stringFcns.hh"
#include "ObsUtil.hh"
#include "ProposalArray.hh"
#include "PanelArray.hh"

#include "PanelCreateApp.hh"
#include "PanelCreateParameter.hh"
#include "PanelDefs.hh"

#include "find_file.h"

#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>


//-------------------------------------------------
// Constructor
//-------------------------------------------------
PanelCreateApp::PanelCreateApp(char *name) 
	: FW_Application(name) 
{ 


}

//-------------------------------------------------
// Destructor
//-------------------------------------------------
PanelCreateApp::~PanelCreateApp() 
{ 
}

//-------------------------------------------------
// Executes the application
//-------------------------------------------------
FW_Error PanelCreateApp::Execute()
{
  FW_Error error = FW_Error::FW_GOOD;
  string   msg;
  int      retcode;
  int      match;
  int      ii;
  int      prop_cnt = 0;
  string   default_file;
  const char *ao;
  char     *current_ao;
  char     delflag[5];
  Boolean  use_default = FALSE;
  DBSubcat *dbs;
  DBSubcatList dbs_list(xconn);
  PanelArray *newpa = new PanelArray(xconn);
  PanelArray delete_pa(xconn);
  PanelArray pa(xconn);

  current_ao = getenv(PROP_AO_ENV);

  // do they want a copy of the default file?
  default_file = theParameters->GetOutfile();
  if (default_file.length() > 0) {
     cerr << "copying default file to " << default_file << endl;
     CopyDefaultFile(default_file.c_str());
  }

  // do they want to use a file to create panels?
  // if so, go read the file
  default_file = theParameters->GetFilename();
  if (default_file.length() > 0) {
    use_default = TRUE;
    cerr << "reading default panel file " << default_file << endl;
    if (!pa.ReadDefaultPanels((char *)default_file.c_str())) {
      msg = "ERROR:  Unable to read default panel file:\n        ";
      msg.append(default_file);
      msg.append("\n");
      plog->LogMessage(msg.c_str(),TRUE);
      cerr << msg << endl;
      error = FW_Error::FW_BAD;
      return error;
    }
  }

  // delete any existing panels for this AO
  cerr << "loading panels..." << endl;
  if (delete_pa.LoadAllPanels(msg)) {
    if(delete_pa.GetSize() > 0) {
      cerr << "Do you want to delete the current panels for AO ";
      cerr << current_ao << "(Y/N) ? ";
      cerr.flush();
      cin.getline(delflag,sizeof(delflag));
      if (delflag[0] == 'y' || delflag[0] == 'Y') {
        cerr << "deleting panels for AO "<< current_ao << endl;
        if (!delete_pa.DeletePanels()) {
          msg = "Delete failed for panels.";
          cerr << msg << endl;
          plog->LogMessage(msg.c_str(),TRUE);
          msg = delete_pa.GetErrors();
          cerr << msg << endl;
          plog->LogMessage(msg.c_str(),TRUE);
          error = FW_Error::FW_BAD;
          return error;
        }
        else  {
          msg = "Existing panels deleted for AO cycle ";
          msg.append(current_ao);
          msg.append(".");
          plog->LogMessage(msg.c_str(),TRUE);
        }
   
      }
    }
  }
// first create the default panels
  ao = theParameters->GetAO();
  if (use_default || (ao && *ao != '\0')) {
    cerr << "creating default panels " << endl;
    if (!(pa.CreateDefaultPanels(NULL,use_default,(char *)ao))) {
      msg = "ERROR: Panel Creation Failed!\n";
      plog->LogMessage(msg.c_str(),TRUE);
      cerr << msg << endl;
      msg = pa.GetErrors();
      plog->LogMessage(msg.c_str(),FALSE);
      cerr <<  msg << endl;
      error = FW_Error::FW_BAD;
    }
  }

  if (error == FW_Error::FW_GOOD) {
    // now load the new panels from the database
    if (newpa->LoadAllPanels(msg)) {
      msg = "Panels successfully created: ";
      msg.append(stringFcns::itoa(newpa->GetSize()));
      plog->LogMessage(msg.c_str(),TRUE);
      if (theParameters->AssignSQLFiles()) {
        msg = "Proposals automatically assigned to panels using SQL files.";
        plog->LogMessage(msg.c_str(),TRUE);
        cerr << msg << endl;

        vector<int> catcodes;
        vector<int> donecatcodes;
        for (int cidx=0 ; cidx < newpa->GetSize(); cidx++) {
          catcodes.clear();
          for (size_t ss=0;ss<newpa->GetRecordbyIndex(cidx)->GetNbrCategories();
	       ss++) {
            int tcat = newpa->GetRecordbyIndex(cidx)->GetCategoryCode(ss);
            for (size_t jj=0;jj<donecatcodes.size();jj++) {
              if (tcat == donecatcodes[jj]) {
                 tcat = -1;
              } 
            }
            if (tcat > 0) {
              catcodes.push_back(tcat);
              donecatcodes.push_back(tcat);
            }
          }
          if (catcodes.size() > 0) { 
             retcode = newpa->AssignProposalsforCategoryCode(catcodes, NULL, 
	                      prop_cnt,current_ao);
          }
        }
      }
      else if (theParameters->AssignProposals()) {
        msg = "Proposals automatically assigned for new panels by single subject category.";
        plog->LogMessage(msg.c_str(),TRUE);
        cerr << msg << endl;
        // for each subject category, assign proposals to the panels
        retcode = dbs_list.ld_all_subcats(match);
        if (retcode == 0) {
          for (ii=0;ii<match;ii++) {
            dbs = dbs_list.get_subcat(ii);
            if (dbs) {
              vector<int> cat_code;
              cat_code.push_back(dbs->get_catcode());
              retcode = newpa->AssignProposalsforCategoryCode(
			cat_code, dbs->get_description(),
			prop_cnt,current_ao);
              if (retcode == -1) {
                msg = "No panels/proposals available for subject category: ";
                msg.append(dbs->get_description());
                msg.append("\n");
                plog->LogMessage(msg.c_str(),TRUE);
                cerr << msg << endl;
              }
              else if (retcode > 0) {
                msg = "ERROR: Unable to assign proposals for subject category: ";
                msg.append(dbs->get_description());
                msg.append("\n");
                plog->LogMessage(msg.c_str(),TRUE);
                msg = newpa->GetErrors();
                plog->LogMessage(msg.c_str(),TRUE);
                cerr << msg << endl;
              }
            }
          }
        }
        else {
          msg = "ERROR: Unable to load subject category codes from database.\n";
          plog->LogMessage(msg.c_str(),TRUE);
          cerr << "\n" <<  msg;
          msg.clear();
          prop_dberror(xconn,msg);
          plog->LogMessage(msg.c_str(),TRUE);
          cerr << "\n" <<  msg << endl;
        }
      }
      if (prop_cnt > 0) {
        if (newpa)
          delete newpa;
        newpa = new PanelArray(xconn);
        if (!newpa->LoadAllPanels(msg)) {
          msg = "ERROR: Unable to load panel data from database.\n";
          plog->LogMessage(msg.c_str(),TRUE);
          cerr << "\n" <<  msg;
          msg = newpa->GetErrors();
          plog->LogMessage(msg.c_str(),FALSE);
          cerr <<  msg << endl;
        }
        cerr << "calculating allotted times" << endl;
        newpa->CalculateAllottedTimes();
        msg = "Allotted times for panels automatically calculated.";
        plog->LogMessage(msg.c_str(),TRUE);
      }


      newpa->Print(cerr);

      if (theParameters->IsPrint()) {
        ofstream ofile(theParameters->GetListfile(),ios::out); 
        if (ofile.good()) {
          if (getenv("ASCDS_DEBUG"))
            newpa->PrintProposalList(ofile,-1,TRUE);
          else
            newpa->PrintProposalList(ofile);
          ofile.close();
        }
        else {
          msg = "ERROR: Unable to open file for output listing\n       ";
          msg.append(theParameters->GetListfile());
          plog->LogMessage(msg.c_str(),TRUE);
          cerr << "\n" <<  msg << "\n";
        }
      }
    }
    else {
      msg = "ERROR: Unable to load panel data from database.\n";
      plog->LogMessage(msg.c_str(),TRUE);
      cerr << "\n" <<  msg;
      msg = pa.GetErrors();
      plog->LogMessage(msg.c_str(),FALSE);
      cerr <<  msg << endl;
    }
  }
    

  msg = "prop_panel_create completed.";
  plog->LogMessage(msg.c_str(),TRUE);
  return error;
}

//---------------------------------------------------------------------
// The virtual function to allow the developer to perform any internal
// setup functions before executing the application
//---------------------------------------------------------------------
FW_Error PanelCreateApp::Setup() 
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
  logname.append("/panel_create.log");
  plog = new Logger(logname.c_str(),'\n',TRUE,FALSE);
  if (plog->IsReadOnly()) {
    cerr <<  "\nFATAL ERROR! " << "Unable to open ";
    cerr << logname << " log file for writing!\n" << endl ;
    err = FW_Error::FW_BAD;
  }

  else {
    msg = "prop_panel_create  started (logname: ";
    if (getenv("LOGNAME"))
      msg.append(getenv("LOGNAME"));
    msg.append(") ********");
    plog->LogMessage(msg.c_str(),TRUE);
    msg.clear();

    // get the database connection
    // if no connection, create temporary database connection
    xconn= connect_db(msg,theParameters->GetUser(),theParameters->GetPassword(),
	theParameters->GetServer());
    if (!xconn) {
      plog->LogMessage(msg.c_str(),TRUE);
      cerr << "\n" << msg << endl;
      err = FW_Error::FW_BAD;
    }
    else {
      msg = "prop_panel_create  successfully connected to SYBASE.";
      plog->LogMessage(msg.c_str(),TRUE);
    }
  }


  return err;

}

void PanelCreateApp::CopyDefaultFile(const char *fname)
{
  char buffer[FILENAME_MAX];
  char *ptr;
  string  errmsg;

  ptr = getenv("ASCDS_DATA");
  if (find_file(ptr,PANEL_CREATE_DEFAULT_FNAME,R_OK,buffer)) {
      File_Utils fu(buffer);
      if (fu.File_Copy(fname,errmsg) != 0) {
        plog->LogMessage(errmsg.c_str(),TRUE);
        cerr << errmsg << endl;
      }
  }
  return;
}

//-------------------------------------------------
// Pointer to single global instance
//-------------------------------------------------
PanelCreateApp *theApplication = new PanelCreateApp((char *)"Panel Creation");

