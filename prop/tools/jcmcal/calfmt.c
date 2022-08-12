/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

void utn_cs_append_c( char** ptr, char* text, integer siz );

void cal_fmt_year( integer yr, integer prn, char* buf );


/* prn used for time and date  tprec used for number of dp in seconds */ 
/* if calendar is blank, use abbreviated format */
void cal_fmt_fgreg( 
 integer y, integer m, integer d, Sexagesimal* time,
 integer prn, integer tprec, integer mode, char* dayname, char* timescale, char* calendar, char* obuf, integer maxlen )
{
 char* ptr;
 TextBuf buf;
 char f_day[5];
 char f_cal[41];
 char date_field[9];
 char year_field[17];
 integer ys, ye, ds;
 TextWord f_era;
 TextWord f_year;
 char*  f_date;
 TextWord f_time;
 TextCard tmp;
 integer lf_date;
 integer lf_time;
 integer lf_year;
 integer lf_day;
 integer lf_era;
 logical full;

 full = !utn_cs_is_blank( calendar );

 utn_cs_copy( " ", buf );
 if ( prn == TPREC_UNKNOWN || prn >= TPREC_DAYS )
 {
  utn_cs_copy_siz( dayname, f_day, 3 );
 } else {
  utn_cs_copy(  " ", f_day );
 }
 lf_day = 4;
 if ( mode & CAL_MODE_NO_DAYNAME ) lf_day = 0;

 cal_fmt_year( y, prn, year_field );

 lf_era = 3;
 utn_cs_get_ss( year_field, f_era, 1, lf_era );
 utn_cs_get_ss( year_field, tmp, 4, 9 );

/* Handle case of very large year */
 ys = ( utn_cs_is_blank( tmp ) ) ? 10 : 4;

 cal_fmt_month_day( m, d, prn, date_field );
 utn_cs_get_ss( date_field, tmp, 1, 3 );

/* Handle case of blank month differently */
 if ( utn_cs_is_blank( tmp ) )
 {
  ye = 15;
  ds = 2;
 } else {
  ye = 13;
  ds = 0;
 }

 lf_year = ye-ys+1;
 utn_cs_get_ss( year_field, f_year, ys, ye );

 lf_date = 8 - ds;
 f_date  = date_field + ds;

 lf_time = tprec + 9;
 if ( time && (prn == TPREC_UNKNOWN || prn > TPREC_DAY ))
  sxg_fmt( time, prn, mode, f_time, lf_time );
 else
  utn_cs_copy( " ", f_time );

 utn_cs_conc1( timescale, calendar, f_cal );

 ptr = buf;
 if ( lf_day > 1 && full )
    utn_cs_append_c( &ptr, f_day, lf_day );
 if ( full )
  utn_cs_append_c( &ptr, f_era, lf_era );
 utn_cs_append_c( &ptr, f_year, lf_year );
 *ptr++ = ' ';
 utn_cs_append_c( &ptr, f_date, lf_date );

/* Compat */
 if ( ( mode & SXG_MODE_SDB1 || mode & SXG_MODE_CENTI ) && *(ptr-1) == ' ' )
  ptr--;
 if( (mode & SXG_MODE_CENTI ) && *(ptr-1) == ' ' )
  ptr--;

 utn_cs_append_c( &ptr, f_time, lf_time );
 if ( full )
 {
  *ptr++ = ' '; 
  utn_cs_append_c( &ptr, f_cal, 0 );
 }
 utn_cs_copy_siz( buf, obuf, maxlen );
}

void utn_cs_append_c( char** ptr, char* text, integer siz )
{
 TextWord fmt;
 if ( siz > 0 )
  sprintf( fmt, "%s%ld%s", "%-", siz, "s" );
 else
  utn_cs_copy( "%s", fmt );

 *ptr += sprintf( *ptr, fmt, text );
}


void cal_fmt_year( integer yr, integer prn, char* buf )
{
 TextCard tbuf = " ";
 logical bc;
 char* tags[2] = { "BC", "AD" };
 integer tagno;
 integer algebraic_year;
 integer year_value;
 char suffix[8] = " ";

 bc = (yr <= 0);

 algebraic_year = bc ? 1 - yr : yr;
 tagno =          bc ? 0 : 1;
 

 year_value = algebraic_year;
 switch( prn )
 {
  case TPREC_MILLENIUM:
   year_value = bc ? algebraic_year / 1000 : (algebraic_year-1)/1000+1;
   utn_cs_copy( "M", suffix );
   break;
  case TPREC_CENTURIES:
   year_value = bc ? algebraic_year / 100 : (algebraic_year-1)/100+1;
   utn_cs_copy( "C?", suffix );
   break;
  case TPREC_CENTURY:
   year_value = bc ? algebraic_year / 100 : (algebraic_year-1)/100+1;
   utn_cs_copy( "C", suffix );
   break;
  case TPREC_DECADES:
   utn_cs_copy( "s?", suffix );
   break;
  case TPREC_DECADE:
   utn_cs_copy( "s", suffix );
   break;
  case TPREC_YEARS:
   utn_cs_copy( "?", suffix );
   break;
  default:
   break;
 }

 sprintf( tbuf, "%-3s%10ld%s", tags[tagno], year_value, suffix );
 utn_cs_copy( tbuf, buf );

}


