/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/*----------------------------------------------------------------------*/
/*
 * jcm_init
 *
 */

void utn_sys_os( char* sysos )
{
 utn_cs_copy( UT_ARCH, sysos );
}


/*----------------------------------------------------------------------*/
/*
 * utn_sys_filepath
 * Uses csproto.h tokenproto.h
 * Unix version only
 */

void utn_sys_filepath( const char* dir, const char* name, const char* ext, char* buf, long siz )
{
 logical current;
 logical absolute;

 current = utn_cs_eq( dir, "." ) && !utn_cs_is_blank( name );
 absolute = utn_cs_is_blank( dir ) || ( name && name[0] == '/' );

 if ( !absolute && !current ) utn_cs_put_token( dir, &buf, '/', &siz );
 if ( !utn_cs_is_blank( name ) ) utn_cs_put_cn( name, &buf, &siz );
 if ( !utn_cs_is_blank( ext ) ) { utn_cs_put_cn( ".", &buf, &siz ); utn_cs_put_cn( ext, &buf, &siz ); }
 if ( siz ) *buf = '\0';
}

/*----------------------------------------------------------------------*/
/*
 * utn_sys_pathstrip
 *
 * Remove leading directories from the path name. Unix only.
 */

void utn_sys_pathstrip( const char* path, char* dir, char* name, const long maxlen )
{
 char* ptr;
 long len;
 ptr = strrchr( path, '/' );
 if ( ptr ) {
  len = min_i( ptr - path, maxlen );
  utn_cs_copy_siz( path, dir, len );
  utn_cs_copy_siz( ptr + 1, name, maxlen );
 } else {
  *dir = 0;
  utn_cs_copy_siz( path, name, maxlen );
 }
}
