/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

#define TT_OFFSET 32.184

edouble cal_tt_to_lt( edouble tt, integer zone )
{
 edouble tai;
 edouble lt;

 tai = cal_tt_to_tai( tt );
 lt  = cal_tai_to_lt( tai, zone );
 return lt;
}

edouble cal_lt_to_gsd( edouble lt, integer zone )
{
 double dt = -zone;
 edouble ut;
 ut = cal_ed_add( lt, dt / DL_SEC_D );
 return cal_ut1_to_gsd( ut );
}

edouble cal_lt_to_tt( edouble lt, integer zone )
{
 edouble tai;
 tai = cal_lt_to_tai( lt, zone );
 return cal_tai_to_tt( tai );
}

edouble cal_tdb_to_tt( edouble tdb )
{
 double dt;
 dt = cal_tdb( tdb );    /* Input should really be TT, but is close enough */
 return cal_ed_add( tdb, -dt / DL_SEC_D );
}

edouble cal_tt_to_tdb( edouble tt )
{
 double dt;
 dt = cal_tdb( tt );
 return cal_ed_add( tt, -dt / DL_SEC_D );
}

edouble cal_tt_to_ut1( edouble tt )
{
 double dt;
 dt = cal_ut1_minus_tt( tt );
 if ( utn_qnull_d( dt ))
 {
  dt = 0.0;
 }
 return cal_ed_add( tt, dt / DL_SEC_D );
}

edouble cal_tt_to_tai( edouble tt )
{
 double dt = -TT_OFFSET;
 return cal_ed_add( tt, dt / DL_SEC_D);
}

edouble cal_tai_to_tt( edouble tai )
{
 double dt = TT_OFFSET;
 return cal_ed_add( tai, dt / DL_SEC_D );
}


