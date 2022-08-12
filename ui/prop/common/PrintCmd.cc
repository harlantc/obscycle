
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the code for the GUI PrintFileCmd class.


* NOTES: 



* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
        %I%             %G%


*H******************************************************/

#include <Xm/Xm.h>
#include <Xm/RowColumn.h>
#include <Xm/Separator.h>

#include "Label.hh"
#include "TextField.hh"
#include "ToggleButton.hh"
#include "PushButton.hh"
#include "ToolTip.hh"
#include "GUIDefs.h"
#include "QuickHelp.h"
#include "stringFcns.hh"
#include "ErrorMessage.hh"

#include "PrintCmd.hh"


// -----------------------------------------------------------
// Constructor
// -----------------------------------------------------------
PrintFileCmd::PrintFileCmd ( 
        int    active,
	void   *client_data,
        int    window_help,
	Widget parent)
        : GeneralDialogCmd ("Print...",active,client_data,window_help,parent)
{
    SetMenuFields ( 'P',"Ctrl<Key>P","Ctrl+P");
    printer_dev = "lpr ";
    right_pos = 30;
    
    printer_w = NULL;
    text_w    = NULL;
    screen_w  = NULL;
    print_lbl = NULL;
    print_w   = NULL;
    file_w    = NULL;
    browse_w    = NULL;
    select_cmd   = NULL;

    
}


// -----------------------------------------------------------
// Destructor
// -----------------------------------------------------------
PrintFileCmd::~PrintFileCmd()
{

  if (text_w) {
    delete text_w;
    delete screen_w;
  }
  if (print_lbl) {
    delete printer_w;
    delete print_lbl;
    delete print_w;
    delete file_w;
    delete browse_w;
  }

}


// -----------------------------------------------------------
// Virtual routine to create the printer form
// -----------------------------------------------------------
void PrintFileCmd::CreateGeneralForm()
{
   // always give user option to modify print command
   BuildPrinterField();
}

// -----------------------------------------------------------
// Routine to build the printer command label/textfield widgets
// -----------------------------------------------------------
void PrintFileCmd::BuildPrinterField()
{
  Widget label_w;
  Widget rc_w;

  
  label_w = GUI_CreateLabel(gc_main_form,"Print To: ",NULL);
  rc_w = XtVaCreateManagedWidget("RowColumn",
        xmRowColumnWidgetClass,gc_main_form,
        XmNborderWidth,0,
        XmNentryBorder,1,
        XmNorientation,XmHORIZONTAL,
        XmNradioBehavior,True,
        NULL);
  print_w = new ToggleButton( rc_w, (char *)"Printer",
        &PrintFileCmd::ToggleCB,this, 0,0,0);
  print_w->SetState(True,False);
  file_w = new ToggleButton( rc_w, (char *)"File",
        &PrintFileCmd::ToggleCB,this, 0,0,0);
  GUI_AttachRightPos(label_w,rc_w,NULL,right_pos,5);
   
  browse_w   = new PushButton(gc_main_form,
        "Browse",
        &PrintFileCmd::BrowseCB,this,
        0,0,0,NULL);
  theToolTipManager->NewToolTip(
        (char *)"Browse file selection",
        browse_w->baseWidget());
  XtVaSetValues(browse_w->baseWidget(),
		XmNrightAttachment,XmATTACH_FORM,
		XmNrightOffset,5,
		XmNtopAttachment,XmATTACH_WIDGET,
		XmNtopWidget,rc_w,
		XmNsensitive, FALSE,
		NULL);




  print_lbl =  new Label(gc_main_form,(char *)"Print Command: ",0,0,0,NULL);
  printer_w = new TextField(gc_main_form,NULL,0,25,NULL,NULL,
	Q_PRINT_COMMAND,0,0,"PrintCommand");
  GUI_AttachRightPos(print_lbl->baseWidget(),printer_w->baseWidget(),
        rc_w,right_pos,5);
  XtVaSetValues(printer_w->baseWidget(),
		XmNrightAttachment,XmATTACH_WIDGET,
		XmNrightWidget,browse_w->baseWidget(),
		NULL);

  select_cmd = new  PropPrintBrowse(True,0,printer_w);

  XtVaCreateManagedWidget("separator",
          xmSeparatorWidgetClass,gc_main_form,
          XmNtopAttachment,XmATTACH_WIDGET,
          XmNtopWidget,printer_w->baseWidget(),
          XmNleftAttachment,XmATTACH_FORM,
          XmNrightAttachment,XmATTACH_FORM,
          NULL);

}

