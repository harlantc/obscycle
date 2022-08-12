MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../jcmutils
TMP_LIBDIR = $(LIBDIR)

EXEC              =
LIB_FILES         = libjcmio.a
PAR_FILES         =
INC_FILES         =

# Override definition in Makefile.prop: don't install this library
INSTALL_LIB_FILES = $(LIB_FILES:%=$(TMP_LIBDIR)/%)

# ---------------------------

SRCS = ver.c tmpname.c sio.c process.c pager.c msg.c jcmstate.c \
       iostack.c iolib.c iofs.c fiopathlist.c fiopath.c \
       fioparpath.c fiomap.c fiolist.c fiolib.c fiodriver.c \
       fiodir.c fiodata.c fio.c fileprops.c filebuffer.c\
       disp.c debug.c coplib.c copenlib.c copenlib2.c copen.c \
       cop.c ciott.c cio.c

OBJS = $(SRCS:.c=.o)

LIB_SRCS = $(SRCS)
LIB_OBJS = $(OBJS)

include $(MK_TOP)/infra/Makefile.all

SLOCPAGE=
SLOCITEM=
SLOCEST=

#-----------------------------------------------------------------------
#                       MAKEFILE DEPENDENCIES
#-----------------------------------------------------------------------
$(LIB_FILES) : $(LIB_OBJS)                            
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
