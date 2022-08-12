MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../jcmutils -I../jcmio -I../jcmcal -I../calconv
TMP_LIBDIR = $(LIBDIR)

LIBS = -L$(TMP_LIBDIR) -lcalconv -ljcmccal -ljcmio -ljcmnutils

EXEC              = prop_dates_exe
SCRIPTS           = prop_dates
DOC_FILES         = prop_dates.ps
LIB_FILES         = 
PAR_FILES         = 

INSTALL_BIN_FILES = $(EXEC:%=$(BIN_DIR)/%) $(SCRIPTS:%=$(BIN_DIR)/%)

SRCS	= dates.c
OBJS	= $(SRCS:.c=.o)

COMPILE = $(CC) $(CFLAGS) -c $*.c
LINK = $(LN) $(LFLAGS) $(OBJS) $(LIBS) -o $@

MAKETEST_SCRIPT   = prop_dates.t

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

INSTALL_DOC_FILES = $(DOC_FILES:%=$(DOC_DIR)/%)
# $(DOC_FILES):  dates.tex        #if DOC file does not exist, create it
	# $(LATEX)  dates.tex
	# $(DVIPS) -o $@ dates.dvi
	# \rm -f  dates.aux  dates.toc dates.log dates.dvi

depend:
	makedepend $(INCLUDE) $(SRCS)

announce1:
	@echo "   /----------------------------------------------------------\ "
	@echo "   |              Building ${EXEC} program                    | "
	@echo "   \----------------------------------------------------------/ "

# DO NOT DELETE THIS LINE -- make depend depends on it.
