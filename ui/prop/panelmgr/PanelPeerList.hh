/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPeerList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the command which executes 
	the peer_lists.pl command

* NOTES:  

* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/


#ifndef PANELPEERLIST_HH
#define PANELPEERLIST_HH


#include "ExecuteCmd.hh"
#include <string>
#include <unistd.h>


class PanelPeerList : public ExecuteCmd {
    
  protected:

    string pfilename;

    virtual void doit();

    virtual Boolean Setup();

    // initialize parameters to null
    void Initialize();
    
  public:
    
    // Constructor 
    PanelPeerList ( int );

    // Destructor 
    ~PanelPeerList ( );


    virtual const char *const className () { return "PanelPeerList"; }
};
#endif
