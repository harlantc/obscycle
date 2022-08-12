
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

	This file contains all the external routines for the 
	Proposal Planning PANEL MANAGEMENT application

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/

#ifndef PANELEXTERNS_HH
#define PANELEXTERNS_HH



class MenuBar;
class PanelMenuWindow;


// ---Create Pulldown menus---
extern void Panel_CreateFileMenu(MenuBar *menubar,PanelMenuWindow *obj);
extern Widget Panel_CreateEditMenu(MenuBar *menubar,PanelMenuWindow *obj,Boolean popup);
extern void Panel_CreateViewMenu(MenuBar *menubar,PanelMenuWindow *obj,Boolean popup);
extern Widget Panel_CreateAssignMenu(MenuBar *menubar,PanelMenuWindow *obj,Boolean popup);
extern void Panel_CreateStatMenu(MenuBar *menubar,PanelMenuWindow *obj);
extern void Panel_CreateToolMenu(MenuBar *menubar,PanelMenuWindow *obj);
extern void Panel_CreateHelpMenu(MenuBar *menubar,PanelMenuWindow *obj);


#endif
