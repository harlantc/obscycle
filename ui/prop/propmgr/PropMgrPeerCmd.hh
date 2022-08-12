
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrPeerCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the  Proposal Management
        This displays for edit the associated proposal data from the database.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRPEERCMD_HH
#define PROPMGRPEERCMD_HH


#include "ProposalDefs.hh"
#include "GeneralDialogCmd.hh"
#include "ScrollText.hh"



class PropMgrPeerCmd : public GeneralDialogCmd {
    
  public:
    
    // Constructor 
    PropMgrPeerCmd ( int active, const char *name = "Peer Review...",
	int rtype = PEER_REV);


    // Destructor 
    ~PropMgrPeerCmd ( );

    static void confirm_cb(void *); 

    void SetReview (string pno,string pname);

    virtual const char *const className () { return "PropMgrPeerCmd"; }


  protected:
    ScrollText   *st;        // scrolled text widget for displaying message

    string propno;
    string panel_name;
    int    rev_type;
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();

    // Called to Update the text window
    virtual void UpdateGeneralForm ();

    // Virtual callback function for ok
    virtual void ok_cb(void *);

    // Virtual callback function for cancel
    virtual void cancel_cb(void *);

 


};

#endif
