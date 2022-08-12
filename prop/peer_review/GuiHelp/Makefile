#
#
MK_TOP  = ../../../../
include $(MK_TOP)/infra/Makefile.cxc

DATA_DIR = $(PR_TOP)/GuiHelp/

DATA_FILES = PRman

include $(MK_TOP)/infra/Makefile.all
COPY_P 	= cp -rf

$(INSTALL_FILES): 
	$(COPY_P) $(@F)  $(DATA_DIR)

# DO NOT DELETE THIS LINE -- make depend depends on it.
