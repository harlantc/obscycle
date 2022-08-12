/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

double utn_E1_func( const double x )
{
 const long nc0 = 5;
 const long nc1 = 4; 
 const long nc2 = 4; 
 double a0[6] = { -0.57721566, 0.99999193, -0.24991055, 0.05519968,-0.00976004,  0.00107857  };
 double a1[5] = {  0.2677737343,8.6347608925,18.0590169730,   8.5733287401,1.0  };
 double a2[5] = { 3.9584969228,21.0996530827,25.6329561486,   9.5733223454,1.0  };
 double E1;
 double s,s1,s2;
 long i;

 if ( x <= 0.0 ) {
  E1 = 0.0;
 } else if ( x <= 1.0 ) {
  s = 0.0; 
  for ( i = nc0; i >= 0; i-- ) {
   s = s * x + a0[i];
  }
  E1 = s - log( x );
 } else {
  s1 = 0.0;
  for ( i = nc1; i >= 0; i-- ) {
   s1 = s1 * x + a2[i];
  }
  s2 = 0.0;
  for ( i = nc2; i >= 0; i-- ) {
   s2 = s2 * x + a1[i];
  }
  E1 = eexp( -x ) * ( s1 / s2 ) / x;
 }
 return( E1 );
}


double utn_acyc( const double x )
{
 const double third = (1.0/3.0);
 const double ord_unity = 6.0;
 double dc;
 double result;

 if ( ( x <= 0.0 ) || ( x >= 2.0 * M_PI ) ) {
  result = 0.0;
 } else if ( x < UT_APPROX_ARG_1 ) {
  result = pow( 6.0 * x, third );
 } else {
  if ( x < ord_unity ) {
   result = pow( 6.0 * x, third );
  } else {
   result = x;
  }
  dc = result;
  while( fabs( dc/result ) >= UT_FUNC_TOL ) {
   dc = ( x -  ( result - sin( result ) ) ) / ( 1.0 - cos( result ) );
   result += dc;
  }
 }
 return( result );
}


double utn_acych( const double x )
{
 const double third = 1.0/3.0;
 const double ord_unity = 6.0;
 double dc;
 double result;
 if ( x <= 0.0 ) {
  result = 0.0;
 } else if ( x < UT_APPROX_ARG_1 ) {
  result = pow( 6.0 * x, third );
 } else if ( x > 1.0 / UT_APPROX_ARG_1 ) {
  result = log( 2.0 * x );
 } else {
  if ( x < ord_unity ) {
   result = pow( 6.0 * x, third );
  } else {
   result = log( 2.0 * x );
  }
  dc = result;
  while( fabs( dc/result ) >= UT_FUNC_TOL ) {
   dc = ( x -  ( sinh( result ) - result  ) ) / ( cosh( result ) - 1.0 );
   result += dc;
  }
 }
 return( result );
}


double utn_hexagam( const double x )
{
 double result;
 long k;
 const long series_n_terms = 25;
 result = 0.0;
 for ( k = 1; k <= series_n_terms; k++ ) {
  result += ipow_d( x + k, -5 );
 }
 return( result );
}

