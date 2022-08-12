# Makefile for generic proposal tool (ie. EA and PSF) File Viewer
#
#
CP      = cp
MK_TOP  = ../../..
PROP_DIR = ..

include $(MK_TOP)/infra/Makefile.master       # Sets ASCDS_BLD_ env. variables

HTDOC_DIR = $(ASCDS_BLD_WWW_HTDOC)/prop_viewer
CGIBIN_DIR = $(ASCDS_BLD_WWW_CGI)/prop_viewer
WEBADM_DIR = $(ASCDS_WEBSRV_ROOT)/webadmin


HTDOC_FILES = prop_ea_header.html prop_psf_header.html \
	prop_viewer.css prop_viewer_footer.html index.html

PERLSCRIPTS = prop_viewer_create_menu.cgi \
	    prop_viewer_create_plot.cgi \
	    build_viewer.cgi \
	    prop_viewer_create_table.cgi \
	    prop_viewer_create_title.cgi \
	    prop_ea_menu.cgi \
	    prop_psf_menu.cgi \
	    prop_viewer_file.cgi 



CGI_FILES = prop_viewer_create_menu.cgi \
	    prop_viewer_create_plot.cgi \
	    build_viewer.cgi \
	    prop_viewer_create_table.cgi \
	    prop_ea_ao.pl \
	    prop_viewer_create_title.cgi \
	    prop_ea_menu.cgi \
	    prop_viewer_file.cgi \
	    prop_psf_menu.cgi

#DEPLOYMENT_SCRIPT = deploy_prop_viewer.pl


INSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%)
INSTALL_CGI_FILES = $(CGI_FILES:%=$(CGIBIN_DIR)/%)
WEBINSTALL_CGI_FILES = $(CGI_FILES:%=$(CGI_DIR)/prop_viewer/%) 
WEBINSTALL_HTML_FILES = $(HTDOC_FILES:%=$(HTML_DIR)/prop_viewer/%)


MAKEALL = $(PERLSCRIPTS)
MAKECLEAN = $(MAKEALL) *.o *.d *.gp *.tcov Makefile.bak Templates.DB


#INSTALL_BIN_FILES = $(DEPLOYMENT_SCRIPT:%=$(WEBADM_DIR)/%)
INSTALL_FILES = $(INSTALL_CGI_FILES) $(INSTALL_HTDOC_FILES)
WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_CGI_FILES) \
        $(WEBINSTALL_BIN_FILES)



include $(PROP_DIR)/Makefile.obswww



depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.


