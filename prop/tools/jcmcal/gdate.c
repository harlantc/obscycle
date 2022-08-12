/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
#include <ctype.h>
#include <stdio.h>

/*----------------------------------------------------------------------*/
/*
 * cal_zero_gdate
 *
 * Uses sex.h
 * Uses gdate.h
 */

void cal_zero_gdate( GregDate* gdate )
{
 gdate->year = 0;
 gdate->month = 0;
 gdate->day   = 0; 
 gdate->weekday = 0;
 sxg_zero( &(gdate->time) );
 gdate->level = TPREC_UNKNOWN;
}

char* cal_parse_greg_date( char* ibuf, GregDate* gdate )
{
 Sexagesimal* time;
 TextBuf buf;
 char* ptr = buf;
 char* tptr = buf;
 TextCard word;
 integer weekday;
 integer k;
 logical is_frac = UT_FALSE;
 logical verbose;
 TextCard vcalbuf;

 utn_cs_copy( ibuf, buf );  /* If buf is "NOW" its contents will be replaced */
 verbose = utn_dbg_get();
 if ( verbose )
 {
  snprintf( vcalbuf, UT_CARD_SIZE, "Parsing date: %s", buf );
  utn_fio_tmsg( vcalbuf );
 }
 cal_zero_gdate( gdate );

 utn_cs_get_c( &ptr, word, CARD_SIZE );
 
/* Check for a weekday name */
 weekday = cal_weekday_parse( word );
 if ( weekday > 0 )
 {
  tptr = ptr;
  utn_cs_get_c( &ptr, word, CARD_SIZE );
 }
/* If the word has a colon in it, it must be a time all on its own.. */
 k = utn_cs_index_char( word, ':' ); 

 if ( k == 0 )
 {
/* Parse year, month, day */
/* We ignore the last word and reparse in inside parse ymd */
  ptr = tptr;
  is_frac = cal_parse_ymd_to_gdate( &ptr, gdate );
  if ( weekday > 0 )
   gdate->weekday = weekday;
 }

 if ( !is_frac && ptr )
 {
  if ( verbose )
  {
   snprintf( vcalbuf, UT_CARD_SIZE, "Parse time from %s\n", ptr );
   utn_fio_tmsg( vcalbuf );
  }

  time = &gdate->time;
  sxg_parse( &ptr, time );
  if ( time->level != TPREC_UNKNOWN )
   gdate->level = time->level;

 }
 if ( verbose && ptr )
 {
   snprintf( vcalbuf, UT_CARD_SIZE, "Residual text: %s\n", ptr );
   utn_fio_tmsg( vcalbuf );
 }
 return ptr;
}


