
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrViewProposal.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        RPSErrViewChecks class.  This displays the results of
 	the prop_rps_checks.pl script.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef RPSERRVIEWCHECKS_HH
#define RPSERRVIEWCHECKS_HH


#include "PropViewResults.hh"

class RPSErrViewChecks : public PropViewResults {
    
  public:
    
    // Constructor 
    RPSErrViewChecks (const char* name, int active,string,Boolean pflag=TRUE);


    // Destructor 
    ~RPSErrViewChecks ( );


    virtual const char *const className () { return "RPSErrViewChecks"; }


  protected:
    
    // Set up inheritance specifics
    virtual void GetCommand();

    Boolean param_flag;
};

#endif
