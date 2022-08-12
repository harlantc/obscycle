/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: PanelUtils.cc

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:



* NOTES:  


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%


*H******************************************************/
#include <string.h>
#include "PanelUtils.hh"


void FormatCategoryMenu(DBSubcat *dbs,string &catcmd)
{
  char   buf[10];

  catcmd.clear();
  if (dbs) {
    memset(buf,0,sizeof(buf));
    sprintf(buf,"(%02d)",dbs->get_catcode());
    catcmd= buf;
    catcmd.append( dbs->get_description());
  }

}
