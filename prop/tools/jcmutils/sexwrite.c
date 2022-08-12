/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


/* Here we carefully compose things, making sure to round up the
   last digit in the correct way (via making fmt the right length)
 */

void utn_cs_sex_fmt( const logical sg, const long d, const long m, const long s, const double fs, char sep, char* buf, const long siz )
{
 TextWord buf1;
 TextWord buf2;
 char* ptr;
 long mm = 0;
 integer nd;
 TextWord fmt;
 integer tsiz;
 buf2[0] = sg ? '+' : '-';
 tsiz = UT_WORD_SIZE - 3;
 mm = 1;
 ptr = buf2 + 1;
 if ( d > 99 ) {
  snprintf( buf2+mm, tsiz, "%03ld", (long)d );
  mm += 3;
 } else {
  snprintf( buf2+mm, tsiz, "%02ld", (long)d );
  mm += 2;
 }


/* Write the fractional second. If it's >1, we round down */
 

/* Offset: mm + 7 + nd  = siz */
 nd = siz - mm - 7;
 if ( nd > 0 )
 {

  if ( nd > 9 )  nd = 9;
  snprintf( fmt, UT_WORD_SIZE, "%%%ld.%ldf", nd+2, nd );
  snprintf( buf1, UT_WORD_SIZE, fmt, fs );
  if ( buf1[0] == '1' ) utn_cs_copy( "0.999999999", buf1 );
 }
 snprintf( buf2 + mm, tsiz, "%c%02ld%c%02ld%-s", sep, (long)m, sep, (long)s, buf1+1 );
 utn_cs_copy_siz( buf2, buf, siz );
}




char* utn_cs_sexwrite_d( const double x, char sep, char* buf, const long siz )
{
 logical sg;
 long d,m,s;
 double fs;
 utn_sx_wval_to_sxa_d( x, UT_CIRCLE, &sg, &d, &m, &s, &fs );
 utn_cs_sex_fmt( sg, d, m, s, fs, sep, buf, siz );
 return( buf );
}

char* utn_cs_sexwrite_hr_d( const double x, char sep, char* buf, const long siz )
{

 logical sg;
 long d,m,s;
 double fs;
 double x1;
 TextWord buf1;
 integer tsiz = siz+1;
 if ( tsiz > UT_WORD_SIZE ) tsiz  = UT_WORD_SIZE;

 x1 = circ_d( x );
 utn_sx_wval_to_sxa_d( x1, 24, &sg, &d, &m, &s, &fs );
 utn_cs_sex_fmt( sg, d, m, s, fs, sep, buf1, tsiz );
 utn_cs_copy_siz( buf1+1, buf, siz );
 return( buf );
}

