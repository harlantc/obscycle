/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

/* Generic errors */

void utn_error_free( GenError error )
{
 free( error );
}

GenError utn_error( char* buf )
{
 GenError error = calloc( 1, sizeof( struct GenError_s ));
 error->fatal = 1;
 utn_cs_copy_siz( buf, error->buf, UT_TEXT_SIZE );
 return error;
}

GenError utn_warn( char* buf )
{
 return utn_warn_code( buf, 0 );
}

GenError utn_warn_code( char* buf, integer code )
{
 GenError error = calloc( 1, sizeof( struct GenError_s ));
 error->fatal = 0;
 error->code = code;
 utn_cs_copy_siz( buf, error->buf, UT_TEXT_SIZE );
 return error;
}

GenError utn_fatal_code( char* buf, integer code )
{
 GenError error = calloc( 1, sizeof( struct GenError_s ));
 error->fatal = 1;
 error->code = code;
 utn_cs_copy_siz( buf, error->buf, UT_TEXT_SIZE );
 return error;
}




