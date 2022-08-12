/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

/* Local routines */
double julian_os_newyear( integer y );
integer cal_os_ydaynum( integer y );
void cal_os_daymon( integer y, integer doy, integer* m, integer* d );

#define CAL_OS_Y1600 1600
#define CAL_OS_JD1600 2305457

edouble cal_os_to_jde( integer y, integer m, integer d, double sec )
{
 integer doy;
 double t;
 double dt;
 doy = cal_os_daynum( y, m, d );
 t = julian_os_newyear( y ) + doy - CAL_NOON;
 dt  = sec / DL_SEC_D;
 return cal_ed_make( t, dt );
}

/*
 *  Julian LY function
 */ 
integer cal_os_lyr( integer y )
{
 integer lyr;
 lyr =  ( y % 4 == 0 ) ? 1 : 0;
 return lyr;
}

integer cal_os_daynum( integer y, integer m, integer d )
{
 integer leap;
 integer doy;
 integer offset = cal_month_offset( m );
 if ( m > 2 )
  leap = cal_os_lyr( y );
 else
  leap = 0;
 doy = d + leap + offset;
 return doy;
}

double julian_os_newyear( integer y )
{
 double t;
 if ( y >= CAL_OS_Y1600 )
 {
    t = CAL_OS_JD1600 + cal_os_ydaynum( y - CAL_OS_Y1600 );
 } else {
    t = CAL_OS_JD1600 - cal_os_ydaynum( CAL_OS_Y1600 + 1 - y ) + 366;
 }
 return t;
}


integer cal_os_ydaynum( integer y )
{
 if ( y > 0 )
  return ( 365* y + (y-1)/4 + 1 );
 else
  return ( 365 * y + y/4 );
}



void cal_os_daymon( integer y, integer doy, integer* m, integer* d )
{
 integer j;
 long i;
 j = doy;
 if ( j > 59 && y%4 == 0 )
 {
  j--;
  if ( j == 59 )
  {
   *m = 2;
   *d = 29;
   return;
  }
 } else if ( j <= 0 ) {
  *m = 0;
  *d = 0;
  return;
 }
 i = 12;
 while( i > 1 && j <= cal_month_offset( i ) )
  i--;
 *m = i;
 *d = j - cal_month_offset(i);
}



void cal_jde_to_os( edouble et, integer* y, integer* m, integer* d, double* sec )
{
 integer doy;
 cal_jde_to_os_doy( et, y, &doy, sec );
 cal_os_daymon( *y, doy, m, d );
}

void cal_jde_to_os_doy( edouble et, integer* y, integer* doy, double* sec )
{
 integer day;
 edouble et1;
 double fs;
 double dfs;
 integer dday;
 integer n;
 et1 = cal_ed_resolve( et );
 frac_d( et1.t-CAL_OS_JD1600+0.5, &day, &fs );
 frac_d( et1.dt, &dday, &dfs );
 day += dday;
 fs  += dfs;
 if ( 1.0 - fs < 6.0e-8 )
 {
  fs = 0.0;
  day++;
 }
 n = (integer) ( ((double)day)/365.25 );
 while( cal_os_ydaynum( n ) >= day ) n--;
 while( cal_os_ydaynum( n+1 ) < day ) n++;
 day -= cal_os_ydaynum( n );
 *y = n + CAL_OS_Y1600;
 *doy = day;
 *sec = fs * DL_SEC_D;
}
