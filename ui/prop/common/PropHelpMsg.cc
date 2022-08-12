
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the  Proposal Planning 
        code to display a help message in a separate text window.


* NOTES: 



* REVISION HISTORY:


        Ref. No.
        --------
        @V(#) @V(#)

        Date
        ----
        @D(#) @D(#)
 

*H******************************************************/
#include <Xm/Xm.h>
#include <Xm/Text.h>

#include "ap_common.hh"
#include "ScrollText.hh"
#include "PropHelpMsg.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PropHelpMsg::PropHelpMsg (const char *hfile,const char *name ) :
	ViewFileCmd((char*)name,TRUE,0,-1,
		False,0,NULL,False,False,False,True)
{
  SetMenuFields('A',NULL,NULL);
  if (hfile) {
     help_filename = getenv("ASCDS_DOC");
     help_filename += "/";
     help_filename += hfile;
     SetFilename((char *)help_filename.c_str());
  }

}

// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PropHelpMsg::~PropHelpMsg()
{

}

void PropHelpMsg::SetUp()
{

  DisplayFile();
  XmTextShowPosition(st->baseWidget(),0);  


}
