/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <math.h>
#include "jcmastro.h"
void earthpv( double equinox, double t, double* bary, double* helio );

void earthpv( double equinox, double t, double* bary, double* helio )
{
 return; /* XXX */  /* NOT YET IMPLEMENTED */
}

/*
*  Convert star RA,Dec from geocentric apparent to mean place
*  The mean coordinate system is the IAU 1976 system (loosely FK5).
*  The reference frames and timescales used are post IAU 1976.
*  MAPP: Compute star-independent parameters in preparation for
*  conversions between mean place and geocentric apparent place.
*  The parameters produced by this routine are required in the
*  parallax, light deflection, aberration, and precession/nutation
*  parts of the mean/apparent transformations.
*  The reference frames and timescales used are post IAU 1976.
*  MAPQ: Quick mean to apparent place:  transform a star RA,Dec from
*  mean place to geocentric apparent place, given the
*  star-independent parameters.
*  Use of this routine is appropriate when efficiency is important
*  and where many star positions, all referred to the same equator
*  and equinox, are to be transformed for one epoch.  The
*  star-independent parameters can be obtained by calling the
*  AMP_MAPP routine.
*  If the parallax and proper motions are zero the AMP_MAPQZ can
*  be used instead.
*  MAPQZ:
*  Quick mean to apparent place:  transform a star RA,Dec from
*  mean place to geocentric apparent place, given the
*  star-independent parameters, and assuming zero parallax
*  and proper motion.
*  Use of this routine is appropriate when efficiency is important
*  and where many star positions, all with parallax and proper
*  motion either zero or already allowed for, and all referred to
*  the same equator and equinox, are to be transformed for one
*  epoch.  The star-independent parameters can be obtained by
*  calling the AMP_MAPP routine.
*  The corresponding routine for the case of non-zero parallax
*  and proper motion is AMP_MAPQ.
*  The reference frames and timescales used are post IAU 1976.
*  References:
*     1984 Astronomical Almanac, pp B39-B41.
*     (also Lederle & Schwan, Astron. Astrophys. 134,1-6, 1984)
* AMP_MEAN:
*  Given:
*     RA,DA    dp     apparent RA,Dec (deg)
*     DATE     dp     TDB for apparent place (JD)
*     EQ       dp     equinox:  Julian epoch of mean place
*  Returned:
*     RM,DM    dp     mean RA,Dec (deg)
* AMP_APPARENT:
*  Given:
*     RM,DM    dp     mean RA,Dec (deg)
*     PR,PD    dp     proper motions:  RA,Dec changes "/ Julian year
*     PX       dp     parallax (arcsec)
*     RV       dp     radial velocity (km/sec, +ve if receeding)
*     EQ       dp     epoch and equinox of star data (Julian)
*     DATE     dp     TDB for apparent place (JD)
*  Returned:
*     RA,DA    dp     apparent RA,Dec (deg)
*  Called:
*     MAPP        star-independent parameters
*     MAPQ        quick mean to apparent
* MAPP:
*  Given:
*     EQ       dp     epoch of mean equinox to be used (Julian)
*     DATE     dp     TDB
*  Returned:
*     PMT      dp     time interval for proper motion (Julian years)
*     EB      dp(3)   barycentric position of the Earth (AU)
*     EHN     dp(3)   heliocentric direction of the Earth (unit vector)
*     GR2E     dp     (grav rad Sun)*2/(Sun-Earth distance)
*     ABV     dp(3)   barycentric Earth velocity in units of c
*     AB1      dp     sqrt(1-v**2) where v=modulus(ABV)
*     AB2      dp     AB1+1
*     PNM    dp(3,3)  precession/nutation matrix
* MAPQ
*  Given:
*     RM,DM    dp     mean RA,Dec (DEG)
*     PR,PD    dp     proper motions:  RA,Dec changes " per Julian year
*     PX       dp     parallax (arcsec)
*     RV       dp     radial velocity (km/sec, +ve if receeding)
*  Returned:
*     RA,DA    dp     apparent RA,Dec 
* MAPQZ
*  Given:
*     RM,DM    dp     mean RA,Dec (rad)
*  Returned:
*     RA,DA    dp     apparent RA,Dec (rad)

*  Notes:
*     1)  The distinction between the required TDB and TDT is
*         always negligible.  Moreover, for all but the most
*         critical applications UTC is adequate.
*     2)  The accuracy is limited by the routine EARTHPV(qv),
*         which computes the earth positions and velocities using
*         the methods of Stumpff.  The maximum error is about
*         0.3 milliarcsecond.
*     3)  Iterative techniques are used for the aberration and light
*         deflection corrections so that the routines AMP_AMP and
*         AMP_MAP are exact inverses (to Vax REAL*8
*         accuracy and up to 15 arcminutes from the centre of
*         the sun).
*     4)  EQ is the Julian epoch specifying both the reference
*         frame and the epoch of the position - usually 2000.
*         For positions where the epoch and equinox are
*         different, use the routine PMOT to apply proper
*         motion corrections before using this routine.
*     5)  The proper motions in RA are dRA/dt rather than
*         cos(Dec)*dRA/dt.
*     6)  APPARENT: This routine may be wasteful for some applications
*         because it recomputes the Earth position/velocity and
*         the precession/nutation matrix each time, and because
*         it allows for parallax and proper motion.  Where
*         multiple transformations are to be carried out for one
*         epoch, a faster method is to call the MAPP routine
*         once and then either the MAPQ routine (which includes
*         parallax and proper motion) or MAPQZ (which assumes
*         zero parallax and proper motion).
*    7) The vectors BPOS and HPOS are referred to the mean equinox
*       and equator of epoch EQ.
*    8)  Within about 300 arcsec of the centre of the Sun the
*        gravitational deflection term is set to zero to avoid
*        overflow.  Otherwise no account is taken of the
*        impossibility of observing stars which lie behind
*        the Sun.
*  P.T.Wallace   Starlink   August 1987
C----------------------------------------------------------------
*/


