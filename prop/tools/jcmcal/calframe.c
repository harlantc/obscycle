/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

static double ut1[1000];
static integer nut1= 0;
static integer ut1_base = 1600;
void cal_read_ut1( char* filename );

double cal_ut1_minus_tt( edouble et )
{
 CalDate u;
 double x;
 double cal_ut;
 integer offset;
 if ( nut1 == 0 )
  cal_read_ut1( "ut1" );

 u = cal_jde_to_doy( et );
 offset = u.year - ut1_base;
 if ( offset < 0 )
 {
  cal_ut = 0.0;
 } else if ( offset >= nut1 -1 ) {
  cal_ut = ut1[nut1-1];
 } else {
  x = ((double)u.day)/365.0;
  cal_ut = -1 * lint_d( 0.0, 1.0, ut1[offset], ut1[offset+1], x );
 }
 return cal_ut;
}


void cal_read_ut1( char* filename )
{
 FioFile file;
 integer year;
 char* ptr;
 double value;
 TextCard line;
 file = utn_fio_open_ar( filename );
 if ( !file ) 
 {
  sprintf( cal_global_state->errbuf, "UT1 file not found: %s", filename );
  return;
 }
 while ( utn_fio_read_line( file, line, CARD_SIZE ))
 {
  if ( !utn_cs_is_blank(line))
  {
   ptr = line;
   year = utn_cs_get_i( &ptr );
   value = utn_cs_get_d( &ptr );
   if ( nut1 == 0 )
    ut1_base = year;
   else if ( year != ut1_base + nut1 )
   {
    sprintf( cal_global_state->errbuf, "Error reading UT1 file at year %ld", year );
    utn_fio_file_close( file );
    return;
   }
   ut1[nut1++] = value;
  }
 }
 utn_fio_file_close( file );
}
