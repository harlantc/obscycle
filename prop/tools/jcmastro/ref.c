/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

#define TROPO_BASE  0
#define TROPO_TOP   1
#define STRATO_BASE 2
#define STRATO_TOP  3

#define AST_REF_SZ_TOL 1.0e-20

typedef struct {
 double r;
 double temp;
 double dn;
 double rdndr;
 double sin_k;
 double z;
 double ref_integrand;
} AtmSlice;

double ast_refract_high_zd_corr( double z );
double ast_ref_iterate( EarthAtmosphere atm, AtmSlice* slice1, AtmSlice* slice2 );
void ast_ref_integrate( EarthAtmosphere atm, AtmSlice* slice1, double r, double sz,
 double* rp, double* dnp, double* rdndrp );
void ast_earth_atm_calc_slice( EarthAtmosphere atm, AtmSlice* slice1, double r, AtmSlice* slice2 );

/* Return coefficient of tan Z and tan cubed Z, in degrees:
   dZ = Zvac - Zobs = A tan Z + B tan cubed Z 
 */

void ast_refract_consts_quick_a( EarthAtmosphere atm, double mu, double* a, double* b )
{
 double p, T;
 double humidity;
 p = atm->pressure;
 T = atm->temp;
 humidity = atm->humidity;
 ast_refract_consts_quick( T, p, humidity, mu, a, b );
}


void ast_refract_consts_quick( double T, double p, double humidity, double mu, double* a, double* b )
{
 
 double beta;
 double fa;
 double fb;
 double pw;
 double gamma;
 double ra;
 double rb;

 pw = ast_earth_tropo_pwo( T, p, humidity );
 fa = ( mu >= AST_MU_FARIR )? 0.0074 : 0.0;
 fb = 1.0 - fa * pw;
 ra = ast_earth_tropo_ref2( mu );
 rb = ast_earth_tropo_ref3( mu, T );
 beta = fb * ast_atm_scale_height( T );
 gamma = ( ra * p - rb * pw) / T;

 *a =   gamma * ( 1.0 - beta );
 *b =  -gamma * ( beta - gamma / 2.0 );
}

void ast_refract_consts( EarthAtmosphere atm, double mu, double* a, double* b )
{
 double atn1 = 45.0;
 double atn4 = 1.325817663668033 / UT_DEG_RAD;
 double dz1, dz2;

 ast_atm_eval_coeffs( atm, mu );
 dz1 = ast_refract_eval( atm, atn1 );
 dz2 = ast_refract_eval( atm, atn4 );
 
 *a = ( 64.0 * dz1 - dz2 ) / 60.0;
 *b = ( dz2 - 4.0 * dz1 ) / 60.0;
}




double ast_refract( double zobs, double mu, double lat, double height, double temp, double pressure,
                  double humidity, double lapse )
{
 /*  zobs      Observed zenith distance (deg)
     mu        Wavelength in microns
     lat       Latitude (deg)
     height    Above sea level, km
     temp      Temp/K ambient
     pressure  Pressure/mbar
     lapse     Tropospheric lapse rate (K/km)
     Returns z(vac)-z(obs)
  */
 double dz = 0.0;  /* Refraction z(vac)-z(obs); */
 double lon = 0.0; /* Ignored */
 EarthAtmosphere atm = NULL;



 atm = ast_atm_init();
 ast_atm_set_lapse( atm, lapse );
 ast_atm_set_location( atm, lon, lat, height );
 ast_atm_set_conditions( atm, temp, pressure, humidity );

#if 0
 ast_atm_set_refract_tol( atm, tol );
#endif
 ast_atm_eval_coeffs( atm, mu );

 dz = ast_refract_eval( atm, zobs );
 ast_atm_free( atm );
 return dz;
}

/* Calculate atmospheric conditions at boundaries. 
   Initialize by setting slice1 = slice2 and setting value of slice->z. */

