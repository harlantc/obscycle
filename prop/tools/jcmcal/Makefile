MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../jcmutils -I../jcmio
TMP_LIBDIR = $(LIBDIR)

EXEC              =
LIB_FILES         = libjcmccal.a
PAR_FILES         =
INC_FILES         =

# Override definition in Makefile.prop: don't install this library
INSTALL_LIB_FILES = $(LIB_FILES:%=$(TMP_LIBDIR)/%)

# ---------------------------
SRCS = auc.c cal2.c cal.c calfmt.c calframe.c \
       calpack.c calwrap.c cframe.c daysec.c \
       doy.c edouble.c epoch.c gdate.c gst.c \
       gtime.c hms.c leap1.c leap3.c lt.c \
       os.c precision.c sexlib.c tdb.c \
       thermidor.c tszone.c weekday.c maya.c


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
