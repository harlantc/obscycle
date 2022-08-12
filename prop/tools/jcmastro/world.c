/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

integer world_id( World world )
{
 if ( world )
  return world->id;
 else
  return 0;
}

integer world_idmask( World world )
{
 if ( world )
  return world->idmask;
 else
  return -1;
}

void world_idname( World world, char* buf )
{
 if ( world )
  utn_cs_copy( world->idname, buf );
 else
  utn_cs_copy( " ", buf );
}


/* Rotation ephemeris for worlds other than Earth not implemented yet */

double world_sidereal_angle( World primary, double t )
{
 if ( primary->idmask & WORLD_IS_EARTH )
  return gmst(t);
 else
  return 0.0;
}


void world_get_name( World primary, char* name, long maxlen )
{
 utn_cs_copy( " ", name );
 if ( primary )
  utn_cs_copy_siz( primary->name, name, maxlen );
}

/* period in s on input, convert to d */
void world_set( World primary, char* name, double GM, double radius, double period, double J2 )
{
 double precess_rad;
 utn_cs_copy( name, primary->name );
 if ( utn_cs_eq( name, "Earth"))
 {
  primary->idmask |= WORLD_IS_EARTH;
  primary->ew_longitude = UT_TRUE;
 } else if ( utn_cs_eq( name, "Moon")) {
  primary->ew_longitude = UT_TRUE;
 } else {
  primary->ew_longitude = UT_FALSE;
 }
 primary->k3_power = 2.0 / 3.0;
 primary->precess_power = -7.0 / 3.0;
 primary->GM = GM;
/* In seconds */
 primary->rotation_period  = period;
/* Use Kepler 3 to calculate surface skimming orbital period in seconds */
 primary->surface_period   =  sqrt( ( 4 * M_PI * M_PI / GM ) * ipow_d( radius, 3 ) );
/* In revs per day; */
 primary->stationary_motion = DL_SEC_D / primary->rotation_period;
 primary->radius = radius;
/* Use Kepler 3 to calculate radius of stationary orbit */
 primary->stationary_radius = radius * pow( period / primary->surface_period, primary->k3_power );
 primary->J2 = J2;
 precess_rad = 3 * M_PI * J2 * pow( primary->surface_period, 4.0 / 3.0 );
/* In degrees per day for surface skimming orbit */
 primary->precess_const = radout_d( precess_rad ) * DL_SEC_D;
}

void world_list( World primary )
{
 double dayrad;
 dayrad = primary->radius * pow( 86400.0 / primary->surface_period, primary->k3_power );
 printf( "Primary: %s\n", primary->name );
 printf( "Surface orbit period: %12.4f min\n", primary->surface_period/60.0 );
 printf( "Rotation period:      %12.4f min \n", primary->rotation_period/60.0 );
 printf( "Surface radius:       %12.4f km\n", primary->radius );
 printf( "Stationary radius:    %12.4f km\n", primary->stationary_radius );
 printf( "1-day radius:         %12.4f km\n", dayrad );
 printf( "Precession const:     %20.8f\n", primary->precess_const );
 printf( "Stationary motion:    %20.8f rev/day\n", primary->stationary_motion );
}

/*
 *  Return precession factor in degrees per day given period in seconds
 */
double kepler_precess_factor( World primary, double period, double e )
{
 double ecc_factor;
 double precessFactor;

 ecc_factor = ( 1.0 - e * e );
 if ( ecc_factor > 0 ) {
  precessFactor = primary->precess_const * pow( period, primary->precess_power ) / ( ecc_factor * ecc_factor );
 } else {
  precessFactor = 0.0;
 }
 return( precessFactor );
}


/*
 *  Given period and eccentricity, give SMA, perigee and apogee using
 *  Kepler's third law.
 */

void kepler_orbit_heights( World primary, double a0, double e, double* ph, double* ah )
{
 double a = a0;

 if ( e > 1.0 && a > 0 )
  a = -a0;

 if ( a == 0.0 ){
  *ph = 0.0;
  *ah = 0.0;
 } else { 
  *ph = a * ( 1.0 - e ) - primary->radius;
  *ah = a * ( 1.0 + e ) - primary->radius;
 }
}

