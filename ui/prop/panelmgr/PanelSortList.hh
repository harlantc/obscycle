
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelSortList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager scrolled
	list of selected sort options.  This list displays the
	selection order of the sort options.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#ifndef PANELSORTLIST_HH
#define PANELSORTLIST_HH



#include "ScrolledList.hh"


class PanelSortList : public ScrolledList {

  public:

    PanelSortList( Widget parent);

    ~PanelSortList (); 



    virtual const char *const className() {return ("PanelSortList");}

  protected:

    // callback for option selected
    virtual void SelectAction(XmListCallbackStruct *);



};


#endif
