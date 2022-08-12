# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_
#-----------------------------------------------------------------------
#
#               Makefile for the PRoVis html files
#
#-----------------------------------------------------------------------
MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master


WEBINSTALL_MODULES  = help images
MODULES             = help images

include $(MK_TOP)/infra/Makefile.module       # Makefile for whole directory tree


# Define local include and library directories
HTDOC_DIR   = $(ASCDS_BLD_WWW_HTDOC)/provis

CGI_HTML         = provis.css \
		blankframe.html \
		buttonsframe.html \
		coordinates.html \
		dataframe.html \
		inputframe.html \
		optionsframe.html \
		plotframe.html \
		titleframe.html \
		footerframe.html \
		index.html \
		welcome.html


CGI_JAVASCRIPT   = buttonsframe.js \
		dataframe.js \
		globals.js \
		inputframe.js \
		optionsframe.js \
		plotframe.js

HTDOC_FILES = $(CGI_HTML) $(CGI_JAVASCRIPT)
INSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%) 

MAKEALL = 

INSTALL_FILES = $(INSTALL_HTDOC_FILES)


WEBINSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTML_DIR)/provis/%)

WEBINSTALL_FILES = $(WEBINSTALL_HTDOC_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww


# DO NOT DELETE THIS LINE -- make depend depends on it.

