# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_
#-----------------------------------------------------------------------
#
#               Makefile for the PRoVis cgi-bin Files
#
#-----------------------------------------------------------------------
MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.cxc


# Define local include and library directories

CGIBIN_DIR   = $(ASCDS_BLD_WWW_CGI)/provis
SRC_CONFIG_DIR    = ../data/

CGI_FILES = provis_gen.cgi \
		provis_readcal.cgi \
		provis_load.cgi \
		provis_upload.cgi

PERLSCRIPTS	= $(CGI_FILES)

CGI_CONFIG        = provis_config.cgi

INSTALL_CGI_FILES = $(CGI_FILES:%=$(CGIBIN_DIR)/%) \
                    $(CGI_CONFIG:%=$(CGIBIN_DIR)/%)

MAKEALL        = $(PERLSCRIPTS) $(CGI_CONFIG)
MAKECLEAN      = $(MAKEALL)


INSTALL_FILES     = $(INSTALL_CGI_FILES)

WEBINSTALL_CGI_FILES = $(CGI_FILES:%=$(CGI_DIR)/provis/%) \
                       $(CGI_CONFIG:%=$(CGI_DIR)/provis/%)

WEBINSTALL_FILES = $(WEBINSTALL_CGI_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

#***********************Build the Application(s)************
$(CGI_CONFIG): $(CGI_CONFIG:%=$(SRC_CONFIG_DIR)/%) 
	$(REMOVE_F) $@
	$(COPY_P)  $(SRC_CONFIG_DIR)/$@ $(@D)


announce1:
	@echo " /----------------------------------------------------------\ "
	@echo " |                 Installing PRoVis cgi-bin                | "
	@echo " \----------------------------------------------------------/ "



# DO NOT DELETE THIS LINE -- make depend depends on it.


