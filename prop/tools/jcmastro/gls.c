/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

void wcs_world_to_pixel( double* xw, char* ctype, double* crval,
 double* crpix, double* cdelt, double longpole, double* xp )
{
#define N_THETA 0
#define N_PHI   1
 double native[2];
 double physical[2] = { 0.0, 0.0 };
 wcs_world_to_native( xw, ctype, crval, longpole, native );
 wcs_native_to_physical( native, ctype, physical );
 wcs_physical_to_pixel( physical, cdelt, crpix, xp );
}

void wcs_pixel_to_world( double* xp, char* ctype, double* crval, 
 double* crpix, double* cdelt, double longpole, double* xw )
{
 double native[2];
 double physical[2];
 wcs_pixel_to_physical( xp, cdelt, crpix, physical );
 wcs_physical_to_native( physical, ctype, native );
 wcs_native_to_world( native, ctype, crval, longpole, xw );
}


void wcs_pixel_to_physical( double* xp, double* cdelt, double* crpix, double* physical )
{
 physical[0] = crpix[0] + xp[0] / cdelt[0];
 physical[1] = crpix[1] + xp[1] / cdelt[1];
}


void wcs_physical_to_pixel( double* physical, double* cdelt, double* crpix, double* xp )
{
 xp[0] = crpix[0] + physical[0]/cdelt[0];
 xp[1] = crpix[1] + physical[1]/cdelt[1];
}


void wcs_physical_to_native( double* physical, char* ctype, double* native )
{
 if ( utn_cs_eq( ctype, "GLS" )) 
  gls_inverse( physical, native );
 else
  utn_fio_tmsg( "CTYPE not supported" );

}

void wcs_native_to_physical( double* native, char* ctype, double* physical )
{
 if ( utn_cs_eq( ctype, "GLS" )) 
 {
  gls_project( native, physical );
 } else {
  utn_fio_tmsg( "CTYPE not supported" );
 }

}


void wcs_native_to_world( double* native, char* ctype, double* crval, double longpole, double* xw )
{
 double v1[3]; 
 double v2[3];
 double pole[2];
 double r[9];
/* n0 is theta n1 is phi */
 utn_ar_unitll_d( native[1], native[0], v1  );
 /* What are the coords of native North Pole? */
 if ( utn_cs_eq( ctype, "GLS" ))
 {
  pole[0] = 0.0;
  pole[1] = 90.0;
 } else {
  pole[0] = crval[0]; 
  pole[1] = crval[1];
 }
 utn_wcs_make_lrotate( pole, longpole, r );
 utn_ar_lmat_postx_d( r, v1, v2 );
 utn_ar_polarll_d( v2, &xw[0], &xw[1] );
}


void wcs_world_to_native( double* xw, char* ctype, double* crval, double longpole,
 double* native )
{
 double r[9];
 double v1[3];
 double pole[2];
 double phi,theta;
 double v2[3];
 if( utn_cs_eq( ctype, "GLS" ))
 {
  pole[0] = 0.0;
  pole[1] = 90.0;
 } else {
  pole[0] = crval[0];
  pole[1] = crval[1];
 }
 utn_wcs_make_lrotate( pole, longpole, r );
 utn_ar_unitll_d( xw[0], xw[1], v1 );
 utn_ar_lmat_prex_d( r, v1, v2 );
 utn_ar_polarll_d( v2, &phi, &theta );
 phi = utn_circ_ew_d( phi );
 native[0] = theta;
 native[1] = phi;
}



void gls_project( double* native, double* xw )
{
 xw[0] = native[1] * dcosd( native[0] );
 xw[1] = native[0];
}

void gls_inverse( double* xw, double* native )
{
 native[1] = circ_ew_d( xw[0] ) / dcosd( xw[1] );
 native[0] = xw[1];
}

void utn_wcs_make_lrotate( double* theta, double longpole, double* r )
{
 double euler[3];
 euler[0] = longpole;
 euler[1] = 90-0 - theta[1];
 euler[2] = 180.0 - theta[0];
 utn_ar_euler_rotate( euler, r );
}


