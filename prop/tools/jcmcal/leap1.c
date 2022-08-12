/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

/*
 Routines for 1960s UTC interpolated phase
 */

#define NINTERP 13

integer    JD_UTC_INTERP[] = {
        2437300 , 2437512 , 2437665 , 2438334 , 2438395 , 2438486 ,
        2438639 , 2438761 , 2438820 , 2438942 , 2439004 , 2439126 ,
        2439887  };
        
double UTC_INTERP_SEC[] = {
        1.4228180, 1.3728180, 1.8458580, 1.9458580,
        3.2401300, 3.3401300, 3.4401300, 3.5401300,
        3.6401300, 3.7401300, 3.8401300, 4.3131700,
        4.2131700 };

integer JD_ZERO_ENTRY[] = {
         1,  1, 3, 3, 8, 8, 8, 8, 8, 8, 8, 12, 12 };

double UTC_INTERP_SCALE[] = {
        0.001296, 0.001296, 0.0011232, 0.0011232,
       0.001296, 0.001296, 0.001296, 0.001296, 0.001296, 0.001296, 0.001296, 
       0.002592, 0.002592 };
       

edouble cal_lt_to_tai_interp( edouble utc, integer zone )
{
 edouble tai;
 integer leap_entry = 0;
 integer entry;

 double utc_scale;
 integer zero_entry;
 edouble djd;
 double step;
 integer day;
 double sec;

 cal_jde_to_daysec( utc, &day, &sec );
 for ( entry = 1; entry <= NINTERP; entry++ )
 {
  if ( day >= JD_UTC_INTERP[entry-1] )
   leap_entry = entry;
 }
 utc_scale = UTC_INTERP_SCALE[leap_entry -1 ];
 zero_entry = JD_ZERO_ENTRY[leap_entry - 1];
 djd = cal_daysec_to_jde( JD_UTC_INTERP[zero_entry-1], 0.0 );


 step = UTC_INTERP_SEC[leap_entry-1] + cal_ed_minus( utc, djd ) * utc_scale - zone;
 tai = cal_ed_add( utc, step / DL_SEC_D );
 return tai;
}

edouble cal_tai_to_lt_interp( edouble tai, integer zone )
{
 edouble utc;

 integer utai_day;
 double utai_sec;
 double utc_scale;
 integer entry;
 integer leap_entry = 0;
 integer zero_entry;
 double step;
 edouble jd_step;
 cal_jde_to_daysec( tai, &utai_day, &utai_sec );
 for ( entry = 1; entry <= NINTERP; entry++ )
 {
  if ( utai_day >= JD_UTC_INTERP[entry-1] ) 
  {
   if ( utai_day > JD_UTC_INTERP[entry-1] || utai_sec >= UTC_INTERP_SEC[entry-1] )
    leap_entry = entry;
  }
 }

 utc_scale =  - UTC_INTERP_SCALE[leap_entry-1] / ( 1.0 + UTC_INTERP_SCALE[leap_entry-1] );
 zero_entry = JD_ZERO_ENTRY[leap_entry-1];
 jd_step = cal_daysec_to_jde( JD_UTC_INTERP[zero_entry-1], UTC_INTERP_SEC[zero_entry-1] );
 step = -UTC_INTERP_SEC[leap_entry-1] + 
            cal_ed_minus( tai, jd_step ) * utc_scale + zone;
 utc = cal_ed_add( tai, step / DL_SEC_D );
 return utc;
}
