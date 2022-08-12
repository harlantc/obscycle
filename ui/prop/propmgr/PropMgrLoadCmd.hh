
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrLoadCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning 
	


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRLOADCMD_HH
#define PROPMGRLOADCMD_HH


// lib/guiext
#include "GeneralDialogCmd.hh"

#include "ProposalDefs.hh"

class RowColumn;
class TextField;
class ToggleButton;
class Label;

#define  NBR_STATUS_OPTS   3 
#define  NBR_POPTS         11 

class PropMgrLoadCmd : public GeneralDialogCmd {
    
  public:
    
    
    // Constructor 
    PropMgrLoadCmd (int active = FALSE,const char *name="Retrieve..." );

    // Destructor 
    ~PropMgrLoadCmd ( );

    // Accept 
    virtual void ok_cb(void *client_data);


    static void ToggleCB(Widget,XtPointer,XtPointer);

    virtual const char *const className () { return "PropMgrLoadCmd"; }


  protected:
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();
    virtual void UpdateGeneralForm();

  private:

    Label        *warn_label;   // information label
    Label        *form_label;   // information label
    Label        *text_label;   // proposal number
    Label        *ao_label;
    TextField    *ao_w;
    TextField    *textw;
    RowColumn    *status_w;	// RowColumn container widget
    RowColumn    *rowcol_w;	// RowColumn container widget
    ToggleButton *statbtn[NBR_STATUS_OPTS];
    ToggleButton *tbtn[NBR_POPTS];
  
  
    



};

	
#endif
