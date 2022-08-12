/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelUserList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager
	user List.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELUSERLIST_HH
#define PANELUSERLIST_HH


#include "ScrolledList.hh"

#include "PersonArray.hh"
#include "PanelEntry.hh"

#define  P_ASSIGN_CHAIR        "Chair"
#define  P_ASSIGN_DEPUTYCHAIR  "Deputy Chair"
#define  P_ASSIGN_MEMBER       "Reviewer"
#define  P_ASSIGN_PUNDIT       "Pundit"
#define  P_ASSIGN_PUNDITCHAIR  "Pundit Chair"
#define  P_ASSIGN_PUNDITDEPUTY "Pundit Deputy"

class PanelUserList : public ScrolledList { 
  
  public:
    // Constructor
    PanelUserList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PanelUserList();


    // Accessor Functions
    void BuildList (Boolean refresh_flg=TRUE);

    // sort by type as defined in PanelDefs.hh
    void Sort(int type);

    void  Assign(PanelEntry *,string type);

  protected:
 
    // callbacks
    virtual void DefaultAction(XmListCallbackStruct *);
    virtual void SelectAction(XmListCallbackStruct *);
    


  private:


  PersonArray *uarray;
};



#endif
