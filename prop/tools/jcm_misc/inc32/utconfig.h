/*
 *  The file Makevars.in determines basic system properties using
 *  autoconf 2.52 or later and the configure.ac script in this directory.
 *  Configure it to make the system-specific file 'Makevars'.
 *  1) Create configure.ac
 *  2) Run autoconf to create ./configure
 *      >  autoconf
 *     Autoconf requires the presence of the INIT file.
 *  3) Run ./configure 
 *      >  ./configure
 *     This uses config.guess, config.sub and install-sh  
 *     It generates config.log and config.status (which can both be ignored)
 *     and it generates Makevars, which this file is the template for.
 *  (c) Jonathan McDowell 2001
 *  
 */

/*
 *  System architecture
 */

#ifndef UT_ARCH
#define UT_ARCH    "linux"
#define UT_SYSTEM  "x86_64-pc-linux-gnu"
#define UT_UNAME   "Linux-4.18.0-305.10.2.el8_4.x86_64-x86_64"


#define UT_SOURCE_ROOT "/vobs/ASC_BUILD/src/obs/prop/tools/jcm_misc"
#endif

#ifndef UT_SZ_SHORT
/* Numerical architecture */

#define UT_SZ_SHORT  2
#define UT_SZ_DOUBLE 8
#define UT_SZ_FLOAT  4
#define UT_SZ_INT    4
#define UT_SZ_CHAR   1

/* ./configure --with-opt32  */
/* Special hack to allow Opteron compilation with -m32 -DCOMPAT32 */
#define UT_SZ_LONG   4
#define UT_SZ_P      4
#endif

#define UT_BIGEND  0
#define UT_INT32T  unsigned int
#define integer4 int
