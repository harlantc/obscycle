/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

void ast_prec_angles_b_old( double epoch1, double epoch2, double* angles );


void ast_prec_unitmat( double* rmat )
{    
 rmat[0] = 1.0;
 rmat[1] = 0.0;
 rmat[2] = 0.0;
 rmat[3] = 0.0;
 rmat[4] = 1.0;
 rmat[5] = 0.0;
 rmat[6] = 0.0;
 rmat[7] = 0.0;
 rmat[8] = 1.0;
}
    

void ast_prec_angles_eval( double epoch0, double epoch1, double epoch2, 
        double* zeta_coeff, double* z_coeff, double* theta_coeff, integer n,
        double* euler );


void ast_precess_eval( double* celin, double* matrix, double* celout )
{
 double p[3];
 double r[3];
 utn_ar_unitll_d( celin[0], celin[1], p );
 utn_ar_lmat_postx_d( matrix, p, r );
 utn_ar_polarll_d( r, &celout[0], &celout[1] );
}

/*
*  Notes:
*         
*     1)  The epochs are TDB (loosely ET) Julian epochs.
*         
*     2)  The matrix is in the sense   V(EP1)  =  RMATP * V(EP0)
*
*     3)  Though the matrix method itself is rigorous, the precession
*         angles are expressed through canonical polynomials which are
*         valid only for a limited time span.  There are also known
*         errors in the IAU precession rate.  The absolute accuracy
*         of the present formulation is better than 0.1 arcsec from
*         1960AD to 2040AD, better than 1 arcsec from 1640AD to 2360AD,
*         and remains below 3 arcsec for the whole of the period
*         500BC to 3000AD.  The errors exceed 10 arcsec outside the
*         range 1200BC to 3900AD, exceed 100 arcsec outside 4200BC to
*         5600AD and exceed 1000 arcsec outside 6800BC to 8200AD.
*         The SLALIB routine sla_PRECL implements a more elaborate
*         model which is suitable for problems spanning several
*         thousand years.
*
*  References:
*     Lieske,J.H., 1979. Astron.Astrophys.,73,282.
*      equations (6) & (7), p283.

 */

logical ast_prec_matrix_j( double epoch1, double epoch2, double* rmat )
{
 double angles[3];
 if ( epoch1 == epoch2 )
 {
  ast_prec_unitmat( rmat );
  return UT_FALSE;
 }
 ast_prec_angles_j( epoch1, epoch2, angles );
 utn_ar_euler_rotate( angles, rmat );
 return UT_TRUE;
}

logical ast_prec_matrix_b( double epoch1, double epoch2, double* rmat, integer mode )
{
 double angles[3];
 if ( epoch1 == epoch2 )
 {
  ast_prec_unitmat( rmat );
  return UT_FALSE;
 }

 if( mode == AST_PREC_OLD_B )
  ast_prec_angles_b_old( epoch1, epoch2, angles );
 else
  ast_prec_angles_b( epoch1, epoch2, angles );
 utn_ar_euler_rotate( angles, rmat );
 return UT_TRUE;
}


void ast_prec_calc_angles_j( double t, double* angles )
{
 double epoch1;
 double epoch2;

 epoch1 = cal_jc( t );
 epoch2 = Y_J2000;
 ast_prec_angles_j( epoch1, epoch2, angles );

}

void ast_prec_angles_j( double epoch1, double epoch2, double* angles )
{
 if ( epoch1 > AST_PREC_APPROX1 && epoch1 < AST_PREC_APPROX2 )
  ast_prec_angles_j_approx( epoch1, epoch2, angles );
 else
  ast_prec_angles_j_simon( epoch1, epoch2, angles );
}

/* Calculate the precession matrix angles between epoch 1 and 2.



 * Usually epoch1 or epoch2 is 2000.0 
 */


/* The z coefficients are actually  z-zeta */

