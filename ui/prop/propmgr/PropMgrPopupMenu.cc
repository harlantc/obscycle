 
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Planning 
	Manager PopupMenu class.  This class provides a popup 
	menu for easy access to certain commands using the
	right mouse button. 

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>
#include <Xm/Xm.h> 
#include <Xm/RowColumn.h> 
#include <Xm/Separator.h> 

#include "ap_common.hh"
#include "MenuBar.hh"

#include "PropMgrMenuWindow.hh"
#include "PropMgrPopupMenu.hh"
#include "PropMgrExterns.hh"



// -------------------------------------------------------------
// Constructor
// -------------------------------------------------------------
PropMgrPopupMenu :: PropMgrPopupMenu (Widget parent )
	: MenuBar(parent,"PopupMenu",XmMENU_POPUP)
{ 


  PropMgr_CreateEditMenu((MenuBar *)this,thePropMgrWindow,True);
  XtVaCreateManagedWidget("separator",
        xmSeparatorWidgetClass,base_w,
        NULL);
  PropMgr_CreateViewMenu((MenuBar *)this,thePropMgrWindow,True);




}


// -------------------------------------------------------------
// Destructor
// -------------------------------------------------------------
PropMgrPopupMenu :: ~PropMgrPopupMenu ()
{
}


