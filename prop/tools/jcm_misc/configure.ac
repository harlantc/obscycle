# (5/2008) - initial version
#   - the orignal code came from JCM (version of Dec.2007) 
#   - modified to remove cfitsio, one fortran filename from 'filelist',
#     'f77' variable, 'fflags' variable and the whole section of 
#     "Fortran specific configs";
#   - keep "C specific configs" even we don't use it for now.
#
# (5/2011) - jcm205a
#
AC_INIT(INIT)

AC_CHECK_SIZEOF(char)
AC_SUBST(ac_cv_sizeof_char)
AC_CHECK_SIZEOF(int)
AC_SUBST(ac_cv_sizeof_int)
AC_CHECK_SIZEOF(short)
AC_SUBST(ac_cv_sizeof_short)
AC_CHECK_SIZEOF(long)
AC_SUBST(ac_cv_sizeof_long)
AC_CHECK_SIZEOF(double)
AC_SUBST(ac_cv_sizeof_double)
AC_CHECK_SIZEOF(float)
AC_SUBST(ac_cv_sizeof_float)
AC_CHECK_SIZEOF(char *)
AC_SUBST(ac_cv_sizeof_char_p)
AC_CHECK_TYPE(u_int64_t)
AC_CHECK_TYPE(uint64_t)
AC_CHECK_TYPE(u_int32_t)
AC_CHECK_TYPE(uint32_t)
AC_CHECK_SIZEOF(unsigned int)
AC_CHECK_SIZEOF(unsigned long)
AC_ARG_WITH(
 m32,
 [ --with-m32  Build 32-bit on 64-bit machine],
 [ if test "x$withval" != "xno"; then
    have_m32 = yes;
   fi ]
)

if test $ac_cv_sizeof_long -eq 8; then
  ut_sz_long=4
  ut_sz_p=4
  memcflags=""
  memcflags32="-m32"
  libsuffix="64" 
  aincsuffix="64"
  optsuffix="32"
else
  ut_sz_long=$ac_cv_sizeof_long
  ut_sz_p=$ac_cv_sizeof_char_p
  memcflags=""
  libsuffix=""
  aincsuffix="32"
  optsuffix="32"
fi


AC_SUBST(ut_sz_long)
AC_SUBST(ut_sz_p)
AC_SUBST(libsuffix)
AC_SUBST(memcflags)
AC_SUBST(memcflags32)
AC_SUBST(optsuffix)
AC_SUBST(aincsuffix)
if test $ac_cv_have_u_int_32_t; then
 fourbyte="u_int32_t";
elif test $ac_cv_have_uint32_t; then
 fourbyte="uint32_t";
elif test $ac_cv_sizeof_unsigned_long -eq 4; then
 fourbyte="unsigned long" 
elif test $ac_cv_sizeof_unsigned_int -eq 4; then
 fourbyte="unsigned int"
else
 fourbyte="unsigned int"
fi
AC_SUBST(fourbyte)
AC_C_BIGENDIAN
if test "$ac_cv_c_bigendian" = "yes"; then
 bigendian=1
else
 bigendian=0
fi
AC_SUBST(bigendian)

AC_CANONICAL_BUILD
AC_CANONICAL_HOST
AC_CANONICAL_TARGET
AC_PROG_LEX
AC_PROG_YACC
AC_PATH_XTRA

if  test "$X_LIBS" = ""; then
AC_CHECK_FILE(/usr/shlib/libX11.so, X_LIBS=-L/usr/shlib)
AC_CHECK_FILE(/usr/include/X11, X_CFLAGS=-I/usr/include/X11)
fi

if  test "$X_LIBS" = ""; then
AC_CHECK_FILE(/opt/X11/lib, X_LIBS=-L/opt/X11/lib)
AC_CHECK_FILE(/opt/X11/include, X_CFLAGS=-I/opt/X11/include)
fi
echo XLIBS = $X_LIBS

top=`pwd`
AC_SUBST(top)


dnl
dnl C specific configs
dnl

gnuflags="-Wall -W -pedantic -Winline -Wmissing-prototypes -Wnested-externs -Wpointer-arith -Wcast-align -Wstrict-prototypes"  

AC_SUBST(cc)
AC_SUBST(cflags)           
AC_SUBST(systype)
AC_SUBST(syslibs)
AC_SUBST(uname)
uname=`uname -s`-`uname -r`-`uname -p`

case "$target" in 
*solaris*    )
               systype="sparc"
               cc="cc"
               syslibs="-lsocket -lnsl -ldl -lelf"
               cflags=" ";;
*sun*        )
               systype="sun"
               cc="cc"
               syslibs=" "
               cflags=" ";;
*ibm-aix*     )
               systype="aix"
               cc="cc"
               syslibs=" "
               cflags=" ";;
mips-sgi-irix*)
               systype="irix"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
alpha*-dec-osf*)
               systype="osf"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
powerpc-apple-darwin* )
               systype="macppc"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
*86-apple-darwin* )
               systype="macintel"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
Darwin-*-i386  )
               systype="macintel"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
*-pc-linux-* )
               systype="linux"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
* )         
               systype="linux"
               cc="gcc"
               syslibs=" "
               cflags=$gnuflags;;
esac
if test $ac_cv_sizeof_long -eq 8; then               
filelist="Makevars Makevars32 inc64/utconfig.h inc32/utconfig.h "
else
filelist="Makevars inc32/utconfig.h"
fi
echo FILELIST=$filelist
AC_CONFIG_FILES($filelist)
AC_OUTPUT
