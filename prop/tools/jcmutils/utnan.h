/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <math.h>


#define UT_NULL_S      0x8000
#define UT_INF_S       0x8001
#define UT_NINF_S      0x8002

#define UT_NULL_I      0x7FFFFFFF
#define UT_INF_I       0x7F800000
#define UT_NINF_I      0xFF800000

#define UT_NAN_R_MASK  0x7F800000
#define UT_NAN_D_MASK  0x7FF00000
#define UT_INF_D_MASK  0x7FF00000
#define UT_NINF_D_MASK 0xFFF00000

#define UT_NAN_R_CXC   0x7FC00000
#define UT_NAN_D_CXC   0x7FF80000

#define UT_NAN_R       0xFFC00000
#define UT_NAN_D       0xFFF80000

#define UT_NAN_R_OLD   0x77FFFFFF
#define UT_INF_R_OLD   0x7EFFFFFF
#define UT_NINF_R_OLD  0xFEFFFFFF

#define UT_NAN_D_OLD   0x77FFFFFFFFFFFFFF
#define UT_INF_D_OLD   0x7EFFFFFFFFFFFFFF
#define UT_NINF_D_OLD  0xFEFFFFFFFFFFFFFF

#define UT_INF_R       0x7F800000
#define UT_NINF_R      0xFF800000

#define UT_INF_D       0x7FF00000
#define UT_NINF_D      0xFFF00000

float utn_nan_create_r( UT_INT32T pattern );
double utn_nan_create_d( UT_INT32T pattern );

/* Code for POSIX (when isnan(x) not available) */

#if defined(__alpha) && defined(__unix__)
#include <nan.h>
/* test for double NaN */
#define dmt_dNAN(X) IsNANorINF(X)
#else

#if defined(__sparc) || defined(sparc)
/* Extract value of double high order word and cast to unsigned long */
/* 32 bit mask to catch double NaN or Inf */

#define DNANCAST(M) ( (unsigned long )(*(unsigned long*)&(M)) )
#else
/************************************************************
  Linux definitions
************************************************************/

#if defined(__APPLE__)
#include <machine/endian.h>
#else
#include <endian.h>
#endif

#if UT_SZ_LONG == 8
#define N_UINT4 unsigned int
#else
#define N_UINT4 unsigned long
#endif

#if BYTE_ORDER == LITTLE_ENDIAN
#define DNANCAST(M) ( (N_UINT4 )(*(((N_UINT4*)&(M))+1)) )
#else
#define DNANCAST(M) ( (N_UINT4 )(*(((N_UINT4*)&(M))  )) )
#endif

#endif

/* Test for double NaN */
#define dmt_dNAN(L) \
     (L == 0.0 ?  0 : \
      ( ( DNANCAST(L) & UT_NAN_D_MASK) == UT_NAN_D_MASK ?  1 : 0))
#endif


