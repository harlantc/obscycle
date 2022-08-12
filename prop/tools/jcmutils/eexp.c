/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <float.h>

double utn_eexp( const double x )
{
 double result;
 if ( x > UT_MAX_EXP_ARG ) {
  result = utn_infinity_d( 1 );
 } else if ( x < -UT_MAX_EXP_ARG ) {
  result = 0.0;
 } else {
  result = exp( x );
 }
 return( result );
}

double utn_ecosh( const double x )
{
 double result;
 result = 0.5 * ( eexp( x ) + eexp( -x ) );
 return( result );
}

double utn_esinh( const double x )
{
 double result;
 if ( fabs( x ) < UT_MIN_SINH_ARG ) {
  result = x;
 } else {
  result = 0.5 * ( eexp( x ) - eexp( -x ) ); 
 }
 return( result );

}

double utn_asinh( const double x )
{
 double result;
 double ax;

 ax = fabs( x );
 if ( ax < UT_MIN_SINH_ARG ) {
  result = ax;
 } else if ( ax > UT_MAX_SINH_ARG ) { 
  result = log( 2.0 * ax );
 } else {
  result = log( ax + sqrt( 1.0 + ax * ax ) );
 }
 if ( x < 0.0 ) result *= -1;
 return( result );
}

double utn_atanh( double x )
{
 double f, y;
 f = ( 1.0 + x ) / ( 1.0 - x );
 y = 0.5 * log(  f );
 return( y );
}



double utn_acosh( double x )
{
 double eps;
 double result;
 eps = x - 1.0;
 
 if ( eps < 0.0 ) {
  result = 0.0;
 } else if ( eps < UT_MIN_SINH_ARG ) {
  result = sqrt( 2.0 * eps );
 } else if ( eps > UT_MAX_SINH_ARG ) {
  result = log( 2.0 * x );
 } else {
  result = log( x + sqrt ( x * x - 1.0 ) );
 }
 return( result );
}


double utn_bose_einstein( const double x )
{
 double result;
 if ( x > UT_MAX_EXP_ARG ) {
  result = 0.0;
 } else if ( x < -UT_MAX_EXP_ARG ) {
  result = -1.0;
 } else if ( fabs(x) < UT_MIN_EXP_ARG ) {
  if ( fabs(x) < UT_TINY_D ) {
   result = utn_infinity_d( 1 );
  } else {
   result = 1.0 / x;
  }
 } else {
  result = 1.0 / ( eexp(x) - 1.0 );
 }
 return( result );
}

double utn_dplanckn( const double x, const long n )
{
 double result;
 if ( n == 0 ) {
  result = bose_einstein( x );
 } else if ( x > UT_MAX_EXP_ARG ) {
  result = 0.0;
 } else if ( x < -UT_MAX_EXP_ARG ) {
  result = -1.0 * ipow_d( x, n );
 } else if ( fabs(x) < UT_MIN_EXP_ARG ) {

  if ( fabs( x ) < UT_TINY_D ) {
   if ( n == 1 ) {
    result = 1.0;
   } else if ( n < 0 ) {
    result = utn_infinity_d( 1 );
   } else {
    result = 0.0;
   }
  } else {
   result = ipow_d( x, (n-1) );
  }
 } else {
  result = ipow_d( x, n ) / ( eexp( x ) - 1.0 );
 }
 return( result );
}

/*
 *  Raise to long power
 *   ipow_d( x, 0 ) = 1
 *   ipow_d( x, 1 ) = x
 *   ipow_d( x, -n ) = 1 / x^n
 *   ipow_d( x, n ) = x^n
 */
double utn_ipow_d( const double x, const long m )
{
 long n = m;
 double result;
 result = 1.0;
 if ( n == 0 ) {
  return( result );
 } else if ( n == 1 ) {
  return ( x );
 } else if ( n < 0 ) {
  if ( x == 0.0 ) return( utn_infinity_d( 1 ) );
  while( n++ ) result *= x;
  return( 1.0/result );
 } else {
  if ( x == 0.0 ) return ( x );
  while( n-- ) result *= x;
  return( result );
 }
}

