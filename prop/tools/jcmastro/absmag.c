/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"


double frw_ldistf( double z, double omega, double h0 )
{
 double f;
 double d;
 if ( z < 1.0E-3 )
 {
  f = z;
 } else if ( omega == 0.0 ) {
  f = z * ( 1 + 0.5 * z );
 } else if ( omega == 1.0 ) {
  f = 2.0 * ( 1.0 + z - sqrt( 1.0 + z ) );
 } else {
  f = 2.0 * ( omega * z + (2.0 - omega) * ( 1.0 - sqrt( 1.0 + omega* z )))/
                    omega * omega;
 }
 d = 6.0e9 * ( 50.0 / h0 ) * f;
 return d;
}

double ast_absmag( K_Correction* kcor, double v, double z, double omega, double h0 )
{
/*
c Routines to get absolute V mag from V and Z
*/
 double dm;
 double dpc;
 double k;
 double a;
 if ( z == 0.0 || utn_qnull_d( z ) || utn_qnull_d( v ) )
 {
  a = utn_null_d( );
  return a;
 }
  dpc = frw_ldistf( z, omega, h0 );
/* K correction */
  if ( kcor && kcor->alpha != 0.0 )
  {
/* Alpha is 1 - power index */
   k = -2.5 * kcor->alpha * log10( 1.0 + z );
  } else {
   k = 0.0;
  }
/* Line correction */
  if ( kcor && kcor->n > 0 )
   dm = utn_ar_linterp_d( kcor->z, kcor->mv, kcor->n, z );
  else
   dm = 0.0;

  a = v - 5.0 * log10( dpc / 10.0 ) - k + dm;
  return a;
}

void ast_absmag_free( K_Correction* k )
{
 free( k );
}

K_Correction* ast_absmag_init( char* infile, double alpha )
{
 K_Correction* kcor;
 FioFile in;
 TextCard line;
 char* ptr;
 kcor = (K_Correction*)calloc( 1, sizeof( K_Correction ));
 kcor->n = 0;
 kcor->alpha = 1.0-alpha;
 if ( !utn_cs_is_blank( infile ))
 {
   in = utn_fio_open_ar( infile );
   while( utn_fio_read_line( in, line, CARD_SIZE ))
   {
    ptr = line;
    kcor->z[kcor->n] = utn_cs_get_r( &ptr );
    kcor->mv[kcor->n]= utn_cs_get_r( &ptr );
    kcor->n++;
   }     
   utn_fio_file_close( in );
 }
 return kcor;
}


/* Asteroid sizes from absolute mag - MPC estimate */
/* Value in km; mode = -1, 0, +1 gives lower, mean, upper estimates */
double asteroid_diameter_estimate( double H, integer mode )
{
 double d;
 double ld;
 integer offset;
 double slope = -0.200;
 double zero[3] = { 3.421, 3.634, 3.778 };
 if ( mode < 0 )
  offset = 0;
 else if ( mode > 0 )
  offset = 2;
 else
  offset = 1;

 ld = zero[offset] + slope * H;
 d = pow( 10.0, ld );
 return d;
}
