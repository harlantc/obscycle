/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
void utn_quat_from_matrix2( double* rotate, double* q );

void utn_ar_vcopy_d( const double* p, double* r )
{
 long n = 3;
 while( n-- ) 
 {
  *r = *p++; r++;
 }
}

void utn_ar_vzero_d( double* p )
{
 long n = 3;
 while( n-- )
 {  *p = 0.0; p++; }
}

void utn_ar_vadd_d( const double* p, const double* q, double* r )
{
 long n=0;
 for ( n =0; n<3; n++ )
  r[n] = p[n] + q[n];
}

void utn_ar_vsub_d( const double* p, const double* q, double* r )
{
 long n=0;
 for ( n =0; n<3; n++ )
  r[n] = p[n] - q[n];
}

void utn_ar_vcmult_d( double* p, const double c )
{
 long n = 3;
 while( n-- )
 { *p *= c; p++; }
}

void utn_ar_vdelta_d( const double* p, const double delta, const double* dp, double* r )
{
 long i;
 for ( i = 0; i < 3; i++ )
  r[i] = p[i] + delta * dp[i];
}


void utn_ar_vcdiv_d( double* p, const double c )
{
 long n = 3;
 while( n-- )
 {  *p /= c; p++; }
}


double utn_ar_vdot_d( const double* a, const double* b )
{
 return( a[0] * b[0] + a[1] * b[1] + a[2] * b[2] );
}

double utn_ar_vnorm_d( const double* a )
{
 return( sqrt( a[0]*a[0] + a[1]*a[1] + a[2]*a[2] ) );
}

void utn_ar_vcross_d( const double* a, const double* b, double* c )
{
 c[0] = a[1] * b[2] - a[2] * b[1];
 c[1] = a[2] * b[0] - a[0] * b[2];
 c[2] = a[0] * b[1] - a[1] * b[0];
}


double utn_ar_vmaxmod_d( const double* r )
{
 double result = 0;
 double a;
 long n = 3;
 while ( n-- ) { a = fabs( *r++ ); result = ( a > result ) ? a : result; }
 return( result );
}

double utn_ar_vunit_d( double* r )
{
 double r0;
 long n = 3;
 r0 = utn_ar_vnorm_d( r );
 if ( r0 > 0.0 ) {
  while( n-- ) { *r /= r0; r++; }
 }
 return( r0 );
}

double utn_ar_vcopy_unit_d( double* r, double* unit )
{
 double r0;
 long i;
 long n = 3;
 r0 = utn_ar_vnorm_d( r );
 if ( r0 > 0.0 ) 
 {
  for ( i = 0; i < n; i++ )
   unit[i] = r[i] / r0;
 }
 else
 {
  utn_ar_vzero_d( unit );
 }
 return( r0 );
}


float utn_ar_vdot_r( const float* a, const float* b )
{
 return( a[0] * b[0] + a[1] * b[1] + a[2] * b[2] );
}

float utn_ar_vnorm_r( const float* a )
{
 return( rsqrt( a[0]*a[0] + a[1]*a[1] + a[2]*a[2] ) );
}

void utn_ar_vcross_r( const float* a, const float* b, float* c )
{
 c[0] = a[1] * b[2] - a[2] * b[1];
 c[1] = a[2] * b[0] - a[0] * b[2];
 c[2] = a[0] * b[1] - a[1] * b[0];
}


float utn_ar_vmaxmod_r( const float* r )
{
 float result = 0;
 float a;
 long n = 3;
 while ( n-- ) { a = rabs( *r ); r++; result = ( a > result ) ? a : result; }
 return( result );
}

float utn_ar_vunit_r( float* r )
{
 float r0;
 long n = 3;
 r0 = utn_ar_vnorm_r( r );
 if ( r0 > 0.0 ) {
  while( n-- ) { *r /= r0; r++; }
 }
 return( r0 );
}




void utn_ar_vcopy_r( const float* p, float* r )
{
 long n = 3;
 while( n-- ) { *r = *p++; r++; }
}

