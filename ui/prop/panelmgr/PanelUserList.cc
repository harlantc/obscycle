/*H****************************************************************************
* Copyright (c) 1995, 2022 Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager 
	scrolled list of panel members.

* NOTES:



* REVISION HISTORY:


        Ref. No.        Date
        --------        ----
	%I%		%G%


*H****************************************************************************/
#include <stdlib.h>
#include <sys/param.h>
#include <Xm/List.h>
#include <string.h>

#include "ap_common.hh"
#include "ErrorMessage.hh"
#include "MessageAreaList.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "PanelUserList.hh"

#include "PanelApp.hh"
#include "PanelMenuWindow.hh"
#include "PanelLogCmd.hh"
#include "PanelLogMsgCmd.hh"
#include "connect_db.hh"

#include "WarnDialogManager.hh"

// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PanelUserList :: PanelUserList( Widget parent,void *cbData)
	: ScrolledList(parent,XmEXTENDED_SELECT,cbData,
                       0,0,0,(char *)"PanelUserList")
{

  uarray = NULL;

}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PanelUserList ::~PanelUserList()
{
  delete uarray;
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
void 
PanelUserList::SelectAction(XmListCallbackStruct *)
{


}

// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void PanelUserList::DefaultAction(XmListCallbackStruct *)
{
}

  
// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelUserList :: BuildList(Boolean refresh_flg)
{
  int     ii;
  string  mainbuf;
  string  msg;

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),TRUE);
  // delete the existing list
  DeleteAllItems();

  if (refresh_flg) {
     delete uarray;
     uarray = NULL;
  }
  if (!uarray) {
    uarray = new PersonArray(theApplication->GetDBConnection());

    // retrieve persons and display them
    cerr << "loading all persons...." << endl;
    if (uarray->LoadAllCandidates(msg)) {
      cerr << "retrieved " << uarray->GetSize() << " persons." << endl;
    }
    else {
      cerr << msg << endl;
    }
  }

//cerr << "UserList::  need to retrieve years asked/served\n";

  for (ii = 0; ii < uarray->GetSize(); ii++) {
    mainbuf.clear();
    msg.clear();
    if (uarray->GetData(ii,mainbuf,msg)) {
      AddItem((char *)mainbuf.c_str(),0);     
    }
    else {
      theMessageAreaList->SetText((char *)msg.c_str());
    }
  }
  GUI_SetBusyCursor(thePanelWindow->baseWidget(),FALSE);
}


// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PanelUserList :: Sort (int )
{
/*
  uarray->Sort(type);
*/
  BuildList();
}
// ---------------------------------------------------------------
// ---------------------------------------------------------------
void  PanelUserList :: Assign(PanelEntry *panel,string member_type)
{
  int *pos;
  int  cnt;
  int  ii;
  PersonEntry *pe;
  string msg;
  string type;
  string last_name;
  string first_name;
  string inst;
  string no_inst_msg;

  type = member_type;
  if (strstr(panel->GetPanelName(),"P") > 0) {
    if (type.find("Deputy") != STL_NPOS) 
      type = P_ASSIGN_PUNDITDEPUTY;
    else if (type.find("Chair") != STL_NPOS) 
      type = P_ASSIGN_PUNDITCHAIR;
    else
      type = P_ASSIGN_PUNDIT;
  }
  //cerr << "Type in: " << member_type << "   Type out: " << type << endl;

  pos = this->GetSelectedItems(&cnt);
  if ((type.find("Chair") != STL_NPOS && cnt > 1) ||
      (type.find("Deputy") != STL_NPOS && cnt > 1)) {
     msg = "Only 1 person can be selected as ";
     msg += type;
     msg += ".\n";
     theErrorMessage->DisplayMessage((char *)msg.c_str());
  }
  else {
    for (ii=0;ii< cnt;ii++) {
      pe = uarray->GetRecordbyIndex(pos[ii]-1);
      // Warn if panelist has no instituion
      if (panel->AddMember(pe,type.c_str())) {
        last_name = pe->GetLast();
        first_name = pe->GetFirst();
        inst = pe->GetInstitution();
        if (inst.empty() || inst.find_first_not_of(' ') == std::string::npos){
          no_inst_msg += first_name + " " + last_name; 
        }

        msg = "Added ";
        msg += last_name;
        msg += " to panel ";
        msg += panel->GetPanelName();
        msg += " --";
        msg += type;
        theApplication->Log(msg);
        if (((PanelLogCmd *)(thePanelWindow->log_cmd()))->IsActive()) {
          thePanelWindow->thelogmsg_cmd()->SetMessage(msg);
          thePanelWindow->thelogmsg_cmd()->execute();
        }
      }
      else {
        msg = "Error occurred adding panel member.\n";
        msg += panel->GetErrors();
        theErrorMessage->DisplayMessage((char *)msg.c_str());
      }
    }
    if (!no_inst_msg.empty()){
      no_inst_msg.insert(0,"The following panelist has no institution. "
                           "Adding them as such will result in them having "
                           "conflicts with all users in PAS.\n\n");
      theWarnDialogManager->post(0,(char*)no_inst_msg.c_str(),NULL,NULL,NULL,NULL);
    }
    
    
  }


}


