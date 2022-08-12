
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel manager to 
	view CoIs for all proposals assigned to a panel.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#ifndef PROPVIEWCOIS_HH
#define PROPVIEWCOIS_HH


#include "GeneralDialogCmd.hh"
#include "PersonArray.hh"
#include "PropCoIList.hh"
#include "PropPrintFile.hh"


class Label;
class Pushbutton;

class PropViewCoIs : public GeneralDialogCmd {
    
  public:
    
    // Constructor 
    PropViewCoIs ( int , Widget main_w,
	const char *name="Co-Investigators...");

    // Destructor 
    ~PropViewCoIs ( );

    // Accessor methods


    static void SortbyProposal(Widget,XtPointer,XtPointer);
    static void SortbyCoI(Widget,XtPointer,XtPointer);
    static void SortbyInstitution(Widget,XtPointer,XtPointer);

    void SetMain(Widget main_window) ;
    virtual const char *const className () { return "PropViewCoIs"; }


  protected:
    
    virtual ProposalArray * GetProposals() { return NULL; }

    // Called to create the specific fields
    virtual void CreateGeneralForm ();

    // Called to Update data window
    virtual void UpdateGeneralForm ();

    // Update the display 
    virtual void ok_cb (void *);
 
    PropCoIList *plist;
    PushButton   *pbtn;
    PushButton   *cbtn;
    PushButton   *ibtn;
    PropPrintFile *printcmd;

    Widget main_w;
    
};
#endif
