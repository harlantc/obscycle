/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
integer utn_ar_sum_i( 
 const integer* a,	/* (i) Array */
 const integer m 		/* (i) Array size */
 )
{
 integer n = m;

 integer sum = 0;
 while( n-- ) sum += *a++;
 return( sum );
}

short utn_ar_sum_s( 
 const short* a,	/* (i) Array */
 const integer m 		/* (i) Array size */
 )
{
 integer n = m;

 short sum = 0;
 while( n-- ) sum += *a++;
 return( sum );
}


real utn_ar_sum_r( 
 const real* a,	/* (i) Array */
 const integer m 		/* (i) Array size */
 )
{
 integer n = m;

 real sum = 0;
 while( n-- ) sum += *a++;
 return( sum );
}

double utn_ar_sum_d( 
 const double* a,	/* (i) Array */
 const integer m 		/* (i) Array size */
 )
{
 integer n = m;

 double sum = 0;
 while( n-- ) sum += *a++;
 return( sum );
}

