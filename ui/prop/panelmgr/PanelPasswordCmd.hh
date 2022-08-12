
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the 



* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PANELPASSWORDCMD_HH
#define PANELPASSWORDCMD_HH


#include "PasswordPopup.hh"



class PanelPasswordCmd : public PasswordPopup {
    
  public:
    
    // Constructor 
    PanelPasswordCmd ( char *,int, Widget parent=NULL,Boolean needuser = TRUE );

    // Destructor 
    ~PanelPasswordCmd ( );


    virtual const char *const className () { return "PanelPasswordCmd"; }

  protected:
    
    // Update the display 
    virtual void ok_cb (void *);


    
};
#endif
