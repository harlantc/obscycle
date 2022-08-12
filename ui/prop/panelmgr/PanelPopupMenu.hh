

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME:	%M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	Panel manager Popup Menu for the right mouse button .


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PANELPOPUPMENU_HH
#define PANELPOPUPMENU_HH



#include "MenuBar.hh"

class PanelPopupMenu : public MenuBar {

  public:
    

    PanelPopupMenu (Widget parent); 
    ~PanelPopupMenu (); 


    virtual const char *const className() {return ("PanelPopupMenu");}

  protected:

    Widget move_pulldown;

};


#endif
