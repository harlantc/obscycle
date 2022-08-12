/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME:	%M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	manager Popup Menu for the right mouse button .


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PROPMGRPOPUPMENU_HH
#define PROPMGRPOPUPMENU_HH



#include "MenuBar.hh"

class PropMgrPopupMenu : public MenuBar {

  public:
    

    PropMgrPopupMenu (Widget parent); 
    ~PropMgrPopupMenu (); 


    virtual const char *const className() {return ("PropMgrPopupMenu");}

  protected:

    Widget move_pulldown;

};


#endif
