/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
void frw_xset( Cosmology* c, double h0, double omega, double lambda, double omega_r );

void frw_set_mode( Cosmology* c, char* opt )
{
 if ( utn_cs_uceq( opt, "PEN" ) )
  c->mode = COS_MODE_PEN;
 else if ( utn_cs_uceq( opt, "PEN1" ))
  c->mode = COS_MODE_PEN1;
 else if ( utn_cs_uceq( opt, "GEN" ))
  c->mode = COS_MODE_GEN;
 else if ( utn_cs_uceq( opt, "FRW" ))
  c->mode = COS_MODE_FRW;
 frw_agecalc( c );
}

void frw_get_mode( Cosmology* c, char* opt )
{

 if ( c->mode == COS_MODE_PEN )
  utn_cs_copy( "PEN", opt );
 else if ( c->mode == COS_MODE_PEN1 )
  utn_cs_copy( "PEN1", opt );
 else if ( c->mode == COS_MODE_FRW )
  utn_cs_copy( "FRW", opt );
 else if ( c->mode == COS_MODE_GEN )
  utn_cs_copy( "GEN", opt );
 else
  utn_cs_copy( "-", opt );
}

void frw_free( Cosmology* c )
{
 free( c );
}

void frw_set_H0( Cosmology* c, double H0 )
{
 frw_xset( c, H0, c->omega, c->lambda, c->omega_r );
}

void frw_set_Omega( Cosmology* c, double Omega )
{
 frw_xset( c, c->h0, Omega, c->lambda, c->omega_r );
}

void frw_set_Lambda( Cosmology* c, double Lambda )
{
 frw_xset( c, c->h0, c->omega, Lambda, c->omega_r );
}

void frw_set_Omega_r( Cosmology* c, double omega_r )
{
 frw_xset( c, c->h0, c->omega, c->lambda, omega_r );
}

void frw_set_da( Cosmology* c, double da )
{
 c->da = da;
}

double frw_get_da( Cosmology* c )
{
 return c->da;
}

/* Standard WMAP cosmology */
Cosmology* frw_init_std( char* id )
{
 Cosmology* cosmology;
 double H0 = 71.0;
 double Omega = 1.0;
 double Lambda = 0.73;
 double Omega_r = 8.35e-5;

 if ( utn_cs_uceq( id, "WMAP" ))
 {
  /* For now, WMAP is only option */
 } 
 cosmology = frw_init( H0, Omega, Lambda );
 frw_set_Omega_r( cosmology, Omega_r );
 frw_set_mode( cosmology, "GEN" );
 return cosmology;
}

Cosmology* frw_init( double h0, double omega, double lambda )
{
 Cosmology* c;
 ast_init();
 c = calloc( 1, sizeof( Cosmology ));
 c->da = 1.0e-7;  /* Integration step size */
 frw_xset( c, h0, omega, lambda, 0.0 );
 return c;
}

void frw_set( Cosmology* c, double h0, double omega, double lambda )
{
 frw_xset( c, h0, omega, lambda, c->omega_r );
}

void frw_xset( Cosmology* c, double h0, double omega, double lambda, double omega_r )
{
 c->h0 = h0;
 c->omega = omega;
 c->lambda = lambda;
 c->th0 = 1.0;
 c->omega_r = omega_r;
 frw_agecalc( c );
}

double frw_get_H0( Cosmology* c )
{
 return c->h0;
}

double frw_get_Omega( Cosmology* c )
{
 return c->omega;
}
double frw_get_Lambda( Cosmology* c )
{
 return c->lambda;
}

double frw_get_Omega_r( Cosmology* c )
{
 return c->omega_r;
}


/* Given omega H0, calculate chi0 k t0 eta0 */

void frw_agecalc( Cosmology* c )
{

 if ( c->omega == 0.0 )
 {
  c->chi0 = 1.0; 
  c->k    = -1;
 } else if ( c->omega < 1.0 ) {
  c->chi0 = 1.0 / sqrt( 1.0 - c->omega );
  c->k = -1;
 } else if ( c->omega  == 1.0 ) {
  c->chi0 = 0.0; /* Really +Inf */
  c->k    = 0;
 } else {
  c->chi0 = 1.0 / sqrt(  c->omega - 1.0 );
  c->k = 1;
 }

 c->eta0 = -1; /* Force precompute caching of eta0 and t0 */
 c->t0 = frw_cosmic_time( c, 0.0 );
 c->eta0 = frw_conformal_time( c, 0.0 );
}



double frw_a_to_htime( Cosmology* c, double a )
{
 double ht;

 ht = pow( a, 1.5 ) / sqrt( c->omega + a * ( 1.0 - c->omega ));
 return ht;
}

double frw_a_to_omega( Cosmology* c, double a )
{
 return  c->omega / ( c->omega + a * ( 1.0 - c->omega ) );
}

double frw_hubble_param( Cosmology* c, double z )
{
 return c->h0 * ( 1.0 + z ) * sqrt( 1 + c->omega * z );
}



/*
 *   1 Mpc/(km/s) = 3,086E16+6-3 =3.086E19s
 */
double frw_hubble_scale( Cosmology* c, char* opt )
{
 double t;
 double scale;

 if ( utn_cs_eq( opt, "Myr" ))
  t =  AST_C * AST_PC / ( AST_LY * AST_KILO );
 else if ( utn_cs_eq( opt, "yr" )) 
  t =  AST_C * AST_PC / ( AST_LY * AST_MEGA * AST_KILO );
 else if ( utn_cs_eq( opt, "s" )) 
  t = AST_PC * AST_KILO;
 else if ( utn_cs_eq( opt, "Gyr" ))
  t =  AST_C * AST_PC /  AST_LY;
 else if ( utn_cs_eq( opt, "cm" ))
  t = AST_C * AST_PC * AST_MEGA / 10.0;
 else if ( utn_cs_eq( opt, "m" ))
  t = AST_C * AST_PC * AST_KILO;
 else if ( utn_cs_eq( opt, "pc" ))
  t = AST_C * AST_KILO;
 else if ( utn_cs_eq( opt, "kpc" ))
  t = AST_C;
 else if ( utn_cs_eq( opt, "Mpc" ))
  t = AST_C/ AST_KILO;
 else if ( utn_cs_eq( opt, "Gpc" ))
  t = AST_C/ AST_MEGA;
 else
  t = c->h0;

 scale = t / c->h0; 
 return scale;
}

void frw_get_unit( Cosmology* c, char* opt )
{
 utn_cs_copy( c->unit, opt );
}



/*  UNIT: LOG (W PER JY-HZ * sq m * 4 pi) */
double frw_log_lum_area( Cosmology* c, double z )
{
 double A;

 A = AST_LLUM + 2.0 * log10( frw_ldist( c, z ) / c->h0 );
 return A;
}

double frw_log_lnu( Cosmology* c, double Snu, double z )
{
 double f;
 f = Snu + frw_log_lum_area( c, z ) - log10( 1.0 + z );
 return f;
}

double frw_log_flux( Cosmology* c, double L, double z )
{
 double f;
 f = L - frw_log_lum_area( c, z );
 return f;
}


double frw_log_fnu( Cosmology* c, double L, double z )
{
 double f;
 f = L - frw_log_lum_area( c, z ) + log10( 1.0+z );
 return f; 
}


