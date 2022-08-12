/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*======================================================================*/
/*
 * Math routines
 *
 * <math.h>:  	log  used in logbase_d
 * 		modf used in frac_d_std
 * <float.h>:	DBL_EPSILON used in frac_d
 */
/*======================================================================*/

#include <float.h>
#include "utlib.h"

double utn_range_d( double x, double xmin, double xmax )
{
 if ( x < xmin ) x = xmin;
 if ( x > xmax ) x = xmax;
 return x;
}


double utn_min_d( double x, double y )
{
 return( x < y ? x : y );
}

double utn_max_d( double x, double y )
{
 return( x > y ? x : y );
}

long utn_ceiling_d( const double x )
{
 long i;
 i = (long)x;
 if ( x > (double)i ) i++;
 return( i );
}

long utn_ceiling_r( const float x )
{
 long i;
 i = (long)x;
 if ( x > (float)i ) i++;
 return( i );
}

long utn_floor_d( const double x )
{
 long i;
 i = (long)(x+ 2 * DBL_EPSILON);
 if ( x < (double)i ) i--;
 return( i );
}

long utn_floor_r( const float x )
{
 long i;
 i = (long)(x + 2 * FLT_EPSILON);
 if ( x < (float)i ) i--;
 return( i );
}


/* The ANSI C modulo operator is unreliable (machine dept) for negative operands 
 and undefined for float operands */

long utn_modulo_i( const long x, const long base )
{
 long result;
 if ( x > 0 && base > 0 ) {
  result = x % base;
 } else {
  result = x - floor_d( (double)x / (double)base ) * base;
 }
 return( result );
}

short utn_modulo_s( const short x, const short base )
{
 short result;
 if ( x > 0 && base > 0 ) {
  result = x % base;
 } else {
  result = x - floor_d( (double)x / (double)base ) * base;
 }
 return( result );
}

float utn_modulo_r( const float x, const float base )
{
 float result;
 result = x - floor_r( x / base ) * base;
 if ( base > 0 && result > base - 10 * base * FLT_EPSILON ) result = 0.0;
 return( result );
}

double utn_modulo_d( const double x, const double base )
{
 double result;
 result = x - floor_d( x / base ) * base;
 if ( base > 0 && result > base - 10 * base * DBL_EPSILON ) result = 0.0;
 return( result );
}



/*----------------------------------------------------------------------*/
/*   divisible
 *
 *   Is a divisible by b? 
 *
 *   1996 Aug 29 JCM
 *   Args:
 *  	a		(i)	Operand
 *	b		(i)	Divisor
 */

logical utn_divisible( const long a, const long b )
{
 return (  ( a/b ) * b == a  );
}

/*----------------------------------------------------------------------*/
/*
 *   min_i
 *
 *   Return smaller of i and j
 *   1996 Aug 29 JCM
 *
 */
 
long utn_min_i( const long i, const long j )
{
 long k;
 k = i < j ? i : j;
 return( k );
}

/*----------------------------------------------------------------------*/
/*
 *   max_i
 *
 *   Return larger of i and j
 *   1996 Aug 29 JCM
 *
 */

long utn_max_i( const long i, const long j )
{
 long k;
 k = i > j ? i : j;
 return( k );
}


/*----------------------------------------------------------------------*/
/*
 *   rmod_d
 *
 *   Calculate quotient and remainder
 *   1996 Aug 29 JCM
 *   Args:
 *	x		(i)	Input value
 *	y		(o)	Remainder
 *	r		(i)	Divisor
 *	rmod_d		(o)	Quotient
 */

long utn_rmod_d( const double value, double* remainder, const double divisor )
{
 long quotient;
 quotient = floor_d( value / divisor );
 *remainder = value - divisor * quotient; 
 return( quotient );
}

/*----------------------------------------------------------------------*/
/*
 *   rmod_i
 *
 *   Calculate quotient and remainder
 *   1996 Aug 29 JCM
 */

long utn_rmod_i( const long value, long* remainder, const long divisor )
{
 long quotient;
 quotient = floor_d( (double)value / (double) divisor );
 *remainder = value - divisor * quotient;
 return( quotient );
}


long utn_rmod_r( const float value, float* remainder, const float divisor )
{
 long quotient;
 quotient = floor_r( value / divisor );
 *remainder = value - divisor * quotient; 
 return( quotient );
}

long utn_rmod_s( const short value, short* remainder, const short divisor )
{
 long quotient;
 quotient = floor_r( (float)value / (float) divisor );
 *remainder = value - divisor * quotient;
 return( quotient );
}


/*----------------------------------------------------------------------*/
/*
 *  frac_r
 *
 *  Split value into long and fractional parts.
 *  Uses FLT_EPSILON from <float.h>
 *  1996 Aug 29 JCM
 *
 */

void utn_frac_r( const float value, long* int_val, float* fraction )
{
 long long_part;

 long_part = floor_r( value );
 *fraction = value - long_part;
 if ( *fraction >= 1.0 - 2 * FLT_EPSILON ) 
 {
  *fraction = 0.0; long_part++;
 }
 *int_val = long_part;
}

