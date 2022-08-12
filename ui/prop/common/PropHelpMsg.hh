
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning 
        code to display a help file in a separate text window.


* NOTES:  


* REVISION HISTORY:


        Ref. No.
        --------
        @V(#) @V(#)

        Date
        ----
        @D(#) @D(#)
 


*H******************************************************/

#ifndef PROPHELPMSG_HH
#define PROPHELPMSG_HH

#include <string>

#include "ViewFileCmd.hh"

class PropHelpMsg : public ViewFileCmd {
    
  public:
    
    // Constructor 
    PropHelpMsg (const char *,const char *); 

    // Destructor 
    ~PropHelpMsg ( );

    virtual void SetUp();

    virtual const char *const className () { return "PropHelpMsg"; }


  protected:
    
     string help_filename;

};

	
#endif
