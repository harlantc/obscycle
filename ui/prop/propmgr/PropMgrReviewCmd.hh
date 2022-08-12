
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrReviewCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  Proposal Management
        View Proposal class.  This displays the associated
	proposal data from the database.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRREVIEWCMD_HH
#define PROPMGRREVIEWCMD_HH


#include "ViewMsgCmd.hh"



class PropMgrReviewCmd : public ViewMsgCmd {
    
  public:
    
    // Constructor 
    PropMgrReviewCmd ( const char *name, int rtype,char mn, int active);


    // Destructor 
    ~PropMgrReviewCmd ( );

    void SetReview(string pno,string panel_name);

    virtual const char *const className () { return "PropMgrReviewCmd"; }


  protected:
    
    // Set up inheritance specifics
    virtual void SetUp();
 

    int rev_type;
    string propno;
    string panel_name;

};

#endif
