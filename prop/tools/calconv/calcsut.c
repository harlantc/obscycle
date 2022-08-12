/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

logical cal_is_utc( GenCal* date )
{
 logical utc;
 integer tst;
 tst = cal_ts_type( date );
 utc = ( tst == UTC_ );
 return utc; 
}

integer cal_ts_type( GenCal* date )
{
 integer tst;
 Timescale* ts;
 ts = cal_cs_get_ts( date );
 tst = ts->type;
 return tst;

}


double cal_cs_utd( GenCal* cal )
{
 edouble et;
 double sec;
 et = cal_cs_utd_to_jde( cal );
 sec = et.dt * DL_SEC_D;
 return sec;
}

edouble cal_cs_utd_to_jde( GenCal* cal )
{
 integer day;
 Timescale* ts;
 edouble et;
 double dtsec;
 double sec = 0.0;
 integer tprec;
 Sexagesimal time;
/* JD of 0h UT on given day */
 Calendar* calen;
 calen = cal->sys->cal;

 if ( utn_cs_eq( calen->name, "OS" ))
  et = cal_os_to_jde( cal->data[GC_YR], cal->data[GC_MON], cal->data[GC_DAY], 0.0 );
 else
  et.t = cal_jd( cal->data[GC_YR], cal->data[GC_MON], cal->data[GC_DAY], 0.0 );

 tprec = cal->precision;
 if ( gcal_verbose )
 {
  TextCard tname;  
  cal_precision_name( cal->precision, tname, CARD_SIZE );
  printf( "Cal precision = %ld %s Time precision = %ld\n", cal->precision, tname, tprec );
 }
 if ( tprec > TPREC_DAY || tprec == TPREC_UNKNOWN )
 {
  dtsec = cal->et.dt * DL_SEC_D;
  if ( dtsec < 1.0 )
  {
   time = sxg_init( 1, cal->data[GC_HR], cal->data[GC_MIN], cal->data[GC_SEC], dtsec, tprec );
   if ( cal_is_utc( cal ) )
   {
    if ( gcal_verbose )
     printf( "Time is UTC type\n" );

    day = et.t - 0.5;
    ts = cal_cs_get_ts( cal );
    sec = cal_lt_hms_to_sec( day, &time, (integer)ts->zone ); 

   }
   else
   {
    sec = sxg_out_d( &time );
    if ( gcal_verbose )
     printf( "Calc seconds = %f\n", sec );

   }
  } else {
   sec = dtsec;

  }
 }

 et.dt = sec / DL_SEC_D;

 return et;

}
