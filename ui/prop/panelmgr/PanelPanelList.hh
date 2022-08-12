/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPanelList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager
	Panel List.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELPANELLIST_HH
#define PANELPANELLIST_HH


#include "ScrolledList.hh"
#include "PanelArray.hh"


class PanelPanelList : public ScrolledList { 
  
  public:
    // Constructor
    PanelPanelList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PanelPanelList();


    // Accessor Functions
    PanelArray *BuildList (PanelArray *pa,Boolean cb_flg=TRUE);

    void SortByPanelId();


  protected:
 
    // callbacks
    virtual void DefaultAction(XmListCallbackStruct *);
    virtual void SelectAction(XmListCallbackStruct *);
    


  private:


};



#endif
