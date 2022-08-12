/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
#include <stdio.h>
#include <limits.h>
/*
 *  Resolve an extended double into (integer+0.5) and fractional part
 *  The choice of the extra 0.5 is because of the origin of JD
 */
/*
 *   dt1 is the UTC of day, it1 is the JD to 0 UTC
 *   t2 is the sum of the fractional parts
 *   t1 is the sum of the integer parts

 *   tt is the high precision sum t + dt;
 */
edouble cal_ed_resolve( edouble e )
{
 double scale = DL_SEC_D;
 double epsilon = SXG_EPSILON;
 double step = 0.5;
 return cal_ed_resolve_scale( e, scale, epsilon, step );
}

edouble cal_ed_resolve_scale( edouble e, double scale, double epsilon, double step )
{
 double s;
 integer it1, it2, it3, it4;
 double dt1, dt2, fdt;
 double dtr;
 double t1, t2;
 edouble tt;

/* Check for value that doesn't fit in LONG */
 s = e.t + e.dt;
 if ( s > LONG_MAX || s < -LONG_MAX )
 {
  return e;
 }
 s = e.t - step;
 frac_d( s, &it1, &dt1 );
 if ( e.dt != 0.0 )
 {
  frac_d( e.dt, &it2, &dt2 );
 } else {
  it2 = 0;
  dt2 = 0.0;
 }
 t2 = dt1 + dt2;
 frac_d( t2, &it3, &fdt );
 t1 = it1 + it2 + it3 + step; 

/* Rounding check assumes units are days 
 * Convert to seconds, dtr is fractions of second
 */
 frac_d( fdt * scale, &it2, &dtr );
/* Now if frac of second is 0.99, dtr is 0.01 */
 dtr = 1.0 - dtr;
#if DEBUG
printf( "DTR = %20.10f EPS = %20.10f RATIO = %20.10f SCALE = %20.10f\n", dtr, epsilon, dtr/epsilon, scale );
#endif
 if ( dtr > 0.0 && dtr < epsilon )
 {
/* Round up: now dtr is seconds in units of days */
  dtr = (double)(it2+1) / scale;
/* it4 is round seconds in units of days */
  frac_d( dtr, &it4, &fdt );
  t1 += it4;
 }

 tt = cal_ed_compose( t1, fdt );
 return tt;
}



/*
 * Convert to a system where the format is ( real, frac )
 * instead of ( real + 0.5, frac - 0.5 );
 *  e = ( j + 0.5 ) + ( f - 0.5 );   ( f in 0.5, 1.5 )
 *  e - 0.5 = j + 0.5 + ( f - 1 );    (f-1 in -0.5, 0.5 )
 * Resolving:   s = j dt1=0 it1=j it2=-1,0   dt2=f, f-1 in ( 0.5:1,0:0.5 )
 *              t2 = f,f-1  it3=0 fdt = f,f-1
 *              t1 = j-1,j + 0.5 
 *          = j-0.5+f, j-0.5+f
 *  
 *  e = 
 */
edouble cal_ed_norm( edouble e )
{
 edouble e1 = { 0.0, 0.0 };
 double s;
 integer it1;
 integer ival;
 double dt1;
 s = e.t - 0.5;
 frac_d( s, &it1, &dt1 );
 frac_d( e.dt+dt1+0.5, &ival, &e1.dt );
 e1.t = it1 + ival;
 return e1;
}


edouble cal_ed_add( edouble t1, double dt )
{
 edouble t2;
 integer it1;
 double dt1;

 frac_d( dt, &it1, &dt1 );
 t2.t  = t1.t  + it1;
 t2.dt = t1.dt + dt1;
 return cal_ed_resolve( t2 );
}


edouble cal_ed_make( double t, double dt )
{
 edouble e;
 e = cal_ed_compose( t, dt );
 e = cal_ed_resolve( e );
 return e;
}

edouble cal_ed_compose( double t, double dt )
{
 edouble e;
 e.t = t;
 e.dt = dt;
 return e;
}

double cal_ed_double( edouble et )
{
 return( et.t + et.dt );
}



logical cal_ed_eq( edouble e1, edouble e2 )
{
 return( e1.t + e1.dt == e2.t + e2.dt );
}

double cal_ed_minus( edouble e1, edouble e2 )
{
 return ( (e1.t-e2.t) + (e1.dt-e2.dt) );
}

logical cal_ed_le( edouble et1, edouble et2 )
{
 logical q;
 q = (et1.t < et2.t ) || 
     ( ( et1.t == et2.t ) && ( et1.dt <= et2.dt ));
 return q;
}
