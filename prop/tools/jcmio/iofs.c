/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fiolist.h"
#include <string.h>
/*----------------------------------------------------------------------*/
/*
 * esc_continue
 * 
 */

static logical esc_continue( char** ptr, char cont_char, char esc_char )
{
 long j;
 char* buf;
 buf = *ptr;
 j = strlen( buf );
 if( j >= 2 && buf[j-1] == cont_char ) {
  if( buf[j-2] == esc_char ) {
  /*  Escaped cont char */
   buf[j-2] = cont_char;
   buf[j-1] = 0;
  } else {
  /* Have a continuation */
   *ptr = &buf[j-1];
   return( UT_TRUE );
  }
 }
 return( UT_FALSE );
}

/*----------------------------------------------------------------------*/
/*
 * iofs_read
 * Uses csproto.h
 * Uses <stdlib.h>: system
 * Reads a line, handling continuations and removal of tabs, and
 * supporting system escapes.
 */


logical utn_fio_iostack_read( FioStack iofs, char* buf, char* prompt, long siz )
{
 logical cont_trap;  /* Continuation lines */
 logical loop;     
 FioFile file;
 FioFileList list = NULL;
 char sys_char, esc_char, cont_char;
 char* cmd;
 logical ios = 0;

 if ( !iofs ) iofs = utn_fio_get_stack();
 sys_char = utn_esc_special_char( SYS_CHAR );
 esc_char = utn_esc_special_char( ESC_CHAR );
 cont_char = utn_esc_special_char( CONT_CHAR );
 utn_fio_iostack_set_input( iofs, list );
 file = utn_fio_iostack_file( iofs, FIO_INPUT );
 loop = UT_TRUE;
 while ( loop ) {
  if( utn_fio_file_interactive( file ) ) 
  { 
   utn_fio_tmsgl( prompt ); 
   utn_fio_tmsgl( ": "); 
   if ( file && file->flags[FIO_FLAG_RAW] ) fflush( stdout );
  }
  cont_trap = UT_TRUE;
  cmd = buf;
  while( cont_trap && loop ) {
   loop = utn_fio_read_line( file, cmd, siz );
   ios = loop;
   if ( !loop )
   {
     TextBuf tmp;
     if ( file && file->name )
      snprintf( tmp, UT_TEXT_SIZE, "EOF found on file %s", file->name );
     else
      utn_cs_copy( tmp, "EOF found" );
     utn_msg_d( UT_VERBOSE, tmp );
   }
   if ( loop ) cont_trap = esc_continue( &cmd, esc_char, cont_char );
  } 

  /* end inner loop */
  /* OK we have a line including continuations */
  if ( *buf == sys_char ) {
   system( &buf[1] );
  } else {
   if ( buf[1] == esc_char && buf[2] == sys_char ) {
    utn_cs_copy( &buf[1], buf );
   }
   loop = UT_FALSE;
  } /* end if sys char test */
 } /* end outer loop */

 if ( iofs->detab_flag ) utn_cs_detab( buf );
 return( ios );
}




