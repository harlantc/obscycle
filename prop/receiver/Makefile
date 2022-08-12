# makefile for ascds proposal receiver application
#
#

MK_TOP = ../../..
include $(MK_TOP)/infra/Makefile.obscycle
TEMPLATE_DIR = $(ASCDS_BLD_CONFIG_OBS_TEMPLATES)


TEMPLATES	= rps_too.template rps_trigger.template rps_ddt.template
EXECUTABLES 	= prop_rps_receiver $(PERLSCRIPTS) 
LIB_FILES 	= 
PERLSCRIPTS	= prop_kill_rps_receiver.pl \
		  prop_rps_receiver_died.pl \
		  prop_rps_too_mail.pl \
		  prop_rpsinc.pl \
		  prop_start_rps_receiver.pl \
		  prop_start_rps_too_receiver.pl

SCRIPTS 	= monitor_rps_receiver
PAR_FILES 	= 
# don't install test files
INSTALL_TEMPLATE_FILES = $(TEMPLATES:%=$(TEMPLATE_DIR)/%)


INSTALL_FILES = \
	$(INSTALL_BIN_FILES) \
	$(INSTALL_TEMPLATE_FILES)


LOCAL_INCLUDES = \
        $(STLINCS) \
        $(PROPINCLUDES) \
        $(STLDEFS)

LOCAL_LIBS = \
        $(STLLIBS) \
        $(LIBFRAME) \
        $(PROPPARAMLIB) \
        $(OBSLIBS)




include $(MK_TOP)/infra/Makefile.all

APP_OBJS	= \
	RPSReceiverFile.o \
	RPSReceiverApp.o \
	RPSMailTOO.o \
	RPSReceiverParameter.o

APP_SRCS	= $(APP_OBJS:.o=.cc)
SRCS		= $(APP_SRCS)


prop_rps_receiver:	$(APP_OBJS)
	$(LINKCPP) -o $@ $(APP_OBJS) $(LIBS_CMDLINE)

depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.

