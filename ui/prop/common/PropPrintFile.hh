
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  UI

* DESCRIPTION:

        This file contains the definition for the Proposal Planning
	Print  class.


* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/

#ifndef PROPPRINTFILE_HH
#define PROPPRINTFILE_HH


#include "PrintCmd.hh"


class ToggleButton;

class PropPrintFile : public PrintFileCmd {
    
    
  protected:
    
    virtual void ok_cb(void *);
    virtual void cancel_cb(void *);

    string filename;
    Boolean delete_flg;


  public:
    
    // Constructor 
    PropPrintFile (  int active,void *client_data,int window_help);


    // Destructor 
    ~PropPrintFile ( );

    void SetFilename(const char *str,Boolean flg)  
	{ filename = str; delete_flg=flg; }

    virtual const char *const className () { return "PropPrintFile"; }
};

#endif
