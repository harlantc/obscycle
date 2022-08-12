/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrPropList.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Manager 
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
#include "FileUtils.hh"
#include "MessageAreaList.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"


#include "ObsUtil.hh"
#include "stringFcns.hh"

#include "PropMgrPropList.hh"
#include "PropMgrApp.hh"
#include "PropMgrParameter.hh"
#include "PropMgrMenuWindow.hh"

#include "ProposalEntry.hh"
#include "PanelEntry.hh"


// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PropMgrPropList :: PropMgrPropList( Widget parent,void *cbData)
	: ScrolledList(parent,XmEXTENDED_SELECT,cbData,
                       0,0,0,(char *)"PropMgrList",TRUE)
{
  string  tmp;

  parray = NULL;
  tarray = NULL;
  cur_type = LOAD_ALL;
  prop_view=TRUE;

  get_tmppath(tmp);
  sprintf(pwdfile,"%s/proppXXXXXXXX",tmp.c_str());
  int filedes = mkstemp(pwdfile);
  if (filedes > 0) {
    close(filedes);
  }


}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PropMgrPropList ::~PropMgrPropList()
{
  delete parray;
  delete tarray;

  if (pwdfile) {
    unlink(pwdfile);
  }

}


// -------------------------------------------------------------
// -------------------------------------------------------------
void PropMgrPropList :: Refresh()
{
  if (parray) {
    delete parray;
    parray = NULL;
  }
  if (tarray) {
    delete tarray;
    tarray = NULL;
  }
  RebuildList(cur_type,cur_param.c_str(),cur_ao.c_str(),cur_status.c_str());
}
// -------------------------------------------------------------
// -------------------------------------------------------------
string PropMgrPropList::GetCurrentProposalNumber()
{
  char    propno[PROP_NBR_SIZE];
  Strings tmp;
  string  prop_str;

  memset(propno,0,PROP_NBR_SIZE);
  if (GetCurrentItem(tmp) > -1 ) {
    sscanf(tmp.chars(),"%s",propno);
  }

  prop_str = propno;
  return prop_str;
}

// -------------------------------------------------------------
// -------------------------------------------------------------
ProposalEntry* PropMgrPropList :: GetProposal()
{
  ProposalEntry *pe = NULL;
  string pno;


  if (parray) {
   pno = GetCurrentProposalNumber();
   pe = parray->GetRecordbyProposal(atoi(pno.c_str()));
   if (pe)
    pe->Retrieve_Reviews();
  }
  return pe;
}
// -------------------------------------------------------------
// -------------------------------------------------------------
void PropMgrPropList :: UpdateReviews(long pid)
{
 ProposalEntry *pe;

 if (parray) {
   pe = parray->GetRecordbyProposalId(pid);
   if (pe)
    pe->Retrieve_Reviews(TRUE);
 }
   
}


// -------------------------------------------------------------
// -------------------------------------------------------------
void PropMgrPropList :: RebuildList(enum LOAD_TYPES tt,const char *param,
	const char *ao,const char *status)
{
  Boolean need_to_rebuild = FALSE;
  string  str;
  string tmp_ao;
  string tmp_status;
  string tmp_param;

  if (param)
     tmp_param = param;
  if (ao)
    tmp_ao = ao;
  if (status && *status != '\0' ) {
    tmp_status = status;
  }
  else {
    tmp_status = "%";
  }

  if (cur_type != tt)  {
    need_to_rebuild = TRUE;
  }
  cur_type = tt;

  if (cur_ao != tmp_ao)  {
    need_to_rebuild = TRUE;
  }
  cur_ao = tmp_ao;

  if (cur_status != tmp_status)  {
    need_to_rebuild = TRUE;
  }
  cur_status = tmp_status;

  if (strcmp(cur_param.c_str(),tmp_param.c_str()) != 0) {
    need_to_rebuild = TRUE;
  }
  cur_param = tmp_param;

  if (need_to_rebuild) {
    if (parray) {
      delete parray;
      parray = NULL;
    }
    if (tarray) {
      delete tarray;
      tarray = NULL;
    }
  }

  BuildList(prop_view);
}
  
