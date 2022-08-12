/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

void cal_free( Calendrical* state )
{
 cal_cal_list_free( state->calendars );
 cal_ts_list_free( state->timescales );
 cal_state_free( state->calglobal );
 free( state );
 utn_free_jcmlib();
}


Calendrical* cal_init( void )
{
 Calendrical* state = NULL;
 if ( !utn_ver_init( "JCMCAL", "V3.0" )) {
  state = calloc( 1, sizeof( Calendrical ));
  state->calglobal = cal_state_init();
  state->timescales = cal_ts_init( "Time_Zones" );
  state->calendars = cal_init_calendars();
 }
 return state;
}


void cal_parse_spec( char* opt, char* cal, char* ts )
{
 TextCard uopt;
 char* kptr;
 integer ptr1;
 integer ptr;
 utn_cs_copy( " ", cal );
 utn_cs_copy( " ", ts );
 if ( utn_cs_is_blank( opt ) )
  return;

 utn_cs_copy( opt, uopt );
 utn_cs_upper( uopt );
 ptr = utn_cs_index_char( uopt, '/' );
 ptr1 = utn_cs_index_char( uopt, '(' );
/* Support syntax    "CAL/TS"  */
 if ( ptr > 0 && ( ptr1 == 0 || ptr1 > ptr ))
 {
  utn_cs_copy( uopt+ptr, ts );
  if ( ptr > 1 )
   utn_cs_get_ss( uopt, cal, 1, ptr-1 );
 } else {
/* Support syntax   "CAL(TS)"  */
  ptr = utn_cs_index_char( uopt, '(' );
  if ( ptr > 0 ) 
  {
   kptr = uopt+ptr;
   utn_cs_deparen( &kptr, ts );
/* Support syntax    "(TS)"    */
   if ( ptr > 1 )
    utn_cs_get_ss( uopt, cal, 1, ptr-1 );    
  } else {
   utn_cs_copy( uopt, cal );
  }
 }
}
