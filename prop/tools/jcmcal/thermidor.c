/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

#define CAL_FR_START 2375838.5
#define CAL_FR_JAN1 102
#define CAL_FR_VEN1 263


void cal_thermidor_date( edouble et, char* buf, integer maxlen )
{
 integer an, mois, jour;
 cal_jde_to_thermidor( et, &an, &mois, &jour );
 cal_thermidor_format( an, mois, jour, buf, maxlen );
}

void cal_jde_to_thermidor( edouble et, integer* an, integer* mois, integer* jour )
{
 CalDate u; 
 integer FR_New_Year; 
 integer Greg_Leap;
 integer FR_Leap;
 integer Greg_Year;
 integer FR_Year;
 integer Greg_Day;
 integer FR_Day;
 integer FR_Month;
 integer FR_Year_Length = 365;
 if ( et.t < CAL_FR_START )
 {
  *an = -1;
  *mois = 0;
  *jour = 0;
 } else {
  u = cal_jde_to_doy( et );
  Greg_Year = u.year;
  Greg_Day  = u.day;
  FR_New_Year = CAL_FR_VEN1;

  Greg_Leap = cal_greg_lyr( Greg_Year );
  FR_Leap =  ( Greg_Year % 4 == 3 ) ? 1 : 0;
  FR_Year =  Greg_Year - 1792;
  if( Greg_Day > FR_New_Year + FR_Leap + Greg_Leap ) 
     FR_Year++;
  FR_Year_Length += FR_Leap;
  FR_Day = ( Greg_Day + CAL_FR_JAN1 - Greg_Leap ) % FR_Year_Length;
  if ( FR_Day == 0 ) FR_Day = FR_Year_Length;
  FR_Month = rmod_i( FR_Day, &FR_Day, 30 );
  if ( FR_Day != 0 ) 
   FR_Month++;
  else
   FR_Day = 30;

  *an = FR_Year;
  *mois = FR_Month; 
  *jour = FR_Day;
 }

}





/*

C>       Returns date in French Revolutionary Calendar given JD
c
c        Algorithm : gadfly%ihlpa@research.att.com (Ken Perlow)
c        Translated to Fortran in 1989 May by JCM
C>       Last modified 1989 May 8
C        Retranslated back to ANSI C in 2000 May by JCM

c 
c The French Revolutionary Calendar featured 12 months of 30 days each
c plus 5 intercalary days (6 in leap years).  The calendar was adopted
c in October 1793, retroactive to 22 Sept 1792.  This date became
c 1 Vendemiaire, An I de la Revolution.
c 
c Note that the Convention adopted yrs of the form 4n+3 as leap years,  
c not the 4n of the Gregorian calendar.  (These were marked by a 6th   
c  "jour
c sans-cullotide" at the end of the year.)  There is no reference I
c know of to any further corrections, such as the Gregorian
c calendar's non-leap
c centuries in years of the form 400n.  The calendar did not last past
c year XIII, however, so the point is moot.  Napoleon reinstated the
c Gregorian calendar on 1 January 1806.
c
c Most Revolutionary dates I have encountered in historical literature
c are translated correctly by this program. Indeed, the few differences
c I have found are clearly arithmetic errors by the historian. Any valid
c discrepancies should be due to irregularities in the calendars extant
c in 18th Century Europe.
c

*/



void cal_thermidor_format( integer FR_Year, integer FR_Month, integer FR_Day, char* date, integer maxlen )
{
 char* mois[] = { "Vendemiaire", "Brumaire", "Frimaire", 
                  "Nivose",     "Pluviose", "Ventose", 
                  "Germinal",   "Floreal",  "Prairial",
                  "Messidor",   "Thermidor", "Fructidor" };
 TextCard royr;
 char filler[8];

 if ( FR_Year < 0 )
 {
  utn_cs_copy_siz( "Date avant la revolution", date, maxlen );
  return;
 } 
 utn_cs_roman_numeral( FR_Year, royr );
 if ( FR_Month == 13 )
 {
  if ( FR_Day == 1 )
   utn_cs_copy( "ier", filler );
  else
   utn_cs_copy( "ieme", filler );
  snprintf( date, maxlen, "%2ld%-4s Jour Sans-culottide An %s", FR_Day, filler, royr );
 } else {
  snprintf( date, maxlen, "%2ld %-12sAn %s", FR_Day, mois[FR_Month-1], royr );
 }
}
