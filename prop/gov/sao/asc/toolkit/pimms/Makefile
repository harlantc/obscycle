# Makefile for the PIMMS tool.

MK_TOP = ../../../../../../..
ASC_DIR = ../..
PROP_DIR = $(ASC_DIR)/../../..

# Include the highest level macro definitions, mainly pointers.
include $(MK_TOP)/Makefile.master
include $(ASC_DIR)/Makefile.master.overrides

# Setup the installation macros
HTML_FILES = 
JAVASCRIPT_FILES = pimms.js
JSP_FILES = \
	prop_help_pimms.jsp
	pimmsBackgroundEstimationInput.jsp \
	pimmsInputControls.jsp \
	pimmsInputEnergyControls.jsp \
	pimmsInputFluxSelector.jsp \
        pimmsInputInstrument.jsp \
	pimmsInputMissionSelector.jsp \
	pimmsInputMode.jsp \
        pimmsInstruments.jsp \
	pimmsMissions.jsp \
	pimmsModelParameters.jsp \
	pimmsOutputControls.jsp \
        pimmsOutputEnergyControls.jsp \
	pimmsOutputFluxSelector.jsp \
	pimmsOutputInstrument.jsp \
        pimmsOutputMissionSelector.jsp \
	pimmsOutputMode.jsp \
	pimms.jsp \
	pimmsResults.jsp \
	pimmsSetup.jsp

MAKEALL =
MAKECLEAN =
INSTALL_FILES =

#WEBINSTALL_HTML_FILES = $(HTML_FILES:%=$(HTML_DIR)/%)
#WEBINSTALL_JAVASCRIPT_FILES = $(JAVASCRIPT_FILES:%=$(JAVASCRIPT_DIR)/%)
#WEBINSTALL_JSP_FILES = $(JSP_FILES:%=$(JSP_DIR)/%)
#WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_JAVASCRIPT_FILES) \
	#$(WEBINSTALL_JSP_FILES)

include $(PROP_DIR)/Makefile.obswww
