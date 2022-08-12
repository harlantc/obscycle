/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <limits.h>

/*
 *   Read a value, splitting it into its sign, long part and fractional part.
 *   Advance the pointer so it points to the end of the value, ready to
 *   read more.
 */

void utn_cs_read_val_d( char** buf, 
                 long* sign_val,
		 long* int_val,
		 double* frac_val
		)
{
 char* ptr;
 long digits;
 double dval;
 *int_val = 0;
 *frac_val = 0.0;
 ptr = *buf;
 if ( !utn_cs_is_blank( ptr ) ) {
  utn_cs_advptr( &ptr );
  if ( utn_cs_nancode( ptr ) ) {
   ptr = NULL;
   *int_val = utn_null_i();
  } else {
   *sign_val = utn_cs_read_val_sign( &ptr );
   dval = utn_cs_read_d( ptr );
   if ( dval > LONG_MAX )
   {
    *int_val = 0;
    *frac_val = dval;
   } else {
    *int_val = utn_cs_read_val_int( &ptr, &digits );
    *frac_val = utn_cs_read_val_frac( &ptr, &digits );
   }
  }
 } else {
  ptr = NULL;
 }
 *buf = ptr;
}

/*
 *  Read a value with exponent, returning sign, long part, and fractional
 *  part.
 */

void utn_cs_read_val_de( char** buf, long* sign_val, long* int_val, double* frac_val )
{
 char* ptr;
 long sign_exp, int_exp;
 double frac_exp;
 double evalue;
 double dvalue;
 ptr = *buf;
 utn_cs_read_val_d( &ptr, sign_val, int_val, frac_val );

 if ( utn_cs_is_blank( ptr ) ) {
  ptr = NULL;
 } else if ( *ptr == 'E' || *ptr == 'D' ) {
  ptr++;
  utn_cs_read_val_d( &ptr, &sign_exp, &int_exp, &frac_exp );
  evalue = sign_exp * ( int_exp + frac_exp );
  if ( evalue != 0.0 ) {
   dvalue = ( *int_val + *frac_val ) * pow( 10.0, evalue );
   frac_d( dvalue, int_val, frac_val );
  }
 }
 *buf = ptr;
}

/*
 *  Read a value with exponent. returning sign, long part, and fractional part
 *  and exponent.
 *
 */

/* Efficient routine to test for a number, supporting 'D' format */

double utn_cs_read_numeric_val( char** pptr )
{
  long sval;
  double dval;
  long ival;
  double fval;
  long eval;
 
  utn_cs_read_val_ee( pptr, &sval, &ival, &fval, &eval );

  dval = sval * ( ival + fval ) * tpow_d( eval );
  return dval;
}


