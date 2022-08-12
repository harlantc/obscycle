/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <ctype.h>
#include "utlib.h"

logical utn_cs_digit_char( const char c )
{
 return( isdigit( c ) );
}

logical utn_cs_alpha_char( const char c )
{
 return( isalpha( c ) );
}


logical utn_cs_numeric_char( const char c )
{
 return( isdigit( c ) || c == '+' || c == '-' || c == '.' );
}

logical utn_cs_nancode( const char* buf )
{
 return( 
 utn_cs_eq( buf, "-" ) ||
 utn_cs_eq( buf, "NA" ) ||
 utn_cs_eq( buf, "NAN" ) || utn_cs_eq( buf, "NaN" )
 );
}

