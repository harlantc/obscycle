/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrDeleteProposal.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  delete proposal function.
        This will delete the current proposal file from the database.


* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#ifndef RPSERRDELETEPROPOSAL_HH
#define RPSERRDELETEPROPOSAL_HH


#include <string>
#include "AskFirstCmd.hh"


class RPSErrDeleteProposal : public AskFirstCmd {

  public:
    
    // Constructor 
    RPSErrDeleteProposal ( int );

    // Destructor 
    ~RPSErrDeleteProposal ( );

    virtual void updateQuestion();

    virtual const char *const className () { return "RPSErrDeleteProposal"; }

  protected:

    virtual void doit();
    virtual void undoit() {return;}

  private:
    string  filename;
    string  propno;
    Boolean mystat;

};
#endif

