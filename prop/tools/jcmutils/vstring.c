/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <stdarg.h>

long var_sprintf( char* buf, integer length, char* format, ... )
{
 long n;
 va_list args;
 va_start( args, format );
 n = vsnprintf( buf, length, format, args );
 va_end( args );
 if ( n < 0 )
   buf[length-1] = '\0';
 return n;
}


