/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"


double gmst( double jd )
{
 edouble t;
 edouble g;
 double st_deg;
 t= cal_ed_make( jd, 0.0 );
 g = cal_ut1_to_gsd( t );
 st_deg = g.dt * 360;
 return( st_deg );
}

edouble cal_ut1_to_tt( edouble ut1 )
{
 utn_fio_tmsg( "Bad timescale conversion: UT1 to TT" );
 return ut1;
}


edouble cal_ut1_to_gsd( edouble et )
{
 double Centuries;
 double Years;
 double Year_Fraction;
 integer Whole_Years;
 double Solar_Term;
 double Sidereal_Term;
 double Sid_Frac;
 double Rem;
 integer Sid_Day, Delta_Days;
 double Fine_Correction;
 double ST_Fine_Coeff[4] = { 0.0, 184.812866, 0.093104, -6.2E-6 };
 double jd;
 integer GSD_Start = 6714;
 double  ST_DEG_J2000 = 100.4606184;
 edouble g = { 0.0, 0.0 };


 jd = cal_ed_double( et );
 Centuries = cal_jc( jd );
 Years = 100 * Centuries;
 frac_d( Years, &Whole_Years, &Year_Fraction );
 Solar_Term = et.dt;
 Sidereal_Term = Year_Fraction;
 Sid_Day = et.t + Whole_Years;
/* Finer corrections */
 Fine_Correction = poly_d( Centuries, ST_Fine_Coeff, 3 ) / DL_SEC_D;
 Rem = ST_DEG_J2000 / 360.0 + Solar_Term + Sidereal_Term + Fine_Correction;
 frac_d( Rem, &Delta_Days, &Sid_Frac );
 g.t = Sid_Day + Delta_Days + GSD_Start;
 g.dt = Sid_Frac;
 return( g );
}


double gmst_simple( double t )
{
 double g;
 double t_int, t_frac;
 double Solar_Term;
 double Sidereal_Term;
 double Fine_Correction;
 double Total ;
 integer Whole_Years;
 double zero_point = 100.4606184 / 360.0;
 double ST;
 double Years;
 double Centuries;
 double J2000 = 2451544.5;
/* Calculate days since midnight GMT */
 t_int = (long)t + 0.5;
 t_frac =  t - t_int;


 Centuries = ( t - J2000 - 0.5 ) / 36525;
 Years = 100 * Centuries;
 Whole_Years = (long)Years;
 Solar_Term = t_frac;
 Sidereal_Term = Years - Whole_Years;
 Fine_Correction = (( 184.812866  + ( 0.093104 -6.2E-6 * Centuries ) * Centuries ) * Centuries)/86400.0;
 Total = zero_point + Solar_Term + Sidereal_Term + Fine_Correction;
 ST = Total - (long)Total;
 g = ST * 360.0;
 return g;
}

void local_p(char* a, double et );

edouble cal_lst_to_utc( edouble lst, double zone )
{
 edouble et;
 edouble gst0;
 edouble gst1;
 edouble gst;
 edouble et0;
 double dt0;
 double dt;
 integer iter;
 double zdt;
 double delta;
 double dut;
 double gain = 0.8;
 double tol = 1.0 / 86400.0;  /* 5 seconds */
/* Rate GST / UTC approx */
 double DLST_SCALE= 360.98564736629 / 360.0;
 zdt = zone / DL_SEC_D;
/* Time since LST midnight */
 dt = lst.dt;
#if DEBUG_ON
 printf( "CAL LST ZONE=%f ZDT=%f\n", zone, zdt );
 local_p( "INPUT LST", dt );
#endif
/* GST corresponding to LST */
 gst = cal_ed_add( lst, zdt );
/* Let's decide on the UTC breakpoint.
 * It is probably either UTC midnight or LT midnight.
 * We'll do the latter for now since it's the hard one
 */
 et.t = lst.t;
 et.dt = -zdt;
 et0 = cal_ed_resolve( et ); /* UTC of start of local day */
#if DEBUG_ON
 local_p( "LOCAL MID UTC ",et0.dt );
#endif
 gst0 = cal_ut1_to_gsd( et0 );  /* GSD of start of local day */
#if DEBUG_ON
 local_p( "LOCAL MID GSD ",gst0.dt );
#endif
 dt0 = lst.dt - gst0.dt;
 if ( dt0 < 0 ) dt0 += 1.0;
 dut = dt0 * DLST_SCALE;
#if DEBUG_ON
 local_p( "GUESS LT ",dut );
#endif
 et = cal_ed_add( et0, dut ); /* Guess UTC of LST */
#if DEBUG_ON
 local_p( "GUESS UTC ",et.dt );
#endif
 delta = 100.0;
 iter = 0;
 while( fabs( delta ) > tol && iter < 10 )
 { 
  iter++;
  gst1 = cal_ut1_to_gsd( et );  /* Check GSD of this UTC */
  delta =  dt - gst1.dt;   /* How much are we off in LST */
  dut = gain * DLST_SCALE * delta; /* guess Error in UT */
  et = cal_ed_add( et, dut );
#if DEBUG_ON
  local_p( "ITER GST",gst1.dt );
  local_p( "ITER DEL",delta );
  local_p( "ITER DUT",dut );
  local_p( "ITER UT ", et.dt );
  utn_fio_msg( " " );
#endif
 }
#if DEBUG_ON
 local_p( "RESULT", et.dt );
#endif
 return et;
}


void local_p( char* a, double et )
{
 TextCard date2;
 TextCard aa;
  Sexagesimal utime;
   
  sxg_in_d( et * DL_SEC_D, &utime );
  sxg_fmt( &utime, TPREC_UNDEF, SXG_MODE_STD, date2, 12 );
  snprintf( aa, UT_CARD_SIZE, "%-24s %20.6f %s", a, et, date2 );
  utn_fio_msg( aa );
}

edouble cal_lst_to_tt( edouble ut1, double zone )
{
 edouble et = ut1; /* Not implemented */
 return et;
}

