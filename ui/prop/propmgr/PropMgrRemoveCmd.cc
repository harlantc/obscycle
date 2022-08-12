
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrRemoveCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Manager GUI 
	remove command. This removes an item/items form the current list.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>
#include <string>

#include "ap_common.hh"
#include "GUIDefs.h"
#include "Strings.hh"


#include "PropMgrRemoveCmd.hh"
#include "PropMgrMenuWindow.hh"

#define DEFAULTREMOVE "The following item(s) will be removed from the list.\n  Press OK to confirm, else press Cancel.\n\n "



// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PropMgrRemoveCmd::PropMgrRemoveCmd ( char *name, int active) : 
                     AskFirstCmd ( name, active )
{
    setQuestion ( DEFAULTREMOVE );
    SetMenuFields ( 'R',NULL,NULL);


    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PropMgrRemoveCmd::~PropMgrRemoveCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrRemoveCmd::updateQuestion()
{
  string msg;
  char  *newmsg=NULL;
  PropMgrPropList *mlist;
  ostrstream tmp;
 

  mlist = thePropMgrWindow->GetPropList();
  if (mlist->IsProposalView()) {
    mlist->PrintProposalNumbers(tmp,FALSE);
    tmp.put('\0');

    msg = DEFAULTREMOVE;
    msg.append("\n\n");
    char *ptr = tmp.str();
    if (ptr) {
      msg.append(ptr);
      delete ptr;
    }
    msg.append("\n");
  }
  else {
    msg = "Not implemented for Target View\n";
  }
  setQuestion((char *)msg.c_str());
  if (newmsg)
     free(newmsg);
}


// ------------------------------------------------------------
// ------------------------------------------------------------
void PropMgrRemoveCmd::doit()
{

  if (thePropMgrWindow->GetPropList()->IsProposalView()) {
    GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),True);

    thePropMgrWindow->GetPropList()->RemoveItems();

    GUI_SetBusyCursor(thePropMgrWindow->baseWidget(),False);
  }

  return;	
}
