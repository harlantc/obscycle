/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
/*
 *  Calculate the day of the week.
 *  Algorithm: simple modulo 7 from J2000
 */
/*----------------------------------------------------------------------*/
/*
 * cal_weekday_name
 *
 * Uses csproto.h
 *
 */
static char *daynames[] = { " ",
 "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" } ;



void cal_weekday_name( integer weekday_no, char* buf )
{
 if ( weekday_no < 0 || weekday_no > WK_DAYS ) weekday_no = 0;
 utn_cs_copy( daynames[weekday_no], buf );
}


integer cal_weekday_no( double t )
{
 return cal_jde_weekday_no( cal_ed_make( t, 0.0 ));
}

integer cal_weekday_parse( char* buf )
{
 TextCard token;
 integer wno;
 integer wlen;

 utn_cs_copy( buf, token );
 utn_cs_mixcase( token );
 wlen = utn_cs_ends(token);
 for (  wno = 1; wno <= WK_DAYS; wno++ )
 {
   if ( utn_cs_eq( token, daynames[wno] ) 
   ||  ( wlen == 3 && utn_cs_eq_siz( token, daynames[wno], 3 ) ))
    return wno;
 }
 return 0;
}



integer cal_jde_weekday_no( edouble et )
{
 integer Weekday_No;
 double utd;
 integer fdays;
 double futd;
 integer days;

/* In the current implementation we ignore the
 * fractional part of the day. This is because it may be
 * greater than one in the case of a leap second day.
 */
 integer mode = 0; 

 frac_d( et.t + CAL_NOON - JD_J2000, &days, &utd );
 if ( mode )
 {
  frac_d( et.dt , &fdays, &futd ); 
  days += fdays;
 }
 Weekday_No = ( days + WKDAY_J2000 ) % WK_DAYS;
 if ( Weekday_No <= 0 ) Weekday_No += WK_DAYS;
 return Weekday_No;
}


