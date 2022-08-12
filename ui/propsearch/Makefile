# Makefile for obs/ui/propsearch (User Interface for Proposal Search) 
#
MK_TOP  = ../../..
include $(MK_TOP)/infra/Makefile.cxc



HTDOC_DIR = $(ASCDS_BLD_WWW_HTDOC)/propsearch
CGIBIN_DIR = $(ASCDS_BLD_WWW_CGI)/propsearch
WWW_BIN_DIR = $(ASCDS_BLD_WWW_BIN)/propsearch

PM_DIR = CDA


HTDOC_FILES = index.html  \
	prop_search.html \
	prop_help.html \
	prop_search.js \
	prop_search.css 

PERLSCRIPTS =  prop_search.cgi \
	prop_details.cgi \
	prop_get_ao.cgi \
	prop_utils.pl

PERLPKG = PropPaperLookup.pm


INSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%)
INSTALL_CGI_FILES = $(PERLSCRIPTS:%=$(CGIBIN_DIR)/%) 
INSTALL_PM_FILES = $(PERLPKG:%=$(CGIBIN_DIR)/CDA/%) 

MAKEALL = $(PERLSCRIPTS) $(PERLPKG)
MAKECLEAN = $(MAKEALL) *.o *.d *.gp *.tcov Makefile.bak Templates.DB


INSTALL_FILES = $(INSTALL_HTDOC_FILES) $(INSTALL_CGI_FILES) $(INSTALL_PM_FILES)

WEBINSTALL_CGI_FILES = $(PERLSCRIPTS:%=$(CGI_DIR)/propsearch/%)  
WEBINSTALL_HTML_FILES = $(HTDOC_FILES:%=$(HTML_DIR)/propsearch/%)
WEBINSTALL_PM_FILES = $(PERLPKG:%=$(CGI_DIR)/propsearch/CDA/%) 

WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_CGI_FILES) $(WEBINSTALL_PM_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww

$(PERLPKG):   $(PERLPKG:%=$(PM_DIR)/%)
	$(REMOVE_F) $@
	$(COPY_P) $(PM_DIR)/$@ $(@D)


# DO NOT DELETE THIS LINE -- make depend depends on it.
