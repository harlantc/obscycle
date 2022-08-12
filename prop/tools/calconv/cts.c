/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* 11/14/2012 - fix bug 13429 */

#include "gcal.h"

/*
 *  We support the following types of option:
 *    [<CAL>][/<TS>]   Calendar and/or timescale
 *    TIME[/<ZERO>]    ELAPSE calendar with optional zero point
 *    [<TS>]           Timescale only
 *    For <CAL> of type GST, if no timescale is given we assume GMST.
 *    For the 'timescale only' variant, we require <TS> is not a recognized
 *    calendar.
 */

#define CTS_OK    0
#define CTS_NOCAL 1
#define CTS_NOTS  2

CalendarSystem* cal_cts_copy( CalendarSystem* sys )
{
 CalendarSystem* nsys;
 nsys = calloc( 1, sizeof( CalendarSystem ));
 *nsys = *sys;
 return nsys;
}
/*
 *   The old cal_cts_set routine sets up the system from scratch
 *   and gives errors if the result is not recognized.
 *   We now make this a wrapper for cal_cts_reset, which doesn't
 *   complain, and is used for parsing strings which may or may not
 *   contain a calendar spec.
 */
void cal_cts_set( Calendrical* state, CalendarSystem* sys, char* opt )
{
 integer ecode;
 TextCard ebuf;

 sys->cal = NULL;
 sys->ts  = NULL;
#if 0  /* Don't override existing tzero */
 sys->tzero = cal_ed_compose( 0.0, 0.0 );
#endif
 ecode = cal_cts_reset( state, sys, opt );
 switch ( ecode )
 {
  case CTS_OK:
   break;
  case CTS_NOCAL:
   sprintf( ebuf, "Unknown calendar option %s", opt );
   utn_fio_tmsg( ebuf );
   break;
  case CTS_NOTS:
   sprintf( ebuf, "JCMCAL warning: bad timescale %s\n", opt );
   utn_fio_tmsg( ebuf );
   break;
 }
}


integer cal_cts_reset( Calendrical* state, CalendarSystem* sys, char* opt )
{
/* LST SCALE maps degrees to seconds */
 double LST_SCALE= 86400.0 / 360.0;
 integer ecode = CTS_OK;
 TextCard cal_opt;
 double zone = 0.0;
 Calendar* id = NULL;
 Timescale* ts;
 char* default_cal = "GREG";
 TextBuf zero = " ";
 char* pzero = zero;
 TextBuf default_zero ="1998 Jan 1.0";
 TextCard ts_opt;

 if ( utn_cs_uceq( opt, "Chandra" ))
 {
  utn_cs_copy( "TIME", cal_opt );
  utn_cs_copy( "1998 Jan 1", ts_opt );
 } else {
  cal_parse_spec( opt, cal_opt, ts_opt );
 }

 if ( gcal_verbose )
  printf( "cal_cts_set: Cal = %s  Ts = %s\n", cal_opt, ts_opt );

 if ( ( utn_cs_eq( cal_opt, "TIME" ) || utn_cs_eq( cal_opt,"DAYS"))&& !utn_cs_is_blank( ts_opt ))
 {
  utn_cs_copy( ts_opt, zero );
  utn_cs_copy( " ", ts_opt );
 } else if ( utn_cs_eq( cal_opt, "LST" ) && !utn_cs_is_blank( ts_opt ) ) {
  zone = utn_cs_read_d( ts_opt ) * LST_SCALE;


  utn_cs_copy( " ", ts_opt );
 }

 id = cal_calendar_parse_spec( state->calendars, cal_opt );
 if ( id )
 {
  if ( gcal_verbose )
   printf( "cal_cts_set: Matched calendar %s with ID %s Type %ld\n", cal_opt,  id->name, id->type );

/* Special case: default timescale for GSD is GMST */
  if ( utn_cs_is_blank( ts_opt ) && cal_calendar_get_type( id ) == TGST_ )
   utn_cs_copy( "GMST", ts_opt );

 } else {  /* Timescale only? */
  if ( gcal_verbose )
   printf( "cal_cts_set: No calendar found\n" );
  if ( utn_cs_is_blank( ts_opt ) )
  {
   utn_cs_copy( cal_opt, ts_opt );
  }
  else
  {
/* OK now we have a timescale option and an unknown calendar */
   ecode = CTS_NOCAL;
  }
  
 }
 ts = cal_ts_parse_spec( state->timescales, ts_opt );
 if ( !ts )
   ecode = CTS_NOTS;
 else
 {
  if ( gcal_verbose ) 
    printf( "cal_cts_set: found timescale %s\n", ts->name );
  if ( !id && ecode != CTS_NOCAL ) 
   id = cal_calendar_parse_spec( state->calendars, default_cal );

  if ( zone != 0.0 )
   ts->zone = zone;
 }
 if ( id )
  sys->cal = id;
 if ( ts )
  sys->ts  = ts;

 if ( ecode == CTS_OK )
 {
  utn_cs_copy( " ", sys->prompt );
  if (( utn_cs_eq( cal_opt, "TIME" ) || utn_cs_eq( cal_opt, "DAYS" )))
  {
   if ( !utn_cs_is_blank( zero ))
   {
    pzero = zero;
    cal_tzero_set( state, sys, &pzero );
   } else {
    pzero = default_zero;
    cal_tzero_set( state, sys, &pzero );
   }
  } else if ( id && utn_cs_eq( id->name, "MJD" )) {
   sys->tzero = cal_ed_compose( MJD_ZERO, 0.0 );
  } else {
   sys->tzero = cal_ed_compose( 0.0, 0.0 );
  }
 } 

 return ecode;
}


void cal_tzero_set( Calendrical* state, CalendarSystem* sys, char** tzerop )
{
 Timescale* ts;


 edouble et;

 TextCard calname;
 CalendarSystem* nsys;
 Calendar* cal;
 cal=sys->cal;
 ts = sys->ts;
 if ( !ts || !cal )
  return;

 utn_cs_copy( cal->name, calname );
 if ( gcal_verbose )
   printf( "Calculating TZERO's ET for TS = %s CAL=%s\n", ts->name, calname );
 


/* Use GREG calendar. We set tzero to zero so that we can calulate a JD zero point ok */
 nsys = cal_cts_copy( sys );
 cal_cts_reset( state, nsys, "GREG" );
 nsys->tzero =  cal_ed_compose( 0.0, 0.0 );

 et = cal_cts_date_to_jde( state, nsys, tzerop );


 if ( gcal_verbose  )
 {
  printf( "TZSYS = %s\n", nsys->ts->name );
  printf ( "TZERO's ET = %g %g\n", et.t,et.dt);
 }
 sys->tzero = et;
 sys->ts = nsys->ts;


 free( nsys );
}


edouble cal_tzero( CalendarSystem* sys )
{

 if ( !sys ) return cal_ed_compose( 0.0, 0.0 );
 return sys->tzero;
}