void ast_apparent_to_mean( EarthState* earth, double* apparent, double* mean )
{

    double p1[3];
    double p3[3];
    double p2[3];
    double* bvel = &earth->bary_state[3];

    double p[3];
/* Geocentric apparent RA,Dec*/
    utn_ar_unitll_d( apparent[0], apparent[1], p3 );
/* Precession and nutation*/
    utn_ar_lmat_prex_d( earth->pnm, p3, p2 );
/* Aberration */
    ast_aberrate_invert( p2, bvel, earth->ab, p1 );
/* Light deflection*/
    ast_light_deflect_invert( p1, earth->helio_state, earth->gr2e, p );
    utn_ar_polarll_d( p, &mean[0], &mean[1] );
}

EarthState* ast_earth_state_init( double t )
{
 return ast_earth_state_init_equinox( Y_J2000, t );
}

void ast_earth_state_free( EarthState* state )
{
 if ( !state ) return;

 ast_nut_free( state->nut );
 free( state );
}

EarthState* ast_earth_state_init_equinox( double equinox, double t )
{
 double helio_dist;
 EarthState* earth;
 double* bary_vel;

 earth = (EarthState*)calloc( 1, sizeof( EarthState ));
 earth->t = t;
 earth->epoch   = cal_jd_to_jepoch( t );
 earth->equinox = equinox;
 earth->nut = ast_nut_read( NULL );
/*  Precession/nutation matrix */
 ast_precnutmat( earth->nut, equinox, t, earth->pnm );
 earthpv( equinox, t, earth->bary_state, earth->helio_state );
/*  Heliocentric direction of earth (normalised) and modulus */
 helio_dist = utn_ar_vunit_d( earth->helio_state );
/*  Convert barycentric velocity to c units; Was in AU/s */
 bary_vel = &earth->bary_state[3];
 utn_ar_vcmult_d( bary_vel, AST_AU_S );
 earth->ab   = ast_earth_aberration( bary_vel );
 earth->gr2e = ast_solar_light_deflection( helio_dist );

 return earth;
}




void ast_mean_to_apparent_pm( EarthState* earth,
               double* mean, double* proper_motion, double parallax,
               double radial_vel, double* result )
{
/* Proper motion in arcsec/yr */
	double p[3];  /* Earth-centered vector */
        double pa[3];
        double p1[3];  /* Mean vector corrected for space motion */
  double dt_yr;

  dt_yr = earth->epoch - earth->equinox;
  ast_apply_proper_motion( mean, proper_motion, parallax, radial_vel, dt_yr, p1 );
  ast_correct_vector_from_barycenter( p1, parallax, earth->bary_state, p );
  ast_aberration_calc( earth, p, pa );
  utn_ar_polarll_d( pa, &result[0], &result[1] );
}

/* PM in arcsec per year */
void ast_correct_proper_motion( double* mean, double* proper_motion,  double parallax, double radial_vel, double dt_yr, double* corr )
{
  double p1[3];
  ast_apply_proper_motion( mean, proper_motion, parallax, radial_vel, dt_yr, p1 );
  utn_ar_polarll_d( p1, &corr[0], &corr[1] );
}


void ast_mean_to_apparent( EarthState* earth, double* mean, double * result )
{
 double p[3];
 double pa[3];
 utn_ar_unitll_d( mean[0], mean[1], p );
 ast_aberration_calc( earth, p, pa );
 utn_ar_polarll_d( pa, &result[0], &result[1] );
}

void ast_aberration_calc( EarthState* earth, double* p, double* pa )
{
/*
 C Convert mean vector P(3) to apparent vector PA(3),
 ! correcting for light deflection, aberration, precession and nutation.
 ! Requires previous call to APP\_INIT.
 */
 double p1[3];
 double p2[3];
 double* helio_pos = &earth->helio_state[0];
 double* bary_vel = &earth->bary_state[3];

/*  Light deflection*/
 ast_light_deflect( p, helio_pos, earth->gr2e, p1 );
/*  Aberration */
 ast_aberrate( p1, bary_vel, earth->ab, p2 );
/* Prec and nut */
 utn_ar_lmat_postx_d( earth->pnm, p2, pa );
}