logical cal_parse_ymd_to_gdate( char** pptr, GregDate* gdate )
{
 char* ptr = *pptr;
 logical bc = 0;
 TextCard word;
 TextCard tmp;
 integer digits;
 char* wptr;
 char* sptr;
 TextCard today;
 integer last;
 integer prn;
 integer y = 0;
 integer m = 0;
 integer d = 0;
 double utd = 0.0;
 logical approx = 0; 
 logical is_frac = 0;
 logical ok = UT_TRUE;

 utn_cs_get_c( &ptr, word, CARD_SIZE );

/* Check for "circa" abbreviation */
 if ( *word == 'c' )
 {
  if ( utn_cs_eq( word, "c" ) || utn_cs_eq( word, "c." ) || utn_cs_eq( word, "ca." )
                          || utn_cs_eq( word, "ca" ) )
  {
   approx = 1;
   utn_cs_get_c( &ptr, word, CARD_SIZE );
  }
 }

 if ( utn_cs_uceq( word, "TODAY" ))   /* At 0hUTC today */
 {
  utn_sys_today( today, CARD_SIZE );  
  today[11] = '\0'; /* Kill time of day */
/* Overwrite input buffer */
  ptr = *pptr;
  utn_cs_copy( today, ptr );
  utn_cs_get_c( &ptr, word, CARD_SIZE );
 } else if ( utn_cs_uceq( word, "NOW" )) {
  utn_sys_today( today, CARD_SIZE );  
/* Overwrite input buffer */
  ptr = *pptr;
  utn_cs_copy( today, ptr );
  utn_cs_get_c( &ptr, word, CARD_SIZE );
 }
 

/* Check for BC and AD */
 if ( utn_cs_uceq_siz( word, "BC", 2 ) )
 {
  bc=1;
  if ( utn_cs_is_blank( word+2 ))
   utn_cs_get_c( &ptr, word, CARD_SIZE );
  else
  {
   utn_cs_copy_siz( word+2, tmp, CARD_SIZE );
   utn_cs_copy( tmp, word );
  }
 } else if ( utn_cs_uceq_siz( word, "AD", 2 )) {  
/*  Just swallow AD, it's the default */
  if ( utn_cs_is_blank( word+2 ))
   utn_cs_get_c( &ptr, word, CARD_SIZE );
  else
  {
   utn_cs_copy_siz( word+2, tmp, CARD_SIZE );
   utn_cs_copy( tmp, word );
  }
 }
 wptr = word;

/* Handle J2000.0 etc */
 if ( *wptr == 'J' || *wptr == 'B' ) wptr++;
 
/* Look for a year. 
 * Support decades '1990s'
 *         centuries '17C'
 *         millennia 'BC 3M'
 * 
 */

 y = utn_cs_read_val_int( &wptr, &digits );

 prn = TPREC_YEAR;
 if ( approx ) prn = TPREC_YEARS;
 if ( !ptr && wptr )
 {
  if ( utn_cs_eq( wptr, "?" ))
   prn = TPREC_YEARS;
  else if ( wptr - word > 1 && utn_cs_eq( wptr-1, "0s" ) ) 
  {
   prn = TPREC_DECADE;
   if ( approx ) prn = TPREC_DECADES;
  }
  else if ( wptr - word > 1 && utn_cs_eq( wptr-1, "0s?" ) )
   prn = TPREC_DECADES;
  else if ( utn_cs_eq( wptr, "C" )) 
  {
   prn = TPREC_CENTURY;
   if ( approx ) prn = TPREC_CENTURIES;
   y = bc ? y * 100: (y-1)*100 + 1;    
  }
  else if ( utn_cs_eq( wptr, "C?" ) )
  {
   prn = TPREC_CENTURIES;
   y = bc ? y * 100: (y-1)*100 + 1;    
  }
  else if ( utn_cs_eq( wptr, "M" ) )
  {
   prn = TPREC_MILLENIUM;
   y = bc ? y * 1000: (y-1)*1000 + 1;    
  }
 }
 if ( bc ) y = 1 - y;
 wptr = ptr;
/* Get the month */ 
 if ( ptr ) 
 {
  utn_cs_get_c( &ptr, word, CARD_SIZE );
  last = utn_cs_ends( word );
  approx = ( word[last-1] == '?' );
  if ( approx ) word[last-1] = '\0';
  m = cal_imon( word );
  if ( m < 0 )
  {
   ok = UT_FALSE;
   ptr = wptr;
  }
  else if ( m > 12 || approx )
   prn = TPREC_QTR;
  else
   prn = TPREC_MONTH;
 }

/* Require next item to be digit */
 if ( ok && ptr && *ptr && !isdigit( *ptr ))
 {
  ok = UT_FALSE;   
 }
 if ( ok && ptr ) {
  prn = TPREC_DAY;
  sptr = ptr;
  utn_cs_get_c( &ptr, word, CARD_SIZE );
  wptr = word;
  while( *wptr && *wptr == ' ' ) wptr++;

  d = utn_cs_read_val_int( &wptr, &digits );
  utn_cs_advance( &wptr );

  if ( wptr )
  {
   approx = (*wptr == '?');
   if ( approx )
    prn = TPREC_DAYS;
   else if ( *wptr == '.' )
   {
    is_frac = 1;
    utd = utn_cs_read_val_frac( &wptr, &digits );
    if ( digits < 2 )
     prn = TPREC_DECIDAY;
    else if ( digits < 3 )
     prn = TPREC_CENTIDAY;
    else if ( digits < 5 )
     prn = TPREC_MINUTE;
    else if ( digits < 6 )
     prn = TPREC_SECOND;
    else
     prn = TPREC_MILLISEC;
   }
  }
 }

 gdate->year = y;
 gdate->month = m;
 gdate->day = d;
 sxg_in_d( utd * DL_SEC_D, &gdate->time );
 gdate->level = prn;
 *pptr = ptr;
 if ( !ok ) is_frac = UT_TRUE;  /* skip hms.ss parsing */
 return is_frac;
}


