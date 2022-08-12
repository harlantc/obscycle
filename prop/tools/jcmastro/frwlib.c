/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
double frw_development_angle_z( Cosmology* c, double z );

double frw_hubble_dist( Cosmology* c, double z )
{
/* The low redshift approximation: classic Hubble law */
 return z;
}

double frw_conformal_time( Cosmology* c, double z )
{
 double arc;
 double a;
 double zero = 0.0;
 double da;
 double eta;
 if ( z == 0.0 )
 {
/* Efficient precomputed case */
  if ( c->eta0 >= 0.0 )
     return c->eta0;
 }

 if ( c->mode == COS_MODE_GEN )
 {
  double omega_m;
  a = 1.0 / ( 1.0 + z );
  omega_m = c->omega - c->lambda - c->omega_r;
  da = c->da;
  eta = frw_eta_integral( c->k, omega_m, c->omega_r, c->lambda, zero, a, da );  
  return eta;
 }
 else if ( c->mode == COS_MODE_PEN )
 {
  double omega_m;
  omega_m = c->omega - c->lambda;
  return frw_eta_pen( omega_m, z );
 } else if ( c->mode == COS_MODE_PEN1 ) {
  double omega_m;
  omega_m = c->omega - c->lambda;
  return frw_eta_pen1( omega_m, z );

 }

 if ( z == 0.0 )
 {
/* Efficient precomputed case */
  if ( c->k == 0 ) 
     return 2.0;
  if ( c->omega == 0.0 )  
     return 0.0;
 }

 a = 1.0 / ( 1.0 + z );
 if ( c->k == 0 )
  arc = 2.0 * sqrt( a );
 else if ( c->omega == 0.0 ) 
  arc = log( a );
 else
  arc = c->chi0 * UT_DEG_RAD * frw_development_angle_z( c,  z );

 return arc;
}


double frw_coord_dist( Cosmology* c, double z )
{
 double d;
 double chi;
/* RW Coordinate distance */
/* For the simple FRW case we used optimized formulae
 * for the eta0-eta subtraction. For other methods we do the 
 * subtraction explicitly.
 */
 if ( c->mode != COS_MODE_FRW )
 {
  chi = frw_proper_dist( c, z );
  d = frw_proper_to_coord( c, chi );
  return d;
 }
 if ( z < 1.0E-3 )
  d = z;
 else if ( c->omega == 0.0 )
  d = z * ( 1.0 + 0.5 * z ) / ( 1.0 + z );
 else if ( c->omega == 1.0 )
  d = 2.0 * ( 1.0 - 1.0 / sqrt( 1.0 + z ) );
 else
 {
  double f;
  f = sqrt( 1.0 + c->omega * z );
  d = 2.0 * ( c->omega * z + ( 2.0 - c->omega ) * (1.0 - f) )/
                ( c->omega * c->omega * (1.0 + z ) );
 }
 return d;
}


double frw_ldist( Cosmology* c, double z )
{
 double d;
 d = frw_coord_dist( c, z ) * ( 1.0 + z );
 return d;
}

double frw_angdist( Cosmology* c, double z )
{
 double d;

 d = frw_coord_dist( c, z ) / ( 1.0 + z );
 return d;
}

double frw_volume( Cosmology* c, double z )
{
 const double fpi = 4 * M_PI;
 double vol;
 double v;
 double a2;
 double a;

 a = 1.0 / ( 1.0 + z );

 if ( c->omega == 0.0 ) 
 {
   a2 = a * a;
   v = (1.0 - a2 *a2 )/( 8.0 * a2 ) + 0.5 * log( a );
 } 
 else if ( c->k == 0 ) 
 {
   v = 4 * TWO_THIRDS * pow( ( 1.0 - sqrt( a )), 3.0 );
 }
 else if ( c->k == -1 )
 {  
   double c2, c3, om1, om2, h, f, g, v1, v2, v3;

   c2 = c->chi0 * c->chi0;
   c3 = c2 * c->chi0;
   om1 = c->omega * c2;
   om2 = c->omega * c->omega;
   h = sqrt( om1 + a );
   f = ( h + sqrt ( a ) ) / ( h - sqrt(a) );
   g = ( c->chi0 - 1.0 ) / ( c->chi0 + 1.0 );
   v1 = 0.5 * c3 * log( f * g );
   f = ( 2.0 - c->omega ) * c2 / om2;
   g = sqrt(a) * ( c->omega + 2 * a ) * h / ( c->chi0 * om2 );
   v2 = -g + f;
   f = 8.0 * sqrt( a ) * h / ( om2 * om2 * c3 );
   g = sqrt(a) * ( 2.0 - c->omega ) * h * c->chi0 - ( c->omega + 2.0 * a );
   v3 = f * g;
   v = v1 + v2 + v3;
  } else {
   double v1, v2, v3;
   double c2, om1, om2, f, g;
   c2 = c->chi0 * c->chi0;
   om1 = c->omega * c2;
   om2 = c->omega * c->omega;
   f = argd_d( c->chi0, 1.0 );
   g = argd_d( sqrt( om1 - a ), sqrt( a ) );
   v1 = c2 * c->chi0 * ( f - g );
   f = c2 * ( 2.0 - c->omega ) / om2;
   g = sqrt( a ) * ( om1 - 2.0 * a ) * sqrt( om1 - a ) / ( c->chi0 * om2 );
   v2 = g - f;
   f = 8.0 * sqrt( a ) * sqrt( om1-a) / ( om2 * om2 * c2 * c->chi0 );
   g = sqrt( a ) * ( 2.0 - c->omega ) * sqrt( om1 - a ) * c->chi0 - ( om1 - 2.0 * a );
   v3 = f * g;
   v = v1 + v2 + v3;
 }
 vol = fpi * v;
 return vol;
}


