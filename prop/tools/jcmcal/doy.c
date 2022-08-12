/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
#include <limits.h>

/*
 *   Checks for unnormalized et
 */
CalDate cal_jde_to_doy( edouble et )
{
 CalDate date;
 double dt;
 integer days;
 double dsec;
 integer y,d;
 double t;
 double tdsec,tdsec0;
 double ddays;
 double rdays;
 double edays;
 double yn;
 integer m = 0;

 if ( et.dt > CAL_LARGE_DT )
 {
/* Handle case where JD does not fit in integer */
  t = et.t + et.dt;
  ddays = t - JD_C2000;
  yn = ddays/YL_GREG;
  if ( yn > LONG_MAX || yn < -LONG_MAX )
  {
/* ERR */  
   sprintf( cal_global_state->errbuf,  "Error: year out of range %f\n", yn );   
  }

  while( cal_greg_offset_d(yn) >= ddays  && m < 10)
 {

   yn = yn - 1.0;
 }
  while( cal_greg_offset_d(yn+1) < ddays )
   yn = yn + 1.0;
  edays = cal_greg_offset_d( yn );
  rdays = ddays -  edays;
  y = yn + IY_J2000;
  frac_d( rdays, &d, &date.sec );  
 } else {
  dt = (et.t - CAL_NOON);
  frac_d( dt, &days, &dsec );
/* 
   We don't fractionate et.dt, since it may be 1 + epsilon on a leap day.
   This requires that the input date be correctly normalized.
 */
  tdsec0 = (dsec+et.dt);
  tdsec = tdsec0* DL_SEC;
  date.sec = tdsec;
  cal_jd_to_doy_i( days, &y, &d );
 }
#if 0
printf( "DOY %ld %ld DT %f DSEC+ET.DT= (%20.12f+%20.12f) D.S=%20.12f TD=%20.12f %20.12f DL_SEC=%d\n", y, d, dt, dsec, et.dt, date.sec, tdsec, tdsec0, DL_SEC );
#endif
 date.year  = y;
 date.month = 0;
 date.day   = d;
 return date;
}

/*
 *  Calculate the day of year by calculating the first day of the year
 */
void cal_jd_to_doy_i( integer jd, integer* year, integer* doy )
{
 integer days;
 integer n;

/* Correct to Jan 0.0  which is 1 less than Jan 0.5 */
 days = jd - IJD_J2000 + 1;

/* Make rough guess at year no and then iterate */

 n = ((double)days)/YL_GREG;
 while( cal_greg_offset(n) >= days )
  n--;
 
 while( cal_greg_offset(n+1) < days )
  n++;

 *doy = days - cal_greg_offset( n );
 *year = n + IY_J2000;
 
}



