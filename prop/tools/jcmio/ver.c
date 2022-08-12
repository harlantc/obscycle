/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"


UtilVersionData utn_version_data_create( void )
{
  UtilVersionData verdata;
  verdata = calloc( 1, sizeof( struct UtilVersionData_s ));
  verdata->nver = 0;
  return verdata;
}

void utn_version_data_free( UtilVersionData verdata )
{
 utn_ar_free_c( verdata->program );
 utn_ar_free_c( verdata->version );
 free( verdata );
}

/*
 *   Add program prog, version ver to the version tree.
 *   Warns if same program loaded with different versions
 *   If verbose set, displays state
 */
logical utn_ver_init( const char* prog, const char* ver )
{
 return utn_ver_init_data( NULL, prog, ver );
}

logical utn_ver_init_data( UtilVersionData verdata, const char* prog, const char* ver )
{
 long i;
 TextCard buf;

 if ( !verdata ) verdata = utn_ver_data( );
 if ( verdata->program == NULL ) {
  verdata->program = utn_ar_alloc_c( UT_CARD_SIZE, UT_NVER );
  verdata->version = utn_ar_alloc_c( UT_WORD_SIZE, UT_NVER );
  verdata->nver = 0;
 }
 i = utn_ar_cmatch_c( prog, verdata->program, verdata->nver );
 if ( i >= 0  ) {
  if ( !utn_cs_eq( ver, verdata->version[i] ) ) 
  {
    TextBuf tmp;
    snprintf( tmp, UT_TEXT_SIZE, "JCMLIB WARNING: Package version mismatch for %s ", prog );
    utn_state_error( NULL, tmp );
  }
  return( UT_TRUE );
 } else {
  if ( verdata->nver < UT_NVER-1 ) verdata->nver++;
  i = verdata->nver - 1;
  utn_cs_copy_siz( prog, verdata->program[i], UT_CARD_SIZE );
  utn_cs_copy_siz( ver, verdata->version[i], UT_WORD_SIZE );
  snprintf( buf, UT_CARD_SIZE, "Init %s %s\n", prog, ver );
  utn_msg_d( UT_VERBOSE, buf );
  return( UT_FALSE );
 }
}

/*
 *   Display the version tree to MSG
 */
void utn_ver_msg( void )
{
 TextCard buf;
 long i;
 UtilVersionData verdata = utn_ver_data( );

 for ( i = 0; i < verdata->nver; i++ ) {
  snprintf( buf, UT_CARD_SIZE, "%s %s", verdata->program[i], verdata->version[i] );
  utn_fio_msg( buf );
 }
}


UtilVersionData utn_ver_data( void )
{
 return utn_state_ver_data( NULL );
}