// -------------------------------------------------------------
// -------------------------------------------------------------
void PropMgrPropList :: BuildList(Boolean view_flag)
{
  int     ii;
  string  mainbuf;
  string  msg;
  int cnt;
  Boolean stat = FALSE;

  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),True);
  this->unmanage();

  // delete the existing list
  DeleteAllItems();

  prop_view = view_flag;

  // always need to retrieve the proposals
  if (!parray) {
    parray = new ProposalArray();
    // retrieve proposals and display them based on retrieve type
    if (cur_type == LOAD_ALL) {
      cerr << "loading all proposals...." << endl;
      stat= parray->LoadAllProposals(msg,theApplication->GetDBConnection(),
	&cnt,cur_status.c_str(),cur_ao.c_str());
    }
    else if (cur_type == LOAD_PANEL) {
      cerr << "loading proposals by panel...." << endl;
      PanelEntry panel(theApplication->GetDBConnection(),cur_param.c_str(),
	cur_ao.c_str());
      if (panel.GetState() == GOOD) {
        stat= parray->LoadProposalsByPanel(panel.GetPanelId(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
      }
    }
    else if (cur_type == LOAD_PI) {
      cerr << "loading proposals by PI...." << endl;
      stat= parray->LoadProposalsByPI(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_COI) {
      cerr << "loading proposals by CoI...." << endl;
      stat= parray->LoadProposalsByCoI(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_PROPOSAL_TYPE) {
      cerr << "loading proposals by proposal type...." << endl;
      stat= parray->LoadProposalsByType(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_JOINT) {
      cerr << "loading proposals by joint ...." << endl;
      stat= parray->LoadProposalsByJoint(msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_CATEGORY) {
      cerr << "loading proposals by subject category code ...." << endl;
      stat= parray->LoadProposalsBySubject(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_TARGNAME) {
      cerr << "loading proposals by target name...." << endl;
      stat= parray->LoadProposalsByTargetName(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_COI_INSTITUTE) {
      cerr << "loading proposals by CoI Institute...." << endl;
      stat= parray->LoadProposalsByCoIInstitute(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_INSTITUTE) {
      cerr << "loading proposals by Institute ...." << endl;
      stat= parray->LoadProposalsByInstitute(cur_param.c_str(),msg,
	  theApplication->GetDBConnection(),&cnt,
	  cur_status.c_str(), cur_ao.c_str());
    }
    else if (cur_type == LOAD_FILE) {
      cerr << "loading proposals by file...." << endl;
      stat= parray->LoadProposalsByFile(cur_param.c_str(),msg,
	theApplication->GetDBConnection(),&cnt);
    }
    else if (cur_type == LOAD_PROPOSAL) {
      cerr << "loading proposals by proposal number...." << endl;
      vector<string> slist;

      if (strchr(cur_param.c_str(),','))
        stringFcns::split(cur_param,slist,',');
      else
        stringFcns::split(cur_param,slist,' ');
      stat= parray->LoadProposalsByList(slist,msg,
	theApplication->GetDBConnection(),&cnt);
    }
   
    if (stat) {
      cerr << "loading additional proposal data..." << endl;
      parray->LoadAdditional();
/*
      cerr << "loading reviews..." << endl;
      parray->LoadReviews(msg);
      cerr << "loading stats..." << endl;
      parray->LoadStats();
      cerr << "loading panel names..." << endl;
      parray->LoadPanelNames();
*/
      cerr << "retrieved " << cnt << " proposals." << endl;
      string xx = "Retrieved ";
      xx.append(stringFcns::itoa(cnt));
      xx.append(" proposals.");
      theMessageAreaList->SetText((char *)xx.c_str());
      parray->Sort(PROP_SORT_PROPNO);
      }
    else {
      cerr << msg << endl;
    }
  }

  if (prop_view){

    int jj;
    if ( (jj = parray->GetSize()) > 0) {
      for (ii = 0; ii < jj; ii++) {
        mainbuf.clear();
        msg.clear();
        if (parray->GetProposalItem(ii,mainbuf,msg,TRUE)) {
          AddItem((char *)mainbuf.c_str(),0);     
        }
        else {
          theMessageAreaList->SetText((char *)msg.c_str()); 
        }

      }
    }
    else {
      theMessageAreaList->SetText("No proposals retrieved for current query." );
      
    }
  }
  else {
    if (!tarray)  {
      cerr << "Loading targets..." << endl;
      tarray = new TargetArray(parray,theApplication->GetDBConnection());
      cerr << "Retrieved " << tarray->GetSize() << " targets." << endl;
    }

    if (tarray->GetSize() > 0) {
      for (ii = 0; ii < tarray->GetSize(); ii++) {
        mainbuf.clear();
        msg.clear();
        if (tarray->GetTargetItem(ii,mainbuf,msg)) {
          AddItem((char *)mainbuf.c_str(),0);     
        }
        else {
          theMessageAreaList->SetText( (char *)msg.c_str());
        }
      }
    }
    else {
      theMessageAreaList->SetText(
	"No targets retrieved for current proposals." );
      
    }
  }

  this->manage();
  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),False);
}

// ---------------------------------------------------------------
// Sort
// ---------------------------------------------------------------
void PropMgrPropList ::SortByProposalNumber()
{

  if (parray)
    parray->Sort(PROP_SORT_PI);
}

// ---------------------------------------------------------------
Boolean PropMgrPropList ::Sort(int *opts,int nbr_opts,Boolean is_proposal)
{
  Boolean stat = TRUE;

  if (is_proposal) {
    if (parray)
      parray->Sort(opts,nbr_opts);
    else
      stat = FALSE;
  }
  else if (tarray)
    tarray->Sort(opts,nbr_opts);
  else
    stat = FALSE;

  return stat;
}
// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PropMgrPropList ::RemoveItems()
{
  int cnt;
  int *pos;
  int ii;

  pos = GetSelectedItems(&cnt);

  // go backwards so we don't have to adjust index into proposal/target
  // array
  for (ii=cnt-1; ii>-1; ii--) { 
    if (prop_view)
      parray->Delete(pos[ii]-1);
    else
      tarray->Delete(pos[ii]-1);
  }
  XmListDeletePositions(base_w,pos,cnt);
}

// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PropMgrPropList::PrintProposal(ostream &oss,const char *propno)
{
  int pno;

  pno = atoi(propno);
  
  if (parray) {
    ProposalEntry *pe = parray->GetRecordbyProposal(pno);
    if (pe) {
      pe->Print(oss);
    }
  }
}


// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PropMgrPropList::PrintReview(int type,ostream &oss,const char *propno,
	const char *pname)
{
  int pno;
  ProposalEntry *pe;
  REVIEW_TYPE ttype = (REVIEW_TYPE)type;

  switch (type) {
    case PRI_REV:
      oss << "Primary Review";
      break;
    case SEC_REV:
      oss << "Secondary Review";
      break;
    case PEER_REV:
      oss << "Peer Review";
      break;
    case FINAL_REV:
      oss << "Final Review";
      break;
  }
    
  oss << " for Proposal " << propno ;
  if (pname && *pname != '\0') {
     oss << ", Panel "<< pname;
  }
  oss << "\n\n";
  pno = atoi(propno);
  pe =  parray->GetRecordbyProposal(pno);
  if (pe) {
    pe->PrintReview(ttype,oss,FALSE,pname);
  }
}



// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PropMgrPropList::PrintProposalNumbers(ostream &oss,Boolean all_flag)
{
  int *pos;
  int ii;
  int cnt;
  ProposalEntry *pe;
  string  pno;

  if (!parray) return;

  if (all_flag) {
    for (ii=0; ii<parray->GetSize(); ii++) { 
      pe = parray->GetRecordbyIndex(ii);
      pe->GetProposalNumber(pno);
      oss << pno << "\n";
    }
  }
  else {
    pos = GetSelectedItems(&cnt);

    for (ii=0; ii<cnt; ii++) { 
      pe = parray->GetRecordbyIndex(pos[ii] - 1);
      if (pe) {
        pe->GetProposalNumber(pno);
        oss << pno << "\n";
      }
    }
  }
}

void PropMgrPropList::PrintTargetIds(ostream &oss,Boolean all_flag)
{
  int *pos;
  int ii;
  int cnt;
  TargetEntry *te;
  long tid;

  if (!tarray)  {
    tarray = new TargetArray(parray,theApplication->GetDBConnection());
  }

  if (all_flag) {
    for (ii=0; ii<tarray->GetSize(); ii++) { 
      te = tarray->GetRecordbyIndex(ii);
      tid = te->GetTargetId();
      oss << tid << "\n";
    }
  }
  else {
    pos = GetSelectedItems(&cnt);

    for (ii=0; ii<cnt; ii++) { 
      te = tarray->GetRecordbyIndex(pos[ii] - 1);
      if (te) {
        tid = te->GetTargetId();
        oss << tid << "\n";
      }
    }
  }
}
