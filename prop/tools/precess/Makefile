MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../jcmutils -I../jcmio -I../jcmcal \
            -I../unitlib -I../jcmastro -I../precesslib
TMP_LIBDIR = $(LIBDIR)

LIBS  = -L$(TMP_LIBDIR) -lprecess -ljcmcastro \
        -ljcmccal  -ljcmio -ljcmnutils

EXEC              = prop_precess_exe
SCRIPTS           = prop_precess
DOC_FILES         = prop_precess.ps
LIB_FILES         = 
PAR_FILES         =

INSTALL_BIN_FILES = $(EXEC:%=$(BIN_DIR)/%) $(SCRIPTS:%=$(BIN_DIR)/%)

SRCS	= precess.c
OBJS	= $(SRCS:.c=.o)

COMPILE = $(CC) $(CFLAGS) -c $*.c       # $(ASCDMCFLAGS)
LINK = $(LN) $(LFLAGS) $(OBJS) $(LIBS)  -o $@

MAKETEST_SCRIPT   = prop_precess.t

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
# $(DOC_FILES):  precess.tex       # if DOC file does not exist, create it
	# $(LATEX) precess.tex
	# $(DVIPS) -o $@ precess.dvi
	# \rm -f  precess.aux  precess.toc precess.log precess.dvi

depend:
	makedepend $(INCLUDE) $(SRCS)

announce1:
	@echo "   /----------------------------------------------------------\ "
	@echo "   |              Building ${EXEC} program                    | "
	@echo "   \----------------------------------------------------------/ "

# DO NOT DELETE THIS LINE -- make depend depends on it.
