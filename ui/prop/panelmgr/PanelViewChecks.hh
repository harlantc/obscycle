
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelViewChecks.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PanelViewChecks class.  This displays the results of
 	the prop_view_linked.pl script.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELVIEWLINKED_HH
#define PANELVIEWLINKED_HH


#include "PropViewResults.hh"

class PanelViewChecks : public PropViewResults {
    
  public:
    
    // Constructor 
    PanelViewChecks (const char* name, int active,string,Boolean findBtn=TRUE );


    // Destructor 
    ~PanelViewChecks ( );


    virtual const char *const className () { return "PanelViewChecks"; }


  protected:
    
    // Set up inheritance specifics
    virtual void GetCommand();
};

#endif