void utn_ar_vzero_r( float* p )
{
 long n = 3;
 while( n-- ) { *p = 0.0; p++; }
}

void utn_ar_vadd_r( const float* p, const float* q, float* r )
{
 long n=0;
 for ( n =0; n<3; n++ )
  r[n] = p[n] + q[n];

}

void utn_ar_vsub_r( const float* p, const float* q, float* r )
{
 long n=0;
 for ( n =0; n<3; n++ )
  r[n] = p[n] - q[n];
}

void utn_ar_vcmult_r( float* p, const float c )
{
 long n = 3;
 while( n-- ) {*p *= c; p++; }
}


/* Linear matrices */
void utn_ar_lmat_prex_d( double* aa, const double* y, double* x )
{
 long i,j;
 long ji;
 for ( i = 0; i < 3; i++ ) {
  *x = 0.0;
  for( j = 0; j < 3; j++ ) {
   ji = 3 * j + i;
   *x += y[j] * aa[ji];
  }
  x++;
 }
}

void utn_ar_lmat_postx_d( double* aa, const double* y, double* x )
{
 long i,j;
 long ij;
 for ( i = 0; i < 3; i++ ) {
  *x = 0.0;
  for( j = 0; j < 3; j++ ) {
   ij = i * 3 + j;
   *x += y[j] * aa[ij];
  }
  x++;
 }
}

void utn_ar_mat_prex_d( double** aa, const double* y, double* x )
{
 long i,j;
 for ( i = 0; i < 3; i++ ) {
  *x = 0.0;
  for( j = 0; j < 3; j++ ) {
   *x += y[j] * aa[j][i];
  }
  x++;
 }
}

void utn_ar_mat_prex_r( float** aa, const float* y, float* x )
{
 long i,j;
 for ( i = 0; i < 3; i++ ) {
  *x = 0.0;
  for( j = 0; j < 3; j++ ) {
   *x += y[j] * aa[j][i];
  }
  x++;
 }
}


void utn_ar_mat_postx_d( double** aa, const double* y, double* x )
{
 long i,j;
 for ( i = 0; i < 3; i++ ) {
  *x = 0.0;
  for( j = 0; j < 3; j++ ) {
   *x += y[j] * aa[i][j];
  }
  x++;
 }
}

void utn_ar_mat_postx_r( float** aa, const float* y, float* x )
{
 long i,j;
 for ( i = 0; i < 3; i++ ) {
  *x = 0.0;
  for( j = 0; j < 3; j++ ) {
   *x += y[j] * aa[i][j];
  }
  x++;
 }
}

void utn_ar_mat_mult_d( double** a, double** b, double** c )
{
 long i,j,k;
 double w;
 for ( i = 0; i < 3; i++ ) {
  for( j = 0; j < 3; j++ ) {
   w = 0.0;
   for( k = 0; k < 3; k++ ) {
    w += a[i][k] * b[k][j];
   }
   c[i][j] = w;
  }
 }
}

void utn_ar_lmat_mult_d( double* a, double* b, double* c )
{
 long i,j,k;
 double w;
 for ( i = 1; i <= 3; i++ ) {
  for( j = 1; j <= 3; j++ ) {
   w = 0.0;
   for( k = 1; k <= 3; k++ ) {
    w += utn_ar_lmat33_d( a, i, k )  * utn_ar_lmat33_d( b, k, j );
   }
   utn_ar_lmat33_d( c, i, j ) = w;
  }
 }
}

void utn_ar_mat_mult_r( float** a, float** b, float** c )
{
 long i,j,k;
 float w;
 for ( i = 0; i < 3; i++ ) {
  for( j = 0; j < 3; j++ ) {
   w = 0.0;
   for( k = 0; k < 3; k++ ) {
    w += a[i][k] * b[k][j];
   }
   c[i][j] = w;
  }
 }
}


