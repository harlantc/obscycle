# makefile for ascds panel background programs 
#
#

MK_TOP = ../../..
include $(MK_TOP)/infra/Makefile.obscycle

EXECUTABLES 	= prop_panel_create $(PERLSCRIPTS)
LIB_FILES 	= 
PERLSCRIPTS 	= prop_panel_assign.pl \
	prop_create_lp.pl \
	prop_assign_lp.pl
SCRIPTS		=
PAR_FILES 	= 


LOCAL_INCLUDES = \
        $(DBINC) \
        $(PROPINCLUDES) \
        $(STLDEFS)

LOCAL_LIBS = \
        $(LIBFRAME) \
        $(DBLIBS) \
        $(PROPLIB) \
        $(PROPPARAMLIB) \
        $(OBSLIBS)

include $(MK_TOP)/infra/Makefile.all


APP_OBJS	= \
	PanelCreateApp.o \
	PanelCreateParameter.o

APP_SRCS	= $(APP_OBJS:.o=.cc)
SRCS		= $(APP_SRCS)


prop_panel_create:	$(APP_OBJS)
	$(LINKCPP) -o $@ $(APP_OBJS) $(LIBS_CMDLINE) 

depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.

