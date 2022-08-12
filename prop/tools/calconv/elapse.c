/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

/* Days elapsed since tzero */
double cal_cs_elapse( GenCal* date )
{
  double mjd;
  mjd = ( date->data[JD_SIGN] * ( date->data[JD_DAY] + date->et.t ) )
         + ( date->data[JD_SIGN] * date->et.dt  );

  return mjd;
}

/* Is there a GREG_YR field? */
logical cal_cts_has_years( CalendarSystem* cts )
{
 Calendar* cal = NULL;
 logical ok = UT_FALSE;
 if ( cts )
  cal  = cts->cal;
 if ( cal )
  ok =  cal->annual;
 return ok;
}


logical cal_cts_leap( CalendarSystem* cts )
{
 logical ok = UT_FALSE;
 if ( cts )
  ok = cal_ts_leap( cts->ts );
 return ok;
}

/* Does timescale have leap years? */
logical cal_ts_leap( Timescale* ts )
{
 logical ok = UT_FALSE;
 if ( ts )
  ok = ( ts->type == UTC_ );
 return ok;
}



edouble cal_ts_jde_add( Timescale* ts, edouble et1, double dt )
{
 edouble et2;
 edouble tt;
 edouble tt2;

 if ( ts->type == UTC_ )
 {
  tt = cal_ts_converter( et1, ts->type, TT_, ts->zone, 0 );
  tt2 = cal_ed_add( tt, dt );
  et2 = cal_ts_converter( tt2, TT_, ts->type, 0, ts->zone );
 } else {
  et2 = cal_ed_add( et1, dt );
 }
 return et2;
}