/* Forward translate/rotate 3-vector */
void utn_ar_transform_forward( const double* in, double* rotate, double* translate, double* out )
{
 long i,j;
 
 for ( i = 0; i < 3; i++ ) {
  out[i] = translate[i];  
  for ( j = 0; j < 3; j++ ) {
   out[i] += utn_ar_lmat33_d( rotate, j+1, i+1 ) * in[j];
  }
 }
}

void utn_ar_transform_backward( const double* in, double* rotate, double* translate, double* out )
{
 long i,j;
 for ( i = 0; i < 3; i++ ) {
  out[i] = 0.0;
  for ( j = 0; j < 3; j++ ) {
   out[i] += utn_ar_lmat33_d( rotate, i+1, j+1 ) * ( in[j] - translate[j] );
  }
 }
}
 

void utn_ar_euler_rotate( double* euler, double* rotate )
{
 long axes[3] = { 1, 2, 3 };
/* ZYZ */
 utn_ar_euler_rotate_gen( euler, axes, rotate );
}

void utn_ar_euler_rotate_alt( double* euler, double* rotate )
{
/* XYX */
 long axes[3] = { 3, 2, 1 };
 utn_ar_euler_rotate_gen( euler, axes, rotate );
}

void utn_ar_euler_rotate_gen( double* euler, long* axes, double* rotate )
{
 double cphi;
 double ctheta;
 double cpsi;
 double sphi;
 double stheta;
 double spsi;
 long a1,a2,a3;

 a1 =axes[0];
 a2 =axes[1];
 a3 =axes[2];

/* Accelerated case */
 if ( euler[0] == 270.0 && euler[2] == 90.0 ) 
 {
  cphi =  0.0;
  sphi = -1.0;
  ctheta=dcosd( euler[1] );
  stheta=dsind( euler[1] );
  cpsi  = 0.0;
  spsi  = 1.0;
 } else {
  cphi = dcosd( euler[0] );
  sphi = dsind( euler[0] );
  ctheta=dcosd( euler[1] );
  stheta=dsind( euler[1] );
  cpsi  =dcosd( euler[2] );
  spsi  =dsind( euler[2] );
 }
 utn_ar_lmat33_d( rotate, a1, a1 ) =   cphi * ctheta * cpsi - sphi * spsi;
 utn_ar_lmat33_d( rotate, a1, a2 ) =   sphi * ctheta * cpsi + cphi * spsi;
 utn_ar_lmat33_d( rotate, a1, a3 ) =        - stheta * cpsi;
 utn_ar_lmat33_d( rotate, a2, a1 ) = - cphi * ctheta * spsi - sphi * cpsi;
 utn_ar_lmat33_d( rotate, a2, a2 ) = - sphi * ctheta * spsi + cphi * cpsi;
 utn_ar_lmat33_d( rotate, a2, a3 ) =          stheta * spsi;
 utn_ar_lmat33_d( rotate, a3, a1 ) =   cphi * stheta;
 utn_ar_lmat33_d( rotate, a3, a2 ) =   sphi * stheta;
 utn_ar_lmat33_d( rotate, a3, a3 ) =          ctheta;
}



void utn_ar_euler_calc( double* r, double* euler )
{
 long axes[3] = { 1, 2, 3 };
 utn_ar_euler_calc_gen( r, axes, euler );
}

