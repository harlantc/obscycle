/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
#define MAYA_DEG_STD 5
/* Start the Maya day at 0h CST = 0600 UTC */
/* Delta of 0 gives a noon UTC start: this is 0600 UTC on Greg BC 3114 Aug 12 */
#define MAYA_LOCAL  0.75
#define AHAU_GMT  (584283.0 + MAYA_LOCAL)
#define TZOLKIN_NZERO 16
#define TZOLKIN_DZERO 19
#define HAAB_NZERO 347


void maya_msg( double t, integer degree )
{
 integer lc[20];
 TextBuf buf;
 jd_to_maya_long( t, lc, degree );
 maya_long_fmt( lc, degree, buf, UT_TEXT_SIZE );
 printf( "%s\n", buf );
 maya_long_fmt2( lc, degree, buf, UT_TEXT_SIZE );
 printf( "%s\n", buf );

}

void jd_to_maya_long( double t, integer* lc, integer degree )
{
 integer i_uinal = degree - 2;
 integer scale;
 integer m, i;
 integer n = t -  AHAU_GMT;
 if ( degree == 0 ) degree = MAYA_DEG_STD;
 for ( i = degree - 1; i >= 0; i-- )
 { 
  scale = ( i == i_uinal ) ? 18 : 20;
  m = n % scale;
  n = n / scale;
  lc[i] = m;
 }

}


/* Extended Maya long count */
double maya_long_to_jd( integer* lc, integer degree )
{
 integer n;
 integer scale, i;
 double t;
 integer i_uinal = degree - 2;
 if ( degree == 0 ) degree = MAYA_DEG_STD;
/* !    kin           uinal           tun        katun          baktun */
 n = 0;
 for ( i = 0; i < degree; i++ )
 { 
  scale = ( i == i_uinal ) ? 18 : 20;
  n = scale * n + lc[i];
 }
 t = AHAU_GMT + n;
 return t;
}

void maya_long_fmt2( integer* lc, integer degree, char* buf, integer maxlen )
{
 integer m = 9;
 char* names[] = { "kin", "uinal", "tun", "katun", "baktun", "pictun", "calabtun", "kinchitun", "alautun", "." };
 char* ptr = buf;
 integer i, j;
 for ( i = 0; i < degree; i++ )
 {
  j = degree - i - 1;
  if ( j >= m ) j = m;
  ptr += snprintf( ptr, maxlen, "%ld %s ", lc[i], names[j] );
 }
}

void maya_full_fmt( integer* lc, integer degree, integer tn, integer td, integer hn, integer hd, 
    char* buf, integer maxlen )
{
 TextCard mlc;
 TextCard haab;
 TextCard tzol;

 maya_long_fmt( lc, degree, mlc, maxlen );
 haab_fmt( hn, hd, haab, UT_CARD_SIZE ); 
 tzolkin_fmt( tn,td, tzol, UT_CARD_SIZE ); 
 snprintf( buf, maxlen, "%s %s %s", mlc, tzol, haab );
}

void aztec_full_fmt( integer* lc, integer degree, integer tn, integer td, integer hn, integer hd, 
    char* buf, integer maxlen )
{
 TextCard mlc;
 TextCard haab;
 TextCard tzol;

 maya_long_fmt( lc, degree, mlc, maxlen );
 haab_afmt( hn, hd, haab, UT_CARD_SIZE ); 
 tzolkin_afmt( tn,td, tzol, UT_CARD_SIZE ); 
 snprintf( buf, maxlen, "%s %s %s", mlc, haab, tzol );
}

void maya_long_fmt( integer* lc, integer degree, char* buf, integer maxlen )
{
 char* ptr = buf;
 integer i;
 for ( i = 0; i < degree-1; i++ )
 {
  ptr += snprintf( ptr, maxlen, "%ld.", lc[i] );
 }
 ptr += snprintf( ptr, maxlen, "%ld", lc[degree-1] );
}


void maya_jd_to_tzolkin( double t, integer* number, integer* day )
{
 integer n;

 n = ( t - AHAU_GMT );
 *number = ( n + TZOLKIN_NZERO ) % 13 + 1;
 *day    = ( n + TZOLKIN_DZERO ) % 20 + 1;
}

void maya_jd_to_haab( double t, integer* number, integer* haab_month )
{
 integer n;
 integer d;
 n = ( t - AHAU_GMT );
 d = (n + HAAB_NZERO ) % 365;
 *haab_month = d / 20 + 1;
 *number = d % 20 + 1;
}


void haab_fmt( integer n, integer m, char* buf, integer maxlen )
{
 char* names[] = { "UNK", "Pop", "Uo", "Zip", "Zotz", "Tzec", "Xul",
    "Yaxkin", "Mol", "Chen", "Yax", "Zac", "Ceh", "Mac", "Kankin", "Muan", 
    "Pax", "Kayab", "Cumku", "Uayeb" };
 if ( m < 0 || m > 19 ) m = 0;
 snprintf( buf, maxlen, "%2ld %s", n, names[m] );
}

void tzolkin_fmt( integer n, integer m, char* buf, integer maxlen )
{
 char* names[] = { "UNK", "Imix", "Ik", "Akbal", "Kan", "Chicchan", "Cimi", 
  "Manik", "Lamat", "Muluc", "Oc", "Chuen", "Eb", "Ben", "Ix", "Men", "Cib",
  "Caban", "Etz'nab", "Cauac", "Ahau" };
 if ( m < 0 || m > 20 ) m = 0;
 snprintf( buf, maxlen, "%2ld %s", n, names[m] );
}

void haab_afmt( integer n, integer m, char* buf, integer maxlen )
{
 char* names[] = { "UNK", "Tlaxochimaco", "Xocotlhuetzli",
 "Ochpaniztli", "Teotleco", "Tepeilhuitl", "Quecholli","Panquetzaliztli",
  "Atemoztli", "Tititl", "Izcalli", "Atlcahualo", "Tlacaxipeualiztli",
 "Tozoztontli", "Hueytozoztli", "Toxcatl", "Etzalcualiztli", 
  "Tecuilhuitontli", "Hueytechuilhuitl", "Nemontemi" };
 if ( m < 0 || m > 19 ) m = 0;
 snprintf( buf, maxlen, "%2ld %s", n, names[m] );
}

void tzolkin_afmt( integer n, integer m, char* buf, integer maxlen )
{
 char* names[] = { "UNK", "Cipactli", "Ehacatl", "Calli",  "Cuetzpallin",
 "Coatl", "Miquiztli", "Mazatl", "Tochtli", "Atl", "Itzcuintli", "Ozomatli",
 "Malinalli", "Acatl", "Ocelotl", "Cuauhtli", "Cozcacuauhtli", "Ollin",
 "Tecpatl", "Quiahuitl", "Xochitl" };
 if ( m < 0 || m > 20 ) m = 0;
 snprintf( buf, maxlen, "%2ld %s", n, names[m] );
}