float utn_ipow_r( const float x, const long m )
{
 long n = m;
 float result;
 result = 1.0;
 if ( n == 0 ) {
  return( result );
 } else if ( n == 1 ) {
  return ( x );
 } else if ( n < 0 ) {
  if ( x == 0.0 ) return( utn_infinity_d( 1 ) );
  while( n++ ) result *= x;
  return( 1.0/result );
 } else {
  if ( x == 0.0 ) return ( x );
  while( n-- ) result *= x;
  return( result );
 }
}

/* Ten to the power */

double utn_tpow_d( const long m )
{
 long n = m;
 double result;
 result = 1.0;
 if ( n == 0 ) {
  return( result );
 } else if ( n < 0 ) {
  while( n++ ) result *= 10.0;
  return( 1.0/result );
 } else {
  while( n-- ) result *= 10.0;
  return( result );
 }
}

float utn_tpow_r( const long m )
{
 long n = m;
 float result;
 result = 1.0;
 if ( n == 0 ) {
  return( result );
 } else if ( n < 0 ) {
  while( n++ ) result *= 10.0;
  return( 1.0/result );
 } else {
  while( n-- ) result *= 10.0;
  return( result );
 }
}

double utn_dgauss( const double x, const double x0, const double sigma )
{
 double arg;
 double result;
 if ( sigma == 0.0 ) {
  result = 0.0;
 } else {
  arg = ( x - x0 ) / sigma;
  result = eexp( -0.5 *  ( arg * arg ) );
 }
 return( result );
}


double utn_erf( const double x )
{
 double f,g,y;
 double x1;
 double coeffs[10] = { -1.26551223, 1.00002368, 0.37409196, 0.09678418, -0.18628806,
                      0.27886807, -1.13520398, 1.48851587, -0.82215223, 0.17087277 };
 integer degree = 9;
 x1 = fabs( x );
 y  = 1.0/(1.0 + 0.5 * x1 );
 f =  utn_poly_d( y, coeffs, degree );
 g = 1.0 - y * utn_eexp( -x1*x1 + f );
 if ( x < 0 ) g = -g;
 return g;
}

/* Here we do erf = -inf to inf 
 *   This is the fractional area under the curve from -inf to x 
 */
double utn_erftot( const double x )
{
 double g;
 double root2 = sqrt(2.0);
 g = 0.5 * ( 1 + erf(x/root2));
 return g;
}

double utn_erfinv( const double p )
{

  double a1 = -39.69683028665376;
  double a2 = 220.9460984245205;
  double a3 = -275.9285104469687;
  double a4 = 138.3577518672690;
  double a5 =-30.66479806614716;
  double a6 = 2.506628277459239;

  double b1 = -54.47609879822406;
  double b2 = 161.5858368580409;
  double b3 = -155.6989798598866;
  double b4 = 66.80131188771972;
  double b5 = -13.28068155288572;

  double c1 = -0.007784894002430293;
  double c2 = -0.3223964580411365;
  double c3 = -2.400758277161838;
  double c4 = -2.549732539343734;
  double c5 = 4.374664141464968;
  double c6 = 2.938163982698783;

  double d1 = 0.007784695709041462;
  double d2 = 0.3224671290700398;
  double d3 = 2.445134137142996;
  double d4 = 3.754408661907416;

  double q;
  double x = 0;
  double y = 0;
  double r= 0;
  /* Define break-points. */

  double p_low =  0.02425;
  double p_high = 1 - p_low;
#if 0
  double sigma = 1.318;
  double my = 9.357;
#endif

 if ( p == 0.0 ) return utn_infinity_d(-1);

  /* Rational approximation for lower region. */


  if (0 < p && p < p_low) {
    q = sqrt(-2*log(p));
    x = (((((c1*q+c2)*q+c3)*q+c4)*q+c5)*q+c6) / ((((d1*q+d2)*q+d3)*q+d4)*q+1);
  }

  /* Rational approximation for central region. */

  if (p_low <= p && p <= p_high) {
    q = p - 0.5;
    r = q*q;
    x = (((((a1*r+a2)*r+a3)*r+a4)*r+a5)*r+a6)*q / (((((b1*r+b2)*r+b3)*r+b4)*r+b5)*r+1);
  }

  /* Rational approximation for upper region. */

  if (p_high < p && p < 1) 
  {
    q = sqrt(-2*log(1-p));
    x = -(((((c1*q+c2)*q+c3)*q+c4)*q+c5)*q+c6) / ((((d1*q+d2)*q+d3)*q+d4)*q+1);
  }
#if 0
  y = exp(sigma*x+my);
printf( "%f %f %f %f %g\n", q, x, sigma, my, y );
#endif
  y = x;
  return y;
}