void utn_ar_euler_calc_gen( double* r, long* axes, double* euler )
{
 double phi, theta, psi;
 double s1, s2, s;
 double r31, r32, r13, r23;
 double x, y;
 long a1,a2,a3; 

 a1 = axes[0];
 a2 = axes[1];
 a3 = axes[2];
 r31 = utn_ar_lmat33_d( r, a3, a1 );
 r32 = utn_ar_lmat33_d( r, a3, a2 );
 r13 = utn_ar_lmat33_d( r, a1, a3 );
 r23 = utn_ar_lmat33_d( r, a2, a3 );
 s1 = sqrt( r31 * r31 + r32 * r32 );
 s2 = sqrt( r13 * r13 + r23 * r23 );
 s  = 0.5 * ( s1 + s2 );

 theta = argd_d(  utn_ar_lmat33_d( r, a3, a3 ), s );
 if ( s1 > 0.0 && s2 > 0.0 )
 {
  x =  -utn_ar_lmat33_d( r, a1, a3 ) / s2;
  y =   utn_ar_lmat33_d( r, a2, a3 ) / s2;
  psi = argd_d( x, y );
  x =   utn_ar_lmat33_d( r, a3, a1 ) / s1; 
  y =   utn_ar_lmat33_d( r, a3, a2 ) / s1;
  phi = argd_d( x, y );
 } else {
  phi = argd_d( utn_ar_lmat33_d( r, a1, a1 ), utn_ar_lmat33_d( r, a1, a2 ) );
  psi = 0.0;
 }
 euler[0] = phi;
 euler[1] = theta;
 euler[2] = psi;

}

double utn_quat_unit( double* p )
{
 double norm;
 norm = sqrt( utn_ar_dot_d( p, p, 4 ) );
 if ( norm > 0.0 )
 {
  utn_ar_cmult_d( p, 4, 1.0/norm );
 }
 return norm;
}


double utn_quat_dot( double* p, double* q )
{
 return utn_ar_dot_d( p, q, 4 );
}

/* Multiply quaternions */
void utn_quat_mult( double* p, double* q, double* r )
{
 r[0] = p[0] * q[0] - utn_ar_dot_d( p+1, q+1, 3 );
 utn_ar_vcross_d( p+1, q+1, r+1 );
 utn_ar_vdelta_d( r+1,  p[0], q+1, r+1 );
 utn_ar_vdelta_d( r+1,  q[0], p+1, r+1 );
}

void utn_quat_rot( double theta, double* n, double* p )
{
 double s;

 p[0] =  dcosd( 0.5 * theta );
 s = dsind( 0.5 * theta );
 p[1] =  s * n[0];
 p[2] =  s * n[1];
 p[3] =  s * n[2];
}

void utn_asp_to_mat( double a, double d, double r, double* T );

void utn_quat_from_asp( double ra, double dec, double roll, double* q )
{
 double T[9];
 utn_asp_to_mat( ra, dec, roll, T );
 utn_quat_from_matrix( T, q );
}


void utn_asp_to_mat( double a, double d, double r, double* T )
{

 double ca,cd,cr,sr,sa,sd;

 ca = dcosd( a );
 sa = dsind( a );
 cd = dcosd( d );
 sd = dsind( d );
 cr = dcosd ( r);
 sr = dsind( r );

 T[0] = ca * cd;
 T[1] = sa * cd;
 T[2] = sd;
 T[3] = -ca * sd * sr - sa * cr;
 T[4] = -sa * sd * sr + ca * cr;
 T[5] = cd * sr;
 T[6] = -ca * sd * cr + sa * sr;
 T[7] = -sa * sd * cr - ca * sr;
 T[8] = cd * cr;

}


void utn_quat_to_asp( double* q, double* ra, double* dec, double* roll )
{
 double xa,xb,xn,yn,zn, xn2;
 double ddec;
 xa = q[0]*q[0] - q[1]*q[1] -q[2]*q[2] + q[3]*q[3];
 xb = 2 * ( q[0] * q[1] + q[2] * q[3] );
 xn = 2 * ( q[0] * q[2] - q[1] * q[3] );
 yn = 2 * ( q[1] * q[2] + q[0] * q[3] );
 zn = q[3] * q[3] + q[2] * q[2] - q[0] *q[0] - q[1] *q[1];
 xn2 = sqrt( 1 - xn * xn );
 *ra = argd_d( xa, xb );
 ddec = argd_d( xn2, xn );
 if ( ddec > 180.0 )
  ddec = ddec -360.0;
 *dec = ddec;
 *roll = argd_d( zn, yn );
}

