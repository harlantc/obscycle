/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrPropList.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the RPS Error GUI - 
	Proposal ScrolledList class.

* NOTES:



* REVISION HISTORY:


        Ref. No.        Date
        --------        ----



*H****************************************************************************/
#include <stdlib.h>
#include <sys/param.h>
#include <Xm/List.h>
#include <string.h>

#include "ap_common.hh"
#include "ProposalArray.hh"

#include "ErrorMessage.hh"
#include "MessageAreaList.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "RPSErrPropList.hh"

#include "RPSErrApp.hh"
#include "RPSErrParameter.hh"
#include "RPSErrMenuWindow.hh"
#include "ProposalEntry.hh"
#include "connect_db.hh"

#if (__SUNPRO_CC == 0x420)
#else
  using std::qsort;
#endif



// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
RPSErrPropList :: RPSErrPropList( Widget parent,void *cbData)
	: ScrolledList(parent,XmEXTENDED_SELECT,cbData,
                       0,0,0,(char *)"RPSPropList")
{

  prop_list = NULL;
  prop_cnt = 0;
  sort_type = RPSErrPropList::SORT_REVERSE_4;


}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
RPSErrPropList ::~RPSErrPropList()
{
  delete prop_list;
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
/*
void 
RPSErrPropList::SelectAction(XmListCallbackStruct *cbs)
{


  char    *choice;
  XmStringGetLtoR(cbs->item,XmFONTLIST_DEFAULT_TAG,&choice);
  if (choice)
    {
    GUI_SetBusyCursor(base_w,True);

    XtFree(choice);

    GUI_SetBusyCursor(base_w,False);
    }
}

// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void RPSErrPropList::DefaultAction(XmListCallbackStruct *cbs)
{
  
}

*/
  
// -------------------------------------------------------------
// -------------------------------------------------------------
void RPSErrPropList :: BuildList()
{
  ProposalArray plist;
  int     ii;
  string  mainbuf;
  string  msg;
  Boolean retval = FALSE;


  delete prop_list;

  // retrieve proposals and display them
  cerr << "loading proposals...." << endl;
  if (theParameters->IsAllProposals())
    retval = plist.LoadProposalsByAO(msg,theApplication->GetDBConnection(),
	&prop_cnt,theParameters->GetAO(),theParameters->GetStatus()); 
  else
    retval = plist.LoadAllProposals(msg,theApplication->GetDBConnection(),
	&prop_cnt,theParameters->GetStatus(),theParameters->GetAO()); 

  if (retval) {
    cerr << "retrieved " << prop_cnt << " proposals." << endl;

    prop_list = new SHORT_LINE[prop_cnt];
  
    for (ii = 0; ii < prop_cnt; ii++) {
      mainbuf.clear();
      msg.clear();
      if (plist.GetShortList(ii,mainbuf,msg,True)) {
        strcpy(prop_list[ii].pline,mainbuf.c_str());
      }
      else {
        theMessageAreaList->SetText((char *)msg.c_str());
      }
    }

    SortList(sort_type);

  }
  else
    theErrorMessage->DisplayMessage("Database Error: ",(char *)msg.c_str());


}

// ---------------------------------------------------------------
// ---------------------------------------------------------------
void RPSErrPropList::DisplayItems()
{
  int  ii;

  // delete the existing list
  DeleteAllItems();

  for  (ii=0;ii<prop_cnt;ii++) {
    AddItem(prop_list[ii].pline,0);
  }
}

void RPSErrPropList::SortList(int type)
{
 
 sort_type = type;

 if (sort_type == RPSErrPropList::SORT_PROPNO)
    SortByProposalNumber(0);
  else if (sort_type == RPSErrPropList::SORT_LAST_4)
    SortByProposalNumber(1);
  else if (sort_type == RPSErrPropList::SORT_REVERSE_4)
    SortByProposalNumber(2);
  else if (sort_type == RPSErrPropList::SORT_PI)
    SortByPI();
  else if (sort_type == RPSErrPropList::SORT_TITLE)
    SortByTitle();
  else if (sort_type == RPSErrPropList::SORT_TYPE)
    SortByType();
}

// ---------------------------------------------------------------
// Sort
// ---------------------------------------------------------------
void RPSErrPropList ::SortByProposalNumber(int flg)
{

  if (flg == 0)
    qsort((void *)prop_list,(size_t)prop_cnt,sizeof(SHORT_LINE),
	&RPSErrPropList::CompareProposalNumber);
  else if (flg == 1)
    qsort((void *)prop_list,(size_t)prop_cnt,sizeof(SHORT_LINE),
	&RPSErrPropList::CompareLast4);
  else if (flg == 2)
    qsort((void *)prop_list,(size_t)prop_cnt,sizeof(SHORT_LINE),
	&RPSErrPropList::CompareReverse4);
  DisplayItems(); 
}

void RPSErrPropList ::SortByPI()
{
  qsort((void *)prop_list,(size_t)prop_cnt,sizeof(SHORT_LINE),
	&RPSErrPropList::ComparePI);
  DisplayItems(); 
}


void RPSErrPropList ::SortByTitle()
{
  qsort((void *)prop_list,(size_t)prop_cnt,sizeof(SHORT_LINE),
	&RPSErrPropList::CompareTitle);
  DisplayItems(); 
}

void RPSErrPropList ::SortByType()
{
  qsort((void *)prop_list,(size_t)prop_cnt,sizeof(SHORT_LINE),
	&RPSErrPropList::CompareType);
  DisplayItems(); 
}
// ---------------------------------------------------------------
// Compare routines for call by qsort function
// ---------------------------------------------------------------
int 
RPSErrPropList::CompareProposalNumber(const void *in1,const void *in2)
{
  char *s1 = (char *)in1;
  char *s2 = (char *)in2;
  int  ival1;
  int  ival2;

  sscanf(s1,"%d",&ival1);
  sscanf(s2,"%d",&ival2);

  return (ival1 - ival2);

}
int RPSErrPropList::CompareReverse4(const void *in1,const void *in2)
{
  char *s1 = (char *)in1;
  char *s2 = (char *)in2;
  int  ival1;
  int  ival2;

  sscanf(&s1[4],"%d",&ival1);
  sscanf(&s2[4],"%d",&ival2);

  return (ival2 - ival1);
}

int RPSErrPropList::CompareLast4(const void *in1,const void *in2)
{
  char *s1 = (char *)in1;
  char *s2 = (char *)in2;
  int  ival1;
  int  ival2;

  sscanf(&s1[4],"%d",&ival1);
  sscanf(&s2[4],"%d",&ival2);

  return (ival1 - ival2);

}

int RPSErrPropList::ComparePI(const void *in1,const void *in2)
{
  char *ptr1 = (char *)in1;
  char *ptr2 = (char *)in2;
  int retval;

  // skip the proposal number
  ptr1 = strstr(ptr1,"  ");
  while (*ptr1 == ' ') *ptr1++;

  // skip the proposal number
  ptr2 = strstr(ptr2,"  ");
  while (*ptr2 == ' ') *ptr2++;

  retval = strcasecmp(ptr1,ptr2);


  return (retval);

}

int RPSErrPropList::CompareType(const void *in1,const void *in2)
{
  char *ptr1 = (char *)in1;
  char *ptr2 = (char *)in2;

  return (strcasecmp(&ptr1[RPS_TYPE_OFFSET],&ptr2[RPS_TYPE_OFFSET]));

}
int RPSErrPropList::CompareTitle(const void *in1,const void *in2)
{
  char *ptr1 = (char *)in1;
  char *ptr2 = (char *)in2;

  return (strcasecmp(&ptr1[RPS_TITLE_OFFSET],&ptr2[RPS_TITLE_OFFSET]));

}


string RPSErrPropList::GetCurrentProposalNumber()
{
  char    propno[PROP_NBR_SIZE];
  string  prop_str;
  char    *tmp = NULL;

  memset(propno,0,PROP_NBR_SIZE);
  if (this->GetCurrentItem(&tmp) > -1 ) {
    if (tmp)
      sscanf(tmp,"%s",propno);
  }

  if (tmp) 
    free(tmp);
  prop_str = propno;
  return prop_str;

}
vector<string> RPSErrPropList::GetCurrentProposalNumbers()
{
  int     cnt,ii;
  char    *items = NULL;
  char    *tmp,*ptr;
  int     pno;
  char   propno[10];
  vector<string> pnos;

  cnt = this->GetCurrentItems(&items);
  tmp = items;
  for (ii=0;ii<cnt;ii++) {
    sscanf(tmp,"%d",&pno);
    sprintf(propno,"%8.8d",pno);
    pnos.push_back(propno);
    ptr = strchr(tmp,'\n');
    if (ptr) {
      tmp =  ptr;
      tmp++;
    }

  }
  return pnos;
}

void RPSErrPropList::SetHardCopyReceipt(Boolean flag,Boolean email_flag)
{

  string pno = this->GetCurrentProposalNumber();
  ProposalEntry pe(theApplication->GetDBConnection(),atoi(pno.c_str()));
  if (email_flag  && flag) {
    pe.MailHardCopyConfirmation();
  }
  else
    pe.UpdateHardCopy(flag);

  // now update this entry in the display
  int cnt;

  int *pos_list = GetSelectedItems(&cnt);
  string  nitem = pe.GetShortList(True);

  if (cnt > 0) {
    strcpy(prop_list[pos_list[0] - 1].pline,nitem.c_str());
    DisplayItems();
  }
  else {
    theErrorMessage->DisplayMessage("Please select a proposal and try again.");
  }
  
  
}
