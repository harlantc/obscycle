
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelRefreshCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the code for the Panel Manager GUI Refresh
       command class. This class refreshes the list of proposal entries.

* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"
#include "PanelRefreshCmd.hh"
#include "PanelMenuWindow.hh"
#include "PanelPropList.hh"
#include "PanelSortCmd.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelRefreshCmd::PanelRefreshCmd ( int active, char *name) : 
                     NoUndoCmd ( name, active )
{
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelRefreshCmd::~PanelRefreshCmd()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelRefreshCmd::doit()
{

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),True);
  PanelEntry *pe = thePanelWindow->GetCurrentPanel();
  if (pe) {
    pe->LoadProposals();
    ((PanelSortCmd*)(thePanelWindow->sort_cmd()))->ok_cb((void *)0);
    //parray = pe->GetProposalList();
    //cerr << "loading additional proposal data...." << endl;
    //parray->LoadAdditional();
    //cerr << "retrieved " << parray->GetSize() << " proposals." << endl;

    thePanelWindow->DisplayPanels(TRUE,TRUE);
  }

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),False);
  
  return;	
}
