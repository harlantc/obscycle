/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"


void cal_cts_print( CalendarSystem* cts )
{
 TextBuf buf;
 utn_fio_msg( " " );
 sprintf( buf, "System: %s", cts->prompt );
 utn_fio_msg( buf );
 utn_fio_msg( " " );
 cal_ts_list_hdr();
 cal_ts_print( cts->ts );
}


void cal_cts_get_prompt( CalendarSystem* cts, char* prompt )
{
 utn_cs_copy( cts->prompt, prompt );
}
