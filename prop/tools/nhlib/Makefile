MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../jcmutils -I../jcmio -I../jcmfits -I../jcmcal -I../unitlib -I../jcmastro -I../precesslib $(FITSIO_INC)
TMP_LIBDIR = $(LIBDIR)

EXEC              =
LIB_FILES         = libnh.a       
PAR_FILES         =
INC_FILES         =  # nhlib.h

# Override definition in Makefile.prop: don't install this library
INSTALL_LIB_FILES = $(LIB_FILES:%=$(TMP_LIBDIR)/%)

SRCS    = nh.c bell.c nrao.c nhlib.c
OBJS    = $(SRCS:.c=.o)

LIB_SRCS = $(SRCS)
LIB_OBJS = $(OBJS)

include $(MK_TOP)/infra/Makefile.all

SLOCPAGE=
SLOCITEM=
SLOCEST=

#-----------------------------------------------------------------------
#                       MAKEFILE DEPENDENCIES
#-----------------------------------------------------------------------
$(LIB_FILES): $(LIB_OBJS)                            
	$(AR) $@ $(LIB_OBJS)
	cp -fp  $@  $(TMP_LIBDIR)/.
	$(RANLIB) $@
	@echo

depend:
	makedepend $(INCLUDE) $(SRCS)

announce1:
	@echo "  /----------------------------------------------------------\ "
	@echo "  |                  Building $(LIB_FILES)    | "
	@echo "  \----------------------------------------------------------/ "

# DO NOT DELETE THIS LINE -- make depend depends on it.
