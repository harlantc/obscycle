/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

void cal_cts_jde_to_date( CalendarSystem* cts, edouble et, char* buf )
{
 GenCal date;

 if ( gcal_verbose )
  printf( "Formatting date %f %f\n", et.t, et.dt );
 cal_cs_init( &date, cts );
 cal_cs_eval_jde( et, &date );
 if ( gcal_verbose )
  printf( "Now print date\n" );
 cal_cs_print( &date, buf );
}

void cal_cs_init( GenCal* date, CalendarSystem* cts )
{
 utn_ar_zero_i( date->data, CALDEPTH );
 date->sys = cts;
 date->et = cal_ed_compose( 0.0, 0.0 );
 date->precision = TPREC_UNKNOWN;
}
