/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
#include <string.h>
double ast_mag_calc( MagnitudeBandList list, double mag, char* bandname )
{
 MagnitudeBand band;
 double s;
 if ( !list ) list = ast_mag_band_read( NULL );
 band = ast_mag_band( list, bandname );
 if ( band )
  s = band->f0 * pow( 10.0, -AST_MAG_DEX * mag );
 else
  s = utn_null_d();

 return s;
}

/* What's the frequency?
   Check band names and  '... GHz' format.
 */

double ast_mag_freq( MagnitudeBandList list, char* bandname )
{
 MagnitudeBand band;
 TextWord uband;
 TextWord pfx;
 double u  = 0.0;
 double f;
 integer j;

 band = ast_mag_band( list, bandname );

 if ( band )
  u = band->u;
 else
 {
  utn_cs_copy( bandname, uband );
  j = utn_cs_index( uband, "HZ" );
  if ( j > 0 ) 
  {
   utn_cs_get_ss( bandname, pfx, 1, j-1 );
   f = utn_si_read_d( pfx );
   if ( f > 0 )
    u = log10( f );
  } else {
    u = utn_null_d();
  }
 }

 return u;
}


/* Delta log mag for given convention */

double ast_mag_delta( char* type )
{
 double dlf;
 TextWord utype;
 utn_cs_copy( type, utype );
 utn_cs_upper( utype );
 if ( utn_cs_eq( type, "CIT" ))
  dlf = 0.0;
 else if ( utn_cs_eq( type, "AZ" ))
  dlf = AST_MAG_DEX * AST_VEGA_AZ;
 else if ( utn_cs_eq( type, "AZIR" ))
  dlf = AST_MAG_DEX * AST_VEGA_AZIR;
 else if ( utn_cs_eq( type, "AB" ))
  dlf = AST_MAG_DEX * AST_VEGA_AB;
 else
  dlf = 0.0;
 
 return dlf; 
}

MagnitudeBand ast_mag_band( MagnitudeBandList list, char* bandname )
{
 integer i;
 MagnitudeBand band;

 if ( !list ) return NULL;

 for ( i = 0; i < list->n; i++ ) 
 {
  band = list->data[i];
  if ( utn_cs_eq( bandname, band->name ))
   return band;
 }
 return NULL;
}

void ast_mag_band_list_free( MagnitudeBandList list )
{
 integer i;
 if ( !list ) return;
 for ( i =0; i < list->n; i++ )
 {
  free( list->data[i] );
 }
 free( list->data );
 free( list );
}

MagnitudeBandList ast_mag_band_read( char* filename )
{
 FioFile file;
 char* ptr;
 TextCard line;
 MagnitudeBandList list = NULL;
 MagnitudeBand band;
 Filename magfile;
 if ( filename && *filename )
  utn_cs_copy( filename, magfile );
 else
  utn_cs_copy( AST_MAG_DEFAULT_FILE, magfile );

 file = utn_fio_open_ar( magfile );
 if ( !file )
 {
  sprintf( ast_state->errbuf, "Failed to find magnitude band data file %s", magfile );
  return list;
 }
 list = calloc( 1, sizeof( struct MagnitudeBandList_s ));
 list->nmax = 20;
 list->data = calloc( list->nmax, UT_SZ_P );

 while( utn_fio_read_line( file, line, CARD_SIZE ) )
 {
  if ( !utn_cs_is_blank( line ) && *line != '#' )
  {
   if ( utn_cs_eq_siz( line, "SYSTEM", 6 ))
   {
    ptr = strchr( line, '=' );
    ptr++;
    utn_cs_get_c( &ptr, list->name, CARD_SIZE );
   } else {
    band = calloc( 1, sizeof( struct MagnitudeBand_s ));
    list->data[list->n] = band;   
    list->n++;
    if ( list->n >= list->nmax )
    {
     list->nmax = 2 * list->nmax;
     list->data = realloc( list->data, list->nmax * UT_SZ_P );
    }
    ptr = line;
    utn_cs_get_c( &ptr, band->name, WORD_SIZE );
    band->u  = utn_cs_get_d( &ptr );
    band->u1 = utn_cs_get_d( &ptr );
    band->u2 = utn_cs_get_d( &ptr );
    band->f0 = utn_cs_get_d( &ptr );
   }
  }
 }
 utn_fio_file_close( file );
 return list;
}

