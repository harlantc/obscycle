/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#define LIB_MAIN 1
#include "gcal.h"

void cal_conv_cal_prompt( CalendarSystem* cts1, char* buf1, char* buf2 );

void cal_conv_header( CalendarSystem* cts1, CalendarSystem* cts2, integer mode )
{
 TextBuf buf;
 if ( mode <= 0 )
  return;

 if ( mode == 1 )
 {
  sprintf( buf, "%-40s%-40s", cts1->prompt, cts2->prompt );
  utn_fio_msg( buf );
 } else if ( mode == 2 ) {
  utn_fio_tmsg( "Dates [Conversion mode]" );
  utn_fio_tmsg( "Enter \"q\" to return to setup mode" );
 }

}



void cal_conv_list( CalendarSystem* cts1, CalendarSystem* cts2 )
{
 TextBuf buf;
 sprintf( buf, "From: %s", cts1->prompt );
 utn_fio_tmsg( buf );
 sprintf( buf, "To:   %s", cts2->prompt );
 utn_fio_tmsg( buf );
}

void cal_conv_cal_prompt( CalendarSystem* cts1, char* buf1, char* buf2 )
{
 Timescale* ts;
 TextCard calname;
 TextCard gbuf;
 TextCard zbuf;
 TextWord gbuf2;
 TextCard zbuf1;
 integer maxlen = CARD_SIZE;
 edouble tz; 
 double dt;
 cal_ts_get_name( cts1->ts, buf2 );
 cal_calendar_get_name( cts1->cal, calname );
 ts = cts1->ts;
 if ( utn_cs_eq( calname, "DAYS" ))
 {
  tz = cal_tzero( cts1 );
  dt = tz.t + tz.dt;
  cal_fmt_days( dt, zbuf, maxlen );
  utn_cs_numberclean( zbuf, 0, zbuf1, CARD_SIZE );
  cal_fmt_date( dt, gbuf, maxlen );
  utn_cs_copy_siz( gbuf, gbuf2, 12);
  sprintf( buf1, "Days(since JD %s %s, %s)", zbuf1, buf2, gbuf2 );
  utn_cs_copy( " ", buf2 );
 } else if ( utn_cs_eq( calname, "TIME" )) {
  tz = cal_tzero( cts1 );
  cal_fmt_days( tz.t + tz.dt, zbuf, maxlen );
  cal_fmt_date( tz.t + tz.dt, gbuf, maxlen );
  utn_cs_copy_siz( gbuf, gbuf2, 12);
  utn_cs_numberclean( zbuf, 0, zbuf1, CARD_SIZE );  
  sprintf( buf1, "Time(since JD %s %s, %s)", zbuf1, buf2, gbuf2 );
  utn_cs_copy( " ", buf2 );
 } else {
  cal_calendar_get_label( cts1->cal, buf1 );
 }
}

void cal_conv_make_prompts( CalendarSystem* cts1, CalendarSystem* cts2 )
{
 TextCard buf1;
 TextCard tbuf1; 
 TextCard buf2;
 TextCard tbuf2;
 char* prompt1 = cts1->prompt;
 char* prompt2 = cts2->prompt;
 integer caltype;


 cal_conv_cal_prompt( cts1, buf1, tbuf1 );
 caltype =  cal_calendar_get_type( cts1->cal );

 cal_conv_cal_prompt( cts2, buf2, tbuf2 );



 utn_cs_copy( buf1, prompt1 );
 utn_cs_copy( buf2, prompt2 );
 if ( utn_cs_eq( tbuf1, tbuf2 ) )
 {
  return;
 }
 else if ( utn_cs_eq( buf1, buf2 ) && caltype == TGREG_ )
 {
  utn_cs_copy( tbuf1, prompt1 );
  utn_cs_copy( tbuf2, prompt2 );
 } 
 else
 {
  if ( !utn_cs_is_blank( tbuf1 ) && !utn_cs_eq( tbuf1, "GMST" ))
   sprintf( prompt1, "%s (%s)", buf1, tbuf1 );
  if ( !utn_cs_is_blank( tbuf2 ) && !utn_cs_eq( tbuf2, "GMST" ))
   sprintf( prompt2, "%s (%s)", buf2, tbuf2 );
 }


}


edouble cal_conv_time( CalendarSystem* fsys, CalendarSystem* tsys, edouble et1 )
{
 return cal_ts_convert( fsys->ts, tsys->ts, et1 );
}



void cal_conv_output( CalendarSystem* cts1, CalendarSystem* cts2, char* buf1, char* buf2, integer mode )
{
 TextBuf buf;

 switch( mode )
 {
  case 0:
   utn_fio_msg( buf2 );
   break;
  case 1:
   sprintf( buf, "%-80s%s", buf1, buf2 );
   utn_fio_msg( buf );
   break;
  case 2:
   utn_fio_dashline(80);
   sprintf( buf, "%-24s %s", cts1->prompt, buf1 );
   utn_fio_msg( buf );
   sprintf( buf, "%-24s %s", cts2->prompt, buf2 );
   utn_fio_msg( buf );
   utn_fio_dashline(80);
   break;
  default:
   break;
 }

}
