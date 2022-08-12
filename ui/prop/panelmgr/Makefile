# Makefile for the Proposal Panel Manager GUI application
#
#

# Don't change this
MK_TOP  = ../../../..
include $(MK_TOP)/infra/Makefile.obscycle
 
EXECUTABLES     = prop_panel_gui
LIB_FILES	=
SCRIPTS         = # Add the list of scripts here
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
	PanelAllMembers.o \
	PanelApp.o \
	PanelAssignMember.o \
	PanelAssignProposal.o \
	PanelAssignReviewer.o \
	PanelClumpCmd.o \
	PanelFindCmd.o \
	PanelFindProp.o \
	PanelLogCmd.o \
	PanelLogMsgCmd.o \
	PanelMemberList.o \
	PanelMenuWindow.o \
	PanelMenus.o \
	PanelMoveProp.o \
	PanelPanelList.o \
	PanelParameter.o \
	PanelPasswordCmd.o \
	PanelPeerList.o \
	PanelPopupMenu.o \
	PanelPrintCmd.o \
	PanelPropList.o \
	PanelPropSelectCmd.o \
	PanelPropMgrCmd.o \
	PanelProposalList.o \
	PanelRefreshCmd.o \
	PanelRemoveMember.o \
	PanelRemoveProposal.o \
	PanelSelectLinked.o \
	PanelSortCmd.o \
	PanelSortList.o \
	PanelUserList.o \
	PanelUtils.o \
	PanelViewConflicts.o \
	PanelViewChecks.o \
	PanelViewCoIs.o \
	PanelViewDetectors.o \
	PanelViewLog.o \
	PanelViewMember.o \
	PanelViewProposal.o

C_OBJS   = 

CPP_SRCS = $(CPP_OBJS:.o=.cc)
C_SRCS 	 = $(C_OBJS:.o=.c)
SRCS 	 = $(CPP_SRCS) $(C_SRCS)

# the executable that you wish to build
prop_panel_gui:	$(CPP_OBJS)
	$(LINKCPP) -o $@ $(CPP_OBJS) $(LIBS)

depend:
	makedepend $(INC) $(SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.
