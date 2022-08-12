/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*
 * Roman and Greek 
 */
#include "utlib.h"


static char*   ut_constants_roman = "IVXLCDM";
static long ut_constants_roman_val[] = { 1, 5, 10, 50, 100, 500, 1000, 0 };
static char*   ut_constants_greek[] = 
                     { "Alpha", "Beta", "Gamma", "Delta", "Epsilon", 
 "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi",
 "Omicron", "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega" };

long utn_cs_roman_parse( const char* buf )
{
 long n;
 long pos;
 char* ptr;
 long this_val;
 long this_sign;
 long big;
 long right_val = 0;

 pos = utn_cs_ends( buf );
 this_val = 1;
 big = 0;
 right_val = 0;
 n = 0;
 ptr = (char*)buf + pos - 1;
 this_sign = 1;

 while ( pos >= 1 && this_val != 0 )  {
  this_val = utn_cs_index_char( ut_constants_roman, *ptr-- );
  if ( this_val > 0 ) {
   if ( this_val >= big ) {
    big = this_val;
    this_sign = 1;
   } else if ( this_val < right_val ) {
    this_sign *= -1;
   } else if ( this_val >= right_val ) {
/* Same as or bigger than one to its right */
    ;
   }
  }
  n += this_sign * ut_constants_roman_val[ this_val-1 ];
  right_val = this_val;
  pos--;
 }
 if ( big == 0 ) n = 0;
 return( n );
}


void utn_cs_roman_numeral( const long value, char* word )
{
 long val;
 long pos;
 long rval;
 long rem;
 long val2;
 long n;
/* 1998 May 27 made argument const */
 n = value;
 pos = 0;
 val = 6;
 while( val >= 0 ) {
  rval = ut_constants_roman_val[ val ];
  while( n >= rval ) {
   n -= rval;
   *word++ = ut_constants_roman[ val ];
  }
  if ( val > 0 ) {
   rem = rval - n;
   val2 = 2 * ( ( val - 1 ) / 2 );
   if ( rem <= ut_constants_roman_val[ val2 ] ) {
    n += ut_constants_roman_val[val2] - ut_constants_roman_val[val];
    *word++ = ut_constants_roman[ val2 ];
    *word++ = ut_constants_roman[ val ];
   }
  }
  val--;
 }
 *word = '\0';
}



long utn_cs_greek_no( const char* buf ) 
{
 TextWord word; 
 utn_cs_copy( buf, word );
 utn_cs_mixcase( word );
 return( utn_ar_match_c( word, (char**)ut_constants_greek, UT_NGREEK ) );
}


void utn_cs_greek_letter( const long n, char* buf )
{
 if ( n > 0 && n <= UT_NGREEK ) {
  utn_cs_copy( ut_constants_greek[n-1], buf );
 } else {
  utn_cs_copy( " ", buf );
 }
}
