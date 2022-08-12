
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Panel Manager 
        FindProp class.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
        %I%             %G%

*H******************************************************/

#ifndef PANELFINDPROP_HH
#define PANELFINDPROP_HH



#include "GeneralDialogCmd.hh"

class TextField;
class Label;
class PanelViewChecks;

class PanelFindProp : public GeneralDialogCmd {
    

  public:
    
    // Constructor 
    PanelFindProp ( const char*name="Find Proposal...",int active=TRUE);



    // Destructor 
    ~PanelFindProp ( );

    // Perform search
    virtual void ok_cb(void *client_data);

    virtual void cancel_cb(void *client_data);

    virtual const char *const className () { return "PanelFindProp"; }



  protected:
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();
    virtual void UpdateGeneralForm();



  private:

    // buttons
    TextField    *search_w;    // search text widget
    Label        *search_label;
    Label        *search_label2;
    PanelViewChecks *view_results;


};

#endif
