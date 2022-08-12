/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelMemberList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager
	member List.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELMEMBERLIST_HH
#define PANELMEMBERLIST_HH

#include "ScrolledList.hh"
#include "PanelEntry.hh"


class PanelMemberList : public ScrolledList { 
  
  public:
    // Constructor
    PanelMemberList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PanelMemberList();


    // Accessor Functions
    void BuildList (PanelEntry *,Boolean del_flag=TRUE,Boolean doScore=TRUE);

    // sort by type as defined in PanelDefs.hh
    void Sort(int type);


    PersonEntry *GetCurrent();

    // get email address of all/selected members
    string GetEmailAddr(Boolean all_flag=TRUE);

  protected:
 
    // callbacks
    virtual void DefaultAction(XmListCallbackStruct *);
    virtual void SelectAction(XmListCallbackStruct *);
    

    PanelEntry *panel;   // current panel used to build list

  private:


};



#endif