void PrintFileCmd::BrowseCB(Widget ,XtPointer cbData,XtPointer )
{
  PrintFileCmd *obj = (PrintFileCmd*)cbData;

  obj->select_cmd->execute();

}
void PrintFileCmd::ToggleCB(Widget ,XtPointer cbData,XtPointer )
{
  PrintFileCmd *obj = (PrintFileCmd *)cbData;

  if (obj->print_w->GetState()) {
    obj->print_lbl->SetLabel((char *)"Print Command: ");
    obj->printer_w->Set((char *)"lpr ");
    XtVaSetValues(obj->browse_w->baseWidget(),
		XmNsensitive, FALSE,
		NULL);
  }
  else {
    obj->print_lbl->SetLabel((char *)"File: ");
    obj->printer_w->Set((char *)"");
    XtVaSetValues(obj->browse_w->baseWidget(),
		XmNsensitive, TRUE,
		NULL);
  }
}


// -----------------------------------------------------------
// Routine to build the output type toggle buttons
// -----------------------------------------------------------
void PrintFileCmd::BuildOutputField(Widget attach_w)
{
  Widget rc_w;
  Widget label_w;


  label_w = GUI_CreateLabel(gc_main_form,(char *)"Output: ",
                NULL);
  rc_w = XtVaCreateManagedWidget("RowColumn",
        xmRowColumnWidgetClass,gc_main_form,
        XmNborderWidth,0,
        XmNentryBorder,1,
        XmNorientation,XmHORIZONTAL,
        XmNradioBehavior,True,
        NULL);

  text_w   = new ToggleButton(rc_w,"Text",NULL,NULL,Q_PRINT_OPTIONS,0,0,"PrintText");
  screen_w = new ToggleButton(rc_w,"Screen Dump",NULL,NULL,Q_PRINT_OPTIONS,0,0,"PrintScreen");

  GUI_AttachRightPos(label_w,rc_w,attach_w,right_pos,20);

}

// -----------------------------------------------------------
// Virtual ok routine
// -----------------------------------------------------------
void PrintFileCmd::ok_cb(void*)
{
}

// -----------------------------------------------------------
// Virtual cancel routine
// -----------------------------------------------------------
void PrintFileCmd::cancel_cb(void* client_data)
{
   (void) client_data;  // remove compiler warning 
}

// -----------------------------------------------------------
// Print text file to user specified print command
// -----------------------------------------------------------
void PrintFileCmd::Print(char *filename,char *precommand)
{
  string command;
  string tmp;

  if (precommand)
    command = precommand;

  tmp = printer_w->Get();
  stringFcns::trim_leading_white_space(&tmp);
  
  // Print toggle button is set
  if (print_w->GetState()) {
    // if no command entered just use generic lpr command
    if (tmp.length() <= 0 ) {
      command.append(" lpr ");
      if (filename)
        command.append(filename);
    }
    else {
      // otherwise, if a filename is entered, cat the file and pipe it
      // to the new command
      if (filename) {
        command.append(" cat ");
        command.append(filename);
        command.append(" |  ");
      }
      command.append(tmp);
    }
  }
  else {
    // save to file
    if (filename) {
      command.append(" cat ");
      command.append(filename);
    }
    command += " > ";
    //cerr << "**" << tmp << "**" << endl;
    if (tmp.length() <= 0 ) {
      theErrorMessage->DisplayMessage("You must select a file if the Print To: File option is chosen. \n");
      return;
    }
    else {
      command += tmp;
    }
  }
    

  // command.append(" &");
  system(command.c_str());

}


// -----------------------------------------------------------
// This routine performs an 'xwd' command on the appropriate
// window.  It then performs the 'xpr' command to convert the
// dump to postscript and pipes the output to the user specified
// printer command. 
// -----------------------------------------------------------
void PrintFileCmd::ScreenDump(Widget ww,char *filename)
{
  string  command;
  char    buffer[20];
  char    *tmp;


  // first dump the screen to a file 
  command = "xwd -frame";
  if (ww) 
    {
    sprintf(buffer,"%ld",XtWindow(ww));
    command.append(" -id ");
    command.append(buffer);
    }

  command.append(" -out ");
  command.append(filename);
  system(command.c_str());

  // now convert to postscript 
  //command = "xpr -device ps -gray 2 -scale 3 < ";
  command = "convert -monochrome xwd:";
  command += filename;
  command += " ps:-";
  command.append(" | ");

  // get the printer command and append it to the command string
  tmp = printer_w->Get();

  if (print_w->GetState()) {
    if (!tmp || *tmp == '\0')
      command.append("lpr ");
    else
      command.append(tmp);
  }
  else {
    command += " cat > ";
    command +=  tmp;
  }
  command.append(" &");

  // execute the command
  system(command.c_str());

}
