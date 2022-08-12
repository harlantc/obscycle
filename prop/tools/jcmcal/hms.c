/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"
void cal_sec_to_hms_d( double sec, integer* hr, integer* mn, integer* s, double* fs )
{
 Sexagesimal tu;
 sxg_in_d( sec, &tu );
 *hr = tu.hour;
 *mn = tu.min;
 *s  = tu.sec;
 *fs = tu.frac;
}

double cal_hms_to_sec_d( integer h, integer mn, integer s, double fs )
{
 double ds;
 ds = utn_sx_sxa_to_val_d( 1, h, mn, s, fs );
 return ds;
}


integer cal_hms_to_sec_i( integer h, integer mn, integer s )
{
 integer ds;
 ds = utn_sx_sxa_to_val_i( 1, h, mn, s );
 return ds;
}




