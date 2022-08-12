
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelViewDetectors.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PanelViewDetectors class.  This displays the results of
 	the prop_detector_for_proposals.pl script.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELVIEWDETECTOR_HH
#define PANELVIEWDETECTOR_HH


#include "PropViewResults.hh"

class PanelViewDetectors : public PropViewResults {
    
  public:
    
    // Constructor 
    PanelViewDetectors (const char* name, int active,string);


    // Destructor 
    ~PanelViewDetectors ( );


    virtual const char *const className () { return "PanelViewDetectors"; }


  protected:
    
    // Set up inheritance specifics
    virtual void GetCommand();
    string orig_cmd;
};

#endif