void ast_earth_atm_calc_slice( EarthAtmosphere atm, AtmSlice* slice1, double height, AtmSlice* slice2 )
{
 double cx,sx;
 double r;

 r = AST_R_EARTH + height;
 slice2->r = r; 

 ast_earth_atm_refindex( atm, 
       slice1->r, slice2->r,
       slice1->temp, &slice2->temp, &slice2->dn, &slice2->rdndr );
 
 if ( slice1 == slice2 )  /* Initialization */
 {
  slice2->sin_k = slice2->dn * slice2->r * dsind( slice2->z );
 } else {
  slice2->sin_k = slice1->sin_k;
  sx = slice2->sin_k / ( slice2->r * slice2->dn );
  cx = sqrt( max_d( 1.0 - sx*sx, 0.0 ));
  slice2->z = argd_d( cx, sx );
 }
 slice2->ref_integrand = slice2->rdndr / ( slice2->dn + slice2->rdndr );
}

double ast_refract_eval( EarthAtmosphere atm, double zobs )
{
 double znorm;
 logical zsign;
 AtmSlice slice[4];
 double dz = 0.0; 
/* Put z in range -93, 93 */
 znorm = circ_ew_d( zobs );
 zsign = ( znorm < 0.0 );
 if ( zsign ) znorm = -znorm;
 if ( znorm > AST_REFRACT_MAXZ ) znorm = AST_REFRACT_MAXZ;
 slice[TROPO_BASE].z = znorm;
 if ( atm->height < -1.0 || atm->height > AST_TROPOPAUSE )
 {
  /* Error state here */
  atm->height = utn_range_d( atm->height, -1.0, 10.0 );
 }
/* Troposphere at observer */
 ast_earth_atm_calc_slice( atm, &slice[TROPO_BASE], atm->height,    &slice[TROPO_BASE] );
/* Troposphere side of tropopause */
 ast_earth_atm_calc_slice( atm, &slice[TROPO_BASE], AST_TROPOPAUSE, &slice[TROPO_TOP] );
 ast_atm_set_tropo_index( atm, slice[TROPO_TOP].dn );
/* Stratosphere side of tropopause */
 ast_earth_atm_calc_slice( atm, &slice[TROPO_TOP],  AST_TROPOPAUSE, &slice[STRATO_BASE] );
/* Stratosphere top */
 ast_earth_atm_calc_slice( atm, &slice[STRATO_BASE],AST_MESOPAUSE,  &slice[STRATO_TOP] ); 

 dz    = ast_ref_iterate( atm, &slice[TROPO_BASE], &slice[TROPO_TOP] ); 
 dz   += ast_ref_iterate( atm, &slice[STRATO_BASE], &slice[STRATO_TOP] );

 if ( zsign ) 
  dz = -dz;
 return dz;
}


double ast_ref_iterate( EarthAtmosphere atm, AtmSlice* slice1, AtmSlice* slice2 )
{
 logical loop =1;
 integer dstep;
 integer step;
 integer nsteps = 8;
 double dzold = AST_MEGA;
 double dh;
 double r;
 double ff;
 double fb;
 double fo = 0;
 double fe = 0;
 double f;
 double dn;
 double rdndr;
 double dz;

  fb = slice1->ref_integrand;
  ff = slice2->ref_integrand;
  fo = 0.0;
  fe = 0.0;
  dstep = 1;
  loop = UT_TRUE;
  while( loop )
  {
   dh =  (slice2->z - slice1->z) / ((double)nsteps);
   r = slice1->r;
   for ( step = 1; step < nsteps ; step += dstep )
   {
    ast_ref_integrate( atm, slice1, r, dh * step, &r, &dn, &rdndr );
    f = rdndr / ( dn + rdndr );
    if ( dstep == 1 && ( step % 2 == 0 )) 
     fe += f;
    else
     fo += f;
   }
/* Simpson's Rule integration */
   dz = dh * ( fb + 4 * fo + 2 * fe * ff ) / 3.0;
   loop =  ( fabs( dz - dzold ) > atm->rtol );
   if ( loop )
   {
    dzold = dz;
    nsteps *= 2;
    fe += fo;
    fo = 0.0;
    dstep = 2;  /* Skip even values this time */
   }
  }
 return dz;
}


