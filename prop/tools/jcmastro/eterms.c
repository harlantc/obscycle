/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
/* 
 * ETERMS: Elliptic component of annual aberration  
 *   Get the B1950 eterms state per the old SLALIB approach
 */
void ast_earth_eterms_b( double* eterms )
{
 double eterms_data[6] = { -1.62557e-6,  -0.31919e-6, -0.13843e-6,
                           +1.245e-3,    -1.580e-3,   -0.659e-3 };
 utn_ar_copy_d( eterms_data, eterms, 6 );
}
/* The new SLALIB approach */

void ast_earth_eterms( double t, double* eterms )
{
 double ecc, peri, obl;

 ecc = ast_earth_ecc( t );
 peri = ast_earth_perihelion( t );
 obl = ast_obl_ecliptic( t );
 ast_calc_eterms( ecc, peri, obl, eterms );
}


/* Correct position vector for eterms */
void ast_sub_eterms( double* p1, double* eterms, double* p2 )
{
 double w;
 integer i;

 w = 1.0 + utn_ar_vdot_d( p1, eterms );
 for ( i =0; i < 3; i++ ) 
  p2[i] = w * p1[i] - eterms[i]; 

}

/* Correct position vector for eterms */
void ast_sub_eterms6( double* p1, double* eterms, double* p2 )
{
 double w;
 double wd;
 integer i;

 w = 1.0 + utn_ar_vdot_d( p1, eterms );
 wd = utn_ar_vdot_d( p1+3, eterms+3 );
 for ( i =0; i < 3; i++ ) 
 {
  p2[i] = w * p1[i] - eterms[i]; 
  p2[i+3]= p1[i+3] - eterms[i+3] + wd * p1[i];
 }
}

/* Add back in eterms. We iterate twice for accuracy. */

void ast_add_eterms6( double* v1, double* eterms, double* v2 )
{
 double r;
 double w;
 double wd;
 integer i;
 integer iter;

 utn_ar_copy_d( v1, v2, 6 ); 

 for ( iter = 1; iter <= 2; iter++ )
 {
  r = utn_ar_vnorm_d( v2 );  /* 3D norm */
  w = 1.0 - utn_ar_vdot_d( v2, eterms );
/* Apply eterms to position, recalculate radius vector */
  for ( i = 0; i< 3; i++ )
   v2[i] = w * v2[i] + r * eterms[i];
 
  if ( iter == 2 )
  {
   wd= utn_ar_vdot_d( v2, eterms+3 );
   for ( i = 0; i <3; i++ )
     v2[i+3] = v2[i+3] - wd * v2[i] + r * eterms[i+6];
  }
 }

}



void ast_add_eterms6_old( double* v1, double* eterms, double* v2 )
{
 double r;
 integer i;

 r = utn_ar_vnorm_d( v1 );  /* 3D norm */
/* Apply eterms to position, recalculate radius vector */
 for ( i = 0; i< 3; i++ )
   v2[i] = v1[i] + r * eterms[i];

}



void ast_calc_eterms( double ecc, double peri, double obl, double* eterms )
{
 double cp,sp;
 double co,so;
 double e;
 cp = dcosd( peri );
 sp = dsind( peri );
 co = dcosd( obl );
 so = dsind( obl );
 e = ecc * AST_ABERR_J2000;
 eterms[0] =  e * sp;
 eterms[1] = -e * cp*co;
 eterms[2] = -e * cp*so;
}

