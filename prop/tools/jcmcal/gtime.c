/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <stdio.h>
#include "jcmcal.h"

/* Round up close values */
void sxg_round( Sexagesimal* ptr, integer dp, integer max_sec )
{
/* Are we at 0.9999? */
 double df;
 
  df = 1 - ptr->frac;
  if ( df > 0.0 && df < 1.E-4 )
  {
   df = -log10( df );
   if ( df > dp + 1 ) 
   {
    ptr->frac = 0.0;
    ptr->sec++;
    if ( ptr->sec == max_sec )  /* Careful with leap seconds here! */
    {
     ptr->sec = 0;
     ptr->min++;
     if ( ptr->min == 60 )
     {
      ptr->min = 0;
      ptr->hour++;
     }
    }
   }
  }

}

void sxg_fmt( Sexagesimal* ptr, integer level, integer mode, char* buf,  integer siz )
{
 TextCard tmp=" ";
 TextCard tmp1=" ";
#define FFSIZE 8
 char hour_field[FFSIZE];
 char min_field[FFSIZE];
 TextCard frac_field;
 TextCard minsec_field;
 integer lh;
 double s;
 logical nocolon = 0;
/* If precision unknown, use full */
 if ( level == TPREC_UNDEF ) level = ptr->level;
 if ( level == TPREC_UNKNOWN ) level = TPREC_MILLISEC;
 if ( level <= TPREC_DAY )
  utn_cs_copy( " ", buf );
 else if ( mode & SXG_MODE_CENTI )
 {
  s = sxg_out_d( ptr ) / DL_SEC_D;
  if ( level >= TPREC_CENTIDAY )
   snprintf( hour_field, FFSIZE, "%3.2f", s );
  else if ( level >= TPREC_DECIDAY )
   snprintf( hour_field,FFSIZE, "%2.1f", s );
  else
   utn_cs_copy( "  ", hour_field );
  utn_cs_copy_siz( hour_field+1, buf, siz );
 } else {
  if ( mode & SXG_MODE_DEG )
  {
   snprintf( hour_field, FFSIZE, "%4d", ptr->sign * ptr->hour );
   lh = 4;
   if ( ptr->hour == 0 ) utn_cs_copy( "  -0", hour_field );
  }
  else if ( ptr->sign < 0 )
  {
   snprintf( hour_field, FFSIZE, "-%02d", ptr->hour );
   lh = 3;
  }
  else
  {
   snprintf( hour_field, FFSIZE, "%02d", ptr->hour );
   lh = 2;
  }
  if ( level > TPREC_MINUTE )
   snprintf( min_field, FFSIZE, "%02d:%02d", ptr->min, ptr->sec );
  else 
   snprintf( min_field, FFSIZE, "%02d", ptr->min );

  if ( level > TPREC_SECOND )  
    snprintf( frac_field, UT_CARD_SIZE, "%14.12f", ptr->frac );
  else
    utn_cs_copy( "  ", frac_field );
  if ( level < TPREC_SECOND )
  {   
   if ( level == TPREC_DECIDAY ) 
    utn_cs_copy( "??", tmp1 ); 
   else if ( level == TPREC_CENTIDAY ) 
    utn_cs_copy( "?", tmp1 );
   else if ( level == TPREC_HOUR )
   {
    utn_cs_copy( " ", tmp1 );
    if ( mode & SXG_MODE_DEG )
     utn_cs_copy( "d", min_field );
    else
     utn_cs_copy( "h", min_field );
    nocolon = 1;
   }
   else
    utn_cs_copy( " ", tmp1 );
     
   snprintf( minsec_field, UT_CARD_SIZE, "%-2s%s", min_field, tmp1 );
  } else {
   snprintf( minsec_field, UT_CARD_SIZE, "%-4s%s", min_field, frac_field+1 );
  }

  if ( nocolon || ( mode & SXG_MODE_SDB ))
   snprintf( tmp, UT_CARD_SIZE, "%s%s", hour_field, minsec_field );
  else
   snprintf( tmp, UT_CARD_SIZE, "%s:%s", hour_field, minsec_field );
  utn_cs_copy_siz( tmp, buf, siz );

 }
}


