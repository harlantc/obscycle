/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
void ast_earth_strato_refindex( double r0, double r, double T0, double rate,
    double dn0, double* dn, double* rdndr );

EarthAtmosphere ast_atm_init( void )
{
 EarthAtmosphere atm = calloc( 1, sizeof( struct EarthAtmosphere_s ));
 ast_atm_set_conditions( atm, AST_ATM_STD_TEMP, AST_ATM_STD_PRESSURE, 0.0 );
 ast_atm_set_lapse( atm, AST_TROPO_LAPSE );
 ast_atm_set_location( atm, 0.0, 0.0, 0.0 );
 ast_atm_set_refract_tol( atm, AST_REFRACT_TOL );
 ast_atm_eval_coeffs( atm, 0.5 );
 return atm;
}

void ast_atm_free( EarthAtmosphere atm )
{
 if ( !atm ) return;
 free( atm );
}

void ast_atm_set_conditions( EarthAtmosphere atm, double temp, double pressure,
 double humidity )
{ 
 if ( !atm ) return;
 atm->temp = temp;
 atm->pressure = utn_range_d( pressure, 0.0, 10000.0 );
 atm->humidity = utn_range_d( humidity, 0.0, 1.0 );
}

void ast_atm_set_lapse( EarthAtmosphere atm, double lapse0 )
{
 double lapse;  /* Lapse constrained to safe range in K/km */
 if ( !atm ) return;
 lapse    = utn_range_d( lapse0, 1.0, 10.0 );
 atm->lapse = lapse;
}

void ast_atm_set_tropo_index( EarthAtmosphere atm, double dnt )
{
 if ( !atm ) return;
 atm->dnt = dnt;
}

void ast_atm_set_refract_tol( EarthAtmosphere atm, double tol )
{
 if ( !atm ) return;
 atm->rtol = tol;
}


void ast_atm_set_location( EarthAtmosphere atm, double lon, double lat, double height )
{
 if ( !atm ) return;
 atm->lon = lon;
 atm->lat = lat;
 atm->height = height;
 atm->g = ast_earth_g( lat, height ); 
}

/* Probably accel due to gravity?  in m/s**2 */
/* Lat in deg, height in km */
double ast_earth_g( double lat, double height )
{
 double gb;
 gb = 9.784 * ( 1.0 - 0.026 * dcosd( 2.0 * lat ) - 2.8e-4 * height );
 return gb;
}

void ast_atm_eval_rates( EarthAtmosphere atm )
{
 double gamma, gamal;
 gamal = AST_KILO * atm->g * AST_MWT_AIR / AST_GAS_CONST;
 gamma = gamal / ( atm->lapse * AST_MEGA );

 atm->rates[0] = atm->lapse;
 atm->rates[1] = gamma - 2.0;
 atm->rates[2] = AST_DPDT_EXP - 2.0;
 atm->rates[3] = gamal;

}

void ast_atm_eval_coeffs( EarthAtmosphere atm, double mu )
{
 if ( !atm ) return;
 atm->wavelength = mu;

 ast_atm_eval_rates( atm );
 ast_atm_tropo_coeffs( mu, atm->temp, atm->pressure, atm->humidity, atm->lapse,
                       atm->g, atm->tcoeffs );

}



