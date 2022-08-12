
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropViewResults.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
        PropViewResults class.  This displays the results of
 	a system command.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPVIEWRESULTS_HH
#define PROPVIEWRESULTS_HH

#include <string>
#include "ViewFileCmd.hh"
#include "XConnection.hh"
#include "PropPrintFile.hh"

class PropViewResults : public ViewFileCmd {
    
  public:
    
    // Constructor 
    PropViewResults ( int active,const char *name,Boolean findBtn=TRUE);


    // Destructor 
    ~PropViewResults ( );


    virtual const char *const className () { return "PropViewResults"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();
    virtual void apply_cb(void *);
    virtual void GetCommand();

    string cmd;
    string ao;
    string pstatus;
    XConnection *xconn;
    PropPrintFile *printcmd;
};

#endif
