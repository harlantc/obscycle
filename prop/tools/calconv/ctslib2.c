/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* Generalized date */
#define CAL_SIZE 10
#define CTS_2
#include "gcal.h"
#include <ctype.h>

/* GLOBALS */
integer gcal_verbose = 0;
char gcalbuf[256];

edouble cal_cts_date_to_jde( Calendrical* state, CalendarSystem* cts, char** bufp )
{

 GenCal date;
 edouble et= { 0.0, 0.0 };
 char* ptr = *bufp;

 if ( gcal_verbose )
  printf( "Calculate GenCal date of %s\n", *bufp );

 date = cal_cs_from_gen_date( state, cts, bufp );

 if ( gcal_verbose )
 {
  printf( "Calculate JDE of %s\n", ptr );
  if ( *bufp ) printf( "Residual text: %s\n", *bufp );
 }
 et = cal_cs_weekday_jde( &date );

 return et;
}

GenCal cal_cs_from_gen_date( Calendrical* calendrical, CalendarSystem* ncts, char** pptr )
{
 char* ptr = *pptr;
 TextCard word;
 char* wptr = word;
 integer ecode=1;  /* Zero is good, 1 is bad */
/* 
   cts is the default calendar to use. But it can be overridden.
   Read a leading word from the buffer and check if it defines
   a calendar
 */
 utn_cs_get_c( &ptr, word, CARD_SIZE );

/* We test for word starting with letter not number 
 * Note potential problems with "2000" being a legitimate calendar
 * (default calendar with +2000 time zone, PST)
 * which we avoid this way.
 */
 if ( isalpha( wptr[0] ) )
  ecode = cal_cts_reset( calendrical, ncts, word );
/* If no calendar was found, reset pointer to start */
 if ( ecode )
 {
  if ( gcal_verbose )
   printf( "No override calendar found in string ECODE=%ld\n", ecode );
 } else {
   if ( gcal_verbose ) 
     printf( "Overrode with calendar %s\n", word );
  *pptr = ptr;
 }
 return cal_cs_from_date( ncts, pptr );
}


char* cal_cs_date_to_gen_cal( CalendarSystem* cts, char* ptr, GenCal* gcal )
{
 Calendar* cal= cts->cal;
 cal_cs_init( gcal, cts );
 if ( cal )
  ptr = cal->parse( ptr, gcal );
 return ptr;
}

char* cal_cs_parse_os( char* ptr, GenCal* date )
{
 GregDate gdate;
 char* rptr;
 rptr = cal_parse_greg_date( ptr, &gdate );
 cal_cs_from_gdate( &gdate, date );
 return rptr;
}

char* cal_parse_elapse( char* ptr, GenCal* date )
{
 edouble et;
 double scale = 1.0;  /* Units are seconds */
 double epsilon = 1.0E-5;

 et.t = 0.0;
 et.dt = utn_cs_get_d( &ptr );
 date->et = cal_ed_resolve_scale( et, scale, epsilon, 0.0 );
 return ptr;
}

char* cal_cs_parse_greg( char* ptr, GenCal* date )
{
 GregDate gdate; 
 char* rptr;
 rptr = cal_parse_greg_date( ptr, &gdate );
 cal_cs_from_gdate( &gdate, date );
 return rptr;
}

char* cal_cs_parse_doy( char* ptr, GenCal* date )
{
 GregDate gdate;
 double pdoy;
 integer doy;
 double utd;
 pdoy = utn_cs_get_d( &ptr );
 cal_doy_unpack( pdoy,&gdate.year, &doy,&utd );
 gdate.month = 0;
 gdate.day = doy;
 gdate.level = TPREC_MILLISEC;
 sxg_in_d( utd, &gdate.time );
 cal_cs_from_gdate( &gdate, date );
 return ptr;
}



char* cal_cs_parse_pack( char* ptr, GenCal* date )
{
 GregDate gdate;
 double tp;
 double utd;
 integer m, d;
 tp = utn_cs_get_d( &ptr );
 cal_greg_unpack( tp, &gdate.year, &m, &d, &utd );
 gdate.month = m;
 gdate.day = d;
 cal_cs_from_gdate( &gdate, date );
 gdate.level = TPREC_MILLISEC;
 sxg_in_d( utd, &gdate.time );
 cal_cs_from_gdate( &gdate, date );
 return ptr;
}



char* cal_cs_parse_fail( char* ptr, GenCal* gcal )
{
 TextCard cname;
  integer type;
  type = cal_cs_type( gcal );
  cal_caltype_get_name( type, cname );
  printf( "cal_cs_from_date: Error: Unsupported calendar type %s\n", cname );
  return NULL;
}


