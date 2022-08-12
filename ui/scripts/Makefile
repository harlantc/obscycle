# Makefile for whole ui directory.
#

MK_TOP = ../../..
include $(MK_TOP)/infra/Makefile.cxc


WEBADMIN_DIR = $(ASCDS_WEBSRV_ROOT)/webadmin/


FILES = .htaccess
PERLSCRIPTS = \
	icxc_wwwinstall.pl \
	webapp_install.pl



INSTALL_BIN_FILES = $(PERLSCRIPTS:%=$(WEBADMIN_DIR)/%) $(FILES:%=$(WEBADMIN_DIR)/%)  


MAKEALL =  $(PERLSCRIPTS)
MAKECLEAN = $(MAKEALL)
INSTALL_FILES = $(INSTALL_BIN_FILES)


include $(MK_TOP)/infra/Makefile.all


# DO NOT DELETE THIS LINE -- make depend depends on it.
