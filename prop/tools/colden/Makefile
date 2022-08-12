MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR) -I../jcmutils -I../jcmio -I../jcmfits -I../jcmcal \
            -I../unitlib -I../jcmastro -I../precesslib -I../nhlib  $(FITSIO_INC)
TMP_LIBDIR = $(LIBDIR)

LIBS  = -L$(TMP_LIBDIR) -lnh -lprecess -ljcmcastro -lunit -ljcmccal \
        -ljcmcfits -ljcmio -ljcmnutils  $(CFITSIOLIBS) $(UNIXLIBS)

EXEC              = prop_colden_exe
SCRIPTS           = prop_colden
DOC_FILES         = prop_colden.ps
LIB_FILES         = 
PAR_FILES         =

INSTALL_BIN_FILES = $(EXEC:%=$(BIN_DIR)/%) $(SCRIPTS:%=$(BIN_DIR)/%)

SRCS	= colden.c
OBJS	= $(SRCS:.c=.o)

COMPILE = $(CC) $(CFLAGS) -c $*.c
LINK = $(LN) $(LFLAGS) $(OBJS) $(LIBS)   -o $@

MAKETEST_SCRIPT   = prop_colden.t

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
# $(DOC_FILES):    colden.tex     # if DOC file does not exist, create it. 
	# -$(LATEX) colden.tex
	# -$(DVIPS) -o $@ colden.dvi
	# -\rm -f  colden.aux  colden.toc colden.log colden.dvi

depend:
	makedepend $(INCLUDE) $(SRCS)

announce1:
	@echo "   /----------------------------------------------------------\ "
	@echo "   |              Building ${EXEC} program                    | "
	@echo "   \----------------------------------------------------------/ "

# DO NOT DELETE THIS LINE -- make depend depends on it.
