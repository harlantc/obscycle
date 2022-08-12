 
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Planning 
	Panel Manager PopupMenu class.  This class provides a popup 
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

#include "MenuBar.hh"

#include "PanelMenuWindow.hh"
#include "PanelPopupMenu.hh"
#include "PanelExterns.hh"



// -------------------------------------------------------------
// Constructor
// -------------------------------------------------------------
PanelPopupMenu :: PanelPopupMenu (Widget parent )
	: MenuBar(parent,"PopupMenu",XmMENU_POPUP)
{ 

  //Widget sep;

  move_pulldown = Panel_CreateEditMenu((MenuBar *)this,thePanelWindow,True);
  XtVaCreateManagedWidget("separator",
        xmSeparatorWidgetClass,base_w,
        NULL);
  Panel_CreateAssignMenu((MenuBar *)this,thePanelWindow,True);




}


// -------------------------------------------------------------
// Destructor
// -------------------------------------------------------------
PanelPopupMenu :: ~PanelPopupMenu ()
{
}


