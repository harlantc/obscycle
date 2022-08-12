/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
double cube_root( double x );
/* Geodetic latitude */

double geod_flattening( World world )
{

 double f;
 if ( !world->polar_radius ) return 0.0;

 f = 1 - world->polar_radius / world->radius;
 return f;

}

double geodet_aux_C( double one_minus_f, double phi )
{
 double C;

 double cp, sp;
 double x;

 cp = dcosd( phi );
 sp = one_minus_f * dsind( phi );
 x = sqrt( cp * cp + sp * sp );
 if ( x == 0.0 )
  C = 0.0;
 else
  C = 1.0 / x;
 return C;
}


/* Transform GEOD coords to EFG coords */
void geod_to_efg( World world, double h, double glat, double lon, double* efg )
{
 double S, C;
 double re;
 double one_minus_f = world->polar_radius / world->radius;
 if ( one_minus_f == 0.0 ) one_minus_f = 1.0;  /* Protect against no polar data */

 C = geodet_aux_C( one_minus_f, glat );
 S = ( one_minus_f * one_minus_f ) * C;
 re     = ( world->radius * C + h ) * dcosd( glat );
 efg[0] =  re * dcosd( lon );
 efg[1] =  re * dsind( lon );
 efg[2] = ( world->radius * S + h ) * dsind( glat );
}


void geod_calc_re_z( World world, double h, double glat, double* re, double* z )
{
 double C, S;
 double one_minus_f = world->polar_radius / world->radius;
 if ( one_minus_f == 0.0 ) one_minus_f = 1.0;  /* Protect against no polar data */
 C = geodet_aux_C( one_minus_f, glat );
 S = ( one_minus_f * one_minus_f ) * C;
 *re  = ( world->radius * C + h ) * dcosd( glat );
 *z   = ( world->radius * S + h ) * dsind( glat );

}

double geod_to_geoc_height( World world, double h, double glat, double* latp )
{
 double re;
 double hc;
 double z;
 double r;

 geod_calc_re_z( world, h, glat, &re, &z );
 *latp = utn_atan_d( re, z );  
 r   = sqrt( re * re + z * z );
 hc  = r - world->radius;
 return hc;
}


double geod_to_geoc_lat( World world, double h, double glat )
{
 double S, C;
 double re;
 double lat;
 double z;
 double one_minus_f = world->polar_radius / world->radius;
 if ( one_minus_f == 0.0 ) one_minus_f = 1.0;  /* Protect against no polar data */
 C = geodet_aux_C( one_minus_f, glat );
 S = ( one_minus_f * one_minus_f ) * C;
 re  = ( world->radius * C + h ) * dcosd( glat );
 z   = ( world->radius * S + h ) * dsind( glat );
 lat = utn_atan_d( re, z );  
 return lat;
}


/* Convert from geocentric to geodetic latitude.
 * This depends on the height: but note this is the GEOCENTRIC height!
 */
double geoc_to_geod_lat( World world, double lat, double height )
{
 double re, z;
 double phi;
 z  = ( world->radius + height ) * dsind( lat );
 re = ( world->radius + height ) * dcosd( lat );
 phi = geoc_to_geod_lat_iter( world, lat, re, z );
 return phi;
}


double geoc_to_geod_height( World world, double height, double lat, double* glatp )
{
 double h, C;
 double re;
 double one_minus_f = world->polar_radius / world->radius;
 double phi;
 double z;
 if ( one_minus_f == 0.0 ) one_minus_f = 1.0;  /* Protect against no polar data */
 z  = ( world->radius + height ) * dsind( lat );
 re = ( world->radius + height ) * dcosd( lat );
 phi = geoc_to_geod_lat_iter( world, lat, re, z );
 C = geodet_aux_C( one_minus_f, phi );
 h = re / dcosd( phi ) - C * world->radius;
 *glatp = phi;
 return h;

}