void ast_atm_tropo_coeffs( double mu, double temp, double pressure, double humidity,
 double lapse,  double g, double* c )
{
 const double AST_REF_C4CO_RADIO = 0.0;
 const double AST_REF_C4CO_OPT   = 0.371897;
 const double AST_REF_C1CO_RADIO = 12.92;
 const double AST_REF_C1CO_OPT   = 11.2684;

 double ast_ref_c1co;
 double ast_ref_c4co;
 double pwo;
 double pw;
 double delta = AST_DPDT_EXP;
 double alpha = lapse;  /* K/m */
 double gamma;
 double gamal;  /* K/m? */  /* Means gas const is    J/K/kg */
 double a;

 a = ast_earth_tropo_ref1( mu );
 pwo = ast_earth_tropo_pwo( temp, pressure, humidity );
 gamal = g * AST_MWT_AIR / AST_GAS_CONST;
 gamma = gamal / ( AST_KILO * lapse );
 pw = pwo * ( 1.0 - AST_MWT_H2O / AST_MWT_AIR ) * gamma / ( delta - gamma );
 ast_ref_c1co = ( mu >= AST_MU_FARIR ) ?
                AST_REF_C1CO_RADIO * AST_ATM_STD_PRESSURE / AST_ATM_STD_TEMP : 
                AST_REF_C1CO_OPT * AST_ATM_STD_PRESSURE / AST_ATM_STD_TEMP;
 ast_ref_c4co = ( mu >= AST_MU_FARIR ) ? AST_REF_C4CO_RADIO : AST_REF_C4CO_OPT;

 c[0] =  ( a *  ( pressure + pw ) / AST_ATM_STD_PRESSURE ) * ( AST_ATM_STD_TEMP / temp );
 c[1] =( ( a * pw + ast_ref_c1co * pwo ) / AST_ATM_STD_PRESSURE ) * (AST_ATM_STD_TEMP / temp );
 c[2] = ( gamma - 1.0 ) * alpha * c[0] / temp;
 c[3] = ( delta - 1.0 ) * alpha * c[1] / temp;
 c[4] = ast_ref_c4co * pwo / temp;  /* K */
 c[5] = c[4] * ( delta - 2 ) * alpha / ( temp * temp );   /* 1/km */

}


/* Saturation vapor pressure of water */
double ast_earth_tropo_psat( double temp, double pressure )
{
 double log_psat;
 double pterm;
 double psat;
 double tx;
 double tc = temp - AST_ZERO_CELSIUS;
 double pterm0 = 0.7859;
 double pscale = 222000.0; /* mbar */
 double tc0 = 86.602;
 double tc1 = 242.72;
 double tc2 = 22.60;

/* Needs pressure in millibar */

 tx = tc / tc0;
 pterm =  ( 1.0 + ( pressure / pscale ) * ( 1 + tx * tx ));
 log_psat = pterm0 * pterm * ( 1 + tc / tc2 ) / ( 1.0 + tc / tc1 );
 psat = pow( 10.0, log_psat );
 return psat;
}

/* Water vapor pressure */
double ast_earth_tropo_pwo( double T, double p, double humidity )
{ 
 double psat;
 double pco;
 double pwo = 0.0;

 if ( p > 0.0 )
 {
  psat = ast_earth_tropo_psat( T, p );
  pco = ( 1.0 - humidity ) * psat / p;
  pwo = humidity * psat / ( 1.0 - pco );
 }
 return pwo;
}



double ast_earth_tropo_ref3( double mu, double T )
{
 double b;
 if ( mu >= AST_MU_FARIR )
 {
  b = 12.92e-6 - 0.371897/T;
 } else {
  b = 11.2684e-6;
 }
 return b;
}

/* From refcoq.f, in some other units */

double ast_earth_tropo_ref2( double mu )
{
 double acoeff[3] = { 0.092e-6, 4.391e-7, 3.57e-9 };
 double a0 = 77.624e-6;
 double inv_lamsq;
 double mu_uv = 0.1;
 double a;

 a = AST_REFCO_ZERO;
 a = a0;

 if ( mu <= AST_MU_FARIR )
 {
  if ( mu >= mu_uv )
   inv_lamsq = 1.0 / (mu * mu);
  else
   inv_lamsq = 100.0;

  a  += poly_d( inv_lamsq, acoeff, 2 );
 }  
 return a;
}
/*
 *  Refractive index correction as a function of wavelength.
 * A quadratic in 1/lambda squared, constant above 100 microns and below 1000A.
 */
double ast_earth_tropo_ref1( double mu )
{
 double inv_lamsq;
 double mu_uv = 0.1;
 double a;
 double acoeff[3] = { -0.337, 1.6288, 0.0136 };

 a = AST_REFCO_ZERO;
 if ( mu <= AST_MU_FARIR )
 {
  if ( mu >= mu_uv )
   inv_lamsq = 1.0 / (mu * mu);
  else
   inv_lamsq = 100.0;

  a  += poly_d( inv_lamsq, acoeff, 2 );
 }  
 return a;
}    




