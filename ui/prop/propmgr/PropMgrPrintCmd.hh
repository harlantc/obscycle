
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	PropMgrPrintCmd class.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRPRINTCMD_HH
#define PROPMGRPRINTCMD_HH


#include "PrintCmd.hh"
#include "ProposalDefs.hh"


class RowColumn;
class ToggleButton;
class Label;


class PropMgrPrintCmd : public PrintFileCmd {
    
  public:
    
    // Constructor 
    PropMgrPrintCmd (  int active,void *client_data,int window_help);


    // Destructor 
    ~PropMgrPrintCmd ( );

    virtual const char *const className () { return "PropMgrPrintCmd"; }
    
  protected:
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();
    virtual void UpdateGeneralForm ();

    virtual void ok_cb(void *);
    virtual void cancel_cb(void *);

    Label        *form_label;    // information label
    Label        *form2_label;   // information label
    Label        *nbr_label;     // information label
    RowColumn    *rowcol_w;      // RowColumn container widget
    RowColumn    *rowcol2_w;     // RowColumn container widget
    ToggleButton **tb_array;     // form option toggle buttons
    RowColumn    *rowcol3_w;     // RowColumn container widget
    ToggleButton *all_btn;       // form option toggle buttons
    ToggleButton *select_btn;    // form option toggle buttons

    int          form_nbr;       // total number of form options
    int          tgt_nbr;        // total number of form options

    char         pwdfile[FILENAME_MAX];
    Widget       form1_w;
    Widget       form2_w;


};

#endif
