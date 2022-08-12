/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPropList.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager 
	scrolled list of proposals.

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
#include "WarnDialogManager.hh"
#include "QuestionDialogManager.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "PanelPropList.hh"
#include "PersonEntry.hh"
#include "stringFcns.hh"

#include "PanelApp.hh"
#include "PanelMenuWindow.hh"
#include "PanelLogCmd.hh"
#include "PanelLogMsgCmd.hh"
#include "ProposalEntry.hh"
#include "ReviewerScores.hh"
#include "connect_db.hh"



// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PanelPropList :: PanelPropList( Widget parent,void *cbData)
	: ScrolledList(parent,XmMULTIPLE_SELECT,cbData,
                       0,0,0,(char *)"PanelPropList")
{

  parray = NULL;
  current_person = NULL;
  current_pe= NULL;
  last_selected_propno = 0;

}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PanelPropList ::~PanelPropList()
{
  if (current_person) {
    delete current_person;
  }
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
void 
PanelPropList::SelectAction(XmListCallbackStruct *cbs)
{
  char    *choice;
  XmStringGetLtoR(cbs->item,XmFONTLIST_DEFAULT_TAG,&choice);

  last_selected_propno = 0;
  if (choice) {
    last_selected_propno =  atoi(choice);
    XtFree(choice);
  }
  thePanelWindow->GetMemberList()->BuildList(thePanelWindow->GetCurrentPanel());

}

// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void PanelPropList::DefaultAction(XmListCallbackStruct *)
{
}

  
// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelPropList :: BuildList(PanelEntry *pe)
{
  int     ii;
  string  mainbuf;
  string  msg;
  int     revID = 0;
  int     cnt=0;
  char    *tmp;
  char    *items = NULL;
  int     pno;
  char    *ptr;

  this->unmanage();
  last_selected_propno = 0;

  //need selected entries if pe is null so we can re-hilite entries
  if (!pe) {
    cnt = GetCurrentItems(&items);
  }


  // delete the existing list
  DeleteAllItems();

  //cerr << "in PanelPropList::BuildList" << endl;
  PersonEntry *curRev = thePanelWindow->GetCurrentMember();
  if (curRev && curRev->GetId() > 0) {
    revID = curRev->GetId();
    //cerr << "got reviewer: " << curRev->GetId() << endl;
  }

  if (pe)  {
    _panel_name = pe->GetPanelName();
    parray = pe->GetProposalList();
    if (!parray || getenv("ASCDS_PROP_REFRESH")) {
      // retrieve proposals and display them
      cerr << "loading proposals for panel...." << endl;
      pe->LoadProposals(NULL,"PROPOSED",1);
      parray = pe->GetProposalList();
      cerr << "retrieved " << parray->GetSize() << " proposals." << endl;
    }
  }


  ReviewerScores *rs = ReviewerScores::Instance();
  double rval = 0.0;
  

  if (parray) {
    for (ii = 0; ii < parray->GetSize(); ii++) {
      string spno;
      ProposalEntry *prop = parray->GetRecordbyIndex(ii);
      prop->GetProposalNumber(spno);
      int propno = atoi(spno.c_str());
      rval = 0.0;
      if (revID >= 0) {
        rval = rs->getReviewerScore(propno,revID);
        //if (rval > 0) 
          //cerr <<  pno <<  " score= " << rval << endl;
      }

      tmp = items;
      int hilite = 0;
      for (int pp=0;pp<cnt;pp++) {
         sscanf(tmp,"%d",&pno);
         if (propno == pno) 
           hilite=1;
         ptr = strchr(tmp,'\n');
         if (ptr) {
          tmp =  ptr;
          tmp++;
         }
      }

      mainbuf= prop->GetPanelProposalItem(_panel_name.c_str(),rval);
      
      if (hilite == 0) 
        AddItem((char *)mainbuf.c_str(),0);     
      else
        AddSelectedItem((char *)mainbuf.c_str(),0);     
    }
  }
  this->manage();
}
// ---------------------------------------------------------------
// ---------------------------------------------------------------
int PanelPropList::RemoveProposals(PanelEntry *old_pa)
{
  int retval = FALSE;
  int ii;
  int cnt;
  ProposalEntry *pe;
  ProposalReview *prev;
  DBProposalReview *peer;
  string  msg;
  string  propno;
  char *tmp;
  char *items = NULL;
  string sitems;
  int  pno;
  char *ptr;

  cnt = GetCurrentItems(&items);
  tmp = items;
  for (ii=0;ii<cnt;ii++) {
    sscanf(tmp,"%d",&pno);
    pe = parray->GetRecordbyProposal(pno);
    pe->GetProposalNumber(propno);
    if (!CheckLinked(&items,cnt,pe))  {
       msg += "Proposal ";
       msg += propno;
       msg += " is linked to ";
       msg += pe->GetDBProposal()->get_linked_propnum();
       msg += "\n";
    }
    ptr = strchr(tmp,'\n');
    if (ptr) {
      tmp =  ptr;
      tmp++;
    }
  }
  if (msg.length() > 0) {
       msg += "\nYou must also remove this proposal from the panel.\n";
       msg += "Proposal was NOT removed.\n"; 
       theApplication->Log(msg);
       theErrorMessage->DisplayMessage(msg.c_str());
  }
  else {

    tmp = items;
    for (ii=0;ii<cnt;ii++) {
      sscanf(tmp,"%d",&pno);
      pe = parray->GetRecordbyProposal(pno);
      pe->GetProposalNumber(propno);
      prev = pe->GetProposalReview();
      if (prev) {
        peer= prev->GetReview(old_pa->GetPanelName());
        if (peer) {
          msg = "Removed reviewers for ";
          msg += propno;
          theApplication->Log(msg);
          prev->RemovePrimaryReviewer(peer);
          prev->RemoveSecondaryReviewer(peer);
          //prev->RemoveTechnicalEvaluator();
          }
      }
      if (old_pa->RemoveProposal(pe->GetProposalId(),propno.c_str())) {
        retval = TRUE;
        msg = "Removed proposal ";
        msg += propno;
        msg += " from panel ";
        msg += old_pa->GetPanelName();
        theApplication->Log(msg);

      }
      else {
        msg = "Error occurred trying to remove proposal ";
        msg += propno;
        msg += " from panel ";
        msg += old_pa->GetPanelName();
        msg += ".\n";
      
        theErrorMessage->DisplayMessage(msg.c_str());
        theApplication->Log(msg);
      }
      ptr = strchr(tmp,'\n');
      if (ptr) {
        tmp =  ptr;
        tmp++;
      }
    }
  }
  if (items)
    free(items);

  return retval;
}


// ---------------------------------------------------------------
// ---------------------------------------------------------------
int PanelPropList::MoveProposals(PanelEntry *old_pa,PanelEntry *new_pa,
	Boolean remove_revs)
{
  int retval = FALSE;
  int ii;
  int cnt;
  ProposalEntry *pe;
  ProposalReview *prev;
  DBProposalReview *peer;
  string  msg,xstr;
  string  propno;
  char *tmp;
  char *items = NULL;
  string sitems;
  int  pno;
  char *ptr;

  cnt = GetCurrentItems(&items);
  tmp = items;
  for (ii=0;ii<cnt;ii++) {
    sscanf(tmp,"%d",&pno);
    pe = parray->GetRecordbyProposal(pno);
    pe->GetProposalNumber(propno);
    if (!CheckLinked(&items,cnt,pe))  {
       msg += "Proposal ";
       msg += propno;
       msg += " is linked to ";
       msg += pe->GetDBProposal()->get_linked_propnum();
       msg += "\n";
    }
    ptr = strchr(tmp,'\n');
    if (ptr) {
      tmp =  ptr;
      tmp++;
    }
  }
  if (msg.length() > 0) {
       msg += "\nYou must also move this proposal to the new panel.\n";
       msg += "Proposal was NOT moved.\n"; 
       theApplication->Log(msg);
       theErrorMessage->DisplayMessage(msg.c_str());
  }
  else {

    tmp = items;
    for (ii=0;ii<cnt;ii++) {
      sscanf(tmp,"%d",&pno);
      pe = parray->GetRecordbyProposal(pno);
      pe->GetProposalNumber(propno);
      prev = pe->GetProposalReview();
      if (remove_revs && prev) {
        peer= prev->GetReview(old_pa->GetPanelName());
        if (peer) {
          msg = "Removed reviewers for ";
          msg += propno;
          theApplication->Log(msg);
          prev->RemovePrimaryReviewer(peer);
          prev->RemoveSecondaryReviewer(peer);
          //prev->RemoveTechnicalEvaluator();
          }
        }
      else {
        if (prev) {
          msg = "Reviewers not removed for ";
          msg += propno;
          theApplication->Log(msg);
        }
      }
      if (old_pa->MoveProposal(pe->GetProposalId(),propno.c_str(),
		new_pa->GetPanelId(),xstr)) {
        retval = TRUE;
        msg = "Moved proposal ";
        msg += propno;
        msg += " from panel ";
        msg += old_pa->GetPanelName();
        msg += " to panel ";
        msg += new_pa->GetPanelName();
        theApplication->Log(msg);
        theApplication->MoveLog(msg);
        theMessageAreaList->SetText((char *)msg.c_str());

        if (((PanelLogCmd *)(thePanelWindow->log_cmd()))->IsActive()) {
          thePanelWindow->thelogmsg_cmd()->SetMessage(msg);
          thePanelWindow->thelogmsg_cmd()->execute();
        }
      }
      else {
        msg = "Error occurred trying to move proposal ";
        msg += propno;
        msg += " from panel ";
        msg += old_pa->GetPanelName();
        msg += ".\nProposal may already exist in Panel ";
        msg += new_pa->GetPanelName();
        msg += ".\n\n";
        msg += xstr ;
      
        theErrorMessage->DisplayMessage(msg.c_str());
        theApplication->Log(msg);
      }
      ptr = strchr(tmp,'\n');
      if (ptr) {
        tmp =  ptr;
        tmp++;
      }
    }
  
  }
  if (items)
    free(items);

  return retval;
}



// ---------------------------------------------------------------
// assign selected proposals to reviewer
// ---------------------------------------------------------------
void PanelPropList::AssignReviewer(int type,PersonEntry *rev,int *props,
		int propcnt)
{
  int ii;
  string ctmp;
  string conflicts;
  int gotone=0;

  if (current_person) {
      delete current_person;
      current_person=NULL;
  }
  peList.clear();


  current_type = type;
  ctmp.clear();
  current_person = new PersonEntry(theApplication->GetDBConnection(),rev->GetId());
  conflicts = "Conflicts found for reviewer.\n";
  conflicts.append("Do you really want to assign ");
  conflicts.append(current_person->GetLast());
  conflicts.append(" to the selected proposals\nwhich contain the following conflicts?\n\n");

  // get all personal conflicts for the member, then check
  list<string> pconflicts ;
  thePanelWindow->GetCurrentPanel()->GetConflictsForMember(rev->GetId(),pconflicts);
  string personal_conflicts;
  stringFcns::join(&pconflicts,"  ",&personal_conflicts);
  //cerr << "PanelPropList " <<  personal_conflicts << endl;

  for (ii=0; ii < propcnt;ii++) {
    ctmp.clear();
    current_pe = parray->GetRecordbyIndex(props[ii] - 1);
    peList.push_back(current_pe);

    string pno;
    current_pe->GetProposalNumber(pno);
    if (strstr(personal_conflicts.c_str(),pno.c_str())) {
      conflicts.append(pno);
      conflicts.append(":Personal " );
      gotone=1;
    }
    current_pe->IsPIConflict(ctmp,ctmp,current_person->GetLast(),
	current_person->GetFirst(),
	current_person->GetInstitution(),	
	current_person->GetEmail());
    current_pe->IsCoIConflict(ctmp,ctmp,current_person->GetLast(),
	current_person->GetFirst(),
	current_person->GetInstitution(),
	current_person->GetEmail());
    if (ctmp.length() > 0) {
       conflicts.append(ctmp);
       conflicts.append("\n");
       gotone = 1;
    }
  }
  if (gotone) {
      theQuestionDialogManager->post_newlabels ( thePanelWindow->baseWidget()
	,conflicts.c_str(),
        (void *) this, &PanelPropList::yesCallback,
	&PanelPropList::noCallback,NULL,(char *)"Yes",(char*)"No",NULL);
    }
  else {
    yesCallback(this);
  }
/*
  if (conflicts.length() > 0) {
    ctmp = "Conflicts found for ";
    ctmp += tmp->GetLast();
    ctmp += "\n\n";
    conflicts.insert(0,ctmp);
    theWarnDialogManager->post(0,(char*)conflicts.c_str(),NULL,NULL,NULL,NULL);
  }
*/

}
// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PanelPropList::yesCallback(void *clientData)
{
  PanelPropList *obj = (PanelPropList *)clientData;
  size_t ii;
  string msg;

  for (ii=0; ii < obj->peList.size();ii++) {
    obj->peList[ii]->AssignReviewer(obj->current_type,
	obj->current_person->GetDBPerson(),(obj->_panel_name).c_str());

    obj->peList[ii]->GetProposalNumber(msg);
    msg += " assigned to ";
    msg.append(obj->current_person->GetLast());
    if (obj->current_type == PRI_REV) {
       msg.append("(primary)");
    }
    else if (obj->current_type == SEC_REV) {
       msg.append("(secondary)");
    }
    theApplication->Log(msg);
    if (((PanelLogCmd *)(thePanelWindow->log_cmd()))->IsActive()) {
      thePanelWindow->thelogmsg_cmd()->SetMessage(msg);
      thePanelWindow->thelogmsg_cmd()->execute();
    }
  }
  obj->BuildList(thePanelWindow->GetCurrentPanel());
  thePanelWindow->GetMemberList()->BuildList(thePanelWindow->GetCurrentPanel());
  //thePanelWindow->DisplayPanels(False,True);


}
// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PanelPropList::noCallback(void *clientData)
{
  PanelPropList *obj = (PanelPropList *)clientData;
  string msg;
  msg = "Reviewer assignment (";
  msg += obj->current_person->GetLast();
  msg += ") is canceled for all selected proposals.";
  theApplication->Log(msg);
  theMessageAreaList->SetText(msg.c_str());

}

// ---------------------------------------------------------------
// ---------------------------------------------------------------
double PanelPropList::CalculateAllottedTime()
{
   double retval = 0.0;

   if (parray)
     retval = parray->CalculateAllottedTime();
 
   return retval;
}

// ---------------------------------------------------------------
// Sort
// ---------------------------------------------------------------
void PanelPropList ::Sort(int stype)
{
  int revID=0;
  PersonEntry *curRev = thePanelWindow->GetCurrentMember();
  if (curRev && curRev->GetId() > 0) {
    revID = curRev->GetId();
  }
  //cerr << "panelPropList: type=" << stype << "    revid=" <<revID << endl;

  if (parray) {
    parray->Sort(&stype,1,_panel_name,revID);
    BuildList(NULL);
  }

}
// ---------------------------------------------------------------
// Sort
// ---------------------------------------------------------------
void PanelPropList ::Sort(int *type,int nbr_options)
{
  int revID=0;
  PersonEntry *curRev = thePanelWindow->GetCurrentMember();
  if (curRev && curRev->GetId() > 0) {
    revID = curRev->GetId();
  }
  //cerr << "panelPropList: type=" << type[0] << "    revid=" <<revID << endl;

  if (parray) {
    parray->Sort(type,nbr_options,_panel_name,revID);
    BuildList(NULL);
  }

}

// ---------------------------------------------------------------
// ---------------------------------------------------------------
string PanelPropList::PrintProposalNumbers(Boolean all_flag)
{
  int *pos;
  int ii;
  int cnt;
  ProposalEntry *pe;
  string  pno;
  string  retstr;

  if (parray)  {

    if (all_flag) {
      for (ii=0; ii<parray->GetSize(); ii++) {
        pe = parray->GetRecordbyIndex(ii);
        pe->GetProposalNumber(pno);
        retstr += pno;
        retstr += "\n";
      }
    }
    else {
      pos = GetSelectedItems(&cnt);
  
      for (ii=0; ii<cnt; ii++) {
        pe = parray->GetRecordbyIndex(pos[ii] - 1);
        if (pe) {
          pe->GetProposalNumber(pno);
          retstr += pno;
          retstr += "\n";
        }
      }
    }
  }
  return retstr;
}


// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PanelPropList::SelectProposals(vector<string> &propno_list)
{

  int start_pos;

  // for some reason, multiple SelectPos doesn't work with EXTENDED
  //XtVaSetValues(baseWidget(), XmNselectionPolicy,XmMULTIPLE_SELECT,NULL);

  for (size_t ii=0;ii<propno_list.size();ii++) {
    start_pos = 0;
    SetMatchingItem((char *)propno_list[ii].c_str(),&start_pos,False,False);
  }

  //XtVaSetValues(baseWidget(),XmNselectionPolicy,XmEXTENDED_SELECT,NULL);
}

Boolean PanelPropList::IsProposalSelected(const char*pno)
{
  int ii;
  Boolean retval = FALSE;
  int *pos;
  int cnt;
  ProposalEntry *pe;
  int cpno = 0;

  
  if (pno) {
    cpno = atoi(pno);
  }
  if (parray)  {
    pos = GetSelectedItems(&cnt);
    for (ii=0;ii<cnt;ii++) {
      pe = parray->GetRecordbyIndex(pos[ii]-1);
      if (pe  && pe->GetProposalNumber() == cpno) {
          retval = TRUE;
          break;
      }
    }
  }
 
  return retval;
}

Boolean PanelPropList::CheckLinked(char **tstr ,int cnt,ProposalEntry *pe)
{
  int ii;
  int pno;
  int lpno;
  char *tmp,*ptr;
  Boolean retval;

  if (pe->GetDBProposal()->get_linked_proposal()== 'Y') {
    lpno = atoi(pe->GetDBProposal()->get_linked_propnum());
    // but first check that linked proposal exists in the Panel
    ProposalEntry *lpe = parray->GetRecordbyProposal(lpno);
    if (!lpe) {
      retval = TRUE;
    } else {
      retval = FALSE;
    
      // this checks if the linked proposal has also been selected
      tmp = *tstr;
      for (ii=0;ii<cnt;ii++) {
        sscanf(tmp,"%d",&pno);
        if (pno == lpno) {
           retval = TRUE;
        }
        ptr = strchr(tmp,'\n');
        if (ptr) {
          tmp =  ptr;
          tmp++;
        }
      }
    }
  }
  else {
    retval = TRUE;
  }
 
  return retval;
}

void PanelPropList::SelectLinked()
{
  int ii,jj,xx;
  int lpno;
  int cnt,*pos;
  ProposalEntry *pe2;
  ProposalEntry *pe;

  if (!parray) return;
  pos = GetSelectedItems(&cnt);
  for (ii=0;ii<cnt;ii++) {
    pe = parray->GetRecordbyIndex(pos[ii]-1);
    if (pe && pe->GetDBProposal()->get_linked_proposal()== 'Y') {
      lpno = atoi(pe->GetDBProposal()->get_linked_propnum());
      pe2 = parray->GetRecordbyProposal(lpno);
      for (jj=0;jj<parray->GetSize();jj++) {
        if (pe2 == parray->GetRecordbyIndex(jj)) 
          break;
      }
      if (jj < parray->GetSize()) {
        for (xx=0;xx<cnt;xx++) {
          if (pos[xx] == jj+1) {
            break;
          }
        }
        if (xx >= cnt) {
          XmListSelectPos(base_w,(jj+1),False);
        }
      }
    }
  }
}

           
        