double efg_to_geod( World world, double* efg, double* glatp, double* lonp )
{
 double C;
 double lon = utn_argd_d( efg[0], efg[1] );
 double phi, phi0;
 double re = sqrt( efg[0] * efg[0] + efg[1] * efg[1] ); 
 double z = efg[2];
 double h;
 double one_minus_f = world->polar_radius / world->radius;
 if ( one_minus_f == 0.0 ) one_minus_f = 1.0;  /* Protect against no polar data */ 
 phi0 = utn_atan_d( re, z );  /* Geocentric lat */
 phi = geoc_to_geod_lat_iter( world, phi0, re, z );
 C = geodet_aux_C( one_minus_f, phi );
 h = re / dcosd( phi ) - C * world->radius;
 *lonp  = lon;
 *glatp = phi;
 return h;
}

/* Give geocentric lat with eq radius and z */
double geoc_to_geod_lat_iter( World world, double phi0, double r, double z )
{
 double e = geod_e( world );
 double phi;
 double g, g2, c, k, z1;
 double phi_old; 
 double tol = 0.0001;  /* lt 1 arcsec */
 integer iter = 0;
 phi_old = 0;
 phi = phi0;
 while( fabs( phi - phi_old ) > tol )
 {
  iter++;
  phi_old = phi;
  g = e * dsind( phi );  
  g2 = 1.0 - g * g;
  if ( g2 < 0.0 ) g2 = 0.0;
  c = 1.0 / sqrt ( g2 );
  k = c * e * e * dsind( phi );
  z1 = z + k * world->radius;
  phi = utn_atan_d( r,  z1 );
 }
/* Phi is converged */
printf( "NITER = %ld\n", iter );
 return phi;
}


double geod_e( World world )
{
 double g = world->polar_radius / world->radius;
 double e2, e;
 if( g == 0.0 ) g = 1.0;  /* Protect against no polar data */
 e2 = 1.0 - g * g;
 if ( e2 < 0.0 ) e2 = 0.0;
 e = sqrt( e2 );
 return e;
}

/* Borkowski 1987, ApSpSci 139, 1 */
/* This could replace geoc_to_geod_height. It uses an exact formula quartic solution
 * instead of an iterative approach and might work better in extreme cases of b/a, h/a etc.
 */
double geod_calc( World world, double height, double ilat, double* glatp )
{
 double re, z;
 double E, F, G, f1, t, phi, h;
 double a,b;
 double theta;
 double P, Q, D;
 double D2;
 double v1, v2, v, P2;
 double lat;
 integer q = 1;
 lat = ilat;
 if ( lat < 0.0 )
 {
  q = -1;
  lat = -ilat;
 }

 z  = ( world->radius + height ) * dsind( lat );
 re = ( world->radius + height ) * dcosd( lat );

 a = world->radius;
 b = world->polar_radius;
 E = ( b * z - ( a*a - b*b )) / ( a * re );
 F = ( b * z + ( a*a - b*b )) / ( a * re );
 P = ( 4.0 / 3.0 ) * ( E * F + 1.0 );
 Q = 2.0 * ( E * E - F * F );
 D = P * P * P + Q * Q;
 if ( D < 0.0 )
 {
  P2 = sqrt( - P );
  D2 = P2 * P;
  v1 = - Q / D2;
  theta = dacosd( v1 ) / 3.0;
  v = 2 * P2 * dcosd( theta );
 } else {
  D2 = sqrt( D );
  v1 = Q + D2;
  v2 = Q - D2;
  v = - cube_root( v1 ) - cube_root( v2 );
 }
 G = ( sqrt( E*E+v ) + E ) / 2.0;
 f1 = G * G + ( F - v * G ) / ( 2 * G - E );
 t = sqrt( f1 ) - G;
 phi = utn_atan_d( 2 * b * t, a * ( 1 - t * t ) );
 h = ( re - a  * t ) * dcosd( phi ) + ( z - b ) * dsind( phi );
 *glatp = q * phi;
 return h;
}

double cube_root( double x )
{
 double c;
 double t = 1.0 / 3.0;
 if ( x < 0.0 ) /* cube root of -1 is -1  */
  c = - pow( -x, t );
 else
  c = pow( x, t );
 return c;
}
