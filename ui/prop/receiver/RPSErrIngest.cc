/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSErrIngest.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:

	This class processes a single proposal file for ingestion into
	the Proposal database.  It attempts to verify that the file is
	a new proposal and not a resubmission.  If any proposals are
	retrieved from the database with the same PI and/or Proposal
	Title, the file is moved to the error queue for human intervention.
	Otherwise, a proposal number based on the category is assigned and
	the proposal is ingested into the database.  A confirmation 
	message with the assigned proposal number is e-mailed to the
	PI and/or Observing Investigator.  


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

#include "File_Utils.hh"

// database include files
#include "dbclt.hh"
#include "dbproposal_list.hh"
#include "ProposalEntry.hh"
#include "Proposal_Ingest.hh"

// proposal
#include "RPSErrParameter.hh"
#include "RPSErrIngest.hh"
#include "RPSErrApp.hh"

// -----------------------------------------------------------------
// Constructor
// -----------------------------------------------------------------
RPSErrIngest::RPSErrIngest(Logger *rpslog,const char *pwd,const char *usr)
	: ProposalProcess(usr,pwd,rpslog)
{

 do_rename=TRUE;
}

// -----------------------------------------------------------------
// Destructor
// -----------------------------------------------------------------
RPSErrIngest::~RPSErrIngest()
{
}

// -----------------------------------------------------------------
// assign new proposal number 
// -----------------------------------------------------------------
Boolean RPSErrIngest::NewProposal(string &filename,string  &propno)
{
  string   msg;
  Boolean  stat = FALSE;


  propno.clear();
  do_rename = TRUE;

  if (err != GOOD) return stat;

  // delete the previous proposal entry
  delete proposal;
  proposal = NULL;

  // initialize values
  fname  = filename;

  // read in at least the first 4096 bytes of the file
  // this should be enough to find the PI and Title
  // if not, then something is wrong with the file(wrong format....)
  ifstream infile(fname.c_str(),ios::in);
  msg = "RPSErrIngest::NewProposal -  Processing " ;
  msg.append(fname.c_str());
  log->LogMessage(msg.c_str(),TRUE);
  if (infile.good()) {
    memset(buffer,0,sizeof(buffer));
    infile.read(buffer,sizeof(buffer)-1); 
    infile.close();

    if (GetKeywords()) {
      // proposal number
      proposal = new ProposalEntry(ao,(char *)category.c_str(),xc);
      if (proposal->GetState() == GOOD) {
        proposal->GetProposalNumber(propno);
        stat = TRUE;
      }
      else {
        err = BAD;
        msg = proposal->GetErrors();
        log->LogMessage(msg.c_str(),TRUE);
      }
    }
    else {
        err = BAD;
        msg="Unable to retrieve keyword fields for ";
        msg.append(fname);
        log->LogMessage(msg.c_str(),TRUE);
    }
  }
  else {
    // if we can't open, we can't rename so don't even try to move it
    // to the error queue.
    err = BAD;
    msg = "ERROR: Unable to open and read file: ";
    msg.append(fname);
    log->LogMessage(msg.c_str(),TRUE);
  }

  return stat;
}

// -----------------------------------------------------------------
// -----------------------------------------------------------------
Boolean RPSErrIngest::ResubmitProposal(string &filename,const char *propno)
{
  int      pno = 0;
  Boolean  stat = FALSE;
  string   rootname;
  string   msg;

  // delete the previous proposal entry
  delete proposal;
  proposal = NULL;

  // initialize values
  fname  = filename;
  File_Utils fu(filename);
  rootname = fu.File_GetRootName();
  msg = "RPSErrIngest::ResubmitProposal -  Processing " ;
  msg.append(filename);
  log->LogMessage(msg.c_str(),TRUE);
  
  if (strcmp(rootname.c_str(),propno) == 0)
    do_rename = FALSE;
  else
    do_rename = TRUE;

  pno = atoi(propno);
  proposal = new ProposalEntry(theApplication->GetDBConnection(), pno);
  if (proposal->GetState() == GOOD) {
    msg = "RPSErrIngest::ResubmitProposal -  state GOOD" ;
    log->LogMessage(msg.c_str(),TRUE);
     stat = TRUE;
  }
  else {
    delete proposal;
    proposal = new ProposalEntry((char *)propno);
    msg = "RPSErrIngest::ResubmitProposal -  using propno " ;
    msg.append(propno);
    log->LogMessage(msg.c_str(),TRUE);
  }
	
  return stat;
}

// -----------------------------------------------------------------
// Process one file from the mail inbox.  
// -----------------------------------------------------------------
Boolean RPSErrIngest::ProcessFile()
{
  string   msg;
  Boolean  stat = FALSE;


  if (proposal->GetState() == GOOD) {
    if (do_rename) 
      stat = RenameToProposalNumber(); 
    else 
      stat = TRUE;
    if (stat)
      if ((stat = IngestDB())) 
	stat = NotifyPI();
  }
  else {
    stat = False;
    err = BAD;
    msg = proposal->GetErrors();
    log->LogMessage(msg.c_str(),TRUE);
  }

  return stat;
}

// -------------------------------------------------------------
// arc4gl retrieve of specified proposal
// -------------------------------------------------------------
Boolean RPSErrIngest::RetrieveProposal(const char *propno,string &pfname)
{
  string  pfile;
  Boolean retval = FALSE;

  if (propno) {
    pfile = propno;
    pfile.append(RPS_PROPOSAL_EXT);

    if (pingest->Retrieve((char *)pfile.c_str(),pfname) == 0)
       retval = TRUE;
  }

  return retval;
  
}

