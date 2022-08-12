/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

char* PrecisionNames[] = { "Unknown", "Millenium", "Centuries", "Century",
       "Decades", "Decade", "Years", "Year", "Quarter", "Months", "Month",
       "Days", "Day", "Deciday", "Hour", "Centiday", "Minute", "Second",
       "Millisec" };

/* 2009 Dec: changed Days precision from 10d to 3d */

double PrecisionVals[] = { 0.0, 365250.0, 100000.0, 36525.0, 10000.0, 3652.5,
  1000.0, 365.25, 100.0, 60.0, 30.0, 3.0, 1.0, 0.1, 0.042, 0.010, 0.00069,
  1.16e-05, 1.16e-08 };


void cal_precision_name( integer precision, char* name, integer maxlen )
{
 utn_cs_copy_siz( PrecisionNames[precision], name, maxlen );
}

double cal_precision_val( integer precision )
{
 double p;
 p = PrecisionVals[precision];
 return p;
}

/* Given a date with an error, construct a human-friendly approximate date string */

void cal_fmt_prec_dt_date( double t, double dt, char* buf, integer maxlen )
{
 integer prec;
 prec = cal_precision_eval_code( dt );
 cal_fmt_prec_date( t, prec, buf, maxlen );
}

/* Centiday date with precision */
void cal_fmt_prec_centidate( double t, double dt, char* buf, integer maxlen )
{
 GregDate gdate;
 integer prec;
 integer mode = SXG_MODE_CENTI;
 prec = cal_precision_eval_code( dt );
 if ( prec > TPREC_CENTIDAY )
  prec = TPREC_CENTIDAY;
 else if ( prec > TPREC_DECIDAY )
  prec = TPREC_DECIDAY;

 cal_greg_gdate( t, &gdate );
 gdate.level = prec;
 cal_fmt_gdate( gdate, mode, buf, maxlen );
}

integer cal_precision_eval_code( double dt )
{
 integer i;
 integer np = 20;
 for ( i = 1; i < np; i++ )
 {
  if ( dt > PrecisionVals[i] )
   return i;
 }
 return 0; 
}

void cal_fmt_prec_date( double t, integer prec, char* buf, integer maxlen )
{
 GregDate gdate;
 integer mode = SXG_MODE_SDB;
 cal_greg_gdate( t, &gdate );
 gdate.level = prec;
 cal_fmt_gdate( gdate, mode, buf, maxlen );
}
