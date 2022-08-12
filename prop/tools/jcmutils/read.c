/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <ctype.h>

static void utn_cs_rfclean( char* tbuf );

integer utn_cs_read_hex( char* buf )
{
  integer value = -1;
  unsigned long v;
  if ( utn_cs_eq_siz( buf, "0x", 2 ) && (sscanf( buf, "0x%lx", &v ) == 1 ))
   value = v;
  return value;
}
    

static const struct {
  char nanstring[6];
  double efmt_max;
  double efmt_min;
  char efmt[8];
  char ffmt[8];
 } ut_constants_format = { "  -  ", 1.0E5, 1.0E-3, "%12.5E", "%12.5f" };


/*----------------------------------------------------------------------*/
/*
 *  read_d
 *
 * Read a float from a char*
 * Uses atof from <stdlib.h>
 */

double utn_cs_read_d( const char* buf )
{
 char* err;
 double x;
 TextCard buf2;
 if ( utn_cs_is_blank( buf ) ) {
  return( 0.0 );
 } else if ( utn_cs_nancode( buf ) ) {
  return( utn_null_d() );
 } else {
  x = strtod( buf, &err );
/* Handle D format; sometimes C does't floatize D = E */
  if ( *err == 'D' ) {
   utn_cs_copy( buf, buf2 );
   buf2[err-buf]='E';
   x = strtod( buf2, &err );
  }
  if ( !utn_cs_is_blank( err ) ) 
  {
   TextBuf tmp;
   snprintf( tmp, UT_TEXT_SIZE, "cs_read_d failed to parse %s", buf );
   /* error not handled */
  }
  return ( x );
 }
}

float utn_cs_read_r( const char* buf )
{
 double x;
 char* err;
 if ( utn_cs_is_blank( buf ) ) {
  return( 0.0 );
 } else if ( utn_cs_nancode( buf ) ) {
  return( utn_null_r() );
 } else {
  x = strtod( buf, &err );
  if ( !utn_cs_is_blank( err ) ) 
  {
   TextBuf tmp;
   snprintf( tmp, UT_TEXT_SIZE, "cs_read_r failed to parse %s", buf );
   /* error not handled */
  }
  return ( (float)x );
 }
}

/*----------------------------------------------------------------------*/
/*
 *  read_i
 *
 * Read an long from a char*
 * Uses atoi from <stdlib.h>
 *
 */

long utn_cs_read_i( const char* buf )
{
 long x;
 char* err; 
 if ( utn_cs_is_blank( buf ) ) {
  return( 0 );
 } else if ( utn_cs_nancode( buf ) ) {
  return( utn_null_i() );
 } else {
  x = strtol( buf, &err, (int)10 );
  if ( err != NULL ) {
   if ( !utn_cs_is_blank( err ) ) 
   {
    TextBuf tmp;
    snprintf( tmp, UT_TMP_MAX, "cs_read_i failed to parse %s", buf );
    /* Error not handled */
   }
  }
  return ( x  );
 }
}

short utn_cs_read_s( const char* buf )
{
 long x;
 char* err;
 if ( utn_cs_is_blank( buf ) ) {
  return( 0 );
 } else if ( utn_cs_nancode( buf ) ) {
  return( utn_null_s() );
 } else {
  x = strtol( buf, &err, 10 );
   if ( !utn_cs_is_blank( err ) ) 
   {
    TextBuf tmp;
    snprintf( tmp, UT_TEXT_SIZE, "cs_read_s failed to parse %s", buf );
    /* Error not handled */
   }
  return ( (short)x  );
 }
}




logical utn_cs_read_q( const char* ibuf )
{
 logical ok;
 long ival = 0;
 char* buf = (char*)ibuf;
 buf += utn_cs_begs( buf ) - 1;
 ok = utn_cs_ss_char( "YyTt", *buf );
 if ( ok ) return ok;
/* Support 0=F other = T as per C convention */
 if ( utn_cs_digit_char( buf[0] ) )
  ival = utn_cs_read_i( buf );
 ok = ( ival != 0 );
 return ok;
}


/*----------------------------------------------------------------------*/
/*
 *  write_i
 *
 * Write an long to a char*, left justified. Check to not overflow buffer.
 * Uses snprintf from <stdio.h>
 * Uses utn_cs_copy_siz from csproto.h
 */

