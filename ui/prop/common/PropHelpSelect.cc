
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PropHelpSelect.cc

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the GUI PropHelpSelect class.
	This is the class called to actually open the file.


* NOTES: 



* REVISION HISTORY:


        Ref. No.
        --------
        @V(#) @V(#)

        Date
        ----
        @D(#) @D(#)
 

*H******************************************************/

#include "ap_common.hh"
#include "PropHelpSelect.hh"
#include "PropHelpMsg.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropHelpSelect::PropHelpSelect (  int active)
		: SelectFileCmd ("On Proposal Applications",
			active,-1,NULL,NULL,NULL)
{
  char *ptr = getenv ("ASCDS_DOC");

  SetDirectoryPath(ptr,"prop_*.hlp");

  msg_cmd = new PropHelpMsg(NULL,"Proposal Applications");

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropHelpSelect::~PropHelpSelect()
{
  delete msg_cmd;

}
// ----------------------------------------------------------
// ------------------------------------------------------------
void PropHelpSelect::fileSelected(char *filename)
{
  curfile = filename;

  msg_cmd->SetFilename((char *)curfile.c_str());
  msg_cmd->execute();
}


