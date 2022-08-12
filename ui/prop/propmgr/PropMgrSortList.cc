

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Manager scrolled 
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
#include "PropMgrSortList.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrSortList :: PropMgrSortList(Widget parent) 
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
PropMgrSortList :: ~PropMgrSortList()
{
}

// ----------------------------------------------------------
// list Selection Callback
// ----------------------------------------------------------
void PropMgrSortList :: SelectAction(XmListCallbackStruct *callData)
{

}
