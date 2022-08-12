MK_TOP = ../../../..
include $(MK_TOP)/infra/Makefile.master
include $(MK_TOP)/infra/Makefile.scidev

# AINCDIR is for utconfig.h; LIBDIR is for jcm's lib.
include ../jcm_misc/Makevars${BITS}
LOCAL_INC = -I$(AINCDIR)
TMP_LIBDIR = $(LIBDIR)

EXEC              =
LIB_FILES         = libjcmnutils.a
PAR_FILES         =
INC_FILES         =

# Override definition in Makefile.prop: don't install this library
INSTALL_LIB_FILES = $(LIB_FILES:%=$(TMP_LIBDIR)/%)

# ---------------------------

F90C = array.c bit.c rbits.c search.c

LAYER1 = $(F90C)

UTILS_STRING = cs.c cslib.c cslib2.c cslib3.c csop.c cs2.c char.c   format.c rg.c parse.c 

UTILS_AR = ar.c ar2.c cmath.c  car.c match.c cmatch.c clip.c  vec.c glib.c intersect.c eexp.c bytes.c polar.c circ.c poly.c trap.c func.c

UTILS_SYS = csys.c posix.c nan.c random.c pwd.c  vstring.c putbytes.c

UTILS_TOKEN = token.c read.c readval.c sex.c sexwrite.c cslib5.c fstring.c spline.c  gerror.c esc.c

UTILS_SORT = sortc.c sortd.c sorti.c csortc.c csorti.c csortd.c sorts.c csorts.c sortr.c csortr.c

UTILS_GTOKEN = gtoken.c cslib4.c 

UTILS_LISTS = fnamelist.c mixarray.c alias.c vlist.c gstack.c genlist.c keylist.c namelist.c


# mixarray    Typed pointers into data array
# genlist     list of fixed size void data
# gstack      Simple void* stack
# vlist       list of void pointers   (list type, memsize)

# namelist    list of allocated strings
# fnamelist   Fixed size name list

# alias       list of ( name, namelist ) pairs
# keylist     list of typed name-value pairs


SRCS = $(LAYER1) $(UTILS_STRING) $(UTILS_AR) $(UTILS_SYS) $(UTILS_TOKEN) $(UTILS_SORT)   \
  $(UTILS_GTOKEN) $(UTILS_OPEN) $(UTILS_LISTS)

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
