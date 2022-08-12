
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Management


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <Xm/Xm.h>
#include <Xm/Text.h>

#include "ap_common.hh"
#include "ToolTip.hh"
#include "ErrorMessage.hh"
#include "QuestionDialogManager.hh"

#include "ProposalDefs.hh"
#include "ProposalEntry.hh"

#include "PropHelpCodes.h"
#include "PropMgrPeerCmd.hh"
#include "PropMgrApp.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrPeerCmd::PropMgrPeerCmd ( int active,const char *name,int rtype )
        : GeneralDialogCmd((char *)name,active,this,PROPMGR_EDIT_PEER_HTML,
	                   NULL,True,True)


{

  //SetMenuFields ( ,NULL,NULL);
   st = NULL;
   rev_type = rtype;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrPeerCmd::~PropMgrPeerCmd()
{
  if (st)
    delete st;

}
// ----------------------------------------------------------
// ----------------------------------------------------------
void PropMgrPeerCmd::SetReview(string pno,string pname)
{
  propno = pno;
  panel_name = pname;
}
// ----------------------------------------------------------
// Create a scrolled text widget to display the current log file.
// ----------------------------------------------------------
void PropMgrPeerCmd::CreateGeneralForm()
{

  st = new ScrollText(gc_main_form,NULL,0,80,25,NULL,NULL,
        0,0,0,NULL);
  theToolTipManager->NewToolTip(
	(char *)"View/edit the peer review for the selected proposal.",
 	st->baseWidget());
  

  XtVaSetValues(XtParent(st->baseWidget()),
        XmNtopAttachment ,XmATTACH_FORM,
        XmNleftAttachment ,XmATTACH_FORM,
        XmNrightAttachment ,XmATTACH_FORM,
        XmNbottomAttachment ,XmATTACH_FORM,
        NULL);



  // update data in form
  UpdateGeneralForm();

}
// ----------------------------------------------------------
// Routine to update view of current text file.
// ----------------------------------------------------------
void PropMgrPeerCmd::UpdateGeneralForm()
{

  ostrstream str;
  size_t cnt;
  char *ptr = NULL;

  str.clear();
  thePropMgrWindow->GetPropList()->PrintReview(rev_type,str,propno.c_str(),
	panel_name.c_str());

  ptr = str.str();
  cnt= str.pcount();
  if (cnt < strlen(ptr))
    ptr[cnt] = '\0';

  st->Set(ptr);
  
  if (ptr)
    delete ptr;
  

}


// ----------------------------------------------------------
// Virtual callback function for cancel
// ----------------------------------------------------------
void PropMgrPeerCmd::cancel_cb(void *)
{
}

// ----------------------------------------------------------
// Virtual callback function for ok
// ----------------------------------------------------------

void PropMgrPeerCmd::ok_cb(void *)
{
  theQuestionDialogManager->post ( 0,
	"Are you sure you want to update the database\nfor this peer review?  Press Ok to confirm.\n",
        (void *) this, &PropMgrPeerCmd::confirm_cb, &QuestionDialogManager::null_cb, NULL);

}

void PropMgrPeerCmd::confirm_cb(void *clientData)
{
  PropMgrPeerCmd *obj = (PropMgrPeerCmd *)clientData;
  strstream str;
  string    errmsg;
  char *ptr;
  int  retval;
  long pid;

  ptr  = obj->st->Get();
  str << ptr;

  ProposalReview prev(theApplication->GetDBConnection());
  retval = prev.ReadReviewForm(str,errmsg,TRUE); 
  theApplication->Log(errmsg);
  if (retval != 0) {
    theErrorMessage->DisplayMessage((char *)errmsg.c_str());
  }
  else {
    pid = prev.GetProposalId();
    thePropMgrWindow->GetPropList()->UpdateReviews(pid);
  }

  
}

