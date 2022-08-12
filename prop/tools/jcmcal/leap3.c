/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011-2012,2015,2016)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*1/2012 - bug 13117: leap second */
/*5/2012 - bug 13244: leap second */ 
/*1/2015 - bug 14060: leap second */ 
/*10/2016 - bug 14366: leap second */ 

#include "jcmcal.h"
#include <limits.h>
#define NLEAP 27
/* Number of leap seconds at step zero */
#define LEAP_OFFSET_ZERO 10

static integer jdleap[] = { 
        2441499, 2441683, 2442048, 2442413, 2442778,
        2443144, 2443509, 2443874, 2444239, 2444786, 2445151,
        2445516, 2446247, 2447161, 2447892, 2448257, 2448804,
        2449169, 2449534, 2450083, 2450630, 2451179, 2453736,
        2454832, 2456109, 2457204, 2457754
};

/*
 *     val        old leap_add  old leap adj
 *     -1          0            0    No leap second or before leap second
 *      0          1            1    This is the leap second
 *      1          0            1    This is after the leap second
 */
integer cal_lt_find_leap( edouble et, integer zone )
{
 integer sec;
 integer zone_offset;
 integer leap_entry = -1;
 integer leap_time;
 integer leap_add = -1;
 integer entry;
 integer day; 
 double dsec;

 if ( et.t > LONG_MAX || et.dt > CAL_LARGE_DT )
 {
  return 0;
 }
 cal_jde_to_daysec( et, &day, &dsec );
 sec = (integer)dsec;


 if ( zone > 0 )
 {
  leap_time = zone;
  zone_offset = 0;
 } else {
  leap_time = zone + DL_SEC;
  zone_offset = -1;
 }

 for ( entry = 1; entry <= NLEAP; entry++ )
 {
  if( day == jdleap[ entry-1 ] + zone_offset ) 
    leap_entry = entry;   
 }
 if ( leap_entry > 0 ) 
 {

   if ( sec > leap_time )
     leap_add = 1;
   else if ( sec == leap_time ) 
     leap_add = 0;

 }
 return leap_add;
}


edouble cal_lt_to_tai_leap( edouble utc, integer zone )
{
 edouble tai;
 LeapInfo info;
 double dt;
 int utc_flag = 1;
 info =  cal_lt_leap( utc_flag, utc, zone );
 dt = info.tai_minus_utc - info.add - zone;
 tai = cal_ed_add( utc, dt / DL_SEC_D );
#if 0
printf( "TAI %f %20.12f UTC %f %20.12f DT %f ADD %d ZONE %d\n", tai.t, tai.dt, utc.t, utc.dt, dt, info.add, zone );
#endif
 return tai;
}


/*
 *  Routine for case of post 1972 dates. The number of seconds
 *  depends on whether T lies in the UTCZ leap day.
 */
edouble cal_tai_to_lt_leap( edouble tai, integer zone )
{
 edouble lt;
 double dt;
 int utc_flag = 0;
 LeapInfo info;
 integer day;
 double sec;
 info = cal_lt_leap( utc_flag, tai, zone );
 dt = -info.tai_minus_utc +info.adjust + zone;
 lt = cal_ed_add( tai, dt / DL_SEC_D );
 cal_jde_to_daysec( lt, &day, &sec );
 if ( day > info.day ) info.add = 0;
 lt.dt += info.add / DL_SEC_D;
#if 0
printf( "TAI %f %20.12f UTC %f %20.12f DT %f ADD %d DS = %ld %f\n", tai.t, tai.dt*DL_SEC_D, lt.t, lt.dt*DL_SEC_D, dt, info.add, day, sec );
#endif
 return lt;

}

double cal_lt_hms_to_sec( integer day, Sexagesimal* time, integer zone )
{
 double lts;
/*
! Convert hours minutes seconds local time into seconds of day local time.
! Result is in the range [0.0,86401.0).
! If this day has 86401 seconds, and we are past the leap time,
! add a second. ISEC initally has the same value for the leap second and
! the second following it. The leap second has s=60 which is how we spot it.
!
 */
 integer isec;
 integer adj;
 edouble et;
 double t;
 double dt;

 isec = cal_hms_to_sec_i( (integer)time->hour, (integer)time->min, (integer)time->sec );
 t = day + 0.5;
 dt = isec / DL_SEC_D;
 et = cal_ed_make( t, dt );  /* The et value for 17:59:60 and 18:00:00 are the same */
 adj = cal_lt_find_leap( et, zone );
/*
 *                        Non leap day   Leap day
 *  23:59:59   86399       0 0           0 0
 *  23:59:60   86400       0 0           0 1
 *  00:00:00   0           0 0
 *  00:00:01   1           0 0
 *
 *  17:59:59   64799 0 0     64799 0 0   0
 *  17:59:60                 64800 1 1   0
 *  18:00:00   64800 0 0     64801 1 1.0 1
 *  18:00:01   64801 0 0     64802 1 0   1 
 *
 */

 /* disinguish between :60 and :00 */
 if ( adj == 0 && time->sec != 60 ) adj = 1;
 if ( adj < 0 ) adj = 0;
 lts = isec + adj + time->frac;
 return lts;
}