/* Invert an aberrated vector given the velocity and the aberration constant.
   Two iterations are used for convergence.
 */
void ast_aberrate_invert( double* p2, double* bary_vel, double ab, double* p1 )
{
    integer iter;
    integer i;
    double p1dv;
    double wp,wv;
    utn_ar_vcopy_d( p2, p1 );
/*  Aberration: iterate twice */
    for ( iter = 0; iter < 2; iter++ )
    {
      p1dv = utn_ar_vdot_d( p1, bary_vel );
      wp = ( p1dv + 1.0 );
      wv = ( p1dv / (ab + 1.0) + 1.0 );
      for ( i = 0; i < 3; i++ )
       p1[i] = ( wp * p2[i] - wv * bary_vel[i] ) / ab;
      utn_ar_vunit_d( p1 );
    }
}

void ast_aberrate( double* p, double* bary_vel, double ab_const, double* p1 )
{
/* Barycenter vel v/c */
 double p_dot_dv;
 double wp, wv;
 integer i;

 p_dot_dv = utn_ar_vdot_d( p, bary_vel );
 wp = ab_const;
 wv = ( p_dot_dv / (ab_const + 1.0) + 1.0 );
 for ( i = 0; i < 3; i++ )
  p1[i] = wp * p[i] + wv * bary_vel[i];
 utn_ar_vunit_d( p1 );
}


void ast_light_deflect( double* p, double* helio_pos, double gr2e, double* p1 )
{
  double pde;  /* Dot product of direction with Earth-sun direction */
  double tmp;
  integer i;


  pde = utn_ar_vdot_d( p, helio_pos );
  tmp = 1.0 + pde;
  if ( tmp < AST_R_SUN_DIRCOS ) tmp = AST_R_SUN_DIRCOS; /* Avoid div by zero */
  for ( i = 0; i < 3; i++ )
   p1[i] = p[i] + gr2e * ( helio_pos[i] - pde * p[i] ) / tmp;
  utn_ar_vunit_d( p1 );
}


void ast_light_deflect_invert( double* p, double* helio_pos, double gr2e, double* p1 )
{
 double pde;
 integer iter;
 double tmp;
 integer i;
 utn_ar_vcopy_d( p, p1 );
 for ( iter = 0; iter < 5; iter++ )
 {
  pde = utn_ar_vdot_d( p1, helio_pos );
  tmp = 1.0 + pde;
  if ( tmp < AST_R_SUN_DIRCOS ) tmp = AST_R_SUN_DIRCOS; /* Avoid div by zero */
  for ( i = 0; i < 3; i++ )
    p1[i] = ( tmp * p[i] - gr2e *  helio_pos[i] ) / ( tmp - gr2e * pde );
  utn_ar_vunit_d( p1 );
 }
}

/* Correct a vector to be in the Earth frame at given epoch */
void ast_apply_proper_motion( double* mean, double* proper_motion, double parallax, double radial_vel, double dt_yr, double* p1 )
{
 double q[3];
 double s[3];
 double em[3];
 double p[3];
 double pxr; /* Parallax in radians */
 integer i;
 double motion[3];

/*
 *  If pma is proper motion in alpha per year, (rad/yr)
 *  then true space vel is r * pma (AU/yr)
 *  Now  parallax  is   r = 1/p (AU)
 *  so  radial vel is          r * (v/r)  = r * p * v (AU/yr)
 */

 pxr = UT_DEG_RAD * parallax / AST_SEC_PER_DEG;
/* Make triad of radial, tangential and N pole */
 ast_triad( mean[0], mean[1], p, s, q );
/*  Space motion (radians per year) */
 motion[0] =  UT_DEG_RAD *  (-proper_motion[0]) / AST_SEC_PER_DEG;
 motion[1] =  UT_DEG_RAD *  ( proper_motion[1]) / AST_SEC_PER_DEG;
 motion[2] =  AST_AU_PER_YR * radial_vel * pxr;
 for ( i = 0; i < 2; i++ )
  em[i] = motion[0] * s[i] + motion[1] * q[i] + motion[2] * p[i];

/*  Geocentric direction of star (normalised)*/
 utn_ar_vdelta_d( p, dt_yr, em, p1 );
 utn_ar_vunit_d( p1 );
}

void ast_correct_from_barycenter( EarthState* earth, double* mean, double parallax, double* apparent )
{
 double p1[3];
 double p2[3];

 utn_ar_unitll_d( mean[0], mean[1], p1 );
 ast_correct_vector_from_barycenter( p1, parallax, earth->bary_state, p2 );
 utn_ar_polarll_d( p2, &apparent[0], &apparent[1] );
 
}

