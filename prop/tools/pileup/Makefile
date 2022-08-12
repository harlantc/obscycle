#   ******************************************************************
#      Copyright (c) 1999   Smithsonian  Astrophysical  Observatory
#   The contents of this file are protected under the copyright notice
#   contained in the file  COPYRIGHT_SAO located in the root directory
#   of this distribution.
#   ******************************************************************

# Makefile for pileup directory, containing ACIS routines
#
#

MK_TOP  = ../../../..

include $(MK_TOP)/infra/Makefile.prop

EXECUTABLES     = prop_pileup_exe prop_frametime_exe
LIB_FILES	=
SCRIPTS         = prop_pileup prop_frametime
PAR_FILES       = 
DATA_FILES	= 
HELP_FILES	=
QUICKHELP_FILES =


include $(MK_TOP)/infra/Makefile.all

prop_pileup_exe:	pileup.o
	$(CC) $(CFLAGS) $(PROP_LINK_DYN_OR_STAT) -o $@ pileup.o -lm -lc

prop_frametime_exe:	frametime.o
	$(CC) $(CFLAGS) $(PROP_LINK_DYN_OR_STAT) -o $@ frametime.o -lm -lc


depend:
	makedepend $(INC) $(C_SRCS)

# DO NOT DELETE THIS LINE -- make depend depends on it.
