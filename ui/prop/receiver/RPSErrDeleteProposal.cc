
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrDeleteProposal.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  delete function.
        This will delete the current proposal file from the database.



* NOTES:  



* REVISION HISTORY:


        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#include "ap_common.hh"
#include <stdlib.h>
#include <string>

// libraries
#include "ProposalUtils.hh"
#include "GUIDefs.h"
#include "MessageAreaList.hh"
#include "ErrorMessage.hh"

#include "RPSErrDeleteProposal.hh"
#include "RPSErrRefreshCmd.hh"
#include "RPSErrMenuWindow.hh"
#include "RPSErrApp.hh"

#define DEFAULTDELETE "The following proposal will be deleted from the database.\n  Press OK to confirm deletion, else press Cancel.\n\n"


RPSErrDeleteProposal::RPSErrDeleteProposal ( int active) : 
                     AskFirstCmd ( "Delete Proposal...", active )
{
    
    setQuestion ( DEFAULTDELETE );
    //SetMenuFields ( 'D',NULL,NULL);

}


RPSErrDeleteProposal::~RPSErrDeleteProposal()
{
}

void RPSErrDeleteProposal::updateQuestion()
{
  string msg;
  

  mystat = TRUE;

  propno = theRPSErrWindow->GetCurrentProposalNumber();
  if (propno.length() > 0) {
    msg = DEFAULTDELETE;
    msg.append(propno);
  } 
  else {
    mystat = FALSE;
    msg = "Please select a proposal and try again.\n";
  }

  setQuestion((char *)msg.c_str());
}

void RPSErrDeleteProposal::doit()
{
  RPSErrIngest *prop; 
  string msg;
  ProposalUtils pu;
  

  if (mystat) {

    GUI_SetBusyCursor(theRPSErrWindow->baseWidget(),True);
    prop = theApplication->GetIngest();
    if (prop->DeleteProposal(propno.c_str()) == 0) {
      //theRPSErrWindow->ClearCurrentFiles();
      msg = pu.IgnoreUploadFiles(propno.c_str());
      if (msg.length() > 0) {
        theApplication->Log(msg.c_str(),TRUE);
      }
      msg = "Delete successful for proposal number ";
      msg += propno;
      theMessageAreaList->SetText( (char *)msg.c_str());
      ((RPSErrRefreshCmd *)theRPSErrWindow->refresh_cmd())->execute();
    }
    else {
      msg = "Delete failed for proposal ";
      msg += propno;
      theErrorMessage->DisplayMessage(msg.c_str());
    }
    GUI_SetBusyCursor(theRPSErrWindow->baseWidget(),False);

  }

}  
