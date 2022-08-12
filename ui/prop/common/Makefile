# Makefile for the Proposal Planning Common GUI library
#
#

# Don't change this
MK_TOP  = ../../../..
include $(MK_TOP)/infra/Makefile.obscycle

XRESOURCE_DIR = $(ASCDS_BLD_CONFIG_XRESOURCES)

 
EXECUTABLES     = 
LIB_FILES	= libPropGui.so
SCRIPTS         = # Add the list of scripts here
PAR_FILES       = # Add the list of parameter files here
HELP_FILES	= prop.helpmsg
#QUICKHELP_FILES = prop.quickhelp prop.statusmsg prop.errormsg
XRESOURCE_FILES = ProposalPlanning


INSTALL_XRESOURCE_FILES = $(XRESOURCE_FILES:%=$(XRESOURCE_DIR)/%)
WEBINSTALL_HELP_FILES = $(HELP_FILES:%=$(HELP_DIR)/%)


# Add what ever C++ libraries you will need here
LOCAL_INCLUDES   =  \
	$(DBINC) \
	$(PROPINCLUDES) \
	$(STLDEFS)

INSTALL_FILES = $(INSTALL_BIN_FILES) $(INSTALL_PAR_FILES) \
                $(INSTALL_LIB_FILES) \
                $(INSTALL_XRESOURCE_FILES) \
		$(INSTALL_QUICKHELP_FILES)


include $(MK_TOP)/infra/Makefile.all

install: $(install) $(WEBINSTALL_HELP_FILES)

# All of the .o's for this 
CPP_OBJS = \
	PrintCmd.o \
	PropPrintBrowse.o \
	PropHelpMsg.o \
	PropHelpSelect.o \
	PropPrintFile.o \
	PropViewCoIs.o \
	PropViewResults.o \
	PropCoIList.o

C_OBJS   = 

CPP_SRCS = $(CPP_OBJS:.o=.cc)
C_SRCS 	 = $(C_OBJS:.o=.c)
SRCS 	 = $(CPP_SRCS) $(C_SRCS)

# the executable that you wish to build
libPropGui.so :	$(CPP_OBJS)
	$(LINKCPP.SO) -o $@ $(CPP_OBJS)


$(WEBINSTALL_HELP_FILES) : $(@F)
	rm -f $@
	@if  [ "$(MYDOCDIR)" ] ; then \
	cat $(@F) | sed 's@/home/ascds/DS.release/doc@'$(MYDOCDIR)'@' \
	> $(MYDOCDIR)/$(@F) ; \
	ls $(MYDOCDIR)/$(@F); \
	cp $(MYDOCDIR)/$(@F) $@; \
	else   \
	cp $(@F) $@; \
	fi
	ls $@
	chmod a-w $@


depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.
