
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelMoveProp.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel GUI move proposal class.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>
#include <math.h>
#include "ap_common.hh"

//guiext
#include "GUIDefs.h"
#include "QuestionDialogManager.hh"

#include "PanelMoveProp.hh"
#include "PanelMenuWindow.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelMoveProp::PanelMoveProp ( char *name,char *ptype, int active) : 
                     NoUndoCmd ( name, active )
{
  panel_id = ptype;
    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelMoveProp::~PanelMoveProp()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelMoveProp::doit()
{
  string         errmsg;
  string         propno;
  ProposalArray *pa = NULL;
  ProposalEntry *pe = NULL;
  string         qmsg;
  string         wmsg = "";
  int            *pnos;
  int            pcnt = 0;
  int            jj;
  ProposalEntry *cpe;        /* clumps */

  qmsg = "Do you really want to move the selected proposal(s) to another panel?\n" ;
  pa = thePanelWindow->GetPropList()->GetProposals();
   
  pnos = thePanelWindow->GetSelectedProposals(&pcnt);
  if (pa && pcnt > 0) {
    for (int pidx=0; pidx < pcnt; pidx++) {
      pe = pa->GetRecordbyIndex(pnos[pidx]-1);
      if (pe) {
        vector<string> clump_list;
        jj=0;
        /* retrieves proposals with target conflicts for current proposal */
        if ( pe->GetClumps(clump_list,errmsg)) {
          string spno;
          pe->GetProposalNumber(spno) ;
          /* for each proposal that has a conflict,
           * If it's on this panel and is not selected, display a warning
           *
          */
          for (size_t ii=0;ii<clump_list.size(); ii++) {
            cpe = pa->GetRecordbyProposal(atoi(clump_list[ii].c_str()));
            if (cpe && !thePanelWindow->GetPropList()->IsProposalSelected(clump_list[ii].c_str())) {
              if (jj== (int)0) {
                wmsg += "\n";
                wmsg += spno;
                wmsg += ":";
              }
              wmsg += " ";
              wmsg += clump_list[ii];
              double dd = fmod((double)(jj+1),5.0);
              if (dd == 0) {
                wmsg += "\n         ";
              }
              jj++;
            }
          }
        }
      }
    }
    if (wmsg.length() > 1) {
      qmsg += "\nOther proposals that have a target conflict in common with these selected proposals are:\n\n";
      qmsg += wmsg;
    }

      
 
    theQuestionDialogManager->post_newlabels ( 0,
	(char *)qmsg.c_str(),
        (void *) this, 
	&PanelMoveProp::yes_cb, &PanelMoveProp::no_cb, NULL, 
	(char *)"Yes",(char *)"No");
  }
}

void PanelMoveProp::yes_cb(void *clientData)
{
  PanelMoveProp *obj = (PanelMoveProp *)clientData;

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),True);
  thePanelWindow->MoveProposals(obj->panel_id.c_str(),TRUE); 
  GUI_SetBusyCursor(thePanelWindow->baseWidget(),False);

  return;	
}
void PanelMoveProp::no_cb(void *) 
{

  return;	
}

