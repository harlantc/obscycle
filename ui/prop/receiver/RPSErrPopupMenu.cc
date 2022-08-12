 
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Proposal Planning 
	RPS error gui PopupMenu class.  This class provides a popup 
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

#include "RPSErrMenuWindow.hh"
#include "RPSErrPopupMenu.hh"
#include "RPSErrExterns.hh"



// -------------------------------------------------------------
// Constructor
// -------------------------------------------------------------
RPSErrPopupMenu :: RPSErrPopupMenu (Widget parent )
	: MenuBar(parent,"PopupMenu",XmMENU_POPUP)
{ 


  RPSErr_CreateViewMenu((MenuBar *)this,theRPSErrWindow,True);




}


// -------------------------------------------------------------
// Destructor
// -------------------------------------------------------------
RPSErrPopupMenu :: ~RPSErrPopupMenu ()
{
}


