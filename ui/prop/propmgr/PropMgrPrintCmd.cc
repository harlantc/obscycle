
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrPrintCmd.cc

* DEVELOPMENT:  UI

* DESCRIPTION:	This contains the code for the Print option on the
		File pulldown menubar for the Proposal Planning - 
		application.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <Xm/Xm.h>
#include <Xm/Form.h>
#include <Xm/Separator.h>

#include "ToolTip.hh"
#include "TextField.hh"
#include "ToggleButton.hh"
#include "Label.hh"
#include "RowColumn.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"
#include "ErrorMessage.hh"


#include "File_Utils.hh"
#include "ObsUtil.hh"
#include "PropMgrPrintCmd.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrParameter.hh"
#include "PropMgrApp.hh"

static const char *prop_forms[] = {
	"Final Review Format",
        "NASA Form w/Abstract ",
        "Proposal List ",
        "Proposal w/Titles ",
        "Peer Review",
        "Primary Review",
        "Secondary Review",
	};

static const char *tgt_forms[] = {
	"Target List",
        "Targets w/ PI",
        };

static enum PROP_FORMS form_options[] = {
	FINAL_FORM,
        ABSTRACT_FORM,
        PROPLIST_FORM,
        PROPTITLES_FORM,
        PEER_FORM,
        PRI_FORM,
        SEC_FORM,
	};

static enum PROP_FORMS tgt_options[] = {
        PROPLIST_FORM,
        TGT_FORM,
        };

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrPrintCmd::PropMgrPrintCmd (  
        int  active,
	void *client_data,
        int  window_help)
	: PrintFileCmd (active,client_data,window_help)
{
  string tmp;

  tb_array    = NULL;
  rowcol_w    = NULL;
  rowcol2_w   = NULL;
  rowcol3_w   = NULL;
  form_label  = NULL;
  form2_label = NULL;
  all_btn     = NULL;
  select_btn  = NULL;

  form_nbr = XtNumber(form_options);
  tgt_nbr  = XtNumber(tgt_options);

  right_pos = 32;

  get_tmppath(tmp);
  sprintf(pwdfile,"%s/proppXXXXXXXX",tmp.c_str());
  int filedes = mkstemp(pwdfile);
  if (filedes > 0) {
    close(filedes);
  }


}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrPrintCmd::~PropMgrPrintCmd()
{
  delete form_label;

  if (pwdfile) {
    unlink(pwdfile);
  }

  if (tb_array) {
    for (int ii=0; ii < form_nbr + tgt_nbr; ii++)
      delete tb_array[ii];

    delete [] tb_array;
  }

  delete rowcol_w;
  delete rowcol2_w;
  delete rowcol3_w;
  delete form_label;
  delete form2_label;
  delete all_btn;
  delete select_btn;

}

