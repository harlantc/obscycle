/*H****************************************************************************
* Copyright (c) 1995-2019, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSReceiverFile.cc
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:

	This class processes a single incoming proposal file.  It searches
        for the proposal tag number.  If found, the file is renamed to
        <tag number>.prop and copied to the official ASCDS_PROP_RPS_RECV
        directory.  If not found or any other errors occur, it is
        copied to the ASCDS_PROP_RPS_ERR directory.  A log file is
        kept containing the following information when available:

        timestamp,official filename,
        tag number,PI(title,first,middle,last),Proposal Title


 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include <ap_common.hh>
#include <sys/types.h>
#include <sys/stat.h>
#include <fstream>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <iomanip>
#include <vector>

#include "time_convert.h"
#include "stringFcns.hh"
#include "RPSReceiverFile.hh"
#include "File_Utils.hh"
#include "ObsUtil.hh"
#include "ProposalDefs.hh"


// -----------------------------------------------------------------
// Constructor
// -----------------------------------------------------------------
RPSReceiverFile::RPSReceiverFile(Logger *rpslog,Boolean isTOO)
{
  string logname;
  string msg;
  
  is_too = isTOO;
  log = rpslog;   // general log for executing process
  pri.clear();

  // create class for mail/paging requirements
  mail_too = new RPSMailTOO(log);
  err = GOOD;

  // open log for processed proposal data
  logname = (char *)getenv(RPS_LOG_ENV);
  logname.append("/");
  if (!is_too)
    logname.append(RPS_PROPLOG_FILENAME);
  else
    logname.append(RPS_TOO_PROPLOG_FILENAME);
  proposal_log = new Logger(logname.c_str(),'\n',TRUE,FALSE);
  if (proposal_log->IsReadOnly()) {
    msg = "FATAL ERROR! prop_rps_receiver unable to open ";
    msg.append(logname.c_str());
    msg.append(" log file for writing!\n");
    log->LogMessage(msg.c_str(),TRUE);
    err = BAD;
  }


  // make sure we can access the file containing the tag number.
  // If it doesn't exist, create it.
  if (!is_too)
    tag_number = 10000;  // skip past any numbers that may be assigned
                         // by RPS
  else
    tag_number = 7999;  // currently all TOO's,DDT's will be between
                        // 8000 and 10000 
  tag_filename = (char *)getenv(RPS_LOG_ENV);
  tag_filename.append("/");
  if (!is_too)
    tag_filename.append(RPS_TAG_FILE);
  else
    tag_filename.append(RPS_TOO_TAG_FILE);

  if (access(tag_filename.c_str(),F_OK) != 0) {
    msg = "Unable to open file ";
    msg.append( tag_filename);
    msg.append(" \nAttempting to create with initial value of ");
    msg.append(stringFcns::itoa(tag_number));
    msg.append(".");
    log->LogMessage(msg.c_str(),TRUE);
    ofstream outfile(tag_filename.c_str(),ios::out );
    if (outfile.good()  ) {
      outfile <<  tag_number;
      outfile.close();
    }
    else {
      msg = "ERROR: Unable to create file \n";
      msg.append(tag_filename.c_str());
      log->LogMessage(msg.c_str(),TRUE);
      err = BAD;
      if (is_too)
        mail_too->ReadError(tag_filename.c_str());
    }
  }  

}

// -----------------------------------------------------------------
// Destructor
// -----------------------------------------------------------------
RPSReceiverFile::~RPSReceiverFile()
{
  delete proposal_log;
  delete mail_too;
}


// -----------------------------------------------------------------
// Process one file from the mail inbox.  
// -----------------------------------------------------------------
void RPSReceiverFile::ProcessFile(string &filename,int mailFlag)
{
  string  msg;
  char     *ptr,*ptr2;
  char    tbuf[4000];
  string  tmp;
  char    statusUrgency[100];
  string  cmtOnly;
  int     cmtFlag = 0;

  if (err != GOOD) return;

  // initialize values
  fname       = filename;
  tag_number  = 0;
  too_urgency = (char *)"";
  is_cost = 0;
  is_cps = 0;
  is_mail= mailFlag;

  // read in the file to find the PI, Title, and tag number
  ifstream infile(fname.c_str(),ios::in);
  if (infile.good()) {
    buffer.clear();
    while (!infile.eof()) {
       memset(tbuf,0,sizeof(tbuf));
       infile.read(tbuf,sizeof(tbuf)-1); 
       buffer += tbuf;
    }
    infile.close();
 
    // first check for CPS confirmation message
    if (strstr(fname.c_str(),"_confirm.prop") )  {
      MailDDTParameters();
      return;
    }
    
    // get proposal number if already assigned
    ptr = (char *)strstr(buffer.c_str(),RPS_TAG_KEY);
    if (ptr) {
      GetKeyValue(ptr,tmp);
      tag_number = atoi(tmp.c_str());
    }
    ptr = (char *)strstr(buffer.c_str(),RPS_TYPE_KEY);
    if (ptr) {
      GetKeyValue(ptr,ptype);
    }
    ptr = (char *)strstr(buffer.c_str(),"SUBMISSION[");
    if (ptr) {
      GetKeyValue(ptr,tmp);
      if (strstr(tmp.c_str(),"CPS"))
         is_cps=1;
    }

    // determine type of TOO and the urgency of the TOO 
    // then fill in the "nice" format for urgency
    if (is_too) {
      ptr = (char *)strstr(buffer.c_str(),RPS_URGENT_KEY);
      if (!ptr) {
         ptr = (char *)strstr(buffer.c_str(),"Paging urgency ");
         if (ptr) {
           strcpy(statusUrgency,RPS_URGENT_KEY);
           strcat(statusUrgency,"=");
           ptr += strlen("Paging urgency ");
           strncat(statusUrgency,ptr,strlen("Paging urgency "));
           ptr = &statusUrgency[0];
         }
      }
         
      if (ptr) {
        GetKeyValue(ptr,pri);
        log->LogMessage(pri.c_str(),TRUE);
        // determine the urgency of the TOO and fill in the "nice" format
        too_urgency = GetUrgency();
        if (too_urgency == NULL) 
          too_urgency = (char *)"";
        log->LogMessage(too_urgency,TRUE);
      }
      if (getenv("ASCDS_PROP_CMTONLY")) {
        cmtOnly = getenv("ASCDS_PROP_CMTONLY");
      }
      else {
           cmtOnly = "*Comment Update Only*";
      }
      ptr = (char *)strstr(buffer.c_str(),cmtOnly.c_str());
      if (ptr) {
        // now override the urgency for these messages 
        log->LogMessage("overriding  urgency to slow for TOO Status Comment Update ",TRUE);
        if (strcasecmp(too_urgency,RPS_TOO_NONTRANSIENT) != 0) {
          too_urgency = (char *)RPS_TOO_SLOW;
        }
        cmtFlag = 1;
      }

      // Trigger TOO
      ptr = (char *)strstr(buffer.c_str(),RPS_OBSCAT_KEY);
      if (ptr) {
        is_too = IS_TRIGGER;
        GetKeyValue(ptr,tmp);
        tag_number = atoi(tmp.c_str());  // use sequence number instead of proposal nbr
      }
      else  {
        ptr = (char *)strstr(buffer.c_str(),"DDT.CYCLE");
        if (ptr) {
          if (strcasecmp(too_urgency,RPS_TOO_SLOW) == 0 || 
              strcasecmp(too_urgency,RPS_TOO_NONTRANSIENT) == 0) 
            is_too = IS_DDT;
          else 
            is_too= IS_TOO;
        }
        else if ((strstr(buffer.c_str(),"a Peer-reviewed TOO Trigger request")) ||
                 (strstr(buffer.c_str(),"NRA TOO request")) ) {
          is_too= IS_TRIGGER;
        }
        else if ((strstr(buffer.c_str(),RPS_DDT_SUBJ_LINE) == 0) &&
	    (strstr(buffer.c_str(),RPS_TOO_STATUS_LINE) == 0) &&
            (strstr(buffer.c_str(),RPS_TOO_SUBJ_LINE) == 0) ) {
          is_too = IS_UNKNOWN;
        }
        else if (strcasecmp(too_urgency,RPS_TOO_SLOW) == 0   ||
                 strcasecmp(too_urgency,RPS_TOO_NONTRANSIENT) == 0) {
          is_too = IS_DDT;
        }
        else {
          is_too = IS_TOO;
        }
      }
    }

    ptr = (char *)strstr(buffer.c_str(),"From:");
    ptr2 = (char *)strstr(buffer.c_str(),"Return-Path:");
    if ((ptr && strstr(ptr,"MAILER-DAEMON") != 0 ) || 
       (ptr2 && strstr(ptr2,"MAILER-DAEMON") != 0 ) ) {
      tag_number = 99999;
      MoveFile(FALSE);
      if ((strstr(buffer.c_str(),RPS_TOO_STATUS_LINE) != 0))  {
        mail_too->BadFile(fname.c_str());
      }
      return;
    }

    // this may be a status response to a TOO message 
    if ((strstr(buffer.c_str(),RPS_TOO_STATUS_LINE) != 0)  ) {
      // status message
      mail_too->StatusMsg(fname.c_str(),(char *)buffer.c_str(),
	(char *)RPS_TOO_STATUS_LINE,too_urgency,cmtFlag);
      if (!is_mail) {
        File_Utils fu(fname);
        string root_name = fu.File_GetBaseName(fname.c_str());
        string deleteName;
        fu.File_GetPath(deleteName);
        deleteName += "/.deleted/";
        deleteName += root_name;
        msg= "Renaming " + fname + " to " + deleteName + "\n";
        log->LogMessage(msg.c_str(),TRUE);
        File_Utils fu2(deleteName.c_str());
        string deleteUniq = fu2.File_UniqueName();
        //rename(fname.c_str(),deleteName.c_str());
        if  (RenameFile(fname.c_str(),deleteUniq.c_str(),msg) != 0) {
          msg.append(DB_FATAL_ERR_MSG);
          msg.append("Rename failed for : ");
          msg.append(fname);
          msg.append("\n  to ");
          msg.append(deleteUniq);
          msg.append("\n");
          msg.append(strerror(errno));
          err = BAD;
          log->LogMessage(msg.c_str(),TRUE);
          mail_too->SWAlert(msg.c_str());
        }
      }

      return;
    }

    // if we can't determine type of TOO, save file and notify 
    // someone
    if (is_too == IS_UNKNOWN) {
      tag_number = 99999;
      MoveFile(FALSE,TRUE);
      //This is probably spam so don't send mail anymore.
      //mail_too->BadFile(fname.c_str());
      return;
    }

    // now if the tag number hasn't been assigned, go get the next available
    if (tag_number <= 0 && is_too) { 
       //this should never be any more and should go to spam directory
       //tag_number = GetNextTagNumber();
    }
    if (tag_number <= 0 ) { 
        msg = "Unable to assign tag number for:  ";
        msg.append(fname.c_str());
        log->LogMessage(msg.c_str(),TRUE);
        if (is_too)
          mail_too->TagError(fname.c_str());
        MoveFile(FALSE,TRUE);
        CreateErrorFile(tag_number,msg);
    }
    else {
        // make sure file has the keywords, if so log it and move it
        if ( (strstr(buffer.c_str(),RPS_NAME_LAST_KEY)) &&
             (strstr(buffer.c_str(),RPS_NAME_FIRST_KEY)) &&
             (strstr(buffer.c_str(),RPS_PROP_TITLE_KEY)) ) {

          if (strstr(buffer.c_str(),RPS_BUDGET_KEY)) {
            is_cost = 1;
          }
          if (strstr(buffer.c_str(),"EPO.TYPE[")) {
            is_cost = 2;
          }
          MoveFile(TRUE);
        }
        else {
          MoveFile(FALSE,TRUE);  // probably spam
          if (is_too)
            mail_too->BadFile(fname.c_str());
          msg = "Unable to find keywords for file:\n  ";
          msg.append(fname.c_str());
          msg.append("\n  Keywords= ");
          msg.append(RPS_NAME_LAST_KEY);
          msg.append(",");
          msg.append(RPS_NAME_FIRST_KEY);
          msg.append(",");
          msg.append(RPS_CATEGORY_KEY);
          msg.append(",");
          msg.append(RPS_PROP_TITLE_KEY);
          log->LogMessage(msg.c_str(),TRUE);

	  // now create an error message file in the error queue
          CreateErrorFile(tag_number,msg);
        }
    }
  }
  else {
    msg = "ERROR: Unable to open and read file: ";
    msg.append(fname);
    log->LogMessage(msg.c_str(),TRUE);
    err = BAD;
    if (is_too)
      mail_too->ReadError(fname.c_str());
  }

}


// -----------------------------------------------------------------
// This routine moves the file to the appropriate directory.
// If a tag number was found, the file is copied to the ASCDS_PROP_RPS_RECV
// path, else the file remains in the inbox and will be processed
// during the next loop of incorporating mail.
// -----------------------------------------------------------------
void RPSReceiverFile::MoveFile(Boolean goodfile,Boolean spam )
{
  string msg;
  string unique_name;
  string root_name;
  string cmd;
  string recvenv;
  int     status;
  char    outname[FILENAME_MAX];
  
  // make the output filename
  if (goodfile) { 
    if (!is_too) {
      // normal cycle
      if (!is_cost) {
        recvenv = getenv(RPS_RECV_ENV);
      }
      else if (is_cost == 2) {
        recvenv = (char *)getenv("ASCDS_PROP_RPS_DIR");
        recvenv += "/epo";
      }
      else {
        recvenv = (char *)getenv("ASCDS_PROP_RPS_DIR");
        recvenv += "/budget";
      }
    }
    else if (is_too != IS_TRIGGER && is_cps != 1)
      // unanticipated TOO
      recvenv = getenv(RPS_TOO_RECV_ENV);
    else
      // peer reviewed approved TOO
      recvenv = getenv(RPS_ARCH_ENV);
  }
  else  {
    recvenv = getenv(RPS_ERR_ENV);
    if (spam) {
      recvenv += "/spam";
    }
  }

  if (!is_cost && is_too != IS_TRIGGER) 
    sprintf(outname,"%s/%08d%s",
              recvenv.c_str(),tag_number,RPS_PROPOSAL_EXT);
  else if (!is_cost) 
    sprintf(outname,"%s/%d%s",
              recvenv.c_str(),tag_number,RPS_PROPOSAL_EXT);
  else {
    // cost proposal
    char last_name[RPS_MAX_KEY_VALUE_SIZE];
    char lname[RPS_MAX_KEY_VALUE_SIZE];
    char *ptr;
    int  oidx,nidx;
    string lstr;
    string itype;

    memset(last_name,0,RPS_MAX_KEY_VALUE_SIZE);
    ptr = (char *)strstr(buffer.c_str(),RPS_NAME_LAST_KEY);
    if (ptr)
      GetKeyValue(ptr,lstr);
    strcpy(last_name,lstr.c_str());

    for (oidx=0,nidx=0;oidx<(int)strlen(last_name);oidx++) {
      if (isalpha(last_name[oidx]) || isdigit(last_name[oidx])) {
         lname[nidx++] = last_name[oidx];
      }
    }
    lname[nidx] = '\0';
    lstr = lname;

    ptr = (char *)strstr(buffer.c_str(),"INVESTIGATOR.TYPE");
    if (ptr) {
      GetKeyValue(ptr,itype);
      log->LogMessage(itype.c_str(),TRUE);

      int iidx = itype.find("Co-");
      if (iidx < 0) {
        iidx = itype.find("CO-");
      }
      if (iidx >= 0) {
        log->LogMessage("append the coi",TRUE);
        lstr.append("_coi");
        recvenv += "/CoI";
      }
    }

    sprintf(outname,"%s/%d_%s%s",
              recvenv.c_str(),tag_number,lstr.c_str(),RPS_PROPOSAL_EXT);
    log->LogMessage(outname,TRUE);
  }

  // need to make sure name is unique (just a double check)
  File_Utils fu(outname);
  unique_name = fu.File_UniqueName();
  log->LogMessage(unique_name.c_str(),TRUE);

  // now copy the file
  
  mode_t mode = S_IRUSR | S_IRGRP;
  status = fu.File_Copy(unique_name.c_str(),msg,mode,fname.c_str());
  log->LogMessage(fname.c_str(),TRUE);
  //strsub(msg," ","\n");
  log->LogMessage(msg.c_str(),FALSE);

  if (is_too==IS_TRIGGER && goodfile)
    MailNRATOO(unique_name);
  else if (is_too && goodfile)
    MailTOO(unique_name);

  // message successfully copy, so remove from current message queue
  if (status == 0 ) {
    root_name = fu.File_GetBaseName(fname.c_str());
    if (is_mail) {
      cmd = "refile -unlink +deleted ";
      cmd.append(root_name.c_str());
      system(cmd.c_str());

    }
    else {
      string deleteName;
      fu.File_GetPath(deleteName);
      deleteName += "/.deleted/";
      deleteName += root_name;
      File_Utils fu2(deleteName.c_str());
      string deleteUniq = fu2.File_UniqueName();
      msg= "Renaming " + fname + " to " + deleteUniq + "\n";
      log->LogMessage(msg.c_str(),TRUE);
      if  (RenameFile(fname.c_str(),deleteUniq.c_str(),msg) != 0) {
          msg.append(DB_FATAL_ERR_MSG);
          msg.append("Rename failed for : ");
          msg.append(fname);
          msg.append("\n  to ");
          msg.append(deleteUniq);
          msg.append("\n");
          msg.append(strerror(errno));
          err = BAD;
          log->LogMessage(msg.c_str(),TRUE);
          mail_too->SWAlert(msg.c_str());
      }
    }

    fname = unique_name.c_str();
    if (goodfile)
      // now log the PI and Title information
      LogKeywords(unique_name);
  }
  else {
    // error message already logged, just set status
    err = BAD;
  }
  

}

// -----------------------------------------------------------------
// Search the first 4096 bytes for specific keywords for logging
// the message and sending the first level mail confirmation message
// -----------------------------------------------------------------
void RPSReceiverFile::LogKeywords(string &unique_name)
{
  char *ptr;
  string last_name;
  string first_name;
  string middle_name;
  string title_name;
  string title;
  string category;
  string num_target;
  string exp_time;
  string email_addr;
  string seqnbr;
  string coi_contact;
  string coi_email;
  string msg;
  string fast_proc;
  string budget;
  string obstype;


  ptr = (char *)strstr(buffer.c_str(),RPS_NAME_TITLE_KEY);
  if (ptr)  
    GetKeyValue(ptr,title_name); 

  ptr = (char *)strstr(buffer.c_str(),RPS_NAME_FIRST_KEY);
  if (ptr)
      GetKeyValue(ptr,first_name); 

  ptr = (char *)strstr(buffer.c_str(),RPS_NAME_MIDDLE_KEY);
  if (ptr)
      GetKeyValue(ptr,middle_name); 

  ptr = (char *)strstr(buffer.c_str(),RPS_NAME_LAST_KEY);
  if (ptr)
      GetKeyValue(ptr,last_name); 

  ptr = (char *)strstr(buffer.c_str(),RPS_PROP_TITLE_KEY);
  if (ptr)
      GetKeyValue(ptr,title); 

  ptr = (char *)strstr(buffer.c_str(),RPS_CATEGORY_KEY);
  if (ptr)
      GetKeyValue(ptr,category); 

  ptr = (char *)strstr(buffer.c_str(),RPS_NUM_TARGET_KEY);
  if (ptr)
      GetKeyValue(ptr,num_target); 

  ptr = (char *)strstr(buffer.c_str(),RPS_TOTAL_TIME_KEY);
  if (ptr)
      GetKeyValue(ptr,exp_time); 

  ptr = (char *)strstr(buffer.c_str(),RPS_NETWORK_KEY);
  if (ptr)
      GetKeyValue(ptr,email_addr); 
  else {
    ptr = (char *)strstr(buffer.c_str(),RPS_EMAIL_KEY);
    if (ptr)
      GetKeyValue(ptr,email_addr); 
  }

  ptr = (char *)strstr(buffer.c_str(),RPS_OBSCAT_KEY);
  if (ptr)
      GetKeyValue(ptr,seqnbr); 

  ptr = (char *)strstr(buffer.c_str(),"COICON.CONTACT");
  if (ptr)
      GetKeyValue(ptr,coi_contact); 

  ptr = (char *)strstr(buffer.c_str(),"COI.EMAIL(1)");
  if (ptr) {
      GetKeyValue(ptr,coi_email); 
  }
  ptr = (char *)strstr(buffer.c_str(),"FAST.PROC");
  if (ptr) {
      GetKeyValue(ptr,fast_proc); 
  }
  ptr = (char *)strstr(buffer.c_str(),"PROPOSED.BUDGET");
  if (ptr) {
      GetKeyValue(ptr,budget); 
  }


  if (coi_contact == "Y") {
    email_addr += ",";
    email_addr += coi_email;
  }
  msg += "EMAIL:  ";
  msg += coi_contact;
  msg += " -- ";
  msg += email_addr;
  proposal_log->LogMessage(msg.c_str(),TRUE);

  msg = "FILE== ";
  msg.append(unique_name.c_str());
  proposal_log->LogMessage(msg.c_str(),TRUE);

  msg = "  PI== ";
  msg.append(title_name);
  msg.append(" ");
  msg.append(first_name);
  msg.append(" ");
  msg.append(middle_name);
  msg.append(" ");
  msg.append(last_name);
  if (is_too) {
    msg.append("\n  URGENCY== ");
    msg.append(pri);
    msg.append("\n  FAST PROC== ");
    msg.append(fast_proc);
  }
  if (is_too == IS_TRIGGER) {
    msg.append("\n  OBSCAT== ");
    msg.append(seqnbr);
  }

  msg.append("\n  CATEGORY== ");
  if (!is_cost)
    msg.append(category);
  else if(is_cost == 2) 
    msg.append("EPO");
  else 
    msg.append("BUDGET");
  msg.append("\n  TITLE== ");
  msg.append(title);

  proposal_log->LogMessage(msg.c_str(),FALSE);

  if (!is_cps) {
    MailConfirmation(category.c_str(),title.c_str(),num_target.c_str(),
	exp_time.c_str(),email_addr.c_str(),seqnbr.c_str(),
	budget.c_str());
  }
}

// -----------------------------------------------------------
void RPSReceiverFile::MailConfirmation(const char *category,const char *title,
	const char *num_target,const char *exp_time,
	const char *email_addr,const char *seqnbr,const char* budget)
{
  string tmpname = getenv("ASCDS_TMP_PATH");
  char    *ptr;
  string emailaddr;
  string replyaddr;
  string msn;

  if (is_cost) {
    msn = "Chandra Cost Proposal";
  }
  else if (is_too == IS_TRIGGER) {
    msn = "Peer-reviewed TOO Trigger request";
  }
  else if (is_too ) {
    msn = "Chandra DDT Proposal";
  }
  else {
    msn = "Chandra Proposal";
  }
  replyaddr = " cxchelp@head.cfa.harvard.edu";
  tmpname.append("/rpsconfirm.tmp");
  ofstream ofile(tmpname.c_str(),ios::out);
  if (ofile.good()) {
    ofile << "The " << msn << " described below has been received by the Chandra X-ray Observatory.\n\n";
    if (!is_too ) {
      if (! is_cost) {
        ofile << "You will receive a more detailed confirmation message within the next week.\n";
        ofile << "The detailed confirmation will include a reflection from our database of\n" ;
        ofile << "all the entries in the submitted proposal parameters. We will ask that\n";
        ofile << "those entries be checked for accuracy.\n";
        ofile << "\nAll investigators listed on the RPS form will receive an email after the\nsubmission deadline containing basic information such as proposal number,\nP.I., co-investigators, proposal title, and abstract." << endl;
      }
    }
    else  if (is_too == IS_TRIGGER) {

      // read in current file and copy everything after "Submission Date" 
      ifstream infile(fname.c_str(),ios::in);
      if (infile.good()) {
        char tbuf [200];
        int docopy = 0;
        while (!infile.eof()) {
           memset(&tbuf[0],0,sizeof(tbuf));
           infile.getline(tbuf,sizeof(tbuf)); 
           if ((infile.gcount() == (sizeof(tbuf)-1)) && (infile.fail())) {
              infile.clear();
           } 
           if (strncasecmp(tbuf,"Submission Date",15) == 0) {
              docopy = 1;
           }
           if (docopy == 1) {
             ofile << tbuf << endl;
           }
        }
        infile.close();
     }
 
    // get proposal number if already assigned
    ptr = (char *)strstr(buffer.c_str(),RPS_TAG_KEY);
      
     

    } 
    else  if (is_too == IS_DDT) {
      ofile << "You will receive a more detailed message as soon as possible regarding\n";
      ofile << "your request for Director's Discretionary Time proposal.\n";
    } 
    else {
      ofile << "You will receive a more detailed message within the next week regarding\n";
      ofile << "your proposal.\n";
    }
    if (is_too != IS_TRIGGER) {
      ofile << "\nProposal Number : " << setfill('0') << setw(8) << tag_number;
      ofile << "\nTitle           : " << title;
      if (!is_cost) {
        ofile << "\nType            : " << ptype;
        ofile << "\nCategory        : " << category;
 
        if (ptype.find("ARC") != STL_NPOS  ||
            ptype.find("THE") != STL_NPOS  ) {
          ofile << "\nProposed Budget : " << budget;
        }
        else {
          ofile << "\nNumber Targets  : " << num_target;
          ofile << "\nTotal Time      : " << exp_time;
        }
      }
    }
    ofile << endl;
    ofile.close();
  
    ptr = getenv("ASCDS_PROP_TEST_MODE");
    if (ptr)
      emailaddr = getenv("ASCDS_PROP_RPS_EMAIL");
    else
      emailaddr = email_addr;

    string lmsg ="Mail sent for Chandra RPS Proposal Receipt: ";
    lmsg += emailaddr;
    lmsg += "\n";
    log->LogMessage(lmsg.c_str(),TRUE);
 
    mail_file((char *)emailaddr.c_str(),
	(char *)"Chandra RPS Proposal Receipt",
 	(char *)tmpname.c_str(),NULL,(char *)replyaddr.c_str());
    unlink(tmpname.c_str());
  }
    
}

// -----------------------------------------------------------
void RPSReceiverFile::GetKeyValue(char *ptr,string &retstr)
{

  char *t1;

  retstr.clear();

  t1 = strchr(ptr,'=');
  if (t1) {
    while (t1++ && *t1 != '\0'  && *t1 != '\n') 
      retstr += *t1; 
  }
    
  return;
}

// ---------------------------------------------------------------
// open a file and find the next available tag number for the
// proposals. 
// ---------------------------------------------------------------
int RPSReceiverFile::GetNextTagNumber()

{
  int tagno = -1;
  fstream infile(tag_filename.c_str(),ios::in | ios::out);
  if (infile.good() ) {
    infile >>  tagno;
    infile.seekg(0);
    tagno += 1;
    infile << tagno;
    infile.close();
  }

  return tagno;
}

// ---------------------------------------------------------------
// Create/Append a message to the error file for the specified  
// tag number.
// ---------------------------------------------------------------
void RPSReceiverFile::CreateErrorFile(int tagno, string &errmsg)
{
  string errname;
  string msg;
  char    tag_nbr[9];

  if (tagno < 10000) {
    sprintf(tag_nbr,"%05d",tagno);
  }
  else {
    sprintf(tag_nbr,"%08d",tagno);
  }
  errname = (char *)getenv(RPS_ERR_ENV);
  errname.append("/");
  errname.append(tag_nbr);
  errname.append(RPS_ERR_EXT);

  fstream outfile(errname.c_str(),ios::app);
  if  (outfile.good()) {
    outfile << errmsg.c_str() << endl;
    outfile.close();
  }
  else  {
    msg = "Unable to open error file for ";
    msg.append(errname.c_str());
    log->LogMessage(msg.c_str(),TRUE);
  }
}

// ---------------------------------------------------------------
// mail formatted messages to too recipients (director,MP,USG)
// ---------------------------------------------------------------
    
void RPSReceiverFile::MailTOO(string &unique_name)
{
  int ii;
  const char *labels[] = {
 	RPS_NAME_FIRST_KEY,
 	RPS_NAME_LAST_KEY,
	RPS_TYPE_KEY,
        RPS_RA_KEY,
        RPS_DEC_KEY,
	"FAST.PROC",
        "TOO.START",
        "TOO.STOP",
 	NULL};
  vector<string> kptrs(10);
  char *tmp;
  string tstr;
  string tstart = "";
  string tstop = "";
  string the_targets;
  string poc;
  string piname;
  string time_buffer;
  string obs_type;
  string fast_proc;
  char itmp[2000];
  char tbuff[200];
  string keyval;
  double  dval;
  const char *subject = NULL;
  const char *delim="******************************************************************";


  // open the RPS file and get all the necessary information
  // PI, and multiple RA,DEC
  ifstream rpsfile(fname.c_str(),ios::in);
  if (rpsfile.good()) {
    while (!rpsfile.eof()) {
      memset(itmp,0,sizeof(itmp));
      rpsfile.getline(itmp,sizeof(itmp)); 
      if ((rpsfile.gcount() == (sizeof(itmp)-1)) && (rpsfile.fail())) {
        rpsfile.clear();
      }

      for (ii=0;labels[ii] != NULL;ii++) {
        tmp = strstr(itmp,labels[ii]);
        if (tmp) {
          GetKeyValue(tmp,keyval);
          if (keyval.length() > 0) {
            kptrs[ii] += keyval;
            if (strstr(labels[ii],RPS_TYPE_KEY)) {
              ptype = keyval;
            }
            else if (strcmp(labels[ii],"FAST.PROC")==0) {
              if (fast_proc != "Y")
                 fast_proc=keyval;
            }
            else if (strcmp(labels[ii],"TOO.START")==0) {
               dval = atof(keyval.c_str());
               sprintf(tbuff,"%.1f",dval);
               tstart= tbuff;
            }
            else if (strcmp(labels[ii],"TOO.STOP")==0) {
               dval = atof(keyval.c_str());
               sprintf(tbuff,"%.1f",dval);
               tstop= tbuff;
            }
            // special spacing in case of multiple targets (ra,dec)
            else if (ii == 3 || ii == 4 )
              kptrs[ii] += "    ";
          }
        }
      }
    }
  rpsfile.close();
  }

  if (tstart != "" && tstop != "") {
    pri += "( " + tstart + "-" + tstop + " days)";
  }

  if (fast_proc == "Y") 
    fast_proc = "[x] ";
  else
    fast_proc = "[] ";
  fast_proc += "Fast Processing required";

  // use first initial, last name for PI
  ii = 0;
  piname = kptrs[ii++].substr(0,1);
  piname += ". ";
  piname += kptrs[ii++];
  the_targets = this->GetTargets();

  // get the POC info for the message
  string cmd = RPS_TOO_MAIL_EXEC;
  cmd += " -poc";
  FILE *pd = popen(cmd.c_str(),"r");
  if (pd) {
    memset(itmp,0,sizeof(itmp));
    while (fgets( itmp,sizeof(itmp),pd) != NULL)
      poc += itmp;
    pclose(pd);
  }

  
  // read the input template file

  // get the current time
  time_buffer = GetCurrentTime();
  

  // set the output mail file name to the same name, append extension
  File_Utils fu(unique_name);
  string mname;
  string stmp = fu.File_GetBaseName();
  mname.append(getenv(PROP_OUTMAIL_ENV));
  mname.append("/");
  mname.append(stmp);
  mname.append(RPS_MAILTOO_PAGER_EXT);

  // read the input template file
  char ftmp[RPS_BUFFER_SIZE];
  string tname = getenv(PROP_TEMPLATE_ENV);
  tname += "/";
  if (is_too != IS_DDT) {
    tname += RPS_TOO_TEMPLATE;
    subject = "Unanticipated TOO Request";
  }
  else {
    tname += RPS_DDT_TEMPLATE;
    if (ptype.find("CAL") != STL_NPOS) 
      subject = "Request for Calibration Time";
    else
      subject = "Request for Director's Discretionary Time";
  }
  ifstream infile(tname.c_str(),ios::in);
  if (infile.good()) {
    memset(ftmp,0,sizeof(ftmp));
    infile.read(ftmp,sizeof(ftmp)-1); 
    infile.close();
  
    // open up the output mail file and fill in the variables
    if (is_too != IS_DDT) {
      FILE *fp = fopen(mname.c_str(),"w");
      fprintf(fp,ftmp,
	  time_buffer.c_str(),
	  piname.c_str(), 
	  pri.c_str(), tag_number,
          kptrs[3].c_str(),
          kptrs[4].c_str(),
          fast_proc.c_str(),	  
	  poc.c_str(),
          the_targets.c_str(),
	  " "," "," ");
      fclose(fp);
  
      // send the pager mail
      mail_too->SendPagerMsg(mname.c_str(),tag_number,too_urgency);
    }

    // now send the regular email with the RPS file attached
    stmp = fu.File_GetBaseName(unique_name.c_str());
    mname = getenv(PROP_OUTMAIL_ENV);
    mname.append("/");
    mname.append(stmp);
    mname.append(RPS_MAILTOO_EXT);

    FILE *fp2 = fopen(mname.c_str(),"w");
    if (is_too != IS_DDT) {
      fprintf(fp2,ftmp,
	time_buffer.c_str(),
        piname.c_str(), 
	pri.c_str(),tag_number,
        kptrs[3].c_str(),
        kptrs[4].c_str(),
          fast_proc.c_str(),	  
	poc.c_str(),
        the_targets.c_str(),
   	delim,buffer.c_str());
    }
    else {
      fprintf(fp2,ftmp,
	time_buffer.c_str(),
        kptrs[2].c_str(),
        piname.c_str(), 
	pri.c_str(),tag_number,
        kptrs[3].c_str(),
        kptrs[4].c_str(),
        fast_proc.c_str(),	  
	poc.c_str(),
        the_targets.c_str(),
   	delim,buffer.c_str());
    }
    fclose(fp2);
 
    if (subject)
      mail_too->SendMsg(mname.c_str(),too_urgency,(char *)subject,tag_number);
    else
      mail_too->SendMsg(mname.c_str(),too_urgency);

  }
  else 
    mail_too->ReadError(RPS_TOO_TEMPLATE);


}

// ---------------------------------------------------------------
// mail formatted messages to too recipients (director,MP,USG)
// ---------------------------------------------------------------
    
void RPSReceiverFile::MailNRATOO(string &unique_name)
{
  int ii;
  const char *labels[] = {
 	RPS_NAME_FIRST_KEY,
 	RPS_NAME_LAST_KEY,
        RPS_RA_KEY,
        RPS_DEC_KEY,
	RPS_OBSCAT_KEY,
	"FAST.PROC",
 	NULL};
  char ptrs[7][RPS_MAX_KEY_VALUE_SIZE];
  char *tmp;
  string poc;
  string piname;
  string fast_proc;
  string time_buffer;
  char itmp[2000];
  string keyval;
  const char *delim="******************************************************************";
  string linked_obsids;


  // open the RPS file and get all the necessary information
  // PI, and multiple RA,DEC
  memset(ptrs,0,sizeof(ptrs));
  ifstream rpsfile(fname.c_str(),ios::in);
  if (rpsfile.good()) {
    while (!rpsfile.eof()) {
      memset(itmp,0,sizeof(itmp));
      rpsfile.getline(itmp,sizeof(itmp)); 
      if ((rpsfile.gcount() == (sizeof(itmp)-1)) && (rpsfile.fail())) {
        rpsfile.clear();
      }
      for (ii=0;labels[ii] != NULL;ii++) {
        tmp = strstr(itmp,labels[ii]);
        if (tmp) {
          GetKeyValue(tmp,keyval);
          if (keyval.size() > 0) {
            if (strcmp(labels[ii],"FAST.PROC") == 0) {
              if (fast_proc != "Y")
                 fast_proc=keyval;
            }
            if (ii==2 && ptrs[ii][0] == '\0')
               strcat(&ptrs[ii][0],"RA  :  ");
            if (ii==3 && ptrs[ii][0] == '\0')
               strcat(&ptrs[ii][0],"Dec : ");
            strcat(&ptrs[ii][0],keyval.c_str());
            // special spacing in case of multiple targets (ra,dec)
            if (ii == 2)
              strcat(&ptrs[ii][0],"    ");
            else if (ii == 3)
              strcat(&ptrs[ii][0],"   ");
          }
        }
      }
    }
  rpsfile.close();
  }

  // use first initial, last name for PI
  ii = 0;
  piname = ptrs[ii++][0];
  piname += ". ";
  piname += &ptrs[ii++][0];



  linked_obsids = GetLinkedObsids(&ptrs[4][0]);
  if (linked_obsids.length() > 2) {
     linked_obsids.insert(0,"Linked Observations:\n");
  }

  if (fast_proc == "Y") 
    fast_proc = "[x] ";
  else
    fast_proc = "[ ] ";
  fast_proc += "Fast Processing required";

  // get the current time
  time_buffer = GetCurrentTime();
  
  // set the output mail file name to the same name, append extension
  File_Utils fu;
  string mname;
  string stmp = fu.File_GetBaseName(unique_name.c_str());
  mname = getenv(PROP_OUTMAIL_ENV);
  mname.append("/");
  mname.append(stmp);
  mname.append(RPS_MAILTOO_PAGER_EXT);

  // get the POC info for the message
  string cmd = RPS_TOO_MAIL_EXEC;
  cmd += " -poc";
  FILE *pd = popen(cmd.c_str(),"r");
  if (pd) {
    memset(itmp,0,sizeof(itmp));
    while (fgets( itmp,sizeof(itmp),pd) != NULL)
      poc += itmp;
    pclose(pd);
  }

  

  // read the input template file
  char ftmp[RPS_BUFFER_SIZE];
  string tname = getenv(PROP_TEMPLATE_ENV);
  tname += "/";
  tname += RPS_TRIGGER_TEMPLATE;
  ifstream infile(tname.c_str(),ios::in);
  if (infile.good()) {
    memset(ftmp,0,sizeof(ftmp));
    infile.read(ftmp,sizeof(ftmp)-1); 
    infile.close();
  
    // open up the output mail file and fill in the variables
    FILE *fp = fopen(mname.c_str(),"w");
    fprintf(fp,ftmp,
	time_buffer.c_str(),piname.c_str(), 
	pri.c_str(), tag_number,
        &ptrs[4][0],
        &ptrs[2][0],
        &ptrs[3][0],
        linked_obsids.c_str(),
        fast_proc.c_str(),	  
	poc.c_str(),"","");
    fclose(fp);
 
    // send the pager mail
     if ((strcmp(too_urgency,RPS_TOO_SLOW) !=0)    &&
         (strcmp(too_urgency,RPS_TOO_NONTRANSIENT) !=0) ) {
      mail_too->SendPagerMsg(mname.c_str(),tag_number,too_urgency,
	"Peer-reviewed TOO Trigger Request");
    }

    // now send the regular email with the RPS file attached
    stmp = fu.File_GetBaseName(unique_name.c_str());
    mname = getenv(PROP_OUTMAIL_ENV);
    mname.append("/");
    mname.append(stmp);
    mname.append(RPS_MAILTOO_EXT);

    FILE *fp2 = fopen(mname.c_str(),"w");
    fprintf(fp2,ftmp,
	time_buffer.c_str(),piname.c_str(), 
	pri.c_str(),tag_number,
        &ptrs[4][0],
        &ptrs[2][0],
        &ptrs[3][0],
        linked_obsids.c_str(),
        fast_proc.c_str(),	  
	poc.c_str(),
   	delim,buffer.c_str());
    fclose(fp2);
 
    mail_too->SendMsg(mname.c_str(),too_urgency,"Peer-reviewed TOO Trigger Request",tag_number);

  }
  else 
    mail_too->ReadError(RPS_TRIGGER_TEMPLATE);


}

// -----------------------------------------------------------------
//  need proposal number, non-transient?  for subject/mailing
//
// -----------------------------------------------------------------
void RPSReceiverFile::MailDDTParameters()
{
  char *ptr;
  string mailfile;
  string unique_mail;
  string tstr;
  string pno;
  string msg;
  Boolean isNonTransient=false;


  // first line has proposal number
  GetKeyValue((char *)buffer.c_str(),pno);
  stringFcns::trim_white_space(&pno);
  ptr = strstr((char *)buffer.c_str(),(char *)"Response Time") ;
  if (ptr) {
    GetKeyValue(ptr,tstr);
    if (strstr(tstr.c_str(),"NON-TRANSIENT"))
      isNonTransient=true;
  }
  
  // Move file to archive area
  ptr = getenv(RPS_ARCH_ENV);
  if (ptr)
    mailfile = ptr;
  else
    mailfile = "/tmp";
  mailfile += "/mail/";
  if (access(mailfile.c_str(),W_OK) != 0) {
    cerr << "creating mail subdirectory for confirmation messages" << endl;
    mkdir(mailfile.c_str(),S_IRWXU | S_IRWXG);
    chmod(mailfile.c_str(),S_IRWXU | S_IRWXG);
  }

  mailfile += pno;
  if (strstr(fname.c_str(),"conflict")) 
    mailfile += ".propconflict";
  else
    mailfile +=  RPS_MAIL_EXT;

  File_Utils fu(mailfile.c_str());
  unique_mail = fu.File_UniqueName();
  //need to copy and delete since maybe different file systems 
  string tmsg = "RENAME "  + fname + " to " + unique_mail ;
  log->LogMessage(tmsg.c_str(),TRUE);
  
  if  (RenameFile(fname.c_str(),unique_mail.c_str(),msg) == 0) {
    string cmd;
    cmd = RPS_TOO_MAIL_EXEC;
    if (strstr(fname.c_str(),"conflict")) 
      cmd += " -s \"Out-of-Cycle DDT Proposal Check for #";
    else 
      cmd += " -s \"Out-of-Cycle DDT Confirmation message #";
    cmd += pno;
    cmd += "\"  -normal ";
    if (isNonTransient)
      cmd += " -p NT ";
    cmd += " -f ";
    cmd += unique_mail;
    log->LogMessage(cmd.c_str(),TRUE);
    system(cmd.c_str());
  } else {
    tmsg.append(DB_FATAL_ERR_MSG);
    tmsg.append("Rename failed for : ");
    tmsg.append(fname);
    tmsg.append("\n  to ");
    tmsg.append(unique_mail);
    tmsg.append("\n");
    tmsg.append(strerror(errno));
    err = BAD;
    log->LogMessage(tmsg.c_str(),TRUE);
    mail_too->SWAlert(tmsg.c_str());
  }
}



// ------------------------------------------------------------
char *RPSReceiverFile::GetUrgency()
{
  const char *str = NULL;
  stringFcns::trim_white_space(&pri);

  // determine the urgency of the TOO and fill in the "nice" format
  if (strncasecmp(pri.c_str(),TOO_E,strlen(TOO_E)) == 0)
    str = RPS_TOO_FAST;
  else if (strncasecmp(pri.c_str(),"FAST",4) == 0)
    str = RPS_TOO_FAST;
  else if (strncasecmp(pri.c_str(),TOO_I,strlen(TOO_I)) == 0)
    str = RPS_TOO_MEDIUM;
  else if (strncasecmp(pri.c_str(),"MEDIUM",6) == 0)
    str = RPS_TOO_MEDIUM;
  else if (strncasecmp(pri.c_str(),"SLOW",4) == 0)
    str = RPS_TOO_SLOW;
  else if (strncasecmp(pri.c_str(),"NON-TRANSIENT",9) == 0)
    str = RPS_TOO_NONTRANSIENT;
  else if (strncasecmp(pri.c_str(),TOO_R,strlen(TOO_R)) == 0)
    str = RPS_TOO_SLOW;
  else if (strncasecmp(pri.c_str(),TOO_U,strlen(TOO_U)) == 0)
    str = RPS_TOO_MEDIUM;
  else if (strncasecmp(pri.c_str(),TOO_P,strlen(TOO_P)) == 0)
    str = RPS_TOO_MEDIUM;
  else
    str = RPS_TOO_SLOW;


  return (char *)str;
}

// ------------------------------------------------------------
string RPSReceiverFile::GetCurrentTime()
{
  string time_buffer;
  time_t now;
  struct tm *utctime;
  char *tmp;
  
  // get the current time
  now = time(NULL);
  utctime = localtime(&now);
  tmp = asctime(utctime);
  time_buffer = tmp;
  stringFcns::trim_trailing_white_space(&time_buffer);

  return time_buffer;
}


// ------------------------------------------------------------
string RPSReceiverFile::GetTargets()
{
  string the_targets;
  string the_fups;

  const char *targkeys[] = {
	"TARGET.NUMBER",
  	"TARGET.NAME",
   	"TOTAL.OBS.TIME",
  	"NUMBER.OBS",
        "TOO.MAX.FOLLOWUP",
	"TOO.INITIAL.TIME"};
  int  nkeys = 6;

  const char *targfmt[] = {
	"%3.3s",
        "%-18.18s",
	"%8.8s",
	"%3.3s",
	"%3.3s",
	"%8.8s" };
	
  string tstr;
  char str[7][30];
  char ttype[12];
  char tmpchar[20];
  char *ptr;
  char *optr;
  int  ii;
  int  nobs,max_fups;
  double ttime,dtmp;
  
  ptr = (char *)buffer.c_str();
  while (ptr) {
    //cerr << "\n======================\n" << ptr  << endl;
    nobs = 0;
    strcpy(ttype,"-      ");
    ttime = 0.0;
    dtmp  = 0.0;
    for (ii=0;ii<nkeys;ii++) {
      ptr = strstr(ptr,targkeys[ii]);
      //cerr << "******" << targkeys[ii] << endl;
      if (ptr) {
        //cerr << "******" << ptr  << endl;
        // get value for specified keyword
        tstr.clear();
        GetKeyValue(ptr,tstr); 
        memset(&str[ii][0],0,30);
        sprintf(&str[ii][0],targfmt[ii],tstr.c_str());

        if (strstr(targkeys[ii],"TOTAL"))
          ttime = strtod(&str[ii][0],NULL);
        else if (strstr(targkeys[ii],"NUMBER.OBS")) {
          nobs = atoi(&str[ii][0]);
          if (nobs > 1) 
            strcpy(ttype,"MONITOR");
        }
        else if (strstr(targkeys[ii],"MAX")) {
          max_fups = atoi(&str[ii][0]);
          if (max_fups <= 0) {
              max_fups = 8;
          }
        }
        else if (strstr(targkeys[ii],"INITIAL")) {
          if (!strstr(ttype,"MONITOR")) {
            dtmp = strtod(&str[ii][0],NULL);
            if (dtmp > 0)
              ttime = dtmp;
          }
        }
      }
      else {
        break;
      }
    }
    the_fups.clear();
    if (ttime > 0) {
      optr = ptr;
      for (ii=0;ii < max_fups &&ptr;ii++) {
        ptr = strstr(ptr,"TOO.FUP.TIME");
        if (ptr) {
          GetKeyValue(ptr,tstr); 
          dtmp = strtod(tstr.c_str(),NULL);
          if (dtmp > 0.) {
            //the_fups += &str[0][0];
            the_fups += "   ";
            the_fups += " ";
            //the_fups += &str[1][0];
            the_fups += "                  ";
            the_fups += " ";
            the_fups += "FOLLOWUP ";
            tstr.clear();
            sprintf(tmpchar,"%7.2f",dtmp);
            the_fups += tmpchar;
            the_fups += "   1    [  ]   [ ]\n";
          }
          ptr += 1;
        }
      }
      if (!ptr) ptr = optr;
    }
   

    // build the target line
    if (ttime > 0) {
      the_targets += &str[0][0];
      the_targets += " ";
      the_targets += &str[1][0];
      the_targets += " ";
      the_targets += ttype;
      the_targets += "  ";
      sprintf(tmpchar,"%7.2f",ttime);
      the_targets += tmpchar;
      the_targets += " ";
      sprintf(tmpchar,"%3d",nobs);
      the_targets += tmpchar;
      the_targets += "    [  ]   [ ]\n";
      the_targets += the_fups;

    }
  }

  return the_targets;
}

string RPSReceiverFile::GetLinkedObsids(const char *obsid )
{
  FILE *pd;
  char *ptr;
  string theresults;
  char itmp[1000];

  string cmd = "prop_linked_obsids.pl -U mpbrowser -q ";
  ptr = getenv("ASCDS_PROP_RPS_DIR");
  cmd += ptr;
  cmd += "/.htfile -o ";
  cmd += obsid;
  log->LogMessage(cmd.c_str(),TRUE);
  pd = popen(cmd.c_str(),"r");
  if (pd) {
    memset(itmp,0,sizeof(itmp));
    while (fgets( itmp,sizeof(itmp),pd) != NULL)
      theresults += itmp;
    pclose(pd);
  }

  return (theresults);
}

// -----------------------------------------------------------------
// -----------------------------------------------------------------
int  RPSReceiverFile::RenameFile(const char *old_name,const char *new_name,
        string &emsg)
{
  int retval = 0;

  File_Utils fu(old_name);
  retval = fu.File_Copy(new_name,emsg);
  if (retval == 0) {
    unlink(old_name);
    chmod(new_name,S_IRUSR | S_IRGRP);
  }

  return retval;
}


