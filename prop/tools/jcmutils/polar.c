/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

static double polar_ang_d( const double*, const double );



double utn_dacosd( const double x )
{
 if ( x >= 1.0 && x < 1.01 ) return 0.0;  /* Handle case of 1.00001 */
 return( acos( x )/UT_DEG_RAD );
}

static double polar_ang_d( const double* p, const double r )
{
 double x;
 double re;
/* Make sure you get an accurate off axis angle. If the off axis
   angle is small, safer to take the size of the normal component.
 */
 x = acos( p[2] / r );
 if ( fabs( x ) < 0.001 ) {
  re = sqrt( p[0] * p[0] + p[1] * p[1] );
  x  = asin( re / r );
 }
 return( x/UT_DEG_RAD );
}

double utn_dasind( const double x )
{
 if ( x >= 1.0 && x < 1.01 ) return 90.0;  /* Handle case of 1.00001 */
 return( asin( x )/UT_DEG_RAD );
}

float utn_racosd( const float x )
{ 
 float utr;
 utr =   (float)acos( (double)x );
 return( utr/UT_DEG_RAD );
}

float utn_rasind( const float x )
{
 float utr;
 utr = (float) asin( (double)x );
 return (utr/UT_DEG_RAD);
}


double utn_dcosd( const double x )
{
 return( cos( UT_DEG_RAD * x  ));
}

double utn_dsind( const double x )
{
 return( sin( UT_DEG_RAD * x  ));
}

float utn_rcosd( const float x )
{
 return( (float)cos( UT_DEG_RAD * (double)x  ));
}

float utn_rsind( const float x )
{
 return( (float)sin( UT_DEG_RAD * (double)x ) ) ;
}

void utn_ar_vec_d( const double r, const double theta, const double phi, double* p )
{
 if ( theta == 0.0 || theta == 180.0 ) {
  p[0] = 0.0;
  p[1] = 0.0;
 } else {
  p[0] = r * dcosd( phi ) * dsind( theta );
  p[1] = r * dsind( phi ) * dsind( theta );
 }
 p[2] = r * dcosd( theta );
}

void utn_ar_vec_r( const float r, const float theta, const float phi, float* p )
{
 if ( theta == 0.0 || theta == 180.0 ) {
  p[0] = 0.0;
  p[1] = 0.0;
 } else {
  p[0] = r * rcosd( phi ) * rsind( theta );
  p[1] = r * rsind( phi ) * rsind( theta );
 }
 p[2] = r * rcosd( theta );
}

void utn_ar_polar_d( const double* p, double* r, double* theta, double* phi )
{
 *r = utn_ar_vnorm_d( p );
 if ( *r > 0 ) {
  *phi = argd_d( p[0], p[1] );
  *theta = polar_ang_d( p, *r );
 } else { 
  *phi =0.0;
  *theta = 0.0;
 }
}

void utn_ar_polar_r( const float* p, float* r, float* theta, float* phi )
{
 *r = utn_ar_vnorm_r( p );
 if ( *r > 0.0 ) {
  *phi = argd_r( p[0], p[1] );
  *theta = racosd( p[2]/(*r) );
 } else { 
  *phi =0.0;
  *theta = 0.0;
 }
}


double utn_ar_polarll_d( const double* p, double* llong, double* lat )
{
 double r;
 r = utn_ar_vnorm_d( p );
 if ( r > 0.0 ) {
  *llong = argd_d( p[0], p[1] );
  *lat  = utn_constants.d.polar_lat - polar_ang_d( p, r );
 } else {
  *llong = 0.0;
  *lat = utn_constants.d.polar_lat;
 }
 return( r );
}

float utn_ar_polarll_r( const float* p, float* llong, float* lat )
{
 float r;
 r = utn_ar_vnorm_r( p );
 if ( r > 0.0 ) {
  *llong = argd_r( p[0], p[1] );
  *lat  = utn_constants.r.polar_lat - racosd( p[2]/r );
 } else {
  *llong = 0.0;
  *lat = utn_constants.r.polar_lat;
 }
 return( r );
}


void utn_ar_unitll_d( const double llong, const double lat, double* p )
{
 if ( fabs( lat ) == utn_constants.d.polar_lat ) {
  p[0] = 0.0;
  p[1] = 0.0;
 } else {
  p[0] = dcosd( llong ) * dcosd( lat );
  p[1] = dsind( llong ) * dcosd( lat );
 }
 p[2] = dsind( lat );
}

