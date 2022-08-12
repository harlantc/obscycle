# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_
#-----------------------------------------------------------------------
#
#               Makefile for the PRoVis html files
#
#-----------------------------------------------------------------------
MK_TOP = ../../../../..
include $(MK_TOP)/infra/Makefile.cxc


PROVIS_HTML_HELP_DIR   = $(ASCDS_BLD_WWW_HTDOC)/provis/help
PROVIS_WEB    = $(HTML_DIR)/provis/help

CGI_HELP_HTML  = helpframe.html


WEB_FILES        = $(CGI_HELP_HTML) 

INSTALL_HTDOC_FILES = $(WEB_FILES:%=$(PROVIS_HTML_HELP_DIR)/%)
INSTALL_FILES = $(INSTALL_HTDOC_FILES)


WEBINSTALL_HTML_FILES  = $(WEB_FILES:%=$(PROVIS_WEB)/%)
WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww


# DO NOT DELETE THIS LINE -- make depend depends on it.