char* utn_cs_write_i( const long val, char* buf, const long siz )
{
 TextCard tbuf;
 if ( utn_qnull_i( val )) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else {
  snprintf( tbuf, UT_CARD_SIZE, "%ld", (long)val );
  utn_cs_copy_siz( tbuf, buf, siz );
 }
 return( buf );
}

char* utn_cs_write_s( const short val, char* buf, const long siz )
{
 TextCard tbuf;
 if ( utn_qnull_s( val )) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else {
  snprintf( tbuf, UT_CARD_SIZE, "%d", val );
  utn_cs_copy_siz( tbuf, buf, siz );
 }
 return( buf );
}

char* utn_cs_write_r( const float val, char* buf, const long siz )
{
 TextCard tbuf;
 if ( utn_qnull_r( val ) ) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else if ( val == 0.0 ) {
  utn_cs_copy_siz( "0", buf, siz );
 } else {
  if ( rabs( val ) > ut_constants_format.efmt_max || rabs( val ) < ut_constants_format.efmt_min ) {
   snprintf( tbuf, UT_CARD_SIZE, ut_constants_format.efmt, val );
  } else { 
   snprintf( tbuf, UT_CARD_SIZE, ut_constants_format.ffmt, val );
  }
  utn_cs_numberclean( tbuf, 0, buf, siz );
 }
 return( buf );
}


char* utn_cs_writef_clean_d( const double val, const char* fmt, const logical clean, const long sig, char* buf, const long siz )
{
 TextCard tbuf = " ";
 long i;
 if ( utn_qnull_d( val ) ) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else if ( clean && val == 0.0 ) {
  utn_cs_copy_siz( "0", buf, siz );
 } else {
  i = (long)val;
  if ( clean && (double)i == val )
   snprintf( tbuf, UT_CARD_SIZE, "%ld", i );
  else if ( fmt && !utn_cs_is_blank( fmt ) )
   snprintf( tbuf, UT_CARD_SIZE, fmt, val );
  else if ( fabs( val ) > ut_constants_format.efmt_max || fabs( val ) < ut_constants_format.efmt_min ) 
   snprintf( tbuf, UT_CARD_SIZE, ut_constants_format.efmt, val );
  else 
   snprintf( tbuf, UT_CARD_SIZE, ut_constants_format.ffmt, val );
  if ( clean )
   utn_cs_numberclean( tbuf, sig, buf, siz );
  else
  {
   utn_cs_rfclean( tbuf );
   utn_cs_copy_siz( tbuf, buf, siz );
  }   

 }
 return( buf );

}

char* utn_cs_writef_d( const double val, const char* fmt, char* buf, const long siz )
{
 return utn_cs_writef_clean_d( val, fmt, UT_FALSE, 0, buf, siz );
}

char* utn_cs_write_d( const double val, char* buf, const long siz )
{
 return utn_cs_writef_clean_d( val, NULL, UT_TRUE, 0, buf, siz );
}


char* utn_cs_write_q_string( logical val )
{
	if (val)
		return("TRUE");
	else
		return("FALSE");
}

char utn_cs_write_q( const logical q )
{
 return( q ? 'T' : 'F' );
}

long utn_cs_read_i1( const char* buf )
{
 long i;

 i = *buf - '0';
 if ( i < 0 || i > 9 ) i = 0;
 return( i );
}


/*
 * Clean a number.
 * (1) Standardize leading zero before decimal point.
 * (2) Remove leading spaces
 * (3) Remove trailing zeroes in mantissa if after a decimal point
 * (4) Delete trivial exponents
 * (5) Truncate to given number of significant dp (if sig > 0 )
 */

void utn_cs_numberclean( char* tbuf, long sig, char* buf, long siz )
{
 long head;
 long tail;
 long expt;
 long decimal_point;

 if ( !siz ) return;
/* Standardize leading zero before decimal point */
 utn_cs_rfclean( tbuf );
/* Check where 'E' char is */
 head = utn_cs_begs( tbuf );
/* Skip leading zeroes */
 tbuf += head-1;
 expt = 0;
 if ( tbuf[0] == '-' || tbuf[0] == '+' ) expt++;
 while( isdigit( tbuf[expt] ) || tbuf[expt] == '.' ) expt++;
/* Tail is last digit, 1-based */
 tail = expt;
 if ( tail < 1 )
  return;  /* Error */
/* Locate decimal point, 1-based */
 decimal_point = utn_cs_index_char( tbuf, '.' );

/* Remove trailing zeros in decimal */
 if ( decimal_point > 0 ) {
  while ( tail > decimal_point && tbuf[tail-1] == '0' ) tail--;
  if ( tail == decimal_point ) tail--;
  if ( sig > 0 && tail > decimal_point + sig )
   tail = decimal_point + sig;
 }


 utn_cs_copy_siz( tbuf, buf, tail );
/* Append exponent, unless it is trivial */
 if ( expt > 0 )
 {
  tbuf = tbuf + expt;
  if ( !utn_cs_is_blank( tbuf ) && !utn_cs_eq( tbuf, "E+00" ) ) 
   utn_cs_copy( tbuf, buf+tail );
 }

}

