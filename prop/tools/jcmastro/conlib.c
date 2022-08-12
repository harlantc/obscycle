/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"
#include <string.h>

/*  Name of this file was con.c but apparently this is illegal on VFAT, changed
    at KJG's request 
 */

ConstellationBoundary ast_constell_parse_line( ConstellationData data, char* line );

void ast_constell_free( ConstellationData data )
{
 if ( !data ) return;
 utn_ar_free_c( data->abbr );
 utn_ar_free_c( data->nominative );
 utn_ar_free_c( data->genitive );
 free( data->meridians );
 free( data->parallels );
 free( data );
}


ConstellationData ast_constell_init( char* names_file, char* boundary_file )
{
 ConstellationData data;
 data = ast_constell_alloc( names_file );
 if ( data )
  ast_constell_read_boundaries( data, boundary_file );
 return data;
}

integer ast_constell_find( ConstellationData data, double ra, double dec, char* abbr )
{
 logical seek = 1;
 integer con = 0;
 integer i = 0;
 ConstellationBoundary* b;
 if ( !data ) 
   data = ast_constell_init( NULL, NULL);
/* Assumes parallels are dec-ordered */
 if ( !data )  /* Failed */
  return con;

 while ( seek && i < data->npar )
 {
   b = &data->parallels[i];  
   if ( dec >= b->dec1 && ra >= b->ra1 && ra <= b->ra2 )
   {
    seek = 0;
    con = b->con1;    
   }
   i++;
 }
 if (  abbr )
 {
  if ( con ) 
   utn_cs_copy( data->abbr[con-1], abbr );
  else
   utn_cs_copy( "---", abbr );
 }
 return con; 
}

/*
 *  The meridians are not actually used but are good for cross-checking and plotting
 *  The parallels must be dec-ordered.
 */
void ast_constell_read_boundaries( ConstellationData data, char* boundary_file )
{
 char* filename = boundary_file;
 char* default_file = "Constellations";
 FioFile in;
 TextCard line;
 TextBuf buf;
 ConstellationBoundary b;

 if ( !data->meridians )
  data->meridians = calloc( AST_CON_MAX_BDY, sizeof( ConstellationBoundary ));

 if ( !data->parallels )
  data->parallels = calloc( AST_CON_MAX_BDY, sizeof( ConstellationBoundary ));

 data->nmer = 0;
 data->npar = 0;

 if ( !filename ) filename = default_file;

 in = utn_fio_open_ar( filename );
 if ( !in )
 {
  sprintf( buf, "Failed to open file %s", filename );
  utn_fio_tmsg( buf );
  return;
 }
 while( utn_fio_read_line( in, line, CARD_SIZE ))
 {
  if ( line[0] != '#' && !utn_cs_is_blank( line ) ) 
  {
   b = ast_constell_parse_line( data, line );
   switch( b.type )
   { 
    case AST_CON_MERIDIAN:
     data->meridians[data->nmer++] = b;
     break;
    case AST_CON_PARALLEL:
     data->parallels[data->npar++] = b;
     break;
    case AST_CON_ERROR:
    default:
     break;
   }
  }
 }
 utn_fio_file_close( in );
 return;
}