void utn_ar_unitll_r( const float llong, const float lat, float* p )
{
 if ( rabs( lat ) == utn_constants.r.polar_lat ) {
  p[0] = 0.0;
  p[1] = 0.0;
 } else {
  p[0] = rcosd( llong ) * rcosd( lat );
  p[1] = rsind( llong ) * rcosd( lat );
 }
 p[2] = rsind( lat );
}



void utn_ar_polar6_r( const float* p, float* r, float* theta, float* phi, float* rrdot, float* tdot, float* phidot )
{
 double dr, dtheta, dphi, drdot, dtdot, dphidot;
 double dp[6];
 long i;
 for ( i = 0; i < 6; i++ )
  dp[i] = p[i];

 utn_ar_polar6_d( dp, &dr, &dtheta, &dphi, &drdot, &dtdot, &dphidot );
 *r = (float)dr;
 *theta = (float)dtheta;
 *phi = (float)dphi;
 *rrdot = (float) drdot;
 *tdot = (float)  dtdot;
 *phidot= (float) dphidot;
}

void utn_ar_polar6_d( const double* p, double* r, double* theta, double* phi, double* rrdot, double* tdot, double* phidot )
{
 double e[3];
 double de[3];
 double enorm;
 double ec[3];
 double r0;

 utn_ar_polar_d( p, &r0, theta, phi );
 *r = r0;
 if ( r0 > 0.0 ) {
  utn_ar_vcopy_d( p, e );
  utn_ar_vcopy_d( p+3, de );
  *rrdot = utn_ar_vdot_d( e, de ) / r0;
  e[2] = 0.0;
  de[2] = 0.0;
  enorm = utn_ar_vnorm_d( e );
  utn_ar_vcross_d( e, de, ec );
  if ( enorm > 0.0 ) {
   *tdot = ( ( p[2] * utn_ar_vdot_d( e, de ) - p[5] * ( enorm * enorm ) )
                            / ( enorm * r0 * r0 ) ) / UT_DEG_RAD;
   *phidot = (  ec[2] / ( enorm * enorm ) ) / UT_DEG_RAD;
  } else {
   *tdot = ( utn_ar_vnorm_d( de ) / r0 ) / UT_DEG_RAD;
   if ( *theta > 90.0 ) *tdot *= -1.0;
   *phidot = 0;
  }
 } else {
  *rrdot = 0;
  *tdot = 0;
  *phidot = 0;
 }
}

void utn_ar_vec6_r( const float r, const float theta, const float phi, const float rrdot, const float tdot, const float phidot, float* p )
{
 double dp[6];
 long i;
 utn_ar_vec6_d( (double)r, (double)theta, (double)phi, (double)rrdot, (double)tdot, (double)phidot, dp );
 for ( i = 0; i < 6; i++ )
  p[i] = dp[i];
}


void utn_ar_vec6_d( const double r, const double theta, const double phi, const double rrdot, const double tdot, const double phidot, double* p )
{
 double tdotr, phidotr;
 double ca, sa, cb, sb;
 double w;

 utn_ar_vec_d( r, theta, phi, p );
 tdotr = UT_DEG_RAD * tdot;
 phidotr = UT_DEG_RAD * phidot;
 ca = dcosd( phi );
 sa = dsind( phi );
 cb = dcosd( theta );
 sb = dsind( theta );
 w = r * tdotr * cb + rrdot * sb;
 p[3] = -p[1] * phidotr      + w * ca;
 p[4] =  p[0] * phidotr      + w * sa;
 p[5] = -r    * tdotr   * sb + rrdot * cb;
}

float utn_spharc_r( const float a1, const float b1, const float a2, const float b2 )
{
 return( (float)utn_spharc_d( (double)a1, (double)b1, (double)a2, (double)b2 ) );
}

double utn_spharc_d( const double a1, const double b1, const double a2, const double b2 )
{
 double v1[3];
 double v2[3];
 double w;
 double dw;
 double x, y;
 long  i;

 utn_ar_unitll_d( a1, b1, v1 );
 utn_ar_unitll_d( a2, b2, v2 );
 w = 0.0;
 for ( i = 0; i < 3; i++ ) {
  dw = ( v1[i] - v2[i] ) / 2.0;
  w += dw * dw;
 }
 x = w < 1.0 ? sqrt( 1.0 - w ) : 0;
 y = sqrt( w );
 return( 2.0 * utn_argd_d( x, y ) );
}