void ast_ref_integrate( EarthAtmosphere atm, AtmSlice* slice1, double r, double dz,
 double* rp, double* dnp, double* rdndrp )
{
 double w;
 double dr; 
 integer rstep;
 double tg;
 double dn;
 double rdndr;
 double sz;
    sz = dsind( slice1->z + dz );
    if ( sz > AST_REF_SZ_TOL )
    {
     w = slice1->sin_k / sz;    /* r dn */
     dr = AST_MEGA;
     for ( rstep = 0; rstep < 4 && fabs( dr ) > 1.0; rstep++ )
     {
      ast_earth_atm_refindex( atm, slice1->r, r, slice1->temp, &tg, &dn, &rdndr );
      dr = ( r * dn - w ) / ( dn + rdndr );
      r = r - dr;
     }
    }
    ast_earth_atm_refindex( atm, slice1->r, r, slice1->temp, &tg, &dn, &rdndr );

  *rp = r;
  *dnp = dn;
  *rdndrp= rdndr;
}

/*
 *  Based on Starlink code described as follows:
*+
*     - - - - - -
*      R E F R O
*     - - - - - -
*
*  Atmospheric refraction for radio and optical/IR wavelengths.
*
*  Given:
*    ZOBS    d  observed zenith distance of the source (radian)
*    HM      d  height of the observer above sea level (metre)
*    TDK     d  ambient temperature at the observer (deg K)
*    PMB     d  pressure at the observer (millibar)
*    RH      d  relative humidity at the observer (range 0-1)
*    WL      d  effective wavelength of the source (micrometre)
*    PHI     d  latitude of the observer (radian, astronomical)
*    TLR     d  temperature lapse rate in the troposphere (degK/metre)
*    EPS     d  precision required to terminate iteration (radian)
*
*  Returned:
*    REF     d  refraction: in vacuo ZD minus observed ZD (radian)
*
*  Notes:
*
*  1  A suggested value for the TLR argument is 0.0065D0.  The
*     refraction is significantly affected by TLR, and if studies
*     of the local atmosphere have been carried out a better TLR
*     value may be available.
*
*  2  A suggested value for the EPS argument is 1D-8.  The result is
*     usually at least two orders of magnitude more computationally
*     precise than the supplied EPS value.
*
*  3  The routine computes the refraction for zenith distances up
*     to and a little beyond 90 deg using the method of Hohenkerk
*     and Sinclair (NAO Technical Notes 59 and 63, subsequently adopted
*     in the Explanatory Supplement, 1992 edition - see section 3.281).
*
*  4  The code is a development of the optical/IR refraction subroutine
*     AREF of C.Hohenkerk (HMNAO, September 1984), with extensions to
*     support the radio case.  Apart from merely cosmetic changes, the
*     following modifications to the original HMNAO optical/IR refraction
*     code have been made:
*
*     .  The angle arguments have been changed to radians.
*
*     .  Any value of ZOBS is allowed (see note 6, below).
*
*     .  Other argument values have been limited to safe values.
*
*     .  Murray's values for the gas constants have been used
*        (Vectorial Astrometry, Adam Hilger, 1983).
*
*     .  The numerical integration phase has been rearranged for
*        extra clarity.
*
*     .  A better model for Ps(T) has been adopted (taken from
*        Gill, Atmosphere-Ocean Dynamics, Academic Press, 1982).
*
*     .  More accurate expressions for Pwo have been adopted
*        (again from Gill 1982).
*
*     .  Provision for radio wavelengths has been added using
*        expressions devised by A.T.Sinclair, RGO (private
*        communication 1989), based on the Essen & Froome
*        refractivity formula adopted in Resolution 1 of the
*        13th International Geodesy Association General Assembly
*        (Bulletin Geodesique 70 p390, 1963).
*
*     .  Various small changes have been made to gain speed.
*
*     None of the changes significantly affects the optical/IR results
*     with respect to the algorithm given in the 1992 Explanatory
*     Supplement.  For example, at 70 deg zenith distance the present
*     routine agrees with the ES algorithm to better than 0.05 arcsec
*     for any reasonable combination of parameters.  However, the
*     improved water-vapour expressions do make a significant difference
*     in the radio band, at 70 deg zenith distance reaching almost
*     4 arcsec for a hot, humid, low-altitude site during a period of
*     low pressure.
*
*  5  The radio refraction is chosen by specifying WL > 100 micrometres.
*     Because the algorithm takes no account of the ionosphere, the
*     accuracy deteriorates at low frequencies, below about 30 MHz.
*
*  6  Before use, the value of ZOBS is expressed in the range +/- pi.
*     If this ranged ZOBS is -ve, the result REF is computed from its
*     absolute value before being made -ve to match.  In addition, if
*     it has an absolute value greater than 93 deg, a fixed REF value
*     equal to the result for ZOBS = 93 deg is returned, appropriately
*     signed.
*
*  7  As in the original Hohenkerk and Sinclair algorithm, fixed values
*     of the water vapour polytrope exponent, the height of the
*     tropopause, and the height at which refraction is negligible are
*     used.
*
*  8  The radio refraction has been tested against work done by
*     Iain Coulson, JACH, (private communication 1995) for the
*     James Clerk Maxwell Telescope, Mauna Kea.  For typical conditions,
*     agreement at the 0.1 arcsec level is achieved for moderate ZD,
*     worsening to perhaps 0.5-1.0 arcsec at ZD 80 deg.  At hot and
*     humid sea-level sites the accuracy will not be as good.
*
*  9  It should be noted that the relative humidity RH is formally
*     defined in terms of "mixing ratio" rather than pressures or
*     densities as is often stated.  It is the mass of water per unit
*     mass of dry air divided by that for saturated air at the same
*     temperature and pressure (see Gill 1982).
*
*  Called:  sla_DRANGE, sla__ATMT, sla__ATMS
*
*  P.T.Wallace   Starlink   25 May 2000
*
*  Copyright (C) 2000 Rutherford Appleton Laboratory
*/

