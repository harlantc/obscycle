/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

void ast_precnutmat( NutCoeffs* nut, double equinox, double t, double* matrix )
{
 double prec_matrix[9];
 double nut_matrix[9];
 double epoch;

 epoch = cal_jd_to_jepoch( t );
 ast_prec_matrix_j( equinox, epoch, prec_matrix );
 ast_nut_matrix( nut, t, nut_matrix );
 utn_ar_lmat_mult_d( nut_matrix, prec_matrix, matrix );
}

void ast_triad( double xlong, double xlat, double* r0, double* r1, double* r2 )
{
/*
! Given a celestial longitude and latitude (e.g. RA and Dec), return
! an axis triad with vectors pointing along increasing radius, longitude,
! and latitude.
*/        

 double sr, cr, sd, cd;

 cr = dcosd( xlong );
 sr = dsind( xlong );
 cd = dcosd( xlat );
 sd = dsind( xlat );

/* Radial axis */
 r0[0] = cr * cd;
 r0[1] = sr * cd;
 r0[2] =      sd;
/* RA axis */
 r1[0] = -sr * cd;
 r1[1] =  cr * cd;
 r1[2] =  0.0;
/* Dec axis */
 r2[0] = -cr * sd;
 r2[1] = -sr * sd;
 r2[2] =       cd;

}




/*  Convert B1950.0 FK4 star data to J2000.0 FK5 (double precision)
*
*  This routine converts stars from the old, Bessel-Newcomb, FK4
*  system to the new, IAU 1976, FK5, Fricke system.  The precepts
*  of Smith et al (Ref 1) are followed, using the implementation
*  by Yallop et al (Ref 2) of a matrix method due to Standish.
*  Kinoshita's development of Andoyer's post-Newcomb precession is
*  used.  The numerical constants from Seidelmann et al (Ref 3) are
*  used canonically.
*
*  Given:  (all B1950.0,FK4)
*     R1950,D1950     dp    B1950.0 RA,Dec (rad)
*     DR1950,DD1950   dp    B1950.0 proper motions (rad/trop.yr)
*     P1950           dp    parallax (arcsec)
*     V1950           dp    radial velocity (km/s, +ve = moving away)
*
*  Returned:  (all J2000.0,FK5)
*     R2000,D2000     dp    J2000.0 RA,Dec (rad)
*     DR2000,DD2000   dp    J2000.0 proper motions (rad/Jul.yr)
*     P2000           dp    parallax (arcsec)
*     V2000           dp    radial velocity (km/s, +ve = moving away)
* 
*  Notes:
*      
*  1)  The proper motions in RA are dRA/dt rather than
*      cos(Dec)*dRA/dt, and are per year rather than per century.
*  
*  2)  Conversion from Besselian epoch 1950.0 to Julian epoch
*      2000.0 only is provided for.  Conversions involving other
*      epochs will require use of the appropriate precession,
*      proper motion, and E-terms routines before and/or
*      after FK425 is called.
*  
*  3)  In the FK4 catalogue the proper motions of stars within
*      10 degrees of the poles do not embody the differential
*      E-term effect and should, strictly speaking, be handled
*      in a different manner from stars outside these regions.
*      However, given the general lack of homogeneity of the star
*      data available for routine astrometry, the difficulties of
*      handling positions that may have been determined from
*      astrometric fields spanning the polar and non-polar regions,  
*      the likelihood that the differential E-terms effect was not
*      taken into account when allowing for proper motion in past
*      astrometry, and the undesirability of a discontinuity in
*      the algorithm, the decision has been made in this routine to
*      include the effect of differential E-terms on the proper
*      motions for all stars, whether polar or not.  At epoch 2000,  
*      and measuring on the sky rather than in terms of dRA, the
*      errors resulting from this simplification are less than
*      1 milliarcsecond in position and 1 milliarcsecond per
*      century in proper motion.
*    
*  References:
*
*     1  Smith, C.A. et al, 1989.  "The transformation of astrometric
*        catalog systems to the equinox J2000.0".  Astron.J. 97, 265.
*      
*     2  Yallop, B.D. et al, 1989.  "Transformation of mean star places
*        from FK4 B1950.0 to FK5 J2000.0 using matrices in 6-space".
*        Astron.J. 97, 274.
*      
*     3  Seidelmann, P.K. (ed), 1992.  "Explanatory Supplement to
*        the Astronomical Almanac", ISBN 0-935702-68-7.
*      
*  P.T.Wallace   Starlink   19 December 1993
*      
*  Copyright (C) 1995 Rutherford Appleton Laboratory
*/

void ast_b1950_to_j2000( double* cel_b, double* cel_j )
{
 double pm_b[2] = { 0.0, 0.0 };
 double parallax_b = 0.0;
 double vel_b = 0.0;
 double pm_j[2] = { 0.0, 0.0 };
 double parallax_j = 0.0;
 double vel_j = 0.0;
 
 ast_b1950_to_j2000_full( cel_b, pm_b, parallax_b, vel_b, Y_B1950, AST_FK5_SIMPLE,
                          cel_j, pm_j, &parallax_j, &vel_j, UT_FALSE );
}

void ast_j2000_to_b1950( double* cel_j, double* cel_b )
{
 double pm_b[2] = { 0.0, 0.0 };
 double parallax_b = 0.0;
 double vel_b = 0.0;
 double pm_j[2] = { 0.0, 0.0 };
 double parallax_j = 0.0;
 double vel_j = 0.0;
 
 ast_j2000_to_b1950_full( cel_j, pm_j, parallax_j, vel_j, Y_B1950, AST_FK5_SIMPLE,
                          cel_b, pm_b, &parallax_b, &vel_b, UT_FALSE );
}