/*
 * Clean a number - standardize leading zero.
 * (1) Change  "-." to "-0."
 * (2) Change  " ." to "0."
 */
static void utn_cs_rfclean( char* tbuf )
{
 long j;
 j = utn_cs_begs( tbuf );
 if ( j > 1 ) {
  if( utn_cs_eq_ss( tbuf, j-1, j+1, " -." ) ) {
   utn_cs_put_ss( "-0.", tbuf, j-1, j+1 );
  } else if ( utn_cs_eq_ss( tbuf, j-1, j, " ." )) {
   utn_cs_put_ss( "0.", tbuf, j-1, j );
  }
 }
}




char* utn_cs_writef_i( const long val, const char* fmt, char* buf, const long siz )
{
 TextCard tbuf;
 if( utn_cs_is_blank( fmt ) ) {
  (void)utn_cs_write_i( val, buf, siz );
 } else if ( utn_qnull_i( val ) ) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else {
  snprintf( tbuf, UT_CARD_SIZE, fmt, val );
  utn_cs_copy_siz( tbuf, buf, siz );
 }
 return( buf );  
}




/* Write a char* with ni long digits  - forcing leading zeros - and nf places.
   The default C formatting doesn't allow you to force leading zeroes with
   floating point numbers. */
void utn_cs_writef_zeros( double x, long ni, long nf, char* xbuf, long maxlen )
{
  long ix;
  double fx;
  TextWord fmt;
  frac_d( x, &ix, &fx );
  if ( nf == 0 )
  {
   snprintf( fmt, UT_WORD_SIZE, "%%%ld.%ldld", ni, ni );
   snprintf( xbuf, maxlen, fmt, ix );
  }
  else
  {
   snprintf( fmt, UT_WORD_SIZE, "%%%ld.%ldld%%%ld.%ldf", ni, ni, nf+1, nf );
   snprintf( xbuf, maxlen, fmt, ix, fx );
  }
}





char* utn_cs_writef_s( const short val, const char* fmt, char* buf, const  long siz )
{
 TextCard tbuf;
 if( utn_cs_is_blank( fmt ) ) {
  (void)utn_cs_write_s( val, buf, siz );
 } else if ( utn_qnull_s( val ) ) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else {
  snprintf( tbuf, UT_CARD_SIZE, fmt, val );
  utn_cs_copy_siz( tbuf, buf, siz );
 }
 return( buf );  
}


char* utn_cs_writef_r( const float val, const char* fmt, char* buf, const long siz )
{
 TextCard tbuf;
 if( utn_cs_is_blank( fmt ) ) {
  (void)utn_cs_write_r( val, buf, siz );
 } else if ( utn_qnull_r( val ) ) {
  utn_cs_copy_siz( (char*)ut_constants_format.nanstring, buf, siz );
 } else {
  snprintf( tbuf, UT_CARD_SIZE, fmt, val );
  utn_cs_rfclean( tbuf );
  utn_cs_copy_siz( tbuf, buf, siz );
 }
 return( buf );  
}






char* utn_cs_write_opt_s( const char* prompt, const short val, char* buf, const long siz )
{
 TextWord tbuf;
 TextWord tbuf1;
 TextWord tbuf2;
 utn_cs_conc1( prompt, utn_cs_sparen( utn_cs_write_s( val, tbuf, WORD_SIZE ), tbuf1 ), tbuf2  );
 utn_cs_copy_siz( tbuf2, buf, siz );
 return( buf );
}

