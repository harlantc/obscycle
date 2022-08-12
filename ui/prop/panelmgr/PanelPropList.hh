/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelPropList.hh

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager
	Proposal List.

* NOTES:



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELPROPLIST_HH
#define PANELPROPLIST_HH

#include <vector>
#include <string>

#include "ScrolledList.hh"
#include "ProposalArray.hh"
#include "PanelEntry.hh"

class PersonEntry;

// cheat
typedef struct {
  void *plist;
  ProposalEntry *pe;
} cbPropData;



class PanelPropList : public ScrolledList { 
  
  public:
    // Constructor
    PanelPropList(
        Widget parent,           // parent widget
        void   *cbData=NULL);    // client data for callback


    // Destructor
    ~PanelPropList();


    // Accessor Functions
    int  GetCount()  { return parray->GetSize(); }

    void BuildList (PanelEntry *pe=NULL);

    ProposalArray *GetProposals() { return parray; }
    int GetLastSelected() { return last_selected_propno; }

    // sort by type as defined in ProposalDefs.hh
    void Sort(int type);
    void Sort(int *type,int nbr_options);

    // assign selected propsals to reviewer
    void AssignReviewer(int,PersonEntry *,int *,int);

    // move proposals between panels
    int RemoveProposals(PanelEntry *old_panel);
    int MoveProposals(PanelEntry *old_panel,PanelEntry *new_panel,Boolean remove_rev);
    // verify linked proposal is also selected
    Boolean CheckLinked(char **tmplist,int cnt,ProposalEntry *pe);

    // verify if given proposal is selected
    Boolean IsProposalSelected(const char* propno);
    
    string PrintProposalNumbers(Boolean all_flag=TRUE);

    void SelectProposals(vector<string> &);

    double CalculateAllottedTime();
    void  SelectLinked();

    static void yesCallback(void *);
    static void noCallback(void *);

  protected:
 
    // callbacks
    virtual void DefaultAction(XmListCallbackStruct *);
    virtual void SelectAction(XmListCallbackStruct *);
    


  private:

    ProposalArray *parray;

    ProposalEntry *current_pe;
    PersonEntry *current_person;
    int    current_type;
    vector<ProposalEntry *> peList;
    string _panel_name;
    int  last_selected_propno;
 

};



#endif