/*----------------------------------------------------------------------*/
/* 
 * cal_greg_gdate
 *
 * Uses cal.h
 * Uses sex.h
 */

/* JD to GDATE  */
void cal_greg_gdate( double t, GregDate* gdate )
{
 double utd;
 integer month;
 integer day;
 cal_greg( t, &gdate->year, &month, &day, &utd );
 gdate->month = month;
 gdate->day = day;
 sxg_in_d( utd, &gdate->time );
 gdate->level = TPREC_UNKNOWN;
}
/*----------------------------------------------------------------------*/
/*
 * cal_jd_gdate
 * 
 * Uses sex.h
 * 
 */

/* GDATE TO JD */
double cal_jd_gdate( GregDate gdate )
{
 double utd;
 utd = sxg_out_d( &gdate.time );
 return ( cal_jd( gdate.year, (integer)gdate.month, (integer)gdate.day, utd ) );
}


GregDate cal_ymd_to_gdate( CalDate ymd )
{
 GregDate gdate;
 integer level = TPREC_UNKNOWN;
 edouble et;
 integer weekday = 0;
 Sexagesimal time;
 sxg_in_d( ymd.sec, &time ); 
 et = cal_ymd_to_jde( ymd );
 weekday = cal_jde_weekday_no( et );
 gdate = cal_gdate_set( (integer)ymd.year, (integer)ymd.month, (integer)ymd.day, 
              &time, weekday, level );
 return gdate;               
}

GregDate cal_gdate_set( integer y, integer m, integer d, Sexagesimal* time, integer weekday, integer level )
{
 GregDate gdate;
 gdate.year = y;
 gdate.month = m;
 gdate.day = d;
 gdate.time = *time;
 gdate.weekday = weekday;
 gdate.level = level;
 return gdate;
}

CalDate cal_gdate_to_ymd( GregDate gdate )
{
 double sec; 

 sec = sxg_out_d( &gdate.time );
 return cal_ymd_create( gdate.year, (integer)gdate.month, (integer)gdate.day, sec );
}


/*----------------------------------------------------------------------*/

void cal_fmt_gdate( GregDate date, integer mode, char* tbuf, integer siz )
{
 integer prn_dp = 2;
 integer prn;
 integer y,m,d;

 y = date.year;
 m = date.month;
 d = date.day;
 prn = ( date.level == TPREC_UNKNOWN ) ? TPREC_SECOND : date.level;
 cal_fmt_fgreg( y, m, d, &date.time, prn, prn_dp, mode, " ",  " ", " ", tbuf, siz );

}

void cal_fmt_date( double jd, char* buf, integer maxlen )
{
 GregDate g;
 integer mode = SXG_MODE_SDB;
 if ( jd == 0.0 )
  utn_cs_copy( "-", buf );
 else 
 {
  cal_greg_gdate( jd, &g );
  cal_fmt_gdate( g, mode, buf, maxlen );
 }
}

void cal_fmt_std_date( double jd, char* buf, integer maxlen )
{
 GregDate g;
 integer mode = SXG_MODE_STD;
 if ( jd == 0.0 )
  utn_cs_copy( "-", buf );
 else 
 {
  cal_greg_gdate( jd, &g );
  cal_fmt_gdate( g, mode, buf, maxlen );
 }
}

