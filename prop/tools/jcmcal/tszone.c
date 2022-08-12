/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"


/*
 *  Map a time zone code like "-0300" to the number of seconds
 *  relative to GMT, e.g "-0300" maps to -10800.
 */
integer cal_zone_get_code( char* zonename )
{
 integer val;
 integer k;
 integer hr,mins;
 integer dir = 0;

 char sign;
 if ( utn_cs_is_blank( zonename ) || utn_cs_eq( zonename, "0" ) )
 {
  val = 0;
 } else {
  sign = zonename[0];
  k = utn_cs_read_i( zonename + 1 );
  hr = rmod_i( k, &mins, 100 );
  if ( sign == '-' ) 
   dir = -1;
  else if ( sign == '+' ) 
   dir = +1;
  else
  {
   TextCard buf;
   snprintf( buf, UT_CARD_SIZE, "Bad time zone label %s", zonename );
   utn_fio_tmsg( buf );
  }
  val = utn_sx_sxa_to_val_i( dir, hr, mins, 0 );
 }
 return val;
}

/*
 *  Maps zone in seconds to label in hhmm.
 */
void cal_zone_set_code( integer zone, char* label )
{
 integer dir, hr, mins, sec;
 char sign;
 char buf[12]; 
 utn_sx_val_to_sxa_i( zone, &dir, &hr, &mins, &sec );
 sign = ( dir < 0 ) ? '-' : '+' ;
 snprintf( buf, 12, "%c%2.2ld%2.2ld", sign, hr, mins );
 utn_cs_copy( buf, label );   
}
