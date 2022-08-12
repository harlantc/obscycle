/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "unitlib.h"
#include "si.h"

/* Return the SI prefix char* */


void utn_si_getpfx( long exponent, char* pfx, long* remainder )
{
 long si_step;
 *remainder = 0;
 if ( exponent == 2 ) {
  *pfx = ut_constants_siu[s_hecto];
 } else if ( exponent == 1 ) {
  *pfx = ut_constants_siu[s_deka];
 } else if ( exponent == -1 ) {
  *pfx = ut_constants_siu[s_deci];
 } else if ( exponent == -2 ) {
  *pfx = ut_constants_siu[s_centi];
 } else if ( exponent >= -UT_SI_NPFX && exponent <= UT_SI_NPFX ) {  
  si_step = rmod_i( exponent, remainder, 3 );
  *pfx = ut_constants_siu[si_step+UT_SI_NPFX];
 } else {
  *pfx = ' ';
  *remainder = exponent;
 }
}

/*
 *   Return the SI exponent given the prefix.
 *   Also return a status, which is false if the prefix
 *   is not valid.
 */
long utn_si_exp( char* pfx, logical* ok )
{
 long pos;
 long step;
 *ok = UT_TRUE;
 for ( pos = -UT_SI_NPFX; pos <= +UT_SI_NPFX+4; pos++ ) {
  step = pos + UT_SI_NPFX;
  if ( *pfx == ut_constants_siu[step] ) { 
   *ok = UT_TRUE;
   return( ut_constants_siv[step] );
  }
 }

 if ( utn_cs_eq( pfx, UT_SI_MICRO ) ) {
  return( -6 );
 }
 *ok = UT_FALSE;
 return( 0 );
}


long utn_si_eval_r( float x )
{
 long exponent;
 float exponent1; 
 float rem;
 if ( x == 0.0 ) {
  exponent = 0;
 } else {
  exponent1 = rlog10( rabs( x ) );
  exponent = 3 * rmod_r( exponent1, &rem, 3.0 );
 } 
 return( exponent );
}

long utn_si_eval_d( double x )
{
 long exponent;
 double exponent1;
 double rem;
 if ( x == 0.0 ) {
  exponent = 0;
 } else {
  exponent1 = log10( fabs( x ) );
  exponent = 3 * rmod_d( exponent1, &rem, 3.0 );
 } 
 return( exponent );
}


void utn_si_calc_d( double x, double* y, char* pfx, long* exponent, char* mode )
{
 long ex;
 long si;

 *pfx = ' ';
 *(pfx+1) = '\0';
 ex = utn_si_eval_d( x );
 si = ex / 3;
 if ( abs( si ) <= UT_SI_NPFX ) {
  *y = x / tpow_d( ex );
  if ( si == -2 && utn_cs_ss_char( mode, 'u' ) ) {
   utn_cs_copy( UT_SI_MICRO, pfx );
  } else if ( si == -1 && fabs( *y ) >= 10.0  && utn_cs_ss_char( mode, 'c' ) ) {
   *y /= 10.0;
   ex = -2;
   *pfx = ut_constants_siu[s_centi];
  } else {
   *pfx = ut_constants_siu[si + UT_SI_NPFX];
  }
 } else {
  *y = x;
  *pfx = '-';
  ex = 0;
 }
 *exponent = ex;
}



void utn_si_write_d( double x, char* cy )
{
 long exponent;
 const long siz = 9;
 char pfx[4];
 double y;
 utn_si_calc_d( x, &y, pfx, &exponent, " " );
 if ( *pfx == '-' ) {
  utn_cs_writef_d( x, "%8.2E ", cy, siz );
 } else {
  utn_cs_writef_d( y, "%7.2f", cy, siz );
  cy[8] = *pfx;
 }
}

double utn_si_read_d( char* cy )
{
 TextWord cy1;
 logical ok;
 char last_char;
 long exponent;
 double value;
 double scaled_value;
 long slen;
 slen = utn_cs_ends( cy );
 value = 0.0;
 last_char = cy[slen-1];
 if ( utn_cs_digit_char( last_char ) ) {
  value = utn_cs_read_d( cy );
 } else {
  if ( slen == 1 ) {
   scaled_value = 1.0;
  } else {
   utn_cs_copy_siz( cy, cy1, slen-1 );
   scaled_value = utn_cs_read_d( cy1 );
  }
  exponent = utn_si_exp( cy+slen-1, &ok );
  if ( !ok ) {
   value = utn_null_d();
  } else {
   value = scaled_value * tpow_d( exponent );
  }
 }
 return( value );
}


void utn_si_calc_r( float x, float* y, char* pfx, long* exponent, char* mode )
{
 long ex;
 long si;

 *pfx = ' ';
 *(pfx+1) = '\0';
 ex = utn_si_eval_r( x );
 si = ex / 3;
 if ( abs( si ) <= UT_SI_NPFX ) {
  *y = x / tpow_r( ex );
  if ( si == -2 && utn_cs_ss_char( mode, 'u' ) ) {
   utn_cs_copy( UT_SI_MICRO, pfx );
  } else if ( si == -1 && rabs( *y ) >= 10.0  && utn_cs_ss_char( mode, 'c' ) ) {
   *y /= 10.0;
   ex = -2;
   *pfx = ut_constants_siu[s_centi];
  } else {
   *pfx = ut_constants_siu[si + UT_SI_NPFX];
  }
 } else {
  *y = x;
  *pfx = '-';
  ex = 0;
 }
 *exponent = ex;
}



void utn_si_write_r( float x, char* cy )
{
 long exponent;
 const long siz = 9;
 char pfx[4];
 float y;
 utn_si_calc_r( x, &y, pfx, &exponent, " " );
 if ( *pfx == '-' ) {
  utn_cs_writef_r( x, "%8.2E ", cy, siz );
 } else {
  utn_cs_writef_r( y, "%7.2f", cy, siz );
  cy[8] = *pfx;
 }
}

float utn_si_read_r( char* cy )
{
 TextWord cy1;
 logical ok;
 char last_char;
 long exponent;
 float value;
 float scaled_value;
 long slen;
 slen = utn_cs_ends( cy );
 value = 0.0;
 last_char = cy[slen-1];
 if ( utn_cs_digit_char( last_char ) ) {
  value = utn_cs_read_r( cy );
 } else {
  if ( slen == 1 ) {
   scaled_value = 1.0;
  } else {
   utn_cs_copy_siz( cy, cy1, slen-1 );
   scaled_value = utn_cs_read_r( cy1 );
  }
  exponent = utn_si_exp( cy+slen-1, &ok );
  if ( !ok ) {
   value = utn_null_r();
  } else {
   value = scaled_value * tpow_r( exponent );
  }
 }
 return( value );
}



void utn_si_check( char* unit, long* factor, long* pos )
{
 long slen;
 char utmp[2];
 logical ok;
 slen = utn_cs_ends( unit );
 if ( slen > 3 && utn_cs_eq_siz( unit, UT_SI_MICRO, 3 ) ) {
  *factor = -6;
  *pos = 4;
 } else if ( slen == 1 ) {
  *factor = 0;
  *pos = 1;
 } else {
  utn_cs_copy_siz( unit, utmp, 1 );
  *factor = utn_si_exp( utmp, &ok );
  *pos = ok ? 2 : 1;
 }
}

