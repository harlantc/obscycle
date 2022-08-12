/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/*----------------------------------------------------------------------*/
/*
 * utn_ar_zero_i
 *
 * Zero each element of an long array.
 *
 */

void utn_ar_zero_i( long* a, const long n )
{
 long siz = n;
 while( siz-- ) { *a = 0; a++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_zero_d
 *
 * Zero each element of a double array.
 *
 */

void utn_ar_zero_d( double* a, const long n )
{
 long siz = n;
 while( siz-- ) { *a = 0; a++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_copy_i
 *
 * Copy one long array to another
 *
 */

void utn_ar_copy_i( const long* a, long* b, const long n )
{
 long siz = n;
 while( siz-- ) { *b = *a++; b++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_copy_d
 *
 * Copy one long array to another
 *
 */

void utn_ar_copy_d( const double* a, double* b, const long n )
{
 long siz = n;
 while( siz-- ) { *b = *a++; b++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_dot
 *
 *
 */

long utn_ar_dot_i( const long* x, const long* y, const long n )
{
 long sum;
 long i;
 
 sum = 0;
 for ( i = 0; i < n; i++ ) {
  sum += x[i] * y[i];
 }
 return( sum );
}


short utn_ar_dot_s( const short* x, const short* y, const long n )
{
 short sum;
 long i;
 
 sum = 0;
 for ( i = 0; i < n; i++ ) {
  sum += x[i] * y[i];
 }
 return( sum );
}


double utn_ar_dot_d( const double* x, const double* y, const long n )
{
 double sum;
 long i;

 sum = 0;
 for ( i = 0; i < n; i++ ) {
  sum += x[i] * y[i];
 }
 return( sum );
}

float utn_ar_dot_r( const float* x, const float* y, const long n )
{
 float sum;
 long i;

 sum = 0;
 for ( i = 0; i < n; i++ ) {
  sum += x[i] * y[i];
 }
 return( sum );
}


/*----------------------------------------------------------------------*/
/*
 *+ar_bounds
 *
 */

void utn_ar_bounds_i( const long* x, const long n, long* xmin, long* xmax )
{
 long i;
 long val;
 *xmin = x[0];
 *xmax = *xmin;
 for ( i = 1; i < n; i++ ) {
  val = x[i];
  if ( val < *xmin ) *xmin = val;
  if ( val > *xmax ) *xmax = val;
 }
}

void utn_ar_bounds_d( const double* x, const long n, double* xmin, double* xmax )
{
 long i;
 double val;
 *xmin = x[0];
 *xmax = *xmin;
 for ( i = 1; i < n; i++ ) {
  val = x[i];
  if ( val < *xmin ) *xmin = val;
  if ( val > *xmax ) *xmax = val;
 }
}

void utn_ar_bounds_s( const short* x, const long n, short* xmin, short* xmax )
{
 long i;
 short val;
 *xmin = x[0];
 *xmax = *xmin;
 for ( i = 1; i < n; i++ ) {
  val = x[i];
  if ( val < *xmin ) *xmin = val;
  if ( val > *xmax ) *xmax = val;
 }
}

void utn_ar_bounds_r( const float* x, const long n, float* xmin, float* xmax )
{
 long i;
 float val;
 *xmin = x[0];
 *xmax = *xmin;
 for ( i = 1; i < n; i++ ) {
  val = x[i];
  if ( val < *xmin ) *xmin = val;
  if ( val > *xmax ) *xmax = val;
 }
}


/*----------------------------------------------------------------------*/
/*
 *+ar_stats
 *
 */
void utn_ar_stats_d( const double* x, const long n , double* mean, double* var )
{
 long i;
 double off;
 *mean = x[0];
 *var  = 0;
 if ( n > 1 ) {
  for ( i = 1; i < n; i++ ) { *mean += x[i]; }
  *mean /= n;
  for ( i = 0; i < n; i++ ) { off = ( x[i] - *mean ) ; *var += off * off; }
  *var /= n;
 }
}


void utn_ar_stats_i( const long* x, const long n , double* mean, double* var )
{
 long i;
 double off;
 *mean = (double) x[0];
 *var  = 0;
 if ( n > 1 ) {
  for ( i = 1; i < n; i++ ) { *mean += x[i]; }
  *mean /= n;
  for ( i = 0; i < n; i++ ) { off = ( x[i] - *mean ) ; *var += off * off; }
  *var /= n;
 }
}



void utn_ar_stats_r( const float* x, const long n, float* mean, float* var )
{
 long i;
 float off;
 *mean = x[0];
 *var  = 0;
 if ( n > 1 ) {
  for ( i = 1; i < n; i++ ) { *mean += x[i]; }
  *mean /= n;
  for ( i = 0; i < n; i++ ) { off = ( x[i] - *mean ) ; *var += off * off; }
  *var /= n;
 }
}


void utn_ar_stats_s( const short* x, const long n, float* mean, float* var )
{
 long i;
 float off;
 *mean = (float) x[0];
 *var  = 0;
 if ( n > 1 ) {
  for ( i = 1; i < n; i++ ) { *mean += x[i]; }
  *mean /= n;
  for ( i = 0; i < n; i++ ) { off = ( x[i] - *mean ) ; *var += off * off; }
  *var /= n;
 }
}
/*----------------------------------------------------------------------*/
/*
 * utn_ar_zero_s
 *
 * Zero each element of an long array.
 *
 */

void utn_ar_zero_s( short* a, const long n )
{
 long siz = n;
 while( siz-- ) { *a = 0; a++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_zero_r
 *
 * Zero each element of a double array.
 *
 */

void utn_ar_zero_r( float* a, const long n )
{
 long siz = n;
 while( siz-- ) { *a = 0; a++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_copy_s
 *
 * Copy one long array to another
 *
 */

void utn_ar_copy_s( const short* a, short* b, const long n )
{ 
 long siz = n;
 while( siz-- ) { *b = *a++; b++; }
}

/*----------------------------------------------------------------------*/
/*
 * utn_ar_copy_r
 *
 * Copy one float array to another
 *
 */

void utn_ar_copy_r( const float* a, float* b, const long siz )
{ 
 long n = siz;
 while( n-- ) { *b = *a++; b++; }
}


/*----------------------------------------------------------------------*/
/*
 * utn_ar_copy_q
 *
 * Copy one logical array to another
 *
 */

void utn_ar_copy_q( const logical* a, logical* b, const long siz )
{
 long n = siz;
 while( n-- ) { *b = *a++; b++; }
}

void utn_ar_fill_s( short* x, const long m, const short x0 )
{
 long n = m;
 while( n-- ) { *x = x0; x++; }
}

void utn_ar_fill_q( logical* x, const long m, const logical x0 )
{
 long n = m;
 while( n-- ) { *x = x0; x++; }
}

void utn_ar_cmult_s( short* x, const long m, const short x0 )
{
 long n = m;
 while( n-- ) { *x *= x0; x++; }
}

void utn_ar_cadd_s( short* x, const long m, const short x0 )
{
 long n = m;
 while( n-- ) { *x += x0; x++; }
}

void utn_ar_add_s( const short* x, short* y, const long m )
{
 long n = m;
 while( n-- ) { *y += *x++; y++; }
}

void utn_ar_sub_s( const short* x, short* y, const long m )
{
 long n = m;

 while( n-- ) { *y -= *x++; y++; }
}

void utn_ar_wadd_s( const short* x1, const short* x2,
                const short w1, const short w2,
                short* y,
                const long m )
{
 long n = m;
 while( n-- ) { *y = w1 * (*x1++) + w2 * (*x2++); y++; }
}

void utn_ar_bin_s( short* x, const long n, const short x0, const short dx )
{
 long i;
 for ( i = 0; i < n; i++ ) {  x[i] = x0 + i * dx; }
}


void utn_ar_fill_i( long* x, const long m, const long x0 )
{
 long n = m;
 while( n-- ) { *x = x0; x++; }
}

void utn_ar_cmult_i( long* x, const long m, const long x0 )
{
 long n = m;
 while( n-- ) { *x *= x0; x++; }
}

void utn_ar_cadd_i( long* x, const long m, const long x0 )
{
 long n = m;
 while( n-- ) { *x += x0; x++; }
}

void utn_ar_add_i( const long* x, long* y, const long m )
{
 long n = m;
 while( n-- ) { *y += *x++;  y++; }
}

void utn_ar_sub_i( const long* x, long* y, const long m )
{
 long n = m;
 while( n-- ) { *y -= *x++; y++; }
}

void utn_ar_wadd_i( const long* x1, const long* x2,
                const long w1, const long w2,
                long* y, const long m )
{
 long n = m;
 while( n-- ) { *y = w1 * (*x1++) + w2 * (*x2++); y++; }
}

void utn_ar_bin_i( long* x, const long n, const long x0, const long dx )
{
 long i;
 for ( i = 0; i < n; i++ ) {  x[i] = x0 + i * dx; }
}

void utn_ar_fill_r( float* x, const long m, const float x0 )
{
 long n = m;
 while( n-- ) { *x = x0; x++; }
}

void utn_ar_cmult_r( float* x, const long m, const float x0 )
{
 long n = m;
 while( n-- ) { *x *= x0; x++; }
}

void utn_ar_cadd_r( float* x, const long m, const float x0 )
{
 long n = m;
 while( n-- ) { *x += x0; x++; }
}

void utn_ar_add_r( const float* x, float* y, const long m )
{
 long n = m;
 while( n-- ) { *y += *x++; y++; }
}

void utn_ar_sub_r( const float* x, float* y, const long m )
{
 long n = m;
 while( n-- ) { *y -= *x++; y++; }
}

void utn_ar_wadd_r( const float* x1, const float* x2,
                const float w1, const float w2,
                 float* y, const long m )

{
 long n = m;
 while( n-- ) { *y = w1 * (*x1++) + w2 * (*x2++); y++; }
}

void utn_ar_bin_r( float* x, const long n, const float x0, const float dx )
{
 long i;
 for ( i = 0; i < n; i++ ) {  x[i] = x0 + i * dx; }
}

void utn_ar_fill_d( double* x, const long m, const double x0 )
{
 long n = m;
 while( n-- ) { *x = x0; x++;}
}

void utn_ar_cmult_d( double* x, const long m, const double x0 )
{
 long n = m;
 while( n-- ) { *x *= x0; x++; }
}

void utn_ar_cadd_d( double* x, const long m, const double x0 )
{
 long n = m;
 while( n-- ) { *x += x0; x++; }
}

void utn_ar_add_d( const double* x, double* y, const long m )
{
 long n = m;
 while( n-- ) { *y += *x++; y++; }
}

void utn_ar_sub_d( const double* x, double* y, const long m )
{
 long n = m;
 while( n-- ) { *y -= *x++; y++; }
}

void utn_ar_wadd_d( const double* x1, const double* x2,
                const double w1,  const double w2,
                double* y,
                const long siz )
{
 long n = siz;
 while( n-- ) { *y = w1 * (*x1++) + w2 * (*x2++); y++; }
}

void utn_ar_bin_d( double* x, const long n, const double x0, const double dx )
{
 long i;
 for ( i = 0; i < n; i++ ) {  x[i] = x0 + i * dx; }
}




void ar_minmax_d( 
 double* x,		/* (i) Array */
 integer  n,		/* (i) Array size */
 double* range,		/* (o) Min max value */
 integer* loc )
{
 integer i;
 range[0] = x[0];
 range[1] = x[0];
 loc[0] = 1;  /* 1-based index */
 loc[1] = 1;
 for ( i = 1; i < n; i++ )
 {
  if( x[i] < range[0] ) {
   range[0] = x[i];
   loc[0] = i+1;
  }
  else if ( x[i] > range[1] ) 
  {
   range[1] = x[i];
   loc[1] = i+1;
  }
 }

}



double utn_ar_max_d( double* x, integer n )
{
 integer i;
 double r;
 r = x[0];
 for ( i =1; i < n; i++ )
  if ( x[i] > r ) r = x[i];
 return r;
}

float utn_ar_max_r( float* x, integer n )
{
 integer i;
 float r;
 r = x[0];
 for ( i =1; i < n; i++ )
  if ( x[i] > r ) r = x[i];
 return r;
}


