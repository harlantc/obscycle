
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelFindProp.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the  Panel Manager 
        PanelFindProp class. This class searches the database and
        displays panel info for the entered proposal number.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#include <unistd.h>
#include <stdlib.h>
#include <sys/param.h>
#include <Xm/Xm.h>
#include <Xm/Form.h>
#include <Xm/TextF.h>

#include "PanelMenuWindow.hh"
#include "PanelFindProp.hh"
#include "PanelViewChecks.hh"

// gui extensions
#include "ToolTip.hh"
#include "Label.hh"
#include "TextField.hh"

// general library
#include "GUIDefs.h"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelFindProp::PanelFindProp ( const char *name, int active)
        : GeneralDialogCmd (name,active,this,0,NULL)

{

   search_w     = NULL;
   search_label = NULL;
   search_label2 = NULL;
   view_results = NULL;
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelFindProp::~PanelFindProp()
{
  delete search_w;
  delete search_label;
  delete search_label2;
  delete view_results;

}


// ----------------------------------------------------------
// Virtual: Update form to display current data
// ----------------------------------------------------------
void PanelFindProp::UpdateGeneralForm()
{
}

// ----------------------------------------------------------
// Virtual: Create form to display constraints data
// ----------------------------------------------------------
void PanelFindProp::CreateGeneralForm()
{

  search_label = new Label(gc_main_form,(char *)"Proposal Number :",0,0,0,NULL);
  search_w = new TextField(gc_main_form,NULL,0,8,NULL,NULL,0,
      0,0,NULL,TRUE);
   theToolTipManager->NewToolTip(
      (char *)"Find Panel for entered Proposal Number", search_w->baseWidget());
   GUI_AttachLeftWidget(search_label->baseWidget(),
      search_w->baseWidget(),NULL,NULL,GEN_FORM_OFFSET);

  search_label2 = new Label(gc_main_form,
	(char *)"You may use the '%' character as a wildcard.",0,0,0,NULL);
  XtVaSetValues(search_label2->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,search_w->baseWidget(),
        XmNtopOffset,GEN_FORM_OFFSET+5,
        XmNleftAttachment,XmATTACH_FORM,
        XmNleftOffset,GEN_FORM_OFFSET,
        NULL);



  UpdateGeneralForm();

}
// ----------------------------------------------------------
// Accept 
// ----------------------------------------------------------
void PanelFindProp::ok_cb(void *)
{
  string cmd;
  char * pno;

  if (view_results) {
    delete view_results;
  }
  pno = search_w->Get();
  if (pno && *pno != '\0') {
    cmd = "prop_panel_for_proposal.pl -r -p ";
    cmd +=  pno;
  
    cerr << cmd << endl;

    view_results = new PanelViewChecks("Find Panel",True,cmd.c_str(),False);
    view_results->execute();
  }
  else {
  }


}


// ----------------------------------------------------------
// Cancel 
// ----------------------------------------------------------
void PanelFindProp::cancel_cb(void *)
{


}