void utn_cs_read_val_ee( char** buf, long* sign_val, long* int_val, double* frac_val, long* exp_val )
{
 char* ptr;
 long sign_exp, int_exp;
 double frac_exp;
 double dvalue;
 logical ten;
 double evalue;
 double base;

 ptr = *buf;
 *exp_val = 0;

 utn_cs_read_val_d( &ptr, sign_val, int_val, frac_val );

 if ( utn_cs_is_blank( ptr ) ) {
  ptr = NULL;
 } else if ( utn_cs_ss_char( "EeDd", *ptr ) ) {
  ptr++;
  utn_cs_read_val_d( &ptr, &sign_exp, &int_exp, &frac_exp );
  if ( frac_exp != 0.0 ) {
   if ( sign_exp < 0 ) {
    frac_exp = 1.0 - frac_exp; 
    int_exp++;
   }
   dvalue = ( *int_val + *frac_val ) * pow( 10.0, frac_exp );
   frac_d( dvalue, int_val, frac_val );
  }
  *exp_val = sign_exp * int_exp;
 } else if ( *ptr == 'x' ) {
/* General power notation    n x foo */
  ptr++;
  utn_cs_read_val_d( &ptr, &sign_exp, &int_exp, &frac_exp );
  ten = ( sign_exp == 1 && int_exp == 10 && frac_exp == 0.0 );
  if ( *ptr == '^' ) {    /* buf = n x foo ^ */
   base = sign_exp * ( int_exp + frac_exp );
   ptr++;
   utn_cs_read_val_d( &ptr, &sign_exp, &int_exp, &frac_exp );
   if ( ten ) {
    /* buf = n x 10 ^ foo */
    if ( frac_exp != 0.0 ) {
     if ( sign_exp < 0 ) { frac_exp = 1.0 -frac_exp; int_exp++; }
     dvalue = ( *int_val + *frac_val ) * pow( 10.0, frac_exp );
     frac_d( dvalue, int_val, frac_val );
    }
    *exp_val = sign_exp * int_exp;
   } else {  /* Not ten; buf = n x y ^ foo */
    evalue = sign_exp * ( int_exp + frac_exp );
    dvalue = pow( base, evalue );
    if ( dvalue < 0.0 ) { 
     dvalue *= -1;
     sign_exp = -1;
    } else {
     sign_exp = +1;
    }
    dvalue = ( *int_val + *frac_val ) * dvalue;
    frac_d( dvalue, int_val, frac_val );
    *sign_val *=  sign_exp;
    *exp_val = 0;
   }
  } 
 } else if ( *ptr == '^' ) {    /* buf = n ^ foo */
  ptr++;
  utn_cs_read_val_d( &ptr, &sign_exp, &int_exp, &frac_exp );
  ten = ( *int_val == 10 && *frac_val == 0.0 );
  if ( ten ) {  /* buf = 10 ^ foo */
   *int_val = 1;
   if ( frac_exp != 0.0 ) {
    if ( sign_exp < 0 ) { frac_exp = 1.0 -frac_exp; int_exp++; }
    dvalue = pow( 10.0, frac_exp );
    frac_d( dvalue, int_val, frac_val );
   }
   *exp_val = sign_exp * int_exp;
  } else {  /* Not ten */
   dvalue = *sign_val * ( *int_val + *frac_val );
   evalue = sign_exp  * (  int_exp +  frac_exp );
   dvalue = pow( dvalue, evalue );
   if ( dvalue < 0.0 ) {
    *sign_val = -1; dvalue *= -1;
   } else {
    *sign_val = +1;
   }
   frac_d( dvalue, int_val, frac_val );
   *exp_val = 0.0;
  }

 }
 if ( ptr )
 {
  while ( *ptr == ' ' ) ptr++;
  if ( *ptr == '\0' ) ptr = NULL;
 }
 *buf = ptr;

}

long utn_cs_read_val_int( char** buf, long* pdigits )
{
 long val;
 char* ptr;
 logical loop;
 long n;
 long digits; 
 ptr = *buf;
 val = 0;
 digits = 0;
 loop = ( ptr != NULL );
 while( loop && *ptr != '\0' ) {
  n = *ptr - '0';
  loop = ( n >= 0 && n <= 9 ); /* Is a digit */
  if ( loop ) {
   ptr++;
   if ( digits < UT_MAX_DIGITS ) {
    val = val * 10 + n;
    digits++;
   }
  }
 }
 *buf = ptr;
 *pdigits = digits;
 return( val );
}

long utn_cs_read_val_sign( char** buf )
{
 char* ptr;
 ptr = *buf;
 if ( *ptr == '\0' ) {
  return( 0 );
 } else  if ( *ptr == '+' ) {
  (*buf)++;
  return( +1 );
 } else if ( *ptr == '-' ) {
  (*buf)++;
  return( -1 );
 } else {
  return( +1 ); 
 }
}


double utn_cs_read_val_frac( char** buf, long* dig )
{
 double val;
 long ival;
 long digits = 0;
 char* ptr;
 val = 0.0;
 ptr = *buf;
 if ( *ptr == '.' ) {
  ptr++;
  ival = utn_cs_read_val_int( &ptr, &digits );
  if ( digits > 0 ) {
   val = (double)ival / tpow_d( digits );
  }
 }
 *buf = ptr;
 *dig = digits;
 return( val );
}
