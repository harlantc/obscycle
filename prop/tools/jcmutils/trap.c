/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/*
 *   Simple trapezium rule integration
 *   Integral y(x) is returned as function value
 */

double utn_ar_trap_d( const double* x, const double* y, const long n )
{
 long i;
 double sum;

 if ( n <= 1 ) {
  sum = 0.0;
 } else if ( n == 2 ) {
  sum = ( y[1] + y[0] ) * ( x[1] - x[0] );
 } else {
  sum = y[0] * ( x[1] - x[0] ) + y[n-1] * ( x[n-1] - x[n-2] );
  for ( i = 1; i < n-1; i++ ) {
   sum += y[i] * ( x[i+1] - x[i-1] );
  }
 }
 return( 0.5 * sum );
}

float utn_ar_trap_r( const float* x, const float* y, const long n )
{
 long i;
 float sum;

 if ( n <= 1 ) {
  sum = 0.0;
 } else if ( n == 2 ) {
  sum = ( y[1] + y[0] ) * ( x[1] - x[0] );
 } else {
  sum = y[0] * ( x[1] - x[0] ) + y[n-1] * ( x[n-1] - x[n-2] );
  for ( i = 1; i < n-1; i++ ) {
   sum += y[i] * ( x[i+1] - x[i-1] );
  }
 }
 return( 0.5 * sum );
}

/*
 *  Carry out an integration given the logs of the y axis.
 *   We integrate I = int 10^y dx 
 *   and return log I.
 * This is useful when dealing with e.g. flux spectra given
 * in log units.
 */
double utn_ar_trap_log_d( const double* x, const double* y, const long n )
{
 long i;
 double sum;
 double ymean;
 double yvar;
 double yy1,y2;
 double result;

 utn_ar_stats_d( y, n, &ymean, &yvar );
 if ( n <= 1 ) {
  result = 0.0;
 } else if ( n == 2 ) {
  yy1 = pow( 10.0, y[0] - ymean );
  y2 = pow( 10.0, y[n-1] - ymean );
  sum = 0.5 * ( yy1 + y2 ) * ( x[1] - x[0] );
  result = ymean + log10( sum );
 } else {
  yy1 = pow( 10.0, y[0] - ymean );
  y2 = pow( 10.0, y[n-1] - ymean );
  sum = yy1 * ( x[1] - x[0] ) + y2 * ( x[n-1] - x[n-2] );
  for ( i = 1; i < n-1; i++ ) {
   yy1 = pow( 10.0, y[i] - ymean );
   sum += yy1 * ( x[i+1] - x[i-1] );
  }
  sum *= 0.5;
  result = ymean + log10( sum );
 }
 return( result );
}

float utn_ar_trap_log_r( const float* x, const float* y, const long n )
{
 long i;
 float sum;
 float ymean;
 float yvar;
 float yy1,y2;
 float result;

 utn_ar_stats_r( y, n, &ymean, &yvar );
 if ( n <= 1 ) {
  result = 0.0;
 } else if ( n == 2 ) {
  yy1 = rpow( 10.0, y[0] - ymean );
  y2 = rpow( 10.0, y[n-1] - ymean );
  sum = 0.5 * ( yy1 + y2 ) * ( x[1] - x[0] );
  result = ymean + rlog10( sum );
 } else {
  yy1 = rpow( 10.0, y[0] - ymean );
  y2 = rpow( 10.0, y[n-1] - ymean );
  sum = yy1 * ( x[1] - x[0] ) + y2 * ( x[n-1] - x[n-2] );
  for ( i = 1; i < n-1; i++ ) {
   yy1 = rpow( 10.0, y[i] - ymean );
   sum += yy1 * ( x[i+1] - x[i-1] );
  }
  sum *= 0.5;
  result = ymean + rlog10( sum );
 }
 return( result );
}

/*
 *   Integral of evenly spaced array
 */

double utn_ar_trap_dx_d( const double* y, const long n, const double dx )
{
 long i;
 float sum;

 if ( n <= 1 ) {
  sum = 0.0;
 } else if ( n == 2 ) {
  sum = 0.5 * ( y[1] + y[0] ) * dx;
 } else {
  sum = 0.5 * ( y[0] + y[n-1] );
  for ( i = 1; i < n-1; i++ ) {
   sum += y[i];
  }
  sum *= dx;
 }
 return( sum );

}

float utn_ar_trap_dx_r( const  float* y, const long n, const float dx )
{
 long i;
 float sum;

 if ( n <= 1 ) {
  sum = 0.0;
 } else if ( n == 2 ) {
  sum = 0.5 * ( y[1] + y[0] ) * dx;
 } else {
  sum = 0.5 * ( y[0] + y[n-1] );
  for ( i = 1; i < n-1; i++ ) {
   sum += y[i];
  }
  sum *= dx;
 }
 return( sum );

}

