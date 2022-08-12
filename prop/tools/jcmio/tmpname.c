/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include <unistd.h>
#include <time.h>

char* utn_fio_tmpname( void )
{
 time_t now;
 TextCard name;
 while( UT_TRUE )
 {
  now = time( NULL );
  snprintf( name, UT_CARD_SIZE, "tmp.%ld", now );
  if ( access( name, F_OK ) != 0 )
   return utn_cs_dup( name );
 }
 return NULL;
}

