/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelProposalList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager
	entire proposal List.  This is not the list of proposals
	already assigned to a specific panel.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELPROPOSALLIST_HH
#define PANELPROPOSALLIST_HH


#include "ScrolledList.hh"

#include "ProposalArray.hh"
#include "PanelEntry.hh"

class PanelProposalList : public ScrolledList { 
  
  public:
    // Constructor
    PanelProposalList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PanelProposalList();


    // Accessor Functions
    void BuildList ();

    // sort by type as defined in PanelDefs.hh
    void Sort(int type);

    void  Assign(PanelEntry *);

  protected:
 
    // callbacks
    virtual void DefaultAction(XmListCallbackStruct *);
    virtual void SelectAction(XmListCallbackStruct *);
    


  private:


  ProposalArray *parray;
};



#endif
