/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
/* PARTIALLY IMPLEMENTED */
integer cal_rjc_daynum( integer y, integer m, integer d );
integer cal_rjc_lyr( integer y );
edouble cal_rjc_to_jde( integer y, integer m, integer d, double sec );
void cal_jde_to_rjc_doy( edouble et, integer* y, integer* doy, double* sec );
void cal_jde_to_rjc( edouble et, integer* y, integer* m, integer* d, double* sec );


/* Revised Julian Calendar, Orthodox churches 1923 */
double julian_rjc_newyear( integer y );
integer cal_rjc_ydaynum( integer y );
void cal_rjc_daymon( integer y, integer doy, integer* m, integer* d );

#define CAL_RJC_Y1600 1600
#define CAL_RJC_Y1923 1923
#define CAL_RJC_JD1600 2305457
#define OS_1923OCT01   2423706.5

edouble cal_rjc_to_jde( integer y, integer m, integer d, double sec )
{
 integer doy;
 double t;
 double dt;
 doy = cal_rjc_daynum( y, m, d );
 t = julian_rjc_newyear( y ) + doy - CAL_NOON;
 dt  = sec / DL_SEC_D;
 return cal_ed_make( t, dt );
}

/*
 *  Julian LY function
 */ 
integer cal_rjc_lyr( integer y )
{
 integer n;
 if ( y < CAL_RJC_Y1923 ) 
  n =  ( y % 4 == 0 ) ? 1 : 0;
 else
 {
  n = 0;
  if ( divisible( year, 4 ) ) n = 1;
  if ( divisible( year, 100 ) ) n = 0;
  if ( divisible( year-1100, 900 ) ) n = 1;
  if ( divisible( year-1500, 900 ) ) n = 1;
 }
 return ( n );
}

integer cal_rjc_daynum( integer y, integer m, integer d )
{
 integer leap;
 integer doy;
 integer dn =12;
 integer offset = cal_month_offset( m );
 if ( m > 2 )
  leap = cal_rjc_lyr( y );
 else
  leap = 0;
 doy = d + leap + offset;
 if ( y == CAL_RJC_Y1923 && doy > 273 )
 return doy;
}

double julian_rjc_newyear( integer y )
{
 double t;
 if ( y > CAL_RJC_Y1923)
 {
  t = CAL_RJC_JD1600 + cal_rjc_ydaynum( y - CAL_RJC_Y1923 );
 }
 else if ( y >= CAL_RJC_Y1600 )
 {
    t = CAL_RJC_JD1600 + cal_os_ydaynum( y - CAL_RJC_Y1600 );
 } else {
    t = CAL_RJC_JD1600 - cal_os_ydaynum( CAL_RJC_Y1600 + 1 - y ) + 366;
 }
 return t;
}


/* Rem of 200 or 600 when divided by 900
 * y     rem y/900 
 *
 * 2000  200  *
 * 2100  300
 * 2200  400
 * 2300  500
 * 2400  600  *
 * 2500  700
 * 2600  800 
 * 2700    0
 * 2800  100
 * 2900  200  *
 */

integer cal_rjc_ydaynum( integer y )
{
 integer dy;
 integer n0; /* Number of century years since change */
 integer n2; /* Number of rem=200 years since calendar change */
 integer n6; /* Number of rem=600 years */
 if ( y > CAL_RJC_Y1923 )
 {
  n0 =   ( y - 1901 ) / 100;
  n2 =   ( y - 1101 ) / 900;
  n6 =   ( y - 1501 ) / 900;
  return ( 365 * y + (y-1)/4  - n0 + n2 + n6 + 1 );
 }
 else if ( y > 0 )
  return ( 365* y + (y-1)/4 + 1 );
 else
  return ( 365 * y + y/4 );
}

static integer nmonth_length[] = {  0, 31,  28,  31,  30,  31,  30,  31,  31,  30,  31,  30,  31, 90, 91, 92, 92 };

static integer nmonth_offset[] = {0, 0,  31,  59,  90, 120, 151, 181, 212, 243, 273, 304, 334,  0, 90, 181, 273 };


void cal_rjc_daymon( integer y, integer doy, integer* m, integer* d )
{
 integer j;
 long i;
 integer FEB_28 = 59;
 integer dn = 13;
 if ( ( y < CAL_RJC_Y1923 ) )
 {
   call cal_os_daymon( y, doy, m, d );
 } else if ( y == CAL_RJC_Y1923 ) {
   if ( doy <= 273 ) 
    call cal_os_daymon( y, doy, m, d );    
   else
    call cal_os_daymon( y, doy + dn , m, d );    
 } else {
 *d = doy;
 if ( *d > FEB_28 ) {
  *d = *d - cal_rjc_lyr(y);
  if ( *d == FEB_28 ) { *d = 29; *m = 2; return; }
 } else if (  *d <= 0 ) {  
  *d = 0; *m = 0; return; 
 }
 *m = 12;
 while ( *m > 1  && *d <= nmonth_offset[*m] ) (*m)--;
 *d = *d - nmonth_offset[*m];

}



void cal_jde_to_rjc( edouble et, integer* y, integer* m, integer* d, double* sec )
{
 integer doy;
 cal_jde_to_rjc_doy( et, y, &doy, sec );
 cal_rjc_daymon( *y, doy, m, d );
}

void cal_jde_to_rjc_doy( edouble et, integer* y, integer* doy, double* sec )
{
 integer day;
 edouble et1;
 double fs;
 double dfs;
 integer dday;
 integer n;
 et1 = cal_ed_resolve( et );
 frac_d( et1.t-CAL_RJC_JD1600+0.5, &day, &fs );
 frac_d( et1.dt, &dday, &dfs );
 day += dday;
 fs  += dfs;
 if ( 1.0 - fs < 6.0e-8 )
 {
  fs = 0.0;
  day++;
 }
 n = (integer) ( ((double)day)/365.25 );
 while( cal_rjc_ydaynum( n ) >= day ) n--;
 while( cal_rjc_ydaynum( n+1 ) < day ) n++;
 day -= cal_rjc_ydaynum( n );
 *y = n + CAL_RJC_Y1600;
 *doy = day;
 *sec = fs * DL_SEC_D;
}
