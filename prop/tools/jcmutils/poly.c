/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

float utn_ar_linterp_r( const float* x, const float* y, const long n, const float x0 )
{
 long i;
 i = utn_ar_cbisect_r( x, n, x0 );
 i = min_i( i, n-2 );
 return( lint_r( x[i], x[i+1], y[i], y[i+1], x0 ) );
}

double utn_ar_linterp_d( const double* x, const double* y, const long n, const double x0 )
{
 long i;
 i = utn_ar_cbisect_d( x, n, x0 );
 i = min_i( i, n-2 );
 return( lint_d( x[i], x[i+1], y[i], y[i+1], x0 ) );
}

void utn_ar_movag_r( const float* yy0, float* y, const long n, const long k )
{
 long i, j, j1, j2;
 long m1, m2;
 double sum;

 j = ( k - 1 ) / 2;
 j1 = -j-1;
 j2 = j1 + k;
 sum = 0.0;
 for ( i = j1 ; i < j2; i++ ) {
  m1 = min_i( max_i( i, 0 ), n-1 );
  sum += yy0[m1];
 }

 for ( i = 0; i < n; i++ ) {
  m1 = min_i( max_i( j1 + i, 0 ), n-1 );
  m2 = min_i( max_i( j2 + i, 0 ), n-1 );
  sum += yy0[m2] - yy0[m1];
  y[i] = sum/k;
 }
}

void utn_ar_movag_d( const double* yy0, double* y, const long n, const long k )
{
 long i, j, j1, j2;
 long m1, m2;
 double sum;

 j = ( k - 1 ) / 2;
 j1 = -j-1;
 j2 = j1 + k;
 sum = 0.0;
 for ( i = j1 ; i < j2; i++ ) {
  m1 = min_i( max_i( i, 0 ), n-1 );
  sum += yy0[m1];
 }

 for ( i = 0; i < n; i++ ) {
  m1 = min_i( max_i( j1 + i, 0 ), n-1 );
  m2 = min_i( max_i( j2 + i, 0 ), n-1 );
  sum += yy0[m2] - yy0[m1];
  y[i] = sum/k;
 }
}

double utn_poly_d( const double x, const double* coeffs, const long degree )
{
 long order;
 double yval = 0.0;
 for ( order = degree; order >= 0; order-- ) {
  yval = yval * x + coeffs[order];
 }
 return( yval );
}

float utn_poly_r( const float x, const float* coeffs, const long degree )
{
 long order;
 float yval = 0.0;
 for ( order = degree; order >= 0; order-- ) {
  yval = yval * x + coeffs[order];
 }
 return( yval );
}

void utn_ar_poleval_d( const double* x, double* y, const long m, const double* coeffs, const long degree )
{
 long n = m;
 while( n-- ) { *y++ = poly_d( *x++, coeffs, degree ); }
}

void utn_ar_poleval_r( const float* x, float* y, const long m, const float* coeffs, const long degree )
{
 long n = m;
 while( n-- ) { *y++ = poly_r( *x++, coeffs, degree ); }
}


void utn_ar_lsfit_r( const float* x, const float* y, const long n, float* a, float* b )
{
 double xbar, ybar, xvar, sxy, t;
 long i;

 xbar = 0;
 ybar = 0;
 xvar = 0;
 sxy = 0;
 for ( i = 0; i < n; i++ ) {
  xbar += x[i];
  ybar += y[i];
 }
 xbar /= n;
 ybar /= n;
 for ( i = 0; i < n; i++ ) {
  t = x[i] - xbar;
  xvar += t * t;
  sxy  += t * y[i];
 }
 *b = sxy / xvar;
 *a = ybar - (*b) * xbar;
}

void utn_ar_lsfit_d( const double* x, const double* y, const long n, double* a, double* b )
{
 double xbar, ybar, xvar, sxy, t;
 long i;

 xbar = 0;
 ybar = 0;
 xvar = 0;
 sxy = 0;
 for ( i = 0; i < n; i++ ) {
  xbar += x[i];
  ybar += y[i];
 }
 xbar /= n;
 ybar /= n;
 for ( i = 0; i < n; i++ ) {
  t = x[i] - xbar;
  xvar += t * t;
  sxy  += t * y[i];
 }
 *b = sxy / xvar;
 *a = ybar - (*b) * xbar;
}

