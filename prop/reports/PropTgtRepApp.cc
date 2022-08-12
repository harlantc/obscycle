/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropTgtRepApp.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:

	This application processes requests for various report
        formats for the specified targets. The reports process information
        on a target basis. 


 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include <fstream>
#include <stdlib.h>
#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>


#include "connect_db.hh"

#include "PropTgtRepApp.hh"
#include "PropTgtRepParameter.hh"
#include "ProposalDefs.hh"
#include "TargetArray.hh"
#include "PanelEntry.hh"


//-------------------------------------------------
// Constructor
//-------------------------------------------------
PropTgtRepApp::PropTgtRepApp(const char *name) 
	: FW_Application(name) 
{ 


}

//-------------------------------------------------
// Destructor
//-------------------------------------------------
PropTgtRepApp::~PropTgtRepApp() 
{ 
}

//-------------------------------------------------
// Executes the application
//-------------------------------------------------
FW_Error PropTgtRepApp::Execute()
{
  FW_Error error = FW_Error::FW_GOOD;

  const char *obs_type;
  const char *status;
  const char *tstatus;
  const char *ao;

  const char *fname;
  const char *tfname;
  const char *propno;
  const char *panelid;

  string  errmsg;
  Boolean tstat;
  int     match;
  int     mode = 0;
  Boolean stat = FALSE;
  int     pno;

  ProposalEntry *pe;
  ProposalArray plist;
  TargetArray *tlist;
  
  errmsg.clear();
  
  if (theParameters->IsPI()) 
    mode |= PI_TGT;
  if (theParameters->IsPropNo()) 
    mode |= PROPNO_TGT;

  obs_type = theParameters->GetType();
  status   = theParameters->GetStatus();
  tstatus  = theParameters->GetTgtStatus();
  ao       = theParameters->GetAO();

  tfname   = theParameters->GetTgtFile();
  fname    = theParameters->GetFile();
  propno   = theParameters->GetPropNo();
  panelid  = theParameters->GetPanelId();

  if (tfname != (char*)0 && *tfname != '\0' )  {
    tlist = new TargetArray();
    stat = tlist->LoadByFile(tfname,errmsg,xconn,&match );
    if (stat)
      cerr << "Load succeeded for " << match << " targets.";
  }
  else {
    // user specified proposal number
    if (propno && *propno != '\0') {
      pno = atoi(propno);
      pe = new ProposalEntry(xconn,pno,FALSE);
      if (pe->GetState() == GOOD) {
        plist.Append(pe);
        stat = TRUE;
        match = 1;
      }
      else {
        cerr << "Unable to retrieve proposal: " << propno << endl;
        cerr << pe->GetErrors() << endl;
        error = FW_Error::FW_BAD;
      }
    }
    // user specified file of proposal numbers
    else if (fname  && *fname != '\0') {
      if (plist.LoadProposalsByFile(fname,errmsg,xconn,&match)) {
        stat = TRUE;
      }
      else  {
        cerr << "Load proposals FAILED - database error." << endl;
        cerr << errmsg << endl;
        error = FW_Error::FW_BAD;
      }
    }
    // user specified panel name
    else if (panelid && *panelid != '\0') {
      PanelEntry panel_entry(xconn);
      if (ao && *ao != '\0' )
        tstat = panel_entry.Retrieve(panelid,ao);
      else
        tstat = panel_entry.Retrieve(panelid);

      if (tstat) {
        if (plist.LoadProposalsByPanel(panel_entry.GetPanelId(),errmsg,xconn,
		&match,status,ao) ) {
          stat = TRUE;
        }
        else {
          cerr << "ERROR: Unable to load proposals for panel " << panelid << endl;
          error = FW_Error::FW_BAD;
        }
      }
      else {
        error = FW_Error::FW_BAD;
        cerr << "ERROR: Unable to load panel " << panelid << endl;
        cerr << panel_entry.GetErrors() << endl;
      }
    }
    // user specified all option
    else {
      if (theParameters->IsAll())
        stat = plist.LoadProposalsByAO(errmsg,xconn,&match,ao,status);
      else
        // defaults to all proposed for current cycle
        stat = plist.LoadAllProposals(errmsg,xconn,&match,status,ao) ;
  
      if (!stat) {
        cerr << "Load ALL proposals FAILED - database error." << endl;
        cerr << errmsg << endl;
        error = FW_Error::FW_BAD;
      }
    }

    // if successfully loaded proposals, then sort them
    if (stat) {
      cerr << "Load proposals succeeded for " << match << " proposals." << endl;

      if (obs_type && *obs_type != '\0')
        plist.LimitByType(obs_type);
      if (status && *status != '\0')
        plist.LimitByStatus((char *)status);
      if (ao && *ao != '\0')
        plist.LimitByAO(ao);

      cerr << "Loading targets for specified proposals..." << endl;
      tlist = new TargetArray(&plist,xconn);
      cerr << "Load targets succeeded for " << tlist->GetSize() << " targets." <<endl;
    }
  }

  if (stat) {
    cerr << endl;

    if (tstatus && *tstatus != '\0') 
      tlist->LimitByStatus(tstatus);

    int stype = PROP_SORT_RA_DEC;
    tlist->Sort(&stype,1);
    if (theParameters->IsMP())
      tlist->PrintConfirmationTargets(cout,FALSE);
    else if (theParameters->IsConflicts())
      tlist->PrintConflicts(cout);
    else if (theParameters->IsApproved())
      tlist->PrintApproved(cout,TRUE);
    else
      tlist->Print(cout,mode);
    delete tlist;
  }
  else  {
    error = FW_Error::FW_BAD;
    cerr << "ERROR: Load FAILED." << endl;
    cerr << errmsg << endl;
  }

  delete xconn;
  
  return error;
}

//---------------------------------------------------------------------
// The virtual function to allow the developer to perform any internal
// setup functions before executing the application
//---------------------------------------------------------------------
FW_Error PropTgtRepApp::Setup() 
{ 
  FW_Error err = FW_Error::FW_GOOD;
  string msg;

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

//-------------------------------------------------
// Pointer to single global instance
//-------------------------------------------------
PropTgtRepApp *theApplication = new PropTgtRepApp("Proposal Reports");