#if 0
void utn_quat_to_asp( double* q, double* ra, double* dec, double* roll )
{
 double xa,xb,xn,yn,zn, xn2;
 double rotate[9];
 utn_quat_to_matrix( q, rotate );
 xa =  utn_ar_lmat33_d( rotate, 3, 3 );
 xb =  utn_ar_lmat33_d( rotate, 3, 2 );
 xn = -utn_ar_lmat33_d( rotate, 3, 1 );
 yn =  utn_ar_lmat33_d( rotate, 2, 1 );
 zn = -utn_ar_lmat33_d( rotate, 1, 1 );
 xn2 = sqrt( 1 - xn * xn );
 *ra = argd_d( xa, xb );
 *dec = argd_d( xn2, xn );
 *roll = argd_d( zn, yn );
}
#endif

void utn_quat_to_matrix( double* q, double* rotate )
{
 utn_ar_lmat33_d( rotate, 1, 1 ) =   q[0] * q[0] + q[1] * q[1] - q[2] * q[2] - q[3] * q[3];
 utn_ar_lmat33_d( rotate, 1, 2 ) =   2 * ( -q[0] * q[3] + q[1] * q[2] );
 utn_ar_lmat33_d( rotate, 1, 3 ) =   2 * (  q[0] * q[2] + q[1] * q[3] );
 utn_ar_lmat33_d( rotate, 2, 1 ) =   2 * (  q[0] * q[3] + q[2] * q[1] );
 utn_ar_lmat33_d( rotate, 2, 2 ) =   q[0] * q[0] - q[1] * q[1] + q[2] * q[2] - q[3] * q[3];
 utn_ar_lmat33_d( rotate, 2, 3 ) =   2 * ( -q[0] * q[1] + q[2] * q[3] );
 utn_ar_lmat33_d( rotate, 3, 1 ) =   2 * ( -q[0] * q[2] + q[3] * q[1] );
 utn_ar_lmat33_d( rotate, 3, 2 ) =   2 * (  q[0] * q[1] + q[3] * q[2] );
 utn_ar_lmat33_d( rotate, 3, 3 ) =  q[0] * q[0] - q[1] * q[1] - q[2] * q[2] + q[3] * q[3];
}



void utn_quat_from_matrix2( double* rotate, double* q )
{
 double qq[4];
 long qi;
 double qx;
 double r11,r22,r33;
 r11 = utn_ar_lmat33_d( rotate, 1, 1 );
 r22 = utn_ar_lmat33_d( rotate, 2, 2 );
 r33 = utn_ar_lmat33_d( rotate, 3, 3 );
 qi = 0;
 qq[0] = 1 + r11 + r22 + r33;
 qq[1] = 1 + r11 - r22 - r33;
 if ( qq[1] > qq[qi] ) qi = 1;
 qq[2] = 1 - r11 + r22 - r33;
 if ( qq[2] > qq[qi] ) qi = 2;
 qq[3] = 1 - r11 - r22 + r33;
 if ( qq[3] > qq[qi] ) qi = 3;

 qx = sqrt( qq[qi] / 4.0 );
 
 switch ( qi )
 {
  case 0:
   q[0] = qx;
   q[1] = 0.25 * ( utn_ar_lmat33_d( rotate, 3, 2 ) - utn_ar_lmat33_d( rotate, 2, 3 ) ) / qx;
   q[2] = 0.25 * ( utn_ar_lmat33_d( rotate, 1, 3 ) - utn_ar_lmat33_d( rotate, 3, 1 ) ) / qx;
   q[3] = 0.25 * ( utn_ar_lmat33_d( rotate, 2, 1 ) - utn_ar_lmat33_d( rotate, 1, 2 ) ) / qx;
   break;
  case 1:
   q[0] = 0.25 * ( utn_ar_lmat33_d( rotate, 3, 2 ) - utn_ar_lmat33_d( rotate, 2, 3 ) ) / qx;
   q[1] = qx;
   q[2] = 0.25 * ( utn_ar_lmat33_d( rotate, 1, 2 ) + utn_ar_lmat33_d( rotate, 2, 1 ) ) / qx;
   q[3] = 0.25 * ( utn_ar_lmat33_d( rotate, 1, 3 ) + utn_ar_lmat33_d( rotate, 3, 1 ) ) / qx;
   break;
  case 2:
   q[0] = 0.25 * ( utn_ar_lmat33_d( rotate, 1, 3 ) - utn_ar_lmat33_d( rotate, 3, 1 ) ) / qx;
   q[1] = 0.25 * ( utn_ar_lmat33_d( rotate, 1, 2 ) + utn_ar_lmat33_d( rotate, 2, 1 ) ) / qx;
   q[2] = qx;
   q[3] = 0.25 * ( utn_ar_lmat33_d( rotate, 2, 3 ) + utn_ar_lmat33_d( rotate, 3, 2 ) ) / qx;
   break;
  case 3:
   q[0] = 0.25 * ( utn_ar_lmat33_d( rotate, 2, 1 ) - utn_ar_lmat33_d( rotate, 1, 2 ) ) / qx;
   q[1] = 0.25 * ( utn_ar_lmat33_d( rotate, 1, 3 ) + utn_ar_lmat33_d( rotate, 3, 1 ) ) / qx;
   q[2] = 0.25 * ( utn_ar_lmat33_d( rotate, 2, 3 ) + utn_ar_lmat33_d( rotate, 3, 2 ) ) / qx;
   q[3] = qx;
   break;   
 }
}


