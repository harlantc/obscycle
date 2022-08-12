
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: RPSErrViewConflicts.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

	This file contains the code for the Proposal Planning
        RPSErrViewConflicts class.  This displays the conflict file
        for the selected proposal.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include "ap_common.hh"
#include <stdlib.h>
#include <string>

#include "ToolTip.hh"
#include "ScrollText.hh"

#include "ProposalDefs.hh"


#include "PanelViewConflicts.hh"
#include "PanelApp.hh"
#include "PanelMenuWindow.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelViewConflicts::PanelViewConflicts ( int active,const char *name,const char *iname)
        : ViewFileCmd ((char *)name,active,True,-1,
                       False,0,NULL,False,False,TRUE,TRUE)


{
  if (iname) 
    _fname = iname;
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelViewConflicts::~PanelViewConflicts()
{
}

// ----------------------------------------------------------
// Set up class specifics
// ----------------------------------------------------------
void PanelViewConflicts::SetUp()
{
  string  propno;
  

  propno = thePanelWindow->GetCurrentProposalNumber();
  theToolTipManager->NewToolTip(
        (char *)"View conflict file for selected proposal.",
        st->baseWidget());
  if (propno.length() > 0) {
    if (getenv("ASCDS_PROP_UPLOAD")) {
       _fname =  getenv("ASCDS_PROP_UPLOAD");
       _fname += "/CHANDRA/conflict_files/";
       _fname += propno;
       _fname += ".conflicts";

      if (access(_fname.c_str(),R_OK) == 0) {
        SetFilename((char *)_fname.c_str());
        DisplayFile();
      } else {
         st->Set((char *) "No conflict file available");
         cerr << "Unable to access " << _fname << endl;
      }

    }
  }
  else {
         st->Set((char *) "Select a single proposal to view conflict file");
  }

}
