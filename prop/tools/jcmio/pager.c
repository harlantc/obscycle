/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fiolist.h"
/* The pager connects TTY IN and TTY OUT since both affect the terminal line position */

FioPager utn_fio_pager_init( void )
{
 FioPager pager = calloc( 1, sizeof( struct FioPager_s ));
 pager->nlines = 40;
 pager->line = 0;
 pager->quiet = UT_FALSE;
 pager->on = UT_FALSE;
 return pager;
}

void utn_fio_pager_free( FioPager pager )
{
 free( pager );
}

FioPager utn_fio_pager( FioFileList list )
{
 if ( !list )
  list = utn_fio_get_list();
 return list->pager;
}

void utn_fio_pager_print_state( FioPager pager )
{

 TextCard buf;
 long last_line;
 TextWord state;

 if ( !pager ) pager = utn_fio_pager( NULL );
 last_line = pager->line;
 if ( pager->on ) {
  if ( pager->quiet ) {
   utn_cs_copy_siz( "Quiet", state, UT_WORD_SIZE );
  } else {
   utn_cs_copy_siz( "Active", state, UT_WORD_SIZE );
  }
 } else {
  utn_cs_copy_siz( "Off", state, UT_WORD_SIZE );
 }
 snprintf( buf, UT_CARD_SIZE, " Pager status:       %s", state ); utn_fio_msg( buf );
 snprintf( buf, UT_CARD_SIZE, " Pager page size:    %ld", (long)pager->nlines ); utn_fio_msg( buf );
 snprintf( buf, UT_CARD_SIZE, " Pager current line: %ld", (long)last_line );    utn_fio_msg( buf );
 snprintf( buf, UT_CARD_SIZE, " Pager new current line: %ld", (long)pager->line );    utn_fio_msg( buf );
 utn_fio_msg( " " );
}

void utn_fio_pager_set_length( FioPager pager, long page_length )
{
 if ( !pager ) pager = utn_fio_pager( NULL );
 pager->nlines = page_length;
}

void utn_fio_pager_set( FioPager pager, logical q )
{
 if ( !pager ) pager = utn_fio_pager( NULL );
 pager->on = q;
 pager->quiet = UT_FALSE;
}


logical utn_fio_pager_tty_read( FioPager pager, char* buf, long siz )
{
 
 logical ios; 
 if ( !pager ) pager = utn_fio_pager( NULL );
 ios = utn_fio_get_line( NULL, buf, siz );
 pager->line = 0;
 pager->quiet = UT_FALSE;
 return( ios );
}


logical utn_fio_pager_tty_write( FioPager pager, const char* buf, logical newline )
{
 logical ios = UT_TRUE;
 if ( !pager )
  pager = utn_fio_pager( NULL );

 if ( ! pager->quiet ) {
  if ( newline )
  {
   ios = utn_fio_put_line_n( NULL, buf, UT_TRUE, UT_TRUE, 0 );
   pager->line = ( pager->line + 1 ) % pager->nlines;
   if ( pager->on && pager->line == 0 ) utn_fio_tty_pager_query( pager );
  } else {
   ios = utn_fio_put_line_n( NULL, buf, UT_FALSE, UT_FALSE, 0 );
  }
 }
 return( ios );
}



void utn_fio_tty_pager_query( FioPager pager )
{
 char c;
 printf( "--Press Return to Continue--" );
 c = getc( stdin );
 if ( c == 'q' || c == 'Q' ) pager->quiet = UT_TRUE;
}

