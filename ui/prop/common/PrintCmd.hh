
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the GUI PrintFileCmd class.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
        %I%             %G%

*H******************************************************/
#ifndef PRINTFILECMD_HH
#define PRINTFILECMD_HH


#include <string>
#include "cxcconfig.h"
#include "GeneralDialogCmd.hh"
#include "PropPrintBrowse.hh"

class TextField;
class ToggleButton;
class PushButton;
class Label;


class PrintFileCmd : public GeneralDialogCmd {
    
  protected:

    Label        *print_lbl;
    ToggleButton *print_w;     // print file
    ToggleButton *file_w;     // file selection
    PushButton   *browse_w;   // browse for file selection

    TextField    *printer_w;  // text for printer command
    ToggleButton *text_w;     // output type - text
    ToggleButton *screen_w;   // output type - screen dump
   
    string    printer_dev;
    int        right_pos;

    PropPrintBrowse *select_cmd;
    
    virtual void CreateGeneralForm();

    virtual void ok_cb(void *);
    virtual void cancel_cb(void *);

    static void ToggleCB(Widget,XtPointer,XtPointer);
    static void BrowseCB(Widget,XtPointer,XtPointer);

    // build the printer label/textfield
    void  BuildPrinterField();   

    // build the output type toggle buttons
    void  BuildOutputField(Widget attach_w);  
    
  public:
    
    // Constructor 
    PrintFileCmd (int active,void *client_data,int window_help,
	          Widget parent=NULL);

    // Destructor 
    ~PrintFileCmd ( );


    void Print(char *filename,char *precommand = NULL);
    void ScreenDump(Widget,char *filename);

    virtual const char *const className () { return "PrintFileCmd"; }

    

};
#endif
