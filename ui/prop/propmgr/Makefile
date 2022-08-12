# Makefile for the Proposal Manager GUI application
#
#

# Don't change this
MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.obscycle

 
EXECUTABLES     = prop_manager_gui
LIB_FILES	=
SCRIPTS         = 
PAR_FILES       = # Add the list of parameter files here
HELP_FILES	= 
QUICKHELP_FILES =


LOCAL_INCLUDES = \
	$(DBINC) \
	$(PROPINCLUDES) \
	-I$(PROPCOMMONINC) \
	$(STLDEFS)

LOCAL_LIBS = \
	$(OBSLIBS) \
	$(DBLIBS) \
	$(PROPLIB) \
	$(PROPPARAMLIB) \
	$(PROPCOMMONLIB)



include $(MK_TOP)/infra/Makefile.all


# All of the .o's for this 
CPP_OBJS = \
	PropMgrApp.o \
	PropMgrFindCmd.o \
	PropMgrLayoutCmd.o \
	PropMgrLoadCmd.o \
	PropMgrLoadFileCmd.o \
	PropMgrLoadSelectCmd.o \
	PropMgrMenuWindow.o \
	PropMgrMenus.o \
	PropMgrParameter.o \
	PropMgrPasswordCmd.o \
	PropMgrPeerCmd.o \
	PropMgrPopupMenu.o \
	PropMgrPrintCmd.o \
	PropMgrPropList.o \
	PropMgrRefreshCmd.o \
	PropMgrRemoveCmd.o \
	PropMgrReviewCmd.o \
	PropMgrReviewsCmd.o \
	PropMgrSelectCmd.o \
	PropMgrSortCmd.o \
	PropMgrSortList.o \
	PropMgrViewCoIs.o \
	PropMgrViewChecks.o \
	PropMgrViewProposal.o

C_OBJS   = 

CPP_SRCS = $(CPP_OBJS:.o=.cc)
C_SRCS 	 = $(C_OBJS:.o=.c)
SRCS 	 = $(CPP_SRCS) $(C_SRCS)

# the executable that you wish to build
prop_manager_gui:	$(CPP_OBJS)
	$(LINKCPP) -o $@ $(CPP_OBJS)  $(LIBS)

depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.
