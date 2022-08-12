# 1. data files below are from JCM release directory '<release-root>/libdata/'
# 2. 'make install' - copy data files to /vobs/ASC_BUILD/config/jcm_data/
 
MK_TOP  = ../../../..

include $(MK_TOP)/infra/Makefile.prop

EXECUTABLES     = 
LIB_FILES       =
SCRIPTS         =
PAR_FILES       =
DATA_FILES      =
DOC_FILES       = 
HELP_FILES      =
QUICKHELP_FILES =
COLDEN_DATA     = bell.dat bell.idx nrao.fits
PRECESS_DATA    = Constellations Constellation.names Constellation.parallels
DATES_DATA      = Time_Zones COS ut1

install:	$(COLDEN_DATA) $(PRECESS_DATA) $(DATES_DATA)

include $(MK_TOP)/infra/Makefile.all
