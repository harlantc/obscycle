/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropCoIList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal 
	list of Co-Investigators .

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PROPCOILIST_HH
#define PROPCOILIST_HH


#include "ScrolledList.hh"

#include "PersonArray.hh"
#include "ProposalArray.hh"

class PropCoIList : public ScrolledList { 
  
  public:
    // Constructor
    PropCoIList(
        Widget parent,           // parent widget
        Widget main_window,
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PropCoIList();


    // Accessor Functions
    void BuildList (ProposalArray *);

    // sort by type 
    void Sort(enum PERSON_SORT_TYPES);

    void DisplayData();

    void SetMain (Widget mw) { main_w = mw; }
  protected:
 
    // callbacks
    virtual void DefaultAction(XmListCallbackStruct *);
    virtual void SelectAction(XmListCallbackStruct *);
    
    Widget main_w;


  private:


  PersonArray *parray;
};



#endif
