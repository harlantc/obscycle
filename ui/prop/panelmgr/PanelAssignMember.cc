
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel manager class which
	assigns users to a panel.


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
#include "ToolTip.hh"
#include "PushButton.hh"
#include "GUIDefs.h"
#include "QuestionDialogManager.hh"
#include "ErrorMessage.hh"
#include "Label.hh"


#include "ObsUtil.hh"
#include "PropHelpCodes.h"
#include "PanelApp.hh"
#include "PanelAssignMember.hh"
#include "PanelUserList.hh"
#include "PanelMenuWindow.hh"
#include "PanelFindCmd.hh"


static char *USER_LIST_LBL  = (char *)
"                                 Years    Years  Current\nLast Name       First Name       Served   Asked  Panel   Type         Institution";


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelAssignMember::PanelAssignMember (int active,char *name) : 
                     GeneralDialogCmd (name,active,this,
			                PANEL_ASSIGN_MEMBER_HTML,NULL,
	False,False,True)
{

  ulist = NULL;
  ulbl  = NULL;
  ucnt  = 0;
  chair_w   = NULL;
  member_w  = NULL;
  vicechair_w  = NULL;
  find_w    = NULL;
  refresh_w = NULL;
  search_pos = 0;

    
  find_cmd = new PanelFindCmd(TRUE,TRUE);
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelAssignMember::~PanelAssignMember()
{
  delete ulist;
  delete ulbl;

  delete chair_w;
  delete vicechair_w;
  delete member_w;
  delete find_w;
  delete refresh_w;

  delete find_cmd;
}


// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAssignMember::CreateGeneralForm()
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
  ulbl = new Label(form1_w,USER_LIST_LBL,
        0,0,0,(char *)"ListLabel",XmALIGNMENT_BEGINNING);
  XtVaSetValues(ulbl->baseWidget(),
        XmNtopAttachment,XmATTACH_FORM,
        XmNleftAttachment,XmATTACH_FORM,
        NULL);

  ulist = new PanelUserList(form1_w,this);
  theToolTipManager->NewToolTip(
	(char *)"List of possible panel members",
        ulist->baseWidget());

  XtVaSetValues(XtParent(ulist->baseWidget()),
        XmNtopAttachment,XmATTACH_WIDGET,
        XmNtopWidget,ulbl->baseWidget(),
        XmNleftAttachment,XmATTACH_FORM,
        XmNrightAttachment,XmATTACH_FORM,
        XmNbottomAttachment,XmATTACH_FORM,
        NULL);



  UpdateGeneralForm();
}

void PanelAssignMember::AddActionButtons()
{
  chair_w = new PushButton(gc_btn_form,
	"Assign\nChair",
	&PanelAssignMember::AssignChairCB,this,
	0,0,0,NULL);
  theToolTipManager->NewToolTip(
	(char *)"Assign member to panel as Chairperson.",
        chair_w->baseWidget());
  gc_btn_list.Append(chair_w->baseWidget());

  vicechair_w = new PushButton(gc_btn_form,
	(char *)"Assign\nDeputy Chair",
	&PanelAssignMember::AssignDeputyChairCB,this,
	0,0,0,NULL);
  theToolTipManager->NewToolTip((char *)"Assign Deputy Chair to panel.",
        vicechair_w->baseWidget());
  gc_btn_list.Append(vicechair_w->baseWidget());

  member_w = new PushButton(gc_btn_form,
	(char *)"Assign\nReviewer",
	&PanelAssignMember::AssignMemberCB,this,
	0,0,0,NULL);
  theToolTipManager->NewToolTip((char *)"Assign member to panel.",
        member_w->baseWidget());
  gc_btn_list.Append(member_w->baseWidget());


  find_w = new PushButtonInterface(gc_btn_form,
	find_cmd,0);
  theToolTipManager->NewToolTip("Find string in list.",
        find_w->baseWidget());
  gc_btn_list.Append(find_w->baseWidget());

  refresh_w = new PushButton(gc_btn_form,
	"Refresh",
	&PanelAssignMember::RefreshCB,this,
	0,0,0,NULL);
  theToolTipManager->NewToolTip("Refresh current list from database.",
        refresh_w->baseWidget());
  gc_btn_list.Append(refresh_w->baseWidget());
  print_w = new PushButton(gc_btn_form,
        "Print...",
        &PanelAssignMember::PrintCB,this, 0,0,0,NULL);
  theToolTipManager->NewToolTip(
        (char *)"Print member list .",
        print_w->baseWidget());
  gc_btn_list.Append(print_w->baseWidget());



}
// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAssignMember::UpdateGeneralForm()
{
  ulist->BuildList();

}