double ast_parse_freq( MagnitudeBandList list, char* buf )
{
 double u = 0.0; 
 double nu;
 char* opt;
 integer itemcode[AST_MAX_ITEMS];
 integer itempos[AST_MAX_ITEMS];
 TextBuf ebuf;
 integer nitems; 
 TextWord bname;
 char* ptr;
 integer debug = 0;
 char mode[] = "GROUP,NOAN,WORD,CLASS";

 ptr = buf;
 utn_cs_parser( ptr, itemcode, itempos, &nitems, mode, debug );


 utn_cs_get_ss( ptr, bname, 1, itempos[0] );
 if ( nitems == 1 && itemcode[0] == (integer)PTT_Word )
 {
  u = ast_mag_freq( list, bname );
 } else if ( nitems == 2 && itemcode[0] == (integer)PTT_Word && itemcode[1] == (integer)PTT_Symbol ) {
/* e.g. L' */
  u = ast_mag_freq( list, buf );
 } else if ( nitems == 1 && itemcode[0] == (integer)PTT_Number ) {
  u = utn_cs_get_d( &ptr );
 } else if ( itemcode[0] == (integer)PTT_Number ) {
  ptr = bname;
  nu = utn_cs_get_d( &ptr );
  opt = buf + itempos[0];
  u = ast_parse_freq_unit( nu, opt );  
 } else {
  u = utn_null_d();
  utn_cs_parse_fmt( buf, itemcode, itempos, nitems, ebuf ); 
  sprintf( ast_state->errbuf, "Error parsing frequency from %s", ebuf  );
 }
 return u;
}


void ast_band_regime_name( integer i, char* name )
{
 char* regimes[9] = { "Spurious", "Radio", "Millimetre", "Far Infrared", 
      "Optical/Infrared", "Ultraviolet", "Extreme Ultraviolet", "X-ray", "Gamma ray" };
 if ( i >= 0 && i <= 9 )
  utn_cs_copy( regimes[i], name );
}

integer ast_band_find_regime( double u )
{

 integer n = 8;
 integer i;
 double boundaries[] = { 0.0, 11.0, 12.47, 13.40, 15.00, 15.52, 16.00, 19.38 };
 for ( i =0; i < n; i++ )
 {
  if ( u <= boundaries[i] )
   return i;
 }
 return n+1;
}

double ast_parse_freq_unit( double nu, char* opt )
{
 TextWord uopt;
 integer n;
 double u = 0.0;
 integer exponent;
 char* tail;
 logical ok;
 char tmp[8];
 utn_cs_copy( opt, uopt );
 utn_cs_upper( uopt );
 n = utn_cs_ends( uopt );
 tail = opt + n - 2;

 if ( utn_cs_eq( uopt, "MU" ))
 {
  u = ast_lam_to_freq( nu );
 } else if ( utn_cs_eq( opt, "A" )) {
  u = ast_lam_to_freq( nu / 10000.0 );
 } else if ( utn_cs_eq( tail, "Hz" ) ) {
  if ( n >= 3 ) 
  {
   utn_cs_get_ss( opt, tmp, 1, n-2 );
   exponent = utn_si_exp( tmp, &ok );
   nu = nu * tpow_d( exponent );
  }
  u = ast_hz_to_freq( nu );
 } else if ( utn_cs_eq( tail, "eV" )) {
  if ( n == 3 )
  {
   utn_cs_copy_siz( opt, tmp, 1 );
   exponent = utn_si_exp( tmp, &ok );
   nu = nu * tpow_d( exponent );
  }
  u = ast_kev_to_freq( nu / 1000.0 );
 } else if ( utn_cs_eq( tail+1, "m" )) {
  if ( n == 2 )
  {
   utn_cs_copy_siz( opt, tmp, 1 );
   exponent = utn_si_exp( tmp, &ok );
   nu = nu * tpow_d( exponent );
  }
  u = ast_lam_to_freq( nu * 1.0E6 );
 }
 return u;
}

/* SILAM */
void ast_compose_freq( double u, char* buf, integer maxlen )
{
 double y;
 double x;
 TextCard lbuf;
 TextCard mbuf;
 y = AST_LOG_MU - u;
 
 if ( u > AST_LOG_MU && u < 16.0 ) 
 {
   x = pow( 10.0, AST_LOG_MU - u ) * 10000.0;  /* Angstroms */
   sprintf( lbuf, "%7.2f  A", x ); 
 } else if ( u > 16.0 ) {
   x = pow( 10.0, u - AST_LOG_KEV ) * 1000.0; /* eV */
   utn_si_write_d( x, mbuf );
   sprintf( lbuf, "%-9s%s", mbuf, "eV" );
 } else {
   x = pow( 10.0,  AST_LOG_MU - u - 6 );
   utn_si_write_d( x, mbuf );
   sprintf( lbuf, "%-9s%s", mbuf, "m" );
 }
 utn_cs_copy_siz( lbuf, buf, maxlen );
}


