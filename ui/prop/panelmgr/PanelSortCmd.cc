
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelSortCmd.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This file contains the code for the  Panel Manager 
        SortCmd class. This class displays the list of
	sort options and initiates displaying the entries in the selected 
	sort order.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%

*H******************************************************/

#include <unistd.h>
#include <stdlib.h>
#include <sys/param.h>
#include <Xm/Xm.h>
#include <Xm/Form.h>
#include <Xm/TextF.h>

#include "PanelMenuWindow.hh"
#include "PanelPropList.hh"
#include "PanelSortCmd.hh"
#include "PanelSortList.hh"
#include "PropHelpCodes.h"

// gui extensions
#include "ToolTip.hh"
#include "RowColumn.hh"
#include "ToggleButton.hh"
#include "Label.hh"

// general library
#include "GUIDefs.h"

static const char *prop_sort[] = {
	"Institution",
	"Joint",
	"PI",
	"Primary Reviewer",
	"Proposal Number",
	"Proposal Type",
	"Requested Exp. Time",
	"Reviewer Score",
	"Secondary Reviewer",
 	"Subject Category",
	"Title", 
	};

static int sort_options[] = {
	PROP_SORT_PI_INST,
	PROP_SORT_JOINT,
	PROP_SORT_PI,
	PROP_SORT_PRIMARY,
	PROP_SORT_PROPNO,
	PROP_SORT_TYPE,
	PROP_SORT_TIME,
	PROP_SORT_SCORE,
	PROP_SORT_SECONDARY,
	PROP_SORT_CATEGORY,
	PROP_SORT_TITLE,
	};


// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------
PanelSortCmd::PanelSortCmd ( char *name, int active)
        : GeneralDialogCmd (name,active,this,PROP_SORT_HTML,NULL)

{
   SetMenuFields ( 'S');


   tb_array   = NULL;
   rowcol_w   = NULL;
   sort_label = NULL;
   list_label = NULL;
   sort_list  = NULL;

   sort_nbr = XtNumber(sort_options);
   selected_options = new int[sort_nbr];
   nbr_selected = 0;
}


// ----------------------------------------------------------
// Destructor
// ----------------------------------------------------------
PanelSortCmd::~PanelSortCmd()
{
  delete [] selected_options;

  delete sort_label;
  delete list_label;
  delete sort_list;

  if (tb_array) {
    for (int ii=0; ii < sort_nbr; ii++)
      delete tb_array[ii];

    delete [] tb_array;
  }

  if (rowcol_w)
    delete rowcol_w;

}


// ----------------------------------------------------------
// Virtual: Update form to display current data
// ----------------------------------------------------------
void PanelSortCmd::UpdateGeneralForm()
{
}

