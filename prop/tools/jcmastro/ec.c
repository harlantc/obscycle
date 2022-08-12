/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"



void ast_cel_to_ec( double ra, double dec, double equinox, double* el, double* eb )
{
 double rmat[9];
 double r[3];
 double r2[3];
 double t;

 ast_prec_matrix_j( Y_J2000, equinox, rmat );
 utn_ar_unitll_d( ra, dec, r );
 utn_ar_lmat_postx_d( rmat, r, r2 );
 t = cal_jepoch_to_jd( equinox );
 ast_ec_mat( t, rmat );
 utn_ar_lmat_postx_d( rmat, r2, r );
 utn_ar_polarll_d( r, el, eb );
}

void ast_ec_to_cel( double el, double eb, double equinox, double* ra, double* dec )
{
 double rmat[9];
 double r[3];
 double r2[3]; 
 double t;
 utn_ar_unitll_d( el, eb, r );
 t = cal_jepoch_to_jd( equinox );
 ast_ec_mat( t, rmat );
 utn_ar_lmat_prex_d( rmat, r, r2 );
 ast_prec_matrix_j( Y_J2000, equinox, rmat );
 utn_ar_lmat_prex_d( rmat, r2, r );
 utn_ar_polarll_d( r, ra, dec );
}

void ast_ec_mat( double t, double* rmat )
{
 double euler[3];

 euler[0] = 270.0;
 euler[1] = ast_obl_ecliptic( t );
 euler[2] = 90.0;
 utn_ar_euler_rotate( euler, rmat );
}


