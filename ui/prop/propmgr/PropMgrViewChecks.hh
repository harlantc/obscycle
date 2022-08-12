
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrViewChecks.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PropMgrViewChecks class.  This displays the results of
 	the specified script.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRVIEWLINKED_HH
#define PROPMGRVIEWLINKED_HH


#include "PropViewResults.hh"

class PropMgrViewChecks : public PropViewResults {
    
  public:
    
    // Constructor 
    PropMgrViewChecks (const char* name, int active,string);


    // Destructor 
    ~PropMgrViewChecks ( );


    virtual const char *const className () { return "PropMgrViewChecks"; }


  protected:
    
    // Set up inheritance specifics
    virtual void GetCommand();
};

#endif
