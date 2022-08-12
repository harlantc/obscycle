/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

double frw_development_angle_rad( Cosmology* c, double a );

double frw_a_to_coord_dist( Cosmology* c, double a )
{
 return frw_coord_dist( c, frw_a_to_z( a ) );
}

double frw_a_to_dprop( Cosmology* c, double a )
{
 double d; 
 double z;
/* Proper distance to redshift z*/
 if ( 1.0 - a < 1.0e-3 )
 { 
  z = frw_a_to_z( a );
  d = frw_hubble_dist( c, z );
 }
 else 
  d = frw_a_to_arc( c, FRW_NOW ) - frw_a_to_arc( c, a );
 return d;
}

/* Compute arc parameter. Make case a=1 efficient. */

double frw_a_to_arc( Cosmology* c, double a )
{

 double z;
 if ( a == 0.0 )
  return 0.0;
 else
 {
   z = 1.0 / a - 1;
   return frw_conformal_time( c, z );
 }
}


double frw_t_to_a( Cosmology* c, double t )
{
 /* 
C 	 Calculates redshift on past light cone given T since singularity
  */

  double t1;
  double x;
  double phi;
  double a;
  double tfrw;
  t1 = t;
  if ( c->k == 0 ) 
  {
    a = pow( t1 / TWO_THIRDS, TWO_THIRDS );
  } else {
   if ( c->omega == 0.0 ) 
     a = t1;
   else
   {
     tfrw = 0.5 * c->omega * pow( c->chi0, 3 );
     x = t1 / tfrw;
     if ( x  < 5.0E-4 )
      a = pow( 1.5 * sqrt( c->omega ) * t1, TWO_THIRDS );
     else if ( c->k == -1 )
     {
       phi = acych( x );
       a = 0.5 * (c->omega / ( 1.0 - c->omega )) * (ecosh( phi ) - 1.0 );
     } else {
       phi = acyc( x );
       a = 0.5 * ( c->omega / ( c->omega - 1.0 )) * ( 1.0 - cos( phi ) );
     }
    }
   }
   return a;
}

double frw_a_to_cosmic_time( Cosmology* c, double a )
{

 double phi_min = FRW_MIN_DEVEL_ANGLE;
 double texp;
 double phi;

/* Precomputed for a=1? */
 if ( a == 1.0 && c->eta0 >= 0.0 )
  return c->t0; 

 if ( c->k == 0 ) 
 {
  texp = TWO_THIRDS * pow( a, 1.5 );
 } else if ( c->k == -1 ) {
  if ( c->omega == 0.0 )
  {
   texp = a;
  } else {
   phi = frw_development_angle_rad( c, a );
   if ( phi > phi_min ) 
    texp = 0.5 * c->omega * pow( c->chi0, 3.0 ) * ( esinh( phi) - phi );
   else
/* Omega=1 universe approximation */
    texp = TWO_THIRDS * sqrt(  pow( a, 3.0 ) / c->omega );
  }
 } else {
  phi = frw_development_angle_rad( c, a );
  texp = 0.5 * c->omega * pow( c->chi0, 3.0 ) * ( phi - sin( phi ));
 }
 return texp;
}



double frw_a_to_vol( Cosmology* c, double a )
{
 const double fpi = 4 * M_PI;
 double vol;
 double v;


 if ( a > 0.0 )
 {
  return frw_volume( c, 1.0 / a - 1.0 );
 }

/* Case a = 0 */
 if ( c->omega == 0.0 ) 
 {
   v = utn_infinity_d( 1 );
 } 
 else if ( c->k == 0 ) 
 {
   v = 4 * TWO_THIRDS;
 }
 else if ( c->k == -1 )
 {  
   double c3, g;
   c3 = c->chi0 * c->chi0 * c->chi0;
   g = ( c->chi0 - 1.0 ) / ( c->chi0 + 1.0 );
   v = 0.5 * c3 * log( g ) + 1;
 } else {
   double c3, f;
   c3 = c->chi0 * c->chi0 * c->chi0;
   f = argd_d( c->chi0, 1.0 );
   v = f * ( c3 - 1 );
 }
 vol = fpi * v;
 return vol;
}



double frw_development_angle( Cosmology* c, double a )
{
 double devr;
 devr = frw_development_angle_rad( c, a );
 return ( devr / UT_DEG_RAD );
}

double frw_development_angle_rad( Cosmology* c, double a )
{
	double f;
	double x,y;
	double phi = 0.0;

	if ( c->k == 0 || c->omega == 0.0 )
        {
          phi = 0.0;
        } else {

         f = c->omega * c->chi0 * c->chi0;
         if ( c->k == -1 ) {
          x  = sqrt( a ) + sqrt( a + f );
          y  = x * x / f;
          phi = log( y );
         } else if ( c->k == 1 ) {
          y = 2.0 * sqrt( a ) * sqrt( f - a );
          x = f - 2.0 * a;
          phi = UT_DEG_RAD * argd_d( x, y );
         }
	}
 return phi;
}


double frw_a_to_z( double a )
{
 double z = 1.0 / a - 1.0;
 return z;
}
double frw_z_to_a( double z )
{
 double a = 1.0 / ( 1.0 + z );
 return a;
}



