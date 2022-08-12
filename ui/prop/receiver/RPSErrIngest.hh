
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSErrIngest.hh
 
* DEVELOPEMENT: ObsCycle
 
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

#ifndef RPSERRINGEST_HH
#define RPSERRINGEST_HH

#include "Logger.hh"
#include "ProposalDefs.hh"
#include "ProposalProcess.hh"
#include "ProposalEntry.hh"
#include "Proposal_Ingest.hh"

class XConnection;

class RPSErrIngest  : public ProposalProcess {

public:
  // Constructor
  RPSErrIngest(Logger *log,const char *pwd,const char *usr);

  // Destructor
  ~RPSErrIngest();

  Boolean NewProposal(string &filename,string  &propno);
  Boolean ResubmitProposal(string  &filename,const char *propno);
  Boolean RetrieveProposal(const char *propno,string &fname);

  const char *  GetErrors()  { return pingest->GetErrors(); };

  Boolean ProcessFile();

protected:

  Boolean do_rename;
};

#endif
