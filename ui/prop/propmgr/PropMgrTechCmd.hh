
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Technical Review class.  



* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PROPMGRTECHCMD_HH
#define PROPMGRTECHCMD_HH


#include "GeneralDialogCmd.hh"
#include "ScrollText.hh"

class PropMgrTechCmd : public GeneralDialogCmd {
    
  public:
    

    // Constructor 
    PropMgrTechCmd ( int active,char *nm="Technical Evaluation...");

    // Destructor 
    ~PropMgrTechCmd ( );

    static void confirm_cb(void *);

    virtual const char *const className () { return "PropMgrTechCmd"; }

  protected:
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();

    // Called to Update data window
    virtual void UpdateGeneralForm ();

    // Update the display 
    virtual void ok_cb (void *);


  private:


    ScrollText *st;
    
};
#endif
