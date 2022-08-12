# Makefile for obs/ui/op (User Interface for Obs Processing Status) 
#
MK_TOP  = ../../..
include $(MK_TOP)/infra/Makefile.cxc

OTHER_CONFIG_DIR = ../cxc_include

HTDOC_DIR = $(ASCDS_BLD_WWW_HTDOC)/op
CGIBIN_DIR = $(ASCDS_BLD_WWW_CGI)/op
WWW_BIN_DIR = $(ASCDS_BLD_WWW_BIN)/op

SCRIPTS          = op_reports.pl  

# have copy because OP also installs on icxc
OTHER_FILES = cxcds_utils.pl

WEBBIN_FILES = op_archive.tcsh


HTDOC_FILES = index.html op_header.html op_topheader.html op.css \
            op_pst.html op_olist.html op_status_table.html \
            op_custom_table.html op_ghelp.html op_help.html op_issue.html \
            op_shelp.html op_phelp.html op_dhelp.html op_lhelp.html \
            op_ilist.html op_footer.html \
	    op_pst_icxc.html \
            op_ball.gif op_pst_statuslist.inc op_pst_statuslist_icxc.inc

PERLSCRIPTS = op_custom_table.cgi \
	    op_detailed_history.cgi \
	    op_display_issue.cgi \
	    op_issue.cgi \
            op_process_history.cgi \
	    op_reports.pl \
	    op_status_issue.cgi \
            op_status_list.cgi \
	    op_status_obsid.cgi \
	    op_status_routines.pl \
	    op_status_table.cgi \
	    op_status_table_icxc.cgi \
	    op_subroutines.pl \
	    op_vvreport.cgi 

CGI_ICXC = \
	op_custom_table.cgi \
	op_status_list.cgi \
	op_status_table_icxc.cgi \
	op_issue.cgi

CGI_FILES = \
	    op_detailed_history.cgi \
	    op_display_issue.cgi \
            op_process_history.cgi \
	    op_status_issue.cgi \
	    op_status_obsid.cgi \
	    op_status_routines.pl \
	    op_status_table.cgi \
	    op_subroutines.pl \
	    op_vvreport.cgi \
	    op_config.pl \
	    op_issue_routines.pl op_status_columns.inc 


INSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%)
INSTALL_CGI_FILES = $(CGI_FILES:%=$(CGIBIN_DIR)/%) $(OTHER_FILES:%=$(CGIBIN_DIR)/%) $(CGI_ICXC:%=$(CGIBIN_DIR)/%)
INSTALL_BIN_FILES = $(SCRIPTS:%=$(BIN_DIR)/%) 
INSTALL_WWW_FILES = $(WEBBIN_FILES:%=$(WWW_BIN_DIR)/%)


MAKEALL = $(PERLSCRIPTS)  $(OTHER_FILES)
MAKECLEAN = $(MAKEALL) *.o *.d *.gp *.tcov Makefile.bak Templates.DB


INSTALL_FILES = $(INSTALL_BIN_FILES) $(INSTALL_HTDOC_FILES) \
	$(INSTALL_CGI_FILES)	$(INSTALL_WWW_FILES) 

WEBINSTALL_CGI_FILES = $(CGI_FILES:%=$(CGI_DIR)/op/%) $(OTHER_FILES:%=$(CGI_DIR)/op/%)
WEBINSTALL_HTML_FILES = $(HTDOC_FILES:%=$(HTML_DIR)/op/%)
WEBINSTALL_BIN_FILES = $(WEBBIN_FILES:%=$(WEB_BIN_DIR)/op/%)

WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_CGI_FILES) \
	$(WEBINSTALL_BIN_FILES) 


include ../../prop/Makefile.obswww

$(OTHER_FILES): $(OTHER_FILES:%=$(OTHER_CONFIG_DIR)/%)
	$(REMOVE_F) $@
	$(COPY_P)  $(OTHER_CONFIG_DIR)/$@ $(@D)



# DO NOT DELETE THIS LINE -- make depend depends on it.
