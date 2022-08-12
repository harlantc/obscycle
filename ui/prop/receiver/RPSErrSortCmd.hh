
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrSortCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

       This file contains the definition for the RPS Error GUI Sort  class.

* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/


#ifndef RPSERRSORTCMD_HH
#define RPSERRSORTCMD_HH


#include "NoUndoCmd.hh"

class RPSErrSortCmd : public NoUndoCmd {
    
  protected:
    int  sort_type;
    
    virtual void doit();
    
  public:
    enum SORT_TYPE {SORT_PROPNO,SORT_PI,SORT_TITLE,SORT_TYPE,SORT_LAST_4,
	SORT_REVERSE_4};
    
    // Constructor 
    RPSErrSortCmd ( char *,int, int );

    // Destructor 
    ~RPSErrSortCmd ( );

    virtual const char *const className () { return "RPSErrSortCmd"; }
};
#endif
