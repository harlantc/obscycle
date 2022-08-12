
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelViewConflicts.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PanelViewConflicts class.  This displays the associcated conflict
        file from the upload area.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELVIEWCONFLICTS_HH
#define PANELVIEWCONFLICTS_HH


#include "ViewFileCmd.hh"



class PanelViewConflicts : public ViewFileCmd {
    
  public:
    
    // Constructor 
    PanelViewConflicts ( int active,const char *name="View Log",const char*fname=NULL);


    // Destructor 
    ~PanelViewConflicts ( );


    virtual const char *const className () { return "PanelViewConflicts"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();
    string _fname;

};

#endif