GenCal cal_cs_from_date( CalendarSystem* ncts, char** pptr )
{
 GenCal date;
 char* ptr;
 ptr = cal_cs_date_to_gen_cal( ncts, *pptr, &date );
 *pptr = ptr;
 return date;
}

char* cal_parse_epoch( char* buf, GenCal* date )
{
 char* ptr = buf;
 char c;
 while ( *ptr == ' ') ptr++;
 if ( *ptr == 'J' )

 {
  c = 'J';
  ptr++;
 } else if ( *ptr == 'B' ) {
  c = 'B';
  ptr++;
 } else {
  c = 'J';
 }
 date->data[GC_EPT] = c;
 date->et.t = utn_cs_get_d( &ptr );
 return ptr;
}

char* cal_parse_jd_date( char* buf, GenCal* date )
{
 char* ptr = buf;
 double fracval;
 integer sign;
 integer intval;

 if ( !ptr ) return NULL;

 utn_cs_read_val_d( &ptr, &sign, &intval, &fracval );
 date->data[JD_SIGN] = sign;
 date->data[JD_DAY] = intval;
 date->et.t = 0.0;
 date->et.dt = fracval;
 if ( gcal_verbose )
 {
  printf( "PARSE JD DATE: %s = %ld %ld %f\n", buf, sign, intval, fracval );
 }
 if ( utn_cs_index_char( buf, '.' ))
  date->precision = TPREC_MILLISEC;
 else
  date->precision = TPREC_DAY;
 return ptr;
}

void cal_cs_from_gdate( GregDate* gdate, GenCal* date )
{
 Sexagesimal* time;
/* Now fill the structure */
 if ( gcal_verbose )
 {
  sprintf( gcalbuf, "Parsed YMD = %ld/%02ld/%02ld Prn=%ld", gdate->year, (integer)gdate->month, (integer)gdate->day, (integer)gdate->level );
  utn_fio_tmsg( gcalbuf );
 }

 date->data[GC_YR]= gdate->year;
 date->data[GC_MON]=gdate->month;
 date->data[GC_DAY]=gdate->day;
 time = &gdate->time;
 date->data[GC_HR] = time->hour;
 date->data[GC_MIN] = time->min;
 date->data[GC_SEC] = time->sec;
 date->data[GC_DOW]= gdate->weekday;
 date->et.t = 0.0;
 date->et.dt = time->frac / DL_SEC_D;
 date->precision = gdate->level;
}

edouble cal_cs_julian_jde( GenCal* date )
{

 Calendar* cal; 
 edouble et = { 0.0, 0.0 };
 cal = date->sys->cal;
 if ( cal )
  et = cal->convert( date );
 return et;
}


edouble cal_cs_fail_to_jde( GenCal* date )
{
 edouble et = { 0.0, 0.0 };
 integer type;
 type =  cal_cs_type( date );
 printf( "cal_cs_julian_jde: Unsupported calendar type %ld\n", type );
 return et;
}


edouble cal_cs_elapse_to_jde( GenCal* date )
{
 edouble tz;
 edouble tz_tt;

 edouble et;
 edouble et2;
 edouble et3;
 integer s;
 Timescale* ts;
 double t_elapse, t_elapse_d;
 double ft_elapse, dt_elapse;
 integer it_elapse;
   tz = cal_tzero( date->sys );
   ts = cal_cs_get_ts( date );
   if ( ts->type == UTC_ )
   {

if ( gcal_verbose )
printf( "ET ELAPSE = %20.10f %20.10f\n", date->et.t, date->et.dt );
    t_elapse_d = date->et.t / DL_SEC_D;
    s =( t_elapse_d < 0 ) ? -1 : 1; 
    frac_d( s * t_elapse_d, &it_elapse, &ft_elapse );
    t_elapse = it_elapse;    /* Integer days */
    dt_elapse = s * it_elapse * DL_SEC_D;   /* Seconds corr to integer days */
/* Add fractional days in seconds to fractional part */
    dt_elapse = date->et.dt + ( date->et.t - dt_elapse );
if ( gcal_verbose )
printf( "TELD = %20.10f IT EL = %ld DT_EL = %20.10f\n", t_elapse_d, it_elapse, dt_elapse );
if ( gcal_verbose )
printf( "ET ELAP(R)= %20.10f %20.10f\n", t_elapse, dt_elapse );
if ( gcal_verbose )
printf( "TZ UTC    = %20.10f %20.10f\n", tz.t, tz.dt * DL_SEC_D );
    tz_tt = cal_ts_converter( tz, ts->type, TT_, ts->zone, 0 );
if ( gcal_verbose )
printf( "TZ TT     = %20.10f %20.10f\n", tz_tt.t, tz_tt.dt * DL_SEC_D );
    et2.t  = s * t_elapse + tz_tt.t;
    et2.dt = dt_elapse / DL_SEC_D + tz_tt.dt;
if ( gcal_verbose )
printf( "ET TT     = %20.10f %20.10f\n", et2.t, et2.dt * DL_SEC_D );
    et = cal_ed_resolve( et2 );
if ( gcal_verbose )
printf( "ET TT (R) = %20.10f %20.10f\n", et.t, et.dt * DL_SEC_D );
    et3 = cal_ts_converter( et, TT_, ts->type, 0, ts->zone );
if ( gcal_verbose )
printf( "ET UTC    = %20.10f %20.10f\n", et3.t, et3.dt * DL_SEC_D );
    et = cal_ed_resolve( et3 );
if ( gcal_verbose )
printf( "ET UTC (R)= %20.10f %20.10f\n", et.t, et.dt * DL_SEC_D );
   } else {
    et2.t  = date->et.t / DL_SEC_D + tz.t;
    et2.dt = date->et.dt / DL_SEC_D + tz.dt;
    et = cal_ed_resolve( et2 );
   }

  return et;
}

