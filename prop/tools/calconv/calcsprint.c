/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

void cal_cs_print_thermidor( GenCal* date, char* buf, integer maxlen )
{
 TextCard buf1;
 cal_thermidor_format( date->data[GC_YR], date->data[GC_MON], date->data[GC_DAY], buf1, UT_CARD_SIZE );
 utn_cs_copy_siz( buf1, buf, maxlen );
}

void cal_cs_print_auc( GenCal* date, char* buf, integer maxlen )
{
  cal_auc_format( date->data[GC_YR], date->data[GC_MON], date->data[RC_SECT], date->data[RC_DAY], buf, maxlen );
}

void cal_cs_print_maya( GenCal* date, char* buf, integer maxlen )
{
 integer* d = date->data;
 maya_full_fmt( &d[GC_MAYA_LC], d[GC_MAYA_DEG],
   d[GC_MAYA_TZOL], d[GC_MAYA_TZOL+1], d[GC_MAYA_HAAB], d[GC_MAYA_HAAB+1], buf, maxlen );
}

void cal_cs_print_aztec( GenCal* date, char* buf, integer maxlen )
{
 integer* d = date->data;
 aztec_full_fmt( &d[GC_MAYA_LC], d[GC_MAYA_DEG],
   d[GC_MAYA_TZOL], d[GC_MAYA_TZOL+1], d[GC_MAYA_HAAB], d[GC_MAYA_HAAB+1], buf, maxlen );
}

void cal_cs_print_pack( GenCal* date, char* buf, integer maxlen )
{
  double g;
  integer y;
  double utd;
  TextWord buf1;

  utd = cal_cs_utd( date );
  g = cal_greg_pack( date->data[GC_YR], date->data[GC_MON], date->data[GC_DAY], utd );

/* Old style Y2K packing */
  sprintf( buf1, "%18.8f", g );
  y = date->data[GC_YR];
  if( y > 1940 && y <= 2040 )
  {
    buf1[1] = ' ';
    buf1[2] = ' ';
  }
 

  utn_cs_copy_siz( buf1, buf, maxlen );
}


void cal_cs_print_gst( GenCal* date, char* buf, integer maxlen )
{
  Sexagesimal utime;
  char c;
  integer uday;
  double usec;
 TextCard buf1;
 TextCard date2;
  cal_jde_to_daysec( date->et, &uday, &usec );
  sxg_in_d( usec, &utime );
  sxg_fmt( &utime, TPREC_UNDEF, SXG_MODE_STD, date2, 12 );
  if ( date->data[JD_SIGN] == -1 )
   c = '-';
  else
   c = ' ';
  sprintf( buf1, "GSD %c%ld %s", c, date->data[JD_DAY], date2 );
 utn_cs_copy_siz( buf1, buf, maxlen );
}


void cal_cs_print_gsd( GenCal* date, char* buf, integer maxlen )
{
 TextCard date1;
 TextCard date2;
  sprintf( date2, "%12.10f", date->et.dt );
  if ( date->data[JD_SIGN] == -1 )
   sprintf( date1, "GSD -%ld%s", date->data[JD_DAY], date2+1 );
  else
   sprintf( date1, "GSD %ld%s", date->data[JD_DAY], date2+1 );
 utn_cs_copy_siz( date1, buf, maxlen );
}

void cal_cs_print_epoch( GenCal* date, char* buf, integer maxlen )
{
 TextCard buf1;
 char c = 'J';
 double year = date->et.t;

 c = (char)(date->data[GC_EPT]);
 if ( year > 0 && year <= 9999 ) 
  sprintf( buf1, "%c%7.2f", c, year );
 else
  sprintf( buf1, "%c%g", c, year );

 utn_cs_copy_siz( buf1, buf, maxlen );
}

void cal_cs_print_elapse( GenCal* date, char* buf, integer maxlen )
{
 TextCard buf1;
  sprintf( buf1, "%20.8f", date->et.t + date->et.dt );
  utn_cs_numberclean( buf1, 0, buf, maxlen );
}

