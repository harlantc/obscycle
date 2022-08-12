

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelSortList.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel Manager scrolled 
	list of selected sort options.  This list displays the
        selection order of the sort options.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>

#include "ap_common.hh"
#include "ToolTip.hh"
#include "PanelSortList.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelSortList :: PanelSortList(Widget parent) 
	: ScrolledList(parent,XmBROWSE_SELECT,NULL,
        	0,0,0,(char *)"SortList")

{

  theToolTipManager->NewToolTip(
	(char *)"Selected sort options in prioritized order.",
        base_w);



}

// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelSortList :: ~PanelSortList()
{
}

// ----------------------------------------------------------
// list Selection Callback
// ----------------------------------------------------------
void PanelSortList :: SelectAction(XmListCallbackStruct *callData)
{

}
