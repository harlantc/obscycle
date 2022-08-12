/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fiolist.h"

void utn_fio_msg( char* buf )
{
 utn_fio_iostack_msg( NULL, NULL, buf, UT_TRUE, UT_FALSE );
}

void utn_fio_tmsg( char* buf )
{
 utn_fio_iostack_msg( NULL, NULL, buf, UT_TRUE, UT_TRUE );
}

void utn_fio_tmsgl( char* buf )
{
 utn_fio_iostack_msg( NULL, NULL, buf, UT_FALSE, UT_TRUE );
}

void utn_fio_msgl( char* buf )
{
 utn_fio_iostack_msg( NULL, NULL, buf, UT_FALSE, UT_FALSE );
}

void utn_fio_set_msg( void (*mf)(char*, logical) ) 
{
 utn_fio_iostack_set_msg( NULL, mf );
}

void utn_fio_iostack_msg( FioStack iofs, FioFileList list, char* buf, logical newline, logical tty )
{
 if ( tty )
 {
  FioPager pager = utn_fio_pager( list );
  utn_fio_pager_tty_write( pager, buf, newline );
 }
 else
 {
  if ( !iofs ) iofs = utn_fio_get_stack();
  iofs->msgfunc( buf, newline );
 }
}


void utn_fio_iostack_set_msg( FioStack iofs, void (*mf)(char*, logical) ) 
{
 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( mf ) {
  iofs->msgfunc = mf;
 } else {
  iofs->msgfunc = utn_fio_std_msg;
 }
}
