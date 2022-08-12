/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPanelList.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager 
	scrolled list of existing panels.

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

#include "ObsUtil.hh"
#include "stringFcns.hh"
#include "PanelPanelList.hh"

#include "PanelApp.hh"
#include "PanelMenuWindow.hh"
#include "connect_db.hh"



// ---------------------------------------------------------------
// Constructor
// ---------------------------------------------------------------
PanelPanelList :: PanelPanelList( Widget parent,void *cbData)
	: ScrolledList(parent,XmBROWSE_SELECT,cbData,
                       0,0,0,(char *)"PanelList")
{


}


// ---------------------------------------------------------------
// Destructor
// ---------------------------------------------------------------
PanelPanelList ::~PanelPanelList()
{
}

// ---------------------------------------------------------------
// virtual select action callback routine
// ---------------------------------------------------------------
void 
PanelPanelList::SelectAction(XmListCallbackStruct *)
{
  int  *pos;
  int   cnt;
  int   ypos;


  GUI_SetBusyCursor(thePanelWindow->baseWidget(),TRUE);

  pos = GetSelectedItems(&cnt);
  if (cnt == 1) {
    ypos = XmListGetKbdItemPos(base_w);
    thePanelWindow->DisplayCurrentPanelData(*pos - 1);
    //thePanelWindow->DisplayPanels(FALSE);
    XmListSetKbdItemPos(base_w,ypos);
    //XmListSetPos(base_w,*pos);
  }
  else
    cerr << "No panel selected" << endl;
  
  GUI_SetBusyCursor(thePanelWindow->baseWidget(),FALSE);
}




// ---------------------------------------------------------------
// virtual default action callback routine
// ---------------------------------------------------------------
void PanelPanelList::DefaultAction(XmListCallbackStruct *)
{
/*

  PanelEntry *pe;
  int  *pos;
  int   cnt;


  pos = GetSelectedItems(&cnt);
  if (cnt == 1) {
    thePanelWindow->DisplayCurrentPanelData(*pos - 1);
  }
  else
    cerr << "No panel selected" << endl;
  
*/
}

  
// -------------------------------------------------------------
// -------------------------------------------------------------
PanelArray *PanelPanelList :: BuildList(PanelArray *parray,Boolean cb_flg)
{
  int     ii;
  string  mainbuf;
  string  msg;
  string  olditem;
  char    *tmp=NULL;
  int     visItem=-1; 

  GetCurrentItem(&tmp);
  olditem = tmp;
  if (tmp)
     free(tmp);
  if (olditem.length() > 0) {
    stringFcns::sub(olditem,' ','\0');
    string t1 = olditem.c_str();
    olditem = t1;
  }

  XtVaGetValues(this->baseWidget(),XmNtopItemPosition,&visItem,NULL);

  // delete the existing list
  DeleteAllItems();



  if (!parray) {
    parray = new PanelArray(theApplication->GetDBConnection());

    // retrieve proposals and display them
    cerr << "loading all panels...." << endl;
    if (parray->LoadAllPanels(msg)) {
      cerr << "retrieved " << parray->GetSize() << " panels." << endl;
      parray->Sort();  // sort by panel id
    }
    else {
      cerr << msg << endl;
    }
  }
  
  for (ii = 0; ii < parray->GetSize(); ii++) {
    mainbuf.clear();
    msg.clear();
    if (parray->GetPanelData(ii,mainbuf,msg)) {
      AddItem((char *)mainbuf.c_str(),0);     
      if (olditem.length() > 0) {
        if (strncmp(mainbuf.c_str(),olditem.c_str(),olditem.length()) == 0) {
          SelectItem((char *)mainbuf.c_str(),cb_flg);
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


  return parray;
}

// ---------------------------------------------------------------
// Sort
// ---------------------------------------------------------------
void PanelPanelList ::SortByPanelId()
{

  thePanelWindow->GetPanelArray()->Sort();


}

