/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
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
#include <sstream>

#include "ap_common.hh"
#include "ToolTip.hh"
#include "ErrorMessage.hh"
#include "MessageAreaList.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"
#include "stringFcns.hh"

#include "PanelMemberList.hh"
#include "ReviewerScores.hh"

#include "PanelApp.hh"
#include "PanelSortCmd.hh"
#include "PanelMenuWindow.hh"
#include "connect_db.hh"



// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PanelMemberList :: PanelMemberList( Widget parent,void *cbData)
	: ScrolledList(parent,XmSINGLE_SELECT,cbData,
                       0,0,0,(char*)"PanelMemberList")
{

  theToolTipManager->NewToolTip(
        (char*)"List of members for current panel.",  
        base_w);

}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PanelMemberList ::~PanelMemberList()
{
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
void 
PanelMemberList::SelectAction(XmListCallbackStruct *)
{
  // this is only needed if we do the reviewer score display
  PanelSortCmd *xx = (PanelSortCmd *)thePanelWindow->sort_cmd();
//  thePanelWindow->(PanelSortCmd*(sort_cmd()))->ok_cb();
 
  if (xx->GetNbrSelected() <= 0) {
    thePanelWindow->GetPropList()->Sort(PROP_SORT_PROPNO);
  }
  else {
    thePanelWindow->GetPropList()->Sort(xx->GetSelectedOptions(),xx->GetNbrSelected());
  }


}

// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void PanelMemberList::DefaultAction(XmListCallbackStruct *)
{
}
  
// -------------------------------------------------------------
// -------------------------------------------------------------
PersonEntry *PanelMemberList::GetCurrent()
{
  int cnt;
  int *pos;
  PersonEntry *pe = NULL;

  pos = this->GetSelectedItems(&cnt);
  if (cnt == 1 ) {
    pe = panel->GetMember(pos[0] - 1);
  }

  return pe;

}

// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelMemberList :: BuildList(PanelEntry *pe,Boolean del_flag,Boolean doScore)
{
  int     ii;
  string  mainbuf,conflicts;
  string  msg;
  ProposalArray *pa;
  PersonEntry *member;
  long    old_persid = 0;
  int     visItem=-1;
  int     propno = thePanelWindow->GetPropList()->GetLastSelected();


  // find current selected member so that we can re-hilite
  member = GetCurrent();
  if (member != NULL) {
    old_persid = member->GetId();
  }

  // find current top visible item in list so we can reset
  XtVaGetValues(this->baseWidget(),XmNtopItemPosition,&visItem,NULL);

  // delete the existing list
  if (del_flag) {
    DeleteAllItems();
  }
 

  panel = pe;
  pe->SortMembers();
  ReviewerScores *rs = ReviewerScores::Instance();
  double rval=-1.0;

  for (ii = 0; ii < pe->GetMemberSize(); ii++) {
    mainbuf.clear();
    msg.clear();
    pa = pe->GetProposalList();
    conflicts = "";
    if (pe->GetMemberItem(ii,mainbuf,msg)) {
      if (pa) {
        member = pe->GetMember(ii);
        char xx[20] = "";
        if (member) {
          if (doScore) {
            sprintf(xx,"%16.16s", " ");
            if (propno > 0) {
              rval = rs->getReviewerScore(propno,member->GetId());
              if (rval >= 0) 
                sprintf(xx,"%08d:%5.2f  ",propno,rval);
            }
          }
          mainbuf.append(xx); 

          
          if (!pa->GotCoIs()) 
            pa->LoadCoIs(msg);
          // get personal conflicts reported by panel member
          list<string> pconflicts ;
          pe->GetConflictsForMember(member->GetId(),pconflicts);
          // get proposal conflicts for given member
          stringFcns::join(&pconflicts,"  ",&conflicts);
          if (pconflicts.size() > 0) conflicts.append("  ");
         
          pa->IsUserConflict(conflicts,(char *)member->GetLast(),
		(char *)member->GetFirst(),(char *)member->GetInstitution(),
     		(char *)member->GetEmail());
          
          if (conflicts.length() > 600) {
              cerr << "TOO MANY CONFLICTS: " << member->GetLast() << "  " << conflicts << endl;
              conflicts = conflicts.substr(0,600);
              conflicts.append(" ...TOO MANY CONFLICTS");
          }
        }
      }
      else {
        conflicts =  "N/A";
      }
      mainbuf.insert(0,"   ");
      mainbuf.insert(0,pe->GetPanelName());
      if (strstr(conflicts.c_str(),"PI_Name")) {
        vector<string> slist;
        stringFcns::split(conflicts,slist,' ');
        string redStr = "";
        string normStr = "";
        for (size_t ii=0;ii< slist.size();ii++) {
          char cstr[30];
          sprintf(cstr,"%-19.19s",slist[ii].c_str());
          if (slist[ii].find("PI_Name",0) != STL_NPOS)  
            redStr += cstr;
          else
            normStr += cstr;
        }
        
        XmString str,strb,str1,str2,str3;
        str2 = XmStringGenerate((XtPointer)redStr.c_str(),XmFONTLIST_DEFAULT_TAG,XmCHARSET_TEXT,(char *)"RED");
        str3 = XmStringGenerate((XtPointer)normStr.c_str(),XmFONTLIST_DEFAULT_TAG,XmCHARSET_TEXT,NULL);
        str1 = XmStringGenerate((XtPointer)mainbuf.c_str(),XmFONTLIST_DEFAULT_TAG,XmCHARSET_TEXT,NULL);
        
        strb= XmStringConcat(str1,str2);
        str = XmStringConcat(strb,str3);

        
        if (old_persid > 0 && old_persid == member->GetId()) {
          XmListAddItem(base_w,str,0);
          XmListSelectItem(base_w,str,0);
        } else {
          XmListAddItemUnselected(base_w,str,0);
        }
        XmStringFree(str1);
        XmStringFree(str2);
        XmStringFree(str3);
        XmStringFree(strb);
        XmStringFree(str);
      } else {
        mainbuf.append(conflicts.c_str());
        AddItem((char *)mainbuf.c_str(),0);     
        if (old_persid > 0 && old_persid == member->GetId()) {
          SelectItem((char *)mainbuf.c_str(),FALSE);
        }
      }

    }
    else {
      theMessageAreaList->SetText((char *)msg.c_str());
    }
  }
  if (visItem > 0) {
    XtVaSetValues(this->baseWidget(),XmNtopItemPosition,visItem,NULL);
  }
}


// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PanelMemberList :: Sort (int )
{
}

// ---------------------------------------------------------------
// get email address of all/selected members
// ---------------------------------------------------------------
string PanelMemberList::GetEmailAddr(Boolean all_flag)
{
  string retstr;
  PersonEntry *member;
  int ii;

  if (!panel) return retstr;

  if (all_flag) {
    for (ii = 0; ii < panel->GetMemberSize(); ii++) {
      member = panel->GetMember(ii);
      if (member) {
        if (retstr.length() > 0)
          retstr.append(",");
        retstr.append(member->GetEmail());
      }
    }
  }
  else {
    member = GetCurrent();
    if (member)
      retstr.append(member->GetEmail());
  }

  return retstr;

}
