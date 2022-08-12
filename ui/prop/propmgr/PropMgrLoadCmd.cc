
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrLoadCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the  Proposal Planning 
        email forms class. This class displays the list of
	forms options.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#include <unistd.h>
#include <stdlib.h>
#include <fstream>
#include <sys/param.h>
#include <Xm/Xm.h>
#include <Xm/Form.h>
#include <Xm/TextF.h>

#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"
#include "PropMgrLoadCmd.hh"
#include "PropHelpCodes.h"

// gui extensions
#include "ToolTip.hh"
#include "TextField.hh"
#include "RowColumn.hh"
#include "ToggleButton.hh"
#include "Label.hh"

// general library
#include "GUIDefs.h"
#include "FileUtils.hh"
#include "ObsUtil.hh"


static const char *status_opts[] = {
  "PROPOSED",
  "APPROVED",
  "REJECTED",
  };
static const char *labels[] = {
	"(No Parameter.)",
	"Panel Name: ",
	"Proposal Number: ",
	"Proposal Type :\n(wildcard='%')",
	"P.I. Last Name :\n(wildcard='%')",
	"CoI Last Name :\n(wildcard='%')",
	"Target Name :\n(wildcard='%')",
	"(No Parameter.)",
	"Institute :\n(wildcard='%')",
	"Institute :\n(wildcard='%')",
	"Subject Category :\n(wildcard='%') ",
};

static const char *bylabel[] = {
 	"All",
	"By Panel",
	"By Proposal Number",
	"By Proposal Type",
	"By P.I. Last Name",
	"By CoI Last Name",
	"By Target Name",
	"By Joint",
	"By P.I. Institute",
	"By CoI Institute",
	"By Subject Category ",
};


static enum LOAD_TYPES types[] = {
  LOAD_ALL,
  LOAD_PANEL,
  LOAD_PROPOSAL,
  LOAD_PROPOSAL_TYPE,
  LOAD_PI,
  LOAD_COI,
  LOAD_TARGNAME,
  LOAD_JOINT,
  LOAD_INSTITUTE,
  LOAD_COI_INSTITUTE,
  LOAD_CATEGORY,
 };

// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrLoadCmd::PropMgrLoadCmd ( int active,const char *name)
        : GeneralDialogCmd ((char *)name,active,this,PROPMGR_RETRIEVE_HTML,NULL)