char* utn_cs_write_opt_i( const char* prompt, const long val, char* buf, const long siz )
{
 TextWord tbuf;
 TextWord tbuf1;
 TextWord tbuf2;
 utn_cs_conc1( prompt, utn_cs_sparen( utn_cs_write_i( val, tbuf, WORD_SIZE ), tbuf1 ), tbuf2 );
 utn_cs_copy_siz( tbuf2, buf, siz );
 return( buf );
}

char* utn_cs_write_opt_r( const char* prompt, const float val, char* buf, const long siz )
{
 TextWord tbuf;
 TextWord tbuf1;
 TextWord tbuf2;
 utn_cs_conc1( prompt, utn_cs_sparen(  utn_cs_write_r( val, tbuf, WORD_SIZE ), tbuf1 ), tbuf2  );
 utn_cs_copy_siz( tbuf2, buf, siz );
 return( buf );
}

char* utn_cs_write_opt_d( const char* prompt, const double val, char* buf, const long siz )
{
 TextWord tbuf;
 TextWord tbuf1;
 TextWord tbuf2;
 utn_cs_conc1( prompt, utn_cs_sparen( utn_cs_write_d( val, tbuf, WORD_SIZE ), tbuf1 ), tbuf2  );
 utn_cs_copy_siz( tbuf2, buf, siz );
 return( buf );
}

char* utn_cs_write_opt_c( const char* prompt, const char* val, char* buf, const long siz )
{
 TextWord tbuf1;
 TextWord tbuf2;
 utn_cs_conc1( prompt, utn_cs_sparen( val, tbuf1 ), tbuf2  );
 utn_cs_copy_siz( tbuf2, buf, siz );
 return( buf );
}


char* utn_cs_write_e_r( const float x, const float off, char* buf, const long siz )
{
 if ( x == 0.0 ) {
  utn_cs_copy( "0", buf );
 } else if ( utn_qnull_r( x ) ) {
  utn_cs_copy( (char*)ut_constants_format.nanstring, buf );
 } else {
  utn_cs_write_e_d( (double)x, (double)off, buf, siz );
 }
 return( buf );
}

char* utn_cs_write_e_d( const double x, const double off, char* buf, const long siz )
{
 long ioff;
 double x1;
 double roff;
 long exponent;
 double mantissa;
 TextWord tbuf;
 if ( x == 0.0 ) {
  utn_cs_copy( "0", buf );
 } else if ( utn_qnull_d( x ) ) {
  utn_cs_copy( (char*)ut_constants_format.nanstring, buf );
 } else if ( off != 0.0 ) {
  frac_d( off, &ioff, &roff );
  x1 = x * pow( 10.0, roff );
  exponent = (long)log10( fabs(x1) );
  if ( fabs(x1) < 1.0 ) exponent--;
  mantissa = x1/ tpow_d( exponent );
  exponent += ioff;
  if ( abs( exponent ) < 100 ) {
   snprintf( tbuf, UT_WORD_SIZE, "%8.5fE%+3.3ld", mantissa, (long)exponent );
  } else {
   snprintf( tbuf, UT_WORD_SIZE, "%7.4fE%+4.4ld", mantissa, (long)exponent );
  }
  utn_cs_copy_siz( tbuf, buf, siz );
 } else {
  utn_cs_write_d( x, buf, siz );
 }
 return( buf );
}



char* utn_cs_write_tp_i( const long i, char* buf, const long siz )
{
 TextWord tbuf;
 snprintf( tbuf, UT_WORD_SIZE, "{%ld}", (long)i );
 utn_cs_copy_siz( tbuf, buf, siz );
 return( buf );
}


char* utn_cs_write_rj_i( const long i, char* buf, const long siz )
{
 TextWord fmt;
 if ( utn_qnull_i( i ) ) {
  utn_cs_copy( (char*)ut_constants_format.nanstring, buf );
 } else {
  snprintf( fmt, UT_WORD_SIZE, "%%%ldd", (long)siz );
  snprintf( buf, siz, fmt, i );
 }
 return( buf );
}


long utn_cs_read_digit( const char c )
{
 long i;
 i = c - '0';
 if ( i < 0 || i > 9 ) i = 0;
 return( i );
}

long utn_cs_read_hex_digit( const char c )
{
 long i;

 i = c - '0';
 if ( i < 0 || i > 9 ) {
  i = c - 'a';
  if ( i < 0 || i > 5 ) i = c - 'A';
  if ( i < 0 || i > 5 ) {
   i = 0;
  } else {
   i += 10;
  }
 }
 return( i );
}


