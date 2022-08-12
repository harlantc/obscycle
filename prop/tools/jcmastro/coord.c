/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <stdio.h>
#include <string.h>
#include "jcmastro.h"

/* Packed to unpacked */
void coord_unpack( double* xp, double* yp )
{
 logical plus;
 integer d, m, s;
 double fs;
 double x = *xp;
 double y = *yp;
 utn_sx_psx_to_sxa_d( x, &plus, &d, &m, &s, &fs );
 *xp = utn_sx_sxa_to_val_d( iplus(plus), d, m, s, fs );
 utn_sx_psx_to_sxa_d( y, &plus, &d, &m, &s, &fs );
 *yp = utn_sx_sxa_to_val_d( iplus(plus), d, m, s, fs );
}


void coord_write( double ra, double dec, char* buf )
{
 long siz1 = 12;
 long siz2 = 13;
 char sep = ':';
 utn_cs_sexwrite_hr_d( ra, sep, buf, siz1 );
 strcat( buf, " " );
 utn_cs_sexwrite_d( dec, sep, buf+siz1+1, siz2 );
}


logical coord_read( double* ra, double* dec )
{
 TextCard buf;
 char* ptr;
 utn_tpio_cs( "RA", buf, CARD_SIZE );
 if ( utn_cs_is_blank( buf ) ) {
  *ra = 0.0; *dec = 0.0; return( FALSE );
 }
 if ( utn_cs_alpha_char( buf[0] ) ) return( FALSE );
 ptr = buf;
 *ra = sxg_parse_val( &ptr ) * 15 / 3600.0;  /* Hours to degrees */
 if ( ptr == NULL ) {
  utn_tpio_cs( "Dec", buf, CARD_SIZE );
  ptr = buf;
 }
 *dec = sxg_parse_val( &ptr ) / 3600.0 ;
 return( TRUE );
}

logical coord_read_c( char* buf, double* ra, double* dec )
{
 char* ptr;
 if ( utn_cs_is_blank( buf ) ) {
  *ra = 0.0; *dec = 0.0; return( UT_FALSE );
 }
 if ( utn_cs_alpha_char( buf[0] ) ) return( UT_FALSE );
 ptr = buf;
 *ra = sxg_parse_val( &ptr ) * 15 / 3600.0;  /* Hours to degrees */
 if ( ptr == NULL ) {
  *dec = 0.0;
  return UT_FALSE;
 }
 *dec = sxg_parse_val( &ptr ) / 3600.0 ;
 return( UT_TRUE );
}

