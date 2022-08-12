
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel GUI  to remove
	a proposal from the current panel.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#include <stdlib.h>

#include "ap_common.hh"
#include "GUIDefs.h"


#include "PanelRemoveProposal.hh"
#include "PanelMenuWindow.hh"

#define PROPDEFAULTREMOVE "This option should be used with caution! If you want to have the proposal \nreviewed by a different panel, please use the Assign->Move Proposal option. \n\nThis option should be used if there are only 2 panels covering the science category \nand this proposal has been assigned to both panels but you feel that this panel \nshould not review it because there are too many conflicts with the reviewers. \nThis will result in the proposal being reviewed by only 1 panel.\nRemember, if you want to have the proposal reviewed by a different \npanel use the Assign->Move Proposal option. \n\nPress OK to remove this proposal from the current panel, else press Cancel. "



// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelRemoveProposal::PanelRemoveProposal ( char *name, int active) : 
                     AskFirstCmd ( name, active )
{
    setQuestion ( PROPDEFAULTREMOVE );
    SetMenuFields ( 'R',NULL,NULL);


    
}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelRemoveProposal::~PanelRemoveProposal()
{
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelRemoveProposal::updateQuestion()
{
  string  msg;
  string str;
 

  str = thePanelWindow->GetPropList()->PrintProposalNumbers(FALSE);

  msg = PROPDEFAULTREMOVE;
  msg.append("\n\n");
  msg.append(str);
  msg.append("\n");

  setQuestion((char *)msg.c_str());

}


// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelRemoveProposal::doit()
{

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),True);

  thePanelWindow->RemoveProposals();

  GUI_SetBusyCursor(thePanelWindow->baseWidget(),False);

  return;	
}