/* Standard    a tan Z + b tan cubed Z law */

double ast_refract_deapply( double z, double a, double b )
{
 double dz_est;
 double s,c,t;
 s = dsind( z );
 c = dcosd( z );
 t = s / c;
 dz_est = ( a + b * t * t ) * t;
 return dz_est;
}
/* Apply refraction coeffs: return z(vac) - z(obs) given z(vac) */
double ast_refract_apply( double z, double a, double b )
{
 double dz;
 double zl,zu;
 double dzu;
 double dz_est, ddz_est;
 double s,c,t;
 double f = 1.0;
 integer iter;
 zu = z;
 if ( z > AST_REFRACT_BIGZ )
 {
  zu = AST_REFRACT_BIGZ;
  f = ast_refract_high_zd_corr( z ) / ast_refract_high_zd_corr( AST_REFRACT_BIGZ );
 }
 zl = zu;

 for ( iter = 0; iter <= 1; iter++ )
 {
  dzu = zu - zl;
  s = dsind( zl );
  c = dcosd( zl );
  t = s / c;
  dz_est = ( a + b * t * t ) * t;
  ddz_est = UT_DEG_RAD * ( a + 3 * b * t * t ) / ( c * c );
  dz = dzu + ( dz_est - dzu ) / ( 1.0 + ddz_est );
  zl = zl - dz;
 }
 dz *= f;
 return dz;
}

double ast_refract_high_zd_corr( double z )
{
 double f;
 double c1[3] = { 0.55445, -0.01133, 0.00202 };
 double c2[3] = { 1.0,      0.28385, 0.02390 };
 double e;

 e = 90.0 - z;
 if ( e < -3.0 ) e = -3.0;
 f = poly_d( e, c1, 2 ) / poly_d( e, c2, 2 );
 return f;
}

/*
 *   Convert vacuum position vector to predicted  refracted vector.
 *   
 */
void ast_refract_vector( double* v1, double a, double b, double* v2 )
{
 /* keep correction approx constant at low elevation, < 3 deg */
 double z;
 double delta;
 double cos_delta;
 double f;
 double wt;
 double wb, wc;
 double re;
 utn_ar_vcopy_d( v1, v2 );
 v2[2] = 0.0;
 z = v1[2];
 if ( z < 0.05 ) z = 0.05;

 /* Newton-Raphson */
 re = utn_ar_vnorm_d( v2 ) / z; 
 wb = b * re;
 wc = ( a + 3 * wb ) * ( 1.0 + re * re );
 wt =  ( a + wb ) / ( 1.0 + wc );
 delta     = wt * re;
 cos_delta = 1.0 - 0.5 * delta * delta;
 f         = cos_delta * ( 1.0 - wt ); 
 v2[0] *= f;
 v2[1] *= f;
 v2[2] = cos_delta * z * ( 1.0 + delta * re ) + ( v1[2] - z );
}
