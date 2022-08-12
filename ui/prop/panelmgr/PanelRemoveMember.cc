
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel GUI 


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"


#include "PanelRemoveMember.hh"
#include "PanelMenuWindow.hh"

#define DEFAULTREMOVE "The following member will be removed from the current panel.\n  Press OK to confirm, else press Cancel.\n\n  The member is: "



// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelRemoveMember::PanelRemoveMember ( char *name, int active) : 
                     AskFirstCmd ( name, active )
{
    setQuestion ( DEFAULTREMOVE );
    SetMenuFields ( 'R',NULL,NULL);


    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelRemoveMember::~PanelRemoveMember()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelRemoveMember::updateQuestion()
{
  string  msg;
  char    *newmsg = NULL;
  PanelMemberList *mlist;
 

  mlist = thePanelWindow->GetMemberList();
  mlist->GetCurrentItem(&newmsg);

   char *p1 = strchr(newmsg,':');
  if (p1) {
    *p1 = '\0';
    p1 = strrchr(newmsg,' ');
    if (p1) *p1 = '\0';
  }

  msg = DEFAULTREMOVE;
  msg.append("\n\n");
  msg.append(newmsg);
  msg.append("\n");

  setQuestion((char *)msg.c_str());

  if (newmsg)
    free(newmsg);
}


// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelRemoveMember::doit()
{

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),True);

  thePanelWindow->RemoveCurrentMember();

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),False);

  return;	
}