void ast_prec_angles_eval( double epoch0, double epoch1, double epoch2, 
        double* zeta_coeff, double* z_coeff, double* theta_coeff, integer n,
        double* euler )
{
 double jc1, djc;
 integer degree;
 integer i;
 double zeta, z, theta;
 double zeta_pc[6];
 double zc[6];
 double tc[6];
 integer offset;
 jc1 = ( epoch1 - epoch0 ) / CENTURY;
 djc = (epoch2 - epoch1)/ CENTURY;
 degree = n;
 for ( i = 0; i <= n; i++ )
 {
  offset = ( n + 1 ) * i;
  zeta_pc[i] = poly_d( jc1, zeta_coeff+ offset, degree );
  zc[i]      = poly_d( jc1, z_coeff + offset, degree );
  tc[i]      = poly_d( jc1, theta_coeff + offset, degree );
  degree--;
 }

 zeta  =        djc * poly_d( djc, zeta_pc, n ) / AST_SEC_PER_DEG;
 z     = zeta + djc * poly_d( djc, zc, n ) / AST_SEC_PER_DEG;
 theta =        djc * poly_d( djc, tc, n ) / AST_SEC_PER_DEG;
#if 0
printf( "NEW DJC = %f JC1 = %f TC = %f %f %f ZETA =%f %f\n", djc, jc1, zeta_pc[0], zeta_pc[1], zeta_pc[2], zeta * AST_SEC_PER_DEG, zeta);
printf( "NEW DJC = %f JC1 = %f TC = %f %f %f Z    =%f %f\n", djc, jc1, zc[0], zc[1], zc[2], z  * AST_SEC_PER_DEG, z );
printf( "NEW DJC = %f JC1 = %f TC = %f %f %f THETA=%f %f\n", djc, jc1, tc[0], tc[1], tc[2], theta  * AST_SEC_PER_DEG, theta );
#endif
 euler[0] = -z;
 euler[1] = theta;
 euler[2] = -zeta;

}



/*
*     1)  The epochs are TDB Julian epochs.
*  
*     2)  The matrix is in the sense   V(EP1)  =  RMATP * V(EP0)
*  
*     3)  The absolute accuracy of the model is limited by the
*         uncertainty in the general precession, about 0.3 arcsec per
*         1000 years.  The remainder of the formulation provides a
*         precision of 1 mas over the interval from 1000AD to 3000AD,
*         0.1 arcsec from 1000BC to 5000AD and 1 arcsec from
*         4000BC to 8000AD.
* 
*  Reference:
*     Simon, J.L. et al., 1994. Astron.Astrophys., 282, 663-683.
*
*/



void ast_prec_angles_j_simon( double epoch1, double epoch2, double* euler )
{
 integer n = 5;
 double zeta_pcoeff[6][6] ={
       { 23060.9097, 139.7459, -0.0038, -0.5918, -0.0037, 0.0007 },
       {    30.2226,  -0.2523, -0.3840, -0.0014,  0.0007, 0.0000 },
       {    18.0813,  -0.1326,  0.0006,  0.0005,  0.0000, 0.0000 },
       {    -0.0583,  -0.0001,  0.0007,  0.0000,  0.0000, 0.0000 },
       {    -0.0285,   0.0000,  0.0000,  0.0000,  0.0000, 0.0000 },
       {    -0.0002,   0.0000,  0.0000,  0.0000,  0.0000, 0.0000 } };
#if 0
 double z_pcoeff[6][6] = {
  { 23060.9097,139.7459, -0.0038, -0.5918,-0.0037, 0.0007 },
    { 109.5270,  0.2446, -1.3913, -0.0134, 0.0026, 0.0000 },
    {  18.2667, -1.1400, -0.0173,  0.0044, 0.0000, 0.0000 },
    {  -0.2821, -0.0093,  0.0032,  0.0000, 0.0000, 0.0000 },
    {  -0.0301,  0.0006,  0.0000,  0.0000, 0.0000, 0.0000 },
    {  -0.0001,  0.0000,  0.0000,  0.0000, 0.0000, 0.0000 }
 };
#endif
 double zz_pcoeff[6][6] = {
  {     0.0000,  0.0000,  0.0000,  0.0000, 0.0000, 0.0000 },
    {  79.3044,  0.4969, -1.0073, -0.0120, 0.0019, 0.0000 },
    {   0.1854, -1.0074, -0.0179,  0.0039, 0.0000, 0.0000 },
    {  -0.2238, -0.0092,  0.0025,  0.0000, 0.0000, 0.0000 },
    {  -0.0016,  0.0006,  0.0000,  0.0000, 0.0000, 0.0000 },
    {   0.0001,  0.0000,  0.0000,  0.0000, 0.0000, 0.0000 }
 };
 double theta_pcoeff[6][6] ={
  { 20042.0207, -85.3131, -0.2111, 0.3642, 0.0008, -0.0005 },
  {   -42.6566,  -0.2111,  0.5463, 0.0017,-0.0012,  0.0000 },
  {   -41.8238,   0.0359,  0.0027,-0.0001, 0.0000,  0.0000 },
  {    -0.0731,   0.0019,  0.0009, 0.0000, 0.0000,  0.0000 },
  {    -0.0127,   0.0011,  0.0000, 0.0000, 0.0000,  0.0000 },
  {     0.0004,   0.0000,  0.0000, 0.0000, 0.0000,  0.0000 }};

 ast_prec_angles_eval( Y_J2000, epoch1, epoch2, 
        (double*)&zeta_pcoeff[0][0], (double*)&zz_pcoeff[0][0], (double*)&theta_pcoeff[0][0], n, euler );
}


