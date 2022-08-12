/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <stdio.h>
#define UT_STATE_DEF 1
#define UT_DEFINE_GLOBALS 1
#include "jcmstate.h"


/*
 *----------------------------*
 * GLOBALS
 *----------------------------*
 */ 

/* Initialize the library */
void utn_init_jcmlib( void )
{
 utn_state();
}

void utn_free_jcmlib( void )
{
 UtilState state = utn_state();
 utn_state_free( state );
 utn_jcmlib_state = NULL;
}


void utn_dbg_set( long g )
{
 utn_state_dbg_set( NULL, g );
}


long utn_dbg_get( void )
{
 UtilState state = utn_state();
 return state->global_dbg;
}


UtilState utn_state( void )
{
 if ( !utn_jcmlib_state ) 
 {
  utn_jcmlib_state = utn_state_alloc();
  utn_init_state( utn_jcmlib_state );
 }
 return utn_jcmlib_state;
}

/*
 *----------------------------*
 * UtilState object
 *
 *  state::alloc
 *  state::free
 * Get data members
 *  state::ver
 *  state::fio_list
 *  state::dbg_get
 *  state::error_global
 *  state::get_ver_data

 *  state::escapes
 *  state::nulls
 *  state::seed
 * Set data members
 *  state::dbg_set
 *----------------------------*
 */ 

/* Initialize the library state */


UtilState utn_state_alloc( void )
{
 UtilState state = calloc( 1, sizeof( struct UtilState_s ));
 return state; 
}

void utn_state_free( UtilState state )
{
 utn_version_data_free( utn_state_ver_data( state ));
 utn_fio_list_free( utn_state_fio_list( state ) );
 utn_state_clear_error( state );
 utn_tpio_free( utn_state_tpio( state ));
 utn_null_free( utn_state_nulls(state));
 utn_esc_free( state->escapes );
 free( state->ver );
 free( state );
}

/* User routine to return library version */

void utn_ver_jcmlib( char* ver, const long maxlen )
{
 UtilState state = utn_state();
 utn_cs_copy_siz( state->ver, ver, maxlen );
}


void utn_state_error( UtilState state, char* buf )
{
 if ( !state) state = utn_state();
 state->error = utn_error( buf ); 
}

void utn_state_print_error( UtilState state )
{
 if (!state) state = utn_state();
 utn_error_print( state->error ); 
}

void utn_state_clear_error( UtilState state )
{
 utn_error_free( state->error );
 state->error = NULL;
}

GenError utn_state_get_error( UtilState state )
{
 if ( !state ) state = utn_state();
 return state->error;
}


/* Internal routine to initialize a state */
void utn_init_state( UtilState state )
{
 if ( !state ) return;
 state->ver = utn_cs_dup( JCMLIBVER );
 utn_state_dbg_set( state, 0 ); /* Refer to ut_constants */
 utn_state_ver_data( state );
 utn_state_fio_list( state );
 utn_state_tpio( state );
 utn_state_nulls( state );

 state->seed = 0;
}

unsigned int* utn_state_seed( UtilState state )
{
 if ( !state ) state = utn_state();
 return &state->seed;
}

FioFileList utn_state_fio_list( UtilState state )
{
 if ( !state ) state = utn_state();
 if ( !state->fio ) state->fio = utn_fio_list_init();

 return state->fio;
}

void utn_state_dbg_set( UtilState state, long g )
{
 if ( !state ) state = utn_state();
 state->global_dbg = g;
}

long utn_state_dbg_get( UtilState state )
{
 if ( !state ) state = utn_state();
 return state->global_dbg;
}



UtilVersionData utn_state_ver_data( UtilState state )
{
 if ( !state ) state = utn_state();
 if ( !state->versions )
 { 
   state->versions = utn_version_data_create();
   utn_ver_init_data( state->versions, JCMLIB, JCMLIBVER );
 }
 return state->versions;
}


Tpio utn_state_tpio( UtilState state )
{
 if ( !state ) state = utn_state();
 if ( !state->tpio ) state->tpio = utn_tpio_data_init();
 return state->tpio;
}


UtilNulls utn_state_nulls( UtilState state )
{
 if ( !state ) state = utn_state();
 if ( !state->nulls ) state->nulls = utn_null_alloc();
 return state->nulls;

}

/* Back compat */
void utn_error_status( void )
{
 utn_state_print_error( NULL );
}
