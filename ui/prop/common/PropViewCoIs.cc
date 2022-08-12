
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the proposal class which
	displays a list of Co-Investigators for proposals .


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
#include <stdio.h>
#include <fstream>

#include "ap_common.hh"

// gui extension library
#include "ToolTip.hh"
#include "PushButton.hh"
#include "GUIDefs.h"
#include "Label.hh"

// proposal library
#include "ObsUtil.hh"
#include "PersonArray.hh"

#include "PropHelpCodes.h"
#include "PropViewCoIs.hh"
#include "PropCoIList.hh"




// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropViewCoIs::PropViewCoIs (int active,Widget main_window,const char *name) : 
	GeneralDialogCmd (name,active,this,
		PANEL_VIEW_COIS_HTML,NULL,FALSE,TRUE)
{

  plist = NULL;
  pbtn = NULL;
  cbtn = NULL;
  ibtn = NULL;
  main_w = main_window;
    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropViewCoIs::~PropViewCoIs()
{
  delete plist;
  delete pbtn;
  delete cbtn;
  delete ibtn;

}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropViewCoIs::CreateGeneralForm()
{
  Widget paned_w;
  Widget form1_w;


  // create the window pain container
  paned_w = XtVaCreateManagedWidget("CoIProposalPane",
        xmPanedWindowWidgetClass,gc_main_form,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        XmNuserData,this,
        NULL);

  // member list data
  form1_w = XtVaCreateManagedWidget("PropCoIForm",
        xmFormWidgetClass,paned_w,
        NULL);
  pbtn = new PushButton(form1_w,
        (char *)"Proposal",
        &PropViewCoIs::SortbyProposal,this,
        0,0,0,NULL);
  theToolTipManager->NewToolTip((char *)"Sort list by proposal number.",
	pbtn->baseWidget());
  XtVaSetValues(pbtn->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);

  cbtn = new PushButton(form1_w,
        (char *)"Co-Investigator",
        &PropViewCoIs::SortbyCoI,this,
        0,0,0,(char*)"CoIBtn");
  theToolTipManager->NewToolTip((char *)"Sort list by CoI name.",
	cbtn->baseWidget());
  XtVaSetValues(cbtn->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,pbtn->baseWidget(),
        XmNleftOffset,10,
        NULL);
  ibtn = new PushButton(form1_w,
        (char *)"Institution",
        &PropViewCoIs::SortbyInstitution,this,
        0,0,0,(char*)"InstBtn");
  theToolTipManager->NewToolTip((char *)"Sort list by CoI institution.",
	ibtn->baseWidget());
  XtVaSetValues(ibtn->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,cbtn->baseWidget(),
        XmNleftOffset,10,
        NULL);

  plist = new PropCoIList(form1_w,gc_dialog,this);
  XtVaSetValues(XtParent(plist->baseWidget()),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,pbtn->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);
  if (plist) 
    plist->SetMain(main_w); 

  gc_ok_w->SetLabel((char *)"Print...");
  UpdateGeneralForm();
}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropViewCoIs::UpdateGeneralForm()
{
  ProposalArray *parray;

  parray = GetProposals();
  if (parray)
    plist->BuildList(parray);

}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PropViewCoIs::ok_cb(void *)
{
  char tname[FILENAME_MAX];
  string tmp;
  string  str;

  get_tmppath(tmp);
  sprintf(tname,"%s/coiXXXXXXXXX",tmp.c_str());
  int filedes = mkstemp(tname);
  if (filedes > 0) {
    close(filedes);
  }

  ofstream ofile(tname,ios::out);
  ofile << "Proposal#   Co-Investigator        Institution\n";
  ofile << "-------------------------------------------------------------------\n";
  plist->Print(ofile);
  ofile.close();
  chmod(tname,0600);


  if (!printcmd) {
    printcmd = new PropPrintFile(True,NULL,0);
  }
  printcmd->SetFilename((const char *)tname,TRUE);
  printcmd->execute();


}


// ----------------------------------------------------------
// ----------------------------------------------------------
void PropViewCoIs::SortbyProposal(Widget,XtPointer clientData,XtPointer )
{
  PropViewCoIs *obj =  (PropViewCoIs *)clientData;

  obj->plist->Sort(PERSON_IDNAME);

}
void PropViewCoIs::SortbyCoI(Widget , 
	XtPointer clientData,XtPointer )
{
  PropViewCoIs *obj =  (PropViewCoIs *)clientData;

  obj->plist->Sort(PERSON_NAME);

}
void PropViewCoIs::SortbyInstitution(Widget , 
	XtPointer clientData,XtPointer )
{
  PropViewCoIs *obj =  (PropViewCoIs *)clientData;

  obj->plist->Sort(PERSON_INST);

}
void PropViewCoIs::SetMain(Widget main_window)
{
  main_w = main_window; 
}

