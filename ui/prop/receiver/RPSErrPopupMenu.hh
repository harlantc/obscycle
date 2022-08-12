

/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME:	%M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	RPS error processing Popup Menu for the right mouse button .


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef RPSERRPOPUPMENU_HH
#define RPSERRPOPUPMENU_HH



#include "MenuBar.hh"

class RPSErrPopupMenu : public MenuBar {

  public:
    

    RPSErrPopupMenu (Widget parent); 
    ~RPSErrPopupMenu (); 


    virtual const char *const className() {return ("RPSErrPopupMenu");}

  protected:


};


#endif
