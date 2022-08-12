/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <unistd.h>

/* Return current working directory with or without trailing / */

void utn_sys_cwd( char* buf, integer maxlen, logical trail )
{


 getcwd( buf, maxlen );
 if ( utn_cs_is_blank( buf ))
 {
  utn_sys_getenv( "PWD", buf );
  if(  utn_cs_is_blank( buf ))
   utn_sys_getenv( "cwd", buf );
  if(  utn_cs_is_blank( buf ))
   utn_cs_copy( ".", buf );
 }
 if ( trail ) strcat( buf, "/" );
}
 