/*----------------------------------------------------------------------*/
/*
 *  frac_d
 *
 *  Split value into long and fractional parts.
 *  Uses DBL_EPSILON from <float.h>
 *  1996 Aug 29 JCM
 *
 */

void utn_frac_d( const double value, long* int_val, double* fraction )
{
 long long_part;

 long_part = floor_d( value );
 *fraction = value - long_part;
 if ( *fraction > 1.0 - 2 * DBL_EPSILON ) { *fraction = 0.0; long_part++; }
 *int_val = long_part;
}

/*----------------------------------------------------------------------*/
/*
 *  logbase_i
 *
 *  Return log to base n of argument, rounded down to nearest long and
 *  at least unity. Used in sort algorithm.
 *  1996 Aug 30 JCM
 *  Args:
 * 	i		(i)	Argument
 *	n		(i)	Base
 */

long utn_logbase_i( const long i, const long n )
{
 double x;
 x = logbase_d(  (double)i , n ) + UT_FUNC_TOL;
 return( max_i( (long)x, 1 ));
}

/*----------------------------------------------------------------------*/
/*
 *  logbase_d
 *
 *  Return log to base n of argument
 *  Uses log from <math.h>
 *  1996 Aug 30 JCM
 *  Args:
 * 	x		(i)	Argument
 *	n		(i)	Base
 */

double utn_logbase_d( const double x, const long n )
{
 return( log( x )/log( (double)n ) );
}

float utn_logbase_r( const float x, const long n )
{
 double result;
 result = ( log((double)x )/log( (double)n ) );
 return( (float) result );
}

float utn_round_r( const float x, const long f )
{
 long tol;
 float y;
 float x_log;
 long x_exp;
 long x_rounded;
 float x_log_mantissa;

 tol = f - 1;
 if ( x == 0.0 ) {
  y = 0.0;
 } else {
  x_log = rlog10( rabs( x ) );
  x_exp = floor_d( x_log ) - tol;
  x_log_mantissa = x_log - x_exp;
  x_rounded = (long) ( pow( 10.0, x_log_mantissa ) + 0.5 );
  y = x_rounded * tpow_r( x_exp );
  if ( x < 0 ) y *= -1;
 }
 return( y );

}

double utn_round_d( const double x, const long f )
{
 long tol;
 double y;
 double x_log;
 long x_exp;
 long x_rounded;
 double x_log_mantissa;

 tol = f - 1;
 if ( x == 0.0 ) {
  y = 0.0;
 } else {
  x_log = log10( fabs( x ) );
  x_exp = floor_d( x_log ) - tol;
  x_log_mantissa = x_log - x_exp;
  x_rounded = (long) ( pow( 10.0, x_log_mantissa ) + 0.5 );
  y = x_rounded * tpow_d( x_exp );
  if ( x < 0 ) y *= -1;
 }
 return( y );
}

void utn_order_r( float* x, float* y)
{
 float tmp;
 if ( *y < *x ) { tmp = *x; *x = *y; *y = tmp; }
}

void utn_order_d( double* x, double* y )
{
 double tmp;
 if ( *y < *x ) { tmp = *x; *x = *y; *y = tmp; }
}


float utn_lint_r( const float x1, const float x2, const float yy1, const float yy2, const float x0 )
{
 float y;
 if ( x1 == x2 ) {
  y = ( yy1 + yy2 ) / 2.0;
 } else {
  y = ( yy1 * ( x2 - x0 ) + yy2 * ( x0 - x1 ) ) / ( x2 - x1 );
 }
 return( y );

}


double utn_lint_d( const double x1, const double x2, const double yy1, const  double yy2, const double x0 )
{
 double y;
 if ( x1 == x2 ) {
  y = ( yy1 + yy2 ) / 2.0;
 } else {
  y = ( yy1 * ( x2 - x0 ) + yy2 * ( x0 - x1 ) ) / ( x2 - x1 );
 }
 return( y );

}

float utn_rpow( const float x, const float n )
{
 return( (float)pow( (float)x, (float)n ) );
}

float utn_rlog10( const float x )
{
 return( (float)log10( (double) x ) );
}

float utn_rabs( const float x )
{
/* Not in math.h! */
 float y;
 y = x < 0.0 ? -x : x;
 return( y );
}

float utn_rsqrt( const float x )
{
 return( (float)sqrt( (double)x ) );
}


long utn_iplus( const logical q )
{
 return( q ? +1 : -1 );
}

long utn_nint_r( const float x )
{
 long i;
 float f;
 frac_r( x, &i, &f );
 if ( f >= 0.5 ) i++;
 return( i );
}

long utn_nint_d( const double x )
{
 long i;
 double f;
 frac_d( x, &i, &f );
 if ( f >= 0.5 ) i++;
 return( i );
}

double utn_hypot( double x, double y )
{
 double z = x*x + y * y;
 if ( z < 0.0 ) z =0.0;
 return (sqrt(z));
}