// ------------------------------------------------------------
// Build the fields for the print command form
// ------------------------------------------------------------
void PropMgrPrintCmd::CreateGeneralForm()
{
  int ii;
  Widget sep;

  BuildPrinterField();
  BuildOutputField(printer_w->baseWidget());

  nbr_label = new Label(gc_main_form,(char *)"Which Proposals/\n     Targets  :",
	0,0,0,NULL);
  XtVaSetValues(nbr_label->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,XtParent(text_w->baseWidget()),
        XmNtopOffset,GEN_FORM_OFFSET,
        XmNrightAttachment,XmATTACH_POSITION,
        XmNrightPosition,right_pos,
        NULL);


  rowcol3_w = new RowColumn(gc_main_form,False,0,0,0,(char *)"PrintRC",
        XmHORIZONTAL);
  theToolTipManager->NewToolTip("Use all proposals/targets in list.",
	rowcol3_w->baseWidget());
  XtVaSetValues(rowcol3_w->baseWidget(),
        //XmNindicatorType, XmN_OF_MANY,
        XmNradioBehavior, True,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,nbr_label->baseWidget(),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,XtParent(text_w->baseWidget()),
        XmNtopOffset,GEN_FORM_OFFSET,
        NULL);

  all_btn = new ToggleButton( rowcol3_w->baseWidget(), "All",
        NULL,NULL, 0,0,0,"PrintAll");
  theToolTipManager->NewToolTip("Use all proposals/targets in list.",
	all_btn->baseWidget());

  select_btn = new ToggleButton( rowcol3_w->baseWidget(), "Selected",
        NULL,NULL, 0,0,0,"PrintSelected");
  theToolTipManager->NewToolTip(
	"Use only selected(hilighted)  proposals/targets in list.",
	select_btn->baseWidget());

  sep = XtVaCreateManagedWidget("separator",
        xmSeparatorWidgetClass,gc_main_form,
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,rowcol3_w->baseWidget(),
        XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        NULL);

  form1_w = XtVaCreateManagedWidget("PropPrintForm",
        xmFormWidgetClass,gc_main_form,
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,sep,
	XmNbottomAttachment,XmATTACH_FORM,
        NULL);
  form2_w = XtVaCreateManagedWidget("PropPrintForm",
        xmFormWidgetClass,gc_main_form,
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,sep,
	XmNbottomAttachment,XmATTACH_FORM,
        NULL);


  form_label = new Label(form1_w,(char *)"Proposal Options :",0,0,0,NULL);
  XtVaSetValues(form_label->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);

  rowcol_w = new RowColumn(form1_w,False,0,0,0,(char *)"PrintRC",
        XmVERTICAL);
  theToolTipManager->NewToolTip(
	"Select one or more formats for printing.",rowcol_w->baseWidget());
  XtVaSetValues(rowcol_w->baseWidget(),
        XmNindicatorType, XmN_OF_MANY,
        XmNtopAttachment,XmATTACH_FORM,
        XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,form_label->baseWidget(),
        NULL);

  form2_label = new Label(form2_w,(char *)"Target Options :",0,0,0,NULL);
  XtVaSetValues(form2_label->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);


  rowcol2_w = new RowColumn(form2_w,False,0,0,0,(char *)"PrintRC",
        XmVERTICAL);
  theToolTipManager->NewToolTip(
	"Select one or more formats for printing.",rowcol2_w->baseWidget());
  XtVaSetValues(rowcol2_w->baseWidget(),
        XmNindicatorType, XmN_OF_MANY,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_WIDGET,
        XmNleftWidget,form2_label->baseWidget(),
        XmNleftOffset,10,
        NULL);


  tb_array = new ToggleButton *[form_nbr + tgt_nbr];
  for (ii =0 ; ii < form_nbr; ii++) {
    tb_array[ii] = new ToggleButton( rowcol_w->baseWidget(), prop_forms[ii],
        NULL,NULL, 0,0,0,NULL);
    theToolTipManager->NewToolTip(
	"Select one or more formats for printing.",
	tb_array[ii]->baseWidget());

  }
  // set proposal list option as default
  tb_array[2]->SetState(TRUE,TRUE);

  for (int jj=0; jj < tgt_nbr ; jj++,ii++) {
    tb_array[ii] = new ToggleButton( rowcol2_w->baseWidget(), tgt_forms[jj],
        NULL,NULL, 0,0,0,NULL);
    theToolTipManager->NewToolTip(
	"Select one or more formats for printing.",
	tb_array[ii]->baseWidget());
  }


  UpdateGeneralForm();

}

