/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmutils.h"
#ifndef UT_SXG
#define UT_SXG 60
#endif
#include "jcmcal.h"
#include <ctype.h>

#define HOURS   0
#define MINUTES 1
#define SECONDS 2
#define NSTEPS  3

/*----------------------------------------------------------------------*/
/*
 * sex_zero
 *
 * Initialize a sexg structure.
 * Uses sex.h
 */

void sxg_zero( Sexagesimal* x )
{
 x->sign = 1;
 x->hour  = 0;
 x->min=0;
 x->sec=0;
 x->frac = 0.0;
 x->level = TPREC_UNKNOWN;
}


Sexagesimal sxg_init( integer sign, integer hour, integer m, integer sec, double frac, integer precision )
{
 Sexagesimal t;

 t.sign = sign;
 t.hour = hour;
 t.min = m;
 t.sec = sec;
 t.frac = frac;
 t.level = precision;
 return t;
}

/* Struct to Value */

/*----------------------------------------------------------------------*/
/*
 * sxg_out_d 
 *
 * Converts a sexg struct to number of seconds
 * Uses: sex.h
 */

double sxg_out_d( Sexagesimal* x )
{
 double val;
 val = x->sign * ( ( x->hour * UT_SXG + x->min ) * UT_SXG + x->sec + x->frac );
 return( val );
}


/*----------------------------------------------------------------------*/
/*
 * sxg_in_d
 *
 * Converts a value to a sexg struct
 *
 * Uses: sex.h
 *
 */

/* Value to Struct */

void sxg_in_d( double v, Sexagesimal* x )
{
 integer sign;
 integer hour;
 integer min;
 integer sec;
 
 utn_sx_val_to_sxa_d( v, &sign, &hour, &min, &sec, &x->frac );
 x->sign = sign;
 x->hour = hour;
 x->min = min;
 x->sec = sec;
 x->level = TPREC_MILLISEC;
}

double sxg_parse_val( char** pptr )
{
 Sexagesimal s;
 sxg_parse( pptr, &s );
 return sxg_out_d( &s );
}



/*
 *  Read time and return precision
 */
void sxg_parse( char** ptptr, Sexagesimal* time )
{
 TextCard buf = " ";
 integer sign; 
 double fs = 0.0;
 double fs1 = 0.0;
 integer prn = TPREC_UNKNOWN;
 char* ptr = NULL;
 char c;
 integer prec_approx[] = { TPREC_DECIDAY, TPREC_CENTIDAY, TPREC_SECOND };
 integer prec_exact[] = { TPREC_HOUR, TPREC_MINUTE, TPREC_SECOND };

/*
! Read a sexagesimal time quantity. The formats supported are:
!  Example         template           Precision
!  359             hhh                HOUR
!  35920           hhhmm              MINUTE  
!  3592059         hhhmmss            SECOND  
!  1403            hhmm               MINUTE 
!  0003            hhmm               MINUTE  
!  000302          hhmmss             SECOND  
!  140302.33       hhmmss.ff          MILLISEC
!  00:03           hh:mm              MINUTE
!  00:03:02        hh:mm:ss           SECOND
!  00:03:02.33     hh:mm:ss.ff        MILLISEC
!  300:03:02.33    hhh:mm:ss.ff        MILLISEC
!  14h20m02.33s    hh'h'mm'm'ss.ff's' MILLISEC
!  14h             hh'h'              HOUR
!  14h20m          hh'h'mm'm'         MINUTE
!  14h?                               DECIDAY    * Unsupported 
!  14h20m?                            CENTIDAY   * Unsupported
!  1400?                              CENTIDAY
!  14.2            hh.hh              CENTIDAY
!  1420.3          hhmm.m             SECOND
!  14 20 33.3      hh mm ss.s         MILLISEC
!  14 20           hh mm              MINUTE
!  An optional sign is supported for non-time applications.
!
*/
 integer val;
 integer step =0;
 integer hhmm;
 integer mmss;
 integer vals[3];
 integer m;
 char* tptr = *ptptr;

 integer digits = 0;
 integer n;

 if ( utn_cs_is_blank( tptr ))
 {
  sxg_zero( time );  
  return;
 }
/* Copy string to buffer and delete trailing and leading blanks */
 while( *tptr == ' ' ) tptr++;
 utn_cs_copy( tptr, buf );
 ptr = buf;
 n = utn_cs_ends( buf );
 buf[n]= '\0';

/* Parse */
 sign = utn_cs_read_val_sign( &ptr );
 utn_ar_zero_i( vals, 3 );
 if ( ptr )
 {
  val = utn_cs_read_val_int( &ptr, &digits );
/* Case of hours or degrees. */
  if ( digits <= 3 )
  {
    step = HOURS;
    vals[HOURS] = val;
    while( digits > 0 && read_sxg_separator( &ptr, step ) )
    {
       val = utn_cs_read_val_int( &ptr, &digits );
       if ( digits > 0 )
        vals[++step] = val;       
    }
/* Case of hours and minutes with no separator:  hhmm */
  } else if ( digits <= 5 ) {
   vals[HOURS] = rmod_i( val, &vals[MINUTES], 100 ); 
   step = MINUTES;
   /* Support 12h20m or 1220:30 */
   while ( digits > 0 && read_sxg_separator( &ptr, step ))
   {
       val = utn_cs_read_val_int( &ptr, &digits );
       if ( digits > 0 )
        vals[++step] = val;            
   }
/* Case of hhmmss */
  } else {  
   hhmm        = rmod_i( val, &vals[SECONDS], 100 );
   vals[HOURS] = rmod_i( hhmm, &vals[MINUTES], 100 );
   step = SECONDS;
  }
 }
/* Provisional precision */
 prn = prec_exact[step];
 if ( *ptr && digits > 0 ) 
 {
  c = tolower ( *ptr );
  if ( c == '.' && *(ptr+1) )
  {
   fs1 = utn_cs_read_val_frac( &ptr, &digits );
   switch( step )
   {
    case HOURS:
     frac_d( fs1 * 3600, &mmss, &fs );
     vals[MINUTES] = rmod_i( mmss, &vals[SECONDS], 60 );
     prn = prec_approx[step+1];
     break;
    case MINUTES:
     frac_d( fs1 * 60, &vals[SECONDS], &fs );
     prn = prec_approx[step+1];
     break;
    case SECONDS:
     fs = fs1;
     prn = TPREC_MILLISEC;
     break;
    default:
     break;
   }
  } else if ( c == '?' ) {
   ptr++;
   c = tolower( *ptr++ );  /* Swallow one more */
   prn = prec_approx[step];
  } else if ( c != ' ' && step < SECONDS ) {
   /* Bad spec, set error */   
   prn = TPREC_UNKNOWN;
  }
 }  
 time->sign = sign;
 time->hour  = vals[HOURS];
 time->min = vals[MINUTES];
 time->sec = vals[SECONDS];
 time->frac = fs;
 time->level = prn;
/* ptr - buf is offset in copied string
 * we want to set back to original string at same point 
 */
 m = ptr - buf;
 *ptptr = tptr+m;
}

