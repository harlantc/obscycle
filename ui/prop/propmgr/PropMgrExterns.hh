
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrExterns.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

	This file contains all the external routines for the 
	Proposal Planning PROPOSAL MANAGEMENT application

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#ifndef PROPMGREXTERNS_HH
#define PROPMGREXTERNS_HH



class MenuBar;
class PropMgrMenuWindow;


// ---Create Pulldown menus---
extern void PropMgr_CreateFileMenu(MenuBar *menubar,PropMgrMenuWindow *obj);
extern void PropMgr_CreateEditMenu(MenuBar *menubar,PropMgrMenuWindow *obj,Boolean popup);
extern void PropMgr_CreateViewMenu(MenuBar *menubar,PropMgrMenuWindow *obj,Boolean popup);
extern void PropMgr_CreateStatMenu(MenuBar *menubar,PropMgrMenuWindow *obj);
extern void PropMgr_CreateHelpMenu(MenuBar *menubar,PropMgrMenuWindow *obj);


#endif