edouble cal_cs_epoch_to_jde( GenCal* date )
{
 char c;
 edouble et2;
 double epoch;
 edouble et;
 c = (char)date->data[GC_EPT];
 epoch = date->et.t;
 et.dt = 0.0;
 if ( c == 'B' )
 {
  et.t = cal_bepoch_to_jd( epoch );
 } else {
  et.t = cal_jepoch_to_jd( epoch ); 
 }
 et2 = cal_ed_resolve( et );
 return et2;
}

edouble cal_cs_maya_to_jde( GenCal* date )
{
 edouble et = { 0.0, 0.0 };
 et.t = maya_long_to_jd( &date->data[GC_MAYA_LC], date->data[GC_MAYA_DEG] );
 return et;
}





edouble cal_cs_jd_to_jde( GenCal* date )
{
 edouble et;
 double t;
 double dt;
 Timescale* ts;
 edouble tz;
 ts = cal_cs_get_ts( date );
 tz = cal_tzero( date->sys );
 t= date->data[JD_SIGN] * date->data[JD_DAY] + tz.t;
 dt = date->data[JD_SIGN] * date->et.dt + tz.dt;
 et = cal_ed_make( t, dt );
 if ( gcal_verbose )
 {
  printf( "JD TO JDE: %ld %ld %f  ET %f %f TZ %f %f\n", date->data[JD_SIGN],
      date->data[JD_DAY], date->et.dt, et.t, et.dt, tz.t, tz.dt );
 }
 return et;
}

/*
 *   Evaluate JD while updating weekday value in date structure
 *   (if the calendar type is GREG)
 */
edouble cal_cs_weekday_jde( GenCal* date )
{
 edouble et = { 0.0, 0.0 };
 integer type;
 if ( gcal_verbose )
  printf( "cs_weekday_jde: Calculate JD date %ld %ld %ld %ld %20.12f %20.12f\n",
   date->data[0], date->data[1], date->data[2], date->data[3], date->et.t, date->et.dt );

 et = cal_cs_julian_jde( date );

 if ( gcal_verbose )
  printf( "cs_weekday_jde: JD date = %20.4f %20.12f\n", et.t, et.dt );

 type = cal_cs_type( date );
 if ( type == TGREG_ || type == JULIAN_CAL_ )
 {
   if ( gcal_verbose )
    printf( "Type is TGREG or JULIAN_CAL: calculating weekday no \n" );
  date->data[GC_DOW] = cal_jde_weekday_no ( et );
 }
 if ( gcal_verbose )
  printf( "cal_cs_weekday_jde: Calculated JDE = %20.4f %20.12f\n", et.t, et.dt );
 return et;
}

integer cal_cs_type( GenCal* date )
{
 CalendarSystem* sys;
 Calendar* cal = NULL;
 sys = date->sys;
 if ( sys )
  cal = sys->cal;

 if ( gcal_verbose )
 {
  if ( cal )
   printf( "CAL = %s \n", cal->name );
  else
   printf( "cal_cs_type: DATE has no CAL: SYS=%p\n", (void*)sys );
 } 

 if ( cal )
  return cal->type;
 else
  return 0;
}

integer cal_get_precision( GenCal* date )
{
 return date->precision;
}

void cal_set_precision( GenCal* date, integer prn )
{
 date->precision = prn;
}

Timescale* cal_cs_get_ts( GenCal* date )
{
 Timescale* ts;
 ts = date->sys->ts;
 return ts;
}



