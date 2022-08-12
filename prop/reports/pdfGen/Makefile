#
# Makefile for the scripts/executables related to the CPS PDF generation module

MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master

BIN_DIR = $(ASCDS_BLD_BIN)

# List of python scripts
# PYTHONSCRIPTS = $(PYMOD)/genPDF.py
PYTHONSCRIPTS = pdfGen_cli.py
PYMOD = pdfGen


# List of everything to install in the bin directory
INSTALL_BIN_FILES = $(PYTHONSCRIPTS:%=$(BIN_DIR)/%)
MAKEALL = $(PYTHONSCRIPTS)
MAKECLEAN = $(MAKEALL)
INSTALL_FILES = $(INSTALL_BIN_FILES)
include $(MK_TOP)/infra/Makefile.all

SETUP_PY = MK_TOP="$(MK_TOP)" ; \
		   PYTHONPATH="$(PYTHONMOD)" ; \
		   export MK_TOP PYTHONPATH ; \
		   $(PYTHONEXE) setup.py $(SETUP_VARS)

install:
	@echo "   /----------------------------------------------------------\ "
	@echo "   |             Building CPS PDF Gen Module                   | "
	@echo "   \----------------------------------------------------------/ "
	@echo "$(ASCDS_BLD_BIN)"
	@echo "$(ASCDS_BLD_BIN) "
	$(SETUP_PY) install --install-lib=$(PYTHONMOD)

uninstall:
	$(REMOVE_RF) $(PYTHONMOD)/$(PYMOD)/*

