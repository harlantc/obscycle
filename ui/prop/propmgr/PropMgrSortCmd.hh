
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropMgrSortCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Planning 
	SortCmd class. This will display the sort options for
	the Proposal/Target lists table.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPMGRSORTCMD_HH
#define PROPMGRSORTCMD_HH



// lib/guiext
#include "GeneralDialogCmd.hh"

#include "ProposalDefs.hh"

class PropMgrSortList;
class RowColumn;
class ToggleButton;
class Label;

class PropMgrSortCmd : public GeneralDialogCmd {
    
  public:
    
    
    // Constructor 
    PropMgrSortCmd (Boolean stype,const char *name="Sort...", int active = FALSE);

    // Destructor 
    ~PropMgrSortCmd ( );

    // Accept 
    virtual void ok_cb(void *client_data);

    // clear input fields
    virtual void cancel_cb(void *client_data);

    static void ToggleCB(Widget,XtPointer,XtPointer);

    virtual const char *const className () { return "PropMgrSortCmd"; }


  protected:
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();
    virtual void UpdateGeneralForm();

    void ModifyList(int);
  private:

    Label        *sort_label;   // information label
    Label        *list_label;   // information label
    RowColumn    *rowcol_w;	// RowColumn container widget
    PropMgrSortList *sort_list;   // list of selected options
    ToggleButton **tb_array;    // sort option toggle buttons

    int          sort_nbr;	// total number of sort options
    int  *selected_options;     // array of selected sort options
    int  nbr_selected;          // number of options selected

    Boolean is_proposal;

};

	
#endif
