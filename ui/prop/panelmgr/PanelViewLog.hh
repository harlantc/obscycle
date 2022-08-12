
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelViewLog.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PanelViewLog class.  This displays the received proposal log
	produced by the prop_rps_receiver executable.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELVIEWLOG_HH
#define PANELVIEWLOG_HH


#include "ViewFileCmd.hh"



class PanelViewLog : public ViewFileCmd {
    
  public:
    
    // Constructor 
    PanelViewLog ( int active,const char *name="View Log",const char*fname=NULL);


    // Destructor 
    ~PanelViewLog ( );


    virtual const char *const className () { return "PanelViewLog"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();
    string _fname;

};

#endif
