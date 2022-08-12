
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrPasswordCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the 



* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PROPMGRPASSWORDCMD_HH
#define PROPMGRPASSWORDCMD_HH


#include "PasswordPopup.hh"



class PropMgrPasswordCmd : public PasswordPopup {
    
  public:
    
    // Constructor 
    PropMgrPasswordCmd ( char *,int, Widget parent=NULL ,Boolean needuser=FALSE);

    // Destructor 
    ~PropMgrPasswordCmd ( );


    virtual const char *const className () { return "PropMgrPasswordCmd"; }

  protected:
    
    // Update the display 
    virtual void ok_cb (void *);


    
};
#endif
