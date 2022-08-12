/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

double frw_eta_integrand( integer k, double omega_r, double omega_m, double omega_l, double a );
double frw_small_a( integer k, double omega_m, double omega_r, double omega_l );
double frw_small_a_eta_integral( integer k, double omega_m, double omega_r, double omega_l, double a1 );
double frw_small_a_t_integral( integer k, double omega_m, double omega_r, double omega_l, double a1 );
double frw_integrate( double a1, double a2, double da, integer k, double omega_m, double omega_r, double omega_l, double (*integrand)( integer, double, double, double, double ));
double frw_t_integrand( integer k, double omega_r, double omega_m, double omega_l, double a );
double frw_flat_t_integral( double x0 );

double frw_flat_eta_integral( double x0 );


double frw_eta_pen( double omega_m, double z )
{
 double as;

 double s;
 double s3;
 double sr;
 double f;
 double f8;
 double eta;
 double coeff[5] = { 1.0, -0.1540, 0.4304, 0.19097, 0.066941 };

/* NB approx gives eta like omega_m ** -1/3 near omega_m zero */

 as =  1.0 / sqrt( 1.0 + z );
 if ( omega_m == 1.0 )
  eta = 2 * as;
 else
 {
  s3 = ( 1 - omega_m ) / omega_m;
  s = pow( s3, 1./3. );
  sr = s / ( 1.0 + z );
  f = poly_d( sr, coeff, 4 );
  f8  = pow( f, 0.125 );
  eta = 2 * sqrt( s3 + 1.0 ) * as / f8;
 }
 return eta;
}

double frw_eta_pen1( double omega_m, double z )
{
 double as;

 double s;
 double s3;
 double sr;
 double f;
 double eta;

/* NB approx gives eta like omega_m ** -1/3 near omega_m zero */
 if ( omega_m == 1.0 )
 {
  as =  1.0 / sqrt( 1.0 + z );
  eta = 2 * as;
 }
 else
 {
  s3 = ( 1 - omega_m ) / omega_m;
  s = pow( s3, 1./3. );
  sr = s / ( 1.0 + z );
  f = sqrt( (s3+1.0)/s );
  eta = f * frw_flat_eta_integral( sr );
 }
 return eta;

}


double frw_t_pen1( double omega_m, double z )
{


 double s;
 double s3;
 double sr;
 double f;
 double t;

 s3 = ( 1 - omega_m ) / omega_m;
 s = pow( s3, 1./3. );
 sr = s / ( 1.0 + z );
 f = sqrt( (s3+1.0)/s3 );
 t = f * frw_flat_t_integral( sr );
 return t;

}


/* A suitable small value of a to use an approximate integral */
double frw_small_a( integer k, double omega_m, double omega_r, double omega_l )
{
 double a0 = 1.0e-4;
 double a1;
 a1 = a0;
 if ( omega_m > 1.e-5 && omega_r > 0.0 )
   a1 = 0.01 * omega_r/omega_m;
 else if ( omega_m == 0.0 && omega_r == 0.0 )
   a1 = 0.0;
 return a1;
}


double frw_small_a_eta_integral( integer k, double omega_m, double omega_r, double omega_l, double a1 )
{
 double sum = 0.0;
 if ( omega_r == 0.0 )
 {
  if( omega_m > 0 )
    sum = 2 * sqrt( a1 / omega_m );
  else
    sum = 0.0; /* Really infinity */
 }
 else 
  sum = a1 / sqrt( omega_r );
 return sum;
}

double frw_small_a_t_integral( integer k, double omega_m, double omega_r, double omega_l, double a1 )
{
 double sum = 0.0;
 if ( omega_r == 0.0 )
 {
  if( omega_m > 0 )
    sum = ( 2.0 / 3.0 ) * a1 * sqrt( a1 / omega_m );
  else
    sum = 0.0; /* Really infinity */
 }
 else 
  sum = 0.5 * a1 * a1 / sqrt( omega_r );
 return sum;
}

double frw_eta_integral( integer k, double omega_m, double omega_r, double omega_l, double x1, double x2, double da )
{
 double a1;
 double sum;

 sum = 0.0;
 a1 = x1;
 if ( a1 == 0.0 )
 {
  a1 = frw_small_a( k, omega_m, omega_r, omega_l );
  sum = frw_small_a_eta_integral( k, omega_m, omega_r, omega_l, a1 );
 }
/* Now integrate starting at a1 */
 sum += frw_integrate( a1, x2, da, k, omega_m, omega_r, omega_l, frw_eta_integrand );
 return sum;
}

double frw_t_integral( integer k, double omega_m, double omega_r, double omega_l, double x1, double x2, double da )
{
 double a1;
 double sum;

 sum = 0.0;
 a1 = x1;
 if ( a1 == 0.0 )
 {
  a1 = frw_small_a( k, omega_m, omega_r, omega_l );
  sum = frw_small_a_t_integral( k, omega_m, omega_r, omega_l, a1 );
 }
/* Now integrate starting at a1 */
 sum += frw_integrate( a1, x2, da, k, omega_m, omega_r, omega_l, frw_t_integrand );
 return sum;
}

