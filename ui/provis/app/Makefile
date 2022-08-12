# _INSERT_SAO_COPYRIGHT_HERE_(2008,2009)_
# _INSERT_GPL_LICENSE_HERE_

MK_TOP  = 	../../../..
include         $(MK_TOP)/infra/Makefile.cxc   # Sets ASCDS_ env. variables

WWW_BIN_DIR = $(ASCDS_BLD_WWW_BIN)/provis

PERLSCRIPTS = provis_cleanup.pl \
	 xvfb.pl

OTHER_FILES = provis_gen_graph.tcl  
WEBBIN_FILES = provis_gen_data

NOWRAP_FILES = $(PERLSCRIPTS) $(WEBBIN_FILES)

INSTALL_BIN_FILES = $(NOWRAP_FILES:%=$(BIN_DIR)/%)
INSTALL_WWW_FILES = $(WEBBIN_FILES:%=$(WWW_BIN_DIR)/%) $(OTHER_FILES:%=$(WWW_BIN_DIR)/%)

LOCAL_CFLAGS		= $(STLDEFS) 

ASTRODIR		= $(MK_TOP)/dsguilibs/astro
SCCONVDIR               = $(MK_TOP)/dsguilibs/scconv

LOCAL_LIBS		= $(ASTRODIR)/libastro.a  $(SCCONVDIR)/libscconv.a

LOCAL_INCLUDES		=	-I$(ASTRODIR) -I$(SCCONVDIR)

MAKEALL = $(NOWRAP_FILES) $(WEBBIN_FILES) $(OTHER_FILES)

MAKECLEAN = $(PERLSCRIPTS) provis_gen_data.o provis_gen_data

INSTALL_FILES = $(INSTALL_WWW_FILES) $(INSTALL_BIN_FILES)

WEBINSTALL_BIN_FILES = $(WEBBIN_FILES:%=$(WEB_BIN_DIR)/provis/%) \
	$(OTHER_FILES:%=$(WEB_BIN_DIR)/provis/%)


WEBINSTALL_FILES = $(WEBINSTALL_BIN_FILES) 


include ../../../prop/Makefile.obswww


provis_gen_data:	provis_gen_data.o
		$(RM) $@
		$(LINKCPP) -o $@ provis_gen_data.o  $(LOCAL_LIBS) $(UNIXLIBS)



depend:
	makedepend $(INC) $(SRCS)



