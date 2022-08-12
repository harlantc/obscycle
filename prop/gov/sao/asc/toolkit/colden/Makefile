# Makefile for the Colden tool.

MK_TOP = ../../../../../../..
ASC_DIR = ../..
PROP_DIR = $(ASC_DIR)/../../..

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/Makefile.master
include $(ASC_DIR)/Makefile.master.overrides

# Setup the installation macros
HTML_FILES = 
JSP_FILES = \
	prop_help_colden.jsp \
	colden.jsp \
	coldenCoordinateSystems.jsp \
	coldenInputResolver.jsp \
	coldenInputPosition.jsp \
	coldenResults.jsp \
	coldenSetup.jsp \
	coldenVelocity.jsp \
	coldenVelocityRange.jsp \
	coldenVelocityRangeSelector.jsp
JAVASCRIPT_FILES = 

MAKEALL =
MAKECLEAN =
INSTALL_FILES =

include $(MK_TOP)/Makefile.all

#WEBINSTALL_HTML_FILES = $(HTML_FILES:%=$(HTML_DIR)/%)
#WEBINSTALL_JSP_FILES = $(JSP_FILES:%=$(JSP_DIR)/%)
#WEBINSTALL_JAVASCRIPT_FILES = $(JAVASCRIPT_FILES:%=$(JAVASCRIPT_DIR)/%)
#WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_JSP_FILES) \
	#$(WEBINSTALL_JAVASCRIPT_FILES)

include $(PROP_DIR)/Makefile.obswww