double frw_development_angle_z( Cosmology* c, double z )
{
	double f;
	double x,y;
	double phi = 0.0;
	double a;

	a = 1.0 / ( 1.0 + z);

	if ( c->k == 0 || c->omega == 0.0 )
        {
          phi = 0.0;
        } else {

         f = c->omega * c->chi0 * c->chi0;
         if ( c->k == -1 ) {
          x  = sqrt( a ) + sqrt( a + f );
          y  = x * x / f;
          phi = log( y ) / UT_DEG_RAD;
         } else if ( c->k == 1 ) {
          y = 2.0 * sqrt( a ) * sqrt( f - a );
          x = f - 2.0 * a;
          phi = argd_d( x, y );
         }
	}
 return phi;
}


double frw_proper_to_coord( Cosmology* c, double chi )
{
 double r = 0.0;
/* C 	 RW Coordinate distance as a function of radial proper coordinate (Eq 1.4) */
 if ( c->k == 0 )
 {
  r = chi;
 } else if ( c->k == 1 ) {
  r = chi * sin( chi/c->chi0 );
 } else if ( c->k == -1 ) {
  r = chi * esinh( chi / c->chi0 );
 }
 return r;
}

double frw_cosmic_time( Cosmology* c, double z )
{

 double phi_min = FRW_MIN_DEVEL_ANGLE;
 double texp;
 double phi;
 double a;

 a = 1.0 / ( 1.0 + z );
/* Precomputed for a=1? */
 if ( a == 1.0 && c->eta0 >= 0.0 )
  return c->t0; 

 if ( c->mode == COS_MODE_GEN )
 {
  double omega_m;
  omega_m = c->omega - c->lambda - c->omega_r;
  return frw_t_integral( c->k, omega_m, c->omega_r, c->lambda, 0.0, a, c->da );
 }
 else if ( c->mode == COS_MODE_PEN1 || c->mode == COS_MODE_PEN )
 {
  double omega_m;
  omega_m = c->omega - c->lambda;
  return frw_t_pen1( omega_m, z );
 }

 if ( c->k == 0 ) 
 {
  texp = TWO_THIRDS * pow( a, 1.5 );
 } else if ( c->k == -1 ) {
  if ( c->omega == 0.0 )
  {
   texp = a;
  } else {
   phi = UT_DEG_RAD * frw_development_angle( c, z );
   if ( phi > phi_min ) 
    texp = 0.5 * c->omega * pow( c->chi0, 3.0 ) * ( esinh( phi) - phi );
   else
/* Omega=1 universe approximation */
    texp = TWO_THIRDS * sqrt(  pow( a, 3.0 ) / c->omega );
  }
 } else {
  phi = UT_DEG_RAD  * frw_development_angle( c, z );
  texp = 0.5 * c->omega * pow( c->chi0, 3.0 ) * ( phi - sin( phi ));
 }
 return texp;
}




/* 	 Redshift on past light cone */
double frw_t_to_z( Cosmology* c, double t )
{
 double a;

 a= frw_t_to_a( c, t );
 return frw_a_to_z( a );
}


double frw_z_to_omega( Cosmology* c, double z )
{
 return c->omega *( 1.0 +z ) / ( 1.0 + c->omega * z );
}


double frw_proper_dist( Cosmology* c, double z )
{
 double d;
 if ( z < 1.0E-5 ) 
   d = frw_hubble_dist( c, z );
 else
   d = frw_conformal_time( c, 0.0 ) - frw_conformal_time( c, z );
 return d;
}

