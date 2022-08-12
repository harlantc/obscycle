/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

/*
 *  These derived routines don't use the KeplerPrimary object at all
 *  They are more a property of an orbit
 */

double kepler_radius( double a, double e, double true_anom )
{
 double r;
 r = a * ( 1.0 - e * e ) / ( 1.0 + e * dcosd( true_anom ) );
 if ( e > 1.0 && a > 0 ) 
  r = -r;

 return( r );
}

double kepler_radius_q( double q, double e, double true_anom )
{
 double r;
 r = q * ( 1.0 + e ) / ( 1.0 + e * dcosd( true_anom ) );
 if ( e > 1.0 && r < 0 ) 
  r = -r;
 return( r );
}

/* Parab ok */
double kepler_solve( double mean_motion, double e, double t0, double t )
{
 double true;
 double mean_anom;
 mean_anom = kepler_mean_anom( mean_motion, e, t0, t );
 true = kepler_equation( mean_anom, e );
 return( true ); 
}

double kepler_mean_anom( double mean_motion, double e, double t0, double t )
{
 double mean_rev;
 double mean_anom;
 integer mean_rev_no;

  mean_rev    =  mean_motion * ( t - t0 );
  if ( e < 1.0 )
   mean_rev_no = (integer)mean_rev;
  else
   mean_rev_no = 0;

  mean_anom = ( mean_rev - mean_rev_no ) * 360.0;
 return mean_anom; 
}

/*
 *  Get time on this rev when M is a particular value.
 */
double kepler_next_pass( double mean_motion, double t0, double t, double mean_anom )
{
 double mean_rev;
 integer mean_rev_no;
 double t1;
 mean_rev    =  mean_motion * ( t - t0 );
 mean_rev_no = (integer)mean_rev;
 mean_rev = mean_rev_no + mean_anom / 360.0;
 t1 = t0 + ( mean_rev / mean_motion );
 return t1;
}

integer kepler_rev( double mean_motion, double e, double arg_peri, double t0, double t )
{
 double mean_rev;
 double mean_anom;
 double equator_crossing;
 integer rev;

 if ( e >= 1.0 )
  return 0;

 mean_rev = mean_motion * ( t - t0 );
/* Calculate mean rev of equator crossing */
 mean_anom = kepler_conic( 360.0 - arg_peri, e );
 equator_crossing = ( mean_anom / 360.0 - 1.0 );
/* Convert from revs since perigee to revs since equator crossing */
 rev = utn_floor_d( mean_rev - equator_crossing );
 return( rev );
}

/*
 * Get mean anomaly given true anomaly
 */
double kepler_conic( double true, double e )
{
 double mean;
 double ex1;
 double E;
 double tan_half_true;
 if ( e > 1.0 ) {
  tan_half_true = dsind( true / 2.0 )/ dcosd( true /2.0 );
  ex1 = sqrt(  ( e - 1.0 ) / ( e + 1.0 ) );
  E = 2.0 * atanh( ex1  * tan_half_true );
  mean = radout_d( e * sinh( E ) - E );
#if 0
printf( "KC TA %8.3f EX1T2 = %f E  %8.3f MA %8.3f\n", true, ex1 * tan_half_true, radout_d( E), mean );
#endif
 } else if ( e == 1.0 ) {
  E = utn_dsind( 0.5 * true ) / utn_dcosd( 0.5* true );
  mean = E + E*E*E/3;
  mean = radout_d( mean );
 } else {
  ex1 = sqrt( ( 1.0 - e ) / ( 1.0 + e ) );
  E = 2.0 * argd_d( dcosd( true / 2.0 ), ex1 * dsind( true / 2.0 ) );
  mean = E - e * radout_d( dsind( E ) );
  mean = circ_d( mean );
 }
 return( mean );
}