ConstellationBoundary ast_constell_parse_line( ConstellationData data, char* line )
{
 ConstellationBoundary b;
 TextWord rabuf1;
 TextWord rabuf2;
 TextWord decbuf1;
 TextWord decbuf2;
 TextBuf buf;
 double tol = 1.0/3600.0;
 char conbuf1[8];
 char conbuf2[8];
 char* ptr;
 utn_cs_get_ss( line, rabuf1, 1, 9 );
 utn_cs_get_ss( line, rabuf2, 13, 21 );
 utn_cs_get_ss( line, decbuf1, 25, 33 );
 utn_cs_get_ss( line, decbuf2, 37, 45 );
 utn_cs_get_ss( line, conbuf1, 49, 51 );
 utn_cs_get_ss( line, conbuf2, 53, 55 );
 b.con1 = ast_constell_get_no( data, conbuf1 );
 b.con2 = ast_constell_get_no( data, conbuf2 );
 ptr = rabuf1;
 b.ra1 = sxg_parse_val( &ptr ) * 15.0 / 3600.0;
 ptr = rabuf2;
 b.ra2 = sxg_parse_val( &ptr ) * 15.0 / 3600.0;
 ptr = decbuf1;
 b.dec1 = sxg_parse_val( &ptr ) / 3600.0;
 ptr = decbuf2;
 b.dec2 = sxg_parse_val( &ptr ) / 3600.0;
 if ( fabs( b.ra1 - b.ra2 ) < 15.0 * tol )
  b.type = AST_CON_MERIDIAN;
 else if ( fabs(b.dec1 - b.dec2 ) < tol )
 {
  b.type = AST_CON_PARALLEL;
  if ( b.ra2 < b.ra1 )
  {
   sprintf( buf, "Error in constellations file: RA= %20.10f %20.10f\n", b.ra1, b.ra2 );
   utn_fio_tmsg( buf );
  }
 } else {
  b.type = 0;
  sprintf( buf, "Error in constellations file: Dec= %20.10f %20.10f\n", b.dec1, b.dec2 );
  utn_fio_tmsg( buf );
 }
 return b;
}


ConstellationData ast_constell_alloc( char* names_file )
{
 char* filename = names_file;
 char* default_names_file = "Constellation.names";
 FioFile in;
 TextBuf buf;
 TextCard line;
 TextWord tmp;
 integer con;
 ConstellationData data;
/* Read constellation names */
 if ( !filename ) filename = default_names_file;

 data = calloc( 1, sizeof( struct ConstellationData_s ));
 data->n = AST_NO_CONSTELL;
 data->abbr = utn_ar_alloc_c( 4, AST_NO_CONSTELL );
 data->nominative = utn_ar_alloc_c( WORD_SIZE, AST_NO_CONSTELL );
 data->genitive   = utn_ar_alloc_c( WORD_SIZE, AST_NO_CONSTELL );
 sprintf( buf, "Reading Con data from %s", filename );
 utn_msg_d( 1, buf );
 in = utn_fio_open_ar( filename );
 if ( !in )
 {
  sprintf( buf, "Failed to open Constellation file %s", filename );
  utn_fio_tmsg( buf );
  return NULL;
 }
 while( utn_fio_read_line( in, line, CARD_SIZE ))
 {
  if ( line[0] != '#' && !utn_cs_is_blank( line ) ) 
  {
   utn_cs_get_ss( line, tmp, 1, 3 );
   con = utn_cs_read_i( tmp );
   if ( con > 0 && con <= AST_NO_CONSTELL )
   {
    utn_cs_get_ss( line, data->abbr[con-1], 5, 7 );
    utn_cs_get_ss( line, data->nominative[con-1], 9, 30 );
    utn_cs_get_ss( line, data->genitive[con-1], 32, 50);
   }
  }
 }
 utn_fio_file_close( in );
 return data;
}

integer ast_constell_get_n( ConstellationData data )
{
 if ( data )
  return data->n;
 else
  return AST_NO_CONSTELL;
}

/* 
 *  If no constellation found, leave pointer unchanged.
 */
integer ast_constell_parse( ConstellationData data, char** ptr )
{
 TextWord word;
 TextWord word2;
 TextWord two_word;
 char* uptr = *ptr;
 char* uptr2 = *ptr;
 integer con = 0;
 utn_cs_get_c( &uptr, word, WORD_SIZE );
 utn_cs_mixcase( word );

/* Two word search first: to match "Leo Minor" in preference to "Leo"  */
 if ( uptr && utn_cs_alpha_char( *uptr ) )
 {
  uptr2 = uptr;
  utn_cs_get_c( &uptr2, word2, WORD_SIZE );
  utn_cs_mixcase( word2 );
  if ( strlen( word ) + strlen( word2 ) + 2 < WORD_SIZE )
  {
   sprintf( two_word, "%s %s", word, word2 );
   con = ast_constell_get_no( data, two_word );
   if ( con > 0 )
    *ptr = uptr2;
  }
 }

/* One word search */
 if ( !con )
  con = ast_constell_get_no( data, word );
 if ( con > 0 )
  *ptr = uptr;

 return con;
}