{
   int ii;
   SetMenuFields ( 'R');


   for (ii=0;ii<NBR_STATUS_OPTS;ii++)
     statbtn[ii] = NULL;
   for (ii=0;ii<NBR_POPTS;ii++)
     tbtn[ii] = NULL;
   rowcol_w   = NULL;
   status_w   = NULL;
   ao_label   = NULL;
   ao_w       = NULL;
   form_label = NULL;
   warn_label = NULL;
   text_label = NULL;
   textw = NULL;

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrLoadCmd::~PropMgrLoadCmd()
{
  int ii;

  for (ii=0; ii < NBR_STATUS_OPTS; ii++)
      delete statbtn[ii];
  for (ii=0; ii < NBR_POPTS; ii++)
      delete tbtn[ii];

  delete warn_label;
  delete form_label;
  delete text_label;
  delete textw;
  delete rowcol_w;
  delete status_w;
  delete ao_w;
  delete ao_label;

}


// ----------------------------------------------------------
// Virtual: Update form to display current data
// ----------------------------------------------------------
void PropMgrLoadCmd::UpdateGeneralForm()
{
}

// ----------------------------------------------------------
// Virtual: Create form to display constraints data
// ----------------------------------------------------------
void PropMgrLoadCmd::CreateGeneralForm()
{
  Widget form1;
  int    right_pos = 25;

  form1 = XtVaCreateManagedWidget((char *)"LoadForm",
        xmFormWidgetClass,gc_main_form,
        XmNtopAttachment , XmATTACH_FORM,
	XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_FORM,
	XmNleftOffset,GEN_FORM_OFFSET,
        XmNbottomAttachment , XmATTACH_FORM,
	XmNbottomOffset,GEN_FORM_OFFSET,
        XmNrightAttachment , XmATTACH_FORM,
	XmNrightOffset,GEN_FORM_OFFSET,
        NULL);

  ao_label = new Label(form1,(char *)"AO: ",0,0,0,NULL);

  XtVaSetValues(ao_label->baseWidget(),
        XmNtopAttachment , XmATTACH_FORM,
	XmNtopOffset, GEN_FORM_OFFSET+5,
        XmNrightAttachment , XmATTACH_POSITION,
	XmNrightPosition,right_pos,
        NULL);
   

  ao_w = new TextField(form1,NULL,5,5,NULL,NULL,0,0,0,NULL);
  theToolTipManager->NewToolTip(
	(char *)"Retrieve proposals for specified AO cycle.",
  	ao_w->baseWidget());
  XtVaSetValues(ao_w->baseWidget(),
        XmNtopAttachment , XmATTACH_FORM,
        XmNtopOffset , GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_WIDGET,
        XmNleftWidget , ao_label->baseWidget(),
	NULL);
  char *ptr = getenv (PROP_AO_ENV);
  if (ptr)
    ao_w->Set(ptr);

  warn_label = new Label(form1, (char *)"Status: ",
	0,0,0,NULL);
  XtVaSetValues(warn_label->baseWidget(),
        XmNtopAttachment , XmATTACH_WIDGET,
        XmNtopWidget , ao_w->baseWidget(),
	XmNtopOffset, GEN_FORM_OFFSET,
        XmNrightAttachment , XmATTACH_POSITION,
	XmNrightPosition,right_pos,
        NULL);
  status_w = new RowColumn(form1,False,0,0,0,(char *)"StatusRC",
        XmHORIZONTAL);
  theToolTipManager->NewToolTip((char *)"Retrieve proposals for specified status.",
  	status_w->baseWidget());
  XtVaSetValues(status_w->baseWidget(),
        XmNradioBehavior, True,
        XmNradioAlwaysOne, False,
	XmNindicatorType, XmONE_OF_MANY,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,warn_label->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget , ao_w->baseWidget(),
	NULL);
  statbtn[0] = new ToggleButton( status_w->baseWidget(), (char *)"PROPOSED",
        NULL,NULL, 0,0,0,(char *)"PROPOSED");
  statbtn[1] = new ToggleButton( status_w->baseWidget(), (char *)"APPROVED",
        NULL,NULL, 0,0,0,(char *)"APPROVED");
  statbtn[2] = new ToggleButton( status_w->baseWidget(), (char *)"REJECTED",
        NULL,NULL, 0,0,0,(char *)"REJECTED");
  for (int ii = 0;ii<3;ii++)
    theToolTipManager->NewToolTip(
	(char *)"Retrieve proposals for specified status.",
  	statbtn[ii]->baseWidget());

  statbtn[0]->SetState(TRUE,FALSE);

  form_label = new Label(form1,(char *)"Search: ",0,0,0,NULL);
  XtVaSetValues(form_label->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget , status_w->baseWidget(),
        XmNtopOffset,GEN_FORM_OFFSET,
        XmNrightAttachment , XmATTACH_POSITION,
	XmNrightPosition,right_pos,
        NULL);


  rowcol_w = new RowColumn(form1,False,0,0,0,
	(char *)"LoadRC", XmHORIZONTAL);
  theToolTipManager->NewToolTip(
	(char *)"Additional proposal retrieve criteria.",
        rowcol_w->baseWidget());
  XtVaSetValues(rowcol_w->baseWidget(),
        //XmNindicatorType, XmN_OF_MANY,
        XmNradioBehavior, True,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,form_label->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget , status_w->baseWidget(),
        XmNtopOffset,GEN_FORM_OFFSET,
        NULL);

  int ll=0;
  for (ll=0;ll<NBR_POPTS;ll++) {
    tbtn[ll] = new ToggleButton( rowcol_w->baseWidget(), (char *)bylabel[ll],
        &PropMgrLoadCmd::ToggleCB,this, 0,0,0);
    theToolTipManager->NewToolTip(
	(char *)"Additional proposal retrieve criteria.",
        tbtn[ll]->baseWidget());
  }
  tbtn[0]->SetState(True,False);

  text_label = new Label(form1,(char *)labels[0],0,0,0,NULL);
  XtVaSetValues(text_label->baseWidget(),
        XmNtopAttachment , XmATTACH_WIDGET,
        XmNtopWidget , rowcol_w->baseWidget(),
	XmNtopOffset, GEN_FORM_OFFSET+5,
        XmNrightAttachment , XmATTACH_POSITION,
	XmNrightPosition,right_pos,
        NULL);
   

  textw = new TextField(form1,NULL,256,20,NULL,NULL,0,0,0,NULL);
  theToolTipManager->NewToolTip(
	(char *)"Specifiy criteria for retrieving proposals.",
        textw->baseWidget());
  XtVaSetValues(textw->baseWidget(),
        XmNtopAttachment , XmATTACH_WIDGET,
        XmNtopWidget , rowcol_w->baseWidget(),
        XmNtopOffset , GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_WIDGET,
        XmNleftWidget , text_label->baseWidget(),
        //XmNrightAttachment , XmATTACH_FORM,
	XmNuserData,this,
        NULL);

   

  UpdateGeneralForm();

}
// ----------------------------------------------------------
// Accept 
// ----------------------------------------------------------
void PropMgrLoadCmd::ok_cb(void *)
{
  char *param = NULL;
  int  ii;
  string ao,status;

  ao = ao_w->Get();
  for (ii=0; ii < NBR_STATUS_OPTS; ii++) {
    if (statbtn[ii]->GetState()) {
      status = status_opts[ii];
      break;
    }
  }
  for (ii=0; ii < NBR_POPTS; ii++) {
    if (tbtn[ii]->GetState()) {
      if (ii != 0) 
         param = textw->Get();
      break;
    }
  }
  thePropMgrWindow->GetPropList()->RebuildList(types[ii],param,
	ao.c_str(),status.c_str());
}



// ---------------------------------------------------------
// value changed callback for toggle button
// ---------------------------------------------------------
void PropMgrLoadCmd::ToggleCB(Widget ,XtPointer cbData,XtPointer )
{
  PropMgrLoadCmd *obj = (PropMgrLoadCmd *)cbData;
  int ii;
  for (ii=0; ii < NBR_POPTS; ii++) {
    if (obj->tbtn[ii]->GetState())
     obj->text_label->SetLabel((char *)labels[ii]);
     
  }
}