void kepler_orbit_heights_q( World primary, double q, double e, double* ph, double* ah )
{

 if ( q == 0.0 ){
  *ph = 0.0;
  *ah = 0.0;
 } else { 
  *ph = q - primary->radius;

  if ( e == 1.0 ) 
   *ah = utn_infinity_d( 1 );
  else
   *ah = q * ( 1.0 + e ) / ( 1.0 - e ) - primary->radius;
 }
}

void kepler_orbit_ae( World primary, double ph, double ah, double* ap, double* ep )
{
 double a,e;
 a =0.5 * ( ph + ah ) + primary->radius;
 e = ( ah - ph ) / ( 2.0 * a );
 *ap = a;
 *ep = e;
}

/*
 * SemiMajor Axis from period by Kepler's third law 
 */

double kepler_three( World primary, double period )
{
 double a;
 if ( period == 0.0 ) {
  a = 0.0;
 } else {
  a = primary->radius * pow( period / primary->surface_period, primary->k3_power );
 }
 return( a );
}

/*
 * Given a in km, e, true anom in degrees, return h in km
 */
double kepler_height( World primary, double a, double e, double true_anom )
{
 double h;
 h = kepler_radius( a, e, true_anom ) - primary->radius;
 return( h );
}

double kepler_height_q( World primary, double q, double e, double true_anom )
{
 double h;
 h = kepler_radius_q( q, e, true_anom ) - primary->radius;
 return( h );
}

/*
 *  KeplerAscentAngle
 * What is the angle between the velocity vector and the unit vector
 * in the azimuthal direction in the orbit plane? 
 * It's always zero for a circular orbit.
 */

/*
 *  Convert from height and orbit shape to speed
 * cos-1 
 */
double kepler_ascent_angle( World primary, double height, double q, double e )
{
 double h;
 double alpha = 0.0;
 double v;
 double r;
 h = kepler_angular_momentum( primary, q, e );
 v = kepler_speed_q( primary, height, q, e );
 r = height + primary->radius;
/* This does not work for e > 1 */
 alpha = dacosd( h / ( r * v ));
 return alpha;
}

double kepler_angular_momentum( World primary, double q, double e )
{
 double h;
 double p;
 p = q * (1.0 + e );
 if ( p < 0 ) p = -p;
 h = sqrt( p * primary->GM );
 return ( h );
}


double kepler_speed( World primary, double height, double a, double e )
{
 double v;
 double ainv;
 double r = height + primary->radius; 

 if ( e == 1.0 )
 {
  ainv = 0.0;
 }
 else if ( e > 1.0 && a > 0.0 ) 
 {
  ainv = -1.0/a;
 } else {
  ainv =  1.0/a;
 }
 v =  sqrt( primary->GM * (2.0 / r - ainv ));
 return v;
}

double kepler_speed_q( World primary, double height, double q, double e )
{
 double v;
 double ainv;
 double r = height + primary->radius; 

 if ( q <= 0 )
 {
  return 0.0;
 }
 ainv =  (1-e)/q; 
 v =  sqrt( primary->GM * (2.0 / r - ainv ));
 return v;
}

double kepler_circular_speed( World primary, double r )
{
 double v;
 v =  sqrt( primary->GM / r );
 return v;
}

/* Used for hyperbolic orbits too */
double kepler_eccentric_speed( World primary, double a, double e )
{
 double v;
 double h;
 h = a * ( 1 - e * e );
 v =  sqrt( primary->GM / h );
 return v;
}

double kepler_eccentric_speed_q( World primary, double q, double e )
{
 double v;
 double h;
 h = q * (1.0 + e);
 v =  sqrt( primary->GM / h );
 return v;
}

/*
 *  Convert from semimajor axis to period in seconds
 */
double kepler_period( World primary, double a )
{
 double period;
 
 period = primary->surface_period * pow( fabs( a ) / primary->radius, 1.5 ); 
 return period;
}



double kepler_period_au( double a )   /* In days; assumes solar orbit */
{
 double period;
 
 period = YL_ORB * pow( fabs( a ), 1.5 ); 
 return period;
}



double world_radius( World primary )
{
 return primary->radius;
}