double kepler_equation( double M, double e )
{
 double true;

 double ex1;
 integer iter;
 double M_rad;
 double w,y,z;
 double M_estimate;
 double E_rad;
 double dE;
 double etol;
 double tol = 0.000001;
 double M2;
 integer max_iter = 100;
 double err;

 if( e == 1.0 ) {
  return barker_solve( M );  /* Parabola */
 } else if ( e > 1.0 ) {
/* Hyperbolic */
  ex1 = sqrt( ( e + 1.0 ) / ( e - 1.0 ) );
  M_rad = radin_d( M );
  E_rad = M_rad;
  dE = 2.0 * tol;
  iter = 1;
  etol = 5.0 * ( e - 0.5 );
  if ( e < 1.1 ) 
  {
   M2 = M_rad > 0 ? M_rad : -M_rad;
   E_rad = pow( 6.0 * M2 / e,  1.0/3.0 );
   if ( M_rad < 0 ) E_rad = -E_rad;
  } else if ( M_rad < etol ) {
   y = sqrt( 8.0 * ( e - 1 ) / e );
   z = 3.0 * M_rad / ( y * (e-1) );
   w = asinh( z ) / 3.0;
   E_rad = y * esinh( w );
  } else {
   E_rad = log( 2.0 * M_rad / e );
  }
  while ( fabs(dE) > tol && iter < max_iter ) {
   M_estimate = e * esinh( E_rad ) - E_rad;
   dE = ( M_rad - M_estimate ) / ( e * ecosh( E_rad ) - 1.0 );
   E_rad += dE;
   iter++;
  }
  true = 2.0 * argd_d( ecosh( E_rad / 2.0 ), ex1 * esinh( E_rad / 2.0 ) );
  true = circ_ew_d( true );
 } else {
/* Elliptic */
  ex1 = sqrt( ( 1.0 + e ) / ( 1.0 - e ) );
  M_rad = radin_d( M );
  E_rad = M_rad;
  dE = 2.0 * tol;
  iter = 1;
  while( fabs( dE ) > tol && iter < max_iter ) {
   err =  M_rad - E_rad + e * sin( E_rad );
   dE =  err / ( 1.0 - e * cos( E_rad ) );
   E_rad += dE;
   iter++;
  }
  if ( iter >= max_iter ) printf( "Kepler exceeded iterations M = %f err=%f rad %f deg dE=%f\n", M, err, radout_d(err), dE );
  true = 2.0 * argd_d( cos( E_rad / 2.0 ), ex1 * sin( E_rad / 2.0 ) );

  true = circ_d( true );  
 }
#if 0
 printf( "M= %8.3f E = %8.3f TA = %8.3f TNH E=%f \n", M, radout_d(E_rad), true, esinh( E_rad/2.0)/ecosh( E_rad/2.0) );
#endif
 return( true );
}

/*
 *  Calculate the angle from node to projection of position in equatorial
 *  plane.
 */
double kepler_nodal_angle_to_longitude( double nodal_angle, double inc )
{
 double nodal_longitude;

 nodal_longitude = argd_d( dcosd( nodal_angle ), dsind( nodal_angle ) * dcosd( inc ) );
 return( nodal_longitude );
}


double kepler_nodal_angle_to_latitude( double nodal_angle, double inc )
{
 double nodal_latitude;

 nodal_latitude = dasind( dsind( nodal_angle) * dsind( inc ) );
 return( nodal_latitude );
}





/* 
 *  Calculate angle between ascending node and crossing of given
 *  latitude.
 */
double kepler_latitude_to_nodal_angle( double latitude, double inc )
{
 double lat_max;
 double nodal_angle;
 lat_max = min_d( inc, 180.0 - inc );
 if ( fabs( latitude ) > lat_max  ) {
  nodal_angle = 90.0;
 } else if ( inc == 0.0 ) {
  nodal_angle = 0.0;
 } else {
  nodal_angle = dasind( dsind( latitude ) / dsind( inc ) );
 }
 return( nodal_angle );
}


double kepler_height_to_anom( World world, double a, double e, double h )
{
 double x;
 double z = h + world->radius;
 double y; 
 double phi;
 if ( e == 0.0 )
  return 0.0;  
 x = a * ( 1 - e * e ) / z;  
 y = ( x - 1 ) / e;
 phi = dacosd( y );
 return phi;
}

double kepler_height_to_anom_q( World world, double q, double e, double h )
{
 double x;
 double z = h + world->radius;
 double y; 
 double phi;
 if ( e == 0.0 )
  return 0.0;  
 x = q * ( 1 + e ) / z;  
 y = ( x - 1 ) / e;
 phi = dacosd( y );
 return phi;
}