/*
 *  Swallow the next sexagesimal separator.
 *  Returns true if it finds a separator and the step is not
 * the final one (in other words, there may be a further step to read).
 */
logical read_sxg_separator( char** buf, integer step )
{
 logical sep = 0;
 char c;
 char* ptr = *buf;
 char stepname[]= { 'h', 'm',  's' };
 char degname[]=  { 'o', '\'', '"' };

 c = tolower( *ptr );
 if ( c == stepname[step] ||
      c == ' '            || 
      c == degname[step]  ||
      c == ':'            )
 {
   ptr++;
/* Advance past separator */
  while( *ptr == ' ' ) ptr++;
/* Is the next element a digit? */
  sep = ( *ptr && step < NSTEPS-1 );  
 }
 *buf = ptr;
 return sep;
}


void cal_prec( integer precode, double* precl, double* precu )
{

/* NA, Millenium, Centuries, Century, Decades, Decade, Years, Year, 
   Qtr, Months, month, days, day, deciday, hour, centiday, m, s, ms  */
/*     17C?   =  t(16C) to t(19C)-eps. =  -36525 + 2 * 36525
       17C    =  t(17C) +0    to t(17C) + 1C
       2006?  =  t(2006) -365 to t(2006) + 2 * 365  = 2005, 2006, 2007
    but 2006 =  t(2006)+0 to t(2006)+365
 */
 integer code;
 double lprecvals[] = { 0.0, 0.0,     36525.0,      0.0,  3652.5,    0.0,  
   365.25,  0.0,    0.0, 31.0, 0.0,   1.0, 0.1, 0.0,   0.01, 0.0,       0.0, -1.16e-8 };
 double uprecvals[] = { 0.0, 365250.0, 73050.0, 36525.0,  7305.0, 3652.5, 1000.0,
   730.50, 365.25, 90.0, 62.0, 31.0,  2.0,  0.1, 0.042, 0.01, 0.00069, 1.16e-5, 1.16e-8 };
 code = precode;
 if ( code < 0 ) code = 0;
 *precl = lprecvals[code];
 *precu = uprecvals[code];

}

