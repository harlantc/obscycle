
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelSortCmd.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager 
	SortCmd class. This will display the sort options for
	the Proposal list.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PANELSORTCMD_HH
#define PANELSORTCMD_HH



// lib/guiext
#include "GeneralDialogCmd.hh"

#include "ProposalDefs.hh"

class PanelSortList;
class RowColumn;
class ToggleButton;
class Label;

class PanelSortCmd : public GeneralDialogCmd {
    
  public:
    
    
    // Constructor 
    PanelSortCmd (char *name=(char*)"Sort...", int active = TRUE);

    // Destructor 
    ~PanelSortCmd ( );

    // Accept 
    virtual void ok_cb(void *client_data);

    // clear input fields
    virtual void cancel_cb(void *client_data);

    static void ToggleCB(Widget,XtPointer,XtPointer);

    int  *GetSelectedOptions() { return selected_options; } 
    int  GetNbrSelected() { return nbr_selected; } 
    virtual const char *const className () { return "PanelSortCmd"; }


  protected:
    
    // Called to create the specific fields
    virtual void CreateGeneralForm ();
    virtual void UpdateGeneralForm();

    void ModifyList(int);
  private:

    Label        *sort_label;   // information label
    Label        *list_label;   // information label
    RowColumn    *rowcol_w;	// RowColumn container widget
    PanelSortList *sort_list;   // list of selected options
    ToggleButton **tb_array;    // sort option toggle buttons

    int          sort_nbr;	// total number of sort options
    int  *selected_options;     // array of selected sort options
    int  nbr_selected;          // number of options selected


};

	
#endif
