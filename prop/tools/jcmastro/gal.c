/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

/*
 *  L2/B2 system:  RA POLE = 192.25 (B1950.0)  INC = 62.6 (B1950.0) 
 *                 LONG NODE = 33.0
 */

void ast_gal_matrix_j( double* rmat )
{
 double grmat[9] = { 
      -0.054875539726,-0.873437108010,-0.483834985808,
      +0.494109453312,-0.444829589425,+0.746982251810,
      -0.867666135858,-0.198076386122,+0.455983795705 };
 utn_ar_copy_d( grmat, rmat, 9 );
}

void ast_gal_matrix_b( double* rmat )
{
 double grmat[9] = { 
    -0.066988739415,-0.872755765852,-0.483538914632,
    +0.492728466075,-0.450346958020,+0.744584633283,
    -0.867600811151,-0.188374601723,+0.460199784784 };
 utn_ar_copy_d( grmat, rmat, 9 );
}

void ast_cel_to_gal_b( double ra, double dec, double* ll, double* lb )
{
 double v1[3];
 double v2[3];
 double eterms[3];
 double t1950;
 double rmat[9];

 ast_gal_matrix_b( rmat );
 t1950 = cal_bepoch_to_jd( Y_B1950 );
 ast_earth_eterms( t1950, eterms );
 utn_ar_unitll_d( ra, dec, v1 );
 ast_sub_eterms( v1, eterms, v2 );
 utn_ar_lmat_postx_d( rmat, v2, v1 );
 utn_ar_polarll_d( v1, ll, lb );
}

void ast_gal_to_cel_b( double ll, double lb, double* ra, double* dec )
{
 double v1[3];
 double v2[3];
 double eterms[3];
 double t1950;
 double rmat[9];
 
 ast_gal_matrix_b( rmat );
 t1950 = cal_bepoch_to_jd( Y_B1950 );
 ast_earth_eterms( t1950, eterms );

 utn_ar_unitll_d( ll, lb, v1 );
 utn_ar_lmat_prex_d( rmat, v1, v2 );
 utn_ar_vadd_d( v2, eterms, v1 );
 utn_ar_polarll_d( v1, ra, dec );
}


void ast_cel_to_gal( double ra, double dec, double* ll, double* lb )
{
 double v1[3];
 double v2[3];
 double rmat[9];

 ast_gal_matrix_j( rmat );
 utn_ar_unitll_d( ra, dec, v1 );
 utn_ar_lmat_postx_d( rmat, v1, v2 );
 utn_ar_polarll_d( v2, ll, lb );
}

void ast_gal_to_cel( double ll, double lb, double* ra, double* dec )
{
 double v1[3];
 double v2[3];
 double rmat[9];
 
 ast_gal_matrix_j( rmat );
 utn_ar_unitll_d( ll, lb, v1 );
 utn_ar_lmat_prex_d( rmat, v1, v2 );
 utn_ar_polarll_d( v2, ra, dec );
}

/*
 *  For SG pole at 47.37, 6.32
 *  SG (0,0) at 137.37, 0
 */
void ast_gal_to_sgal( double ll, double lb, double* sgl, double* sgb )
{
 double rmat[9];
 double v1[3];
 double v2[3];

 ast_sgal_matrix( rmat );
 utn_ar_unitll_d( ll, lb, v1 );
 utn_ar_lmat_postx_d( rmat, v1, v2 );
 utn_ar_polarll_d( v2, sgl, sgb );
}

void ast_sgal_to_gal( double sl, double sb, double* gl, double* gb )
{
 double rmat[9];
 double v1[3];
 double v2[3];

 ast_sgal_matrix( rmat );
 utn_ar_unitll_d( sl, sb, v1 );
 utn_ar_lmat_prex_d( rmat, v1, v2 );
 utn_ar_polarll_d( v2, gl, gb );
}


void ast_sgal_matrix( double* rmat )
{
 double grmat[9]= {
   -0.735742574804,+0.677261296414,+0.000000000000,
   -0.074553778365,-0.080991471307,+0.993922590400,
   +0.673145302109,+0.731271165817,+0.110081262225  };

 utn_ar_copy_d( grmat, rmat, 9 );
}
