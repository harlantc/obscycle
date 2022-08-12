# Makefile for obs/prop/tools/doc Documents
#

MK_TOP = ../../../..

DOC_DIR = $(ASCDS_BLD_DOC)
AHELP_DIR = $(ASCDS_BLD_DOC)/txt
XML_DIR   = $(ASCDS_BLD_DOC)/xml


ROOT_FILES = 
XML_FILES = \
	colden.xml \
	dates.xml \
	pimms.xml \
	precess.xml \
	prop-coords.xml \
	prop-time.xml \
	prop-tools.xml

#AHELP_FILES =  prop_pileup.hlp prop_frametime.hlp \
#	prop_install_pimms_ea.hlp prop_set_links_pimms_ea.hlp
#DOC_FILES =  prop_pileup.hlp prop_frametime.hlp \
#	prop_install_pimms_ea.hlp prop_set_links_pimms_ea.hlp


#include $(MK_TOP)/infra/Makefile.cxc	

INSTALL_ROOT_FILES   = $(ROOT_FILES:%=$(ASCDS_BLD_INSTALL)/%)
INSTALL_XML_FILES = $(XML_FILES:%=$(XML_DIR)/%)
INSTALL_DOC_FILES = $(DOC_FILES:%=$(DOC_DIR)/%)
INSTALL_AHELP_FILES = $(AHELP_FILES:%=$(AHELP_DIR)/%)


INSTALL_FILES = $(INSTALL_ROOT_FILES) $(INSTALL_XML_FILES) \
	$(INSTALL_DOC_FILES) $(INSTALL_AHELP_FILES)

include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.all