integer ast_constell_get_no( ConstellationData data, char* name )
{
 integer con;
 TextCard uname;

 if ( !data ) return 0;
 utn_cs_copy_siz( name, uname, CARD_SIZE );
 con = utn_ar_search_c( uname, data->abbr, data->n );
 if ( !con )
  con = utn_ar_search_c( uname, data->nominative, data->n );

 if ( !con )
  con = utn_ar_search_c( uname, data->genitive, data->n );

 return con;
}

void ast_constell_get_name( ConstellationData data, integer con, integer mode, char* name, integer maxlen )
{
 char** ptr;
 if ( data )
 {
  switch( mode )
  {
   case AST_CON_NOMINATIVE:
    ptr = data->nominative;
    break;
   case AST_CON_GENITIVE:
    ptr = data->genitive;
    break;
   case AST_CON_ABBR:
   default: 
    ptr = data->abbr;
  }
  utn_cs_copy_siz( ptr[con-1], name, maxlen );
 }
}

/*
 * IAU names:
 *    2+2     16+26
 *    4+2     1623+26
 *    4+3     1623+265
 *    4+4     1623-6916
 *   J4+4     J1623-6916
 *    5+4     1623.2+2638
 *    6+4     162348+2638   (ESO)
  *   7+6     073620.0+702255 (87GB)
 */
void ast_iau_name( double ra, double dec, char* mode, char* name, integer maxlen )
{
 double ra_hr;
 TextWord xbuf;
 TextWord ybuf;
 char c;
 char prefix = ' ';
 TextCard buf;
 integer nr = 4;
 integer nd = 3;  /* Default precision */
 char* mptr = mode;
 
 if ( mptr )
 {
  if ( utn_cs_alpha_char( *mptr ) )
  {
   prefix = *mptr++;
  }
  nr = utn_cs_read_digit( *mptr++ );
  if ( *mptr == '+' ) mptr++;
  nd = utn_cs_read_digit( *mptr );  
 }
 ra_hr = ra / 15.0;
 if ( ra_hr < 0.0 || ra_hr > 24.0 || fabs( dec ) > 90.0 )
 {
  utn_cs_copy( "---- ---", buf );
 } else {
  c = ( dec < 0.0 ) ? '-' : '+';
  ast_iau_format_arg( ra_hr, nr, xbuf );
  ast_iau_format_arg( dec, nd, ybuf );
  if ( prefix == ' ' )
   sprintf( buf, "%s%c%s", xbuf, c, ybuf );
  else
   sprintf( buf, "%c%s%c%s", prefix, xbuf, c, ybuf );
 }
 utn_cs_copy_siz( buf, name, maxlen );
}

void ast_iau_format_arg( double dec, integer nd, char* ybuf )
{
 integer deg;
 double frac;
 double mnx;
 double mnf;
 integer mn;
 double s;
 double y = 0;
 integer ni= 0;
 integer nf;

  frac_d( fabs( dec ), &deg, &frac );
  mnx = 60.0* frac;
  frac_d( mnx, &mn, &mnf ); 
  s = 60.0 * mnf;
  if ( nd < 3 ) 
  {
   y = deg;
   ni = 2;
  } else if ( nd == 3 ) {
   y = fabs( 10 * dec );
   ni = 3;
  } else if ( nd == 4 || nd == 5 ) {
   y = deg * 100 + mnx;
   ni = 4;
  } else if ( nd >= 6 ) {
   y = deg * 10000 + mn * 100 + s;
   ni = 6;
  }
  nf = nd - ni;
  utn_cs_writef_zeros( y, ni, nf, ybuf, WORD_SIZE );
}

