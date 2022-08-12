/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"


static logical save_y2k = 1;

void cal_doy_set_y2k( logical q )
{
 save_y2k = q;
}

logical cal_doy_y2k( void )
{
 return save_y2k;
}

double cal_jde_to_pdoy( edouble et )
{
 CalDate u;
 u = cal_jde_to_doy( et );
 return cal_doy_to_pdoy( u );
}


/* Allow back compatible match of  57041.0 -> 1957041.0  */
integer cal_pack_y2k_year( integer y )
{
 integer c;
 if ( !cal_doy_y2k())
 {
  c = y / CENTURY;
  if ( y > 1900 + Y_2DIGIT_START || y <= 2000 + Y_2DIGIT_START ) c = 0;
  y = c * CENTURY + y % CENTURY;
 }
 return y;
}

/* Y2K support: map 1941,2040 to 0,100 */
integer cal_unpack_y2k_year( integer year )
{
 if ( year < CENTURY ) 
 {
  if ( year > Y_2DIGIT_START )
    year += 1900;
  else 
    year += 2000;
 } 
 return year;
}

double cal_doy_to_pdoy( CalDate u )
{
 integer y = u.year;
 double pdoy;

 y = cal_pack_y2k_year( y );
 pdoy = y * 1000.0 + u.day + u.sec / DL_SEC_D;
 return pdoy;
}

double cal_unpack( double tp )
{
 integer y, m, d;
 double utd;
 if ( tp <= 0.0 ) return( tp );
 cal_greg_unpack( tp, &y, &m, &d, &utd );
 return( cal_jd( y, m, d, utd ) );
}

double cal_greg_pack( integer year, integer m, integer d, double sec )
{
 integer y;
 double pymd;
 y = cal_pack_y2k_year( year );
 pymd =  y*10000.0 + m*100.0+d+sec/DL_SEC_D;
 return( pymd );
}

CalDate cal_pdoy_to_doy( double pdoy )
{
 CalDate date;
 integer PackedDays;
 integer d;
 integer year;
 const integer Packing = 1000;
 frac_d( pdoy, &PackedDays, &date.sec );
 date.sec *= DL_SEC_D;
 year = rmod_i( PackedDays, &d, Packing );
 date.year = cal_unpack_y2k_year( year );
 date.month = 0;
 date.day = d;

 return date;
}


void cal_greg_unpack( double g, integer* y, integer* m, integer* d, double* sec )
{
 integer d1;
 integer year;
 double utd;
 integer PackedDays;
 const integer Packing = 100;
 frac_d( g, &PackedDays, &utd );
 year  = rmod_i( PackedDays, &d1, Packing * Packing );
 *m    = rmod_i( d1, d, Packing  );
 *y    = cal_unpack_y2k_year( year );
 *sec = utd * DL_SEC_D;
}



GregDate cal_pdoy_to_gdate( double pdoy )
{
 integer doy;
 double ut;
 GregDate gdate;
 integer month;
 integer day;

 gdate.level = TPREC_UNKNOWN;
 if ( pdoy < 1000.0 ) return gdate;
 cal_doy_unpack( pdoy, &gdate.year, &doy, &ut );
 cal_daymon( gdate.year, doy, &month, &day );
 gdate.month = month;
 gdate.day = day;
 gdate.level = TPREC_MILLISEC;
 sxg_in_d( ut, &gdate.time );
 return gdate;
}


edouble cal_pdoy_to_jde( double pdoy )
{
 edouble et;
 CalDate d1;
 d1 = cal_pdoy_to_doy( pdoy );
 et = cal_ymd_to_jde( d1 );
 return et;
}