/*----------------------------------------------------------------------*/
/*
 * cal_fmt_hms
 *
 * Uses <stdio.h>
 * Uses csproto.h
 */

void cal_fmt_hms( integer h, integer mn, integer s, double fs, integer prn, char* buf, integer siz )
{
 TextCard datec;
 TextWord datef = " ";
 integer local_prn = prn;

 if ( local_prn == TPREC_UNKNOWN ) local_prn = TPREC_MILLISEC;

 if ( local_prn < TPREC_MINUTE ) 
 {
  sprintf( datec, "%2.2ld:%2.2ld?", h, mn );
 } else if ( local_prn == TPREC_MINUTE ) {
  sprintf( datec, "%2.2ld:%2.2ld ", h, mn );
 } else if ( local_prn > TPREC_SECOND ) {
  sprintf( datef, "%16.12f", fs );
  sprintf( datec, "%2.2ld:%2.2ld:%2.2ld%s", h, mn, s, datef+3 );
 } else {
  sprintf( datec, "%2.2ld:%2.2ld:%2.2ld", h, mn, s );
 }

 utn_cs_copy_siz( datec, buf, siz );
}


void cal_fmt_month_day( integer m, integer d, integer prn, char* date )
{
 logical dayno;
 char day[8];
 char cmonth[8];
 dayno =  ( m == 0 && d > 0 );
 if ( dayno )
 {
  utn_cs_copy( "Day", cmonth );
 } else {
  cal_mon( m, cmonth );
  if ( m <= 12 && prn == TPREC_QTR )
   utn_cs_copy( "?", cmonth+3 );
 } 

 if ( d == 0 )
  utn_cs_copy( " ", day );
 else if ( dayno )
 {
  if ( prn == TPREC_DAYS )
   sprintf( day, "%3.3ld?", d );
  else
   sprintf( day, "%3.3ld", d );
 } else {
  if ( prn == TPREC_DAYS )
   sprintf( day, "%2ld?", d );
  else
   sprintf( day, "%2ld", d );
 }
 sprintf( date, "%-4s%-4s", cmonth, day );

}

void cal_fmt_ymd( integer y, integer m, integer d, char* buf, integer siz )
{
 TextWord md_field;
 TextWord year_field;
 TextWord tbuf;
 cal_fmt_year( y, TPREC_DAY, year_field );
 cal_fmt_month_day( m, d, TPREC_DAY, md_field );
 sprintf( tbuf, "%-4s %s", &year_field[9], md_field );
 utn_cs_copy_siz( tbuf, buf, siz );
}


void cal_fmt_jd( integer sign, integer day, double dt, char* tsname, char* buf, integer siz )
{
 char date1[20];
 TextCard date2;
 double t;
 if ( dt > CAL_LARGE_DT )
 {
  t = sign * ( day  + dt );
  sprintf( date2, "JD %f", t );
  utn_cs_copy_siz( date2, buf, siz );
  return;
 }
  sprintf( date2, "%12.10f", dt );

  if ( sign < 0 )
   sprintf( date1, "JD -%ld", day );
  else   
   sprintf( date1, "JD %ld", day );
  sprintf( buf, "%s%s %s", date1, date2+1, tsname );
}

void cal_fmt_mjd( integer sign, integer day, double dt, char* tsname, char* buf, integer siz )
{
 char hdr[20];
 TextCard date2;
  double mjd;
  integer mjd_int;
  double mjd_frac;
 double t;
 if ( dt > CAL_LARGE_DT )
 {
  t = sign * ( day  + dt );
  sprintf( date2, "MJD %f", t );
  utn_cs_copy_siz( date2, buf, siz );
  return;
 }



  mjd =  sign * day + sign * dt;
  if ( mjd < 0 )
  {
   mjd = -mjd;
   utn_cs_copy( "MJD -", hdr );
  } else if ( mjd < 99999 ) {
   utn_cs_copy( "MJD", hdr );
  } else {
   utn_cs_copy( "MJD ", hdr );
  }
  frac_d( mjd, &mjd_int, &mjd_frac );
  sprintf( date2, "%12.10f", mjd_frac );
  sprintf( buf, "%s%6ld%-12s%s", hdr, mjd_int, date2+1, tsname );
}

void cal_fmt_days( double mjd, char* buf, integer maxlen )
{
 TextCard tmp;
  sprintf( tmp, "%17.10f", mjd );
 utn_cs_copy_siz( tmp, buf, maxlen );
}