// ----------------------------------------------------------
// Virtual: Create form to display constraints data
// ----------------------------------------------------------
void PanelSortCmd::CreateGeneralForm()
{
  int ii;
  Widget form1,form2;

  form1 = XtVaCreateManagedWidget((char *)"SortOptions",
        xmFormWidgetClass,gc_main_form,
        XmNtopAttachment , XmATTACH_FORM,
	XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_FORM,
	XmNleftOffset,GEN_FORM_OFFSET,
        XmNrightAttachment , XmATTACH_FORM,
	XmNrightOffset,GEN_FORM_OFFSET,
        NULL);

  form2 = XtVaCreateManagedWidget((char *)"SortForm",
        xmFormWidgetClass,gc_main_form,
        XmNtopAttachment , XmATTACH_WIDGET,
        XmNtopWidget , form1,
	XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_FORM,
	XmNleftOffset,GEN_FORM_OFFSET,
        XmNrightAttachment , XmATTACH_FORM,
	XmNrightOffset,GEN_FORM_OFFSET,
	XmNbottomAttachment, XmATTACH_FORM,
	XmNbottomOffset,GEN_FORM_OFFSET,
	NULL);

  sort_label = new Label(form1,(char *)"Select Sort Options :",0,0,0,NULL);
  XtVaSetValues(sort_label->baseWidget(),
        XmNtopAttachment , XmATTACH_FORM,
        XmNleftAttachment , XmATTACH_FORM,
        NULL);
   

  rowcol_w = new RowColumn(form1,False,0,0,0,(char *)"SortRC",
	XmHORIZONTAL);
  theToolTipManager->NewToolTip(
        (char *)"Available sort options for the proposal list.",  
        rowcol_w->baseWidget());

  XtVaSetValues(rowcol_w->baseWidget(),
        XmNtopAttachment , XmATTACH_WIDGET,
        XmNtopWidget , sort_label->baseWidget(),
        XmNtopOffset , 10,
        XmNleftAttachment , XmATTACH_FORM,
        XmNrightAttachment , XmATTACH_FORM,
        XmNbottomAttachment , XmATTACH_FORM,
	XmNuserData,this,
        NULL);

  tb_array = new ToggleButton *[sort_nbr];
  for (ii =0 ; ii < sort_nbr; ii++) {
    tb_array[ii] = new ToggleButton(
	rowcol_w->baseWidget(), (char *)prop_sort[ii],
	&PanelSortCmd::ToggleCB,(void *)&sort_options[ii],
	0,0,0,(const char *)0);
    theToolTipManager->NewToolTip(
        (char *)"Available sort options for the proposal list.",  
        tb_array[ii]->baseWidget());

  }


  list_label = new Label(form2,(char *)"Selected Options :",0,0,0,NULL);
  XtVaSetValues(list_label->baseWidget(),
        XmNtopAttachment , XmATTACH_FORM,
	XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_FORM,
        NULL);
   
  sort_list = new PanelSortList(form2);
  XtVaSetValues(XtParent(sort_list->baseWidget()),
        XmNtopAttachment , XmATTACH_WIDGET,
        XmNtopWidget , list_label->baseWidget(),
	XmNtopOffset,GEN_FORM_OFFSET,
        XmNleftAttachment , XmATTACH_FORM,
        XmNrightAttachment , XmATTACH_POSITION,
        XmNrightPosition , 40,
        NULL);

  UpdateGeneralForm();

}
// ----------------------------------------------------------
// Accept 
// ----------------------------------------------------------
void PanelSortCmd::ok_cb(void *)
{
  XmStringTable table;
  XmString str;
  char *ptr;
  int  jj;
  int  ii;
  PanelPropList *obj;

  obj = thePanelWindow->GetPropList();

  if (sort_list ) {
    XtVaGetValues(sort_list->baseWidget(),
	XmNitemCount,&nbr_selected,
	XmNitems,&table,
	NULL);

    for (jj=0; jj < nbr_selected; jj++) {
      str = table[jj];
      XmStringGetLtoR(str,XmFONTLIST_DEFAULT_TAG,&ptr);
      for (ii=0; ii < sort_nbr; ii++) {
        if (strcmp(ptr,prop_sort[ii]) == 0) {
          selected_options[jj] = sort_options[ii];
          break;
        }
      }
    }

    obj->Sort(selected_options, nbr_selected);

  }
}


// ----------------------------------------------------------
// Cancel 
// ----------------------------------------------------------
void PanelSortCmd::cancel_cb(void *)
{


}

// ---------------------------------------------------------
// value changed callback for toggle button
// ---------------------------------------------------------
void PanelSortCmd::ToggleCB(Widget w,XtPointer cbData,XtPointer )
{
  int *tb = (int*)cbData;
  PanelSortCmd *obj;

  XtVaGetValues(XtParent(w),XmNuserData,&obj,NULL);
  obj->ModifyList(*tb);
}

void PanelSortCmd::ModifyList(int tb)
{
  int ii;

  for (ii=0; ii<sort_nbr;ii++) {
    if (sort_options[ii] == tb) {
      if (tb_array[ii]->GetState()) 
        sort_list->AddItem((char *)prop_sort[ii],0);
      else
        sort_list->DeleteItem((char *)prop_sort[ii]);
      break;
    }
  }
}

