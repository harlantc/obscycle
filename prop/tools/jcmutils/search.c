/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/* Bisection and search routines */

/* Fortran convention */

/*
 *  x0 < x(1)     Return 1
 *  x0 > x(n)     Return n
 *  x0 = x(m)-e   Return m-1
 *  x0 = x(m)     Return m
 *  x0 = x(m)+e   Return m
 *  so  x(m) <= x0 < x(m+1)
 */

long utn_ar_bisect_d( const double* x, const long n, const double x0 )
{
 /* Simple bisection search to find position of x0 in array x */
 long element;
 long offset;
 long ju, jl, jm;
 if ( x0 <= x[0] ) {
  element = 1;
 } else if ( x0 >= x[n-1] ) {
  element = n;
 } else {
  jl = 0;
  ju = n+1;
  while( (ju-jl) > 1 ) {
   jm = (ju+jl)/2;
   if ( x0 > x[jm-1] ) {
    jl = jm;
   } else {
    ju = jm;
   }
  }
  element = jl;   /* ie (jl-1) + 1 */
  offset = element - 1;
  /* We know 
   *    x0 in  [x(element),x(element+1)]
   * ie    x[offset] and x[offset+1]
   * We want to return 
   *    x0 in [x(element),x(element+1))
   */
  if( x[offset + 1] == x0 ) element++;
 }
 return( element );
}

long utn_ar_cbisect_d( const double* x, const long n, const double x0 )
{
 return( utn_ar_bisect_d( x, n, x0 ) - 1 );
}

long utn_ar_bisect_r( const float* x, const long n, const float x0 )
{
 long j, ju, jl, jm;
 if ( x0 <= x[0] ) {
  j = 1;
 } else if ( x0 >= x[n-1] ) {
  j = n;
 } else {
  jl = 0;
  ju = n+1;
  while( (ju-jl) > 1 ) {
   jm = (ju+jl)/2;
   if ( x0 > x[jm-1] ) {
    jl = jm;
   } else {
    ju = jm;
   }
  }
  j = jl;
  if( x[jl] == x0 ) j++;
 }
 return( j );
}

long utn_ar_cbisect_r( const float* x, const long n, const float x0 )
{
 return( utn_ar_bisect_r( x, n, x0 ) - 1 );
}


long utn_ar_bisect_s( const short* x, const long n, const short x0 )
{
 long j, ju, jl, jm;
 if ( x0 <= x[0] ) {
  j = 1;
 } else if ( x0 >= x[n-1] ) {
  j = n;
 } else {
  jl = 0;
  ju = n+1;
  while( (ju-jl) > 1 ) {
   jm = (ju+jl)/2;
   if ( x0 > x[jm-1] ) {
    jl = jm;
   } else {
    ju = jm;
   }
  }
  j = jl;
  if( x[jl] == x0 ) j++;
 }
 return( j );
}

long utn_ar_cbisect_s( const short* x, const long n, const short x0 )
{
 return( utn_ar_bisect_s( x, n, x0 ) - 1 );
}


long utn_ar_bisect_i( const long* x, const long n, const long x0 )
{
 long j, ju, jl, jm;
 if ( x0 <= x[0] ) {
  j = 1;
 } else if ( x0 >= x[n-1] ) {
  j = n;
 } else {
  jl = 0;
  ju = n+1;
  while( (ju-jl) > 1 ) {
   jm = (ju+jl)/2;
   if ( x0 > x[jm-1] ) {
    jl = jm;
   } else {
    ju = jm;
   }
  }
  j = jl;
  if( x[jl] == x0 ) j++;
 }
 return( j );
}

long utn_ar_cbisect_i( const long* x, const long n, const long x0 )
{
 return( utn_ar_bisect_i( x, n, x0 ) - 1 );
}

