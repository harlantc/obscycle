
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the code for the Proposal manager GUI Refresh
       command class. This class refreshes the list of proposal entries.

* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"
#include "PropMgrReviewsCmd.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"
#include "ErrorMessage.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrReviewsCmd::PropMgrReviewsCmd ( const char *name,int rtype,
		char imn,int active ) : 
                NoUndoCmd ( (char *)name, active )
{
  rev_type = rtype;
  mn = imn;
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrReviewsCmd::~PropMgrReviewsCmd()
{
  //for (int ii=0;ii<revcmds.size();ii++) {
    //delete revcmds[ii];
  //}
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrReviewsCmd::doit()
{
  PropMgrPropList *obj;
  PropMgrReviewCmd *rc;
  PropMgrPeerCmd *pc;
  DBProposalReview *dbr;
  ProposalEntry *pe;
  ProposalReview *pr;
  string pno;
  int ii;
  string msg;

  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),True);
  obj = thePropMgrWindow->GetPropList();

  pno = obj->GetCurrentProposalNumber();
  pe = obj->GetProposal();
  if (pe) {
    pr = pe->GetProposalReview();
    if (pr) {
      for (ii=0; ii< pr->GetReviewCnt();ii++) {
         dbr = pr->GetReview(ii);
         if (rev_type == PRI_REV || rev_type == SEC_REV) {
           rc = new PropMgrReviewCmd("Review",rev_type,mn,TRUE);
           rc->SetReview(pno,dbr->get_panel_name() );
           rc->execute();
         }
         else if (rev_type == PEER_REV ) {
           pc = new PropMgrPeerCmd(TRUE,"Review",rev_type);
           pc->SetReview(pno,dbr->get_panel_name() );
           pc->execute();
         }
         else if (rev_type == FINAL_REV ) {
           pc = new PropMgrPeerCmd(TRUE,"Review",rev_type);
           pc->SetReview(pno,"" );
           pc->execute();
           break;
         } 
      }
    }
    else {
      msg = "No proposal reviews available for ";
      msg += pno;
      theErrorMessage->DisplayMessage(msg.c_str());
    }
  }
  else {
    msg = "Please select one and only one proposal.";
    theErrorMessage->DisplayMessage(msg.c_str());
  }


  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),False);
  
  return;	
}
