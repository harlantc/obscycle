/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the proposal
	scrolled list of Co-Investigators for selected proposals .

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
#include "ToolTip.hh"
#include "MessageAreaList.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "PropCoIList.hh"

#include "connect_db.hh"



// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PropCoIList :: PropCoIList( Widget parent,Widget main_window,void *cbData)
	: ScrolledList(parent,XmEXTENDED_SELECT,cbData,
                       0,0,0,(char *)"PropCoIList")
{

  parray = NULL;
  main_w = main_window;

  theToolTipManager->NewToolTip(
	(char *)"View list of Co-Investigators for proposals.",
    	base_w);
  
}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PropCoIList ::~PropCoIList()
{
  delete parray;
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
void 
PropCoIList::SelectAction(XmListCallbackStruct *)
{


}

// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void PropCoIList::DefaultAction(XmListCallbackStruct *)
{
}

  
// -------------------------------------------------------------
// -------------------------------------------------------------
void PropCoIList :: BuildList(ProposalArray *props)
{
  int     ii;
  int     jj;
  ProposalEntry *pe;
  PersonEntry *person;
  string  emsg;
  enum PERSON_SORT_TYPES sort_opt = PERSON_NAME;


  GUI_SetBusyCursor(main_w,TRUE);
  // delete the existing list

  if (parray) 
    delete parray;

  // retrieve proposals not assigned to a panel and display them
  parray = new PersonArray();

  if (!props->GotCoIs())
    props->LoadCoIs(emsg);

  for (ii = 0; ii < props->GetSize(); ii++) {
    pe = props->GetRecordbyIndex(ii);
    for (jj=0;jj<pe->GetCoISize(); jj++) {
      person = pe->GetCoI(jj);
      parray->Append(person);
    }
  }
  parray->Sort(sort_opt);



  GUI_SetBusyCursor(main_w,FALSE);

  DisplayData();

}

// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PropCoIList::DisplayData()
{
  int ii;
  string  mainbuf;
  string  msg;

  GUI_SetBusyCursor(main_w,TRUE);
  DeleteAllItems();
  for (ii=0; ii < parray->GetSize(); ii++) {
    mainbuf.clear();
    msg.clear();
    if (parray->GetCoIData(ii,mainbuf,msg)) {
      AddItem((char *)mainbuf.c_str(),0);     
    }
    else {
      theMessageAreaList->SetText((char *)msg.c_str());
    }
  }
  GUI_SetBusyCursor(main_w,FALSE);
}


// ---------------------------------------------------------------
// ---------------------------------------------------------------
void PropCoIList :: Sort (enum PERSON_SORT_TYPES stype)
{
  parray->Sort(stype);
  DisplayData();
  
}

