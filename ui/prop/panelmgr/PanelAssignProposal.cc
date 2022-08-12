
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel manager class which
	assigns proposals to a panel.


* NOTES:

* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <Xm/Xm.h>
#include <Xm/Form.h>
#include <Xm/Label.h>
#include <Xm/PanedW.h>

#include "ap_common.hh"

// gui extension library
#include "PushButton.hh"
#include "GUIDefs.h"
#include "QuestionDialogManager.hh"
#include "ErrorMessage.hh"
#include "ToolTip.hh"
#include "Label.hh"

// database

#include "PropHelpCodes.h"
#include "PanelApp.hh"
#include "PanelAssignProposal.hh"
#include "PanelProposalList.hh"
#include "PanelMenuWindow.hh"


static char *LIST_LBL  =  (char*)
"Proposal   PI                     Institution                #Tgts   CoIs";


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelAssignProposal::PanelAssignProposal (int active,char *name) : 
                     GeneralDialogCmd (name,active,this,
			                PANEL_ASSIGN_PROP_HTML,NULL)
{

  plist = NULL;
    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelAssignProposal::~PanelAssignProposal()
{
  delete plist;

}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAssignProposal::CreateGeneralForm()
{
  Widget paned_w;
  Widget form1_w;


  // create the window pain container
  paned_w = XtVaCreateManagedWidget("PanelProposalPane",
        xmPanedWindowWidgetClass,gc_main_form,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        XmNuserData,this,
        NULL);

  // member list data
  form1_w = XtVaCreateManagedWidget("PanelProposalForm",
        xmFormWidgetClass,paned_w,
        NULL);
  plbl = new Label(form1_w,LIST_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(plbl->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);

  plist = new PanelProposalList(form1_w,this);
  theToolTipManager->NewToolTip(
	"List of proposals not yet assigned to a panel.",plist->baseWidget());


  XtVaSetValues(XtParent(plist->baseWidget()),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,plbl->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);

  UpdateGeneralForm();
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAssignProposal::UpdateGeneralForm()
{
  plist->BuildList();

}

// ----------------------------------------------------------
// OK callback
// ----------------------------------------------------------
void PanelAssignProposal::ok_cb(void *clientData)
{
  char buffer[256];
  PanelEntry *pe;
  int   cnt;

  pe = thePanelWindow->GetCurrentPanel();
  plist->GetSelectedItems(&cnt);

  if (pe && cnt > 0) {

    sprintf( buffer,"Are you sure you want to assign the selected proposal(s) \nto the current panel %s ?",pe->GetPanelName());
    theQuestionDialogManager->post ( 0,buffer,
   	(void *) this, &PanelAssignProposal::yesCallback, 
	&QuestionDialogManager::null_cb, NULL);
  }
  else
    theErrorMessage->DisplayMessage(
	"Please select a panel before assigning proposals.\n");

}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAssignProposal::yesCallback(void *clientData)
{
  PanelAssignProposal *obj = (PanelAssignProposal *)clientData;
  PanelEntry *pe;
  string pname;


  pe = thePanelWindow->GetCurrentPanel();
  pname = pe->GetPanelName();
  obj->plist->Assign(pe);
  pe->Retrieve(pname.c_str());

  thePanelWindow->DisplayPanels();
  thePanelWindow->GetPropList()->BuildList(NULL);

  if (!obj->type)
    obj->UpdateGeneralForm();

}

