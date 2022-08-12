/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"



void cal_cs_eval_jde( edouble et, GenCal* date )
{
 Calendar* cal= NULL;
 CalendarSystem* sys = NULL;
 if ( date )
  sys = date->sys;
 if ( sys )
  cal = sys->cal;
 if ( cal )
  cal->eval( et, date );
}

void cal_cs_eval_thermidor( edouble et, GenCal* date )
{
   cal_jde_to_thermidor( et, &date->data[GC_YR], &date->data[GC_MON], &date->data[GC_DAY] );
}

void cal_cs_eval_maya( edouble et, GenCal* date )
{
 double t = et.t+et.dt;
 maya_msg( t, 6 );  /* Temp test */
 date->data[GC_MAYA_DEG] = 6;
 jd_to_maya_long( t, &date->data[GC_MAYA_LC], date->data[GC_MAYA_DEG] );
 maya_jd_to_tzolkin( t, &date->data[GC_MAYA_TZOL], &date->data[GC_MAYA_TZOL+1] );
 maya_jd_to_haab( t, &date->data[GC_MAYA_HAAB], &date->data[GC_MAYA_HAAB+1] );
}

void cal_cs_eval_fail( edouble et, GenCal* date )
{
    printf( "Unknown calendar type in eval_jde\n" );
}

void cal_cs_eval_elapse( edouble et, GenCal* date )
{
 edouble tz;
 edouble tz_tt;
 edouble et_tt;
 Timescale* ts;
 edouble et2;
 double scale = 1.0;   /* ELAPSE units are seconds */
 double epsilon = 1.0E-5; 
   tz = cal_tzero( date->sys );
   ts = cal_cs_get_ts( date );   
   if ( ts->type == UTC_ )
   {
    tz_tt = cal_ts_converter( tz, ts->type, TT_, ts->zone, 0 );
    et_tt = cal_ts_converter( et, ts->type, TT_, ts->zone, 0 );
    et2.t =  ( et_tt.t  - tz_tt.t ) * DL_SEC_D;       /* ELAPSE units are seconds */
    et2.dt = ( et_tt.dt  - tz_tt.dt ) * DL_SEC_D;     /* ELAPSE units are seconds */
#if 0
printf( "TZ   T = %20.5f DT = %20.5f\n", tz.t, tz.dt );
printf( "ET   T = %20.5f DT = %20.5f\n", et.t, et.dt );
printf( "TZTT T = %20.5f DT = %20.5f TOT(s) = %20.5f\n", tz_tt.t, tz_tt.dt, (tz_tt.t + tz_tt.dt)*DL_SEC_D );
printf( "ETTT T = %20.5f DT = %20.5f\n", et_tt.t, et_tt.dt );
printf( "ET2  T = %20.5f DT = %20.5f TOT(s) = %20.5f\n", et2.t, et2.dt, et2.t + et2.dt );
#endif
   } else {
#if 0
printf( "TZ   T = %20.5f DT = %20.5f\n", tz.t, tz.dt );
printf( "ET   T = %20.5f DT = %20.5f\n", et.t, et.dt );
#endif
    et2.t =  ( et.t  - tz.t ) * DL_SEC_D;       /* ELAPSE units are seconds */
    et2.dt = ( et.dt  - tz.dt ) * DL_SEC_D;       /* ELAPSE units are seconds */
   }
   date->et = cal_ed_resolve_scale( et2, scale, epsilon, 0.0 );
}



void cal_cs_eval_epoch( edouble et, GenCal* date )
{
 char c;
 double epoch;
 double t;
 Calendar* cal;

 cal = date->sys->cal;
 t  = et.t + et.dt;
 if ( utn_cs_eq( cal->name, "BEPOCH" )) 
 {
  c = 'B';
  epoch = cal_jd_to_bepoch( t );
 }
 else 
 {
  c = 'J';
  epoch = cal_jd_to_jepoch( t );
 }
 date->data[GC_EPT] = c;
 date->et.t = epoch;
 date->et.dt = 0.0;
}


void cal_cs_eval_auc( edouble et, GenCal* date )
{
 double t = cal_ed_double( et );
 cal_auc_calc( t, &date->data[GC_YR], &date->data[GC_MON], &date->data[RC_SECT],
     &date->data[RC_DAY] );
}

void cal_cs_eval_jd( edouble et, GenCal* date )
{
 integer day;
 integer sig = 1;
 double dt;
 edouble et2;
 edouble tz;
 edouble tmp = { 0.0, 0.0 };

 tz = cal_tzero( date->sys );
 et2.t = et.t - tz.t;
 et2.dt = et.dt - tz.dt;
 tmp = cal_ed_norm( et2 );
 day = tmp.t;
 dt = tmp.dt;

 if ( day < 0 )
 {
  sig = -1;
  day = -day-1;
  dt = 1.0-dt;
 } 

 date->data[JD_SIGN] = sig;
 date->data[JD_DAY] = day;
 date->et.dt = dt;
 if ( gcal_verbose )
  printf( "cal_cs_eval_jd T = %ld %f  ET2= %f %f TZ=%f %f\n", day, dt, et2.t, et2.dt, tz.t, tz.dt );
}


void cal_cs_eval_greg( edouble et, GenCal* date )
{
 Sexagesimal ts;
 Calendar* cal;
 double sec; 
 double fs;
 integer zone = 0;
 cal = date->sys->cal;
 if ( utn_cs_eq( cal->name, "DOY" ))
 {
  CalDate doydate;
  doydate = cal_jde_to_doy( et );
  date->data[GC_YR] = doydate.year;
  date->data[GC_DAY]= doydate.day;
  date->et.dt = doydate.sec/DL_SEC_D;
 } else if ( utn_cs_eq( cal->name, "OS" )) {
  cal_jde_to_os( et, &date->data[GC_YR], &date->data[GC_MON], &date->data[GC_DAY], &sec );
  cal_sec_to_hms_d( sec, &date->data[GC_HR], &date->data[GC_MIN], &date->data[GC_SEC], &fs );
  date->et.t = 0.0;
  date->et.dt = fs / DL_SEC_D;
 } else {
  CalDate d;

if ( gcal_verbose )
printf( "DAY ET = %20.12f %20.12f\n", et.t, et.dt );

  d = cal_jde_to_ymd( et );
  date->data[GC_YR] = d.year;
  date->data[GC_MON] = d.month;
  date->data[GC_DAY] = d.day;
  ts = cal_lt_sec_to_hms( et, zone );
  date->data[GC_HR] = ts.hour;
  date->data[GC_MIN] = ts.min;
  date->data[GC_SEC] = ts.sec;
  date->et.t = 0.0;
  date->et.dt = ts.frac / DL_SEC_D;
 }
 date->data[GC_DOW] = cal_jde_weekday_no( et );
 date->precision = TPREC_UNKNOWN;
}
