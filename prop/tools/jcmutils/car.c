/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


void utn_ar_copy_c( const char** x, char** y, const long n )
{
 long i;
 for ( i = 0; i < n; i++ ) { utn_cs_copy( x[i], y[i] ); } 
}

void utn_ar_zero_c( char** x, const long n )
{
 long i;
 for ( i = 0; i < n; i++ ) { utn_cs_zero( x[i] ); }
}


void utn_ar_fill_c( char** x, const long n, const char* x0 )
{
 long i;
 for ( i = 0; i < n; i++ ) { utn_cs_copy( x0, x[i] ); }
}


