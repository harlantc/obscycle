/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "unitlib.h"
#include "unit.h"
/*
 *--------------------------------------------------------------------------------
 */

/*
 *   Here we parse a unit char* into a number of unit items, an array of base
 *   units, an array of exponents, and an array of SI prefix factors.
 */

void utn_cs_unit_parse( const char* unit, UnitRegister* reg )
{
 TextWord utn_unit_item;
 char* ptr;
 double exponent;
 TextWord base_token;
 long n;
 logical loop;

 n = 0;
 utn_cs_copy_siz( "10", reg->base[n], UT_WORD_SIZE );
 reg->exp[n] = 0.0;
 reg->fac[n] = 0;
 loop = !utn_cs_is_blank( unit );
 ptr = (char*)unit;
 while ( loop && ptr != NULL ) {
  utn_cs_get_c( &ptr, utn_unit_item, UT_WORD_SIZE );
  utn_unit_parse_exp( utn_unit_item, base_token, UT_WORD_SIZE, &exponent );
  if ( n == 0 && utn_cs_eq( base_token, "10" ) ) {
   reg->exp[n] = exponent;
  } else {
   n++;
   if ( n >= reg->nmax )
    utn_cs_unit_reg_enlarge( reg, 2 * n );
   utn_cs_copy_siz( base_token, reg->base[n], UT_WORD_SIZE );
   reg->exp[n] = exponent;
   reg->fac[n] = 0;
  }
 }
 reg->n = n+1;
}

void utn_cs_unit_reg_enlarge( UnitRegister* reg, long n )
{
 reg->base = (char**)utn_ar_realloc_c( reg->base, UT_WORD_SIZE, n );
 reg->exp = (double*)realloc( reg->exp, n * UT_SZ_D);
 reg->fac = (long*)realloc( reg->fac, n * UT_SZ_I);
 reg->nmax = n;
}

/*
 *--------------------------------------------------------------------------------
 */

void utn_unit_parse_exp( const char* unit_item, char* base_unit, integer maxlen, double* exp_unit )
{
 long j;
 long k;
 char* ptr;
 TextWord tmp;

 /* We look for a ^ character indicating the start of an exponent */

 j = utn_cs_index_char( unit_item, '^' );
 if ( j > 0 ) {
  utn_cs_copy_siz( unit_item, base_unit, j-1 );
  ptr = (char*)(unit_item + j); 

  /* Handle the case of TeX parens  x^{y} */

  if ( *ptr == '{' ) {
   k = utn_cs_index_char( ptr, '}' );
   utn_cs_copy_siz( ptr+1, tmp, k-1 );
   *exp_unit = utn_cs_read_d( tmp );
  } else {

  /* or the case when we have a plain exponent   x^y */

   *exp_unit = utn_cs_read_d( ptr );
  }
 } else {

 /* Default case is an exponent of 1 */

  utn_cs_copy_siz( unit_item, base_unit, maxlen );
  *exp_unit = 1.0;
 }
 utn_unit_parse_mantissa( base_unit, exp_unit );
}

/*
 *--------------------------------------------------------------------------------
 */

void utn_unit_parse_mantissa( char* base_unit, double* exp_unit )
{
 logical ok;
 double log_value;
 long j,k;
 logical found_ten;
 TextWord number;
 double value;

 /* Check for a number that is not 10 */

 ok = utn_cs_digit_char( *base_unit ) && !utn_cs_eq( base_unit, "10" );
 log_value = 0;

 if ( ok ) {
  found_ten = UT_FALSE;
  k = utn_cs_ends( base_unit );

  /* check for TeX  \times 10^2  syntax */

  j = utn_cs_index( base_unit, "\\times10" );
  if ( j > 0 && j+7 == k ) {
   utn_cs_copy_siz( base_unit, number, j-1 );
   found_ten = UT_TRUE;
  } else {
  /*  check for normal x10^2 syntax */
   j = utn_cs_index( base_unit, "x10" );
   if ( j > 0 && j+2 == k ) {
    utn_cs_copy_siz( base_unit, number, j-1 );
    found_ten = UT_TRUE;
   }
  }
/* if we have a leading number, make the base unit 10 and
 * take the log of the number to put in the base unit's exponent
 */
  if ( !found_ten ) {
   utn_cs_copy( base_unit, number );
   if ( *exp_unit != 1.0 ) 
   {
     TextBuf tmp;
     snprintf( tmp, UT_TEXT_SIZE, "Weird unit: %s", base_unit );
     utn_state_error( NULL, tmp );
   }
  }
  value = utn_cs_read_d( number );
  if ( value > 0.0 ) log_value = log10( value );
  if ( found_ten ) {
   *exp_unit += log_value;
  } else {
   *exp_unit = log_value;
  }
  utn_cs_copy( "10", base_unit );
 }

}



