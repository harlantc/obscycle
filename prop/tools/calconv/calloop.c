/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"
double cal_parse_step( CalendarSystem* fsys, char* step, char* dtunit );
edouble cal_conv_step( CalendarSystem* fsys, edouble et1, double dt, char* dtunit );

void cal_conv_loop( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, 
  char* date1, char* date2, char* step, integer mode )
{
 edouble et1;
 edouble et2;
 edouble et;
 double dt;
 TextBuf from_date;
 TextBuf to_date;
 logical loop;
 logical leap;
 char* pdate1;
 char* pdate2;
/*
! If unit is day, can just add days as usual
! If unit is year, need to parse to cs in all non-jd cases.
! If unit is hour or sec, need to handle via elapsed if leap
!   or can add days if not leap.
*/
 TextWord dtunit;
 pdate1 = date1;
 pdate2 = date2;
 et1 = cal_cts_date_to_jde( state, fsys, &pdate1 );
 et2 = cal_cts_date_to_jde( state, fsys, &pdate2 );
 dt = cal_parse_step( fsys, step, dtunit );
 if ( dt == 0.0 )  /* Trap bad case */
  dt = 1.0;

 loop = cal_ed_le( et1, et2 );
 leap = cal_cts_leap( fsys );

 while( loop )
 {
  cal_cts_jde_to_date( fsys, et1, from_date );
  et = cal_conv_time( fsys, tsys, et1 );
  cal_cts_jde_to_date( tsys, et, to_date );
  cal_conv_output( fsys, tsys, from_date, to_date, mode );
  et1 = cal_conv_step( fsys, et1, dt, dtunit );
  loop = cal_ed_le( et1, et2 );
  if ( !loop )
  {  /* Guard against small differences in loop */
   double de = cal_ed_minus( et1, et2 );
   if ( de < 1.0E-6 ) loop = 1;
  }
 }
}


edouble cal_conv_step( CalendarSystem* fsys, edouble et1, double dt, char* dtunit )
{
 integer dti;
 
 double dtf;
 edouble et;
 integer* cal;
 GenCal date;
 if ( utn_cs_eq( dtunit, "d" ))
 {
  et = cal_ed_add( et1, dt );
 }
 else if ( utn_cs_eq( dtunit, "s" ))
 {
  dtf = dt / DL_SEC_D;
  et = cal_ts_jde_add( fsys->ts, et1, dtf );
 }
 else 
/*
! Eventually would like to replace this section with a generalized
! 'add gregorian components' routine
 *
 */
 {
  cal_cs_init( &date, fsys );
  cal_cs_eval_jde( et1, &date );
  cal = date.data;
  dti = max_i( (integer)dt, 1 );

  if ( utn_cs_eq( dtunit, "y" ))
   cal[GC_YR] += dti;
  else if ( utn_cs_eq( dtunit, "h" )) 
  {
   cal[GC_HR] += dti;
   if ( cal[GC_HR] > 24 )
   {
    cal[GC_HR] -= 24;
    cal[GC_DAY]++;
   }
  } 
  else if ( utn_cs_eq( dtunit, "m" )) 
  {
   cal[GC_MIN] += dti;
   if ( cal[GC_MIN] > 60 )
   {
    cal[GC_MIN] -= 60;
    cal[GC_HR]++;
   }
  }

  et = cal_cs_julian_jde( &date );
 }
 return et;
}


double cal_parse_step( CalendarSystem* fsys, char* step, char* dtunit )
{
 logical leap;
 logical annual;
 double dt = 0.0;
 double dt1 = 0.0;
 integer sign_val;
 integer int_exp;
 double frac_exp;
 char* ptr;
 TextCard unit;
 leap = cal_cts_leap( fsys );
 annual = cal_cts_has_years( fsys );
 dt = 1.0;
 utn_cs_copy( "d", dtunit );
 if ( !utn_cs_is_blank( step ))
 {
  ptr = step;
  utn_cs_read_val_d( &ptr, &sign_val, &int_exp, &frac_exp );
  dt = int_exp + frac_exp;
  dt1 = dt;
  if ( sign_val < 0 ) utn_msg_d( 0, "Step must be positive" );
  if ( ptr )
  {
   utn_cs_get_c( &ptr, unit, CARD_SIZE );
   utn_cs_upper(unit);
   if ( utn_cs_eq( unit, "D" ) || utn_cs_eq( unit, "DAY" ) )
     utn_cs_copy( "d", dtunit );
   else if ( utn_cs_eq( unit, "H" ) || utn_cs_eq( unit, "HR" ))
   {
    if ( annual )    
     utn_cs_copy( "h", dtunit );
    else
     dt /= 24.0;
   }
   else if ( utn_cs_eq( unit, "M" ) || utn_cs_eq( unit, "MIN" ) )
   {
    if ( annual )
     utn_cs_copy( "m", dtunit );
    else
     dt *= 60.0 / DL_SEC_D;
   }
   else if ( utn_cs_eq( unit, "S" ) || utn_cs_eq( unit, "SEC" ))
   {
    if ( annual )
     utn_cs_copy( "s", dtunit );
    else
     dt /= DL_SEC_D;
   }
   else if ( utn_cs_eq( unit, "Y" ) || utn_cs_eq( unit, "YR" ))
   {
    if ( annual )
     utn_cs_copy( "y", dtunit );
    else
     dt *= 365.25;
   }
   else
   {
    char ebuf[100];
    sprintf( ebuf, "Unknown step unit %s", unit );
    utn_msg_d( 0, ebuf );
   }
  }
 }


 return dt;

}
