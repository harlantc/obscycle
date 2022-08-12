# Makefile for the Proposal Receiver/Error GUI
#
#

# Don't change this
MK_TOP  = ../../../..
include $(MK_TOP)/infra/Makefile.obscycle


EXECUTABLES     = prop_rps_gui 
LIB_FILES	=
#SCRIPTS         = prop_rpsdiff  

#PERLSCRIPTS	= prop_list_deleted_props.pl \
#		  prop_list_error_queue.pl \
#		  prop_list_resurrect.pl \
#		  prop_resurrect_upload.pl 

PAR_FILES       = # Add the list of parameter files here
HELP_FILES	= 
QUICKHELP_FILES =



LOCAL_INCLUDES = \
        $(DBINC) \
        $(PROPINCLUDES) \
        -I$(PROPCOMMONINC) \
        $(STLDEFS)

LOCAL_LIBS = \
        $(DBLIBS) \
        $(PROPLIB) \
        $(PROPPARAMLIB) \
        $(PROPCOMMONLIB) \
	$(OBSLIBS)


include $(MK_TOP)/infra/Makefile.all


# All of the .o's for this 
CPP_OBJS = \
	RPSErrApp.o \
	RPSErrDeleteProposal.o \
	RPSErrFindCmd.o \
	RPSErrIngest.o \
	RPSErrMenuWindow.o \
	RPSErrMenus.o \
	RPSErrParameter.o \
	RPSErrPasswordCmd.o \
	RPSErrPopupMenu.o \
	RPSErrPropList.o \
	RPSErrRefreshCmd.o \
	RPSErrSortCmd.o \
	RPSErrViewChecks.o \
	RPSErrViewErrorLog.o \
	RPSErrViewProposal.o 

C_OBJS   = 

CPP_SRCS = $(CPP_OBJS:.o=.cc)
C_SRCS 	 = $(C_OBJS:.o=.c)
SRCS 	 = $(CPP_SRCS) $(C_SRCS)

# the executable that you wish to build
prop_rps_gui:	$(CPP_OBJS)
	$(LINKCPP) -o $@ $(CPP_OBJS) $(LIBS)

depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.
