
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:	This contains the code for the Print option on the
		File pulldown menubar for the Proposal Planning - 
		PANEL MANAGER application.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <stdlib.h>
#include <unistd.h>
#include <fstream>
#include <Xm/Xm.h>
#include <Xm/Label.h>
#include <Xm/RowColumn.h>
#include <Xm/Separator.h>

#include "ToolTip.hh"
#include "TextField.hh"
#include "ToggleButton.hh"
#include "GUIDefs.h"
#include "GUIEnv.h"

#include "PanelPrintCmd.hh"
#include "PanelMenuWindow.hh"

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------
PanelPrintCmd::PanelPrintCmd (  
        int  active,
	void *client_data,
        int  window_help)
	: PrintFileCmd (active,client_data,window_help)
{


}


// ------------------------------------------------------------
// Destructor
// ------------------------------------------------------------
PanelPrintCmd::~PanelPrintCmd()
{
  
}

// ------------------------------------------------------------
// Build the fields for the print command form
// ------------------------------------------------------------
void PanelPrintCmd::CreateGeneralForm()
{
  Widget rowcol_w;
  Widget label_w;
  Widget rowcol2_w;
  Widget label2_w;
  Widget sep;

  BuildPrinterField();
  BuildOutputField(printer_w->baseWidget());

  sep = XtVaCreateManagedWidget("separator",
        xmSeparatorWidgetClass,gc_main_form,
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,XtParent(text_w->baseWidget()),
	XmNtopOffset,5,
	XmNleftAttachment,XmATTACH_FORM,
	XmNrightAttachment,XmATTACH_FORM,
        NULL);

  label_w = GUI_CreateLabel(gc_main_form,"All Panels:",
                NULL);
  XtVaSetValues(label_w,
	XmNtopAttachment,XmATTACH_WIDGET,
	//XmNtopWidget,XtParent(text_w->baseWidget()),
	XmNtopWidget,sep,
	XmNtopOffset,5,
	XmNleftAttachment,XmATTACH_FORM,
	NULL);

  rowcol_w = XmCreateRowColumn(gc_main_form,(char *)"TextOptions",0,0);
  XtVaSetValues(rowcol_w,
        XmNindicatorType, XmN_OF_MANY,
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,label_w,
	XmNleftAttachment,XmATTACH_FORM,
        NULL);

  all_btn  = new ToggleButton(rowcol_w,"Panel Data",
        NULL,NULL,0,0,0,"PrintPanel");
  theToolTipManager->NewToolTip("Print panel display.",
	all_btn->baseWidget());
  alllist_btn  = new ToggleButton(rowcol_w,"Proposals w/Titles",
        NULL,NULL,0,0,0,"PrintAllTitles");
  theToolTipManager->NewToolTip("Print proposal data with titles for all panels.",
	alllist_btn->baseWidget());

  label2_w = GUI_CreateLabel(gc_main_form,"Selected Panel:",
                NULL);
  XtVaSetValues(label2_w,
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,sep,
	//XmNtopWidget,XtParent(text_w->baseWidget()),
	XmNtopOffset,5,
	XmNleftAttachment,XmATTACH_WIDGET,
	XmNleftWidget,rowcol_w,
	XmNleftOffset,10,
	NULL);

  rowcol2_w = XmCreateRowColumn(gc_main_form,(char *)"TextOptions",0,0);
  XtVaSetValues(rowcol2_w,
        XmNindicatorType, XmN_OF_MANY,
	XmNtopAttachment,XmATTACH_WIDGET,
	XmNtopWidget,label2_w,
	XmNleftAttachment,XmATTACH_WIDGET,
	XmNleftWidget,rowcol_w,
	XmNleftOffset,10,
        NULL);

  cur_btn  = new ToggleButton(rowcol2_w,"Panel Member Data",
        NULL,NULL,0,0,0,"PrintCurrent");
  theToolTipManager->NewToolTip("Print member display for selected panel.",
	cur_btn->baseWidget());
  plist_btn  = new ToggleButton(rowcol2_w,"Proposal List",
        NULL,NULL,0,0,0,"PrintProposals");
  theToolTipManager->NewToolTip("Print proposal display for selected panel.",
	plist_btn->baseWidget());
  panlist_btn  = new ToggleButton(rowcol2_w,"Proposals w/Titles",
        NULL,NULL,0,0,0,"PrintTitles");
  theToolTipManager->NewToolTip("Print proposal data with titles for selected panel.",
	panlist_btn->baseWidget());

  XtManageChild(rowcol_w);
  XtManageChild(rowcol2_w);

}

// ------------------------------------------------------------
// Virtual callback - Print text and/or screen dump
// ------------------------------------------------------------
void PanelPrintCmd::ok_cb(void *client_data)
{
  string  filename;
  string  command;
  char    *tmp;

  GUI_SetBusyCursor(gc_dialog,True);

  // create a temporary filename
  tmp = getenv(ASCDS_TMP_PATH);
  if (tmp)
    {
    filename = tmp;
    filename.append("/");
    filename.append(".prop_panel");
    }

  // If text option is selected, print each object 

  if (text_w->GetState()) {
    ofstream ofile(filename.c_str());
    if (ofile.good()) {
      thePanelWindow->Print(ofile,
        all_btn->GetState(),cur_btn->GetState(),
        plist_btn->GetState(),panlist_btn->GetState(),
	alllist_btn->GetState());
    }
    else
      cerr << "Unable to open output file for printing." << endl;
    ofile.close();

    Print((char *)filename.c_str());
  }

  // Screen dump option selected
  if (screen_w->GetState())
    {
    filename.append(".xwd");
    ScreenDump(thePanelWindow->baseWidget(),(char *)filename.c_str());
    }

  GUI_SetBusyCursor(gc_dialog,False);
}

// ------------------------------------------------------------
// ------------------------------------------------------------
void PanelPrintCmd::cancel_cb(void *client_data)
{
}
