/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"


edouble cal_ts_convert( Timescale* ts1, Timescale* ts2, edouble et1 )
{
 integer type1, type2;
 double zone1, zone2;
 type1 =  ( ts1 ) ? ts1->type : 0;
 type2 =  ( ts2 ) ? ts2->type : 0;
 zone1 =  (ts1 ) ? ts1->zone : 0.0;
 zone2 = ( ts2 ) ? ts2->zone: 0.0;
 return cal_ts_converter( et1, type1, type2, zone1, zone2 );
}



edouble cal_ts_converter( edouble et1, integer type1, integer type2, double zone1, double zone2 )
{
 edouble tt;
/* Convert to TT */
 edouble et2;
 edouble tai;
 edouble ut1;
/* 
   If timescale types are the same, do a simple copy
   unless UTC, when we may have time zones 
 */

 if ( type1 == type2 && type1 != UTC_ )
  return et1;
/*
 *  Convert from ts1 to tt, tt to ts2 unless there is a shorter path
 *
 *    LT -> TAI -> TT -> UT1 -> GSD
 *                  \
 *                   -> TDB
 */


 switch( type1 )
 {
  case UTC_:

   if ( type2 == UTC_ )
   {
    et2 = cal_lt_convert( et1, (integer)zone1, (integer)zone2 );
    return et2;
   } 
   tai = cal_lt_to_tai( et1, (integer)zone1 );
   if ( type2 == TAI_ )
    return tai;
   tt = cal_tai_to_tt( tai );
   break;
  case TAI_:  

   if ( type2 == UTC_ )
    return cal_tai_to_lt( et1, (integer)zone2 );

   tt = cal_tai_to_tt( et1 );
   break;
  case TDB_:
   tt = cal_tdb_to_tt( et1 );
   break;
  case UT1_:
   if ( type2 == ST_  || type2 == LST_ )
    return cal_ut1_to_gsd( et1 );

   tt = cal_ut1_to_tt( et1 );
   break;
  case LST_:
   if ( type2 == UTC_ )
    return cal_lst_to_utc( et1, zone1 );
   tt = cal_lst_to_tt( et1, zone1 ); /* Not implemented? */
  case TT_:
  default:
   tt = et1;
   break;
 }

 switch( type2 )
 {
  case TT_:
   return tt;

  case TAI_:
   return cal_tt_to_tai( tt );

  case UTC_:
   tai = cal_tt_to_tai( tt );
   return cal_tai_to_lt( tai, (integer)zone2 );

  case TDB_:
   return cal_tt_to_tdb( tt );
 
  case UT1_:
   ut1 = cal_tt_to_ut1( tt );
   return ut1;

  case ST_:
   ut1 = cal_tt_to_ut1( tt );
#if 0
  if ( 1 )
  {
   double g;
    ut1  = et1;
    g = gmst_simple( ut1.t+ut1.dt) / 360.0;
    ut1.t = ut1.t + 0.5;
    ut1.dt = g;
    return ut1;
  }
#endif
   return cal_ut1_to_gsd( ut1 );



  default:
   return tt;
 }

 


}

