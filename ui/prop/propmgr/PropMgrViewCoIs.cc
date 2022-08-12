
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
#include <Xm/Form.h>
#include <Xm/Label.h>
#include <Xm/PanedW.h>
#include <stdio.h>
#include <fstream>


// gui extension library
#include "PropMgrViewCoIs.hh"
#include "PropMgrMenuWindow.hh"



// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropMgrViewCoIs::PropMgrViewCoIs (int active,const char *name) : 
	PropViewCoIs (active,NULL,name)
{

    
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropMgrViewCoIs::~PropMgrViewCoIs()
{

}

// ----------------------------------------------------------
// ----------------------------------------------------------
ProposalArray *PropMgrViewCoIs::GetProposals()
{
  string emsg;
  ProposalArray *parray;
  parray = thePropMgrWindow->GetPropList()->GetProposals();

  if (parray) {
    if (!parray->LoadCoIs(emsg)) {
      cerr << emsg << endl;
    }
  }
  
  return parray;
}