void ast_prec_angles_j_approx( double epoch1, double epoch2, double* euler )
{
 integer n = 2;
 double zeta_acoeff[3][3] = {
      { 2306.218100,  1.396560, -0.000139 },
      {    0.301880, -0.000344,  0.000000 }, 
      {    0.017998,  0.000000,  0.000000 }};
 double zz_acoeff[3][3] = { 
     {    0.000000,  0.000000,  0.000000 },
     {    0.792800,  0.000410,  0.000000 },
     {    0.000205,  0.000000,  0.000000 }};
 double theta_acoeff[3][3] = { 
     { 2004.310900,  -0.853300, -0.000217 },
     {   -0.426650,  -0.000217,  0.000000 },
     {   -0.041833,   0.000000,  0.000000 } };
 ast_prec_angles_eval( Y_J2000, epoch1, epoch2, 
         &zeta_acoeff[0][0],
         &zz_acoeff[0][0],
         &theta_acoeff[0][0], n, euler );
}


void ast_prec_angles_b( double epoch1, double epoch2, double* euler )
{

 integer n = 2;
 double zeta_acoeff[3][3] = { 
     { 2304.250000,  1.396000,  0.000000 },
     {    0.302000,  0.000000,  0.000000 },
     {    0.018000,  0.000000,  0.000000 }};
 double zz_acoeff[3][3] = { 
     {    0.000000,  0.000000,  0.000000 },
     {    0.791000,  0.000000,  0.000000 },
     {    0.000000,  0.000000,  0.000000 }};
 double theta_acoeff[3][3] = { 
     { 2004.310900,  -0.853300,  0.000000 },
     {   -0.426650,   0.000000,  0.000000 },
     {   -0.042000,   0.000000,  0.000000 } };

 ast_prec_angles_eval( Y_B1900, epoch1, epoch2, 
         (double*)&zeta_acoeff[0][0], (double*)&zz_acoeff[0][0], (double*)&theta_acoeff[0][0], n, euler );
}


void ast_prec_angles_b_old( double epoch1, double epoch2, double* euler )
{

 integer n = 2;
 double zeta_acoeff[3][3] = { 
     { 2304.250000,  1.396000,  0.000000 },
     {    0.302000,  0.000000,  0.000000 },
     {    0.018000,  0.000000,  0.000000 }};
 double zz_acoeff[3][3] = { 
     {    0.000000,  0.000000,  0.000000 },
     {    0.791000,  0.000000,  0.000000 },
     {    0.000000,  0.000000,  0.000000 }};
 double theta_acoeff[3][3] = { 
     { 2004.682000,  -0.853000,  0.000000 },
     {   -0.426650,   0.000000,  0.000000 },
     {   -0.042000,   0.000000,  0.000000 } };
 ast_prec_angles_eval( Y_B1900, epoch1, epoch2, 
         (double*)&zeta_acoeff[0][0], (double*)&zz_acoeff[0][0], (double*)&theta_acoeff[0][0], n, euler );
}