void cal_cs_print_days( GenCal* date, char* buf, integer maxlen )
{
 double   dt = cal_cs_elapse( date );
 cal_fmt_days( dt, buf, maxlen );
}

void cal_cs_print_jd( GenCal* date, char* buf, integer siz )
{
  Timescale* ts = cal_cs_get_ts( date );
  cal_fmt_jd( date->data[JD_SIGN], date->data[JD_DAY], date->et.dt, ts->name, buf, siz );
}

void cal_cs_print_mjd( GenCal* date, char* buf, integer maxlen  )
{
  Timescale* ts = cal_cs_get_ts( date );
  cal_fmt_mjd( date->data[JD_SIGN], date->data[JD_DAY], date->et.dt, ts->name, buf, maxlen );
}
void cal_cs_print_doy( GenCal* date, char* buf, integer maxlen )
{
  double g;
  double utd; 
 TextWord buf1;
  utd = cal_cs_utd( date );
  g = cal_doy_pack( date->data[GC_YR], date->data[GC_DAY], utd );
  sprintf( buf1, "%18.8f", g );
  utn_cs_copy_siz( buf1, buf, maxlen );
}

void cal_cs_print( GenCal* date, char* buf )
{
 CalendarSystem* sys = NULL;
 Calendar* cal = NULL;
 
 logical maxlen = TEXT_SIZE;
 if ( date )
  sys = date->sys;
 if ( sys )
  cal = sys->cal;
 if ( cal )
  cal->print( date, buf, maxlen ); 
}




void cal_cs_print_greg( GenCal* gcal, char* buf, long maxlen )
{
 logical use_hms = 1;
 cal_cs_print_gen( gcal, "Gregorian", use_hms, buf, maxlen );
}

void cal_cs_print_os( GenCal* gcal, char* buf, long maxlen )
{
 logical use_hms = 1;
 cal_cs_print_gen( gcal, "Julian", use_hms, buf, maxlen );
}

void cal_cs_print_simple_greg( GenCal* gcal, char* buf, long maxlen )
{
 logical use_hms = 0;
 cal_cs_print_gen( gcal, " ", use_hms, buf, maxlen );
}

void cal_cs_print_gen( GenCal* gcal, char* cdate, logical use_hms, char* buf, long maxlen )
{
 integer prn_dp;
 integer hr,mn,s;
 integer prn;
 double fs; 
 double sec;
 TextCard tsname;
 Sexagesimal time;
 TextCard dayname;
 TextCard pcdate;
 logical full;
 integer mode = SXG_MODE_STD;
 
 full = !utn_cs_is_blank( cdate );
 if ( full )
 {
  cal_ts_get_name( gcal->sys->ts, tsname );
  cal_weekday_name( gcal->data[GC_DOW], dayname );
  utn_cs_paren( cdate, pcdate );
 } else {
  utn_cs_copy( " ", tsname );
  utn_cs_copy( " ", dayname );
  utn_cs_copy( " ", pcdate );
 }
 if ( gcal_verbose )
  printf( "CSPG DOW=%ld HMS = %ld %ld %ld DT=%f\n", gcal->data[GC_DOW], gcal->data[GC_HR], gcal->data[GC_MIN], gcal->data[GC_SEC], gcal->et.dt );

 if ( use_hms )
 {
  hr = gcal->data[GC_HR];
  mn = gcal->data[GC_MIN];
  s  = gcal->data[GC_SEC];
  fs = gcal->et.dt * DL_SEC_D;
  prn = gcal->precision;
  prn_dp = 2;
  time = sxg_init( 1, hr, mn, s, fs, prn );
 } else {
  sec = cal_cs_utd( gcal );
  cal_sec_to_hms_d( sec, &hr, &mn, &s, &fs );
  prn = TPREC_UNKNOWN;
  prn_dp = 8;
  time = sxg_init( 1, hr, mn, s, fs, prn );
 }
 cal_fmt_fgreg( gcal->data[GC_YR], gcal->data[GC_MON],  gcal->data[GC_DAY], &time,
                   prn, prn_dp, mode, dayname, tsname, pcdate, buf, maxlen );

}




