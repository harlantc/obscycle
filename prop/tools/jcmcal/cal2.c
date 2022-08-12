/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
/*
 *  This file deals with the CalDate object
 *
 */

CalDate cal_jde_to_ymd( edouble et )
{
 CalDate date;

 integer month,day;
 
 date = cal_jde_to_doy( et );
 cal_daymon( (integer)date.year, date.day, &month, &day );
 date.month = month;
 date.day = day;
 return date;
}

void cal_ymd_to_greg( CalDate u, integer *y, integer* m, integer* d, double* sec )
{
 *y = u.year;
 *m = u.month;
 *d = u.day;
 *sec = u.sec;
}

void cal_doy_to_ymd( CalDate* u )
{
 integer m, d;
 cal_daymon( (integer)u->year, (integer)u->day, &m, &d );
 u->month = m;
 u->day = d;
}

CalDate cal_ymd_create( integer y, integer m, integer d, double sec )
{
 CalDate date;
 date.year  = y;
 date.month = m;
 date.day   = d;
 date.sec   = sec;
 return date;
}


edouble cal_ymd_to_jde( CalDate ymd )
{
 integer doy;
 integer start;
 edouble jd;

 jd.dt = ymd.sec / DL_SEC_D;
 start = cal_jd_yr( (integer)ymd.year );
 doy = cal_daynum( (integer)ymd.year, (integer)ymd.month, (integer)ymd.day );
 jd.t = start + doy - CAL_NOON;
 return( jd );
}




