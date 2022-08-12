/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


long utn_ar_match_s( 
 const short value,
 const short* list,
 const long n
)
{
 long i;
 for ( i = 0; i < n; i++ ) {
  if ( value == list[i] ) return( i+1 );
 }
 return( 0 );
}

long utn_ar_match_i( 
 const long value,
 const long* list,
 const long n
)
{
 long i;
 for ( i = 0; i < n; i++ ) {
  if ( value == list[i] ) return( i+1 );
 }
 return( 0 );
}



long utn_ar_match_r( 
 const float value,
 const float* list,
 const long n
)
{
 long i;
 for ( i = 0; i < n; i++ ) {
  if ( value == list[i] ) return( i+1 );
 }
 return( 0 );
}


long utn_ar_match_d( 
 const double value,
 const double* list,
 const long n
)
{
 long i;
 for ( i = 0; i < n; i++ ) {
  if ( value == list[i] ) return( i+1 );
 }
 return( 0 );
}


