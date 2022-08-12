/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"

/*======================================================================*/
/*
 * Debug routines
 *
 * Static storage:
 *	debug_val		Debug level, 0 = no debug
 *
 */

logical utn_dbg_level( const long level )
{
 return( level <= utn_dbg_get() );
}

void utn_dbg_msg_tag( const long level, char* buf )
{
 snprintf( buf, UT_WORD_SIZE, "[*%ld]", (long)level );
}

void utn_msg_d( const long level, const char* buf )
{
 TextBuf tbuf;
 TextBuf buf2;
 TextWord tmp;
 if ( level <= utn_dbg_get() ) {
  utn_dbg_msg_tag( level, tmp );
  utn_cs_copy_siz( buf, buf2, TEXT_SIZE-20 ); /* Protect sprintf */
  snprintf( tbuf, UT_TEXT_SIZE, "%-6s%s", tmp, buf2 );
  utn_err_msg( tbuf );
 }
}

void utn_error_print( GenError error )
{
 utn_err_msg( error->buf );
}