void cal_fmt_day( double jd, char* buf )
{
 if ( jd == 0.0 )
  utn_cs_copy( " ", buf );
 else
  cal_fmt_date( jd, buf, 12 );
}


void cal_fmt_centiday( double te, char* buf, integer maxlen )
{
 integer y,m,d;
 double utd;   
 double dd;    
 if ( te == 0 ) {
  utn_cs_copy( " ", buf );
 } else {
  cal_greg( te, &y, &m, &d, &utd );
  dd = d + utd / DL_SEC_D;
  cal_fmt_ymd( y, m, d, buf, CARD_SIZE );
  snprintf( buf+9, maxlen-9, "%5.2f", dd );
 }
} 
 

void cal_fmt_date_mode( double jd, char* buf, integer maxlen, integer mode )
{
 GregDate g;

 if ( jd == 0.0 )
  utn_cs_copy( "-", buf );
 else 
 {
  cal_greg_gdate( jd, &g );
  cal_fmt_gdate( g, mode, buf, maxlen );
 }
}

/* Round a date to a given precision and return both the new string and value */
double cal_date_round( char* buf, integer level, integer maxlen )
{
 GregDate date;
 TextCard buf1;
 integer mode = SXG_MODE_SDB | SXG_MODE_SDB1;  /* Include colons */

 cal_parse_greg_date(  buf, &date );
 if ( level <= TPREC_DAY )
 {
  sxg_zero( &date.time );
 }
 date.level = level;
 cal_fmt_gdate( date, mode, buf1, CARD_SIZE );
 utn_cs_copy_siz( buf1, buf, maxlen );
 return cal_date_to_jd( buf );
}

integer cal_date_get_precision( char* cdate )
{
 GregDate date;
 integer level;
 if ( utn_cs_eq( cdate, "-" ))
  level = 0.0;
 else
 {
  cal_parse_greg_date( cdate, &date );
  level = date.level;
 }
 return level;
}

double cal_date_to_jd( char* ldate )
{
 GregDate date;
 double t;
 char* ptr;
 integer sv, iv;
 double tpack, fv;
 char* cdate = ldate;

 while( *cdate == ' ' ) cdate++;
 if ( utn_cs_is_blank( cdate ) || utn_cs_eq( cdate, "q" ) || utn_cs_eq( cdate, "-"))
 {
  t = 0.0;  
 }
 else
 {
/* Check for packed epoch format */
  ptr = cdate;


  utn_cs_read_val_de( &ptr, &sv, &iv, &fv );
  if ( utn_cs_is_blank ( ptr ) && sv == 1 &&
      ( iv > 10000 || iv == 0 || *cdate == '0' ))  /* Big numbers, or leading zero */
  {
   tpack = iv + fv;
   if ( tpack == 0.0 )
    t = 0.0;
   else if ( tpack < 100000.0 || ( tpack >= 1.0E6 && tpack < 1.0E7 ))  /* PDOY */
    t = cal_pdoy_to_jd( tpack );
   else  /* GPACK */
    t = cal_unpack( tpack );
  } else {
   cal_parse_greg_date( cdate, &date );

   t = cal_jd_gdate( date );

  }
 }

 return t; 
}


GregDate cal_jde_to_gdate( edouble t )
{
 GregDate date;
 integer year;
 integer month,day;
 double sec;

 cal_greg( t.t, &year, &month, &day, &sec );
 date.year = year;
 date.month = month;
 date.day = day;
 sec += t.dt;
 sxg_in_d( sec, &date.time );
 date.level = TPREC_MILLISEC;
 return date;
}


edouble cal_gdate_to_jde( GregDate* gdate )
{
 edouble jd;

 jd.t = cal_jd( (integer)gdate->year, (integer)gdate->month, (integer)gdate->day, 0.0 );
 jd.dt = sxg_out_d( &gdate->time ) / DL_SEC_D; 
 return( jd );
}


