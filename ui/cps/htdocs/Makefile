# Makefile for obs/ui/cps (User Interface for CPS Help files) 
#
MK_TOP  = ../../../..
include $(MK_TOP)/infra/Makefile.master

HTDOC_DIR = $(ASCDS_BLD_WWW_HTDOC)/cps

HTDOC_FILES = index.html \
	Chandra.help.html \
	ChandraDDT.help.html \
	chandra_coi.html \
	chandra_constraints.html \
	chandra_cover.html \
	chandra_ddt.html \
	chandra_ddt_cover.html \
	chandra_files.html \
	chandra_instr.html \
	chandra_joint.html \
	chandra_pi.html \
	chandra_target.html \
	chandra_too.html \
	science_keywords.html \
	cpshelp.css



INSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%)

MAKEALL =
MAKECLEAN = $(MAKEALL) *.o *.d *.gp *.tcov Makefile.bak Templates.DB
INSTALL_FILES = $(INSTALL_HTDOC_FILES)

WEBINSTALL_HTML_FILES = $(HTDOC_FILES:%=$(HTML_DIR)/cps/%)

WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) 

include $(MK_TOP)/obscycle/prop/Makefile.obswww



# DO NOT DELETE THIS LINE -- make depend depends on it.
