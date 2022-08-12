
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the definition for the Panel Manager
	class which allows the user to add notes to the Log file
	based on specific GUI actions (reassigning a reviewer,
        moving a proposal, etc)


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <Xm/Xm.h>
#include <Xm/Text.h>

#include "ap_common.hh"
#include "time_convert.h"
#include "ToolTip.hh"
#include "ErrorMessage.hh"
#include "QuestionDialogManager.hh"

#include "ProposalDefs.hh"

#include "PanelLogMsgCmd.hh"
#include "PanelApp.hh"


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelLogMsgCmd::PanelLogMsgCmd ( int active,const char *name )
        : GeneralDialogCmd((char *)name,active,this,0,
	                   NULL,True,True)


{

  st = NULL;

  logname = (char *)getenv(PROP_LOG_ENV);
  logname.append("/");
  logname.append(getenv("LOGNAME"));
  logname.append("_panel.notes");

}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelLogMsgCmd::~PanelLogMsgCmd()
{
  if (st)
    delete st;

}
// ----------------------------------------------------------
// Create a scrolled text widget to display the current log file.
// ----------------------------------------------------------
void PanelLogMsgCmd::CreateGeneralForm()
{

  st = new ScrollText(gc_main_form,NULL,0,80,25,NULL,NULL,
        0,0,0,NULL);
  theToolTipManager->NewToolTip(
	(char *)"Add notes to the panel manager log file.",
 	st->baseWidget());
  

  XtVaSetValues(XtParent(st->baseWidget()),
        XmNtopAttachment ,XmATTACH_FORM,
        XmNleftAttachment ,XmATTACH_FORM,
        XmNrightAttachment ,XmATTACH_FORM,
        XmNbottomAttachment ,XmATTACH_FORM,
        NULL);



  // update data in form
  UpdateGeneralForm();

}
// ----------------------------------------------------------
// Routine to update view of current text file.
// ----------------------------------------------------------
void PanelLogMsgCmd::UpdateGeneralForm()
{
  XtUnmanageChild(st->baseWidget());

  st->DisplayFile((char *)logname.c_str(),TRUE);

  
  XtManageChild(st->baseWidget());

}


// ----------------------------------------------------------
// Virtual callback function for cancel
// ----------------------------------------------------------
void PanelLogMsgCmd::cancel_cb(void *)
{
}

// ----------------------------------------------------------
// Virtual callback function for ok
// ----------------------------------------------------------

void PanelLogMsgCmd::ok_cb(void *clientData)
{
  PanelLogMsgCmd *obj = (PanelLogMsgCmd *)clientData;

  obj->SetMessage("");
  
}

void PanelLogMsgCmd::SetMessage(char *str)
{
  string val;
  if (str) val=str;
  SetMessage(val);
}
void PanelLogMsgCmd::SetMessage(const char *str)
{
  string val;
  if (str) val=str;
  SetMessage(val);
}
void PanelLogMsgCmd::SetMessage(string str)
{
  char buff[YMD_LENGTH];
  char buffer[200];
  string emsg;
  string msg; 

  msg.clear();
  GetCurrentLocalTime(buff);
  if (st) {
    msg  = st->Get();
  }
  else {
    ifstream infile (logname.c_str(),ios::in);
    if (infile.good()) {
      while (!infile.eof()) {
        memset(buffer,0,sizeof(buffer));
        infile.getline(buffer,sizeof(buffer));
        if ((infile.gcount() == (sizeof(buffer)-1)) && (infile.fail())) {
          infile.clear();
        }
        msg += buffer;
        msg += "\n";
      }
      infile.close();
    }
  }
    
  if (str.length() > 0) {
    msg.append("\n"); 
    msg.append(buff); 
    msg.append(": "); 
    msg.append(str); 
    msg.append("\n"); 
  }

  ofstream ofile (logname.c_str(),ios::out);
  if (ofile.good()) {
    ofile << msg << endl;
    ofile.close();
  }
  else {
    emsg = "Unable to open ";
    emsg += logname;
    emsg += " for writing.";
    theApplication->Log(emsg);
    theApplication->Log(msg);
    theErrorMessage->DisplayMessage(emsg.c_str());
  } 

}

