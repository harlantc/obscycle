# Makefile for the Resouce Cost Calculator tool.

MK_TOP = ../../../../../../..
ASC_DIR = ../..
PROP_DIR = $(ASC_DIR)/../../..

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/Makefile.master
include $(ASC_DIR)/Makefile.master.overrides

# Setup the installation macros
HTML_FILES = 
JAVASCRIPT_FILES = 
JSP_FILES = \
	rccalc.jsp \
	rccalcInputPosition.jsp \
	rccalcInputResolver.jsp \
	rccalcCoordinateSystems.jsp \
	rccalcInputExposure.jsp \
	rccalcInputInstrument.jsp \
	rccalcInputUninterruptConstraint.jsp \
	rccalcInputCoordinatedConstraint.jsp \
	rccalcInputPhaseConstraint.jsp \
	rccalcInputPointingConstraint.jsp \
	rccalcInputGroupConstraint.jsp \
	rccalcInputMonitorConstraint.jsp \
	rccalcInputWindowConstraint.jsp \
	rccalcInputRollConstraint.jsp \
	rccalcSetup.jsp \
	rccalcResults.jsp \
	prop_help_rccalc.jsp


MAKEALL =
MAKECLEAN =
INSTALL_FILES =

include $(MK_TOP)/Makefile.all

#WEBINSTALL_HTML_FILES = $(HTML_FILES:%=$(HTML_DIR)/%)
#WEBINSTALL_JSP_FILES = $(JSP_FILES:%=$(JSP_DIR)/%)
#WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_JSP_FILES) 

include $(PROP_DIR)/Makefile.obswww
