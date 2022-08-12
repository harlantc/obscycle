# Makefile for prop Documents
#

MK_TOP = ../../..
include $(MK_TOP)/infra/Makefile.master	# Sets ASCDS_BLD_ env. variables

DOC_DIR = $(ASCDS_BLD_DOC)
AHELP_DIR = $(ASCDS_BLD_DOC)/txt
XML_DIR   = $(ASCDS_BLD_DOC)/xml
HTDOC_DIR = $(ASCDS_BLD_WWW_HTDOC)/docs

DOC_FILES = \
	prop.hlp \
	prop_SETUP.hlp prop_rps.hlp prop_rps_receiver.hlp  \
	prop_rps_ingest.hlp  \
	prop_rps_gui.hlp \
	prop_manager_gui.hlp prop_read_reviews.hlp \
	prop_reports.hlp prop_rps_reports.hlp \
	prop_create_peer.hlp prop_panel_create.hlp \
	prop_panel_gui.hlp prop_target_reports.hlp \
	prop_email_forms.hlp prop_read_prelim.hlp \
	propconflict.pl.hlp


# AHELP_FILES contains only application-specific help files which are 
#   accessible through on-line "ahelp";
#   prop.hlp is the required master list of files in AHELP_FILES
AHELP_FILES = prop.hlp \
	prop_rps.hlp prop_rps_receiver.hlp  \
	prop_rps_ingest.hlp  \
	prop_rps_gui.hlp \
	prop_manager_gui.hlp prop_read_reviews.hlp \
	prop_reports.hlp prop_rps_reports.hlp \
	prop_create_peer.hlp prop_panel_create.hlp \
	prop_panel_gui.hlp prop_target_reports.hlp \
	prop_email_forms.hlp prop_read_prelim.hlp \
	propconflict.pl.hlp

XML_FILES = \
	rps.xml

# web files (none currently used for prop)
HTDOC_FILES = 

INSTALL_DOC_FILES = $(DOC_FILES:%=$(DOC_DIR)/%)
INSTALL_AHELP_FILES = $(AHELP_FILES:%=$(AHELP_DIR)/%)
INSTALL_XML_FILES = $(XML_FILES:%=$(XML_DIR)/%)
WEBINSTALL_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%)

MAKEALL =
MAKECLEAN =

INSTALL_FILES = $(INSTALL_DOC_FILES) $(INSTALL_AHELP_FILES) $(INSTALL_XML_FILES)

$(WEBINSTALL_FILES) : $$(@F)
	$(REMOVE_F) $@
	cp -p $(@F) $@
	chmod a-w+x $@

include $(MK_TOP)/infra/Makefile.all

