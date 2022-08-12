/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.

* FILE NAME: %M%

* DEVELOPMENT:  ObsCycle

* DESCRIPTION:

        This header file contains the defines for the Proposal Planning
	applications help HTML indices.

* NOTES:


* REVISION HISTORY:

        Ref. No.        Date
        --------        ----
	%I%		%G%



*H****************************************************************************/

#ifndef PROPHELPCODE_H
#define PROPHELPCODE_H

#include "HelpCodes.h"

#define PROP_HELP_FILE "prop.helpmsg"

/* Prop help doesn't map to CIAO help so just delete for now */
/* make sure all helpcodes are < 0, then the help button won't show */
#define PROP_HELPMSG  -200

#define PANEL_MAIN_HTML           PROP_HELPMSG + 0
#define PANEL_PRINT_HTML          PROP_HELPMSG + 1
#define RPSERR_MAIN_HTML          PROP_HELPMSG + 2
#define RPSERR_PRINT_HTML         PROP_HELPMSG + 3
#define RPSERR_OPEN_HTML          PROP_HELPMSG + 4
#define RPSERR_VIEW_HTML          PROP_HELPMSG + 5
#define RPSERR_FIND_HTML          PROP_HELPMSG + 6
#define RPSERR_DIFF_HTML          PROP_HELPMSG + 7
#define RPSERR_INGEST_HTML        PROP_HELPMSG + 8
#define TECHEVAL_MAIN_HTML        PROP_HELPMSG + 9
#define TECHEVAL_EDIT_HTML        PROP_HELPMSG + 10
#define TECHEVAL_CONFLICTS_HTML   PROP_HELPMSG + 11
#define TECHEVAL_FIND_HTML        PROP_HELPMSG + 12
#define TECHEVAL_VIEWLOG_HTML     PROP_HELPMSG + 13
#define TECHEVAL_EXIT_HTML        PROP_HELPMSG + 14
#define TECHEVAL_PRINT_HTML       PROP_HELPMSG + 15
#define PANEL_EDIT_HTML           PROP_HELPMSG + 16
#define PANEL_VIEW_PROP_HTML      PROP_HELPMSG + 17
#define PANEL_VIEW_MEMBER_HTML    PROP_HELPMSG + 18
#define PANEL_ASSIGN_MEMBER_HTML  PROP_HELPMSG + 19
#define PANEL_ASSIGN_PROP_HTML    PROP_HELPMSG + 20
#define PANEL_VIEW_COIS_HTML      PROP_HELPMSG + 21
#define PROP_SORT_HTML            PROP_HELPMSG + 22
#define PROPMGR_MAIN_HTML         PROP_HELPMSG + 23
#define PROPMGR_PRINT_HTML        PROP_HELPMSG + 24
#define PROPMGR_VIEWPROP_HTML     PROP_HELPMSG + 25
#define PROPMGR_REVIEWS_HTML      PROP_HELPMSG + 26
#define PROPMGR_EMAIL_HTML        PROP_HELPMSG + 27
#define PROPMGR_RETRIEVE_HTML     PROP_HELPMSG + 28
#define PROPMGR_EDIT_TECH_HTML    PROP_HELPMSG + 29
#define PROPMGR_EDIT_PEER_HTML    PROP_HELPMSG + 30
#define RPSERR_RETRIEVE_HTML      PROP_HELPMSG + 31


#endif
