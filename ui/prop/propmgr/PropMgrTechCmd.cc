
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrTechCmd.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the Technical Evaluation
	class. 


* NOTES:

* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <Xm/Xm.h>
#include <Xm/Text.h>


// gui extension library
#include "GUIDefs.h"
#include "ToolTip.hh"
#include "ErrorMessage.hh"
#include "QuestionDialogManager.hh"

#include "ProposalDefs.hh"
#include "ProposalEntry.hh"

#include "PropMgrTechCmd.hh"
#include "PropHelpCodes.h"
#include "PropMgrApp.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"



// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrTechCmd::PropMgrTechCmd (int active,char *name) : 
            GeneralDialogCmd (name,active,this,PROPMGR_EDIT_TECH_HTML,NULL)
{


  if (strstr(name,"Tech") != 0)
        SetMenuFields ( 'T',NULL,NULL);
    
  st = NULL;
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrTechCmd::~PropMgrTechCmd()
{
  delete st;
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropMgrTechCmd::CreateGeneralForm()
{
  st = new ScrollText(gc_main_form,NULL,0,80,25,NULL,NULL,
        0,0,0,NULL);

  theToolTipManager->NewToolTip(
	"View/edit technical review for the specified proposal.", 
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
// ----------------------------------------------------------
void PropMgrTechCmd::UpdateGeneralForm()
{

  string  propno;
  int     cnt;
  char    *ptr = NULL;
  ostrstream str;

  XtUnmanageChild(st->baseWidget());

  propno = thePropMgrWindow->GetCurrentProposalNumber();
  if (propno.length() > 0) {
    str.clear();
    thePropMgrWindow->GetPropList()->PrintReview(TECH_REV,str,propno.c_str());

    ptr = str.str();
    cnt= str.pcount();
    if (cnt < strlen(ptr));
      ptr[cnt] = '\0';

    st->Set(ptr);
    if (ptr)
      delete ptr;
  }
  else {
    gc_error_flag = TRUE;
    theErrorMessage->DisplayMessage(
	"Please select one proposal from the proposal list,\nthen try again.");
  }
  


  XtManageChild(st->baseWidget());
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropMgrTechCmd::ok_cb(void *clientData)
{
  theQuestionDialogManager->post ( 0,
        "Are you sure you want to update the database for this\ntechnical evaluation?  Press Ok to confirm.\n",
        (void *) this, &PropMgrTechCmd::confirm_cb, 
        &QuestionDialogManager::null_cb, NULL);


}
// ----------------------------------------------------------
// ----------------------------------------------------------
void PropMgrTechCmd::confirm_cb(void *clientData)
{
  PropMgrTechCmd *obj = (PropMgrTechCmd *)clientData;
  string    errmsg;
  char *ptr;
  char *s1;
  char *s2;
  char *hdr;
  int  retval = -1;
  long pid;
  Boolean stat = TRUE;

  ptr  = obj->st->Get();
  s1 = strstr(ptr,"Reviewer :");
  // this will let us always use the ptr to prepend the header info
  // to the stream for the print routine
  if (s1) 
    *(s1-1) = '\0';

  while(s1) {
    strstream str;
    s2 =  strstr((s1+1),"Reviewer :");
    if (s2)
      *s2 = '\0';
    str << ptr <<"\n" << s1;

    ProposalReview prev(theApplication->GetDBConnection());
    retval = prev.ReadReviewForm(str,errmsg,TRUE); 
    if (retval != 0) {
      stat = FALSE;
    }
    else {
      pid = prev.GetProposalId();
    }
   
    if (s2)
      *s2 = 'R';
    s1 = s2;
  }

  if (stat)
      thePropMgrWindow->GetPropList()->UpdateReviews(pid);
  else
      theErrorMessage->DisplayMessage((char *)errmsg.c_str());
  

}