void ast_earth_strato_refindex( double r0, double r, double T0, double rate,
    double dn0, double* dn, double* rdndr )
{
 double rh;
 double w;
 
 rh =  T0 / rate;
 w = ( dn0 - 1.0 ) * exp( - ( r - r0 ) / rh);

 *dn =    1.0 + w;
 *rdndr = - ( r / rh ) * w;
}

/*
 *  Given temp0 (at r0), rates and coeffs,
 *  find temp (at r), dn (refractive index at r) and  r * dn/dr.
 */ 
 
void ast_earth_atm_refindex( EarthAtmosphere atm, double r0, double r, 
                               double T0, double* Tp, double* dn, double* rdndr )
{
  double T;
  double lapse;
  double tbounds[2] = { 100.0, 320.0 };
  double ratio1;
  double ratio2;
  double term1, term2;

  *Tp = T0;
  *dn = 0.0;
  *rdndr = 0.0;
  if ( !atm ) return;

  if ( r0 >= AST_R_EARTH + 0.99 * AST_TROPOPAUSE )
  {  
   *Tp = T0;
   ast_earth_strato_refindex( r0, r, T0, atm->rates[3], atm->dnt, dn, rdndr ); 
  } else {
   lapse = atm->rates[0];
   T = T0 - lapse * ( r - r0 );
   T = utn_range_d( T, tbounds[0], tbounds[1] );
   ratio1 = pow( T / T0, atm->rates[1] );
   ratio2 = pow( T / T0, atm->rates[2] );
   term1 =   atm->tcoeffs[0] * ratio1 - ( atm->tcoeffs[1] - atm->tcoeffs[4] / T ) * ratio2;
   term2 =  -atm->tcoeffs[2] * ratio1 + ( atm->tcoeffs[3] - atm->tcoeffs[5] * (T0 / T ) ) * ratio2;

   *Tp    = T;
   *dn    = 1.0 + term1 * ( T / T0 );
   *rdndr = r * term2;
  }
}



/* Ratio of scale height to geocentric distance */
double ast_atm_scale_height( double temp )
{
 /* 4.4474e-6 * temp */
 double beta;
 beta = AST_ATM_DH_DT * temp;
 return beta;
}

/* Temperature K vs height km, NASA-GRC model */
/* Modified coeff 6.49 to 6.50 for matching */
double ast_earth_atm_temp_grc( double h )
{
 double T0 = 273.15;
 double T;
 if ( h < 11.0 )
 {
  T = 15.04 - 6.50 * h;
 }
 else if ( h < 25.0 )
 {
  T = -56.46;
 } else {
  T = -131.21 + 2.99 * h;
 }
 return T + T0;
}

/* Atmospheric pressure, kPa */
double ast_earth_atm_pressure_grc( double h )
{
 double T;
 double p;
 double x;
 if ( h < 11.0 )
 {
  T = ast_earth_atm_temp_grc( h );
  x = T / 288.08;
  p = 101.29 * pow( x, 5.256 );
 } else if ( h < 25.0 ) {
  x = 1.73 - 0.157 * h;
  p = 22.65 * exp( x ); 
 } else {
  T = ast_earth_atm_temp_grc( h );
  x = T / 216.60;
  p = 2.488 * pow( x, -11.388 );
 }
 return p;
}

/* Density in kg m^-3 given p in kPa and T in K */

double ast_earth_atm_density_grc( double T, double p )
{
 double rho;
 rho = p / ( 0.2869 * T );
 return rho;
}

/* Return sound speed in km/s */

double ast_earth_atm_cs_grc( double h )
{
 double cs;
 double k = 1.4;
 double p;
 double T;
 double rho;
 double kilo = 1000.0;
 T = ast_earth_atm_temp_grc( h );
 p = ast_earth_atm_pressure_grc( h );
 rho = ast_earth_atm_density_grc( T, p );
/* rho is in kg m-3
 * p in Pa= kg m s-2 *  m -2 = kg m-1 s-2
 * p / rho = ( m /s ) **2
 */
 cs = sqrt( k * kilo * p / rho ) / kilo;
 return cs;
}