// ----------------------------------------------------------
// OK callback
// ----------------------------------------------------------
void PanelAssignMember::ok_cb(void *)
{
  char buffer[256];
  PanelEntry *pe;
  PersonEntry *person;
  string str;

  pe = thePanelWindow->GetCurrentPanel();

  if (pe) {
    if (strcmp(type_update.c_str(),P_ASSIGN_CHAIR) == 0) {
      person = pe->GetChair();
      if (person) {
        str = "\n\nThis will replace the current Chair: ";
        str.append(person->GetLast());
      }
    }
    if (strcmp(type_update.c_str(),P_ASSIGN_DEPUTYCHAIR)==0) {
      person = pe->GetDeputyChair();
      if (person) {
        str = "\n\nThis will replace the current Deputy Chair: ";
        str.append(person->GetLast());
      }
    }
       
    sprintf( buffer,"Are you sure you want to assign the selected user(s) as \n'%s'  to Panel %s ?",this->type_update.c_str(),pe->GetPanelName());
    str.insert(0,buffer);
    theQuestionDialogManager->post ( 0,(char *)str.c_str(),
   	(void *) this, &PanelAssignMember::yesCallback, 
	&QuestionDialogManager::null_cb, NULL);
  }
  else
    theErrorMessage->DisplayMessage(
	"Please select a panel before assigning members.\n");

}
void PanelAssignMember::AssignChairCB(Widget,XtPointer clientData,XtPointer )
{
  PanelAssignMember *obj = (PanelAssignMember *)clientData;

  obj->type_update = P_ASSIGN_CHAIR;
  obj->ok_cb(NULL);
}

void PanelAssignMember::AssignDeputyChairCB(Widget,XtPointer clientData,XtPointer )
{
  PanelAssignMember *obj = (PanelAssignMember *)clientData;

  obj->type_update = P_ASSIGN_DEPUTYCHAIR;
  obj->ok_cb(NULL);
}

void PanelAssignMember::AssignMemberCB(Widget,XtPointer clientData,XtPointer)
{
  PanelAssignMember *obj = (PanelAssignMember *)clientData;

  obj->type_update = P_ASSIGN_MEMBER;
  obj->ok_cb(NULL);
}

void PanelAssignMember::RefreshCB(Widget,XtPointer clientData,XtPointer )
{
  PanelAssignMember *obj = (PanelAssignMember *)clientData;

  obj->ulist->BuildList(TRUE);
}
// ----------------------------------------------------------
void PanelAssignMember::PrintCB( Widget,XtPointer clientData,XtPointer )
{
  PanelAssignMember *obj = (PanelAssignMember *)clientData;
  string  tmp;

  char tname[FILENAME_MAX];

  get_tmppath(tmp);
  sprintf(tname,"%s/.panXXXXXXXX",tmp.c_str());

  int filedes = mkstemp(tname);
  if ( filedes > 0) {
    close (filedes);
  }

  ofstream ofile(tname,ios::out);

  ofile << USER_LIST_LBL << endl;
  ofile << "---------------------------------------------------------------------" << endl;
  obj->ulist->Print(ofile);

  ofile.close();
  chmod(tname,0600);

  if (!obj->printcmd) {
    obj->printcmd = new PropPrintFile(True,(char*)0,0);
  }
  obj->printcmd->SetFilename((const char *)tname,TRUE);
  obj->printcmd->execute();


}



// ----------------------------------------------------------
// ----------------------------------------------------------
void PanelAssignMember::yesCallback(void *clientData)
{
  PanelAssignMember *obj = (PanelAssignMember *)clientData;
  PanelEntry *pe;


  pe = thePanelWindow->GetCurrentPanel();
  obj->ulist->Assign(pe,obj->type_update);

  thePanelWindow->GetMemberList()->BuildList(pe);
}

// find string in scrolled text area
Boolean PanelAssignMember::Find(char *str,Boolean case_flag,Boolean start_flag)
{
  if (start_flag)
    search_pos = 0;
    
  return (ulist->SetMatchingItem(str,&search_pos,FALSE,case_flag));
}

