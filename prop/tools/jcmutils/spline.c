/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
double utn_spline_exec( double* xa, double* ya, double* y2a, integer n,
   double x, logical* status );
void utn_spline_calc(double* x, double* y, integer n, double* y2, double* u);


void utn_spline_free( Spline spline )
{
 free( spline->x );
 free( spline->y );
 free( spline->y2 );
 free( spline->u );
 free( spline );
}


Spline utn_spline_init_i( integer* x, integer* y, integer n )
{
 integer i;
 Spline spline = calloc( 1, sizeof( struct Spline_s ));
 spline->x =  calloc( n, UT_SZ_D );
 spline->y =  calloc( n, UT_SZ_D );
 spline->y2 = calloc( n, UT_SZ_D );
 spline->u = calloc( n, UT_SZ_D );
 spline->n = n;
 for ( i = 0; i < n; i++ )
 {
  spline->x[i] = x[i]; 
  spline->y[i] = y[i]; 
 }
 utn_spline_calc( spline->x, spline->y, spline->n, spline->y2, spline->u );
 return spline;
}


Spline utn_spline_init_d( double* x, double* y, integer n )
{
 integer i;
 Spline spline = calloc( 1, sizeof( struct Spline_s ));
 spline->x =  calloc( n, UT_SZ_D );
 spline->y =  calloc( n, UT_SZ_D );
 spline->y2 = calloc( n, UT_SZ_D );
 spline->u = calloc( n, UT_SZ_D );
 spline->n = n;
 for ( i = 0; i < n; i++ )
 {
  spline->x[i] = x[i]; 
  spline->y[i] = y[i]; 
 }
 utn_spline_calc( spline->x, spline->y, spline->n, spline->y2, spline->u );
 return spline;
}


/*********************/
void utn_spline_calc(double* x, double* y, integer n, double* y2, double* u)
{
  /* given arrays of data points x[0..n-1] and y[0..n-1], computes the
     values of the second derivative at each of the data points
     y2[0..n-1] for use in the splint function */

  integer i,k;
  double p,qn,sig,un;

  y2[0] = u[0] = 0.0;

  for (i=1; i<n-1; i++) 
  {
    sig = ((double) x[i]-x[i-1]) / ((double) x[i+1] - x[i-1]);
    p = sig * y2[i-1] + 2.0;
    y2[i] = (sig-1.0) / p;
    u[i] = (((double) y[i+1]-y[i]) / (x[i+1]-x[i])) - 
           (((double) y[i]-y[i-1]) / (x[i]-x[i-1]));
    u[i] = (6.0 * u[i]/(x[i+1]-x[i-1]) - sig*u[i-1]) / p;
  }
  qn = un = 0.0;

  y2[n-1] = (un-qn*u[n-2]) / (qn*y2[n-2]+1.0);
  for (k=n-2; k>=0; k--)
    y2[k] = y2[k]*y2[k+1]+u[k];
}

double utn_spline_eval( Spline spline, double x, logical* status )
{
 return utn_spline_exec( spline->x, spline->y, spline->y2, spline->n,
     x, status );
}

/*********************/
double utn_spline_exec( double* xa, double* ya, double* y2a, integer n,
   double x, logical* status )
{
  int klo,khi,k;
  double h,b,a;
  
  *status = UT_FALSE;
  klo = 0;
  khi = n-1;
  while (khi-klo > 1) {
    k = (khi+klo) >> 1;
    if (xa[k] > x) khi = k;
    else klo = k;
  }
  h = xa[khi] - xa[klo];
  if ( h== 0.0 ) *status = UT_TRUE;
  a = (xa[khi]-x)/h;
  b = (x-xa[klo])/h;
  return (a*ya[klo] + b*ya[khi] + ((a*a*a-a)*y2a[klo] +(b*b*b-b)*y2a[khi]) 
	  * (h*h) / 6.0);
}
    


