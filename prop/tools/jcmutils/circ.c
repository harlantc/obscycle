/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


float utn_radin_r( const float x ) 
{
 return( x * utn_constants.r.deg_rad );
}

float utn_radout_r( const float x )
{
 return( x / utn_constants.r.deg_rad );
}

double utn_radin_d( const double x ) 
{
 return( x * utn_constants.d.deg_rad );
}

double utn_radout_d( const double x )
{
 return( x / utn_constants.d.deg_rad );
}



double utn_circ_d( const double x )
{
 return( modulo_d( x, utn_constants.d.circle ) );
}


float utn_circ_r( const float x )
{
 return( modulo_r( x, utn_constants.r.circle ) );
}

float utn_circ_ew_r( const float x )
{
 float y;
 y = modulo_r( x, utn_constants.r.circle );
 if ( y >= utn_constants.r.circle/2 ) y -= utn_constants.r.circle;
 return( y );
}

double utn_circ_ew_d( const double x )
{
 double y;
 y = modulo_d( x, utn_constants.d.circle );
 if ( y >= utn_constants.d.circle/2 ) y -= utn_constants.d.circle;
 return( y );
}


double utn_circ_rad_d( const double x )
{
 return( modulo_d( x, utn_constants.d.twopi ) );
}


float utn_circ_rad_r( const float x )
{
 return( modulo_r( x, utn_constants.r.twopi )  );
}

float utn_circ_ew_rad_r( const float x )
{
 float y;
 y = modulo_r( x, utn_constants.r.twopi );
 if ( y >= M_PI ) y -= utn_constants.r.twopi;
 return( y );
}

double utn_circ_ew_rad_d( const double x )
{
 double y;
 y = modulo_d( x, utn_constants.d.twopi );
 if ( y >= M_PI ) y -= utn_constants.d.twopi;
 return( y );
}



float utn_argd_r( const float x, const float y )
{
 float a;
 if ( x == 0.0 && y == 0.0 ) {
  a = 0.0;
 } else {
  a = atan2( y, x ) / utn_constants.r.deg_rad;
  a = circ_r( a );
 }
 return( a );
}


double utn_argd_d( const double x, const double y )
{
 double a;
 if ( x == 0.0 && y == 0.0 ) {
  a = 0.0;
 } else {
  a = atan2( y, x ) / utn_constants.d.deg_rad;
  a = circ_d( a );
 }
 return( a );
}

/* Version returning -pi to pi */
double utn_atan_d( const double x, const double y )
{
 double a;
 if ( x == 0.0 && y == 0.0 ) {
  a = 0.0;
 } else {
  a = atan2( y, x ) / utn_constants.d.deg_rad;
 }
 return( a );
}



