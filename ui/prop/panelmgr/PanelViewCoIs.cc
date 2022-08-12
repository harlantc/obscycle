
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the Panel manager class which
	displays a list of Co-Investigators for proposals assigned to
	a panel.


* NOTES:

* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <Xm/Xm.h>


#include "PanelApp.hh"
#include "PanelViewCoIs.hh"
#include "PanelMenuWindow.hh"




// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelViewCoIs::PanelViewCoIs (int active,const char *name) : 
	PropViewCoIs (active,NULL,name)
{

    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelViewCoIs::~PanelViewCoIs()
{

}

// ----------------------------------------------------------
// ----------------------------------------------------------
ProposalArray *PanelViewCoIs::GetProposals()
{
  ProposalArray *parray;
  parray = thePanelWindow->GetPropList()->GetProposals();

  return parray;

}

