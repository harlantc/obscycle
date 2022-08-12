#   ******************************************************************
#   Copyright (c) 1999, 2000, 2002, 2007 Smithsonian  Astrophysical  Observatory
#   The contents of this file are protected under the copyright notice
#   contained in the file  COPYRIGHT_SAO located in the root directory
#   of this distribution.
#   ******************************************************************

# Makefile for pimms source directory
#
#

MK_TOP  = ../../../../..

include $(MK_TOP)/infra/Makefile.prop

EXECUTABLES     = prop_pimms_exe  prop_xsing  prop_pimms_ahmake
LIB_FILES	= 
SCRIPTS         = 
PAR_FILES       = 
DATA_FILES	= 
PIMMS_DATA	= pimms.ahl
HELP_FILES	= 
QUICKHELP_FILES = 


include $(MK_TOP)/infra/Makefile.all


SITE_OBJS = pimms.o      pms_index.o  pms_intlz.o  pms_rarea.o  pms_slmdl.o \
            pms_specl.o  pms_spec2.o  sis_obstm.o

PIMMS_OBJS = pimms.o        pms_intlz.o    pms_slmdl.o    pms_selct.o \
             pms_docrt.o    pms_outpt.o    pms_rarea.o    pms_specl.o \
             pms_spec2.o    pms_write.o    pms_index.o    pms_afdir.o \
             pms_colmn.o    sis_limit.o    sis_obstm.o    gis_limit.o \
hxd_limit.o  hxt_limit.o    pca_limit.o    ha4_levin.o    sax_limit.o \
             acis_pileup.o  epic_pileup.o  xrs_pileup.o   int_limit.o \
             bat_limit.o    dcd_rrang.o    rd_real.o      simp32.o \
             spec.o         tranmm.o       tranuvo.o      intcom.o \
             getcom.o       set_param.o    get_param.o    force_comm.o \
             ark_help.o     comm_lib.o     sys.o          lentrim.o \
             upcase.o       getlun.o       nustar_limit.o sxs_grades.o \
             sgd_limit.o    nicer_limit.o

XSING_OBJS = xsing.o  sys.o  lentrim.o  upcase.o  getlun.o


FFLAGS		= $(FFLAGS_PIMMS)


prop_pimms_exe:		$(PIMMS_OBJS)
	$(F77) $(PROP_LINK_DYN_OR_STAT) -o $@ $(PIMMS_OBJS)
	rm -f ../$@
	$(LN_S) source/$@ ..

prop_xsing:		$(XSING_OBJS)
	$(F77) $(PROP_LINK_DYN_OR_STAT) -o $@ $(XSING_OBJS)


pimms.ahl:		prop_pimms_ahmake

prop_pimms_ahmake:	ahmake.o  sys.o  lentrim.o  upcase.o  getlun.o \
			pimms.hlp
	$(F77) -o $@ ahmake.o sys.o lentrim.o upcase.o getlun.o
	env LD_LIBRARY_PATH=$(ASCDS_BLD_INSTALL)/ots/lib ./$@
	@-if [ ! -f  ../data/pimms.ahl ]; then \
		cp -p pimms.ahl ../data/.; \
	fi

# (MADESRC should probably be renamed MADESPECIAL (here and in 
#  src/include/Makefile.prop[.in]), since it may include not only 
#  generated sources but also other files that should be removed 
#  by "make clean" but that are not picked up by the definition of MAKECLEAN 
#  in src/include/Makefile.prop[.in])
MADESRC		= pimms.ahl  prop_pimms_ahmake  sys.f

# NOTE: CXCDS IS USING GCC COMPILER FOR ALPHA PORT, NOT NATIVE OSF COMPILER

sys.f:		sun.sys  solaris.sys  ultrix.sys  osf.sys  linux.sys
	@if [ -f $@ ]; \
	then \
		chmod u+w $@; \
	fi
	@if  [ '$(ARCH_PIMMS)' = 'sun' ]; \
	then \
		cp sun.sys	$@; \
	elif [ '$(ARCH_PIMMS)' = 'solaris' ]; \
	then \
		cp solaris.sys	$@; \
	elif [ '$(ARCH_PIMMS)' = 'dec' ]; \
	then \
		cp ultrix.sys	$@; \
	elif [ '$(ARCH_PIMMS)' = 'alpha' ]; \
	then \
		cp linux.sys	$@; \
	elif [ '$(ARCH_PIMMS)' = 'linux' ]; \
	then \
		cp linux.sys	$@; \
	elif [ '$(ARCH_PIMMS)' = 'darwin' ]; \
	then \
		cp linux.sys	$@; \
	elif [ '$(ARCH_PIMMS)' = 'osx' ]; \
	then \
		cp macosx.sys	$@; \
	else \
		echo "Architecture not supported: ARCH_PIMMS=$(ARCH_PIMMS)";  exit 1 ; \
	fi
	@chmod a-w $@


$(SITE_OBJS):	./sitespec.inc

comm_lib.o:	xcomm.inc
force_comm.o:	xcomm.inc
get_param.o:	xcomm.inc
getcom.o:	xcomm.inc
intcom.o:	xcomm.inc
pms_docrt.o:	pimms.inc
pms_outpt.o:	pimms.inc
pms_index.o:	pms_index.inc
pms_intlz.o:	pms_index.inc
pms_selct.o:	pms_index.inc
pms_slmdl.o:	pimms.inc
pms_specl.o:	pms_index.inc
pms_spec2.o:	pms_index.inc
pms_write.o:	pms_index.inc
pms_write.o:	pimms.inc
set_param.o:	xcomm.inc
spec.o:		pimms.inc
