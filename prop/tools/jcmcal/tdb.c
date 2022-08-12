/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

#define D2PI 6.283185307179586476925287
/*
 *  Earth M in radians given time in Julian Centuries rel to J2000.0
 */
double cal_earth_anom( double Centuries )
{
 double coeffs[] = { 6.24005225, 628.301973182, 0.0000002909, 0.00000005818 };
 double g;
 double dg;
 integer revs;
 double theta;
 integer i;
 double norm[2] = { 1.2896e-6, 31.0281e-6};
 double phase[2] ={ 2.631957, 4.387583 };
 double rate[2] = { 2.076942, 0.352557 };

 theta = poly_d( Centuries, coeffs, 3 );
 revs = (integer) (theta / D2PI);
 g = theta - revs * D2PI;

/* 
!  Long period perturbations in mean anomaly and longitude
!  4Mars-7Earth+3Venus
!  3Jupiter-8Mars+4Earth
 */
 for ( i = 0; i < 2; i++ )
 {
  dg = norm[i] * sin( phase[i] + rate[i] * Centuries );
  g += dg;
 }

 return g;
}


double cal_tdb( edouble et )
{
 double Centuries;
 double g;
 double dtdb;
 double amplitude[2] = { 0.001658, 0.000014 };
 integer n;
 Centuries = cal_jc( cal_ed_double( et ) );
 g = cal_earth_anom( Centuries );
 dtdb = 0.0;
 for ( n = 1; n <= 2; n++ )
 {
  dtdb += amplitude[n-1] * sin( n * g );
 }
 return dtdb;
}
