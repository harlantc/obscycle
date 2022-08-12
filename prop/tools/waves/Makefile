MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../nutils -I../jcmcal -I../jcmastro
TMP_LIBDIR = $(LIBDIR)

LIBS = -L$(TMP_LIBDIR) -ljcmcastro -ljcmccal -ljcmnutils

EXEC              = waves
LIB_FILES         = 
PAR_FILES         =

SRCS	= waves.c

OBJS	= $(SRCS:.c=.o)

COMPILE = $(CC) $(CFLAGS) $(ASCDMCFLAGS) -c $*.c

LINK = $(LN) $(LFLAGS) $(OBJS) $(LIBS)   -o $@

MAKETEST_SCRIPT   = 

SLOCPAGE=
SLOCITEM=
SLOCEST=

include $(MK_TOP)/infra/Makefile.all

#-----------------------------------------------------------------------
# 			MAKEFILE DEPENDENCIES	
#-----------------------------------------------------------------------
$(EXEC): $(OBJS) 
	$(LINK)
	@echo

depend:
	makedepend $(INCLUDE) $(SRCS)

announce1:
	@echo "   /----------------------------------------------------------\ "
	@echo "   |              Building ${EXEC} program                    | "
	@echo "   \----------------------------------------------------------/ "

# DO NOT DELETE THIS LINE -- make depend depends on it.
