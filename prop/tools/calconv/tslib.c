/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

static void cal_ts_read_def_file( TimescaleList* list, char* filename );

TimescaleList* cal_ts_init( char* filename )
{
 TimescaleList* list = cal_ts_list_alloc( TS_NMAX );
 cal_ts_read_def_file( list, filename );
 return list;
}

static void cal_ts_read_def_file( TimescaleList* list, char* filename )
{
 FioFile in;
 TextCard line;
 char* ptr;
 TextWord type;
 TextWord zone;
 TextWord name;
 TextCard label;
 
 in = utn_fio_open_ar( filename );
 if ( in == 0 ) 
 {
   fprintf( stderr, "Warning: failed to open time zone file %s\n", filename );
   utn_fio_path_list();
   cal_ts_default( list );
 } else {

 while( utn_fio_read_line( in, line, CARD_SIZE ))
 {
  utn_cs_decmt( line, '\\', '#' );
  if ( !utn_cs_is_blank( line ) )
  {
   ptr = line;
   utn_cs_get_c( &ptr, type, WORD_SIZE );
   utn_cs_get_c( &ptr, zone, WORD_SIZE );
   utn_cs_get_c( &ptr, name, WORD_SIZE );
   utn_cs_get_cs( &ptr, label, CARD_SIZE );
   cal_ts_create( list, name, label, type, zone );
  }
 }
 utn_fio_file_close( in );

 }
}


void cal_ts_get_name( Timescale* ts, char* name )
{
 if ( !ts )
  utn_cs_copy( " ", name );
 else
  utn_cs_copy( ts->name, name );
}

integer cal_ts_create( TimescaleList* list, char* name, char* label, char* type,
 char* zonecode )
{
 Timescale* ts;
 integer type_id;
 integer zone;
 ts = cal_ts_alloc( list );
 if ( !ts )
  return 0;

 ts->name = utn_cs_dup( name );
 ts->label = utn_cs_dup( label );
 type_id = cal_tstype_get_id( type );
 zone = cal_zone_get_code( zonecode );
 ts->type = type_id;
 ts->zone = zone;
 return list->n;
}

Timescale* cal_ts_alloc( TimescaleList* list )
{
 Timescale* ts;
 if ( list->n >= list->nmax )
 {
  list->data = realloc( list->data, 2 * list->nmax * sizeof( Timescale ));
  list->nmax *= 2;
 }
 ts = &list->data[list->n++];
 ts->no = list->n;
 return ts;
}

TimescaleList* cal_ts_list_alloc( integer n )
{
 TimescaleList* list;
 list = calloc( 1, sizeof( TimescaleList ));
 list->n = 0;
 list->nmax = n;
 list->data = calloc( n, sizeof( Timescale ));
 return list;
}

void cal_ts_list_free( TimescaleList* list )
{
 integer i;
 Timescale* t;
 for ( i =0; i < list->n; i++ )
 {
  t= &list->data[i];
  free( t->name ); 
  free( t->label );
 }
 free( list->data );
 free( list );
}

Timescale* cal_ts_match( TimescaleList* list, char* opt )
{
 integer i;
 Timescale* t = NULL;

 if ( utn_cs_is_blank( opt ))
  return t;

 for ( i =0; i < list->n; i++ )
 {
  t= &list->data[i];
  if ( utn_cs_uceq( opt, t->name ))
   return t;
 }
 t = NULL;
 return t;
}

Timescale* cal_ts_parse_spec( TimescaleList* list, char* opt )
{
 Timescale* t = NULL;
 TextCard buf;
 integer id;
 char* default_opt= "UTC";    /* Default to TT or UTC? */

 if ( utn_cs_is_blank(opt) )
  opt = default_opt;

 t = cal_ts_match( list, opt );
 if ( !t ) {
  if ( *opt == '+' || *opt == '-' )
  {
   sprintf( buf, "Zone %s", opt );
   id = cal_ts_create( list, opt, buf, "UTC", opt );
   t = &list->data[id-1];
  } else if ( gcal_verbose ) {
   sprintf( buf, "Unknown timescale %s", opt );
   utn_fio_tmsg( buf );
  }
 }
 return t;
}


void cal_ts_list_hdr( void )
{
 utn_fio_msg( " " );
 utn_fio_msg( "TSID  Timescale   Timescale Name                   Type                    Zone Par   Zone " );
 utn_fio_msg( " " );
}

void cal_ts_print( Timescale* ts )
{
 TextBuf buf;
 TextCard typelabel;
 TextCard zonelabel = " ";
 
 cal_tstype_get_label( ts->type, typelabel );
 if ( ts->type == UTC_ )
   cal_zone_set_code( (integer)ts->zone, zonelabel );
 sprintf( buf, "%-6ld %-12s%-32s %-24s%8ld %-8s",
  ts->no, ts->name, ts->label, typelabel, (integer)ts->zone, zonelabel );
 utn_fio_msg( buf );
}

void cal_ts_list( TimescaleList* list )
{
 integer tsid;
 cal_ts_list_hdr();
 for ( tsid = 1; tsid < list->n; tsid++ )
 {
  cal_ts_print( &list->data[tsid-1] );
 }
 utn_fio_msg( " " );
}
