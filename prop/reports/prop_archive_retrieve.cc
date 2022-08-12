
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME:	prop_archive_retrieve.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:  
	Application to retrieve specified proposal from the
	database archive utilizing arc4gl library interface.

	prop_archive_retrieve -U <database user> -r proposal_number

* NOTES:

  None

* REVISION HISTORY:

        Ref. No.        Date
        --------        ---- 
	%I%		%G%

*H****************************************************************************/
#include <string.h>
#include <fstream>
#include <ospace/time.h>
#include <ospace/helper.h>
#include <ospace/file.h>
#include <ospace/stream.h>
#include <ospace/unix.h>
#include <ospace/network.h>


#include "ProposalDefs.hh"
#include "ObsUtil.hh"
#include "Proposal_Ingest.hh"
#include "connect_db.hh"

#include "Directory.hh"

XConnection *xc;

int main(int argc,char **argv)
{
  string  pno;
  string  retname;
  string  pwd;
  string  user;

  int retcode;
  int cc;
  extern char *optarg;
  string  tmp;

  while (( cc= getopt(argc,argv,"p:U:")) != EOF) {
    switch (cc) {
      case 'U':
        user = optarg;
        break;
      case 'p': 
        // retrieve and send email message
        pno = optarg;
        break;
    }
  }

  if (user.length() > 0 && pno.length() > 0 ) {
    static os_helper_toolkit init_helper;
    static os_file_toolkit init_file;
    static os_time_toolkit init_time;
    static os_streaming_toolkit init_stream;
    static os_unix_toolkit init_unix;
    static os_network_toolkit init_network;



    // get password for proposal database
    get_password(pwd);


    retname = "./";
    retname.append(pno);
    retname.append(".prop");
  
    Proposal_Ingest pingest(user.c_str(),pwd.c_str());
    if (pingest.GetState() == BAD) {
      cerr << pingest.GetErrors() << endl;
    }
    else {  
      cerr << "Start archive retrieval" << endl;
      retcode = pingest.Retrieve(pno.c_str(),retname);
      if (retcode == 0) {
        cerr << "Successful archive retrieve: " << retname << endl;
      }
      else {
        cerr << "Archive retrieve failed for: " << pno << endl;
        cerr << pingest.GetErrors() << endl;
      }
    }
  }

  else {
    cerr << "USAGE: prop_archive_retrieve -U user_name -p proposal_number" << endl;
    cerr << "       Server is $DB_PROP_ARCSRV" << endl;
  }
}
