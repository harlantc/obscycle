
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrViewErrorLog.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        RPSErrViewLog class.  This displays the log for the GUI error
	processing.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef RPSERRVIEWERRLOG_HH
#define RPSERRVIEWERRLOG_HH


#include "ViewFileCmd.hh"



class RPSErrViewErrorLog : public ViewFileCmd {
    
  public:
    
    // Constructor 
    RPSErrViewErrorLog ( int active);


    // Destructor 
    ~RPSErrViewErrorLog ( );


    virtual const char *const className () { return "RPSErrViewErrorLog"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();

};

#endif
