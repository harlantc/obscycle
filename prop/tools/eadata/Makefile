# Makefile for obs/prop/tools/eadata
#
#
CP      = cp
MK_TOP  = ../../../..
include $(MK_TOP)/infra/Makefile.master       # Sets ASCDS_BLD_ env. variables
BIN_DIR = $(ASCDS_BLD_BIN)
PAR_DIR = $(ASCDS_BLD_SYS_PARAM)
LIB_DIR = $(ASCDS_BLD_LIB)
DATA_DIR = $(ASCDS_BLD_DATA)
HELP_DIR = $(ASCDS_BLD_HELP_PATH)
QUICKHELP_DIR = $(ASCDS_BLD_QUICKHELP_PATH)
 
EXECUTABLES     =
LIB_FILES	= 
PERLSCRIPTS	= prop_get_pimms_ea.pl prop_get_caldb_ea.pl prop_strip_ea.pl \
		  prop_install_pimms_ea.pl prop_set_links_pimms_ea.pl \
		  prop_copy_files_pimms_ea.pl
SCRIPTS		=
PAR_FILES       = 
HELP_FILES      = 
DATA_FILES      = 
QUICKHELP_FILES = 

INSTALL_BIN_FILES = $(EXECUTABLES:%=$(BIN_DIR)/%) $(PERLSCRIPTS:%=$(BIN_DIR)/%)
INSTALL_PAR_FILES = $(PAR_FILES:%=$(PAR_DIR)/%)
INSTALL_LIB_FILES = $(LIB_FILES:%=$(LIB_DIR)/%)
INSTALL_HELP_FILES = $(HELP_FILES:%=$(HELP_DIR)/%)
INSTALL_QUICKHELP_FILES = $(QUICKHELP_FILES:%=$(QUICKHELP_DIR)/%)
INSTALL_PROJECT_FILES = $(PROJECT_FILES:%=$(PROJECT_DIR)/%)
 
MAKEALL = $(PERLSCRIPTS)
MAKECLEAN = $(MAKEALL) *.o *.d *.gp *.tcov Makefile.bak Templates.DB
INSTALL_FILES = $(INSTALL_BIN_FILES) $(INSTALL_PAR_FILES) \
		$(INSTALL_LIB_FILES) \
		$(INSTALL_HELP_FILES) $(INSTALL_QUICKHELP_FILES)

include $(MK_TOP)/infra/Makefile.all

depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.


