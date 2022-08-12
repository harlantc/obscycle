/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

double ast_earth_ecc( double t )
{
 double centuries;
 double ecoeff[3] = { 0.01673011, -0.00004193, -0.000000126 };
 double e;

 centuries = cal_jc( t ) - cal_jc( JD_B1950 );
 e = poly_d( centuries, ecoeff, 2 );
 return e; 
}

/* Mean longitude of perihelion from etrms.f */

double ast_earth_perihelion( double t )
{
 double centuries;
 double ecoeff[4] = { 1015489.951, 6190.67, 1.65, 0.012 };
 double lp;

 centuries = cal_jc( t ) - cal_jc( JD_B1950 );
 lp = poly_d( centuries, ecoeff, 3 ) / AST_SEC_PER_DEG;
 return lp; 

}

/* Return components of obliquity vector, from SLALIB el2ue.f and pv2el.f and planet.f */
void ast_obl_ecliptic_j2000_vector( double* obl )
{
 obl[0] = 0.0;
 obl[1] = AST_OBL_J2000_COS;
 obl[2] = AST_OBL_J2000_SIN;
}

/* Mean obliquity from SLALIB nutc.f, ecmat.f and dmoon.f (IAU 1976) */
double ast_obl_ecliptic( double t )
{
 double eps_coeff[4] = { 84381.448, -46.8150, -0.00059, 0.001813 };
 double centuries;
 double obliquity;
 double scale;
 centuries = cal_jc( t );
 obliquity = utn_poly_d( centuries, eps_coeff, 3 );
 scale = AST_SEC_PER_DEG;
 obliquity = obliquity / scale;
 return obliquity;
}

double ast_obl_ecliptic_alt( double t, integer method )
{
/* Mean obliquity from SLALIB nutc.f, ecmat.f and dmoon.f (IAU 1976) */
/* The std method is the same as the above routine */
 double eps_coeff_std[4] = { 84381.448, -46.8150, -0.00059, 0.001813 };
/* Mean obliquity from E-terms in SLALIB etrms.f */
 double eps_coeff_et[4]  = { 84404.836, -46.8495, -0.00319, -0.00181 };
/* Mean obliquity from earth pv in SLALIB earth.f; uses years since 1900 */
 double eps_coeff_ea[4]  = { 84428.259, -46.82, 0.0, 0.0 };
/* Mean obliquity from moon pv in SLALIB moon.f; uses years since 1900 */
 double eps_coeff_m[4]   = { 84428.244, -46.80, 0.0, 0.0 };
 double* eps_coeff = NULL;
 double centuries = 0.0;
 double obliquity;

 switch( method )
 {
  case AST_OBL_METHOD_STD:
   centuries = cal_jc( t );
   eps_coeff = eps_coeff_std;
   break;
  case AST_OBL_METHOD_ET:
   centuries = cal_jc( t ) - cal_jc( JD_B1950 );
   eps_coeff = eps_coeff_et;
   break;
  case AST_OBL_METHOD_EA:
   centuries = cal_jc_b1900( t );
   eps_coeff = eps_coeff_ea;
   break;
  case AST_OBL_METHOD_M:
   centuries = cal_jc_b1900( t );
   eps_coeff = eps_coeff_m;
   break;
 }
 obliquity = poly_d( centuries, eps_coeff, 3 ) / AST_SEC_PER_DEG;
 return obliquity;
}



/*  Mean longitude of the moon minus mean longitude of the moon's node */
double ast_lunar_mean_longitude_from_node( double t )
{
 double f;
  double centuries;
  double long_coeffs[4] = { 335778.877, 295263.137, -13.257, 0.011 };
  integer long_cycles_per_century = 1342;
  centuries = cal_jc( t );
  f = poly_d( centuries, long_coeffs, 3 ) / AST_SEC_PER_DEG
        + long_cycles_per_century * UT_CIRCLE * centuries;
  return utn_circ_d( f );
}


/*  Mean elongation of the moon from the sun */

double ast_lunar_mean_elongation( double t )
{
 double d;
  double centuries;
  double elong_coeffs[4] = { 1072261.307, 1105601.328, -6.891, 0.019 };
  integer elong_cycles_per_century = 1236;
  centuries = cal_jc( t );
  d = poly_d( centuries, elong_coeffs, 3 ) / AST_SEC_PER_DEG
          + elong_cycles_per_century * UT_CIRCLE * centuries;
 return utn_circ_d( d );
}


/*  Mean longitude of the sun minus mean longitude of the sun's perigee */
double ast_solar_mean_longitude_from_peri( double t )
{
  double centuries;
  double elp;
  double elp_coeffs[4] = { 1287099.804, 1292581.224, -0.577, -0.012 };
  double elp_cycles = 99;

  centuries = cal_jc( t );
  elp = poly_d( centuries, elp_coeffs, 3 ) / AST_SEC_PER_DEG
           + elp_cycles * UT_CIRCLE * centuries;

   return utn_circ_d( elp );
}


/* Mean longitude of the moon minus mean longitude of the moons perigee, arcsec */
double ast_lunar_mean_longitude_from_peri( double t )
{
 double centuries;
 double el;
 double el_coeffs[4] = { 485866.733, 715922.633, 31.310, 0.064 };
 double el_cycles = 1325;

 centuries = cal_jc( t );
 el = poly_d( centuries, el_coeffs, 3 )/ AST_SEC_PER_DEG
         + el_cycles * UT_CIRCLE * centuries;
 return utn_circ_d( el );
}



/*
!  Longitude of the mean ascending node of the lunar orbit on the
!   ecliptic, measured from the mean equinox of date
   Result in degrees (was arcsec)
 */

double ast_lunar_mean_node( double t )
{
 double om;
  double centuries;
  double lunar_node_coeffs[4] = { 450160.280, -482890.539, 7.455, 0.008 };
  integer node_cycles_per_century = -5;
  centuries = cal_jc( t );

  om = poly_d( centuries, lunar_node_coeffs, 3 )/ AST_SEC_PER_DEG  
         + node_cycles_per_century * UT_CIRCLE * centuries;
  return utn_circ_d( om );
}


/* Give light deflection parameter given heliocentric distance in AU */
double ast_solar_light_deflection( double r )
{
 return ( 2.0 * AST_RG_SUN_AU / r );
}

/* Calculate light aberration at Earth = Lorentz factor */
/* For null arg, return standard J2000 aberration constant */
double ast_earth_aberration( double* bary_vel )
{
 double vm;

 if ( !bary_vel )
  return AST_ABERR_J2000;

 vm  = utn_ar_vnorm_d( bary_vel );
 return ( sqrt( 1.0 - vm * vm ) );
}


void ast_correct_vector_from_barycenter( double* p1, double parallax, double* bary_state, double* p )
{
 double pxr;
/* Correct vector to be Earth-centered frame 
 *  Why the extra parallax correction? To correct to barycenter.
 *   R(Sol,Star) = R(Sol,Earth) + R(Earth,Star)
 *   r P(Sol,Star) = R(Sol,Earth) + r P(Earth,Star)
 *   P(Sol,Star) = p R(Sol,Earth) + P(Earth,Star)
 */

 pxr = UT_DEG_RAD * parallax / AST_SEC_PER_DEG;
 utn_ar_vdelta_d( p1, -pxr, bary_state, p );
 utn_ar_vunit_d( p );
}