void utn_ar_lmatnn_prex( double* v1, double* r, double* v2, long n )
{
 long i, j;

 double w;
 double a;
 for ( i =1; i <= n; i++ )
 {
  w = 0.0;
  for ( j = 1; j <= n; j++ )
  {
   a = utn_ar_lmatnn_d( r, n, i, j );
   w += a * v1[j-1];
  }
  v2[i-1] = w;
 }
}

void utn_ar_lmatnn_postx( double* v1, double* r, double* v2, long n )
{
 long i, j;
 double a;
 double w;

 for ( i =1; i <= n; i++ )
 {
  w = 0.0;
  for ( j = 1; j <= n; j++ )
  {
   a = utn_ar_lmatnn_d( r, n, j, i );
   w += a * v1[j-1];
  }
  v2[i-1] = w;
 }
}

void utn_quat_from_matrix( double* rotate, double* q )
{
 double den[4];
  double max_den;
 double denom;
 integer index = 0;
 integer i;
 den[0] = 1.0 + rotate[0] - rotate[4] - rotate[8];
 den[1] = 1.0 - rotate[0] + rotate[4] - rotate[8];
 den[2] = 1.0 - rotate[0] - rotate[4] + rotate[8];
 den[3] = 1.0 + rotate[0] + rotate[4] + rotate[8];
 max_den = -1.0;
  for (i = 0; i < 4; i++)
    {
      if (den[i] > max_den)
        {   
          max_den = den[i];
          index = i;
        }
    }
 q[index] = 0.5 * sqrt( max_den);
 denom = 4.0 * q[index];    

    
  switch (index)
    {
    case 0: 
      q[1] = (rotate[1] + rotate[3]) / denom;
      q[2] = (rotate[2] + rotate[6]) / denom;
      q[3] = (rotate[5] - rotate[7]) / denom;
      break;
    case 1:
      q[0] = (rotate[1] + rotate[3]) / denom;
      q[2] = (rotate[5] + rotate[7]) / denom;
      q[3] = (rotate[6] - rotate[2]) / denom;
      break;
    case 2:
      q[0] = (rotate[2] + rotate[6]) / denom;
      q[1] = (rotate[5] + rotate[7]) / denom;
      q[3] = (rotate[1] - rotate[3]) / denom;
      break;
    case 3:
      q[0] = (rotate[5] - rotate[7]) / denom;
      q[1] = (rotate[6] - rotate[2]) / denom;
      q[2] = (rotate[1] - rotate[3]) / denom;
      break;
    }



}

