/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"


void cal_jde_to_daysec( edouble et, integer* day, double* sec )
{
 *sec = et.dt * DL_SEC_D;
 *day = et.t - 0.5;
}

edouble cal_daysec_to_jde( integer day, double sec )
{
 edouble et;

 et.t = day + 0.5;
 et.dt = sec / DL_SEC_D;
 return et;
}
