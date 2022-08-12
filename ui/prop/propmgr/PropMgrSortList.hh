
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Manager scrolled
	list of selected sort options.  This list displays the
	selection order of the sort options.


* NOTES: 


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H******************************************************/
#ifndef PROPMGRSORTLIST_HH
#define PROPMGRSORTLIST_HH



#include "ScrolledList.hh"


class PropMgrSortList : public ScrolledList {

  public:

    PropMgrSortList( Widget parent);

    ~PropMgrSortList (); 



    virtual const char *const className() {return ("PropMgrSortList");}

  protected:

    // callback for option selected
    virtual void SelectAction(XmListCallbackStruct *);



};


#endif