double utn_erfinv2(double p)
{


/*
 * Lower tail quantile for standard normal distribution function.
 *
 * This function returns an approximation of the inverse cumulative
 * standard normal distribution function.  I.e., given P, it returns
 * an approximation to the X satisfying P = Pr{Z <= X} where Z is a
 * random variable from the standard normal distribution.
 *
 * The algorithm uses a minimax approximation by rational functions
 * and the result has a relative error whose absolute value is less
 * than 1.15e-9.
 *
 * Author:      Peter J. Acklam
 * Time-stamp:  2002-06-09 18:45:44 +0200
 * E-mail:      jacklam@math.uio.no
 * WWW URL:     http://www.math.uio.no/~jacklam
 *
 * C implementation adapted from Peter's Perl version
 */


/* Coefficients in rational approximations. */
const double a[] =
{
	-3.969683028665376e+01,
	 2.209460984245205e+02,
	-2.759285104469687e+02,
	 1.383577518672690e+02,
	-3.066479806614716e+01,
	 2.506628277459239e+00
};

const double b[] =
{
	-5.447609879822406e+01,
	 1.615858368580409e+02,
	-1.556989798598866e+02,
	 6.680131188771972e+01,
	-1.328068155288572e+01
};

const double c[] =
{
	-7.784894002430293e-03,
	-3.223964580411365e-01,
	-2.400758277161838e+00,
	-2.549732539343734e+00,
	 4.374664141464968e+00,
	 2.938163982698783e+00
};

const double d[] =
{
	7.784695709041462e-03,
	3.224671290700398e-01,
	2.445134137142996e+00,
	3.754408661907416e+00
};

#define LOW 0.02425
#define HIGH 0.97575

	double q, r;



	if (p < 0 || p > 1)
	{
		return 0.0;
	}
	else if (p == 0)
	{
		return -DBL_MAX /* minus "infinity" */;
	}
	else if (p == 1)
	{
		return DBL_MAX /* "infinity" */;
	}
	else if (p < LOW)
	{
		/* Rational approximation for lower region */
		q = sqrt(-2*log(p));
		return (((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
			((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
	}
	else if (p > HIGH)
	{
		/* Rational approximation for upper region */
		q  = sqrt(-2*log(1-p));
		return -(((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
			((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
	}
	else
	{
		/* Rational approximation for central region */
    		q = p - 0.5;
    		r = q*q;
		return (((((a[0]*r+a[1])*r+a[2])*r+a[3])*r+a[4])*r+a[5])*q /
			(((((b[0]*r+b[1])*r+b[2])*r+b[3])*r+b[4])*r+1);
	}
}


/* Asymmetric sinc function */

double utn_asym_sinc( double x, double a )
{
 double s;
 if ( x == 0.0 )
  s = cos(a);
 else
  s = ( sin( x-a ) + sin( a ) ) / x;
 return s;
}
