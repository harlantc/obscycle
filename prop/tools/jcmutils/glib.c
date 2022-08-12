/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/*
 * Safe mem free
 */

void utn_free( void* p )
{
 if ( p ) free( p );
}
/*
 * Allocate array of 3-vectors of length n 
 */

double** utn_ar_valloc_d( const long n )
{
 double* p;
 long c;
 double** v;
 v = (double**)malloc( n * UT_SZ_D );
 p = (double *) malloc( n * 3 * UT_SZ_D );
 for ( c = 0; c < n; c++ ) {
  v[c] = p + 3 * c;
  utn_ar_vzero_d( v[c] );
 }
 return( v );
}

/* Free an array of 3-vectors */

void utn_ar_vfree_d( double** v )
{
 free( v[0] );
 free( v );
}

/* Print a 3-vector */

char* utn_cs_vwritef_d( const double* p, const char* fmt, char* buf, const long siz )
{
 TextCard fbuf;
 TextCard tbuf;
 snprintf( fbuf, UT_CARD_SIZE, "%s %s %s", fmt, fmt, fmt );
 snprintf( tbuf, UT_CARD_SIZE, fbuf, p[0], p[1], p[2] );
 utn_cs_copy_siz( tbuf, buf, siz );
 return( buf );
}

char* utn_cs_vwritef_r( const float* p, const char* fmt, char* buf, const long siz )
{
 TextCard fbuf;
 TextCard tbuf;
 snprintf( fbuf, UT_CARD_SIZE, "%s %s %s", fmt, fmt, fmt );
 snprintf( tbuf, UT_CARD_SIZE, fbuf, p[0], p[1], p[2] );
 utn_cs_copy_siz( tbuf, buf, siz );
 return( buf );
}


/* Test if a 3-vector is null */

logical utn_ar_vnull_d( const double* v )
{
 return( v[0] == 0.0 && v[1] == 0.0 && v[2] == 0.0 );
}

logical utn_ar_vnull_r( const float* v )
{
 return( v[0] == 0.0 && v[1] == 0.0 && v[2] == 0.0 );
}


/* 
 *  Rotate 2-D coordinates through a given angle.
 *  The angle is in degrees.
 */

void rotate2_d( const double x, const double y, const double roll, double* xx1, double* yy1 )
{
 *xx1 = x * dcosd( roll ) - y * dsind( roll );
 *yy1 = x * dsind( roll ) + y * dcosd( roll );
}


