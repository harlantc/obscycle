
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSReceiverFile.hh
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
        This class processes a single incoming proposal file.  It searches
       	for the proposal tag number.  If found, the file is renamed to
	<tag number>.prop and copied to the official ASCDS_PROP_RPS_RECV
    	or ASCDS_PROP_TOO_RECV directory.  If not found or any other 
	errors occur, it is copied to the ASCDS_PROP_RPS_ERR directory.  
	A log file is kept containing the following information when 
	available:

	timestamp,official filename,
	tag number,PI(title,first,middle,last),Proposal Title

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef RPSRECEIVERFILE_HH
#define RPSRECEIVERFILE_HH

#include <string>
#include "Logger.hh"
#include "ProposalDefs.hh"
#include "RPSMailTOO.hh"


class RPSReceiverFile 
{
public:
  // Constructor
  RPSReceiverFile(Logger *log,Boolean isTOO);

  // Destructor
  ~RPSReceiverFile();

  Error_Code GetState() { return err; }

  void ProcessFile(string &filename,int mailFlag);
  string GetTargets();

  // get any obsids 'linked' to the given obsids (monitor,followups,group);
  string GetLinkedObsids(const char *);

  void CheckOverdueStatus() {mail_too->CheckOverdueStatus();}

protected:
  // Objects

  void MailTOO(string &);
  void MailNRATOO(string &);
  void MailDDTParameters();
  void LogKeywords(string &);
  void MailConfirmation(const char *category,const char *title,
        const char *num_target,const char *exp_time,
	const char *email_addr,const char *seqnbr,const char *budget);

  void MoveFile(Boolean,Boolean spam=FALSE);
  void GetKeyValue(char *ptr, string &return_str);
  int  GetNextTagNumber();
  void CreateErrorFile(int tag_number, string &msg);
  string GetCurrentTime();
  char *GetUrgency();
  int     RenameFile(const char *,const char*,string &);


  int    is_mail;          // true, if input is mail file
  int    is_too;           // true, if out-of-cycle TOO, 2 if NRA TOO
  int    is_cost;          // true, if cost proposal
  int    is_cps;           // DDT proposal from CPS  (already in database)
  Logger *log;             // log file for submitted proposal entries
  Logger *proposal_log;    // log file for processed proposals (PI,Title,etc).

  string deleteDir;       // delete directory for input files
  string fname;           // full path for instance of mail file
  string tag_filename;    // filename of tag file (proposal numbers)
			  // this should no longer be needed since 
                          // numbers are assigned during the proposal
			  // process.
  string buffer;          // buffer for reading in some of the file
  int    tag_number;
  string ptype;           // proposal type
  string pri;         // priority for TOO messages
  char   *too_urgency;    // urgency (from priority) used in paging
  RPSMailTOO *mail_too;
  Error_Code err;
  time_t deadline;        // proposal deadline, move to error queue

};

#endif
