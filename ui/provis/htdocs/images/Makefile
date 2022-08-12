# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_
#-----------------------------------------------------------------------
#
#               Makefile for the PRoVis html files
#
#-----------------------------------------------------------------------
MK_TOP = ../../../../../
include $(MK_TOP)/infra/Makefile.master


PROVIS_HTML_DIR   = $(ASCDS_BLD_WWW_HTDOC)/provis/images
PROVIS_WEB    = $(HTML_DIR)/provis/images

# Define local include and library directories


CGI_IMAGES       = axmicro.gif \
	blank.png \
	leftarrow.bmp \
	leftarrow1.bmp \
	maximize.bmp \
	minimize.bmp \
	obs1.gif \
	obs2.gif \
	rightarrow.bmp \
	rightarrow1.bmp \
	sun.gif \
	zoomin.bmp \
	zoomin1.bmp \
	zoomout.bmp \
	zoomout1.bmp \
	zoomreset.bmp \
	zoomreset1.bmp \
	selection.png \
	bar.bmp \
	cross_Black.png \
	cross_Blue.png \
	cross_Cyan.png \
	cross_Green.png \
	cross_Magenta.png \
	cross_Red.png \
	cross_Yellow.png \
	highlimit_Black.png \
	highlimit_Blue.png \
	highlimit_Cyan.png \
	highlimit_Green.png \
	highlimit_Magenta.png \
	highlimit_Red.png \
	highlimit_Yellow.png \
	lowlimit_Black.png \
	lowlimit_Blue.png \
	lowlimit_Cyan.png \
	lowlimit_Green.png \
	lowlimit_Magenta.png \
	lowlimit_Red.png \
	lowlimit_Yellow.png




WEB_FILES        = $(CGI_IMAGES)


INSTALL_HTML_FILES = $(WEB_FILES:%=$(PROVIS_HTML_DIR)/%)
INSTALL_FILES = $(INSTALL_HTML_FILES)


WEBINSTALL_HTML_FILES  = $(WEB_FILES:%=$(PROVIS_WEB)/%)
WEBINSTALL_FILES = $(WEBINSTALL_HTML_FILES)

include $(MK_TOP)/obscycle/prop/Makefile.obswww


announce1:
	@echo " /----------------------------------------------------------\ "
	@echo " |                 Installing PRoVis images                | "
	@echo " \----------------------------------------------------------/ "


# DO NOT DELETE THIS LINE -- make depend depends on it.

