
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel manager class which
	displays all panel members


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


// gui extension library
#include "ap_common.hh"
#include "GUIDefs.h"
#include "ErrorMessage.hh"
#include "ToolTip.hh"
#include "Label.hh"
#include "PushButton.hh"


#include "ObsUtil.hh"
#include "PropHelpCodes.h"
#include "PanelApp.hh"
#include "PanelAllMembers.hh"
#include "PanelMemberList.hh"
#include "PanelMenuWindow.hh"
#include "PanelArray.hh"
#include "PanelEntry.hh"


static const char *LIST_LBL  = "Panel Name                 Type             #Pri #Sec   Conflicts";

// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelAllMembers::PanelAllMembers (int active,const char *name) : 
                     GeneralDialogCmd (name,active,this, -1,NULL,
			FALSE,TRUE,TRUE,"Print...")
{

  plist = NULL;
    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelAllMembers::~PanelAllMembers()
{
  delete plist;

}

// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAllMembers::CreateGeneralForm()
{
  Widget paned_w;
  Widget form1_w;


  // create the window pain container
  paned_w = XtVaCreateManagedWidget("PanelMemberPane",
        xmPanedWindowWidgetClass,gc_main_form,
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        XmNuserData,this,
        NULL);

  // member list data
  form1_w = XtVaCreateManagedWidget("PanelMemberForm",
        xmFormWidgetClass,paned_w,
        NULL);
  plbl = new Label(form1_w,(char *)LIST_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(plbl->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);

  plist = new PanelMemberList(form1_w,this);
  theToolTipManager->NewToolTip(
	"List all members currently assigned to panels.",plist->baseWidget());


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
void PanelAllMembers::AddActionButtons()
{
/*
  print_w = new PushButton(gc_dialog,
        "Print...",
        &PanelAllMembers::PrintCB,this, 0,0,0,NULL);
  theToolTipManager->NewToolTip(
        (char *)"Print member list .",
        print_w->baseWidget());
  gc_btn_list.Append(print_w->baseWidget());
*/
}

// ----------------------------------------------------------
void PanelAllMembers::ok_cb( void * clientData)
{

  PanelAllMembers *obj = (PanelAllMembers *)clientData;
  string  tmp;
  char tname[FILENAME_MAX];

  get_tmppath(tmp);
  sprintf(tname,"%s/.panXXXXXXXX",tmp.c_str());

  int filedes = mkstemp(tname);
  if ( filedes > 0) {
    close (filedes);
  }
  ofstream ofile(tname,ios::out);
  ofile << LIST_LBL << endl;
  ofile << "--------------------------------------------------------------------" << endl;
  obj->plist->Print(ofile);

  ofile.close();
  chmod(tname,0600);

  if (!obj->printcmd) {
    obj->printcmd = new PropPrintFile(True,(void*)0,0);
  }
  obj->printcmd->SetFilename((const char *)tname,TRUE);
  obj->printcmd->execute();


}
// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAllMembers::UpdateGeneralForm()
{
  PanelArray *pa;
  PanelEntry *pe;
  Boolean del_flag = TRUE;
  
  pa = thePanelWindow->GetPanelArray();
  if (pa) {
     for (int ii=0;ii<pa->GetSize();ii++) {
       pe = pa->GetRecordbyIndex(ii);
       if (pe) {
         pe->ReloadMembers();
         plist->BuildList(pe,del_flag,FALSE);
         del_flag = FALSE;
       }
     }
  }

}

// ----------------------------------------------------------
// OK callback
// ----------------------------------------------------------
void PanelAllMembers::PrintCB( Widget,XtPointer ,XtPointer )
{
}

