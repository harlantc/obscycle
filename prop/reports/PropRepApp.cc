/*H****************************************************************************
* Copyright (c) 1995,2019 Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropRepApp.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:  This application processes requests for various report
	formats for the specified proposals. The reports process information
 	on a proposal basis not a target basis. 

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include <fstream>
#include <sys/types.h>
#include <grp.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>


#include "connect_db.hh"
#include "PanelEntry.hh"
#include "PanelArray.hh"
#include "ProposalSupFiles.hh"
#include "File_Utils.hh"

#include "PropRepApp.hh"
#include "PropRepParameter.hh"
#include "ProposalDefs.hh"

//-------------------------------------------------
// Constructor
//-------------------------------------------------
PropRepApp::PropRepApp(const char *name) 
	: FW_Application(name) 
{ 

}

//-------------------------------------------------
// Destructor
//-------------------------------------------------
PropRepApp::~PropRepApp() 
{ 
}

//-------------------------------------------------
// Executes the application
//-------------------------------------------------
FW_Error PropRepApp::Execute()
{
  FW_Error error = FW_Error::FW_GOOD;
  string  tmp;
  string  obs_type;
  string  status;
  string  ao;
  string  errmsg;
  string  propno;
  string  filename;
  string  panelid;
  int     match = 0;
  int     pno;
  int     ii;
  Boolean stat;

  ProposalEntry *pe;
  ProposalArray plist;
  
  errmsg.clear();
  
  ao       = theParameters->GetAO();
  status   = theParameters->GetStatus();
  obs_type = theParameters->GetType();

  propno   = theParameters->GetPropNo();
  filename = theParameters->GetFilename();
  panelid  = theParameters->GetPanelId();

  if (theParameters->IsUserConflicts()) {
    error = process_conflicts(panelid,ao);
    return error;
  }
    // user specified proposal number
  if (propno.length() > 0) {
    pno = atoi(propno.c_str());
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
  else if (filename.length() > 0) {
    if (plist.LoadProposalsByFile(filename.c_str(),errmsg,xconn,&match)) {
      stat = TRUE;
    }
    else  {
      cerr << "Load proposals FAILED - database error." << endl;
      cerr << errmsg << endl;
      error = FW_Error::FW_BAD;
    }
  }
  // user specified panel name
  else if (panelid.length() > 0) {
    Boolean tstat;
    PanelEntry panel_entry(xconn);
    if (ao.length() > 0)
      tstat = panel_entry.Retrieve(panelid.c_str(),ao.c_str());
    else
      tstat = panel_entry.Retrieve(panelid.c_str());
    if (tstat) {
      if (plist.LoadProposalsByPanel(panel_entry.GetPanelId(),errmsg,xconn,&match,
		status.c_str(),ao.c_str())) {
        plist.LoadAdditional();
        stat = TRUE;
        if (!theParameters->IsLetter() && !theParameters->IsApproved() && !theParameters->IsSupportingFiles() ) {
          cout << "PANEL "<< panelid << endl;
          cout << "---------------" << endl;
        }
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
      stat = plist.LoadProposalsByAO(errmsg,xconn,&match,ao.c_str(),
	status.c_str());
    else
      stat = plist.LoadAllProposals(errmsg,xconn,&match,
	status.c_str(),ao.c_str()) ;

    if (!stat) {
      cerr << "Load ALL proposals FAILED - database error." << endl;
      cerr << errmsg << endl;
      error = FW_Error::FW_BAD;
    }
    else {
      plist.LoadCoIs(errmsg,xconn);
    }
  }

  // if successfully loaded proposals, then sort them
  if (stat) {
    cerr << "Load proposals succeeded for " << match << " proposals.";
    cerr << endl;
    if (theParameters->IsAbstract())
      plist.Sort(PROP_SORT_PI);
    else if (theParameters->IsAppSummary())
      plist.Sort(PROP_SORT_PI);
    else
      plist.Sort(PROP_SORT_PROPNO);
  }

  if (error == FW_Error::FW_GOOD) {

    // limit proposals by specified parameters of Observation Type, 
    // proposal status and AO
    if (obs_type.length() > 0)
      plist.LimitByType(obs_type.c_str());
    if (status.length() > 0)
      plist.LimitByStatus(status.c_str());
    if (ao.length() > 0)
      plist.LimitByAO(ao.c_str());

    // process the report options
    if (theParameters->IsAbstract()) {
      // only accepted targets
      plist.PrintNasa(cout,obs_type.c_str(),theParameters->TargetStatus());
    }
    if(theParameters->IsTitle()) {
      plist.PrintPanelList(cout,-1,TRUE);
    }
    if (theParameters->IsTechnicalEval()) {
      plist.PrintTechEval(cout,-1);
    }
    if (theParameters->IsPrimaryReviews()) {
      plist.PrintReview(PRI_REV,cout,errmsg,-1,FALSE,panelid.c_str());
    }
    if (theParameters->IsSecondaryReviews()) {
      plist.PrintReview(SEC_REV,cout,errmsg,-1,FALSE,panelid.c_str());
    }
    if (theParameters->IsPeerReview()) {
      plist.PrintReview(PEER_REV,cout,errmsg,-1,FALSE,panelid.c_str());
    }
    if (theParameters->IsFinalReview()) {
      plist.PrintReview(FINAL_REV,cout,errmsg,-1,FALSE,panelid.c_str());
    }
    if (theParameters->IsConfirmation()) {
      plist.Print(cout,-1);
    }
    if (theParameters->IsAppSummary()) {
      for(ii=0;ii<plist.GetSize();ii++) {
        pe = plist.GetRecordbyIndex(ii);
        if (pe->IsApproved()) {
            pe->Retrieve(pe->GetProposalNumber(),FALSE);
            pe->PrintApprovedSummary(cout);
           cout << "\n\n\n";
        }
      }
    }
    if (theParameters->IsSupportingFiles()) {
      strstream stmp;
      time_t secs = time(NULL);
      stmp << (char *)getenv("ASCDS_PROP_DIR");
      stmp << "/" <<  secs  << '\0';
      if (mkdir(stmp.str(),S_IRWXU | S_IRWXG | S_ISGID) != 0
          && errno != EEXIST)  {
        cerr << "Error: Unable to create directory for ";
        cerr << stmp.str() << endl;
        error = FW_Error::FW_BAD;
      }
      ProposalSupFiles *arc = new ProposalSupFiles(theParameters->GetUser(),theParameters->GetPassword());
      for(int ii=0;ii<plist.GetSize() && (error == FW_Error::FW_GOOD);ii++) {
        ProposalEntry *pe = plist.GetRecordbyIndex(ii);
        error = process_supporting_files(arc,stmp.str(),pe);
      }
      if (error == FW_Error::FW_BAD)
        cerr << "Working directory is " << stmp.str() << endl;
      rmdir(stmp.str());
    }
    if (theParameters->IsLetter() || theParameters->IsApproved() || theParameters->IsFairshare()) {
      tmp = (char *)getenv("ASCDS_PROP_DIR");
      tmp.append("/letters");
      if (panelid.length()  > 0) {
        tmp.append("/panel");
        tmp.append(panelid.c_str());
      }
      if (mkdir(tmp.c_str(),S_IRWXU | S_IRWXG) != 0
          && errno != EEXIST)  {
        cerr << "Error: Unable to create directory for ";
        cerr << tmp << endl;
        error = FW_Error::FW_BAD;
        return error;
      }
     
      int mycnt=0;
      for(ii=0;ii<plist.GetSize();ii++) {
        pe = plist.GetRecordbyIndex(ii);
        pe->GetProposalNumber(propno);
        filename = tmp;
        if (panelid.length()  == 0) {
          filename.append("/panel");
          string  xx;
          xx = pe->GetPanelName();
          filename.append(xx.c_str());
        }
        if (mkdir(filename.c_str(),S_IRWXU | S_IRWXG) != 0 
            && errno != EEXIST)  {
          cerr << "Error: Unable to create directory for ";
          cerr << filename << endl;
          error = FW_Error::FW_BAD;
          return error;
        }
        filename.append("/");
        filename.append(propno);
        filename.append(".txt");

        if (theParameters->IsLetter() ||
	    (theParameters->IsApproved() && pe->IsApproved())  ||
	    (theParameters->IsFairshare() && pe->IsApproved())) {
          int mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP;
          ofstream ofile(filename.c_str(),ios::out);
          if (ofile.good()) {
            if (theParameters->IsLetter() || theParameters->IsApproved()) {
              pe->PrintFinalLetter(ofile,stat); 
              cerr << pe->GetErrors();
              ofile << "\n\n";
              pe->PrintReview(FINAL_FMT,ofile,TRUE);
            } else {
              pe->PrintFairshare(ofile); 
            }
            mycnt++;
          }
          ofile.close();
          chmod(filename.c_str(),mode);
        }
      }
      cerr << "Processed " << mycnt << " entries" << endl;
    }
  }

  delete xconn;
  
  return error;
}

//---------------------------------------------------------------------
// The virtual function to allow the developer to perform any internal
// setup functions before executing the application
//---------------------------------------------------------------------
FW_Error PropRepApp::Setup() 
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


FW_Error PropRepApp::process_supporting_files(ProposalSupFiles *arc,
	string stmp,ProposalEntry *pe)
{
  FW_Error error = FW_Error::FW_GOOD;
  string msg,tmp;
  string propno;
  struct group  *grp;
  gid_t  gid = 0;
  grp = getgrnam("prop");
  if (grp == NULL) 
    cerr << "Failed to get gid for group prop" << endl;
  else 
    gid = grp->gr_gid;

  string curdir = get_current_dir_name();

  pe->GetProposalNumber(propno);
  cerr << "Processing " << propno << endl;
  if ( chdir(stmp.c_str()) != 0) {
      error = FW_Error::FW_BAD;
      cerr << "Unable to chdir to " << stmp << " errno=" << errno << endl;
      return error;
  }
  int cnt = arc->Retrieve(propno.c_str());
  chdir(curdir.c_str());
  string basename = "/";
  basename.append(propno);
  basename.append("_");
  basename.insert(0,theParameters->GetOutDir());
  if (getenv("ASCDS_USE_PINAME")) {
    if (pe->GetPIFirst() != NULL)
      basename.append(pe->GetPIFirst(),1);
    if (pe->GetPILast() != NULL)
      basename.append(pe->GetPILast());
    basename.append("_");
    for (size_t pos = 0; pos < basename.length(); ) {
      if (!isascii(basename[pos]))
        basename.erase(pos,1);
      else
        pos++;
    }
    transform(basename.begin(), basename.end(), basename.begin(), ::tolower);
  }


  File_Utils fu(stmp.c_str());
  vector<string> dlist;
  fu.Directory(dlist);
  if ((size_t)cnt != dlist.size()) {
    cerr << "ERROR: Expected " << cnt <<  " files. Found " << dlist.size() << " files" << endl;
    error = FW_Error::FW_BAD;
    return error;
  }
  if (getenv("ASCDS_DEBUG"))
    cerr << propno << ": " << " retrieved " << cnt << "  found "  << dlist.size() << " files" << endl;
  for (size_t dd=0; dd<dlist.size();dd++) {
    tmp=basename;
    if (strstr(dlist[dd].c_str(),"_sj.pdf") > 0) {
      tmp.append("sj.pdf");
    } else if (strstr(dlist[dd].c_str(),"_cv.pdf") > 0) {
      tmp.append("cv.pdf");
    } else if (strstr(dlist[dd].c_str(),"_pc.pdf") > 0) {
      tmp.append("pc.pdf");
    }
    string fromname = stmp;
    fromname.append("/");
    fromname.append(dlist[dd]);
    File_Utils fu;
    mode_t mode=0660;
    msg="";
    if (getenv("ASCDS_DEBUG"))
      cerr << "cp " << fromname << " to " << tmp << endl;
    int retval = fu.File_Copy(tmp.c_str(),msg,mode,fromname.c_str());
     
    if (retval ==0) {
      unlink(fromname.c_str());
      if (gid > 0) chown(tmp.c_str(),-1,gid);
    } else {
      cerr << "rename  failed! Processing STOPPED"  << endl;
      cerr << fromname << " TO " << tmp << endl;
      cerr << msg << endl;
      error = FW_Error::FW_BAD;
      return error;
    }
  }
  if (arc->GetState()== BAD ) {
    cerr << "Unable to retrieve supporting files" << endl;
    error = FW_Error::FW_BAD;
  }
  return error;
}

FW_Error PropRepApp::process_conflicts(string panelid,string ao_str)
{
  FW_Error err = FW_Error::FW_BAD;
  string uconflicts;
  string errmsg;
  char *ao = NULL;


  if (ao_str.length() > 0) {
     ao = (char *)ao_str.c_str();
  }
  else {  // if no ao, use current
    ao = getenv(PROP_AO_ENV);
  }
    
  if (panelid.length() > 0) {
    PanelEntry pe(xconn);
    if (pe.Retrieve(panelid.c_str(),ao)) {
      pe.GetUserConflicts(uconflicts);
      err = FW_Error::FW_GOOD;
    }
  }
  else {
    PanelArray pa(xconn);
    if (pa.LoadAllPanels(errmsg)) {
       pa.GetUserConflicts(uconflicts);
       err = FW_Error::FW_GOOD;
    }
  }
  cout << uconflicts << endl;


  return err;
}

//-------------------------------------------------
// Pointer to single global instance
//-------------------------------------------------
PropRepApp *theApplication = new PropRepApp("Proposal Reports");

