/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

/*----------------------------------------------------------------------*/
/*
 * doy_jd
 *
 */

double cal_pdoy_to_jd( double pdoy )
{
 edouble et;
 et = cal_pdoy_to_jde( pdoy );
 return( cal_ed_double( et ));
}


/*----------------------------------------------------------------------*/
/*
 *
 * cal_doy
 *
 * Uses cmathproto.h
 */

void cal_doy( double t, integer* y, integer* doy, double* dsec )
{
 CalDate date;
 date = cal_jde_to_doy( cal_ed_make( t, 0.0 ));
 *y   = date.year;
 *doy = date.day;
 *dsec= date.sec;
}

/*----------------------------------------------------------------------*/
/*
 * cal_doy_unpack
 *
 * Uses cmathproto.h
 * Uses jcm_date.h
 * Uses readproto.h
 */

void cal_doy_unpack( double pdoy, integer* y, integer* doy, double* utd )
{
 CalDate date;
 date = cal_pdoy_to_doy( pdoy );
 *y = date.year;
 *doy = date.day;
 *utd = date.sec;
}
/*----------------------------------------------------------------------*/
/*
 * cal_doy_pack
 * 
 *
 */

double cal_doy_pack( integer y, integer d, double sec )
{
 CalDate doy;

 doy = cal_ymd_create( y, 0, d, sec );
 return cal_doy_to_pdoy( doy );
}


