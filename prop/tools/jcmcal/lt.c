/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

double cal_tai_minus_lt( edouble lt, integer zone )
{
 integer day;
 double sec;
 LeapInfo info;
 double dt;
 edouble tai;
 cal_jde_to_daysec( lt, &day, &sec );
 if ( day < UTC_START_JD )
  dt = -zone;
 else if ( day < UTC_LEAP_START )
 {
  tai = cal_lt_to_tai_interp( lt, zone );
  dt = cal_ed_minus( tai, lt ) * DL_SEC_D;
 }
 else
 {
   info = cal_lt_leap( 1, lt, zone );
   dt = info.tai_minus_utc - info.adjust - zone;
 } 
 return dt;
}


double cal_lt_minus_tai( edouble tai, integer zone )
{
 integer day;
 double sec;
 LeapInfo info;
 double dt;
 edouble utc;
 cal_jde_to_daysec( tai, &day, &sec );

 if ( day < UTC_START_JD )
  dt = zone;
 else if ( ( day < UTC_LEAP_START )
         || ( day == UTC_LEAP_START && sec < 10.0 ) )
 {
  utc = cal_tai_to_lt_interp( tai, zone );
  dt = cal_ed_minus( utc, tai ) * DL_SEC_D;
 }
 else 
 {
  info = cal_lt_leap( 0, tai, zone );
  dt = - (info.tai_minus_utc - info.adjust - zone);
 }
 return dt;
}


edouble cal_tai_to_lt( edouble tai, integer zone )
{
 edouble lt;
 integer day;
 double sec;
 double dt;
 cal_jde_to_daysec( tai, &day, &sec );
 if ( day < UTC_START_JD )
 {
  dt = zone;
  lt = cal_ed_add( tai, dt / DL_SEC_D );
 } 
 else if ( ( day < UTC_LEAP_START )
         || ( day == UTC_LEAP_START && sec < 10.0 ) )
 {
  lt = cal_tai_to_lt_interp( tai, zone );
 }
 else
 {
  lt = cal_tai_to_lt_leap( tai, zone );
 }
 return lt;
}

edouble cal_lt_to_tai( edouble lt, integer zone )
{
 edouble tai;
 integer day;
 double sec;
 double dt;
 cal_jde_to_daysec( lt, &day, &sec );
 if ( day < UTC_START_JD )
 {
  dt = -zone;
  tai = cal_ed_add( lt, dt / DL_SEC_D );
 } 
 else if ( day < UTC_LEAP_START )
  tai = cal_lt_to_tai_interp( lt, zone );
 else
  tai = cal_lt_to_tai_leap( lt, zone );

 return tai;

}
