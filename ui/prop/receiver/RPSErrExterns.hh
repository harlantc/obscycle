
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrExterns.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

	This file contains all the external routines for the 
	Proposal Planning RPS Error GUI application.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#ifndef RPSERREXTERNS_HH
#define RPSERREXTERNS_HH



class MenuBar;
class RPSErrMenuWindow;


// ---Create Pulldown menus---
extern void RPSErr_CreateFileMenu(MenuBar *menubar,RPSErrMenuWindow *obj);
extern void RPSErr_CreateEditMenu(MenuBar *menubar,RPSErrMenuWindow *obj,Boolean popup);
extern void RPSErr_CreateViewMenu(MenuBar *menubar,RPSErrMenuWindow *obj,Boolean popup);
extern void RPSErr_CreateAssignMenu(MenuBar *menubar,RPSErrMenuWindow *obj,Boolean popup);
extern void RPSErr_CreateStatMenu(MenuBar *menubar,RPSErrMenuWindow *obj);
extern void RPSErr_CreateHelpMenu(MenuBar *menubar,RPSErrMenuWindow *obj);


#endif
