# Makefile for the Precess tool.

MK_TOP = ../../../../../../..
ASC_DIR = ../..
PROP_DIR = $(ASC_DIR)/../../..

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/Makefile.master
include $(ASC_DIR)/Makefile.master.overrides

# Setup the installation macros
HTML_FILES = 
JSP_FILES = \
	prop_help_precess.jsp \
	precess.jsp \
	precessConstellationResults.jsp \
	precessInputCoordinateSystems.jsp \
	precessInputMode.jsp \
	precessInputPosition.jsp \
	precessInputResolver.jsp \
	precessOutputControls.jsp \
	precessOutputCoordinateSelector.jsp \
	precessOutputCoordinateSystems.jsp \
	precessOutputEquinox.jsp \
	precessOutputMode.jsp \
	precessPositionResults.jsp \
	precessResults.jsp \
	precessSetup.jsp

MAKEALL =
MAKECLEAN =
INSTALL_FILES =

include $(MK_TOP)/Makefile.all

#WEBINSTALL_HTML_FILES = $(HTML_FILES:%=$(HTML_DIR)/%)
#WEBINSTALL_JSP_FILES = $(JSP_FILES:%=$(JSP_DIR)/%)
#WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_JSP_FILES)

include $(PROP_DIR)/Makefile.obswww