/*
 *--------------------------------------------------------------------------------
 */


void utn_unit_compose( char** base_unit, double* exp_unit, long* si_fac, long n_uo, logical texmode, char* unit, long siz )
{
 long ten_exp_i;
 double ten_mantissa;
 double factor;
 char* ptr;
 long i;
 ptr = unit;
 utn_cs_copy( " ", unit );
 if ( n_uo == 0 ) return;
 if ( utn_cs_eq( base_unit[0], "10" ) && exp_unit[0] != 0.0 ) {
  frac_d( exp_unit[0], &ten_exp_i, &ten_mantissa );
  if ( ten_mantissa != 0.0 ) {
   factor = pow( 10.0, ten_mantissa );
   utn_cs_put_d( factor, &ptr, &siz );
/* Trim trailing space */
   if ( *(ptr-1) == ' ' ) ptr--;   
   if ( texmode ) {
    utn_cs_put_cn( "\times", &ptr, &siz );
   } else {
    utn_cs_put_cn( "x", &ptr, &siz );
   }
  }
  utn_unit_compitem( &ptr, base_unit[0], (double)ten_exp_i, 0, &siz );
  if ( !siz ) return;
 }

 for( i = 1; i < n_uo; i++ ) {
  if ( exp_unit[i] > 0.0 ) {
   utn_unit_compitem( &ptr, base_unit[i], exp_unit[i], si_fac[i], &siz );
   if ( !siz ) return;
  }
 }
 for ( i = 1; i < n_uo; i++ ) {
  if ( exp_unit[i] < 0.0 ) {
   utn_unit_compitem( &ptr, base_unit[i], exp_unit[i], si_fac[i], &siz );
   if ( !siz ) return;
  }
 }
}

/*
 *--------------------------------------------------------------------------------
 */



void utn_unit_compitem( char** pptr, char* item, double exp_unit, long si_fac, long* siz )
{
 long k = 0;
 double x;
 long n;
 char pfx[6]="";
 integer tsiz;
 long ix;
 double rx;
 TextWord tmp;
 char* ptr = *pptr;
 utn_si_getpfx( si_fac, pfx, &k );
 pfx[1] = 0;
 if ( utn_cs_is_blank( pfx ) ) { 
  utn_cs_copy( item, tmp );
 } else {
  *tmp = *pfx;
  utn_cs_copy( item, tmp+1 );
 }
 utn_cs_put_c( tmp, &ptr, siz );
/* strip trailing space */
 if ( *(ptr-1) == ' ' ) ptr--;
 x = exp_unit;
 if ( x != 1.0 ) {
  tsiz = *siz;
  frac_d( x, &ix, &rx );
  if ( x > 0.0 && rx == 0.0 && ix < 10 ) /* No {} needed */
  {
   n = snprintf( ptr, tsiz, "^%ld", (long)ix );
  } else {
   if ( rx == 0.0 ) {
    n = snprintf( ptr, tsiz, "^{%ld}", (long)ix );
   } else {
    n = snprintf( ptr, tsiz, "^{%s}", utn_cs_write_d( x, tmp, WORD_SIZE ) );
   }
  }
  ptr += n;
  *siz -= n;
 }
 *ptr = ' ';
 ptr++;
 *ptr = '\0';
 *pptr = ptr;
}

/*
 *--------------------------------------------------------------------------------
 */


void utn_si_tmult( long fac1, double exponent1, long fac2, double exponent2, long* fac, double* mantissa )
{
 double result_factor;
 double exponent;

 result_factor = fac1 * exponent1 + fac2 * exponent2;
 exponent = exponent1 + exponent2;
 if ( exponent == 0.0 ) {
  *fac = 0;
  *mantissa = result_factor;
 } else {
  *fac = fac1;
  *mantissa = result_factor - exponent * fac1;
 }
}

/*
 *--------------------------------------------------------------------------------
  Assumes n1-arrays are sized n1+n2
 */

void utn_unit_tmult( long* n1, char** base1, double* exponent1, long* si_fac1,
                 long* n2, char** base2, double* exponent2, long* si_fac2, long power )
{
 long i;
 long fac;
 long k;
 long n;
 double mantissa;
 
 n = *n1;
 for ( i = 0; i < *n2; i++ ) {
  k = utn_ar_cmatch_c( base2[i], base1, n );
  if ( k >= 0 ) {
   utn_si_tmult( si_fac1[k], exponent1[k], si_fac2[i], power * exponent2[i], 
     &fac, &mantissa );
   exponent1[k] += power * exponent2[i];
   si_fac1[k] = fac;
   exponent1[0] += mantissa;
  } else {
   n++;
   k = n-1;
   utn_cs_copy( base2[i], base1[k] );
   exponent1[k] = power * exponent2[i];
   si_fac1[k] = si_fac2[i];
  } 
 }
 *n1 = n;
}