Sexagesimal cal_lt_sec_to_hms( edouble et, integer zone )
{
 Sexagesimal time;
 integer adj;
 integer day;
 double sec;

 if ( et.dt > CAL_LARGE_DT )
 {
/* Reduce dt until it can fit in an integer, then take fractional part */
  double x;
  integer n;
  double step = 1.0E8;
  n = floor_d( et.dt / step );
  x = et.dt - n * step;
  frac_d( x, &day, &sec );
  sec *= DL_SEC_D;
 } else {
  cal_jde_to_daysec( et, &day, &sec );
 }
 adj = cal_lt_find_leap( et, zone );
 if ( adj >= 0 ) sec -= 1.0;
 sxg_in_d( sec, &time );
 if ( adj == 0 ) time.sec += 1;

 return time;
}

/* Get leap info for given direction (to or from UTC) */
LeapInfo cal_lt_leap( logical utc, edouble et, integer zone )
{
 LeapInfo info;
 integer leap_offset;
 integer step_day;
 double offset;
 integer leap_entry;
 integer entry;
 integer day;
 double sec;

 cal_jde_to_daysec( et, &day, &sec );
 leap_offset =  ( zone > 0 ) ? 0 : -1;
 leap_entry = 0;
 for ( entry = 1; entry <= NLEAP; entry++ )
 {
  if( day + 1 >= jdleap[entry-1] )
   leap_entry = entry;
 }
 step_day = jdleap[leap_entry-1];
 info.day = step_day + leap_offset;
 info.tai_minus_utc = leap_entry + LEAP_OFFSET_ZERO;
/*
! Is TAI before or after the leap second?
! If before, we have one fewer leap second.
! However, to get the rollover correct we need to fudge the
! leap second itself, hence the +1 in offset.
 */
 offset = ( day - step_day ) * DL_SEC + sec + 1;
 if ( utc ) 
 {
  info.add = ( day > info.day ) ? 0 : 1;
  info.adjust = 0;
 } else {
  offset -= info.tai_minus_utc;
  info.add    = ( offset < 0.0 ) ? 0 : 1;
  info.adjust = ( offset < 0.0 ) ? 1 : 0;
 }
#if 0
printf( "DAY = %ld SDAY = %ld IDAY = %ld SEC = %f OFFSET = %f MINUS= %f ADD = %ld ADJUST = %ld UTC=%d\n", day, step_day, info.day, sec, offset, 
   info.tai_minus_utc, info.add, info.adjust, utc );
#endif
 return info;
}



edouble cal_lt_convert( edouble et, integer zone1, integer zone2 )
{
 edouble et2;
 integer adj;
 double dt;
 double dt_leap;
 integer iadj1;
 integer iadj2;

 dt = zone2 - zone1;
 et2 = cal_ed_add( et, dt / DL_SEC_D );
/*
! What if leap second?
! The worst case is if one day is a leap day and the other is not.
! Example:
! Leap sec at 1993 Jun 30 18:59:60 EST = 1993 Jul 1 02:59:60 DMV
! Then 1993 Jun 30 06:00 EST is in a leap day but is also
!  1993 Jun 30 11:00 UTC =  1993 Jun 30 14:00 DMV which is not.
! Both these are before their respective leap seconds so no adjustment needed
! Case where time is after a leap second:
!  1993 Jun 30 23:30 EST = 1993 Jul 1 0730 DMV, both after leap second
!  1993 Jul  1 00:30 EST = 1993 Jul 1 0530 UTC = 1993 Jul 1 0830 DMV, first one
! is not in a leap day.  DT = DT + 1
!
*/

 adj  = cal_lt_find_leap( et, zone1 );
 iadj1= ( adj >= 0 ) ? 1 : 0;
 adj  = cal_lt_find_leap( et2, zone2 );
 iadj2= ( adj >= 0 ) ? 1 : 0;
 dt_leap = ( iadj2 - iadj1 ) / DL_SEC_D;
 if ( et2.dt >= 0 && dt_leap < 0.0 )
 {
  et2.t--;
  et2.dt++;
 } else {
  et2.dt += dt_leap;
 }
 return et2;
}
