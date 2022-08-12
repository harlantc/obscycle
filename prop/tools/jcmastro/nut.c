/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
#include <ctype.h>
#define AST_100MU 10000
#define T2AS 1296000



void ast_nut_eval_poly( double d, double elp, double el, double om, double f, 
             double t, NutCoeffs* nut, double* psi, double* eps );



void ast_nut_free( NutCoeffs* nut )
{
 free( nut );
}
/*

*     Greenwich apparent ST = GMST + sla_EQEQX
*  
*  References:  IAU Resolution C7, Recommendation 3 (1994)
*               Capitaine, N. & Gontier, A.-M., Astron. Astrophys.,
*               275, 645-650 (1993)
*

 */
double ast_eqeqx( NutCoeffs* nut, double t )
{
 double dpsi, deps, eps0;
/*  The result is the equation of the equinoxes (REAL*8)
!  in degrees
!     Greenwich apparent ST = GMST + EQEQX
 */
  double e;
  double om; 
  double coeff[2] = { 0.0264, 0.000063 };
  double angles[3];
  ast_nut_calc( nut, t, angles );
  dpsi = angles[0];
  deps = angles[1];
  eps0 = angles[2];
  om = ast_lunar_mean_node ( t );
  e =  dpsi * dcosd( eps0 ) + 
         ( coeff[0] * dsind( om ) + coeff[1] * dsind( 2 * om ) )/AST_SEC_PER_DEG;
  return e;
}

void ast_nut_matrix( NutCoeffs* nut, double t, double* rmatn )
{
/*
!  Form the matrix of nutation for a given date (IAU 1980 theory)
!  (REAL*8)
!  References:
!     Final report of the IAU Working Group on Nutation,
!      chairman P.K.Seidelmann, 1980.
!     Kaplan,G.H., 1981, USNO circular no. 163, pA3-6.
!  Given:
!     DATE   dp         TDB (loosely ET) as Modified Julian Date
!                                           (=JD-2400000.5)
!  Returned:
!     RMATN  dp(3,3)    nutation matrix
!  The matrix is in the sense   V(true)  =  RMATN * V(mean) .
*/
 double angles[3];
 ast_nut_calc( nut, t, angles );
 utn_ar_euler_rotate_alt( angles, rmatn );
}

void ast_nut_calc( NutCoeffs* nut, double t, double* angles )
{
/*
!  Nutation:  longitude & obliquity components and
!             mean obliquity (IAU 1980 theory)
!  (REAL*8)
!  References:
!     Final report of the IAU Working Group on Nutation,
!      chairman P.K.Seidelmann, 1980.
!     Kaplan,G.H., 1981, USNO circular no. 163, pA3-6.
!  Given:
!     DATE        dp    TDB (loosely ET) as Modified Julian Date
!                                            (JD-2400000.5)
!  Returned:
!     DPSI,DEPS   dp    nutation in longitude,obliquity
!     EPS0        dp    mean obliquity
*/
   NutCoeffs* local_nut;
   double elp, f, d, om, dp, de;
   double el;

   local_nut = nut;
   if ( !local_nut )
    local_nut = ast_nut_read( NULL );


   el =  ast_lunar_mean_longitude_from_peri( t );
   elp = ast_solar_mean_longitude_from_peri( t );
   f   = ast_lunar_mean_longitude_from_node( t );
   d   = ast_lunar_mean_elongation( t );
   om  = ast_lunar_mean_node( t );
   ast_nut_eval_poly( d, elp, el, om, f, t, local_nut, &dp, &de );

   angles[0] = ast_obl_ecliptic( t ); /* EPS0 */
   angles[1] = -dp;  /* DPSI */
   angles[2] = angles[0] + de - 90.0;  /* DEPS */

   if ( !nut )
    free( local_nut );
}

 
NutCoeffs* ast_nut_read( char* filename )
{
 FioFile in;
 long i;
 long n;
 TextCard line;
 char* ptr;
 char* default_filename = "nut.data"; /* Default Nutation filename */

 NutCoeffs* nut = calloc( 1, sizeof( NutCoeffs ));
 if ( !filename ) filename = default_filename;

 in = utn_fio_open_ar( filename );
 n = 0;
 
 while( utn_fio_read_line( in, line, CARD_SIZE ))
 {
  ptr = line;
  if (isdigit(line[0]))
  {
   i = utn_cs_get_i(&ptr);
   if ( i != n+1 ) printf( "NUT format error\n" );
   nut->d[n] = utn_cs_get_i(&ptr);
   nut->elp[n]= utn_cs_get_i(&ptr);
   nut->el[n] = utn_cs_get_i(&ptr);
   nut->om[n] = utn_cs_get_i(&ptr);
   nut->f[n] = utn_cs_get_i(&ptr);
   nut->p0[n]= utn_cs_get_d(&ptr);
   nut->e0[n]= utn_cs_get_d(&ptr);
   nut->p1[n]= utn_cs_get_d(&ptr);
   nut->e1[n]= utn_cs_get_d(&ptr);
   n++;
   if ( n >= AST_NUT_MAX ) printf( "NUT format error\n" );
  }
 }
 nut->n = n;
 utn_fio_file_close( in );
 return nut;
}

void ast_nut_eval_poly( double d, double elp, double el, double om, double f, 
             double t, NutCoeffs* nut, double* psi, double* eps )
{
 long i;
 double dp; /* Result in 100 muas */
 double de;
 double centuries;
 double a, p, e;
 double dr,elpr,elr,omr,fr; /* Args in radians */

 dr  = UT_DEG_RAD * d;
 elpr= UT_DEG_RAD * elp;
 elr = UT_DEG_RAD * el;
 omr = UT_DEG_RAD * om;
 fr  = UT_DEG_RAD * f;
 centuries = cal_jc( t );
 dp = 0.0;
 de = 0.0;
 for ( i = 0; i < nut->n; i++ )
 {
  a = nut->d[i]   * dr 
     +nut->elp[i] * elpr
     +nut->el[i]  * elr
     +nut->om[i]  * omr
     +nut->f[i]   * fr;
  p = nut->p0[i] + centuries * nut->p1[i];
  e = nut->e0[i] + centuries * nut->e1[i];
  if ( p != 0.0 )
   dp += p * sin( a );
  if ( e != 0.0 )
   de += e * cos( a );
 }
 *psi = dp / ( AST_100MU * AST_SEC_PER_DEG );
 *eps = de / ( AST_100MU * AST_SEC_PER_DEG );
}




/*
Space from -4, 4 in 5 axes:  9 x 9 x 9 x 9 x 9
          
elp  -1 2   elp+1:  0 3
el   -2 3   el+2 :  0 5
d    -4 4   d+4:    0 8
om    0 2   om:     0 2
f    -2 4   f+2     0 6

ex = (elp+1)*6 + (el+2);  = 0, 23
df = (d+4)*7 + (f+2)      = 0,62
ox = ex * 3 + om;      71
 */
