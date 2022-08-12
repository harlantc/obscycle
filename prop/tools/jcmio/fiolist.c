/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"

/*
 *  FIO routines using internal state
 */

/* Return UFD */     

FioFileList utn_fio_get_list( void )
{
 return utn_state_fio_list( NULL );
}