double frw_integrate( double a1, double a2, double da, integer k, double omega_m, double omega_r, double omega_l, double (*integrand)( integer, double, double, double, double ))
{
 double sum = 0.0;
 double a;
 double anext;
 double dg;
 a = a1;
 anext = a + da;
 if ( anext >= a2 )
  da = a2-a;
 else
  sum +=  0.5 * da * integrand( k, omega_r, omega_m, omega_l, a );


 while ( anext <  a2 )
 {
  sum +=  da * integrand( k, omega_r, omega_m, omega_l, a );
  a = a + da; 
  anext = a + da;
 }
 dg = 0.5 * da + 0.5 * ( a2 - a );

 sum +=  dg * integrand( k, omega_r, omega_m, omega_l, a );
 sum +=  0.5 * (a2-a) * integrand( k, omega_r, omega_m, omega_l, a2 );

 return sum;
}

double frw_eta_integrand( integer k, double omega_r, double omega_m, double omega_l, double a )
{
  double a2;
  double f;
  double di;
  a2 = a * a;
  f = omega_r + omega_m  * a -k * a2 + omega_l * a2 * a2;
  di = 1.0 / sqrt( f );
  return di;
}

double frw_t_integrand( integer k, double omega_r, double omega_m, double omega_l, double a )
{
  double a2;
  double f;
  double di;
  a2 = a * a;
  f = omega_r + omega_m  * a -k * a2 + omega_l * a2 * a2;
  di = a / sqrt( f );
  return di;
}


/*  int sqrt( x^4 + x ) dx */
/* Expected range x=0 to 1. Steepest part is near x=0 */
double frw_flat_eta_integral( double x0 )
{
#define ETA_NSTEPS 10000
 double xmin = 0.001;
 double x[ETA_NSTEPS];
 double y[ETA_NSTEPS];
 integer n;
 double u;
 double u2; 
 double f;
 double fmin;
 double du1 = 0.0001;
 double du2 = 0.001;
 double ubreak = 0.2;
 double du;
 integer np = 3;
 double f0 = 2 * 0.3566;  /* 6 sqrt(pi) / (G(1/6) G(1/3))  */
 double fp; /* Exponent,  -1/2np */

 if ( x0 > 5.0 )
 {
/* Pen eq. 5 with np = 3 */
  f = ipow_d( x0, np) + np * ipow_d( f0, 2 * np + 1 ) / x0 + ipow_d( f0, 2* np );
  fp = -1.0 / ( 2.0 * np );
  f = 2 * pow( f, fp );  
  return f;
 }

 u = xmin;
 if ( x0 <= xmin )
  u = x0;

/* Small x expansion */
 fmin = 2 * sqrt( u ) * ( 1.0 - ipow_d( u, 3 ) /14.0 );

/* For integral xmin to ubreak and ubreak to x0, use different step sizes. 
   For xmin to ubreak, n = ( 0.2 - 0.001 ) / 0.0001 = 1990
   For ubreak to 1.0,  n = ( 1.0 - 0.2 ) / 0.001 = 800
   So can go up to x0 = 8.21
   Now x0 = ( 1 - Om_m / Om_m ) * ( 1 / ( 1 + z ))
   Large for small Om_m and negative redshift
 */

 du = du1;
 n = 0;
 while( u < x0 && n < ETA_NSTEPS )
 {
  x[n] = u;
  u2 = u * u;
  y[n] = 1 / sqrt( u2 * u2 + u );
  u = u + du;
  n++;
  if ( u > ubreak ) du = du2;
 }
 f = fmin + utn_ar_trap_d( x, y, n );
 return f;
}

double frw_flat_t_integral( double x0 )
{
#define ETA_NSTEPS 10000
 double xmin = 0.001;
 double x[ETA_NSTEPS];
 double y[ETA_NSTEPS];
 integer n;
 double u;
 double u2; 
 double f;
 double fmin;
 double du1 = 0.0001;
 double du2 = 0.001;
 double ubreak = 0.2;
 double du;
#if 0
 integer np = 3;
 double f0 = 2 * 0.3566;  /* 6 sqrt(pi) / (G(1/6) G(1/3))  */
 double fp; /* Exponent,  -1/2np */
#endif

 u = xmin;
 if ( x0 <= xmin )
  u = x0;

/* Small x expansion */
 fmin = ( 2./3.) * u * sqrt( u ) * ( 1.0 - ipow_d( u, 3 ) /3.0 );

/* For integral xmin to ubreak and ubreak to x0, use different step sizes. 
   For xmin to ubreak, n = ( 0.2 - 0.001 ) / 0.0001 = 1990
   For ubreak to 1.0,  n = ( 1.0 - 0.2 ) / 0.001 = 800
   So can go up to x0 = 8.21
   Now x0 = ( 1 - Om_m / Om_m ) * ( 1 / ( 1 + z ))
   Large for small Om_m and negative redshift
 */

 du = du1;
 n = 0;
 while( u < x0 && n < ETA_NSTEPS )
 {
  x[n] = u;
  u2 = u * u;
  y[n] = sqrt( u / ( u2 * u + 1.0 ));
  u = u + du;
  n++;
  if ( u > ubreak ) du = du2;
 }
 f = fmin + utn_ar_trap_d( x, y, n );
 return f;
}