// ------------------------------------------------------------
void PropMgrPrintCmd::UpdateGeneralForm()
{
  if (thePropMgrWindow->GetPropList()->IsProposalView()) {
    XtUnmanageChild(form2_w);
    XtManageChild(form1_w);
  }
  else {
    XtUnmanageChild(form1_w);
    XtManageChild(form2_w);
  }
  
 
}
// ------------------------------------------------------------
// Virtual callback - Print text and/or screen dump
// ------------------------------------------------------------
void PropMgrPrintCmd::ok_cb(void *)
{
  string filename;
  string command;
  string cmd;
  const char    *tmp;
  int      ii=0;
  char    *penv;
  string tname;
  string pname;
  string str;
  string tmppath;
  Boolean gotone = FALSE;
  Boolean didone = FALSE;
  File_Utils fu;

  GUI_SetBusyCursor(gc_dialog,True);

  // create a temporary filename
  get_tmppath(tmppath);
  filename = tmppath;
  filename.append(".prop_propmgr");

  write_password((char *)theApplication->GetPassword(),pwdfile);

  // If text option is selected, print each object 
  if (text_w->GetState()) {

    // first generate filenames for proposal/target lists
    str = tmppath;
    str.append("prop_list.pmgr");
    pname = fu.File_UniqueName(str.c_str());

    str = tmppath;
    str.append("tgt_list.pmgr");
    tname = fu.File_UniqueName(str.c_str());
 
    // get the printer cmd
    penv = printer_w->Get();
    if (penv == NULL || *penv == '\0')
      command = "| lpr";
    else {
      if (file_w->GetState()) 
        command = "> ";
      else
        command = "| ";
      command.append(penv);
    }

    if (thePropMgrWindow->GetPropList()->IsProposalView()) {
      cmd = "prop_reports -U ";
      cmd.append(theApplication->GetUser());
      tmp = theParameters->GetServer();
      if (tmp && *tmp != '\0') {
        cmd.append(" -S ");
        cmd.append(theParameters->GetServer());
      }
      cmd.append(" -pf ");
      cmd.append(pwdfile);
      cmd.append(" -f  ");
      cmd.append(pname.c_str());
      cmd.append(" -panel all  ");
  
      ofstream plistfile(pname.c_str());
      if (plistfile.good())  {
        thePropMgrWindow->GetPropList()->PrintProposalNumbers(plistfile,
		  all_btn->GetState());
      }
      plistfile.close();
  
      for (ii=0; ii< form_nbr; ii++) {
        if (tb_array[ii]->GetState()) {
           switch (form_options[ii]) {
             case NO_FORM:
             case TGT_FORM:
             case TECH_FORM:
		break;
             case ABSTRACT_FORM:
               cmd.append(" -abstract ");
               gotone = TRUE;
               break;
             case PEER_FORM:
               cmd.append(" -peer ");
               gotone = TRUE;
               break;
             case PRI_FORM:
               cmd.append(" -pri ");
               gotone = TRUE;
               break;
             case SEC_FORM:
               cmd.append(" -sec ");
               gotone = TRUE;
               break;
             case FINAL_FORM:
               cmd.append(" -final ");
               gotone = TRUE;
               break;
             case PROPTITLES_FORM:
               cmd.append(" -titles ");
               gotone = TRUE;
               break;
             case PROPLIST_FORM:
               ofstream ofile(filename.c_str(),ios::out);
               if (ofile.good()) {
                 thePropMgrWindow->GetPropList()->Print(ofile,
			all_btn->GetState());
               }
               ofile << "\f";
               ofile.close();
               Print((char *)filename.c_str());
               didone = TRUE;
               break;
           }
        }
      }
      if (gotone) {
        cmd.append(" ");
        cmd.append(command);
        cmd.append(" &");
        theApplication->Log(cmd);
        system(cmd.c_str());
      }
    }
    else {
      ofstream ofile2(tname.c_str());
      if (ofile2.good())  {
        thePropMgrWindow->GetPropList()->PrintTargetIds(ofile2,all_btn->GetState());
      }
      ofile2.close();
      cmd = "prop_target_reports -U ";
      cmd.append(theApplication->GetUser());
      tmp = theParameters->GetServer();
      if (tmp && *tmp != '\0') {
        cmd.append(" -S ");
        cmd.append(theParameters->GetServer());
      }
      cmd.append(" -pf ");
      cmd.append(pwdfile);
      cmd.append(" -tf  ");
      cmd.append(tname);
      ii = form_nbr;
      for (int jj=0; jj< tgt_nbr; jj++,ii++) {
        if (tb_array[ii]->GetState()) {
          switch (tgt_options[jj]) {
            case NO_FORM:
            case TECH_FORM:
            case PEER_FORM:
            case PRI_FORM:
            case SEC_FORM:
            case ABSTRACT_FORM:
            case FINAL_FORM:
            case PROPTITLES_FORM:
		break;
            case TGT_FORM:
               gotone = TRUE;
               break;
             case PROPLIST_FORM:
               ofstream ofile(filename.c_str(),ios::out);
               if (ofile.good()) {
                 thePropMgrWindow->GetPropList()->Print(ofile,
			all_btn->GetState());
               }
               ofile << "\f";
               ofile.close();
               Print((char *)filename.c_str());
               didone = TRUE;
               break;

          }
        }
      }
      if (gotone) {
        cmd.append(" ");
        cmd.append(command);
        cmd.append(" &");
        theApplication->Log(cmd);
        system(cmd.c_str());
      }
    }

    if (!didone && !gotone) {
      theErrorMessage->DisplayMessage("No options selected. Please select an option.");
  
    }
  }

  // Screen dump option selected
  if (screen_w->GetState())
    {
    filename.append(".xwd");
    ScreenDump(thePropMgrWindow->baseWidget(),(char *)filename.c_str());
    }

  if (!screen_w->GetState() && !text_w->GetState()) {
      theErrorMessage->DisplayMessage("Please select an Output type - Text or Screen Dump.");
  }
   
  GUI_SetBusyCursor(gc_dialog,False);
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrPrintCmd::cancel_cb(void *)
{
}
