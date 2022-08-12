
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Manager layout
	command.  This will switch the view between proposal and
	target items.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#include "ap_common.hh"
#include <stdlib.h>

//guiext
#include "GUIDefs.h"


#include "PropMgrLayoutCmd.hh"
#include "PropMgrMenuWindow.hh"
#include "PropMgrPropList.hh"
#include "QuestionDialogManager.hh"

#define DEFAULTLAYOUT "The database access can take a VERY LONG time.\nAre you sure you want to do this?\nPress OK to confirm, else press Cancel.\n\n "

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrLayoutCmd::PropMgrLayoutCmd ( char *name,Boolean type, int active) : 
                     NoUndoCmd ( name, active )
{
  layout_prop = type;
  firsttime = FALSE;  // faster now so skip the question!
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrLayoutCmd::~PropMgrLayoutCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrLayoutCmd::doit()
{


  if (firsttime) {
    theQuestionDialogManager->post ( 0,DEFAULTLAYOUT,
        (void *) this, &PropMgrLayoutCmd::yesCallback, 
	&QuestionDialogManager::null_cb, NULL);
  }
  else
    this->yesCallback(this);

}

void PropMgrLayoutCmd::yesCallback(void *clientData)
{
  PropMgrLayoutCmd *obj = (PropMgrLayoutCmd *)clientData;
  PropMgrPropList *proplist;
  const char *lbl;
  extern Widget LAYOUT_W;

  obj->firsttime = FALSE;
  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),True);

  proplist = thePropMgrWindow->GetPropList();

  // switch the order for the layout between proposal/target
  if (obj->layout_prop == TRUE) 
    obj->layout_prop = FALSE;
  else 
    obj->layout_prop = TRUE;
  

  proplist->BuildList(obj->layout_prop);

  switch (obj->layout_prop) {
    case FALSE:
      lbl = "Layout by Proposal";
      break;

    case TRUE:
      lbl = "Layout by Target";
      break;
  }

  thePropMgrWindow->SetHeader(obj->layout_prop);
  XmString str = XmStringCreateLtoR((char *)lbl,XmFONTLIST_DEFAULT_TAG);
  XtVaSetValues(LAYOUT_W,XmNlabelString,str,NULL);
  XmStringFree(str);

  GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),False);
  return;	
}
