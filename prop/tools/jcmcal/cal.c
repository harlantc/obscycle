/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#define CAL_GLOBAL 
#include <math.h>
#include <stdio.h>
#include "jcmcal.h"

void cal_state_free( CalState* state )
{
 if ( !state ) 
 {
  state = cal_global_state;
  cal_global_state = NULL;
 }
 free( state );
}


CalState* cal_state_init( void )
{
 if ( !utn_ver_init( "JCMCAL", "V2.0" ))
 {
  cal_global_state = calloc( 1, sizeof( CalState ));
  utn_cs_copy( " ", cal_global_state->errbuf );
 }
 return cal_global_state;
}


/*----------------------------------------------------------------------*/

static char *month_data[] = { "   ",
 "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
 "Oct", "Nov", "Dec" , "Q1", "Q2", "Q3", "Q4" };
static const integer n_months = 16;

 static integer month_length[] = {  0, 31,  28,  31,  30,  31,  30,  31,  31,  30,  31,  30,  31, 90, 91, 92, 92 };

 static integer month_offset[] = {0, 0,  31,  59,  90, 120, 151, 181, 212, 243, 273, 304, 334,  0, 90, 181, 273 };

/*----------------------------------------------------------------------*/
/*
 * cal_mon
 * Uses csproto.h
 */
char* cal_mon( integer month, char* buf )
 
{
 if ( month < 0 || month > n_months ) month = 0;
 utn_cs_copy( month_data[month], buf );
 return( buf );
}

/*----------------------------------------------------------------------*/
/*
 * cal_imon
 *
 * Uses csproto.h
 *
 */

integer cal_imon( char* buf )
{
 integer month;
 for ( month = 0; month <= n_months; month++ ) {
  if ( utn_cs_uceq( buf, month_data[month] ) ) return( month );
 }
 if ( utn_cs_uceq( buf, "Day" )) return 0;  /* Day of year */
 if ( utn_cs_digit_char( buf[0] ))  /* See if we can read a valid month number code 0 to 16 */
 {
  month = utn_cs_read_i( buf );
  if ( month >= 0 && month <= n_months ) return month;
 }
 return( -1 );
}

/*----------------------------------------------------------------------*/
/*
 * cal_month_length
 *
 */

integer cal_month_length( integer month )
{
 if ( month < 0 ) month = 0;
 return ( month_length[ month ] );
}
/*----------------------------------------------------------------------*/
/*
 * cal_greg_offset
 *
 */

integer cal_greg_offset( integer dy )
{
 integer n;
 if ( dy > 0 ) 
  n = 365 *dy + (dy-1)/4  - (dy-1) /100 + (dy-1)/400 + 1;
 else
  n = 365 * dy + dy / 4 - dy / 100 + dy / 400;
 return( n );
}

/* Special routine for large year numbers */
double cal_greg_offset_d( double ry )
{
 double n;
 integer dn;
 integer dy;
 double dn400 = 97000000.0;  
 const integer big400 = 4.0E8;
 integer nx;
 double dx;
  dx = ry / big400;
  nx = floor_d( dx );
  dy = ry - big400 * nx;

 if ( ry > 0 ) 
 {
  dn = (dy-1)/4  - (dy-1) /100 + (dy-1)/400 + 1;
 }
 else
 {
  dn = dy / 4 - dy / 100 + dy / 400;
 }
 n = 365.0 * dy + 365.0 * nx * big400 + dn + nx * dn400;
 return( n );
}


/*----------------------------------------------------------------------*/
/*
 * cal_greg_lyr
 * Uses cmathproto.h
 *
 */
integer cal_greg_lyr( integer year )
{
 integer n;
 n = 0;
 if ( divisible( year, 4 ) ) n = 1;
 if ( divisible( year, 100 ) ) n = 0;
 if ( divisible( year, 400 ) ) n = 1;
 return ( n );
}
/*----------------------------------------------------------------------*/
/*
 * cal_daynum
 *
 */
integer cal_daynum( integer y, integer m, integer d )
{
 /* Returns DOY number given ymd */
 integer offset;

 offset = cal_month_offset( m );

 if ( m <= 2 )
  return ( offset + d );
 else 
  return( offset + d + cal_greg_lyr( y ));
}

integer cal_month_offset( integer m )
{
 /* Returns DOY number given ymd */

 if ( m == 0 || m > n_months ) 
  return( 0 );
 else
  return( month_offset[m] );

}

/*----------------------------------------------------------------------*/
/*
 * cal_daymon
 *
 */

void cal_daymon( integer y, integer doy, integer*m, integer* d )
{
 /* Converts DOY to month and day */
 integer FEB_28 = month_offset[3];
 *d = doy;
 if ( *d > FEB_28 ) {
  *d = *d - cal_greg_lyr(y);
  if ( *d == FEB_28 ) { *d = 29; *m = 2; return; }
 } else if (  *d <= 0 ) {  
  *d = 0; *m = 0; return; 
 }
 *m = 12;
 while ( *m > 1  && *d <= month_offset[*m] ) (*m)--;
 *d = *d - month_offset[*m];
}


/*----------------------------------------------------------------------*/
/*
 * 
 * cal_jd_yr
 *
!  This routine returns the Julian Date for Jan 0.5 UTC in the given year.
! We treat years before and after J2000 separately. Calculate the
! number of years away from J2000, and then calculate the number
! of days corresponding to that interval and add it to the JD of J2000.
! Calls the cal_greg_offset function which calculates the number of days
! in a set of years.
 *
*/
integer cal_jd_yr( integer year )
{
 integer dyear;
 integer days;
 if ( year >= IY_J2000 ) {
  dyear = year - IY_J2000;
  days  = cal_greg_offset( dyear );
 } else {
  dyear = IY_J2000 + 1 - year;
  days = YL_DAYS + 1 - cal_greg_offset( dyear );
 }
 return ( IJD_J2000 + days );
}


edouble cal_jde( 
  integer y, 
  integer m, 
  integer d, 
  double sec 
)
{
 integer doy;
 edouble et;

 doy = cal_daynum( y, m, d );
 et.t = cal_jd_yr( y ) + doy - CAL_NOON;
 et.dt = sec / DL_SEC_D;
 return( et ); 
}



double cal_jd( integer year, integer month, integer day, double sec )
{
 double t;
 double dt;
 integer start, doy;
 start = cal_jd_yr( year );
 doy = cal_daynum( year, month, day );
 dt = sec / DL_SEC_D;
 t = start + doy - CAL_NOON;
 return t+dt;
}




double cal_mjd( integer y, integer m, integer d, double utd )
{
 double jd;
 jd =  cal_jd( y,m,d,utd ) - MJD_ZERO;
 return jd;
}

/*----------------------------------------------------------------------*/
/*
 * cal_greg
 *
 */

void cal_greg( double t, integer* y, integer* m, integer* d, double* utd )
{
 integer doy;
 cal_doy( t, y, &doy, utd );
 cal_daymon( *y, doy, m, d );
}



