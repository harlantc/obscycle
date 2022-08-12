
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
#ifndef RPSERRPASSWORDCMD_HH
#define RPSERRPASSWORDCMD_HH


#include "PasswordPopup.hh"



class RPSErrPasswordCmd : public PasswordPopup {
    
  public:
    
    // Constructor 
    RPSErrPasswordCmd ( char *,int, Widget parent=NULL,Boolean needuser=FALSE );

    // Destructor 
    ~RPSErrPasswordCmd ( );


    virtual const char *const className () { return "RPSErrPasswordCmd"; }

  protected:
    
    // Update the display 
    virtual void ok_cb (void *);


    
};
#endif
