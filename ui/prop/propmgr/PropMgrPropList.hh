/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Proposal Manager
	Proposal List.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PROPMGRPROPLIST_HH
#define PROPMGRPROPLIST_HH


#include <string>
#include "ScrolledList.hh"
#include "ProposalArray.hh"
#include "TargetArray.hh"


class PropMgrPropList : public ScrolledList { 
  
  public:


    // Constructor
    PropMgrPropList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PropMgrPropList();


    // Accessor Functions
    void BuildList (Boolean view_flag=TRUE);
    void RebuildList(enum LOAD_TYPES=LOAD_ALL,const char *param=NULL,
	const char *ao = NULL, const char *status = NULL);
    void Refresh();

    Boolean IsProposalView() { return prop_view; }

    void SortByProposalNumber();
    Boolean Sort(int *, int,Boolean is_prop = TRUE);

    int  GetPropCount()  { return parray->GetSize(); }
    int  GetTargetCount()  { return tarray->GetSize(); }

    void RemoveItems();

    void UpdateReviews(long proposal_id);

    void PrintProposal(ostream &oss,const char *propno);
    void PrintReview(int type,ostream &,const char *propno,const char *pname=NULL);

    void PrintProposalNumbers(ostream &oss,Boolean all_flag=FALSE);
    void PrintTargetIds(ostream &oss,Boolean all_flag=FALSE);

    ProposalArray *GetProposals() { return parray; }
    ProposalEntry *GetProposal();
    string GetCurrentProposalNumber();

  protected:
 
    // callbacks
    
    


  private:

    ProposalArray   *parray;
    TargetArray     *tarray;
    int             prop_view;
    char            pwdfile[FILENAME_MAX];
    enum LOAD_TYPES cur_type;
    string          cur_param;
    string          cur_ao;
    string          cur_status;

};



#endif
