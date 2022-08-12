/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPeerList.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code that executes the proposal
	peer_list.pl command.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "ap_common.hh"
#include "PanelPeerList.hh"
#include "PanelParameter.hh"
#include "PanelApp.hh"
#include "stringFcns.hh"
#include "MessageAreaList.hh"
#include "InfoDialogManager.hh"


#define PEERLIST_EXEC "peer_lists.pl"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelPeerList::PanelPeerList ( int active) : 
          ExecuteCmd ( (char *)"Peer Lists", active,(char *)PEERLIST_EXEC )
{
    
    SetMenuFields ( 'P');
    Initialize();
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelPeerList::~PanelPeerList()
{
  if (pfilename.length() > 0) {
    unlink(pfilename.c_str());
  }
}
// ------------------------------------------------------------
// ------------------------------------------------------------
Boolean PanelPeerList::Setup()
{
  return TRUE;
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelPeerList::Initialize()
{
}
// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelPeerList::doit()
{
  string params;
  string str;
  XConnection *xconn;


  xconn = theApplication->GetDBConnection();
  if (xconn) {
    pfilename = getenv(PROP_LOG_ENV);
    pfilename += "/.plist_";
    pfilename += getenv("LOGNAME");
    ofstream pfile (pfilename.c_str(),ios::out);
    pfile << xconn->get_password();
    pfile.close();
    chmod(pfilename.c_str(),0600);

    params += " -U^";
    params += xconn->get_user();
    params += "^-q^";
    params += pfilename;
    params += "^-S^";
    params += xconn->get_server();
    params += "^-o^";

    if (getenv("ASCDS_PROP_DIR")) 
      str =  getenv("ASCDS_PROP_DIR");
    else
      str = ".";
    
    str += "/reports/peer_lists";
    params += str;
      
    SetParameters((char *)params.c_str(),'^');
      
    stringFcns::sub(params,"^"," ");
    params.insert(0,PEERLIST_EXEC);
    theApplication->Log(params);
    theMessageAreaList->SetText((char*)params.c_str());
    ExecCommand();

    str.insert(0,"Lists are located in ");
    theInfoDialogManager->post(NULL, (char *)str.c_str(),
          NULL,NULL,NULL,NULL);

  }
}
