/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

/*
 * Precoded timescales
 */
char* tstyc[] = { "TT", "UTC", "TDB", "TAI", "UT1", "GMST", "LST" };
char* tstyn[] = { "Terrestrial Time", "Civil Time", "Dynamical Time", "Atomic Time",
                  "UT1 Time", "Sidereal Time", "Local Sid. Time" };


/*
 *  Default time zone defs in absence of TS file
 */
void cal_ts_default( TimescaleList* list )
{
 integer i;
 for ( i =0; i < TSTYPE_N; i++ )
  cal_ts_create( list, tstyc[i], tstyn[i], tstyc[i], "0" );
}

/* This maps the tstyc names to the TAI_ enum tags in gcal.h */
integer cal_tstype_get_id( char* type )
{
 integer id;
 id =  utn_ar_match_c( type, tstyc, TSTYPE_N );
 return id;
}

void cal_tstype_get_name( integer id, char* name )
{
 if ( id > 0 && id <= TSTYPE_N )
 {
  utn_cs_copy( tstyc[id-1], name );
 } else {
  utn_cs_copy( " ", name );
 }
}

void cal_tstype_get_label( integer id, char* name )
{
 if ( id > 0 && id <= TSTYPE_N )
 {
  utn_cs_copy( tstyn[id-1], name );
 } else {
  utn_cs_copy( " ", name );
 }
}

void cal_tstype_list( void )
{ 
 integer ty;
 TextBuf buf;
 for ( ty = 1; ty <= TSTYPE_N; ty++ )
 {
  sprintf( buf, "%6ld %-8s %-40s", ty, tstyc[ty-1], tstyn[ty-1] );
  utn_fio_msg( buf );
 }
}