/* If mode is set, position is at BEPOCH */

void ast_j2000_to_b1950_full( double* cel_j, double* pm_j, double parallax_j, double vel_j,
  double epoch_b, integer mode, double* cel_b, double* pm_b, double* parallax_b, double* vel_b, logical old )
{

 double v1[6];
 double v2[6];
 double em[36];
 double eterms_b[6]; 
 double dt_b;

 pm_b[0] = 0.0;
 pm_b[1] = 0.0;
 *parallax_b = 0.0;
 *vel_b = vel_j;

 dt_b = epoch_b - Y_B1950;  
 ast_parallax_to_state( cel_j, pm_j, vel_j, parallax_j, v1 );
 if ( old ) 
  ast_fk5_to_fk4_matrix_old( em );
 else
  ast_fk5_to_fk4_matrix( em );
 utn_ar_lmatnn_postx( v1, em, v2, 6 );
 ast_earth_eterms_b( eterms_b );
 if ( old )
  ast_add_eterms6_old( v2, eterms_b, v1 );
 else
  ast_add_eterms6( v2, eterms_b, v1 );
 ast_state_to_parallax( v1, parallax_j, cel_b, pm_b, vel_b, parallax_b );

/* If needed, apply fictitious pm in FK4 frame to given BEPOCH */
 if ( mode == AST_FK5_SIMPLE && dt_b != 0.0 )
  ast_correct_proper_motion( cel_b, pm_b, 0.0, 0.0, -dt_b, cel_b );
}

void ast_b1950_to_j2000_full( double* cel_b, double* pm_b, double parallax_b, double vel_b,
  double epoch_b, integer mode, double* cel_j, double* pm_j, double* parallax_j, double* vel_j, logical old )
{
 double eterms_b[6]; 
 double v1[6];
 double v2[6];
 double em[36];
 double dt_b = 0.0;
 double dt_j = 0.0;

 dt_b = epoch_b - Y_B1950;
 dt_j = cal_bepoch_to_jepoch( epoch_b ) - Y_J2000;

 ast_parallax_to_state( cel_b, pm_b, vel_b, parallax_b, v1 );
 ast_earth_eterms_b( eterms_b );
 ast_sub_eterms6( v1, eterms_b, v2 );
 if (old )
  ast_fk4_to_fk5_matrix_old( em );
 else
  ast_fk4_to_fk5_matrix( em );

 if ( mode == AST_FK5_SIMPLE ) 
  ast_state_zero_vel( v2, -dt_b );

 utn_ar_lmatnn_postx( v2, em, v1, 6 );

/* V1 velocities are now in units of radians/yr? */
 utn_ar_vcmult_d( v1+3, 1.0 / AST_RAD_ASEC );

 if ( mode == AST_FK5_SIMPLE )
 {
  ast_state_zero_vel( v1, dt_j );
  ast_state_to_parallax_zero( v1, cel_j, pm_j, vel_j, parallax_j );
 } else {
  ast_state_to_parallax( v1, parallax_b, cel_j, pm_j, vel_j, parallax_j );
 }
}

/* Linearly extrapolate a state by a given time period, and then zero out the
   velocity. Used by the FK4/FK5 routines to estimate positions on the
   assumption that velocity is zero (neglible) in FK5 at present epoch */


void ast_state_zero_vel( double* v1, double dt )
{

  integer i;
  for ( i = 0; i < 3; i++ )
  {
   v1[i] = v1[i] + ( dt / CENTURY ) * v1[i+3];
   v1[i+3] = 0.0;
  }
}


void ast_parallax_to_state( double* cel, double* pm, double rv,
 double parallax,  double* v1 )
{
 double r_dot, alpha_dot, delta_dot;
 double ex[3];
 double ey[3];
 double ez[3];

 ast_triad( cel[0], cel[1], ex, ey, ez );
 r_dot = AST_AU_PER_BYR * rv * parallax;  /* au per yr times arcsec? */
 alpha_dot = pm[0] * CENTURY; /* Arcsec per T century: input in arcsec per T year */
 delta_dot = pm[1] * CENTURY;
 v1[0] = ex[0];
 v1[1] = ex[1];
 v1[2] = ex[2];
 v1[3] = r_dot * ex[0] + alpha_dot * ey[0] + delta_dot * ez[0];
 v1[4] = r_dot * ex[1] + alpha_dot * ey[1] + delta_dot * ez[1];
 v1[5] = r_dot * ex[2] + alpha_dot * ey[2] + delta_dot * ez[2];
}

/* 
   From 6-state to proper motion, parallax and radial velocity. 
   Requires initial inverse parallax p0 to be set on input. 
 */
void ast_state_to_parallax( double* v1, double p0, double* cel, double* pm, double* rv, double* parallax )
{
 double r, theta, alpha, r_dot, theta_dot, alpha_dot;

 utn_ar_polar6_d( v1, &r, &theta, &alpha, &r_dot, &theta_dot, &alpha_dot );
 cel[0] = alpha;
 cel[1] = 90.0 - theta;
 pm[0] =  alpha_dot / CENTURY;
 pm[1] = -theta_dot / CENTURY;
 if ( p0 > 0.0 )
    *rv = r_dot / ( p0 * AST_AU_PER_BYR );
 if ( r > 0.0 ) 
    *parallax = p0 / r;
}


void ast_state_to_parallax_zero( double* v1, double* cel, double* pm, double* rv, double* parallax )
{
 utn_ar_polarll_d( v1, &cel[0], &cel[1] );
 pm[0] = 0.0;
 pm[1] = 0.0;
 *parallax = 0.0;
 *rv = 0.0;
}


