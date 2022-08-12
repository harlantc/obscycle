/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* Program Control Chracters */

#include "utlib.h"

/*
 *---------------------------
 * GLOBALS
 *---------------------------
 */

UtilEscData utn_esc_data = NULL;


static long utn_esc_id( const char* name );

void utn_esc_init( void )
{
 utn_esc_init_state( NULL );
}

 
UtilEscData utn_state_escapes( void )
{
 if ( !utn_esc_data ) utn_esc_data = utn_esc_alloc();
 return utn_esc_data;
}
 


/*
 *---------------------------
 * ESC
 * esc::alloc
 * esc::init_state
 * esc::free
 *Get by type
 * esc::special_char
 * esc::get_default_char
 *Get by name
 * esc::get
 * esc::get_default
 *Set by name
 * esc::reset
 *Print
 * esc::status
 *---------------------------
 */

UtilEscData utn_esc_alloc( void )
{
 UtilEscData edata = calloc( 1, sizeof( struct UtilEscapeData_s ));
 edata->data = calloc( ESC_NTYPES, sizeof( UtilEscapes ));
 edata->n = ESC_NTYPES;
 utn_esc_init_state( edata );
 return edata;
}

void utn_esc_init_state( UtilEscData edata )
{ 
 long type;
 UtilEscapes* data;
 char escdefaults[] = "\\(),;$$#$ ";
 char* escnames[] = { "ESC", "LPAR", "RPAR", "WORD", "STRING", "SYS", "CONT", "CMT", "MACRO", "ARG" };
 if ( !edata ) edata = utn_state_escapes(  );
 for ( type = 0; type < edata->n; type++ ) {
  data = &edata->data[type];
  utn_cs_copy( escnames[type], data->name  );
  data->echar  =   escdefaults[type];
  data->edefault = escdefaults[type];
 }
}


void utn_esc_free( UtilEscData edata )
{
 if ( edata )
  free( edata->data );
 free( edata );
}

static long utn_esc_id( const char* name )
{
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 long type;
 for ( type = 0; type < ESC_NTYPES; type++ ) {
  if ( utn_cs_eq( name, utn_esc_data[type].name ) ) return type;
 }
 return -1;
}



void utn_esc_get_name( const long type, char* name )
{
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 if ( type >= 0 )
  utn_cs_copy( utn_esc_data[type].name, name );
 else 
  utn_cs_copy( " ", name );
}
/*----------------------------------------------------------------------*/
/*
 * utn_esc_get_char
 *
 */

char utn_esc_get_default_char( const long type )
{
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 if ( type >= 0 )
  return( utn_esc_data[type].edefault );
 else 
  return ' ';
}

char utn_esc_special_char( const long type )
{
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 if ( type >= 0 )
  return( utn_esc_data[type].echar );
 else
  return ' ';
}

GenError utn_esc_reset( const char* name, const char val )
{
 GenError error = NULL;
 long type;
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 type = utn_esc_id( name );
 if ( type >= 0 )
   utn_esc_data[type].echar = val;
 else
 {
  TextBuf buf;
  snprintf( buf, UT_TEXT_SIZE, "Unknown escape keyword %s", name );
  error = utn_error( buf );
 }
 return error;
}




char utn_esc_get( const char* name )
{
 long type;
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 type = utn_esc_id( name );
 if ( type >= 0 ) 
  return utn_esc_data[type].echar;
 else
  return ' ';
}

char utn_esc_get_default( const char* name )
{
 long type;
 UtilEscData edata = utn_state_escapes(  );
 UtilEscapes* utn_esc_data = edata->data;
 type = utn_esc_id( name );
 if ( type >= 0 ) 
  return utn_esc_data[type].edefault;
 else
  return ' ';
 }


 

