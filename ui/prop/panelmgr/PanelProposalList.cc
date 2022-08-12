/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelProposalList.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager 
	scrolled list of proposals.  This is NOT the list of 
	proposals assigned to a panel.

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
#include "MessageAreaList.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "PanelProposalList.hh"

#include "PanelApp.hh"
#include "PanelMenuWindow.hh"
#include "connect_db.hh"



// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PanelProposalList :: PanelProposalList( Widget parent,void *cbData)
	: ScrolledList(parent,XmEXTENDED_SELECT,cbData,
                       0,0,0,(char *)"PanelProposalList")
{

  parray = NULL;

}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PanelProposalList ::~PanelProposalList()
{
  delete parray;
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
void 
PanelProposalList::SelectAction(XmListCallbackStruct *)
{


}

// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void PanelProposalList::DefaultAction(XmListCallbackStruct *)
{
}

  
// -------------------------------------------------------------
// -------------------------------------------------------------
void PanelProposalList :: BuildList()
{
  int     ii;
  string  mainbuf;
  string  msg;
  int cnt;

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),TRUE);
  // delete the existing list
  DeleteAllItems();

  if (parray) 
    delete parray;

  // retrieve proposals not assigned to a panel and display them
  cerr << "loading all proposals not assigned to a panel...." << endl;
  parray = new ProposalArray();
  if (parray->LoadProposalsByPanel(0,msg,theApplication->GetDBConnection(),
                                   &cnt)) {
    cerr << "retrieved " << parray->GetSize() << " proposals." << endl;
  }
  else {
    cerr << msg << endl;
  }


  for (ii = 0; ii < parray->GetSize(); ii++) {
    mainbuf.clear();
    msg.clear();
    if (parray->GetAssignProposalItem(ii,mainbuf,msg)) {
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
void PanelProposalList :: Sort (int )
{
/*
  parray->Sort(type);
*/
  BuildList();
}
// ---------------------------------------------------------------
// ---------------------------------------------------------------
void  PanelProposalList :: Assign(PanelEntry *panel)
{
  int *pos;
  int  cnt;
  int  ii;
  ProposalEntry *pe;
  string msg,pno;

  
  pos = this->GetSelectedItems(&cnt);
  for (ii=0;ii< cnt;ii++) {
    pe = parray->GetRecordbyIndex(pos[ii]-1);
    pe->GetProposalNumber(pno);
    if (panel->AddProposal(pe->GetProposalId(),pno.c_str())) {
      
      msg = "Proposal " ;
      msg += pno;
      msg += " added to Panel ";
      msg += panel->GetPanelName();
      theApplication->Log(msg);
    
    }
  }


}

