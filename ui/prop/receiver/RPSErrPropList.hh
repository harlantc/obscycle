/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrPropList.hh

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the RPS Error GUI -
	Proposal ScrolledList class.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef RPSERRPROPLIST_HH
#define RPSERRPROPLIST_HH


#include <string>
#include <vector>
#include "ScrolledList.hh"

typedef struct {
  char pline[200];
}SHORT_LINE;

class RPSErrPropList : public ScrolledList { 
  
  public:
    // Constructor
    RPSErrPropList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback

    // Destructor
    ~RPSErrPropList();

    enum SORT_TYPE {SORT_PROPNO,SORT_PI,SORT_TITLE,SORT_TYPE,SORT_LAST_4,
        SORT_REVERSE_4};


    // Accessor Functions
    void BuildList ();


    // 0= whole proposal number; 1=last 4 characters; 2=last 4,reversed
    void SortByProposalNumber(int);
    void SortByPI();
    void SortByTitle();
    void SortByType();
    void SortByHardcopy();
    void SortList(int type);

    int  GetCount()  { return prop_cnt; }

    static int CompareProposalNumber(const void *, const void *);
    static int CompareLast4(const void *, const void *);
    static int CompareReverse4(const void *, const void *);
    static int ComparePI(const void *, const void *);
    static int CompareTitle(const void *, const void *);
    static int CompareType(const void *, const void *);



    void    SetHardCopyReceipt(Boolean,Boolean email_flag);
    string  GetCurrentProposalNumber();
    vector<string>  GetCurrentProposalNumbers();


  protected:
 
    // callbacks
    // virtual void DefaultAction(XmListCallbackStruct *);
    // virtual void SelectAction(XmListCallbackStruct *);
    

    void DisplayItems();

  private:

    SHORT_LINE  *prop_list;
    int   prop_cnt;
    int   sort_type;

};



#endif
