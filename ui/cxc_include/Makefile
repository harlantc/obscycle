# Makefile for obs/ui/op (User Interface for Obs Processing Status) 
#
MK_TOP  = ../../..
include $(MK_TOP)/infra/Makefile.cxc

MYMKDIR  = mkdir -p
MYCOPY_R = cp -rp

HTDOC_DIR = $(ASCDS_BLD_WWW_HTDOC)/include
CGIBIN_DIR = $(ASCDS_BLD_WWW_CGI)/

SRC_CONFIG_DIR = $(ASCDS_BLD_INSTALL)

PERLSCRIPTS = \
	cxcds_utils.pl

HTFILE = .htaccess
IND_FILE = index.html

OTSINC_FILES = \
   $(DSOTS)/jquery-3.6.0/js/jquery.min.js \
   $(DSOTS)/jquery-ui-1.13.0/js/jquery-ui.js \
   $(DSOTS)/jquery-ui-1.13.0/js/jquery-ui.css \
   $(DSOTS)/Mottie-tablesorter-08bf513/js/jquery.tablesorter.widgets.min.js \
   $(DSOTS)/Mottie-tablesorter-08bf513/js/jquery.tablesorter.min.js \
   $(DSOTS)/Mottie-tablesorter-08bf513/js/parsers/ \
   $(DSOTS)/Mottie-tablesorter-08bf513/css/images/white-asc.gif \
   $(DSOTS)/Mottie-tablesorter-08bf513/css/images/white-desc.gif \
   $(DSOTS)/Mottie-tablesorter-08bf513/css/images/white-unsorted.gif


HTDOC_FILES = .htaccess \
            index.html \
            cxcds.css \
            blank.gif \
            header_left.gif \
            galactic.jpg \
            footer.jpg \
            bg.jpg \
            nasa.png \
            smithsonian.png \
            cxcds_meta.html \
            cxcheaderlogo.png \
            cxcfooterCXCicon.png \
            cxcfooterbackground.png \
            cxcheader.html \
            cxcfooter.html \
            cxcfooterj.html \
            CfA_Logo.png \
            rcw108_800.jpg 

#           si.png 
#           header_right.gif 
#           header_left_sm.gif 
#            cxcdsplain.css 
#            cxcfooterplain.html 
#            cxcfooterplainj.html 
VERSION_CONFIG = VERSION


INSTALL_HTDOC_FILES = $(HTDOC_FILES:%=$(HTDOC_DIR)/%)
INSTALL_CGI_FILES = $(PERLSCRIPTS:%=$(CGIBIN_DIR)/%) $(HTFILE:%=$(CGIBIN_DIR)/%)




MAKEALL = $(PERLSCRIPTS) $(VERSION_CONFIG)  

INSTALL_FILES = $(INSTALL_HTDOC_FILES)  $(INSTALL_CGI_FILES)  OTSINC


WEBINSTALL_CGI_FILES = $(PERLSCRIPTS:%=$(CGI_DIR)/%) $(HTFILE:%=$(CGI_DIR)/%)
 
WEBINSTALL_HTML_FILES = $(HTDOC_FILES:%=$(HTML_DIR)/include/%) $(VERSION_CONFIG:%=$(HTML_DIR)/include/%) $(IND_FILE:%=$(HTML_DIR)/%)   



WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES) $(WEBINSTALL_CGI_FILES) OTSINCWWW 


include ../../prop/Makefile.obswww

$(VERSION_CONFIG): $(VERSION_CONFIG:%=$(SRC_CONFIG_DIR)/%)
	$(REMOVE_F) $@ 
	$(COPY_P)  $(SRC_CONFIG_DIR)/$@ $(@D) 

OTSINCWWW:  #$(REMOVE_RF) $(HTML_DIR)/include/jquery
	$(MYMKDIR) $(HTML_DIR)/include/jquery
	$(MYCOPY_R)  $(OTSINC_FILES) $(HTML_DIR)/include/jquery/.

OTSINC: #$(REMOVE_RF) $(HTDOC_DIR)/jquery
	$(MYMKDIR) $(HTDOC_DIR)/jquery
	$(MYCOPY_R)  $(OTSINC_FILES) $(HTDOC_DIR)/jquery/


# DO NOT DELETE THIS LINE -- make depend depends on it.
