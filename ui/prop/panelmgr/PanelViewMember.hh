
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelViewMember.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  Panel Management
        View Panel Member class.  This displays the associated
	panel member data from the database.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELVIEWMEMBER_HH
#define PANELVIEWMEMBER_HH


#include "ViewMsgCmd.hh"



class PanelViewMember : public ViewMsgCmd {
    
  public:
    
    // Constructor 
    PanelViewMember ( int active);


    // Destructor 
    ~PanelViewMember ( );


    virtual const char *const className () { return "PanelViewMember"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();

};

#endif
