/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
#include "orbit_types.h"

/*
 *  These derived routines don't use the KeplerPrimary object internals
 */

/*
 *  Convert from semimajor axis to mean motion in revs per day
 */
double kepler_mean_motion( World primary, double a )
{
 double period;
 period = kepler_period( primary, a );
 return DL_SEC_D / period;
}

double kepler_parabolic_mean_motion( World primary, double q )
{
 double period;
 period = kepler_parabolic_period( primary, q );
 return DL_SEC_D / period;
}

double kepler_parabolic_period( World primary, double q )
{
 double period;
/* Nominal period for parabola chosen to be sqrt( 32 q^3 pi^2 / GM )
 * Compare ellipse  sqrt ( 4 a^3 pi^2 / GM )
 * So equivalent to 2q = a 
 */
 period = sqrt( 2.0 ) * kepler_period( primary, q );
 return period;
}

/* Find periapsis from 'period parameter' */
double kepler_parabolic_three( World primary, double period )
{
 return kepler_three( primary, 0.5 * period );
}


void kepler_eval_pos( World primary, double a, double e, double inc, double node, double arg_peri, double true_anom, double* height, double* lat, double* ra )
{
 double lnode;
 double node_anom;
 double mean_anom;

 mean_anom = kepler_conic( true_anom, e );  /* Not used */
 node_anom = true_anom + arg_peri;
 lnode = argd_d( dcosd( node_anom ), dcosd( inc ) * dsind( node_anom ) );
 *height = kepler_height( primary, a, e, true_anom );
 *lat = dasind( dsind( node_anom ) * dsind( inc ) );
 *ra  = lnode + node;

}



/*
 *   Classify type of orbit
 */

void kepler_orbit_type( World primary, double period, double a, double e, double i, char* type )
{
 
 double perigee, apogee;
 double T;  /* Period in minutes */

 kepler_orbit_heights( primary, a, e, &perigee, &apogee );
 T = period / MINUTE;

 if ( utn_cs_eq( primary->name, "Earth" ) ) {
  if ( e >= 1.0 ) {
   utn_cs_copy( "EEO", type );
  } else if ( apogee >= CISLUNAR ) {
   utn_cs_copy( "CLO", type );
  } else if ( apogee <= SPACE_BOUNDARY ) {
   utn_cs_copy( "ATM", type );
  } else if ( perigee < 0.0 ) {
   utn_cs_copy( "SO", type );
  } else if ( perigee < SPACE_BOUNDARY ) {
   utn_cs_copy( "TA", type );
  } else if ( T < LEO_UPPER ) {
/* LEO */
   if ( i <= EQU_INC ) {
    utn_cs_copy( "LEO/E", type );
   } else if ( i < POLAR_INC ) {
    utn_cs_copy( "LEO/I", type );
   } else if ( i < SSO_INC ) {
    utn_cs_copy( "LEO/P", type );
   } else if ( i < RETRO_INC ) {
    utn_cs_copy( "LEO/S", type );
   } else {
    utn_cs_copy( "LEO/R", type );
   }
  } else if ( T < MEO_UPPER ) {
   if (       T >= MOL_LOW && T <= MOL_HIGH 
           && i >= MOL_LOW_INC && i <= MOL_HIGH_INC
           && e > ECC_THRESH ) {
    utn_cs_copy( "HEO/M", type );
   } else if ( e > ECC_THRESH ) {
    if ( T >= GTO_LOW && T <= MOL_HIGH ) {
     utn_cs_copy( "GTO", type );
    } else {
     utn_cs_copy( "HEO", type );
    }
   } else {
    utn_cs_copy( "MEO", type );
   }
  } else if ( T < GEO_UPPER ) {
   if ( T >= GEO_S_LOWER && T <= GEO_S_UPPER ) {
/* GEO strict */
    if ( e < GEO_S_ECC && i <= GEO_S_INC ) {
     utn_cs_copy( "GEO/S", type );
    } else if ( e < GEO_D_ECC && i <= EQU_INC ) {
     utn_cs_copy( "GEO/I", type ); 
    } else {
     utn_cs_copy( "GEO/T", type );
    }
   
   } else {
/* GEO band */
    if( e < GEO_D_ECC && i < GEO_S_INC ) {
     utn_cs_copy( "GEO/D", type );
    } else if ( e < GEO_D_ECC && i < EQU_INC ) {
     utn_cs_copy( "GEO/ID", type );
    } else {
     utn_cs_copy( "GEO/NS", type );
    }
   }
  } else {

   if ( e < ECC_THRESH ) {
    utn_cs_copy( "DSO", type );
   } else {
    utn_cs_copy( "DHEO", type );
   }   
  }
 } else if ( utn_cs_eq( primary->name, "Sun" ) ) {
  if ( e < 1.0 ) {
   utn_cs_copy( "HCO", type );
  } else {
   utn_cs_copy( "SSE", type );
  }
 } else if ( utn_cs_eq( primary->name, "Luna" ) ) {
  if ( e < 1.0 ) {
   utn_cs_copy( "LO", type );
  } else {
   utn_cs_copy( "PEO", type );
  }
 } else {
  if ( e < 1.0 ) {
   utn_cs_copy( "PCO", type );
  } else {
   utn_cs_copy( "PEO", type );
  }
 }

}

#if 0
void barker_demo( void )
{
 double M;
 double phi;
 double phi0;
 double E;  
 for ( phi0 = -180.0; phi0 < 180.9; phi0+= 1.0 )
 {
  E = utn_dsind( 0.5 * phi0 ) / utn_dcosd( 0.5* phi0 );
  M = E + E*E*E/3;
  phi = barker_solve( M * 180 / M_PI );
  printf( "%10.2f %20.5f %20.5f\n", M, phi, phi0 );
 }
}
#endif

/* Solve Barker's equation for the parabolic orbit */
double barker_solve( double M )
{
 double s;
 double theta;
 double c2s,s2s;
 double c3s,s3s;
 double c2t,s2t;
 double E;
 double phi;
 double Mr;
 if ( M == 0.0 )
  return 0.0;

 Mr = radin_d( M );
 s = utn_argd_d( 1.5 * Mr, 1.0 );
 c2s = utn_dcosd( 0.5 * s );
 s2s = utn_dsind( 0.5 * s );  
 c3s = pow( c2s, 1.0/3.0 );
 s3s = pow( s2s, 1.0/3.0 );
 theta = utn_argd_d( c3s, s3s );
 c2t = utn_dcosd( 2.0 * theta );
 s2t = utn_dsind( 2.0 * theta );
 E = 2 * c2t / s2t;
 phi = utn_argd_d( s2t, 2.0 * c2t );
 if ( phi > 180.0 ) phi = phi - 360.0;
 phi = 2.0 * phi;
 printf( "%10.3f %10.3f %10.3f %10.3f %10.3f E= %10.3f %10.3f %10.3f P= %10.3f : ", 
   M, s, c3s, s3s, theta, E, c2t, s2t, phi );
 return phi;
}
